package wtf.onetap.util;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTypeUtil implements MinecraftAccess {
   public static final Set<BlockPos> BLOCK_POS_SET = new HashSet<>();
   private static int value = (int)-2128831371685134336L;
   private static int value2 = (int)2910714461226532863L;
   private static int value3 = (int)-6460386454697672704L;
   private static int value4 = (int)1685668343912071167L;

   private BlockTypeUtil() {
   }

   public static void m_8_void(int value5, int value6, int value7, int value8) {
      value = value5;
      value2 = value6;
      value3 = value7;
      value4 = value8;
   }

   public static boolean isInArea(BlockPos blockPos) {
      return blockPos.getX() >= value && blockPos.getX() <= value2 && blockPos.getZ() >= value3 && blockPos.getZ() <= value4;
   }

   public static boolean m_5_boolean(BlockPos blockPos) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return true;
      } else {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos);
         if (blockState.isAir()) {
            return true;
         } else {
            Block block = blockState.getBlock();
            if (block instanceof CarpetBlock
               || block instanceof SculkVeinBlock
               || block instanceof ButtonBlock
               || block instanceof LeverBlock
               || block instanceof LightBlock) {
               return true;
            } else {
               return !(block instanceof FenceBlock) && !(block instanceof WallBlock) && !(block instanceof FenceGateBlock)
                  ? !blockState.isSolid() || blockState.getCollisionShape(MinecraftAccess.MINECRAFT.level, blockPos).isEmpty()
                  : false;
            }
         }
      }
   }
}
