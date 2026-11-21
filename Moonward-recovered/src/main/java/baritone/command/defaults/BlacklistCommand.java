package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.process.IGetToBlockProcess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BlacklistCommand extends Command {
   public BlacklistCommand(IBaritone var1) {
      super(var1, "blacklist");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(0);
      IGetToBlockProcess var3 = this.baritone.getGetToBlockProcess();
      if (!var3.isActive()) {
         throw new CommandInvalidStateException("GetToBlockProcess is not currently active");
      }

      if (var3.blacklistClosest()) {
         this.logDirect("Blacklisted closest instances");
      } else {
         throw new CommandInvalidStateException("No known locations, unable to blacklist");
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Blacklist closest block";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "While going to a block this command blacklists the closest block so that Baritone won't attempt to get to it.", "", "Usage:", "> blacklist"
      );
   }
}
