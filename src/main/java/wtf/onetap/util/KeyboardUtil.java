package wtf.onetap.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.gui.ColorPicker;
import wtf.onetap.gui.setting.ValueLabelRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.setting.TextSetting;

public class KeyboardUtil {
   private final TextSetting textSetting;
   private final ValueLabelRenderer valueLabelRenderer;
   private final Animation animation;
   private final Animation animation2;
   private final Animation animation3;
   private boolean flag;
   private boolean flag2;
   private int value;
   private int value2;
   private int value3;
   private boolean flag3;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;

   public KeyboardUtil(TextSetting textSetting2) {
      this.valueLabelRenderer = new ValueLabelRenderer();
      this.animation = new Animation(180L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(160L, 0.0F, Easing.EASING8);
      this.animation3 = new Animation(120L, 0.0F, Easing.EASING8);
      this.textSetting = textSetting2;
      this.value = ((String)textSetting2.getValue()).length();
      this.animation2.setDuration(getTextWidth((String)textSetting2.getValue()));
      if (((String)textSetting2.getValue()).isEmpty()) {
         this.animation2.setDuration(getTextWidth("0"));
      }
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public void clear() {
      this.flag2 = false;
      this.flag3 = false;
      this.value2 = this.value3 = 0;
   }

   public void m_7_void(float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      ColorPicker.m_0_StringArr();
      int value4 = (int)(200.0F * floatValue7);
      int value5 = (int)(255.0F * floatValue7);
      int value6 = ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), value4);
      String text = (String)this.textSetting.getValue();
      String text2 = text.isEmpty() ? (this.flag2 ? "" : "0") : null;
      boolean flag4 = this.flag2 && System.currentTimeMillis() / 500L % 2L == 0L;
      String text3 = text.isEmpty() && text2 != null ? text2 : text;
      float floatValue8 = getTextWidth(text3) + 16.0F;
      float floatValue9 = floatValue8;
      if (text.length() > 10) {
         floatValue9 = Math.min(floatValue9, getTextWidth(text.substring(0, 10)) + 16.0F);
      }

      float floatValue10 = Math.max(48.0F, this.animation2.update(Math.max(48.0F, floatValue9)));
      float floatValue11 = floatValue4 + floatValue6 - 6.0F - floatValue10;
      float floatValue12 = floatValue5 + 4.0F;
      float floatValue13 = floatValue4 + 6.0F;
      float floatValue14 = Math.max(0.0F, floatValue11 - floatValue13 - 4.0F);
      this.valueLabelRenderer.m_7(this.textSetting.getName(), floatValue13, floatValue5 + 5.0F, value6, floatValue14, 12.0F, 0.8F, floatValue4, floatValue5, floatValue6, 24.0F);
      float floatValue15 = this.animation.update(this.flag2 ? 1.0F : 0.0F);
      int value7 = ColorUtil.rgba(60, 60, 60, 180);
      int value8 = ColorUtil.rgba(35, 35, 35, 180);
      int value9 = ColorUtil.lerpColor(value7, ColorUtil.primaryColor(), floatValue15);
      int value10 = ColorUtil.lerpColor(value8, ColorUtil.secondaryColor(), floatValue15);
      Draw2D.m_8(floatValue11, floatValue12, floatValue10, 16.0F, 4.0F, 0.0F, ColorUtil.withAlpha(value9, (int)(230.0F * floatValue7)), ColorUtil.withAlpha(value10, (int)(230.0F * floatValue7)));
      float floatValue16 = floatValue11 + 8.0F;
      float floatValue17 = floatValue12 + 2.5F;
      float floatValue18 = floatValue10 - 16.0F;
      if (text.isEmpty() && text2 != null) {
         this.animation3.update(0.0F);
         float floatValue19 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 11.0F);
         int value11 = ColorUtil.withAlpha(ColorUtil.rgba(150, 150, 150, 255), value4);
         Draw2D.drawTextWithShadow(text2, Fonts.SUPPLIER16, floatValue16 + (floatValue18 - floatValue19) / 2.0F, floatValue17, value11, 11.0F);
      } else {
         if (this.flag3) {
            if (KeyNames.isLeftMouseDown()) {
               this.value = this.getIndexAt(getMouseX());
               this.value3 = this.value;
            } else {
               this.flag3 = false;
            }
         }

         int value12 = Math.min(this.value, text.length());
         float floatValue20 = Draw2D.getTextWidth(text.substring(0, value12), Fonts.SUPPLIER16, 11.0F);
         float floatValue21 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F);
         float floatValue22 = Math.max(0.0F, floatValue21 - floatValue18);
         float floatValue23 = floatValue22 > 0.0F ? Math.min(floatValue22, Math.max(0.0F, floatValue20 - floatValue18 + 2.0F)) : 0.0F;
         if (!this.flag) {
            this.animation3.setDuration(floatValue23);
            this.flag = true;
         }

