package baritone.process;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.event.events.BlockChangeEvent;
import baritone.api.event.events.ChunkEvent;
import baritone.api.event.events.PacketEvent;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.events.TickEvent;
import baritone.api.event.events.WorldEvent;
import baritone.api.event.events.type.EventState;
import baritone.api.event.listener.AbstractGameEventListener;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.pathing.goals.GoalYLevel;
import baritone.api.pathing.movement.IMovement;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.IElytraProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.movements.MovementFall;
import baritone.pathing.path.PathExecutor;
import baritone.process.elytra.ElytraBehavior;
import baritone.process.elytra.NetherPathfinderContext;
import baritone.process.elytra.NullElytraProcess;
import baritone.utils.BaritoneProcessHelper;
import baritone.utils.PathingCommandContext;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

public class ElytraProcess extends BaritoneProcessHelper implements IBaritoneProcess, IElytraProcess, AbstractGameEventListener {
   public ElytraProcess.State state;
   private boolean goingToLandingSpot;
   private BetterBlockPos landingSpot;
   private boolean reachedGoal;
   private Goal goal;
   private ElytraBehavior behavior;
   private NetherPathfinderContext npfContext;
   private boolean predictingTerrain;
   private boolean allowTight;
   private boolean allowAboveBuildLimit;
   private boolean allowAboveRoof;
   private final Semaphore npfSema = new Semaphore(1);
   private static final long LANDING_SEARCH_BUDGET_NANOS = TimeUnit.MILLISECONDS.toNanos(25L);
   private int landingColumnHeight = 15;
   private Set<BetterBlockPos> badLandingSpots = new HashSet<>();
   private ElytraProcess.LandingSearchState landingSearchState;
   private static final String AUTO_JUMP_FAILURE_MSG = "Failed to compute a walking path to a spot to jump off from. Consider starting from a higher location, near an overhang. Or, you can disable elytraAutoJump and just manually begin gliding.";

   @Override
   public void onLostControl() {
      this.onLostControl(true);
   }

   public void onLostControl(boolean var1) {
      this.state = ElytraProcess.State.START_FLYING;
      this.goingToLandingSpot = false;
      this.landingSpot = null;
      this.landingSearchState = null;
      this.reachedGoal = false;
      this.goal = null;
      this.destroyBehaviorAsync();
      if (var1) {
         this.destroyNpfContextAsync();
      }
   }

   private ElytraProcess(Baritone var1) {
      super(var1);
      var1.getGameEventHandler().registerEventListener(this);
   }

   public static IElytraProcess create(Baritone var0) {
      return NetherPathfinderContext.isSupported() ? new ElytraProcess(var0) : new NullElytraProcess(var0);
   }

   @Override
   public boolean isActive() {
      return this.behavior != null;
   }

   @Override
   public void resetState() {
      BlockPos var1 = this.currentDestination();
      this.onLostControl();
      if (var1 != null) {
         this.pathTo(var1);
         this.repackChunks();
      }
   }

