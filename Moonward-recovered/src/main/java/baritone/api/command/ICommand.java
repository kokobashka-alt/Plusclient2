package baritone.api.command;

import baritone.api.command.argument.IArgConsumer;
import baritone.api.utils.Helper;
import java.util.List;
import java.util.stream.Stream;

public interface ICommand extends Helper {
   void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotFoundException, baritone.command.defaults.FollowCommand.NoEntitiesException, baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandTooManyArgumentsException;

   Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   String getShortDesc();

   List<String> getLongDesc();

   List<String> getNames();

   default boolean hiddenFromHelp() {
      return false;
   }
}
