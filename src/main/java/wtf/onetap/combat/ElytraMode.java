package wtf.onetap.combat;

import net.minecraft.world.phys.Vec3;

public class ElytraMode {
   public static final String TEXT = "Полная";
   public static final String TEXT2 = "Быстрая";
   public static final String TEXT3 = "Граунд Грим";

   private ElytraMode() {
   }

   public static boolean isFast(String text) {
      return "Быстрая".equals(text);
   }

   public static boolean isGroundGrim(String text) {
      return "Граунд Грим".equals(text);
   }

   public static String m_2(String text, boolean flag, boolean flag2, boolean flag3) {
      if (!flag) {
         return "Полная";
      } else if ("Граунд Грим".equals(text)) {
         return "Граунд Грим";
      } else if (!"Быстрая".equals(text)) {
         return "Полная";
      } else {
         return flag2 && !flag3 ? "Полная" : "Быстрая";
      }
   }

   public static Vec3 m_9(Vec3 vec3, Vec3 vec32) {
      return m_3(vec3, vec32, 1.5);
   }

   public static Vec3 m_3(Vec3 vec3, Vec3 vec32, double doubleValue) {
      return vec3 != null && vec32 != null
         ? vec3.add(vec32.x * 0.1 + (vec32.x * doubleValue - vec3.x) * 0.5, vec32.y * 0.1 + (vec32.y * doubleValue - vec3.y) * 0.5, vec32.z * 0.1 + (vec32.z * doubleValue - vec3.z) * 0.5)
         : vec3;
   }

   public static Vec3 m_2(Vec3 vec3, float floatValue, String text) {
      if (vec3 != null && "Быстрая".equals(text)) {
         double doubleValue = Math.hypot(vec3.x, vec3.z);
         if (doubleValue < 1.0E-4) {
            return vec3;
         }

         float floatValue2 = -floatValue * Float.intBitsToFloat((int)-6923245237254030795L);
         return new Vec3(Math.sin(floatValue2) * doubleValue, vec3.y, Math.cos(floatValue2) * doubleValue);
      } else {
         return vec3;
      }
   }
}
