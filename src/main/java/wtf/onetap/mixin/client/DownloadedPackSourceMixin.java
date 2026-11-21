package wtf.onetap.mixin.client;

import java.net.URL;
import java.util.UUID;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.resources.server.PackLoadFeedback.FinalResult;
import net.minecraft.client.resources.server.PackLoadFeedback;
import net.minecraft.client.resources.server.ServerPackManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.misc.RpHelper;

@Mixin(DownloadedPackSource.class)
public abstract class DownloadedPackSourceMixin {
   @Shadow
   @Final
   private ServerPackManager manager;
   @Shadow
   private PackLoadFeedback packFeedback;

   @Inject(method = "pushPack", at = @At("HEAD"), cancellable = true)
   private void onPushPack(UUID var1, URL var2, String var3, CallbackInfo var4) {
      RpHelper var5 = getHelper();
      if (var5 != null && var5.isEnabled()) {
         if (var5.isSameText(var3)) {
            this.packFeedback.reportFinalResult(var1, FinalResult.APPLIED);
            var4.cancel();
         } else {
            var5.setText(var3);
            this.manager.popAll();
         }
      }
   }

   @Inject(method = "popPack", at = @At("HEAD"), cancellable = true)
   private void onPopPack(UUID var1, CallbackInfo var2) {
      RpHelper var3 = getHelper();
      if (var3 != null && var3.isEnabled()) {
         var3.m_2_void();
         this.manager.popAll();
         var2.cancel();
      }
   }

   @Inject(method = "popAll", at = @At("HEAD"))
   private void onPopAll(CallbackInfo var1) {
      RpHelper var2 = getHelper();
      if (var2 != null && var2.isEnabled()) {
         var2.m_2_void();
      }
   }

   @Inject(method = "cleanupAfterDisconnect", at = @At("HEAD"), cancellable = true)
   private void onDisconnect(CallbackInfo var1) {
      RpHelper var2 = getHelper();
      if (var2 != null && var2.isEnabled()) {
         var1.cancel();
      }
   }

   private static RpHelper getHelper() {
      return Onetap.MODULE_MANAGER.get(RpHelper.class);
   }
}
