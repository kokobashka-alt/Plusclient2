package baritone.api.event.events;

import baritone.api.event.events.type.Cancellable;

public class TabCompleteEvent extends Cancellable {
   public final String prefix;
   public String[] completions;

   public TabCompleteEvent(String var1) {
      this.prefix = var1;
      this.completions = null;
   }
}
