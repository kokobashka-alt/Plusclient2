package baritone.command.argparser;

import baritone.api.command.argparser.IArgParser;
import baritone.api.command.argparser.IArgParserManager;
import baritone.api.command.argument.ICommandArgument;
import baritone.api.command.exception.CommandInvalidTypeException;
import baritone.api.command.exception.CommandNoParserForTypeException;
import baritone.api.command.registry.Registry;

public enum ArgParserManager implements IArgParserManager {
   INSTANCE;

   public final Registry<IArgParser> registry = new Registry<>();

   ArgParserManager() {
      DefaultArgParsers.ALL.forEach(this.registry::register);
   }

   @Override
   public <T> IArgParser.Stateless<T> getParserStateless(Class<T> var1) {
      return this.registry
         .descendingStream()
         .filter(IArgParser.Stateless.class::isInstance)
         .map(IArgParser.Stateless.class::cast)
         .filter(var1x -> var1x.getTarget().isAssignableFrom(var1))
         .findFirst()
         .orElse(null);
   }

   @Override
   public <T, S> IArgParser.Stated<T, S> getParserStated(Class<T> var1, Class<S> var2) {
      return this.registry
         .descendingStream()
         .filter(IArgParser.Stated.class::isInstance)
         .map(IArgParser.Stated.class::cast)
         .filter(var1x -> var1x.getTarget().isAssignableFrom(var1))
         .filter(var1x -> var1x.getStateType().isAssignableFrom(var2))
         .map(IArgParser.Stated.class::cast)
         .findFirst()
         .orElse(null);
   }

   @Override
   public <T> T parseStateless(Class<T> var1, ICommandArgument var2) throws baritone.api.command.exception.CommandInvalidTypeException {
      IArgParser.Stateless var3 = this.getParserStateless(var1);
      if (var3 == null) {
         throw new CommandNoParserForTypeException(var1);
      }

      try {
         return (T)var3.parseArg(var2);
      } catch (Exception var5) {
         throw new CommandInvalidTypeException(var2, var1.getSimpleName());
      }
   }

   @Override
   public <T, S> T parseStated(Class<T> var1, Class<S> var2, ICommandArgument var3, S var4) throws baritone.api.command.exception.CommandInvalidTypeException {
      IArgParser.Stated var5 = this.getParserStated(var1, var2);
      if (var5 == null) {
         throw new CommandNoParserForTypeException(var1);
      }

      try {
         return (T)var5.parseArg(var3, var4);
      } catch (Exception var7) {
         throw new CommandInvalidTypeException(var3, var1.getSimpleName());
      }
   }

   @Override
   public Registry<IArgParser> getRegistry() {
      return this.registry;
   }
}
