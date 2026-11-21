package baritone.utils.schematic;

import baritone.api.schematic.IStaticSchematic;
import baritone.api.schematic.MaskSchematic;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MapArtSchematic extends MaskSchematic {
   private final int[][] heightMap;

   public MapArtSchematic(IStaticSchematic var1) {
      super(var1);
      this.heightMap = generateHeightMap(var1);
   }

   @Override
   protected boolean partOfMask(int var1, int var2, int var3, BlockState var4) {
      return var2 >= this.heightMap[var1][var3];
   }

   private static int[][] generateHeightMap(IStaticSchematic var0) {
      int[][] var1 = new int[var0.widthX()][var0.lengthZ()];
      int var2 = 0;

      for (int var3 = 0; var3 < var0.widthX(); var3++) {
         for (int var4 = 0; var4 < var0.lengthZ(); var4++) {
            BlockState[] var5 = var0.getColumn(var3, var4);
            OptionalInt var6 = lastIndexMatching(var5, var0x -> !(var0x.getBlock() instanceof AirBlock));
            if (var6.isPresent()) {
               var1[var3][var4] = var6.getAsInt();
            } else {
               var2++;
               var1[var3][var4] = Integer.MAX_VALUE;
            }
         }
      }

      if (var2 != 0) {
         System.out.println(var2 + " columns had no block despite being in a map art, letting them be whatever");
      }

      return var1;
   }

   private static <T> OptionalInt lastIndexMatching(T[] var0, Predicate<? super T> var1) {
      for (int var2 = var0.length - 1; var2 >= 0; var2--) {
         if (var1.test(var0[var2])) {
            return OptionalInt.of(var2);
         }
      }

      return OptionalInt.empty();
   }
}
