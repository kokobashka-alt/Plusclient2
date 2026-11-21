package baritone.api.schematic.mask;

import baritone.api.schematic.mask.operator.BinaryOperatorMask;
import baritone.api.schematic.mask.operator.NotMask;
import baritone.api.utils.BooleanBinaryOperators;
import net.minecraft.world.level.block.state.BlockState;

public interface Mask {
   boolean partOfMask(int var1, int var2, int var3, BlockState var4);

   int widthX();

   int heightY();

   int lengthZ();

   default Mask not() {
      return new NotMask(this);
   }

   default Mask union(Mask var1) {
      return new BinaryOperatorMask(this, var1, BooleanBinaryOperators.OR);
   }

   default Mask intersection(Mask var1) {
      return new BinaryOperatorMask(this, var1, BooleanBinaryOperators.AND);
   }

   default Mask xor(Mask var1) {
      return new BinaryOperatorMask(this, var1, BooleanBinaryOperators.XOR);
   }
}
