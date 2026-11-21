package wtf.onetap.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class BoltRenderer {
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "world/bolt3d");
   private static RenderPipeline renderPipeline;
   private static final Vector4f VECTOR4F = new Vector4f();
   private static float[] floatValueArray = new float[64];
   private static float[] floatValueArray2 = new float[64];
   private static float[] floatValueArray3 = new float[64];
   private static int[] valueArray = new int[64];
   private static float[] floatValueArray4 = new float[64];
   private static float[] floatValueArray5 = new float[64];
   private static float[] floatValueArray6 = new float[64];
   private static float[] floatValueArray7 = new float[64];

   private BoltRenderer() {
   }

   public static void m_0_void() {
      renderPipeline = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayout.builder().withUniform("LineParams", UniformType.UNIFORM_BUFFER).build())
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(IDENTIFIER)
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
         .withPrimitiveTopology(PrimitiveTopology.QUADS)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/bolt3d"))
         .build();
   }

   public static void m_8(Matrix4f matrix4f, Vec3 vec3, List<BoltRenderer.DataRecord> c7List, float floatValue) {
      if (renderPipeline == null) {
         m_0_void();
      }

      int value = 0;

      for (BoltRenderer.DataRecord c7 : c7List) {
         value += Math.max(0, c7.m_0_i() - 1);
      }

      if (value > 0) {
         GpuBuffer gpuBuffer = null;
         GpuBuffer gpuBuffer2 = null;
         ByteBufferBuilder byteBufferBuilder = null;

         try {
            byteBufferBuilder = new ByteBufferBuilder(value * 4 * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize());
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            int value2 = 0;

            for (BoltRenderer.DataRecord c72 : c7List) {
               value2 += m_3(bufferBuilder, matrix4f, vec3, c72, floatValue);
            }

            if (value2 == 0) {
               return;
            }

            MeshData meshData = bufferBuilder.build();
            if (meshData != null) {
               gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Bolt3D Vertices", 32, meshData.vertexBuffer());
               meshData.close();
               gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "Bolt3D Params", 130, 256L);

               try (MappedView mappedView = gpuBuffer2.map(false, true)) {
                  ByteBuffer byteBuffer = mappedView.data().order(ByteOrder.nativeOrder());
                  matrix4f.get(byteBuffer);
               }

               RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
               GpuDevice gpuDevice = RenderSystem.getDevice();
               gpuDevice.precompilePipeline(renderPipeline);

               try (RenderPass renderPass = gpuDevice.createCommandEncoder().createRenderPass(() -> "Onetap Bolt3D", renderTarget.getColorTextureView(), Optional.empty())) {
                  renderPass.setPipeline(renderPipeline);
                  renderPass.setUniform("LineParams", gpuBuffer2.slice());
                  renderPass.setVertexBuffer(0, gpuBuffer.slice());
                  AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
                  renderPass.setIndexBuffer(autoStorageIndexBuffer.getBuffer(value2 * 6), autoStorageIndexBuffer.type());
                  renderPass.drawIndexed(value2 * 6, 1, 0, 0, 0);
                  return;
               }
            }
         } finally {
            if (gpuBuffer != null) {
               gpuBuffer.close();
            }

            if (gpuBuffer2 != null) {
               gpuBuffer2.close();
            }

            if (byteBufferBuilder != null) {
               byteBufferBuilder.close();
            }
         }
      }
   }

   private static int m_3(BufferBuilder bufferBuilder, Matrix4f matrix4f, Vec3 vec3, BoltRenderer.DataRecord c7, float floatValue) {
      Window window = Minecraft.getInstance().getWindow();
      float floatValue2 = window.getWidth() * 0.5F;
      float floatValue3 = window.getHeight() * 0.5F;
      m_5_void(c7.m_0_i());
      int value = 0;
      int value2 = 0;

      for (int i = 0; i <= c7.m_0_i(); i++) {
         boolean flag = false;
         if (i < c7.m_0_i()) {
            VECTOR4F.set(
               (float)(c7.m_6w_DArr()[i * 3] - vec3.x),
               (float)(c7.m_6w_DArr()[i * 3 + 1] - vec3.y),
               (float)(c7.m_6w_DArr()[i * 3 + 2] - vec3.z),
               1.0F
            );
            matrix4f.transform(VECTOR4F);
            flag = VECTOR4F.w > 0.05F;
            if (flag) {
               float floatValue4 = VECTOR4F.x / VECTOR4F.w * floatValue2;
               float floatValue5 = VECTOR4F.y / VECTOR4F.w * floatValue3;
               if (value > 0) {
                  float floatValue6 = floatValue4 - floatValueArray[value - 1];
                  float floatValue7 = floatValue5 - floatValueArray2[value - 1];
                  if (floatValue6 * floatValue6 + floatValue7 * floatValue7 < 0.25F) {
                     continue;
                  }
               }

               floatValueArray[value] = floatValue4;
               floatValueArray2[value] = floatValue5;
               floatValueArray3[value] = c7.m_75()[i];
               valueArray[value] = c7.m_5e()[i];
               value++;
            }
         }

         if (!flag) {
            value2 += m_5(bufferBuilder, value, floatValue2, floatValue3, floatValue);
            value = 0;
         }
      }

      return value2;
   }

   private static int m_5(BufferBuilder bufferBuilder, int value, float floatValue, float floatValue2, float floatValue3) {
      if (value < 2) {
         return 0;
      }

      for (int i = 0; i < value; i++) {
         float floatValue4 = 0.0F;
         float floatValue5 = 0.0F;
         float floatValue6 = 0.0F;
         float floatValue7 = 0.0F;
         boolean flag = i > 0;
         boolean flag2 = i < value - 1;
         if (flag) {
            float floatValue8 = floatValueArray[i] - floatValueArray[i - 1];
            float floatValue9 = floatValueArray2[i] - floatValueArray2[i - 1];
            float floatValue10 = (float)Math.sqrt(floatValue8 * floatValue8 + floatValue9 * floatValue9);
            floatValue4 = floatValue8 / floatValue10;
            floatValue5 = floatValue9 / floatValue10;
         }

         if (flag2) {
            float floatValue11 = floatValueArray[i + 1] - floatValueArray[i];
            float floatValue12 = floatValueArray2[i + 1] - floatValueArray2[i];
            float floatValue13 = (float)Math.sqrt(floatValue11 * floatValue11 + floatValue12 * floatValue12);
            floatValue6 = floatValue11 / floatValue13;
            floatValue7 = floatValue12 / floatValue13;
         }

         float floatValue14;
         float floatValue15;
         if (flag && flag2) {
            floatValue14 = floatValue4 + floatValue6;
            floatValue15 = floatValue5 + floatValue7;
            float floatValue16 = (float)Math.sqrt(floatValue14 * floatValue14 + floatValue15 * floatValue15);
            if (floatValue16 < 0.001F) {
               floatValue14 = floatValue6;
               floatValue15 = floatValue7;
            } else {
               floatValue14 /= floatValue16;
               floatValue15 /= floatValue16;
            }
         } else if (flag2) {
            floatValue14 = floatValue6;
            floatValue15 = floatValue7;
         } else {
            floatValue14 = floatValue4;
            floatValue15 = floatValue5;
         }

         float floatValue17 = -floatValue15;
         float floatValue18 = floatValue14;
         float floatValue19 = 1.0F;
         if (flag && flag2) {
            float floatValue20 = floatValue17 * -floatValue7 + floatValue18 * floatValue6;
            floatValue19 = Math.min(2.5F, 1.0F / Math.max(floatValue20, 0.4F));
         }

         float floatValue21 = floatValueArray3[i] * floatValue19;
         floatValueArray4[i] = (floatValueArray[i] + floatValue17 * floatValue21) / floatValue;
         floatValueArray5[i] = (floatValueArray2[i] + floatValue18 * floatValue21) / floatValue2;
         floatValueArray6[i] = (floatValueArray[i] - floatValue17 * floatValue21) / floatValue;
         floatValueArray7[i] = (floatValueArray2[i] - floatValue18 * floatValue21) / floatValue2;
      }

      for (int i2 = 0; i2 < value - 1; i2++) {
         float floatValue22 = m_3_float(floatValue3, floatValueArray3[i2]);
         float floatValue23 = m_3_float(floatValue3, floatValueArray3[i2 + 1]);
         m_2(bufferBuilder, floatValueArray4[i2], floatValueArray5[i2], floatValue22, -1.0F, valueArray[i2]);
         m_2(bufferBuilder, floatValueArray6[i2], floatValueArray7[i2], floatValue22, 1.0F, valueArray[i2]);
         m_2(bufferBuilder, floatValueArray6[i2 + 1], floatValueArray7[i2 + 1], floatValue23, 1.0F, valueArray[i2 + 1]);
         m_2(bufferBuilder, floatValueArray4[i2 + 1], floatValueArray5[i2 + 1], floatValue23, -1.0F, valueArray[i2 + 1]);
      }

      return value - 1;
   }

   private static float m_3_float(float floatValue, float floatValue2) {
      return Math.min(0.6F, Math.max(0.06F, floatValue / Math.max(floatValue2, 0.01F)));
   }

   private static void m_2(BufferBuilder bufferBuilder, float floatValue, float floatValue2, float floatValue3, float floatValue4, int value) {
      bufferBuilder.addVertex(floatValue, floatValue2, 0.5F).setUv(floatValue3, floatValue4).setColor(value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF, value >>> 24 & 0xFF);
   }

   private static void m_5_void(int value) {
      if (value > floatValueArray.length) {
         int value2 = Math.max(value, floatValueArray.length * 2);
         floatValueArray = new float[value2];
         floatValueArray2 = new float[value2];
         floatValueArray3 = new float[value2];
         valueArray = new int[value2];
         floatValueArray4 = new float[value2];
         floatValueArray5 = new float[value2];
         floatValueArray6 = new float[value2];
         floatValueArray7 = new float[value2];
      }
   }

   public record DataRecord(double[] f_6w, float[] f_75, int[] f_5e, int f_0_i) {
      public double[] m_6w_DArr() {
         return this.f_6w;
      }

      public float[] m_75() {
         return this.f_75;
      }

      public int[] m_5e() {
         return this.f_5e;
      }

      public int m_0_i() {
         return this.f_0_i;
      }
   }
}
