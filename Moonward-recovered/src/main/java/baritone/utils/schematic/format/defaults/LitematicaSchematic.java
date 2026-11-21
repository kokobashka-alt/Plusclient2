package baritone.utils.schematic.format.defaults;

import baritone.api.schematic.CompositeSchematic;
import baritone.api.schematic.IStaticSchematic;
import baritone.utils.schematic.StaticSchematic;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.Validate;

public class LitematicaSchematic extends CompositeSchematic implements IStaticSchematic {
   public LitematicaSchematic(CompoundTag var1) {
      super(0, 0, 0);
      this.fillInSchematic(var1);
   }

   private static CompoundTag[] getRegions(CompoundTag var0) {
      return var0.getCompound("Regions")
         .map(CompoundTag::values)
         .map(var0x -> var0x.stream().filter(var0xx -> var0xx instanceof CompoundTag).map(CompoundTag.class::cast).toArray(CompoundTag[]::new))
         .orElse(new CompoundTag[0]);
   }

   private static int getMinOfSubregion(CompoundTag var0, String var1) {
      int var2 = var0.getCompound("Position").flatMap(var1x -> var1x.getInt(var1)).orElse(0);
      int var3 = var0.getCompound("Size").flatMap(var1x -> var1x.getInt(var1)).orElse(0);
      return Math.min(var2, var2 + var3 + 1);
   }

   private static BlockState[] getBlockList(ListTag var0) {
      BlockState[] var1 = new BlockState[var0.size()];

      for (int var2 = 0; var2 < var0.size(); var2++) {
         CompoundTag var3 = (CompoundTag)var0.get(var2);
         Identifier var4 = Identifier.tryParse(var3.getString("Name").orElse(""));
         Block var5 = var4 == null ? Blocks.AIR : BuiltInRegistries.BLOCK.get(var4).map(Reference::value).orElse(Blocks.AIR);
         CompoundTag var6 = var3.getCompound("Properties").orElse(new CompoundTag());
         var1[var2] = getBlockState(var5, var6);
      }

      return var1;
   }

   private static BlockState getBlockState(Block var0, CompoundTag var1) {
      BlockState var2 = var0.defaultBlockState();

      for (String var4 : var1.keySet()) {
         Property var5 = var0.getStateDefinition().getProperty(var4);
         String var6 = var1.getString(var4).orElse(null);
         if (var5 != null) {
            var2 = setPropertyValue(var2, var5, var6);
         }
      }

      return var2;
   }

   private static <T extends Comparable<T>> BlockState setPropertyValue(BlockState var0, Property<T> var1, String var2) {
      var var3 = var1.getValue(var2);
      if (var3.isPresent()) {
         return var0.setValue(var1, (T)var3.get());
      } else {
         throw new IllegalArgumentException("Invalid value for property " + var1);
      }
   }

   private static int getBitsPerBlock(int var0) {
      return (int)Math.max(2.0, Math.ceil(Math.log(var0) / Math.log(2.0)));
   }

   private static long getVolume(CompoundTag var0) {
      CompoundTag var1 = var0.getCompound("Size").orElse(new CompoundTag());
      return Math.abs(var1.getInt("x").orElse(0) * var1.getInt("y").orElse(0) * var1.getInt("z").orElse(0));
   }

   private static int getMinOfSchematic(CompoundTag var0, String var1) {
      int var2 = Integer.MAX_VALUE;

      for (CompoundTag var6 : getRegions(var0)) {
         var2 = Math.min(var2, getMinOfSubregion(var6, var1));
      }

      return var2;
   }

   private void fillInSchematic(CompoundTag var1) {
      Vec3i var2 = new Vec3i(getMinOfSchematic(var1, "x"), getMinOfSchematic(var1, "y"), getMinOfSchematic(var1, "z"));

      for (CompoundTag var6 : getRegions(var1)) {
         ListTag var7 = var6.getListOrEmpty("BlockStatePalette");
         BlockState[] var8 = getBlockList(var7);
         int var9 = getBitsPerBlock(var7.size());
         long var10 = getVolume(var6);
         long[] var12 = var6.getLongArray("BlockStates").orElse(new long[0]);
         LitematicaSchematic.LitematicaBitArray var13 = new LitematicaSchematic.LitematicaBitArray(var9, var10, var12);
         this.writeSubregionIntoSchematic(var6, var2, var8, var13);
      }
   }

   private void writeSubregionIntoSchematic(CompoundTag var1, Vec3i var2, BlockState[] var3, LitematicaSchematic.LitematicaBitArray var4) {
      int var5 = getMinOfSubregion(var1, "x") - var2.getX();
      int var6 = getMinOfSubregion(var1, "y") - var2.getY();
      int var7 = getMinOfSubregion(var1, "z") - var2.getZ();
      CompoundTag var8 = var1.getCompound("Size").orElse(new CompoundTag());
      int var9 = Math.abs(var8.getInt("x").orElse(0));
      int var10 = Math.abs(var8.getInt("y").orElse(0));
      int var11 = Math.abs(var8.getInt("z").orElse(0));
      BlockState[][][] var12 = new BlockState[var9][var11][var10];
      int var13 = 0;

      for (int var14 = 0; var14 < var10; var14++) {
         for (int var15 = 0; var15 < var11; var15++) {
            for (int var16 = 0; var16 < var9; var16++) {
               var12[var16][var15][var14] = var3[var4.getAt(var13)];
               var13++;
            }
         }
      }

      this.put(new StaticSchematic(var12), var5, var6, var7);
   }

   @Override
   public BlockState getDirect(int var1, int var2, int var3) {
      return this.desiredState(var1, var2, var3, null, Collections.emptyList());
   }

   private static class LitematicaBitArray {
      private final long[] longArray;
      private final int bitsPerEntry;
      private final long maxEntryValue;
      private final long arraySize;

      public LitematicaBitArray(int var1, long var2, @Nullable long[] var4) {
         Validate.inclusiveBetween(1L, 32L, var1);
         this.arraySize = var2;
         this.bitsPerEntry = var1;
         this.maxEntryValue = (1L << var1) - 1L;
         if (var4 != null) {
            this.longArray = var4;
         } else {
            this.longArray = new long[(int)(roundUp(var2 * var1, 64L) / 64L)];
         }
      }

      public static long roundUp(long var0, long var2) {
         byte var4 = 1;
         if (var2 == 0L) {
            return 0L;
         }

         if (var0 == 0L) {
            return var2;
         }

         if (var0 < 0L) {
            var4 = -1;
         }

         long var5 = var0 % (var2 * var4);
         return var5 == 0L ? var0 : var0 + var2 * var4 - var5;
      }

      public int getAt(long var1) {
         Validate.inclusiveBetween(0L, this.arraySize - 1L, var1);
         long var3 = var1 * this.bitsPerEntry;
         int var5 = (int)(var3 >> 6);
         int var6 = (int)((var1 + 1L) * this.bitsPerEntry - 1L >> 6);
         int var7 = (int)(var3 & 63L);
         if (var5 == var6) {
            return (int)(this.longArray[var5] >>> var7 & this.maxEntryValue);
         }

         int var8 = 64 - var7;
         return (int)((this.longArray[var5] >>> var7 | this.longArray[var6] << var8) & this.maxEntryValue);
      }

      public long size() {
         return this.arraySize;
      }
   }
}
