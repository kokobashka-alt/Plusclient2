package baritone.api.schematic;

import baritone.api.utils.BlockOptionalMeta;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;

public class FillSchematic extends AbstractSchematic {
   private final BlockOptionalMeta bom;

   public FillSchematic(int var1, int var2, int var3, BlockOptionalMeta var4) {
      super(var1, var2, var3);
      this.bom = var4;
   }

   public FillSchematic(int var1, int var2, int var3, BlockState var4) {
      this(var1, var2, var3, new BlockOptionalMeta(var4.getBlock()));
   }

   public BlockOptionalMeta getBom() {
      return this.bom;
   }

   @Override
   public BlockState desiredState(int var1, int var2, int var3, BlockState var4, List<BlockState> var5) {
      if (this.bom.matches(var4)) {
         return var4;
      }

      for (BlockState var7 : var5) {
         if (this.bom.matches(var7)) {
            return var7;
         }
      }

      return this.bom.getAnyBlockState();
   }
}
