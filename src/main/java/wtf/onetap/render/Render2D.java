package wtf.onetap.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public class Render2D {
   private static final Deque<ScreenRectangle> DEQUE = new ArrayDeque<>();
   private static final Map<GuiItemRenderState, Integer> GUI_ITEM_RENDER_STATE_MAP = new IdentityHashMap<>();
   private static float floatValue = 1.0F;
   private static float floatValue2 = 0.0F;
   private static float floatValue3 = 0.0F;
   private static String[] textArray;

   private Render2D() {
   }

   public static void setTransform(float floatValue4, float floatValue5, float floatValue6) {
      floatValue = floatValue4;
      floatValue2 = floatValue5;
      floatValue3 = floatValue6;
   }

   public static float getScale() {
      return floatValue;
   }

   public static float getOffsetX() {
      return floatValue2;
   }

   public static float getOffsetY() {
      return floatValue3;
   }

   private static float scaleX(float floatValue4) {
      return floatValue4 * floatValue + floatValue2;
   }

   private static float scaleY(float floatValue4) {
      return floatValue4 * floatValue + floatValue3;
   }

   public static void m_4(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, int value) {
      m_7(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, value, 0.0F);
   }

   public static void m_7(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, int value, float floatValue11) {
      if (floatValue11 <= 0.0F) {
         m_6(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue8, floatValue8, floatValue8, floatValue9, floatValue10, value);
      } else {
         float floatValue12 = scaleX(floatValue4) + floatValue6 * floatValue * 0.5F;
         float floatValue13 = scaleY(floatValue5) + floatValue7 * floatValue * 0.5F;
         Minecraft mc = Minecraft.getInstance();
         int value2 = mc.getWindow().getGuiScale();
         float floatValue14 = (float)(mc.mouseHandler.xpos() / value2);
         float floatValue15 = (float)(mc.mouseHandler.ypos() / value2);
         m_1(BlurRenderer.RENDER_PIPELINE5, floatValue4, floatValue5, floatValue6, floatValue7, floatValue9, floatValue10, value, ShapePipelines.packHalfFloats(floatValue8 * floatValue, floatValue11), ShapePipelines.packHalfFloats(floatValue14 - floatValue12, floatValue15 - floatValue13), 0.0F);
      }
   }

   public static void m_6(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, float floatValue11, float floatValue12, float floatValue13, int value) {
      m_1(BlurRenderer.RENDER_PIPELINE4, floatValue4, floatValue5, floatValue6, floatValue7, floatValue12, floatValue13, value, ShapePipelines.packHalfFloats(floatValue8 * floatValue, floatValue9 * floatValue), ShapePipelines.packHalfFloats(floatValue10 * floatValue, floatValue11 * floatValue), 0.0F);
   }

   public static void m_4(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9) {
      if (!(floatValue9 <= 0.0F)) {
         m_1(BlurRenderer.RENDER_PIPELINE4, floatValue4, floatValue5, floatValue6, floatValue7, 0.0F, floatValue8, -1, 0, 0, Math.min(floatValue9, 1.0F));
      }
   }

   private static void m_1(
      RenderPipeline renderPipeline, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, int value, int value2, int value3, float floatValue10
   ) {
      if (!(floatValue6 <= 0.0F) && !(floatValue7 <= 0.0F) && !(floatValue9 <= 0.0F)) {
         float floatValue11 = floatValue8 * floatValue;
         float floatValue12 = Math.max(floatValue11, 1.0F);
         float floatValue13 = scaleX(floatValue4) - floatValue12;
         float floatValue14 = scaleY(floatValue5) - floatValue12;
         float floatValue15 = scaleX(floatValue4) + floatValue6 * floatValue + floatValue12;
         float floatValue16 = scaleY(floatValue5) + floatValue7 * floatValue + floatValue12;
         ScreenRectangle screenRectangle = toScreenRect(floatValue13, floatValue14, floatValue15, floatValue16);
         if (screenRectangle != null) {
            submit(
               new ShapePipelines(
                  renderPipeline,
                  BlurRenderer.m_4_TextureSetup(floatValue9),
                  floatValue13,
                  floatValue14,
                  floatValue15,
                  floatValue16,
                  (floatValue13 + floatValue15) * 0.5F,
                  (floatValue14 + floatValue16) * 0.5F,
                  value,
                  value,
                  value,
                  value,
                  value2,
                  value3,
                  ShapePipelines.packHalfFloats(floatValue11, floatValue9),
                  floatValue10,
                  peekScissor(),
                  screenRectangle
               )
            );
         }
      }
   }

   public static void m_6(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, int value) {
      m_4(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue8, floatValue8, floatValue8, floatValue9, floatValue10, value, value, value, value);
   }

   public static void m_4(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, float floatValue11, float floatValue12, float floatValue13, int value) {
      m_4(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, floatValue13, value, value, value, value);
   }

   public static void m_4(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, int value, int value2) {
      m_4(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue8, floatValue8, floatValue8, floatValue9, 0.0F, value, value, value2, value2);
   }

   public static void m_1(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, float floatValue11, float floatValue12, int value, int value2) {
      m_4(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, 0.0F, value, value, value2, value2);
   }

   public static void m_4(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10, int value, int value2, int value3, int value4) {
      m_4(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue8, floatValue8, floatValue8, floatValue9, floatValue10, value, value2, value3, value4);
   }

   public static void m_1(
      float floatValue4,
      float floatValue5,
      float floatValue6,
      float floatValue7,
      float floatValue8,
      float floatValue9,
      float floatValue10,
      float floatValue11,
      float floatValue12,
      float floatValue13,
      int value,
      int value2,
      int value3,
      int value4
   ) {
      m_4(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, floatValue13, value, value2, value3, value4);
   }

   private static void m_4(
      float floatValue4,
      float floatValue5,
      float floatValue6,
      float floatValue7,
      float floatValue8,
      float floatValue9,
      float floatValue10,
      float floatValue11,
      float floatValue12,
      float floatValue13,
      int value,
      int value2,
      int value3,
      int value4
   ) {
      m_8(
         ShapePipelines.f_3,
         floatValue4,
         floatValue5,
         floatValue6,
         floatValue7,
         (floatValue12 + floatValue13) * floatValue,
         value,
         value2,
         value3,
         value4,
         ShapePipelines.packHalfFloats(floatValue8 * floatValue, floatValue9 * floatValue),
         ShapePipelines.packHalfFloats(floatValue10 * floatValue, floatValue11 * floatValue),
         ShapePipelines.packHalfFloats(floatValue12 * floatValue, floatValue13 * floatValue),
         0.0F
      );
   }

   public static void m_6(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value) {
      float floatValue9 = floatValue8 * floatValue;
      int value2 = ShapePipelines.packHalfFloats(floatValue9, floatValue9);
      m_8(ShapePipelines.f_1, floatValue4, floatValue5, floatValue6, floatValue7, 0.0F, value, value, value, value, value2, value2, 0, 0.0F);
   }

   public static void m_0(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value, float floatValue9, float floatValue10, float floatValue11) {
      m_2(ShapePipelines.f_4, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, value, 0, 0.0F, floatValue9, floatValue10, floatValue11);
   }

   public static void m_0(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value, int value2, float floatValue9, float floatValue10, float floatValue11, float floatValue12) {
      m_2(ShapePipelines.f_5, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, value, value2, floatValue9, floatValue10, floatValue11, floatValue12);
   }

   private static void m_2(
      RenderPipeline renderPipeline, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value, int value2, float floatValue9, float floatValue10, float floatValue11, float floatValue12
   ) {
      float floatValue13 = floatValue10 * floatValue;
      float floatValue14 = floatValue8 * floatValue;
      m_8(
         renderPipeline,
         floatValue4,
         floatValue5,
         floatValue6,
         floatValue7,
         floatValue14 + floatValue13 * 0.5F,
         value,
         value,
         value,
         value,
         ShapePipelines.packHalfFloats(floatValue13, floatValue14),
         ShapePipelines.toAbgr(value2),
         ShapePipelines.packHalfFloats(floatValue11, floatValue12),
         floatValue9
      );
   }

   public static void m_7(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, int value, int value2, float floatValue10) {
      m_1(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, value, value2, floatValue10, false);
   }

   public static void m_1(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9, int value, int value2, float floatValue10, boolean flag) {
      float floatValue11 = floatValue9 * floatValue;
      m_8(
         ShapePipelines.f_0,
         floatValue4,
         floatValue5,
         floatValue6,
         floatValue7,
         floatValue11,
         value,
         value,
         value,
         value,
         ShapePipelines.packHalfFloats(floatValue8 * floatValue, floatValue11),
         ShapePipelines.toAbgr(value2),
         ShapePipelines.packHalfFloats(flag ? 1.0F : 0.0F, 0.0F),
         floatValue10
      );
   }

   private static void m_8(
      RenderPipeline renderPipeline,
      float floatValue4,
      float floatValue5,
      float floatValue6,
      float floatValue7,
      float floatValue8,
      int value,
      int value2,
      int value3,
      int value4,
      int value5,
      int value6,
      int value7,
      float floatValue9
   ) {
      if (!(floatValue6 <= 0.0F) && !(floatValue7 <= 0.0F)) {
         float floatValue10 = scaleX(floatValue4) - floatValue8;
         float floatValue11 = scaleY(floatValue5) - floatValue8;
         float floatValue12 = scaleX(floatValue4) + floatValue6 * floatValue + floatValue8;
         float floatValue13 = scaleY(floatValue5) + floatValue7 * floatValue + floatValue8;
         ScreenRectangle screenRectangle = toScreenRect(floatValue10, floatValue11, floatValue12, floatValue13);
         if (screenRectangle != null) {
            submit(
               new ShapePipelines(
                  renderPipeline,
                  TextureSetup.noTexture(),
                  floatValue10,
                  floatValue11,
                  floatValue12,
                  floatValue13,
                  (floatValue10 + floatValue12) * 0.5F,
                  (floatValue11 + floatValue13) * 0.5F,
                  value,
                  value2,
                  value3,
                  value4,
                  value5,
                  value6,
                  value7,
                  floatValue9,
                  peekScissor(),
                  screenRectangle
               )
            );
         }
      }
   }

   public static void drawText(FontAtlas fontAtlas, String text, float floatValue4, float floatValue5, float floatValue6, int value) {
      m_6(fontAtlas, text, floatValue4, floatValue5, floatValue6, value, 0.0F, 1.0F, 0.0F, floatValue4);
   }

   public static void m_6(FontAtlas fontAtlas, String text, float floatValue4, float floatValue5, float floatValue6, int value, float floatValue7, float floatValue8, float floatValue9, float floatValue10) {
      if (fontAtlas != null && fontAtlas.isFlag() && text != null && !text.isEmpty()) {
         float floatValue11 = scaleX(floatValue4);
         float floatValue12 = scaleY(floatValue5);
         float floatValue13 = floatValue6 * floatValue;
         float floatValue14 = floatValue9 * floatValue;
         float floatValue15 = scaleX(floatValue10);
         float floatValue16 = floatValue13 * 0.25F;
         int value2 = 1;

         for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
               value2++;
            }
         }

         float floatValue17 = floatValue11 - floatValue16;
         float floatValue18 = floatValue11 + fontAtlas.m_1(text, floatValue13) + floatValue16;
         if (floatValue14 > 0.0F) {
            floatValue17 = Math.max(floatValue17, floatValue15 - floatValue16);
            floatValue18 = Math.min(floatValue18, floatValue15 + floatValue14 + floatValue16);
         }

         ScreenRectangle screenRectangle = toScreenRect(floatValue17, floatValue12 - floatValue16, floatValue18, floatValue12 + fontAtlas.m_1_float(floatValue13) * value2 + floatValue16);
         if (screenRectangle != null) {
            GpuTextureView gpuTextureView = Minecraft.getInstance().getTextureManager().getTexture(fontAtlas.getIdentifier()).getTextureView();
            submit(
               new MsdfPipelines(
                  fontAtlas,
                  text,
                  floatValue11,
                  floatValue12,
                  floatValue13,
                  value,
                  floatValue7,
                  floatValue8,
                  floatValue14,
                  floatValue15,
                  TextureSetup.singleTexture(gpuTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)),
                  peekScissor(),
                  screenRectangle
               )
            );
         }
      }
   }

   public static void m_4(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value, GpuTextureView gpuTextureView) {
      m_6(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, value, gpuTextureView, 0.0F, 0.0F, 1.0F, 1.0F);
   }

   public static void m_6(
      float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, int value, GpuTextureView gpuTextureView, float floatValue9, float floatValue10, float floatValue11, float floatValue12
   ) {
      m_4(floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, value, gpuTextureView, floatValue9, floatValue10, floatValue11, floatValue12, FilterMode.NEAREST);
   }

   public static void m_4(
      float floatValue4,
      float floatValue5,
      float floatValue6,
      float floatValue7,
      float floatValue8,
      int value,
      GpuTextureView gpuTextureView,
      float floatValue9,
      float floatValue10,
      float floatValue11,
      float floatValue12,
      FilterMode filterMode
   ) {
      m_8(TexturePipelines.f_9, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8 * floatValue, floatValue, value, gpuTextureView, floatValue9, floatValue10, floatValue11, floatValue12, filterMode);
   }

   public static void m_2(float floatValue4, float floatValue5, float floatValue6, float floatValue7, int value, GpuTextureView gpuTextureView, float floatValue8, float floatValue9, float floatValue10, float floatValue11) {
      float floatValue12 = 1.0F / Minecraft.getInstance().getWindow().getGuiScale();
      m_8(TexturePipelines.f_8, floatValue4, floatValue5, floatValue6, floatValue7, 0.0F, floatValue12 * 0.5F, value, gpuTextureView, floatValue8, floatValue9, floatValue10, floatValue11, FilterMode.LINEAR);
   }

   private static void m_8(
      RenderPipeline renderPipeline,
      float floatValue4,
      float floatValue5,
      float floatValue6,
      float floatValue7,
      float floatValue8,
      float floatValue9,
      int value,
      GpuTextureView gpuTextureView,
      float floatValue10,
      float floatValue11,
      float floatValue12,
      float floatValue13,
      FilterMode filterMode
   ) {
      if (!(floatValue6 <= 0.0F) && !(floatValue7 <= 0.0F) && gpuTextureView != null) {
         float floatValue14 = scaleX(floatValue4);
         float floatValue15 = scaleY(floatValue5);
         float floatValue16 = floatValue14 + floatValue6 * floatValue;
         float floatValue17 = floatValue15 + floatValue7 * floatValue;
         float floatValue18 = (floatValue12 - floatValue10) * floatValue9 / (floatValue16 - floatValue14);
         float floatValue19 = (floatValue13 - floatValue11) * floatValue9 / (floatValue17 - floatValue15);
         ScreenRectangle screenRectangle = toScreenRect(floatValue14 - floatValue9, floatValue15 - floatValue9, floatValue16 + floatValue9, floatValue17 + floatValue9);
         if (screenRectangle != null) {
            submit(
               new TexturePipelines(
                  renderPipeline,
                  TextureSetup.singleTexture(gpuTextureView, RenderSystem.getSamplerCache().getClampToEdge(filterMode)),
                  floatValue14 - floatValue9,
                  floatValue15 - floatValue9,
                  floatValue16 + floatValue9,
                  floatValue17 + floatValue9,
                  floatValue10 - floatValue18,
                  floatValue11 - floatValue19,
                  floatValue12 + floatValue18,
                  floatValue13 + floatValue19,
                  value,
                  ShapePipelines.packHalfFloats(floatValue8, floatValue9),
                  peekScissor(),
                  screenRectangle
               )
            );
         }
      }
   }

   public static void m_0(GpuTextureView gpuTextureView, float floatValue4, float floatValue5, float floatValue6, float floatValue7, Consumer<VertexConsumer> consumer) {
      ScreenRectangle screenRectangle = toScreenRect(floatValue4, floatValue5, floatValue4 + floatValue6, floatValue5 + floatValue7);
      if (screenRectangle != null && gpuTextureView != null) {
         submit(new GlowPipelines(TextureSetup.singleTexture(gpuTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)), consumer, peekScissor(), screenRectangle));
      }
   }

   public static void drawItem(TrackingItemStackRenderState trackingItemStackRenderState, float floatValue4, float floatValue5, float floatValue6, int value) {
      float floatValue7 = Minecraft.getInstance().getWindow().getGuiScale();
      float floatValue8 = Math.max(1, Math.round(floatValue6 * floatValue * floatValue7)) / floatValue7;
      Matrix3x2f matrix3x2f = new Matrix3x2f().translate(Math.round(scaleX(floatValue4) * floatValue7) / floatValue7, Math.round(scaleY(floatValue5) * floatValue7) / floatValue7).scale(floatValue8 / 16.0F);
      GuiItemRenderState guiItemRenderState = new GuiItemRenderState(matrix3x2f, trackingItemStackRenderState, 0, 0, peekScissor());
      if (guiItemRenderState.bounds() != null && guiItemRenderState.bounds().width() > 0 && guiItemRenderState.bounds().height() > 0) {
         if (value != -1) {
            int value2 = ARGB.alpha(value);
            GUI_ITEM_RENDER_STATE_MAP.put(guiItemRenderState, ARGB.color(value2, ARGB.red(value) * value2 / 255, ARGB.green(value) * value2 / 255, ARGB.blue(value) * value2 / 255));
         }

         getGuiRenderState().addItem(guiItemRenderState);
      }
   }

   public static int getItemStateId(GuiItemRenderState guiItemRenderState) {
      return GUI_ITEM_RENDER_STATE_MAP.getOrDefault(guiItemRenderState, -1);
   }

   public static void clearItemStates() {
      GUI_ITEM_RENDER_STATE_MAP.clear();
   }

   public static void drawItemDecorations(ItemStack itemStack, float floatValue4, float floatValue5, float floatValue6, int value) {
      drawItemDurability(itemStack, floatValue4, floatValue5, floatValue6, value);
      drawItemCount(itemStack, floatValue4, floatValue5, floatValue6, value);
   }

   public static void drawItemDurability(ItemStack itemStack, float floatValue4, float floatValue5, float floatValue6, int value) {
      if (!itemStack.isEmpty() && value > 0 && itemStack.isBarVisible()) {
         value = Math.clamp(value, 0, 255);
         float floatValue7 = floatValue6 / 16.0F;
         m_6(floatValue4 + 2.0F * floatValue7, floatValue5 + 13.0F * floatValue7, 13.0F * floatValue7, 2.0F * floatValue7, 0.0F, 0.0F, 0.0F, ColorUtil.withAlpha(-16777216, value));
         m_6(floatValue4 + 2.0F * floatValue7, floatValue5 + 13.0F * floatValue7, itemStack.getBarWidth() * floatValue7, floatValue7, 0.0F, 0.0F, 0.0F, ColorUtil.withAlpha(itemStack.getBarColor(), value));
      }
   }

   public static void drawItemCount(ItemStack itemStack, float floatValue4, float floatValue5, float floatValue6, int value) {
      if (!itemStack.isEmpty() && value > 0 && itemStack.getCount() != 1) {
         value = Math.clamp(value, 0, 255);
         float floatValue7 = floatValue6 / 16.0F;
         FontAtlas fontAtlas = Fonts.SUPPLIER15.get();
         String text = String.valueOf(itemStack.getCount());
         float floatValue8 = floatValue6 / 2.0F + 1.0F;
         float floatValue9 = floatValue4 + 17.0F * floatValue7 - fontAtlas.m_1(text, floatValue8);
         float floatValue10 = floatValue5 + 7.0F * floatValue7;
         drawText(fontAtlas, text, floatValue9 + 0.6F, floatValue10 + 0.6F, floatValue8, ColorUtil.withAlpha(-16777216, value));
         drawText(fontAtlas, text, floatValue9, floatValue10, floatValue8, ColorUtil.withAlpha(-1, value));
      }
   }

   public static void pushScissor(float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      int value = (int)Math.floor(scaleX(floatValue4));
      int value2 = (int)Math.floor(scaleY(floatValue5));
      ScreenRectangle screenRectangle = new ScreenRectangle(
         value, value2, Math.max(0, (int)Math.ceil(scaleX(floatValue4) + floatValue6 * floatValue) - value), Math.max(0, (int)Math.ceil(scaleY(floatValue5) + floatValue7 * floatValue) - value2)
      );
      Window window = Minecraft.getInstance().getWindow();
      ScreenRectangle screenRectangle2 = Objects.requireNonNullElse(DEQUE.peek(), new ScreenRectangle(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight()));
      DEQUE.push(Objects.requireNonNullElse(screenRectangle.intersection(screenRectangle2), ScreenRectangle.empty()));
   }

   public static void popScissor() {
      DEQUE.poll();
   }

   private static @Nullable ScreenRectangle peekScissor() {
      return DEQUE.peek();
   }

   private static @Nullable ScreenRectangle toScreenRect(float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      int value = (int)Math.floor(floatValue4);
      int value2 = (int)Math.floor(floatValue5);
      ScreenRectangle screenRectangle = new ScreenRectangle(value, value2, (int)Math.ceil(floatValue6) - value, (int)Math.ceil(floatValue7) - value2);
      ScreenRectangle screenRectangle2 = peekScissor();
      if (screenRectangle2 != null) {
         screenRectangle = screenRectangle2.intersection(screenRectangle);
      }

      return screenRectangle != null && screenRectangle.width() > 0 && screenRectangle.height() > 0 ? screenRectangle : null;
   }

   private static void submit(GuiElementRenderState guiElementRenderState) {
      getGuiRenderState().addGuiElement(guiElementRenderState);
   }

   private static GuiRenderState getGuiRenderState() {
      return Minecraft.getInstance().gameRenderer.gameRenderState().guiRenderState;
   }

   static {
      m_9(new String[5]);
   }

   public static void m_9(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_0_StringArr() {
      return textArray;
   }
}
