package wtf.onetap.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record GlowPipelines(TextureSetup textureSetup, Consumer<VertexConsumer> f_6_f, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds)
   implements GuiElementRenderState {
   private static final RenderPipeline f_2 = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
         .withLocation(Identifier.fromNamespaceAndPath("onetap", "pipeline/gui_glow"))
         .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
         .build()
   );

   @Override
   public RenderPipeline pipeline() {
      return f_2;
   }

   @Override
   public void buildVertices(VertexConsumer vertexConsumer) {
      this.f_6_f.accept(vertexConsumer);
   }

   public Consumer<VertexConsumer> m_6_f() {
      return this.f_6_f;
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
