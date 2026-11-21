package wtf.onetap.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

public interface MinecraftAccess {
   Minecraft MINECRAFT = Minecraft.getInstance();

   default LocalPlayer m_1_LocalPlayer() {
      return MINECRAFT.player;
   }

   default ClientLevel m_1_ClientLevel() {
      return MINECRAFT.level;
   }
}
