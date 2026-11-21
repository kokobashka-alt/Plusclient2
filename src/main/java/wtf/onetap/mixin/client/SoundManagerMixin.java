package wtf.onetap.mixin.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine.PlayResult;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.bot.BotConnection;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
   @Inject(method = "play", at = @At("HEAD"), cancellable = true)
   private void onetap$parkedPlay(SoundInstance var1, CallbackInfoReturnable<PlayResult> var2) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var2.setReturnValue(PlayResult.NOT_STARTED);
      }
   }

   @Inject(method = "playDelayed", at = @At("HEAD"), cancellable = true)
   private void onetap$parkedPlayDelayed(SoundInstance var1, int var2, CallbackInfo var3) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var3.cancel();
      }
   }

   @Inject(method = "queueTickingSound", at = @At("HEAD"), cancellable = true)
   private void onetap$parkedQueue(TickableSoundInstance var1, CallbackInfo var2) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var2.cancel();
      }
   }
}
