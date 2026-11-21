package baritone.process;

import baritone.Baritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.process.IBuilderProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.schematic.FillSchematic;
import baritone.api.schematic.ISchematic;
import baritone.api.schematic.IStaticSchematic;
import baritone.api.schematic.MaskSchematic;
import baritone.api.schematic.MirroredSchematic;
import baritone.api.schematic.RotatedSchematic;
import baritone.api.schematic.SubstituteSchematic;
import baritone.api.schematic.format.ISchematicFormat;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.SettingsUtil;
import baritone.api.utils.Tuple;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.MovementHelper;
import baritone.utils.BaritoneProcessHelper;
import baritone.utils.BlockStateInterface;
import baritone.utils.PathingCommandContext;
import baritone.utils.schematic.MapArtSchematic;
import baritone.utils.schematic.SchematicSystem;
import baritone.utils.schematic.SelectionSchematic;
import baritone.utils.schematic.litematica.LitematicaHelper;
import baritone.utils.schematic.schematica.SchematicaHelper;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BuilderProcess extends BaritoneProcessHelper implements IBuilderProcess {
   private static final Set<Property<?>> ORIENTATION_PROPS = ImmutableSet.of(
      RotatedPillarBlock.AXIS,
      HorizontalDirectionalBlock.FACING,
      StairBlock.FACING,
      StairBlock.HALF,
      StairBlock.SHAPE,
      PipeBlock.NORTH,
      new Property[]{PipeBlock.EAST, PipeBlock.SOUTH, PipeBlock.WEST, PipeBlock.UP, TrapDoorBlock.OPEN, TrapDoorBlock.HALF}
   );
   private HashSet<BetterBlockPos> incorrectPositions;
   private LongOpenHashSet observedCompleted;
   private String name;
   private ISchematic realSchematic;
   private ISchematic schematic;
   private Vec3i origin;
   private int ticks;
   private boolean paused;
   private int layer;
   private int numRepeats;
   private List<BlockState> approxPlaceable;
   public int stopAtHeight = 0;

   public BuilderProcess(Baritone var1) {
      super(var1);
   }

   @Override
   public void build(String var1, ISchematic var2, Vec3i var3) {
      this.name = var1;
      this.schematic = var2;
      this.realSchematic = null;
      boolean var4 = var2 instanceof SelectionSchematic;
      if (!Baritone.settings().buildSubstitutes.value.isEmpty()) {
         this.schematic = new SubstituteSchematic(this.schematic, Baritone.settings().buildSubstitutes.value);
      }

      if (Baritone.settings().buildSchematicMirror.value != Mirror.NONE) {
         this.schematic = new MirroredSchematic(this.schematic, Baritone.settings().buildSchematicMirror.value);
      }

      if (Baritone.settings().buildSchematicRotation.value != Rotation.NONE) {
         this.schematic = new RotatedSchematic(this.schematic, Baritone.settings().buildSchematicRotation.value);
      }

      this.schematic = new MaskSchematic(this.schematic) {
         @Override
         public boolean partOfMask(int var1, int var2x, int var3x, BlockState var4x) {
            return !Baritone.settings().buildSkipBlocks.value.contains(this.desiredState(var1, var2x, var3x, var4x, Collections.emptyList()).getBlock());
         }
      };
      int var5 = var3.getX();
      int var6 = var3.getY();
      int var7 = var3.getZ();
      if (Baritone.settings().schematicOrientationX.value) {
         var5 += var2.widthX();
      }

      if (Baritone.settings().schematicOrientationY.value) {
         var6 += var2.heightY();
      }

      if (Baritone.settings().schematicOrientationZ.value) {
         var7 += var2.lengthZ();
      }

      this.origin = new Vec3i(var5, var6, var7);
      this.paused = false;
      this.layer = Baritone.settings().startAtLayer.value;
      this.stopAtHeight = var2.heightY();
      if (Baritone.settings().buildOnlySelection.value && var4) {
         if (this.baritone.getSelectionManager().getSelections().length == 0) {
            this.logDirect("Poor little kitten forgot to set a selection while BuildOnlySelection is true");
            this.stopAtHeight = 0;
         } else if (Baritone.settings().buildInLayers.value) {
            OptionalInt var8 = Stream.of(this.baritone.getSelectionManager().getSelections()).mapToInt(var0 -> var0.min().y).min();
            OptionalInt var9 = Stream.of(this.baritone.getSelectionManager().getSelections()).mapToInt(var0 -> var0.max().y).max();
            if (var8.isPresent() && var9.isPresent()) {
               int var10 = Baritone.settings().layerOrder.value ? var6 + var2.heightY() - var9.getAsInt() : var8.getAsInt() - var6;
               this.stopAtHeight = (Baritone.settings().layerOrder.value ? var6 + var2.heightY() - var8.getAsInt() : var9.getAsInt() - var6) + 1;
               this.layer = Math.max(this.layer, var10 / Baritone.settings().layerHeight.value);
               this.logDebug(String.format("Schematic starts at y=%s with height %s", var6, var2.heightY()));
               this.logDebug(String.format("Selection starts at y=%s and ends at y=%s", var8.getAsInt(), var9.getAsInt()));
               this.logDebug(String.format("Considering relevant height %s - %s", var10, this.stopAtHeight));
            }
         }
      }

      this.numRepeats = 0;
      this.observedCompleted = new LongOpenHashSet();
      this.incorrectPositions = null;
   }

   @Override
   public void resume() {
      this.paused = false;
   }

   @Override
   public void pause() {
      this.paused = true;
   }

   @Override
   public boolean isPaused() {
      return this.paused;
   }

   @Override
   public boolean build(String var1, File var2, Vec3i var3) {
      var var4 = SchematicSystem.INSTANCE.getByFile(var2);
      if (!var4.isPresent()) {
         return false;
      }

      IStaticSchematic var5;
      try {
         var5 = ((ISchematicFormat)var4.get()).parse(new FileInputStream(var2));
      } catch (Exception var7) {
         var7.printStackTrace();
         return false;
      }

      ISchematic var6 = this.applyMapArtAndSelection(var3, var5);
      this.build(var1, var6, var3);
      return true;
   }

   private ISchematic applyMapArtAndSelection(Vec3i var1, IStaticSchematic var2) {
      ISchematic var3 = var2;
      if (Baritone.settings().mapArtMode.value) {
         var3 = new MapArtSchematic(var2);
      }

      if (Baritone.settings().buildOnlySelection.value) {
         var3 = new SelectionSchematic(var3, var1, this.baritone.getSelectionManager().getSelections());
      }

      return var3;
   }

   @Override
   public void buildOpenSchematic() {
      if (SchematicaHelper.isSchematicaPresent()) {
         var var1 = SchematicaHelper.getOpenSchematic();
         if (var1.isPresent()) {
            IStaticSchematic var2 = (IStaticSchematic)((Tuple)var1.get()).getA();
            BlockPos var3 = (BlockPos)((Tuple)var1.get()).getB();
            ISchematic var4 = this.applyMapArtAndSelection(var3, var2);
            this.build(var2.toString(), var4, var3);
         } else {
            this.logDirect("No schematic currently open");
         }
      } else {
         this.logDirect("Schematica is not present");
      }
   }

   @Override
   public void buildOpenLitematic(int var1) {
      if (LitematicaHelper.isLitematicaPresent()) {
         if (LitematicaHelper.hasLoadedSchematic(var1)) {
            var var2 = LitematicaHelper.getSchematic(var1);
            Vec3i var3 = (Vec3i)var2.getB();
            ISchematic var4 = this.applyMapArtAndSelection(var3, (IStaticSchematic)var2.getA());
            this.build(((IStaticSchematic)var2.getA()).toString(), var4, var3);
         } else {
            this.logDirect(String.format("List of placements has no entry %s", var1 + 1));
         }
      } else {
         this.logDirect("Litematica is not present");
      }
   }

   @Override
   public void clearArea(BlockPos var1, BlockPos var2) {
      BlockPos var3 = new BlockPos(Math.min(var1.getX(), var2.getX()), Math.min(var1.getY(), var2.getY()), Math.min(var1.getZ(), var2.getZ()));
      int var4 = Math.abs(var1.getX() - var2.getX()) + 1;
      int var5 = Math.abs(var1.getY() - var2.getY()) + 1;
      int var6 = Math.abs(var1.getZ() - var2.getZ()) + 1;
      this.build("clear area", new FillSchematic(var4, var5, var6, Blocks.AIR.defaultBlockState()), var3);
   }

   @Override
   public List<BlockState> getApproxPlaceable() {
      return new ArrayList<>(this.approxPlaceable);
   }

   @Override
   public boolean isActive() {
      return this.schematic != null;
   }

   public BlockState placeAt(int var1, int var2, int var3, BlockState var4) {
      if (!this.isActive()) {
         return null;
      }

      if (!this.schematic.inSchematic(var1 - this.origin.getX(), var2 - this.origin.getY(), var3 - this.origin.getZ(), var4)) {
         return null;
      }

      BlockState var5 = this.schematic
         .desiredState(var1 - this.origin.getX(), var2 - this.origin.getY(), var3 - this.origin.getZ(), var4, this.approxPlaceable);
      return var5.getBlock() instanceof AirBlock ? null : var5;
   }

   private Optional<Tuple<BetterBlockPos, baritone.api.utils.Rotation>> toBreakNearPlayer(BuilderProcess.BuilderCalculationContext var1) {
      BetterBlockPos var2 = this.ctx.playerFeet();
      BetterBlockPos var3 = this.baritone.getPathingBehavior().pathStart();

      for (int var4 = -5; var4 <= 5; var4++) {
         for (int var5 = Baritone.settings().breakFromAbove.value ? -1 : 0; var5 <= 5; var5++) {
            for (int var6 = -5; var6 <= 5; var6++) {
               int var7 = var2.x + var4;
               int var8 = var2.y + var5;
               int var9 = var2.z + var6;
               if (var5 != -1 || var7 != var3.x || var9 != var3.z) {
                  BlockState var10 = var1.getSchematic(var7, var8, var9, var1.bsi.get0(var7, var8, var9));
                  if (var10 != null) {
                     BlockState var11 = var1.bsi.get0(var7, var8, var9);
                     if (!(var11.getBlock() instanceof AirBlock)
                        && var11.getBlock() != Blocks.WATER
                        && var11.getBlock() != Blocks.LAVA
                        && !valid(var11, var10, false)) {
                        BetterBlockPos var12 = new BetterBlockPos(var7, var8, var9);
                        var var13 = RotationUtils.reachable(this.ctx, var12, this.ctx.playerController().getBlockReachDistance());
                        if (var13.isPresent()) {
                           return Optional.of(new Tuple<>(var12, (baritone.api.utils.Rotation)var13.get()));
                        }
                     }
                  }
               }
            }
         }
      }

      return Optional.empty();
   }

   private Optional<BuilderProcess.Placement> searchForPlacables(BuilderProcess.BuilderCalculationContext var1, List<BlockState> var2) {
      BetterBlockPos var3 = this.ctx.playerFeet();

      for (int var4 = -5; var4 <= 5; var4++) {
         for (int var5 = -5; var5 <= 1; var5++) {
            for (int var6 = -5; var6 <= 5; var6++) {
               int var7 = var3.x + var4;
               int var8 = var3.y + var5;
               int var9 = var3.z + var6;
               BlockState var10 = var1.getSchematic(var7, var8, var9, var1.bsi.get0(var7, var8, var9));
               if (var10 != null) {
                  BlockState var11 = var1.bsi.get0(var7, var8, var9);
                  if (MovementHelper.isReplaceable(var7, var8, var9, var11, var1.bsi)
                     && !valid(var11, var10, false)
                     && (var5 != 1 || !(var1.bsi.get0(var7, var8 + 1, var9).getBlock() instanceof AirBlock))) {
                     var2.add(var10);
                     var var12 = this.possibleToPlace(var10, var7, var8, var9, var1.bsi);
                     if (var12.isPresent()) {
                        return var12;
                     }
                  }
               }
            }
         }
      }

      return Optional.empty();
   }

   public boolean placementPlausible(BlockPos var1, BlockState var2) {
      VoxelShape var3 = var2.getCollisionShape(this.ctx.world(), var1);
      return var3.isEmpty() || this.ctx.world().isUnobstructed(null, var3.move(var1.getX(), var1.getY(), var1.getZ()));
   }

   private Optional<BuilderProcess.Placement> possibleToPlace(BlockState var1, int var2, int var3, int var4, BlockStateInterface var5) {
      for (Direction var9 : Direction.values()) {
         BetterBlockPos var10 = new BetterBlockPos(var2, var3, var4).relative(var9);
         BlockState var11 = var5.get0(var10);
         if (!MovementHelper.isReplaceable(var10.x, var10.y, var10.z, var11, var5)
            && var1.canSurvive(this.ctx.world(), new BetterBlockPos(var2, var3, var4))
            && this.placementPlausible(new BetterBlockPos(var2, var3, var4), var1)) {
            VoxelShape var12 = var11.getShape(this.ctx.world(), var10);
            if (!var12.isEmpty()) {
               AABB var13 = var12.bounds();

               for (Vec3 var17 : aabbSideMultipliers(var9)) {
                  double var18 = var10.x + var13.minX * var17.x + var13.maxX * (1.0 - var17.x);
                  double var20 = var10.y + var13.minY * var17.y + var13.maxY * (1.0 - var17.y);
                  double var22 = var10.z + var13.minZ * var17.z + var13.maxZ * (1.0 - var17.z);
                  baritone.api.utils.Rotation var24 = RotationUtils.calcRotationFromVec3d(
                     RayTraceUtils.inferSneakingEyePosition(this.ctx.player()), new Vec3(var18, var20, var22), this.ctx.playerRotations()
                  );
                  baritone.api.utils.Rotation var25 = this.baritone.getLookBehavior().getAimProcessor().peekRotation(var24);
                  HitResult var26 = RayTraceUtils.rayTraceTowards(this.ctx.player(), var25, this.ctx.playerController().getBlockReachDistance(), true);
                  if (var26 != null
                     && var26.getType() == Type.BLOCK
                     && ((BlockHitResult)var26).getBlockPos().equals(var10)
                     && ((BlockHitResult)var26).getDirection() == var9.getOpposite()) {
                     OptionalInt var27 = this.hasAnyItemThatWouldPlace(var1, var26, var25);
                     if (var27.isPresent()) {
                        return Optional.of(new BuilderProcess.Placement(var27.getAsInt(), var10, var9.getOpposite(), var24));
                     }
                  }
               }
            }
         }
      }

      return Optional.empty();
   }

   private OptionalInt hasAnyItemThatWouldPlace(BlockState var1, HitResult var2, baritone.api.utils.Rotation var3) {
      for (int var4 = 0; var4 < 9; var4++) {
         ItemStack var5 = this.ctx.player().getInventory().getNonEquipmentItems().get(var4);
         if (!var5.isEmpty() && var5.getItem() instanceof BlockItem) {
            float var6 = this.ctx.player().getYRot();
            float var7 = this.ctx.player().getXRot();
            this.ctx.player().setYRot(var3.getYaw());
            this.ctx.player().setXRot(var3.getPitch());
            BlockPlaceContext var8 = new BlockPlaceContext(
               new UseOnContext(this.ctx.world(), this.ctx.player(), InteractionHand.MAIN_HAND, var5, (BlockHitResult)var2) {}
            );
            BlockState var9 = ((BlockItem)var5.getItem()).getBlock().getStateForPlacement(var8);
            this.ctx.player().setYRot(var6);
            this.ctx.player().setXRot(var7);
            if (var9 != null && var8.canPlace() && valid(var9, var1, true)) {
               return OptionalInt.of(var4);
            }
         }
      }

      return OptionalInt.empty();
   }

   private static Vec3[] aabbSideMultipliers(Direction var0) {
      switch (var0) {
         case UP:
            return new Vec3[]{new Vec3(0.5, 1.0, 0.5), new Vec3(0.1, 1.0, 0.5), new Vec3(0.9, 1.0, 0.5), new Vec3(0.5, 1.0, 0.1), new Vec3(0.5, 1.0, 0.9)};
         case DOWN:
            return new Vec3[]{new Vec3(0.5, 0.0, 0.5), new Vec3(0.1, 0.0, 0.5), new Vec3(0.9, 0.0, 0.5), new Vec3(0.5, 0.0, 0.1), new Vec3(0.5, 0.0, 0.9)};
         case NORTH:
         case SOUTH:
         case EAST:
         case WEST:
            double var1 = var0.getStepX() == 0 ? 0.5 : (1 + var0.getStepX()) / 2.0;
            double var3 = var0.getStepZ() == 0 ? 0.5 : (1 + var0.getStepZ()) / 2.0;
            return new Vec3[]{new Vec3(var1, 0.25, var3), new Vec3(var1, 0.75, var3)};
         default:
            throw new IllegalStateException("Unexpected side " + var0);
      }
   }

   @Override
   public PathingCommand onTick(boolean var1, boolean var2) {
      return this.onTick(var1, var2, 0);
   }

   private PathingCommand onTick(boolean var1, boolean var2, int var3) {
      if (var3 > 100) {
         return new PathingCommand(null, PathingCommandType.SET_GOAL_AND_PATH);
      }

      this.approxPlaceable = this.approxPlaceable(36);
      if (this.baritone.getInputOverrideHandler().isInputForcedDown(Input.CLICK_LEFT)) {
         this.ticks = 5;
      } else {
         this.ticks--;
      }

      this.baritone.getInputOverrideHandler().clearAllKeys();
      if (this.paused) {
         return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
      }

      if (Baritone.settings().buildInLayers.value) {
         if (this.realSchematic == null) {
            this.realSchematic = this.schematic;
         }

         final ISchematic var4 = this.realSchematic;
         final int var5;
         final int var6;
         if (Baritone.settings().layerOrder.value) {
            var6 = var4.heightY() - 1;
            var5 = var4.heightY() - this.layer * Baritone.settings().layerHeight.value;
         } else {
            var6 = this.layer * Baritone.settings().layerHeight.value - 1;
            var5 = 0;
         }

         this.schematic = new ISchematic() {
            @Override
            public BlockState desiredState(int var1, int var2x, int var3x, BlockState var4x, List<BlockState> var5x) {
               return var4.desiredState(var1, var2x, var3x, var4x, BuilderProcess.this.approxPlaceable);
            }

            @Override
            public boolean inSchematic(int var1, int var2x, int var3x, BlockState var4x) {
               return ISchematic.super.inSchematic(var1, var2x, var3x, var4x) && var2x >= var5 && var2x <= var6 && var4.inSchematic(var1, var2x, var3x, var4x);
            }

            @Override
            public void reset() {
               var4.reset();
            }

            @Override
            public int widthX() {
               return var4.widthX();
            }

            @Override
            public int heightY() {
               return var4.heightY();
            }

            @Override
            public int lengthZ() {
               return var4.lengthZ();
            }
         };
      }

      BuilderProcess.BuilderCalculationContext var13 = new BuilderProcess.BuilderCalculationContext();
      if (!this.recalc(var13)) {
         if (Baritone.settings().buildInLayers.value && this.layer * Baritone.settings().layerHeight.value < this.stopAtHeight) {
            this.logDirect("Starting layer " + this.layer);
            this.layer++;
            return this.onTick(var1, var2, var3 + 1);
         }

         Vec3i var15 = Baritone.settings().buildRepeat.value;
         int var18 = Baritone.settings().buildRepeatCount.value;
         this.numRepeats++;
         if (var15.equals(new Vec3i(0, 0, 0)) || var18 != -1 && this.numRepeats >= var18) {
            this.logDirect("Done building");
            if (Baritone.settings().notificationOnBuildFinished.value) {
               this.logNotification("Done building", false);
            }

            this.onLostControl();
            return null;
         } else {
            this.layer = 0;
            this.origin = new BlockPos(this.origin).offset(var15);
            if (!Baritone.settings().buildRepeatSneaky.value) {
               this.schematic.reset();
            }

            this.logDirect("Repeating build in vector " + var15 + ", new origin is " + this.origin);
            return this.onTick(var1, var2, var3 + 1);
         }
      } else {
         if (Baritone.settings().distanceTrim.value) {
            this.trim();
         }

         var var14 = this.toBreakNearPlayer(var13);
         if (var14.isPresent() && var2 && this.ctx.player().onGround()) {
            baritone.api.utils.Rotation var17 = (baritone.api.utils.Rotation)((Tuple)var14.get()).getB();
            BetterBlockPos var19 = (BetterBlockPos)((Tuple)var14.get()).getA();
            this.baritone.getLookBehavior().updateTarget(var17, true);
            MovementHelper.switchToBestToolFor(this.ctx, var13.get(var19));
            if (this.ctx.player().isCrouching()) {
               this.baritone.getInputOverrideHandler().setInputForceState(Input.SNEAK, true);
            }

            if (this.ctx.isLookingAt(var19) || this.ctx.playerRotations().isReallyCloseTo(var17)) {
               this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, true);
            }

            return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
         } else {
            ArrayList<BlockState> var16 = new ArrayList<>();
            var var7 = this.searchForPlacables(var13, var16);
            if (var7.isPresent() && var2 && this.ctx.player().onGround() && this.ticks <= 0) {
               baritone.api.utils.Rotation var21 = ((BuilderProcess.Placement)var7.get()).rot;
               this.baritone.getLookBehavior().updateTarget(var21, true);
               this.ctx.player().getInventory().setSelectedSlot(((BuilderProcess.Placement)var7.get()).hotbarSelection);
               this.baritone.getInputOverrideHandler().setInputForceState(Input.SNEAK, true);
               if (this.ctx.isLookingAt(((BuilderProcess.Placement)var7.get()).placeAgainst)
                     && ((BlockHitResult)this.ctx.objectMouseOver()).getDirection().equals(((BuilderProcess.Placement)var7.get()).side)
                  || this.ctx.playerRotations().isReallyCloseTo(var21)) {
                  this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
               }

               return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
            } else {
               if (Baritone.settings().allowInventory.value) {
                  ArrayList var8 = new ArrayList();
                  ArrayList<BlockState> var9 = new ArrayList<>();

                  label154:
                  for (BlockState var11 : var16) {
                     for (int var12 = 0; var12 < 9; var12++) {
                        if (valid(this.approxPlaceable.get(var12), var11, true)) {
                           var8.add(var12);
                           continue label154;
                        }
                     }

                     var9.add(var11);
                  }

                  label140:
                  for (int var22 = 9; var22 < 36; var22++) {
                     for (BlockState var24 : var9) {
                        if (valid(this.approxPlaceable.get(var22), var24, true)) {
                           if (!this.baritone.getInventoryBehavior().attemptToPutOnHotbar(var22, var8::contains)) {
                              return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
                           }
                           break label140;
                        }
                     }
                  }
               }

               Goal var20 = this.assemble(var13, this.approxPlaceable.subList(0, 9));
               if (var20 == null) {
                  var20 = this.assemble(var13, this.approxPlaceable, true);
                  if (var20 == null) {
                     if (Baritone.settings().skipFailedLayers.value
                        && Baritone.settings().buildInLayers.value
                        && this.layer * Baritone.settings().layerHeight.value < this.realSchematic.heightY()) {
                        this.logDirect("Skipping layer that I cannot construct! Layer #" + this.layer);
                        this.layer++;
                        return this.onTick(var1, var2, var3 + 1);
                     }

                     this.logDirect("Unable to do it. Pausing. resume to resume, cancel to cancel");
                     this.paused = true;
                     return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
                  }
               }

               return new PathingCommandContext(var20, PathingCommandType.FORCE_REVALIDATE_GOAL_AND_PATH, var13);
            }
         }
      }
   }

   private boolean recalc(BuilderProcess.BuilderCalculationContext var1) {
      if (this.incorrectPositions == null) {
         this.incorrectPositions = new HashSet<>();
         this.fullRecalc(var1);
         if (this.incorrectPositions.isEmpty()) {
            return false;
         }
      }

      this.recalcNearby(var1);
      if (this.incorrectPositions.isEmpty()) {
         this.fullRecalc(var1);
      }

      return !this.incorrectPositions.isEmpty();
   }

   private void trim() {
      HashSet var1 = new HashSet<>(this.incorrectPositions);
      var1.removeIf(var1x -> ((baritone.api.utils.BetterBlockPos)var1x).distSqr(this.ctx.player().blockPosition()) > 200.0);
      if (!var1.isEmpty()) {
         this.incorrectPositions = var1;
      }
   }

   private void recalcNearby(BuilderProcess.BuilderCalculationContext var1) {
      BetterBlockPos var2 = this.ctx.playerFeet();
      int var3 = Baritone.settings().builderTickScanRadius.value;

      for (int var4 = -var3; var4 <= var3; var4++) {
         for (int var5 = -var3; var5 <= var3; var5++) {
            for (int var6 = -var3; var6 <= var3; var6++) {
               int var7 = var2.x + var4;
               int var8 = var2.y + var5;
               int var9 = var2.z + var6;
               BlockState var10 = var1.getSchematic(var7, var8, var9, var1.bsi.get0(var7, var8, var9));
               if (var10 != null) {
                  BetterBlockPos var11 = new BetterBlockPos(var7, var8, var9);
                  if (valid(var1.bsi.get0(var7, var8, var9), var10, false)) {
                     this.incorrectPositions.remove(var11);
                     this.observedCompleted.add(BetterBlockPos.longHash(var11));
                  } else {
                     this.incorrectPositions.add(var11);
                     this.observedCompleted.remove(BetterBlockPos.longHash(var11));
                  }
               }
            }
         }
      }
   }

   private void fullRecalc(BuilderProcess.BuilderCalculationContext var1) {
      this.incorrectPositions = new HashSet<>();

      for (int var2 = 0; var2 < this.schematic.heightY(); var2++) {
         for (int var3 = 0; var3 < this.schematic.lengthZ(); var3++) {
            for (int var4 = 0; var4 < this.schematic.widthX(); var4++) {
               int var5 = var4 + this.origin.getX();
               int var6 = var2 + this.origin.getY();
               int var7 = var3 + this.origin.getZ();
               BlockState var8 = var1.bsi.get0(var5, var6, var7);
               if (this.schematic.inSchematic(var4, var2, var3, var8)) {
                  if (var1.bsi.worldContainsLoadedChunk(var5, var7)) {
                     if (valid(var1.bsi.get0(var5, var6, var7), this.schematic.desiredState(var4, var2, var3, var8, this.approxPlaceable), false)) {
                        this.observedCompleted.add(BetterBlockPos.longHash(var5, var6, var7));
                     } else {
                        this.incorrectPositions.add(new BetterBlockPos(var5, var6, var7));
                        this.observedCompleted.remove(BetterBlockPos.longHash(var5, var6, var7));
                        if (this.incorrectPositions.size() > Baritone.settings().incorrectSize.value) {
                           return;
                        }
                     }
                  } else if (!this.observedCompleted.contains(BetterBlockPos.longHash(var5, var6, var7))) {
                     this.incorrectPositions.add(new BetterBlockPos(var5, var6, var7));
                     if (this.incorrectPositions.size() > Baritone.settings().incorrectSize.value) {
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private Goal assemble(BuilderProcess.BuilderCalculationContext var1, List<BlockState> var2) {
      return this.assemble(var1, var2, false);
   }

   private Goal assemble(BuilderProcess.BuilderCalculationContext var1, List<BlockState> var2, boolean var3) {
      ArrayList<baritone.api.utils.BetterBlockPos> var4 = new ArrayList<>();
      ArrayList<baritone.api.utils.BetterBlockPos> var5 = new ArrayList<>();
      ArrayList<baritone.api.utils.BetterBlockPos> var6 = new ArrayList<>();
      ArrayList<baritone.api.utils.BetterBlockPos> var7 = new ArrayList<>();
      HashMap var8 = new HashMap();
      ArrayList var9 = new ArrayList();
      this.incorrectPositions.forEach(var8x -> {
         BlockState var9x = var1.bsi.get0(var8x);
         if (var9x.getBlock() instanceof AirBlock) {
            BlockState var10x = var1.getSchematic(var8x.x, var8x.y, var8x.z, var9x);
            if (var10x == null) {
               var9.add(var8x);
            } else if (containsBlockState(var2, var10x)) {
               var4.add(var8x);
            } else {
               var8.put(var10x, 1 + (Integer)var8.getOrDefault(var10x, 0));
            }
         } else if (var9x.getBlock() instanceof LiquidBlock) {
            if (!MovementHelper.possiblyFlowing(var9x)) {
               var6.add(var8x);
            } else {
               var7.add(var8x);
            }
         } else {
            var5.add(var8x);
         }
      });
      this.incorrectPositions.removeAll(var9);
      ArrayList var10 = new ArrayList();
      var5.forEach(var3x -> var10.add(this.breakGoal(var3x, var1)));
      ArrayList var11 = new ArrayList();
      var4.forEach(var4x -> {
         if (!var4.contains(var4x.below()) && !var4.contains(var4x.below(2))) {
            var11.add(this.placementGoal(var4x, var1));
         }
      });
      var6.forEach(var1x -> var11.add(new GoalBlock(var1x.above())));
      if (!var11.isEmpty()) {
         return new BuilderProcess.JankyGoalComposite(new GoalComposite((Goal[])var11.toArray(new Goal[0])), new GoalComposite((Goal[])var10.toArray(new Goal[0])));
      }

      if (var10.isEmpty()) {
         if (var3 && !var8.isEmpty()) {
            this.logDirect("Missing materials for at least:");
            this.logDirect((String)var8.entrySet().stream().map(var0 -> String.format("%sx %s", ((java.util.Map.Entry)var0).getValue(), ((java.util.Map.Entry)var0).getKey())).collect(Collectors.joining("\n")));
         }

         if (var3 && !var7.isEmpty()) {
            this.logDirect("Unreplaceable liquids at at least:");
            this.logDirect(var7.stream().map(var0 -> String.format("%s %s %s", var0.x, var0.y, var0.z)).collect(Collectors.joining("\n")));
         }

         return null;
      } else {
         return new GoalComposite((Goal[])var10.toArray(new Goal[0]));
      }
   }

   private Goal placementGoal(BlockPos var1, BuilderProcess.BuilderCalculationContext var2) {
      if (!(this.ctx.world().getBlockState(var1).getBlock() instanceof AirBlock)) {
         return new BuilderProcess.GoalPlace(var1);
      }

      boolean var3 = !(this.ctx.world().getBlockState(var1.above()).getBlock() instanceof AirBlock);
      BlockState var4 = this.ctx.world().getBlockState(var1);

      for (Direction var8 : Movement.HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP) {
         if (MovementHelper.canPlaceAgainst(this.ctx, var1.relative(var8))
            && this.placementPlausible(var1, var2.getSchematic(var1.getX(), var1.getY(), var1.getZ(), var4))) {
            return new BuilderProcess.GoalAdjacent(var1, var1.relative(var8), var3);
         }
      }

      return new BuilderProcess.GoalPlace(var1);
   }

   private Goal breakGoal(BlockPos var1, BuilderProcess.BuilderCalculationContext var2) {
      return Baritone.settings().goalBreakFromAbove.value
            && var2.bsi.get0(var1.above()).getBlock() instanceof AirBlock
            && var2.bsi.get0(var1.above(2)).getBlock() instanceof AirBlock
         ? new BuilderProcess.JankyGoalComposite(new BuilderProcess.GoalBreak(var1), new GoalGetToBlock(var1.above()) {
            @Override
            public boolean isInGoal(int var1, int var2x, int var3) {
               return var2x <= this.y && (var1 != this.x || var2x != this.y || var3 != this.z) ? super.isInGoal(var1, var2x, var3) : false;
            }
         })
         : new BuilderProcess.GoalBreak(var1);
   }

   @Override
   public void onLostControl() {
      this.incorrectPositions = null;
      this.name = null;
      this.schematic = null;
      this.realSchematic = null;
      this.layer = Baritone.settings().startAtLayer.value;
      this.numRepeats = 0;
      this.paused = false;
      this.observedCompleted = null;
   }

   @Override
   public String displayName0() {
      return this.paused ? "Builder Paused" : "Building " + this.name;
   }

   @Override
   public Optional<Integer> getMinLayer() {
      return Baritone.settings().buildInLayers.value ? Optional.of(this.layer) : Optional.empty();
   }

   @Override
   public Optional<Integer> getMaxLayer() {
      return Baritone.settings().buildInLayers.value ? Optional.of(this.stopAtHeight) : Optional.empty();
   }

   private List<BlockState> approxPlaceable(int var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var1; var3++) {
         ItemStack var4 = this.ctx.player().getInventory().getNonEquipmentItems().get(var3);
         if (!var4.isEmpty() && var4.getItem() instanceof BlockItem) {
            BlockState var5 = ((BlockItem)var4.getItem())
               .getBlock()
               .getStateForPlacement(
                  new BlockPlaceContext(
                     new UseOnContext(
                        this.ctx.world(),
                        this.ctx.player(),
                        InteractionHand.MAIN_HAND,
                        var4,
                        new BlockHitResult(
                           new Vec3(this.ctx.player().position().x, this.ctx.player().position().y, this.ctx.player().position().z),
                           Direction.UP,
                           this.ctx.playerFeet(),
                           false
                        )
                     ) {}
                  )
               );
            if (var5 != null) {
               var2.add(var5);
            } else {
               var2.add(Blocks.AIR.defaultBlockState());
            }
         } else {
            var2.add(Blocks.AIR.defaultBlockState());
         }
      }

      return var2;
   }

   private static boolean sameBlockstate(BlockState var0, BlockState var1) {
      if (var0.getBlock() != var1.getBlock()) {
         return false;
      }

      boolean var2 = Baritone.settings().buildIgnoreDirection.value;
      var var3 = Baritone.settings().buildIgnoreProperties.value;
      if (!var2 && var3.isEmpty()) {
         return var0.equals(var1);
      }

      for (Property var5 : var0.getProperties()) {
         if (var0.getValue(var5) != var1.getValue(var5) && (!var2 || !ORIENTATION_PROPS.contains(var5)) && !var3.contains(var5.getName())) {
            return false;
         }
      }

      return true;
   }

   private static boolean containsBlockState(Collection<BlockState> var0, BlockState var1) {
      for (BlockState var3 : var0) {
         if (sameBlockstate(var3, var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean valid(BlockState var0, BlockState var1, boolean var2) {
      if (var1 == null) {
         return true;
      } else if (var0.getBlock() instanceof LiquidBlock && Baritone.settings().okIfWater.value) {
         return true;
      } else if (var0.getBlock() instanceof AirBlock && var1.getBlock() instanceof AirBlock) {
         return true;
      } else if (var0.getBlock() instanceof AirBlock && Baritone.settings().okIfAir.value.contains(var1.getBlock())) {
         return true;
      } else if (var1.getBlock() instanceof AirBlock && Baritone.settings().buildIgnoreBlocks.value.contains(var0.getBlock())) {
         return true;
      } else if (!(var0.getBlock() instanceof AirBlock) && Baritone.settings().buildIgnoreExisting.value && !var2) {
         return true;
      } else if (Baritone.settings().buildValidSubstitutes.value.getOrDefault(var1.getBlock(), Collections.emptyList()).contains(var0.getBlock()) && !var2) {
         return true;
      } else {
         return var0.equals(var1) ? true : sameBlockstate(var0, var1);
      }
   }

   public class BuilderCalculationContext extends CalculationContext {
      private final List<BlockState> placeable = BuilderProcess.this.approxPlaceable(9);
      private final ISchematic schematic;
      private final int originX;
      private final int originY;
      private final int originZ;

      public BuilderCalculationContext() {
         super(BuilderProcess.this.baritone, true);
         this.schematic = BuilderProcess.this.schematic;
         this.originX = BuilderProcess.this.origin.getX();
         this.originY = BuilderProcess.this.origin.getY();
         this.originZ = BuilderProcess.this.origin.getZ();
         this.jumpPenalty += 10.0;
         this.backtrackCostFavoringCoefficient = 1.0;
      }

      private BlockState getSchematic(int var1, int var2, int var3, BlockState var4) {
         return this.schematic.inSchematic(var1 - this.originX, var2 - this.originY, var3 - this.originZ, var4)
            ? this.schematic.desiredState(var1 - this.originX, var2 - this.originY, var3 - this.originZ, var4, BuilderProcess.this.approxPlaceable)
            : null;
      }

      @Override
      public double costOfPlacingAt(int var1, int var2, int var3, BlockState var4) {
         if (!this.isPossiblyProtected(var1, var2, var3) && this.worldBorder.canPlaceAt(var1, var3)) {
            BlockState var5 = this.getSchematic(var1, var2, var3, var4);
            if (var5 != null) {
               if (var5.getBlock() instanceof AirBlock) {
                  return this.placeBlockCost * Baritone.settings().placeIncorrectBlockPenaltyMultiplier.value;
               } else if (this.placeable.contains(var5)) {
                  return 0.0;
               } else {
                  return !this.hasThrowaway ? 1000000.0 : this.placeBlockCost * 1.5 * Baritone.settings().placeIncorrectBlockPenaltyMultiplier.value;
               }
            } else {
               return this.hasThrowaway ? this.placeBlockCost : 1000000.0;
            }
         } else {
            return 1000000.0;
         }
      }

      @Override
      public double breakCostMultiplierAt(int var1, int var2, int var3, BlockState var4) {
         if ((this.allowBreak || this.allowBreakAnyway.contains(var4.getBlock())) && !this.isPossiblyProtected(var1, var2, var3)) {
            BlockState var5 = this.getSchematic(var1, var2, var3, var4);
            if (var5 == null) {
               return 1.0;
            } else if (var5.getBlock() instanceof AirBlock) {
               return 1.0;
            } else {
               return BuilderProcess.valid(this.bsi.get0(var1, var2, var3), var5, false) ? Baritone.settings().breakCorrectBlockPenaltyMultiplier.value : 1.0;
            }
         } else {
            return 1000000.0;
         }
      }
   }

   public static class GoalAdjacent extends GoalGetToBlock {
      private boolean allowSameLevel;
      private BlockPos no;

      public GoalAdjacent(BlockPos var1, BlockPos var2, boolean var3) {
         super(var1);
         this.no = var2;
         this.allowSameLevel = var3;
      }

      @Override
      public boolean isInGoal(int var1, int var2, int var3) {
         if (var1 == this.x && var2 == this.y && var3 == this.z) {
            return false;
         } else if (var1 == this.no.getX() && var2 == this.no.getY() && var3 == this.no.getZ()) {
            return false;
         } else if (!this.allowSameLevel && var2 == this.y - 1) {
            return false;
         } else {
            return var2 < this.y - 1 ? false : super.isInGoal(var1, var2, var3);
         }
      }

      @Override
      public double heuristic(int var1, int var2, int var3) {
         return this.y * 100 + super.heuristic(var1, var2, var3);
      }

      @Override
      public boolean equals(Object var1) {
         if (!super.equals(var1)) {
            return false;
         }

         BuilderProcess.GoalAdjacent var2 = (BuilderProcess.GoalAdjacent)var1;
         return this.allowSameLevel == var2.allowSameLevel && Objects.equals(this.no, var2.no);
      }

      @Override
      public int hashCode() {
         int var1 = 806368046;
         var1 = var1 * 1412661222 + super.hashCode();
         var1 = var1 * 1730799370 + (int)BetterBlockPos.longHash(this.no.getX(), this.no.getY(), this.no.getZ());
         return var1 * 260592149 + (this.allowSameLevel ? -1314802005 : 1565710265);
      }

      @Override
      public String toString() {
         return String.format(
            "GoalAdjacent{x=%s,y=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.y), SettingsUtil.maybeCensor(this.z)
         );
      }
   }

   public static class GoalBreak extends GoalGetToBlock {
      public GoalBreak(BlockPos var1) {
         super(var1);
      }

      @Override
      public boolean isInGoal(int var1, int var2, int var3) {
         return var2 > this.y ? false : super.isInGoal(var1, var2, var3);
      }

      @Override
      public String toString() {
         return String.format("GoalBreak{x=%s,y=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.y), SettingsUtil.maybeCensor(this.z));
      }

      @Override
      public int hashCode() {
         return super.hashCode() * 1636324008;
      }
   }

   public static class GoalPlace extends GoalBlock {
      public GoalPlace(BlockPos var1) {
         super(var1.above());
      }

      @Override
      public double heuristic(int var1, int var2, int var3) {
         return this.y * 100 + super.heuristic(var1, var2, var3);
      }

      @Override
      public int hashCode() {
         return super.hashCode() * 1910811835;
      }

      @Override
      public String toString() {
         return String.format("GoalPlace{x=%s,y=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.y), SettingsUtil.maybeCensor(this.z));
      }
   }

   public static class JankyGoalComposite implements Goal {
      private final Goal primary;
      private final Goal fallback;

      public JankyGoalComposite(Goal var1, Goal var2) {
         this.primary = var1;
         this.fallback = var2;
      }

      @Override
      public boolean isInGoal(int var1, int var2, int var3) {
         return this.primary.isInGoal(var1, var2, var3) || this.fallback.isInGoal(var1, var2, var3);
      }

      @Override
      public double heuristic(int var1, int var2, int var3) {
         return this.primary.heuristic(var1, var2, var3);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            BuilderProcess.JankyGoalComposite var2 = (BuilderProcess.JankyGoalComposite)var1;
            return Objects.equals(this.primary, var2.primary) && Objects.equals(this.fallback, var2.fallback);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int var1 = -1701079641;
         var1 = var1 * 1196141026 + this.primary.hashCode();
         return var1 * -80327868 + this.fallback.hashCode();
      }

      @Override
      public String toString() {
         return "JankyComposite Primary: " + this.primary + " Fallback: " + this.fallback;
      }
   }

   public static class Placement {
      private final int hotbarSelection;
      private final BlockPos placeAgainst;
      private final Direction side;
      private final baritone.api.utils.Rotation rot;

      public Placement(int var1, BlockPos var2, Direction var3, baritone.api.utils.Rotation var4) {
         this.hotbarSelection = var1;
         this.placeAgainst = var2;
         this.side = var3;
         this.rot = var4;
      }
   }
}
