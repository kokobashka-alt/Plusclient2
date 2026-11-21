package baritone.process;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.process.IFarmProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.selection.ISelection;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.MovementHelper;
import baritone.utils.BaritoneProcessHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FarmProcess extends BaritoneProcessHelper implements IFarmProcess {
   private boolean active;
   private List<BlockPos> locations;
   private int tickCount;
   private int range;
   private BlockPos center;
   private static final List<Item> FARMLAND_PLANTABLE = Arrays.asList(
      Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.POTATO, Items.CARROT
   );
   private static final List<Item> PICKUP_DROPPED = Arrays.asList(
      Items.BEETROOT_SEEDS,
      Items.BEETROOT,
      Items.MELON_SEEDS,
      Items.MELON_SLICE,
      Blocks.MELON.asItem(),
      Items.WHEAT_SEEDS,
      Items.WHEAT,
      Items.PUMPKIN_SEEDS,
      Blocks.PUMPKIN.asItem(),
      Items.POTATO,
      Items.CARROT,
      Items.NETHER_WART,
      Items.COCOA_BEANS,
      Blocks.SUGAR_CANE.asItem(),
      Blocks.BAMBOO.asItem(),
      Blocks.CACTUS.asItem()
   );

   public FarmProcess(Baritone var1) {
      super(var1);
   }

   @Override
   public boolean isActive() {
      return this.active;
   }

   @Override
   public void farm(int var1, BlockPos var2) {
      if (var2 == null) {
         this.center = this.baritone.getPlayerContext().playerFeet();
      } else {
         this.center = var2;
      }

      this.range = var1;
      this.active = true;
      this.locations = null;
   }

   private boolean readyForHarvest(Level var1, BlockPos var2, BlockState var3) {
      for (FarmProcess.Harvest var7 : FarmProcess.Harvest.values()) {
         if (var7.block == var3.getBlock()) {
            return var7.readyToHarvest(var1, var2, var3);
         }
      }

      return false;
   }

   private boolean isPlantable(ItemStack var1) {
      return FARMLAND_PLANTABLE.contains(var1.getItem());
   }

   private boolean isBoneMeal(ItemStack var1) {
      return !var1.isEmpty() && var1.getItem().equals(Items.BONE_MEAL);
   }

   private boolean isNetherWart(ItemStack var1) {
      return !var1.isEmpty() && var1.getItem().equals(Items.NETHER_WART);
   }

   private boolean isCocoa(ItemStack var1) {
      return !var1.isEmpty() && var1.getItem().equals(Items.COCOA_BEANS);
   }

   @Override
   public PathingCommand onTick(boolean var1, boolean var2) {
      if (Baritone.settings().mineGoalUpdateInterval.value != 0 && this.tickCount++ % Baritone.settings().mineGoalUpdateInterval.value == 0) {
         ArrayList var3 = new ArrayList();

         for (FarmProcess.Harvest var7 : FarmProcess.Harvest.values()) {
            var3.add(var7.block);
         }

         if (Baritone.settings().replantCrops.value) {
            var3.add(Blocks.FARMLAND);
            var3.add(Blocks.JUNGLE_LOG);
            if (Baritone.settings().replantNetherWart.value) {
               var3.add(Blocks.SOUL_SAND);
            }
         }

         Baritone.getExecutor()
            .execute(
               () -> this.locations = BaritoneAPI.getProvider()
                  .getWorldScanner()
                  .scanChunkRadius(this.ctx, var3, Baritone.settings().farmMaxScanSize.value, 10, 10)
            );
      }

      if (this.locations == null) {
         return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
      }

      if (Baritone.settings().farmUsingSelection.value) {
         ISelection var19 = this.baritone.getSelectionManager().getLastSelection();
         if (var19 != null) {
            this.locations.removeIf(var1x -> !var19.aabb().contains(var1x.getX() + 0.5, var1x.getY() + 0.5, var1x.getZ() + 0.5));
         }
      }

      ArrayList<BlockPos> var20 = new ArrayList<>();
      ArrayList<BlockPos> var21 = new ArrayList<>();
      ArrayList<BlockPos> var22 = new ArrayList<>();
      ArrayList<BlockPos> var23 = new ArrayList<>();
      ArrayList<BlockPos> var24 = new ArrayList<>();

      for (BlockPos var9 : this.locations) {
         if (this.range == 0 || !(var9.distSqr(this.center) > this.range * this.range)) {
            BlockState var10 = this.ctx.world().getBlockState(var9);
            boolean var11 = this.ctx.world().getBlockState(var9.above()).getBlock() instanceof AirBlock;
            if (var10.getBlock() == Blocks.FARMLAND) {
               if (var11) {
                  var21.add(var9);
               }
            } else if (var10.getBlock() == Blocks.SOUL_SAND) {
               if (var11) {
                  var23.add(var9);
               }
            } else if (var10.getBlock() == Blocks.JUNGLE_LOG) {
               for (Direction var13 : Plane.HORIZONTAL) {
                  if (this.ctx.world().getBlockState(var9.relative(var13)).getBlock() instanceof AirBlock) {
                     var24.add(var9);
                     break;
                  }
               }
            } else if (this.readyForHarvest(this.ctx.world(), var9, var10)) {
               var20.add(var9);
            } else if (var10.getBlock() instanceof BonemealableBlock) {
               BonemealableBlock var12 = (BonemealableBlock)var10.getBlock();
               if (var12.isValidBonemealTarget(this.ctx.world(), var9, var10)
                  && var12.isBonemealSuccess(this.ctx.world(), this.ctx.world().getRandom(), var9, var10)) {
                  var22.add(var9);
               }
            }
         }
      }

      this.baritone.getInputOverrideHandler().clearAllKeys();
      BetterBlockPos var25 = this.ctx.playerFeet();
      double var26 = this.ctx.playerController().getBlockReachDistance();

      for (BlockPos var30 : var20) {
         if (!(var25.distSqr(var30) > var26 * var26)) {
            var var35 = RotationUtils.reachable(this.ctx, var30);
            if (var35.isPresent() && var2) {
               this.baritone.getLookBehavior().updateTarget((Rotation)var35.get(), true);
               MovementHelper.switchToBestToolFor(this.ctx, this.ctx.world().getBlockState(var30));
               if (this.ctx.isLookingAt(var30)) {
                  this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, true);
               }

               return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
            }
         }
      }

      ArrayList<BlockPos> var28 = new ArrayList<>(var21);
      var28.addAll(var23);

      for (BlockPos var36 : var28) {
         if (!(var25.distSqr(var36) > var26 * var26)) {
            boolean var14 = var23.contains(var36);
            var var15 = RotationUtils.reachableOffset(this.ctx, var36, new Vec3(var36.getX() + 0.5, var36.getY() + 1, var36.getZ() + 0.5), var26, false);
            if (var15.isPresent() && var2 && this.baritone.getInventoryBehavior().throwaway(true, var14 ? this::isNetherWart : this::isPlantable)) {
               HitResult var16 = RayTraceUtils.rayTraceTowards(this.ctx.player(), (Rotation)var15.get(), var26);
               if (var16 instanceof BlockHitResult && ((BlockHitResult)var16).getDirection() == Direction.UP) {
                  this.baritone.getLookBehavior().updateTarget((Rotation)var15.get(), true);
                  if (this.ctx.isLookingAt(var36)) {
                     this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
                  }

                  return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
               }
            }
         }
      }

      for (BlockPos var37 : var24) {
         if (!(var25.distSqr(var37) > var26 * var26)) {
            for (Direction var53 : Plane.HORIZONTAL) {
               if (this.ctx.world().getBlockState(var37.relative(var53)).getBlock() instanceof AirBlock) {
                  Vec3 var56 = Vec3.atCenterOf(var37).add(Vec3.atLowerCornerOf(var53.getUnitVec3i()).scale(0.5));
                  var var17 = RotationUtils.reachableOffset(this.ctx, var37, var56, var26, false);
                  if (var17.isPresent() && var2 && this.baritone.getInventoryBehavior().throwaway(true, this::isCocoa)) {
                     HitResult var18 = RayTraceUtils.rayTraceTowards(this.ctx.player(), (Rotation)var17.get(), var26);
                     if (var18 instanceof BlockHitResult && ((BlockHitResult)var18).getDirection() == var53) {
                        this.baritone.getLookBehavior().updateTarget((Rotation)var17.get(), true);
                        if (this.ctx.isLookingAt(var37)) {
                           this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
                        }

                        return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
                     }
                  }
               }
            }
         }
      }

      for (BlockPos var38 : var22) {
         if (!(var25.distSqr(var38) > var26 * var26)) {
            var var46 = RotationUtils.reachable(this.ctx, var38);
            if (var46.isPresent() && var2 && this.baritone.getInventoryBehavior().throwaway(true, this::isBoneMeal)) {
               this.baritone.getLookBehavior().updateTarget((Rotation)var46.get(), true);
               if (this.ctx.isLookingAt(var38)) {
                  this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
               }

               return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
            }
         }
      }

      if (var1) {
         this.logDirect("Farm failed");
         if (Baritone.settings().notificationOnFarmFail.value) {
            this.logNotification("Farm failed", true);
         }

         this.onLostControl();
         return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
      } else {
         ArrayList var34 = new ArrayList();

         for (BlockPos var47 : var20) {
            var34.add(new BuilderProcess.GoalBreak(var47));
         }

         if (this.baritone.getInventoryBehavior().throwaway(false, this::isPlantable)) {
            for (BlockPos var48 : var21) {
               var34.add(new GoalBlock(var48.above()));
            }
         }

         if (this.baritone.getInventoryBehavior().throwaway(false, this::isNetherWart)) {
            for (BlockPos var49 : var23) {
               var34.add(new GoalBlock(var49.above()));
            }
         }

         if (this.baritone.getInventoryBehavior().throwaway(false, this::isCocoa)) {
            for (BlockPos var50 : var24) {
               for (Direction var57 : Plane.HORIZONTAL) {
                  if (this.ctx.world().getBlockState(var50.relative(var57)).getBlock() instanceof AirBlock) {
                     var34.add(new GoalGetToBlock(var50.relative(var57)));
                  }
               }
            }
         }

         if (this.baritone.getInventoryBehavior().throwaway(false, this::isBoneMeal)) {
            for (BlockPos var51 : var22) {
               var34.add(new GoalBlock(var51));
            }
         }

         for (Entity var52 : this.ctx.entities()) {
            if (var52 instanceof ItemEntity && var52.onGround()) {
               ItemEntity var55 = (ItemEntity)var52;
               if (PICKUP_DROPPED.contains(var55.getItem().getItem())) {
                  var34.add(new GoalBlock(new BetterBlockPos(var52.position().x, var52.position().y + 0.1, var52.position().z)));
               }
            }
         }

         if (var34.isEmpty()) {
            this.logDirect("Farm failed");
            if (Baritone.settings().notificationOnFarmFail.value) {
               this.logNotification("Farm failed", true);
            }

            this.onLostControl();
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
         } else {
            return new PathingCommand(new GoalComposite((Goal[])var34.toArray(new Goal[0])), PathingCommandType.SET_GOAL_AND_PATH);
         }
      }
   }

   @Override
   public void onLostControl() {
      this.active = false;
   }

   @Override
   public String displayName0() {
      return "Farming";
   }

   private enum Harvest {
      WHEAT((CropBlock)Blocks.WHEAT),
      CARROTS((CropBlock)Blocks.CARROTS),
      POTATOES((CropBlock)Blocks.POTATOES),
      BEETROOT((CropBlock)Blocks.BEETROOTS),
      PUMPKIN(Blocks.PUMPKIN, var0 -> true),
      MELON(Blocks.MELON, var0 -> true),
      NETHERWART(Blocks.NETHER_WART, var0 -> var0.getValue(NetherWartBlock.AGE) >= 3),
      COCOA(Blocks.COCOA, var0 -> var0.getValue(CocoaBlock.AGE) >= 2),
      SUGARCANE(Blocks.SUGAR_CANE, null) {
         @Override
         public boolean readyToHarvest(Level var1, BlockPos var2, BlockState var3) {
            return Baritone.settings().replantCrops.value ? var1.getBlockState(var2.below()).getBlock() instanceof SugarCaneBlock : true;
         }
      },
      BAMBOO(Blocks.BAMBOO, null) {
         @Override
         public boolean readyToHarvest(Level var1, BlockPos var2, BlockState var3) {
            return Baritone.settings().replantCrops.value ? var1.getBlockState(var2.below()).getBlock() instanceof BambooStalkBlock : true;
         }
      },
      CACTUS(Blocks.CACTUS, null) {
         @Override
         public boolean readyToHarvest(Level var1, BlockPos var2, BlockState var3) {
            return Baritone.settings().replantCrops.value ? var1.getBlockState(var2.below()).getBlock() instanceof CactusBlock : true;
         }
      };

      public final Block block;
      public final Predicate<BlockState> readyToHarvest;

      Harvest(CropBlock var3) {
         this(var3, var3::isMaxAge);
      }

      Harvest(Block var3, Predicate<BlockState> var4) {
         this.block = var3;
         this.readyToHarvest = var4;
      }

      public boolean readyToHarvest(Level var1, BlockPos var2, BlockState var3) {
         return this.readyToHarvest.test(var3);
      }
   }
}
