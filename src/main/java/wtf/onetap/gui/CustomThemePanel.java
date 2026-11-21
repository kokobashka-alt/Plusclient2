package wtf.onetap.gui;

import net.minecraft.client.Minecraft;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.ThemeManager;

public class CustomThemePanel {
   private final ColorPicker colorPicker;
   private final ColorPicker colorPicker2;
   private int value;
   private int value2;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private boolean flag;
   private boolean flag2;
   private final Animation animation;
   private final Animation animation2;

   public CustomThemePanel() {
      this.colorPicker = new ColorPicker(Onetap.THEME_MANAGER.getColorSetting());
      this.colorPicker2 = new ColorPicker(Onetap.THEME_MANAGER.getColorSetting2());
      this.value = (Integer)Onetap.THEME_MANAGER.getColorSetting().getValue();
      this.value2 = (Integer)Onetap.THEME_MANAGER.getColorSetting2().getValue();
      this.animation = new Animation(300L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(150L, 0.0F, Easing.EASING8);
   }

   public void openAt(float floatValue5, float floatValue6) {
      if (!this.flag) {
         this.floatValue = floatValue5;
         this.floatValue2 = floatValue6;
         this.flag = true;
         this.animation.setDuration(0.0F);
         this.m_1(true);
      }
   }

   public void close() {
      this.flag = false;
      this.m_1(false);
   }

   public void m_1(boolean flag3) {
      this.animation.setTarget(this.flag && flag3 ? 1.0F : 0.0F);
      if (!flag3) {
         this.flag2 = false;
         this.colorPicker.reset();
         this.colorPicker2.reset();
         Onetap.THEME_MANAGER.m_7_void();
      }
   }

   public boolean isFlag() {
      return this.flag;
   }

   public boolean m_7_boolean(float floatValue5, float floatValue6) {
      if (!this.flag) {
         return false;
      }

      float floatValue7 = this.animation.get();
      float floatValue8 = 0.9F + 0.1F * floatValue7;
      float floatValue9 = this.m_8_float();
      float floatValue10 = this.floatValue + 100.0F;
      float floatValue11 = this.floatValue2 + floatValue9 / 2.0F;
      float floatValue12 = 200.0F * floatValue8 / 2.0F;
      float floatValue13 = floatValue9 * floatValue8 / 2.0F;
      return floatValue5 >= floatValue10 - floatValue12 && floatValue5 <= floatValue10 + floatValue12 && floatValue6 >= floatValue11 - floatValue13 && floatValue6 <= floatValue11 + floatValue13;
   }

   private float m_8_float() {
      return 60.0F + this.colorPicker.getPanelHeight() - 3.0F + this.colorPicker2.getPanelHeight() - 3.0F + 6.0F;
   }

   public void resetPickers() {
      if (this.flag) {
         this.flag2 = false;
         this.colorPicker.reset();
         this.colorPicker2.reset();
      }
   }

   public boolean m_2(float floatValue5, float floatValue6, int value3) {
      if (!this.flag) {
         return false;
      } else {
         float floatValue7 = this.animation.get();
         float floatValue8 = 0.9F + 0.1F * floatValue7;
         float floatValue9 = this.m_8_float();
         float floatValue10 = this.floatValue + 100.0F;
         float floatValue11 = this.floatValue2 + floatValue9 / 2.0F;
         float floatValue12 = floatValue10 + (floatValue5 - floatValue10) / floatValue8;
         float floatValue13 = floatValue11 + (floatValue6 - floatValue11) / floatValue8;
         if (this.m_9_boolean(floatValue12, floatValue13)) {
            this.close();
            return true;
         } else if (value3 == 0 && this.m_2_boolean(floatValue12, floatValue13)) {
            this.flag2 = true;
            this.floatValue3 = floatValue12 - this.floatValue;
            this.floatValue4 = floatValue13 - this.floatValue2;
            return true;
         } else if (value3 != 0) {
            return true;
         } else {
            return this.colorPicker.m_6(floatValue12, floatValue13, this.floatValue + 6.0F, this.floatValue2 + 60.0F, 188.0F)
               ? true
               : this.colorPicker2.m_6(floatValue12, floatValue13, this.floatValue + 6.0F, this.floatValue2 + 60.0F + this.colorPicker.getPanelHeight() - 3.0F, 188.0F);
         }
      }
   }

   public void m_2_void() {
      ClickGUI.m_3_String();
      this.m_4_void();
      this.m_3_void();
      float floatValue5 = this.animation.get();
      if (!(floatValue5 < 0.01F)) {
         float floatValue6 = this.m_8_float();
         float floatValue7 = 0.9F + 0.1F * floatValue5;
         float floatValue8 = Render2D.getScale();
         float floatValue9 = Render2D.getOffsetX();
         float floatValue10 = Render2D.getOffsetY();
         float floatValue11 = this.floatValue + 100.0F;
         float floatValue12 = this.floatValue2 + floatValue6 / 2.0F;
         Render2D.setTransform(floatValue8 * floatValue7, floatValue9 + floatValue8 * (floatValue11 - floatValue11 * floatValue7), floatValue10 + floatValue8 * (floatValue12 - floatValue12 * floatValue7));

         try {
            ThemeManager themeManager = Onetap.THEME_MANAGER;
            Draw2D.m_5(this.floatValue, this.floatValue2, 200.0F, floatValue6, 8.0F, 2.0F * floatValue5, ColorUtil.rgba(155, 155, 155, (int)(255.0F * floatValue5)), ClickGUI.getGlow() * floatValue5);
            Draw2D.m_1(this.floatValue, this.floatValue2, 200.0F, floatValue6, 8.0F, 4.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue5)));
            Draw2D.roundedRect(this.floatValue, this.floatValue2, 200.0F, floatValue6, 8.0F, ColorUtil.rgba(12, 12, 22, (int)(45.0F * floatValue5)));
            Draw2D.drawStyledText("Кастомная тема", Fonts.SUPPLIER16, this.floatValue + 12.0F, this.floatValue2 + 8.0F, ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), (int)(222.0F * floatValue5)), 13.0F);
            Minecraft mc = Minecraft.getInstance();
            double doubleValue = mc.getWindow().getGuiScale();
            float floatValue13 = (float)(mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale();
            float floatValue14 = (float)(mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale();
            float floatValue15 = this.animation2.update(this.m_9_boolean(floatValue13, floatValue14) ? 1.0F : 0.0F);
            float floatValue16 = 14.0F + floatValue15 * 2.0F;
            float floatValue17 = Draw2D.getTextWidth("\uf00d", Fonts.SUPPLIER8, floatValue16);
            int value3 = (int)(190.0F + 45.0F * floatValue15);
            Draw2D.drawStyledText(
               "\uf00d",
               Fonts.SUPPLIER8,
               this.floatValue + 200.0F - 17.0F - floatValue17 / 2.0F,
               this.floatValue2 + (33.0F - floatValue16) / 2.0F,
               ColorUtil.rgba(value3, value3, value3, (int)(220.0F * floatValue5)),
               floatValue16
            );
            Draw2D.m_0(
               this.floatValue + 12.0F,
               this.floatValue2 + 30.0F,
               176.0F,
               22.0F,
               5.0F,
               0.0F,
               ColorUtil.withAlpha((Integer)themeManager.getColorSetting().getValue(), (int)(255.0F * floatValue5)),
               ColorUtil.withAlpha((Integer)themeManager.getColorSetting2().getValue(), (int)(255.0F * floatValue5))
            );
            Draw2D.m_4(this.floatValue + 12.0F, this.floatValue2 + 30.0F, 176.0F, 22.0F, 0.5F, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(30.0F * floatValue5)));
            this.colorPicker.m_4_void(this.floatValue + 6.0F, this.floatValue2 + 60.0F, 188.0F, floatValue5);
            this.colorPicker2.m_4_void(this.floatValue + 6.0F, this.floatValue2 + 60.0F + this.colorPicker.getPanelHeight() - 3.0F, 188.0F, floatValue5);
         } finally {
            Render2D.setTransform(floatValue8, floatValue9, floatValue10);
         }
      }
   }

   private void m_3_void() {
      ThemeManager themeManager = Onetap.THEME_MANAGER;
      int value3 = (Integer)themeManager.getColorSetting().getValue();
      int value4 = (Integer)themeManager.getColorSetting2().getValue();
      if (value3 != this.value || value4 != this.value2) {
         this.value = value3;
         this.value2 = value4;
         themeManager.m_4_void();
      }
   }

   private boolean m_9_boolean(float floatValue5, float floatValue6) {
      return floatValue5 >= this.floatValue + 200.0F - 28.0F && floatValue5 <= this.floatValue + 200.0F - 6.0F && floatValue6 >= this.floatValue2 + 4.0F && floatValue6 <= this.floatValue2 + 26.0F;
   }

   private boolean m_2_boolean(float floatValue5, float floatValue6) {
      return floatValue5 >= this.floatValue && floatValue5 <= this.floatValue + 200.0F && floatValue6 >= this.floatValue2 && floatValue6 <= this.floatValue2 + 28.0F;
   }

   private void m_4_void() {
      if (this.flag2) {
         Minecraft mc = Minecraft.getInstance();
         double doubleValue = mc.getWindow().getGuiScale();
         this.floatValue = (float)((mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale() - this.floatValue3);
         this.floatValue2 = (float)((mc.mouseHandler.ypos() / doubleValue - Render2D.getOffsetY()) / Render2D.getScale() - this.floatValue4);
      }
   }

   private static String a(byte[] byteArray) {
      int value3 = 0;
      int value4;
      char[] charArray = new char[value4 = byteArray.length];

      for (int i = 0; i < value4; i++) {
         int value5;
         if ((value5 = 255 & byteArray[i]) < 192) {
            charArray[value3++] = (char)value5;
         } else if (value5 < 224) {
            char ch = (char)((char)(value5 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value3++] = ch;
         } else if (i < value4 - 2) {
            char ch2 = (char)((char)(value5 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value3++] = ch2;
         }
      }

      return new String(charArray, 0, value3);
   }
}
