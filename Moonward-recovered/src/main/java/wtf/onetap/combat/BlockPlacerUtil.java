package wtf.onetap.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

public class BlockPlacerUtil implements MinecraftAccess {
   private BlockPlacerUtil() {
   }

   public static boolean m_5(BlockPos blockPos, Direction direction, Vec3 vec3) {
      return switch (direction) {
         case UP -> vec3.y >= blockPos.getY() + 1.0;
         case DOWN -> vec3.y <= blockPos.getY();
         case NORTH -> vec3.z <= blockPos.getZ();
         case SOUTH -> vec3.z >= blockPos.getZ() + 1.0;
         case WEST -> vec3.x <= blockPos.getX();
         case EAST -> vec3.x >= blockPos.getX() + 1.0;
      };
   }

   public static Direction m_3(BlockPos blockPos, Player player) {
      Direction direction = null;
      double doubleValue = Double.MAX_VALUE;
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      Vec3 vec32 = player.position();

      for (Direction direction2 : Direction.values()) {
         if (m_5(blockPos, direction2, vec3)) {
            BlockPos blockPos2 = blockPos.relative(direction2);
            if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2).isAir()) {
               double doubleValue2 = Vec3.atBottomCenterOf(blockPos2.above()).distanceToSqr(vec32);
               if (doubleValue2 < doubleValue) {
                  doubleValue = doubleValue2;
                  direction = direction2;
               }
            }
         }
      }

      return direction;
   }

   public static Direction m_0(BlockPos blockPos, Player player) {
      Vec3 vec3 = player.getEyePosition();
      Direction direction = null;
      double doubleValue = Double.MAX_VALUE;

      for (Direction direction2 : Direction.values()) {
         if (m_5(blockPos, direction2, vec3)) {
            Vec3 vec32 = m_6(blockPos, direction2);
            Rotations rotations = new Rotations(MinecraftAccess.MINECRAFT.player.getEyePosition(), vec32);
            if (AimUtil.m_3(rotations, blockPos, 4.5) != null) {
               double doubleValue2 = vec3.distanceToSqr(vec32);
               if (doubleValue2 < doubleValue) {
                  doubleValue = doubleValue2;
                  direction = direction2;
               }
            }
         }
      }

      return direction;
   }

   public static BlockHitResult m_5(BlockPos blockPos, float floatValue) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos);
         if (blockState.canBeReplaced() && blockState.getFluidState().isEmpty()) {
            Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
            BlockHitResult blockHitResult = null;
            double doubleValue = Double.MAX_VALUE;
            Direction[] directionArray = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};

            for (Direction direction : directionArray) {
               BlockPos blockPos2 = blockPos.relative(direction);
               Direction direction2 = direction.getOpposite();
               BlockState blockState2 = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
               if (m_6(blockState2, blockPos2) && m_5(blockPos2, direction2, vec3)) {
                  Vec3 vec32 = m_6(blockPos2, direction2);
                  BlockHitResult blockHitResult2 = AimUtil.m_3(new Rotations(vec3, vec32), blockPos2, floatValue);
                  if (blockHitResult2 != null && blockHitResult2.getDirection() == direction2) {
                     if (direction == Direction.DOWN) {
                        return new BlockHitResult(m_7(blockPos2, direction2), direction2, blockPos2, false);
                     }

                     double doubleValue2 = vec3.distanceToSqr(vec32);
                     if (doubleValue2 < doubleValue) {
                        doubleValue = doubleValue2;
                        blockHitResult = new BlockHitResult(m_7(blockPos2, direction2), direction2, blockPos2, false);
                     }
                  }
               }
            }

            return blockHitResult;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static boolean m_6(BlockState blockState, BlockPos blockPos) {
      if (blockState.is(Blocks.COBWEB)) {
         return true;
      } else {
         return !blockState.isAir() && !blockState.canBeReplaced()
            ? !blockState.getCollisionShape(MinecraftAccess.MINECRAFT.level, blockPos).isEmpty() || blockState.blocksMotion() || blockState.isSolid()
            : false;
      }
   }

   public static Vec3 m_7(BlockPos blockPos, Direction direction) {
      double doubleValue = blockPos.getX();
      double doubleValue2 = blockPos.getY();
      double doubleValue3 = blockPos.getZ();

      return switch (direction) {
         case UP -> new Vec3(doubleValue + 0.5, doubleValue2 + 1.0, doubleValue3 + 0.5);
         case DOWN -> new Vec3(doubleValue + 0.5, doubleValue2 + 0.0, doubleValue3 + 0.5);
         case NORTH -> new Vec3(doubleValue + 0.5, doubleValue2 + 0.5, doubleValue3 + 0.0);
         case SOUTH -> new Vec3(doubleValue + 0.5, doubleValue2 + 0.5, doubleValue3 + 1.0);
         case WEST -> new Vec3(doubleValue + 0.0, doubleValue2 + 0.5, doubleValue3 + 0.5);
         case EAST -> new Vec3(doubleValue + 1.0, doubleValue2 + 0.5, doubleValue3 + 0.5);
      };
   }

   public static Vec3 m_6(BlockPos blockPos, Direction direction) {
      return new Vec3(blockPos.getX() + 0.5 + direction.getStepX() * 0.5001, blockPos.getY() + 0.5 + direction.getStepY() * 0.5001, blockPos.getZ() + 0.5 + direction.getStepZ() * 0.5001);
   }
}
