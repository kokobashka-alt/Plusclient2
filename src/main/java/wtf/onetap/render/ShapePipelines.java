package wtf.onetap.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public record ShapePipelines(
   RenderPipeline pipeline,
   TextureSetup textureSetup,
   float f_5_x,
   float f_5_b,
   float f_5_g,
   float f_5t,
   float f_5m,
   float f_5x,
   int f_4j,
   int f_4n,
   int f_44,
   int f_4_c,
   int f_4a,
   int f_4_q,
   int f_4_v,
   float f_5_f,
   @Nullable ScreenRectangle scissorArea,
   @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
   private static final Identifier f_2 = Identifier.fromNamespaceAndPath("onetap", "2d/shape");
   public static final Snippet f_7 = m_7(GpuFormat.RG16_FLOAT, null);
   private static final Snippet f_6 = m_7(GpuFormat.RGBA8_UNORM, "EFFECT");
   public static final RenderPipeline f_3 = m_4(f_7, "shape", f_2);
   public static final RenderPipeline f_1 = m_4(f_7, "shape_hue", f_2, "HUE");
   public static final RenderPipeline f_0 = m_4(f_6, "shape_flow", m_9_Identifier(), "FLOW");
   public static final RenderPipeline f_4 = m_4(f_6, "shape_ring", m_9_Identifier(), "RING");
   public static final RenderPipeline f_5 = m_4(f_6, "shape_gradient_ring", m_9_Identifier(), "RING", "GRADIENT");

   @Override
   public void buildVertices(VertexConsumer vertexConsumer) {
      this.m_7(vertexConsumer, this.f_5_x, this.f_5_b, this.f_4j);
      this.m_7(vertexConsumer, this.f_5_x, this.f_5t, this.f_4n);
      this.m_7(vertexConsumer, this.f_5_g, this.f_5t, this.f_44);
      this.m_7(vertexConsumer, this.f_5_g, this.f_5_b, this.f_4_c);
   }

   private void m_7(VertexConsumer vertexConsumer, float floatValue, float floatValue2, int value) {
      vertexConsumer.addVertex(floatValue, floatValue2, this.f_5_f)
         .setColor(value)
         .setUv(floatValue - this.f_5m, floatValue2 - this.f_5x)
         .setUv1(this.f_4a & 65535, this.f_4a >>> 16)
         .setUv2(this.f_4_q & 65535, this.f_4_q >>> 16)
         .setLineWidth(Float.intBitsToFloat(this.f_4_v));
   }

   public static int packHalfFloats(float floatValue, float floatValue2) {
      return Float.floatToFloat16(floatValue) & 65535 | Float.floatToFloat16(floatValue2) << 16;
   }

   public static int toAbgr(int value) {
      return ARGB.toABGR(value);
   }

   public static RenderPipeline m_4(Snippet snippet, String text, Identifier identifier, String... stringArray) {
      Builder builder = RenderPipeline.builder(snippet).withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/" + text)).withFragmentShader(identifier);

      for (String text2 : stringArray) {
         builder.withShaderDefine(text2);
      }

      return RenderPipelines.register(builder.build());
   }

   private static Identifier m_9_Identifier() {
      return Identifier.fromNamespaceAndPath("onetap", "2d/effect");
   }

   private static Snippet m_7(GpuFormat gpuFormat, @Nullable String text) {
      VertexFormat vertexFormat = VertexFormat.builder(0)
         .addAttribute("Position", GpuFormat.RGB32_FLOAT)
         .addAttribute("Color", GpuFormat.RGBA8_UNORM)
         .addAttribute("UV0", GpuFormat.RG32_FLOAT)
         .addAttribute("UV1", GpuFormat.RG16_FLOAT)
         .addAttribute("UV2", gpuFormat)
         .addAttribute("LineWidth", GpuFormat.RG16_FLOAT)
         .build();
      Builder builder = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
         .withVertexShader(f_2)
         .withVertexBinding(0, vertexFormat)
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA));
      if (text != null) {
         builder.withShaderDefine(text);
      }

      return builder.buildSnippet();
   }

   public float m_5_x() {
      return this.f_5_x;
   }

   public float m_5_b() {
      return this.f_5_b;
   }

   public float m_5_g() {
      return this.f_5_g;
   }

   public float m_5t() {
      return this.f_5t;
   }

   public float m_5m() {
      return this.f_5m;
   }

   public float m_5x() {
      return this.f_5x;
   }

   public int m_4j() {
      return this.f_4j;
   }

   public int m_4n_int() {
      return this.f_4n;
   }

   public int m_44() {
      return this.f_44;
   }

   public int m_4_c_int() {
      return this.f_4_c;
   }

   public int m_4a() {
      return this.f_4a;
   }

   public int m_4_q() {
      return this.f_4_q;
   }

   public int m_4_v() {
      return this.f_4_v;
   }

   public float m_5_f() {
      return this.f_5_f;
   }
}
