package wtf.onetap.mixin.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.bot.BotConnection;
import wtf.onetap.config.ChatPrivacy;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.module.movement.Blink;
import wtf.onetap.util.PacketUtil;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientPacketListenerMixin {
   @Shadow
   @Final
   protected Connection connection;

   @Inject(method = "send", at = @At("HEAD"), cancellable = true)
   private void onetap$onSend(Packet<?> var1, CallbackInfo var2) {
      if (!PacketUtil.consumeTracked(var1)) {
         SendPacketEvent var3 = new SendPacketEvent(var1);
         Onetap.EVENT_BUS.unregister(var3);
         if (var3.isCancelled()) {
            var2.cancel();
            return;
         }
      }

      Blink var4 = Onetap.MODULE_MANAGER.get(Blink.class);
      if (var4 != null && var4.m_3((ClientCommonPacketListenerImpl)(Object)this, var1) || PacketUtil.m_9(this.connection, var1)) {
         var2.cancel();
      }
   }

   @Inject(method = "send", at = @At("TAIL"))
   private void onetap$onSent(Packet<?> var1, CallbackInfo var2) {
      PacketUtil.m_4(this.connection, var1);
      ChatPrivacy.m_0_void(var1);
   }

   @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
   private void onetap$onCustomPayload(ClientboundCustomPayloadPacket var1, CallbackInfo var2) {
      ReceivePacketEvent var3 = new ReceivePacketEvent(var1);
      Onetap.EVENT_BUS.unregister(var3);
      if (var3.isCancelled()) {
         var2.cancel();
      }
   }

   @Inject(method = "handlePing", at = @At("HEAD"))
   private void onetap$onPing(ClientboundPingPacket var1, CallbackInfo var2) {
      if (!Minecraft.getInstance().isSameThread()) {
         Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
      }
   }

   @Inject(method = "handleDisconnect", at = @At("HEAD"), cancellable = true)
   private void onetap$onDisconnect(ClientboundDisconnectPacket var1, CallbackInfo var2) {
      ReceivePacketEvent var3 = new ReceivePacketEvent(var1);
      Onetap.EVENT_BUS.unregister(var3);
      if (var3.isCancelled()) {
         var2.cancel();
      }
   }

   @Inject(method = "onPacketError", at = @At("HEAD"), cancellable = true)
   private void onetap$parkedPacketError(Packet<?> var1, Exception var2, CallbackInfo var3) {
      if (BotConnection.BOT_CONNECTION.m_4((PacketListener)this, var1, var2)) {
         var3.cancel();
      }
   }

   @Inject(method = "handleTransfer", at = @At("HEAD"), cancellable = true)
   private void onetap$parkedTransfer(ClientboundTransferPacket var1, CallbackInfo var2) {
      if (BotConnection.BOT_CONNECTION.m_9_boolean()) {
         this.connection.disconnect(Component.translatable("disconnect.transfer"));
         var2.cancel();
      }
   }

   @Inject(method = "handleResourcePackPush", at = @At("HEAD"), cancellable = true)
   private void onetap$onResourcePackPush(ClientboundResourcePackPushPacket var1, CallbackInfo var2) {
      ReceivePacketEvent var3 = new ReceivePacketEvent(var1);
      Onetap.EVENT_BUS.unregister(var3);
      if (var3.isCancelled()) {
         ClientCommonPacketListenerImpl var4 = (ClientCommonPacketListenerImpl)(Object)this;
         var4.send(new ServerboundResourcePackPacket(var1.id(), Action.ACCEPTED));
         var4.send(new ServerboundResourcePackPacket(var1.id(), Action.DOWNLOADED));
         var4.send(new ServerboundResourcePackPacket(var1.id(), Action.SUCCESSFULLY_LOADED));
         var2.cancel();
      }
   }
}
