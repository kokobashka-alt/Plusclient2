package wtf.onetap.render;

import java.util.function.Supplier;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;

public class Theme {
   public static final int VALUE = ColorUtil.rgba(0, 0, 0, 130);
   public static final int VALUE2 = ColorUtil.rgba(0, 0, 0, 90);
   public static final int VALUE3 = ColorUtil.rgba(222, 222, 222, 255);
   public static final int VALUE4 = ColorUtil.rgba(120, 80, 255, 255);
   private static final int VALUE5 = ColorUtil.rgba(255, 255, 255, 255);
   private final int value;
   private final int value2;
   private final int value3;
   private final ColorSetting colorSetting;
   private final ColorSetting colorSetting2;
   private final ColorSetting colorSetting3;
   private final ColorSetting colorSetting4;
   private final BooleanSetting booleanSetting;
   private final BooleanSetting booleanSetting2;
   private final FloatSetting floatSetting;
   private final Animation animation;
   private final Animation animation2;

   public Theme(String text) {
      this(text, false);
   }

   public Theme(String text, boolean flag) {
      this(text, flag, VALUE, VALUE3, VALUE5);
   }

   public Theme(String text, int value4, int value5, int value6) {
      this(text, false, value4, value5, value6);
   }

   private Theme(String text, boolean flag, int value4, int value5, int value6) {
      this.animation = new Animation(250L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(250L, 0.0F, Easing.EASING8);
      this.value = value4;
      ClickGUI.m_3_String();
      this.value2 = value5;
      this.value3 = value6;
      this.colorSetting = new ColorSetting(text + ".Шапка", value4).visibleWhen(() -> false).build();
      this.colorSetting2 = new ColorSetting(text + ".Тело", VALUE2).visibleWhen(() -> false).build();
      this.colorSetting3 = new ColorSetting(text + ".Текст", value5).visibleWhen(() -> false).build();
      this.colorSetting4 = new ColorSetting(text + ".Акцент", VALUE4).visibleWhen(() -> false).build();
      this.booleanSetting = new BooleanSetting(text + ".От темы", !flag).visibleWhen(() -> false).build();
      this.booleanSetting2 = flag ? new BooleanSetting(text + ".От хп", true).visibleWhen(() -> false).build() : null;
      this.floatSetting = new FloatSetting(text + ".Блюр", 3.0F, 0.0F, 6.0F, 1.0F).visibleWhen(() -> false).build();
   }

   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   public ColorSetting getColorSetting2() {
      return this.colorSetting2;
   }

   public ColorSetting getColorSetting3() {
      return this.colorSetting3;
   }

   public ColorSetting getColorSetting4() {
      return this.colorSetting4;
   }

   public BooleanSetting getBooleanSetting() {
      return this.booleanSetting;
   }

   public boolean m_1_boolean() {
      return this.booleanSetting2 != null;
   }

   public BooleanSetting getBooleanSetting2() {
      return this.booleanSetting2;
   }

   public Theme.ModeKind m_6_C8() {
      if (this.booleanSetting2 != null && (Boolean)this.booleanSetting2.getValue()) {
         return Theme.ModeKind.C8;
      } else {
         return this.booleanSetting.getValue() ? Theme.ModeKind.THEME : Theme.ModeKind.CUSTOM;
      }
   }

   public void m_4(Theme.ModeKind c8) {
      this.booleanSetting.setValue(c8 == Theme.ModeKind.THEME);
      if (this.booleanSetting2 != null) {
         this.booleanSetting2.setValue(c8 == Theme.ModeKind.C8);
      }
   }

   public int[] m_8_IArr(int value4) {
      Theme.ModeKind c8 = this.m_6_C8();
      float floatValue = this.animation.update(c8 == Theme.ModeKind.THEME ? 1.0F : 0.0F);
      float floatValue2 = this.animation2.update(c8 == Theme.ModeKind.CUSTOM ? 1.0F : 0.0F);
      int value5 = ColorUtil.rgba(0, 0, 0, 255);
      int value6 = (Integer)this.colorSetting4.getValue();
      int value7 = ColorUtil.lerpColor(value4, ColorUtil.primaryColor(), floatValue);
      int value8 = ColorUtil.lerpColor(ColorUtil.lerpColor(value5, value4, 0.5F), ColorUtil.secondaryColor(), floatValue);
      value7 = ColorUtil.lerpColor(value7, ColorUtil.withAlpha(value6, 255), floatValue2);
      value8 = ColorUtil.lerpColor(value8, ColorUtil.lerpColor(value5, ColorUtil.withAlpha(value6, 255), 0.5F), floatValue2);
      return new int[]{value7, value8};
   }

   public FloatSetting getFloatSetting() {
      return this.floatSetting;
   }

   public int getAccentColor() {
      return this.booleanSetting.getValue() ? ColorUtil.primaryColor() : (Integer)this.colorSetting4.getValue();
   }

   public int m_2_int() {
      return this.booleanSetting.getValue() ? ColorUtil.secondaryColor() : ColorUtil.lerpColor((Integer)this.colorSetting4.getValue(), ColorUtil.rgba(0, 0, 0, 255), 0.25F);
   }

   public int m_3_int() {
      return ColorUtil.lerpColor(this.getAccentColor(), VALUE5, 0.5F);
   }

   public int m_8_int(int value4) {
      return scaleAlpha((Integer)this.colorSetting3.getValue(), value4);
   }

   public void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      this.m_1(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, (Integer)this.colorSetting.getValue());
   }

