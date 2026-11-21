package baritone.utils.pathing;

import baritone.api.pathing.calc.IPath;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.api.utils.IPlayerContext;
import baritone.pathing.movement.CalculationContext;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;

public class Favoring {
   private final Long2DoubleOpenHashMap favorings = new Long2DoubleOpenHashMap();

   public Favoring(IPlayerContext var1, IPath var2, CalculationContext var3) {
      this(var2, var3);

      for (Avoidance var5 : Avoidance.create(var1)) {
         var5.applySpherical(this.favorings);
      }

      Helper.HELPER.logDebug("Favoring size: " + this.favorings.size());
   }

   public Favoring(IPath var1, CalculationContext var2) {
      this.favorings.defaultReturnValue(1.0);
      double var3 = var2.backtrackCostFavoringCoefficient;
      if (var3 != 1.0 && var1 != null) {
         var1.positions().forEach(var3x -> this.favorings.put(BetterBlockPos.longHash(var3x), var3));
      }
   }

   public boolean isEmpty() {
      return this.favorings.isEmpty();
   }

   public double calculate(long var1) {
      return this.favorings.get(var1);
   }
}
