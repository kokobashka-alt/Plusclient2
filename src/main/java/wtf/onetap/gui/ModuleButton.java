package wtf.onetap.gui;

import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.module.Module;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.util.KeyNames;

public class ModuleButton {
   private final Module module;
   private final Animation animation;
   private boolean flag;
   private float floatValue;
   private float floatValue2;

   public ModuleButton(Module module2) {
      this.animation = new Animation(200L, 0.0F, Easing.EASING8);
      this.module = module2;
      if (module2.isEnabled()) {
         this.animation.setDuration(1.0F);
      }
   }

   public void m_7_void() {
      this.flag = true;
   }

   public void m_2_void() {
      this.flag = false;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public Module getModule() {
      return this.module;
   }

   public void m_7_void(float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      ClickGUI.m_3_String();
      int value = ColorUtil.primaryColor();
      int value2 = ColorUtil.secondaryColor();
      Draw2D.roundedRect(floatValue3, floatValue4, floatValue5, 26.0F, 8.0F, ColorUtil.rgba(0, 0, 0, (int)(35.0F * floatValue6)));
      float floatValue7 = this.animation.update(this.module.isEnabled() ? 1.0F : 0.0F);
      if (floatValue7 > 0.01F) {
         Draw2D.m_0(floatValue3, floatValue4, floatValue5, 26.0F, 8.0F, 0.0F, ColorUtil.withAlpha(value, (int)(175.0F * floatValue7 * floatValue6)), ColorUtil.withAlpha(value2, (int)(175.0F * floatValue7 * floatValue6)));
      }

      Draw2D.m_4(floatValue3, floatValue4, floatValue5, 26.0F, 0.5F, 8.0F, ColorUtil.withAlpha(ColorUtil.rgba(190, 190, 190, 222), (int)(60.0F * floatValue6)));
      boolean flag2 = !this.module.getSettings().isEmpty();
      float floatValue8 = flag2 ? Draw2D.getTextWidth("\ue90c", Fonts.SUPPLIER7, 14.0F) : 0.0F;
      float floatValue9 = flag2 ? Draw2D.getTextWidth("\ue90c", Fonts.SUPPLIER7, 17.0F) : 0.0F;
      int value3 = this.module.getKey();
      float floatValue10 = floatValue3 + floatValue5 - 1.0F - floatValue9;
      String text = null;
      if (!this.flag && value3 != -1) {
         text = "[" + KeyNames.getKeyName(value3) + "]";
         floatValue10 -= 4.0F + Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F);
      }

      String text2 = this.flag ? "Binding..." : this.module.getName();
      float floatValue11 = floatValue3 + 6.0F;
      float floatValue12 = Math.max(0.0F, floatValue10 - floatValue11 - 4.0F);
      Draw2D.drawTextSoftShadow(
         text2,
         Fonts.SUPPLIER16,
         floatValue11,
         floatValue4 + 6.0F,
         ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), (int)((130.0F + 125.0F * floatValue7) * floatValue6)),
         0.8F,
         1.0F,
         floatValue12 + 7.0F,
         13.0F
      );
      if (text != null) {
         Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER16, floatValue10, floatValue4 + 7.0F, ColorUtil.withAlpha(ColorUtil.rgba(180, 180, 180, 255), (int)((130.0F + 125.0F * floatValue7) * floatValue6)), 11.0F);
      }

      if (flag2) {
         this.floatValue = floatValue3 + floatValue5 - 8.0F - floatValue8;
         this.floatValue2 = floatValue3 + floatValue5 - 8.0F;
         Draw2D.drawTextWithShadow("\ue90c", Fonts.SUPPLIER7, this.floatValue, floatValue4 + 6.0F, ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), (int)((110.0F + 125.0F * floatValue7) * floatValue6)), 16.0F);
      }
   }

   public boolean isHovered(float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      return floatValue3 >= floatValue5 && floatValue3 <= floatValue5 + floatValue7 && floatValue4 >= floatValue6 && floatValue4 <= floatValue6 + 26.0F;
   }

   public boolean m_2_boolean(float floatValue3, float floatValue4, float floatValue5) {
      return this.module.getSettings().isEmpty() ? false : floatValue3 >= this.floatValue && floatValue3 <= this.floatValue2 && floatValue4 >= floatValue5 && floatValue4 <= floatValue5 + 26.0F;
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