         float floatValue24 = this.animation3.update(floatValue23);
         this.floatValue = floatValue11;
         this.floatValue2 = floatValue16;
         this.floatValue3 = floatValue24;
         Render2D.pushScissor(floatValue11, floatValue12, floatValue10, 16.0F);
         int value13 = Math.min(this.value2, this.value3);
         int value14 = Math.max(this.value2, this.value3);
         if (this.flag2 && value14 > value13) {
            float floatValue25 = Draw2D.getTextWidth(text.substring(0, value13), Fonts.SUPPLIER16, 11.0F);
            float floatValue26 = Draw2D.getTextWidth(text.substring(value13, value14), Fonts.SUPPLIER16, 11.0F);
            int value15 = ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(120.0F * floatValue7));
            Draw2D.roundedRect(floatValue16 + floatValue25 - floatValue24, floatValue17 - 1.0F, floatValue26, 13.0F, 2.0F, value15);
         }

         int value16 = ColorUtil.lerpColor(value6, ColorUtil.withAlpha(ColorUtil.rgba(245, 245, 245, 255), value5), floatValue15);
         Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER16, floatValue16 - floatValue24, floatValue17, value16, 11.0F);
         if (this.flag2 && flag4) {
            int value17 = ColorUtil.withAlpha(ColorUtil.rgba(245, 245, 245, 255), value5);
            Draw2D.roundedRect(floatValue16 + floatValue20 - floatValue24, floatValue17 - 1.0F, 1.0F, 13.0F, 0.0F, value17);
         }

         Render2D.popScissor();
      }
   }

   public boolean m_6(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      if (floatValue4 >= floatValue6 && floatValue4 <= floatValue6 + floatValue8 && floatValue5 >= floatValue7 && floatValue5 <= floatValue7 + 24.0F) {
         this.flag2 = true;
         if (floatValue4 >= this.floatValue && !((String)this.textSetting.getValue()).isEmpty()) {
            this.value = this.getIndexAt(floatValue4);
            this.flag3 = true;
         } else {
            this.value = ((String)this.textSetting.getValue()).length();
         }

         this.value2 = this.value3 = this.value;
         return true;
      } else {
         this.flag2 = false;
         return false;
      }
   }

   private int getIndexAt(float floatValue4) {
      String text = (String)this.textSetting.getValue();
      float floatValue5 = floatValue4 - this.floatValue2 + this.floatValue3;
      int value4 = 0;
      float floatValue6 = Float.MAX_VALUE;

      for (int i = 0; i <= text.length(); i++) {
         float floatValue7 = Math.abs(Draw2D.getTextWidth(text.substring(0, i), Fonts.SUPPLIER16, 11.0F) - floatValue5);
         if (floatValue7 < floatValue6) {
            floatValue6 = floatValue7;
            value4 = i;
         }
      }

      return value4;
   }

   private static float getMouseX() {
      Minecraft mc = Minecraft.getInstance();
      return (float)(mc.mouseHandler.xpos() / mc.getWindow().getGuiScale() - Render2D.getOffsetX()) / Render2D.getScale();
   }

   public void typeChar(int value4) {
      if (this.flag2) {
         char ch = (char)value4;
         if (!Character.isISOControl(ch)) {
            this.insertText(String.valueOf(ch));
         }
      }
   }

   public boolean onKeyPressed(int value4) {
      if (!this.flag2) {
         return false;
      }

      if (value4 == 256) {
         this.flag2 = false;
         return true;
      }

      if (value4 != 257 && value4 != 335) {
         boolean flag4 = isCtrlDown();
         boolean flag5 = isShiftDown();
         if (value4 == 259) {
            if (this.hasSelection()) {
               this.deleteSelection();
            } else if (flag4) {
               int value5 = this.wordLeftIndex();
               this.replaceRange(value5, this.value, "");
            } else if (this.value > 0) {
               this.replaceRange(this.value - 1, this.value, "");
            }

            return true;
         } else if (value4 == 261) {
            if (this.hasSelection()) {
               this.deleteSelection();
            } else if (flag4) {
               int value6 = this.wordRightIndex();
               this.replaceRange(this.value, value6, "");
            } else if (this.value < ((String)this.textSetting.getValue()).length()) {
               this.replaceRange(this.value, this.value + 1, "");
            }

            return true;
         } else if (value4 == 263) {
            this.value = flag4 ? this.wordLeftIndex() : Math.max(0, this.value - 1);
            if (!flag5) {
               this.value2 = this.value;
            }

            this.value3 = this.value;
            return true;
         } else if (value4 == 262) {
            this.value = flag4 ? this.wordRightIndex() : Math.min(((String)this.textSetting.getValue()).length(), this.value + 1);
            if (!flag5) {
               this.value2 = this.value;
            }

            this.value3 = this.value;
            return true;
         } else if (value4 == 268) {
            this.value = 0;
            if (!flag5) {
               this.value2 = 0;
            }

            this.value3 = 0;
            return true;
         } else if (value4 == 269) {
            this.value = ((String)this.textSetting.getValue()).length();
            if (!flag5) {
               this.value2 = this.value;
            }

            this.value3 = this.value;
            return true;
         } else {
            if (flag4 && value4 == 65) {
               this.value2 = 0;
               this.value3 = ((String)this.textSetting.getValue()).length();
               this.value = this.value3;
               return true;
            }

            if (flag4 && value4 == 67 && this.hasSelection()) {
               Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
               return true;
            }

            if (flag4 && value4 == 88 && this.hasSelection()) {
               Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
               this.deleteSelection();
               return true;
            }

            if (flag4 && value4 == 86) {
               String text = Minecraft.getInstance().keyboardHandler.getClipboard();
               if (text != null) {
                  this.insertText(sanitize(text));
               }

               return true;
            } else {
               return true;
            }
         }
      } else {
         this.flag2 = false;
         return true;
      }
   }

   private void insertText(String text) {
      if (text != null && !text.isEmpty()) {
         if (((String)this.textSetting.getValue()).length() + text.length() > 64) {
            int value4 = 64 - ((String)this.textSetting.getValue()).length();
            if (value4 <= 0) {
               return;
            }

            text = text.substring(0, value4);
         }

         if (this.hasSelection()) {
            this.replaceRange(Math.min(this.value2, this.value3), Math.max(this.value2, this.value3), text);
         } else {
            String text2 = (String)this.textSetting.getValue();
            String text3 = text2.substring(0, this.value) + text + text2.substring(this.value);
            this.textSetting.setValue(text3);
            this.value = this.value + text.length();
            this.value2 = this.value3 = this.value;
         }
      }
   }

   private void deleteSelection() {
      int value4 = Math.min(this.value2, this.value3);
      int value5 = Math.max(this.value2, this.value3);
      this.replaceRange(value4, value5, "");
   }

   private void replaceRange(int value4, int value5, String text) {
      String text2 = (String)this.textSetting.getValue();
      value4 = clampIndex(value4, text2.length());
      value5 = clampIndex(value5, text2.length());
      String text3 = text2.substring(0, value4) + text + text2.substring(value5);
      this.textSetting.setValue(text3);
      this.value = value4 + text.length();
      this.value2 = this.value3 = this.value;
   }

   private boolean hasSelection() {
      return this.value2 != this.value3;
   }

   private String getSelectedText() {
      int value4 = Math.min(this.value2, this.value3);
      int value5 = Math.max(this.value2, this.value3);
      return ((String)this.textSetting.getValue()).substring(value4, value5);
   }

   private int wordLeftIndex() {
      String text = (String)this.textSetting.getValue();
      int value4 = this.value;

      while (value4 > 0 && Character.isWhitespace(text.charAt(value4 - 1))) {
         value4--;
      }

      while (value4 > 0 && !isAllowedChar(text.charAt(value4 - 1))) {
         value4--;
      }

      while (value4 > 0 && isAllowedChar(text.charAt(value4 - 1))) {
         value4--;
      }

      return value4;
   }

   private int wordRightIndex() {
      String text = (String)this.textSetting.getValue();
      int value4 = this.value;

      while (value4 < text.length() && Character.isWhitespace(text.charAt(value4))) {
         value4++;
      }

      while (value4 < text.length() && !isAllowedChar(text.charAt(value4))) {
         value4++;
      }

      while (value4 < text.length() && isAllowedChar(text.charAt(value4))) {
         value4++;
      }

      return value4;
   }

   private static boolean isAllowedChar(char ch) {
      return Character.isLetterOrDigit(ch) || ch == '_' || ch == ':' || ch == '.';
   }

   private static int clampIndex(int value4, int value5) {
      if (value4 < 0) {
         return 0;
      } else {
         return value4 > value5 ? value5 : value4;
      }
   }

   private static String sanitize(String text) {
      StringBuilder stringBuilder = new StringBuilder(text.length());

      for (int i = 0; i < text.length() && stringBuilder.length() < 64; i++) {
         char ch = text.charAt(i);
         if (ch != '\n' && ch != '\r' && !Character.isISOControl(ch)) {
            stringBuilder.append(ch);
         }
      }

      return stringBuilder.toString();
   }

   private static boolean isCtrlDown() {
      Window window = Minecraft.getInstance().getWindow();
      return InputConstants.isKeyDown(window, 341) || InputConstants.isKeyDown(window, 345);
   }

   private static boolean isShiftDown() {
      Window window = Minecraft.getInstance().getWindow();
      return InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
   }

   private static float getTextWidth(String text) {
      return Draw2D.getTextWidth(text != null && !text.isEmpty() ? text : "0", Fonts.SUPPLIER16, 11.0F);
   }

   public void m_5_void() {
   }
}
