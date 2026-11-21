package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.RelativeCoordinate;
import baritone.api.command.datatypes.RelativeGoal;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.pathing.goals.Goal;
import baritone.api.process.ICustomGoalProcess;
import baritone.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GoalCommand extends Command {
   public GoalCommand(IBaritone var1) {
      super(var1, "goal");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      ICustomGoalProcess var3 = this.baritone.getCustomGoalProcess();
      if (var2.hasAny() && Arrays.asList("reset", "clear", "none").contains(var2.peekString())) {
         var2.requireMax(1);
         if (var3.getGoal() != null) {
            var3.setGoal(null);
            this.logDirect("Cleared goal");
         } else {
            this.logDirect("There was no goal to clear");
         }
      } else {
         var2.requireMax(3);
         BetterBlockPos var4 = this.ctx.playerFeet();
         Goal var5 = var2.getDatatypePost(RelativeGoal.INSTANCE, var4);
         var3.setGoal(var5);
         this.logDirect(String.format("Goal: %s", var5.toString()));
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      TabCompleteHelper var3 = new TabCompleteHelper();
      if (var2.hasExactlyOne()) {
         var3.append("reset", "clear", "none", "~");
      } else if (var2.hasAtMost(3)) {
         while (var2.has(2) && var2.peekDatatypeOrNull(RelativeCoordinate.INSTANCE) != null) {
            var2.get();
            if (!var2.has(2)) {
               var3.append("~");
            }
         }
      }

      return var3.filterPrefix(var2.getString()).stream();
   }

   @Override
   public String getShortDesc() {
      return "Set or clear the goal";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The goal command allows you to set or clear Baritone's goal.",
         "",
         "Wherever a coordinate is expected, you can use ~ just like in regular Minecraft commands. Or, you can just use regular numbers.",
         "",
         "Usage:",
         "> goal - Set the goal to your current position",
         "> goal <reset/clear/none> - Erase the goal",
         "> goal <y> - Set the goal to a Y level",
         "> goal <x> <z> - Set the goal to an X,Z position",
         "> goal <x> <y> <z> - Set the goal to an X,Y,Z position"
      );
   }
}