   public void m_0(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      this.m_1(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, (Integer)this.colorSetting2.getValue());
   }

   public Theme.DataRecord m_9_C6(String text) {
      return new Theme.DataRecord(
         text,
         (Integer)this.colorSetting.getValue(),
         (Integer)this.colorSetting2.getValue(),
         (Integer)this.colorSetting3.getValue(),
         (Integer)this.colorSetting4.getValue(),
         (Boolean)this.booleanSetting.getValue(),
         this.booleanSetting2 != null && (Boolean)this.booleanSetting2.getValue(),
         (Float)this.floatSetting.getValue()
      );
   }

   public void m_9(Theme.DataRecord c6) {
      this.colorSetting.setValue(c6.m_8_z());
      this.colorSetting2.setValue(c6.m_8l_int());
      this.colorSetting3.setValue(c6.m_8_d());
      ClickGUI.m_3_String();
      this.colorSetting4.setValue(c6.m_8_u());
      if (this.booleanSetting2 != null) {
         this.booleanSetting2.setValue(c6.m_3_e_boolean());
         this.booleanSetting.setValue(!c6.m_3_e_boolean() && c6.m_3_x());
      } else {
         this.booleanSetting.setValue(c6.m_3_x() || c6.m_3_e_boolean());
      }

      this.floatSetting.setValue(c6.m_4e());
   }

