package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.gui.TitleScreenRenderer;
import wtf.onetap.util.ChatUtil;

@Mixin(Screen.class)
public abstract class TitleScreenInputMixin {
   @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void onetap$titleKeys(KeyEvent var1, CallbackInfoReturnable<Boolean> var2) {
      if ((Object)this instanceof TitleScreen && !ChatUtil.isUnHooked()) {
         if (TitleScreenRenderer.TITLE_SCREEN_RENDERER.m_6(var1)) {
            var2.setReturnValue(true);
         }
      }
   }
}
