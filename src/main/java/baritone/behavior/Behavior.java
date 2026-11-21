package baritone.behavior;

import baritone.Baritone;
import baritone.api.behavior.IBehavior;
import baritone.api.utils.IPlayerContext;

public class Behavior implements IBehavior {
   public final Baritone baritone;
   public final IPlayerContext ctx;

   protected Behavior(Baritone var1) {
      this.baritone = var1;
      this.ctx = var1.getPlayerContext();
   }
}
