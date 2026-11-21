package baritone.api.utils;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import java.util.Optional;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RotationUtils {
   private static final Vec3[] BLOCK_SIDE_MULTIPLIERS = new Vec3[]{
      new Vec3(Double.longBitsToDouble(4602678819172646912L), 0.0, Double.longBitsToDouble(4602678819172646912L)),
      new Vec3(Double.longBitsToDouble(4602678819172646912L), 1.0, Double.longBitsToDouble(4602678819172646912L)),
      new Vec3(Double.longBitsToDouble(4602678819172646912L), Double.longBitsToDouble(4602678819172646912L), 0.0),
      new Vec3(Double.longBitsToDouble(4602678819172646912L), Double.longBitsToDouble(4602678819172646912L), 1.0),
      new Vec3(0.0, Double.longBitsToDouble(4602678819172646912L), Double.longBitsToDouble(4602678819172646912L)),
      new Vec3(1.0, Double.longBitsToDouble(4602678819172646912L), Double.longBitsToDouble(4602678819172646912L))
   };

   private RotationUtils() {
   }

   public static Rotation calcRotationFromCoords(BlockPos var0, BlockPos var1) {
      return calcRotationFromVec3d(new Vec3(var0.getX(), var0.getY(), var0.getZ()), new Vec3(var1.getX(), var1.getY(), var1.getZ()));
   }

   public static Rotation wrapAnglesToRelative(Rotation var0, Rotation var1) {
      return var0.yawIsReallyClose(var1) ? new Rotation(var0.getYaw(), var1.getPitch()) : var1.subtract(var0).normalize().add(var0);
   }

   public static Rotation calcRotationFromVec3d(Vec3 var0, Vec3 var1, Rotation var2) {
      return wrapAnglesToRelative(var2, calcRotationFromVec3d(var0, var1));
   }

   private static Rotation calcRotationFromVec3d(Vec3 var0, Vec3 var1) {
      double[] var2 = new double[]{var0.x - var1.x, var0.y - var1.y, var0.z - var1.z};
      double var3 = Mth.atan2(var2[0], -var2[2]);
      double var5 = Math.sqrt(var2[0] * var2[0] + var2[2] * var2[2]);
      double var7 = Mth.atan2(var2[1], var5);
      return new Rotation((float)(var3 * (180.0 / Math.PI)), (float)(var7 * (180.0 / Math.PI)));
   }

   public static Vec3 calcLookDirectionFromRotation(Rotation var0) {
      float var1 = Mth.cos(-var0.getYaw() * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var2 = Mth.sin(-var0.getYaw() * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var3 = -Mth.cos(-var0.getPitch() * (float) (Math.PI / 180.0));
      float var4 = Mth.sin(-var0.getPitch() * (float) (Math.PI / 180.0));
      return new Vec3(var2 * var3, var4, var1 * var3);
   }

   @Deprecated
   public static Vec3 calcVec3dFromRotation(Rotation var0) {
      return calcLookDirectionFromRotation(var0);
   }

   public static Optional<Rotation> reachable(IPlayerContext var0, BlockPos var1) {
      return reachable(var0, var1, false);
   }

   public static Optional<Rotation> reachable(IPlayerContext var0, BlockPos var1, boolean var2) {
      return reachable(var0, var1, var0.playerController().getBlockReachDistance(), var2);
   }

   public static Optional<Rotation> reachable(IPlayerContext var0, BlockPos var1, double var2) {
      return reachable(var0, var1, var2, false);
   }

   public static Optional<Rotation> reachable(IPlayerContext var0, BlockPos var1, double var2, boolean var4) {
      if (var1 instanceof BetterBlockPos) {
         var1 = new BlockPos(var1.getX(), var1.getY(), var1.getZ());
      }

      if (BaritoneAPI.getSettings().remainWithExistingLookDirection.value && var0.isLookingAt(var1)) {
         Rotation var5 = var0.playerRotations().add(new Rotation(0.0F, 1.0E-4F));
         if (!var4) {
            return Optional.of(var5);
         }

         HitResult var6 = RayTraceUtils.rayTraceTowards(var0.player(), var5, var2, true);
         if (var6 != null && var6.getType() == Type.BLOCK && ((BlockHitResult)var6).getBlockPos().equals(var1)) {
            return Optional.of(var5);
         }
      }

      Optional var18 = reachableCenter(var0, var1, var2, var4);
      if (var18.isPresent()) {
         return var18;
      }

      BlockState var20 = var0.world().getBlockState(var1);
      VoxelShape var7 = var20.getShape(var0.world(), var1);
      if (var7.isEmpty()) {
         var7 = Shapes.block();
      }

      for (Vec3 var11 : BLOCK_SIDE_MULTIPLIERS) {
         double var12 = var7.min(Axis.X) * var11.x + var7.max(Axis.X) * (1.0 - var11.x);
         double var14 = var7.min(Axis.Y) * var11.y + var7.max(Axis.Y) * (1.0 - var11.y);
         double var16 = var7.min(Axis.Z) * var11.z + var7.max(Axis.Z) * (1.0 - var11.z);
         var18 = reachableOffset(var0, var1, new Vec3(var1.getX(), var1.getY(), var1.getZ()).add(var12, var14, var16), var2, var4);
         if (var18.isPresent()) {
            return var18;
         }
      }

      return Optional.empty();
   }

   public static Optional<Rotation> reachableOffset(IPlayerContext var0, BlockPos var1, Vec3 var2, double var3, boolean var5) {
      Vec3 var6 = var5 ? RayTraceUtils.inferSneakingEyePosition(var0.player()) : var0.player().getEyePosition(1.0F);
      Rotation var7 = calcRotationFromVec3d(var6, var2, var0.playerRotations());
      Rotation var8 = BaritoneAPI.getProvider().getBaritoneForPlayer(var0.player()).getLookBehavior().getAimProcessor().peekRotation(var7);
      HitResult var9 = RayTraceUtils.rayTraceTowards(var0.player(), var8, var3, var5);
      if (var9 != null && var9.getType() == Type.BLOCK) {
         if (((BlockHitResult)var9).getBlockPos().equals(var1)) {
            return Optional.of(var7);
         }

         if (var0.world().getBlockState(var1).getBlock() instanceof BaseFireBlock && ((BlockHitResult)var9).getBlockPos().equals(var1.below())) {
            return Optional.of(var7);
         }
      }

      return Optional.empty();
   }

   public static Optional<Rotation> reachableCenter(IPlayerContext var0, BlockPos var1, double var2, boolean var4) {
      return reachableOffset(var0, var1, VecUtils.calculateBlockCenter(var0.world(), var1), var2, var4);
   }

   @Deprecated
   public static Optional<Rotation> reachable(LocalPlayer var0, BlockPos var1, double var2) {
      return reachable(var0, var1, var2, false);
   }

   @Deprecated
   public static Optional<Rotation> reachable(LocalPlayer var0, BlockPos var1, double var2, boolean var4) {
      IBaritone var5 = BaritoneAPI.getProvider().getBaritoneForPlayer(var0);
      IPlayerContext var6 = var5.getPlayerContext();
      return reachable(var6, var1, var2, var4);
   }

   @Deprecated
   public static Optional<Rotation> reachableOffset(Entity var0, BlockPos var1, Vec3 var2, double var3, boolean var5) {
      Vec3 var6 = var5 ? RayTraceUtils.inferSneakingEyePosition(var0) : var0.getEyePosition(1.0F);
      Rotation var7 = calcRotationFromVec3d(var6, var2, new Rotation(var0.getYRot(), var0.getXRot()));
      HitResult var8 = RayTraceUtils.rayTraceTowards(var0, var7, var3, var5);
      if (var8 != null && var8.getType() == Type.BLOCK) {
         if (((BlockHitResult)var8).getBlockPos().equals(var1)) {
            return Optional.of(var7);
         }

         if (var0.level().getBlockState(var1).getBlock() instanceof BaseFireBlock && ((BlockHitResult)var8).getBlockPos().equals(var1.below())) {
            return Optional.of(var7);
         }
      }

      return Optional.empty();
   }

   @Deprecated
   public static Optional<Rotation> reachableCenter(Entity var0, BlockPos var1, double var2, boolean var4) {
      return reachableOffset(var0, var1, VecUtils.calculateBlockCenter(var0.level(), var1), var2, var4);
   }
}
