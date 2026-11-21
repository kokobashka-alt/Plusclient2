package baritone.api.event.events;

import baritone.api.event.events.type.EventState;
import net.minecraft.client.multiplayer.ClientLevel;

public class WorldEvent {
   private final ClientLevel world;
   private final EventState state;

   public WorldEvent(ClientLevel var1, EventState var2) {
      this.world = var1;
      this.state = var2;
   }

   public final ClientLevel getWorld() {
      return this.world;
   }

   public final EventState getState() {
      return this.state;
   }
}
