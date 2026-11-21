package baritone.api.pathing.goals;

import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.SettingsUtil;
import baritone.api.utils.interfaces.IGoalRenderPos;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import net.minecraft.core.BlockPos;

public class GoalNear implements Goal, IGoalRenderPos {
   private final int x;
   private final int y;
   private final int z;
   private final int rangeSq;

   public GoalNear(BlockPos var1, int var2) {
      this.x = var1.getX();
      this.y = var1.getY();
      this.z = var1.getZ();
      this.rangeSq = var2 * var2;
   }

   @Override
   public boolean isInGoal(int var1, int var2, int var3) {
      int var4 = var1 - this.x;
      int var5 = var2 - this.y;
      int var6 = var3 - this.z;
      return var4 * var4 + var5 * var5 + var6 * var6 <= this.rangeSq;
   }

   @Override
   public double heuristic(int var1, int var2, int var3) {
      int var4 = var1 - this.x;
      int var5 = var2 - this.y;
      int var6 = var3 - this.z;
      return GoalBlock.calculate(var4, var5, var6);
   }

   @Override
   public double heuristic() {
      int var1 = (int)Math.ceil(Math.sqrt(this.rangeSq));
      DoubleOpenHashSet var2 = new DoubleOpenHashSet();
      double var3 = Double.POSITIVE_INFINITY;

      for (int var5 = -var1; var5 <= var1; var5++) {
         for (int var6 = -var1; var6 <= var1; var6++) {
            for (int var7 = -var1; var7 <= var1; var7++) {
               double var8 = this.heuristic(this.x + var5, this.y + var6, this.z + var7);
               if (var8 < var3 && this.isInGoal(this.x + var5, this.y + var6, this.z + var7)) {
                  var2.add(var8);
               } else {
                  var3 = Math.min(var3, var8);
               }
            }
         }
      }

      double var10 = Double.NEGATIVE_INFINITY;
      DoubleIterator var11 = var2.iterator();

      while (var11.hasNext()) {
         double var12 = var11.nextDouble();
         if (var12 < var3) {
            var10 = Math.max(var10, var12);
         }
      }

      return var10;
   }

   @Override
   public BlockPos getGoalPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         GoalNear var2 = (GoalNear)var1;
         return this.x == var2.x && this.y == var2.y && this.z == var2.z && this.rangeSq == var2.rangeSq;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return (int)BetterBlockPos.longHash(this.x, this.y, this.z) + this.rangeSq;
   }

   @Override
   public String toString() {
      return String.format(
         "GoalNear{x=%s, y=%s, z=%s, rangeSq=%d}",
         SettingsUtil.maybeCensor(this.x),
         SettingsUtil.maybeCensor(this.y),
         SettingsUtil.maybeCensor(this.z),
         this.rangeSq
      );
   }
}
