package baritone.api.process;

import net.minecraft.core.BlockPos;

public interface IFarmProcess extends IBaritoneProcess {
   void farm(int var1, BlockPos var2);

   default void farm() {
      this.farm(0, null);
   }

   default void farm(int var1) {
      this.farm(var1, null);
   }
}
