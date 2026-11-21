package wtf.onetap.mixin.player;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.module.player.VoiceChat;
import wtf.onetap.util.MinecraftAccess;

@Mixin(targets = "de.maxhenkel.voicechat.voice.client.PTTKeyHandler", remap = false)
public class MixinVoiceChatPttKeyHandler implements MinecraftAccess {
   @Shadow(remap = false)
   private boolean pttKeyDown;
   @Shadow(remap = false)
   private boolean whisperKeyDown;

   private static boolean onetap$blockPttWhileScreenOpen() {
      return VoiceChat.isActive() && MINECRAFT != null && MINECRAFT.gui.screen() != null;
   }

   private void onetap$clearPttState() {
      this.pttKeyDown = false;
      this.whisperKeyDown = false;
   }

   @Inject(method = "onKeyboardEvent", at = @At("HEAD"), cancellable = true, remap = false)
   private void onetap$blockKeyboardInScreen(KeyEvent var1, CallbackInfo var2) {
      if (onetap$blockPttWhileScreenOpen()) {
         this.onetap$clearPttState();
         var2.cancel();
      }
   }

   @Inject(method = "onMouseEvent", at = @At("HEAD"), cancellable = true, remap = false)
   private void onetap$blockMouseInScreen(MouseButtonInfo var1, int var2, CallbackInfo var3) {
      if (onetap$blockPttWhileScreenOpen()) {
         this.onetap$clearPttState();
         var3.cancel();
      }
   }

   @Inject(method = "isPTTDown", at = @At("HEAD"), cancellable = true, remap = false)
   private void onetap$pttDownInScreen(CallbackInfoReturnable<Boolean> var1) {
      if (onetap$blockPttWhileScreenOpen()) {
         var1.setReturnValue(false);
      }
   }

   @Inject(method = "isWhisperDown", at = @At("HEAD"), cancellable = true, remap = false)
   private void onetap$whisperDownInScreen(CallbackInfoReturnable<Boolean> var1) {
      if (onetap$blockPttWhileScreenOpen()) {
         var1.setReturnValue(false);
      }
   }

   @Inject(method = "isAnyDown", at = @At("HEAD"), cancellable = true, remap = false)
   private void onetap$anyDownInScreen(CallbackInfoReturnable<Boolean> var1) {
      if (onetap$blockPttWhileScreenOpen()) {
         var1.setReturnValue(false);
      }
   }
}
