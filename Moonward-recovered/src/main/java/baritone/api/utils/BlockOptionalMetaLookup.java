package baritone.api.utils;

import baritone.api.utils.accessor.IItemStack;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockOptionalMetaLookup {
   private final ImmutableSet<Block> blockSet;
   private final ImmutableSet<BlockState> blockStateSet;
   private final ImmutableSet<Integer> stackHashes;
   private final BlockOptionalMeta[] boms;

   public BlockOptionalMetaLookup(BlockOptionalMeta... var1) {
      this.boms = var1;
      HashSet var2 = new HashSet();
      HashSet var3 = new HashSet();
      HashSet var4 = new HashSet();

      for (BlockOptionalMeta var8 : var1) {
         var2.add(var8.getBlock());
         var3.addAll(var8.getAllBlockStates());
         var4.addAll(var8.stackHashes());
      }

      this.blockSet = ImmutableSet.copyOf(var2);
      this.blockStateSet = ImmutableSet.copyOf(var3);
      this.stackHashes = ImmutableSet.copyOf(var4);
   }

   public BlockOptionalMetaLookup(Block... var1) {
      this(Stream.of(var1).map(BlockOptionalMeta::new).toArray(BlockOptionalMeta[]::new));
   }

   public BlockOptionalMetaLookup(List<Block> var1) {
      this(var1.stream().map(BlockOptionalMeta::new).toArray(BlockOptionalMeta[]::new));
   }

   public BlockOptionalMetaLookup(String... var1) {
      this(Stream.of(var1).map(BlockOptionalMeta::new).toArray(BlockOptionalMeta[]::new));
   }

   public boolean has(Block var1) {
      return this.blockSet.contains(var1);
   }

   public boolean has(BlockState var1) {
      return this.blockStateSet.contains(var1);
   }

   public boolean has(ItemStack var1) {
      int var2 = ((IItemStack)(Object)var1).getBaritoneHash();
      var2 -= var1.getDamageValue();
      return this.stackHashes.contains(var2);
   }

   public List<BlockOptionalMeta> blocks() {
      return Arrays.asList(this.boms);
   }

   @Override
   public String toString() {
      return String.format("BlockOptionalMetaLookup{%s}", Arrays.toString(this.boms));
   }
}
