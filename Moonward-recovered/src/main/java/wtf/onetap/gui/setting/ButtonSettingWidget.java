package wtf.onetap.gui.setting;

import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.ButtonSetting;

public class ButtonSettingWidget {
   private final ButtonSetting buttonSetting;

   public ButtonSettingWidget(ButtonSetting buttonSetting2) {
      this.buttonSetting = buttonSetting2;
   }

   public void m_4_void(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      Draw2D.roundedRect(floatValue + 6.0F, floatValue2 + 3.0F, floatValue3 - 12.0F, 18.0F, 3.0F, ColorUtil.rgba(100, 100, 100, (int)((this.buttonSetting.m_8_boolean() ? 100 : 40) * floatValue4)));
      String text = this.buttonSetting.m_7_String();
      float floatValue5 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 12.0F);
      Draw2D.drawStyledText(text, Fonts.SUPPLIER16, floatValue + (floatValue3 - floatValue5) / 2.0F, floatValue2 + 6.0F, ColorUtil.rgba(222, 222, 222, (int)((this.buttonSetting.m_8_boolean() ? 200 : 90) * floatValue4)), 12.0F);
   }

   public boolean m_8(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      if (!(floatValue < floatValue3 + 6.0F) && !(floatValue > floatValue3 + floatValue5 - 6.0F) && !(floatValue2 < floatValue4 + 3.0F) && !(floatValue2 > floatValue4 + 21.0F)) {
         this.buttonSetting.click();
         return true;
      } else {
         return false;
      }
   }
}
