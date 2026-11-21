package wtf.onetap.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.player.ItemHelper;

@Mixin(AbstractContainerScreen.class)
public abstract class ItemHelperScreenMixin {
   @Inject(
      method = "extractContents",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlotHighlightFront(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"
      )
   )
   private void onetap$renderItemHelper(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      ItemHelper var6 = Onetap.MODULE_MANAGER.get(ItemHelper.class);
      if (var6 != null && var6.isEnabled()) {
         AbstractContainerScreen var7 = (AbstractContainerScreen)(Object)this;
         if (!(var7 instanceof CreativeModeInventoryScreen)) {
            if (var7.getMenu() instanceof InventoryMenu) {
               var6.renderSlots(var1, var7.getMenu());
            }
         }
      }
   }
}
