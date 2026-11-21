package baritone.api.schematic;

import net.minecraft.world.level.block.state.BlockState;

public interface IStaticSchematic extends ISchematic {
   BlockState getDirect(int var1, int var2, int var3);

   default BlockState[] getColumn(int var1, int var2) {
      BlockState[] var3 = new BlockState[this.heightY()];

      for (int var4 = 0; var4 < this.heightY(); var4++) {
         var3[var4] = this.getDirect(var1, var4, var2);
      }

      return var3;
   }
}
