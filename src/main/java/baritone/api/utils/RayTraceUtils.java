package baritone.api.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RayTraceUtils {
   private RayTraceUtils() {
   }

   public static HitResult rayTraceTowards(Entity var0, Rotation var1, double var2) {
      return rayTraceTowards(var0, var1, var2, false);
   }

   public static HitResult rayTraceTowards(Entity var0, Rotation var1, double var2, boolean var4) {
      Vec3 var5;
      if (var4) {
         var5 = inferSneakingEyePosition(var0);
      } else {
         var5 = var0.getEyePosition(1.0F);
      }

      Vec3 var6 = RotationUtils.calcLookDirectionFromRotation(var1);
      Vec3 var7 = var5.add(var6.x * var2, var6.y * var2, var6.z * var2);
      return var0.level().clip(new ClipContext(var5, var7, Block.OUTLINE, Fluid.NONE, var0));
   }

   public static Vec3 inferSneakingEyePosition(Entity var0) {
      return new Vec3(var0.getX(), var0.getY() + var0.getEyeHeight(Pose.CROUCHING), var0.getZ());
   }
}
