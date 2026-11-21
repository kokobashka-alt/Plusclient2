package wtf.onetap.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import wtf.onetap.obf.C7n;

public class HotbarUtil {
   public static void m_9_void(int value) {
      C7n.m_1_C3_sArr();
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && value >= 0 && value <= 8 && mc.player.getInventory().getSelectedSlot() != value) {
         mc.player.getInventory().setSelectedSlot(value);
         mc.player.connection.send(new ServerboundSetCarriedItemPacket(value));
      }
   }
}
