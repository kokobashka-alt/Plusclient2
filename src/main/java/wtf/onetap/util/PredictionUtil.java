package wtf.onetap.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.combat.EntityPrediction;

public class PredictionUtil implements MinecraftAccess {
   public static Vec3 m_3(LivingEntity livingEntity, double doubleValue) {
      double doubleValue2 = m_6(livingEntity, doubleValue);
      int value = (int)doubleValue2;
      double doubleValue3 = doubleValue2 - value;
      Vec3 vec3 = predictPosition(livingEntity, value);
      return doubleValue3 <= 0.0 ? vec3 : vec3.lerp(predictPosition(livingEntity, value + 1), doubleValue3);
   }

   public static Vec3 predictPosition(LivingEntity livingEntity, int value) {
      return EntityPrediction.m_6(livingEntity, value);
   }

   public static double m_6(LivingEntity livingEntity, double doubleValue) {
      InventoryUtil.m_8_StringArr();
      double doubleValue2 = Mth.clamp(doubleValue, 1.0, 9.0);
      if (MinecraftAccess.MINECRAFT.player == null) {
         return doubleValue2;
      }

      double doubleValue3 = m_8_double(livingEntity);
      if (doubleValue3 <= 5.0) {
         return doubleValue2;
      }

      double doubleValue4 = (9.0 - doubleValue2) * (1.0 - Math.exp(-(doubleValue3 - 5.0) / 18.0));
      double doubleValue5 = Mth.clamp((m_9_double(livingEntity) - 15.0) / 30.0, 0.0, 1.0) * 1.0;
      return Mth.clamp(doubleValue2 + doubleValue4 + doubleValue5, doubleValue2, 9.0);
   }

   private static double m_9_double(LivingEntity livingEntity) {
      InventoryUtil.m_8_StringArr();
      Vec3 vec3 = livingEntity.getDeltaMovement();
      if (vec3.lengthSqr() < 1.0E-7) {
         vec3 = new Vec3(livingEntity.getX() - livingEntity.xo, livingEntity.getY() - livingEntity.yo, livingEntity.getZ() - livingEntity.zo);
      }

      return vec3.length() * 20.0;
   }

   private static double m_8_double(LivingEntity livingEntity) {
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      AABB aABB = livingEntity.getBoundingBox();
      return vec3.distanceTo(
         new Vec3(Mth.clamp(vec3.x, aABB.minX, aABB.maxX), Mth.clamp(vec3.y, aABB.minY, aABB.maxY), Mth.clamp(vec3.z, aABB.minZ, aABB.maxZ))
      );
   }

   private PredictionUtil() {
   }
}
