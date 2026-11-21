package wtf.onetap.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class BlurRenderer {
   private static final RenderPipeline RENDER_PIPELINE = m_6_RenderPipeline("down");
   private static final RenderPipeline RENDER_PIPELINE2 = m_6_RenderPipeline("up");
   private static final RenderPipeline RENDER_PIPELINE3 = m_6_RenderPipeline("copy");
   public static final RenderPipeline RENDER_PIPELINE4 = m_8_RenderPipeline("blur");
   public static final RenderPipeline RENDER_PIPELINE5 = m_8_RenderPipeline("glass", "GLASS");
   private static final GpuTextureView[] GPU_TEXTURE_VIEW_ARRAY = new GpuTextureView[9];
   private static final GpuTextureView[] GPU_TEXTURE_VIEW_ARRAY2 = new GpuTextureView[8];
   private static final GpuTextureView[] GPU_TEXTURE_VIEW_ARRAY3 = new GpuTextureView[9];
   private static final boolean[] FLAG_ARRAY = new boolean[9];
   private static int value;
   private static int value2;
   private static int value3;
   private static final ScreenRectangle SCREEN_RECTANGLE = new ScreenRectangle(
      (int)-7093374334589206528L, (int)1336459961268436992L, (int)-566782404277043200L, (int)-6890753290982653952L
   );
   private static final IntList INT_LIST = new IntArrayList();
   private static final List<ScreenRectangle> SCREEN_RECTANGLE_LIST = new ArrayList<>();
   private static @Nullable ScreenRectangle screenRectangle = SCREEN_RECTANGLE;
   private static @Nullable GpuTextureView gpuTextureView;
   private static @Nullable ScreenRectangle screenRectangle2;

   private BlurRenderer() {
   }

   public static TextureSetup m_4_TextureSetup(float floatValue) {
      RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
      if (renderTarget.width != value || renderTarget.height != value2) {
         m_9(renderTarget.width, renderTarget.height);
      }

      int value4 = Math.clamp((int)Math.ceil(floatValue), 1, 8);
      FLAG_ARRAY[value4] = true;
      value3 = Math.max(value3, value4);
      return TextureSetup.singleTexture(m_4_GpuTextureView(value4), getSampler());
   }

   public static void m_7(GuiElementRenderState guiElementRenderState, int value4) {
      ScreenRectangle screenRectangle3 = guiElementRenderState.bounds();
      boolean flag = screenRectangle3 != null && (guiElementRenderState.pipeline() == RENDER_PIPELINE4 || guiElementRenderState.pipeline() == RENDER_PIPELINE5);
      if (flag) {
         if (!screenRectangle3.equals(screenRectangle2) && screenRectangle != null && screenRectangle.intersects(screenRectangle3)) {
            screenRectangle = null;
            if (INT_LIST.isEmpty() || INT_LIST.getInt(INT_LIST.size() - 1) != value4) {
               INT_LIST.add(value4);
               SCREEN_RECTANGLE_LIST.add(screenRectangle3);
            }
         }

         int value5 = SCREEN_RECTANGLE_LIST.size() - 1;
         SCREEN_RECTANGLE_LIST.set(value5, m_2(SCREEN_RECTANGLE_LIST.get(value5), screenRectangle3));
      }

      screenRectangle2 = flag ? screenRectangle3 : null;
      screenRectangle = screenRectangle3 == null ? SCREEN_RECTANGLE : m_2(screenRectangle, screenRectangle3);
   }

   public static void m_5_void() {
      screenRectangle = SCREEN_RECTANGLE;
      screenRectangle2 = null;
   }

   public static IntList m_0_IntList() {
      return INT_LIST;
   }

   public static void m_4(GpuTextureView gpuTextureView2) {
      gpuTextureView = gpuTextureView2;
   }

   public static void m_6_void() {
      if (gpuTextureView != null) {
         GpuTextureView gpuTextureView2 = Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTextureView();
         m_1(RenderSystem.getDevice().createCommandEncoder(), RENDER_PIPELINE3, gpuTextureView, gpuTextureView2, null);
         gpuTextureView = null;
      }
   }

   public static void m_5_void(int value4) {
      if (value3 != 0) {
         Minecraft mc = Minecraft.getInstance();
         GpuTextureView gpuTextureView2 = mc.gameRenderer.mainRenderTarget().getColorTextureView();
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         ScreenRectangle screenRectangle3 = SCREEN_RECTANGLE_LIST.get(value4);
         int value5 = mc.gameRenderer.gameRenderState().windowRenderState.guiScale;
         int value6 = 2 << value3;
         ScreenRectangle screenRectangle4 = new ScreenRectangle(
            screenRectangle3.left() * value5 - value6, screenRectangle3.top() * value5 - value6, screenRectangle3.width() * value5 + value6 * 2, screenRectangle3.height() * value5 + value6 * 2
         );
         m_1(commandEncoder, RENDER_PIPELINE, gpuTextureView2, GPU_TEXTURE_VIEW_ARRAY[1], screenRectangle4);

         for (int i = 2; i <= value3; i++) {
            m_1(commandEncoder, RENDER_PIPELINE, GPU_TEXTURE_VIEW_ARRAY[i - 1], GPU_TEXTURE_VIEW_ARRAY[i], screenRectangle4);
         }

         for (int i2 = 2; i2 <= value3; i2++) {
            if (FLAG_ARRAY[i2]) {
               GpuTextureView gpuTextureView3 = GPU_TEXTURE_VIEW_ARRAY[i2];

               for (int i3 = i2 - 1; i3 > 1; i3--) {
                  m_1(commandEncoder, RENDER_PIPELINE2, gpuTextureView3, GPU_TEXTURE_VIEW_ARRAY2[i3], screenRectangle4);
                  gpuTextureView3 = GPU_TEXTURE_VIEW_ARRAY2[i3];
               }

               m_1(commandEncoder, RENDER_PIPELINE2, gpuTextureView3, GPU_TEXTURE_VIEW_ARRAY3[i2], screenRectangle4);
            }
         }
      }
   }

   public static void m_7_void() {
      Arrays.fill(FLAG_ARRAY, false);
      value3 = 0;
      INT_LIST.clear();
      SCREEN_RECTANGLE_LIST.clear();
      screenRectangle = SCREEN_RECTANGLE;
      screenRectangle2 = null;
   }

   private static ScreenRectangle m_2(@Nullable ScreenRectangle screenRectangle3, ScreenRectangle screenRectangle4) {
      if (screenRectangle3 == null) {
         return screenRectangle4;
      }

      int value4 = Math.min(screenRectangle3.left(), screenRectangle4.left());
      int value5 = Math.min(screenRectangle3.top(), screenRectangle4.top());
      return new ScreenRectangle(value4, value5, Math.max(screenRectangle3.right(), screenRectangle4.right()) - value4, Math.max(screenRectangle3.bottom(), screenRectangle4.bottom()) - value5);
   }

   private static GpuTextureView m_4_GpuTextureView(int value4) {
      for (int i = 1; i <= value4; i++) {
         m_4(GPU_TEXTURE_VIEW_ARRAY, i, i);
      }

      if (value4 == 1) {
         return GPU_TEXTURE_VIEW_ARRAY[1];
      }

      for (int i2 = 2; i2 < value4; i2++) {
         m_4(GPU_TEXTURE_VIEW_ARRAY2, i2, i2);
      }

      return m_4(GPU_TEXTURE_VIEW_ARRAY3, value4, 1);
   }

   private static GpuTextureView m_4(GpuTextureView[] gpuTextureViewArray, int value4, int value5) {
      if (gpuTextureViewArray[value4] == null) {
         GpuDevice gpuDevice = RenderSystem.getDevice();
         GpuTexture gpuTexture = gpuDevice.createTexture(
            () -> "Onetap Blur " + value5, 12, GpuFormat.RGBA8_UNORM, Math.max(1, value >> value5), Math.max(1, value2 >> value5), 1, 1
         );
         gpuTextureViewArray[value4] = gpuDevice.createTextureView(gpuTexture);
      }

      return gpuTextureViewArray[value4];
   }

   private static void m_9(int value4, int value5) {
      for (GpuTextureView[] gpuTextureViewArray : new GpuTextureView[][]{GPU_TEXTURE_VIEW_ARRAY, GPU_TEXTURE_VIEW_ARRAY2, GPU_TEXTURE_VIEW_ARRAY3}) {
         for (int i = 0; i < gpuTextureViewArray.length; i++) {
            if (gpuTextureViewArray[i] != null) {
               gpuTextureViewArray[i].close();
               gpuTextureViewArray[i].texture().close();
               gpuTextureViewArray[i] = null;
            }
         }
      }

      value = value4;
      value2 = value5;
   }

   private static void m_1(CommandEncoder commandEncoder, RenderPipeline renderPipeline, GpuTextureView gpuTextureView2, GpuTextureView gpuTextureView3, @Nullable ScreenRectangle screenRectangle3) {
      int value4 = gpuTextureView3.texture().getWidth(0);
      int value5 = gpuTextureView3.texture().getHeight(0);

      try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap Blur", gpuTextureView3, Optional.empty())) {
         if (screenRectangle3 != null) {
            float floatValue = (float)value4 / value;
            float floatValue2 = (float)value5 / value2;
            int value6 = Math.clamp((int)Math.floor(screenRectangle3.left() * floatValue), 0, value4);
            int value7 = Math.clamp((int)Math.floor(screenRectangle3.top() * floatValue2), 0, value5);
            int value8 = Math.clamp((int)Math.ceil(screenRectangle3.right() * floatValue), 0, value4);
            int value9 = Math.clamp((int)Math.ceil(screenRectangle3.bottom() * floatValue2), 0, value5);
            if (value6 >= value8 || value7 >= value9) {
               return;
            }

            renderPass.enableScissor(value6, value5 - value9, value8 - value6, value9 - value7);
         }

         renderPass.setPipeline(renderPipeline);
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.bindTexture("InSampler", gpuTextureView2, getSampler());
         renderPass.draw(3, 1, 0, 0);
      }
   }

   private static GpuSampler getSampler() {
      return RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
   }

   private static RenderPipeline m_6_RenderPipeline(String text) {
      return RenderPipelines.register(
         RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/blur_" + text))
            .withVertexShader("core/screenquad")
            .withFragmentShader(Identifier.fromNamespaceAndPath("onetap", "blur/" + text))
            .withBindGroupLayout(BindGroupLayouts.IN_SAMPLER)
            .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
            .build()
      );
   }

   private static RenderPipeline m_8_RenderPipeline(String text, String... stringArray) {
      Builder builder = RenderPipeline.builder(ShapePipelines.f_7)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/" + text))
         .withFragmentShader(Identifier.fromNamespaceAndPath("onetap", "blur/composite"))
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withShaderDefine("BLUR");

      for (String text2 : stringArray) {
         builder.withShaderDefine(text2);
      }

      return RenderPipelines.register(builder.build());
   }
}
