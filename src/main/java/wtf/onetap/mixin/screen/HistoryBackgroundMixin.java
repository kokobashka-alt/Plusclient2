package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.autobuy.PurchaseHistoryOverlay;

@Mixin(Screen.class)
public abstract class HistoryBackgroundMixin {
   @Inject(method = "extractTransparentBackground", at = @At("HEAD"), cancellable = true)
   private void onetap$historyBackground(GuiGraphicsExtractor var1, CallbackInfo var2) {
      if ((Object)this instanceof ContainerScreen && PurchaseHistoryOverlay.m_7_C5_v().m_4_boolean()) {
         var2.cancel();
      }
   }
}
