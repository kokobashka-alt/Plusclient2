package baritone.api.schematic.mask.operator;

import baritone.api.schematic.mask.AbstractMask;
import baritone.api.schematic.mask.Mask;
import baritone.api.schematic.mask.StaticMask;
import net.minecraft.world.level.block.state.BlockState;

public class NotMask extends AbstractMask {
   private final Mask source;

   public NotMask(Mask var1) {
      super(var1.widthX(), var1.heightY(), var1.lengthZ());
      this.source = var1;
   }

   @Override
   public boolean partOfMask(int var1, int var2, int var3, BlockState var4) {
      return !this.source.partOfMask(var1, var2, var3, var4);
   }

   public static final class Static extends AbstractMask implements StaticMask {
      private final StaticMask source;

      public Static(StaticMask var1) {
         super(var1.widthX(), var1.heightY(), var1.lengthZ());
         this.source = var1;
      }

      @Override
      public boolean partOfMask(int var1, int var2, int var3) {
         return !this.source.partOfMask(var1, var2, var3);
      }
   }
}
