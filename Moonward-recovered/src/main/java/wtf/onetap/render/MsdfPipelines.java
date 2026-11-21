package wtf.onetap.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record MsdfPipelines(
   FontAtlas f_6s,
   String f_2_a,
   float f_9c,
   float f_9_v,
   float f_9_u,
   int f_3_k,
   float f_9t,
   float f_9_b,
   float f_9_o,
   float f_9j,
   TextureSetup textureSetup,
   @Nullable ScreenRectangle scissorArea,
   @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
   private static final RenderPipeline f_4 = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/msdf"))
         .withVertexShader(Identifier.fromNamespaceAndPath("onetap", "2d/msdf"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("onetap", "2d/msdf"))
         .withVertexBinding(
            0,
            VertexFormat.builder(0)
               .addAttribute("Position", GpuFormat.RGB32_FLOAT)
               .addAttribute("UV0", GpuFormat.RG32_FLOAT)
               .addAttribute("Color", GpuFormat.RGBA8_UNORM)
               .addAttribute("LineWidth", GpuFormat.R32_FLOAT)
               .build()
         )
         .build()
   );

   @Override
   public RenderPipeline pipeline() {
      return f_4;
   }

   @Override
   public void buildVertices(VertexConsumer vertexConsumer) {
      float floatValue = this.f_9_u / this.f_6s.getFloatValue5();
      float floatValue2 = this.f_9c;
      float floatValue3 = this.f_9_v + this.f_6s.getFloatValue2() * floatValue;
      float floatValue4 = this.f_6s.getFloatValue4();

      for (int i = 0; i < this.f_2_a.length(); i++) {
         char ch = this.f_2_a.charAt(i);
         if (ch == '\n') {
            floatValue2 = this.f_9c;
            floatValue3 += this.f_6s.getFloatValue() * floatValue;
         } else {
            C7y c7y = this.f_6s.getGlyph(ch);
            if (c7y != null) {
               float floatValue5 = floatValue2 + c7y.m_06() * floatValue;
               float floatValue6 = floatValue3 - c7y.m_0_a() * floatValue;
               float floatValue7 = floatValue2 + c7y.m_0_q() * floatValue;
               float floatValue8 = floatValue3 - c7y.m_0_h_float() * floatValue;
               floatValue2 += c7y.m_0x() * floatValue;
               if (!(floatValue7 <= floatValue5) && !(floatValue8 <= floatValue6) && c7y.m_7_u_int() > 0 && c7y.m_7u() > 0) {
                  float floatValue9 = c7y.m_05();
                  float floatValue10 = c7y.m_0b();
                  float floatValue11 = c7y.m_0_w();
                  float floatValue12 = c7y.m_0_n();
                  float floatValue13 = this.f_9j + this.f_9_o;
                  if (!(this.f_9_o > 0.0F) || !(floatValue5 >= floatValue13)) {
                     if (this.f_9_o > 0.0F && floatValue7 > floatValue13) {
                        float floatValue14 = (floatValue13 - floatValue5) / (floatValue7 - floatValue5);
                        floatValue7 = floatValue13;
                        floatValue11 = floatValue9 + (floatValue11 - floatValue9) * floatValue14;
                     }

                     if (this.f_9_o > 0.0F && this.f_9c < this.f_9j - 0.01F) {
                        if (floatValue7 <= this.f_9j) {
                           continue;
                        }

                        if (floatValue5 < this.f_9j) {
                           float floatValue15 = (this.f_9j - floatValue5) / (floatValue7 - floatValue5);
                           floatValue5 = this.f_9j;
                           floatValue9 += (floatValue11 - floatValue9) * floatValue15;
                        }
                     }

                     int value = this.m_8_int(floatValue5);
                     int value2 = this.m_8_int(floatValue7);
                     vertexConsumer.addVertex(floatValue5, floatValue6, 0.0F).setUv(floatValue9, floatValue10).setColor(value).setLineWidth(floatValue4);
                     vertexConsumer.addVertex(floatValue5, floatValue8, 0.0F).setUv(floatValue9, floatValue12).setColor(value).setLineWidth(floatValue4);
                     vertexConsumer.addVertex(floatValue7, floatValue8, 0.0F).setUv(floatValue11, floatValue12).setColor(value2).setLineWidth(floatValue4);
                     vertexConsumer.addVertex(floatValue7, floatValue6, 0.0F).setUv(floatValue11, floatValue10).setColor(value2).setLineWidth(floatValue4);
                  }
               }
            }
         }
      }
   }

   private int m_8_int(float floatValue) {
      int value = this.f_3_k >>> 24 & 0xFF;
      if (this.f_9_o > 0.0F) {
         float floatValue2 = this.f_9j + this.f_9_o;
         float floatValue3 = this.f_9j + this.f_9_o * this.f_9t;
         float floatValue4 = this.f_9j + this.f_9_o * this.f_9_b;
         if (floatValue >= floatValue2) {
            value = 0;
         } else {
            float floatValue5 = this.f_9j - this.f_9c;
            if (floatValue5 > 0.01F) {
               float floatValue6 = this.f_9_o * 0.2F;
               float floatValue7 = Math.clamp(floatValue5 / (floatValue6 * 0.4F), 0.0F, 1.0F);
               float floatValue8 = Math.clamp((floatValue - this.f_9j) / floatValue6, 0.0F, 1.0F);
               value = (int)(value * (1.0F - floatValue7 * (1.0F - floatValue8)));
            }

            if (floatValue4 > floatValue3 && floatValue > floatValue3) {
               value = (int)(value * Math.clamp((floatValue4 - floatValue) / (floatValue4 - floatValue3), 0.0F, 1.0F));
            }
         }
      }

      return value << 24 | this.f_3_k & 16777215;
   }

   public FontAtlas m_6s_C6v() {
      return this.f_6s;
   }

   public String m_2_a() {
      return this.f_2_a;
   }

   public float m_9c() {
      return this.f_9c;
   }

   public float m_9_v() {
      return this.f_9_v;
   }

   public float m_9_u() {
      return this.f_9_u;
   }

   public int m_3_k() {
      return this.f_3_k;
   }

   public float m_9t() {
      return this.f_9t;
   }

   public float m_9_b_float() {
      return this.f_9_b;
   }

   public float m_9_o() {
      return this.f_9_o;
   }

   public float m_9j() {
      return this.f_9j;
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
