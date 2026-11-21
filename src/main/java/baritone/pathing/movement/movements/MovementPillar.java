package baritone.pathing.movement.movements;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.pathing.movement.MovementStatus;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.VecUtils;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.movement.MovementState;
import baritone.utils.BlockStateInterface;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;

public class MovementPillar extends Movement {
   public MovementPillar(IBaritone var1, BetterBlockPos var2, BetterBlockPos var3) {
      super(var1, var2, var3, new BetterBlockPos[]{var2.above(2)}, var2);
   }

   @Override
   public double calculateCost(CalculationContext var1) {
      return cost(var1, this.src.x, this.src.y, this.src.z);
   }

   @Override
   protected Set<BetterBlockPos> calculateValidPositions() {
      return ImmutableSet.of(this.src, this.dest);
   }

   public static double cost(CalculationContext var0, int var1, int var2, int var3) {
      BlockState var4 = var0.get(var1, var2, var3);
      Block var5 = var4.getBlock();
      boolean var6 = MovementHelper.isClimbable(var5);
      BlockState var7 = var0.get(var1, var2 - 1, var3);
      if (!var6) {
         if (MovementHelper.isClimbable(var7.getBlock())) {
            return 1000000.0;
         }

         if (var7.getBlock() instanceof SlabBlock && var7.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
            return 1000000.0;
         }
      }

      BlockState var8 = var0.get(var1, var2 + 2, var3);
      Block var9 = var8.getBlock();
      if (var9 instanceof FenceGateBlock) {
         return 1000000.0;
      }

      BlockState var10 = null;
      if (MovementHelper.isWater(var8) && MovementHelper.isWater(var4)) {
         var10 = var0.get(var1, var2 + 1, var3);
         if (MovementHelper.isWater(var10)) {
            return 8.51063829787234;
         }
      }

      double var11 = 0.0;
      if (!var6) {
         var11 = var0.costOfPlacingAt(var1, var2, var3, var4);
         if (var11 >= 1000000.0) {
            return 1000000.0;
         }

         if (var7.getBlock() instanceof AirBlock) {
            var11 += 0.1;
         }
      }

      if ((!MovementHelper.isLiquid(var4) || MovementHelper.canPlaceAgainst(var0.bsi, var1, var2 - 1, var3, var7))
         && (!MovementHelper.isLiquid(var7) || !var0.assumeWalkOnWater)) {
         if ((var5 == Blocks.LILY_PAD || var5 instanceof CarpetBlock) && !var7.getFluidState().isEmpty()) {
            return 1000000.0;
         }

         double var13 = MovementHelper.getMiningDurationTicks(var0, var1, var2 + 2, var3, var8, true);
         if (var13 >= 1000000.0) {
            return 1000000.0;
         }

         if (var13 != 0.0) {
            if (MovementHelper.isClimbable(var9)) {
               var13 = 0.0;
            } else {
               BlockState var15 = var0.get(var1, var2 + 3, var3);
               if (var15.getBlock() instanceof FallingBlock) {
                  if (var10 == null) {
                     var10 = var0.get(var1, var2 + 1, var3);
                  }

                  if (!(var9 instanceof FallingBlock) || !(var10.getBlock() instanceof FallingBlock)) {
                     return 1000000.0;
                  }
               }
            }
         }

         return var6 ? 8.51063829787234 + var13 * 5.0 : JUMP_ONE_BLOCK_COST + var11 + var0.jumpPenalty + var13;
      } else {
         return 1000000.0;
      }
   }

   @Override
   public MovementState updateState(MovementState var1) {
      super.updateState(var1);
      if (var1.getStatus() != MovementStatus.RUNNING) {
         return var1;
      }

      if (this.ctx.playerFeet().y < this.src.y) {
         return var1.setStatus(MovementStatus.UNREACHABLE);
      }

      BlockState var2 = BlockStateInterface.get(this.ctx, this.src);
      if (MovementHelper.isWater(var2) && MovementHelper.isWater(this.ctx, this.dest)) {
         var1.setTarget(
            new MovementState.MovementTarget(
               RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), VecUtils.getBlockPosCenter(this.dest), this.ctx.playerRotations()), false
            )
         );
         Vec3 var16 = VecUtils.getBlockPosCenter(this.dest);
         if (Math.abs(this.ctx.player().position().x - var16.x) > 0.2 || Math.abs(this.ctx.player().position().z - var16.z) > 0.2) {
            var1.setInput(Input.MOVE_FORWARD, true);
         }