   @Override
   public PathingCommand onTick(boolean var1, boolean var2) {
      try {
         long var3 = Baritone.settings().elytraNetherSeed.value;
         if (var3 != this.behavior.npfContext.getSeed()) {
            this.logDirect("Nether seed changed, recalculating path");
            this.resetState();
         }

         if (this.predictingTerrain != Baritone.settings().elytraPredictTerrain.value && this.ctx.player().level().dimension() == Level.NETHER) {
            this.logDirect("elytraPredictTerrain setting changed, recalculating path from scratch");
            this.predictingTerrain = Baritone.settings().elytraPredictTerrain.value;
            this.resetState();
         }

         if (this.allowTight != Baritone.settings().elytraAllowTightSpaces.value) {
            this.logDirect("elytraAllowTightSpaces setting changed, recalculating path from scratch");
            this.allowTight = Baritone.settings().elytraAllowTightSpaces.value;
            this.resetState();
         }

         if (this.allowAboveBuildLimit != Baritone.settings().elytraAllowAboveBuildLimit.value) {
            this.logDirect("elytraAllowAboveBuildLimit setting changed, recalculating path from scratch");
            this.allowAboveBuildLimit = Baritone.settings().elytraAllowAboveBuildLimit.value;
            this.resetState();
         }

         if (this.allowAboveRoof != Baritone.settings().elytraAllowAboveRoof.value && this.ctx.player().level().dimension() == Level.NETHER) {
            this.logDirect("elytraAllowAboveRoof setting changed, recalculating path from scratch");
            this.allowAboveRoof = Baritone.settings().elytraAllowAboveRoof.value;
            this.resetState();
         }
      } catch (IllegalArgumentException var12) {
         this.logDirect(var12.getMessage(), ChatFormatting.RED);
         return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
      }

      this.behavior.onTick();
      if (var1) {
         this.onLostControl();
         this.logDirect(
            "Failed to compute a walking path to a spot to jump off from. Consider starting from a higher location, near an overhang. Or, you can disable elytraAutoJump and just manually begin gliding."
         );
         return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
      }

      boolean var13 = false;
      if (this.ctx.player().isFallFlying() && this.shouldLandForSafety()) {
         if (Baritone.settings().elytraAllowEmergencyLand.value) {
            this.logDirect("Emergency landing - almost out of elytra durability or fireworks");
            var13 = true;
         } else {
            this.logDirect("almost out of elytra durability or fireworks, but I'm going to continue since elytraAllowEmergencyLand is false");
         }
      }

      if (this.ctx.player().isFallFlying() && this.state != ElytraProcess.State.LANDING && (this.behavior.pathManager.isComplete() || var13)) {
         BetterBlockPos var4 = this.behavior.pathManager.path.getLast();
         if (var4 != null
            && (this.ctx.player().position().distanceToSqr(Vec3.atCenterOf(var4)) < 2304.0 || var13)
            && (!this.goingToLandingSpot || var13 && this.landingSpot == null)) {
            if (this.landingSearchState == null) {
               this.logDirect("Path complete, searching for safe landing spot...");
            }

            BetterBlockPos var5 = this.findSafeLandingSpot(this.ctx.playerFeet());
            if (var5 != null) {
               this.logDirect("Found potential landing spot.");
               this.pathTo0(var5, true);
               this.landingSpot = var5;
               this.goingToLandingSpot = true;
            } else {
               this.goingToLandingSpot = false;
            }
         }

         if (var4 != null && this.ctx.player().position().distanceToSqr(Vec3.atCenterOf(var4)) < 1.0) {
            if (Baritone.settings().notificationOnPathComplete.value && !this.reachedGoal) {
               this.logNotification("Pathing complete", false);
            }

            if (Baritone.settings().disconnectOnArrival.value && !this.reachedGoal) {
               this.onLostControl();
               if (this.ctx.world() instanceof ClientLevel var20) {
                  var20.disconnect(Component.literal("[Baritone] Arrived at goal!"));
               }

               return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
            }

            this.reachedGoal = true;
            if (this.goingToLandingSpot && this.landingSpot != null) {
               this.state = ElytraProcess.State.LANDING;
               this.logDirect("Above the landing spot, landing...");
            }
         }
      }

      if (this.state == ElytraProcess.State.LANDING) {
         BetterBlockPos var14 = this.landingSpot != null ? this.landingSpot : this.behavior.pathManager.path.getLast();
         if (this.ctx.player().isFallFlying() && var14 != null) {
            Vec3 var17 = this.ctx.player().position();
            Vec3 var6 = new Vec3(var14.x + 0.5, var17.y, var14.z + 0.5);
            Rotation var7 = RotationUtils.calcRotationFromVec3d(var17, var6, this.ctx.playerRotations());
            this.baritone.getLookBehavior().updateTarget(new Rotation(var7.getYaw(), 0.0F), false);
            if (this.ctx.player().position().y < var14.y - this.landingColumnHeight) {
               this.logDirect("bad landing spot, trying again...");
               this.landingSpotIsBad(var14);
            }
         }
      }

      if (this.ctx.player().isFallFlying()) {
         this.behavior.landingMode = this.state == ElytraProcess.State.LANDING;
         this.goal = null;
         this.baritone.getInputOverrideHandler().clearAllKeys();
         if (this.behavior.npfContext.tryAcquireReadLock()) {
            try {
               this.behavior.tick();
            } finally {
               this.behavior.npfContext.releaseReadLock();
            }
         }

         return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
      } else if (this.state == ElytraProcess.State.LANDING) {
         if (this.ctx.playerMotion().multiply(1.0, 0.0, 1.0).length() > 0.001) {
            this.logDirect("Landed, but still moving, waiting for velocity to die down... ");
            this.baritone.getInputOverrideHandler().setInputForceState(Input.SNEAK, true);
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
         } else {
            this.logDirect("Done :)");
            this.baritone.getInputOverrideHandler().clearAllKeys();
            this.onLostControl();
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
         }
      } else {
         if (this.state == ElytraProcess.State.FLYING || this.state == ElytraProcess.State.START_FLYING) {
            this.state = this.ctx.player().onGround() && Baritone.settings().elytraAutoJump.value
               ? ElytraProcess.State.LOCATE_JUMP
               : ElytraProcess.State.START_FLYING;
         }

         if (this.state == ElytraProcess.State.LOCATE_JUMP) {
            if (this.shouldLandForSafety()) {
               this.logDirect("Not taking off, because elytra durability or fireworks are so low that I would immediately emergency land anyway.");
               this.onLostControl();
               return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
            }

            if (this.goal == null) {
               this.goal = new GoalYLevel(31);
            }

            PathExecutor var16 = this.baritone.getPathingBehavior().getCurrent();
            if (var16 != null && var16.getPath().getGoal() == this.goal) {
               IMovement var19 = var16.getPath().movements().stream().filter(var0 -> var0 instanceof MovementFall).findFirst().orElse(null);
               if (var19 == null) {
                  this.onLostControl();
                  this.logDirect(
                     "Failed to compute a walking path to a spot to jump off from. Consider starting from a higher location, near an overhang. Or, you can disable elytraAutoJump and just manually begin gliding."
                  );
                  return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
               }

               BetterBlockPos var21 = new BetterBlockPos(
                  (var19.getSrc().x + var19.getDest().x) / 2, (var19.getSrc().y + var19.getDest().y) / 2, (var19.getSrc().z + var19.getDest().z) / 2
               );
               this.behavior.pathManager.pathToDestination(var21).whenComplete((var1x, var2x) -> {
                  if (var2x == null) {
                     this.state = ElytraProcess.State.GET_TO_JUMP;
                  } else {
                     this.onLostControl();
                  }
               });
               this.state = ElytraProcess.State.PAUSE;
            }

            return new PathingCommandContext(this.goal, PathingCommandType.SET_GOAL_AND_PAUSE, new ElytraProcess.WalkOffCalculationContext(this.baritone));
         } else {
            if (this.state == ElytraProcess.State.PAUSE) {
               return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
            }

            if (this.state == ElytraProcess.State.GET_TO_JUMP) {
               PathExecutor var15 = this.baritone.getPathingBehavior().getCurrent();
               boolean var18 = this.ctx.player().getDeltaMovement().y < -0.377
                  && !var2
                  && var15 != null
                  && var15.getPath().movements().get(var15.getPosition()) instanceof MovementFall;
               if (!var18) {
                  return new PathingCommand(null, PathingCommandType.SET_GOAL_AND_PATH);
               }

               this.state = ElytraProcess.State.START_FLYING;
            }

            if (this.state == ElytraProcess.State.START_FLYING) {
               if (!var2) {
                  this.baritone.getPathingBehavior().secretInternalSegmentCancel();
               }

               this.baritone.getInputOverrideHandler().clearAllKeys();
               if (this.ctx.player().getDeltaMovement().y < -0.377) {
                  this.baritone.getInputOverrideHandler().setInputForceState(Input.JUMP, true);
               }
            }

            return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
         }
      }
   }

