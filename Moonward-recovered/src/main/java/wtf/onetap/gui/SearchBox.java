package wtf.onetap.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;
import wtf.onetap.util.KeyNames;

public class SearchBox {
   private final Animation animation;
   private final Animation animation2;
   private final Animation animation3;
   private final Animation animation4;
   private final Animation animation5;
   private final Animation animation6;
   private String text;
   private String text2;
   private int value;
   private int value2;
   private int value3;
   private int value4;
   private boolean flag;
   private boolean flag2;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;

   public SearchBox() {
      this.animation = new Animation(180L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(150L, 1.0F, Easing.EASING8);
      this.animation3 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation4 = new Animation(120L, 1.0F, Easing.EASING8);
      this.animation5 = new Animation(120L, 0.0F, Easing.EASING8);
      this.animation6 = new Animation(120L, 0.0F, Easing.EASING8);
      this.text = "";
      this.text2 = "";
   }

   public String getText() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void m_5(float floatValue6, float floatValue7, float floatValue8, float floatValue9) {
      this.floatValue = floatValue6;
      this.floatValue3 = floatValue7;
      this.floatValue2 = (floatValue8 - 26.0F) / 2.0F;
      this.m_2_void(this.animation.update(this.flag ? 1.0F : 0.0F), floatValue9);
      float floatValue10 = 12.0F;
      float floatValue11 = this.floatValue + 10.0F;
      float floatValue12 = Draw2D.getCenteredY("\uf002", Fonts.SUPPLIER8, floatValue10, this.floatValue2, 26.0F);
      int value5 = this.flag ? 225 : 150;
      Draw2D.drawStyledText("\uf002", Fonts.SUPPLIER8, floatValue11, floatValue12, ColorUtil.rgba(value5, value5, value5, (int)(220.0F * floatValue9)), floatValue10);
      float floatValue13 = floatValue11 + Draw2D.getTextWidth("\uf002", Fonts.SUPPLIER8, floatValue10) + 6.0F;
      float floatValue14 = Draw2D.getTextBaseline(Fonts.SUPPLIER16, 13.0F, this.floatValue2, 26.0F);
      float floatValue15 = this.animation2.update(this.text.isEmpty() && !this.flag ? 1.0F : 0.0F);
      float floatValue16 = this.animation3.update(this.text.isEmpty() ? 0.0F : 1.0F);
      float floatValue17 = this.m_7_float();
      m_6_void(floatValue13, floatValue14, floatValue15, floatValue9);
      this.m_2_void(floatValue9, !this.text.isEmpty() && this.m_2_boolean(getMouseX(), getMouseY()));
      if (this.text.isEmpty()) {
         if (this.isCaretVisible()) {
            this.m_3_void(floatValue13, floatValue9);
         }
      } else {
         this.m_5(floatValue13, floatValue14, floatValue9, floatValue16, floatValue17);
      }
   }

   private void m_2_void(float floatValue6, float floatValue7) {
      Draw2D.roundedRect(this.floatValue, this.floatValue2, this.floatValue3, 26.0F, 8.0F, ColorUtil.rgba(0, 0, 0, (int)(40.0F * floatValue7)));
      Draw2D.m_4(this.floatValue, this.floatValue2, this.floatValue3, 26.0F, 0.5F, 8.0F, ColorUtil.rgba(190, 190, 190, (int)((60.0F + 40.0F * floatValue6) * floatValue7)));
   }

   private static void m_6_void(float floatValue6, float floatValue7, float floatValue8, float floatValue9) {
      if (!(floatValue8 <= 0.01F)) {
         Draw2D.drawStyledText("Поиск...", Fonts.SUPPLIER16, floatValue6, floatValue7, ColorUtil.rgba(150, 150, 150, (int)(180.0F * floatValue8 * floatValue9)), 13.0F);
      }
   }

   private void m_2_void(float floatValue6, boolean flag3) {
      float floatValue7 = this.animation6.update(this.text.isEmpty() ? 0.0F : 1.0F);
      if (!(floatValue7 <= 0.01F)) {
         float floatValue8 = this.animation5.update(flag3 ? 1.0F : 0.0F);
         float floatValue9 = 11.0F + floatValue8 * 2.0F;
         int value5 = (int)(160.0F + 65.0F * floatValue8);
         float floatValue10 = this.floatValue + this.floatValue3 - 10.0F - floatValue9;
         float floatValue11 = Draw2D.getCenteredY("\uf00d", Fonts.SUPPLIER8, floatValue9, this.floatValue2, 26.0F);
         Draw2D.drawStyledText("\uf00d", Fonts.SUPPLIER8, floatValue10, floatValue11, ColorUtil.rgba(value5, value5, value5, (int)(200.0F * floatValue7 * floatValue6)), floatValue9);
      }
   }

   private void m_5(float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10) {
      ColorPicker.m_0_StringArr();
      float floatValue11 = this.floatValue + this.floatValue3 - 10.0F - 16.0F - floatValue6;
      float floatValue12 = Draw2D.getTextWidth(this.text, Fonts.SUPPLIER16, 13.0F);
      if (this.flag2) {
         if (KeyNames.isLeftMouseDown()) {
            this.value2 = this.m_0_int(getMouseX());
            this.value4 = this.value2;
         } else {
            this.flag2 = false;
         }
      }

      float floatValue13 = Draw2D.getTextWidth(this.text.substring(0, this.value2), Fonts.SUPPLIER16, 13.0F);
      float floatValue14 = 0.0F;
      if (floatValue12 > floatValue11) {
         floatValue14 = Math.max(0.0F, Math.min(floatValue12 - floatValue11, floatValue13 - floatValue11 * 0.5F));
      }

      this.floatValue4 = floatValue6;
      this.floatValue5 = floatValue14;
      Render2D.pushScissor(floatValue6, this.floatValue2, floatValue11, 26.0F);
      if (this.flag && this.hasSelection()) {
         int value5 = Math.min(this.value3, this.value4);
         int value6 = Math.max(this.value3, this.value4);
         float floatValue15 = Draw2D.getTextWidth(this.text.substring(0, value5), Fonts.SUPPLIER16, 13.0F);
         float floatValue16 = Draw2D.getTextWidth(this.text.substring(value5, value6), Fonts.SUPPLIER16, 13.0F);
         Draw2D.roundedRect(floatValue6 + floatValue15 - floatValue14, this.m_5_float(), floatValue16, 15.0F, 2.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(120.0F * floatValue8)));
      }

      int value7 = (int)(235.0F * floatValue9 * floatValue8);
      if (value7 > 0) {
         if (this.value > 0 && this.value < this.text.length()) {
            String text3 = this.text.substring(0, this.value);
            Draw2D.drawStyledText(text3, Fonts.SUPPLIER16, floatValue6 - floatValue14, floatValue7, ColorUtil.rgba(222, 222, 222, value7), 13.0F);
            int value8 = (int)(value7 * floatValue10);
            if (value8 > 0) {
               float floatValue17 = floatValue6 + Draw2D.getTextWidth(text3, Fonts.SUPPLIER16, 13.0F) - floatValue14;
               Draw2D.drawStyledText(this.text.substring(this.value), Fonts.SUPPLIER16, floatValue17, floatValue7, ColorUtil.rgba(222, 222, 222, value8), 13.0F);
            }
         } else {
            Draw2D.drawStyledText(this.text, Fonts.SUPPLIER16, floatValue6 - floatValue14, floatValue7, ColorUtil.rgba(222, 222, 222, value7), 13.0F);
         }
      }

      if (this.isCaretVisible()) {
         this.m_3_void(floatValue6 + floatValue13 - floatValue14, floatValue8);
      }

      Render2D.popScissor();
   }

