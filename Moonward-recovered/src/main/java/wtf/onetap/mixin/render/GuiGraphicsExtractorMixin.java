package wtf.onetap.mixin.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import wtf.onetap.obf.C6_e;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {
   @ModifyVariable(method = "innerFill", at = @At("HEAD"), argsOnly = true, ordinal = 4)
   private int onetap$tabFillColor(int var1) {
      return C6_e.m_5_int(var1);
   }

   @ModifyVariable(
      method = "text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)V",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 2
   )
   private int onetap$tabTextColor(int var1) {
      return C6_e.m_5_int(var1);
   }

   @ModifyVariable(
      method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;IIIIFFFFI)V",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 4
   )
   private int onetap$tabBlitColor(int var1) {
      return C6_e.m_5_int(var1);
   }
}
