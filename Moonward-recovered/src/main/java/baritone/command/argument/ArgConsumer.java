package baritone.command.argument;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.argument.ICommandArgument;
import baritone.api.command.datatypes.IDatatype;
import baritone.api.command.datatypes.IDatatypeContext;
import baritone.api.command.datatypes.IDatatypeFor;
import baritone.api.command.datatypes.IDatatypePost;
import baritone.api.command.exception.CommandException;
import baritone.api.command.exception.CommandInvalidTypeException;
import baritone.api.command.exception.CommandNotEnoughArgumentsException;
import baritone.api.command.exception.CommandTooManyArgumentsException;
import baritone.api.command.manager.ICommandManager;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ArgConsumer implements IArgConsumer {
   private final ICommandManager manager;
   private final IDatatypeContext context;
   private final LinkedList<ICommandArgument> args;
   private final Deque<ICommandArgument> consumed;

   private ArgConsumer(ICommandManager var1, Deque<ICommandArgument> var2, Deque<ICommandArgument> var3) {
      this.manager = var1;
      this.context = new ArgConsumer.Context();
      this.args = new LinkedList<>(var2);
      this.consumed = new LinkedList<>(var3);
   }

   public ArgConsumer(ICommandManager var1, List<ICommandArgument> var2) {
      this(var1, new LinkedList<>(var2), new LinkedList<>());
   }

   @Override
   public LinkedList<ICommandArgument> getArgs() {
      return this.args;
   }

   @Override
   public Deque<ICommandArgument> getConsumed() {
      return this.consumed;
   }

   @Override
   public boolean has(int var1) {
      return this.args.size() >= var1;
   }

   @Override
   public boolean hasAny() {
      return this.has(1);
   }

   @Override
   public boolean hasAtMost(int var1) {
      return this.args.size() <= var1;
   }

   @Override
   public boolean hasAtMostOne() {
      return this.hasAtMost(1);
   }

   @Override
   public boolean hasExactly(int var1) {
      return this.args.size() == var1;
   }

   @Override
   public boolean hasExactlyOne() {
      return this.hasExactly(1);
   }

   @Override
   public ICommandArgument peek(int var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      this.requireMin(var1 + 1);
      return this.args.get(var1);
   }

   @Override
   public ICommandArgument peek() throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peek(0);
   }

   @Override
   public boolean is(Class<?> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peek(var2).is(var1);
   }

   @Override
   public boolean is(Class<?> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.is(var1, 0);
   }

   @Override
   public String peekString(int var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peek(var1).getValue();
   }

   @Override
   public String peekString() throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peekString(0);
   }

   @Override
   public <E extends Enum<?>> E peekEnum(Class<E> var1, int var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peek(var2).getEnum(var1);
   }

   @Override
   public <E extends Enum<?>> E peekEnum(Class<E> var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peekEnum(var1, 0);
   }

   @Override
   public <E extends Enum<?>> E peekEnumOrNull(Class<E> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      try {
         return this.peekEnum(var1, var2);
      } catch (CommandInvalidTypeException var4) {
         return null;
      }
   }

   @Override
   public <E extends Enum<?>> E peekEnumOrNull(Class<E> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peekEnumOrNull(var1, 0);
   }

   @Override
   public <T> T peekAs(Class<T> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException {
      return this.peek(var2).getAs(var1);
   }

   @Override
   public <T> T peekAs(Class<T> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException {
      return this.peekAs(var1, 0);
   }

   @Override
   public <T> T peekAsOrDefault(Class<T> var1, T var2, int var3) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      try {
         return this.peekAs(var1, var3);
      } catch (CommandInvalidTypeException var5) {
         return (T)var2;
      }
   }

   @Override
   public <T> T peekAsOrDefault(Class<T> var1, T var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peekAsOrDefault(var1, (T)var2, 0);
   }

   @Override
   public <T> T peekAsOrNull(Class<T> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peekAsOrDefault(var1, null, var2);
   }

   @Override
   public <T> T peekAsOrNull(Class<T> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peekAsOrNull(var1, 0);
   }

   @Override
   public <T> T peekDatatype(IDatatypeFor<T> var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.copy().getDatatypeFor(var1);
   }

   @Override
   public <T, O> T peekDatatype(IDatatypePost<T, O> var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.peekDatatype(var1, null);
   }

   @Override
   public <T, O> T peekDatatype(IDatatypePost<T, O> var1, O var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.copy().getDatatypePost(var1, var2);
   }

   @Override
   public <T> T peekDatatypeOrNull(IDatatypeFor<T> var1) {
      return this.copy().getDatatypeForOrNull(var1);
   }

   @Override
   public <T, O> T peekDatatypeOrNull(IDatatypePost<T, O> var1) {
      return this.copy().getDatatypePostOrNull(var1, null);
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T peekDatatypePost(D var1, O var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.copy().getDatatypePost((D)var1, var2);
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T peekDatatypePostOrDefault(D var1, O var2, T var3) {
      return this.copy().getDatatypePostOrDefault((D)var1, var2, (T)var3);
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T peekDatatypePostOrNull(D var1, O var2) {
      return this.peekDatatypePostOrDefault((D)var1, var2, null);
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T peekDatatypeFor(Class<D> var1) {
      return this.copy().peekDatatypeFor(var1);
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T peekDatatypeForOrDefault(Class<D> var1, T var2) {
      return this.copy().peekDatatypeForOrDefault(var1, (T)var2);
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T peekDatatypeForOrNull(Class<D> var1) {
      return this.peekDatatypeForOrDefault(var1, null);
   }

   @Override
   public ICommandArgument get() throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      this.requireMin(1);
      ICommandArgument var1 = this.args.removeFirst();
      this.consumed.add(var1);
      return var1;
   }

   @Override
   public String getString() throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.get().getValue();
   }

   @Override
   public <E extends Enum<?>> E getEnum(Class<E> var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.get().getEnum(var1);
   }

   @Override
   public <E extends Enum<?>> E getEnumOrDefault(Class<E> var1, E var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      try {
         this.peekEnum(var1);
         return this.getEnum(var1);
      } catch (CommandInvalidTypeException var4) {
         return (E)var2;
      }
   }

   @Override
   public <E extends Enum<?>> E getEnumOrNull(Class<E> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.getEnumOrDefault(var1, null);
   }

   @Override
   public <T> T getAs(Class<T> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException {
      return this.get().getAs(var1);
   }

   @Override
   public <T> T getAsOrDefault(Class<T> var1, T var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      try {
         Object var3 = this.peek().getAs(var1);
         this.get();
         return (T)var3;
      } catch (CommandInvalidTypeException var4) {
         return (T)var2;
      }
   }

   @Override
   public <T> T getAsOrNull(Class<T> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return this.getAsOrDefault(var1, null);
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T getDatatypePost(D var1, O var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      try {
         return (T)var1.apply(this.context, var2);
      } catch (Exception var4) {
         if (Baritone.settings().verboseCommandExceptions.value) {
            var4.printStackTrace();
         }

         throw new CommandInvalidTypeException(this.hasAny() ? this.peek() : this.consumed(), var1.getClass().getSimpleName(), var4);
      }
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T getDatatypePostOrDefault(D var1, O var2, T var3) {
      ArrayList var4 = new ArrayList<>(this.args);
      ArrayList var5 = new ArrayList<>(this.consumed);

      try {
         return this.getDatatypePost((D)var1, var2);
      } catch (Exception var7) {
         this.args.clear();
         this.args.addAll(var4);
         this.consumed.clear();
         this.consumed.addAll(var5);
         return (T)var3;
      }
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T getDatatypePostOrNull(D var1, O var2) {
      return this.getDatatypePostOrDefault((D)var1, var2, null);
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T getDatatypeFor(D var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      try {
         return (T)var1.get(this.context);
      } catch (Exception var3) {
         if (Baritone.settings().verboseCommandExceptions.value) {
            var3.printStackTrace();
         }

         throw new CommandInvalidTypeException(this.hasAny() ? this.peek() : this.consumed(), var1.getClass().getSimpleName(), var3);
      }
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T getDatatypeForOrDefault(D var1, T var2) {
      ArrayList var3 = new ArrayList<>(this.args);
      ArrayList var4 = new ArrayList<>(this.consumed);

      try {
         return this.getDatatypeFor((D)var1);
      } catch (Exception var6) {
         this.args.clear();
         this.args.addAll(var3);
         this.consumed.clear();
         this.consumed.addAll(var4);
         return (T)var2;
      }
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T getDatatypeForOrNull(D var1) {
      return this.getDatatypeForOrDefault((D)var1, null);
   }

   @Override
   public <T extends IDatatype> Stream<String> tabCompleteDatatype(T var1) {
      try {
         return var1.tabComplete(this.context);
      } catch (CommandException var3) {
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return Stream.empty();
   }

   @Override
   public String rawRest() {
      return this.args.size() > 0 ? this.args.getFirst().getRawRest() : "";
   }

   @Override
   public void requireMin(int var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      if (this.args.size() < var1) {
         throw new CommandNotEnoughArgumentsException(var1 + this.consumed.size());
      }
   }

   @Override
   public void requireMax(int var1) throws baritone.api.command.exception.CommandTooManyArgumentsException {
      if (this.args.size() > var1) {
         throw new CommandTooManyArgumentsException(var1 + this.consumed.size());
      }
   }

   @Override
   public void requireExactly(int var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandTooManyArgumentsException {
      this.requireMin(var1);
      this.requireMax(var1);
   }

   @Override
   public boolean hasConsumed() {
      return !this.consumed.isEmpty();
   }

   @Override
   public ICommandArgument consumed() {
      return this.consumed.size() > 0 ? this.consumed.getLast() : CommandArguments.unknown();
   }

   @Override
   public String consumedString() {
      return this.consumed().getValue();
   }

   public ArgConsumer copy() {
      return new ArgConsumer(this.manager, this.args, this.consumed);
   }

   private final class Context implements IDatatypeContext {
      @Override
      public final IBaritone getBaritone() {
         return ArgConsumer.this.manager.getBaritone();
      }

      public final ArgConsumer getConsumer() {
         return ArgConsumer.this;
      }
   }
}
