package wtf.onetap.gui.setting;

import wtf.onetap.gui.ColorPicker;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.LabelSetting;

public class LabelWidget {
   private final LabelSetting labelSetting;

   public LabelWidget(LabelSetting labelSetting2) {
      this.labelSetting = labelSetting2;
   }

   public void m_1_void(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      String text = this.labelSetting.getName();
      float floatValue5 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 12.0F);
      float floatValue6 = floatValue + 6.0F;
      float floatValue7 = floatValue + floatValue3 - 6.0F;
      ColorPicker.m_0_StringArr();
      float floatValue8 = (floatValue6 + floatValue7) / 2.0F;
      float floatValue9 = floatValue8 - floatValue5 / 2.0F;
      float floatValue10 = floatValue2 + 11.5F;
      int value = ColorUtil.rgba(255, 255, 255, (int)(60.0F * floatValue4));
      float floatValue11 = floatValue9 - 6.0F - floatValue6;
      if (floatValue11 > 0.0F) {
         Draw2D.roundedRect(floatValue6, floatValue10, floatValue11, 1.0F, 0.5F, value);
      }

      float floatValue12 = floatValue9 + floatValue5 + 6.0F;
      float floatValue13 = floatValue7 - floatValue12;
      if (floatValue13 > 0.0F) {
         Draw2D.roundedRect(floatValue12, floatValue10, floatValue13, 1.0F, 0.5F, value);
      }

      Draw2D.drawStyledText(text, Fonts.SUPPLIER16, floatValue9, floatValue2 + 6.0F, ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), (int)(200.0F * floatValue4)), 12.0F);
   }
}
