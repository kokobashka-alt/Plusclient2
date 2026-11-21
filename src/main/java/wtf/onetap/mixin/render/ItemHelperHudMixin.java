package wtf.onetap.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.player.ItemHelper;

@Mixin(Hud.class)
public abstract class ItemHelperHudMixin {
   @Inject(method = "extractItemHotbar", at = @At("RETURN"))
   private void onetap$renderItemHelperHotbar(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      ItemHelper var4 = Onetap.MODULE_MANAGER.get(ItemHelper.class);
      if (var4 != null && var4.isEnabled()) {
         var4.m_6(var1);
      }
   }
}
