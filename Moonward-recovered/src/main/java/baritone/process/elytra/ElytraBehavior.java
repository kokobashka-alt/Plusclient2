package baritone.process.elytra;

import baritone.Baritone;
import baritone.api.Settings;
import baritone.api.behavior.look.IAimProcessor;
import baritone.api.behavior.look.ITickableAimProcessor;
import baritone.api.event.events.BlockChangeEvent;
import baritone.api.event.events.ChunkEvent;
import baritone.api.event.events.PacketEvent;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.events.TickEvent;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.Pair;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.SettingsUtil;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.MovementHelper;
import baritone.process.ElytraProcess;
import baritone.utils.BaritoneMath;
import baritone.utils.BlockStateInterface;
import baritone.utils.IRenderer;
import baritone.utils.PathRenderer;
import baritone.utils.accessor.IFireworkRocketEntity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class ElytraBehavior implements Helper {
   private final Baritone baritone;
   private final IPlayerContext ctx;
   private final List<Pair<Vec3, Vec3>> clearLines;
   private final List<Pair<Vec3, Vec3>> blockedLines;
   private List<Vec3> simulationLine;
   private BlockPos aimPos;
   private List<BetterBlockPos> visiblePath;
   public NetherPathfinderContext npfContext;
   public IElytraPathFinder pathFinder;
   public final ElytraBehavior.PathManager pathManager;
   private final ElytraProcess process;
   private int remainingFireworkTicks;
   private int remainingSetBackTicks;
   public boolean landingMode;
   private int minimumBoostTicks;
   private boolean deployedFireworkLastTick;
   private final int[] nextTickBoostCounter;
   private BlockStateInterface bsi;
   public final BetterBlockPos destination;
   private final boolean appendDestination;
   private final ExecutorService solverExecutor;
   private Future<ElytraBehavior.Solution> solver;
   private ElytraBehavior.Solution pendingSolution;
   private boolean solveNextTick;
   private long timeLastCacheCull;
   private int invTickCountdown;
   private final Queue<Runnable> invTransactionQueue;

   public ElytraBehavior(Baritone var1, ElytraProcess var2, NetherPathfinderContext var3, BlockPos var4, boolean var5) {
      this.timeLastCacheCull = 0L;
      this.invTickCountdown = 0;
      this.invTransactionQueue = new LinkedList<>();
      this.baritone = var1;
      this.ctx = var1.getPlayerContext();
      this.clearLines = new CopyOnWriteArrayList<>();
      this.blockedLines = new CopyOnWriteArrayList<>();
      this.pathManager = new ElytraBehavior.PathManager();
      this.process = var2;
      this.destination = new BetterBlockPos(var4);
      this.appendDestination = var5;
      this.solverExecutor = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon().factory());
      this.nextTickBoostCounter = new int[2];
      this.npfContext = var3;
      if (this.ctx.world().dimension() == Level.NETHER) {
         this.pathFinder = Baritone.settings().elytraAllowAboveRoof.value && Baritone.settings().elytraAllowAboveBuildLimit.value
            ? new BuildLimitPathFinder(this.ctx, this.npfContext)
            : this.npfContext;
      } else {
         this.pathFinder = Baritone.settings().elytraAllowAboveBuildLimit.value ? new BuildLimitPathFinder(this.ctx, this.npfContext) : this.npfContext;
      }
   }

   public void onRenderPass(RenderEvent var1) {
      Settings var2 = Baritone.settings();
      if (this.visiblePath != null) {
         PathRenderer.drawPath(var1.getModelViewStack(), this.visiblePath, 0, Color.RED, false, 0, 0, 0.0);
      }

      if (this.aimPos != null) {
         PathRenderer.drawGoal(var1.getModelViewStack(), this.ctx, new GoalBlock(this.aimPos), var1.getPartialTicks(), Color.GREEN);
      }

      if (!this.clearLines.isEmpty() && var2.elytraRenderRaytraces.value) {
         BufferBuilder var3 = IRenderer.startLines(Color.GREEN);

         for (Pair var5 : this.clearLines) {
            IRenderer.emitLine(var3, var1.getModelViewStack(), (Vec3)var5.first(), (Vec3)var5.second(), var2.pathRenderLineWidthPixels.value);
         }

         IRenderer.endLines(var3, var2.renderPathIgnoreDepth.value);
      }

      if (!this.blockedLines.isEmpty() && Baritone.settings().elytraRenderRaytraces.value) {
         BufferBuilder var8 = IRenderer.startLines(Color.BLUE);

         for (Pair var12 : this.blockedLines) {
            IRenderer.emitLine(var8, var1.getModelViewStack(), (Vec3)var12.first(), (Vec3)var12.second(), var2.pathRenderLineWidthPixels.value);
         }

         IRenderer.endLines(var8, var2.renderPathIgnoreDepth.value);
      }

      if (this.simulationLine != null && Baritone.settings().elytraRenderSimulation.value) {
         BufferBuilder var9 = IRenderer.startLines(new Color(3591388));
         Vec3 var11 = this.ctx.player().getPosition(var1.getPartialTicks());

         for (int var13 = 0; var13 < this.simulationLine.size() - 1; var13++) {
            Vec3 var6 = this.simulationLine.get(var13).add(var11);
            Vec3 var7 = this.simulationLine.get(var13 + 1).add(var11);
            IRenderer.emitLine(var9, var1.getModelViewStack(), var6, var7, var2.pathRenderLineWidthPixels.value);
         }

         IRenderer.endLines(var9, var2.renderPathIgnoreDepth.value);
      }
   }

   public void onChunkEvent(ChunkEvent var1) {
      if (var1.isPostPopulate() && this.npfContext != null) {
         LevelChunk var2 = this.ctx.world().getChunk(var1.getX(), var1.getZ());
         this.npfContext.queueForPacking(var2);
      }
   }

   public void onBlockChange(BlockChangeEvent var1) {
      this.npfContext.queueBlockUpdate(var1);
   }

   public void onReceivePacket(PacketEvent var1) {
      if (var1.getPacket() instanceof ClientboundPlayerPositionPacket) {
         this.ctx.minecraft().execute(() -> this.remainingSetBackTicks = Baritone.settings().elytraFireworkSetbackUseDelay.value);
      }
   }

   public void pathTo() {
      if (!Baritone.settings().elytraAutoJump.value || this.ctx.player().isFallFlying()) {
         this.pathManager.pathToDestination();
      }
   }

   public void destroy() {
      if (this.solver != null) {
         this.solver.cancel(true);
      }

      this.solverExecutor.shutdown();

      try {
         while (!this.solverExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
         }
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }
   }

   public void onTick() {
      if (this.npfContext.tryAcquireReadLock()) {
         try {
            this.onTick0();
         } finally {
            this.npfContext.releaseReadLock();
         }
      }

      long var1 = System.currentTimeMillis();
      if ((var1 - this.timeLastCacheCull) / 1000L > Baritone.settings().elytraTimeBetweenCacheCullSecs.value) {
         this.npfContext
            .queueCacheCulling(this.ctx.player().chunkPosition().x(), this.ctx.player().chunkPosition().z(), Baritone.settings().elytraCacheCullDistance.value);
         this.timeLastCacheCull = var1;
      }
   }

   private void onTick0() {
      this.pendingSolution = null;
      label63:
      if (this.solver == null) {
         this.tickInventoryTransactions();
         if (this.remainingFireworkTicks > 0) {
            this.remainingFireworkTicks--;
         }

         if (this.remainingSetBackTicks > 0) {
            this.remainingSetBackTicks--;
         }

         if (!this.getAttachedFirework().isPresent()) {
            this.minimumBoostTicks = 0;
         }

         this.clearLines.clear();
         this.blockedLines.clear();
         this.visiblePath = null;
         this.simulationLine = null;
         this.aimPos = null;
         NetherPath var1 = this.pathManager.getPath();
         if (!var1.isEmpty()) {
            if (this.destination == null) {
               this.pathManager.clear();
            } else {
               this.bsi = new BlockStateInterface(this.ctx);
               this.pathManager.tick();
               int var2 = this.pathManager.getNear();
               this.visiblePath = var1.subList(Math.max(var2 - 30, 0), Math.min(var2 + 100, var1.size()));
            }
         }
      } else {
         if (this.solver.isDone()) {
            try {
               this.pendingSolution = this.solver.get();
            } catch (Exception var5) {
            } finally {
               this.solver = null;
            }
         } else {
            this.solver.cancel(true);
            this.solver = null;
         }
         break label63;
      }
   }

   public void tick() {
      if (!this.pathManager.getPath().isEmpty()) {
         this.trySwapElytra();
         if (this.ctx.player().horizontalCollision) {
            this.logVerbose("hbonk");
         }

         if (this.ctx.player().verticalCollision) {
            this.logVerbose("vbonk");
         }

         ElytraBehavior.SolverContext var1 = new ElytraBehavior.SolverContext(false);
         this.solveNextTick = true;
         ElytraBehavior.Solution var2;
         if (this.pendingSolution != null && this.pendingSolution.context.equals(var1)) {
            var2 = this.pendingSolution;
         } else {
            var2 = this.solveAngles(var1);
         }

         if (this.deployedFireworkLastTick) {
            this.nextTickBoostCounter[var1.boost.isBoosted() ? 1 : 0]++;
            this.deployedFireworkLastTick = false;
         }

         boolean var3 = this.ctx.player().isInLava();
         if (var3) {
            this.baritone.getInputOverrideHandler().setInputForceState(Input.JUMP, true);
         }

         if (var2 == null) {
            this.logVerbose("no solution");
         } else {
            this.baritone.getLookBehavior().updateTarget(var2.rotation, false);
            if (!var2.solvedPitch) {
               this.logVerbose("no pitch solution, probably gonna crash in a few ticks LOL!!!");
            } else {
               this.aimPos = new BetterBlockPos(var2.goingTo.x, var2.goingTo.y, var2.goingTo.z);
               this.tickUseFireworks(var2.context.start, var2.goingTo, var2.context.boost.isBoosted(), var2.forceUseFirework || var3);
            }
         }
      }
   }

   public void onPostTick(TickEvent var1) {
      if (var1.getType() == TickEvent.Type.IN && this.solveNextTick) {
         this.pathManager.updatePlayerNear();
         ElytraBehavior.SolverContext var2 = new ElytraBehavior.SolverContext(true);
         this.solver = this.solverExecutor.submit(() -> {
            this.npfContext.acquireReadLock();

            try {
               return this.solveAngles(var2);
            } finally {
               this.npfContext.releaseReadLock();
            }
         });
         this.solveNextTick = false;
      }
   }

   private ElytraBehavior.Solution solveAngles(ElytraBehavior.SolverContext var1) {
      NetherPath var2 = var1.path;
      int var3 = this.landingMode ? var2.size() - 1 : var1.playerNear;
      Vec3 var4 = var1.start;
      ElytraBehavior.Solution var5 = null;

      for (int var6 = 0; var6 < 3; var6++) {
         int[] var7 = var1.boost.isBoosted() ? new int[]{20, 10, 5, 0} : new int[]{0};
         int var8 = var6 == 0 ? 2 : 3;
         int var9 = var3;

         for (int var10 = Math.min(var3 + 20, var2.size() - 1); var10 >= var9; var10--) {
            if (Thread.interrupted()) {
               return null;
            }

            ArrayList<Pair> var11 = new ArrayList<>();

            for (int var15 : var7) {
               if (var6 != 0 && var10 != var9) {
                  if (var6 == 1) {
                     double[] var27 = new double[]{1.0, 0.75, 0.5, 0.25};

                     for (double var34 : var27) {
                        Vec3 var22 = var34 == 1.0 ? var2.getVec(var10) : var2.getVec(var10).scale(var34).add(var2.getVec(var10 - 1).scale(1.0 - var34));
                        var11.add(new Pair<>(var22, var15));
                     }
                  } else {
                     Vec3 var16 = var2.getVec(var10).subtract(var2.getVec(var10 - 1));
                     int var17 = BaritoneMath.fastFloor(var16.length());
                     Vec3 var18 = var16.normalize();
                     Vec3 var19 = var2.getVec(var10);

                     for (int var20 = 0; var20 < var17; var20++) {
                        var11.add(new Pair<>(var19, var15));
                        var19 = var19.subtract(var18);
                     }
                  }
               } else {
                  var11.add(new Pair<>(var2.getVec(var10), var15));
               }
            }

            for (Pair var24 : var11) {
               Integer var25 = (Integer)var24.second();
               Vec3 var26 = ((Vec3)var24.first()).add(0.0, var25.intValue(), 0.0);
               if (this.landingMode) {
                  var26 = var26.add(0.5, 0.5, 0.5);
               }

               if (var25 == 0
                  || var10 + var8 < var2.size()
                     && (
                        var4.distanceTo(var26) < 40.0
                           ? this.clearView(var26, var2.getVec(var10 + var8).add(0.0, var25.intValue(), 0.0), false)
                              && this.clearView(var26, var2.getVec(var10 + var8), false)
                           : this.clearView(var26, var2.getVec(var10), false)
                     )) {
                  double var28 = Baritone.settings().elytraMinimumAvoidance.value;
                  Double var31 = var6 == 2 ? null : var6 == 0 ? 2.0 * var28 : var28;
                  if (this.isHitboxClear(var1, var26, var31)) {
                     float var33 = RotationUtils.calcRotationFromVec3d(var4, var26, this.ctx.playerRotations()).getYaw();
                     var var35 = this.solvePitch(var1, var26, var6);
                     if (var35 != null) {
                        return new ElytraBehavior.Solution(var1, new Rotation(var33, (Float)var35.first()), var26, true, (Boolean)var35.second());
                     }

                     var5 = new ElytraBehavior.Solution(var1, new Rotation(var33, this.ctx.playerRotations().getPitch()), null, false, false);
                  }
               }
            }
         }
      }

      return var5;
   }

   private void tickUseFireworks(Vec3 var1, Vec3 var2, boolean var3, boolean var4) {
      if (this.remainingSetBackTicks > 0) {
         this.logDebug("waiting for elytraFireworkSetbackUseDelay: " + this.remainingSetBackTicks);
      } else if (!this.landingMode) {
         boolean var5 = !Baritone.settings().elytraConserveFireworks.value || this.ctx.player().position().y < var2.y + 5.0;
         double var6 = new Vec3(
               this.ctx.player().getDeltaMovement().x,
               this.ctx.player().position().y < var2.y ? Math.max(0.0, this.ctx.player().getDeltaMovement().y) : this.ctx.player().getDeltaMovement().y,
               this.ctx.player().getDeltaMovement().z
            )
            .lengthSqr();
         double var8 = Baritone.settings().elytraFireworkSpeed.value;
         if (this.remainingFireworkTicks <= 0
            && (
               var4
                  || !var3
                     && var5
                     && (
                        this.ctx.player().position().y < var2.y - 5.0
                           || var1.distanceTo(new Vec3(var2.x + 0.5, this.ctx.player().position().y, var2.z + 0.5)) > 5.0
                     )
                     && var6 < var8 * var8
            )) {
            if (!this.baritone.getInventoryBehavior().throwaway(true, ElytraBehavior::isBoostingFireworks)
               && !this.baritone.getInventoryBehavior().throwaway(true, ElytraBehavior::isFireworks)) {
               this.logDirect("no fireworks");
               return;
            }

            this.logVerbose("attempting to use firework" + (var4 ? " (forced)" : ""));
            this.ctx.playerController().processRightClick(this.ctx.player(), this.ctx.world(), InteractionHand.MAIN_HAND);
            this.minimumBoostTicks = 10 * (1 + getFireworkBoost(this.ctx.player().getItemInHand(InteractionHand.MAIN_HAND)).orElse(0));
            this.remainingFireworkTicks = 10;
            this.deployedFireworkLastTick = true;
         }
      }
   }

   public static boolean isFireworks(ItemStack var0) {
      if (var0.getItem() != Items.FIREWORK_ROCKET) {
         return false;
      }

      Fireworks var1 = var0.get(DataComponents.FIREWORKS);
      return var1 != null && var1.explosions().isEmpty();
   }

   private static boolean isBoostingFireworks(ItemStack var0) {
      return getFireworkBoost(var0).isPresent();
   }

   private static OptionalInt getFireworkBoost(ItemStack var0) {
      Fireworks var1 = var0.get(DataComponents.FIREWORKS);
      return var1 != null && var1.explosions().isEmpty() ? OptionalInt.of(var1.flightDuration()) : OptionalInt.empty();
   }

   private Optional<FireworkRocketEntity> getAttachedFirework() {
      return this.ctx
         .entitiesStream()
         .filter(var0 -> var0 instanceof FireworkRocketEntity)
         .filter(var1 -> Objects.equals(((IFireworkRocketEntity)var1).getBoostedEntity(), this.ctx.player()))
         .map(var0 -> (FireworkRocketEntity)var0)
         .findFirst();
   }

   private boolean isHitboxClear(ElytraBehavior.SolverContext var1, Vec3 var2, Double var3) {
      Vec3 var4 = var1.start;
      boolean var5 = var1.ignoreLava;
      if (!this.clearView(var4, var2, var5)) {
         return false;
      }

      if (var3 == null) {
         return true;
      }

      AABB var6 = var1.boundingBox.inflate(var3);
      double var7 = var2.x - var4.x;
      double var9 = var2.y - var4.y;
      double var11 = var2.z - var4.z;
      double[] var13 = new double[]{
         var6.minX,
         var6.minY,
         var6.minZ,
         var6.minX,
         var6.minY,
         var6.maxZ,
         var6.minX,
         var6.maxY,
         var6.minZ,
         var6.minX,
         var6.maxY,
         var6.maxZ,
         var6.maxX,
         var6.minY,
         var6.minZ,
         var6.maxX,
         var6.minY,
         var6.maxZ,
         var6.maxX,
         var6.maxY,
         var6.minZ,
         var6.maxX,
         var6.maxY,
         var6.maxZ
      };
      double[] var14 = new double[]{
         var6.minX + var7,
         var6.minY + var9,
         var6.minZ + var11,
         var6.minX + var7,
         var6.minY + var9,
         var6.maxZ + var11,
         var6.minX + var7,
         var6.maxY + var9,
         var6.minZ + var11,
         var6.minX + var7,
         var6.maxY + var9,
         var6.maxZ + var11,
         var6.maxX + var7,
         var6.minY + var9,
         var6.minZ + var11,
         var6.maxX + var7,
         var6.minY + var9,
         var6.maxZ + var11,
         var6.maxX + var7,
         var6.maxY + var9,
         var6.minZ + var11,
         var6.maxX + var7,
         var6.maxY + var9,
         var6.maxZ + var11
      };
      if (Baritone.settings().elytraRenderHitboxRaytraces.value) {
         boolean var15 = true;

         for (int var16 = 0; var16 < 8; var16++) {
            Vec3 var17 = new Vec3(var13[var16 * 3], var13[var16 * 3 + 1], var13[var16 * 3 + 2]);
            Vec3 var18 = new Vec3(var14[var16 * 3], var14[var16 * 3 + 1], var14[var16 * 3 + 2]);
            if (!this.clearView(var17, var18, false)) {
               var15 = false;
            }
         }

         return var15;
      } else {
         return this.raytrace(8, var13, var14, 0);
      }
   }

   public boolean clearView(Vec3 var1, Vec3 var2, boolean var3) {
      boolean var4;
      if (!var3) {
         var4 = var1.equals(var2) || this.raytrace(var1, var2);
      } else {
         var4 = this.ctx.world().clip(new ClipContext(var1, var2, Block.COLLIDER, Fluid.NONE, this.ctx.player())).getType() == Type.MISS;
      }

      if (Baritone.settings().elytraRenderRaytraces.value) {
         (var4 ? this.clearLines : this.blockedLines).add(new Pair<>(var1, var2));
      }

      return var4;
   }

   private static FloatArrayList pitchesToSolveFor(float var0, boolean var1) {
      float var2 = var1 ? -90.0F : Math.max(var0 - Baritone.settings().elytraPitchRange.value.intValue(), -89.0F);
      float var3 = var1 ? 90.0F : Math.min(var0 + Baritone.settings().elytraPitchRange.value.intValue(), 89.0F);
      FloatArrayList var4 = new FloatArrayList(BaritoneMath.fastCeil(var3 - var2) + 1);

      for (float var5 = var0; var5 <= var3; var5++) {
         var4.add(var5);
      }

      for (float var6 = var0 - 1.0F; var6 >= var2; var6--) {
         var4.add(var6);
      }

      return var4;
   }

   private Pair<Float, Boolean> solvePitch(ElytraBehavior.SolverContext var1, Vec3 var2, int var3) {
      boolean var4 = var3 == 2;
      float var5 = RotationUtils.calcRotationFromVec3d(var1.start, var2, this.ctx.playerRotations()).getPitch();
      FloatArrayList var6 = pitchesToSolveFor(var5, var4);
      ElytraBehavior.IntTriFunction var7 = (var5x, var6x, var7x) -> this.solvePitch(var1, var2, var3, var6.iterator(), var5x, var6x, var7x);
      ArrayList var8 = new ArrayList();
      if (var1.boost.isBoosted()) {
         int var9 = var1.boost.getGuaranteedBoostTicks();
         if (var9 == 0) {
            int var10 = Math.max(4, 10 - var1.boost.getMaximumBoostTicks());
            var8.add(new ElytraBehavior.IntTriple(var10, 1, 0));
         } else if (var9 <= 5) {
            var8.add(new ElytraBehavior.IntTriple(var9 + 5, var9, 0));
         } else {
            var8.add(new ElytraBehavior.IntTriple(var9 + 1, var9, 0));
         }
      }

      int var13 = var4 ? 3 : (var1.boost.isBoosted() ? Math.max(5, var1.boost.getGuaranteedBoostTicks()) : Baritone.settings().elytraSimulationTicks.value);
      var8.add(new ElytraBehavior.IntTriple(var13, var1.boost.isBoosted() ? var13 : 0, 0));
      Optional var14 = var8.stream()
         .map(var1x -> (ElytraBehavior.PitchResult)var7.apply(((baritone.process.elytra.ElytraBehavior.IntTriple)var1x).first, ((baritone.process.elytra.ElytraBehavior.IntTriple)var1x).second, ((baritone.process.elytra.ElytraBehavior.IntTriple)var1x).third))
         .filter(Objects::nonNull)
         .findFirst();
      if (var14.isPresent()) {
         return new Pair<>(((ElytraBehavior.PitchResult)var14.get()).pitch, false);
      }

      if (var4) {
         ArrayList var11 = new ArrayList();
         var11.add(new ElytraBehavior.IntTriple(var13, 10, 3));
         var11.add(new ElytraBehavior.IntTriple(var13, 10, 2));
         var11.add(new ElytraBehavior.IntTriple(var13, 10, 1));
         Optional var12 = var11.stream()
            .map(var1x -> (ElytraBehavior.PitchResult)var7.apply(((baritone.process.elytra.ElytraBehavior.IntTriple)var1x).first, ((baritone.process.elytra.ElytraBehavior.IntTriple)var1x).second, ((baritone.process.elytra.ElytraBehavior.IntTriple)var1x).third))
            .filter(Objects::nonNull)
            .findFirst();
         if (var12.isPresent()) {
            return new Pair<>(((ElytraBehavior.PitchResult)var12.get()).pitch, true);
         }
      }

      return null;
   }

   private ElytraBehavior.PitchResult solvePitch(ElytraBehavior.SolverContext var1, Vec3 var2, int var3, FloatIterator var4, int var5, int var6, int var7) {
      Vec3 var8 = var2.subtract(var1.start);
      Vec3 var9 = var8.normalize();
      ArrayDeque<ElytraBehavior.PitchResult> var10 = new ArrayDeque<>();

      while (var4.hasNext()) {
         float var11 = var4.nextFloat();
         var var12 = this.simulate(var1, var8, var11, var5, var6, var7);
         if (var12 != null) {
            Vec3 var13 = (Vec3)var12.get(var12.size() - 1);
            double var14 = var9.dot(var13.normalize());
            if (this.landingMode) {
               var14 = -var8.subtract(var13).length();
            }

            ElytraBehavior.PitchResult var16 = (ElytraBehavior.PitchResult)var10.peek();
            if (var16 == null || var14 > var16.dot) {
               var10.push(new ElytraBehavior.PitchResult(var11, var14, var12));
            }
         }
      }

      var var17 = var10.iterator();

      ElytraBehavior.PitchResult var18;
      label38:
      while (true) {
         if (!var17.hasNext()) {
            return null;
         }

         var18 = (ElytraBehavior.PitchResult)var17.next();
         if (var3 < 2) {
            int var19 = var18.steps.size() - 1;

            while (true) {
               if (var19 < 1) {
                  break label38;
               }

               if (!this.clearView(var1.start.add(var18.steps.get(var19)), var2, var1.ignoreLava)) {
                  break;
               }

               var19--;
            }
         } else {
            if (!this.clearView(var1.start.add(var18.steps.get(var18.steps.size() - 1)), var2, var1.ignoreLava)) {
               continue;
            }
            break;
         }
      }

      this.simulationLine = var18.steps;
      return var18;
   }

   private List<Vec3> simulate(ElytraBehavior.SolverContext var1, Vec3 var2, float var3, int var4, int var5, int var6) {
      ITickableAimProcessor var7 = var1.aimProcessor.fork();
      Vec3 var8 = var2;
      Vec3 var9 = var1.motion;
      AABB var10 = var1.boundingBox;
      ArrayList<Vec3> var11 = new ArrayList<>(var4 + 1);
      var11.add(Vec3.ZERO);
      int var12 = var5;

      for (int var13 = 0; var13 < var4 && !(var8.lengthSqr() < 1.0); var13++) {
         Rotation var18 = var7.nextRotation(RotationUtils.calcRotationFromVec3d(Vec3.ZERO, var8, this.ctx.playerRotations()).withPitch(var3));
         Vec3 var19 = RotationUtils.calcLookDirectionFromRotation(var18);
         var9 = step(var9, var19, var18.getPitch());
         var8 = var8.subtract(var9);
         AABB var20 = var10.expandTowards(var9.x, var9.y, var9.z).inflate(0.01);
         int var21 = BaritoneMath.fastFloor(var20.minX);
         int var22 = BaritoneMath.fastCeil(var20.maxX);
         int var23 = BaritoneMath.fastFloor(var20.minY);
         int var24 = BaritoneMath.fastCeil(var20.maxY);
         int var25 = BaritoneMath.fastFloor(var20.minZ);
         int var26 = BaritoneMath.fastCeil(var20.maxZ);

         for (int var27 = var21; var27 < var22; var27++) {
            for (int var28 = var23; var28 < var24; var28++) {
               for (int var29 = var25; var29 < var26; var29++) {
                  if (!this.passable(var27, var28, var29, var1.ignoreLava)) {
                     return null;
                  }
               }
            }
         }

         var10 = var10.move(var9);
         var11.add(((Vec3)var11.get(var11.size() - 1)).add(var9));
         if (var13 >= var6 && var12-- > 0) {
            var9 = var9.add(
               var19.x * 0.1 + (var19.x * 1.5 - var9.x) * 0.5, var19.y * 0.1 + (var19.y * 1.5 - var9.y) * 0.5, var19.z * 0.1 + (var19.z * 1.5 - var9.z) * 0.5
            );
         }
      }

      return var11;
   }

   private static Vec3 step(Vec3 var0, Vec3 var1, float var2) {
      double var3 = var0.x;
      double var5 = var0.y;
      double var7 = var0.z;
      float var9 = var2 * (float) (Math.PI / 180.0);
      double var10 = Math.sqrt(var1.x * var1.x + var1.z * var1.z);
      double var12 = Math.sqrt(var3 * var3 + var7 * var7);
      double var14 = var1.length();
      float var16 = Mth.cos(var9);
      var16 = (float)((double)var16 * var16 * Math.min(1.0, var14 / 0.4));
      var5 += -0.08 + var16 * 0.06;
      if (var5 < 0.0 && var10 > 0.0) {
         double var17 = var5 * -0.1 * var16;
         var5 += var17;
         var3 += var1.x * var17 / var10;
         var7 += var1.z * var17 / var10;
      }

      if (var9 < 0.0F) {
         double var24 = var12 * -Mth.sin(var9) * 0.04;
         var5 += var24 * 3.2;
         var3 -= var1.x * var24 / var10;
         var7 -= var1.z * var24 / var10;
      }

      if (var10 > 0.0) {
         var3 += (var1.x / var10 * var12 - var3) * 0.1;
         var7 += (var1.z / var10 * var12 - var7) * 0.1;
      }

      var3 *= 0.99F;
      var5 *= 0.98F;
      var7 *= 0.99F;
      return new Vec3(var3, var5, var7);
   }

   private boolean passable(int var1, int var2, int var3, boolean var4) {
      if (!var4) {
         return this.passable(var1, var2, var3);
      }

      BlockState var5 = this.bsi.get0(var1, var2, var3);
      return var5.getBlock() instanceof AirBlock || MovementHelper.isLava(var5);
   }

   private void tickInventoryTransactions() {
      if (this.invTickCountdown <= 0) {
         Runnable var1 = this.invTransactionQueue.poll();
         if (var1 != null) {
            var1.run();
            this.invTickCountdown = Baritone.settings().ticksBetweenInventoryMoves.value;
         }
      }

      if (this.invTickCountdown > 0) {
         this.invTickCountdown--;
      }
   }

   private void queueWindowClick(int var1, int var2, int var3, ContainerInput var4) {
      this.invTransactionQueue.add(() -> this.ctx.playerController().windowClick(var1, var2, var3, var4, this.ctx.player()));
   }

   private int findGoodElytra() {
      var var1 = this.ctx.player().getInventory().getNonEquipmentItems();

      for (int var2 = 0; var2 < var1.size(); var2++) {
         ItemStack var3 = (ItemStack)var1.get(var2);
         if (var3.getItem() == Items.ELYTRA && var3.getMaxDamage() - var3.getDamageValue() > Baritone.settings().elytraMinimumDurability.value) {
            return var2;
         }
      }

      return -1;
   }

   private void trySwapElytra() {
      if (Baritone.settings().elytraAutoSwap.value && this.invTransactionQueue.isEmpty()) {
         ItemStack var1 = this.ctx.player().getItemBySlot(EquipmentSlot.CHEST);
         if (var1.getItem() == Items.ELYTRA && var1.getMaxDamage() - var1.getDamageValue() <= Baritone.settings().elytraMinimumDurability.value) {
            int var2 = this.findGoodElytra();
            if (var2 != -1) {
               int var4 = var2 < 9 ? var2 + 36 : var2;
               this.queueWindowClick(this.ctx.player().inventoryMenu.containerId, var4, 0, ContainerInput.PICKUP);
               this.queueWindowClick(this.ctx.player().inventoryMenu.containerId, 6, 0, ContainerInput.PICKUP);
               this.queueWindowClick(this.ctx.player().inventoryMenu.containerId, var4, 0, ContainerInput.PICKUP);
            }
         }
      }
   }

   void logVerbose(String var1) {
      if (Baritone.settings().elytraChatSpam.value) {
         this.logDebug(var1);
      }
   }

   private BetterBlockPos fixDestination(BetterBlockPos var1) {
      if (this.ctx.world().dimension() == Level.NETHER) {
         if (this.ctx.player().getY() >= 128.0 && var1.y < 128) {
            return new BetterBlockPos(var1.x, 128, var1.z);
         }

         if (this.ctx.player().getY() < 128.0 && var1.y >= 128) {
            return new BetterBlockPos(var1.x, 64, var1.z);
         }
      }

      return var1;
   }

   private BetterBlockPos destinationFixed() {
      return this.fixDestination(this.destination);
   }

   public boolean raytrace(double var1, double var3, double var5, double var7, double var9, double var11) {
      int var13 = this.npfContext.getMaxHeight() + this.ctx.world().getMinY();
      int var14 = this.ctx.world().getMinY();
      boolean var15 = var3 >= var13 || var9 >= var13 || var3 < var14 || var9 < var14;
      if (var15) {
         Vec3 var16 = new Vec3(var1, var3, var5);
         Vec3 var17 = new Vec3(var7, var9, var11);
         return this.ctx.world().clip(new ClipContext(var16, var17, Block.COLLIDER, Fluid.NONE, this.ctx.player())).getType() == Type.MISS;
      } else {
         return this.npfContext.raytrace(var1, var3, var5, var7, var9, var11);
      }
   }

   public boolean raytrace(Vec3 var1, Vec3 var2) {
      int var3 = this.npfContext.getMaxHeight() + this.ctx.world().getMinY();
      int var4 = this.ctx.world().getMinY();
      boolean var5 = var1.y >= var3 || var2.y >= var3 || var1.y < var4 || var2.y < var4;
      return var5
         ? this.ctx.world().clip(new ClipContext(var1, var2, Block.COLLIDER, Fluid.NONE, this.ctx.player())).getType() == Type.MISS
         : this.npfContext.raytrace(var1.x, var1.y, var1.z, var2.x, var2.y, var2.z);
   }

   public boolean raytrace(int var1, double[] var2, double[] var3, int var4) {
      if (var2.length == var1 * 3 && var2.length == var3.length) {
         int var5 = this.npfContext.getMaxHeight() + this.ctx.world().getMinY();
         boolean var6 = false;

         for (byte var7 = 1; var7 < var2.length; var7 += 3) {
            if (var2[var7] >= var5 || var2[var7] < this.ctx.world().getMinY() || var3[var7] >= var5 || var3[var7] < this.ctx.world().getMinY()) {
               var6 = true;
               break;
            }
         }

         if (var6) {
            for (int var10 = 0; var10 < var1; var10++) {
               Vec3 var8 = new Vec3(var2[var10 * 3], var2[var10 * 3 + 1], var2[var10 * 3 + 2]);
               Vec3 var9 = new Vec3(var3[var10 * 3], var3[var10 * 3 + 1], var3[var10 * 3 + 2]);
               if (this.ctx.world().clip(new ClipContext(var8, var9, Block.COLLIDER, Fluid.NONE, this.ctx.player())).getType() != Type.MISS) {
                  return false;
               }
            }

            return true;
         } else {
            return this.npfContext.raytrace(var1, var2, var3, var4);
         }
      } else {
         throw new IllegalArgumentException("Expected source and dst to have length of " + var1 * 3);
      }
   }

   public boolean passable(int var1, int var2, int var3) {
      return var2 < this.ctx.world().getMaxY() && var2 >= this.ctx.world().getMinY() ? this.npfContext.passable(var1, var2, var3) : true;
   }

   private static final class FireworkBoost {
      private final Integer fireworkTicksExisted;
      private final int minimumBoostTicks;
      private final int maximumBoostTicks;

      public FireworkBoost(Integer var1, int var2) {
         this.fireworkTicksExisted = var1;
         this.minimumBoostTicks = var2;
         this.maximumBoostTicks = var2 + 11;
      }

      public boolean isBoosted() {
         return this.fireworkTicksExisted != null;
      }

      public int getGuaranteedBoostTicks() {
         return this.isBoosted() ? Math.max(0, this.minimumBoostTicks - this.fireworkTicksExisted) : 0;
      }

      public int getMaximumBoostTicks() {
         return this.isBoosted() ? Math.max(0, this.maximumBoostTicks - this.fireworkTicksExisted) : 0;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && var1.getClass() == ElytraBehavior.FireworkBoost.class) {
            ElytraBehavior.FireworkBoost var2 = (ElytraBehavior.FireworkBoost)var1;
            return !this.isBoosted() && !var2.isBoosted()
               ? true
               : Objects.equals(this.fireworkTicksExisted, var2.fireworkTicksExisted)
                  && this.minimumBoostTicks == var2.minimumBoostTicks
                  && this.maximumBoostTicks == var2.maximumBoostTicks;
         } else {
            return false;
         }
      }
   }

   @FunctionalInterface
   private interface IntTriFunction<T> {
      T apply(int var1, int var2, int var3);
   }

   private static final class IntTriple {
      public final int first;
      public final int second;
      public final int third;

      public IntTriple(int var1, int var2, int var3) {
         this.first = var1;
         this.second = var2;
         this.third = var3;
      }
   }

   public final class PathManager {
      public NetherPath path;
      private boolean completePath;
      private boolean recalculating;
      private int maxPlayerNear;
      private int ticksNearUnchanged;
      private int playerNear;

      public PathManager() {
         this.clear();
      }

      public void tick() {
         this.updatePlayerNear();
         int var1 = this.maxPlayerNear;
         this.maxPlayerNear = Math.max(this.maxPlayerNear, this.playerNear);
         if (this.maxPlayerNear == var1 && ElytraBehavior.this.ctx.player().isFallFlying()) {
            this.ticksNearUnchanged++;
         } else {
            this.ticksNearUnchanged = 0;
         }

         ElytraBehavior.this.ctx.world().dimensionType().minY();
         ElytraBehavior.this.ctx.playerFeet();
         ElytraBehavior.this.npfContext.acquireReadLock();

         try {
            this.pathfindAroundObstacles();
         } finally {
            ElytraBehavior.this.npfContext.releaseReadLock();
         }

         this.attemptNextSegment();
      }

      public CompletableFuture<Void> pathToDestination() {
         return this.pathToDestination(ElytraBehavior.this.ctx.playerFeet());
      }

      public CompletableFuture<Void> pathToDestination(BlockPos var1) {
         long var2 = System.nanoTime();
         return this.path0(var1, ElytraBehavior.this.destinationFixed(), UnaryOperator.identity()).thenRun(() -> {
            double var3 = this.path.get(0).distanceTo(this.path.get(this.path.size() - 1));
            if (this.completePath) {
               ElytraBehavior.this.logVerbose(String.format("Computed path (%.1f blocks in %.4f seconds)", var3, (System.nanoTime() - var2) / 1.0E9));
            } else {
               ElytraBehavior.this.logVerbose(String.format("Computed segment (Next %.1f blocks in %.4f seconds)", var3, (System.nanoTime() - var2) / 1.0E9));
            }
         }).whenComplete((var1x, var2x) -> {
            this.recalculating = false;
            if (var2x != null) {
               Throwable var3 = var2x.getCause();
               if (var3 instanceof PathCalculationException) {
                  ElytraBehavior.this.logDirect("Failed to compute path to destination");
               } else {
                  ElytraBehavior.this.logUnhandledException(var3);
               }
            }
         });
      }

      public CompletableFuture<Void> pathRecalcSegment(OptionalInt var1) {
         if (this.recalculating) {
            throw new IllegalStateException("already recalculating");
         }

         this.recalculating = true;
         List var2 = var1.isPresent() ? this.path.subList(var1.getAsInt() + 1, this.path.size()) : Collections.emptyList();
         boolean var3 = this.completePath;
         return this.path0(
               ElytraBehavior.this.ctx.playerFeet(),
               var1.isPresent() ? ElytraBehavior.this.fixDestination(this.path.get(var1.getAsInt())) : ElytraBehavior.this.destinationFixed(),
               var3x -> var3x.append(var2.stream(), var3 || var3x.isFinished() && !var1.isPresent())
            )
            .whenComplete((var1x, var2x) -> {
               this.recalculating = false;
               if (var2x != null) {
                  Throwable var3x = var2x.getCause();
                  if (var3x instanceof PathCalculationException) {
                     ElytraBehavior.this.logDirect("Failed to recompute segment");
                  } else {
                     ElytraBehavior.this.logUnhandledException(var3x);
                  }
               }
            });
      }

      public void pathNextSegment(int var1) {
         if (!this.recalculating) {
            this.recalculating = true;
            var var2 = this.path.subList(0, var1 + 1);
            long var3 = System.nanoTime();
            BetterBlockPos var5 = this.path.get(var1);
            this.path0(var5, ElytraBehavior.this.destinationFixed(), var1x -> var1x.prepend(var2.stream()))
               .thenRun(
                  () -> {
                     int var4 = this.path.size() - var2.size() - 1;
                     double var5x = var4 > 0 ? this.path.get(0).distanceTo(this.path.get(var4)) : 0.0;
                     if (this.completePath) {
                        ElytraBehavior.this.logVerbose(String.format("Computed path (%.1f blocks in %.4f seconds)", var5x, (System.nanoTime() - var3) / 1.0E9));
                     } else {
                        ElytraBehavior.this.logVerbose(
                           String.format("Computed segment (Next %.1f blocks in %.4f seconds)", var5x, (System.nanoTime() - var3) / 1.0E9)
                        );
                     }
                  }
               )
               .whenComplete(
                  (var2x, var3x) -> {
                     this.recalculating = false;
                     if (var3x != null) {
                        Throwable var4 = var3x.getCause();
                        if (var4 instanceof PathCalculationException) {
                           ElytraBehavior.this.logDirect("Failed to compute next segment");
                           if (ElytraBehavior.this.ctx.player().distanceToSqr(Vec3.atCenterOf(var5)) < 256.0) {
                              ElytraBehavior.this.logVerbose(
                                 "Player is near the segment start, therefore repeating this calculation is pointless. Marking as complete"
                              );
                              this.completePath = true;
                           }
                        } else {
                           ElytraBehavior.this.logUnhandledException(var4);
                        }
                     }
                  }
               );
         }
      }

      public void clear() {
         this.path = NetherPath.emptyPath();
         this.completePath = true;
         this.recalculating = false;
         this.playerNear = 0;
         this.ticksNearUnchanged = 0;
         this.maxPlayerNear = 0;
      }

      private void setPath(UnpackedSegment var1) {
         var var2 = var1.collect();
         if (ElytraBehavior.this.appendDestination) {
            BetterBlockPos var3 = ElytraBehavior.this.destinationFixed();
            BlockPos var4 = !var2.isEmpty() ? (BlockPos)var2.get(var2.size() - 1) : null;
            if (var4 != null && ElytraBehavior.this.clearView(Vec3.atLowerCornerOf(var3), Vec3.atLowerCornerOf(var4), false)) {
               var2.add(new BetterBlockPos(var3));
            } else {
               ElytraBehavior.this.logDirect("unable to land at " + var3);
               ElytraBehavior.this.process.landingSpotIsBad(new BetterBlockPos(var3));
            }
         }

         this.path = new NetherPath(var2);
         this.completePath = var1.isFinished();
         this.playerNear = 0;
         this.ticksNearUnchanged = 0;
         this.maxPlayerNear = 0;
      }

      public NetherPath getPath() {
         return this.path;
      }

      public int getNear() {
         return this.playerNear;
      }

      private CompletableFuture<Void> path0(BlockPos var1, BlockPos var2, UnaryOperator<UnpackedSegment> var3) {
         return ElytraBehavior.this.pathFinder
            .pathFindAsync(var1, var2)
            .thenApply(var3)
            .thenAcceptAsync(this::setPath, ElytraBehavior.this.ctx.minecraft()::execute);
      }

      private void pathfindAroundObstacles() {
         if (!this.recalculating) {
            int var1 = this.playerNear;
            int var2 = this.playerNear;

            while (var2 < this.path.size() && ElytraBehavior.this.npfContext.hasChunk(ChunkPos.containing(this.path.get(var2)))) {
               var2++;
            }

            if (var1 < var2) {
               BetterBlockPos var3 = this.path.get(var1);
               if (ElytraBehavior.this.passable(var3.x, var3.y, var3.z, false)) {
                  if (ElytraBehavior.this.process.state != ElytraProcess.State.LANDING && this.ticksNearUnchanged > 100) {
                     this.pathRecalcSegment(OptionalInt.of(var2 - 1))
                        .thenRun(() -> ElytraBehavior.this.logVerbose("Recalculating segment, no progress in last 100 ticks"));
                     this.ticksNearUnchanged = 0;
                  } else {
                     boolean var4 = false;

                     for (int var5 = var1; var5 < var2 - 1; var5++) {
                        if (ElytraBehavior.this.clearView(ElytraBehavior.this.ctx.playerFeetAsVec(), this.path.getVec(var5), false)
                           || ElytraBehavior.this.clearView(ElytraBehavior.this.ctx.playerHead(), this.path.getVec(var5), false)) {
                           var4 = true;
                        }

                        if (!ElytraBehavior.this.clearView(this.path.getVec(var5), this.path.getVec(var5 + 1), false)) {
                           BetterBlockPos var7 = ElytraBehavior.this.destinationFixed();
                           OptionalInt var6;
                           if (this.path.get(var2 - 1).distanceSq(var7) < ElytraBehavior.this.ctx.playerFeet().distanceSq(var7)) {
                              var6 = OptionalInt.of(var2 - 1);
                           } else {
                              var6 = OptionalInt.empty();
                           }

                           BetterBlockPos var8 = this.path.get(var5);
                           double var9 = ElytraBehavior.this.ctx.playerFeet().distanceTo(this.path.get(var6.orElse(this.path.size() - 1)));
                           long var11 = System.nanoTime();
                           this.pathRecalcSegment(var6)
                              .thenRun(
                                 () -> ElytraBehavior.this.logVerbose(
                                    String.format(
                                       "Recalculated segment around path blockage near %s %s %s (next %.1f blocks in %.4f seconds)",
                                       SettingsUtil.maybeCensor(var8.x),
                                       SettingsUtil.maybeCensor(var8.y),
                                       SettingsUtil.maybeCensor(var8.z),
                                       var9,
                                       (System.nanoTime() - var11) / 1.0E9
                                    )
                                 )
                              );
                           return;
                        }
                     }

                     if (!var4 && var1 < var2 - 2 && ElytraBehavior.this.process.state != ElytraProcess.State.GET_TO_JUMP) {
                        this.pathRecalcSegment(OptionalInt.of(var2 - 1))
                           .thenRun(() -> ElytraBehavior.this.logVerbose("Recalculated segment since no path points were visible"));
                     }
                  }
               }
            }
         }
      }

      private void attemptNextSegment() {
         if (!this.recalculating) {
            int var1 = this.path.size() - 1;
            BetterBlockPos var2 = this.path.get(this.path.size() - 1);
            if (!this.completePath && ElytraBehavior.this.ctx.world().getChunkSource().hasChunk(var2.x >> 4, var2.z >> 4)) {
               this.pathNextSegment(var1);
            }
         }
      }

      public void updatePlayerNear() {
         if (!this.path.isEmpty()) {
            int var1 = this.playerNear;
            BetterBlockPos var2 = ElytraBehavior.this.ctx.playerFeet();

            for (int var3 = var1; var3 >= Math.max(var1 - 1000, 0); var3 -= 10) {
               if (this.path.get(var3).distanceSq(var2) < this.path.get(var1).distanceSq(var2)) {
                  var1 = var3;
               }
            }

            for (int var4 = var1; var4 < Math.min(var1 + 1000, this.path.size()); var4 += 10) {
               if (this.path.get(var4).distanceSq(var2) < this.path.get(var1).distanceSq(var2)) {
                  var1 = var4;
               }
            }

            for (int var5 = var1; var5 >= Math.max(var1 - 50, 0); var5--) {
               if (this.path.get(var5).distanceSq(var2) < this.path.get(var1).distanceSq(var2)) {
                  var1 = var5;
               }
            }

            for (int var6 = var1; var6 < Math.min(var1 + 50, this.path.size()); var6++) {
               if (this.path.get(var6).distanceSq(var2) < this.path.get(var1).distanceSq(var2)) {
                  var1 = var6;
               }
            }

            this.playerNear = var1;
         }
      }

      public boolean isComplete() {
         return this.completePath;
      }
   }

   private static final class PitchResult {
      public final float pitch;
      public final double dot;
      public final List<Vec3> steps;

      public PitchResult(float var1, double var2, List<Vec3> var4) {
         this.pitch = var1;
         this.dot = var2;
         this.steps = var4;
      }
   }

   private static final class Solution {
      public final ElytraBehavior.SolverContext context;
      public final Rotation rotation;
      public final Vec3 goingTo;
      public final boolean solvedPitch;
      public final boolean forceUseFirework;

      public Solution(ElytraBehavior.SolverContext var1, Rotation var2, Vec3 var3, boolean var4, boolean var5) {
         this.context = var1;
         this.rotation = var2;
         this.goingTo = var3;
         this.solvedPitch = var4;
         this.forceUseFirework = var5;
      }
   }

   private final class SolverContext {
      public final NetherPath path = ElytraBehavior.this.pathManager.getPath();
      public final int playerNear = ElytraBehavior.this.pathManager.getNear();
      public final Vec3 start = ElytraBehavior.this.ctx.playerFeetAsVec();
      public final Vec3 motion = ElytraBehavior.this.ctx.playerMotion();
      public final AABB boundingBox = ElytraBehavior.this.ctx.player().getBoundingBox();
      public final boolean ignoreLava = ElytraBehavior.this.ctx.player().isInLava();
      public final ElytraBehavior.FireworkBoost boost;
      public final IAimProcessor aimProcessor;

      public SolverContext(boolean var2) {
         Integer var3;
         if (var2 && ElytraBehavior.this.deployedFireworkLastTick) {
            int[] var4 = ElytraBehavior.this.nextTickBoostCounter;
            var3 = var4[1] > var4[0] ? 0 : null;
         } else {
            var3 = ElytraBehavior.this.getAttachedFirework().map(var0 -> var0.tickCount).orElse(null);
         }

         this.boost = new ElytraBehavior.FireworkBoost(var3, ElytraBehavior.this.minimumBoostTicks);
         ITickableAimProcessor var5 = ElytraBehavior.this.baritone.getLookBehavior().getAimProcessor().fork();
         if (var2) {
            var5.advance(1);
         }

         this.aimProcessor = var5;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && var1.getClass() == ElytraBehavior.SolverContext.class) {
            ElytraBehavior.SolverContext var2 = (ElytraBehavior.SolverContext)var1;
            return this.path == var2.path
               && this.playerNear == var2.playerNear
               && Objects.equals(this.start, var2.start)
               && Objects.equals(this.motion, var2.motion)
               && Objects.equals(this.boundingBox, var2.boundingBox)
               && this.ignoreLava == var2.ignoreLava
               && Objects.equals(this.boost, var2.boost);
         } else {
            return false;
         }
      }
   }
}
