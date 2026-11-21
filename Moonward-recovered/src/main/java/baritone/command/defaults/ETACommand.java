package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.behavior.IPathingBehavior;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.pathing.calc.IPathingControlManager;
import baritone.api.process.IBaritoneProcess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ETACommand extends Command {
   public ETACommand(IBaritone var1) {
      super(var1, "eta");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(0);
      IPathingControlManager var3 = this.baritone.getPathingControlManager();
      IBaritoneProcess var4 = var3.mostRecentInControl().orElse(null);
      if (var4 == null) {
         throw new CommandInvalidStateException("No process in control");
      }

      IPathingBehavior var5 = this.baritone.getPathingBehavior();
      double var6 = var5.ticksRemainingInSegment().orElse(Double.NaN);
      double var8 = var5.estimatedTicksToGoal().orElse(Double.NaN);
      this.logDirect(String.format("Next segment: %.1fs (%.0f ticks)\nGoal: %.1fs (%.0f ticks)", var6 / 20.0, var6, var8 / 20.0, var8));
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "View the current ETA";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The ETA command provides information about the estimated time until the next segment.",
         "and the goal",
         "",
         "Be aware that the ETA to your goal is really unprecise",
         "",
         "Usage:",
         "> eta - View ETA, if present"
      );
   }
}
