package wtf.onetap.combat.crystal;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import wtf.onetap.Onetap;
import wtf.onetap.mixin.client.BlockStatePredictionHandlerAccessor;
import wtf.onetap.mixin.client.ClientLevelAccessor;
import wtf.onetap.module.misc.NoInteract;
import wtf.onetap.util.MinecraftAccess;

public class CrystalInteraction implements MinecraftAccess {
   private static final double[][] DOUBLE_VALUE_ARRAY = new double[][]{
      {Double.longBitsToDouble(4602678819172646912L), Double.longBitsToDouble(4602678819172646912L)},
      {Double.longBitsToDouble(4598175219545276416L), Double.longBitsToDouble(4602678819172646912L)},
      {Double.longBitsToDouble(4604930618986332160L), Double.longBitsToDouble(4602678819172646912L)},
      {Double.longBitsToDouble(4602678819172646912L), Double.longBitsToDouble(4598175219545276416L)},
      {Double.longBitsToDouble(4602678819172646912L), Double.longBitsToDouble(4604930618986332160L)}
   };
   private static int value;

   private CrystalInteraction() {
   }

   public static CrystalInteraction.DataRecord m_6(BlockPos blockPos, Vec3 vec3, double doubleValue) {
      if (!CrystalPlacer.isObsidianOrBedrock(blockPos)) {
         return null;
      }

      VoxelShape voxelShape = MINECRAFT.level.getBlockState(blockPos).getShape(MINECRAFT.level, blockPos);
      CrystalInteraction.DataRecord c2 = m_6(voxelShape, blockPos, Direction.UP, vec3, clampInteractionRange(doubleValue));
      return c2 != null ? c2 : m_8(voxelShape, blockPos, vec3, clampInteractionRange(doubleValue));
   }

   public static boolean m_8(BlockPos blockPos, BlockHitResult blockHitResult, ItemStack itemStack) {
      if (blockHitResult != null && MINECRAFT.player != null && MINECRAFT.level != null && !itemStack.isEmpty()) {
         BlockPlaceContext blockPlaceContext = new BlockPlaceContext(MINECRAFT.player, InteractionHand.MAIN_HAND, itemStack, blockHitResult);
         return blockPlaceContext.getClickedPos().equals(blockPos) && blockPlaceContext.canPlace();
      } else {
         return false;
      }
   }

   public static BlockHitResult m_2(CrystalInteraction.DataRecord c2, double doubleValue, Vec3 vec3, float floatValue, float floatValue2) {
      if (c2 != null && MINECRAFT.player != null && MINECRAFT.level != null) {
         double doubleValue2 = clampInteractionRange(doubleValue);
         if (doubleValue2 <= 0.0) {
            return null;
         }

         Vec3 vec32 = Vec3.directionFromRotation(floatValue2, floatValue);
         BlockPos blockPos = c2.m_5b();
         BlockHitResult blockHitResult = MINECRAFT.level.getBlockState(blockPos).getShape(MINECRAFT.level, blockPos).clip(vec3, vec3.add(vec32.scale(doubleValue2)), blockPos);
         return m_7(c2, blockHitResult) && vec3.distanceToSqr(blockHitResult.getLocation()) <= doubleValue2 * doubleValue2 ? blockHitResult : null;
      } else {
         return null;
      }
   }

   public static double clampInteractionRange(double doubleValue) {
      return MINECRAFT.player == null ? 0.0 : Math.max(0.0, Math.min(doubleValue, MINECRAFT.player.blockInteractionRange()));
   }

   public static int getTimeoutTicks() {
      return MINECRAFT.player != null && MINECRAFT.getConnection() != null ? Mth.clamp(12 + getPing() * 2, 12, 60) : 20;
   }

   public static int getPing() {
      if (MINECRAFT.player != null && MINECRAFT.getConnection() != null) {
         PlayerInfo playerInfo = MINECRAFT.getConnection().getPlayerInfo(MINECRAFT.player.getUUID());
         int value2 = playerInfo == null ? 0 : Math.max(0, playerInfo.getLatency());
         return (value2 + 49) / 50;
      } else {
         return 0;
      }
   }

