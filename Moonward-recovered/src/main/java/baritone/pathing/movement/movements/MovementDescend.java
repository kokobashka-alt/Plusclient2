package baritone.pathing.movement.movements;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.pathing.movement.MovementStatus;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.movement.MovementState;
import baritone.utils.BlockStateInterface;
import baritone.utils.pathing.MutableMoveResult;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MovementDescend extends Movement {
   private int numTicks = 0;
   public boolean forceSafeMode = false;

   public MovementDescend(IBaritone var1, BetterBlockPos var2, BetterBlockPos var3) {
      super(var1, var2, var3, new BetterBlockPos[]{var3.above(2), var3.above(), var3}, var3.below());
   }

   @Override
   public void reset() {
      super.reset();
      this.numTicks = 0;
      this.forceSafeMode = false;
   }

   public void forceSafeMode() {
      this.forceSafeMode = true;
   }

   @Override
   public double calculateCost(CalculationContext var1) {
      MutableMoveResult var2 = new MutableMoveResult();
      cost(var1, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z, var2);
      return var2.y != this.dest.y ? 1000000.0 : var2.cost;
   }

   @Override
   protected Set<BetterBlockPos> calculateValidPositions() {
      return ImmutableSet.of(this.src, this.dest.above(), this.dest);
   }

   public static void cost(CalculationContext var0, int var1, int var2, int var3, int var4, int var5, MutableMoveResult var6) {
      double var7 = 0.0;
      BlockState var9 = var0.get(var4, var2 - 1, var5);
      var7 += MovementHelper.getMiningDurationTicks(var0, var4, var2 - 1, var5, var9, false);
      if (!(var7 >= 1000000.0)) {
         var7 += MovementHelper.getMiningDurationTicks(var0, var4, var2, var5, false);
         if (!(var7 >= 1000000.0)) {
            var7 += MovementHelper.getMiningDurationTicks(var0, var4, var2 + 1, var5, true);
            if (!(var7 >= 1000000.0)) {
               Block var10 = var0.get(var1, var2 - 1, var3).getBlock();
               if (!MovementHelper.isClimbable(var10)) {
                  BlockState var11 = var0.get(var4, var2 - 2, var5);
                  if (!MovementHelper.canWalkOn(var0, var4, var2 - 2, var5, var11)) {
                     dynamicFallCost(var0, var1, var2, var3, var4, var5, var7, var11, var6);
                  } else if (var9.getBlock() != Blocks.LADDER && var9.getBlock() != Blocks.VINE) {
                     if (!MovementHelper.canUseFrostWalker(var0, var9)) {
                        double var12 = 3.7062775075283763;
                        if (var10 == Blocks.SOUL_SAND) {
                           var12 *= 2.0;
                        }

                        var7 += var12 + Math.max(FALL_N_BLOCKS_COST[1], 0.9265693768820937);
                        var6.x = var4;
                        var6.y = var2 - 1;
                        var6.z = var5;
                        var6.cost = var7;
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean dynamicFallCost(
      CalculationContext var0, int var1, int var2, int var3, int var4, int var5, double var6, BlockState var8, MutableMoveResult var9
   ) {
      if (var6 != 0.0 && var0.get(var4, var2 + 2, var5).getBlock() instanceof FallingBlock) {
         return false;
      }

      if (!MovementHelper.canWalkThrough(var0, var4, var2 - 2, var5, var8)) {
         return false;
      }

      double var10 = 0.0;
      int var12 = var2;
      int var13 = 3;

      while (true) {
         int var14 = var2 - var13;
         if (var14 < var0.world.getMinY()) {
            return false;
         }

         boolean var15 = var13 >= var0.minFallHeight;
         BlockState var16 = var0.get(var4, var14, var5);
         int var17 = var13 - (var2 - var12);
         double var18 = 3.7062775075283763 + FALL_N_BLOCKS_COST[var17] + var6 + var10;
         if (var15 && MovementHelper.isWater(var16)) {
            if (!MovementHelper.canWalkThrough(var0, var4, var14, var5, var16)) {
               return false;
            }

            if (var0.assumeWalkOnWater) {
               return false;
            }

            if (MovementHelper.isFlowing(var4, var14, var5, var16, var0.bsi)) {
               return false;
            }

            if (!MovementHelper.canWalkOn(var0, var4, var14 - 1, var5)) {
               return false;
            }

            var9.x = var4;
            var9.y = var14;
            var9.z = var5;
            var9.cost = var18;
            return false;
         }

         if (var15 && var0.allowFallIntoLava && MovementHelper.isLava(var16)) {
            var9.x = var4;
            var9.y = var14;
            var9.z = var5;
            var9.cost = var18;
            return false;
         }

         if (var17 <= 11 && MovementHelper.isClimbable(var16.getBlock())) {
            var10 += FALL_N_BLOCKS_COST[var17 - 1];
            var10 += 6.666666666666667;
            var12 = var14;
         } else if (!MovementHelper.canWalkThrough(var0, var4, var14, var5, var16)) {
            if (!MovementHelper.canWalkOn(var0, var4, var14, var5, var16)) {
               return false;
            }

            if (MovementHelper.isBottomSlab(var16)) {
               return false;
            }

            if (var15 && var17 <= var0.maxFallHeightNoWater + 1) {
               var9.x = var4;
               var9.y = var14 + 1;
               var9.z = var5;
               var9.cost = var18;
               return false;
            }

            if (var15 && var0.hasWaterBucket && var17 <= var0.maxFallHeightBucket + 1) {
               var9.x = var4;
               var9.y = var14 + 1;
               var9.z = var5;
               var9.cost = var18 + var0.placeBucketCost();
               return true;
            }

            return false;
         }

         var13++;
      }
   }

   @Override
   public MovementState updateState(MovementState var1) {
      super.updateState(var1);
      if (var1.getStatus() != MovementStatus.RUNNING) {
         return var1;
      }

      BetterBlockPos var2 = this.ctx.playerFeet();
      BlockPos var3 = new BlockPos(this.dest.getX() * 2 - this.src.getX(), this.dest.getY(), this.dest.getZ() * 2 - this.src.getZ());
      if (!var2.equals(this.dest) && !var2.equals(var3)
         || !MovementHelper.isLiquid(this.ctx, this.dest) && !(this.ctx.player().position().y - this.dest.getY() < 0.5)) {
         if (this.safeMode()) {
            double var16 = (this.src.getX() + 0.5) * 0.17 + (this.dest.getX() + 0.5) * 0.83;
            double var17 = (this.src.getZ() + 0.5) * 0.17 + (this.dest.getZ() + 0.5) * 0.83;
            var1.setTarget(
                  new MovementState.MovementTarget(
                     RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), new Vec3(var16, this.dest.getY(), var17), this.ctx.playerRotations())
                        .withPitch(this.ctx.playerRotations().getPitch()),
                     false
                  )
               )
               .setInput(Input.MOVE_FORWARD, true);
            return var1;
         }

         double var4 = this.ctx.player().position().x - (this.dest.getX() + 0.5);
         double var6 = this.ctx.player().position().z - (this.dest.getZ() + 0.5);
         double var8 = Math.sqrt(var4 * var4 + var6 * var6);
         double var10 = this.ctx.player().position().x - (this.src.getX() + 0.5);
         double var12 = this.ctx.player().position().z - (this.src.getZ() + 0.5);
         double var14 = Math.sqrt(var10 * var10 + var12 * var12);
         var1.setInput(
            Input.SNEAK,
            Baritone.settings().allowWalkOnMagmaBlocks.value
               && this.ctx.world().getBlockState(this.ctx.player().blockPosition().below()).is(Blocks.MAGMA_BLOCK)
         );
         if (!var2.equals(this.dest) || var8 > 0.25) {
            if (this.numTicks++ < 20 && var14 < 1.25) {
               MovementHelper.moveTowards(this.ctx, var1, var3);
            } else {
               MovementHelper.moveTowards(this.ctx, var1, this.dest);
            }
         }

         return var1;
      } else {
         return var1.setStatus(MovementStatus.SUCCESS);
      }
   }

   public boolean safeMode() {
      if (this.forceSafeMode) {
         return true;
      }

      BlockPos var1 = this.dest.subtract(this.src.below()).offset(this.dest);
      if (this.skipToAscend()) {
         return true;
      }

      for (int var2 = 0; var2 <= 2; var2++) {
         if (MovementHelper.avoidWalkingInto(BlockStateInterface.get(this.ctx, var1.above(var2)))) {
            return true;
         }
      }

      return false;
   }

   public boolean skipToAscend() {
      BlockPos var1 = this.dest.subtract(this.src.below()).offset(this.dest);
      return !MovementHelper.canWalkThrough(this.ctx, new BetterBlockPos(var1))
         && MovementHelper.canWalkThrough(this.ctx, new BetterBlockPos(var1).above())
         && MovementHelper.canWalkThrough(this.ctx, new BetterBlockPos(var1).above(2));
   }
}
