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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Line3DRenderer {
   private static RenderPipeline renderPipeline;
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "world/line3d");
   private static int value;

   private Line3DRenderer() {
   }

   public static void createPipeline() {
      renderPipeline = RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayout.builder().withUniform("LineParams", UniformType.UNIFORM_BUFFER).build())
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(IDENTIFIER)
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
         .withPrimitiveTopology(PrimitiveTopology.QUADS)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/line3d"))
         .build();
   }

   public static void drawLineStrip(Matrix4f matrix4f, List<Vec3> vec3List, Vec3 vec3, float floatValue, int value2) {
      if (renderPipeline == null) {
         createPipeline();
      }

      if (vec3List.size() >= 2) {
         uploadAndDraw(matrix4f, vec3List.size() - 1, bufferBuilder -> m_5(bufferBuilder, matrix4f, vec3List, vec3, floatValue, value2));
      }
   }

   public static void m_9(Matrix4f matrix4f, List<List<Vec3>> listList, Vec3 vec3, float floatValue, int value2) {
      if (renderPipeline == null) {
         createPipeline();
      }

      int value3 = 0;

      for (List list : listList) {
         value3 += Math.max(0, list.size() - 1);
      }

      uploadAndDraw(matrix4f, value3, bufferBuilder -> {
         int value4 = 0;

         for (List list2 : listList) {
            value4 += m_5(bufferBuilder, matrix4f, list2, vec3, floatValue, value2);
         }

         return value4;
      });
   }

   public static void drawLines(Matrix4f matrix4f, List<Vec3> vec3List, Vec3 vec3, float floatValue, int value2) {
      if (renderPipeline == null) {
         createPipeline();
      }

      if (vec3List.size() >= 2) {
         uploadAndDraw(matrix4f, vec3List.size() / 2, bufferBuilder -> m_4(bufferBuilder, matrix4f, vec3List, null, vec3, floatValue, value2));
      }
   }

   public static void m_7(Matrix4f matrix4f, List<Vec3> vec3List, int[] intArray, Vec3 vec3, float floatValue) {
      if (renderPipeline == null) {
         createPipeline();
      }

      if (vec3List.size() >= 2) {
         uploadAndDraw(matrix4f, vec3List.size() / 2, bufferBuilder -> m_4(bufferBuilder, matrix4f, vec3List, intArray, vec3, floatValue, -1));
      }
   }

   public static void m_1(Matrix4f matrix4f, List<Vec3> vec3List, int[] intArray, Vec3 vec3, float floatValue) {
      if (renderPipeline == null) {
         createPipeline();
      }

      if (vec3List.size() >= 2 && intArray.length >= vec3List.size()) {
         uploadAndDraw(matrix4f, vec3List.size() - 1, bufferBuilder -> m_2(bufferBuilder, matrix4f, vec3List, intArray, vec3, floatValue, false));
      }
   }

   public static void m_6(Matrix4f matrix4f, List<Vec3> vec3List, int[] intArray, Vec3 vec3, float floatValue) {
      if (renderPipeline == null) {
         createPipeline();
      }

      if (vec3List.size() >= 2 && intArray.length >= vec3List.size()) {
         int value2 = vec3List.size() - 1;
         uploadAndDraw(matrix4f, value2 * 36, bufferBuilder -> m_2(bufferBuilder, matrix4f, vec3List, intArray, vec3, floatValue, true));
      }
   }

   private static int m_2(BufferBuilder bufferBuilder, Matrix4f matrix4f, List<Vec3> vec3List, int[] intArray, Vec3 vec3, float floatValue, boolean flag) {
      Window window = Minecraft.getInstance().getWindow();
      float floatValue2 = window.getWidth() / 2.0F;
      float floatValue3 = window.getHeight() / 2.0F;
      float floatValue4 = Math.max(1.0F, floatValue);
      int value2 = vec3List.size();
      Vector4f[] vector4fArray = new Vector4f[value2];

      for (int i = 0; i < value2; i++) {
         vector4fArray[i] = project(matrix4f, (Vec3)vec3List.get(i), vec3);
      }

      ArrayList list = new ArrayList();
      ArrayList list2 = new ArrayList();
      int value3 = 0;

      for (int i2 = 0; i2 < value2 - 1; i2++) {
         Vector4f vector4f = new Vector4f(vector4fArray[i2]);
         Vector4f vector4f2 = new Vector4f(vector4fArray[i2 + 1]);
         boolean flag2 = vector4f.w > 0.06F;
         boolean flag3 = vector4f2.w > 0.06F;
         if (!clipLine(vector4f, vector4f2)) {
            value3 += m_5(bufferBuilder, list, list2, floatValue2, floatValue3, floatValue4, flag);
         } else {
            float floatValue5 = flag2 == flag3 ? 0.0F : (0.06F - vector4fArray[i2].w) / (vector4fArray[i2 + 1].w - vector4fArray[i2].w);
            int value4 = flag2 ? intArray[i2] : lerpColor(intArray[i2], intArray[i2 + 1], floatValue5);
            int value5 = flag3 ? intArray[i2 + 1] : lerpColor(intArray[i2], intArray[i2 + 1], floatValue5);
            if (list.isEmpty() || !flag2) {
               value3 += m_5(bufferBuilder, list, list2, floatValue2, floatValue3, floatValue4, flag);
               list.add(toNdc(vector4f, floatValue2, floatValue3));
               list2.add(value4);
            }

            list.add(toNdc(vector4f2, floatValue2, floatValue3));
            list2.add(value5);
            if (!flag3) {
               value3 += m_5(bufferBuilder, list, list2, floatValue2, floatValue3, floatValue4, flag);
            }
         }
      }

      return value3 + m_5(bufferBuilder, list, list2, floatValue2, floatValue3, floatValue4, flag);
   }

   private static float[] toNdc(Vector4f vector4f, float floatValue, float floatValue2) {
      return new float[]{vector4f.x / vector4f.w * floatValue, vector4f.y / vector4f.w * floatValue2, vector4f.z / vector4f.w};
   }

   private static int m_5(BufferBuilder bufferBuilder, List<float[]> arrayList, List<Integer> integerList, float floatValue, float floatValue2, float floatValue3, boolean flag) {
      ArrayList list = new ArrayList(arrayList.size());
      ArrayList<Integer> integerList2 = new ArrayList<>(arrayList.size());

      for (int i = 0; i < arrayList.size(); i++) {
         float[] floatArray = (float[])arrayList.get(i);
         if (!list.isEmpty()) {
            float[] floatArray2 = (float[])list.get(list.size() - 1);
            float floatValue4 = floatArray[0] - floatArray2[0];
            float floatValue5 = floatArray[1] - floatArray2[1];
            if (floatValue4 * floatValue4 + floatValue5 * floatValue5 < 1.0E-4F) {
               integerList2.set(integerList2.size() - 1, (Integer)integerList.get(i));
               continue;
            }
         }

         list.add(floatArray);
         integerList2.add((Integer)integerList.get(i));
      }

      arrayList.clear();
      integerList.clear();
      int value2 = list.size();
      if (value2 < 2) {
         return 0;
      }

      float[][] arrayArray = new float[value2][];
      float[][] arrayArray2 = new float[value2][];
      float[][] arrayArray3 = new float[value2][];

      for (int i2 = 0; i2 < value2; i2++) {
         float[] floatArray3 = (float[])list.get(i2);
         float[] floatArray4 = i2 > 0 ? m_9((float[])list.get(i2 - 1), floatArray3) : null;
         float[] floatArray5 = i2 < value2 - 1 ? m_9(floatArray3, (float[])list.get(i2 + 1)) : null;
         float floatValue6 = floatValue3;
         float floatValue7;
         float floatValue8;
         if (floatArray4 == null) {
            floatValue7 = floatArray5[0];
            floatValue8 = floatArray5[1];
         } else if (floatArray5 == null) {
            floatValue7 = floatArray4[0];
            floatValue8 = floatArray4[1];
         } else {
            floatValue7 = floatArray4[0] + floatArray5[0];
            floatValue8 = floatArray4[1] + floatArray5[1];
            float floatValue9 = (float)Math.sqrt(floatValue7 * floatValue7 + floatValue8 * floatValue8);
            if (floatValue9 < 0.001F) {
               floatValue7 = floatArray5[0];
               floatValue8 = floatArray5[1];
            } else {
               floatValue7 /= floatValue9;
               floatValue8 /= floatValue9;
               float floatValue10 = floatValue7 * floatArray5[0] + floatValue8 * floatArray5[1];
               floatValue6 = floatValue3 / Math.max(floatValue10, 0.5F);
            }
         }

         arrayArray[i2] = new float[]{(floatArray3[0] + floatValue7 * floatValue6) / floatValue, (floatArray3[1] + floatValue8 * floatValue6) / floatValue2, floatArray3[2]};
         arrayArray2[i2] = new float[]{(floatArray3[0] - floatValue7 * floatValue6) / floatValue, (floatArray3[1] - floatValue8 * floatValue6) / floatValue2, floatArray3[2]};
         arrayArray3[i2] = new float[]{floatValue7 * floatValue6, floatValue8 * floatValue6};
      }

      if (flag) {
         return m_0(bufferBuilder, list, integerList2, arrayArray3, floatValue, floatValue2, floatValue3);
      }

      for (int i3 = 0; i3 < value2 - 1; i3++) {
         int value3 = (Integer)integerList2.get(i3);
         int value4 = (Integer)integerList2.get(i3 + 1);
         m_9(bufferBuilder, arrayArray[i3][0], arrayArray[i3][1], arrayArray[i3][2], value3);
         m_9(bufferBuilder, arrayArray2[i3][0], arrayArray2[i3][1], arrayArray2[i3][2], value3);
         m_9(bufferBuilder, arrayArray2[i3 + 1][0], arrayArray2[i3 + 1][1], arrayArray2[i3 + 1][2], value4);
         m_9(bufferBuilder, arrayArray[i3 + 1][0], arrayArray[i3 + 1][1], arrayArray[i3 + 1][2], value4);
      }

      return value2 - 1;
   }

   private static int m_0(BufferBuilder bufferBuilder, List<float[]> arrayList, List<Integer> integerList, float[][] arrayArray, float floatValue, float floatValue2, float floatValue3) {
      int value2 = arrayList.size();
      int value3 = 0;

      for (int i = 0; i < value2 - 1; i++) {
         float[] floatArray = (float[])arrayList.get(i);
         float[] floatArray2 = (float[])arrayList.get(i + 1);
         float[] floatArray3 = arrayArray[i];
         float[] floatArray4 = arrayArray[i + 1];
         int value4 = (Integer)integerList.get(i);
         int value5 = (Integer)integerList.get(i + 1);
         int value6 = ensureAlpha(value4);
         int value7 = ensureAlpha(value5);
         int value8 = value4 & 16777215;
         int value9 = value5 & 16777215;

         for (byte byteValue = -1; byteValue <= 1; byteValue += 2) {
            m_4(bufferBuilder, floatArray, floatArray3, 0.0F, floatValue, floatValue2, value6);
            m_4(bufferBuilder, floatArray, floatArray3, byteValue * 0.45F, floatValue, floatValue2, value4);
            m_4(bufferBuilder, floatArray2, floatArray4, byteValue * 0.45F, floatValue, floatValue2, value5);
            m_4(bufferBuilder, floatArray2, floatArray4, 0.0F, floatValue, floatValue2, value7);
            m_4(bufferBuilder, floatArray, floatArray3, byteValue * 0.45F, floatValue, floatValue2, value4);
            m_4(bufferBuilder, floatArray, floatArray3, byteValue, floatValue, floatValue2, value8);
            m_4(bufferBuilder, floatArray2, floatArray4, byteValue, floatValue, floatValue2, value9);
            m_4(bufferBuilder, floatArray2, floatArray4, byteValue * 0.45F, floatValue, floatValue2, value5);
            value3 += 2;
         }
      }

      value3 += m_7(bufferBuilder, (float[])arrayList.get(0), (float[])arrayList.get(1), (Integer)integerList.get(0), floatValue, floatValue2, floatValue3);
      return value3 + m_7(bufferBuilder, (float[])arrayList.get(value2 - 1), (float[])arrayList.get(value2 - 2), (Integer)integerList.get(value2 - 1), floatValue, floatValue2, floatValue3);
   }

   private static int m_7(BufferBuilder bufferBuilder, float[] floatArray, float[] floatArray2, int value2, float floatValue, float floatValue2, float floatValue3) {
      if ((value2 >>> 24 & 0xFF) == 0) {
         return 0;
      }

      float floatValue4 = floatArray[0] - floatArray2[0];
      float floatValue5 = floatArray[1] - floatArray2[1];
      float floatValue6 = (float)Math.sqrt(floatValue4 * floatValue4 + floatValue5 * floatValue5);
      if (floatValue6 < 1.0E-5F) {
         return 0;
      }

      floatValue4 /= floatValue6;
      floatValue5 /= floatValue6;
      float floatValue7 = -floatValue5;
      float floatValue8 = floatValue4;
      int value3 = ensureAlpha(value2);
      int value4 = value2 & 16777215;
      byte byteValue = 0;

      for (int i = 0; i < 8; i++) {
         double doubleValue = Math.PI * i / 8.0;
         double doubleValue2 = Math.PI * (i + 1) / 8.0;
         float[] floatArray3 = new float[]{
            (float)(floatValue7 * Math.cos(doubleValue) + floatValue4 * Math.sin(doubleValue)) * floatValue3, (float)(floatValue8 * Math.cos(doubleValue) + floatValue5 * Math.sin(doubleValue)) * floatValue3
         };
         float[] floatArray4 = new float[]{
            (float)(floatValue7 * Math.cos(doubleValue2) + floatValue4 * Math.sin(doubleValue2)) * floatValue3, (float)(floatValue8 * Math.cos(doubleValue2) + floatValue5 * Math.sin(doubleValue2)) * floatValue3
         };
         m_4(bufferBuilder, floatArray, floatArray3, 0.0F, floatValue, floatValue2, value3);
         m_4(bufferBuilder, floatArray, floatArray3, 0.45F, floatValue, floatValue2, value2);
         m_4(bufferBuilder, floatArray, floatArray4, 0.45F, floatValue, floatValue2, value2);
         m_4(bufferBuilder, floatArray, floatArray4, 0.0F, floatValue, floatValue2, value3);
         m_4(bufferBuilder, floatArray, floatArray3, 0.45F, floatValue, floatValue2, value2);
         m_4(bufferBuilder, floatArray, floatArray3, 1.0F, floatValue, floatValue2, value4);
         m_4(bufferBuilder, floatArray, floatArray4, 1.0F, floatValue, floatValue2, value4);
         m_4(bufferBuilder, floatArray, floatArray4, 0.45F, floatValue, floatValue2, value2);
         byteValue += 2;
      }

      return byteValue;
   }

   private static void m_4(BufferBuilder bufferBuilder, float[] floatArray, float[] floatArray2, float floatValue, float floatValue2, float floatValue3, int value2) {
      m_9(bufferBuilder, (floatArray[0] + floatArray2[0] * floatValue) / floatValue2, (floatArray[1] + floatArray2[1] * floatValue) / floatValue3, floatArray[2], value2);
   }

   private static int ensureAlpha(int value2) {
      int value3 = value2 & 0xFF000000;
      int value4 = value2 >> 16 & 0xFF;
      int value5 = value2 >> 8 & 0xFF;
      int value6 = value2 & 0xFF;
      value4 += (int)((255 - value4) * 0.35F);
      value5 += (int)((255 - value5) * 0.35F);
      value6 += (int)((255 - value6) * 0.35F);
      return value3 | value4 << 16 | value5 << 8 | value6;
   }

   private static float[] m_9(float[] floatArray, float[] floatArray2) {
      float floatValue = floatArray2[0] - floatArray[0];
      float floatValue2 = floatArray2[1] - floatArray[1];
      float floatValue3 = (float)Math.sqrt(floatValue * floatValue + floatValue2 * floatValue2);
      return new float[]{-floatValue2 / floatValue3, floatValue / floatValue3};
   }

   private static int lerpColor(int value2, int value3, float floatValue) {
      int value4 = (int)((value2 >>> 24 & 0xFF) + ((value3 >>> 24 & 0xFF) - (value2 >>> 24 & 0xFF)) * floatValue);
      int value5 = (int)((value2 >> 16 & 0xFF) + ((value3 >> 16 & 0xFF) - (value2 >> 16 & 0xFF)) * floatValue);
      int value6 = (int)((value2 >> 8 & 0xFF) + ((value3 >> 8 & 0xFF) - (value2 >> 8 & 0xFF)) * floatValue);
      int value7 = (int)((value2 & 0xFF) + ((value3 & 0xFF) - (value2 & 0xFF)) * floatValue);
      return value4 << 24 | value5 << 16 | value6 << 8 | value7;
   }

   private static void m_9(BufferBuilder bufferBuilder, float floatValue, float floatValue2, float floatValue3, int value2) {
      bufferBuilder.addVertex(floatValue, floatValue2, floatValue3).setColor(value2 >> 16 & 0xFF, value2 >> 8 & 0xFF, value2 & 0xFF, value2 >>> 24 & 0xFF);
   }

   private static void uploadAndDraw(Matrix4f matrix4f, int value2, ToIntFunction<BufferBuilder> toIntFunction) {
      if (value2 > 0) {
         GpuBuffer gpuBuffer = null;
         GpuBuffer gpuBuffer2 = null;
         ByteBufferBuilder byteBufferBuilder = null;

         try {
            byteBufferBuilder = new ByteBufferBuilder(value2 * 4 * DefaultVertexFormat.POSITION_COLOR.getVertexSize());
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
            int value3 = toIntFunction.applyAsInt(bufferBuilder);
            if (value3 == 0) {
               return;
            }

            MeshData meshData = bufferBuilder.build();
            if (meshData != null) {
               gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Line3D Vertices", 32, meshData.vertexBuffer());
               meshData.close();
               gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "Line3D Params", 130, 256L);

               try (MappedView mappedView = gpuBuffer2.map(false, true)) {
                  ByteBuffer byteBuffer = mappedView.data().order(ByteOrder.nativeOrder());
                  matrix4f.get(byteBuffer);
               }

               Minecraft mc = Minecraft.getInstance();
               RenderTarget renderTarget = mc.gameRenderer.mainRenderTarget();
               GpuDevice gpuDevice = RenderSystem.getDevice();
               gpuDevice.precompilePipeline(renderPipeline);

               try (RenderPass renderPass = gpuDevice.createCommandEncoder().createRenderPass(() -> "Onetap Line3D", renderTarget.getColorTextureView(), Optional.empty())) {
                  renderPass.setPipeline(renderPipeline);
                  renderPass.setUniform("LineParams", gpuBuffer2.slice());
                  renderPass.setVertexBuffer(0, gpuBuffer.slice());
                  AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
                  renderPass.setIndexBuffer(autoStorageIndexBuffer.getBuffer(value3 * 6), autoStorageIndexBuffer.type());
                  renderPass.drawIndexed(value3 * 6, 1, 0, 0, 0);
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

   private static int m_4(BufferBuilder bufferBuilder, Matrix4f matrix4f, List<Vec3> vec3List, int[] intArray, Vec3 vec3, float floatValue, int value2) {
      Minecraft mc = Minecraft.getInstance();
      float floatValue2 = Math.max(1.0F, floatValue) / mc.getWindow().getWidth() * 2.0F;
      float floatValue3 = Math.max(1.0F, floatValue) / mc.getWindow().getHeight() * 2.0F;
      int value3 = 0;
      int vertexIndex = 0;

      for (int i = 0; vertexIndex + 1 < vec3List.size(); i++) {
         int value4 = intArray != null ? intArray[i] : value2;
         int value5 = value4 >> 16 & 0xFF;
         int value6 = value4 >> 8 & 0xFF;
         int value7 = value4 & 0xFF;
         int value8 = value4 >> 24 & 0xFF;
         Vector4f vector4f = project(matrix4f, (Vec3)vec3List.get(vertexIndex), vec3);
         Vector4f vector4f2 = project(matrix4f, (Vec3)vec3List.get(vertexIndex + 1), vec3);
         if (clipLine(vector4f, vector4f2)) {
            vector4f.div(vector4f.w);
            vector4f2.div(vector4f2.w);
            float floatValue4 = vector4f2.x - vector4f.x;
            float floatValue5 = vector4f2.y - vector4f.y;
            float floatValue6 = (float)Math.sqrt(floatValue4 * floatValue4 + floatValue5 * floatValue5);
            if (!(floatValue6 < 1.0E-5F)) {
               float floatValue7 = -floatValue5 / floatValue6 * floatValue2;
               float floatValue8 = floatValue4 / floatValue6 * floatValue3;
               addVertex(bufferBuilder, vector4f.x + floatValue7, vector4f.y + floatValue8, vector4f.z, value5, value6, value7, value8);
               addVertex(bufferBuilder, vector4f.x - floatValue7, vector4f.y - floatValue8, vector4f.z, value5, value6, value7, value8);
               addVertex(bufferBuilder, vector4f2.x - floatValue7, vector4f2.y - floatValue8, vector4f2.z, value5, value6, value7, value8);
               addVertex(bufferBuilder, vector4f2.x + floatValue7, vector4f2.y + floatValue8, vector4f2.z, value5, value6, value7, value8);
               value3++;
            }
         }

         vertexIndex += 2;
      }

      return value3;
   }

   private static int m_5(BufferBuilder bufferBuilder, Matrix4f matrix4f, List<Vec3> vec3List, Vec3 vec3, float floatValue, int value2) {
      Minecraft mc = Minecraft.getInstance();
      float floatValue2 = Math.max(1.0F, floatValue) / mc.getWindow().getWidth() * 2.0F;
      float floatValue3 = Math.max(1.0F, floatValue) / mc.getWindow().getHeight() * 2.0F;
      int value3 = value2 >> 16 & 0xFF;
      int value4 = value2 >> 8 & 0xFF;
      int value5 = value2 & 0xFF;
      int value6 = value2 >> 24 & 0xFF;
      int value7 = Math.max(2, Math.min(8, vec3List.size() / 3));
      int value8 = 0;

      for (int i = 0; i < vec3List.size() - 1; i++) {
         Vector4f vector4f = project(matrix4f, (Vec3)vec3List.get(i), vec3);
         Vector4f vector4f2 = project(matrix4f, (Vec3)vec3List.get(i + 1), vec3);
         if (clipLine(vector4f, vector4f2)) {
            vector4f.div(vector4f.w);
            vector4f2.div(vector4f2.w);
            float floatValue4 = vector4f2.x - vector4f.x;
            float floatValue5 = vector4f2.y - vector4f.y;
            float floatValue6 = (float)Math.sqrt(floatValue4 * floatValue4 + floatValue5 * floatValue5);
            if (!(floatValue6 < 1.0E-5F)) {
               float floatValue7 = -floatValue5 / floatValue6 * floatValue2;
               float floatValue8 = floatValue4 / floatValue6 * floatValue3;
               int value9 = (int)(value6 * fadeFactor(i, vec3List.size() - 1, value7));
               int value10 = (int)(value6 * fadeFactor(i + 1, vec3List.size() - 1, value7));
               addVertex(bufferBuilder, vector4f.x + floatValue7, vector4f.y + floatValue8, vector4f.z, value3, value4, value5, value9);
               addVertex(bufferBuilder, vector4f.x - floatValue7, vector4f.y - floatValue8, vector4f.z, value3, value4, value5, value9);
               addVertex(bufferBuilder, vector4f2.x - floatValue7, vector4f2.y - floatValue8, vector4f2.z, value3, value4, value5, value10);
               addVertex(bufferBuilder, vector4f2.x + floatValue7, vector4f2.y + floatValue8, vector4f2.z, value3, value4, value5, value10);
               value8++;
            }
         }
      }

      return value8;
   }

   private static Vector4f project(Matrix4f matrix4f, Vec3 vec3, Vec3 vec32) {
      Vector4f vector4f = new Vector4f((float)(vec3.x - vec32.x), (float)(vec3.y - vec32.y), (float)(vec3.z - vec32.z), 1.0F);
      matrix4f.transform(vector4f);
      return vector4f;
   }

   private static boolean clipLine(Vector4f vector4f, Vector4f vector4f2) {
      boolean flag = vector4f.w > 0.06F;
      boolean flag2 = vector4f2.w > 0.06F;
      if (!flag && !flag2) {
         return false;
      }

      if (flag && flag2) {
         return true;
      }

      float floatValue = (0.06F - vector4f.w) / (vector4f2.w - vector4f.w);
      float floatValue2 = vector4f.x + (vector4f2.x - vector4f.x) * floatValue;
      float floatValue3 = vector4f.y + (vector4f2.y - vector4f.y) * floatValue;
      float floatValue4 = vector4f.z + (vector4f2.z - vector4f.z) * floatValue;
      if (!flag) {
         vector4f.set(floatValue2, floatValue3, floatValue4, 0.06F);
      } else {
         vector4f2.set(floatValue2, floatValue3, floatValue4, 0.06F);
      }

      return true;
   }

   private static float fadeFactor(int value2, int value3, int value4) {
      return value3 <= 1 ? 1.0F : Math.min(1.0F, (float)Math.min(value2, value3 - value2) / value4);
   }

   private static void addVertex(BufferBuilder bufferBuilder, float floatValue, float floatValue2, float floatValue3, int value2, int value3, int value4, int value5) {
      bufferBuilder.addVertex(floatValue, floatValue2, floatValue3).setColor(value2, value3, value4, value5);
   }

   static {
      m_1_void(67);
   }

   public static void m_1_void(int value2) {
      value = value2;
   }

   public static int m_5_int() {
      return value;
   }

   public static int m_9_int() {
      return 0;
   }
}
