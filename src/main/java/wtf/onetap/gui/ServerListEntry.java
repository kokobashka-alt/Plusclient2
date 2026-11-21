package wtf.onetap.gui;

import java.util.function.IntPredicate;
import net.minecraft.client.input.KeyEvent;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;

public class ServerListEntry {
   private final Animation animation;
   private final Animation animation2;
   private final Animation animation3;
   private final Animation animation4;
   private int value;
   private String text;
   private String text2;
   private String text3;
   private int value2;
   private int value3;
   private int value4;
   private int value5;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private float floatValue;
   private float floatValue2;

   public ServerListEntry(int value6, String text4) {
      this.text2 = "";
      this.text3 = "";
      this.value = value6;
      this.text = text4;
      this.animation = new Animation(150L, 1.0F, Easing.EASING8);
      this.animation2 = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation3 = new Animation(120L, 1.0F, Easing.EASING8);
      this.animation4 = new Animation(180L, 0.0F, Easing.EASING8);
   }

   public String getText2() {
      return this.text2;
   }

   public boolean isEmpty() {
      return this.text2.isEmpty();
   }

   public void setText(String text4) {
      this.text = text4 == null ? "" : text4;
   }

   public void setMaxLength(int value6) {
      this.value = Math.max(1, value6);
      if (this.text2.length() > this.value) {
         this.text2 = this.text2.substring(0, this.value);
         this.value3 = Math.min(this.value3, this.text2.length());
         this.value4 = Math.min(this.value4, this.text2.length());
         this.value5 = Math.min(this.value5, this.text2.length());
      }
   }

   public int getValue() {
      return this.value;
   }

