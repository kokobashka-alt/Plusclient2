package baritone.api.schematic;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class RotatedSchematic implements ISchematic {
   private final ISchematic schematic;
   private final Rotation rotation;
   private final Rotation inverseRotation;

   public RotatedSchematic(ISchematic var1, Rotation var2) {
      this.schematic = var1;
      this.rotation = var2;
      this.inverseRotation = var2.getRotated(var2).getRotated(var2);
   }

   @Override
   public boolean inSchematic(int var1, int var2, int var3, BlockState var4) {
      return this.schematic
         .inSchematic(
            rotateX(var1, var3, this.widthX(), this.lengthZ(), this.inverseRotation),
            var2,
            rotateZ(var1, var3, this.widthX(), this.lengthZ(), this.inverseRotation),
            rotate(var4, this.inverseRotation)
         );
   }

   @Override
   public BlockState desiredState(int var1, int var2, int var3, BlockState var4, List<BlockState> var5) {
      return rotate(
         this.schematic
            .desiredState(
               rotateX(var1, var3, this.widthX(), this.lengthZ(), this.inverseRotation),
               var2,
               rotateZ(var1, var3, this.widthX(), this.lengthZ(), this.inverseRotation),
               rotate(var4, this.inverseRotation),
               rotate(var5, this.inverseRotation)
            ),
         this.rotation
      );
   }

   @Override
   public void reset() {
      this.schematic.reset();
   }

   @Override
   public int widthX() {
      return flipsCoordinates(this.rotation) ? this.schematic.lengthZ() : this.schematic.widthX();
   }

   @Override
   public int heightY() {
      return this.schematic.heightY();
   }

   @Override
   public int lengthZ() {
      return flipsCoordinates(this.rotation) ? this.schematic.widthX() : this.schematic.lengthZ();
   }

   private static boolean flipsCoordinates(Rotation var0) {
      return var0 == Rotation.CLOCKWISE_90 || var0 == Rotation.COUNTERCLOCKWISE_90;
   }

   private static int rotateX(int var0, int var1, int var2, int var3, Rotation var4) {
      switch (var4) {
         case NONE:
            return var0;
         case CLOCKWISE_90:
            return var3 - var1 - 1;
         case CLOCKWISE_180:
            return var2 - var0 - 1;
         case COUNTERCLOCKWISE_90:
            return var1;
         default:
            throw new IllegalArgumentException("Unknown rotation");
      }
   }

   private static int rotateZ(int var0, int var1, int var2, int var3, Rotation var4) {
      switch (var4) {
         case NONE:
            return var1;
         case CLOCKWISE_90:
            return var0;
         case CLOCKWISE_180:
            return var3 - var1 - 1;
         case COUNTERCLOCKWISE_90:
            return var2 - var0 - 1;
         default:
            throw new IllegalArgumentException("Unknown rotation");
      }
   }

   private static BlockState rotate(BlockState var0, Rotation var1) {
      return var0 == null ? null : var0.rotate(var1);
   }

   private static List<BlockState> rotate(List<BlockState> var0, Rotation var1) {
      return var0 == null ? null : var0.stream().map(var1x -> rotate(var1x, var1)).collect(Collectors.toList());
   }
}
