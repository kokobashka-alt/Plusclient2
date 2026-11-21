package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.ItemById;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class PickupCommand extends Command {
   public PickupCommand(IBaritone var1) {
      super(var1, "pickup");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      HashSet var3 = new HashSet();

      while (var2.hasAny()) {
         Item var4 = var2.getDatatypeFor(ItemById.INSTANCE);
         var3.add(var4);
      }

      if (var3.isEmpty()) {
         this.baritone.getFollowProcess().pickup(var0 -> true);
         this.logDirect("Picking up all items");
      } else {
         this.baritone.getFollowProcess().pickup(var1x -> var3.contains(var1x.getItem()));
         this.logDirect("Picking up these items:");
         ((java.util.stream.Stream<Item>)(java.util.stream.Stream)var3.stream()).map(BuiltInRegistries.ITEM::getKey).map(Identifier::toString).forEach(this::logDirect);
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      while (var2.has(2)) {
         if (var2.peekDatatypeOrNull(ItemById.INSTANCE) == null) {
            return Stream.empty();
         }

         var2.get();
      }

      return var2.tabCompleteDatatype(ItemById.INSTANCE);
   }

   @Override
   public String getShortDesc() {
      return "Pickup items";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList("Usage:", "> pickup - Pickup anything", "> pickup <item1> <item2> <...> - Pickup certain items");
   }
}
