package wtf.onetap.mixin.render;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.NameProtect;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
   @ModifyArg(
      method = "addMessage",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/chat/GuiMessage;<init>(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"
      ),
      index = 1
   )
   private Component onetap$protectName(Component var1) {
      NameProtect var2 = Onetap.MODULE_MANAGER.get(NameProtect.class);
      return var2 != null && var2.isEnabled() ? var2.replaceInComponent(var1) : var1;
   }
}
