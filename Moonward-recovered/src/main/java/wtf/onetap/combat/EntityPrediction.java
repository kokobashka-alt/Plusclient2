package wtf.onetap.combat;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.mixin.entity.LivingEntityInvoker;
import wtf.onetap.mixin.entity.RemotePlayerAccessor;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;

public class EntityPrediction implements MinecraftAccess {
   public static Vec3 m_5_Vec3() {
      return m_1_Vec3(1);
   }

   public static Vec3 m_1_Vec3(int value) {
      return PlayerUtil.predictLocal(value).vec3;
   }

   public static Vec3 m_2(Player player, int value) {
      return PlayerUtil.predict(player, value).vec3;
   }

   public static Vec3 getInterpolatedPosition(LivingEntity livingEntity) {
      return livingEntity instanceof RemotePlayer remotePlayer ? remotePlayer.getInterpolation().position() : livingEntity.position();
   }

   public static Vec3 m_2_Vec3(LivingEntity livingEntity) {
      return livingEntity instanceof RemotePlayer remotePlayer ? ((RemotePlayerAccessor)remotePlayer).onetap$getLerpDeltaMovement() : livingEntity.getDeltaMovement();
   }

   public static Vec3 m_6(LivingEntity livingEntity, int value) {
      return m_4(livingEntity, value, m_2_Vec3(livingEntity));
   }

   public static Vec3 m_4(LivingEntity livingEntity, int value, Vec3 vec3) {
      Vec3 vec32 = getInterpolatedPosition(livingEntity);
      float floatValue = livingEntity.getYRot();
      float floatValue2 = livingEntity.getXRot();
      if (livingEntity instanceof RemotePlayer remotePlayer) {
         InterpolationHandler interpolationHandler = remotePlayer.getInterpolation();
         floatValue = interpolationHandler.yRot();
         floatValue2 = interpolationHandler.xRot();
      }

      if (value > 0 && livingEntity.isFallFlying()) {
         double doubleValue = ((LivingEntityInvoker)livingEntity).onetap$getEffectiveGravity();

         for (int i = 0; i < value; i++) {
            vec3 = m_4(livingEntity, vec3, floatValue, floatValue2, doubleValue);
            vec32 = vec32.add(vec3);
         }

         return vec32;
      } else {
         return vec32;
      }
   }

   public static Vec3 m_4(LivingEntity livingEntity, Vec3 vec3, float floatValue, float floatValue2, double doubleValue) {
      Vec3 vec32 = livingEntity.calculateViewVector(floatValue2, floatValue);
      InventoryUtil.m_8_StringArr();
      float floatValue3 = floatValue2 * Float.intBitsToFloat((int)7954430323999046197L);
      double doubleValue2 = Math.sqrt(vec32.x * vec32.x + vec32.z * vec32.z);
      double doubleValue3 = vec3.horizontalDistance();
      double doubleValue4 = Math.cos(floatValue3);
      doubleValue4 *= doubleValue4;
      vec3 = vec3.add(0.0, doubleValue * (-1.0 + doubleValue4 * 0.75), 0.0);
      if (vec3.y < 0.0 && doubleValue2 > 0.0) {
         double doubleValue5 = vec3.y * -0.1 * doubleValue4;
         vec3 = vec3.add(vec32.x * doubleValue5 / doubleValue2, doubleValue5, vec32.z * doubleValue5 / doubleValue2);
      }

      if (floatValue3 < 0.0F && doubleValue2 > 0.0) {
         double doubleValue6 = doubleValue3 * -Math.sin(floatValue3) * 0.04;
         vec3 = vec3.add(-vec32.x * doubleValue6 / doubleValue2, doubleValue6 * 3.2, -vec32.z * doubleValue6 / doubleValue2);
      }

      if (doubleValue2 > 0.0) {
         double doubleValue7 = vec32.x / doubleValue2 * doubleValue3;
         double doubleValue8 = vec32.z / doubleValue2 * doubleValue3;
         vec3 = vec3.add((doubleValue7 - vec3.x) * 0.1, 0.0, (doubleValue8 - vec3.z) * 0.1);
      }

      return vec3.multiply(0.99, 0.98, 0.99);
   }

   public static Vec3 m_2_Vec3(LivingEntity livingEntity, float floatValue) {
      if (livingEntity instanceof Player player) {
         return PlayerUtil.predict(player, Math.max(1, Math.round(floatValue))).aABB.getCenter();
      } else {
         Vec3 vec3 = new Vec3(livingEntity.getX() - livingEntity.xo, 0.0, livingEntity.getZ() - livingEntity.zo);
         return livingEntity.getBoundingBox().getCenter().add(vec3.scale(Math.max(0.0F, floatValue)));
      }
   }

   private EntityPrediction() {
   }
}
