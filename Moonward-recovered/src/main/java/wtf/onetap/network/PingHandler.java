package wtf.onetap.network;

import java.util.ArrayDeque;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;

public class PingHandler {
   private static final ArrayDeque<Long> ARRAY_DEQUE = new ArrayDeque<>();
   private static long longValue;

   public static synchronized long m_0_long() {
      if (ARRAY_DEQUE.isEmpty()) {
         return 0L;
      }

      long longValue2 = 0L;

      for (long longValue3 : ARRAY_DEQUE) {
         longValue2 += longValue3;
      }

      return longValue2 / ARRAY_DEQUE.size();
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (event.getPacket() instanceof ServerboundPongPacket) {
         synchronized (PingHandler.class) {
            longValue = System.currentTimeMillis();
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundPingPacket) {
         synchronized (PingHandler.class) {
            long longValue2 = System.currentTimeMillis() - longValue;
            if (longValue2 >= 0L && longValue2 < 10000L) {
               ARRAY_DEQUE.addLast(longValue2);
               if (ARRAY_DEQUE.size() > 10) {
                  ARRAY_DEQUE.removeFirst();
               }
            }
         }
      }
   }
}
