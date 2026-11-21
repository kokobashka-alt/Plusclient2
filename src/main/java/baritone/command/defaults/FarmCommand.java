package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.cache.IWaypoint;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.ForWaypoints;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FarmCommand extends Command {
   public FarmCommand(IBaritone var1) {
      super(var1, "farm");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      var2.requireMax(2);
      int var3 = 0;
      BetterBlockPos var4 = null;
      if (var2.has(1)) {
         var3 = var2.getAs(Integer.class);
      }

      if (var2.has(1)) {
         IWaypoint[] var5 = var2.getDatatypeFor(ForWaypoints.INSTANCE);
         IWaypoint var6 = null;
         switch (var5.length) {
            case 0:
               throw new CommandInvalidStateException("No waypoints found");
            case 1:
               var6 = var5[0];
               var4 = var6.getLocation();
               break;
            default:
               throw new CommandInvalidStateException("Multiple waypoints were found");
         }
      }

      this.baritone.getFarmProcess().farm(var3, var4);
      this.logDirect("Farming");
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Farm nearby crops";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The farm command starts farming nearby plants. It harvests mature crops and plants new ones.",
         "",
         "Usage:",
         "> farm - farms every crop it can find.",
         "> farm <range> - farm crops within range from the starting position.",
         "> farm <range> <waypoint> - farm crops within range from waypoint."
      );
   }
}
