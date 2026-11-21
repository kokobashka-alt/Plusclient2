package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ExecutionControlCommands {
   Command pauseCommand;
   Command resumeCommand;
   Command pausedCommand;
   Command cancelCommand;

   public ExecutionControlCommands(final IBaritone var1) {
      final boolean[] var2 = new boolean[]{false};
      var1.getPathingControlManager().registerProcess(new IBaritoneProcess() {
         @Override
         public boolean isActive() {
            return var2[0];
         }

         @Override
         public PathingCommand onTick(boolean var1x, boolean var2x) {
            var1.getInputOverrideHandler().clearAllKeys();
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
         }

         @Override
         public boolean isTemporary() {
            return true;
         }

         @Override
         public void onLostControl() {
         }

         @Override
         public double priority() {
            return 0.0;
         }

         @Override
         public String displayName0() {
            return "Pause/Resume Commands";
         }
      });
      this.pauseCommand = new Command(var1, new String[]{"pause", "p", "paws"}) {
         @Override
         public void execute(String var1, IArgConsumer var2x) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandTooManyArgumentsException {
            var2x.requireMax(0);
            if (var2[0]) {
               throw new CommandInvalidStateException("Already paused");
            }

            var2[0] = true;
            this.logDirect("Paused");
         }

         @Override
         public Stream<String> tabComplete(String var1, IArgConsumer var2x) {
            return Stream.empty();
         }

         @Override
         public String getShortDesc() {
            return "Pauses Baritone until you use resume";
         }

         @Override
         public List<String> getLongDesc() {
            return Arrays.asList(
               "The pause command tells Baritone to temporarily stop whatever it's doing.",
               "",
               "This can be used to pause pathing, building, following, whatever. A single use of the resume command will start it right back up again!",
               "",
               "Usage:",
               "> pause"
            );
         }
      };
      this.resumeCommand = new Command(var1, new String[]{"resume", "r", "unpause", "unpaws"}) {
         @Override
         public void execute(String var1, IArgConsumer var2x) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandTooManyArgumentsException {
            var2x.requireMax(0);
            this.baritone.getBuilderProcess().resume();
            if (!var2[0]) {
               throw new CommandInvalidStateException("Not paused");
            }

            var2[0] = false;
            this.logDirect("Resumed");
         }

         @Override
         public Stream<String> tabComplete(String var1, IArgConsumer var2x) {
            return Stream.empty();
         }

         @Override
         public String getShortDesc() {
            return "Resumes Baritone after a pause";
         }

         @Override
         public List<String> getLongDesc() {
            return Arrays.asList("The resume command tells Baritone to resume whatever it was doing when you last used pause.", "", "Usage:", "> resume");
         }
      };
      this.pausedCommand = new Command(var1, new String[]{"paused"}) {
         @Override
         public void execute(String var1, IArgConsumer var2x) throws baritone.api.command.exception.CommandTooManyArgumentsException {
            var2x.requireMax(0);
            this.logDirect(String.format("Baritone is %spaused", var2[0] ? "" : "not "));
         }

         @Override
         public Stream<String> tabComplete(String var1, IArgConsumer var2x) {
            return Stream.empty();
         }

         @Override
         public String getShortDesc() {
            return "Tells you if Baritone is paused";
         }

         @Override
         public List<String> getLongDesc() {
            return Arrays.asList("The paused command tells you if Baritone is currently paused by use of the pause command.", "", "Usage:", "> paused");
         }
      };
      this.cancelCommand = new Command(var1, new String[]{"cancel", "c", "stop"}) {
         @Override
         public void execute(String var1, IArgConsumer var2x) throws baritone.api.command.exception.CommandTooManyArgumentsException {
            var2x.requireMax(0);
            if (var2[0]) {
               var2[0] = false;
            }

            this.baritone.getPathingBehavior().cancelEverything();
            this.logDirect("ok canceled");
         }

         @Override
         public Stream<String> tabComplete(String var1, IArgConsumer var2x) {
            return Stream.empty();
         }

         @Override
         public String getShortDesc() {
            return "Cancel what Baritone is currently doing";
         }

         @Override
         public List<String> getLongDesc() {
            return Arrays.asList("The cancel command tells Baritone to stop whatever it's currently doing.", "", "Usage:", "> cancel");
         }
      };
   }
}