   private float m_5_float() {
      return this.floatValue2 + 5.5F;
   }

   private void m_3_void(float floatValue6, float floatValue7) {
      Draw2D.roundedRect(floatValue6, this.m_5_float(), 1.0F, 15.0F, 0.0F, ColorUtil.rgba(245, 245, 245, (int)(235.0F * floatValue7)));
   }

   private float m_7_float() {
      if (!this.text.equals(this.text2)) {
         if (this.text.startsWith(this.text2) && this.text.length() > this.text2.length()) {
            this.value = this.text2.length();
            this.animation4.setDuration(0.0F);
            this.animation4.setTarget(1.0F);
         } else {
            this.value = this.text.length();
            this.animation4.setDuration(1.0F);
         }

         this.text2 = this.text;
      }

      return this.animation4.get();
   }

   public boolean m_6_boolean(float floatValue6, float floatValue7) {
      if (!(floatValue6 >= this.floatValue) || !(floatValue6 <= this.floatValue + this.floatValue3) || !(floatValue7 >= this.floatValue2) || !(floatValue7 <= this.floatValue2 + 26.0F)) {
         this.flag = false;
         this.flag2 = false;
         return false;
      }

      if (!this.text.isEmpty() && this.m_2_boolean(floatValue6, floatValue7)) {
         this.clear();
         this.flag = false;
         return true;
      }

      boolean flag3 = this.flag && isShiftDown();
      this.flag = true;
      if (!this.text.isEmpty() && !(floatValue6 < this.floatValue4)) {
         this.value2 = this.m_0_int(floatValue6);
         if (!flag3) {
            this.value3 = this.value2;
         }

         this.value4 = this.value2;
         this.flag2 = true;
      } else {
         this.value2 = this.text.length();
         this.value3 = this.value4 = this.value2;
      }

      return true;
   }

