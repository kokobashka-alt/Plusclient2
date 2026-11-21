package baritone.api.command.datatypes;

import baritone.api.command.helpers.TabCompleteHelper;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

public enum EntityClassById implements IDatatypeFor<EntityType> {
   INSTANCE;

   public EntityType get(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      Identifier var2 = Identifier.parse(var1.getConsumer().getString());
      EntityType var3;
      if ((var3 = BuiltInRegistries.ENTITY_TYPE.getOptional(var2).orElse(null)) == null) {
         throw new IllegalArgumentException("no entity found by that id");
      } else {
         return var3;
      }
   }

   @Override
   public Stream<String> tabComplete(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return new TabCompleteHelper()
         .append(BuiltInRegistries.ENTITY_TYPE.stream().map(Object::toString))
         .filterPrefixNamespaced(var1.getConsumer().getString())
         .sortAlphabetically()
         .stream();
   }
}
