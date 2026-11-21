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

public class GlowEspRenderer {
   private static RenderPipeline renderPipeline;
   private static RenderPipeline renderPipeline2;
   private static RenderPipeline renderPipeline3;
   private static RenderPipeline renderPipeline4;
   private static GpuSampler gpuSampler;
   private static GpuBuffer gpuBuffer;
   private static GpuBuffer gpuBuffer2;
   private static int value;
   private static boolean flag;
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
   private static boolean flag2;
   private static boolean flag3;
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "glowesp/trail");
   private static final Identifier IDENTIFIER2 = Identifier.fromNamespaceAndPath("onetap", "glowesp/down");
   private static final Identifier IDENTIFIER3 = Identifier.fromNamespaceAndPath("onetap", "glowesp/up");
   private static final Identifier IDENTIFIER4 = Identifier.fromNamespaceAndPath("onetap", "glowesp/comp");
   private static final Optional<Vector4fc> OPTIONAL = Optional.of(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F));

   private GlowEspRenderer() {
   }

   public static void m_4_void() {
      BindGroupLayout bindGroupLayout = BindGroupLayout.builder().withUniform("GlowParams", UniformType.UNIFORM_BUFFER).build();
      renderPipeline = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1)
         .withBindGroupLayout(bindGroupLayout)
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(IDENTIFIER)
         .withColorTargetState(ColorTargetState.DEFAULT)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/glowesp_trail"))
         .build();
      renderPipeline2 = m_6(IDENTIFIER2, "glowesp_down", bindGroupLayout);
      renderPipeline3 = m_6(IDENTIFIER3, "glowesp_up", bindGroupLayout);
      renderPipeline4 = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1)
         .withBindGroupLayout(bindGroupLayout)
         .withVertexShader(IDENTIFIER4)
         .withFragmentShader(IDENTIFIER4)
         .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/glowesp_comp"))
         .build();
   }

   private static RenderPipeline m_6(Identifier identifier, String text, BindGroupLayout bindGroupLayout) {
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

   public static void m_5(RenderTarget renderTarget, boolean flag4, float floatValue, float floatValue2, int value4, float floatValue3) {
      m_2(renderTarget, flag4, floatValue, floatValue2, value4, floatValue3, false);
   }

   public static void m_2(RenderTarget renderTarget, boolean flag4, float floatValue, float floatValue2, int value4, float floatValue3, boolean flag5) {
      if (flag4) {
         if (!(floatValue <= 0.001F) && !(floatValue2 <= 0.001F)) {
            if (renderPipeline == null) {
               m_4_void();
            }

            if (renderTarget != null && renderTarget.getColorTextureView() != null) {
               RenderTarget renderTarget2 = Minecraft.getInstance().gameRenderer.mainRenderTarget();
               int value5 = renderTarget.width;
               int value6 = renderTarget.height;
               if (value5 > 0 && value6 > 0) {
                  float floatValue4 = Math.clamp(floatValue, 0.0F, 9.0F);
                  boolean flag6 = value4 == 2;
                  float floatValue5 = flag6 ? 0.75F + Math.min(floatValue4, 4.0F) * 0.22F : 0.95F + floatValue4 / 9.0F * 2.15F;
                  float floatValue6 = Math.clamp(floatValue2, 0.0F, 1.0F);
                  m_0_void(value5, value6);
                  m_1_void();
                  m_8_void();
                  m_9_void();
                  CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
                  GpuTextureView gpuTextureView5 = renderTarget.getColorTextureView();
                  if (flag6) {
                     m_5_void();
                     if (!flag3) {
                        m_1(commandEncoder);
                        flag3 = true;
                     }

                     m_7(commandEncoder, renderPipeline2, gpuTextureView5, gpuTextureView3, 1.0F);
                     GpuTextureView gpuTextureView6 = flag2 ? gpuTextureView2 : gpuTextureView;
                     GpuTextureView gpuTextureView7 = flag2 ? gpuTextureView : gpuTextureView2;

                     try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap GlowESP Trail", gpuTextureView7, OPTIONAL)) {
                        renderPass.setPipeline(renderPipeline);
                        renderPass.bindTexture("Sampler0", gpuTextureView6, gpuSampler);
                        renderPass.bindTexture("Sampler1", gpuTextureView3, gpuSampler);
                        renderPass.setVertexBuffer(0, gpuBuffer.slice());
                        renderPass.setUniform("GlowParams", m_7(0.0F, 0.0F, 0.0F, 0.0F, 0.88F, 1.15F, floatValue3, 0.0F));
                        renderPass.draw(3, 1, 0, 0);
                     }

                     flag2 = !flag2;
                     m_7(commandEncoder, renderPipeline2, gpuTextureView7, gpuTextureView4, floatValue5);
                     m_7(commandEncoder, renderPipeline3, gpuTextureView4, gpuTextureView3, floatValue5);
                  } else {
                     if (!flag5) {
                        flag3 = false;
                     }

                     m_7(commandEncoder, renderPipeline2, gpuTextureView5, gpuTextureView3, floatValue5);
                     m_7(commandEncoder, renderPipeline2, gpuTextureView3, gpuTextureView4, floatValue5 * 0.9F);
                     m_7(commandEncoder, renderPipeline3, gpuTextureView4, gpuTextureView3, floatValue5);
                  }

                  try (RenderPass renderPass2 = commandEncoder.createRenderPass(() -> "Onetap GlowESP", renderTarget2.getColorTextureView(), Optional.empty())) {
                     renderPass2.setPipeline(renderPipeline4);
                     renderPass2.bindTexture("Sampler0", gpuTextureView5, gpuSampler);
                     renderPass2.bindTexture("Sampler1", gpuTextureView3, gpuSampler);
                     renderPass2.setVertexBuffer(0, gpuBuffer.slice());
                     renderPass2.setUniform("GlowParams", m_7(0.0F, 0.0F, 0.0F, 0.0F, floatValue6, value4, floatValue3, 0.0F));
                     renderPass2.draw(3, 1, 0, 0);
                  }
               }
            }
         }
      }
   }

   public static void m_6_void() {
      flag3 = false;
   }

   private static void m_1(CommandEncoder commandEncoder) {
      RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap GlowESP TrailClearA", gpuTextureView, OPTIONAL);
      if (renderPass != null) {
         renderPass.close();
      }

      renderPass = commandEncoder.createRenderPass(() -> "Onetap GlowESP TrailClearB", gpuTextureView2, OPTIONAL);
      if (renderPass != null) {
         renderPass.close();
      }

      flag2 = false;
   }

   private static void m_7(CommandEncoder commandEncoder, RenderPipeline renderPipeline5, GpuTextureView gpuTextureView5, GpuTextureView gpuTextureView6, float floatValue) {
      try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap GlowESP Kawase", gpuTextureView6, OPTIONAL)) {
         renderPass.setPipeline(renderPipeline5);
         renderPass.bindTexture("Sampler0", gpuTextureView5, gpuSampler);
         renderPass.setVertexBuffer(0, gpuBuffer.slice());
         renderPass.setUniform("GlowParams", m_7(0.0F, 0.0F, 0.0F, 0.0F, floatValue, 0.0F, 0.0F, 0.0F));
         renderPass.draw(3, 1, 0, 0);
      }
   }

   private static GpuBufferSlice m_7(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      int value4 = value++ % 16;
      long longValue = value4 * 256L;

      try (MappedView mappedView = gpuBuffer2.map(longValue, 256L, false, true)) {
         ByteBuffer byteBuffer = mappedView.data().order(ByteOrder.nativeOrder());
         byteBuffer.putFloat(floatValue).putFloat(floatValue2).putFloat(floatValue3).putFloat(floatValue4);
         byteBuffer.putFloat(floatValue5).putFloat(floatValue6).putFloat(floatValue7).putFloat(floatValue8);
      }

      return gpuBuffer2.slice(longValue, 256L);
   }

   private static void m_9_void() {
      if (!flag) {
         GpuDevice gpuDevice = RenderSystem.getDevice();
         gpuDevice.precompilePipeline(renderPipeline);
         gpuDevice.precompilePipeline(renderPipeline2);
         gpuDevice.precompilePipeline(renderPipeline3);
         gpuDevice.precompilePipeline(renderPipeline4);
         flag = true;
      }
   }

   private static void m_8_void() {
      if (gpuBuffer2 == null || gpuBuffer2.isClosed()) {
         gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "GlowESP Params", 130, 4096L);
         value = 0;
      }
   }

   private static void m_0_void(int value4, int value5) {
      if (value4 != value2 || value5 != value3 || gpuTextureView3 == null) {
         m_7_void();
         int value6 = Math.max(1, value4 >> 1);
         int value7 = Math.max(1, value5 >> 1);
         GpuDevice gpuDevice = RenderSystem.getDevice();
         byte byteValue = 12;
         gpuTexture3 = gpuDevice.createTexture(() -> "GlowESP LowA", byteValue, GpuFormat.RGBA8_UNORM, value6, value7, 1, 1);
         gpuTexture4 = gpuDevice.createTexture(() -> "GlowESP LowB", byteValue, GpuFormat.RGBA8_UNORM, value6, value7, 1, 1);
         gpuTextureView3 = gpuDevice.createTextureView(gpuTexture3);
         gpuTextureView4 = gpuDevice.createTextureView(gpuTexture4);
         if (gpuSampler == null) {
            gpuSampler = gpuDevice.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         }

         value2 = value4;
         value3 = value5;
         flag3 = false;
         flag2 = false;
      }
   }

   private static void m_5_void() {
      if (gpuTextureView == null) {
         int value4 = gpuTexture3.getWidth(0);
         int value5 = gpuTexture3.getHeight(0);
         GpuDevice gpuDevice = RenderSystem.getDevice();
         byte byteValue = 12;
         gpuTexture = gpuDevice.createTexture(() -> "GlowESP TrailA", byteValue, GpuFormat.RGBA8_UNORM, value4, value5, 1, 1);
         gpuTexture2 = gpuDevice.createTexture(() -> "GlowESP TrailB", byteValue, GpuFormat.RGBA8_UNORM, value4, value5, 1, 1);
         gpuTextureView = gpuDevice.createTextureView(gpuTexture);
         gpuTextureView2 = gpuDevice.createTextureView(gpuTexture2);
         flag3 = false;
         flag2 = false;
      }
   }

   private static void m_1_void() {
      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(3 * DefaultVertexFormat.POSITION_TEX.getVertexSize());
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(3.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(-1.0F, 3.0F, 0.0F).setUv(0.0F, 0.0F);
         MeshData meshData = bufferBuilder.build();
         gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "GlowESP Quad", 32, meshData.vertexBuffer());
         meshData.close();
         byteBufferBuilder.close();
      }
   }

   private static void m_7_void() {
      m_3_void();
      if (gpuTextureView3 != null) {
         gpuTextureView3.close();
      }

      if (gpuTextureView4 != null) {
         gpuTextureView4.close();
      }

      if (gpuTexture3 != null) {
         gpuTexture3.close();
      }

      if (gpuTexture4 != null) {
         gpuTexture4.close();
      }

      gpuTextureView4 = null;
      gpuTextureView3 = null;
      gpuTexture4 = null;
      gpuTexture3 = null;
      value3 = -1;
      value2 = -1;
      flag3 = false;
   }

   private static void m_3_void() {
      if (gpuTextureView != null) {
         gpuTextureView.close();
      }

      if (gpuTextureView2 != null) {
         gpuTextureView2.close();
      }

      if (gpuTexture != null) {
         gpuTexture.close();
      }

      if (gpuTexture2 != null) {
         gpuTexture2.close();
      }

      gpuTextureView2 = null;
      gpuTextureView = null;
      gpuTexture2 = null;
      gpuTexture = null;
      flag3 = false;
      flag2 = false;
   }
}
