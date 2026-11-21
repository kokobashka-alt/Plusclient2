package wtf.onetap.module.player;

import java.util.Locale;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoAccept", description = "Автоматически принимает запросы телепортации", category = Category.PLAYER)
public class AutoAccept extends Module {
   private final BooleanSetting friends = new BooleanSetting("Друзья", false).build();

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      VoiceChat.m_9_StringArr();
      if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         String text = clientboundSystemChatPacket.content().getString().toLowerCase(Locale.ROOT);
         if (text.contains("телепортироваться") || text.contains("has requested teleport")) {
            if (!(Boolean)this.friends.getValue() || !Onetap.FRIEND_MANAGER.getFriends().stream().noneMatch(text2 -> text.contains(text2.toLowerCase(Locale.ROOT)))) {
               MinecraftAccess.MINECRAFT.getConnection().sendCommand("tpaccept");
            }
         }
      }
   }
}
