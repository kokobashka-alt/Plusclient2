package wtf.onetap.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
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
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public class SkyRenderer {
   private static final float[] FLOAT_VALUE_ARRAY = new float[]{
      Float.intBitsToFloat(1006726912), Float.intBitsToFloat(1015061833), 0.0F, Float.intBitsToFloat(1009518641), 0.0F, Float.intBitsToFloat(1006726912)
   };
   private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("onetap", "sky/sky");
   private static final Identifier IDENTIFIER2 = Identifier.fromNamespaceAndPath("onetap", "sky/upscale");
   private static final Identifier IDENTIFIER3 = Identifier.fromNamespaceAndPath("onetap", "sky/blackhole");
   private static final Identifier[] IDENTIFIER_ARRAY = new Identifier[]{
      Identifier.fromNamespaceAndPath("onetap", "sky/starnest"),
      Identifier.fromNamespaceAndPath("onetap", "sky/storm"),
      Identifier.fromNamespaceAndPath("onetap", "sky/aurora"),
      Identifier.fromNamespaceAndPath("onetap", "sky/space"),
      Identifier.fromNamespaceAndPath("onetap", "sky/blackhole"),
      Identifier.fromNamespaceAndPath("onetap", "sky/tokyo")
   };
   private static RenderPipeline[] renderPipelineArray;
   private static RenderPipeline[] renderPipelineArray2;
   private static RenderPipeline renderPipeline;
   private static RenderPipeline renderPipeline2;
   private static GpuBuffer gpuBuffer;
   private static GpuBuffer gpuBuffer2;
   private static int value;
   private static final SkyRenderer.StateHolder C3 = new SkyRenderer.StateHolder("ShaderSky Offscreen", GpuFormat.RGBA8_UNORM);
   private static final SkyRenderer.StateHolder C32 = new SkyRenderer.StateHolder("ShaderSky Field", GpuFormat.RGBA16_FLOAT);
   private static GpuSampler gpuSampler;
   private static GpuSampler gpuSampler2;

   private SkyRenderer() {
   }

   public static void m_9_void() {
      BindGroupLayout bindGroupLayout = BindGroupLayout.builder().withUniform("SkyParams", UniformType.UNIFORM_BUFFER).build();
      ColorTargetState colorTargetState = new ColorTargetState(Optional.empty(), GpuFormat.RGBA16_FLOAT, 15);
      renderPipelineArray = new RenderPipeline[IDENTIFIER_ARRAY.length];
      renderPipelineArray2 = new RenderPipeline[IDENTIFIER_ARRAY.length];

      for (int i = 0; i < IDENTIFIER_ARRAY.length; i++) {
         boolean flag = i == 4;
         boolean flag2 = FLOAT_VALUE_ARRAY[i] > 0.0F;
         Builder builder = m_8(bindGroupLayout, "shadersky" + i, IDENTIFIER, IDENTIFIER_ARRAY[i], ColorTargetState.DEFAULT, flag || flag2);
         if (flag2) {
            builder.withShaderDefine("SKY_UV");
         }

         if (flag) {
            builder.withShaderDefine("SKY_BACKGROUND");
         }

         renderPipelineArray[i] = builder.build();
         if (flag || flag2) {
            renderPipelineArray2[i] = m_8(bindGroupLayout, "shadersky_field" + i, flag ? IDENTIFIER2 : IDENTIFIER, IDENTIFIER_ARRAY[i], colorTargetState, false).withShaderDefine("SKY_FIELD").build();
         }
      }

      renderPipeline2 = m_8(bindGroupLayout, "shadersky_march", IDENTIFIER3, IDENTIFIER_ARRAY[4], ColorTargetState.DEFAULT, true).withCull(false).build();
      renderPipeline = m_8(bindGroupLayout, "shadersky_upscale", IDENTIFIER2, IDENTIFIER2, ColorTargetState.DEFAULT, true).build();
   }

   private static Builder m_8(BindGroupLayout bindGroupLayout, String text, Identifier identifier, Identifier identifier2, ColorTargetState colorTargetState, boolean flag) {
      Builder builder = RenderPipeline.builder();
      if (flag) {
         builder.withBindGroupLayout(BindGroupLayouts.SAMPLER0);
      }

      return builder.withBindGroupLayout(bindGroupLayout)
         .withVertexShader(identifier)
         .withFragmentShader(identifier2)
         .withColorTargetState(colorTargetState)
         .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/" + text));
   }

   public static void m_1(CameraRenderState cameraRenderState, float floatValue, float floatValue2, float floatValue3, int value2, int value3, int value4, int value5, float floatValue4) {
      if (renderPipelineArray == null) {
         m_9_void();
      }

      RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
      if (renderTarget.getColorTextureView() != null) {
         int value6 = renderTarget.width;
         int value7 = renderTarget.height;
         if (value6 > 0 && value7 > 0) {
            m_2_void();
            int value8 = Math.clamp(value5, 0, renderPipelineArray.length - 1);
            boolean flag = value8 == 4;
            GpuDevice gpuDevice = RenderSystem.getDevice();
            if (m_7(gpuDevice, renderPipelineArray[value8]) && m_7(gpuDevice, flag ? renderPipeline2 : renderPipeline)) {
               if (renderPipelineArray2[value8] == null || m_7(gpuDevice, renderPipelineArray2[value8])) {
                  Matrix4f matrix4f = new Matrix4f(cameraRenderState.projectionMatrix).invert();
                  Matrix4f matrix4f2 = new Matrix4f().rotate(cameraRenderState.orientation);
                  GpuBufferSlice gpuBufferSlice = m_3(matrix4f, matrix4f2, floatValue, floatValue2, floatValue3, value2, value3, value4, value8, floatValue4);
                  CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
                  if (renderPipelineArray2[value8] != null) {
                     if (flag) {
                        C32.m_1_void(512, 256);
                     } else {
                        float floatValue5 = 2.0F / (Math.abs(cameraRenderState.projectionMatrix.m11()) * value7);
                        int value9 = Math.clamp((int)(FLOAT_VALUE_ARRAY[value8] * floatValue2 / floatValue5), 2, 16);
                        C32.m_1_void((value6 + value9 - 1) / value9, (value7 + value9 - 1) / value9);
                     }

                     m_1(commandEncoder, "Onetap ShaderSky Field", C32.gpuTextureView, renderPipelineArray2[value8], gpuBufferSlice, null, null);
                  }

                  if (flag) {
                     try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Onetap ShaderSky", renderTarget.getColorTextureView(), Optional.empty())) {
                        renderPass.setVertexBuffer(0, gpuBuffer.slice());
                        renderPass.setUniform("SkyParams", gpuBufferSlice);
                        renderPass.bindTexture("Sampler0", C32.gpuTextureView, gpuSampler2);
                        renderPass.setPipeline(renderPipelineArray[value8]);
                        renderPass.draw(3, 1, 0, 0);
                        renderPass.setPipeline(renderPipeline2);
                        renderPass.draw(3, 1, 0, 0);
                     }
                  } else {
                     C3.m_1_void(Math.max(1, value6 >> 1), Math.max(1, value7 >> 1));
                     m_1(commandEncoder, "Onetap ShaderSky", C3.gpuTextureView, renderPipelineArray[value8], gpuBufferSlice, renderPipelineArray2[value8] != null ? C32.gpuTextureView : null, gpuSampler);
                     m_1(commandEncoder, "Onetap ShaderSky Upscale", renderTarget.getColorTextureView(), renderPipeline, gpuBufferSlice, C3.gpuTextureView, gpuSampler);
                  }
               }
            }
         }
      }
   }

   private static boolean m_7(GpuDevice gpuDevice, RenderPipeline renderPipeline3) {
      return gpuDevice.precompilePipeline(renderPipeline3).isValid();
   }

   private static void m_1(
      CommandEncoder commandEncoder, String text, GpuTextureView gpuTextureView, RenderPipeline renderPipeline3, GpuBufferSlice gpuBufferSlice, GpuTextureView gpuTextureView2, GpuSampler gpuSampler3
   ) {
      try (RenderPass renderPass = commandEncoder.createRenderPass(() -> text, gpuTextureView, Optional.empty())) {
         renderPass.setPipeline(renderPipeline3);
         if (gpuTextureView2 != null) {
            renderPass.bindTexture("Sampler0", gpuTextureView2, gpuSampler3);
         }

         renderPass.setUniform("SkyParams", gpuBufferSlice);
         renderPass.setVertexBuffer(0, gpuBuffer.slice());
         renderPass.draw(3, 1, 0, 0);
      }
   }

   private static GpuBufferSlice m_3(Matrix4f matrix4f, Matrix4f matrix4f2, float floatValue, float floatValue2, float floatValue3, int value2, int value3, int value4, int value5, float floatValue4) {
      int value6 = value++ % 4;
      long longValue = value6 * 256L;

      try (MappedView mappedView = gpuBuffer2.map(longValue, 256L, false, true)) {
         ByteBuffer byteBuffer = mappedView.data().order(ByteOrder.nativeOrder());
         matrix4f.get(0, byteBuffer);
         matrix4f2.get(64, byteBuffer);
         byteBuffer.putFloat(128, floatValue);
         byteBuffer.putFloat(132, value5);
         byteBuffer.putFloat(136, floatValue2);
         byteBuffer.putFloat(140, floatValue3);
         m_8(byteBuffer, 144, value2);
         m_8(byteBuffer, 160, value3);
         m_8(byteBuffer, 176, value4);
         byteBuffer.putFloat(192, floatValue4);
      }

      return gpuBuffer2.slice(longValue, 256L);
   }

   private static void m_8(ByteBuffer byteBuffer, int value2, int value3) {
      byteBuffer.putFloat(value2, (value3 >> 16 & 0xFF) / 255.0F);
      byteBuffer.putFloat(value2 + 4, (value3 >> 8 & 0xFF) / 255.0F);
      byteBuffer.putFloat(value2 + 8, (value3 & 0xFF) / 255.0F);
      byteBuffer.putFloat(value2 + 12, (value3 >> 24 & 0xFF) / 255.0F);
   }

   private static void m_2_void() {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      if (gpuBuffer2 == null || gpuBuffer2.isClosed()) {
         gpuBuffer2 = gpuDevice.createBuffer(() -> "ShaderSky Params", 130, 1024L);
         value = 0;
      }

      if (gpuSampler == null) {
         gpuSampler = gpuDevice.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         gpuSampler2 = gpuDevice.createSampler(AddressMode.REPEAT, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
      }

      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(3 * DefaultVertexFormat.POSITION_TEX.getVertexSize());
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(3.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
         bufferBuilder.addVertex(-1.0F, 3.0F, 0.0F).setUv(0.0F, 0.0F);
         MeshData meshData = bufferBuilder.build();
         gpuBuffer = gpuDevice.createBuffer(() -> "ShaderSky Quad", 32, meshData.vertexBuffer());
         meshData.close();
         byteBufferBuilder.close();
      }
   }

   private static final class StateHolder {
      private final String text;
      private final GpuFormat gpuFormat;
      private GpuTexture gpuTexture;
      private GpuTextureView gpuTextureView;

      private StateHolder(String text2, GpuFormat gpuFormat2) {
         this.text = text2;
         this.gpuFormat = gpuFormat2;
      }

      private void m_1_void(int value2, int value3) {
         if (this.gpuTextureView == null || this.gpuTextureView.isClosed() || this.gpuTexture.getWidth(0) != value2 || this.gpuTexture.getHeight(0) != value3) {
            if (this.gpuTextureView != null) {
               this.gpuTextureView.close();
            }

            if (this.gpuTexture != null) {
               this.gpuTexture.close();
            }

            GpuDevice gpuDevice = RenderSystem.getDevice();
            this.gpuTexture = gpuDevice.createTexture(() -> this.text, 12, this.gpuFormat, value2, value3, 1, 1);
            this.gpuTextureView = gpuDevice.createTextureView(this.gpuTexture);
         }
      }
   }
}
