package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.gui.TitleScreenRenderer;
import wtf.onetap.util.ChatUtil;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
   @Inject(method = "init", at = @At("HEAD"), cancellable = true)
   private void onetap$init(CallbackInfo var1) {
      if (!ChatUtil.isUnHooked()) {
         TitleScreenRenderer.TITLE_SCREEN_RENDERER.m_7((TitleScreen)(Object)this);
         var1.cancel();
      }
   }

   @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
   private void onetap$render(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (!ChatUtil.isUnHooked()) {
         TitleScreenRenderer.TITLE_SCREEN_RENDERER.m_9((TitleScreen)(Object)this, var1, var2, var3);
         var5.cancel();
      }
   }

   @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void onetap$click(MouseButtonEvent var1, boolean var2, CallbackInfoReturnable<Boolean> var3) {
      if (!ChatUtil.isUnHooked()) {
         var3.setReturnValue(TitleScreenRenderer.TITLE_SCREEN_RENDERER.m_8((TitleScreen)(Object)this, var1, var2));
      }
   }
}
