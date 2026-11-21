package baritone.utils.schematic;

import baritone.api.schematic.ISchematic;
import baritone.api.schematic.MaskSchematic;
import baritone.api.selection.ISelection;
import java.util.stream.Stream;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public class SelectionSchematic extends MaskSchematic {
   private final ISelection[] selections;

   public SelectionSchematic(ISchematic var1, Vec3i var2, ISelection[] var3) {
      super(var1);
      this.selections = Stream.of(var3)
         .map(var1x -> var1x.shift(Direction.WEST, var2.getX()).shift(Direction.DOWN, var2.getY()).shift(Direction.NORTH, var2.getZ()))
         .toArray(ISelection[]::new);
   }

   @Override
   protected boolean partOfMask(int var1, int var2, int var3, BlockState var4) {
      for (ISelection var8 : this.selections) {
         if (var1 >= var8.min().x && var2 >= var8.min().y && var3 >= var8.min().z && var1 <= var8.max().x && var2 <= var8.max().y && var3 <= var8.max().z) {
            return true;
         }
      }

      return false;
   }
}
