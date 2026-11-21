package baritone.pathing.movement.movements;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.pathing.movement.MovementStatus;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.movement.MovementState;
import baritone.utils.BlockStateInterface;
import baritone.utils.pathing.MutableMoveResult;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;

public class MovementParkour extends Movement {
   private static final BetterBlockPos[] EMPTY = new BetterBlockPos[0];
   private final Direction direction;
   private final int dist;
   private final boolean ascend;

   private MovementParkour(IBaritone var1, BetterBlockPos var2, int var3, Direction var4, boolean var5) {
      super(var1, var2, var2.relative(var4, var3).above(var5 ? 1 : 0), EMPTY, var2.relative(var4, var3).below(var5 ? 0 : 1));
      this.direction = var4;
      this.dist = var3;
      this.ascend = var5;
   }

   public static MovementParkour cost(CalculationContext var0, BetterBlockPos var1, Direction var2) {
      MutableMoveResult var3 = new MutableMoveResult();
      cost(var0, var1.x, var1.y, var1.z, var2, var3);
      int var4 = Math.abs(var3.x - var1.x) + Math.abs(var3.z - var1.z);
      return new MovementParkour(var0.getBaritone(), var1, var4, var2, var3.y > var1.y);
   }

   public static void cost(CalculationContext var0, int var1, int var2, int var3, Direction var4, MutableMoveResult var5) {
      if (var0.allowParkour) {
         if (var0.allowJumpAtBuildLimit || var2 < var0.world.getMaxY()) {
            int var6 = var4.getStepX();
            int var7 = var4.getStepZ();
            if (MovementHelper.fullyPassable(var0, var1 + var6, var2, var3 + var7)) {
               BlockState var8 = var0.get(var1 + var6, var2 - 1, var3 + var7);
               if (!MovementHelper.canWalkOn(var0, var1 + var6, var2 - 1, var3 + var7, var8)) {
                  if (!MovementHelper.avoidWalkingInto(var8) || var8.getFluidState().getType() instanceof WaterFluid) {
                     if (MovementHelper.fullyPassable(var0, var1 + var6, var2 + 1, var3 + var7)) {
                        if (MovementHelper.fullyPassable(var0, var1 + var6, var2 + 2, var3 + var7)) {
                           if (MovementHelper.fullyPassable(var0, var1, var2 + 2, var3)) {
                              BlockState var9 = var0.get(var1, var2 - 1, var3);
                              if (!MovementHelper.isClimbable(var9.getBlock())
                                 && !(var9.getBlock() instanceof StairBlock)
                                 && !MovementHelper.isBottomSlab(var9)) {
                                 if (!var0.assumeWalkOnWater || var9.getFluidState().isEmpty()) {
                                    if (var0.get(var1, var2, var3).getFluidState().isEmpty()) {
                                       byte var10;
                                       if (var0.allowWalkOnMagmaBlocks && var9.is(Blocks.MAGMA_BLOCK)) {
                                          var10 = 2;
                                       } else if (var9.getBlock() == Blocks.SOUL_SAND) {
                                          var10 = 2;
                                       } else if (var0.canSprint) {
                                          var10 = 4;
                                       } else {
                                          var10 = 3;
                                       }

                                       int var11 = 1;

                                       for (int var12 = 2; var12 <= var10; var11 = var12++) {
                                          int var13 = var1 + var6 * var12;
                                          int var14 = var3 + var7 * var12;
                                          if (!MovementHelper.fullyPassable(var0, var13, var2 + 1, var14)
                                             || !MovementHelper.fullyPassable(var0, var13, var2 + 2, var14)) {
                                             break;
                                          }

                                          BlockState var15 = var0.bsi.get0(var13, var2, var14);
                                          if (!MovementHelper.fullyPassable(var0, var13, var2, var14, var15)) {
                                             if (var12 <= 3
                                                && var0.allowParkourAscend
                                                && var0.canSprint
                                                && MovementHelper.canWalkOn(var0, var13, var2, var14, var15)
                                                && checkOvershootSafety(var0.bsi, var13 + var6, var2 + 1, var14 + var7)) {
                                                var5.x = var13;
                                                var5.y = var2 + 1;
                                                var5.z = var14;
                                                var5.cost = var12 * 3.563791874554526 + var0.jumpPenalty;
                                                return;
                                             }
                                             break;
                                          }

                                          BlockState var16 = var0.bsi.get0(var13, var2 - 1, var14);
                                          if (var16.getBlock() != Blocks.FARMLAND && MovementHelper.canWalkOn(var0, var13, var2 - 1, var14, var16)
                                             || Math.min(16, var0.frostWalker + 2) >= var12 && MovementHelper.canUseFrostWalker(var0, var16)) {
                                             if (checkOvershootSafety(var0.bsi, var13 + var6, var2, var14 + var7)) {
                                                var5.x = var13;
                                                var5.y = var2;
                                                var5.z = var14;
                                                var5.cost = costFromJumpDistance(var12) + var0.jumpPenalty;
                                                return;
                                             }
                                             break;
                                          }

                                          if (!MovementHelper.fullyPassable(var0, var13, var2 + 3, var14)) {
                                             break;
                                          }
                                       }

                                       if (var0.allowParkourPlace) {
                                          for (int var22 = var11; var22 > 1; var22--) {
                                             int var23 = var1 + var22 * var6;
                                             int var24 = var3 + var22 * var7;
                                             BlockState var25 = var0.get(var23, var2 - 1, var24);
                                             double var26 = var0.costOfPlacingAt(var23, var2 - 1, var24, var25);
                                             if (!(var26 >= 1000000.0)
                                                && MovementHelper.isReplaceable(var23, var2 - 1, var24, var25, var0.bsi)
                                                && checkOvershootSafety(var0.bsi, var23 + var6, var2, var24 + var7)) {
                                                for (int var18 = 0; var18 < 5; var18++) {
                                                   int var19 = var23 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var18].getStepX();
                                                   int var20 = var2 - 1 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var18].getStepY();
                                                   int var21 = var24 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var18].getStepZ();
                                                   if ((var19 != var23 - var6 || var21 != var24 - var7)
                                                      && MovementHelper.canPlaceAgainst(var0.bsi, var19, var20, var21)) {
                                                      var5.x = var23;
                                                      var5.y = var2;
                                                      var5.z = var24;
                                                      var5.cost = costFromJumpDistance(var22) + var26 + var0.jumpPenalty;
                                                      return;
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean checkOvershootSafety(BlockStateInterface var0, int var1, int var2, int var3) {
      return !MovementHelper.avoidWalkingInto(var0.get0(var1, var2, var3)) && !MovementHelper.avoidWalkingInto(var0.get0(var1, var2 + 1, var3));
   }

   private static double costFromJumpDistance(int var0) {
      switch (var0) {
         case 2:
            return 9.26569376882094;
         case 3:
            return 13.89854065323141;
         case 4:
            return 14.255167498218103;
         default:
            throw new IllegalStateException("LOL " + var0);
      }
   }

   @Override
   public double calculateCost(CalculationContext var1) {
      MutableMoveResult var2 = new MutableMoveResult();
      cost(var1, this.src.x, this.src.y, this.src.z, this.direction, var2);
      return var2.x == this.dest.x && var2.y == this.dest.y && var2.z == this.dest.z ? var2.cost : 1000000.0;
   }

   @Override
   protected Set<BetterBlockPos> calculateValidPositions() {
      HashSet var1 = new HashSet();

      for (int var2 = 0; var2 <= this.dist; var2++) {
         for (int var3 = 0; var3 < 2; var3++) {
            var1.add(this.src.relative(this.direction, var2).above(var3));
         }
      }

      return var1;
   }

   @Override
   public boolean safeToCancel(MovementState var1) {
      return var1.getStatus() != MovementStatus.RUNNING;
   }

   @Override
   public MovementState updateState(MovementState var1) {
      super.updateState(var1);
      if (var1.getStatus() != MovementStatus.RUNNING) {
         return var1;
      }

      if (this.ctx.playerFeet().y < this.src.y) {
         this.logDebug("sorry");
         return var1.setStatus(MovementStatus.UNREACHABLE);
      }

      if (this.dist >= 4 || this.ascend) {
         var1.setInput(Input.SPRINT, true);
      }

      if (Baritone.settings().allowWalkOnMagmaBlocks.value && this.ctx.world().getBlockState(this.ctx.playerFeet().below()).is(Blocks.MAGMA_BLOCK)) {
         var1.setInput(Input.SNEAK, true);
      }

      MovementHelper.moveTowards(this.ctx, var1, this.dest);
      if (this.ctx.playerFeet().equals(this.dest)) {
         Block var2 = BlockStateInterface.getBlock(this.ctx, this.dest);
         if (var2 == Blocks.VINE || var2 == Blocks.LADDER) {
            return var1.setStatus(MovementStatus.SUCCESS);
         }

         if (this.ctx.player().position().y - this.ctx.playerFeet().getY() < 0.094) {
            var1.setStatus(MovementStatus.SUCCESS);
         }
      } else if (!this.ctx.playerFeet().equals(this.src)) {
         if (!this.ctx.playerFeet().equals(this.src.relative(this.direction)) && !(this.ctx.player().position().y - this.src.y > 1.0E-4)) {
            if (!this.ctx.playerFeet().equals(this.dest.relative(this.direction, -1))) {
               var1.setInput(Input.SPRINT, false);
               if (this.ctx.playerFeet().equals(this.src.relative(this.direction, -1))) {
                  MovementHelper.moveTowards(this.ctx, var1, this.src);
               } else {
                  MovementHelper.moveTowards(this.ctx, var1, this.src.relative(this.direction, -1));
               }
            }
         } else {
            if (Baritone.settings().allowPlace.value
               && ((Baritone)this.baritone).getInventoryBehavior().hasGenericThrowaway()
               && !MovementHelper.canWalkOn(this.ctx, this.dest.below())
               && !this.ctx.player().onGround()
               && MovementHelper.attemptToPlaceABlock(var1, this.baritone, this.dest.below(), true, false) == MovementHelper.PlaceResult.READY_TO_PLACE) {
               var1.setInput(Input.CLICK_RIGHT, true);
            }

            if (this.dist == 3 && !this.ascend) {
               double var8 = this.src.x + 0.5 - this.ctx.player().position().x;
               double var4 = this.src.z + 0.5 - this.ctx.player().position().z;
               double var6 = Math.max(Math.abs(var8), Math.abs(var4));
               if (var6 < 0.7) {
                  return var1;
               }
            }

            var1.setInput(Input.JUMP, true);
         }
      }

      return var1;
   }
}
