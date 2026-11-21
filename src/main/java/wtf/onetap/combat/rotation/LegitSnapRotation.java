package wtf.onetap.combat.rotation;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@RotationInfo(name = "LegitSnap", fps = 240)
public class LegitSnapRotation implements MinecraftAccess, RotationMode {
   private int value;
   private boolean flag;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10;
   private float floatValue11;
   private float floatValue12;
   private float floatValue13;
   private float floatValue14;
   private float floatValue15;
   private long longValue;
   private long longValue2;

   public LegitSnapRotation() {
      this.floatValue10 = 0.5F;
      this.floatValue11 = 0.6F;
      this.floatValue12 = 0.5F;
      this.floatValue15 = 0.26F;
   }

   @Override
   public Rotations m_5(float floatValue16, float floatValue17, Rotations rotations, AABB aABB, boolean flag2, float floatValue18, float floatValue19) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return new Rotations(floatValue16, floatValue17);
      }

      float floatValue20 = floatValue19 <= 0.0F ? 0.05F : Math.min(floatValue19, 0.1F);
      boolean flag3 = Onetap.ROTATION_MODES.isFlag4();
      float floatValue21 = flag3 ? MinecraftAccess.MINECRAFT.player.getYRot() : Onetap.ROTATION_MODES.getRenderYaw();
      float floatValue22 = flag3 ? MinecraftAccess.MINECRAFT.player.getXRot() : Onetap.ROTATION_MODES.getRenderPitch();
      if (!this.flag || this.value != 0 && Math.abs(Mth.wrapDegrees(this.floatValue - floatValue16)) > 4.0F) {
         this.flag = true;
         this.value = 0;
      }

      if (this.value == 0) {
         this.floatValue = floatValue21;
         this.floatValue2 = floatValue22;
      }

      if (aABB == null) {
         return new Rotations(this.floatValue, this.floatValue2);
      }

      switch (this.value) {
         case 0:
            long longValue3 = System.currentTimeMillis();
            boolean flag4 = floatValue18 >= this.floatValue15 && longValue3 - this.longValue > 160L && longValue3 >= this.longValue2;
            if (flag4 && !m_6(floatValue21, floatValue22, aABB, 0.08, 0.12)) {
               this.floatValue3 = floatValue21;
               this.floatValue4 = floatValue22;
               this.m_8_void(1.0F);
               this.m_5(aABB, true);
            }
            break;
         case 1:
            this.m_4(floatValue20, aABB);
            break;
         case 2:
            this.floatValue13 -= floatValue20;
            if (this.floatValue13 <= 0.0F) {
               this.m_5(aABB, false);
            }
            break;
         case 3:
            this.m_8(floatValue20, aABB);
            break;
         case 4:
            this.floatValue13 -= floatValue20;
            if (this.floatValue13 <= 0.0F) {
               this.m_4_void(flag3 ? this.floatValue3 : floatValue21, flag3 ? this.floatValue4 : floatValue22);
            }
            break;
         case 5:
            this.m_9_void(floatValue20, flag3 ? this.floatValue3 : floatValue21, flag3 ? this.floatValue4 : floatValue22);
            break;
         default:
            this.value = 0;
      }

      return new Rotations(this.floatValue, this.floatValue2);
   }

   @Override
   public boolean m_2_boolean() {
      return this.value == 0 && Onetap.ROTATION_MODES.isFlag4();
   }

   @Override
   public void m_8_void() {
      if (this.value != 0 && this.value != 4 && this.value != 5) {
         this.longValue = System.currentTimeMillis();
         this.value = 4;
         this.floatValue13 = ThreadLocalRandom.current().nextFloat(0.03F, 0.085F);
      }
   }

   @Override
   public void m_1_void() {
      this.flag = false;
      this.value = 0;
      this.longValue2 = 0L;
      this.longValue = 0L;
      this.floatValue15 = ThreadLocalRandom.current().nextFloat(0.6F, 0.72F);
   }

   private static boolean m_6(float floatValue16, float floatValue17, AABB aABB, double doubleValue, double doubleValue2) {
      AABB aABB2 = aABB.inflate(-doubleValue, -doubleValue2, -doubleValue);
      return AimUtil.isLookingAt(new Rotations(floatValue16, floatValue17), 12.0, aABB2);
   }

   private void m_8_void(float floatValue16) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      float floatValue17 = threadLocalRandom.nextFloat();
      float floatValue18;
      if (floatValue17 < 0.55F) {
         floatValue18 = threadLocalRandom.nextFloat(0.45F, 0.8F);
      } else if (floatValue17 < 0.8F) {
         floatValue18 = threadLocalRandom.nextFloat(0.8F, 0.95F);
      } else {
         floatValue18 = threadLocalRandom.nextFloat(0.2F, 0.45F);
      }

      float floatValue19 = threadLocalRandom.nextFloat(0.12F, 0.88F);
      float floatValue20 = threadLocalRandom.nextFloat(0.12F, 0.88F);
      this.floatValue10 = 0.5F + (floatValue19 - 0.5F) * floatValue16;
      this.floatValue11 = 0.6F + (floatValue18 - 0.6F) * floatValue16;
      this.floatValue12 = 0.5F + (floatValue20 - 0.5F) * floatValue16;
   }

   private Rotations m_7_C3b(AABB aABB) {
      Vec3 vec3 = new Vec3(aABB.minX + aABB.getXsize() * this.floatValue10, aABB.minY + aABB.getYsize() * this.floatValue11, aABB.minZ + aABB.getZsize() * this.floatValue12);
      return new Rotations(MinecraftAccess.MINECRAFT.player.getEyePosition(), vec3);
   }

   private void m_5(AABB aABB, boolean flag2) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      Rotations rotations = this.m_7_C3b(aABB);
      float floatValue16 = (float)Math.hypot(Mth.wrapDegrees(rotations.getFloatValue() - this.floatValue), rotations.getFloatValue2() - this.floatValue2);
      this.floatValue5 = this.floatValue;
      this.floatValue6 = this.floatValue2;
      this.floatValue8 = 0.0F;
      this.floatValue14 = 0.0F;
      if (flag2) {
         this.floatValue7 = floatValue16 > 25.0F ? threadLocalRandom.nextFloat(0.93F, 1.06F) : 1.0F;
         this.floatValue9 = randomSpeed(floatValue16);
      } else {
         this.floatValue7 = 1.0F;
         this.floatValue9 = 0.02F + 9.0E-4F * floatValue16;
      }

      this.value = 1;
   }

   private void m_4(float floatValue16, AABB aABB) {
      this.floatValue8 += floatValue16;
      Rotations rotations = this.m_7_C3b(aABB);
      float floatValue17 = Mth.clamp(this.floatValue8 / this.floatValue9, 0.0F, 1.0F);
      float floatValue18 = Mth.clamp((this.floatValue8 - 0.006F) / this.floatValue9, 0.0F, 1.0F);
      this.floatValue = this.floatValue5 + Mth.wrapDegrees(rotations.getFloatValue() - this.floatValue5) * this.floatValue7 * smootherStep(floatValue17);
      this.floatValue2 = Mth.clamp(this.floatValue6 + (rotations.getFloatValue2() - this.floatValue6) * this.floatValue7 * smootherStep(floatValue18), -90.0F, 90.0F);
      if (!(floatValue17 < 1.0F)) {
         if (this.floatValue7 != 1.0F) {
            this.value = 2;
            this.floatValue13 = ThreadLocalRandom.current().nextFloat(0.02F, 0.045F);
         } else {
            this.value = 3;
            this.floatValue14 = 0.0F;
         }
      }
   }

   private void m_8(float floatValue16, AABB aABB) {
      this.floatValue14 += floatValue16;
      if (this.floatValue14 > 0.5F) {
         this.longValue2 = System.currentTimeMillis() + 900L;
         this.value = 4;
         this.floatValue13 = ThreadLocalRandom.current().nextFloat(0.03F, 0.085F);
      } else {
         if (!m_6(this.floatValue, this.floatValue2, aABB, 0.04, 0.06)) {
            this.m_8_void(0.5F);
            this.m_5(aABB, false);
         }
      }
   }

   private void m_4_void(float floatValue16, float floatValue17) {
      float floatValue18 = (float)Math.hypot(Mth.wrapDegrees(floatValue16 - this.floatValue), floatValue17 - this.floatValue2);
      this.floatValue5 = this.floatValue;
      this.floatValue6 = this.floatValue2;
      this.floatValue8 = 0.0F;
      this.floatValue9 = randomSpeed(floatValue18) * ThreadLocalRandom.current().nextFloat(1.0F, 1.2F);
      this.value = 5;
   }

   private void m_9_void(float floatValue16, float floatValue17, float floatValue18) {
      this.floatValue8 += floatValue16;
      float floatValue19 = Mth.clamp(this.floatValue8 / this.floatValue9, 0.0F, 1.0F);
      float floatValue20 = Mth.clamp((this.floatValue8 - 0.006F) / this.floatValue9, 0.0F, 1.0F);
      this.floatValue = this.floatValue5 + Mth.wrapDegrees(floatValue17 - this.floatValue5) * smootherStep(floatValue19);
      this.floatValue2 = Mth.clamp(this.floatValue6 + (floatValue18 - this.floatValue6) * smootherStep(floatValue20), -90.0F, 90.0F);
      if (floatValue19 >= 1.0F) {
         this.value = 0;
      }
   }

   private static float randomSpeed(float floatValue16) {
      float floatValue17 = 0.028F + 9.0E-4F * floatValue16;
      return Mth.clamp(floatValue17 * ThreadLocalRandom.current().nextFloat(0.9F, 1.15F), 0.032F, 0.13F);
   }

   private static float smootherStep(float floatValue16) {
      return floatValue16 * floatValue16 * floatValue16 * (10.0F + floatValue16 * (-15.0F + floatValue16 * 6.0F));
   }
}
