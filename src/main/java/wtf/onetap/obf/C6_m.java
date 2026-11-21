package wtf.onetap.obf;

import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;

public class C6_m {
   private static final Animation f_6 = new Animation(Easing.C02, 150L);

   private C6_m() {
   }

   public static void m_1_void() {
      f_6.reset(0.0F);
   }

   public static float m_9_float() {
      return f_6.update(1.0F);
   }

   public static boolean m_8_boolean() {
      return f_6.isFinished();
   }
}
