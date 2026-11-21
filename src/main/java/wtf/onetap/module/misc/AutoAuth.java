package wtf.onetap.module.misc;

import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoAuth", description = "Авто регистр/логин на серверах", category = Category.MISC)
public class AutoAuth extends Module {
   private final TextSetting password = new TextSetting("Ваш пароль:", "").numericOnly().build();

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
            String text = clientboundSystemChatPacket.content().getString();
            String text2 = (String)this.password.getValue();
            if (MinecraftAccess.MINECRAFT.getConnection() != null) {
               if (text.contains("Войдите") || text.contains("/login")) {
                  MinecraftAccess.MINECRAFT.getConnection().sendCommand("login " + text2);
               }

               if ((text.contains("Зарегистрируйтесь") || text.contains("/reg")) && text2 != null && text2.length() >= 4) {
                  MinecraftAccess.MINECRAFT.getConnection().sendCommand("reg " + text2 + " " + text2);
               }
            }
         }
      }
   }
}
