package wtf.onetap.combat.rotation;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.util.C9_q;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@RotationInfo(name = "Shakal")
public class ShakalRotation implements MinecraftAccess, RotationMode {
   private int value;
   private int value2;
   private float floatValue;
   private AABB aABB;
   private AABB aABB2;
   private int value3;
   private int value4;

   @Override
   public Rotations m_5(float floatValue2, float floatValue3, Rotations rotations, AABB aABB3, boolean flag, float floatValue4, float floatValue5) {
      return this.m_6(floatValue2, floatValue3, rotations, aABB3, flag, floatValue4, floatValue5, null);
   }

   @Override
   public Rotations m_6(float floatValue2, float floatValue3, Rotations rotations, AABB aABB3, boolean flag, float floatValue4, float floatValue5, LivingEntity livingEntity) {
      if (aABB3 == null) {
         return null;
      }

      int value5 = livingEntity == null ? 0 : livingEntity.getId();
      AABB aABB4 = this.m_3(aABB3, value5);
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
      Vec3 vec32 = aABB4.getCenter().subtract(vec3);
      Vec3 vec33 = aABB3.getCenter().subtract(vec3);
      float floatValue6 = MinecraftAccess.MINECRAFT.options.sensitivity().get().floatValue();
      if (this.value2 == Integer.MIN_VALUE || value5 != this.value || MinecraftAccess.MINECRAFT.player.tickCount != this.value2 + 1 || floatValue6 != this.floatValue) {
         C9_q.m_3_boolean(10);
      }

      this.value = value5;
      this.value2 = MinecraftAccess.MINECRAFT.player.tickCount;
      this.floatValue = floatValue6;
      double[] doubleArray = new double[]{
         floatValue2,
         floatValue3,
         vec32.x,
         vec32.y,
         vec32.z,
         aABB4.getXsize(),
         aABB4.getYsize(),
         aABB4.getZsize(),
         vec33.x,
         vec33.y,
         vec33.z,
         aABB3.getXsize(),
         aABB3.getYsize(),
         aABB3.getZsize(),
         MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.5F),
         getSensitivityFactor(floatValue6),
         0.0,
         0.0,
         0.0,
         0.0
      };
      Vec3 vec34 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
      doubleArray[16] = vec34.x;
      doubleArray[17] = vec34.y;
      doubleArray[18] = vec34.z;
      doubleArray[19] = 1.0;
      return !C9_q.m_7(10, doubleArray) ? new Rotations(floatValue2, floatValue3) : this.m_0(floatValue2, floatValue3, (int)doubleArray[0], (int)doubleArray[1]);
   }

   private AABB m_3(AABB aABB3, int value5) {
      int value6 = MinecraftAccess.MINECRAFT.player.tickCount;
      if (value6 != this.value4 || value5 != this.value3) {
         this.aABB2 = value6 == this.value4 + 1 && value5 == this.value3 ? this.aABB : null;
         this.aABB = aABB3;
         this.value4 = value6;
         this.value3 = value5;
      }

      return this.aABB2 != null ? this.aABB2 : aABB3;
   }

   @Override
   public Rotations m_8(float floatValue2, float floatValue3, Rotations rotations, float floatValue4) {
      Vec3 vec3 = m_7_Vec3(rotations);
      C9_q.m_3_boolean(10);
      this.value = Integer.MIN_VALUE;
      double[] doubleArray = new double[]{
         floatValue2,
         floatValue3,
         vec3.x,
         vec3.y,
         vec3.z,
         0.6,
         1.8,
         0.6,
         vec3.x,
         vec3.y,
         vec3.z,
         0.6,
         1.8,
         0.6,
         1.0,
         getSensitivityFactor(MinecraftAccess.MINECRAFT.options.sensitivity().get().floatValue()),
         0.0,
         0.0,
         0.0,
         0.0
      };
      Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
      doubleArray[16] = vec32.x;
      doubleArray[17] = vec32.y;
      doubleArray[18] = vec32.z;
      doubleArray[19] = 0.0;
      return !C9_q.m_7(10, doubleArray) ? new Rotations(floatValue2, floatValue3) : this.m_0(floatValue2, floatValue3, (int)doubleArray[0], (int)doubleArray[1]);
   }

   @Override
   public boolean isCloseTo(float floatValue2, float floatValue3, Rotations rotations) {
      AABB aABB3 = AABB.ofSize(MinecraftAccess.MINECRAFT.player.getEyePosition().add(m_7_Vec3(rotations)), 0.6, 1.8, 0.6);
      return AimUtil.isLookingAt(new Rotations(floatValue2, floatValue3), 7.0, aABB3);
   }

   private static Vec3 m_7_Vec3(Rotations rotations) {
      double doubleValue = Math.toRadians(rotations.getFloatValue());
      double doubleValue2 = Math.toRadians(rotations.getFloatValue2());
      return new Vec3(-Math.sin(doubleValue) * Math.cos(doubleValue2), -Math.sin(doubleValue2), Math.cos(doubleValue) * Math.cos(doubleValue2)).scale(3.5);
   }

   @Override
   public void m_1_void() {
      C9_q.m_3_boolean(10);
      this.value = Integer.MIN_VALUE;
      this.value2 = Integer.MIN_VALUE;
      this.aABB = null;
      this.aABB2 = null;
   }

   @Override
   public boolean m_6_boolean() {
      return true;
   }

   private Rotations m_0(float floatValue2, float floatValue3, int value5, int value6) {
      double doubleValue = MinecraftAccess.MINECRAFT.options.sensitivity().get() * 0.6F + 0.2F;
      double doubleValue2 = doubleValue * doubleValue * doubleValue * 8.0;
      int value7 = Math.clamp(Math.round(MinecraftAccess.MINECRAFT.getFps() / 20.0F), 1, 64);
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      int[] intArray = m_3(value5, value7, threadLocalRandom);
      int[] intArray2 = m_3(value6, value7, threadLocalRandom);

      for (int i = 0; i < value7; i++) {
         floatValue2 += (float)(intArray[i] * doubleValue2) * 0.15F;
         floatValue3 = Mth.clamp(floatValue3 + (float)(intArray2[i] * doubleValue2) * 0.15F, -90.0F, 90.0F);
      }

      return new Rotations(floatValue2, floatValue3);
   }

   private static int[] m_3(int value5, int value6, ThreadLocalRandom threadLocalRandom) {
      int[] intArray = new int[value6];
      int value7 = Integer.signum(value5);

      for (int i = Math.abs(value5); i > 0; i--) {
         intArray[threadLocalRandom.nextInt(value6)] += value7;
      }

      return intArray;
   }

   private static float getSensitivityFactor(float floatValue2) {
      float floatValue3 = floatValue2 * 0.6F + 0.2F;
      return floatValue3 * floatValue3 * floatValue3 * 1.2F;
   }
}
