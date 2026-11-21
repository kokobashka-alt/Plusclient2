package baritone.launch.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.ChatEvent;
import baritone.api.utils.Helper;
import baritone.utils.accessor.IGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen implements IGuiScreen {
   @Inject(method = "defaultHandleGameClickEvent", at = @At("HEAD"), cancellable = true)
   private static void handleCustomClickEvent(ClickEvent var0, Minecraft var1, Screen var2, CallbackInfo var3) {
      if (var0 != null) {
         if (var0 instanceof RunCommand(String var4)) {
            if (var4.startsWith(Helper.FORCE_COMMAND_PREFIX)) {
               IBaritone var8 = BaritoneAPI.getProvider().getPrimaryBaritone();
               if (var8 != null) {
                  var8.getGameEventHandler().onSendChatMessage(new ChatEvent(var4));
               }

               var3.cancel();
            }
         }
      }
   }
}
