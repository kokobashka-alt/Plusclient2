package wtf.onetap.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font.GlyphVisitor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable.Styled;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class TextRenderer {
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private final TextRenderer.CallbackType[] c9Array;
   private static int value;

   private TextRenderer(TextRenderer.CallbackType[] c9Array2) {
      this.c9Array = c9Array2;
   }

   public static TextRenderer m_6(Component component, Supplier<FontAtlas> supplier, Supplier<FontAtlas> supplier2) {
      return m_4(component, supplier, supplier2, false);
   }

   public static TextRenderer m_2(Component component, Supplier<FontAtlas> supplier, Supplier<FontAtlas> supplier2) {
      return m_4(component, supplier, supplier2, true);
   }

   public static TextRenderer m_2(String text, Supplier<FontAtlas> supplier, Supplier<FontAtlas> supplier2) {
      return m_6(Component.literal(text), supplier, supplier2);
   }

   private static TextRenderer m_4(Component component, Supplier<FontAtlas> supplier, Supplier<FontAtlas> supplier2, boolean flag) {
      ArrayList list = new ArrayList();
      component.visit((style, text) -> {
         m_8(list, text, style, supplier, supplier2, flag);
         return Optional.empty();
      }, Style.EMPTY);
      return new TextRenderer((CallbackType[])list.toArray(TextRenderer.CallbackType[]::new));
   }

   public float getWidth(float floatValue) {
      float floatValue2 = 0.0F;

      for (TextRenderer.CallbackType c9 : this.c9Array) {
         floatValue2 += c9.m_0_float(floatValue);
      }

      return floatValue2;
   }

   public boolean isEmpty() {
      return this.c9Array.length == 0;
   }

   public void drawOpaque(float floatValue, float floatValue2, float floatValue3) {
      this.draw(floatValue, floatValue2, floatValue3, 255);
   }

   public void draw(float floatValue, float floatValue2, float floatValue3, int value2) {
      for (TextRenderer.CallbackType c9 : this.c9Array) {
         c9.m_4(floatValue, floatValue2, floatValue3, value2);
         floatValue += c9.m_0_float(floatValue3);
      }
   }

   private static void m_8(ArrayList<TextRenderer.CallbackType> c9List, String text, Style style, Supplier<FontAtlas> supplier, Supplier<FontAtlas> supplier2, boolean flag) {
      if (!text.isEmpty()) {
         boolean flag2 = flag && !FontDescription.DEFAULT.equals(style.getFont());
         int value2 = m_3(style);
         boolean flag3 = c9List.isEmpty();
         int value3 = 0;
         Supplier supplier3 = null;
         int value4 = -1;
         int value5 = 0;

         while (value5 < text.length()) {
            int value6 = text.codePointAt(value5);
            int value7 = Character.charCount(value6);
            var supplier4 = m_8(value6, supplier, supplier2);
            boolean flag4 = flag2 ? supplier4 == null || Character.getType(value6) != 28 : flag && supplier4 == null && !Character.isWhitespace(value6);
            if (flag4) {
               if (supplier3 != null) {
                  m_1(c9List, text.substring(value3, value5), supplier3, value2);
                  supplier3 = null;
               }

               if (value4 < 0) {
                  value4 = value5;
               }

               flag3 = false;
               value5 += value7;
            } else {
               if (value4 >= 0) {
                  c9List.add(m_4(text.substring(value4, value5), style, supplier));
                  value4 = -1;
               }

               if (supplier4 == null || flag3 && Character.isWhitespace(value6)) {
                  if (supplier3 != null) {
                     m_1(c9List, text.substring(value3, value5), supplier3, value2);
                     supplier3 = null;
                  }

                  value5 += value7;
                  value3 = value5;
               } else {
                  if (supplier4 != supplier3) {
                     if (supplier3 != null) {
                        m_1(c9List, text.substring(value3, value5), supplier3, value2);
                     }

                     supplier3 = supplier4;
                     value3 = value5;
                  }

                  flag3 = false;
                  value5 += value7;
               }
            }
         }

         if (value4 >= 0) {
            c9List.add(m_4(text.substring(value4), style, supplier));
         } else if (supplier3 != null) {
            m_1(c9List, text.substring(value3), supplier3, value2);
         }
      }
   }

   private static Supplier<FontAtlas> m_8(int value2, Supplier<FontAtlas> supplier, Supplier<FontAtlas> supplier2) {
      if (((FontAtlas)supplier.get()).hasGlyph(value2)) {
         return supplier;
      } else {
         return ((FontAtlas)supplier2.get()).hasGlyph(value2) ? supplier2 : null;
      }
   }

   private static TextRenderer.DataRecord2 m_4(String text, Style style, Supplier<FontAtlas> supplier) {
      FontAtlas fontAtlas = (FontAtlas)supplier.get();
      return new TextRenderer.DataRecord2(text, style, m_4(style), fontAtlas.getFloatValue2() / fontAtlas.getFloatValue5());
   }

   private static void m_1(ArrayList<TextRenderer.CallbackType> c9List, String text, Supplier<FontAtlas> supplier, int value2) {
      if (!c9List.isEmpty() && c9List.getLast() instanceof TextRenderer.DataRecord c2 && c2.m_0d_int() == value2 && c2.m_3z() == supplier) {
         c9List.set(c9List.size() - 1, new TextRenderer.DataRecord(c2.m_4_y() + text, supplier, value2));
      } else {
         c9List.add(new TextRenderer.DataRecord(text, supplier, value2));
      }
   }

   private static int m_3(Style style) {
      return m_5(style, ColorUtil.rgba(245, 245, 245, 255));
   }

   private static int m_4(Style style) {
      return m_5(style, ColorUtil.rgba(255, 255, 255, 255));
   }

   private static int m_5(Style style, int value2) {
      TextColor textColor = style.getColor();
      return textColor == null ? value2 : ColorUtil.rgba(textColor.getValue() >> 16 & 0xFF, textColor.getValue() >> 8 & 0xFF, textColor.getValue() & 0xFF, 255);
   }

   static {
      m_5_void(77);
   }

   public static void m_5_void(int value2) {
      value = value2;
   }

   public static int m_8_int() {
      return value;
   }

   public static int m_4_int() {
      return 0;
   }

   private record DataRecord(String f_4_y, Supplier<FontAtlas> f_3z, int f_0d) implements TextRenderer.CallbackType {
      @Override
      public float m_0_float(float floatValue) {
         return Draw2D.getTextWidth(this.f_4_y, this.f_3z, floatValue);
      }

      @Override
      public void m_4(float floatValue, float floatValue2, float floatValue3, int value2) {
         Draw2D.drawStyledText(this.f_4_y, this.f_3z, floatValue, floatValue2, ColorUtil.withAlpha(this.f_0d, value2), floatValue3);
      }

      public String m_4_y() {
         return this.f_4_y;
      }

      public Supplier<FontAtlas> m_3z() {
         return this.f_3z;
      }

      public int m_0d_int() {
         return this.f_0d;
      }
   }

   private static final class StateHolder implements VertexConsumer {
      private int value;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8;

      private StateHolder() {
         this.floatValue = Float.MAX_VALUE;
         this.floatValue2 = Float.MAX_VALUE;
         this.floatValue3 = -Float.MAX_VALUE;
         this.floatValue4 = -Float.MAX_VALUE;
         this.floatValue5 = Float.MAX_VALUE;
         this.floatValue6 = Float.MAX_VALUE;
         this.floatValue7 = -Float.MAX_VALUE;
         this.floatValue8 = -Float.MAX_VALUE;
      }

      @Override
      public VertexConsumer addVertex(float floatValue9, float floatValue10, float floatValue11) {
         if (++this.value > 4) {
            return this;
         }

         this.floatValue = Math.min(this.floatValue, floatValue9);
         this.floatValue2 = Math.min(this.floatValue2, floatValue10);
         this.floatValue3 = Math.max(this.floatValue3, floatValue9);
         this.floatValue4 = Math.max(this.floatValue4, floatValue10);
         return this;
      }

      @Override
      public VertexConsumer setUv(float floatValue9, float floatValue10) {
         if (this.value > 4) {
            return this;
         }

         this.floatValue5 = Math.min(this.floatValue5, floatValue9);
         this.floatValue6 = Math.min(this.floatValue6, floatValue10);
         this.floatValue7 = Math.max(this.floatValue7, floatValue9);
         this.floatValue8 = Math.max(this.floatValue8, floatValue10);
         return this;
      }

      @Override
      public VertexConsumer setColor(int value2, int value3, int value4, int value5) {
         return this;
      }

      @Override
      public VertexConsumer setColor(int value2) {
         return this;
      }

      @Override
      public VertexConsumer setUv1(int value2, int value3) {
         return this;
      }

      @Override
      public VertexConsumer setUv2(int value2, int value3) {
         return this;
      }

      @Override
      public VertexConsumer setNormal(float floatValue9, float floatValue10, float floatValue11) {
         return this;
      }

      @Override
      public VertexConsumer setLineWidth(float floatValue9) {
         return this;
      }
   }

   private record DataRecord2(String f_94, Style f_0_b, int f_79, float f_9_x) implements TextRenderer.CallbackType {
      @Override
      public float m_0_float(float floatValue) {
         Font font = Minecraft.getInstance().font;
         return font.width(FormattedText.of(this.f_94, this.f_0_b)) * floatValue / 9.0F;
      }

      @Override
      public void m_4(final float floatValue, float floatValue2, float floatValue3, int value2) {
         Fonts.m_1_String();
         Font font = Minecraft.getInstance().font;
         final float floatValue4 = floatValue3 / 9.0F;
         final int value3 = ColorUtil.withAlpha(this.f_79, value2);
         final float floatValue5 = floatValue2 + this.f_9_x * floatValue3 - 7.0F * floatValue4;
         font.prepareText(FormattedCharSequence.forward(this.f_94, this.f_0_b), 0.0F, 0.0F, -1, false, false, 0)
            .visit(
               new GlyphVisitor() {
                  @Override
                  public void acceptGlyph(Styled styled) {
                     Fonts.m_1_String();
                     TextRenderer.StateHolder c3 = new TextRenderer.StateHolder();
                     styled.render(TextRenderer.MATRIX4F, c3, 0, true);
                     if (c3.value >= 4 && styled.textureView() != null) {
                        Render2D.m_2(
                           floatValue + c3.floatValue * floatValue4,
                           floatValue5 + c3.floatValue2 * floatValue4,
                           (c3.floatValue3 - c3.floatValue) * floatValue4,
                           (c3.floatValue4 - c3.floatValue2) * floatValue4,
                           value3,
                           styled.textureView(),
                           c3.floatValue5,
                           c3.floatValue6,
                           c3.floatValue7,
                           c3.floatValue8
                        );
                     }
                  }
               }
            );
      }

      public String m_94() {
         return this.f_94;
      }

      public Style m_0_b() {
         return this.f_0_b;
      }

      public int m_79() {
         return this.f_79;
      }

      public float m_9_x_float() {
         return this.f_9_x;
      }
   }

   private sealed interface CallbackType permits TextRenderer.DataRecord, TextRenderer.DataRecord2 {
      float m_0_float(float floatValue);

      void m_4(float floatValue, float floatValue2, float floatValue3, int value2);
   }
}
