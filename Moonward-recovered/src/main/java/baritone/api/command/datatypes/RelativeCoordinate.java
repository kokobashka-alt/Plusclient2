package baritone.api.command.datatypes;

import baritone.api.command.argument.IArgConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public enum RelativeCoordinate implements IDatatypePost<Double, Double> {
   INSTANCE;

   private static String ScalesAliasRegex = "[kKmM]";
   private static Pattern PATTERN = Pattern.compile("^(~?)([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(" + ScalesAliasRegex + "?)|)$");

   public Double apply(IDatatypeContext var1, Double var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      if (var2 == null) {
         var2 = 0.0;
      }

      Matcher var3 = PATTERN.matcher(var1.getConsumer().getString());
      if (!var3.matches()) {
         throw new IllegalArgumentException("pattern doesn't match");
      }

      boolean var4 = !var3.group(1).isEmpty();
      double var5 = var3.group(2).isEmpty() ? 0.0 : Double.parseDouble(var3.group(2).replaceAll(ScalesAliasRegex, ""));
      if (var3.group(2).toLowerCase().contains("k")) {
         var5 *= 1000.0;
      }

      if (var3.group(2).toLowerCase().contains("m")) {
         var5 *= 1000000.0;
      }

      return var4 ? var2 + var5 : var5;
   }

   @Override
   public Stream<String> tabComplete(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      IArgConsumer var2 = var1.getConsumer();
      return !var2.has(2) && var2.getString().matches("^(~|$)") ? Stream.of("~") : Stream.empty();
   }
}
