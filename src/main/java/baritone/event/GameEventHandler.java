package baritone.event;

import baritone.Baritone;
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
import baritone.api.event.events.type.EventState;
import baritone.api.event.listener.IEventBus;
import baritone.api.event.listener.IGameEventListener;
import baritone.api.utils.Helper;
import baritone.api.utils.Pair;
import baritone.cache.CachedChunk;
import baritone.cache.WorldProvider;
import baritone.utils.BlockStateInterface;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.chunk.LevelChunk;
import wtf.onetap.util.ChatUtil;

public class GameEventHandler implements IEventBus, Helper {
   private final Baritone baritone;
   private final List<IGameEventListener> listeners = new CopyOnWriteArrayList<>();

   public GameEventHandler(Baritone var1) {
      this.baritone = var1;
   }

   @Override
   public final void onTick(TickEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         if (var1.getType() == TickEvent.Type.IN) {
            try {
               this.baritone.bsi = new BlockStateInterface(this.baritone.getPlayerContext(), true);
            } catch (Exception var3) {
               var3.printStackTrace();
               this.baritone.bsi = null;
            }
         } else {
            this.baritone.bsi = null;
         }

         this.listeners.forEach(var1x -> var1x.onTick(var1));
      }
   }

   @Override
   public void onPostTick(TickEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onPostTick(var1));
      }
   }

   @Override
   public final void onPlayerUpdate(PlayerUpdateEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onPlayerUpdate(var1));
      }
   }

   @Override
   public final void onSendChatMessage(ChatEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onSendChatMessage(var1));
      }
   }

   @Override
   public void onPreTabComplete(TabCompleteEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onPreTabComplete(var1));
      }
   }

   @Override
   public void onChunkEvent(ChunkEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         EventState var2 = var1.getState();
         ChunkEvent.Type var3 = var1.getType();
         Level var4 = this.baritone.getPlayerContext().world();
         boolean var5 = var2 == EventState.PRE
            && var3 == ChunkEvent.Type.UNLOAD
            && var4.getChunkSource().getChunk(var1.getX(), var1.getZ(), null, false) != null;
         if (var1.isPostPopulate() || var5) {
            this.baritone.getWorldProvider().ifWorldLoaded(var2x -> {
               LevelChunk var3x = var4.getChunk(var1.getX(), var1.getZ());
               var2x.getCachedWorld().queueForPacking(var3x);
            });
         }

         this.listeners.forEach(var1x -> var1x.onChunkEvent(var1));
      }
   }

   @Override
   public void onBlockChange(BlockChangeEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         if (Baritone.settings().repackOnAnyBlockChange.value) {
            boolean var2 = var1.getBlocks().stream().map(Pair::second).map(BlockStateBase::getBlock).anyMatch(CachedChunk.BLOCKS_TO_KEEP_TRACK_OF::contains);
            if (var2) {
               this.baritone.getWorldProvider().ifWorldLoaded(var2x -> {
                  Level var3 = this.baritone.getPlayerContext().world();
                  ChunkPos var4 = var1.getChunkPos();
                  var2x.getCachedWorld().queueForPacking(var3.getChunk(var4.x(), var4.z()));
               });
            }
         }

         this.listeners.forEach(var1x -> var1x.onBlockChange(var1));
      }
   }

   @Override
   public final void onRenderPass(RenderEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onRenderPass(var1));
      }
   }

   @Override
   public final void onWorldEvent(WorldEvent var1) {
      WorldProvider var2 = this.baritone.getWorldProvider();
      if (var1.getState() == EventState.POST) {
         var2.closeWorld();
         if (var1.getWorld() != null) {
            var2.initWorld(var1.getWorld());
         }
      }

      this.listeners.forEach(var1x -> var1x.onWorldEvent(var1));
   }

   @Override
   public final void onSendPacket(PacketEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onSendPacket(var1));
      }
   }

   @Override
   public final void onReceivePacket(PacketEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onReceivePacket(var1));
      }
   }

   @Override
   public void onPlayerRotationMove(RotationMoveEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onPlayerRotationMove(var1));
      }
   }

   @Override
   public void onPlayerSprintState(SprintStateEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onPlayerSprintState(var1));
      }
   }

   @Override
   public void onBlockInteract(BlockInteractEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onBlockInteract(var1));
      }
   }

   @Override
   public void onPlayerDeath() {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(IGameEventListener::onPlayerDeath);
      }
   }

   @Override
   public void onPathEvent(PathEvent var1) {
      if (!ChatUtil.isUnHooked()) {
         this.listeners.forEach(var1x -> var1x.onPathEvent(var1));
      }
   }

   @Override
   public final void registerEventListener(IGameEventListener var1) {
      this.listeners.add(var1);
   }
}
