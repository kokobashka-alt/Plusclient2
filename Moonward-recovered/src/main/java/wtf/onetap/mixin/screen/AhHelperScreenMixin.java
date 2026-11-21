package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.autobuy.AhHelperRenderer;

@Mixin(AbstractContainerScreen.class)
public abstract class AhHelperScreenMixin {
   @Inject(
      method = "extractContents",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlotHighlightFront(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"
      )
   )
   private void onetap$renderAhHelper(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      AhHelperRenderer.m_9(var1, (AbstractContainerScreen<?>)(Object)this);
   }
}
