package baritone.api.command.datatypes;

import baritone.api.command.helpers.TabCompleteHelper;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public enum BlockById implements IDatatypeFor<Block> {
   INSTANCE;

   public Block get(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      Identifier var2 = Identifier.parse(var1.getConsumer().getString());
      Block var3;
      if ((var3 = BuiltInRegistries.BLOCK.getOptional(var2).orElse(null)) == null) {
         throw new IllegalArgumentException("no block found by that id");
      } else {
         return var3;
      }
   }

   @Override
   public Stream<String> tabComplete(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      String var2 = var1.getConsumer().getString();
      return new TabCompleteHelper()
         .append(BuiltInRegistries.BLOCK.keySet().stream().map(Object::toString))
         .filterPrefixNamespaced(var2)
         .sortAlphabetically()
         .stream();
   }
}
