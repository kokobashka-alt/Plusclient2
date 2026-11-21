package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalInverted;
import baritone.api.process.ICustomGoalProcess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class InvertCommand extends Command {
   public InvertCommand(IBaritone var1) {
      super(var1, "invert");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(0);
      ICustomGoalProcess var3 = this.baritone.getCustomGoalProcess();
      Goal var4;
      if ((var4 = var3.getGoal()) == null) {
         throw new CommandInvalidStateException("No goal");
      }

      if (var4 instanceof GoalInverted) {
         var4 = ((GoalInverted)var4).origin;
      } else {
         var4 = new GoalInverted(var4);
      }

      var3.setGoalAndPath(var4);
      this.logDirect(String.format("Goal: %s", var4.toString()));
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Run away from the current goal";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The invert command tells Baritone to head away from the current goal rather than towards it.", "", "Usage:", "> invert - Invert the current goal."
      );
   }
}
