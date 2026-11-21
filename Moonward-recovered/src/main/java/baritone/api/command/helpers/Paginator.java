package baritone.api.command.helpers;

import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandInvalidTypeException;
import baritone.api.utils.Helper;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;

public class Paginator<E> implements Helper {
   public final List<E> entries;
   public int pageSize = 8;
   public int page = 1;

   public Paginator(List<E> var1) {
      this.entries = var1;
   }

   public Paginator(E... var1) {
      this.entries = Arrays.asList((E[])var1);
   }

   public Paginator<E> setPageSize(int var1) {
      this.pageSize = var1;
      return this;
   }

   public int getMaxPage() {
      return (this.entries.size() - 1) / this.pageSize + 1;
   }

   public boolean validPage(int var1) {
      return var1 > 0 && var1 <= this.getMaxPage();
   }

   public Paginator<E> skipPages(int var1) {
      this.page += var1;
      return this;
   }

   public void display(Function<E, Component> var1, String var2) {
      int var3 = (this.page - 1) * this.pageSize;

      for (int var4 = var3; var4 < var3 + this.pageSize; var4++) {
         if (var4 < this.entries.size()) {
            this.logDirect((Component)var1.apply(this.entries.get(var4)));
         } else {
            this.logDirect("--", ChatFormatting.DARK_GRAY);
         }
      }

      boolean var9 = var2 != null && this.validPage(this.page - 1);
      boolean var5 = var2 != null && this.validPage(this.page + 1);
      MutableComponent var6 = Component.literal("<<");
      if (var9) {
         var6.setStyle(
            var6.getStyle()
               .withClickEvent(new RunCommand(String.format("%s %d", var2, this.page - 1)))
               .withHoverEvent(new ShowText(Component.literal("Click to view previous page")))
         );
      } else {
         var6.setStyle(var6.getStyle().withColor(ChatFormatting.DARK_GRAY));
      }

      MutableComponent var7 = Component.literal(">>");
      if (var5) {
         var7.setStyle(
            var7.getStyle()
               .withClickEvent(new RunCommand(String.format("%s %d", var2, this.page + 1)))
               .withHoverEvent(new ShowText(Component.literal("Click to view next page")))
         );
      } else {
         var7.setStyle(var7.getStyle().withColor(ChatFormatting.DARK_GRAY));
      }

      MutableComponent var8 = Component.literal("");
      var8.setStyle(var8.getStyle().withColor(ChatFormatting.GRAY));
      var8.append(var6);
      var8.append(" | ");
      var8.append(var7);
      var8.append(String.format(" %d/%d", this.page, this.getMaxPage()));
      this.logDirect(var8);
   }

   public void display(Function<E, Component> var1) {
      this.display(var1, null);
   }

   public static <T> void paginate(IArgConsumer var0, Paginator<T> var1, Runnable var2, Function<T, Component> var3, String var4) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      int var5 = 1;
      var0.requireMax(1);
      if (var0.hasAny()) {
         var5 = var0.getAs(Integer.class);
         if (!var1.validPage(var5)) {
            throw new CommandInvalidTypeException(var0.consumed(), String.format("a valid page (1-%d)", var1.getMaxPage()), var0.consumed().getValue());
         }
      }

      var1.skipPages(var5 - var1.page);
      if (var2 != null) {
         var2.run();
      }

      var1.display(var3, var4);
   }

   public static <T> void paginate(IArgConsumer var0, List<T> var1, Runnable var2, Function<T, Component> var3, String var4) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, new Paginator(var1), var2, var3, var4);
   }

   public static <T> void paginate(IArgConsumer var0, T[] var1, Runnable var2, Function<T, Component> var3, String var4) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, Arrays.asList(var1), var2, var3, var4);
   }

   public static <T> void paginate(IArgConsumer var0, Paginator<T> var1, Function<T, Component> var2, String var3) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, var1, null, var2, var3);
   }

   public static <T> void paginate(IArgConsumer var0, List<T> var1, Function<T, Component> var2, String var3) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, new Paginator(var1), null, var2, var3);
   }

   public static <T> void paginate(IArgConsumer var0, T[] var1, Function<T, Component> var2, String var3) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, Arrays.asList(var1), null, var2, var3);
   }

   public static <T> void paginate(IArgConsumer var0, Paginator<T> var1, Runnable var2, Function<T, Component> var3) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, var1, var2, var3, null);
   }

   public static <T> void paginate(IArgConsumer var0, List<T> var1, Runnable var2, Function<T, Component> var3) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, new Paginator(var1), var2, var3, null);
   }

   public static <T> void paginate(IArgConsumer var0, T[] var1, Runnable var2, Function<T, Component> var3) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, Arrays.asList(var1), var2, var3, null);
   }

   public static <T> void paginate(IArgConsumer var0, Paginator<T> var1, Function<T, Component> var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, var1, null, var2, null);
   }

   public static <T> void paginate(IArgConsumer var0, List<T> var1, Function<T, Component> var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, new Paginator(var1), null, var2, null);
   }

   public static <T> void paginate(IArgConsumer var0, T[] var1, Function<T, Component> var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      paginate(var0, Arrays.asList(var1), null, var2, null);
   }
}
