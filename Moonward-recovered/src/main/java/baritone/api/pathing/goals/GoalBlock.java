package baritone.api.pathing.goals;

import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.SettingsUtil;
import baritone.api.utils.interfaces.IGoalRenderPos;
import net.minecraft.core.BlockPos;

public class GoalBlock implements Goal, IGoalRenderPos {
   public final int x;
   public final int y;
   public final int z;

   public GoalBlock(BlockPos var1) {
      this(var1.getX(), var1.getY(), var1.getZ());
   }

   public GoalBlock(int var1, int var2, int var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   @Override
   public boolean isInGoal(int var1, int var2, int var3) {
      return var1 == this.x && var2 == this.y && var3 == this.z;
   }

   @Override
   public double heuristic(int var1, int var2, int var3) {
      int var4 = var1 - this.x;
      int var5 = var2 - this.y;
      int var6 = var3 - this.z;
      return calculate(var4, var5, var6);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         GoalBlock var2 = (GoalBlock)var1;
         return this.x == var2.x && this.y == var2.y && this.z == var2.z;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return (int)BetterBlockPos.longHash(this.x, this.y, this.z) * 905165533;
   }

   @Override
   public String toString() {
      return String.format("GoalBlock{x=%s,y=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.y), SettingsUtil.maybeCensor(this.z));
   }

   @Override
   public BlockPos getGoalPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public static double calculate(double var0, int var2, double var3) {
      double var5 = 0.0;
      var5 += GoalYLevel.calculate(0, var2);
      return var5 + GoalXZ.calculate(var0, var3);
   }
}
