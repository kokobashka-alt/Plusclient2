package baritone.api.event.events;

import net.minecraft.core.BlockPos;

public class BlockInteractEvent {
   private final BlockPos pos;
   private final BlockInteractEvent.Type type;

   public BlockInteractEvent(BlockPos var1, BlockInteractEvent.Type var2) {
      this.pos = var1;
      this.type = var2;
   }

   public final BlockPos getPos() {
      return this.pos;
   }

   public final BlockInteractEvent.Type getType() {
      return this.type;
   }

   public enum Type {
      START_BREAK,
      USE;
   }
}
