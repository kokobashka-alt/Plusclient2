package wtf.onetap.gui.setting;

import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.gui.ColorPicker;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.KeyNames;

public class KeybindWidget implements SettingWidget {
   private final KeybindSetting keybindSetting;
   private final ValueLabelRenderer valueLabelRenderer;
   private final Animation animation;
   private final Animation animation2;
   private boolean flag;

   public KeybindWidget(KeybindSetting keybindSetting2) {
      this.valueLabelRenderer = new ValueLabelRenderer();
      this.animation = new Animation(300L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(200L, 0.0F, Easing.EASING8);
      this.keybindSetting = keybindSetting2;
      String text = keybindSetting2.getValue() == -1 ? "NONE" : KeyNames.getKeyName((Integer)keybindSetting2.getValue());
      this.animation2.setDuration(Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F) + 10.0F);
      if ((Integer)keybindSetting2.getValue() != -1) {
         this.animation.setDuration(1.0F);
      }
   }

   public void m_6_void() {
      this.flag = true;
   }

   @Override
   public void deactivate() {
      this.flag = false;
   }

   @Override
   public void onKeyPress(int value) {
      this.keybindSetting.setValue(value);
      this.flag = false;
   }

   @Override
   public void bindKey(int value) {
      this.keybindSetting.setValue(-value - 1);
      this.flag = false;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void m_2(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      ColorPicker.m_0_StringArr();
      int value = (int)(200.0F * floatValue4);
      int value2 = (int)(255.0F * floatValue4);
      int value3 = ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), value);
      String text = this.flag ? "..." : (this.keybindSetting.getValue() == -1 ? "NONE" : KeyNames.getKeyName((Integer)this.keybindSetting.getValue()));
      float floatValue5 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F) + 10.0F;
      float floatValue6 = this.animation2.update(floatValue5);
      float floatValue7 = floatValue + floatValue3 - 6.0F - floatValue6;
      float floatValue8 = floatValue2 + 4.0F;
      float floatValue9 = floatValue + 6.0F;
      float floatValue10 = Math.max(0.0F, floatValue7 - floatValue9 - 4.0F);
      this.valueLabelRenderer.m_7(this.keybindSetting.getName(), floatValue9, floatValue2 + 5.0F, value3, floatValue10, 12.0F, 0.8F, floatValue, floatValue2, floatValue3, 24.0F);
      float floatValue11 = this.animation.update(!this.flag && this.keybindSetting.getValue() == -1 ? 0.0F : 1.0F);
      int value4 = ColorUtil.rgba(60, 60, 60, 180);
      int value5 = ColorUtil.rgba(35, 35, 35, 180);
      int value6 = ColorUtil.lerpColor(value4, ColorUtil.primaryColor(), floatValue11);
      int value7 = ColorUtil.lerpColor(value5, ColorUtil.secondaryColor(), floatValue11);
      Draw2D.m_8(floatValue7, floatValue8, floatValue6, 16.0F, 4.0F, 0.0F, ColorUtil.withAlpha(value6, (int)(230.0F * floatValue4)), ColorUtil.withAlpha(value7, (int)(230.0F * floatValue4)));
      float floatValue12 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F);
      int value8 = ColorUtil.lerpColor(value3, ColorUtil.withAlpha(ColorUtil.rgba(245, 245, 245, 255), value2), floatValue11);
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER16, floatValue7 + (floatValue6 - floatValue12) / 2.0F, floatValue8 + 2.0F, value8, 11.0F);
   }

   public boolean m_2(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      if (floatValue >= floatValue3 && floatValue <= floatValue3 + floatValue5 && floatValue2 >= floatValue4 && floatValue2 <= floatValue4 + 24.0F) {
         this.flag = true;
         return true;
      } else {
         return false;
      }
   }

   public void m_8_void() {
   }
}
