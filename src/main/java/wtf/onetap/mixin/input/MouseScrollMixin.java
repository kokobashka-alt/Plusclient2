package wtf.onetap.mixin.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.autobuy.PurchaseHistoryOverlay;
import wtf.onetap.event.MouseScrollEvent;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.module.render.Zoom;

@Mixin(MouseHandler.class)
public class MouseScrollMixin {
   @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
   private void onetap$onScroll(long var1, double var3, double var5, CallbackInfo var7) {
      Minecraft var8 = Minecraft.getInstance();
      MouseHandler var9 = var8.mouseHandler;
      if (ClickGUI.isActive()) {
         double var17 = var8.getWindow().getGuiScale();
         Onetap.EVENT_BUS.unregister(new MouseScrollEvent(var9.xpos() / var17, var9.ypos() / var17, var5));
         var7.cancel();
      } else {
         if (var8.gui.screen() instanceof ContainerScreen && PurchaseHistoryOverlay.m_7_C5_v().m_4_boolean()) {
            double var10 = var8.getWindow().getGuiScale();
            double var12 = var9.xpos() / var10;
            double var14 = var9.ypos() / var10;
            if (PurchaseHistoryOverlay.m_7_C5_v().m_3_boolean(var12, var14, var5)) {
               var7.cancel();
            }
         }

         if (var8.gui.screen() == null) {
            Zoom var16 = Onetap.MODULE_MANAGER.get(Zoom.class);
            if (var16 != null && var16.m_1_boolean(var5)) {
               var7.cancel();
            }
         }
      }
   }

   @Redirect(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
   private Object onetap$zoomSensitivity(OptionInstance<?> var1) {
      Object var2 = var1.get();
      Zoom var3 = Onetap.MODULE_MANAGER.get(Zoom.class);
      return var2 instanceof Double var4 && var3 != null && var3.isEnabled() ? var4 / Math.max(0.5, var3.getDoubleValue()) : var2;
   }
}
