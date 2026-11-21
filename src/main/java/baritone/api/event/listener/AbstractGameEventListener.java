package baritone.api.event.listener;

import baritone.api.event.events.BlockChangeEvent;
import baritone.api.event.events.BlockInteractEvent;
import baritone.api.event.events.ChatEvent;
import baritone.api.event.events.ChunkEvent;
import baritone.api.event.events.PacketEvent;
import baritone.api.event.events.PathEvent;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.events.RotationMoveEvent;
import baritone.api.event.events.SprintStateEvent;
import baritone.api.event.events.TabCompleteEvent;
import baritone.api.event.events.TickEvent;
import baritone.api.event.events.WorldEvent;

public interface AbstractGameEventListener extends IGameEventListener {
   @Override
   default void onTick(TickEvent var1) {
   }

   @Override
   default void onPostTick(TickEvent var1) {
   }

   @Override
   default void onPlayerUpdate(PlayerUpdateEvent var1) {
   }

   @Override
   default void onSendChatMessage(ChatEvent var1) {
   }

   @Override
   default void onPreTabComplete(TabCompleteEvent var1) {
   }

   @Override
   default void onChunkEvent(ChunkEvent var1) {
   }

   @Override
   default void onBlockChange(BlockChangeEvent var1) {
   }

   @Override
   default void onRenderPass(RenderEvent var1) {
   }

   @Override
   default void onWorldEvent(WorldEvent var1) {
   }

   @Override
   default void onSendPacket(PacketEvent var1) {
   }

   @Override
   default void onReceivePacket(PacketEvent var1) {
   }

   @Override
   default void onPlayerRotationMove(RotationMoveEvent var1) {
   }

   @Override
   default void onPlayerSprintState(SprintStateEvent var1) {
   }

   @Override
   default void onBlockInteract(BlockInteractEvent var1) {
   }

   @Override
   default void onPlayerDeath() {
   }

   @Override
   default void onPathEvent(PathEvent var1) {
   }
}
