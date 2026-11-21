package baritone.command.argparser;

import baritone.api.command.argparser.IArgParser;
import baritone.api.command.argument.ICommandArgument;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DefaultArgParsers {
   public static final List<IArgParser<?>> ALL = Arrays.asList(
      DefaultArgParsers.IntArgumentParser.INSTANCE,
      DefaultArgParsers.LongArgumentParser.INSTANCE,
      DefaultArgParsers.FloatArgumentParser.INSTANCE,
      DefaultArgParsers.DoubleArgumentParser.INSTANCE,
      DefaultArgParsers.BooleanArgumentParser.INSTANCE
   );

   public static class BooleanArgumentParser implements IArgParser.Stateless<Boolean> {
      public static final DefaultArgParsers.BooleanArgumentParser INSTANCE = new DefaultArgParsers.BooleanArgumentParser();
      public static final List<String> TRUTHY_VALUES = Arrays.asList("1", "true", "yes", "t", "y", "on", "enable");
      public static final List<String> FALSY_VALUES = Arrays.asList("0", "false", "no", "f", "n", "off", "disable");

      @Override
      public Class<Boolean> getTarget() {
         return Boolean.class;
      }

      public Boolean parseArg(ICommandArgument var1) {
         String var2 = var1.getValue();
         if (TRUTHY_VALUES.contains(var2.toLowerCase(Locale.US))) {
            return true;
         } else if (FALSY_VALUES.contains(var2.toLowerCase(Locale.US))) {
            return false;
         } else {
            throw new IllegalArgumentException("invalid boolean");
         }
      }
   }

   public enum DoubleArgumentParser implements IArgParser.Stateless<Double> {
      INSTANCE;

      @Override
      public Class<Double> getTarget() {
         return Double.class;
      }

      public Double parseArg(ICommandArgument var1) {
         String var2 = var1.getValue();
         if (!var2.matches("^([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)|)$")) {
            throw new IllegalArgumentException("failed double format check");
         } else {
            return Double.parseDouble(var2);
         }
      }
   }

   public enum FloatArgumentParser implements IArgParser.Stateless<Float> {
      INSTANCE;

      @Override
      public Class<Float> getTarget() {
         return Float.class;
      }

      public Float parseArg(ICommandArgument var1) {
         String var2 = var1.getValue();
         if (!var2.matches("^([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)|)$")) {
            throw new IllegalArgumentException("failed float format check");
         } else {
            return Float.parseFloat(var2);
         }
      }
   }

   public enum IntArgumentParser implements IArgParser.Stateless<Integer> {
      INSTANCE;

      @Override
      public Class<Integer> getTarget() {
         return Integer.class;
      }

      public Integer parseArg(ICommandArgument var1) {
         return Integer.parseInt(var1.getValue());
      }
   }

   public enum LongArgumentParser implements IArgParser.Stateless<Long> {
      INSTANCE;

      @Override
      public Class<Long> getTarget() {
         return Long.class;
      }

      public Long parseArg(ICommandArgument var1) {
         return Long.parseLong(var1.getValue());
      }
   }
}
