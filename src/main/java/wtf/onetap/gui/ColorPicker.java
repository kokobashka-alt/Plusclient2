package wtf.onetap.gui;

import net.minecraft.client.Minecraft;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.ColorSetting;

public class ColorPicker {
   private final ColorSetting colorSetting;
   private final Animation animation;
   private final Animation animation2;
   private final Animation animation3;
   private final Animation animation4;
   private boolean flag;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private boolean flag2;
   private boolean flag3;
   private int value;
   private String text;
   private long longValue;
   private long longValue2;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private static String[] textArray;

   public ColorPicker(ColorSetting colorSetting2) {
      this.animation = new Animation(200L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation3 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation4 = new Animation(120L, 0.0F, Easing.EASING18);
      this.value = -1;
      this.colorSetting = colorSetting2;
      float[] floatArray = ColorUtil.m_2(colorSetting2.getRed(), colorSetting2.getGreen(), colorSetting2.getBlue());
      this.floatValue = floatArray[0];
      this.floatValue2 = floatArray[1];
      this.floatValue3 = floatArray[2];
   }

   public float getPanelHeight() {
      return 24.0F + 110.0F * this.animation.update(this.flag ? 1.0F : 0.0F);
   }

   public void m_4_void(float floatValue8, float floatValue9, float floatValue10, float floatValue11) {
      m_0_StringArr();
      int value2 = (int)(200.0F * floatValue11);
      int value3 = ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), value2);
      this.floatValue4 = floatValue8 + 6.0F;
      this.floatValue5 = floatValue10 - 12.0F;
      Draw2D.drawStyledText(this.colorSetting.getName(), Fonts.SUPPLIER16, floatValue8 + 6.0F, floatValue9 + 5.0F, value3, 12.0F);
      float floatValue12 = floatValue8 + floatValue10 - 20.0F;
      float floatValue13 = floatValue9 + 5.0F;
      int value4 = (Integer)this.colorSetting.getValue();
      Draw2D.roundedRect(floatValue12, floatValue13, 14.0F, 14.0F, 4.0F, ColorUtil.withAlpha(value4, (int)(255.0F * floatValue11)));
      Draw2D.m_4(floatValue12, floatValue13, 14.0F, 14.0F, 0.5F, 4.0F, ColorUtil.withAlpha(ColorUtil.rgba(255, 255, 255, 30), (int)(155.0F * floatValue11)));
      float floatValue14 = this.animation.update(this.flag ? 1.0F : 0.0F);
      if (!(floatValue14 < 0.01F)) {
         float floatValue15 = floatValue9 + 27.0F;
         this.floatValue6 = this.floatValue4;
         this.floatValue7 = floatValue15 + 9.0F;
         this.m_3_void();
         Render2D.pushScissor(floatValue8, floatValue9 + 24.0F, floatValue10, 110.0F * floatValue14);
         int value5 = (int)(255.0F * floatValue11 * floatValue14);
         Draw2D.m_3(this.floatValue4, floatValue15, this.floatValue5, 6.0F, 2.0F, ColorUtil.rgba(255, 255, 255, value5));
         float floatValue16 = clamp(this.floatValue4 + this.floatValue5 * this.floatValue, this.floatValue4 + 4.0F, this.floatValue4 + this.floatValue5 - 4.0F);
         Draw2D.roundedRect(floatValue16 - 4.0F, floatValue15 - 2.0F, 8.0F, 10.0F, 2.0F, ColorUtil.rgba(255, 255, 255, value5));
         Draw2D.m_4(floatValue16 - 4.0F, floatValue15 - 2.0F, 8.0F, 10.0F, 0.5F, 2.0F, ColorUtil.rgba(0, 0, 0, value5));
         floatValue15 += 9.0F;
         m_4(this.floatValue6, this.floatValue7, this.floatValue5, 80.0F, this.floatValue, value5);
         float floatValue17 = clamp(this.floatValue6 + this.floatValue5 * this.floatValue2, this.floatValue6 + 4.0F, this.floatValue6 + this.floatValue5 - 4.0F);
         float floatValue18 = clamp(this.floatValue7 + 80.0F * (1.0F - this.floatValue3), this.floatValue7 + 4.0F, this.floatValue7 + 76.0F);
         Draw2D.roundedRect(floatValue17 - 4.0F, floatValue18 - 4.0F, 8.0F, 8.0F, 4.0F, ColorUtil.rgba(255, 255, 255, value5));
         Draw2D.m_4(floatValue17 - 4.0F, floatValue18 - 4.0F, 8.0F, 8.0F, 0.5F, 4.0F, ColorUtil.rgba(0, 0, 0, value5));
         long longValue3 = System.currentTimeMillis();
         if (longValue3 < this.longValue2) {
            float floatValue19 = this.m_3_float(longValue3);
            float floatValue20 = this.animation4.get();
            float floatValue21 = (1.0F - floatValue19) * 12.0F;
            if (floatValue20 < 0.99F) {
               this.m_2("Цвет скопирован!", this.floatValue7 + 34.0F - floatValue20 * 3.0F, floatValue21, floatValue11 * floatValue14 * floatValue19 * (1.0F - floatValue20));
            }

            if (floatValue20 > 0.01F) {
               this.m_2("Цвет вставлен!", this.floatValue7 + 34.0F + (1.0F - floatValue20) * 3.0F, floatValue21, floatValue11 * floatValue14 * floatValue19 * floatValue20);
            }
         }

         floatValue15 += 83.0F;
         String text2 = this.m_5_String();
         Draw2D.drawStyledText(text2, Fonts.SUPPLIER16, this.floatValue4, floatValue15, ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), (int)(200.0F * floatValue11 * floatValue14)), 12.0F);
         float floatValue22 = this.floatValue4 + Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 12.0F) + 6.0F;
         float floatValue23 = floatValue22 + 18.0F;
         Minecraft mc = Minecraft.getInstance();
         double doubleValue = mc.getWindow().getGuiScale();
         float floatValue24 = (float)(mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale();
         float floatValue25 = (float)(mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale();
         float floatValue26 = this.animation2.update(isHovered(floatValue24, floatValue25, floatValue22, floatValue15 - 1.0F) ? 1.0F : 0.0F);
         float floatValue27 = this.animation3.update(isHovered(floatValue24, floatValue25, floatValue23, floatValue15 - 1.0F) ? 1.0F : 0.0F);
         Draw2D.roundedRect(floatValue22, floatValue15 - 1.0F, 14.0F, 14.0F, 3.0F, ColorUtil.rgba(255, 255, 255, (int)(20.0F * floatValue11 * floatValue14 * floatValue26)));
         Draw2D.roundedRect(floatValue23, floatValue15 - 1.0F, 14.0F, 14.0F, 3.0F, ColorUtil.rgba(255, 255, 255, (int)(20.0F * floatValue11 * floatValue14 * floatValue27)));
         float floatValue28 = 10.0F + floatValue26;
         float floatValue29 = 10.0F + floatValue27;
         this.m_8("\uf0c5", floatValue22, floatValue15 - 1.0F, floatValue28, ColorUtil.rgba(222, 222, 222, (int)((170.0F + 85.0F * floatValue26) * floatValue11 * floatValue14)));
         this.m_8("\uf0ea", floatValue23, floatValue15 - 1.0F, floatValue29, ColorUtil.rgba(222, 222, 222, (int)((170.0F + 85.0F * floatValue27) * floatValue11 * floatValue14)));
         Render2D.popScissor();
      }
   }

   public boolean m_6(float floatValue8, float floatValue9, float floatValue10, float floatValue11, float floatValue12) {
      if (floatValue8 >= floatValue10 && floatValue8 <= floatValue10 + floatValue12 && floatValue9 >= floatValue11 && floatValue9 <= floatValue11 + 24.0F) {
         this.flag = !this.flag;
         return true;
      }

      if (this.flag && floatValue8 >= floatValue10 && floatValue8 <= floatValue10 + floatValue12 && floatValue9 >= floatValue11 && floatValue9 <= floatValue11 + this.getPanelHeight()) {
         float floatValue13 = floatValue10 + 6.0F;
         float floatValue14 = floatValue12 - 12.0F;
         float floatValue15 = floatValue11 + 27.0F;
         if (floatValue8 >= floatValue13 && floatValue8 <= floatValue13 + floatValue14 && floatValue9 >= floatValue15 && floatValue9 <= floatValue15 + 6.0F) {
            this.flag2 = true;
            this.floatValue = Math.max(0.0F, Math.min(1.0F, (floatValue8 - floatValue13) / floatValue14));
            this.m_6_void();
            return true;
         }

         float floatValue16 = floatValue15 + 9.0F;
         if (floatValue8 >= floatValue13 && floatValue8 <= floatValue13 + floatValue14 && floatValue9 >= floatValue16 && floatValue9 <= floatValue16 + 80.0F) {
            this.flag3 = true;
            this.floatValue2 = Math.max(0.0F, Math.min(1.0F, (floatValue8 - floatValue13) / floatValue14));
            this.floatValue3 = Math.max(0.0F, Math.min(1.0F, 1.0F - (floatValue9 - floatValue16) / 80.0F));
            this.m_6_void();
            return true;
         }

         float floatValue17 = floatValue16 + 82.0F;
         float floatValue18 = floatValue13 + Draw2D.getTextWidth(this.m_5_String(), Fonts.SUPPLIER16, 12.0F) + 6.0F;
         float floatValue19 = floatValue18 + 18.0F;
         Minecraft mc = Minecraft.getInstance();
         if (isHovered(floatValue8, floatValue9, floatValue18, floatValue17)) {
            mc.keyboardHandler.setClipboard(this.m_5_String());
            this.m_9_void(false);
            return true;
         }

         if (isHovered(floatValue8, floatValue9, floatValue19, floatValue17)) {
            String text2 = mc.keyboardHandler.getClipboard().trim();
            if (text2.startsWith("#")) {
               text2 = text2.substring(1);
            }

            try {
               if (text2.length() != 6) {
                  return true;
               }

               int value2 = Integer.parseInt(text2, 16);
               this.colorSetting.setValue(this.colorSetting.getAlpha() << 24 | value2);
               float[] floatArray = ColorUtil.m_2(this.colorSetting.getRed(), this.colorSetting.getGreen(), this.colorSetting.getBlue());
               this.floatValue = floatArray[0];
               this.floatValue2 = floatArray[1];
               this.floatValue3 = floatArray[2];
               this.m_9_void(true);
            } catch (NumberFormatException numberFormatException) {
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void reset() {
      this.flag2 = false;
      this.flag3 = false;
   }

   private void m_3_void() {
      if (this.flag2 || this.flag3) {
         Minecraft mc = Minecraft.getInstance();
         double doubleValue = mc.getWindow().getGuiScale();
         double doubleValue2 = (mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale();
         double doubleValue3 = (mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale();
         if (this.flag2) {
            this.floatValue = (float)Math.max(0.0, Math.min(1.0, (doubleValue2 - this.floatValue4) / this.floatValue5));
            this.m_6_void();
         }

         if (this.flag3) {
            this.floatValue2 = (float)Math.max(0.0, Math.min(1.0, (doubleValue2 - this.floatValue6) / this.floatValue5));
            this.floatValue3 = (float)Math.max(0.0, Math.min(1.0, 1.0 - (doubleValue3 - this.floatValue7) / 80.0));
            this.m_6_void();
         }
      }
   }

   private void m_9_void(boolean flag4) {
      long longValue3 = System.currentTimeMillis();
      if (longValue3 >= this.longValue2) {
         this.longValue = longValue3;
         this.animation4.setDuration(flag4 ? 1.0F : 0.0F);
      } else {
         float floatValue8 = this.m_3_float(longValue3);
         this.longValue = longValue3 - (long)(200.0 * (1.0 - Math.cbrt(1.0F - Math.max(0.0F, Math.min(1.0F, floatValue8)))));
         this.animation4.setTarget(flag4 ? 1.0F : 0.0F);
      }

      this.longValue2 = longValue3 + 1200L;
   }

   private float m_3_float(long longValue3) {
      float floatValue8 = (float)(longValue3 - this.longValue);
      float floatValue9 = (float)(this.longValue2 - longValue3);
      return floatValue8 < 200.0F ? Easing.EASING8.ease(floatValue8, 0.0F, 1.0F, 200.0F) : (floatValue9 < 200.0F ? Easing.EASING7.ease(floatValue9, 0.0F, 1.0F, 200.0F) : 1.0F);
   }

   private void m_2(String text2, float floatValue8, float floatValue9, float floatValue10) {
      float floatValue11 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 12.0F);
      Draw2D.drawTextWithShadow(text2, Fonts.SUPPLIER16, this.floatValue6 + (this.floatValue5 - floatValue11) / 2.0F + floatValue9, floatValue8, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue10)), 12.0F);
   }

   private void m_8(String text2, float floatValue8, float floatValue9, float floatValue10, int value2) {
      float floatValue11 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER8, floatValue10);
      Draw2D.drawStyledText(text2, Fonts.SUPPLIER8, floatValue8 + (14.0F - floatValue11) / 2.0F, floatValue9 + (14.0F - floatValue10) / 2.0F + 0.5F, value2, floatValue10);
   }

   private static boolean isHovered(float floatValue8, float floatValue9, float floatValue10, float floatValue11) {
      return floatValue8 >= floatValue10 && floatValue8 <= floatValue10 + 14.0F && floatValue9 >= floatValue11 && floatValue9 <= floatValue11 + 14.0F;
   }

   private void m_6_void() {
      int value2 = ColorUtil.m_7_int(this.floatValue, this.floatValue2, this.floatValue3);
      int value3 = this.colorSetting.getAlpha() << 24 | value2 & 16777215;
      if ((Integer)this.colorSetting.getValue() != value3) {
         this.colorSetting.setValue(value3);
      }
   }

   private String m_5_String() {
      int value2 = (Integer)this.colorSetting.getValue() & 16777215;
      if (this.value != value2) {
         this.value = value2;
         this.text = String.format("#%06X", value2);
      }

      return this.text;
   }

   public static void m_4(float floatValue8, float floatValue9, float floatValue10, float floatValue11, float floatValue12, int value2) {
      int value3 = ColorUtil.m_7_int(floatValue12, 1.0F, 1.0F);
      Draw2D.roundedRect(floatValue8, floatValue9, floatValue10, floatValue11, 6.0F, ColorUtil.withAlpha(value3, value2));
      Draw2D.m_0(floatValue8, floatValue9, floatValue10, floatValue11, 6.0F, 0.0F, ColorUtil.rgba(255, 255, 255, value2), ColorUtil.rgba(255, 255, 255, 0));
      Render2D.m_4(
         floatValue8 - 0.5F,
         floatValue9 - 0.5F,
         floatValue10 + 1.0F,
         floatValue11 + 1.0F,
         6.0F,
         0.0F,
         0.0F,
         ColorUtil.rgba(0, 0, 0, 0),
         ColorUtil.rgba(0, 0, 0, value2),
         ColorUtil.rgba(0, 0, 0, value2),
         ColorUtil.rgba(0, 0, 0, 0)
      );
   }

   private static float clamp(float floatValue8, float floatValue9, float floatValue10) {
      return Math.max(floatValue9, Math.min(floatValue10, floatValue8));
   }

   public static void m_4(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_0_StringArr() {
      return textArray;
   }

   static {
      m_4(null);
   }
}
