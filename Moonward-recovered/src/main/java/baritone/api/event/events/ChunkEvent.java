package baritone.api.event.events;

import baritone.api.event.events.type.EventState;

public class ChunkEvent {
   private final EventState state;
   private final ChunkEvent.Type type;
   private final int x;
   private final int z;

   public ChunkEvent(EventState var1, ChunkEvent.Type var2, int var3, int var4) {
      this.state = var1;
      this.type = var2;
      this.x = var3;
      this.z = var4;
   }

   public EventState getState() {
      return this.state;
   }

   public ChunkEvent.Type getType() {
      return this.type;
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public boolean isPostPopulate() {
      return this.state == EventState.POST && this.type.isPopulate();
   }

   public enum Type {
      LOAD,
      UNLOAD,
      POPULATE_FULL,
      POPULATE_PARTIAL;

      public final boolean isPopulate() {
         return this == POPULATE_FULL || this == POPULATE_PARTIAL;
      }
   }
}