   private int m_0_int(float floatValue6) {
      float floatValue7 = floatValue6 - this.floatValue4 + this.floatValue5;
      int value5 = 0;
      float floatValue8 = Float.MAX_VALUE;

      for (int i = 0; i <= this.text.length(); i++) {
         float floatValue9 = Math.abs(Draw2D.getTextWidth(this.text.substring(0, i), Fonts.SUPPLIER16, 13.0F) - floatValue7);
         if (floatValue9 < floatValue8) {
            floatValue8 = floatValue9;
            value5 = i;
         }
      }

      return value5;
   }

   private boolean m_2_boolean(float floatValue6, float floatValue7) {
      float floatValue8 = 13.0F;
      float floatValue9 = this.floatValue + this.floatValue3 - 10.0F - floatValue8;
      return floatValue6 >= floatValue9 - 5.0F && floatValue6 <= floatValue9 + floatValue8 + 5.0F && floatValue7 >= this.floatValue2 && floatValue7 <= this.floatValue2 + 26.0F;
   }

   public void m_1_void() {
      this.flag = false;
      this.flag2 = false;
      this.value3 = this.value4 = 0;
   }

   public void m_3_void(int value5) {
      if (this.flag) {
         char ch = (char)value5;
         if (!Character.isISOControl(ch)) {
            this.m_4_void(String.valueOf(ch));
         }
      }
   }

   public void m_1_void(int value5) {
      if (this.flag) {
         if (value5 != 256 && value5 != 257 && value5 != 335) {
            boolean flag3 = isCtrlDown();
            boolean flag4 = isShiftDown();
            if (value5 == 259) {
               if (this.hasSelection()) {
                  this.m_2_void();
               } else if (flag3) {
                  this.m_4(this.wordLeft(), this.value2, "");
               } else if (this.value2 > 0) {
                  this.m_4(this.value2 - 1, this.value2, "");
               }
            } else if (value5 == 261) {
               if (this.hasSelection()) {
                  this.m_2_void();
               } else if (flag3) {
                  this.m_4(this.value2, this.wordRight(), "");
               } else if (this.value2 < this.text.length()) {
                  this.m_4(this.value2, this.value2 + 1, "");
               }
            } else if (value5 == 263) {
               this.setCaret(flag3 ? this.wordLeft() : Math.max(0, this.value2 - 1), flag4);
            } else if (value5 == 262) {
               this.setCaret(flag3 ? this.wordRight() : Math.min(this.text.length(), this.value2 + 1), flag4);
            } else if (value5 == 268) {
               this.setCaret(0, flag4);
            } else if (value5 == 269) {
               this.setCaret(this.text.length(), flag4);
            } else if (flag3 && value5 == 65) {
               this.value3 = 0;
               this.value4 = this.text.length();
               this.value2 = this.value4;
            } else if (flag3 && value5 == 67 && this.hasSelection()) {
               Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            } else if (flag3 && value5 == 88 && this.hasSelection()) {
               Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
               this.m_2_void();
            } else {
               if (flag3 && value5 == 86) {
                  String text3 = Minecraft.getInstance().keyboardHandler.getClipboard();
                  if (text3 != null) {
                     this.m_4_void(m_1_String(text3));
                  }
               }
            }
         } else {
            this.flag = false;
         }
      }
   }

