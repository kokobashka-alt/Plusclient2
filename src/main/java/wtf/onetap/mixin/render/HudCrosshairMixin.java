package wtf.onetap.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.module.combat.AutoSwap;
import wtf.onetap.module.render.Crosshair;

@Mixin(Hud.class)
public class HudCrosshairMixin {
   @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
   private void onetap$cancelCrosshair(GuiGraphicsExtractor var1, DeltaTracker var2, CallbackInfo var3) {
      Crosshair var4 = Onetap.MODULE_MANAGER.get(Crosshair.class);
      if (var4 != null && var4.isEnabled()) {
         var3.cancel();
      } else {
         AutoSwap var5 = Onetap.MODULE_MANAGER.get(AutoSwap.class);
         if (var5 != null && var5.m_3_boolean()) {
            var3.cancel();
         }
      }
   }
}