   public static boolean isValid(String text4) {
      if (text4 == null) {
         return false;
      }

      int value6 = text4.length();
      if (value6 >= 3 && value6 <= 16) {
         for (int i = 0; i < value6; i++) {
            char ch = text4.charAt(i);
            if (ch != '_' && !Character.isLetterOrDigit(ch)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void setFlag2(boolean flag4) {
      this.flag2 = flag4;
   }

   public void setFocused(boolean flag4) {
      if (this.flag != flag4) {
         this.flag = flag4;
         if (flag4) {
            this.value3 = this.text2.length();
            this.value4 = this.value5 = this.value3;
         } else {
            this.value4 = this.value5 = this.value3;
            this.flag3 = false;
         }
      }
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void m_6_void(String text4) {
      this.text2 = text4 == null ? "" : text4;
      if (this.text2.length() > this.value) {
         this.text2 = this.text2.substring(0, this.value);
      }

      this.value3 = this.text2.length();
      this.value4 = this.value5 = this.value3;
      this.flag3 = false;
      this.text3 = this.text2;
      this.value2 = this.text2.length();
      this.animation.setDuration(this.text2.isEmpty() && !this.flag ? 1.0F : 0.0F);
      this.animation2.setDuration(this.text2.isEmpty() ? 0.0F : 1.0F);
      this.animation3.setDuration(1.0F);
      this.animation4.setDuration(0.0F);
   }

   public void setTextLimited(String text4) {
      String text5 = text4 == null ? "" : text4;
      if (text5.length() > this.value) {
         text5 = text5.substring(0, this.value);
      }

      if (!text5.equals(this.text2)) {
         this.text2 = text5;
         this.value3 = this.text2.length();
         this.value4 = this.value5 = this.value3;
      }
   }

   public void clear() {
      if (!this.text2.isEmpty()) {
         this.text2 = "";
         this.value3 = 0;
         this.value4 = this.value5 = 0;
      }
   }

   public boolean insert(String text4, IntPredicate intPredicate) {
      if (text4 != null && !text4.isEmpty()) {
         this.deleteSelection();
         StringBuilder stringBuilder = new StringBuilder();

         for (int i = 0; i < text4.length(); i++) {
            char ch = text4.charAt(i);
            if (intPredicate == null || intPredicate.test(ch)) {
               if (this.text2.length() + stringBuilder.length() >= this.value) {
                  break;
               }

               stringBuilder.append((char)ch);
            }
         }

         if (stringBuilder.isEmpty()) {
            return false;
         }

         this.text2 = this.text2.substring(0, this.value3) + stringBuilder + this.text2.substring(this.value3);
         this.value3 = this.value3 + stringBuilder.length();
         this.value4 = this.value5 = this.value3;
         return true;
      } else {
         return false;
      }
   }

   public boolean backspace() {
      if (this.hasSelection()) {
         this.deleteSelection();
         return true;
      }

      if (this.value3 <= 0) {
         return false;
      }

      this.text2 = this.text2.substring(0, this.value3 - 1) + this.text2.substring(this.value3);
      this.value3--;
      this.value4 = this.value5 = this.value3;
      return true;
   }

   public void selectAll() {
      this.value4 = 0;
      this.value5 = this.text2.length();
      this.value3 = this.value5;
   }

   public boolean hasSelection() {
      return this.value4 != this.value5;
   }

   public String getSelection() {
      if (!this.hasSelection()) {
         return this.text2;
      }

      int value6 = Math.min(this.value4, this.value5);
      int value7 = Math.max(this.value4, this.value5);
      return this.text2.substring(value6, value7);
   }

   public void deleteOrClear() {
      if (this.hasSelection()) {
         this.deleteSelection();
      } else {
         this.clear();
      }
   }

   public void paste(String text4, IntPredicate intPredicate) {
      this.insert(text4 == null ? "" : text4.replace("\r", "").replace("\n", ""), intPredicate);
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void m_5_void(float floatValue3, boolean flag4) {
      int value6 = this.m_5_int(floatValue3);
      if (!flag4) {
         this.value4 = value6;
      }

      this.value5 = value6;
      this.value3 = value6;
      this.flag3 = true;
   }

   public void m_6_void(float floatValue3) {
      if (this.flag3) {
         int value6 = this.m_5_int(floatValue3);
         this.value5 = value6;
         this.value3 = value6;
      }
   }

   public void m_5_void() {
      this.flag3 = false;
   }

   public boolean onKey(KeyEvent event) {
      boolean flag4 = event.hasShiftDown();
      boolean flag5 = event.hasControlDownWithQuirk();
      switch (event.key()) {
         case 259:
            if (this.hasSelection()) {
               this.deleteSelection();
            } else if (flag5) {
               this.select(this.wordLeft(), this.value3);
            } else {
               this.backspace();
            }
            break;
         case 260:
         case 264:
         case 265:
         case 266:
         case 267:
         default:
            return false;
         case 261:
            if (this.hasSelection()) {
               this.deleteSelection();
            } else if (flag5) {
               this.select(this.value3, this.wordRight());
            } else {
               this.select(this.value3, this.value3 + 1);
            }
            break;
         case 262:
            if (this.hasSelection() && !flag4 && !flag5) {
               this.setCaret(Math.max(this.value4, this.value5), false);
            } else {
               this.setCaret(flag5 ? this.wordRight() : Math.min(this.text2.length(), this.value3 + 1), flag4);
            }
            break;
         case 263:
            if (this.hasSelection() && !flag4 && !flag5) {
               this.setCaret(Math.min(this.value4, this.value5), false);
            } else {
               this.setCaret(flag5 ? this.wordLeft() : Math.max(0, this.value3 - 1), flag4);
            }
            break;
         case 268:
            this.setCaret(0, flag4);
            break;
         case 269:
            this.setCaret(this.text2.length(), flag4);
      }

      return true;
   }

   public float m_5_float() {
      return this.animation.update(this.text2.isEmpty() && !this.flag ? 1.0F : 0.0F);
   }

   public void m_3_void(float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      ClickGUI.m_3_String();
      float floatValue8 = this.m_5_float();
      float floatValue9 = this.animation2.update(this.text2.isEmpty() ? 0.0F : 1.0F);
      float floatValue10 = this.m_9_float();
      String text4 = this.getDisplayText();
      String text5 = this.flag2 ? "•".repeat(Math.min(this.value3, text4.length())) : this.text2.substring(0, Math.min(this.value3, this.text2.length()));
      float floatValue11 = Draw2D.getTextBaseline(Fonts.SUPPLIER13, 12.0F, floatValue4, floatValue6);
      float floatValue12 = floatValue5;
      float floatValue13 = Draw2D.getTextWidth(text4, Fonts.SUPPLIER13, 12.0F);
      float floatValue14 = Draw2D.getTextWidth(text5, Fonts.SUPPLIER13, 12.0F);
      float floatValue15 = 0.0F;
      if (floatValue13 > floatValue12) {
         floatValue15 = Math.max(0.0F, Math.min(floatValue13 - floatValue12, floatValue14 - floatValue12 * 0.55F));
      }

      float floatValue16 = this.animation4.update(floatValue15);
      this.floatValue = floatValue3;
      this.floatValue2 = floatValue16;
      float floatValue17 = floatValue3 - floatValue16;
      Render2D.pushScissor(floatValue3, floatValue4, floatValue12, floatValue6);

      try {
         if (floatValue8 > 0.01F && this.text != null && !this.text.isEmpty()) {
            Draw2D.drawStyledText(this.text, Fonts.SUPPLIER13, floatValue3, floatValue11, ColorUtil.rgba(200, 200, 200, (int)(110.0F * floatValue8 * floatValue7)), 12.0F);
         }

         if (this.flag && this.hasSelection() && !text4.isEmpty()) {
            int value6 = Math.min(this.value4, this.value5);
            int value7 = Math.max(this.value4, this.value5);
            if (this.flag2) {
               value6 = Math.min(value6, text4.length());
               value7 = Math.min(value7, text4.length());
            }

            float floatValue18 = Draw2D.getTextWidth(text4.substring(0, value6), Fonts.SUPPLIER13, 12.0F);
            float floatValue19 = Draw2D.getTextWidth(text4.substring(value6, value7), Fonts.SUPPLIER13, 12.0F);
            float floatValue20 = Math.max(16.0F, floatValue6 - 6.0F);
            Draw2D.roundedRect(floatValue17 + floatValue18, floatValue4 + (floatValue6 - floatValue20) / 2.0F, Math.max(floatValue19, 1.0F), floatValue20, 0.0F, ColorUtil.rgba(51, 153, 255, (int)(200.0F * floatValue7)));
         }

         int value8 = (int)(235.0F * floatValue9 * floatValue7);
         if (value8 > 0 && !text4.isEmpty()) {
            if (this.value2 > 0 && this.value2 < text4.length()) {
               String text6 = text4.substring(0, this.value2);
               Draw2D.drawStyledText(text6, Fonts.SUPPLIER13, floatValue17, floatValue11, ColorUtil.rgba(245, 245, 245, value8), 12.0F);
               int value9 = (int)(value8 * floatValue10);
               if (value9 > 0) {
                  float floatValue21 = floatValue17 + Draw2D.getTextWidth(text6, Fonts.SUPPLIER13, 12.0F);
                  Draw2D.drawStyledText(text4.substring(this.value2), Fonts.SUPPLIER13, floatValue21, floatValue11, ColorUtil.rgba(245, 245, 245, value9), 12.0F);
               }
            } else {
               Draw2D.drawStyledText(text4, Fonts.SUPPLIER13, floatValue17, floatValue11, ColorUtil.rgba(245, 245, 245, value8), 12.0F);
            }
         }

         if (this.flag && !this.hasSelection() && System.currentTimeMillis() / 500L % 2L == 0L) {
            Draw2D.roundedRect(floatValue17 + floatValue14, floatValue4 + (floatValue6 - 14.0F) / 2.0F, 1.0F, 14.0F, 0.0F, ColorUtil.rgba(245, 245, 245, (int)(235.0F * floatValue7)));
         }
      } finally {
         Render2D.popScissor();
      }

      if (floatValue13 > floatValue12 - 1.0F || floatValue16 > 0.5F) {
         float floatValue22 = Math.min(28.0F, floatValue12 * 0.22F);
         int value10 = ColorUtil.rgba(22, 22, 22, 0);
         int value11 = ColorUtil.rgba(22, 22, 22, (int)(210.0F * floatValue7));
         Render2D.m_4(floatValue3 + floatValue12 - floatValue22, floatValue4, floatValue22, floatValue6, 0.0F, 0.0F, 0.0F, value10, value10, value11, value11);
      }
   }

   private float m_9_float() {
      String text4 = this.getDisplayText();
      if (!text4.equals(this.text3)) {
         if (text4.startsWith(this.text3) && text4.length() > this.text3.length()) {
            this.value2 = this.text3.length();
            this.animation3.setDuration(0.0F);
            this.animation3.setTarget(1.0F);
         } else {
            this.value2 = text4.length();
            this.animation3.setDuration(1.0F);
         }

         this.text3 = text4;
      }

      return this.animation3.get();
   }

   private void deleteSelection() {
      if (this.hasSelection()) {
         int value6 = Math.min(this.value4, this.value5);
         int value7 = Math.max(this.value4, this.value5);
         this.text2 = this.text2.substring(0, value6) + this.text2.substring(value7);
         this.value3 = value6;
         this.value4 = this.value5 = this.value3;
      }
   }

   private void setCaret(int value6, boolean flag4) {
      this.value3 = value6;
      if (!flag4) {
         this.value4 = value6;
      }

      this.value5 = value6;
   }

   private void select(int value6, int value7) {
      value6 = Math.max(0, value6);
      value7 = Math.min(this.text2.length(), value7);
      if (value6 < value7) {
         this.text2 = this.text2.substring(0, value6) + this.text2.substring(value7);
         this.value3 = value6;
         this.value4 = this.value5 = this.value3;
      }
   }

   private int m_5_int(float floatValue3) {
      String text4 = this.getDisplayText();
      float floatValue4 = floatValue3 - this.floatValue + this.floatValue2;
      int value6 = 0;
      float floatValue5 = Float.MAX_VALUE;

      for (int i = 0; i <= text4.length(); i++) {
         float floatValue6 = Math.abs(Draw2D.getTextWidth(text4.substring(0, i), Fonts.SUPPLIER13, 12.0F) - floatValue4);
         if (floatValue6 < floatValue5) {
            floatValue5 = floatValue6;
            value6 = i;
         }
      }

      return value6;
   }

   private int wordLeft() {
      int value6 = this.value3;

      while (value6 > 0 && Character.isWhitespace(this.text2.charAt(value6 - 1))) {
         value6--;
      }

      while (value6 > 0 && !isAllowedChar(this.text2.charAt(value6 - 1))) {
         value6--;
      }

      while (value6 > 0 && isAllowedChar(this.text2.charAt(value6 - 1))) {
         value6--;
      }

      return value6;
   }

   private int wordRight() {
      int value6 = this.value3;

      while (value6 < this.text2.length() && Character.isWhitespace(this.text2.charAt(value6))) {
         value6++;
      }

      while (value6 < this.text2.length() && !isAllowedChar(this.text2.charAt(value6))) {
         value6++;
      }

      while (value6 < this.text2.length() && isAllowedChar(this.text2.charAt(value6))) {
         value6++;
      }

      return value6;
   }

   private static boolean isAllowedChar(char ch) {
      return Character.isLetterOrDigit(ch) || ch == '_' || ch == ':' || ch == '.';
   }

   private String getDisplayText() {
      return this.flag2 && !this.text2.isEmpty() ? "•".repeat(this.text2.length()) : this.text2;
   }
}
