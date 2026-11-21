package wtf.onetap.mixin.input;

import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.module.render.ClickGUI;

@Mixin(KeyMapping.class)
public class KeyMappingClickMixin {
   @Inject(method = "click", at = @At("HEAD"), cancellable = true)
   private static void onetap$click(Key var0, CallbackInfo var1) {
      if (ClickGUI.isActive()) {
         var1.cancel();
      }
   }
}