         return this.ctx.playerFeet().equals(this.dest) ? var1.setStatus(MovementStatus.SUCCESS) : var1;
      } else {
         boolean var3 = MovementHelper.isClimbable(var2.getBlock());
         Rotation var4 = RotationUtils.calcRotationFromVec3d(
            this.ctx.playerHead(), VecUtils.getBlockPosCenter(this.positionToPlace), this.ctx.playerRotations()
         );
         if (!var3) {
            var1.setTarget(new MovementState.MovementTarget(this.ctx.playerRotations().withPitch(var4.getPitch()), true));
         }

         boolean var5 = MovementHelper.canWalkOn(this.ctx, this.src) || var3;
         if (var3) {
            if (this.ctx.playerFeet().equals(this.dest)) {
               return var1.setStatus(MovementStatus.SUCCESS);
            }

            MovementHelper.moveTowards(this.ctx, var1, this.dest);
            var1.setInput(Input.JUMP, true);
            return var1;
         } else {
            if (!((Baritone)this.baritone).getInventoryBehavior().selectThrowawayForLocation(true, this.src.x, this.src.y, this.src.z)) {
               return var1.setStatus(MovementStatus.UNREACHABLE);
            }

            var1.setInput(Input.SNEAK, true);
            double var6 = this.ctx.player().position().x - (this.dest.getX() + 0.5);
            double var8 = this.ctx.player().position().z - (this.dest.getZ() + 0.5);
            double var10 = Math.sqrt(var6 * var6 + var8 * var8);
            double var12 = Math.sqrt(
               this.ctx.player().getDeltaMovement().x * this.ctx.player().getDeltaMovement().x
                  + this.ctx.player().getDeltaMovement().z * this.ctx.player().getDeltaMovement().z
            );
            if (var10 > 0.17) {
               var1.setInput(Input.MOVE_FORWARD, true);
               var1.setTarget(new MovementState.MovementTarget(var4, true));
            } else if (var12 < 0.05) {
               var1.setInput(Input.JUMP, this.ctx.player().position().y < this.dest.getY());
            }

            if (!var5) {
               BlockState var14 = BlockStateInterface.get(this.ctx, this.src);
               Block var15 = var14.getBlock();
               if (!(var15 instanceof AirBlock) && !var14.canBeReplaced()) {
                  RotationUtils.reachable(this.ctx, this.src, this.ctx.playerController().getBlockReachDistance())
                     .map(var0 -> new MovementState.MovementTarget(var0, true))
                     .ifPresent(var1::setTarget);
                  var1.setInput(Input.JUMP, false);
                  var1.setInput(Input.CLICK_LEFT, true);
                  var5 = false;
               } else if (this.ctx.player().isCrouching()
                  && (this.ctx.isLookingAt(this.src.below()) || this.ctx.isLookingAt(this.src))
                  && this.ctx.player().position().y > this.dest.getY() + 0.1) {
                  var1.setInput(Input.CLICK_RIGHT, true);
               }
            }

            return this.ctx.playerFeet().equals(this.dest) && var5 ? var1.setStatus(MovementStatus.SUCCESS) : var1;
         }
      }
   }

   @Override
   protected boolean prepared(MovementState var1) {
      if (this.ctx.playerFeet().equals(this.src) || this.ctx.playerFeet().equals(this.src.below())) {
         Block var2 = BlockStateInterface.getBlock(this.ctx, this.src.below());
         if (MovementHelper.isClimbable(var2)) {
            var1.setInput(Input.SNEAK, true);
         }
      }

      return MovementHelper.isWater(this.ctx, this.dest.above()) ? true : super.prepared(var1);
   }
}
