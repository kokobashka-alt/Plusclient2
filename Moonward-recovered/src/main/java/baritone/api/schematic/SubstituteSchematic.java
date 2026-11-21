package baritone.api.schematic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class SubstituteSchematic extends AbstractSchematic {
   private final ISchematic schematic;
   private final Map<Block, List<Block>> substitutions;
   private final Map<BlockState, Map<Block, BlockState>> blockStateCache = new HashMap<>();

   public SubstituteSchematic(ISchematic var1, Map<Block, List<Block>> var2) {
      super(var1.widthX(), var1.heightY(), var1.lengthZ());
      this.schematic = var1;
      this.substitutions = var2;
   }

   @Override
   public boolean inSchematic(int var1, int var2, int var3, BlockState var4) {
      return this.schematic.inSchematic(var1, var2, var3, var4);
   }

   @Override
   public BlockState desiredState(int var1, int var2, int var3, BlockState var4, List<BlockState> var5) {
      BlockState var6 = this.schematic.desiredState(var1, var2, var3, var4, var5);
      Block var7 = var6.getBlock();
      if (!this.substitutions.containsKey(var7)) {
         return var6;
      }

      var var8 = this.substitutions.get(var7);
      if (var8.contains(var4.getBlock()) && !(var4.getBlock() instanceof AirBlock)) {
         return this.withBlock(var6, var4.getBlock());
      }

      for (Block var10 : var8) {
         if (var10 instanceof AirBlock) {
            return var4.getBlock() instanceof AirBlock ? var4 : Blocks.AIR.defaultBlockState();
         }

         for (BlockState var12 : var5) {
            if (var10.equals(var12.getBlock())) {
               return this.withBlock(var6, var12.getBlock());
            }
         }
      }

      return ((Block)var8.get(0)).defaultBlockState();
   }

   private BlockState withBlock(BlockState var1, Block var2) {
      if (this.blockStateCache.containsKey(var1) && this.blockStateCache.get(var1).containsKey(var2)) {
         return this.blockStateCache.get(var1).get(var2);
      }

      var var3 = var1.getProperties();
      BlockState var4 = var2.defaultBlockState();

      for (Property var6 : var3) {
         try {
            var4 = this.copySingleProp(var1, var4, var6);
         } catch (IllegalArgumentException var8) {
         }
      }

      this.blockStateCache.computeIfAbsent(var1, var0 -> new HashMap<>()).put(var2, var4);
      return var4;
   }

   private <T extends Comparable<T>> BlockState copySingleProp(BlockState var1, BlockState var2, Property<T> var3) {
      return var2.setValue(var3, var1.getValue(var3));
   }
}
