package wtf.onetap.mixin.client;

import com.mojang.blaze3d.platform.FramerateLimitTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.util.ChatUtil;

@Mixin(FramerateLimitTracker.class)
public abstract class FramerateLimitTrackerMixin {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Shadow
   private int framerateLimit;

   @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
   private void onetap$uncapTitle(CallbackInfoReturnable<Integer> var1) {
      if (!this.minecraft.getWindow().isIconified() && !ChatUtil.isUnHooked()) {
         if (this.minecraft.gui.screen() instanceof TitleScreen) {
            var1.setReturnValue(this.framerateLimit);
         }
      }
   }
}
