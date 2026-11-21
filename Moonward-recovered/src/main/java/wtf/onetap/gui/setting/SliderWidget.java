package wtf.onetap.gui.setting;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.FloatSetting;

public class SliderWidget {
   private final FloatSetting floatSetting;
   private final ValueLabelRenderer valueLabelRenderer;
   private final Animation animation;
   private final Animation animation2;
   private final String text;
   private float floatValue;
   private boolean flag;

   public SliderWidget(FloatSetting floatSetting2) {
      this.valueLabelRenderer = new ValueLabelRenderer();
      this.animation = new Animation(180L, 0.0F, Easing.EASING8);
      this.floatValue = -1.0F;
      this.floatSetting = floatSetting2;
      this.animation2 = new Animation(120L, ((Float)floatSetting2.getValue() - floatSetting2.getMin()) / (floatSetting2.getMax() - floatSetting2.getMin()), Easing.EASING8);
      this.text = formatValue(floatSetting2.getMax(), floatSetting2.getStep());
   }

   public void m_5(float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      if (this.floatValue < 0.0F) {
         this.floatValue = Draw2D.getTextWidth(this.text, Fonts.SUPPLIER16, 12.0F) + 6.0F;
      }

      int value = (int)(200.0F * floatValue5);
      int value2 = ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), value);
      String text2 = formatValue(((Float)this.floatSetting.getValue()).floatValue(), this.floatSetting.getStep());
      Draw2D.drawStyledText(text2, Fonts.SUPPLIER16, floatValue2 + floatValue4 - this.floatValue + 4.0F, floatValue3 + 5.0F, value2, 12.0F);
      float floatValue6 = Draw2D.getTextWidth(this.floatSetting.getName(), Fonts.SUPPLIER16, 12.0F);
      float floatValue7 = Math.max(0.0F, floatValue4 - 12.0F - 2.0F - this.floatValue - 36.0F);
      float floatValue8 = Math.min(floatValue6, floatValue7);
      this.valueLabelRenderer.m_7(this.floatSetting.getName(), floatValue2 + 6.0F, floatValue3 + 5.0F, value2, floatValue8, 12.0F, 0.65F, floatValue2, floatValue3, 6.0F + floatValue8 + 3.0F, 24.0F);
      float floatValue9 = floatValue2 + 6.0F + floatValue8 + 6.0F;
      float floatValue10 = floatValue4 - 12.0F - floatValue8 - 2.0F - this.floatValue;
      if (!(floatValue10 <= 0.0F)) {
         float floatValue11 = floatValue3 + 11.0F;
         if (this.flag) {
            this.m_9_void(floatValue9, floatValue10);
         }

         float floatValue12 = ((Float)this.floatSetting.getValue() - this.floatSetting.getMin()) / (this.floatSetting.getMax() - this.floatSetting.getMin());
         float floatValue13;
         if (this.flag) {
            floatValue13 = this.animation2.getValue() + (floatValue12 - this.animation2.getValue()) * 0.05F;
            this.animation2.setDuration(floatValue13);
         } else {
            floatValue13 = this.animation2.update(floatValue12);
         }

         Draw2D.roundedRect(floatValue9, floatValue11, floatValue10, 4.0F, 1.75F, ColorUtil.withAlpha(ColorUtil.rgba(60, 60, 60, 255), value));
         float floatValue14 = floatValue10 * floatValue13;
         if (floatValue14 > 0.0F) {
            Draw2D.m_0(floatValue9, floatValue11, floatValue14, 4.0F, 1.75F, 0.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), value), ColorUtil.withAlpha(ColorUtil.secondaryColor(), value));
         }

         float floatValue15 = 9.0F + 2.0F * this.animation.update(this.flag ? 1.0F : 0.0F);
         float floatValue16 = floatValue9 + floatValue10 * floatValue13 - floatValue15 / 2.0F;
         float floatValue17 = floatValue11 + 2.0F - floatValue15 / 2.0F;
         Draw2D.roundedRect(floatValue16, floatValue17, floatValue15, floatValue15, floatValue15 / 2.0F - 0.5F, ColorUtil.withAlpha(ColorUtil.rgba(255, 255, 255, 255), (int)(240.0F * floatValue5)));
      }
   }

   public boolean m_1_boolean(float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      if (floatValue2 >= floatValue4 && floatValue2 <= floatValue4 + floatValue6 && floatValue3 >= floatValue5 && floatValue3 <= floatValue5 + 24.0F) {
         this.flag = true;
         return true;
      } else {
         return false;
      }
   }

   public void m_8_void() {
      this.flag = false;
   }

   private void m_9_void(float floatValue2, float floatValue3) {
      Minecraft mc = Minecraft.getInstance();
      double doubleValue = mc.getWindow().getGuiScale();
      double doubleValue2 = (mc.mouseHandler.xpos() / doubleValue - Render2D.getOffsetX()) / Render2D.getScale();
      double doubleValue3 = this.floatSetting.getMin() + (this.floatSetting.getMax() - this.floatSetting.getMin()) * ((doubleValue2 - floatValue2) / floatValue3);
      double doubleValue4 = this.floatSetting.getStep();
      double doubleValue5 = doubleValue4 > 0.0 ? Math.round(doubleValue3 / doubleValue4) * doubleValue4 : doubleValue3;
      this.floatSetting.setValue((float)Math.max(this.floatSetting.getMin(), Math.min(this.floatSetting.getMax(), doubleValue5)));
   }

   private static String formatValue(double doubleValue, float floatValue2) {
      int value = m_7_int(floatValue2);
      return String.format(Locale.US, "%." + value + "f", doubleValue);
   }

   private static int m_7_int(float floatValue2) {
      String text2 = String.valueOf(floatValue2).replaceAll("0+$", "").replaceAll("\\.$", "");
      int value = text2.indexOf(46);
      return value < 0 ? 0 : text2.length() - value - 1;
   }
}
