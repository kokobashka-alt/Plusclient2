package wtf.onetap.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AimPointUtil implements MinecraftAccess {
   private static String[] textArray;

   private AimPointUtil() {
   }

   public static Vec3 m_5(Entity entity, double doubleValue) {
      return m_5(entity, doubleValue, 0.1);
   }

   public static Vec3 m_5(Entity entity, double doubleValue, double doubleValue2) {
      m_9_StringArr();
      AABB aABB = entity.getBoundingBox();
      Vec3 vec3 = null;
      double doubleValue3 = Double.MAX_VALUE;
      double doubleValue4 = aABB.minX + doubleValue;

      while (doubleValue4 <= aABB.maxX - doubleValue) {
         for (double doubleValue5 = aABB.minY + doubleValue; doubleValue5 <= aABB.maxY - doubleValue; doubleValue5 += doubleValue2) {
            for (double doubleValue6 = aABB.minZ + doubleValue; doubleValue6 <= aABB.maxZ - doubleValue; doubleValue6 += doubleValue2) {
               Vec3 vec32 = new Vec3(doubleValue4, doubleValue5, doubleValue6);
               double doubleValue7 = MinecraftAccess.MINECRAFT.player.getEyePosition().distanceTo(vec32);
               if (doubleValue7 < doubleValue3) {
                  doubleValue3 = doubleValue7;
                  vec3 = vec32;
               }
            }
         }

         doubleValue4 += doubleValue2;
      }

      return vec3 != null ? vec3 : entity.position();
   }

   public static Vec3 closestPointOnBox(AABB aABB, double doubleValue) {
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      double doubleValue2 = Math.max(aABB.minX + doubleValue, Math.min(vec3.x, aABB.maxX - doubleValue));
      double doubleValue3 = Math.max(aABB.minY + doubleValue, Math.min(vec3.y, aABB.maxY - doubleValue));
      double doubleValue4 = Math.max(aABB.minZ + doubleValue, Math.min(vec3.z, aABB.maxZ - doubleValue));
      return new Vec3(doubleValue2, doubleValue3, doubleValue4);
   }

   public static void m_7(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_9_StringArr() {
      return textArray;
   }

   static {
      m_7(new String[4]);
   }
}
