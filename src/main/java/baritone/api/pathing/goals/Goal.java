package baritone.api.pathing.goals;

import net.minecraft.core.BlockPos;

public interface Goal {
   boolean isInGoal(int var1, int var2, int var3);

   double heuristic(int var1, int var2, int var3);

   default boolean isInGoal(BlockPos var1) {
      return this.isInGoal(var1.getX(), var1.getY(), var1.getZ());
   }

   default double heuristic(BlockPos var1) {
      return this.heuristic(var1.getX(), var1.getY(), var1.getZ());
   }

   default double heuristic() {
      return 0.0;
   }
}
