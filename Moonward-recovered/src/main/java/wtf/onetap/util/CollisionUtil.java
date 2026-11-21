package wtf.onetap.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionUtil {
   Direction[] DIRECTION_ARRAY = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};
   Direction[] DIRECTION_ARRAY2 = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.DOWN};

   VoxelShape m_9_VoxelShape(BlockPos blockPos);

   float m_1_float(BlockPos blockPos);

   CollisionUtil.ModeKind2 m_7_C1(BlockPos blockPos);

   static CollisionUtil m_7(final BlockGetter blockGetter) {
      return new CollisionUtil() {
         @Override
         public VoxelShape m_9_VoxelShape(BlockPos blockPos) {
            return blockGetter.getBlockState(blockPos).getCollisionShape(blockGetter, blockPos);
         }

         @Override
         public float m_1_float(BlockPos blockPos2) {
            BlockState blockState = blockGetter.getBlockState(blockPos2);
            FluidState fluidState = blockState.getFluidState();
            return blockState.isAir() && fluidState.isEmpty()
               ? Float.intBitsToFloat((int)3012800680957050880L)
               : Math.max(blockState.getBlock().getExplosionResistance(), fluidState.getExplosionResistance());
         }

         @Override
         public CollisionUtil.ModeKind2 m_7_C1(BlockPos blockPos3) {
            BlockState blockState2 = blockGetter.getBlockState(blockPos3);
            if (blockState2.isAir()) {
               return CollisionUtil.ModeKind2.AIR;
            } else if (blockState2.is(Blocks.COBWEB)) {
               return CollisionUtil.ModeKind2.WEB;
            } else if (blockState2.is(Blocks.OBSIDIAN) || blockState2.is(Blocks.BEDROCK)) {
               return CollisionUtil.ModeKind2.BASE;
            } else if (blockState2.canBeReplaced()) {
               return blockState2.getFluidState().isEmpty() ? CollisionUtil.ModeKind2.REPLACEABLE : CollisionUtil.ModeKind2.FLUID;
            } else {
               return blockState2.getCollisionShape(blockGetter, blockPos3).isEmpty() && !blockState2.blocksMotion() && !blockState2.isSolid() ? CollisionUtil.ModeKind2.DECOR : CollisionUtil.ModeKind2.SOLID;
            }
         }
      };
   }

   default CollisionUtil m_8(BlockPos blockPos, CollisionUtil.ModeKind c0) {
      return this.m_0(Map.of(blockPos.immutable(), c0));
   }

   default CollisionUtil m_9(Collection<BlockPos> blockPosList) {
      HashSet<BlockPos> blockPosSet = new HashSet<>();

      for (BlockPos blockPos : blockPosList) {
         blockPosSet.add(blockPos.immutable());
      }

      HashMap map = new HashMap();
      ArrayDeque<BlockPos> arrayDeque = new ArrayDeque<>();

      for (BlockPos blockPos2 : blockPosSet) {
         for (Direction direction : DIRECTION_ARRAY) {
            if (this.m_7_C1(blockPos2.relative(direction)) == CollisionUtil.ModeKind2.FLUID) {
               map.put(blockPos2, CollisionUtil.ModeKind.FLUID);
               arrayDeque.add(blockPos2);
               break;
            }
         }
      }

      while (!arrayDeque.isEmpty()) {
         BlockPos blockPos3 = (BlockPos)arrayDeque.poll();

         for (Direction direction2 : DIRECTION_ARRAY2) {
            BlockPos blockPos4 = blockPos3.relative(direction2);
            if (!map.containsKey(blockPos4)) {
               if (blockPosSet.contains(blockPos4)) {
                  map.put(blockPos4, CollisionUtil.ModeKind.FLUID);
                  arrayDeque.add(blockPos4);
               } else if (blockPosSet.contains(blockPos3) && this.m_7_C1(blockPos4) == CollisionUtil.ModeKind2.AIR) {
                  map.put(blockPos4, CollisionUtil.ModeKind.FLUID);
               }
            }
         }
      }

      for (BlockPos blockPos5 : blockPosSet) {
         map.putIfAbsent(blockPos5, CollisionUtil.ModeKind.AIR);
      }

      return this.m_0(map);
   }

   default CollisionUtil m_6_C6y(final BlockPos blockPos) {
      final CollisionUtil collisionUtil = this;
      return new CollisionUtil() {
         private CollisionUtil collisionUtil;

         private CollisionUtil m_2_C6y() {
            if (this.collisionUtil == null) {
               this.collisionUtil = collisionUtil.m_9(AimPoints.m_8(collisionUtil, blockPos));
            }

            return this.collisionUtil;
         }

         @Override
         public VoxelShape m_9_VoxelShape(BlockPos blockPos2) {
            return this.m_2_C6y().m_9_VoxelShape(blockPos2);
         }

         @Override
         public float m_1_float(BlockPos blockPos3) {
            return this.m_2_C6y().m_1_float(blockPos3);
         }

         @Override
         public CollisionUtil.ModeKind2 m_7_C1(BlockPos blockPos4) {
            return this.m_2_C6y().m_7_C1(blockPos4);
         }
      };
   }

   default CollisionUtil m_0(final Map<BlockPos, CollisionUtil.ModeKind> map) {
      if (map.isEmpty()) {
         return this;
      }

      final CollisionUtil collisionUtil = this;
      return new CollisionUtil() {
         @Override
         public VoxelShape m_9_VoxelShape(BlockPos blockPos) {
            CollisionUtil.ModeKind c0 = (CollisionUtil.ModeKind)map.get(blockPos);
            return c0 != null ? c0.voxelShape : collisionUtil.m_9_VoxelShape(blockPos);
         }

         @Override
         public float m_1_float(BlockPos blockPos2) {
            CollisionUtil.ModeKind c02 = (CollisionUtil.ModeKind)map.get(blockPos2);
            return c02 != null ? c02.floatValue : collisionUtil.m_1_float(blockPos2);
         }

         @Override
         public CollisionUtil.ModeKind2 m_7_C1(BlockPos blockPos3) {
            CollisionUtil.ModeKind c03 = (CollisionUtil.ModeKind)map.get(blockPos3);
            return c03 != null ? c03.c1 : collisionUtil.m_7_C1(blockPos3);
         }
      };
   }

   enum ModeKind {
      AIR(Shapes.empty(), Float.intBitsToFloat((int)80250925972717568L), CollisionUtil.ModeKind2.AIR),
      FLUID(Shapes.empty(), Float.intBitsToFloat((int)-8435394924470861824L), CollisionUtil.ModeKind2.FLUID),
      OBSIDIAN(Shapes.block(), Float.intBitsToFloat((int)2395885543731101696L), CollisionUtil.ModeKind2.BASE);

      public final VoxelShape voxelShape;
      public final float floatValue;
      public final CollisionUtil.ModeKind2 c1;

      ModeKind(VoxelShape voxelShape2, float floatValue2, CollisionUtil.ModeKind2 c12) {
         this.voxelShape = voxelShape2;
         this.floatValue = floatValue2;
         this.c1 = c12;
      }

      private static String a(byte[] byteArray) {
         int value = 0;
         int value2;
         char[] charArray = new char[value2 = byteArray.length];

         for (int i = 0; i < value2; i++) {
            int value3;
            if ((value3 = 255 & byteArray[i]) < 192) {
               charArray[value++] = (char)value3;
            } else if (value3 < 224) {
               char ch = (char)((char)(value3 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value++] = ch;
            } else if (i < value2 - 2) {
               char ch2 = (char)((char)(value3 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value++] = ch2;
            }
         }

         return new String(charArray, 0, value);
      }
   }

   enum ModeKind2 {
      AIR,
      REPLACEABLE,
      FLUID,
      WEB,
      BASE,
      SOLID,
      DECOR;

      private static int value;

      public boolean m_7_boolean() {
         return this == AIR || this == REPLACEABLE || this == FLUID;
      }

      public boolean m_3_boolean() {
         return this == BASE || this == SOLID || this == WEB;
      }

      public boolean m_6_boolean() {
         return this != AIR && this != FLUID && this != WEB;
      }

      public static void m_4_void(int value2) {
         value = value2;
      }

      public static int m_5_int() {
         return value;
      }

      public static int m_9_int() {
         return 0;
      }

      private static String a(byte[] byteArray) {
         int value2 = 0;
         int value3;
         char[] charArray = new char[value3 = byteArray.length];

         for (int i = 0; i < value3; i++) {
            int value4;
            if ((value4 = 255 & byteArray[i]) < 192) {
               charArray[value2++] = (char)value4;
            } else if (value4 < 224) {
               char ch = (char)((char)(value4 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value2++] = ch;
            } else if (i < value3 - 2) {
               char ch2 = (char)((char)(value4 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value2++] = ch2;
            }
         }

         return new String(charArray, 0, value2);
      }
   }
}
