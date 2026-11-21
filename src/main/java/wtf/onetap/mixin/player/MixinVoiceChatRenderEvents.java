package wtf.onetap.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.module.player.VoiceChat;

@Mixin(targets = "de.maxhenkel.voicechat.voice.client.RenderEvents", remap = false)
public class MixinVoiceChatRenderEvents {
   @Inject(method = "shouldShowIcons()Z", at = @At("HEAD"), cancellable = true, remap = false)
   private void onetap$hideIcons(CallbackInfoReturnable<Boolean> var1) {
      if (!VoiceChat.isActive()) {
         var1.setReturnValue(false);
      }
   }
}
