package wtf.onetap.network;

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.util.ChatUtil;

public class ServerInfoListener {
   private static String text;

   public static String m_0_String() {
      return text;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      ChatUtil.m_3_IArr();
      switch (event.getPacket()) {
         case ClientboundCustomPayloadPacket clientboundCustomPayloadPacket when clientboundCustomPayloadPacket.payload() instanceof BrandPayload brandPayload:
            text = brandPayload.brand();
            break;
         case ClientboundLoginPacket clientboundLoginPacket:
            text = null;
            break;
         case ClientboundRespawnPacket clientboundRespawnPacket:
            text = null;
            break;
         case ClientboundDisconnectPacket clientboundDisconnectPacket:
            text = null;
            break;
         default:
      }
   }
}
