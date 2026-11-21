package wtf.onetap.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.function.Supplier;

public class Draw2D {
   private static int value;

   private Draw2D() {
   }

   public static void drawStyledText(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, int value2, float floatValue3) {
      FontAtlas fontAtlas = (FontAtlas)supplier.get();
      int value3 = value2;
      int value4 = 0;
      float floatValue4 = floatValue;

      for (int i = 0; i < text.length() - 1; i++) {
         if (text.charAt(i) == 167) {
            if (i > value4) {
               String text2 = text.substring(value4, i);
               Render2D.drawText(fontAtlas, text2, floatValue4, floatValue2, floatValue3, value3);
               floatValue4 += fontAtlas.m_1(text2, floatValue3);
            }

            value3 = getFormatColor(text.charAt(++i), value2);
            value4 = i + 1;
         }
      }

      if (value4 < text.length()) {
         Render2D.drawText(fontAtlas, text.substring(value4), floatValue4, floatValue2, floatValue3, value3);
      }
   }

   public static void drawColoredText(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, int value2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      drawColoredTextRaw(text, supplier, floatValue, floatValue2, value2, floatValue3, floatValue4, floatValue5, floatValue6);
   }

   public static void drawColoredTextRaw(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, int value2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      FontAtlas fontAtlas = (FontAtlas)supplier.get();
      int value3 = value2;
      int value4 = 0;
      float floatValue7 = floatValue;

      for (int i = 0; i < text.length() - 1; i++) {
         if (text.charAt(i) == 167) {
            if (i > value4) {
               String text2 = text.substring(value4, i);
               Render2D.m_6(fontAtlas, text2, floatValue7, floatValue2, floatValue6, value3, floatValue3, floatValue4, floatValue5, floatValue);
               floatValue7 += fontAtlas.m_1(text2, floatValue6);
            }

            value3 = getFormatColor(text.charAt(++i), value2);
            value4 = i + 1;
         }
      }

      if (value4 < text.length()) {
         Render2D.m_6(fontAtlas, text.substring(value4), floatValue7, floatValue2, floatValue6, value3, floatValue3, floatValue4, floatValue5, floatValue);
      }
   }

   public static void drawTextWithShadow(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, int value2, float floatValue3) {
      int value3 = Math.min(value2 >> 24 & 0xFF, 165);
      drawStyledText(text, supplier, floatValue + 0.6F, floatValue2 + 0.6F, ColorUtil.rgba(25, 25, 25, value3), floatValue3);
      drawStyledText(text, supplier, floatValue, floatValue2, value2, floatValue3);
   }

   public static void m_9(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, int value2, int value3, float floatValue3, float floatValue4) {
      FontAtlas fontAtlas = (FontAtlas)supplier.get();
      if (text != null && !text.isEmpty()) {
         float floatValue5 = fontAtlas.m_1(text, floatValue3);
         float floatValue6 = floatValue;

         for (int i = 0; i < text.length(); i++) {
            String text2 = String.valueOf(text.charAt(i));
            float floatValue7 = floatValue5 > 0.0F ? (floatValue6 - floatValue) / floatValue5 : 0.0F;
            float floatValue8 = 0.5F + 0.5F * (float)Math.sin((floatValue7 + floatValue4) * (Math.PI * 2));
            int value4 = ColorUtil.lerpColor(value2, value3, floatValue8);
            Render2D.drawText(fontAtlas, text2, floatValue6, floatValue2, floatValue3, value4);
            floatValue6 += fontAtlas.m_1(text2, floatValue3);
         }
      }
   }

   public static void drawTextSoftShadow(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, int value2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      int value3 = Math.min(value2 >> 24 & 0xFF, 165);
      drawColoredTextRaw(text, supplier, floatValue + 0.6F, floatValue2 + 0.6F, ColorUtil.rgba(25, 25, 25, value3), floatValue3, floatValue4, floatValue5, floatValue6);
      drawColoredTextRaw(text, supplier, floatValue, floatValue2, value2, floatValue3, floatValue4, floatValue5, floatValue6);
   }

   public static void drawTextOutlined(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, int value2, float floatValue3) {
      int value3 = value2 >> 24 & 0xFF;
      int value4 = ColorUtil.rgba(0, 0, 0, (int)(value3 * 0.6F));
      float floatValue4 = 0.6F;
      drawStyledText(text, supplier, floatValue - floatValue4, floatValue2 - floatValue4, value4, floatValue3);
      drawStyledText(text, supplier, floatValue + floatValue4, floatValue2 - floatValue4, value4, floatValue3);
      drawStyledText(text, supplier, floatValue - floatValue4, floatValue2 + floatValue4, value4, floatValue3);
      drawStyledText(text, supplier, floatValue + floatValue4, floatValue2 + floatValue4, value4, floatValue3);
      drawStyledText(text, supplier, floatValue, floatValue2 - floatValue4, value4, floatValue3);
      drawStyledText(text, supplier, floatValue, floatValue2 + floatValue4, value4, floatValue3);
      drawStyledText(text, supplier, floatValue - floatValue4, floatValue2, value4, floatValue3);
      drawStyledText(text, supplier, floatValue + floatValue4, floatValue2, value4, floatValue3);
      drawStyledText(text, supplier, floatValue, floatValue2, value2, floatValue3);
   }

