package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.ShulkerView;

@Mixin(AbstractContainerScreen.class)
public abstract class ShulkerViewScreenMixin {
   @Shadow
   protected Slot hoveredSlot;

   @Inject(method = "extractTooltip", at = @At("HEAD"), cancellable = true)
   private void onetap$shulkerInventoryTooltip(GuiGraphicsExtractor var1, int var2, int var3, CallbackInfo var4) {
      ShulkerView var5 = Onetap.MODULE_MANAGER.get(ShulkerView.class);
      if (var5 != null && var5.isEnabled()) {
         if (this.isPlayerInventory()) {
            if (var5.shouldShowInInventory(this.hoveredSlot)) {
               var4.cancel();
               var1.nextStratum();
               var5.m_7(var1, this.hoveredSlot, var2, var3);
            }
         }
      }
   }

   @Inject(method = "extractRenderState", at = @At("RETURN"))
   private void onetap$shulkerContainerPreview(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      ShulkerView var6 = Onetap.MODULE_MANAGER.get(ShulkerView.class);
      if (var6 != null && var6.isEnabled()) {
         if (!this.isPlayerInventory()) {
            if (var6.m_9(this.hoveredSlot)) {
               var1.nextStratum();
               var6.m_0(var1, this.hoveredSlot, var2, var3);
            }
         }
      }
   }

   private boolean isPlayerInventory() {
      ShulkerViewScreenMixin var1 = this;
      return ((Object)var1) instanceof InventoryScreen || ((Object)var1) instanceof CreativeModeInventoryScreen;
   }
}
