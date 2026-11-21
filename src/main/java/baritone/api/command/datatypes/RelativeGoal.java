package baritone.api.command.datatypes;

import baritone.api.command.argument.IArgConsumer;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.pathing.goals.GoalYLevel;
import baritone.api.utils.BetterBlockPos;
import java.util.stream.Stream;

public enum RelativeGoal implements IDatatypePost<Goal, BetterBlockPos> {
   INSTANCE;

   public Goal apply(IDatatypeContext var1, BetterBlockPos var2) {
      if (var2 == null) {
         var2 = BetterBlockPos.ORIGIN;
      }

      IArgConsumer var3 = var1.getConsumer();
      GoalBlock var4 = var3.peekDatatypePostOrNull(RelativeGoalBlock.INSTANCE, var2);
      if (var4 != null) {
         return var4;
      }

      GoalXZ var5 = var3.peekDatatypePostOrNull(RelativeGoalXZ.INSTANCE, var2);
      if (var5 != null) {
         return var5;
      }

      GoalYLevel var6 = var3.peekDatatypePostOrNull(RelativeGoalYLevel.INSTANCE, var2);
      return var6 != null ? var6 : new GoalBlock(var2);
   }

   @Override
   public Stream<String> tabComplete(IDatatypeContext var1) {
      return var1.getConsumer().tabCompleteDatatype(RelativeCoordinate.INSTANCE);
   }
}
