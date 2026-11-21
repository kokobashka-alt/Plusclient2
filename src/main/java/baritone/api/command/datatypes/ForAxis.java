package baritone.api.command.datatypes;

import baritone.api.command.helpers.TabCompleteHelper;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.core.Direction.Axis;

public enum ForAxis implements IDatatypeFor<Axis> {
   INSTANCE;

   public Axis get(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return Axis.valueOf(var1.getConsumer().getString().toUpperCase(Locale.US));
   }

   @Override
   public Stream<String> tabComplete(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return new TabCompleteHelper()
         .append(Stream.of(Axis.values()).map(Axis::getName).map(String::toLowerCase))
         .filterPrefix(var1.getConsumer().getString())
         .stream();
   }
}
