package wtf.onetap.mixin.input;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent.Action;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.event.CharTypedEvent;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.module.render.ClickGUI;

@Mixin(KeyboardHandler.class)
public class KeyboardInputMixin {
   @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
   private void onetap$keyPress(long var1, @Action int var3, KeyEvent var4, CallbackInfo var5) {
      if (var4.key() != -1) {
         if (ClickGUI.isActive() && var4.key() == 256) {
            if (var3 == 1) {
               Onetap.EVENT_BUS.unregister(new KeyPressEvent(var4.key(), var3));
            }

            var5.cancel();
         } else if (ClickGUI.isActive() && ClickGuiScreen.isEditing()) {
            if (var3 == 1 || var3 == 2) {
               Onetap.EVENT_BUS.unregister(new KeyPressEvent(var4.key(), var3));
            }

            var5.cancel();
         } else {
            if (var3 == 1 || var3 == 2) {
               KeyPressEvent var6 = new KeyPressEvent(var4.key(), var3);
               Onetap.EVENT_BUS.unregister(var6);
               if (var6.isCancelled()) {
                  var5.cancel();
               }
            }
         }
      }
   }

   @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
   private void onetap$charTyped(long var1, CharacterEvent var3, CallbackInfo var4) {
      if (ClickGUI.isActive() && ClickGuiScreen.isEditing()) {
         Onetap.EVENT_BUS.unregister(new CharTypedEvent(var3.codepoint()));
         var4.cancel();
      }
   }
}
