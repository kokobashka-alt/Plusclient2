package baritone.launch.mixins;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.BlockChangeEvent;
import baritone.api.event.events.ChatEvent;
import baritone.api.event.events.ChunkEvent;
import baritone.api.event.events.type.EventState;
import baritone.api.utils.Pair;
import baritone.cache.CachedChunk;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPlayNetHandler extends ClientCommonPacketListenerImpl {
   protected MixinClientPlayNetHandler(Minecraft var1, Connection var2, CommonListenerCookie var3) {
      super(var1, var2, var3);
   }

   @Inject(method = "sendChat(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
   private void sendChatMessage(String var1, CallbackInfo var2) {
      ChatEvent var3 = new ChatEvent(var1);
      IBaritone var4 = BaritoneAPI.getProvider().getBaritoneForPlayer(this.minecraft.player);
      if (var4 != null) {
         var4.getGameEventHandler().onSendChatMessage(var3);
         if (var3.isCancelled()) {
            var2.cancel();
         }
      }
   }

   @Inject(method = "handleLevelChunkWithLight", at = @At("RETURN"))
   private void postHandleChunkData(ClientboundLevelChunkWithLightPacket var1, CallbackInfo var2) {
      for (IBaritone var4 : BaritoneAPI.getProvider().getAllBaritones()) {
         LocalPlayer var5 = var4.getPlayerContext().player();
         if (var5 != null && var5.connection == (ClientPacketListener)(Object)this) {
            var4.getGameEventHandler()
               .onChunkEvent(
                  new ChunkEvent(
                     EventState.POST, !var1.isSkippable() ? ChunkEvent.Type.POPULATE_FULL : ChunkEvent.Type.POPULATE_PARTIAL, var1.getX(), var1.getZ()
                  )
               );
         }
      }
   }

   @Inject(method = "handleForgetLevelChunk", at = @At("HEAD"))
   private void preChunkUnload(ClientboundForgetLevelChunkPacket var1, CallbackInfo var2) {
      for (IBaritone var4 : BaritoneAPI.getProvider().getAllBaritones()) {
         LocalPlayer var5 = var4.getPlayerContext().player();
         if (var5 != null && var5.connection == (ClientPacketListener)(Object)this) {
            var4.getGameEventHandler().onChunkEvent(new ChunkEvent(EventState.PRE, ChunkEvent.Type.UNLOAD, var1.pos().x(), var1.pos().z()));
         }
      }
   }

   @Inject(method = "handleForgetLevelChunk", at = @At("RETURN"))
   private void postChunkUnload(ClientboundForgetLevelChunkPacket var1, CallbackInfo var2) {
      for (IBaritone var4 : BaritoneAPI.getProvider().getAllBaritones()) {
         LocalPlayer var5 = var4.getPlayerContext().player();
         if (var5 != null && var5.connection == (ClientPacketListener)(Object)this) {
            var4.getGameEventHandler().onChunkEvent(new ChunkEvent(EventState.POST, ChunkEvent.Type.UNLOAD, var1.pos().x(), var1.pos().z()));
         }
      }
   }

   @Inject(method = "handleBlockUpdate", at = @At("RETURN"))
   private void postHandleBlockChange(ClientboundBlockUpdatePacket var1, CallbackInfo var2) {
      if (Baritone.settings().repackOnAnyBlockChange.value) {
         if (CachedChunk.BLOCKS_TO_KEEP_TRACK_OF.contains(var1.getBlockState().getBlock())) {
            for (IBaritone var4 : BaritoneAPI.getProvider().getAllBaritones()) {
               LocalPlayer var5 = var4.getPlayerContext().player();
               if (var5 != null && var5.connection == (ClientPacketListener)(Object)this) {
                  var4.getGameEventHandler()
                     .onChunkEvent(new ChunkEvent(EventState.POST, ChunkEvent.Type.POPULATE_FULL, var1.getPos().getX() >> 4, var1.getPos().getZ() >> 4));
               }
            }
         }
      }
   }

   @Inject(method = "handleChunkBlocksUpdate", at = @At("RETURN"))
   private void postHandleMultiBlockChange(ClientboundSectionBlocksUpdatePacket var1, CallbackInfo var2) {
      IBaritone var3 = BaritoneAPI.getProvider().getBaritoneForConnection((ClientPacketListener)(Object)this);
      if (var3 != null) {
         ArrayList var4 = new ArrayList();
         var1.runUpdates((var1x, var2x) -> var4.add(new Pair<>(var1x.immutable(), var2x)));
         if (!var4.isEmpty()) {
            var3.getGameEventHandler().onBlockChange(new BlockChangeEvent(ChunkPos.containing((BlockPos)((Pair)var4.get(0)).first()), var4));
         }
      }
   }

   @Inject(method = "handlePlayerCombatKill", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;shouldShowDeathScreen()Z"))
   private void onPlayerDeath(ClientboundPlayerCombatKillPacket var1, CallbackInfo var2) {
      for (IBaritone var4 : BaritoneAPI.getProvider().getAllBaritones()) {
         LocalPlayer var5 = var4.getPlayerContext().player();
         if (var5 != null && var5.connection == (ClientPacketListener)(Object)this) {
            var4.getGameEventHandler().onPlayerDeath();
         }
      }
   }
}
