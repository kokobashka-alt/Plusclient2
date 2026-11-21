package baritone.pathing.calc;

import baritone.api.pathing.goals.Goal;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.SettingsUtil;

public class PathNode {
   public final int x;
   public final int y;
   public final int z;
   public final double estimatedCostToGoal;
   public double cost;
   public double combinedCost;
   public PathNode previous;
   public int heapPosition;

   public PathNode(int var1, int var2, int var3, Goal var4) {
      this.previous = null;
      this.cost = 1000000.0;
      this.estimatedCostToGoal = var4.heuristic(var1, var2, var3);
      if (Double.isNaN(this.estimatedCostToGoal)) {
         throw new IllegalStateException(
            String.format(
               "%s calculated implausible heuristic NaN at %s %s %s",
               var4,
               SettingsUtil.maybeCensor(var1),
               SettingsUtil.maybeCensor(var2),
               SettingsUtil.maybeCensor(var3)
            )
         );
      }

      this.heapPosition = -1;
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public boolean isOpen() {
      return this.heapPosition != -1;
   }

   @Override
   public int hashCode() {
      return (int)BetterBlockPos.longHash(this.x, this.y, this.z);
   }

   @Override
   public boolean equals(Object var1) {
      PathNode var2 = (PathNode)var1;
      return this.x == var2.x && this.y == var2.y && this.z == var2.z;
   }
}
