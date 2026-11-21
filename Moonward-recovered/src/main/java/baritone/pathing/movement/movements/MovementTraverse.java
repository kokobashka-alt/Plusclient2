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
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;

public class MovementTraverse extends Movement {
   private boolean wasTheBridgeBlockAlwaysThere = true;

   public MovementTraverse(IBaritone var1, BetterBlockPos var2, BetterBlockPos var3) {
      super(var1, var2, var3, new BetterBlockPos[]{var3.above(), var3}, var3.below());
   }

   @Override
   public void reset() {
      super.reset();
      this.wasTheBridgeBlockAlwaysThere = true;
   }

   @Override
   public double calculateCost(CalculationContext var1) {
      return cost(var1, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z);
   }

   @Override
   protected Set<BetterBlockPos> calculateValidPositions() {
      return ImmutableSet.of(this.src, this.dest);
   }

   public static double cost(CalculationContext var0, int var1, int var2, int var3, int var4, int var5) {
      BlockState var6 = var0.get(var4, var2 + 1, var5);
      BlockState var7 = var0.get(var4, var2, var5);
      BlockState var8 = var0.get(var4, var2 - 1, var5);
      BlockState var9 = var0.get(var1, var2 - 1, var3);
      Block var10 = var9.getBlock();
      boolean var11 = MovementHelper.mustBeSolidToWalkOn(var0, var1, var2 - 1, var3, var9);
      boolean var12 = var11 && !var0.assumeWalkOnWater && MovementHelper.canUseFrostWalker(var0, var8);
      if (!var12 && !MovementHelper.canWalkOn(var0, var4, var2 - 1, var5, var8)) {
         if (MovementHelper.isClimbable(var10)) {
            return 1000000.0;
         }

         if (!MovementHelper.isReplaceable(var4, var2 - 1, var5, var8, var0.bsi)) {
            return 1000000.0;
         }

         boolean var26 = MovementHelper.isWater(var6) || MovementHelper.isWater(var7);
         if (MovementHelper.isWater(var8) && var26) {
            return 1000000.0;
         }

         double var14 = var0.costOfPlacingAt(var4, var2 - 1, var5, var8);
         if (var14 >= 1000000.0) {
            return 1000000.0;
         }

         double var27 = MovementHelper.getMiningDurationTicks(var0, var4, var2, var5, var7, false);
         if (var27 >= 1000000.0) {
            return 1000000.0;
         }

         double var18 = MovementHelper.getMiningDurationTicks(var0, var4, var2 + 1, var5, var6, true);
         double var20 = var26 ? var0.waterWalkSpeed : 4.63284688441047;

         for (int var22 = 0; var22 < 5; var22++) {
            int var23 = var4 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var22].getStepX();
            int var24 = var2 - 1 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var22].getStepY();
            int var25 = var5 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[var22].getStepZ();
            if ((var23 != var1 || var25 != var3) && MovementHelper.canPlaceAgainst(var0.bsi, var23, var24, var25)) {
               return var20 + var14 + var27 + var18;
            }
         }

         if (var10 == Blocks.SOUL_SAND || var10 instanceof SlabBlock && var9.getValue(SlabBlock.TYPE) != SlabType.DOUBLE) {
            return 1000000.0;
         }

         if (!var11) {
            return 1000000.0;
         }

         Block var29 = var0.getBlock(var1, var2, var3);
         if ((var29 == Blocks.LILY_PAD || var29 instanceof CarpetBlock) && !var9.getFluidState().isEmpty()) {
            return 1000000.0;
         }

