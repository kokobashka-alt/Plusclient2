package wtf.onetap.gui;

import com.mojang.blaze3d.textures.GpuTextureView;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.module.Category;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.platform.C51;
import wtf.onetap.platform.C7a;
import wtf.onetap.render.AvatarTexture;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;

public class SidebarPanel {
   private static final Category[] CATEGORY_ARRAY = Category.values();
   private int value;
   private SidebarPanel.ModeKind c7;
   private SidebarPanel.ModeKind c72;
   private final Animation animation;
   private final Animation animation2;

   public SidebarPanel() {
      this.c7 = SidebarPanel.ModeKind.MODULES;
      this.c72 = SidebarPanel.ModeKind.MODULES;
      this.animation = new Animation(150L, 0.0F, Easing.EASING8);
      this.animation2 = new Animation(150L, 1.0F, Easing.EASING8);
   }

   public int getValue() {
      return this.value;
   }

   public SidebarPanel.ModeKind getC7() {
      return this.c7;
   }

   public void m_3_void() {
      if (this.c7 == SidebarPanel.ModeKind.MODELS) {
         this.c7 = this.c72;
      } else {
         this.c72 = this.c7;
         this.c7 = SidebarPanel.ModeKind.MODELS;
      }
   }

   public boolean m_7(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      for (int i = 0; i < CATEGORY_ARRAY.length; i++) {
         float floatValue6 = floatValue4 + 4.0F + i * 25;
         if (floatValue >= floatValue3 + 4.0F && floatValue <= floatValue3 + 4.0F + 120.0F && floatValue2 >= floatValue6 && floatValue2 <= floatValue6 + 24.0F) {
            if (i == this.value && this.c7 == SidebarPanel.ModeKind.MODULES) {
               return false;
            }

            this.value = i;
            this.m_5_void(floatValue4 + 4.0F + i * 25);
            this.c7 = SidebarPanel.ModeKind.MODULES;
            return true;
         }
      }

      float floatValue7 = floatValue5 - 37.0F;
      if (!(floatValue >= floatValue3 + 4.0F) || !(floatValue <= floatValue3 + 4.0F + 120.0F) || !(floatValue2 >= floatValue7 + 4.0F) || !(floatValue2 <= floatValue7 + 4.0F + 24.0F)) {
         return false;
      }

      if (this.c7 == SidebarPanel.ModeKind.THEMES) {
         return false;
      }

      this.m_5_void(floatValue7 + 4.0F);
      this.c7 = SidebarPanel.ModeKind.THEMES;
      return true;
   }

   private void m_5_void(float floatValue) {
      if (this.c7 == SidebarPanel.ModeKind.MODELS) {
         this.animation.setDuration(floatValue);
      } else {
         this.animation.setTarget(floatValue);
      }
   }

