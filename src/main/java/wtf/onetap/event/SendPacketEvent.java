package wtf.onetap.event;

import net.minecraft.network.protocol.Packet;

public class SendPacketEvent extends Event implements Cancellable {
   private final Packet<?> packet;
   private boolean cancelled;
   private static String[] textArray;

   public SendPacketEvent(Packet<?> packet2) {
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

   public static void m_6(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_7_StringArr() {
      return textArray;
   }
}
