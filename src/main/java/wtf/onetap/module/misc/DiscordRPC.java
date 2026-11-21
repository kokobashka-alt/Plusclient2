package wtf.onetap.module.misc;

import net.minecraft.client.multiplayer.ServerData;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.discord.DiscordPresence;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.platform.C7a;
import wtf.onetap.platform.PlatformConnection;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "DiscordRPC", description = "Дискорд активность", category = Category.MISC)
public class DiscordRPC extends Module {
   private int value;

   @EventTarget
   private void onTick(TickEvent event) {
      UnHook.m_7_IArr();
      String text = C7a.m_4_String();
      String text2 = C7a.m_0_String();
      if (PlatformConnection.PLATFORM_CONNECTION.isFlag() && text != null && text2 != null) {
         if (!DiscordPresence.isRunning()) {
            DiscordPresence.start();
            this.value = 0;
         }

         if (this.value++ % 100 == 0) {
            String text3 = DiscordPresence.m_8_String();
            if (text3 != null) {
               ChatUtil.log("§cDiscord RPC: " + text3);
            }

            ServerData serverData = MinecraftAccess.MINECRAFT.getCurrentServer();
            DiscordPresence.m_2("UID: " + text, "Роль: " + text2, serverData != null ? serverData.ip : null);
         }
      } else {
         if (DiscordPresence.isRunning()) {
            DiscordPresence.m_7_void();
         }
      }
   }

   @Override
   public void onEnable() {
      this.value = 0;
   }

   @Override
   public void onDisable() {
      DiscordPresence.m_7_void();
   }
}
