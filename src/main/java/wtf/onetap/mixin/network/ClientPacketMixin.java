package wtf.onetap.mixin.network;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.ChatType.Bound;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.bot.BotConnection;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.RemoveEntitiesEvent;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.module.render.TotemPop;
import wtf.onetap.network.ServerTpsTracker;

@Mixin(ClientPacketListener.class)
public class ClientPacketMixin {
   private static final String ENSURE_RUNNING_ON_CLIENT_THREAD = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V";

   @Inject(method = "handleSetTime", at = @At("TAIL"))
   private void onetap$onTimeUpdate(ClientboundSetTimePacket var1, CallbackInfo var2) {
      ServerTpsTracker.SERVER_TPS_TRACKER.m_2((ClientPacketListener)(Object)this);
   }

   @Inject(
      method = "handleLogin",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onLogin(ClientboundLoginPacket var1, CallbackInfo var2) {
      BotConnection.BOT_CONNECTION.m_9((ClientPacketListener)(Object)this);
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Redirect(
      method = "handleConfigurationStart",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V")
   )
   private void onetap$parkedReconfigure(Minecraft var1, Screen var2) {
      if (!BotConnection.BOT_CONNECTION.m_9_boolean()) {
         var1.clearClientLevel(var2);
      }
   }

   @ModifyArg(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setId(I)V"))
   private int onetap$fixZeroPlayerId(int var1) {
      return var1 == 0 ? Integer.MAX_VALUE : var1;
   }

   @Inject(
      method = "handleBossUpdate",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      ),
      cancellable = true
   )
   private void onetap$safeBoss(ClientboundBossEventPacket var1, CallbackInfo var2) {
      PacketListener var3 = (PacketListener)this;
      if (BotConnection.BOT_CONNECTION.m_4(var3)) {
         BotConnection.BOT_CONNECTION.m_5(var3, var1);
         var2.cancel();
      } else {
         try {
            Minecraft.getInstance().gui.hud.getBossOverlay().update(var1);
         } catch (NullPointerException var5) {
         }

         var2.cancel();
      }
   }

   @Inject(
      method = "handleRespawn",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onRespawn(ClientboundRespawnPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "setActionBarText",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onActionBar(ClientboundSetActionBarTextPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleMovePlayer",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onMovePlayer(ClientboundPlayerPositionPacket var1, CallbackInfo var2) {
      NoClip.m_7s_void();
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(method = "handleMovePlayer", at = @At("RETURN"))
   private void onetap$noClipRestoreRotation(ClientboundPlayerPositionPacket var1, CallbackInfo var2) {
      NoClip.m_7i_void();
   }

   @Inject(
      method = "handleChunkBlocksUpdate",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleBlockUpdate",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onBlockUpdate(ClientboundBlockUpdatePacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleLevelChunkWithLight",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onLevelChunk(ClientboundLevelChunkWithLightPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleForgetLevelChunk",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onForgetLevelChunk(ClientboundForgetLevelChunkPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleItemCooldown",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onItemCooldown(ClientboundCooldownPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleSoundEvent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onSound(ClientboundSoundPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleTakeItemEntity",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onTakeItemEntity(ClientboundTakeItemEntityPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleSetEntityData",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onSetEntityData(ClientboundSetEntityDataPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleSetEntityMotion",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      ),
      cancellable = true
   )
   private void onetap$onEntityVelocity(ClientboundSetEntityMotionPacket var1, CallbackInfo var2) {
      ReceivePacketEvent var3 = new ReceivePacketEvent(var1);
      Onetap.EVENT_BUS.unregister(var3);
      if (var3.isCancelled()) {
         var2.cancel();
      }
   }

   @Inject(
      method = "handleMoveEntity",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onMoveEntity(ClientboundMoveEntityPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleEntityPositionSync",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onEntityPositionSync(ClientboundEntityPositionSyncPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleExplosion",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      ),
      cancellable = true
   )
   private void onetap$onExplosion(ClientboundExplodePacket var1, CallbackInfo var2) {
      ReceivePacketEvent var3 = new ReceivePacketEvent(var1);
      Onetap.EVENT_BUS.unregister(var3);
      if (var3.isCancelled()) {
         var2.cancel();
      }
   }

   @Inject(
      method = "handleEntityEvent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onEntityEvent(ClientboundEntityEventPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleRemoveMobEffect",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onRemoveMobEffect(ClientboundRemoveMobEffectPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Redirect(
      method = "handleEntityEvent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/particle/ParticleEngine;createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;I)V"
      )
   )
   private void onetap$cancelVanillaTotemParticles(ParticleEngine var1, Entity var2, ParticleOptions var3, int var4) {
      if (var3 == ParticleTypes.TOTEM_OF_UNDYING) {
         TotemPop var5 = Onetap.MODULE_MANAGER.get(TotemPop.class);
         if (var5 != null && var5.isEnabled()) {
            return;
         }
      }

      var1.createTrackingEmitter(var2, var3, var4);
   }

   @Inject(
      method = "handleRemoveEntities",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onRemoveEntities(ClientboundRemoveEntitiesPacket var1, CallbackInfo var2) {
      for (int var3 = 0; var3 < var1.getEntityIds().size(); var3++) {
         Onetap.EVENT_BUS.unregister(new RemoveEntitiesEvent(var1.getEntityIds().getInt(var3)));
      }
   }

   @Inject(
      method = "handleOpenScreen",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onOpenScreen(ClientboundOpenScreenPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleContainerContent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onContainerContent(ClientboundContainerSetContentPacket var1, CallbackInfo var2) {
      Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
   }

   @Inject(
      method = "handleSystemChat",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      ),
      cancellable = true
   )
   private void onetap$onSystemChat(ClientboundSystemChatPacket var1, CallbackInfo var2) {
      if (BotConnection.BOT_CONNECTION.m_4((PacketListener)this)) {
         var2.cancel();
      } else {
         ReceivePacketEvent var3 = new ReceivePacketEvent(var1);
         Onetap.EVENT_BUS.unregister(var3);
         if (var3.isCancelled()) {
            var2.cancel();
         }
      }
   }

   @Inject(
      method = "handlePlayerChat",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      )
   )
   private void onetap$onPlayerChat(ClientboundPlayerChatPacket var1, CallbackInfo var2) {
      if (!BotConnection.BOT_CONNECTION.m_4((PacketListener)this)) {
         Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
      }
   }

   @Redirect(
      method = "handlePlayerChat",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;handlePlayerChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/chat/ChatType$Bound;)V"
      )
   )
   private void onetap$hiddenPlayerChat(ChatListener var1, PlayerChatMessage var2, GameProfile var3, Bound var4) {
      ClientPacketListener var5 = (ClientPacketListener)(Object)this;
      if (!BotConnection.BOT_CONNECTION.m_4(var5)) {
         var1.handlePlayerChatMessage(var2, var3, var4);
      } else if (var2.signature() != null) {
         var5.markMessageAsProcessed(var2.signature(), false);
      }
   }

   @Redirect(
      method = "handlePlayerChat",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;handleChatMessageError(Ljava/util/UUID;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/network/chat/ChatType$Bound;)V"
      )
   )
   private void onetap$hiddenChatError(ChatListener var1, UUID var2, MessageSignature var3, Bound var4) {
      ClientPacketListener var5 = (ClientPacketListener)(Object)this;
      if (!BotConnection.BOT_CONNECTION.m_4(var5)) {
         var1.handleChatMessageError(var2, var3, var4);
      } else if (var3 != null) {
         var5.markMessageAsProcessed(var3, false);
      }
   }

   @Inject(
      method = "handleDisguisedChat",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
         shift = Shift.AFTER
      ),
      cancellable = true
   )
   private void onetap$onDisguisedChat(ClientboundDisguisedChatPacket var1, CallbackInfo var2) {
      if (BotConnection.BOT_CONNECTION.m_4((PacketListener)this)) {
         var2.cancel();
      } else {
         Onetap.EVENT_BUS.unregister(new ReceivePacketEvent(var1));
      }
   }
}
