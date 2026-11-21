package wtf.onetap.event;

import net.minecraft.network.protocol.Packet;

public class ReceivePacketEvent extends Event implements Cancellable {
   private final Packet<?> packet;
   private boolean cancelled;
   private static int value;

   public ReceivePacketEvent(Packet<?> packet2) {
      this.packet = packet2;
   }

   public Packet<?> getPacket() {
      return this.packet;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }

   public static void m_4_void(int value2) {
      value = value2;
   }

   public static int m_1_int() {
      return value;
   }

   public static int m_0_int() {
      return 0;
   }
}
