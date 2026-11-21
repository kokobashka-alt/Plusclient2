package wtf.onetap.mixin.network;

import java.util.Queue;
import net.minecraft.network.PacketProcessor.ListenerAndPacket;
import net.minecraft.network.PacketProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.bot.BotConnection;
import wtf.onetap.bot.BotManager;

@Mixin(PacketProcessor.class)
public class PacketProcessorMixin {
   @Shadow
   private Queue<ListenerAndPacket<?>> packetsToBeHandled;
   @Shadow
   private boolean closed;

   @Inject(method = "processQueuedPackets", at = @At("HEAD"), cancellable = true)
   private void onetap$routeParkedSessions(CallbackInfo var1) {
      if (BotManager.BOT_MANAGER.getBotClient() != null) {
         var1.cancel();
         if (!this.closed) {
            while (!this.packetsToBeHandled.isEmpty()) {
               ListenerAndPacket var2 = this.packetsToBeHandled.poll();
               BotConnection.BOT_CONNECTION.m_9(var2.listener());

               try {
                  var2.handle();
               } finally {
                  BotConnection.BOT_CONNECTION.m_4_void();
               }
            }
         }
      }
   }
}