   public void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value2, int value3) {
      float floatValue6 = CATEGORY_ARRAY.length * 25 - 2 + 8;
      float floatValue7 = floatValue3 - 37.0F;
      Draw2D.roundedRect(floatValue, floatValue2, 128.0F, floatValue6, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue4)));
      Draw2D.roundedRect(floatValue, floatValue7, 128.0F, 32.0F, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue4)));
      ClickGUI.m_3_String();
      Draw2D.roundedRect(floatValue, floatValue3, 128.0F, 48.0F, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue4)));
      float floatValue8 = floatValue + 4.0F;
      float floatValue9 = floatValue3 + 8.0F;
      float floatValue10 = 32.0F;
      ColorUtil.lerpColor(value2, ColorUtil.primaryColor(), floatValue5);
      int value4 = (int)(200.0F * floatValue4);
      int value5 = (int)(222.0F * floatValue4);
      int value6 = ColorUtil.rgba(255, 255, 255, value5);
      int value7 = ColorUtil.rgba(160, 160, 160, value5);
      String text = C7a.m_3_String();
      GpuTextureView gpuTextureView = AvatarTexture.getTextureView();
      Draw2D.m_4(floatValue8, floatValue9, floatValue10, floatValue10, 1.0F, floatValue10 / 2.0F, ColorUtil.withAlpha(ColorUtil.rgba(222, 222, 222, 255), value4));
      if (gpuTextureView != null) {
         int value8 = (int)(255.0F * floatValue4);
         Draw2D.m_4(floatValue8, floatValue9, floatValue10, floatValue10, floatValue10 / 2.0F, ColorUtil.rgba(255, 255, 255, value8), gpuTextureView);
      } else {
         String text2 = text != null && !text.isEmpty() ? String.valueOf(Character.toUpperCase(text.charAt(0))) : "?";
         float floatValue11 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 16.0F);
         int value9 = text != null ? value5 : (int)(120.0F * floatValue4);
         Draw2D.drawStyledText(text2, Fonts.SUPPLIER16, floatValue8 + (floatValue10 - floatValue11) / 2.0F, floatValue9 + (floatValue10 - 16.0F) / 2.0F - 0.5F, ColorUtil.rgba(255, 255, 255, value9), 16.0F);
      }

      float floatValue12 = floatValue8 + floatValue10 + 4.0F;
      float floatValue13 = floatValue + 128.0F - floatValue12 - 4.0F;
      if (text != null) {
         if (Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 12.0F) > floatValue13) {
            Draw2D.drawColoredTextRaw(text, Fonts.SUPPLIER16, floatValue12, floatValue3 + 8.0F, value6, 0.8F, 1.0F, floatValue13, 12.0F);
         } else {
            Draw2D.drawStyledText(text, Fonts.SUPPLIER16, floatValue12, floatValue3 + 8.0F, value6, 12.0F);
         }
      } else {
         Draw2D.drawStyledText("Не подключён", Fonts.SUPPLIER16, floatValue12, floatValue3 + 6.0F, value7, 11.5F);
      }

      String text3 = C51.m_1(C7a.m_1_Instant());
      Draw2D.drawStyledText("Истекает: " + text3, Fonts.SUPPLIER13, floatValue12, floatValue3 + 24.0F, text != null ? value7 : ColorUtil.rgba(120, 120, 120, value5), 9.5F);
      floatValue8 = this.animation2.update(this.c7 == SidebarPanel.ModeKind.MODELS ? 0.0F : 1.0F);
      floatValue9 = this.c7 == SidebarPanel.ModeKind.THEMES ? floatValue7 + 4.0F : floatValue2 + 4.0F + this.value * 25;
      if (this.animation.isFinished() && this.c7 != SidebarPanel.ModeKind.MODELS) {
         this.animation.setDuration(floatValue9);
      }

      floatValue10 = this.animation.get();
      int value10 = ColorUtil.lerpColor(value2, ColorUtil.primaryColor(), floatValue5);
      value4 = ColorUtil.lerpColor(value3, ColorUtil.secondaryColor(), floatValue5);
      Draw2D.m_0(floatValue + 4.0F, floatValue10, 120.0F, 24.0F, 5.0F, 0.0F, ColorUtil.withAlpha(value10, (int)(175.0F * floatValue4 * floatValue8)), ColorUtil.withAlpha(value4, (int)(175.0F * floatValue4 * floatValue8)));

      for (int i = 0; i < CATEGORY_ARRAY.length; i++) {
         float floatValue14 = floatValue2 + 4.0F + i * 25;
         float floatValue15 = Math.abs(floatValue10 - floatValue14);
         float floatValue16 = (1.0F - Math.min(1.0F, floatValue15 / 25.0F)) * floatValue8;
         int value11 = (int)(140.0F + 115.0F * floatValue16);
         int value12 = (int)(175.0F + 55.0F * floatValue16);
         int value13 = ColorUtil.rgba(value11, value11, value11, (int)(value12 * floatValue4));
         float floatValue17 = floatValue + 6.0F;
         // Render category: plain "P" from the text font instead of the icon glyph (Divine Visuals style).
         boolean pIcon = CATEGORY_ARRAY[i] == wtf.onetap.module.Category.RENDER;
         String iconText = pIcon ? "P" : CATEGORY_ARRAY[i].getIcon();
         var iconFont = pIcon ? Fonts.SUPPLIER : Fonts.SUPPLIER7;
         float floatValue18 = Draw2D.getTextWidth(iconText, iconFont, 16.0F);
         Draw2D.drawTextWithShadow(iconText, iconFont, floatValue17, floatValue14 - 1.25F + 6.0F, value13, 16.0F);
         Draw2D.drawTextWithShadow(CATEGORY_ARRAY[i].getDisplayName(), Fonts.SUPPLIER16, floatValue17 + floatValue18 + 1.0F, floatValue14 + 6.0F, value13, 12.0F);
      }

      float floatValue19 = floatValue7 + 4.0F;
      float floatValue20 = Math.abs(floatValue10 - floatValue19);
      float floatValue21 = (1.0F - Math.min(1.0F, floatValue20 / 25.0F)) * floatValue8;
      int value14 = (int)(140.0F + 115.0F * floatValue21);
      int value15 = (int)(175.0F + 55.0F * floatValue21);
      int value16 = ColorUtil.rgba(value14, value14, value14, (int)(value15 * floatValue4));
      floatValue13 = floatValue + 6.0F;
      float floatValue22 = Draw2D.getTextWidth("\ue919", Fonts.SUPPLIER7, 16.0F);
      Draw2D.drawTextWithShadow("\ue919", Fonts.SUPPLIER7, floatValue13, floatValue19 - 1.25F + 6.0F, value16, 16.0F);
      Draw2D.drawTextWithShadow("Themes", Fonts.SUPPLIER16, floatValue13 + floatValue22 + 1.0F, floatValue19 + 6.0F, value16, 12.0F);
   }

   public enum ModeKind {
      MODULES,
      THEMES,
      MODELS;

      private static String a(byte[] byteArray) {
         int value2 = 0;
         int value3;
         char[] charArray = new char[value3 = byteArray.length];

         for (int i = 0; i < value3; i++) {
            int value4;
            if ((value4 = 255 & byteArray[i]) < 192) {
               charArray[value2++] = (char)value4;
            } else if (value4 < 224) {
               char ch = (char)((char)(value4 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value2++] = ch;
            } else if (i < value3 - 2) {
               char ch2 = (char)((char)(value4 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value2++] = ch2;
            }
         }

         return new String(charArray, 0, value2);
      }
   }
}