   public void m_7_void(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      Draw2D.m_7(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue5, floatValue5, floatValue5, 0.0F, (Float)this.floatSetting.getValue() * floatValue6, scaleAlpha(this.value3, (int)(255.0F * floatValue6)));
      int value4 = scaleAlpha((Integer)this.colorSetting.getValue(), (int)(255.0F * floatValue6));
      Draw2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue5, floatValue5, floatValue5, 0.0F, value4, value4);
   }

   public void m_6_void() {
      this.colorSetting.setValue(this.value);
      this.colorSetting2.setValue(VALUE2);
      this.colorSetting3.setValue(this.value2);
      this.colorSetting4.setValue(VALUE4);
      this.booleanSetting.setValue(this.booleanSetting2 == null);
      if (this.booleanSetting2 != null) {
         this.booleanSetting2.setValue(true);
      }

      this.floatSetting.setValue(3.0F);
   }

   public static float getLabelWidth(String text, Supplier<FontAtlas> supplier) {
      return Draw2D.getTextWidth(text, supplier, 7.5F) + 5.0F;
   }

   public static void m_2(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, float floatValue3, int value4, int value5, int value6, boolean flag) {
      float floatValue4 = getLabelWidth(text, supplier);
      float floatValue5 = floatValue - floatValue4;
      float floatValue6 = floatValue2 + (floatValue3 - 8.5F) / 2.0F;
      Draw2D.roundedRect(floatValue5, floatValue6, floatValue4, 8.5F, 2.0F, scaleAlpha(value4, value6));
      Draw2D.drawStyledText(text, supplier, floatValue5 + 2.5F, Draw2D.getTextBaseline(supplier, 7.5F, floatValue6, 8.5F), scaleAlpha(value5, value6), 7.5F);
      if (flag) {
         m_5(floatValue5 - 0.5F, floatValue6 - 0.5F, floatValue4 + 1.0F, 9.5F, ColorUtil.rgba(255, 255, 255, 230));
      }
   }

   public static void m_3(float floatValue, float floatValue2, float floatValue3, int value4) {
      float floatValue4 = m_6_float();
      float floatValue5 = 3.5F;

      for (float floatValue6 = -floatValue4; floatValue6 < floatValue3; floatValue6 += floatValue5) {
         float floatValue7 = Math.max(floatValue6, 0.0F);
         float floatValue8 = Math.min(floatValue6 + 2.0F, floatValue3);
         if (floatValue8 > floatValue7) {
            Draw2D.rect(floatValue + floatValue7, floatValue2, floatValue8 - floatValue7, 0.5F, value4);
         }
      }
   }

   public static void m_1(float floatValue, float floatValue2, float floatValue3, int value4) {
      float floatValue4 = m_6_float();
      float floatValue5 = 3.5F;

      for (float floatValue6 = -floatValue4; floatValue6 < floatValue3; floatValue6 += floatValue5) {
         float floatValue7 = Math.max(floatValue6, 0.0F);
         float floatValue8 = Math.min(floatValue6 + 2.0F, floatValue3);
         if (floatValue8 > floatValue7) {
            Draw2D.rect(floatValue, floatValue2 + floatValue7, 0.5F, floatValue8 - floatValue7, value4);
         }
      }
   }

   public static void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, int value4) {
      m_3(floatValue, floatValue2, floatValue3, value4);
      m_3(floatValue, floatValue2 + floatValue4 - 0.5F, floatValue3, value4);
      m_1(floatValue, floatValue2, floatValue4, value4);
      m_1(floatValue + floatValue3 - 0.5F, floatValue2, floatValue4, value4);
   }

   private static float m_6_float() {
      return (float)(System.currentTimeMillis() % 1400L) / 1400.0F * 3.5F;
   }

   public static int scaleAlpha(int value4, int value5) {
      return ColorUtil.withAlpha(value4, (value4 >>> 24 & 0xFF) * value5 / 255);
   }

   private void m_1(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value4) {
      Draw2D.m_7(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, 0.0F, (Float)this.floatSetting.getValue(), this.value3);
      Draw2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, 0.0F, value4, value4);
   }

   public record DataRecord(String f_6g, int f_8_z, int f_8l, int f_8_d, int f_8_u, boolean f_3_x, boolean f_3_e, float f_4e) {
      public String m_6g_String() {
         return this.f_6g;
      }

      public int m_8_z() {
         return this.f_8_z;
      }

      public int m_8l_int() {
         return this.f_8l;
      }

      public int m_8_d() {
         return this.f_8_d;
      }

      public int m_8_u() {
         return this.f_8_u;
      }

      public boolean m_3_x() {
         return this.f_3_x;
      }

      public boolean m_3_e_boolean() {
         return this.f_3_e;
      }

      public float m_4e() {
         return this.f_4e;
      }
   }

   public enum ModeKind {
      CUSTOM,
      THEME,
      C8;

      private static String a(byte[] byteArray) {
         int value4 = 0;
         int value5;
         char[] charArray = new char[value5 = byteArray.length];

         for (int i = 0; i < value5; i++) {
            int value6;
            if ((value6 = 255 & byteArray[i]) < 192) {
               charArray[value4++] = (char)value6;
            } else if (value6 < 224) {
               char ch = (char)((char)(value6 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value4++] = ch;
            } else if (i < value5 - 2) {
               char ch2 = (char)((char)(value6 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value4++] = ch2;
            }
         }

         return new String(charArray, 0, value4);
      }
   }
}
