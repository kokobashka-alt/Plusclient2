package wtf.onetap.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import wtf.onetap.module.Module;

public class Texture3DRenderer {
   private static RenderPipeline renderPipeline;
   private static RenderPipeline renderPipeline2;
   private static RenderPipeline renderPipeline3;
   private static RenderPipeline renderPipeline4;
   private static RenderPipeline renderPipeline5;
   private static RenderPipeline renderPipeline6;
   private static GpuSampler gpuSampler;
   private static GpuSampler gpuSampler2;
   private static boolean flag;
   public static final Texture3DRenderer.DataRecord C1 = new Texture3DRenderer.DataRecord(true, false);
   public static final Texture3DRenderer.DataRecord C12 = new Texture3DRenderer.DataRecord(false, false);
   public static final Texture3DRenderer.DataRecord C13 = new Texture3DRenderer.DataRecord(true, true);
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "world/texture3d");
   private static final Identifier IDENTIFIER2 = Identifier.fromNamespaceAndPath("onetap", "world/texture3d_flat");
   private static GpuBuffer gpuBuffer;
   private static int value;
   private static GpuBuffer[] gpuBufferArray;
   private static int value2;
   private static boolean flag2;
   private static Module[] moduleArray;

   private Texture3DRenderer() {
   }

   public static void m_4_void() {
      renderPipeline = m_7(false, false, BlendFunction.OVERLAY, "pipeline/texture3d");
      renderPipeline2 = m_7(true, false, BlendFunction.OVERLAY, "pipeline/texture3d_depth");
      renderPipeline3 = m_7(false, false, BlendFunction.TRANSLUCENT, "pipeline/texture3d_translucent");
      renderPipeline4 = m_7(true, false, BlendFunction.TRANSLUCENT, "pipeline/texture3d_depth_translucent");
      renderPipeline5 = m_7(true, true, BlendFunction.TRANSLUCENT, "pipeline/texture3d_gui");
      renderPipeline6 = m_2(false, false, BlendFunction.ADDITIVE, IDENTIFIER2, "pipeline/texture3d_flat");
      m_2_void();
   }

   private static RenderPipeline m_7(boolean flag3, boolean flag4, BlendFunction blendFunction, String text) {
      return m_2(flag3, flag4, blendFunction, IDENTIFIER, text);
   }

   private static RenderPipeline m_2(boolean flag3, boolean flag4, BlendFunction blendFunction, Identifier identifier, String text) {
      Builder builder = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
         .withBindGroupLayout(BindGroupLayout.builder().withUniform("TextureParams", UniformType.UNIFORM_BUFFER).build())
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(identifier)
         .withColorTargetState(new ColorTargetState(blendFunction))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
         .withPrimitiveTopology(PrimitiveTopology.QUADS)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", text));
      if (flag3) {
         builder.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, flag4));
      }

      return builder.build();
   }

   public static void m_8(Matrix4f matrix4f, float floatValue, float floatValue2, float floatValue3, GpuTextureView gpuTextureView, int value3) {
      m_8(matrix4f, gpuTextureView, 1, false, true, bufferBuilder -> m_2(bufferBuilder, floatValue, floatValue2, floatValue3, value3));
   }

   public static void m_5(Matrix4f matrix4f, float floatValue, float floatValue2, float floatValue3, GpuTextureView gpuTextureView, int value3, boolean flag3) {
      m_8(matrix4f, gpuTextureView, 1, false, flag3, bufferBuilder -> m_2(bufferBuilder, floatValue, floatValue2, floatValue3, value3));
   }

   public static void m_8(Matrix4f matrix4f, GpuTextureView gpuTextureView, int value3, Consumer<BufferBuilder> consumer) {
      m_8(matrix4f, gpuTextureView, value3, false, true, consumer);
   }

   public static void m_3(Matrix4f matrix4f, GpuTextureView gpuTextureView, int value3, boolean flag3, Consumer<BufferBuilder> consumer) {
      m_8(matrix4f, gpuTextureView, value3, flag3, true, consumer);
   }

   public static void m_8(Matrix4f matrix4f, GpuTextureView gpuTextureView, int value3, boolean flag3, boolean flag4, Consumer<BufferBuilder> consumer) {
      m_5(matrix4f, gpuTextureView, value3, flag3, flag4, m_5_GpuSampler(), consumer);
   }

   public static void m_1(Matrix4f matrix4f, GpuTextureView gpuTextureView, int value3, GpuSampler gpuSampler3, Consumer<BufferBuilder> consumer) {
      m_6(matrix4f, gpuTextureView, value3, false, false, gpuSampler3, consumer, C13);
   }

   public static void m_5(Matrix4f matrix4f, GpuTextureView gpuTextureView, int value3, GpuSampler gpuSampler3, Consumer<BufferBuilder> consumer) {
      m_6(matrix4f, gpuTextureView, value3, false, true, gpuSampler3, consumer, C12);
   }

   public static void m_5(Matrix4f matrix4f, GpuTextureView gpuTextureView, int value3, boolean flag3, boolean flag4, GpuSampler gpuSampler3, Consumer<BufferBuilder> consumer) {
      m_6(matrix4f, gpuTextureView, value3, flag4, false, gpuSampler3, consumer, flag3 ? C1 : C12);
   }

   private static void m_6(
      Matrix4f matrix4f, GpuTextureView gpuTextureView, int value3, boolean flag3, boolean flag4, GpuSampler gpuSampler3, Consumer<BufferBuilder> consumer, Texture3DRenderer.DataRecord c1
   ) {
      if (value3 > 0) {
         ByteBufferBuilder byteBufferBuilder = null;
         GpuBuffer gpuBuffer2 = null;

         try {
            byteBufferBuilder = new ByteBufferBuilder(value3 * 4 * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize());
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            consumer.accept(bufferBuilder);
            MeshData meshData = bufferBuilder.build();
            if (gpuTextureView == null || meshData == null) {
               if (meshData != null) {
                  meshData.close();
               }

               return;
            }

            if (renderPipeline == null) {
               m_4_void();
            }

            m_9_void();
            int value4 = meshData.drawState().indexCount();
            if (value4 > 0) {
               m_3_void();
               m_6_void();
               GpuBufferSlice gpuBufferSlice = m_6(matrix4f);
               ByteBuffer byteBuffer = meshData.vertexBuffer();
               int value5 = byteBuffer.remaining();
               GpuBufferSlice gpuBufferSlice2;
               if (value5 > (int)71923067073331200L) {
                  gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "Texture3D Vertices", 32, byteBuffer);
                  gpuBufferSlice2 = gpuBuffer2.slice();
               } else {
                  gpuBufferSlice2 = m_1(byteBuffer);
               }

               meshData.close();
               Minecraft mc = Minecraft.getInstance();
               RenderTarget renderTarget = mc.gameRenderer.mainRenderTarget();
               CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
               RenderPipeline renderPipeline7 = flag4 ? renderPipeline6 : (c1.m_9_f() ? renderPipeline5 : (flag3 ? (c1.m_9_n() ? renderPipeline2 : renderPipeline) : (c1.m_9_n() ? renderPipeline4 : renderPipeline3)));
               GpuTextureView gpuTextureView2 = c1.m_9_n() ? renderTarget.getDepthTextureView() : null;

               try (RenderPass renderPass = commandEncoder.createRenderPass(
                     () -> "Onetap Texture3D", renderTarget.getColorTextureView(), Optional.empty(), gpuTextureView2, OptionalDouble.empty()
                  )) {
                  renderPass.setPipeline(renderPipeline7);
                  renderPass.setUniform("TextureParams", gpuBufferSlice);
                  renderPass.bindTexture("Sampler0", gpuTextureView, gpuSampler3);
                  renderPass.setVertexBuffer(0, gpuBufferSlice2);
                  AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
                  renderPass.setIndexBuffer(autoStorageIndexBuffer.getBuffer(value4), autoStorageIndexBuffer.type());
                  renderPass.drawIndexed(value4, 1, 0, 0, 0);
                  return;
               }
            }

            meshData.close();
         } finally {
            if (gpuBuffer2 != null) {
               gpuBuffer2.close();
            }

            if (byteBufferBuilder != null) {
               byteBufferBuilder.close();
            }
         }
      }
   }

   private static void m_2_void() {
      if (!flag2) {
         GpuDevice gpuDevice = RenderSystem.getDevice();
         gpuDevice.precompilePipeline(renderPipeline);
         gpuDevice.precompilePipeline(renderPipeline2);
         gpuDevice.precompilePipeline(renderPipeline3);
         gpuDevice.precompilePipeline(renderPipeline4);
         gpuDevice.precompilePipeline(renderPipeline5);
         gpuDevice.precompilePipeline(renderPipeline6);
         flag2 = true;
      }
   }

   private static void m_3_void() {
      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Texture3D Params", 130, 4096L);
         value = 0;
      }
   }

   private static void m_6_void() {
      if (gpuBufferArray != null) {
         boolean flag3 = true;

         for (GpuBuffer gpuBuffer2 : gpuBufferArray) {
            if (gpuBuffer2 == null || gpuBuffer2.isClosed()) {
               flag3 = false;
               break;
            }
         }

         if (flag3) {
            return;
         }
      }

      gpuBufferArray = new GpuBuffer[16];
      GpuDevice gpuDevice = RenderSystem.getDevice();

      for (int i = 0; i < 16; i++) {
         gpuBufferArray[i] = gpuDevice.createBuffer(() -> "Texture3D Vertices", 34, 1048576L);
      }

      value2 = 0;
   }

   private static GpuBufferSlice m_6(Matrix4f matrix4f) {
      int value3 = value++ % 16;
      long longValue = value3 * 256L;

      try (MappedView mappedView = gpuBuffer.map(longValue, 256L, false, true)) {
         ByteBuffer byteBuffer = mappedView.data().order(ByteOrder.nativeOrder());
         matrix4f.get(byteBuffer);
      }

      return gpuBuffer.slice(longValue, 256L);
   }

   private static GpuBufferSlice m_1(ByteBuffer byteBuffer) {
      int value3 = value2++ % 16;
      GpuBuffer gpuBuffer2 = gpuBufferArray[value3];
      int value4 = byteBuffer.remaining();

      try (MappedView mappedView = gpuBuffer2.map(0L, value4, false, true)) {
         mappedView.data().put(byteBuffer);
      }

      return gpuBuffer2.slice(0L, value4);
   }

   private static void m_9_void() {
      if (!flag) {
         gpuSampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         gpuSampler2 = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty());
         flag = true;
      }
   }

   public static GpuSampler m_6_GpuSampler() {
      m_9_void();
      return gpuSampler2;
   }

   private static GpuSampler m_5_GpuSampler() {
      m_9_void();
      return gpuSampler;
   }

   private static void m_2(BufferBuilder bufferBuilder, float floatValue, float floatValue2, float floatValue3, int value3) {
      int value4 = value3 >> 24 & 0xFF;
      int value5 = value3 >> 16 & 0xFF;
      int value6 = value3 >> 8 & 0xFF;
      int value7 = value3 & 0xFF;
      float floatValue4 = floatValue + floatValue3;
      float floatValue5 = floatValue2 + floatValue3;
      bufferBuilder.addVertex(floatValue, floatValue5, 0.0F).setUv(0.0F, 1.0F).setColor(value5, value6, value7, value4);
      bufferBuilder.addVertex(floatValue4, floatValue5, 0.0F).setUv(1.0F, 1.0F).setColor(value5, value6, value7, value4);
      bufferBuilder.addVertex(floatValue4, floatValue2, 0.0F).setUv(1.0F, 0.0F).setColor(value5, value6, value7, value4);
      bufferBuilder.addVertex(floatValue, floatValue2, 0.0F).setUv(0.0F, 0.0F).setColor(value5, value6, value7, value4);
   }

   static {
      m_4(null);
   }

   public static void m_4(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_8_C3_sArr() {
      return moduleArray;
   }

   public record DataRecord(boolean f_9_n, boolean f_9_f) {
      public boolean m_9_n() {
         return this.f_9_n;
      }

      public boolean m_9_f() {
         return this.f_9_f;
      }
   }
}
