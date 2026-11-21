package wtf.onetap.gui;

import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.FontAtlas;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.ItemRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;

public class ItemRow {
   public static final int VALUE = ColorUtil.rgba(222, 222, 222, 255);
   public static final int VALUE2 = ColorUtil.rgba(150, 150, 150, 255);

   private ItemRow() {
   }

   public static void m_4(String text, String text2, String text3, float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      Draw2D.drawPanel(0.0F, 0.0F, floatValue2, floatValue3, floatValue4);
      m_6(text, text2, text3, floatValue, floatValue3, floatValue4);
   }

   public static void m_6(String text, String text2, String text3, float floatValue, float floatValue2, float floatValue3) {
      int value = (int)(255.0F * floatValue3);
      float floatValue4 = 26.0F;
      float floatValue5 = (floatValue2 - floatValue4) / 2.0F;
      Draw2D.m_8(floatValue + 0.5F, floatValue5 + 1.0F, floatValue4 - 1.0F, floatValue4 - 2.0F, 6.0F, 0.5F, ColorUtil.withAlpha(ColorUtil.primaryColor(), value), ColorUtil.withAlpha(ColorUtil.secondaryColor(), value));
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER8, Draw2D.getCenteredX(text, Fonts.SUPPLIER8, 12.0F, floatValue, floatValue4), Draw2D.getCenteredY(text, Fonts.SUPPLIER8, 12.0F, floatValue5, floatValue4), ColorUtil.rgba(255, 255, 255, value), 12.0F);
      float floatValue6 = floatValue + floatValue4 + 8.0F;
      Draw2D.drawStyledText(text2, Fonts.SUPPLIER18, floatValue6, Draw2D.getTextBaseline(Fonts.SUPPLIER18, 14.0F, floatValue5, 15.0F), ColorUtil.rgba(240, 240, 240, value), 14.0F);
      Draw2D.drawStyledText(text3, Fonts.SUPPLIER15, floatValue6, Draw2D.getTextBaseline(Fonts.SUPPLIER15, 9.5F, floatValue5 + 15.0F, 11.0F), ColorUtil.withAlpha(VALUE2, value), 9.5F);
   }

   public static void m_8(String[] stringArray, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      float floatValue7 = (floatValue2 - 26.0F) / 2.0F;
      float floatValue8 = m_3(stringArray, floatValue, 0) - 2.0F;
      Draw2D.roundedRect(floatValue8, floatValue7, floatValue - floatValue8, 26.0F, 8.0F, ColorUtil.rgba(0, 0, 0, (int)(40.0F * floatValue6)));
      Draw2D.m_4(floatValue8, floatValue7, floatValue - floatValue8, 26.0F, 0.5F, 8.0F, ColorUtil.rgba(190, 190, 190, (int)(60.0F * floatValue6)));
      int value = (int)floatValue3;
      int value2 = Math.min(value + 1, stringArray.length - 1);
      float floatValue9 = floatValue3 - value;
      BaseScreen.m_6_IArr();
      Draw2D.m_0(
         Mth.lerp(floatValue9, m_3(stringArray, floatValue, value), m_3(stringArray, floatValue, value2)),
         floatValue7 + 2.0F,
         Mth.lerp(floatValue9, getLabelWidth(stringArray[value]), getLabelWidth(stringArray[value2])),
         22.0F,
         6.0F,
         0.0F,
         ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(175.0F * floatValue6)),
         ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(175.0F * floatValue6))
      );
      float floatValue10 = Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.0F, floatValue7, 26.0F);

      for (int i = 0; i < stringArray.length; i++) {
         float floatValue11 = m_3(stringArray, floatValue, i);
         float floatValue12 = 1.0F - Math.min(1.0F, Math.abs(floatValue3 - i));
         boolean flag = isInside(floatValue4, floatValue5, floatValue11, floatValue7, getLabelWidth(stringArray[i]), 26.0F);
         int value3 = (int)(150.0F + 105.0F * Math.max(floatValue12, flag ? 0.5F : 0.0F));
         Draw2D.drawTextWithShadow(stringArray[i], Fonts.SUPPLIER16, floatValue11 + 8.0F, floatValue10, ColorUtil.rgba(value3, value3, value3, (int)((180.0F + 50.0F * floatValue12) * floatValue6)), 11.0F);
      }
   }

   public static int m_1(String[] stringArray, float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = (floatValue2 - 26.0F) / 2.0F;

      for (int i = 0; i < stringArray.length; i++) {
         if (isInside(floatValue3, floatValue4, m_3(stringArray, floatValue, i), floatValue5, getLabelWidth(stringArray[i]), 26.0F)) {
            return i;
         }
      }

      return -1;
   }

   private static float getLabelWidth(String text) {
      return Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F) + 16.0F;
   }

   private static float m_3(String[] stringArray, float floatValue, int value) {
      float floatValue2 = floatValue - 2.0F;

      for (int i = stringArray.length - 1; i >= value; i--) {
         floatValue2 -= getLabelWidth(stringArray[i]);
      }

      return floatValue2;
   }

   public static void m_6(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      BaseScreen.m_6_IArr();
      Draw2D.roundedRect(floatValue, floatValue2, floatValue3, floatValue3, 7.0F, ColorUtil.rgba(0, 0, 0, (int)(35.0F * floatValue7)));
      if (floatValue5 > 0.01F) {
         Draw2D.m_0(floatValue, floatValue2, floatValue3, floatValue3, 7.0F, 0.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(150.0F * floatValue5 * floatValue7)), ColorUtil.withAlpha(ColorUtil.secondaryColor(), (int)(150.0F * floatValue5 * floatValue7)));
      }

      if (floatValue4 > 0.01F) {
         Draw2D.roundedRect(floatValue, floatValue2, floatValue3, floatValue3, 7.0F, ColorUtil.rgba(255, 255, 255, (int)(20.0F * floatValue4 * floatValue7)));
      }

      int value = 190 + (int)(65.0F * floatValue6);
      Draw2D.m_4(floatValue, floatValue2, floatValue3, floatValue3, 0.5F + 0.5F * floatValue6, 7.0F, ColorUtil.rgba(value, value, value, (int)((60.0F + 160.0F * floatValue6) * floatValue7)));
   }

   public static void m_5(GuiGraphicsExtractor guiGraphicsExtractor, ItemRenderer itemRenderer, ItemStack itemStack, TextRenderer textRenderer, String text, int value, float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = 36.0F;
      Draw2D.roundedRect(floatValue, floatValue2, floatValue5, floatValue5, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(40.0F * floatValue4)));
      Draw2D.m_4(floatValue, floatValue2, floatValue5, floatValue5, 0.5F, 6.0F, ColorUtil.rgba(190, 190, 190, (int)(60.0F * floatValue4)));
      itemRenderer.m_2(guiGraphicsExtractor, itemStack, floatValue + 6.0F, floatValue2 + 6.0F, 24.0F, floatValue4);
      float floatValue6 = floatValue + floatValue5 + 6.0F;
      float floatValue7 = floatValue + floatValue3 - floatValue6;
      float floatValue8 = Math.max(9.5F, Math.min(12.0F, 12.0F * floatValue7 / textRenderer.getWidth(12.0F)));
      Render2D.pushScissor(floatValue6, floatValue2, floatValue7, 20.0F);
      textRenderer.draw(floatValue6, floatValue2 + 4.0F + (12.0F - floatValue8) / 2.0F, floatValue8, (int)(240.0F * floatValue4));
      Render2D.popScissor();
      Draw2D.drawStyledText(text, Fonts.SUPPLIER15, floatValue6, floatValue2 + 21.0F, ColorUtil.withAlpha(value, (int)(230.0F * floatValue4)), 10.0F);
   }

   public static void m_8_void(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      float floatValue5 = 14.0F;
      int value = ColorUtil.lerpColor(ColorUtil.rgba(100, 100, 100, 180), ColorUtil.primaryColor(), floatValue3);
      int value2 = ColorUtil.lerpColor(ColorUtil.rgba(0, 0, 0, 180), ColorUtil.secondaryColor(), floatValue3);
      Draw2D.m_8(floatValue, floatValue2, floatValue5, floatValue5, 3.0F, 0.0F, ColorUtil.withAlpha(value, (int)(220.0F * floatValue4)), ColorUtil.withAlpha(value2, (int)(220.0F * floatValue4)));
      String text = floatValue3 > 0.5F ? "\uf00c" : "\uf00d";
      float floatValue6 = Math.abs(floatValue3 - 0.5F) * 2.0F;
      float floatValue7 = 12.0F * (0.3F + 0.7F * floatValue6);
      float floatValue8 = Draw2D.getTextWidth(text, Fonts.SUPPLIER8, floatValue7);
      Draw2D.drawTextWithShadow(
         text, Fonts.SUPPLIER8, floatValue + (floatValue5 - floatValue8) / 2.0F, floatValue2 + (floatValue5 - floatValue7) / 2.0F + 0.5F, ColorUtil.rgba(235, 235, 235, (int)(255.0F * floatValue6 * floatValue4)), floatValue7
      );
   }

   public static void m_8(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      Draw2D.roundedRect(floatValue, floatValue2, floatValue3, floatValue4, 5.0F, ColorUtil.rgba(0, 0, 0, (int)(35.0F * floatValue6)));
      if (floatValue5 > 0.01F) {
         Draw2D.roundedRect(floatValue, floatValue2, floatValue3, floatValue4, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(14.0F * floatValue5 * floatValue6)));
      }
   }

   public static void m_8(String text, String text2, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      m_8(floatValue, floatValue2, floatValue3, 24.0F, floatValue4, floatValue6);
      Draw2D.drawStyledText(text, Fonts.SUPPLIER8, floatValue + 7.0F, Draw2D.getCenteredY(text, Fonts.SUPPLIER8, 10.0F, floatValue2, 24.0F), ColorUtil.withAlpha(VALUE2, (int)(220.0F * floatValue6)), 10.0F);
      Draw2D.drawTextWithShadow(text2, Fonts.SUPPLIER16, floatValue + 22.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.5F, floatValue2, 24.0F), ColorUtil.withAlpha(VALUE, (int)((180.0F + 50.0F * floatValue5) * floatValue6)), 11.5F);
      m_8_void(floatValue + floatValue3 - 19.0F, floatValue2 + 5.0F, floatValue5, floatValue6);
   }

   public static void m_5(String text, String text2, float floatValue, float floatValue2, float floatValue3, float floatValue4, int value, float floatValue5, float floatValue6) {
      Draw2D.roundedRect(floatValue, floatValue2, floatValue3, floatValue4, 6.0F, ColorUtil.withAlpha(value, (int)((value >>> 24 & 0xFF) * floatValue6)));
      float floatValue7 = Draw2D.getTextWidth(text, Fonts.SUPPLIER8, 10.0F);
      float floatValue8 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER16, 11.0F);
      float floatValue9 = floatValue + (floatValue3 - floatValue7 - 5.0F - floatValue8) / 2.0F;
      int value2 = ColorUtil.withAlpha(VALUE, (int)((200.0F + 55.0F * floatValue5) * floatValue6));
      Draw2D.drawTextWithShadow(text, Fonts.SUPPLIER8, floatValue9, Draw2D.getCenteredY(text, Fonts.SUPPLIER8, 10.0F, floatValue2, floatValue4), value2, 10.0F);
      Draw2D.drawTextWithShadow(text2, Fonts.SUPPLIER16, floatValue9 + floatValue7 + 5.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.0F, floatValue2, floatValue4), value2, 11.0F);
   }

   public static void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      if (!(floatValue6 <= 0.0F)) {
         float floatValue8 = Math.max(15.0F, floatValue3 * floatValue3 / floatValue4);
         Draw2D.roundedRect(floatValue, floatValue2, 3.0F, floatValue3, 1.5F, ColorUtil.rgba(0, 0, 0, (int)(30.0F * floatValue7)));
         Draw2D.roundedRect(floatValue, floatValue2 + floatValue5 / floatValue6 * (floatValue3 - floatValue8), 3.0F, floatValue8, 1.5F, ColorUtil.rgba(150, 150, 150, (int)(180.0F * floatValue7)));
      }
   }

   public static float smoothStep(float floatValue, float floatValue2) {
      floatValue += (floatValue2 - floatValue) * 0.2F;
      return Math.abs(floatValue - floatValue2) < 0.5F ? floatValue2 : floatValue;
   }

   public static void m_2(TextRenderer textRenderer, String text, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      float floatValue6 = Math.max(textRenderer.getWidth(11.0F), Draw2D.getTextWidth(text, Fonts.SUPPLIER13, 9.5F)) + 14.0F;
      float floatValue7 = 30.0F;
      float floatValue8 = Math.min(floatValue + 10.0F, floatValue3 - floatValue6 - 4.0F);
      BaseScreen.m_6_IArr();
      float floatValue9 = Math.min(floatValue2 + 10.0F, floatValue4 - floatValue7 - 4.0F);
      Draw2D.m_1(floatValue8, floatValue9, floatValue6, floatValue7, 6.0F, 4.0F, ColorUtil.rgba(0, 0, 0, (int)(90.0F * floatValue5)));
      Draw2D.roundedRect(floatValue8, floatValue9, floatValue6, floatValue7, 6.0F, ColorUtil.rgba(22, 22, 24, (int)(235.0F * floatValue5)));
      Draw2D.m_4(floatValue8, floatValue9, floatValue6, floatValue7, 0.5F, 6.0F, ColorUtil.rgba(190, 190, 190, (int)(60.0F * floatValue5)));
      textRenderer.draw(floatValue8 + 7.0F, floatValue9 + 4.0F, 11.0F, (int)(240.0F * floatValue5));
      Draw2D.drawStyledText(text, Fonts.SUPPLIER13, floatValue8 + 7.0F, floatValue9 + 17.0F, ColorUtil.withAlpha(VALUE2, (int)(230.0F * floatValue5)), 9.5F);
   }

   public static void drawCenteredText(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, float floatValue3, int value, float floatValue4) {
      Draw2D.drawStyledText(text, supplier, floatValue + (floatValue2 - Draw2D.getTextWidth(text, supplier, floatValue4)) / 2.0F, floatValue3, value, floatValue4);
   }

   public static boolean isInside(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      return floatValue >= floatValue3 && floatValue <= floatValue3 + floatValue5 && floatValue2 >= floatValue4 && floatValue2 <= floatValue4 + floatValue6;
   }
}