   public static CrystalInteraction.DataRecord m_6(BlockPos blockPos, Vec3 vec3, double doubleValue, boolean flag) {
      if (MINECRAFT.level != null && MINECRAFT.player != null && MINECRAFT.level.getBlockState(blockPos).canBeReplaced()) {
         CrystalInteraction.DataRecord c2 = null;
         double doubleValue2 = Double.MAX_VALUE;
         ItemStack itemStack = (flag ? Items.COBWEB : Items.OBSIDIAN).getDefaultInstance();

         for (Direction direction : Direction.values()) {
            BlockPos blockPos2 = blockPos.relative(direction.getOpposite());
            BlockState blockState = MINECRAFT.level.getBlockState(blockPos2);
            if (!blockState.canBeReplaced() && !blockState.isAir() && (flag || !blockState.is(Blocks.COBWEB)) && !m_3_boolean(blockPos2)) {
               CrystalInteraction.DataRecord c22 = m_4(blockPos2, direction, vec3, clampInteractionRange(doubleValue));
               if (c22 != null && m_3(blockPos, c22, itemStack) && vec3.distanceToSqr(c22.m_7a()) < doubleValue2) {
                  c2 = c22;
                  doubleValue2 = vec3.distanceToSqr(c22.m_7a());
               }
            }
         }

         return c2;
      } else {
         return null;
      }
   }

   public static CrystalInteraction.DataRecord m_9(BlockPos blockPos, Vec3 vec3, double doubleValue) {
      return MINECRAFT.level != null && MINECRAFT.player != null && !m_5_boolean(blockPos)
         ? m_8(MINECRAFT.level.getBlockState(blockPos).getShape(MINECRAFT.level, blockPos), blockPos, vec3, clampInteractionRange(doubleValue))
         : null;
   }

   private static CrystalInteraction.DataRecord m_8(VoxelShape voxelShape, BlockPos blockPos, Vec3 vec3, double doubleValue) {
      CrystalInteraction.DataRecord c2 = null;
      double doubleValue2 = Double.MAX_VALUE;

      for (Direction direction : Direction.values()) {
         CrystalInteraction.DataRecord c22 = m_6(voxelShape, blockPos, direction, vec3, doubleValue);
         if (c22 != null && vec3.distanceToSqr(c22.m_7a()) < doubleValue2) {
            c2 = c22;
            doubleValue2 = vec3.distanceToSqr(c22.m_7a());
         }
      }

      return c2;
   }

   public static boolean m_3(BlockPos blockPos, CrystalInteraction.DataRecord c2, ItemStack itemStack) {
      return c2 != null && m_8(blockPos, new BlockHitResult(c2.m_7a(), c2.m_6h_Direction(), c2.m_5b(), c2.m_6_p()), itemStack);
   }

   public static boolean m_5_boolean(BlockPos blockPos) {
      return MINECRAFT.level != null
         && ((BlockStatePredictionHandlerAccessor)((ClientLevelAccessor)MINECRAFT.level).onetap$getBlockStatePredictionHandler())
            .onetap$getServerVerifiedStates()
            .containsKey(blockPos.asLong());
   }

   private static boolean m_7(CrystalInteraction.DataRecord c2, BlockHitResult blockHitResult) {
      return blockHitResult != null && blockHitResult.getType() == Type.BLOCK && blockHitResult.getBlockPos().equals(c2.m_5b()) && blockHitResult.getDirection() == c2.m_6h_Direction();
   }

   private static CrystalInteraction.DataRecord m_4(BlockPos blockPos, Direction direction, Vec3 vec3, double doubleValue) {
      return m_5_boolean(blockPos) ? null : m_6(MINECRAFT.level.getBlockState(blockPos).getShape(MINECRAFT.level, blockPos), blockPos, direction, vec3, doubleValue);
   }

