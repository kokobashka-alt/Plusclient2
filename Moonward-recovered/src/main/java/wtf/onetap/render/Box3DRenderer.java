package wtf.onetap.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class Box3DRenderer {
   private static RenderPipeline renderPipeline;
   private static RenderPipeline renderPipeline2;
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "world/box3d");
   private static final Optional<Vector4fc> OPTIONAL = Optional.of(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F));
   private static GpuBuffer gpuBuffer;
   private static int value;
   private static GpuBuffer[] gpuBufferArray;
   private static int value2;
   private static boolean flag;
   private static int value3;

   private Box3DRenderer() {
   }

   public static void m_3_void() {
      renderPipeline = m_9(BlendFunction.TRANSLUCENT, "pipeline/box3d");
      renderPipeline2 = m_9(BlendFunction.OVERLAY, "pipeline/box3d_additive");
      m_9_void();
   }

   private static RenderPipeline m_9(BlendFunction blendFunction, String text) {
      return RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayout.builder().withUniform("BoxParams", UniformType.UNIFORM_BUFFER).build())
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(IDENTIFIER)
         .withColorTargetState(new ColorTargetState(blendFunction))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
         .withPrimitiveTopology(PrimitiveTopology.QUADS)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", text))
         .build();
   }

   public static void drawBox(Matrix4f matrix4f, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value4) {
      if (renderPipeline == null) {
         m_3_void();
      }

      int value5 = value4 >> 16 & 0xFF;
      int value6 = value4 >> 8 & 0xFF;
      int value7 = value4 & 0xFF;
      int value8 = value4 >> 24 & 0xFF;
      m_5(renderPipeline, matrix4f, 24, bufferBuilder -> m_7(bufferBuilder, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, value5, value6, value7, value8), getColorTextureView(), Optional.empty());
   }

   public static void m_8(Matrix4f matrix4f, int value4, Consumer<BufferBuilder> consumer) {
      if (renderPipeline == null) {
         m_3_void();
      }

      if (value4 >= 4) {
         m_5(renderPipeline, matrix4f, value4, consumer, getColorTextureView(), Optional.empty());
      }
   }

   public static void m_4(Matrix4f matrix4f, int value4, Consumer<BufferBuilder> consumer) {
      if (renderPipeline == null) {
         m_3_void();
      }

      if (value4 >= 4) {
         m_5(renderPipeline2, matrix4f, value4, consumer, getColorTextureView(), Optional.empty());
      }
   }

   public static void m_7(GpuTextureView gpuTextureView, boolean flag2, Matrix4f matrix4f, int value4, Consumer<BufferBuilder> consumer) {
      if (renderPipeline == null) {
         m_3_void();
      }

      if (value4 >= 4) {
         m_5(renderPipeline, matrix4f, value4, consumer, gpuTextureView, flag2 ? OPTIONAL : Optional.empty());
      }
   }

   private static GpuTextureView getColorTextureView() {
      return Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTextureView();
   }

   private static void m_5(RenderPipeline renderPipeline3, Matrix4f matrix4f, int value4, Consumer<BufferBuilder> consumer, GpuTextureView gpuTextureView, Optional<Vector4fc> optional) {
      ByteBufferBuilder byteBufferBuilder = null;
      GpuBuffer gpuBuffer2 = null;

      try {
         byteBufferBuilder = new ByteBufferBuilder(value4 * DefaultVertexFormat.POSITION_COLOR.getVertexSize());
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
         consumer.accept(bufferBuilder);
         MeshData meshData = bufferBuilder.build();
         if (meshData == null) {
            return;
         }

         int value5 = meshData.drawState().indexCount();
         if (value5 > 0) {
            m_2_void();
            m_7_void();
            GpuBufferSlice gpuBufferSlice = m_1(matrix4f);
            ByteBuffer byteBuffer = meshData.vertexBuffer();
            int value6 = byteBuffer.remaining();
            GpuBufferSlice gpuBufferSlice2;
            if (value6 > (int)-7695766995618168832L) {
               gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "Box3D Vertices", 32, byteBuffer);
               gpuBufferSlice2 = gpuBuffer2.slice();
            } else {
               gpuBufferSlice2 = m_7(byteBuffer);
            }

            meshData.close();

            try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Onetap Box3D", gpuTextureView, optional)) {
               renderPass.setPipeline(renderPipeline3);
               renderPass.setUniform("BoxParams", gpuBufferSlice);
               renderPass.setVertexBuffer(0, gpuBufferSlice2);
               AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
               renderPass.setIndexBuffer(autoStorageIndexBuffer.getBuffer(value5), autoStorageIndexBuffer.type());
               renderPass.drawIndexed(value5, 1, 0, 0, 0);
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

   private static void m_9_void() {
      if (!flag && renderPipeline != null) {
         RenderSystem.getDevice().precompilePipeline(renderPipeline);
         RenderSystem.getDevice().precompilePipeline(renderPipeline2);
         flag = true;
      }
   }

   private static void m_2_void() {
      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Box3D Params", 130, 4096L);
         value = 0;
      }
   }

   private static void m_7_void() {
      if (gpuBufferArray != null) {
         boolean flag2 = true;

         for (GpuBuffer gpuBuffer2 : gpuBufferArray) {
            if (gpuBuffer2 == null || gpuBuffer2.isClosed()) {
               flag2 = false;
               break;
            }
         }

         if (flag2) {
            return;
         }
      }

      gpuBufferArray = new GpuBuffer[16];
      GpuDevice gpuDevice = RenderSystem.getDevice();

      for (int i = 0; i < 16; i++) {
         gpuBufferArray[i] = gpuDevice.createBuffer(() -> "Box3D Vertices", 34, 65536L);
      }

      value2 = 0;
   }

   private static GpuBufferSlice m_1(Matrix4f matrix4f) {
      int value4 = value++ % 16;
      long longValue = value4 * 256L;

      try (MappedView mappedView = gpuBuffer.map(longValue, 256L, false, true)) {
         ByteBuffer byteBuffer = mappedView.data().order(ByteOrder.nativeOrder());
         matrix4f.get(byteBuffer);
      }

      return gpuBuffer.slice(longValue, 256L);
   }

   private static GpuBufferSlice m_7(ByteBuffer byteBuffer) {
      int value4 = value2++ % 16;
      GpuBuffer gpuBuffer2 = gpuBufferArray[value4];
      int value5 = byteBuffer.remaining();

      try (MappedView mappedView = gpuBuffer2.map(0L, value5, false, true)) {
         mappedView.data().put(byteBuffer);
      }

      return gpuBuffer2.slice(0L, value5);
   }

   public static void m_7(BufferBuilder bufferBuilder, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, int value4, int value5, int value6, int value7) {
      bufferBuilder.addVertex(floatValue, floatValue2, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue2, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue2, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue2, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue5, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue5, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue5, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue5, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue2, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue2, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue5, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue5, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue5, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue5, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue2, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue2, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue2, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue5, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue5, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue, floatValue2, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue2, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue5, floatValue6).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue5, floatValue3).setColor(value4, value5, value6, value7);
      bufferBuilder.addVertex(floatValue4, floatValue2, floatValue3).setColor(value4, value5, value6, value7);
   }

   static {
      m_9_void(96);
   }

   public static void m_9_void(int value4) {
      value3 = value4;
   }

   public static int m_3_int() {
      return value3;
   }

   public static int m_4_int() {
      return 0;
   }
}
