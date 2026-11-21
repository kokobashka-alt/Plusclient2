package baritone.api.pathing.goals;

import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.SettingsUtil;
import baritone.api.utils.interfaces.IGoalRenderPos;
import net.minecraft.core.BlockPos;

public class GoalGetToBlock implements Goal, IGoalRenderPos {
   public final int x;
   public final int y;
   public final int z;

   public GoalGetToBlock(BlockPos var1) {
      this.x = var1.getX();
      this.y = var1.getY();
      this.z = var1.getZ();
   }

   @Override
   public BlockPos getGoalPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   @Override
   public boolean isInGoal(int var1, int var2, int var3) {
      int var4 = var1 - this.x;
      int var5 = var2 - this.y;
      int var6 = var3 - this.z;
      return Math.abs(var4) + Math.abs(var5 < 0 ? var5 + 1 : var5) + Math.abs(var6) <= 1;
   }

   @Override
   public double heuristic(int var1, int var2, int var3) {
      int var4 = var1 - this.x;
      int var5 = var2 - this.y;
      int var6 = var3 - this.z;
      return GoalBlock.calculate(var4, var5 < 0 ? var5 + 1 : var5, var6);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         GoalGetToBlock var2 = (GoalGetToBlock)var1;
         return this.x == var2.x && this.y == var2.y && this.z == var2.z;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return (int)BetterBlockPos.longHash(this.x, this.y, this.z) * -49639096;
   }

   @Override
   public String toString() {
      return String.format(
         "GoalGetToBlock{x=%s,y=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.y), SettingsUtil.maybeCensor(this.z)
      );
   }
}
