package wtf.onetap.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
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
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;

public class WeatherRenderer {
   private static final float[] FLOAT_VALUE_ARRAY = new float[]{Float.intBitsToFloat(1084227584), Float.intBitsToFloat(1091567616), Float.intBitsToFloat(1097859072)};
   private static final RenderPipeline[] RENDER_PIPELINE_ARRAY = new RenderPipeline[WeatherRenderer.ModeKind.values().length];
   private static final boolean[] FLAG_ARRAY = new boolean[WeatherRenderer.ModeKind.values().length];
   private static final float[] FLOAT_VALUE_ARRAY2 = new float[56];
   private static GpuSampler gpuSampler;
   private static GpuSampler gpuSampler2;
   private static GpuSampler gpuSampler3;
   private static GpuBuffer gpuBuffer;
   private static GpuBuffer gpuBuffer2;
   private static int value;
   private static GpuTexture gpuTexture;
   private static GpuTextureView gpuTextureView;
   private static int value2 = -1;
   private static int value3 = -1;
   private static GpuTexture gpuTexture2;
   private static GpuTextureView gpuTextureView2;
   private static GpuTexture gpuTexture3;
   private static GpuTextureView gpuTextureView3;
   private static boolean flag;
   private static float floatValue;
   private static float floatValue2;
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "weather/weather");
   private static final Identifier IDENTIFIER2 = Identifier.fromNamespaceAndPath("onetap", "weather/drops");
   private static final Identifier IDENTIFIER3 = Identifier.fromNamespaceAndPath("onetap", "weather/lens");

   private WeatherRenderer() {
   }

   public static void m_4_void() {
      BindGroupLayout bindGroupLayout = BindGroupLayout.builder().withUniform("WeatherParams", UniformType.UNIFORM_BUFFER).build();
      BindGroupLayout bindGroupLayout2 = BindGroupLayout.builder().withSampler("Sampler3").build();
      ColorTargetState colorTargetState = new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA);
      RENDER_PIPELINE_ARRAY[WeatherRenderer.ModeKind.SURFACE.ordinal()] = m_6("weather", ColorTargetState.DEFAULT, bindGroupLayout, bindGroupLayout2, "USE_PUDDLE_MAP");
      RENDER_PIPELINE_ARRAY[WeatherRenderer.ModeKind.MIST.ordinal()] = m_6("weather_mist", colorTargetState, bindGroupLayout, null, "MIST_ONLY");
      RENDER_PIPELINE_ARRAY[WeatherRenderer.ModeKind.BAKE.ordinal()] = m_6("weather_puddles", new ColorTargetState(Optional.empty(), GpuFormat.RGBA16_FLOAT, 15), bindGroupLayout, null, "PUDDLE_BAKE");
      RENDER_PIPELINE_ARRAY[WeatherRenderer.ModeKind.DROPS.ordinal()] = m_1("weather_drops", IDENTIFIER2, colorTargetState, bindGroupLayout);
      RENDER_PIPELINE_ARRAY[WeatherRenderer.ModeKind.LENS.ordinal()] = m_1("weather_lens", IDENTIFIER3, colorTargetState, bindGroupLayout);
      Arrays.fill(FLAG_ARRAY, false);
   }

   private static RenderPipeline m_6(String text, ColorTargetState colorTargetState, BindGroupLayout bindGroupLayout, BindGroupLayout bindGroupLayout2, String text2) {
      Builder builder = RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2);
      if (bindGroupLayout2 != null) {
         builder.withBindGroupLayout(bindGroupLayout2);
      }

      builder.withBindGroupLayout(bindGroupLayout)
         .withVertexShader(IDENTIFIER)
         .withFragmentShader(IDENTIFIER)
         .withColorTargetState(colorTargetState)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/" + text))
         .withShaderDefine(text2);
      return builder.build();
   }

   private static RenderPipeline m_1(String text, Identifier identifier, ColorTargetState colorTargetState, BindGroupLayout bindGroupLayout) {
      return RenderPipeline.builder()
         .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2)
         .withBindGroupLayout(bindGroupLayout)
         .withVertexShader(identifier)
         .withFragmentShader(identifier)
         .withColorTargetState(colorTargetState)
         .withPrimitiveTopology(PrimitiveTopology.QUADS)
         .withCull(false)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/" + text))
         .build();
   }

   public static void m_5(ByteBuffer byteBuffer) {
      m_3_void();
      RenderSystem.getDevice().createCommandEncoder().writeToTexture(gpuTexture2, byteBuffer, 0, 0, 0, 0, 128, 128);
   }

   public static boolean hasTexture() {
      return gpuTexture2 != null && !gpuTexture2.isClosed();
   }

   public static void m_7(Matrix4fc matrix4fc, Matrix4fc matrix4fc2, float[] floatArray) {
      if (RENDER_PIPELINE_ARRAY[0] == null) {
         m_4_void();
      }

      RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
      if (renderTarget.getColorTextureView() != null && renderTarget.getColorTexture() != null && renderTarget.getDepthTextureView() != null) {
         int value4 = renderTarget.width;
         int value5 = renderTarget.height;
         if (value4 > 0 && value5 > 0) {
            boolean flag2 = floatArray[12] > 0.01F;
            boolean flag3 = floatArray[13] > 0.01F;
            boolean flag4 = floatArray[20] > 0.01F;
            m_3_void();
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            GpuTextureView gpuTextureView4 = renderTarget.getColorTextureView();
            GpuTextureView gpuTextureView5 = renderTarget.getDepthTextureView();
            int value6 = m_5(floatArray);
            GpuBufferSlice gpuBufferSlice = m_4(matrix4fc, matrix4fc2, floatArray);
            if (flag3 || flag4) {
               m_5_void(value4, value5);
               commandEncoder.copyTextureToTexture(renderTarget.getColorTexture(), gpuTexture, 0, 0, 0, 0, 0, value4, value5);
            }

            if (flag3) {
               RenderPipeline renderPipeline = m_4(commandEncoder, matrix4fc, matrix4fc2, floatArray) ? m_1(WeatherRenderer.ModeKind.SURFACE) : null;
               if (renderPipeline != null) {
                  try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap Weather Surface", gpuTextureView4, Optional.empty())) {
                     m_1(renderPass, renderPipeline, gpuTextureView, gpuSampler2, gpuTextureView5, gpuBufferSlice);
                     renderPass.bindTexture("Sampler3", gpuTextureView3, gpuSampler3);
                     renderPass.draw(3, 1, 0, 0);
                  }
               }
            } else if (flag2 && floatArray[14] < 1.5F) {
               RenderPipeline renderPipeline2 = m_1(WeatherRenderer.ModeKind.MIST);
               if (renderPipeline2 != null) {
                  try (RenderPass renderPass2 = commandEncoder.createRenderPass(() -> "Onetap Weather Mist", gpuTextureView4, Optional.empty())) {
                     m_1(renderPass2, renderPipeline2, gpuTextureView5, gpuSampler, gpuTextureView5, gpuBufferSlice);
                     renderPass2.draw(3, 1, 0, 0);
                  }
               }
            }

            if (flag2) {
               m_8(commandEncoder, gpuTextureView4, WeatherRenderer.ModeKind.DROPS, gpuTextureView5, gpuSampler, gpuTextureView5, gpuBufferSlice, m_0(floatArray));
            }

            if (flag4) {
               m_8(commandEncoder, gpuTextureView4, WeatherRenderer.ModeKind.LENS, gpuTextureView, gpuSampler2, gpuTextureView5, gpuBufferSlice, value6);
            }
         }
      }
   }

   private static boolean m_4(CommandEncoder commandEncoder, Matrix4fc matrix4fc, Matrix4fc matrix4fc2, float[] floatArray) {
      float floatValue3 = floatArray[4];
      float floatValue4 = floatArray[6];
      if (flag && Math.abs(floatValue3 - floatValue) < 16.0F && Math.abs(floatValue4 - floatValue2) < 16.0F) {
         return true;
      }

      RenderPipeline renderPipeline = m_1(WeatherRenderer.ModeKind.BAKE);
      if (renderPipeline == null) {
         return false;
      }

      System.arraycopy(floatArray, 0, FLOAT_VALUE_ARRAY2, 0, 56);

      try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap Weather Puddles", gpuTextureView3, Optional.empty())) {
         m_1(renderPass, renderPipeline, gpuTextureView2, gpuSampler, gpuTextureView2, m_4(matrix4fc, matrix4fc2, FLOAT_VALUE_ARRAY2));
         renderPass.draw(3, 1, 0, 0);
      }

      floatValue = floatValue3;
      floatValue2 = floatValue4;
      flag = true;
      return true;
   }

   private static void m_1(RenderPass renderPass, RenderPipeline renderPipeline, GpuTextureView gpuTextureView4, GpuSampler gpuSampler4, GpuTextureView gpuTextureView5, GpuBufferSlice gpuBufferSlice) {
      renderPass.setPipeline(renderPipeline);
      renderPass.bindTexture("Sampler0", gpuTextureView4, gpuSampler4);
      renderPass.bindTexture("Sampler1", gpuTextureView5, gpuSampler);
      renderPass.bindTexture("Sampler2", gpuTextureView2, gpuSampler);
      renderPass.setVertexBuffer(0, gpuBuffer.slice());
      renderPass.setUniform("WeatherParams", gpuBufferSlice);
   }

   private static void m_8(
      CommandEncoder commandEncoder, GpuTextureView gpuTextureView4, WeatherRenderer.ModeKind c2, GpuTextureView gpuTextureView5, GpuSampler gpuSampler4, GpuTextureView gpuTextureView6, GpuBufferSlice gpuBufferSlice, int value4
   ) {
      if (value4 > 0) {
         RenderPipeline renderPipeline = m_1(c2);
         if (renderPipeline != null) {
            AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
            GpuBuffer gpuBuffer3 = autoStorageIndexBuffer.getBuffer(value4 * 6);

            try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap Weather Quads", gpuTextureView4, Optional.empty())) {
               renderPass.setPipeline(renderPipeline);
               renderPass.bindTexture("Sampler0", gpuTextureView5, gpuSampler4);
               renderPass.bindTexture("Sampler1", gpuTextureView6, gpuSampler);
               renderPass.bindTexture("Sampler2", gpuTextureView2, gpuSampler);
               renderPass.setUniform("WeatherParams", gpuBufferSlice);
               renderPass.setIndexBuffer(gpuBuffer3, autoStorageIndexBuffer.type());
               renderPass.drawIndexed(value4 * 6, 1, 0, 0, 0);
            }
         }
      }
   }

   private static int m_0(float[] floatArray) {
      int value4 = 0;

      for (int i = 0; i < 3; i++) {
         int value5 = (int)floatArray[40 + i];
         value4 += value5 * value5 * (int)floatArray[44 + i];
      }

      if (floatArray[14] < 0.5F) {
         int value6 = (int)floatArray[40];
         value4 += value6 * value6 * 3;
      } else if (floatArray[14] > 1.5F) {
         int value7 = (int)floatArray[47];
         value4 += value7 * value7 * 2;
      }

      return value4;
   }

   private static int m_5(float[] floatArray) {
      float floatValue3 = floatArray[23];
      int value4 = 0;

      for (int i = 0; i < 3; i++) {
         float floatValue4 = FLOAT_VALUE_ARRAY[i];
         float floatValue5 = i * 3.7F;
         float floatValue6 = i * 1.3F;
         int value5 = (int)Math.floor((floatValue3 + floatValue5) * floatValue4) - (int)Math.floor(floatValue5 * floatValue4) + 1;
         int value6 = (int)Math.floor((1.0F + floatValue6) * floatValue4) - (int)Math.floor(floatValue6 * floatValue4) + 1;
         value4 += value5 * value6;
      }

      return value4;
   }

   private static RenderPipeline m_1(WeatherRenderer.ModeKind c2) {
      RenderPipeline renderPipeline = RENDER_PIPELINE_ARRAY[c2.ordinal()];
      if (!FLAG_ARRAY[c2.ordinal()]) {
         if (!RenderSystem.getDevice().precompilePipeline(renderPipeline).isValid()) {
            return null;
         }

         FLAG_ARRAY[c2.ordinal()] = true;
      }

      return renderPipeline;
   }

   private static GpuBufferSlice m_4(Matrix4fc matrix4fc, Matrix4fc matrix4fc2, float[] floatArray) {
      int value4 = value++ % 8;
      long longValue = value4 * 512L;

      try (MappedView mappedView = gpuBuffer2.map(longValue, 512L, false, true)) {
         ByteBuffer byteBuffer = mappedView.data().order(ByteOrder.nativeOrder());
         matrix4fc.get(0, byteBuffer);
         matrix4fc2.get(64, byteBuffer);

         for (int i = 0; i < 56; i++) {
            byteBuffer.putFloat(128 + i * 4, floatArray[i]);
         }
      }

      return gpuBuffer2.slice(longValue, 512L);
   }

   private static void m_5_void(int value4, int value5) {
      if (value4 != value2 || value5 != value3 || gpuTextureView == null) {
         if (gpuTextureView != null) {
            gpuTextureView.close();
         }

         if (gpuTexture != null) {
            gpuTexture.close();
         }

         GpuDevice gpuDevice = RenderSystem.getDevice();
         byte byteValue = 5;
         gpuTexture = gpuDevice.createTexture(() -> "Onetap Weather Scene", byteValue, GpuFormat.RGBA8_UNORM, value4, value5, 1, 1);
         gpuTextureView = gpuDevice.createTextureView(gpuTexture);
         value2 = value4;
         value3 = value5;
      }
   }

   private static void m_3_void() {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      if (gpuSampler == null) {
         gpuSampler = gpuDevice.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty());
         gpuSampler2 = gpuDevice.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         gpuSampler3 = gpuDevice.createSampler(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
      }

      if (gpuBuffer2 == null || gpuBuffer2.isClosed()) {
         gpuBuffer2 = gpuDevice.createBuffer(() -> "Weather Params", 130, 4096L);
         value = 0;
      }

      if (gpuTexture2 == null || gpuTexture2.isClosed()) {
         byte byteValue = 5;
         gpuTexture2 = gpuDevice.createTexture(() -> "Onetap Weather Heights", byteValue, GpuFormat.RG16_UNORM, 128, 128, 1, 1);
         gpuTextureView2 = gpuDevice.createTextureView(gpuTexture2);
      }

      if (gpuTexture3 == null || gpuTexture3.isClosed()) {
         byte byteValue2 = 12;
         gpuTexture3 = gpuDevice.createTexture(() -> "Onetap Weather Puddles", byteValue2, GpuFormat.RGBA16_FLOAT, 1280, 1280, 1, 1);
         gpuTextureView3 = gpuDevice.createTextureView(gpuTexture3);
         flag = false;
      }

      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(3 * DefaultVertexFormat.POSITION_TEX.getVertexSize());
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(3.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(-1.0F, 3.0F, 0.0F).setUv(0.0F, 0.0F);
         MeshData meshData = bufferBuilder.build();
         gpuBuffer = gpuDevice.createBuffer(() -> "Weather Quad", 32, meshData.vertexBuffer());
         meshData.close();
         byteBufferBuilder.close();
      }
   }

   private enum ModeKind {
      SURFACE,
      MIST,
      BAKE,
      DROPS,
      LENS;

      private static String a(byte[] byteArray) {
         int value4 = 0;
         int value5;
         char[] charArray = new char[value5 = byteArray.length];

         for (int i = 0; i < value5; i++) {
            int value6;
            if ((value6 = 255 & byteArray[i]) < 192) {
               charArray[value4++] = (char)value6;
            } else if (value6 < 224) {
               char ch = (char)((char)(value6 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value4++] = ch;
            } else if (i < value5 - 2) {
               char ch2 = (char)((char)(value6 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value4++] = ch2;
            }
         }

         return new String(charArray, 0, value4);
      }
   }
}