   public static float getTextWidth(String text, Supplier<FontAtlas> supplier, float floatValue) {
      return ((FontAtlas)supplier.get()).m_1(stripColorCodes(text), floatValue);
   }

   private static String stripColorCodes(String text) {
      if (text.indexOf(167) < 0) {
         return text;
      }

      StringBuilder stringBuilder = new StringBuilder(text.length());

      for (int i = 0; i < text.length(); i++) {
         char ch = text.charAt(i);
         if (ch == 167 && i + 1 < text.length()) {
            i++;
         } else {
            stringBuilder.append(ch);
         }
      }

      return stringBuilder.toString();
   }

   private static int getFormatColor(char ch, int value2) {
      int value3 = switch (Character.toLowerCase(ch)) {
         case '0' -> 0;
         case '1' -> 170;
         case '2' -> 43520;
         case '3' -> 43690;
         case '4' -> 11141120;
         case '5' -> 11141290;
         case '6' -> 16755200;
         case '7' -> 11184810;
         case '8' -> 5592405;
         case '9' -> 5592575;
         default -> -1;
         case 'a' -> 5635925;
         case 'b' -> 5636095;
         case 'c' -> 16733525;
         case 'd' -> 16733695;
         case 'e' -> 16777045;
         case 'f' -> 16777215;
      };
      if (value3 < 0) {
         return value2;
      }

      int value4 = value2 >> 24 & 0xFF;
      return value4 << 24 | value3;
   }

   public static float getFontHeight(Supplier<FontAtlas> supplier, float floatValue) {
      return ((FontAtlas)supplier.get()).m_1_float(floatValue);
   }

   public static float getCenteredY(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, float floatValue3) {
      FontAtlas fontAtlas = (FontAtlas)supplier.get();
      if (text != null && !text.isEmpty()) {
         C7y c7y = fontAtlas.getGlyph(text.codePointAt(0));
         if (c7y == null) {
            return floatValue2 + (floatValue3 - getFontHeight(supplier, floatValue)) / 2.0F;
         }

         float floatValue4 = floatValue / fontAtlas.getFloatValue5();
         float floatValue5 = (fontAtlas.getFloatValue2() - c7y.m_0_a()) * floatValue4;
         float floatValue6 = (fontAtlas.getFloatValue2() - c7y.m_0_h_float()) * floatValue4;
         return floatValue2 + floatValue3 / 2.0F - (floatValue5 + floatValue6) / 2.0F;
      } else {
         return floatValue2 + (floatValue3 - getFontHeight(supplier, floatValue)) / 2.0F;
      }
   }

   public static float getTextBaseline(Supplier<FontAtlas> supplier, float floatValue, float floatValue2, float floatValue3) {
      FontAtlas fontAtlas = (FontAtlas)supplier.get();
      float floatValue4 = floatValue / fontAtlas.getFloatValue5();
      C7y c7y = fontAtlas.getGlyph(77);
      float floatValue5 = (fontAtlas.getFloatValue2() - (c7y != null ? c7y.m_0_a() : fontAtlas.getFloatValue2())) * floatValue4;
      float floatValue6 = (fontAtlas.getFloatValue2() - (c7y != null ? c7y.m_0_h_float() : 0.0F)) * floatValue4;
      return floatValue2 + floatValue3 / 2.0F - (floatValue5 + floatValue6) / 2.0F;
   }

   public static float getCenteredX(String text, Supplier<FontAtlas> supplier, float floatValue, float floatValue2, float floatValue3) {
      FontAtlas fontAtlas = (FontAtlas)supplier.get();
      if (text != null && !text.isEmpty()) {
         C7y c7y = fontAtlas.getGlyph(text.codePointAt(0));
         if (c7y == null) {
            return floatValue2 + (floatValue3 - getTextWidth(text, supplier, floatValue)) / 2.0F;
         }

         float floatValue4 = floatValue / fontAtlas.getFloatValue5();
         float floatValue5 = c7y.m_06() * floatValue4;
         float floatValue6 = c7y.m_0_q() * floatValue4;
         return floatValue2 + floatValue3 / 2.0F - (floatValue5 + floatValue6) / 2.0F;
      } else {
         return floatValue2 + (floatValue3 - getTextWidth(text, supplier, floatValue)) / 2.0F;
      }
   }

