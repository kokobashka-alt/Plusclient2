package baritone.api.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VecUtils {
   private VecUtils() {
   }

   public static Vec3 calculateBlockCenter(Level var0, BlockPos var1) {
      if (var1 instanceof BetterBlockPos) {
         var1 = new BlockPos(var1.getX(), var1.getY(), var1.getZ());
      }

      BlockState var2 = var0.getBlockState(var1);
      VoxelShape var3 = var2.getCollisionShape(var0, var1);
      if (var3.isEmpty()) {
         return getBlockPosCenter(var1);
      }

      double var4 = (var3.min(Axis.X) + var3.max(Axis.X)) / 2.0;
      double var6 = (var3.min(Axis.Y) + var3.max(Axis.Y)) / 2.0;
      double var8 = (var3.min(Axis.Z) + var3.max(Axis.Z)) / 2.0;
      if (!Double.isNaN(var4) && !Double.isNaN(var6) && !Double.isNaN(var8)) {
         if (var2.getBlock() instanceof BaseFireBlock) {
            var6 = 0.0;
         }

         return new Vec3(var1.getX() + var4, var1.getY() + var6, var1.getZ() + var8);
      } else {
         throw new IllegalStateException(var2 + " " + var1 + " " + var3);
      }
   }

   public static Vec3 getBlockPosCenter(BlockPos var0) {
      return new Vec3(var0.getX() + 0.5, var0.getY() + 0.5, var0.getZ() + 0.5);
   }

   public static double distanceToCenter(BlockPos var0, double var1, double var3, double var5) {
      double var7 = var0.getX() + 0.5 - var1;
      double var9 = var0.getY() + 0.5 - var3;
      double var11 = var0.getZ() + 0.5 - var5;
      return Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public static double entityDistanceToCenter(Entity var0, BlockPos var1) {
      return distanceToCenter(var1, var0.position().x, var0.position().y, var0.position().z);
   }

   public static double entityFlatDistanceToCenter(Entity var0, BlockPos var1) {
      return distanceToCenter(var1, var0.position().x, var1.getY() + 0.5, var0.position().z);
   }
}
