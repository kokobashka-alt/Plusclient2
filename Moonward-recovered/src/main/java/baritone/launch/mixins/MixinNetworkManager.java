package baritone.launch.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.PacketEvent;
import baritone.api.event.events.type.EventState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class MixinNetworkManager {
   @Shadow
   private Channel channel;
   @Shadow
   @Final
   private PacketFlow receiving;

   @Inject(method = "sendPacket", at = @At("HEAD"))
   private void preDispatchPacket(Packet<?> var1, ChannelFutureListener var2, boolean var3, CallbackInfo var4) {
      if (this.receiving == PacketFlow.CLIENTBOUND) {
         for (IBaritone var6 : BaritoneAPI.getProvider().getAllBaritones()) {
            if (var6.getPlayerContext().player() != null && var6.getPlayerContext().player().connection.getConnection() == (Connection)(Object)this) {
               var6.getGameEventHandler().onSendPacket(new PacketEvent((Connection)(Object)this, EventState.PRE, var1));
            }
         }
      }
   }

   @Inject(method = "sendPacket", at = @At("RETURN"))
   private void postDispatchPacket(Packet<?> var1, ChannelFutureListener var2, boolean var3, CallbackInfo var4) {
      if (this.receiving == PacketFlow.CLIENTBOUND) {
         for (IBaritone var6 : BaritoneAPI.getProvider().getAllBaritones()) {
            if (var6.getPlayerContext().player() != null && var6.getPlayerContext().player().connection.getConnection() == (Connection)(Object)this) {
               var6.getGameEventHandler().onSendPacket(new PacketEvent((Connection)(Object)this, EventState.POST, var1));
            }
         }
      }
   }

   @Inject(
      method = "channelRead0",
      at = @At(
         value = "INVOKE",
         target = "net/minecraft/network/Connection.genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V"
      )
   )
   private void preProcessPacket(ChannelHandlerContext var1, Packet<?> var2, CallbackInfo var3) {
      if (this.receiving == PacketFlow.CLIENTBOUND) {
         for (IBaritone var5 : BaritoneAPI.getProvider().getAllBaritones()) {
            if (var5.getPlayerContext().player() != null && var5.getPlayerContext().player().connection.getConnection() == (Connection)(Object)this) {
               var5.getGameEventHandler().onReceivePacket(new PacketEvent((Connection)(Object)this, EventState.PRE, var2));
            }
         }
      }
   }

   @Inject(method = "channelRead0", at = @At("RETURN"))
   private void postProcessPacket(ChannelHandlerContext var1, Packet<?> var2, CallbackInfo var3) {
      if (this.channel.isOpen() && this.receiving == PacketFlow.CLIENTBOUND) {
         for (IBaritone var5 : BaritoneAPI.getProvider().getAllBaritones()) {
            if (var5.getPlayerContext().player() != null && var5.getPlayerContext().player().connection.getConnection() == (Connection)(Object)this) {
               var5.getGameEventHandler().onReceivePacket(new PacketEvent((Connection)(Object)this, EventState.POST, var2));
            }
         }
      }
   }
}
