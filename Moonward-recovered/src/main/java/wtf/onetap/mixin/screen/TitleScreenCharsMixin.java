package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.CharacterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.gui.TitleScreenRenderer;
import wtf.onetap.util.ChatUtil;

@Mixin(ContainerEventHandler.class)
public interface TitleScreenCharsMixin {
   @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
   private void onetap$titleChars(CharacterEvent var1, CallbackInfoReturnable<Boolean> var2) {
      if (this instanceof TitleScreen && !ChatUtil.isUnHooked()) {
         if (TitleScreenRenderer.TITLE_SCREEN_RENDERER.m_8(var1)) {
            var2.setReturnValue(true);
         }
      }
   }

   @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
   private void onetap$titleScroll(double var1, double var3, double var5, double var7, CallbackInfoReturnable<Boolean> var9) {
      if (this instanceof TitleScreen && !ChatUtil.isUnHooked()) {
         if (TitleScreenRenderer.TITLE_SCREEN_RENDERER.m_9_boolean(var1, var3, var7)) {
            var9.setReturnValue(true);
         }
      }
   }
}
