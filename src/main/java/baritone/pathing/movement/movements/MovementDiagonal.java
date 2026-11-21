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
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MovementDiagonal extends Movement {
   private static final double SQRT_2 = Math.sqrt(Double.longBitsToDouble(4611686018427387904L));

   public MovementDiagonal(IBaritone var1, BetterBlockPos var2, Direction var3, Direction var4, int var5) {
      this(var1, var2, var2.relative(var3), var2.relative(var4), var4, var5);
   }

   private MovementDiagonal(IBaritone var1, BetterBlockPos var2, BetterBlockPos var3, BetterBlockPos var4, Direction var5, int var6) {
      this(var1, var2, var3.relative(var5).above(var6), var3, var4);
   }

   private MovementDiagonal(IBaritone var1, BetterBlockPos var2, BetterBlockPos var3, BetterBlockPos var4, BetterBlockPos var5) {
      super(var1, var2, var3, new BetterBlockPos[]{var4, var4.above(), var5, var5.above(), var3, var3.above()});
   }

   @Override
   protected boolean safeToCancel(MovementState var1) {
      LocalPlayer var2 = this.ctx.player();
      double var3 = 0.25;
      double var5 = var2.position().x;
      double var7 = var2.position().y - 1.0;
      double var9 = var2.position().z;
      if (this.ctx.playerFeet().equals(this.src)) {
         return true;
      } else if (MovementHelper.canWalkOn(this.ctx, new BlockPos(this.src.x, this.src.y - 1, this.dest.z))
         && MovementHelper.canWalkOn(this.ctx, new BlockPos(this.dest.x, this.src.y - 1, this.src.z))) {
         return true;
      } else {
         return !this.ctx.playerFeet().equals(new BetterBlockPos(this.src.x, this.src.y, this.dest.z))
               && !this.ctx.playerFeet().equals(new BetterBlockPos(this.dest.x, this.src.y, this.src.z))
            ? true
            : MovementHelper.canWalkOn(this.ctx, new BetterBlockPos(var5 + var3, var7, var9 + var3))
               || MovementHelper.canWalkOn(this.ctx, new BetterBlockPos(var5 + var3, var7, var9 - var3))
               || MovementHelper.canWalkOn(this.ctx, new BetterBlockPos(var5 - var3, var7, var9 + var3))
               || MovementHelper.canWalkOn(this.ctx, new BetterBlockPos(var5 - var3, var7, var9 - var3));
      }
   }

   @Override
   public double calculateCost(CalculationContext var1) {
      MutableMoveResult var2 = new MutableMoveResult();
      cost(var1, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z, var2);
      return var2.y != this.dest.y ? 1000000.0 : var2.cost;
   }

   @Override
   protected Set<BetterBlockPos> calculateValidPositions() {
      BetterBlockPos var1 = new BetterBlockPos(this.src.x, this.src.y, this.dest.z);
      BetterBlockPos var2 = new BetterBlockPos(this.dest.x, this.src.y, this.src.z);
      if (this.dest.y < this.src.y) {
         return ImmutableSet.of(this.src, this.dest.above(), var1, var2, this.dest, var1.below(), new BetterBlockPos[]{var2.below()});
      } else {
         return this.dest.y > this.src.y
            ? ImmutableSet.of(this.src, this.src.above(), var1, var2, this.dest, var1.above(), new BetterBlockPos[]{var2.above()})
            : ImmutableSet.of(this.src, this.dest, var1, var2);
      }
   }

   public static void cost(CalculationContext var0, int var1, int var2, int var3, int var4, int var5, MutableMoveResult var6) {
      if (MovementHelper.canWalkThrough(var0, var4, var2 + 1, var5)) {
         BlockState var7 = var0.get(var4, var2, var5);
         boolean var9 = false;
         boolean var11 = false;
         boolean var12 = false;
         boolean var13 = false;
         BlockState var8;
         BlockState var10;
         if (!MovementHelper.canWalkThrough(var0, var4, var2, var5, var7)) {
            var9 = true;
            if (!var0.allowDiagonalAscend
               || !MovementHelper.canWalkThrough(var0, var1, var2 + 2, var3)
               || !MovementHelper.canWalkOn(var0, var4, var2, var5, var7)
               || !MovementHelper.canWalkThrough(var0, var4, var2 + 2, var5)) {
               return;
            }

            var10 = var7;
            var8 = var0.get(var1, var2 - 1, var3);
         } else {
            var10 = var0.get(var4, var2 - 1, var5);
            var8 = var0.get(var1, var2 - 1, var3);
            boolean var14 = MovementHelper.mustBeSolidToWalkOn(var0, var1, var2 - 1, var3, var8);
            var12 = var14 && MovementHelper.canUseFrostWalker(var0, var10);
            if (!var12 && !MovementHelper.canWalkOn(var0, var4, var2 - 1, var5, var10)) {
               var11 = true;
               if (!var0.allowDiagonalDescend
                  || !MovementHelper.canWalkOn(var0, var4, var2 - 2, var5)
                  || !MovementHelper.canWalkThrough(var0, var4, var2 - 1, var5, var10)) {
                  return;
               }
            }

            var12 &= !var0.assumeWalkOnWater;
         }

         double var31 = 4.63284688441047;
         if (var10.is(Blocks.SOUL_SAND)) {
            var31 += 2.316423442205235;
         } else if (var0.allowWalkOnMagmaBlocks && var10.is(Blocks.MAGMA_BLOCK)) {
            var31 += 5.375884250102457;
            var13 = true;
         } else if (!var12 && var10.getBlock() == Blocks.WATER) {
            var31 += var0.walkOnWaterOnePenalty * SQRT_2;
         }

         Block var16 = var8.getBlock();
         if (!MovementHelper.isClimbable(var16)) {
            if (var16 == Blocks.SOUL_SAND) {
               var31 += 2.316423442205235;
            } else if (var0.allowWalkOnMagmaBlocks && var16.equals(Blocks.MAGMA_BLOCK)) {
               var31 += 5.375884250102457;
               var13 = true;
            }

            BlockState var17 = var0.get(var1, var2 - 1, var5);
            if ((var0.allowWalkOnMagmaBlocks || !var17.is(Blocks.MAGMA_BLOCK)) && !MovementHelper.isLava(var17)) {
               BlockState var18 = var0.get(var4, var2 - 1, var3);
               if ((var0.allowWalkOnMagmaBlocks || !var17.is(Blocks.MAGMA_BLOCK)) && !MovementHelper.isLava(var18)) {
                  boolean var19 = false;
                  BlockState var20 = var0.get(var1, var2, var3);
                  Block var21 = var20.getBlock();
                  if (MovementHelper.isWater(var20) || MovementHelper.isWater(var7)) {
                     if (var9) {
                        return;
                     }

                     var31 = var0.waterWalkSpeed;
                     var19 = true;
                  }

                  BlockState var22 = var0.get(var1, var2, var5);
                  BlockState var23 = var0.get(var4, var2, var3);
                  if (var9) {
                     boolean var33 = MovementHelper.canWalkThrough(var0, var1, var2 + 2, var5);
                     boolean var25 = MovementHelper.canWalkThrough(var0, var1, var2 + 1, var5);
                     boolean var35 = MovementHelper.canWalkThrough(var0, var1, var2, var5, var22);
                     boolean var27 = MovementHelper.canWalkThrough(var0, var4, var2 + 2, var3);
                     boolean var36 = MovementHelper.canWalkThrough(var0, var4, var2 + 1, var3);
                     boolean var37 = MovementHelper.canWalkThrough(var0, var4, var2, var3, var23);
                     if ((var33 && var25 && var35 || var27 && var36 && var37)
                        && !MovementHelper.avoidWalkingInto(var22)
                        && !MovementHelper.avoidWalkingInto(var23)
                        && (!var33 || !var25 || !MovementHelper.canWalkOn(var0, var1, var2, var5, var22))
                        && (!var27 || !var36 || !MovementHelper.canWalkOn(var0, var4, var2, var3, var23))
                        && (var33 || !var25 || !var35)
                        && (var27 || !var36 || !var37)) {
                        var6.cost = var31 * SQRT_2 + JUMP_ONE_BLOCK_COST;
                        var6.x = var4;
                        var6.z = var5;
                        var6.y = var2 + 1;
                     }
                  } else {
                     double var24 = MovementHelper.getMiningDurationTicks(var0, var1, var2, var5, var22, false);
                     double var26 = MovementHelper.getMiningDurationTicks(var0, var4, var2, var3, var23, false);
                     if (var24 == 0.0 || var26 == 0.0) {
                        BlockState var28 = var0.get(var1, var2 + 1, var5);
                        var24 += MovementHelper.getMiningDurationTicks(var0, var1, var2 + 1, var5, var28, true);
                        if (var24 == 0.0 || var26 == 0.0) {
                           BlockState var29 = var0.get(var4, var2 + 1, var3);
                           if (var24 != 0.0
                              || (!MovementHelper.avoidWalkingInto(var23) || var23.getBlock() == Blocks.WATER) && !MovementHelper.avoidWalkingInto(var29)) {
                              var26 += MovementHelper.getMiningDurationTicks(var0, var4, var2 + 1, var3, var29, true);
                              if (var24 == 0.0 || var26 == 0.0) {
                                 if (var26 != 0.0
                                    || (!MovementHelper.avoidWalkingInto(var22) || var22.getBlock() == Blocks.WATER) && !MovementHelper.avoidWalkingInto(var28)
                                    )
                                  {
                                    if (var24 != 0.0 || var26 != 0.0) {
                                       var31 *= SQRT_2 - 0.001;
                                       if (MovementHelper.isClimbable(var21)) {
                                          return;
                                       }
                                    } else if (var0.canSprint && !var19 && !var13) {
                                       var31 *= 0.7692444761225944;
                                    }

                                    var6.cost = var31 * SQRT_2;
                                    if (var11) {
                                       var6.cost = var6.cost + Math.max(FALL_N_BLOCKS_COST[1], 0.9265693768820937);
                                       var6.y = var2 - 1;
                                    } else {
                                       var6.y = var2;
                                    }

                                    var6.x = var4;
                                    var6.z = var5;
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

   @Override
   public MovementState updateState(MovementState var1) {
      super.updateState(var1);
      if (var1.getStatus() != MovementStatus.RUNNING) {
         return var1;
      }

      if (this.ctx.playerFeet().equals(this.dest)) {
         return var1.setStatus(MovementStatus.SUCCESS);
      }

      if (this.playerInValidPosition() || MovementHelper.isLiquid(this.ctx, this.src) && this.getValidPositions().contains(this.ctx.playerFeet().above())) {
         if (this.dest.y > this.src.y) {
            double var10001 = this.src.y;
            if (this.ctx.player().position().y < var10001 + 0.1 && this.ctx.player().horizontalCollision) {
               var1.setInput(Input.JUMP, true);
            }
         }

         if (this.sprint()) {
            var1.setInput(Input.SPRINT, true);
         }

         var1.setInput(
            Input.SNEAK,
            Baritone.settings().allowWalkOnMagmaBlocks.value
               && MovementHelper.steppingOnBlocks(this.ctx).stream().anyMatch(var1x -> this.ctx.world().getBlockState(var1x).is(Blocks.MAGMA_BLOCK))
         );
         MovementHelper.moveTowards(this.ctx, var1, this.dest);
         return var1;
      } else {
         return var1.setStatus(MovementStatus.UNREACHABLE);
      }
   }

   private boolean sprint() {
      if (MovementHelper.isLiquid(this.ctx, this.ctx.playerFeet()) && !Baritone.settings().sprintInWater.value) {
         return false;
      }

      for (int var1 = 0; var1 < 4; var1++) {
         if (!MovementHelper.canWalkThrough(this.ctx, this.positionsToBreak[var1])) {
            return false;
         }
      }

      return true;
   }

   @Override
   protected boolean prepared(MovementState var1) {
      return true;
   }

   @Override
   public List<BlockPos> toBreak(BlockStateInterface var1) {
      if (this.toBreakCached != null) {
         return this.toBreakCached;
      }

      ArrayList var2 = new ArrayList();

      for (int var3 = 4; var3 < 6; var3++) {
         if (!MovementHelper.canWalkThrough(var1, this.positionsToBreak[var3].x, this.positionsToBreak[var3].y, this.positionsToBreak[var3].z)) {
            var2.add(this.positionsToBreak[var3]);
         }
      }

      this.toBreakCached = var2;
      return var2;
   }

   @Override
   public List<BlockPos> toWalkInto(BlockStateInterface var1) {
      if (this.toWalkIntoCached == null) {
         this.toWalkIntoCached = new ArrayList<>();
      }

      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < 4; var3++) {
         if (!MovementHelper.canWalkThrough(var1, this.positionsToBreak[var3].x, this.positionsToBreak[var3].y, this.positionsToBreak[var3].z)) {
            var2.add(this.positionsToBreak[var3]);
         }
      }

      this.toWalkIntoCached = var2;
      return this.toWalkIntoCached;
   }
}
