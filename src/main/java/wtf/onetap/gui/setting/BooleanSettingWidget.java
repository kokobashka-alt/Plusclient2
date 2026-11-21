package wtf.onetap.gui.setting;

import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.gui.ColorPicker;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.KeyNames;

public class BooleanSettingWidget implements SettingWidget {
   private final BooleanSetting booleanSetting;
   private final ValueLabelRenderer valueLabelRenderer;
   private final Animation animation;
   private boolean flag;

   public BooleanSettingWidget(BooleanSetting booleanSetting2) {
      this.valueLabelRenderer = new ValueLabelRenderer();
      this.animation = new Animation(250L, 0.0F, Easing.EASING8);
      this.booleanSetting = booleanSetting2;
      if ((Boolean)booleanSetting2.getValue()) {
         this.animation.setDuration(1.0F);
      }
   }

   @Override
   public void deactivate() {
      this.flag = false;
   }

   @Override
   public void onKeyPress(int value) {
      this.booleanSetting.setKey(value);
      this.flag = false;
   }

   @Override
   public void bindKey(int value) {
      this.booleanSetting.setKey(-value - 1);
      this.flag = false;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void m_1_void(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = floatValue + floatValue3 - 6.0F - 14.0F;
      ColorPicker.m_0_StringArr();
      float floatValue6 = floatValue2 + 5.0F;
      float floatValue7 = floatValue + 6.0F;
      float floatValue8 = floatValue5;
      String text = this.flag ? "[...]" : (this.booleanSetting.getKey() != -1 ? "[" + KeyNames.getKeyName(this.booleanSetting.getKey()) + "]" : null);
      if (text != null) {
         floatValue8 -= 4.0F + Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F);
         Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER16, floatValue8, floatValue2 + 6.0F, ColorUtil.withAlpha(ColorUtil.rgba(180, 180, 180, 255), (int)(200.0F * floatValue4)), 11.0F);
      }

      float floatValue9 = Math.max(0.0F, floatValue8 - floatValue7 - 4.0F);
      this.valueLabelRenderer
         .m_7(
            this.booleanSetting.getName(),
            floatValue7,
            floatValue2 + 5.0F,
            ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), (int)(200.0F * floatValue4)),
            floatValue9,
            12.0F,
            0.65F,
            floatValue,
            floatValue2,
            floatValue3,
            24.0F
         );
      float floatValue10 = this.animation.update(this.booleanSetting.getValue() ? 1.0F : 0.0F);
      float floatValue11 = 1.0F - floatValue10;
      int value = ColorUtil.rgba(100, 100, 100, 180);
      int value2 = ColorUtil.rgba(0, 0, 0, 180);
      int value3 = ColorUtil.lerpColor(value, ColorUtil.primaryColor(), floatValue10);
      int value4 = ColorUtil.lerpColor(value2, ColorUtil.secondaryColor(), floatValue10);
      Draw2D.m_8(floatValue5, floatValue6, 14.0F, 14.0F, 3.0F, 0.0F, ColorUtil.withAlpha(value3, (int)(220.0F * floatValue4)), ColorUtil.withAlpha(value4, (int)(220.0F * floatValue4)));
      if (floatValue11 > 0.01F) {
         int value5 = ColorUtil.lerpColor(ColorUtil.rgba(235, 235, 235, 255), value, floatValue10);
         float floatValue12 = 12.0F * (0.3F + 0.7F * floatValue11);
         this.m_1("\uf00d", floatValue5, floatValue6, floatValue12, ColorUtil.withAlpha(value5, (int)(255.0F * floatValue11 * floatValue4)));
      }

      if (floatValue10 > 0.01F) {
         int value6 = ColorUtil.lerpColor(value, ColorUtil.rgba(235, 235, 235, 255), floatValue10);
         float floatValue13 = 12.0F * (0.3F + 0.7F * floatValue10);
         this.m_1("\uf00c", floatValue5, floatValue6, floatValue13, ColorUtil.withAlpha(value6, (int)(255.0F * floatValue10 * floatValue4)));
      }
   }

   private void m_1(String text, float floatValue, float floatValue2, float floatValue3, int value) {
      float floatValue4 = Draw2D.getTextWidth(text, Fonts.SUPPLIER8, floatValue3);
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER8, floatValue + (14.0F - floatValue4) / 2.0F, floatValue2 + (14.0F - floatValue3) / 2.0F + 0.5F, value, floatValue3);
   }

   public boolean m_5_boolean(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value) {
      if (floatValue >= floatValue3 && floatValue <= floatValue3 + floatValue5 && floatValue2 >= floatValue4 && floatValue2 <= floatValue4 + 24.0F) {
         if (value == 2) {
            this.flag = true;
         } else {
            this.booleanSetting.setValue(!(Boolean)this.booleanSetting.getValue());
         }

         return true;
      } else {
         return false;
      }
   }

   public void m_4_void() {
   }

   private static String a(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
