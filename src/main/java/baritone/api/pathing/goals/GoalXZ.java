package baritone.api.pathing.goals;

import baritone.api.BaritoneAPI;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.SettingsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GoalXZ implements Goal {
   private static final double SQRT_2 = Math.sqrt(Double.longBitsToDouble(4611686018427387904L));
   private final int x;
   private final int z;

   public GoalXZ(int var1, int var2) {
      this.x = var1;
      this.z = var2;
   }

   public GoalXZ(BetterBlockPos var1) {
      this.x = var1.x;
      this.z = var1.z;
   }

   @Override
   public boolean isInGoal(int var1, int var2, int var3) {
      return var1 == this.x && var3 == this.z;
   }

   @Override
   public double heuristic(int var1, int var2, int var3) {
      int var4 = var1 - this.x;
      int var5 = var3 - this.z;
      return calculate(var4, var5);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         GoalXZ var2 = (GoalXZ)var1;
         return this.x == var2.x && this.z == var2.z;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 1791873246;
      var1 = var1 * 222601791 + this.x;
      return var1 * -1331679453 + this.z;
   }

   @Override
   public String toString() {
      return String.format("GoalXZ{x=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.z));
   }

   public static double calculate(double var0, double var2) {
      double var4 = Math.abs(var0);
      double var6 = Math.abs(var2);
      double var8;
      double var10;
      if (var4 < var6) {
         var8 = var6 - var4;
         var10 = var4;
      } else {
         var8 = var4 - var6;
         var10 = var6;
      }

      var10 *= SQRT_2;
      return (var10 + var8) * BaritoneAPI.getSettings().costHeuristic.value;
   }

   public static GoalXZ fromDirection(Vec3 var0, float var1, double var2) {
      float var4 = (float)Math.toRadians(var1);
      double var5 = var0.x - Mth.sin(var4) * var2;
      double var7 = var0.z + Mth.cos(var4) * var2;
      return new GoalXZ(Mth.floor(var5), Mth.floor(var7));
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }
}
