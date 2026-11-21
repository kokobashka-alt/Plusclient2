package wtf.onetap.obf;

import net.minecraft.util.ARGB;
import org.joml.Matrix3x2fStack;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;

public class C6_e {
   private static final Animation f_1 = new Animation(280L, 0.0F, Easing.EASING8);
   private static float f_4 = 1.0F;

   private C6_e() {
   }

   public static float m_0_float(boolean flag) {
      return f_1.update(flag ? 1.0F : 0.0F);
   }

   public static void m_8_void(float floatValue) {
      f_4 = floatValue;
   }

   public static void m_0_void() {
      f_4 = 1.0F;
   }

   public static int m_5_int(int value) {
      return ARGB.multiplyAlpha(value, f_4);
   }

   public static void m_8(Matrix3x2fStack matrix3x2fStack, float floatValue, float floatValue2) {
      float floatValue3 = 0.94F + 0.06F * floatValue2;
      float floatValue4 = -18.0F * (1.0F - floatValue2);
      matrix3x2fStack.pushMatrix();
      matrix3x2fStack.translate(floatValue * 0.5F, 0.0F);
      matrix3x2fStack.scale(floatValue3, floatValue3);
      matrix3x2fStack.translate(-floatValue * 0.5F, floatValue4);
   }
}