   public void landingSpotIsBad(BetterBlockPos var1) {
      this.badLandingSpots.add(var1);
      this.goingToLandingSpot = false;
      this.landingSpot = null;
      this.landingSearchState = null;
      this.state = ElytraProcess.State.FLYING;
   }

   private void destroyBehaviorAsync() {
      ElytraBehavior var1 = this.behavior;
      if (var1 != null) {
         this.behavior = null;
         Baritone.getExecutor().execute(() -> var1.destroy());
      }
   }

   @Override
   public double priority() {
      return 0.0;
   }

   @Override
   public String displayName0() {
      return "Elytra - " + this.state.description;
   }

   @Override
   public void repackChunks() {
      if (this.npfContext != null) {
         ChunkSource var1 = this.ctx.world().getChunkSource();
         BetterBlockPos var2 = this.ctx.playerFeet();
         int var3 = var2.getX() >> 4;
         int var4 = var2.getZ() >> 4;
         int var5 = var3 - 40;
         int var6 = var4 - 40;
         int var7 = var3 + 40;
         int var8 = var4 + 40;

         for (int var9 = var5; var9 <= var7; var9++) {
            for (int var10 = var6; var10 <= var8; var10++) {
               LevelChunk var11 = var1.getChunk(var9, var10, false);
               if (var11 != null && !var11.isEmpty()) {
                  this.npfContext.queueForPacking(var11);
               }
            }
         }
      }
   }