         var20 *= 3.3207692307692307;
         return var20 + var14 + var27 + var18;
      } else {
         double var13 = 4.63284688441047;
         boolean var15 = false;
         boolean var16 = false;
         if (!MovementHelper.isWater(var6) && !MovementHelper.isWater(var7)) {
            if (var8.getBlock() == Blocks.SOUL_SAND) {
               var13 += 2.316423442205235;
            } else if (!var12 && var8.getBlock() == Blocks.WATER) {
               var13 += var0.walkOnWaterOnePenalty;
            }

            if (var10 == Blocks.SOUL_SAND) {
               var13 += 2.316423442205235;
            } else if (var0.allowWalkOnMagmaBlocks && var10.equals(Blocks.MAGMA_BLOCK)) {
               var16 = true;
               var13 += 5.375884250102457;
            }
         } else {
            var13 = var0.waterWalkSpeed;
            var15 = true;
         }

         double var17 = MovementHelper.getMiningDurationTicks(var0, var4, var2, var5, var7, false);
         if (var17 >= 1000000.0) {
            return 1000000.0;
         }

         double var19 = MovementHelper.getMiningDurationTicks(var0, var4, var2 + 1, var5, var6, true);
         if (var17 == 0.0 && var19 == 0.0) {
            if (!var15 && !var16 && var0.canSprint) {
               var13 *= 0.7692444761225944;
            }

            return var13;
         } else {
            if (MovementHelper.isClimbable(var10)) {
               var17 *= 5.0;
               var19 *= 5.0;
            }

            return var13 + var17 + var19;
         }
      }
   }

   @Override
   public MovementState updateState(MovementState var1) {
      super.updateState(var1);
      BlockState var2 = BlockStateInterface.get(this.ctx, this.positionsToBreak[0]);
      BlockState var3 = BlockStateInterface.get(this.ctx, this.positionsToBreak[1]);
      if (var1.getStatus() != MovementStatus.RUNNING) {
         if (!Baritone.settings().walkWhileBreaking.value) {
            return var1;
         }

         if (var1.getStatus() != MovementStatus.PREPPING) {
            return var1;
         }

         if (MovementHelper.avoidWalkingInto(var2)) {
            return var1;
         }

         if (MovementHelper.avoidWalkingInto(var3)) {
            return var1;
         }

         double var24 = Math.max(
            Math.abs(this.ctx.player().position().x - (this.dest.getX() + 0.5)), Math.abs(this.ctx.player().position().z - (this.dest.getZ() + 0.5))
         );
         if (var24 < 0.83) {
            return var1;
         }

         if (!var1.getTarget().getRotation().isPresent()) {
            return var1;
         }

         float var27 = RotationUtils.calcRotationFromVec3d(
               this.ctx.playerHead(), VecUtils.calculateBlockCenter(this.ctx.world(), this.dest), this.ctx.playerRotations()
            )
            .getYaw();
         float var30 = var1.getTarget().getRotation().get().getPitch();
         if (MovementHelper.isBlockNormalCube(var2)
            || var2.getBlock() instanceof AirBlock && (MovementHelper.isBlockNormalCube(var3) || var3.getBlock() instanceof AirBlock)) {
            var30 = 26.0F;
         }

         return var1.setTarget(new MovementState.MovementTarget(new Rotation(var27, var30), true))
            .setInput(Input.MOVE_FORWARD, true)
            .setInput(Input.SPRINT, true);
      } else {
         Block var4 = BlockStateInterface.get(this.ctx, this.src.below()).getBlock();
         boolean var5 = MovementHelper.isClimbable(var4);
         var1.setInput(
            Input.SNEAK,
            Baritone.settings().allowWalkOnMagmaBlocks.value
               && MovementHelper.steppingOnBlocks(this.ctx).stream().anyMatch(var1x -> this.ctx.world().getBlockState(var1x).is(Blocks.MAGMA_BLOCK))
         );
         if (var2.getBlock() instanceof DoorBlock || var3.getBlock() instanceof DoorBlock) {
            boolean var6 = var2.getBlock() instanceof DoorBlock && !MovementHelper.isDoorPassable(this.ctx, this.src, this.dest)
               || var3.getBlock() instanceof DoorBlock && !MovementHelper.isDoorPassable(this.ctx, this.dest, this.src);
            boolean var7 = !Blocks.IRON_DOOR.equals(var2.getBlock()) && !Blocks.IRON_DOOR.equals(var3.getBlock());
            if (var6 && var7) {
               return var1.setTarget(
                     new MovementState.MovementTarget(
                        RotationUtils.calcRotationFromVec3d(
                           this.ctx.playerHead(), VecUtils.calculateBlockCenter(this.ctx.world(), this.positionsToBreak[0]), this.ctx.playerRotations()
                        ),
                        true
                     )
                  )
                  .setInput(Input.CLICK_RIGHT, true);
            }
         }

         if (var2.getBlock() instanceof FenceGateBlock || var3.getBlock() instanceof FenceGateBlock) {
            BetterBlockPos var25 = !MovementHelper.isGatePassable(this.ctx, this.positionsToBreak[0], this.src.above())
               ? this.positionsToBreak[0]
               : (!MovementHelper.isGatePassable(this.ctx, this.positionsToBreak[1], this.src) ? this.positionsToBreak[1] : null);
            if (var25 != null) {
               var var28 = RotationUtils.reachable(this.ctx, var25);
               if (var28.isPresent()) {
                  return var1.setTarget(new MovementState.MovementTarget((Rotation)var28.get(), true)).setInput(Input.CLICK_RIGHT, true);
               }
            }
         }

         boolean var26 = MovementHelper.canWalkOn(this.ctx, this.positionToPlace) || var5 || MovementHelper.canUseFrostWalker(this.ctx, this.positionToPlace);
         BetterBlockPos var29 = this.ctx.playerFeet();
         if (var29.getY() != this.dest.getY() && !var5) {
            this.logDebug("Wrong Y coordinate");
            if (var29.getY() < this.dest.getY()) {
               System.out.println("In movement traverse");
               return var1.setInput(Input.JUMP, true);
            } else {
               return var1;
            }
         } else if (var26) {
            if (var29.equals(this.dest)) {
               return var1.setStatus(MovementStatus.SUCCESS);
            }

            if (!Baritone.settings().overshootTraverse.value
               || !var29.equals(this.dest.offset(this.getDirection())) && !var29.equals(this.dest.offset(this.getDirection()).offset(this.getDirection()))) {
               Block var31 = BlockStateInterface.get(this.ctx, this.src).getBlock();
               Block var33 = BlockStateInterface.get(this.ctx, this.src.above()).getBlock();
               double var10001 = this.src.y;
               if (!(this.ctx.player().position().y > var10001 + 0.1)
                  || this.ctx.player().onGround()
                  || !MovementHelper.isClimbable(var31) && !MovementHelper.isClimbable(var33)) {
                  BlockPos var10 = this.dest.subtract(this.src).offset(this.dest);
                  BlockState var34 = BlockStateInterface.get(this.ctx, var10);
                  BlockState var36 = BlockStateInterface.get(this.ctx, var10.above());
                  if (this.wasTheBridgeBlockAlwaysThere
                     && (!MovementHelper.isLiquid(this.ctx, var29) || Baritone.settings().sprintInWater.value)
                     && (!MovementHelper.avoidWalkingInto(var34) || MovementHelper.isWater(var34))
                     && !MovementHelper.avoidWalkingInto(var36)) {
                     var1.setInput(Input.SPRINT, true);
                  }

                  BlockState var13 = BlockStateInterface.get(this.ctx, this.dest.below());
                  if (var29.getY() != this.dest.getY() && var5 && MovementHelper.isClimbable(var13.getBlock())) {
                     var1.setInput(Input.JUMP, true);
                  }

                  MovementHelper.moveTowards(this.ctx, var1, this.positionsToBreak[0]);
                  return var1;
               } else {
                  return var1;
               }
            } else {
               return var1.setStatus(MovementStatus.SUCCESS);
            }
         } else {
            this.wasTheBridgeBlockAlwaysThere = false;
            Block var8 = BlockStateInterface.get(this.ctx, var29.below()).getBlock();
            if (var8.equals(Blocks.SOUL_SAND) || var8 instanceof SlabBlock) {
               double var9 = Math.max(
                  Math.abs(this.dest.getX() + 0.5 - this.ctx.player().position().x), Math.abs(this.dest.getZ() + 0.5 - this.ctx.player().position().z)
               );
               if (var9 < 0.85) {
                  MovementHelper.moveTowards(this.ctx, var1, this.dest);
                  return var1.setInput(Input.MOVE_FORWARD, false).setInput(Input.MOVE_BACK, true);
               }
            }

            double var32 = Math.max(
               Math.abs(this.ctx.player().position().x - (this.dest.getX() + 0.5)), Math.abs(this.ctx.player().position().z - (this.dest.getZ() + 0.5))
            );
            MovementHelper.PlaceResult var11 = MovementHelper.attemptToPlaceABlock(
               var1, this.baritone, this.dest.below(), false, !Baritone.settings().assumeSafeWalk.value
            );
            if ((var11 == MovementHelper.PlaceResult.READY_TO_PLACE || var32 < 0.6) && !Baritone.settings().assumeSafeWalk.value) {
               var1.setInput(Input.SNEAK, true);
            }

            switch (var11) {
               case READY_TO_PLACE:
                  if (this.ctx.player().isCrouching() || Baritone.settings().assumeSafeWalk.value) {
                     var1.setInput(Input.CLICK_RIGHT, true);
                  }

                  return var1;
               case ATTEMPTING:
                  if (var32 > 0.83) {
                     float var35 = RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), VecUtils.getBlockPosCenter(this.dest), this.ctx.playerRotations())
                        .getYaw();
                     if (Math.abs(var1.getTarget().rotation.getYaw() - var35) < 0.1) {
                        return var1.setInput(Input.MOVE_FORWARD, true);
                     }
                  } else if (this.ctx.playerRotations().isReallyCloseTo(var1.getTarget().rotation)) {
                     return var1.setInput(Input.CLICK_LEFT, true);
                  }

                  return var1;
               default:
                  if (var29.equals(this.dest)) {
                     double var12 = (this.dest.getX() + this.src.getX() + 1.0) * 0.5;
                     double var14 = (this.dest.getY() + this.src.getY() - 1.0) * 0.5;
                     double var16 = (this.dest.getZ() + this.src.getZ() + 1.0) * 0.5;
                     BetterBlockPos var18 = this.src.below();
                     Rotation var19 = RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), new Vec3(var12, var14, var16), this.ctx.playerRotations());
                     float var20 = var19.getPitch();
                     double var21 = Math.max(Math.abs(this.ctx.player().position().x - var12), Math.abs(this.ctx.player().position().z - var16));
                     if (var21 < 0.29) {
                        float var23 = RotationUtils.calcRotationFromVec3d(
                              VecUtils.getBlockPosCenter(this.dest), this.ctx.playerHead(), this.ctx.playerRotations()
                           )
                           .getYaw();
                        var1.setTarget(new MovementState.MovementTarget(new Rotation(var23, var20), true));
                        var1.setInput(Input.MOVE_BACK, true);
                     } else {
                        var1.setTarget(new MovementState.MovementTarget(var19, true));
                     }

                     if (this.ctx.isLookingAt(var18)) {
                        return var1.setInput(Input.CLICK_RIGHT, true);
                     } else {
                        if (this.ctx.playerRotations().isReallyCloseTo(var1.getTarget().rotation)) {
                           var1.setInput(Input.CLICK_LEFT, true);
                        }

                        return var1;
                     }
                  } else {
                     MovementHelper.moveTowardsWithSlightRotation(this.ctx, var1, this.dest);
                     return var1;
                  }
            }
         }
      }
   }

   @Override
   public boolean safeToCancel(MovementState var1) {
      return var1.getStatus() != MovementStatus.RUNNING || MovementHelper.canWalkOn(this.ctx, this.dest.below());
   }

   @Override
   protected boolean prepared(MovementState var1) {
      if (this.ctx.playerFeet().equals(this.src) || this.ctx.playerFeet().equals(this.src.below())) {
         Block var2 = BlockStateInterface.getBlock(this.ctx, this.src.below());
         if (MovementHelper.isClimbable(var2)) {
            var1.setInput(Input.SNEAK, true);
         }
      }

      return super.prepared(var1);
   }
}
