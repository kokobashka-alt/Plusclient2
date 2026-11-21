package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.module.combat.AutoSwap;

@Mixin(AbstractContainerScreen.class)
public abstract class AutoSwapScreenMixin {
   @Shadow
   protected Slot hoveredSlot;

   @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void onetap$autoSwapPick(MouseButtonEvent var1, boolean var2, CallbackInfoReturnable<Boolean> var3) {
      AutoSwap var4 = Onetap.MODULE_MANAGER.get(AutoSwap.class);
      if (var4 != null && var4.m_5_boolean()) {
         if ((Object)this instanceof InventoryScreen) {
            var3.setReturnValue(true);
            var4.m_3_void(this.hoveredSlot);
         }
      }
   }
}
