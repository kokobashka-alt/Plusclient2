package baritone.api.command.argument;

import baritone.api.command.datatypes.IDatatype;
import baritone.api.command.datatypes.IDatatypeFor;
import baritone.api.command.datatypes.IDatatypePost;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Stream;

public interface IArgConsumer {
   LinkedList<ICommandArgument> getArgs();

   Deque<ICommandArgument> getConsumed();

   boolean has(int var1);

   boolean hasAny();

   boolean hasAtMost(int var1);

   boolean hasAtMostOne();

   boolean hasExactly(int var1);

   boolean hasExactlyOne();

   ICommandArgument peek(int var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   ICommandArgument peek() throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   boolean is(Class<?> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   boolean is(Class<?> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   String peekString(int var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   String peekString() throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <E extends Enum<?>> E peekEnum(Class<E> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException;

   <E extends Enum<?>> E peekEnum(Class<E> var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <E extends Enum<?>> E peekEnumOrNull(Class<E> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <E extends Enum<?>> E peekEnumOrNull(Class<E> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T> T peekAs(Class<T> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException;

   <T> T peekAs(Class<T> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException;

   <T> T peekAsOrDefault(Class<T> var1, T var2, int var3) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T> T peekAsOrDefault(Class<T> var1, T var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T> T peekAsOrNull(Class<T> var1, int var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T> T peekAsOrNull(Class<T> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T> T peekDatatype(IDatatypeFor<T> var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T, O> T peekDatatype(IDatatypePost<T, O> var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T, O> T peekDatatype(IDatatypePost<T, O> var1, O var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T> T peekDatatypeOrNull(IDatatypeFor<T> var1);

   <T, O> T peekDatatypeOrNull(IDatatypePost<T, O> var1);

   <T, O, D extends IDatatypePost<T, O>> T peekDatatypePost(D var1, O var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T, O, D extends IDatatypePost<T, O>> T peekDatatypePostOrDefault(D var1, O var2, T var3);

   <T, O, D extends IDatatypePost<T, O>> T peekDatatypePostOrNull(D var1, O var2);

   <T, D extends IDatatypeFor<T>> T peekDatatypeFor(Class<D> var1);

   <T, D extends IDatatypeFor<T>> T peekDatatypeForOrDefault(Class<D> var1, T var2);

   <T, D extends IDatatypeFor<T>> T peekDatatypeForOrNull(Class<D> var1);

   ICommandArgument get() throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   String getString() throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <E extends Enum<?>> E getEnum(Class<E> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException;

   <E extends Enum<?>> E getEnumOrDefault(Class<E> var1, E var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <E extends Enum<?>> E getEnumOrNull(Class<E> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T> T getAs(Class<T> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandInvalidTypeException;

   <T> T getAsOrDefault(Class<T> var1, T var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T> T getAsOrNull(Class<T> var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T, O, D extends IDatatypePost<T, O>> T getDatatypePost(D var1, O var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T, O, D extends IDatatypePost<T, O>> T getDatatypePostOrDefault(D var1, O var2, T var3);

   <T, O, D extends IDatatypePost<T, O>> T getDatatypePostOrNull(D var1, O var2);

   <T, D extends IDatatypeFor<T>> T getDatatypeFor(D var1) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   <T, D extends IDatatypeFor<T>> T getDatatypeForOrDefault(D var1, T var2);

   <T, D extends IDatatypeFor<T>> T getDatatypeForOrNull(D var1);

   <T extends IDatatype> Stream<String> tabCompleteDatatype(T var1);

   String rawRest();

   void requireMin(int var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException;

   void requireMax(int var1) throws baritone.api.command.exception.CommandTooManyArgumentsException;

   void requireExactly(int var1) throws baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException;

   boolean hasConsumed();

   ICommandArgument consumed();

   String consumedString();

   IArgConsumer copy();
}
