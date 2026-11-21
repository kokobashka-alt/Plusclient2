package baritone.api.schematic;

import net.minecraft.world.level.block.state.BlockState;

public class WallsSchematic extends MaskSchematic {
   public WallsSchematic(ISchematic var1) {
      super(var1);
   }

   @Override
   protected boolean partOfMask(int var1, int var2, int var3, BlockState var4) {
      return var1 == 0 || var3 == 0 || var1 == this.widthX() - 1 || var3 == this.lengthZ() - 1;
   }
}
