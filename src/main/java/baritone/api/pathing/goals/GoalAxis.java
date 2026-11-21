package baritone.api.pathing.goals;

import baritone.api.BaritoneAPI;

public class GoalAxis implements Goal {
   private static final double SQRT_2_OVER_2 = Math.sqrt(Double.longBitsToDouble(4611686018427387904L)) / Double.longBitsToDouble(4611686018427387904L);

   @Override
   public boolean isInGoal(int var1, int var2, int var3) {
      return var2 == BaritoneAPI.getSettings().axisHeight.value && (var1 == 0 || var3 == 0 || Math.abs(var1) == Math.abs(var3));
   }

   @Override
   public double heuristic(int var1, int var2, int var3) {
      int var4 = Math.abs(var1);
      int var5 = Math.abs(var3);
      int var6 = Math.min(var4, var5);
      int var7 = Math.max(var4, var5);
      int var8 = var7 - var6;
      double var9 = Math.min(var4, Math.min(var5, var8 * SQRT_2_OVER_2));
      return var9 * BaritoneAPI.getSettings().costHeuristic.value + GoalYLevel.calculate(BaritoneAPI.getSettings().axisHeight.value, var2);
   }

   @Override
   public boolean equals(Object var1) {
      return var1.getClass() == GoalAxis.class;
   }

   @Override
   public int hashCode() {
      return 201385781;
   }

   @Override
   public String toString() {
      return "GoalAxis";
   }
}
