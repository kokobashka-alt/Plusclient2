package baritone.api.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class BlockUtils {
   private static transient Map<String, Block> resourceCache = new HashMap<>();

   public static String blockToString(Block var0) {
      Identifier var1 = BuiltInRegistries.BLOCK.getKey(var0);
      String var2 = var1.getPath();
      if (!var1.getNamespace().equals("minecraft")) {
         var2 = var1.toString();
      }

      return var2;
   }

   public static Block stringToBlockRequired(String var0) {
      Block var1 = stringToBlockNullable(var0);
      if (var1 == null) {
         throw new IllegalArgumentException(String.format("Invalid block name %s", var0));
      } else {
         return var1;
      }
   }

   public static Block stringToBlockNullable(String var0) {
      Block var1 = resourceCache.get(var0);
      if (var1 != null) {
         return var1;
      }

      if (resourceCache.containsKey(var0)) {
         return null;
      }

      var1 = BuiltInRegistries.BLOCK.getOptional(Identifier.tryParse(var0.contains(":") ? var0 : "minecraft:" + var0)).orElse(null);
      HashMap var2 = new HashMap<>(resourceCache);
      var2.put(var0, var1);
      resourceCache = var2;
      return var1;
   }

   private BlockUtils() {
   }
}