   @Override
   public BlockPos currentDestination() {
      return this.behavior != null ? this.behavior.destination : null;
   }

   @Override
   public List<BetterBlockPos> getPath() {
      return this.behavior != null ? this.behavior.pathManager.getPath() : Collections.emptyList();
   }

   @Override
   public void pathTo(BlockPos var1) {
      if (!this.isSupportedPos(var1)) {
         throw new IllegalArgumentException("The goal must be within bounds to use elytra flight.");
      }

      if (this.ctx.player() != null && !this.isSupportedPos(this.ctx.playerFeet())) {
         throw new IllegalArgumentException("The player must be within bounds to use elytra flight.");
      }

      this.pathTo0(var1, false);
   }

   private void pathTo0(BlockPos var1, boolean var2) {
      if (this.ctx.player() != null) {
         this.onLostControl(false);
         this.predictingTerrain = this.ctx.player().level().dimension() == Level.NETHER && Baritone.settings().elytraPredictTerrain.value;
         this.allowTight = Baritone.settings().elytraAllowTightSpaces.value;
         this.allowAboveBuildLimit = Baritone.settings().elytraAllowAboveBuildLimit.value;
         this.allowAboveRoof = Baritone.settings().elytraAllowAboveRoof.value;
         this.behavior = new ElytraBehavior(this.baritone, this, this.getNpfContext(), var1, var2);
         if (this.ctx.world() != null) {
            this.repackChunks();
         }

         this.behavior.pathTo();
      }
   }

   @Override
   public void pathTo(Goal var1) {
      int var2;
      int var3;
      int var4;
      if (var1 instanceof GoalXZ var5) {
         var2 = var5.getX();
         var3 = 64;
         var4 = var5.getZ();
      } else {
         if (!(var1 instanceof GoalBlock var6)) {
            throw new IllegalArgumentException("The goal must be a GoalXZ or GoalBlock");
         }

         var2 = var6.x;
         var3 = var6.y;
         var4 = var6.z;
      }

      this.pathTo(new BlockPos(var2, var3, var4));
   }

   private boolean isSupportedPos(BlockPos var1) {
      boolean var2 = this.ctx.world().dimension() == Level.NETHER;
      int var3 = this.ctx.world().dimensionType().minY();
      int var4 = var2 && !Baritone.settings().elytraAllowAboveRoof.value ? 127 : Math.min(var3 + 384, this.ctx.world().dimensionType().height() + var3);
      boolean var5 = Baritone.settings().elytraAllowAboveRoof.value;
      boolean var6 = Baritone.settings().elytraAllowAboveBuildLimit.value;
      boolean var7 = var2 ? !var5 || !var6 : !var6;
      return var1.getY() < var3 ? false : !var7 || var1.getY() < var4;
   }

   private boolean shouldLandForSafety() {
      ItemStack var1 = this.ctx.player().getItemBySlot(EquipmentSlot.CHEST);
      if (var1.getItem() == Items.ELYTRA && var1.getMaxDamage() - var1.getDamageValue() >= Baritone.settings().elytraMinimumDurability.value) {
         var var2 = this.ctx.player().getInventory().getNonEquipmentItems();
         int var3 = 0;

         for (int var4 = 0; var4 < 36; var4++) {
            if (ElytraBehavior.isFireworks((ItemStack)var2.get(var4))) {
               var3 += ((ItemStack)var2.get(var4)).getCount();
            }
         }

         return var3 <= Baritone.settings().elytraMinFireworksBeforeLanding.value;
      } else {
         return true;
      }
   }

   @Override
   public boolean isLoaded() {
      return true;
   }

