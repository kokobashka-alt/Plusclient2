package wtf.onetap.combat;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.mixin.entity.LivingEntityInvoker;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

public class ElytraPrediction implements MinecraftAccess {
   private int value;
   private int value2;
   private Vec3 vec3;
   private Vec3 vec32;
   private int value3;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;

   public ElytraPrediction() {
      this.value = Integer.MIN_VALUE;
   }

   public void m_6_void() {
      this.value = Integer.MIN_VALUE;
      this.vec3 = null;
   }

   public ElytraPrediction.DataRecord m_2(LivingEntity livingEntity, double doubleValue) {
      this.m_9_void(livingEntity);
      int value4 = Math.min(livingEntity.tickCount - this.value3, 10);
      int value5 = value4 > livingEntity.getType().updateInterval() ? value4 : 0;
      boolean flag = AttackCooldownTracker.isRecentlyAttacked(livingEntity.getId());
      Vec3[] vec3Array = this.m_9(livingEntity, value5 + Math.max(40, (int)Math.ceil(doubleValue)) + 1, flag);
      Vec3 vec33 = m_9(vec3Array, doubleValue);
      double doubleValue2 = this.m_5(livingEntity, vec3Array, value5);
      double doubleValue3 = doubleValue2 > 0.0 ? value5 + doubleValue2 : 0.0;
      Vec3 vec34 = doubleValue3 > doubleValue ? m_9(vec3Array, doubleValue3) : vec33;
      Vec3 vec35 = vec34.add(0.0, livingEntity.getBbHeight() * 0.5, 0.0);
      return new ElytraPrediction.DataRecord(vec33, vec35, doubleValue, doubleValue3, value5, flag);
   }

   private void m_9_void(LivingEntity livingEntity) {
      InventoryUtil.m_8_StringArr();
      Vec3 vec33 = EntityPrediction.getInterpolatedPosition(livingEntity);
      float floatValue5 = livingEntity.getYRot();
      float floatValue6 = livingEntity.getXRot();
      if (livingEntity instanceof RemotePlayer remotePlayer) {
         floatValue5 = remotePlayer.getInterpolation().yRot();
         floatValue6 = remotePlayer.getInterpolation().xRot();
      }

      int value4 = livingEntity.tickCount;
      boolean flag = livingEntity.getId() == this.value && this.vec3 != null && value4 - this.value2 <= 1;
      this.value2 = value4;
      int value5 = Math.max(1, value4 - this.value3);
      if (!flag || vec33.distanceToSqr(this.vec3) > 16.0 * value5 * value5) {
         this.value = livingEntity.getId();
         this.vec3 = vec33;
         this.value3 = value4;
         this.vec32 = EntityPrediction.m_2_Vec3(livingEntity);
         this.floatValue = floatValue5;
         this.floatValue2 = floatValue6;
         this.floatValue3 = 0.0F;
         this.floatValue4 = 0.0F;
      } else if (!(vec33.distanceToSqr(this.vec3) < 1.0E-8)) {
         this.vec32 = vec33.subtract(this.vec3).scale(1.0 / value5);
         this.floatValue3 = Mth.clamp(Mth.wrapDegrees(floatValue5 - this.floatValue) / value5, -30.0F, 30.0F);
         this.floatValue4 = Mth.clamp((floatValue6 - this.floatValue2) / value5, -20.0F, 20.0F);
         this.vec3 = vec33;
         this.value3 = value4;
         this.floatValue = floatValue5;
         this.floatValue2 = floatValue6;
      }
   }

   private Vec3[] m_9(LivingEntity livingEntity, int value4, boolean flag) {
      Vec3[] vec3Array = new Vec3[value4 + 1];
      vec3Array[0] = this.vec3;
      Vec3 vec33 = this.vec3;
      Vec3 vec34 = this.vec32;
      boolean flag2 = livingEntity.isFallFlying();
      double doubleValue = ((LivingEntityInvoker)livingEntity).onetap$getEffectiveGravity();
      float floatValue5 = this.floatValue;
      float floatValue6 = this.floatValue2;
      float floatValue7 = 1.0F;

      for (int i = 1; i <= value4; i++) {
         if (flag2) {
            floatValue7 *= 0.85F;
            floatValue5 += this.floatValue3 * floatValue7;
            floatValue6 = Mth.clamp(floatValue6 + this.floatValue4 * floatValue7, -90.0F, 90.0F);
            if (flag) {
               vec34 = ElytraMode.m_9(vec34, livingEntity.calculateViewVector(floatValue6, floatValue5));
            }

            vec34 = EntityPrediction.m_4(livingEntity, vec34, floatValue5, floatValue6, doubleValue);
         }

         vec33 = vec33.add(vec34);
         vec3Array[i] = vec33;
      }

      return vec3Array;
   }

   private double m_5(LivingEntity livingEntity, Vec3[] vec3Array, int value4) {
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      double doubleValue = m_6_double();
      double doubleValue2 = livingEntity.getBbHeight() * 0.5;
      double doubleValue3 = m_6(vec33, vec3Array[value4], doubleValue2, 0, doubleValue);
      if (doubleValue3 <= 0.0) {
         return 0.0;
      }

      int value5 = 0;
      double doubleValue4 = doubleValue3;

      for (int i = 1; i <= 40; i++) {
         double doubleValue5 = m_6(vec33, vec3Array[value4 + i], doubleValue2, i, doubleValue);
         if (doubleValue5 <= 0.0) {
            return i - 1 + doubleValue3 / (doubleValue3 - doubleValue5);
         }

         if (doubleValue5 < doubleValue4) {
            value5 = i;
            doubleValue4 = doubleValue5;
         }

         doubleValue3 = doubleValue5;
      }

      return value5;
   }

   private static double m_6(Vec3 vec33, Vec3 vec34, double doubleValue, int value4, double doubleValue2) {
      return vec33.distanceTo(vec34.add(0.0, doubleValue, 0.0)) - 1.0 - doubleValue2 * value4;
   }

   private static double m_6_double() {
      double doubleValue = MinecraftAccess.MINECRAFT.player.getDeltaMovement().length();
      if (AttackCooldownTracker.m_7_boolean()) {
         doubleValue = Math.max(doubleValue, 1.5);
      }

      return Math.max(doubleValue, 0.5);
   }

   private static Vec3 m_9(Vec3[] vec3Array, double doubleValue) {
      double doubleValue2 = Mth.clamp(doubleValue, 0.0, vec3Array.length - 1);
      int value4 = (int)doubleValue2;
      return value4 >= vec3Array.length - 1 ? vec3Array[vec3Array.length - 1] : vec3Array[value4].lerp(vec3Array[value4 + 1], doubleValue2 - value4);
   }

   public record DataRecord(Vec3 f_1_g, Vec3 f_1_m, double f_1x, double f_1z, int f_3n, boolean f_2_h) {
      public Vec3 m_1_g() {
         return this.f_1_g;
      }

      public Vec3 m_1_m() {
         return this.f_1_m;
      }

      public double m_1x_double() {
         return this.f_1x;
      }

      public double m_1z_double() {
         return this.f_1z;
      }

      public int m_3n() {
         return this.f_3n;
      }

      public boolean m_2_h() {
         return this.f_2_h;
      }
   }
}
