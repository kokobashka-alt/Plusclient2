package baritone.pathing.movement;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.movement.ActionCosts;
import baritone.api.pathing.movement.MovementStatus;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.VecUtils;
import baritone.api.utils.input.Input;
import baritone.pathing.precompute.Ternary;
import baritone.utils.BlockStateInterface;
import baritone.utils.ToolSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface MovementHelper extends ActionCosts, Helper {
   static boolean avoidBreaking(BlockStateInterface var0, int var1, int var2, int var3, BlockState var4) {
      if (!var0.worldBorder.canPlaceAt(var1, var3)) {
         return true;
      }

      Block var5 = var4.getBlock();
      return Baritone.settings().blocksToDisallowBreaking.value.contains(var5)
         || var5 == Blocks.ICE
         || var5 instanceof InfestedBlock
         || avoidAdjacentBreaking(var0, var1, var2 + 1, var3, true)
         || avoidAdjacentBreaking(var0, var1 + 1, var2, var3, false)
         || avoidAdjacentBreaking(var0, var1 - 1, var2, var3, false)
         || avoidAdjacentBreaking(var0, var1, var2, var3 + 1, false)
         || avoidAdjacentBreaking(var0, var1, var2, var3 - 1, false);
   }

   static boolean avoidAdjacentBreaking(BlockStateInterface var0, int var1, int var2, int var3, boolean var4) {
      BlockState var5 = var0.get0(var1, var2, var3);
      Block var6 = var5.getBlock();
      if (!var4 && var6 instanceof FallingBlock && Baritone.settings().avoidUpdatingFallingBlocks.value && FallingBlock.isFree(var0.get0(var1, var2 - 1, var3))
         )
       {
         return true;
      }

      if (var6 instanceof LiquidBlock) {
         if (!var4 && !Baritone.settings().strictLiquidCheck.value) {
            int var7 = var5.getValue(LiquidBlock.LEVEL);
            return var7 == 0 ? true : !(var0.get0(var1, var2 - 1, var3).getBlock() instanceof LiquidBlock);
         } else {
            return true;
         }
      } else {
         return !var5.getFluidState().isEmpty();
      }
   }

   static boolean canWalkThrough(IPlayerContext var0, BetterBlockPos var1) {
      return canWalkThrough(new BlockStateInterface(var0), var1.x, var1.y, var1.z);
   }

   static boolean canWalkThrough(BlockStateInterface var0, int var1, int var2, int var3) {
      return canWalkThrough(var0, var1, var2, var3, var0.get0(var1, var2, var3));
   }

   static boolean canWalkThrough(CalculationContext var0, int var1, int var2, int var3, BlockState var4) {
      return var0.precomputedData.canWalkThrough(var0.bsi, var1, var2, var3, var4);
   }

   static boolean canWalkThrough(CalculationContext var0, int var1, int var2, int var3) {
      return var0.precomputedData.canWalkThrough(var0.bsi, var1, var2, var3, var0.get(var1, var2, var3));
   }

   static boolean canWalkThrough(BlockStateInterface var0, int var1, int var2, int var3, BlockState var4) {
      Ternary var5 = canWalkThroughBlockState(var4);
      if (var5 == Ternary.YES) {
         return true;
      } else {
         return var5 == Ternary.NO ? false : canWalkThroughPosition(var0, var1, var2, var3, var4);
      }
   }

   static Ternary canWalkThroughBlockState(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 instanceof AirBlock) {
         return Ternary.YES;
      }

      if (var1 instanceof BaseFireBlock
         || var1 == Blocks.COBWEB
         || var1 == Blocks.END_PORTAL
         || var1 == Blocks.COCOA
         || var1 instanceof AbstractSkullBlock
         || var1 == Blocks.BUBBLE_COLUMN
         || var1 instanceof ShulkerBoxBlock
         || var1 instanceof SlabBlock
         || var1 instanceof TrapDoorBlock
         || var1 == Blocks.HONEY_BLOCK
         || var1 == Blocks.END_ROD
         || var1 == Blocks.SWEET_BERRY_BUSH
         || var1 == Blocks.POINTED_DRIPSTONE
         || var1 instanceof AmethystClusterBlock
         || var1 instanceof AzaleaBlock) {
         return Ternary.NO;
      }

      if (var1 == Blocks.BIG_DRIPLEAF) {
         return Ternary.NO;
      }

      if (var1 == Blocks.POWDER_SNOW) {
         return Ternary.NO;
      }

      if (Baritone.settings().blocksToAvoid.value.contains(var1)) {
         return Ternary.NO;
      }

      if (!(var1 instanceof DoorBlock) && !(var1 instanceof FenceGateBlock)) {
         if (var1 instanceof CarpetBlock) {
            return Ternary.MAYBE;
         } else if (var1 instanceof SnowLayerBlock) {
            return Ternary.MAYBE;
         } else {
            FluidState var2 = var0.getFluidState();
            if (!var2.isEmpty()) {
               return var2.getType().getAmount(var2) != 8 ? Ternary.NO : Ternary.MAYBE;
            } else if (var1 instanceof CauldronBlock) {
               return Ternary.NO;
            } else {
               return var0.isPathfindable(PathComputationType.LAND) ? Ternary.YES : Ternary.NO;
            }
         }
      } else {
         return var1 == Blocks.IRON_DOOR ? Ternary.NO : Ternary.YES;
      }
   }

   static boolean canWalkThroughPosition(BlockStateInterface var0, int var1, int var2, int var3, BlockState var4) {
      Block var5 = var4.getBlock();
      if (var5 instanceof CarpetBlock) {
         return canWalkOn(var0, var1, var2 - 1, var3);
      }

      if (var5 instanceof SnowLayerBlock) {
         if (!var0.worldContainsLoadedChunk(var1, var3)) {
            return true;
         } else {
            return var4.getValue(SnowLayerBlock.LAYERS) >= 3 ? false : canWalkOn(var0, var1, var2 - 1, var3);
         }
      } else {
         FluidState var6 = var4.getFluidState();
         if (!var6.isEmpty()) {
            if (isFlowing(var1, var2, var3, var4, var0)) {
               return false;
            }

            if (Baritone.settings().assumeWalkOnWater.value) {
               return false;
            }

            BlockState var7 = var0.get0(var1, var2 + 1, var3);
            return var7.getFluidState().isEmpty() && !(var7.getBlock() instanceof LilyPadBlock) ? var6.getType() instanceof WaterFluid : false;
         } else {
            return var4.isPathfindable(PathComputationType.LAND);
         }
      }
   }

   static Ternary fullyPassableBlockState(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 instanceof AirBlock) {
         return Ternary.YES;
      } else if (var1 instanceof BaseFireBlock
         || var1 == Blocks.TRIPWIRE
         || var1 == Blocks.COBWEB
         || var1 == Blocks.VINE
         || var1 == Blocks.LADDER
         || var1 == Blocks.COCOA
         || var1 instanceof AzaleaBlock
         || var1 instanceof DoorBlock
         || var1 instanceof FenceGateBlock
         || var1 instanceof SnowLayerBlock
         || !var0.getFluidState().isEmpty()
         || var1 instanceof TrapDoorBlock
         || var1 instanceof EndPortalBlock
         || var1 instanceof SkullBlock
         || var1 instanceof ShulkerBoxBlock) {
         return Ternary.NO;
      } else {
         return var0.isPathfindable(PathComputationType.LAND) ? Ternary.YES : Ternary.NO;
      }
   }

   static boolean fullyPassable(CalculationContext var0, int var1, int var2, int var3) {
      return fullyPassable(var0, var1, var2, var3, var0.get(var1, var2, var3));
   }

   static boolean fullyPassable(CalculationContext var0, int var1, int var2, int var3, BlockState var4) {
      return var0.precomputedData.fullyPassable(var0.bsi, var1, var2, var3, var4);
   }

   static boolean fullyPassable(IPlayerContext var0, BlockPos var1) {
      BlockState var2 = var0.world().getBlockState(var1);
      Ternary var3 = fullyPassableBlockState(var2);
      if (var3 == Ternary.YES) {
         return true;
      } else {
         return var3 == Ternary.NO ? false : var2.isPathfindable(PathComputationType.LAND);
      }
   }

   static boolean fullyPassablePosition(BlockStateInterface var0, int var1, int var2, int var3, BlockState var4) {
      return var4.isPathfindable(PathComputationType.LAND);
   }

   static boolean isReplaceable(int var0, int var1, int var2, BlockState var3, BlockStateInterface var4) {
      Block var5 = var3.getBlock();
      if (var5 instanceof AirBlock) {
         return true;
      } else if (var5 instanceof SnowLayerBlock) {
         return !var4.worldContainsLoadedChunk(var0, var2) ? true : var3.getValue(SnowLayerBlock.LAYERS) == 1;
      } else {
         return var5 != Blocks.LARGE_FERN && var5 != Blocks.TALL_GRASS ? var3.canBeReplaced() : true;
      }
   }

   static boolean isDoorPassable(IPlayerContext var0, BlockPos var1, BlockPos var2) {
      if (var2.equals(var1)) {
         return false;
      }

      BlockState var3 = BlockStateInterface.get(var0, var1);
      return !(var3.getBlock() instanceof DoorBlock) ? true : isHorizontalBlockPassable(var1, var3, var2, DoorBlock.OPEN);
   }

   static boolean isGatePassable(IPlayerContext var0, BlockPos var1, BlockPos var2) {
      if (var2.equals(var1)) {
         return false;
      }

      BlockState var3 = BlockStateInterface.get(var0, var1);
      return !(var3.getBlock() instanceof FenceGateBlock) ? true : var3.getValue(FenceGateBlock.OPEN);
   }

   static boolean isHorizontalBlockPassable(BlockPos var0, BlockState var1, BlockPos var2, BooleanProperty var3) {
      if (var2.equals(var0)) {
         return false;
      }

      Axis var4 = var1.getValue(HorizontalDirectionalBlock.FACING).getAxis();
      boolean var5 = var1.getValue(var3);
      Axis var6;
      if (!var2.north().equals(var0) && !var2.south().equals(var0)) {
         if (!var2.east().equals(var0) && !var2.west().equals(var0)) {
            return true;
         }

         var6 = Axis.X;
      } else {
         var6 = Axis.Z;
      }

      return var4 == var6 == var5;
   }

   static boolean avoidWalkingInto(BlockState var0) {
      Block var1 = var0.getBlock();
      return !var0.getFluidState().isEmpty()
         || var1 == Blocks.MAGMA_BLOCK && !Baritone.settings().allowWalkOnMagmaBlocks.value
         || var1 == Blocks.CACTUS
         || var1 == Blocks.SWEET_BERRY_BUSH
         || var1 instanceof BaseFireBlock
         || var1 == Blocks.END_PORTAL
         || var1 == Blocks.COBWEB
         || var1 == Blocks.BUBBLE_COLUMN;
   }

   static boolean canWalkOn(BlockStateInterface var0, int var1, int var2, int var3, BlockState var4) {
      Ternary var5 = canWalkOnBlockState(var4);
      if (var5 == Ternary.YES) {
         return true;
      } else {
         return var5 == Ternary.NO ? false : canWalkOnPosition(var0, var1, var2, var3, var4);
      }
   }

   static Ternary canWalkOnBlockState(BlockState var0) {
      Block var1 = var0.getBlock();
      if (isBlockNormalCube(var0)
         && (var1 != Blocks.MAGMA_BLOCK || Baritone.settings().allowWalkOnMagmaBlocks.value)
         && var1 != Blocks.BUBBLE_COLUMN
         && var1 != Blocks.HONEY_BLOCK) {
         return Ternary.YES;
      }

      if (var1 instanceof AzaleaBlock) {
         return Ternary.YES;
      }

      if (var1 != Blocks.LADDER && (!isClimbable(var1) || !Baritone.settings().allowVines.value)) {
         if (var1 == Blocks.FARMLAND || var1 == Blocks.DIRT_PATH || var1 == Blocks.SOUL_SAND) {
            return Ternary.YES;
         } else if (var1 == Blocks.ENDER_CHEST || var1 == Blocks.CHEST || var1 == Blocks.TRAPPED_CHEST) {
            return Ternary.YES;
         } else if (var1 == Blocks.GLASS || var1 instanceof StainedGlassBlock) {
            return Ternary.YES;
         } else if (var1 instanceof StairBlock) {
            return Ternary.YES;
         } else if (isWater(var0)) {
            return Ternary.MAYBE;
         } else if (isLava(var0) && Baritone.settings().assumeWalkOnLava.value) {
            return Ternary.MAYBE;
         } else if (!(var1 instanceof SlabBlock)) {
            return Ternary.NO;
         } else if (Baritone.settings().allowWalkOnBottomSlab.value) {
            return Ternary.YES;
         } else {
            return var0.getValue(SlabBlock.TYPE) != SlabType.BOTTOM ? Ternary.YES : Ternary.NO;
         }
      } else {
         return Ternary.YES;
      }
   }

   static boolean canWalkOnPosition(BlockStateInterface var0, int var1, int var2, int var3, BlockState var4) {
      var4.getBlock();
      if (!isWater(var4)) {
         return isLava(var4) && !isFlowing(var1, var2, var3, var4, var0) && Baritone.settings().assumeWalkOnLava.value;
      } else {
         BlockState var6 = var0.get0(var1, var2 + 1, var3);
         Block var7 = var6.getBlock();
         if (var7 != Blocks.LILY_PAD && !(var7 instanceof CarpetBlock)) {
            return !isFlowing(var1, var2, var3, var4, var0) && var6.getFluidState().getType() != Fluids.FLOWING_WATER
               ? isWater(var6) ^ Baritone.settings().assumeWalkOnWater.value
               : isWater(var6) && !Baritone.settings().assumeWalkOnWater.value;
         } else {
            return true;
         }
      }
   }

   static boolean canWalkOn(CalculationContext var0, int var1, int var2, int var3, BlockState var4) {
      return var0.precomputedData.canWalkOn(var0.bsi, var1, var2, var3, var4);
   }

   static boolean canWalkOn(CalculationContext var0, int var1, int var2, int var3) {
      return canWalkOn(var0, var1, var2, var3, var0.get(var1, var2, var3));
   }

   static boolean canWalkOn(IPlayerContext var0, BetterBlockPos var1, BlockState var2) {
      return canWalkOn(new BlockStateInterface(var0), var1.x, var1.y, var1.z, var2);
   }

   static boolean canWalkOn(IPlayerContext var0, BlockPos var1) {
      return canWalkOn(new BlockStateInterface(var0), var1.getX(), var1.getY(), var1.getZ());
   }

   static boolean canWalkOn(IPlayerContext var0, BetterBlockPos var1) {
      return canWalkOn(new BlockStateInterface(var0), var1.x, var1.y, var1.z);
   }

   static boolean canWalkOn(BlockStateInterface var0, int var1, int var2, int var3) {
      return canWalkOn(var0, var1, var2, var3, var0.get0(var1, var2, var3));
   }

   static boolean canUseFrostWalker(CalculationContext var0, BlockState var1) {
      return var0.frostWalker != 0 && var1 == FrostedIceBlock.meltsInto() && var1.getValue(LiquidBlock.LEVEL) == 0;
   }

   static boolean canUseFrostWalker(IPlayerContext var0, BlockPos var1) {
      boolean var2 = false;

      label31:
      for (EquipmentSlot var6 : EquipmentSlot.values()) {
         ItemEnchantments var7 = var0.player().getItemBySlot(var6).getEnchantments();

         for (Holder var9 : var7.keySet()) {
            if (var9.is(Enchantments.FROST_WALKER)) {
               var2 = true;
               break label31;
            }
         }
      }

      BlockState var10 = BlockStateInterface.get(var0, var1);
      return var2 && var10 == FrostedIceBlock.meltsInto() && var10.getValue(LiquidBlock.LEVEL) == 0;
   }

   static boolean mustBeSolidToWalkOn(CalculationContext var0, int var1, int var2, int var3, BlockState var4) {
      Block var5 = var4.getBlock();
      if (isClimbable(var5)) {
         return false;
      }

      if (!var4.getFluidState().isEmpty()) {
         if (var5 instanceof SlabBlock) {
            if (var4.getValue(SlabBlock.TYPE) != SlabType.BOTTOM) {
               return true;
            }
         } else if (var5 instanceof StairBlock) {
            if (var4.getValue(StairBlock.HALF) == Half.TOP) {
               return true;
            }

            StairsShape var6 = var4.getValue(StairBlock.SHAPE);
            if (var6 == StairsShape.INNER_LEFT || var6 == StairsShape.INNER_RIGHT) {
               return true;
            }
         } else if (var5 instanceof TrapDoorBlock) {
            if (!var4.getValue(TrapDoorBlock.OPEN) && var4.getValue(TrapDoorBlock.HALF) == Half.TOP) {
               return true;
            }
         } else {
            if (var5 == Blocks.SCAFFOLDING) {
               return true;
            }

            if (var5 instanceof LeavesBlock) {
               return true;
            }
         }

         if (var0.assumeWalkOnWater) {
            return false;
         }

         Block var7 = var0.getBlock(var1, var2 + 1, var3);
         if (var7 instanceof LiquidBlock) {
            return false;
         }
      }

      return true;
   }

   static boolean canPlaceAgainst(BlockStateInterface var0, int var1, int var2, int var3) {
      return canPlaceAgainst(var0, var1, var2, var3, var0.get0(var1, var2, var3));
   }

   static boolean canPlaceAgainst(BlockStateInterface var0, BlockPos var1) {
      return canPlaceAgainst(var0, var1.getX(), var1.getY(), var1.getZ());
   }

   static boolean canPlaceAgainst(IPlayerContext var0, BlockPos var1) {
      return canPlaceAgainst(new BlockStateInterface(var0), var1);
   }

   static boolean canPlaceAgainst(BlockStateInterface var0, int var1, int var2, int var3, BlockState var4) {
      return !var0.worldBorder.canPlaceAt(var1, var3)
         ? false
         : isBlockNormalCube(var4) || var4.getBlock() == Blocks.GLASS || var4.getBlock() instanceof StainedGlassBlock;
   }

   static boolean isClimbable(Block var0) {
      return var0 == Blocks.LADDER
         || var0 == Blocks.VINE
         || var0 == Blocks.WEEPING_VINES
         || var0 == Blocks.WEEPING_VINES_PLANT
         || var0 == Blocks.TWISTING_VINES
         || var0 == Blocks.TWISTING_VINES_PLANT;
   }

   static double getMiningDurationTicks(CalculationContext var0, int var1, int var2, int var3, boolean var4) {
      return getMiningDurationTicks(var0, var1, var2, var3, var0.get(var1, var2, var3), var4);
   }

   static double getMiningDurationTicks(CalculationContext var0, int var1, int var2, int var3, BlockState var4, boolean var5) {
      var4.getBlock();
      if (!canWalkThrough(var0, var1, var2, var3, var4)) {
         if (!var4.getFluidState().isEmpty()) {
            return 1000000.0;
         }

         double var7 = var0.breakCostMultiplierAt(var1, var2, var3, var4);
         if (var7 >= 1000000.0) {
            return 1000000.0;
         }

         if (avoidBreaking(var0.bsi, var1, var2, var3, var4)) {
            return 1000000.0;
         }

         double var9 = var0.toolSet.getStrVsBlock(var4);
         if (var9 <= 0.0) {
            return 1000000.0;
         }

         double var11 = 1.0 / var9;
         var11 += var0.breakBlockAdditionalCost;
         var11 *= var7;
         if (var5) {
            BlockState var13 = var0.get(var1, var2 + 1, var3);
            if (var13.getBlock() instanceof FallingBlock) {
               var11 += getMiningDurationTicks(var0, var1, var2 + 1, var3, var13, true);
            }
         }

         return var11;
      } else {
         return 0.0;
      }
   }

   static boolean isBottomSlab(BlockState var0) {
      return var0.getBlock() instanceof SlabBlock && var0.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
   }

   static void switchToBestToolFor(IPlayerContext var0, BlockState var1) {
      switchToBestToolFor(var0, var1, new ToolSet(var0.player()), BaritoneAPI.getSettings().preferSilkTouch.value);
   }

   static void switchToBestToolFor(IPlayerContext var0, BlockState var1, ToolSet var2, boolean var3) {
      if (Baritone.settings().autoTool.value && !Baritone.settings().assumeExternalAutoTool.value) {
         var0.player().getInventory().setSelectedSlot(var2.getBestSlot(var1.getBlock(), var3));
      }
   }

   static void moveTowards(IPlayerContext var0, MovementState var1, BlockPos var2) {
      var1.setTarget(
            new MovementState.MovementTarget(
               RotationUtils.calcRotationFromVec3d(var0.playerHead(), VecUtils.getBlockPosCenter(var2), var0.playerRotations())
                  .withPitch(var0.playerRotations().getPitch()),
               false
            )
         )
         .setInput(Input.MOVE_FORWARD, true);
   }

   static void moveTowardsWithoutRotation(IPlayerContext var0, MovementState var1, float var2) {
      MovementOption.getOptions(
            Mth.sin(var0.playerRotations().getYaw() * (float) (Math.PI / 180.0)),
            Mth.cos(var0.playerRotations().getYaw() * (float) (Math.PI / 180.0)),
            Baritone.settings().allowSprint.value
         )
         .min(Comparator.comparing(var1x -> var1x.distanceToSq(Mth.sin(var2 * (float) (Math.PI / 180.0)), Mth.cos(var2 * (float) (Math.PI / 180.0)))))
         .ifPresent(var1x -> var1x.setInputs(var1));
   }

   static void moveTowardsWithoutRotation(IPlayerContext var0, MovementState var1, BlockPos var2) {
      float var3 = RotationUtils.calcRotationFromVec3d(var0.playerHead(), VecUtils.getBlockPosCenter(var2), var0.playerRotations()).getYaw();
      moveTowardsWithoutRotation(var0, var1, var3);
   }

   static void moveTowardsWithSlightRotation(IPlayerContext var0, MovementState var1, BlockPos var2) {
      float var3 = RotationUtils.calcRotationFromVec3d(var0.playerHead(), VecUtils.getBlockPosCenter(var2), var0.playerRotations()).getYaw();
      float var4 = Rotation.yawDistanceFromOffset(var0.playerRotations().getYaw(), var3) % 45.0F;
      float var5 = var4 > 0.0F ? (var4 > 22.5F ? var4 - 45.0F : var4) : (var4 < -22.5F ? var4 + 45.0F : var4);
      var1.setTarget(new MovementState.MovementTarget(new Rotation(var0.playerRotations().getYaw() - var5, var0.playerRotations().getPitch()), true));
      moveTowardsWithoutRotation(var0, var1, var3);
   }

   static boolean isWater(BlockState var0) {
      Fluid var1 = var0.getFluidState().getType();
      return var1 == Fluids.WATER || var1 == Fluids.FLOWING_WATER;
   }

   static boolean isWater(IPlayerContext var0, BlockPos var1) {
      return isWater(BlockStateInterface.get(var0, var1));
   }

   static boolean isLava(BlockState var0) {
      Fluid var1 = var0.getFluidState().getType();
      return var1 == Fluids.LAVA || var1 == Fluids.FLOWING_LAVA;
   }

   static boolean isLiquid(IPlayerContext var0, BlockPos var1) {
      return isLiquid(BlockStateInterface.get(var0, var1));
   }

   static boolean isLiquid(BlockState var0) {
      return !var0.getFluidState().isEmpty();
   }

   static boolean possiblyFlowing(BlockState var0) {
      FluidState var1 = var0.getFluidState();
      return var1.getType() instanceof FlowingFluid && var1.getType().getAmount(var1) != 8;
   }

   static boolean isFlowing(int var0, int var1, int var2, BlockState var3, BlockStateInterface var4) {
      FluidState var5 = var3.getFluidState();
      if (!(var5.getType() instanceof FlowingFluid)) {
         return false;
      } else {
         return var5.getType().getAmount(var5) != 8
            ? true
            : possiblyFlowing(var4.get0(var0 + 1, var1, var2))
               || possiblyFlowing(var4.get0(var0 - 1, var1, var2))
               || possiblyFlowing(var4.get0(var0, var1, var2 + 1))
               || possiblyFlowing(var4.get0(var0, var1, var2 - 1));
      }
   }

   static boolean isBlockNormalCube(BlockState var0) {
      Block var1 = var0.getBlock();
      if (!(var1 instanceof BambooStalkBlock)
         && !(var1 instanceof MovingPistonBlock)
         && !(var1 instanceof ScaffoldingBlock)
         && !(var1 instanceof ShulkerBoxBlock)
         && !(var1 instanceof PointedDripstoneBlock)
         && !(var1 instanceof AmethystClusterBlock)) {
         try {
            return Block.isShapeFullBlock(var0.getCollisionShape(null, null));
         } catch (Exception var2) {
            return false;
         }
      } else {
         return false;
      }
   }

   static MovementHelper.PlaceResult attemptToPlaceABlock(MovementState var0, IBaritone var1, BlockPos var2, boolean var3, boolean var4) {
      IPlayerContext var5 = var1.getPlayerContext();
      var var6 = RotationUtils.reachable(var5, var2, var4);
      boolean var7 = false;
      if (var6.isPresent()) {
         var0.setTarget(new MovementState.MovementTarget((Rotation)var6.get(), true));
         var7 = true;
      }

      int var8 = 0;

      while (true) {
         label81: {
            if (var8 < 5) {
               BlockPos var9 = var2.relative(Movement.HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var8]);
               if (!canPlaceAgainst(var5, var9)) {
                  break label81;
               }

               if (!((Baritone)var1).getInventoryBehavior().selectThrowawayForLocation(false, var2.getX(), var2.getY(), var2.getZ())) {
                  Helper.HELPER.logDebug("bb pls get me some blocks. dirt, netherrack, cobble");
                  var0.setStatus(MovementStatus.UNREACHABLE);
                  return MovementHelper.PlaceResult.NO_OPTION;
               }

               double var10 = (var2.getX() + var9.getX() + 1.0) * 0.5;
               double var12 = (var2.getY() + var9.getY() + 0.5) * 0.5;
               double var14 = (var2.getZ() + var9.getZ() + 1.0) * 0.5;
               Rotation var16 = RotationUtils.calcRotationFromVec3d(
                  var4 ? RayTraceUtils.inferSneakingEyePosition(var5.player()) : var5.playerHead(), new Vec3(var10, var12, var14), var5.playerRotations()
               );
               Rotation var17 = var1.getLookBehavior().getAimProcessor().peekRotation(var16);
               HitResult var18 = RayTraceUtils.rayTraceTowards(var5.player(), var17, var5.playerController().getBlockReachDistance(), var4);
               if (var18 == null
                  || var18.getType() != Type.BLOCK
                  || !((BlockHitResult)var18).getBlockPos().equals(var9)
                  || !((BlockHitResult)var18).getBlockPos().relative(((BlockHitResult)var18).getDirection()).equals(var2)) {
                  break label81;
               }

               var0.setTarget(new MovementState.MovementTarget(var16, true));
               var7 = true;
               if (var3) {
                  break label81;
               }
            }

            if (var5.getSelectedBlock().isPresent()) {
               BlockPos var19 = var5.getSelectedBlock().get();
               Direction var20 = ((BlockHitResult)var5.objectMouseOver()).getDirection();
               if (var19.equals(var2) || canPlaceAgainst(var5, var19) && var19.relative(var20).equals(var2)) {
                  if (var4) {
                     var0.setInput(Input.SNEAK, true);
                  }

                  ((Baritone)var1).getInventoryBehavior().selectThrowawayForLocation(true, var2.getX(), var2.getY(), var2.getZ());
                  return MovementHelper.PlaceResult.READY_TO_PLACE;
               }
            }

            if (var7) {
               if (var4) {
                  var0.setInput(Input.SNEAK, true);
               }

               ((Baritone)var1).getInventoryBehavior().selectThrowawayForLocation(true, var2.getX(), var2.getY(), var2.getZ());
               return MovementHelper.PlaceResult.ATTEMPTING;
            }

            return MovementHelper.PlaceResult.NO_OPTION;
         }

         var8++;
      }
   }

   static boolean isTransparent(Block var0) {
      return var0 instanceof AirBlock || var0 == Blocks.LAVA || var0 == Blocks.WATER;
   }

   static List<BetterBlockPos> steppingOnBlocks(IPlayerContext var0) {
      ArrayList var1 = new ArrayList();

      for (byte var2 = -1; var2 <= 1; var2++) {
         for (byte var3 = -1; var3 <= 1; var3++) {
            if (var0.player()
               .getBoundingBox()
               .intersects(
                  Vec3.atLowerCornerOf(var0.player().blockPosition()).add(var2, 0.0, var3),
                  Vec3.atLowerCornerOf(var0.player().blockPosition()).add(var2 + 1, 1.0, var3 + 1)
               )) {
               var1.add(new BetterBlockPos(var0.player().getBlockX() + var2, var0.player().getBlockY() - 1, var0.player().getBlockZ() + var3));
            }
         }
      }

      return var1;
   }

   enum PlaceResult {
      READY_TO_PLACE,
      ATTEMPTING,
      NO_OPTION;
   }
}
