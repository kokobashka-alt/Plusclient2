package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.pathing.goals.GoalXZ;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ThisWayCommand extends Command {
   public ThisWayCommand(IBaritone var1) {
      super(var1, "thisway", "forward");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException {
      var2.requireExactly(1);
      GoalXZ var3 = GoalXZ.fromDirection(this.ctx.playerFeetAsVec(), this.ctx.player().getYHeadRot(), var2.getAs(Double.class));
      this.baritone.getCustomGoalProcess().setGoal(var3);
      this.logDirect(String.format("Goal: %s", var3));
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Travel in your current direction";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "Creates a GoalXZ some amount of blocks in the direction you're currently looking",
         "",
         "Usage:",
         "> thisway <distance> - makes a GoalXZ distance blocks in front of you"
      );
   }
}
