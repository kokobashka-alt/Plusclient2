package wtf.onetap.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteOrder;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;

public class AtmosphereRenderer {
   private static RenderPipeline renderPipeline;
   private static GpuSampler gpuSampler;
   private static GpuBuffer gpuBuffer;
   private static GpuBuffer gpuBuffer2;
   private static int value;
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "atmosphere/atmosphere");

   private AtmosphereRenderer() {
   }

   public static void m_9_void() {
      BindGroupLayout bindGroupLayout = BindGroupLayout.builder().withUniform("AtmParams", UniformType.UNIFORM_BUFFER).build();
      renderPipeline = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
         .withBindGroupLayout(bindGroupLayout)
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(IDENTIFIER)
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/atmosphere"))
         .build();
   }

   public static void m_6(int value2, float floatValue, float floatValue2) {
      if (!(floatValue <= 1.0F) && !(floatValue2 <= 0.001F)) {
         if (renderPipeline == null) {
            m_9_void();
         }

         RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
         if (renderTarget.getDepthTextureView() != null && renderTarget.getColorTextureView() != null) {
            m_7_void();
            GpuDevice gpuDevice = RenderSystem.getDevice();
            CompiledRenderPipeline compiledRenderPipeline = gpuDevice.precompilePipeline(renderPipeline);
            if (compiledRenderPipeline.isValid()) {
               float floatValue3 = (value2 >> 16 & 0xFF) / 255.0F;
               float floatValue4 = (value2 >> 8 & 0xFF) / 255.0F;
               float floatValue5 = (value2 & 0xFF) / 255.0F;

               try (RenderPass renderPass = gpuDevice.createCommandEncoder().createRenderPass(() -> "Onetap Atmosphere", renderTarget.getColorTextureView(), Optional.empty())) {
                  renderPass.setPipeline(renderPipeline);
                  renderPass.bindTexture("Sampler0", renderTarget.getDepthTextureView(), gpuSampler);
                  renderPass.setVertexBuffer(0, gpuBuffer.slice());
                  renderPass.setUniform("AtmParams", m_3(floatValue3, floatValue4, floatValue5, 1.0F, Math.clamp(floatValue, 4.0F, 200.0F), Math.clamp(floatValue2, 0.01F, 8.0F), 0.0F, 0.0F));
                  renderPass.draw(3, 1, 0, 0);
               }
            }
         }
      }
   }

   private static GpuBufferSlice m_3(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8) {
      int value2 = value++ % 4;
      long longValue = value2 * 256L;

      try (MappedView mappedView = gpuBuffer2.map(longValue, 256L, false, true)) {
         mappedView.data()
            .order(ByteOrder.nativeOrder())
            .putFloat(floatValue)
            .putFloat(floatValue2)
            .putFloat(floatValue3)
            .putFloat(floatValue4)
            .putFloat(floatValue5)
            .putFloat(floatValue6)
            .putFloat(floatValue7)
            .putFloat(floatValue8);
      }

      return gpuBuffer2.slice(longValue, 256L);
   }

   private static void m_7_void() {
      if (gpuSampler == null) {
         gpuSampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty());
      }

      if (gpuBuffer2 == null || gpuBuffer2.isClosed()) {
         gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "Atmosphere Params", 130, 1024L);
         value = 0;
      }

      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(3 * DefaultVertexFormat.POSITION_TEX.getVertexSize());
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(3.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(-1.0F, 3.0F, 0.0F).setUv(0.0F, 0.0F);
         MeshData meshData = bufferBuilder.build();
         gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Atmosphere Quad", 32, meshData.vertexBuffer());
         meshData.close();
         byteBufferBuilder.close();
      }
   }
}
