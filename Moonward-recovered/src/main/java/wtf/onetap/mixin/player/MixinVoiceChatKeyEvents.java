package wtf.onetap.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.module.player.VoiceChat;
import wtf.onetap.util.MinecraftAccess;

@Mixin(targets = "de.maxhenkel.voicechat.voice.client.KeyEvents", remap = false)
public class MixinVoiceChatKeyEvents implements MinecraftAccess {
   @Inject(method = "handleKeybinds()V", at = @At("HEAD"), cancellable = true, remap = false)
   private void onetap$blockKeys(CallbackInfo var1) {
      if (!VoiceChat.isActive()) {
         var1.cancel();
      } else if (MINECRAFT != null && MINECRAFT.gui.screen() != null) {
         var1.cancel();
      }
   }
}
