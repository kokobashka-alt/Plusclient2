package wtf.onetap.mixin.render;

import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.onetap.Onetap;
import wtf.onetap.module.player.AutoTool;

@Mixin(Hud.class)
public class HudHotbarMixin {
   @Redirect(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getSelectedSlot()I"))
   private int onetap$autoToolSelectedSlot(Inventory var1) {
      AutoTool var2 = Onetap.MODULE_MANAGER.get(AutoTool.class);
      return var2 != null ? var2.getSlot(var1) : var1.getSelectedSlot();
   }
}
