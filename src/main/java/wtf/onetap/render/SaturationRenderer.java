package wtf.onetap.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
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
import java.nio.ByteOrder;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;

public class SaturationRenderer {
   private static RenderPipeline renderPipeline;
   private static GpuSampler gpuSampler;
   private static GpuBuffer gpuBuffer;
   private static GpuBuffer gpuBuffer2;
   private static int value;
   private static GpuTexture gpuTexture;
   private static GpuTextureView gpuTextureView;
   private static int value2 = -1;
   private static int value3 = -1;
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "saturation/saturation");

   private SaturationRenderer() {
   }

   public static void m_5_void() {
      BindGroupLayout bindGroupLayout = BindGroupLayout.builder().withUniform("SatParams", UniformType.UNIFORM_BUFFER).build();
      renderPipeline = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
         .withBindGroupLayout(bindGroupLayout)
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(IDENTIFIER)
         .withColorTargetState(ColorTargetState.DEFAULT)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/saturation"))
         .build();
   }

   public static void m_7_void(float floatValue, float floatValue2) {
      if (floatValue != 1.0F || floatValue2 != 1.0F) {
         if (renderPipeline == null) {
            m_5_void();
         }

         RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
         if (renderTarget.getColorTextureView() != null && renderTarget.getColorTexture() != null) {
            int value4 = renderTarget.width;
            int value5 = renderTarget.height;
            if (value4 > 0 && value5 > 0) {
               m_3_void(value4, value5);
               m_7_void();
               GpuDevice gpuDevice = RenderSystem.getDevice();
               CompiledRenderPipeline compiledRenderPipeline = gpuDevice.precompilePipeline(renderPipeline);
               if (compiledRenderPipeline.isValid()) {
                  CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
                  commandEncoder.copyTextureToTexture(renderTarget.getColorTexture(), gpuTexture, 0, 0, 0, 0, 0, value4, value5);

                  try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap Saturation", renderTarget.getColorTextureView(), Optional.empty())) {
                     renderPass.setPipeline(renderPipeline);
                     renderPass.bindTexture("Sampler0", gpuTextureView, gpuSampler);
                     renderPass.setVertexBuffer(0, gpuBuffer.slice());
                     renderPass.setUniform("SatParams", m_8_GpuBufferSlice(floatValue, floatValue2, 0.0F, 0.0F));
                     renderPass.draw(3, 1, 0, 0);
                  }
               }
            }
         }
      }
   }

   private static GpuBufferSlice m_8_GpuBufferSlice(float floatValue, float floatValue2, float floatValue3, float floatValue4) {
      int value4 = value++ % 4;
      long longValue = value4 * 256L;

      try (MappedView mappedView = gpuBuffer2.map(longValue, 256L, false, true)) {
         mappedView.data().order(ByteOrder.nativeOrder()).putFloat(floatValue).putFloat(floatValue2).putFloat(floatValue3).putFloat(floatValue4);
      }

      return gpuBuffer2.slice(longValue, 256L);
   }

   private static void m_3_void(int value4, int value5) {
      if (value4 != value2 || value5 != value3 || gpuTextureView == null) {
         m_9_void();
         GpuDevice gpuDevice = RenderSystem.getDevice();
         byte byteValue = 5;
         gpuTexture = gpuDevice.createTexture(() -> "Onetap Saturation Temp", byteValue, GpuFormat.RGBA8_UNORM, value4, value5, 1, 1);
         gpuTextureView = gpuDevice.createTextureView(gpuTexture);
         value2 = value4;
         value3 = value5;
      }
   }

   private static void m_9_void() {
      if (gpuTextureView != null) {
         gpuTextureView.close();
      }

      if (gpuTexture != null) {
         gpuTexture.close();
      }

      gpuTextureView = null;
      gpuTexture = null;
      value2 = -1;
      value3 = -1;
   }

   private static void m_7_void() {
      if (gpuSampler == null) {
         gpuSampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty());
      }

      if (gpuBuffer2 == null || gpuBuffer2.isClosed()) {
         gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "Saturation Params", 130, 1024L);
         value = 0;
      }

      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(3 * DefaultVertexFormat.POSITION_TEX.getVertexSize());
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(3.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(-1.0F, 3.0F, 0.0F).setUv(0.0F, 0.0F);
         MeshData meshData = bufferBuilder.build();
         gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Saturation Quad", 32, meshData.vertexBuffer());
         meshData.close();
         byteBufferBuilder.close();
      }
   }
}