   public static void rect(float floatValue, float floatValue2, float floatValue3, float floatValue4, int value2) {
      Render2D.m_6(floatValue, floatValue2, floatValue3, floatValue4, 0.0F, 0.0F, 0.0F, value2);
   }

   public static void roundedRect(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value2) {
      Render2D.m_6(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, 0.0F, 0.0F, value2);
   }

   public static void m_6(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2) {
      Render2D.m_6(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, 0.0F, value2);
   }

   public static void m_0(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value2) {
      m_7(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, 0.0F, 0.0F, value2);
   }

   public static void m_4(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2) {
      m_7(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, 0.0F, value2);
   }

   public static void m_7(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, int value2) {
      if (!(floatValue5 <= 0.0F) && !(floatValue3 <= 0.0F) && !(floatValue4 <= 0.0F)) {
         Render2D.m_6(floatValue, floatValue2, floatValue3, floatValue4, floatValue6, floatValue7, floatValue5, value2);
      }
   }

   public static void m_3(float floatValue, float floatValue2, float floatValue3, float floatValue4, int value2, int value3) {
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, 0.0F, 0.0F, value2, value3);
   }

   public static void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value2, int value3) {
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, 0.0F, value2, value3);
   }

   public static void m_0(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2, int value3) {
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, value2, value3);
   }

   public static void m_3(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value2) {
      Render2D.m_6(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, value2);
   }

   public static void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, int value2) {
      if (!(floatValue3 <= 0.0F) && !(floatValue4 <= 0.0F)) {
         Render2D.m_0(floatValue - floatValue3, floatValue2 - floatValue3, floatValue3 * 2.0F, floatValue3 * 2.0F, floatValue7, value2, floatValue4, floatValue5, floatValue6);
      }
   }

   public static void m_0(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, int value2, int value3) {
      if (!(floatValue3 <= 0.0F) && !(floatValue4 <= 0.0F) && !(floatValue5 <= 0.0F)) {
         float floatValue8 = (float)(System.currentTimeMillis() / 200.0 % (Math.PI * 2));
         Render2D.m_0(floatValue - floatValue3, floatValue2 - floatValue3, floatValue3 * 2.0F, floatValue3 * 2.0F, floatValue7, value2, value3, floatValue8, floatValue4, floatValue5, floatValue6);
      }
   }

   public static void m_8(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2, int value3) {
      int[] intArray = gradientColors(value2, value3);
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, 0.0F, intArray[0], intArray[1], intArray[2], intArray[3]);
   }

   public static void m_2(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, int value2, int value3) {
      int[] intArray = gradientColors(value2, value3);
      Render2D.m_1(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, 0.0F, intArray[0], intArray[1], intArray[2], intArray[3]);
   }

   public static void m_9(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, int value2, int value3) {
      int[] intArray = gradientColors(value2, value3);
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue6, floatValue7, floatValue5, intArray[0], intArray[1], intArray[2], intArray[3]);
   }

   public static void m_7(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2, int value3) {
      m_1(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, value2, value3, 200.0, false);
   }

   public static void m_1(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2, int value3) {
      m_1(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, value2, value3, 266.6666666666667, true);
   }

   private static void m_1(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2, int value3, double doubleValue, boolean flag) {
      if (!(floatValue3 <= 0.0F) && !(floatValue4 <= 0.0F)) {
         float floatValue7 = (float)(System.currentTimeMillis() / doubleValue % (Math.PI * 2));
         Render2D.m_1(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, value2, value3, floatValue7, flag);
      }
   }

   public static void m_6(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2, int value3) {
      m_7(floatValue - 1.0F, floatValue2 - 1.0F, floatValue3 + 2.0F, floatValue4 + 2.0F, floatValue5, floatValue6, value2, value3);
   }

   public static void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, int value2) {
      if (!(floatValue5 <= 0.0F) && !(floatValue3 <= 0.0F) && !(floatValue4 <= 0.0F)) {
         Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue5, value2);
      }
   }

   public static void m_4(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, int value2, int value3) {
      Render2D.m_1(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, value2, value3);
   }

   private static int[] gradientColors(int value2, int value3) {
      double doubleValue = System.currentTimeMillis() / 600.0;
      int[] intArray = new int[4];

      for (int i = 0; i < 4; i++) {
         double doubleValue2 = i * (Math.PI / 2);
         intArray[i] = ColorUtil.lerpColor(value2, value3, (float)(Math.sin(doubleValue + doubleValue2) * 0.5 + 0.5));
      }

      return intArray;
   }

   public static void m_1(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2) {
      m_6(floatValue - 1.0F, floatValue2 - 1.0F, floatValue3 + 2.0F, floatValue4 + 2.0F, floatValue5, floatValue6, value2);
   }

   public static void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2, int value3) {
      m_0(floatValue - 1.0F, floatValue2 - 1.0F, floatValue3 + 2.0F, floatValue4 + 2.0F, floatValue5, floatValue6, value2, value3);
   }

   public static void m_5_void(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value2) {
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, 0.0F, 0.0F, floatValue5, value2);
   }

   public static void m_7(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2) {
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, 0.0F, floatValue6, value2);
   }

   public static void m_8(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, int value2) {
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, value2);
   }

   public static void m_5(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value2, float floatValue7) {
      Render2D.m_7(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, 0.0F, floatValue6, value2, floatValue7);
   }

   public static void m_7(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, int value2) {
      Render2D.m_6(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, value2);
   }

   public static void m_9(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, GpuTextureView gpuTextureView) {
      m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, -1, gpuTextureView);
   }

   public static void m_4(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, int value2, GpuTextureView gpuTextureView) {
      Render2D.m_4(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, value2, gpuTextureView);
   }

   public static void m_0(float floatValue, float floatValue2, float floatValue3, float floatValue4, int value2, GpuTextureView gpuTextureView, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      Render2D.m_6(floatValue, floatValue2, floatValue3, floatValue4, 0.0F, value2, gpuTextureView, floatValue5, floatValue6, floatValue7, floatValue8);
   }

   public static void m_7(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9) {
      float floatValue10 = Math.max(floatValue7 * 0.65F, Math.clamp(floatValue8, 0.0F, 1.0F));
      int value2 = ColorUtil.lerpColor(ColorUtil.primaryColor(), ColorUtil.rgba(255, 255, 255, 255), floatValue10 * 0.35F);
      roundedRect(floatValue, floatValue2, floatValue3, floatValue4, 1.4F, ColorUtil.rgba(255, 255, 255, (int)(28.0F * floatValue9)));
      roundedRect(floatValue, floatValue5 + floatValue8 * 1.5F, floatValue3, floatValue6 - floatValue8 * 3.0F, 1.4F, ColorUtil.withAlpha(value2, (int)((210.0F + 45.0F * floatValue10) * floatValue9)));
   }

   public static void drawPanel(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      m_1(floatValue, floatValue2, floatValue3, floatValue4, 8.0F, 4.0F, ColorUtil.rgba(12, 12, 12, (int)(95.0F * floatValue5)));
      m_7(floatValue, floatValue2, floatValue3, floatValue4, 8.0F, 4.0F * floatValue5, ColorUtil.rgba(35, 35, 35, (int)(105.0F * floatValue5)));
   }

   public static void m_3(float floatValue, float floatValue2, float floatValue3, float floatValue4, String text, String text2, float floatValue5, float floatValue6) {
      if (floatValue5 > 0.01F) {
         roundedRect(floatValue, floatValue2, floatValue3, floatValue4, 5.0F, ColorUtil.rgba(255, 255, 255, (int)(22.0F * floatValue5 * floatValue6)));
      }

      int value2 = ColorUtil.rgba(230, 230, 230, (int)(195.0F * floatValue6));
      int value3 = ColorUtil.lerpColor(value2, ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue6)), floatValue5);
      int value4 = ColorUtil.lerpColor(value2, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(255.0F * floatValue6)), floatValue5);
      float floatValue7 = 15.0F;
      float floatValue8 = 12.0F;
      float floatValue9 = 8.0F;
      float floatValue10 = getTextBaseline(Fonts.SUPPLIER16, floatValue8, floatValue2, floatValue4);
      float floatValue11 = getTextWidth(text, Fonts.SUPPLIER8, floatValue7);
      float floatValue12 = getTextWidth(text2, Fonts.SUPPLIER16, floatValue8);
      float floatValue13 = floatValue11 + floatValue9 + floatValue12;
      float floatValue14 = floatValue + (floatValue3 - floatValue13) / 2.0F + floatValue11 + floatValue9;
      drawTextWithShadow(text, Fonts.SUPPLIER8, floatValue14 - floatValue9 - floatValue11, getCenteredY(text, Fonts.SUPPLIER8, floatValue7, floatValue2, floatValue4), value4, floatValue7);
      drawTextWithShadow(text2, Fonts.SUPPLIER16, floatValue14, floatValue10, value3, floatValue8);
   }

   public static void m_6_void(int value2) {
      value = value2;
   }

   public static int m_4_int() {
      return value;
   }

   public static int m_0_int() {
      return 0;
   }

   static {
      m_6_void(112);
   }
}
