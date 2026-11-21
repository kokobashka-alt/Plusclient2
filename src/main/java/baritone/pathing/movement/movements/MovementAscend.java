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
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MovementAscend extends Movement {
   private int ticksWithoutPlacement = 0;

   public MovementAscend(IBaritone var1, BetterBlockPos var2, BetterBlockPos var3) {
      super(var1, var2, var3, new BetterBlockPos[]{var3, var2.above(2), var3.above()}, var3.below());
   }

   @Override
   public void reset() {
      super.reset();
      this.ticksWithoutPlacement = 0;
   }

   @Override
   public double calculateCost(CalculationContext var1) {
      return cost(var1, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z);
   }

   @Override
   protected Set<BetterBlockPos> calculateValidPositions() {
      BetterBlockPos var1 = new BetterBlockPos(this.src.subtract(this.getDirection()).above());
      return ImmutableSet.of(this.src, this.src.above(), this.dest, var1, var1.above());
   }

   public static double cost(CalculationContext var0, int var1, int var2, int var3, int var4, int var5) {
      BlockState var6 = var0.get(var4, var2, var5);
      double var7 = 0.0;
      if (!MovementHelper.canWalkOn(var0, var4, var2, var5, var6)) {
         var7 = var0.costOfPlacingAt(var4, var2, var5, var6);
         if (var7 >= 1000000.0) {
            return 1000000.0;
         }

         if (!MovementHelper.isReplaceable(var4, var2, var5, var6, var0.bsi)) {
            return 1000000.0;
         }

         boolean var9 = false;

         for (int var10 = 0; var10 < 5; var10++) {
            int var11 = var4 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var10].getStepX();
            int var12 = var2 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var10].getStepY();
            int var13 = var5 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var10].getStepZ();
            if ((var11 != var1 || var13 != var3) && MovementHelper.canPlaceAgainst(var0.bsi, var11, var12, var13)) {
               var9 = true;
               break;
            }
         }

         if (!var9) {
            return 1000000.0;
         }
      }

      BlockState var17 = var0.get(var1, var2 + 2, var3);
      if (!(var0.get(var1, var2 + 3, var3).getBlock() instanceof FallingBlock)
         || !MovementHelper.canWalkThrough(var0, var1, var2 + 1, var3) && var17.getBlock() instanceof FallingBlock) {
         BlockState var18 = var0.get(var1, var2 - 1, var3);
         if (MovementHelper.isClimbable(var18.getBlock())) {
            return 1000000.0;
         }

         boolean var19 = MovementHelper.isBottomSlab(var18);
         boolean var20 = MovementHelper.isBottomSlab(var6);
         if (var19 && !var20) {
            return 1000000.0;
         }

         double var22;
         if (var20) {
            if (var19) {
               var22 = Math.max(JUMP_ONE_BLOCK_COST, 4.63284688441047);
               var22 += var0.jumpPenalty;
            } else {
               var22 = 4.63284688441047;
            }
         } else {
            if (var6.is(Blocks.SOUL_SAND)) {
               var22 = 9.26569376882094;
            } else if (var6.is(Blocks.MAGMA_BLOCK)) {
               var22 = 15.384615384615383;
            } else {
               var22 = Math.max(JUMP_ONE_BLOCK_COST, 4.63284688441047);
            }

            var22 += var0.jumpPenalty;
         }

         double var15 = var22 + var7;
         var15 += MovementHelper.getMiningDurationTicks(var0, var1, var2 + 2, var3, var17, false);
         if (var15 >= 1000000.0) {
            return 1000000.0;
         }

         var15 += MovementHelper.getMiningDurationTicks(var0, var4, var2 + 1, var5, false);
         return var15 >= 1000000.0 ? 1000000.0 : var15 + MovementHelper.getMiningDurationTicks(var0, var4, var2 + 2, var5, true);
      } else {
         return 1000000.0;
      }
   }

   @Override
   public MovementState updateState(MovementState var1) {
      if (this.ctx.playerFeet().y < this.src.y) {
         return var1.setStatus(MovementStatus.UNREACHABLE);
      }

      super.updateState(var1);
      if (var1.getStatus() != MovementStatus.RUNNING) {
         return var1;
      }

      if (!this.ctx.playerFeet().equals(this.dest) && !this.ctx.playerFeet().equals(this.dest.offset(this.getDirection().below()))) {
         BlockState var2 = BlockStateInterface.get(this.ctx, this.positionToPlace);
         if (!MovementHelper.canWalkOn(this.ctx, this.positionToPlace, var2)) {
            this.ticksWithoutPlacement++;
            if (MovementHelper.attemptToPlaceABlock(var1, this.baritone, this.dest.below(), false, true) == MovementHelper.PlaceResult.READY_TO_PLACE) {
               var1.setInput(Input.SNEAK, true);
               if (this.ctx.player().isCrouching()) {
                  var1.setInput(Input.CLICK_RIGHT, true);
               }
            }

            if (this.ticksWithoutPlacement > 10) {
               var1.setInput(Input.MOVE_BACK, true);
            }

            return var1;
         } else {
            MovementHelper.moveTowards(this.ctx, var1, this.dest);
            var1.setInput(Input.SNEAK, Baritone.settings().allowWalkOnMagmaBlocks.value && var2.is(Blocks.MAGMA_BLOCK));
            if (MovementHelper.isBottomSlab(var2) && !MovementHelper.isBottomSlab(BlockStateInterface.get(this.ctx, this.src.below()))) {
               return var1;
            }

            if (!Baritone.settings().assumeStep.value && !this.ctx.playerFeet().equals(this.src.above())) {
               int var3 = Math.abs(this.src.getX() - this.dest.getX());
               int var4 = Math.abs(this.src.getZ() - this.dest.getZ());
               double var5 = var3 * Math.abs(this.dest.getX() + 0.5 - this.ctx.player().position().x)
                  + var4 * Math.abs(this.dest.getZ() + 0.5 - this.ctx.player().position().z);
               double var7 = var4 * Math.abs(this.dest.getX() + 0.5 - this.ctx.player().position().x)
                  + var3 * Math.abs(this.dest.getZ() + 0.5 - this.ctx.player().position().z);
               double var9 = var3 * this.ctx.player().getDeltaMovement().z + var4 * this.ctx.player().getDeltaMovement().x;
               if (Math.abs(var9) > 0.1) {
                  return var1;
               } else if (this.headBonkClear()) {
                  return var1.setInput(Input.JUMP, true);
               } else {
                  return !(var5 > 1.2) && !(var7 > 0.2) ? var1.setInput(Input.JUMP, true) : var1;
               }
            } else {
               return var1;
            }
         }
      } else {
         return var1.setStatus(MovementStatus.SUCCESS);
      }
   }

   public boolean headBonkClear() {
      BetterBlockPos var1 = this.src.above(2);

      for (int var2 = 0; var2 < 4; var2++) {
         BetterBlockPos var3 = var1.relative(Direction.from2DDataValue(var2));
         if (!MovementHelper.canWalkThrough(this.ctx, var3)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean safeToCancel(MovementState var1) {
      return var1.getStatus() != MovementStatus.RUNNING || this.ticksWithoutPlacement == 0;
   }
}
