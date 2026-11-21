package baritone.api.command.datatypes;

import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.utils.BlockOptionalMeta;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public enum ForBlockOptionalMeta implements IDatatypeFor<BlockOptionalMeta> {
   INSTANCE;

   private static Pattern PATTERN = Pattern.compile(
      "(?:[a-z0-9_.-]+:)?(?:[a-z0-9/_.-]+(?:\\[(?:(?:[a-z0-9_.-]+=[a-z0-9_.-]+,)*(?:[a-z0-9_.-]+(?:=(?:[a-z0-9_.-]+(?:\\])?)?)?)?|\\])?)?)?"
   );

   public BlockOptionalMeta get(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return new BlockOptionalMeta(var1.getConsumer().getString());
   }

   @Override
   public Stream<String> tabComplete(IDatatypeContext var1) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      String var2 = var1.getConsumer().peekString();
      if (!PATTERN.matcher(var2).matches()) {
         var1.getConsumer().getString();
         return Stream.empty();
      } else if (var2.endsWith("]")) {
         var1.getConsumer().getString();
         return Stream.empty();
      } else if (!var2.contains("[")) {
         return var1.getConsumer().tabCompleteDatatype(BlockById.INSTANCE);
      } else {
         var1.getConsumer().getString();
         String[] var5 = splitLast(var2, '[');
         String var3 = var5[0];
         String var4 = var5[1];
         Block var12 = BuiltInRegistries.BLOCK.getOptional(Identifier.parse(var3)).orElse(null);
         if (var12 == null) {
            return Stream.empty();
         } else {
            String[] var8 = splitLast(var4, ',');
            String var6 = var8[0];
            String var7 = var8[1];
            if (!var7.contains("=")) {
               var var14 = Stream.of(var6.split(",")).map(var0 -> var0.split("=")[0]).collect(Collectors.toSet());
               String var15 = var2.substring(0, var2.length() - var7.length());
               return new TabCompleteHelper()
                  .append(var12.getStateDefinition().getProperties().stream().map(Property::getName))
                  .filter(var1x -> !var14.contains(var1x))
                  .filterPrefix(var7)
                  .sortAlphabetically()
                  .map(var1x -> var15 + var1x)
                  .stream();
            } else {
               String[] var10 = splitLast(var7, '=');
               String var13 = var10[0];
               String var9 = var10[1];
               String var16 = var2.substring(0, var2.length() - var9.length());
               Property var11 = var12.getStateDefinition().getProperty(var13);
               return var11 == null
                  ? Stream.empty()
                  : new TabCompleteHelper().append(getValues(var11)).filterPrefix(var9).sortAlphabetically().map(var1x -> var16 + var1x).stream();
            }
         }
      }
   }

   private static String[] splitLast(String var0, char var1) {
      int var2 = var0.lastIndexOf(var1);
      return var2 == -1 ? new String[]{"", var0} : new String[]{var0.substring(0, var2), var0.substring(var2 + 1)};
   }

   private static <T extends Comparable<T>> Stream<String> getValues(Property<T> var0) {
      return var0.getPossibleValues().stream().map(var0::getName);
   }
}
