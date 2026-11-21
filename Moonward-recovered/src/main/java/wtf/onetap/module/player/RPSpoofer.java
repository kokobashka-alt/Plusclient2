package wtf.onetap.module.player;

import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;

@ModuleInformation(name = "RPSpoofer", description = "Пропускает серверный ресурс-пак, отвечая что он загружен", category = Category.PLAYER)
public class RPSpoofer extends Module {
   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundResourcePackPushPacket) {
         event.cancel();
      }
   }
}
