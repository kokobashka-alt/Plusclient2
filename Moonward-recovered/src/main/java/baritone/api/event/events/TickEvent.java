package baritone.api.event.events;

import baritone.api.event.events.type.EventState;
import java.util.function.BiFunction;

public class TickEvent {
   private static int overallTickCount;
   private final EventState state;
   private final TickEvent.Type type;
   private final int count;

   public TickEvent(EventState var1, TickEvent.Type var2, int var3) {
      this.state = var1;
      this.type = var2;
      this.count = var3;
   }

   public int getCount() {
      return this.count;
   }

   public TickEvent.Type getType() {
      return this.type;
   }

   public EventState getState() {
      return this.state;
   }

   public static synchronized BiFunction<EventState, TickEvent.Type, TickEvent> createNextProvider() {
      int var0 = overallTickCount++;
      return (var1, var2) -> new TickEvent(var1, var2, var0);
   }

   public enum Type {
      IN,
      OUT;
   }
}