   @Override
   public boolean isSafeToCancel() {
      return !this.isActive() || this.state != ElytraProcess.State.FLYING && this.state != ElytraProcess.State.START_FLYING;
   }

   @Override
   public void onRenderPass(RenderEvent var1) {
      if (this.behavior != null) {
         this.behavior.onRenderPass(var1);
      }
   }

   @Override
   public void onWorldEvent(WorldEvent var1) {
      if (var1.getWorld() != null && var1.getState() == EventState.POST) {
         this.destroyBehaviorAsync();
      }
   }

   @Override
   public void onChunkEvent(ChunkEvent var1) {
      if (this.behavior != null) {
         this.behavior.onChunkEvent(var1);
      }
   }

   @Override
   public void onBlockChange(BlockChangeEvent var1) {
      if (this.behavior != null) {
         this.behavior.onBlockChange(var1);
      }
   }

   @Override
   public void onReceivePacket(PacketEvent var1) {
      if (this.behavior != null) {
         this.behavior.onReceivePacket(var1);
      }
   }

   @Override
   public void onPostTick(TickEvent var1) {
      IBaritoneProcess var2 = this.baritone.getPathingControlManager().mostRecentInControl().orElse(null);
      if (this.behavior != null && var2 == this) {
         this.behavior.onPostTick(var1);
      }
   }

   private static boolean isInBounds(Level var0, BlockPos var1) {
      DimensionType var2 = var0.dimensionType();
      int var3 = var2.minY();
      int var4 = var0.dimension() == Level.NETHER && !Baritone.settings().elytraAllowAboveRoof.value ? 127 : Math.min(var3 + 384, var2.height() + var3);
      return var1.getY() >= var3 && var1.getY() < var4;
   }

   private boolean isSafeBlock(Block var1) {
      return var1 == Blocks.NETHERRACK
         || var1 == Blocks.GRAVEL
         || var1 == Blocks.SOUL_SAND
         || var1 == Blocks.SOUL_SOIL
         || var1 == Blocks.NETHER_BRICKS && Baritone.settings().elytraAllowLandOnNetherFortress.value
         || var1 == Blocks.STONE
         || var1 == Blocks.DEEPSLATE
         || var1 == Blocks.GRASS_BLOCK
         || var1 == Blocks.SAND
         || var1 == Blocks.RED_SAND
         || var1 == Blocks.TERRACOTTA
         || var1 == Blocks.SNOW
         || var1 == Blocks.ICE
         || var1 == Blocks.MYCELIUM
         || var1 == Blocks.PODZOL
         || var1 == Blocks.DARK_OAK_LEAVES
         || var1 == Blocks.JUNGLE_LEAVES
         || var1 == Blocks.END_STONE
         || var1 == Blocks.BEDROCK
         || var1 == Blocks.OBSIDIAN
         || var1 == Blocks.COBBLESTONE;
   }

   private boolean isSafeBlock(BlockPos var1) {
      return this.isSafeBlock(this.ctx.world().getBlockState(var1).getBlock());
   }

   private boolean isAtEdge(BlockPos var1) {
      return !this.isSafeBlock(var1.north())
         || !this.isSafeBlock(var1.south())
         || !this.isSafeBlock(var1.east())
         || !this.isSafeBlock(var1.west())
         || !this.isSafeBlock(var1.north().west())
         || !this.isSafeBlock(var1.north().east())
         || !this.isSafeBlock(var1.south().west())
         || !this.isSafeBlock(var1.south().east());
   }

   private boolean isColumnAir(BlockPos var1, int var2) {
      MutableBlockPos var3 = new MutableBlockPos(var1.getX(), var1.getY(), var1.getZ());
      int var4 = var3.getY() + var2;

      for (int var5 = var3.getY() + 1; var5 <= var4; var5++) {
         var3.set(var3.getX(), var5, var3.getZ());
         if (!(this.ctx.world().getBlockState(var3).getBlock() instanceof AirBlock)) {
            return false;
         }
      }

      return true;
   }

