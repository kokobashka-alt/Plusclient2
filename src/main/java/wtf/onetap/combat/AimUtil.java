package wtf.onetap.combat;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

public class AimUtil implements MinecraftAccess {
   private static boolean flag;

   public static boolean isLookingAtEntity(Entity entity, Rotations rotations, double doubleValue) {
      return isLookingAt(rotations, doubleValue, entity.getBoundingBox());
   }

   public static boolean isLookingAtBox(AABB aABB, Rotations rotations, double doubleValue) {
      return isLookingAt(rotations, doubleValue, aABB);
   }

   public static boolean isLookingAt(Rotations rotations, double doubleValue, AABB aABB) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return false;
      }

      float floatValue = rotations != null ? rotations.getFloatValue() : MinecraftAccess.MINECRAFT.player.getYRot();
      float floatValue2 = rotations != null ? rotations.getFloatValue2() : MinecraftAccess.MINECRAFT.player.getXRot();
      float floatValue3 = floatValue2 * (float) (Math.PI / 180.0);
      float floatValue4 = -floatValue * (float) (Math.PI / 180.0);
      float floatValue5 = Mth.cos(floatValue4);
      float floatValue6 = Mth.sin(floatValue4);
      float floatValue7 = Mth.cos(floatValue3);
      float floatValue8 = Mth.sin(floatValue3);
      return isRayHit(new Vec3(floatValue6 * floatValue7, -floatValue8, floatValue5 * floatValue7), doubleValue, aABB);
   }

   public static boolean isRayHit(Vec3 vec3, double doubleValue, AABB aABB) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return false;
      }

      Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      return aABB.contains(vec32) || aABB.clip(vec32, vec32.add(vec3.scale(doubleValue))).isPresent();
   }

   public static Rotations m_9(Rotations rotations, AABB aABB, double doubleValue) {
      ChatUtil.m_3_IArr();
      AABB aABB2 = aABB.deflate(0.1);
      if (isLookingAt(rotations, doubleValue, aABB2)) {
         return rotations;
      }

      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      Rotations rotations2 = new Rotations(vec3, closestPoint(vec3, aABB2));
      double doubleValue2 = rotations2.m_7_double(rotations);

      for (int i = 0; i <= 4; i++) {
         for (int i2 = 0; i2 <= 4; i2++) {
            for (int i3 = 0; i3 <= 4; i3++) {
               Rotations rotations3 = new Rotations(
                  vec3,
                  new Vec3(
                     Mth.lerp(i / 4.0, aABB2.minX, aABB2.maxX), Mth.lerp(i2 / 4.0, aABB2.minY, aABB2.maxY), Mth.lerp(i3 / 4.0, aABB2.minZ, aABB2.maxZ)
                  )
               );
               double doubleValue3 = rotations3.m_7_double(rotations);
               if (doubleValue3 < doubleValue2 && isLookingAt(rotations3, doubleValue, aABB2)) {
                  rotations2 = rotations3;
                  doubleValue2 = doubleValue3;
               }
            }
         }
      }

      return rotations2;
   }

   public static BlockHitResult m_3(Player player, double doubleValue) {
      if (player != null && MinecraftAccess.MINECRAFT.level != null) {
         Vec3 vec3 = player.getEyePosition(1.0F);
         Vec3 vec32 = vec3.add(player.getLookAngle().scale(doubleValue));
         BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec3, vec32, Block.OUTLINE, Fluid.NONE, player));
         return blockHitResult.getType() == Type.BLOCK ? blockHitResult : null;
      } else {
         return null;
      }
   }

   public static BlockHitResult m_5(BlockPos blockPos, double doubleValue) {
      return MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null ? m_5(MinecraftAccess.MINECRAFT.player, blockPos, doubleValue) : null;
   }

   public static BlockHitResult m_3(Rotations rotations, BlockPos blockPos, double doubleValue) {
      return MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null ? m_6(rotations, MinecraftAccess.MINECRAFT.player, blockPos, doubleValue) : null;
   }

   public static BlockHitResult m_5(Player player, BlockPos blockPos, double doubleValue) {
      Vec3 vec3 = player.getEyePosition();
      Vec3 vec32 = player.getLookAngle();
      Vec3 vec33 = vec3.add(vec32.scale(doubleValue));
      AABB aABB = new AABB(blockPos);
      var optional = aABB.clip(vec3, vec33);
      if (optional.isEmpty()) {
         return null;
      }

      Vec3 vec34 = (Vec3)optional.get();
      Direction direction = m_0(vec34, blockPos);
      return new BlockHitResult(vec34, direction, blockPos, false);
   }

   public static BlockHitResult m_6(Rotations rotations, Player player, BlockPos blockPos, double doubleValue) {
      Vec3 vec3 = player.getEyePosition();
      Vec3 vec32 = Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue());
      Vec3 vec33 = vec3.add(vec32.scale(doubleValue));
      AABB aABB = new AABB(blockPos);
      var optional = aABB.clip(vec3, vec33);
      if (optional.isEmpty()) {
         return null;
      }

      Vec3 vec34 = (Vec3)optional.get();
      Direction direction = m_0(vec34, blockPos);
      return new BlockHitResult(vec34, direction, blockPos, false);
   }

   public static boolean m_9(AABB aABB, double doubleValue) {
      return MinecraftAccess.MINECRAFT.player == null ? false : m_5(MinecraftAccess.MINECRAFT.player.getEyePosition(), aABB, doubleValue);
   }

   public static boolean m_5(Vec3 vec3, AABB aABB, double doubleValue) {
      Vec3 vec32 = closestPoint(vec3, aABB);
      return vec3.distanceToSqr(vec32) <= doubleValue * doubleValue;
   }

   public static boolean m_7(Vec3 vec3, AABB aABB, double doubleValue) {
      return aABB.distanceToSqr(vec3) < doubleValue * doubleValue;
   }

   public static AABB getBlockBox(BlockPos blockPos) {
      double doubleValue = blockPos.getX() + 0.5;
      double doubleValue2 = blockPos.getY() + 1.0;
      double doubleValue3 = blockPos.getZ() + 0.5;
      return new AABB(doubleValue - 1.0, doubleValue2, doubleValue3 - 1.0, doubleValue + 1.0, doubleValue2 + 2.0, doubleValue3 + 1.0);
   }

   public static boolean isInRange(Vec3 vec3, BlockPos blockPos, double doubleValue) {
      return m_7(vec3, getBlockBox(blockPos), doubleValue);
   }

   public static boolean m_7(Entity entity, double doubleValue) {
      if (MinecraftAccess.MINECRAFT.player != null && entity != null) {
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getLookAngle();
         Vec3 vec33 = vec3.add(vec32.scale(doubleValue));
         AABB aABB = entity.getBoundingBox();
         return aABB.clip(vec3, vec33).isPresent() || aABB.contains(vec3);
      } else {
         return false;
      }
   }

   public static boolean m_7(Rotations rotations, Entity entity, double doubleValue) {
      if (MinecraftAccess.MINECRAFT.player != null && entity != null) {
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         Vec3 vec32 = Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue());
         Vec3 vec33 = vec3.add(vec32.scale(doubleValue));
         AABB aABB = entity.getBoundingBox();
         return aABB.clip(vec3, vec33).isPresent() || aABB.contains(vec3);
      } else {
         return false;
      }
   }

   public static boolean m_5(Rotations rotations, double doubleValue, AABB aABB, Entity entity) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         Vec3 vec32 = vec3.add(Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue()).scale(doubleValue));
         double doubleValue2 = aABB.contains(vec3) ? 0.0 : aABB.clip(vec3, vec32).map(vec3::distanceToSqr).orElse(Double.MAX_VALUE);
         EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
            MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, vec3, vec32, new AABB(vec3, vec32).inflate(1.0), entity2 -> entity2 != entity && entity2.isPickable(), 0.0F
         );
         return entityHitResult != null && vec3.distanceToSqr(entityHitResult.getLocation()) < doubleValue2;
      } else {
         return false;
      }
   }

   public static boolean m_6(Rotations rotations, double doubleValue, AABB aABB) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         if (aABB.contains(vec3)) {
            return false;
         }

         Vec3 vec32 = vec3.add(Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue()).scale(doubleValue));
         return aABB.clip(vec3, vec32)
            .map(vec33 -> MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec3, vec33, Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player)).getType() == Type.BLOCK)
            .orElse(false);
      } else {
         return false;
      }
   }

   public static Vec3 closestPoint(Vec3 vec3, AABB aABB) {
      double doubleValue = Math.max(aABB.minX, Math.min(aABB.maxX, vec3.x));
      double doubleValue2 = Math.max(aABB.minY, Math.min(aABB.maxY, vec3.y));
      double doubleValue3 = Math.max(aABB.minZ, Math.min(aABB.maxZ, vec3.z));
      return new Vec3(doubleValue, doubleValue2, doubleValue3);
   }

   private static Direction m_0(Vec3 vec3, BlockPos blockPos) {
      double doubleValue = blockPos.getX();
      double doubleValue2 = blockPos.getY();
      double doubleValue3 = blockPos.getZ();
      double doubleValue4 = Math.abs(vec3.x - doubleValue);
      double doubleValue5 = Math.abs(vec3.x - (doubleValue + 1.0));
      double doubleValue6 = Math.abs(vec3.y - doubleValue2);
      double doubleValue7 = Math.abs(vec3.y - (doubleValue2 + 1.0));
      double doubleValue8 = Math.abs(vec3.z - doubleValue3);
      double doubleValue9 = Math.abs(vec3.z - (doubleValue3 + 1.0));
      double doubleValue10 = Math.min(Math.min(Math.min(doubleValue4, doubleValue5), Math.min(doubleValue6, doubleValue7)), Math.min(doubleValue8, doubleValue9));
      if (doubleValue10 == doubleValue7) {
         return Direction.UP;
      } else if (doubleValue10 == doubleValue6) {
         return Direction.DOWN;
      } else if (doubleValue10 == doubleValue8) {
         return Direction.NORTH;
      } else if (doubleValue10 == doubleValue9) {
         return Direction.SOUTH;
      } else {
         return doubleValue10 == doubleValue4 ? Direction.WEST : Direction.EAST;
      }
   }

   public static void m_0_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_8_boolean() {
      return flag;
   }

   public static boolean m_6_boolean() {
      return true;
   }

   static {
      m_0_void(false);
   }
}
