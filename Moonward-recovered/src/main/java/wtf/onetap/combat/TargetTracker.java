package wtf.onetap.combat;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.network.PingHandler;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

public class TargetTracker implements MinecraftAccess {
   private Vec3 vec3;
   private Vec3 vec32;
   private long longValue;
   private long longValue2;
   private float floatValue;
   private double doubleValue;
   private boolean flag;
   private Vec3 vec33;

   public TargetTracker() {
      this.doubleValue = -1.0;
   }

   public void reset() {
      this.vec3 = null;
      this.vec32 = null;
      this.longValue = 0L;
      this.longValue2 = 0L;
      this.floatValue = 0.0F;
      this.doubleValue = -1.0;
      this.flag = false;
      this.vec33 = null;
   }

   public Vec3 updatePosition(LivingEntity livingEntity) {
      this.vec3 = this.m_1_Vec3(livingEntity);
      return this.vec3;
   }

   public void m_5_void() {
      if (MinecraftAccess.MINECRAFT.player == null || !MinecraftAccess.MINECRAFT.player.isFallFlying() || this.vec3 == null) {
         this.doubleValue = -1.0;
         this.flag = false;
         this.vec33 = null;
      } else if (this.vec33 != null && this.vec33.equals(this.vec3)) {
         double doubleValue2 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F).distanceTo(this.vec3);
         if (this.doubleValue >= 0.0 && doubleValue2 > this.doubleValue + 0.001) {
            this.flag = true;
         }

         this.doubleValue = doubleValue2;
      } else {
         this.doubleValue = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F).distanceTo(this.vec3);
         this.flag = false;
         this.vec33 = this.vec3;
      }
   }

   private static Vec3 normalize(Vec3 vec34) {
      double doubleValue2 = vec34.lengthSqr();
      return doubleValue2 < 1.0E-9 ? Vec3.ZERO : vec34.scale(1.0 / Math.sqrt(doubleValue2));
   }

   private static Vec3 closestPoint(AABB aABB, Vec3 vec34) {
      return new Vec3(Mth.clamp(vec34.x, aABB.minX, aABB.maxX), Mth.clamp(vec34.y, aABB.minY, aABB.maxY), Mth.clamp(vec34.z, aABB.minZ, aABB.maxZ));
   }

   private static Vec3 lerp(Vec3 vec34, Vec3 vec35, double doubleValue2) {
      return vec34.scale(1.0 - doubleValue2).add(vec35.scale(doubleValue2));
   }

   private static double lerp(double doubleValue2, double doubleValue3, double doubleValue4) {
      return doubleValue3 + (doubleValue2 - doubleValue3) * doubleValue4;
   }

   private static Vec3 getLookVector(float floatValue2, float floatValue3) {
      return new Vec3(-Mth.sin(floatValue2) * Mth.cos(floatValue3), -Mth.sin(floatValue3), Mth.cos(floatValue2) * Mth.cos(floatValue3));
   }

   private Vec3 m_1_Vec3(LivingEntity livingEntity) {
      InventoryUtil.m_8_StringArr();
      float floatValue2 = 0.0F;
      if (MinecraftAccess.MINECRAFT.player.connection != null) {
         long longValue3 = PingHandler.m_0_long();
         floatValue2 = (float)longValue3 * 0.255F;
      }

      float floatValue3 = MinecraftAccess.MINECRAFT.getDeltaTracker().getGameTimeDeltaPartialTick(true);
      Vec3 vec34 = new Vec3(lerp(livingEntity.getX(), livingEntity.xOld, floatValue3), lerp(livingEntity.getY(), livingEntity.yOld, floatValue3), lerp(livingEntity.getZ(), livingEntity.zOld, floatValue3));
      Vec3 vec35 = livingEntity.getDeltaMovement();
      float floatValue4 = (float)lerp(livingEntity.yRotO, livingEntity.getYRot(), floatValue3);
      float floatValue5 = (float)lerp(livingEntity.xRotO, livingEntity.getXRot(), floatValue3);
      float floatValue6 = Mth.wrapDegrees(livingEntity.getYRot() - livingEntity.yRotO);
      float floatValue7 = livingEntity.getXRot() - livingEntity.xRotO;
      float floatValue8 = Mth.sqrt(floatValue6 * floatValue6 + floatValue7 * floatValue7);
      float floatValue9 = Mth.clamp((float)Math.exp(-floatValue8 / 45.0F), 0.0F, 1.0F);
      Vec3 vec36 = vec34.add(vec35.scale(floatValue2 * floatValue9));
      boolean flag2 = livingEntity.getX() == livingEntity.xOld && livingEntity.getY() == livingEntity.yOld && livingEntity.getZ() == livingEntity.zOld;
      if (flag2) {
         vec36 = vec34;
      }

      float floatValue10 = (float)Math.toRadians(floatValue4);
      float floatValue11 = (float)Math.toRadians(floatValue5);
      Vec3 vec37 = getLookVector(floatValue10, floatValue11);
      Vec3 vec38 = normalize(getLookVector((float)Math.toRadians(floatValue4 + 10.0F), floatValue11).subtract(vec37));
      Vec3 vec39 = normalize(getLookVector(floatValue10, (float)Math.toRadians(floatValue5 + 10.0F)).subtract(vec37));
      double doubleValue2 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F).distanceTo(vec36);
      double doubleValue3 = Math.min(0.05 * doubleValue2, 2.0);
      double doubleValue4 = Math.min(0.04 * doubleValue2, 2.0);
      float floatValue12 = Mth.clamp(1.0F - Math.abs(floatValue7) / 25.0F, -0.5F, 1.0F);
      if (livingEntity.isFallFlying()) {
         vec36 = vec36.subtract(vec37.scale(0.6));
      }

      if (this.flag) {
         long longValue4 = System.currentTimeMillis();
         if (this.longValue2 == 0L) {
            this.longValue2 = longValue4;
         }

         doubleValue3 = Math.min(doubleValue3 * 1.6, 3.0);
         doubleValue4 *= 1.15;
         if (this.vec32 != null && longValue4 <= this.longValue) {
            this.vec32 = this.vec32.add(vec35.scale(Math.max(0.0F, floatValue2) * 0.35));
         } else {
            this.vec32 = vec36;
         }

         this.longValue = longValue4 + 220L;
         vec36 = lerp(vec36, this.vec32, 0.45);
         this.floatValue = this.floatValue + (1.0F - this.floatValue) * 0.2F;
      } else {
         this.longValue2 = 0L;
         this.floatValue = this.floatValue + (0.0F - this.floatValue) * 0.2F;
      }

      Vec3 vec310 = vec36.add(vec37.scale(doubleValue3 * floatValue9)).add(vec38.scale(floatValue6 * doubleValue4 * floatValue9)).add(vec39.scale(floatValue7 * doubleValue4 * floatValue9 * floatValue12));
      if (flag2) {
         vec310 = vec34;
      }

      AABB aABB = livingEntity.getBoundingBox().inflate(0.25, 0.35, 0.25);
      if (!aABB.inflate(0.08).contains(vec310)) {
         Vec3 vec311 = closestPoint(aABB, vec310);
         vec310 = lerp(vec310, vec311, 0.7);
      }

      if (this.flag && this.longValue2 != 0L) {
         double doubleValue5 = (System.currentTimeMillis() - this.longValue2) / 1000.0;
         double doubleValue6 = 7.2;
         double doubleValue7 = 0.06 + Math.min(doubleValue2, 12.0) * 0.005;
         double doubleValue8 = doubleValue7 * Mth.clamp(this.floatValue, 0.0F, 1.0F);
         double doubleValue9 = Math.sin(doubleValue5 * (Math.PI * 2) * doubleValue6) * doubleValue8;
         doubleValue9 = Mth.clamp(doubleValue9, -0.12, 0.12);
         vec310 = vec310.add(0.0, doubleValue9, 0.0);
      }

      return vec310;
   }
}
