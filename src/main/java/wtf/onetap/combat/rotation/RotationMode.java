package wtf.onetap.combat.rotation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.RotationInfo;
import wtf.onetap.util.Rotations;

public interface RotationMode {
   Rotations m_5(float floatValue, float floatValue2, Rotations rotations, AABB aABB, boolean flag, float floatValue3, float floatValue4);

   default Rotations m_6(float floatValue, float floatValue2, Rotations rotations, AABB aABB, boolean flag, float floatValue3, float floatValue4, LivingEntity livingEntity) {
      return this.m_5(floatValue, floatValue2, rotations, aABB, flag, floatValue3, floatValue4);
   }

   default Rotations m_7(float floatValue, float floatValue2, Rotations rotations, AABB aABB, boolean flag, float floatValue3, float floatValue4, LivingEntity livingEntity, Vec3 vec3) {
      return this.m_6(floatValue, floatValue2, rotations, aABB, flag, floatValue3, floatValue4, livingEntity);
   }

   default Rotations m_9(float floatValue, float floatValue2, Rotations rotations, AABB aABB, boolean flag, float floatValue3, float floatValue4, LivingEntity livingEntity, Vec3 vec3, boolean flag2) {
      return this.m_7(floatValue, floatValue2, rotations, aABB, flag, floatValue3, floatValue4, livingEntity, vec3);
   }

   default Rotations m_8(float floatValue, float floatValue2, Rotations rotations, float floatValue3) {
      return null;
   }

   default Rotations m_8_C3b() {
      return null;
   }

   default boolean isCloseTo(float floatValue, float floatValue2, Rotations rotations) {
      return Math.hypot(Mth.wrapDegrees(rotations.getFloatValue() - floatValue), rotations.getFloatValue2() - floatValue2) < 3.0;
   }

   default Vec3 m_1_Vec3() {
      return Vec3.ZERO;
   }

   default void m_8_void() {
   }

   default RotationInfo getInfo() {
      return this.getClass().getAnnotation(RotationInfo.class);
   }

   default int m_6_int() {
      return this.getInfo().fps();
   }

   default void m_1_void() {
   }

   default boolean m_6_boolean() {
      return false;
   }

   default boolean m_2_boolean() {
      return false;
   }
}
