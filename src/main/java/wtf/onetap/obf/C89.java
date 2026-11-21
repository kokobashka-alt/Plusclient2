package wtf.onetap.obf;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import wtf.onetap.render.FontAtlas;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Render2D;

public class C89 {
   private static final Minecraft f_9 = Minecraft.getInstance();

   private C89() {
   }

   public static void m_4(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, int value3, int value4, int value5) {
      guiGraphicsExtractor.fill(value, value2, value3, value4, value5);
   }

   public static void m_1(RenderPipeline renderPipeline, GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, int value3, int value4, int value5) {
      guiGraphicsExtractor.fill(renderPipeline, value, value2, value3, value4, value5);
   }

   public static void m_4(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, int value3, int value4, int value5, int value6) {
      guiGraphicsExtractor.fillGradient(value, value2, value3, value4, value5, value6);
   }

   public static void m_7(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, int value3, int value4, int value5) {
      guiGraphicsExtractor.outline(value, value2, value3, value4, value5);
   }

   public static void m_5(GuiGraphicsExtractor guiGraphicsExtractor, String text, int value, int value2, int value3) {
      guiGraphicsExtractor.text(f_9.font, text, value, value2, value3, true);
   }

   public static void m_2(GuiGraphicsExtractor guiGraphicsExtractor, String text, int value, int value2, int value3, boolean flag) {
      guiGraphicsExtractor.text(f_9.font, text, value, value2, value3, flag);
   }

   public static int m_4_int(String text) {
      return f_9.font.width(text);
   }

   public static int m_9_int() {
      return f_9.getWindow().getGuiScaledWidth();
   }

   public static int m_2_int() {
      return f_9.getWindow().getGuiScaledHeight();
   }

   public static void m_1(GuiGraphicsExtractor guiGraphicsExtractor, String text, float floatValue, float floatValue2, float floatValue3, int value) {
      Render2D.drawText(Fonts.SUPPLIER.get(), text, floatValue, floatValue2, floatValue3, value);
   }

   public static void m_5(GuiGraphicsExtractor guiGraphicsExtractor, FontAtlas fontAtlas, String text, float floatValue, float floatValue2, float floatValue3, int value) {
      Render2D.drawText(fontAtlas, text, floatValue, floatValue2, floatValue3, value);
   }

   public static float m_3(String text, float floatValue) {
      return Fonts.SUPPLIER.get().m_1(text, floatValue);
   }

   public static void m_8(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, int value3, int value4) {
      guiGraphicsExtractor.enableScissor(value, value2, value3, value4);
   }

   public static void m_4(GuiGraphicsExtractor guiGraphicsExtractor) {
      guiGraphicsExtractor.disableScissor();
   }

   public static void m_5(GuiGraphicsExtractor guiGraphicsExtractor) {
      guiGraphicsExtractor.pose().pushMatrix();
   }

   public static void m_3(GuiGraphicsExtractor guiGraphicsExtractor) {
      guiGraphicsExtractor.pose().popMatrix();
   }

   public static void m_7(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue, float floatValue2) {
      guiGraphicsExtractor.pose().translate(floatValue, floatValue2);
   }

   public static void m_8(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue, float floatValue2) {
      guiGraphicsExtractor.pose().scale(floatValue, floatValue2);
   }
}
