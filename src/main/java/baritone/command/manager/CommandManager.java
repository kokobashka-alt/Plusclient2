package baritone.command.manager;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.command.ICommand;
import baritone.api.command.argument.ICommandArgument;
import baritone.api.command.exception.CommandException;
import baritone.api.command.exception.CommandUnhandledException;
import baritone.api.command.exception.ICommandException;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.command.manager.ICommandManager;
import baritone.api.command.registry.Registry;
import baritone.api.utils.Tuple;
import baritone.command.argument.ArgConsumer;
import baritone.command.argument.CommandArguments;
import baritone.command.defaults.DefaultCommands;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import wtf.onetap.util.ChatUtil;

public class CommandManager implements ICommandManager {
   private final Registry<ICommand> registry = new Registry<>();
   private final Baritone baritone;

   public CommandManager(Baritone var1) {
      this.baritone = var1;
      DefaultCommands.createAll(var1).forEach(this.registry::register);
   }

   @Override
   public IBaritone getBaritone() {
      return this.baritone;
   }

   @Override
   public Registry<ICommand> getRegistry() {
      return this.registry;
   }

   @Override
   public ICommand getCommand(String var1) {
      for (ICommand var3 : this.registry.entries) {
         if (var3.getNames().contains(var1.toLowerCase(Locale.US))) {
            return var3;
         }
      }

      return null;
   }

   @Override
   public boolean execute(String var1) {
      return ChatUtil.isUnHooked() ? false : this.execute(expand(var1));
   }

   @Override
   public boolean execute(Tuple<String, List<ICommandArgument>> var1) {
      if (ChatUtil.isUnHooked()) {
         return false;
      }

      CommandManager.ExecutionWrapper var2 = this.from(var1);
      if (var2 != null) {
         var2.execute();
      }

      return var2 != null;
   }

   @Override
   public Stream<String> tabComplete(Tuple<String, List<ICommandArgument>> var1) {
      if (ChatUtil.isUnHooked()) {
         return Stream.empty();
      }

      CommandManager.ExecutionWrapper var2 = this.from(var1);
      return var2 == null ? Stream.empty() : var2.tabComplete();
   }

   @Override
   public Stream<String> tabComplete(String var1) {
      if (ChatUtil.isUnHooked()) {
         return Stream.empty();
      }

      var var2 = expand(var1, true);
      String var3 = (String)var2.getA();
      List var4 = (List)var2.getB();
      return var4.isEmpty() ? new TabCompleteHelper().addCommands(this.baritone.getCommandManager()).filterPrefix(var3).stream() : this.tabComplete(var2);
   }

   private CommandManager.ExecutionWrapper from(Tuple<String, List<ICommandArgument>> var1) {
      String var2 = (String)var1.getA();
      ArgConsumer var3 = new ArgConsumer(this, (List<ICommandArgument>)var1.getB());
      ICommand var4 = this.getCommand(var2);
      return var4 == null ? null : new CommandManager.ExecutionWrapper(var4, var2, var3);
   }

   private static Tuple<String, List<ICommandArgument>> expand(String var0, boolean var1) {
      String var2 = var0.split("\\s", 2)[0];
      var var3 = CommandArguments.from(var0.substring(var2.length()), var1);
      return new Tuple<>(var2, var3);
   }

   public static Tuple<String, List<ICommandArgument>> expand(String var0) {
      return expand(var0, false);
   }

   private static final class ExecutionWrapper {
      private ICommand command;
      private String label;
      private ArgConsumer args;

      private ExecutionWrapper(ICommand var1, String var2, ArgConsumer var3) {
         this.command = var1;
         this.label = var2;
         this.args = var3;
      }

      private void execute() {
         try {
            this.command.execute(this.label, this.args);
         } catch (Throwable var3) {
            ICommandException var2 = var3 instanceof ICommandException ? (ICommandException)var3 : new CommandUnhandledException(var3);
            var2.handle(this.command, this.args.getArgs());
         }
      }

      private Stream<String> tabComplete() {
         try {
            return this.command.tabComplete(this.label, this.args);
         } catch (CommandException var2) {
         } catch (Throwable var3) {
            var3.printStackTrace();
         }

         return Stream.empty();
      }
   }
}