   private boolean hasAirBubble(BlockPos var1) {
      MutableBlockPos var3 = new MutableBlockPos();

      for (int var4 = -4; var4 <= 4; var4++) {
         for (int var5 = -4; var5 <= 4; var5++) {
            for (int var6 = -4; var6 <= 4; var6++) {
               var3.set(var1.getX() + var4, var1.getY() + var5, var1.getZ() + var6);
               if (!(this.ctx.world().getBlockState(var3).getBlock() instanceof AirBlock)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private BetterBlockPos checkLandingSpot(BlockPos var1, LongOpenHashSet var2) {
      MutableBlockPos var3 = new MutableBlockPos(var1.getX(), var1.getY(), var1.getZ());

      while (var3.getY() >= this.ctx.world().dimensionType().minY()) {
         if (var2.contains(var3.asLong())) {
            return null;
         }

         var2.add(var3.asLong());
         Block var4 = this.ctx.world().getBlockState(var3).getBlock();
         if (this.isSafeBlock(var4)) {
            if (!this.isAtEdge(var3)) {
               return new BetterBlockPos(var3);
            }

            return null;
         }

         if (var4 != Blocks.AIR) {
            return null;
         }

         var3.set(var3.getX(), var3.getY() - 1, var3.getZ());
      }

      return null;
   }

   private BetterBlockPos findSafeLandingSpot(BetterBlockPos var1) {
      boolean var2 = this.ctx.player().getY() > this.ctx.world().getHeight(Types.MOTION_BLOCKING, var1.getX(), var1.getZ());
      if (this.landingSearchState != null && this.landingSearchState.isCompatible(var1, var2)) {
         this.landingSearchState.updateStartPosition(var1);
      } else {
         this.landingSearchState = new ElytraProcess.LandingSearchState(var1, this.behavior.destination, var2);
      }

      BetterBlockPos var3 = this.landingSearchState.advance();
      if (var3 != null || this.landingSearchState.exhausted) {
         this.landingSearchState = null;
      }

      return var3;
   }

   private boolean isChunkLoaded(BetterBlockPos var1) {
      return this.ctx.world().getChunkSource().hasChunk(var1.x >> 4, var1.z >> 4);
   }

   private NetherPathfinderContext getNpfContext() {
      if (this.npfContext == null) {
         this.npfSema.acquireUninterruptibly();
         this.npfContext = new NetherPathfinderContext(
            Baritone.settings().elytraNetherSeed.value,
            Baritone.settings().elytraUseCache.value ? this.baritone.getWorldProvider().getCurrentWorld().directory.resolve("cache") : null,
            this.ctx.world()
         );
      }

      return this.npfContext;
   }

   private void destroyNpfContextAsync() {
      NetherPathfinderContext var1 = this.npfContext;
      if (var1 != null) {
         this.npfContext = null;
         Baritone.getExecutor().execute(() -> {
            var1.destroy();
            this.npfSema.release();
         });
      }
   }

   private final class LandingSearchState {
      private final BetterBlockPos origin;
      private final boolean useHeightmap;
      private final Queue<BetterBlockPos> queue;
      private final Set<BetterBlockPos> visited;
      private final LongOpenHashSet checkedPositions;
      private boolean exhausted;

      private LandingSearchState(BetterBlockPos var2, BetterBlockPos var3, boolean var4) {
         this.visited = new HashSet<>();
         this.checkedPositions = new LongOpenHashSet();
         this.origin = var2;
         this.useHeightmap = var4;
         BetterBlockPos var5 = ElytraProcess.this.isChunkLoaded(var3) ? var3 : var2;
         this.queue = new PriorityQueue<>(
            Comparator.<BetterBlockPos>comparingInt(var1x -> (var1x.x - var5.x) * (var1x.x - var5.x) + (var1x.z - var5.z) * (var1x.z - var5.z))
               .thenComparingInt(var0 -> -var0.y)
         );
         this.queue.add(var5);
      }

      private boolean isCompatible(BetterBlockPos var1, boolean var2) {
         return this.useHeightmap == var2 && this.origin.distanceSq(var1) <= 256.0;
      }

      private void updateStartPosition(BetterBlockPos var1) {
         if (this.visited.add(var1)) {
            this.queue.add(var1);
         }
      }

      private BetterBlockPos advance() {
         long var1 = System.nanoTime() + ElytraProcess.LANDING_SEARCH_BUDGET_NANOS;

         while (!this.queue.isEmpty()) {
            if (System.nanoTime() >= var1) {
               return null;
            }

            BetterBlockPos var3 = this.queue.poll();
            if (ElytraProcess.this.isChunkLoaded(var3)) {
               BetterBlockPos var4 = this.useHeightmap ? this.advanceHeightmap(var3) : this.advanceUnderground(var3);
               if (var4 != null) {
                  return var4;
               }
            }
         }

         this.exhausted = true;
         return null;
      }

      private BetterBlockPos advanceUnderground(BetterBlockPos var1) {
         if (ElytraProcess.isInBounds(ElytraProcess.this.ctx.world(), var1) && ElytraProcess.this.ctx.world().getBlockState(var1).getBlock() == Blocks.AIR) {
            BetterBlockPos var2 = ElytraProcess.this.checkLandingSpot(var1, this.checkedPositions);
            if (var2 != null) {
               ElytraProcess.this.landingColumnHeight = 15;
               if (ElytraProcess.this.isColumnAir(var2, ElytraProcess.this.landingColumnHeight)
                  && ElytraProcess.this.hasAirBubble(var2.above(ElytraProcess.this.landingColumnHeight))
                  && !ElytraProcess.this.badLandingSpots.contains(var2.above(ElytraProcess.this.landingColumnHeight))) {
                  return var2.above(ElytraProcess.this.landingColumnHeight);
               }
            }

            if (this.visited.add(var1.north())) {
               this.queue.add(var1.north());
            }

            if (this.visited.add(var1.east())) {
               this.queue.add(var1.east());
            }

            if (this.visited.add(var1.south())) {
               this.queue.add(var1.south());
            }

            if (this.visited.add(var1.west())) {
               this.queue.add(var1.west());
            }

            if (this.visited.add(var1.above())) {
               this.queue.add(var1.above());
            }

            if (this.visited.add(var1.below())) {
               this.queue.add(var1.below());
            }
         }

         return null;
      }

      private BetterBlockPos advanceHeightmap(BetterBlockPos var1) {
         int var2 = ElytraProcess.this.ctx.world().getHeight(Types.MOTION_BLOCKING, var1.getX(), var1.getZ());
         BetterBlockPos var3 = new BetterBlockPos(var1.getX(), var2 + 1, var1.getZ());
         if (ElytraProcess.isInBounds(ElytraProcess.this.ctx.world(), var3) && ElytraProcess.this.ctx.world().getBlockState(var3).getBlock() == Blocks.AIR) {
            BetterBlockPos var4 = ElytraProcess.this.checkLandingSpot(var3, this.checkedPositions);
            if (var4 != null) {
               ElytraProcess.this.landingColumnHeight = ElytraProcess.this.ctx.playerFeet().y - var4.y < 39 ? 15 : 39;
               if (ElytraProcess.this.hasAirBubble(var4.above(ElytraProcess.this.landingColumnHeight))
                  && !ElytraProcess.this.badLandingSpots.contains(var4.above(ElytraProcess.this.landingColumnHeight))) {
                  return var4.above(ElytraProcess.this.landingColumnHeight);
               }
            }

            if (this.visited.add(var3.north())) {
               this.queue.add(var3.north());
            }

            if (this.visited.add(var3.east())) {
               this.queue.add(var3.east());
            }

            if (this.visited.add(var3.south())) {
               this.queue.add(var3.south());
            }

            if (this.visited.add(var3.west())) {
               this.queue.add(var3.west());
            }
         }

         return null;
      }
   }

   public enum State {
      LOCATE_JUMP("Finding spot to jump off"),
      PAUSE("Waiting for elytra path"),
      GET_TO_JUMP("Walking to takeoff"),
      START_FLYING("Begin flying"),
      FLYING("Flying"),
      LANDING("Landing");

      public final String description;

      State(String var3) {
         this.description = var3;
      }
   }

   public static final class WalkOffCalculationContext extends CalculationContext {
      public WalkOffCalculationContext(IBaritone var1) {
         super(var1, true);
         this.allowFallIntoLava = true;
         this.minFallHeight = 8;
         this.maxFallHeightNoWater = 10000;
      }

      @Override
      public double costOfPlacingAt(int var1, int var2, int var3, BlockState var4) {
         return 1000000.0;
      }

      @Override
      public double breakCostMultiplierAt(int var1, int var2, int var3, BlockState var4) {
         return 1000000.0;
      }

      @Override
      public double placeBucketCost() {
         return 1000000.0;
      }
   }
}
