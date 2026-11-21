package wtf.onetap.util;

import net.minecraft.util.Mth;

public class AngleUtil {
   private AngleUtil() {
   }

   public static float clamp(float floatValue, float floatValue2, float floatValue3) {
      return Mth.clamp(floatValue, floatValue2, floatValue3);
   }

   public static float m_1_float(float floatValue) {
      return Mth.wrapDegrees(floatValue);
   }

   public static float wrappedDelta(float floatValue, float floatValue2) {
      return Mth.wrapDegrees(floatValue2 - floatValue);
   }

   public static float m_1_float(float floatValue, float floatValue2, float floatValue3) {
      return Mth.lerp(floatValue, floatValue2, floatValue3);
   }

   public static float m_4_float(float floatValue, float floatValue2, float floatValue3) {
      float floatValue4 = Mth.clamp(floatValue3, 0.0F, 1.0F);
      ChatUtil.m_3_IArr();
      float floatValue5 = Mth.wrapDegrees(floatValue2 - floatValue);
      if (Math.abs(floatValue5) < 0.5F) {
         return floatValue2;
      }

      float floatValue6 = floatValue + floatValue5 * floatValue4;
      return Math.abs(Mth.wrapDegrees(floatValue2 - floatValue6)) < 0.5F ? floatValue2 : floatValue6;
   }

   public static float approach(float floatValue, float floatValue2, float floatValue3) {
      return floatValue < floatValue2 ? clamp(floatValue + floatValue3, floatValue, floatValue2) : clamp(floatValue - floatValue3, floatValue2, floatValue);
   }

   public static float approachDegrees(float floatValue, float floatValue2, float floatValue3) {
      return Mth.approachDegrees(floatValue, floatValue2, floatValue3);
   }

   public static float m_9_float(float floatValue, float floatValue2) {
      return (float)Math.hypot(floatValue, floatValue2);
   }
}
