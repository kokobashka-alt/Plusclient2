package wtf.onetap.render;

import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.util.ChatUtil;

public class ColorUtil {
   private static final Animation ANIMATION = new Animation(400L, 1.0F, Easing.EASING8);
   private static int value;
   private static int value2;
   private static int value3;
   private static int value4;
   private static boolean flag;
   private static int value5;

   private ColorUtil() {
   }

   private static void m_1_void() {
      if (!flag) {
         value2 = Onetap.THEME_MANAGER.m_9_int();
         value4 = Onetap.THEME_MANAGER.m_8_int();
         value = value2;
         value3 = value4;
         flag = true;
      }
   }

   public static void m_9_void() {
      m_1_void();
      value = primaryColor();
      value3 = secondaryColor();
      value2 = Onetap.THEME_MANAGER.m_9_int();
      value4 = Onetap.THEME_MANAGER.m_8_int();
      ANIMATION.setDuration(0.0F);
   }

   public static int rgba(int value6, int value7, int value8, int value9) {
      return (value9 & 0xFF) << 24 | (value6 & 0xFF) << 16 | (value7 & 0xFF) << 8 | value8 & 0xFF;
   }

   public static int rgb(int value6, int value7, int value8) {
      return rgba(value6, value7, value8, 255);
   }

   public static int primaryColor() {
      m_1_void();
      return lerpColor(value, value2, ANIMATION.update(1.0F));
   }

   public static int secondaryColor() {
      m_1_void();
      return lerpColor(value3, value4, ANIMATION.update(1.0F));
   }

   public static int withAlpha(int value6, int value7) {
      return value6 & 16777215 | (value7 & 0xFF) << 24;
   }

   public static int lerpColor(int value6, int value7, float floatValue) {
      int value8 = value6 >> 24 & 0xFF;
      int value9 = value6 >> 16 & 0xFF;
      int value10 = value6 >> 8 & 0xFF;
      int value11 = value6 & 0xFF;
      int value12 = value7 >> 24 & 0xFF;
      int value13 = value7 >> 16 & 0xFF;
      int value14 = value7 >> 8 & 0xFF;
      int value15 = value7 & 0xFF;
      return rgba(
         (int)(value9 + (value13 - value9) * floatValue), (int)(value10 + (value14 - value10) * floatValue), (int)(value11 + (value15 - value11) * floatValue), (int)(value8 + (value12 - value8) * floatValue)
      );
   }

   public static int m_3_int(float floatValue) {
      float floatValue2 = floatValue * (float) (Math.PI * 2);
      return rgb((int)(127.5 + 127.5 * Math.cos(floatValue2)), (int)(127.5 + 127.5 * Math.cos(floatValue2 + 2.094F)), (int)(127.5 + 127.5 * Math.cos(floatValue2 + 4.188F)));
   }

   public static int m_2(float floatValue, int value6, int value7, int value8, int value9) {
      float floatValue2 = Math.clamp(floatValue, 0.0F, 1.0F);
      return withAlpha(floatValue2 > 0.5F ? lerpColor(value8, value9, (floatValue2 - 0.5F) * 2.0F) : lerpColor(value7, value8, floatValue2 * 2.0F), value6);
   }

   public static float[] m_2(int value6, int value7, int value8) {
      float floatValue = value6 / 255.0F;
      float floatValue2 = value7 / 255.0F;
      float floatValue3 = value8 / 255.0F;
      float floatValue4 = Math.max(floatValue, Math.max(floatValue2, floatValue3));
      float floatValue5 = Math.min(floatValue, Math.min(floatValue2, floatValue3));
      ChatUtil.m_3_IArr();
      float floatValue6 = floatValue4 - floatValue5;
      float floatValue7 = 0.0F;
      if (floatValue6 != 0.0F) {
         if (floatValue4 == floatValue) {
            floatValue7 = (floatValue2 - floatValue3) / floatValue6 % 6.0F;
         } else if (floatValue4 == floatValue2) {
            floatValue7 = (floatValue3 - floatValue) / floatValue6 + 2.0F;
         } else {
            floatValue7 = (floatValue - floatValue2) / floatValue6 + 4.0F;
         }

         floatValue7 /= 6.0F;
         if (floatValue7 < 0.0F) {
            floatValue7++;
         }
      }

      float floatValue8 = floatValue4 == 0.0F ? 0.0F : floatValue6 / floatValue4;
      float floatValue9 = floatValue4;
      return new float[]{floatValue7, floatValue8, floatValue9};
   }

   public static int m_7_int(float floatValue, float floatValue2, float floatValue3) {
      if (floatValue2 == 0.0F) {
         int value6 = Math.round(floatValue3 * 255.0F);
         return rgba(value6, value6, value6, 255);
      }

      float floatValue4 = floatValue * 6.0F;
      int value7 = (int)Math.floor(floatValue4);
      float floatValue5 = floatValue4 - value7;
      float floatValue6 = floatValue3 * (1.0F - floatValue2);
      float floatValue7 = floatValue3 * (1.0F - floatValue2 * floatValue5);
      float floatValue8 = floatValue3 * (1.0F - floatValue2 * (1.0F - floatValue5));

      return switch (value7 % 6) {
         case 0 -> rgba(Math.round(floatValue3 * 255.0F), Math.round(floatValue8 * 255.0F), Math.round(floatValue6 * 255.0F), 255);
         case 1 -> rgba(Math.round(floatValue7 * 255.0F), Math.round(floatValue3 * 255.0F), Math.round(floatValue6 * 255.0F), 255);
         case 2 -> rgba(Math.round(floatValue6 * 255.0F), Math.round(floatValue3 * 255.0F), Math.round(floatValue8 * 255.0F), 255);
         case 3 -> rgba(Math.round(floatValue6 * 255.0F), Math.round(floatValue7 * 255.0F), Math.round(floatValue3 * 255.0F), 255);
         case 4 -> rgba(Math.round(floatValue8 * 255.0F), Math.round(floatValue6 * 255.0F), Math.round(floatValue3 * 255.0F), 255);
         case 5 -> rgba(Math.round(floatValue3 * 255.0F), Math.round(floatValue6 * 255.0F), Math.round(floatValue7 * 255.0F), 255);
         default -> 0;
      };
   }

   static {
      m_5_void(0);
   }

   public static void m_5_void(int value6) {
      value5 = value6;
   }

   public static int m_6_int() {
      return value5;
   }

   public static int m_7_int() {
      return 16;
   }
}
