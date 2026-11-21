package baritone.utils;

import baritone.Baritone;
import baritone.api.process.IBaritoneProcess;
import baritone.api.utils.Helper;
import baritone.api.utils.IPlayerContext;

public abstract class BaritoneProcessHelper implements IBaritoneProcess, Helper {
   protected final Baritone baritone;
   protected final IPlayerContext ctx;

   public BaritoneProcessHelper(Baritone var1) {
      this.baritone = var1;
      this.ctx = var1.getPlayerContext();
   }

   @Override
   public boolean isTemporary() {
      return false;
   }
}
