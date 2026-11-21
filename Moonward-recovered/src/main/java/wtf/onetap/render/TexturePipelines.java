package wtf.onetap.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record TexturePipelines(
   RenderPipeline pipeline,
   TextureSetup textureSetup,
   float f_9_w,
   float f_9x,
   float f_98,
   float f_9__,
   float f_9y,
   float f_9_p,
   float f_92,
   float f_9q,
   int f_2x,
   int f_2_e,
   @Nullable ScreenRectangle scissorArea,
   @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
   private static final Snippet f_5 = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
      .withVertexShader(Identifier.fromNamespaceAndPath("onetap", "2d/texture"))
      .withFragmentShader(Identifier.fromNamespaceAndPath("onetap", "2d/texture"))
      .withVertexBinding(
         0,
         VertexFormat.builder(0)
            .addAttribute("Position", GpuFormat.RGB32_FLOAT)
            .addAttribute("Color", GpuFormat.RGBA8_UNORM)
            .addAttribute("UV0", GpuFormat.RG32_FLOAT)
            .addAttribute("UV1", GpuFormat.RG16_FLOAT)
            .addAttribute("UV2", GpuFormat.RG16_FLOAT)
            .build()
      )
      .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA))
      .buildSnippet();
   public static final RenderPipeline f_9 = RenderPipelines.register(
      RenderPipeline.builder(f_5).withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/texture")).withShaderDefine("PREMULTIPLIED").build()
   );
   public static final RenderPipeline f_8 = RenderPipelines.register(
      RenderPipeline.builder(f_5).withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/texture_glyph")).withShaderDefine("SEAM").build()
   );

   @Override
   public void buildVertices(VertexConsumer vertexConsumer) {
      float floatValue = (this.f_9_w + this.f_98) * 0.5F;
      float floatValue2 = (this.f_9x + this.f_9__) * 0.5F;
      this.m_2(vertexConsumer, this.f_9_w, this.f_9x, this.f_9y, this.f_9_p, floatValue, floatValue2);
      this.m_2(vertexConsumer, this.f_9_w, this.f_9__, this.f_9y, this.f_9q, floatValue, floatValue2);
      this.m_2(vertexConsumer, this.f_98, this.f_9__, this.f_92, this.f_9q, floatValue, floatValue2);
      this.m_2(vertexConsumer, this.f_98, this.f_9x, this.f_92, this.f_9_p, floatValue, floatValue2);
   }

   private void m_2(VertexConsumer vertexConsumer, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6) {
      int value = ShapePipelines.packHalfFloats(floatValue - floatValue5, floatValue2 - floatValue6);
      vertexConsumer.addVertex(floatValue, floatValue2, 0.0F).setColor(this.f_2x).setUv(floatValue3, floatValue4).setUv1(value & 65535, value >>> 16).setUv2(this.f_2_e & 65535, this.f_2_e >>> 16);
   }

   public float m_9_w_float() {
      return this.f_9_w;
   }

   public float m_9x_float() {
      return this.f_9x;
   }

   public float m_98_float() {
      return this.f_98;
   }

   public float m_9___float() {
      return this.f_9__;
   }

   public float m_9y_float() {
      return this.f_9y;
   }

   public float m_9_p() {
      return this.f_9_p;
   }

   public float m_92_float() {
      return this.f_92;
   }

   public float m_9q() {
      return this.f_9q;
   }

   public int m_2x() {
      return this.f_2x;
   }

   public int m_2_e_int() {
      return this.f_2_e;
   }

   private static String a(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
