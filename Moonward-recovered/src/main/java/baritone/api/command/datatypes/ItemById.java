package baritone.api.command.datatypes;

import baritone.api.command.helpers.TabCompleteHelper;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public enum ItemById implements IDatatypeFor<Item> {
   INSTANCE;

   public Item get(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      Identifier var2 = Identifier.parse(var1.getConsumer().getString());
      Item var3;
      if ((var3 = BuiltInRegistries.ITEM.getOptional(var2).orElse(null)) == null) {
         throw new IllegalArgumentException("No item found by that id");
      } else {
         return var3;
      }
   }

   @Override
   public Stream<String> tabComplete(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return new TabCompleteHelper()
         .append(BuiltInRegistries.ITEM.keySet().stream().map(Identifier::toString))
         .filterPrefixNamespaced(var1.getConsumer().getString())
         .sortAlphabetically()
         .stream();
   }
}
