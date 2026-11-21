package wtf.onetap.network;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import wtf.onetap.util.ChatUtil;

public class ServerTpsTracker {
   public static final ServerTpsTracker SERVER_TPS_TRACKER = new ServerTpsTracker();
   private final Map<ClientPacketListener, Long> clientPacketListenerMap;
   private final Map<ClientPacketListener, Float> clientPacketListenerMap2;

   private ServerTpsTracker() {
      this.clientPacketListenerMap = new WeakHashMap<>();
      this.clientPacketListenerMap2 = new WeakHashMap<>();
   }

   public void m_2(ClientPacketListener clientPacketListener) {
      long longValue = System.nanoTime();
      Long longObj = this.clientPacketListenerMap.put(clientPacketListener, longValue);
      if (longObj != null) {
         float floatValue = 20.0F * (1.0E9F / (float)(longValue - longObj));
         this.clientPacketListenerMap2.put(clientPacketListener, Math.round(Math.clamp(floatValue, 0.0F, 20.0F) * 100.0F) / 100.0F);
      }
   }

   public float getTps() {
      ChatUtil.m_3_IArr();
      ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
      return clientPacketListener == null ? 20.0F : this.clientPacketListenerMap2.getOrDefault(clientPacketListener, 20.0F);
   }
}
