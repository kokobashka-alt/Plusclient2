package baritone.utils;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockStateInterfaceAccessWrapper implements BlockGetter {
   private final BlockStateInterface bsi;

   BlockStateInterfaceAccessWrapper(BlockStateInterface var1) {
      this.bsi = var1;
   }

   @Nullable
   @Override
   public BlockEntity getBlockEntity(BlockPos var1) {
      return null;
   }

   @Override
   public BlockState getBlockState(BlockPos var1) {
      return this.bsi.get0(var1.getX(), var1.getY(), var1.getZ());
   }

   @Override
   public FluidState getFluidState(BlockPos var1) {
      return this.getBlockState(var1).getFluidState();
   }

   @Override
   public int getHeight() {
      return this.bsi.world.getHeight();
   }

   @Override
   public int getMinY() {
      return this.bsi.world.getMinY();
   }
}