   private void m_4_void(String text3) {
      if (text3 != null && !text3.isEmpty()) {
         int value5 = 40 - (this.text.length() - (this.hasSelection() ? Math.abs(this.value4 - this.value3) : 0));
         if (value5 > 0) {
            if (text3.length() > value5) {
               text3 = text3.substring(0, value5);
            }

            if (this.hasSelection()) {
               this.m_2_void();
            }

            this.text = this.text.substring(0, this.value2) + text3 + this.text.substring(this.value2);
            this.value2 = this.value2 + text3.length();
            this.value3 = this.value4 = this.value2;
         }
      }
   }

   private void m_2_void() {
      int value5 = Math.min(this.value3, this.value4);
      int value6 = Math.max(this.value3, this.value4);
      this.text = this.text.substring(0, value5) + this.text.substring(value6);
      this.value2 = value5;
      this.value3 = this.value4 = this.value2;
   }

   private void m_4(int value5, int value6, String text3) {
      value5 = clampIndex(value5, this.text.length());
      value6 = clampIndex(value6, this.text.length());
      this.text = this.text.substring(0, value5) + text3 + this.text.substring(value6);
      this.value2 = value5 + text3.length();
      this.value3 = this.value4 = this.value2;
   }

   private void setCaret(int value5, boolean flag3) {
      this.value2 = value5;
      if (!flag3) {
         this.value3 = value5;
      }

      this.value4 = value5;
   }

   private boolean hasSelection() {
      return this.value3 != this.value4;
   }

   private String getSelectedText() {
      int value5 = Math.min(this.value3, this.value4);
      int value6 = Math.max(this.value3, this.value4);
      return this.text.substring(value5, value6);
   }

   public void clear() {
      this.text = "";
      this.value2 = 0;
      this.value3 = this.value4 = 0;
      this.flag2 = false;
   }

   private int wordLeft() {
      int value5 = this.value2;

      while (value5 > 0 && Character.isWhitespace(this.text.charAt(value5 - 1))) {
         value5--;
      }

      while (value5 > 0 && !isAllowedChar(this.text.charAt(value5 - 1))) {
         value5--;
      }

      while (value5 > 0 && isAllowedChar(this.text.charAt(value5 - 1))) {
         value5--;
      }

      return value5;
   }

   private int wordRight() {
      int value5 = this.value2;

      while (value5 < this.text.length() && Character.isWhitespace(this.text.charAt(value5))) {
         value5++;
      }

      while (value5 < this.text.length() && !isAllowedChar(this.text.charAt(value5))) {
         value5++;
      }

      while (value5 < this.text.length() && isAllowedChar(this.text.charAt(value5))) {
         value5++;
      }

      return value5;
   }

   private static boolean isAllowedChar(char ch) {
      return Character.isLetterOrDigit(ch) || ch == '_' || ch == ':' || ch == '.';
   }

   private static int clampIndex(int value5, int value6) {
      if (value5 < 0) {
         return 0;
      } else {
         return value5 > value6 ? value6 : value5;
      }
   }

   private boolean isCaretVisible() {
      return this.flag && System.currentTimeMillis() / 500L % 2L == 0L;
   }

   private static float getMouseX() {
      Minecraft mc = Minecraft.getInstance();
      return (float)(mc.mouseHandler.xpos() / mc.getWindow().getGuiScale() - Render2D.getOffsetX()) / Render2D.getScale();
   }

   private static float getMouseY() {
      Minecraft mc = Minecraft.getInstance();
      return (float)(mc.mouseHandler.ypos() / mc.getWindow().getGuiScale() - Render2D.getOffsetY()) / Render2D.getScale();
   }

   private static String m_1_String(String text3) {
      StringBuilder stringBuilder = new StringBuilder(text3.length());

      for (int i = 0; i < text3.length() && stringBuilder.length() < 40; i++) {
         char ch = text3.charAt(i);
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

   private static String a(byte[] byteArray) {
      int value5 = 0;
      int value6;
      char[] charArray = new char[value6 = byteArray.length];

      for (int i = 0; i < value6; i++) {
         int value7;
         if ((value7 = 255 & byteArray[i]) < 192) {
            charArray[value5++] = (char)value7;
         } else if (value7 < 224) {
            char ch = (char)((char)(value7 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value5++] = ch;
         } else if (i < value6 - 2) {
            char ch2 = (char)((char)(value7 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value5++] = ch2;
         }
      }

      return new String(charArray, 0, value5);
   }
}
