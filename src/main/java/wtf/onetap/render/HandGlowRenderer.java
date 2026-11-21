package wtf.onetap.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class HandGlowRenderer {
   private static RenderPipeline renderPipeline;
   private static RenderPipeline renderPipeline2;
   private static RenderPipeline renderPipeline3;
   private static RenderPipeline renderPipeline4;
   private static GpuSampler gpuSampler;
   private static GpuSampler gpuSampler2;
   private static GpuBuffer gpuBuffer;
   private static GpuBuffer gpuBuffer2;
   private static int value;
   private static GpuTexture gpuTexture;
   private static GpuTexture gpuTexture2;
   private static GpuTexture gpuTexture3;
   private static GpuTexture gpuTexture4;
   private static GpuTextureView gpuTextureView;
   private static GpuTextureView gpuTextureView2;
   private static GpuTextureView gpuTextureView3;
   private static GpuTextureView gpuTextureView4;
   private static int value2 = -1;
   private static int value3 = -1;
   private static boolean flag;
   private static boolean flag2;
   private static boolean flag3;
   private static long longValue;
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "handglow/trail");
   private static final Identifier IDENTIFIER2 = Identifier.fromNamespaceAndPath("onetap", "handglow/down");
   private static final Identifier IDENTIFIER3 = Identifier.fromNamespaceAndPath("onetap", "handglow/up");
   private static final Identifier IDENTIFIER4 = Identifier.fromNamespaceAndPath("onetap", "handglow/comp");
   private static final Optional<Vector4fc> OPTIONAL = Optional.of(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F));

   private HandGlowRenderer() {
   }

   public static void m_3_void() {
      flag3 = false;
      BindGroupLayout bindGroupLayout = BindGroupLayout.builder().withUniform("HandParams", UniformType.UNIFORM_BUFFER).build();
      renderPipeline = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1)
         .withBindGroupLayout(bindGroupLayout)
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(IDENTIFIER)
         .withColorTargetState(ColorTargetState.DEFAULT)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/handglow_trail"))
         .build();
      renderPipeline2 = m_7(IDENTIFIER2, "handglow_down", bindGroupLayout);
      renderPipeline3 = m_7(IDENTIFIER3, "handglow_up", bindGroupLayout);
      renderPipeline4 = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1)
         .withBindGroupLayout(bindGroupLayout)
         .withVertexShader(IDENTIFIER4)
         .withFragmentShader(IDENTIFIER4)
         .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/handglow_comp"))
         .build();
   }

   private static RenderPipeline m_7(Identifier identifier, String text, BindGroupLayout bindGroupLayout) {
      return RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
         .withBindGroupLayout(bindGroupLayout)
         .withVertexShader(identifier)
         .withFragmentShader(identifier)
         .withColorTargetState(ColorTargetState.DEFAULT)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/" + text))
         .build();
   }

   public static void m_5(int value4, float floatValue, float floatValue2, int value5, float floatValue3) {
      if (!(floatValue <= 0.001F) && !(floatValue2 <= 0.001F)) {
         if (renderPipeline == null) {
            m_3_void();
         }

         RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
         GpuTextureView gpuTextureView5 = renderTarget.getDepthTextureView();
         if (gpuTextureView5 != null && renderTarget.getColorTextureView() != null) {
            int value6 = renderTarget.width;
            int value7 = renderTarget.height;
            if (value6 > 0 && value7 > 0) {
               float floatValue4 = Math.clamp(floatValue, 0.0F, 9.0F);
               boolean flag4 = value5 == 2;
               float floatValue5 = flag4 ? 0.7F + Math.min(floatValue4, 3.0F) * 0.18F : 0.9F + floatValue4 / 9.0F * 2.4F;
               float floatValue6 = Math.clamp(floatValue2, 0.0F, 1.0F);
               float floatValue7 = (value4 >> 24 & 0xFF) / 255.0F;
               float floatValue8 = (value4 >> 16 & 0xFF) / 255.0F;
               float floatValue9 = (value4 >> 8 & 0xFF) / 255.0F;
               float floatValue10 = (value4 & 0xFF) / 255.0F;
               if (floatValue7 < 0.01F) {
                  floatValue7 = 1.0F;
               }

               m_3_void(value6, value7);
               m_2_void();
               m_5_void();
               m_9_void();
               CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
               if (flag4) {
                  m_0_void();
                  if (!flag2) {
                     m_9(commandEncoder);
                     flag2 = true;
                  }

                  m_5(commandEncoder, gpuTextureView5, gpuTextureView3, 1.0F, floatValue8, floatValue9, floatValue10, floatValue7);
                  GpuTextureView gpuTextureView6 = flag ? gpuTextureView2 : gpuTextureView;
                  GpuTextureView gpuTextureView7 = flag ? gpuTextureView : gpuTextureView2;
                  float floatValue11 = 0.88F;
                  float floatValue12 = m_9_float();
                  float floatValue13 = (float)Math.pow(floatValue11, floatValue12);
                  float floatValue14 = (1.0F - floatValue13) / (1.0F - floatValue11);

                  try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap HandGlow Trail", gpuTextureView7, OPTIONAL)) {
                     renderPass.setPipeline(renderPipeline);
                     renderPass.bindTexture("Sampler0", gpuTextureView6, gpuSampler);
                     renderPass.bindTexture("Sampler1", gpuTextureView3, gpuSampler);
                     renderPass.setVertexBuffer(0, gpuBuffer.slice());
                     renderPass.setUniform("HandParams", m_3(0.0F, 0.0F, 0.0F, 0.0F, floatValue13, 0.0F, floatValue3, floatValue14));
                     renderPass.draw(3, 1, 0, 0);
                  }

                  flag = !flag;
                  m_3(commandEncoder, renderPipeline2, gpuTextureView7, gpuTextureView4, floatValue5);
                  m_3(commandEncoder, renderPipeline3, gpuTextureView4, gpuTextureView3, floatValue5);
               } else {
                  flag2 = false;
                  m_5(commandEncoder, gpuTextureView5, gpuTextureView3, floatValue5, floatValue8, floatValue9, floatValue10, floatValue7);
                  m_3(commandEncoder, renderPipeline2, gpuTextureView3, gpuTextureView4, floatValue5 * 0.9F);
                  m_3(commandEncoder, renderPipeline3, gpuTextureView4, gpuTextureView3, floatValue5);
               }

               try (RenderPass renderPass2 = commandEncoder.createRenderPass(() -> "Onetap HandGlow Comp", renderTarget.getColorTextureView(), Optional.empty())) {
                  renderPass2.setPipeline(renderPipeline4);
                  renderPass2.bindTexture("Sampler0", gpuTextureView5, gpuSampler2);
                  renderPass2.bindTexture("Sampler1", gpuTextureView3, gpuSampler);
                  renderPass2.setVertexBuffer(0, gpuBuffer.slice());
                  renderPass2.setUniform("HandParams", m_3(floatValue8, floatValue9, floatValue10, floatValue7, floatValue6, value5, floatValue3, 0.0F));
                  renderPass2.draw(3, 1, 0, 0);
               }
            }
         }
      }
   }

   public static void reset() {
      flag2 = false;
      longValue = 0L;
   }

   private static void m_9(CommandEncoder commandEncoder) {
      RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap HandGlow TrailClearA", gpuTextureView, OPTIONAL);
      if (renderPass != null) {
         renderPass.close();
      }

      renderPass = commandEncoder.createRenderPass(() -> "Onetap HandGlow TrailClearB", gpuTextureView2, OPTIONAL);
      if (renderPass != null) {
         renderPass.close();
      }

      flag = false;
      longValue = 0L;
   }

   private static void m_3(CommandEncoder commandEncoder, RenderPipeline renderPipeline5, GpuTextureView gpuTextureView5, GpuTextureView gpuTextureView6, float floatValue) {
      try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap HandGlow Kawase", gpuTextureView6, OPTIONAL)) {
         renderPass.setPipeline(renderPipeline5);
         renderPass.bindTexture("Sampler0", gpuTextureView5, gpuSampler);
         renderPass.setVertexBuffer(0, gpuBuffer.slice());
         renderPass.setUniform("HandParams", m_3(0.0F, 0.0F, 0.0F, 0.0F, floatValue, 0.0F, 0.0F, 0.0F));
         renderPass.draw(3, 1, 0, 0);
      }
   }

   private static void m_5(CommandEncoder commandEncoder, GpuTextureView gpuTextureView5, GpuTextureView gpuTextureView6, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap HandGlow MaskDown", gpuTextureView6, OPTIONAL)) {
         renderPass.setPipeline(renderPipeline2);
         renderPass.bindTexture("Sampler0", gpuTextureView5, gpuSampler2);
         renderPass.setVertexBuffer(0, gpuBuffer.slice());
         renderPass.setUniform("HandParams", m_3(floatValue2, floatValue3, floatValue4, floatValue5, floatValue, 1.0F, 0.0F, 0.0F));
         renderPass.draw(3, 1, 0, 0);
      }
   }

   private static float m_9_float() {
      long longValue2 = System.nanoTime();
      long longValue3 = longValue;
      longValue = longValue2;
      if (longValue3 <= 0L) {
         return 1.0F;
      }

      float floatValue = (float)(longValue2 - longValue3) * 1.0E-9F;
      return !(floatValue <= 0.0F) && !(floatValue > 0.5F) ? Math.max(1.0F, floatValue * 120.0F) : 1.0F;
   }

   private static GpuBufferSlice m_3(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      int value4 = value++ % 16;
      long longValue2 = value4 * 256L;

      try (MappedView mappedView = gpuBuffer2.map(longValue2, 256L, false, true)) {
         ByteBuffer byteBuffer = mappedView.data().order(ByteOrder.nativeOrder());
         byteBuffer.putFloat(floatValue).putFloat(floatValue2).putFloat(floatValue3).putFloat(floatValue4);
         byteBuffer.putFloat(floatValue5).putFloat(floatValue6).putFloat(floatValue7).putFloat(floatValue8);
      }

      return gpuBuffer2.slice(longValue2, 256L);
   }

   private static void m_9_void() {
      if (!flag3) {
         GpuDevice gpuDevice = RenderSystem.getDevice();
         gpuDevice.precompilePipeline(renderPipeline);
         gpuDevice.precompilePipeline(renderPipeline2);
         gpuDevice.precompilePipeline(renderPipeline3);
         gpuDevice.precompilePipeline(renderPipeline4);
         flag3 = true;
      }
   }

   private static void m_5_void() {
      if (gpuBuffer2 == null || gpuBuffer2.isClosed()) {
         gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "HandGlow Params", 130, 4096L);
         value = 0;
      }
   }

   private static void m_3_void(int value4, int value5) {
      if (value4 != value2 || value5 != value3 || gpuTextureView3 == null) {
         m_6_void();
         int value6 = Math.max(1, value4 >> 1);
         int value7 = Math.max(1, value5 >> 1);
         GpuDevice gpuDevice = RenderSystem.getDevice();
         byte byteValue = 12;
         gpuTexture3 = gpuDevice.createTexture(() -> "HandGlow HalfA", byteValue, GpuFormat.RGBA8_UNORM, value6, value7, 1, 1);
         gpuTexture4 = gpuDevice.createTexture(() -> "HandGlow HalfB", byteValue, GpuFormat.RGBA8_UNORM, value6, value7, 1, 1);
         gpuTextureView3 = gpuDevice.createTextureView(gpuTexture3);
         gpuTextureView4 = gpuDevice.createTextureView(gpuTexture4);
         if (gpuSampler == null) {
            gpuSampler = gpuDevice.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         }

         if (gpuSampler2 == null) {
            gpuSampler2 = gpuDevice.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty());
         }

         value2 = value4;
         value3 = value5;
         flag2 = false;
         flag = false;
      }
   }

   private static void m_0_void() {
      if (gpuTextureView == null) {
         GpuDevice gpuDevice = RenderSystem.getDevice();
         byte byteValue = 12;
         int value4 = gpuTexture3.getWidth(0);
         int value5 = gpuTexture3.getHeight(0);
         gpuTexture = gpuDevice.createTexture(() -> "HandGlow TrailA", byteValue, GpuFormat.RGBA8_UNORM, value4, value5, 1, 1);
         gpuTexture2 = gpuDevice.createTexture(() -> "HandGlow TrailB", byteValue, GpuFormat.RGBA8_UNORM, value4, value5, 1, 1);
         gpuTextureView = gpuDevice.createTextureView(gpuTexture);
         gpuTextureView2 = gpuDevice.createTextureView(gpuTexture2);
         flag2 = false;
         flag = false;
      }
   }

   private static void m_2_void() {
      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(3 * DefaultVertexFormat.POSITION_TEX.getVertexSize());
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(3.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(-1.0F, 3.0F, 0.0F).setUv(0.0F, 0.0F);
         MeshData meshData = bufferBuilder.build();
         gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "HandGlow Quad", 32, meshData.vertexBuffer());
         meshData.close();
         byteBufferBuilder.close();
      }
   }

   private static void m_6_void() {
      if (gpuTextureView != null) {
         gpuTextureView.close();
      }

      if (gpuTextureView2 != null) {
         gpuTextureView2.close();
      }

      if (gpuTextureView3 != null) {
         gpuTextureView3.close();
      }

      if (gpuTextureView4 != null) {
         gpuTextureView4.close();
      }

      if (gpuTexture != null) {
         gpuTexture.close();
      }

      if (gpuTexture2 != null) {
         gpuTexture2.close();
      }

      if (gpuTexture3 != null) {
         gpuTexture3.close();
      }

      if (gpuTexture4 != null) {
         gpuTexture4.close();
      }

      gpuTextureView4 = null;
      gpuTextureView3 = null;
      gpuTextureView2 = null;
      gpuTextureView = null;
      gpuTexture4 = null;
      gpuTexture3 = null;
      gpuTexture2 = null;
      gpuTexture = null;
      value3 = -1;
      value2 = -1;
      flag2 = false;
   }
}
