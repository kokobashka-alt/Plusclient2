package baritone.api.pathing.goals;

import java.util.Arrays;

public class GoalComposite implements Goal {
   private final Goal[] goals;

   public GoalComposite(Goal... var1) {
      this.goals = var1;
   }

   @Override
   public boolean isInGoal(int var1, int var2, int var3) {
      for (Goal var7 : this.goals) {
         if (var7.isInGoal(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public double heuristic(int var1, int var2, int var3) {
      double var4 = Double.MAX_VALUE;

      for (Goal var9 : this.goals) {
         var4 = Math.min(var4, var9.heuristic(var1, var2, var3));
      }

      return var4;
   }

   @Override
   public double heuristic() {
      double var1 = Double.MAX_VALUE;

      for (Goal var6 : this.goals) {
         var1 = Math.min(var1, var6.heuristic());
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         GoalComposite var2 = (GoalComposite)var1;
         return Arrays.equals(this.goals, var2.goals);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.goals);
   }

   @Override
   public String toString() {
      return "GoalComposite" + Arrays.toString(this.goals);
   }

   public Goal[] goals() {
      return this.goals;
   }
}