   private static CrystalInteraction.DataRecord m_6(VoxelShape voxelShape, BlockPos blockPos, Direction direction, Vec3 vec3, double doubleValue) {
      if (voxelShape.isEmpty()) {
         return null;
      }

      AABB aABB = voxelShape.bounds().move(blockPos);
      if (aABB.contains(vec3)) {
         Vec3 vec32 = vec3.add(-direction.getStepX() * 0.1, -direction.getStepY() * 0.1, -direction.getStepZ() * 0.1);
         BlockHitResult blockHitResult = voxelShape.clip(vec3, vec32, blockPos);
         if (blockHitResult != null && blockHitResult.isInside() && blockHitResult.getDirection() == direction && vec3.distanceToSqr(blockHitResult.getLocation()) <= doubleValue * doubleValue) {
            return new CrystalInteraction.DataRecord(blockPos.immutable(), direction, blockHitResult.getLocation(), true);
         }
      }
      boolean flag = switch (direction) {
         case UP -> vec3.y >= aABB.maxY - 1.0E-7;
         case DOWN -> vec3.y <= aABB.minY + 1.0E-7;
         case EAST -> vec3.x >= aABB.maxX - 1.0E-7;
         case WEST -> vec3.x <= aABB.minX + 1.0E-7;
         case SOUTH -> vec3.z >= aABB.maxZ - 1.0E-7;
         case NORTH -> vec3.z <= aABB.minZ + 1.0E-7;
      };
      if (!aABB.contains(vec3) && !flag) {
         return null;
      }

      for (AABB aABB2 : voxelShape.toAabbs()) {
         AABB aABB3 = aABB2.move(blockPos);

         for (double[] doubleArray : DOUBLE_VALUE_ARRAY) {
            Vec3 vec33 = switch (direction) {
               case UP -> new Vec3(Mth.lerp(doubleArray[0], aABB3.minX, aABB3.maxX), aABB3.maxY, Mth.lerp(doubleArray[1], aABB3.minZ, aABB3.maxZ));
               case DOWN -> new Vec3(Mth.lerp(doubleArray[0], aABB3.minX, aABB3.maxX), aABB3.minY, Mth.lerp(doubleArray[1], aABB3.minZ, aABB3.maxZ));
               case EAST -> new Vec3(aABB3.maxX, Mth.lerp(doubleArray[1], aABB3.minY, aABB3.maxY), Mth.lerp(doubleArray[0], aABB3.minZ, aABB3.maxZ));
               case WEST -> new Vec3(aABB3.minX, Mth.lerp(doubleArray[1], aABB3.minY, aABB3.maxY), Mth.lerp(doubleArray[0], aABB3.minZ, aABB3.maxZ));
               case SOUTH -> new Vec3(Mth.lerp(doubleArray[0], aABB3.minX, aABB3.maxX), Mth.lerp(doubleArray[1], aABB3.minY, aABB3.maxY), aABB3.maxZ);
               case NORTH -> new Vec3(Mth.lerp(doubleArray[0], aABB3.minX, aABB3.maxX), Mth.lerp(doubleArray[1], aABB3.minY, aABB3.maxY), aABB3.minZ);
            };
            Vec3 vec34 = vec33.subtract(vec3);
            if (!(vec34.lengthSqr() < 1.0E-10) && !(vec34.lengthSqr() > doubleValue * doubleValue)) {
               BlockHitResult blockHitResult2 = voxelShape.clip(vec3, vec33.add(vec34.normalize().scale(0.001)), blockPos);
               if (blockHitResult2 != null
                  && blockHitResult2.getDirection() == direction
                  && vec3.distanceToSqr(blockHitResult2.getLocation()) <= doubleValue * doubleValue
                  && (blockHitResult2.isInside() || blockHitResult2.getLocation().distanceToSqr(vec33) <= 1.0E-6)) {
                  return new CrystalInteraction.DataRecord(blockPos.immutable(), direction, blockHitResult2.getLocation(), blockHitResult2.isInside());
               }
            }
         }
      }

      return null;
   }

   private static boolean m_3_boolean(BlockPos blockPos) {
      if (MINECRAFT.player.isSecondaryUseActive()) {
         return false;
      }

      BlockState blockState = MINECRAFT.level.getBlockState(blockPos);
      Block block = blockState.getBlock();
      NoInteract noInteract = Onetap.MODULE_MANAGER.get(NoInteract.class);
      return blockState.getMenuProvider(MINECRAFT.level, blockPos) != null
         || block instanceof DoorBlock
         || block instanceof TrapDoorBlock
         || block instanceof FenceGateBlock
         || block instanceof ButtonBlock
         || block instanceof BedBlock
         || block instanceof LeverBlock
         || block instanceof CakeBlock
         || block instanceof NoteBlock
         || block instanceof RespawnAnchorBlock
         || blockState.is(Blocks.ENDER_CHEST)
         || blockState.is(Blocks.JUKEBOX)
         || noInteract != null && noInteract.isEnabled() && block instanceof FenceBlock;
   }

   static {
      m_6_void(90);
   }

   public static void m_6_void(int value2) {
      value = value2;
   }

   public static int m_4_int() {
      return value;
   }

   public static int m_1_int() {
      return 0;
   }

   public record DataRecord(BlockPos f_5b, Direction f_6h, Vec3 f_7a, boolean f_6_p) {
      private static int[] f_5;

      public BlockPos m_5b() {
         return this.f_5b;
      }

      public Direction m_6h_Direction() {
         return this.f_6h;
      }

      public Vec3 m_7a() {
         return this.f_7a;
      }

      public boolean m_6_p() {
         return this.f_6_p;
      }

      public static void m_3(int[] intArray) {
         f_5 = intArray;
      }

      public static int[] m_2_IArr() {
         return f_5;
      }
   }
}
