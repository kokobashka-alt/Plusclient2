package wtf.onetap.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BoundingBoxUtil implements MinecraftAccess {
   private BoundingBoxUtil() {
   }

   public static Vec3 m_8_Vec3(LivingEntity livingEntity) {
      ChatUtil.m_3_IArr();
      AABB aABB = livingEntity.getBoundingBox();
      double doubleValue = aABB.maxX - aABB.minX;
      double doubleValue2 = aABB.maxY - aABB.minY;
      double doubleValue3 = aABB.maxZ - aABB.minZ;
      boolean flag = livingEntity.isFallFlying();
      double doubleValue4 = System.currentTimeMillis() / 86.0;
      int value = livingEntity.getId();
      return new Vec3(
         aABB.minX + doubleValue / 2.0 + Math.sin(doubleValue4 + value) * (doubleValue * 0.45),
         aABB.minY + doubleValue2 * (flag ? 0.85 : 0.725) + Math.cos(doubleValue4 * 0.8 + value) * (doubleValue2 * (flag ? 0.1F : 0.2F)),
         aABB.minZ + doubleValue3 / 2.0 + Math.cos(doubleValue4 * 1.2 + value) * (doubleValue3 * 0.45)
      );
   }

   public static Vec3 m_9_Vec3(LivingEntity livingEntity) {
      double doubleValue = livingEntity.tickCount * 0.65;
      double doubleValue2 = livingEntity.getBbWidth() * 0.5;
      return livingEntity.position().add(Math.sin(doubleValue) * doubleValue2, livingEntity.getBbHeight() * (0.55 + Math.cos(doubleValue * 0.5) * 0.0), Math.cos(doubleValue) * doubleValue2);
   }
}
