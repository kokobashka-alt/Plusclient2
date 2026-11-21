package baritone.api.event.events;

import baritone.api.event.events.type.EventState;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

public class PacketEvent {
   private final Connection networkManager;
   private final EventState state;
   private final Packet<?> packet;

   public PacketEvent(Connection var1, EventState var2, Packet<?> var3) {
      this.networkManager = var1;
      this.state = var2;
      this.packet = var3;
   }

   public final Connection getNetworkManager() {
      return this.networkManager;
   }

   public final EventState getState() {
      return this.state;
   }

   public final Packet<?> getPacket() {
      return this.packet;
   }

   public final <T extends Packet<?>> T cast() {
      return (T)this.packet;
   }
}
