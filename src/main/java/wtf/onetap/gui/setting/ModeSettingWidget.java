package wtf.onetap.gui.setting;

import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.gui.ColorPicker;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.ModeSetting;

public class ModeSettingWidget {
   private final ModeSetting modeSetting;
   private final Animation animation;
   private String text;

   public ModeSettingWidget(ModeSetting modeSetting2) {
      this.animation = new Animation(250L, 1.0F, Easing.EASING3);
      this.modeSetting = modeSetting2;
      this.text = (String)modeSetting2.getValue();
   }

   public float m_2_float(float floatValue) {
      String[] stringArray = this.modeSetting.getModes().toArray(String[]::new);
      if (stringArray.length == 0) {
         return 42.0F;
      }

      float floatValue2 = 6.0F;
      int value = 1;
      float floatValue3 = 4.0F;

      for (String text2 : stringArray) {
         float floatValue4 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 11.0F) + 6.0F;
         if (floatValue2 + floatValue4 > floatValue + 6.0F && floatValue2 > 6.0F) {
            value++;
            floatValue2 = 6.0F;
         }

         floatValue2 += floatValue4 + floatValue3;
      }

      return 23 + value * 20;
   }

   public void m_2(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      int value = (int)(200.0F * floatValue4);
      int value2 = (int)(255.0F * floatValue4);
      int value3 = ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), value);
      Draw2D.drawStyledText(this.modeSetting.getName(), Fonts.SUPPLIER16, floatValue + 6.0F, floatValue2 + 5.0F, value3, 12.0F);
      float floatValue5 = floatValue2 + 23.0F;
      String[] stringArray = this.modeSetting.getModes().toArray(String[]::new);
      float floatValue6 = floatValue + 6.0F;
      float floatValue7 = 4.0F;
      float floatValue8 = floatValue + 6.0F + floatValue3;
      float floatValue9 = this.animation.get();
      int value4 = ColorUtil.rgba(70, 70, 70, 180);
      int value5 = ColorUtil.rgba(25, 25, 25, 180);
      String[] stringArray2 = stringArray;
      ColorPicker.m_0_StringArr();

      for (String text2 : stringArray2) {
         float floatValue10 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 11.0F) + 6.0F;
         if (floatValue6 + floatValue10 > floatValue8 && floatValue6 > floatValue + 6.0F) {
            floatValue6 = floatValue + 6.0F;
            floatValue5 += 20.0F;
         }

         boolean flag = text2.equals(this.modeSetting.getValue());
         boolean flag2 = text2.equals(this.text);
         float floatValue11 = flag ? floatValue9 : (flag2 && !flag ? 1.0F - floatValue9 : 0.0F);
         int value6 = ColorUtil.lerpColor(value4, ColorUtil.primaryColor(), floatValue11);
         int value7 = ColorUtil.lerpColor(value5, ColorUtil.secondaryColor(), floatValue11);
         Draw2D.m_8(floatValue6, floatValue5, floatValue10, 16.0F, 4.0F, 0.0F, ColorUtil.withAlpha(value6, (int)(230.0F * floatValue4)), ColorUtil.withAlpha(value7, (int)(230.0F * floatValue4)));
         float floatValue12 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 11.0F);
         int value8 = ColorUtil.lerpColor(value3, ColorUtil.withAlpha(ColorUtil.rgba(245, 245, 245, 255), value2), floatValue11);
         Draw2D.drawTextWithShadow(text2, Fonts.SUPPLIER16, floatValue6 + (floatValue10 - floatValue12) / 2.0F, floatValue5 + 2.0F, value8, 11.0F);
         floatValue6 += floatValue10 + floatValue7;
      }
   }

   public boolean m_4_boolean(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      float floatValue6 = floatValue4 + 23.0F;
      if (floatValue2 < floatValue6) {
         return false;
      }

      String[] stringArray = this.modeSetting.getModes().toArray(String[]::new);
      float floatValue7 = floatValue3 + 6.0F;
      float floatValue8 = 4.0F;
      float floatValue9 = floatValue3 + 6.0F + floatValue5;

      for (String text2 : stringArray) {
         float floatValue10 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 11.0F) + 6.0F;
         if (floatValue7 + floatValue10 > floatValue9 && floatValue7 > floatValue3 + 6.0F) {
            floatValue7 = floatValue3 + 6.0F;
            floatValue6 += 20.0F;
         }

         if (floatValue2 >= floatValue6 && floatValue2 <= floatValue6 + 16.0F && floatValue >= floatValue7 && floatValue <= floatValue7 + floatValue10) {
            if (!text2.equals(this.modeSetting.getValue())) {
               float floatValue11 = this.animation.get();
               this.text = (String)this.modeSetting.getValue();
               this.modeSetting.setValue(text2);
               this.animation.setDuration(1.0F - floatValue11);
               this.animation.setTarget(1.0F);
            }

            return true;
         }

         floatValue7 += floatValue10 + floatValue8;
      }

      return false;
   }

   public void m_2_void() {
   }
}
