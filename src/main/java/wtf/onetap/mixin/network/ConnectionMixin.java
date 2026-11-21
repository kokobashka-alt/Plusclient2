package wtf.onetap.mixin.network;

import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.bot.BotConnection;

@Mixin(Connection.class)
public class ConnectionMixin {
   @Inject(method = "configurePacketHandler", at = @At("HEAD"))
   private void onetap$addProxyHandler(ChannelPipeline var1, CallbackInfo var2) {
      BotConnection.BOT_CONNECTION.m_6(var1, (Connection)(Object)this);
   }

   @Redirect(
      method = "handleDisconnection",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketListener;onDisconnect(Lnet/minecraft/network/DisconnectionDetails;)V")
   )
   private void onetap$dropParkedDisconnect(PacketListener var1, DisconnectionDetails var2) {
      if (!BotConnection.BOT_CONNECTION.m_5(var1, var2)) {
         var1.onDisconnect(var2);
      }
   }
}
