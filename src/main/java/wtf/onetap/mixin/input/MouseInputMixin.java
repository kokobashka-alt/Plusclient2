package wtf.onetap.mixin.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.MouseClickEvent;
import wtf.onetap.module.render.ClickGUI;

@Mixin(MouseHandler.class)
public class MouseInputMixin {
   @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
   private void onetap$onButton(long var1, MouseButtonInfo var3, int var4, CallbackInfo var5) {
      Minecraft var6 = Minecraft.getInstance();
      boolean var7 = ClickGUI.isActive();
      boolean var8 = false;
      if (!var7 && var6.gui.screen() == null && var4 == 1 && var3.button() >= 2) {
         KeyPressEvent var9 = new KeyPressEvent(-var3.button() - 1, var4);
         Onetap.EVENT_BUS.unregister(var9);
         var8 = var9.isCancelled();
      }

      double var16 = var6.getWindow().getGuiScale();
      double var11 = var6.mouseHandler.xpos() / var16;
      double var13 = var6.mouseHandler.ypos() / var16;
      MouseClickEvent var15 = new MouseClickEvent(var11, var13, var3.button(), var4);
      Onetap.EVENT_BUS.unregister(var15);
      if (var7 || var15.isCancelled() || var8) {
         var5.cancel();
      }
   }
}
