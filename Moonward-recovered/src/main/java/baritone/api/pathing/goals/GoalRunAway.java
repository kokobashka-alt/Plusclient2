package baritone.api.pathing.goals;

import baritone.api.utils.SettingsUtil;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.core.BlockPos;

public class GoalRunAway implements Goal {
   private final BlockPos[] from;
   private final int distanceSq;
   private final Integer maintainY;

   public GoalRunAway(double var1, BlockPos... var3) {
      this(var1, null, var3);
   }

   public GoalRunAway(double var1, Integer var3, BlockPos... var4) {
      if (var4.length == 0) {
         throw new IllegalArgumentException("Positions to run away from must not be empty");
      }

      this.from = var4;
      this.distanceSq = (int)(var1 * var1);
      this.maintainY = var3;
   }

   @Override
   public boolean isInGoal(int var1, int var2, int var3) {
      if (this.maintainY != null && this.maintainY != var2) {
         return false;
      }

      for (BlockPos var7 : this.from) {
         int var8 = var1 - var7.getX();
         int var9 = var3 - var7.getZ();
         int var10 = var8 * var8 + var9 * var9;
         if (var10 < this.distanceSq) {
            return false;
         }
      }

      return true;
   }

   @Override
   public double heuristic(int var1, int var2, int var3) {
      double var4 = Double.MAX_VALUE;

      for (BlockPos var9 : this.from) {
         double var10 = GoalXZ.calculate(var9.getX() - var1, var9.getZ() - var3);
         if (var10 < var4) {
            var4 = var10;
         }
      }

      var4 = -var4;
      if (this.maintainY != null) {
         var4 = var4 * 0.6 + GoalYLevel.calculate(this.maintainY, var2) * 1.5;
      }

      return var4;
   }

   @Override
   public double heuristic() {
      int var1 = (int)Math.ceil(Math.sqrt(this.distanceSq));
      int var2 = Integer.MAX_VALUE;
      int var3 = Integer.MAX_VALUE;
      int var4 = Integer.MAX_VALUE;
      int var5 = Integer.MIN_VALUE;
      int var6 = Integer.MIN_VALUE;
      int var7 = Integer.MIN_VALUE;

      for (BlockPos var11 : this.from) {
         var2 = Math.min(var2, var11.getX() - var1);
         var3 = Math.min(var3, var11.getY() - var1);
         var4 = Math.min(var4, var11.getZ() - var1);
         var5 = Math.max(var2, var11.getX() + var1);
         var6 = Math.max(var3, var11.getY() + var1);
         var7 = Math.max(var4, var11.getZ() + var1);
      }

      DoubleOpenHashSet var16 = new DoubleOpenHashSet();
      double var17 = Double.POSITIVE_INFINITY;

      for (int var18 = var2; var18 <= var5; var18++) {
         for (int var12 = var3; var12 <= var6; var12++) {
            for (int var13 = var4; var13 <= var7; var13++) {
               double var14 = this.heuristic(var18, var12, var13);
               if (var14 < var17 && this.isInGoal(var18, var12, var13)) {
                  var16.add(var14);
               } else {
                  var17 = Math.min(var17, var14);
               }
            }
         }
      }

      double var19 = Double.NEGATIVE_INFINITY;
      DoubleIterator var20 = var16.iterator();

      while (var20.hasNext()) {
         double var21 = var20.nextDouble();
         if (var21 < var17) {
            var19 = Math.max(var19, var21);
         }
      }

      return var19;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         GoalRunAway var2 = (GoalRunAway)var1;
         return this.distanceSq == var2.distanceSq && Arrays.equals(this.from, var2.from) && Objects.equals(this.maintainY, var2.maintainY);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = Arrays.hashCode(this.from);
      var1 = var1 * 1196803141 + this.distanceSq;
      return var1 * -2053788840 + this.maintainY;
   }

   @Override
   public String toString() {
      return this.maintainY != null
         ? String.format("GoalRunAwayFromMaintainY y=%s, %s", SettingsUtil.maybeCensor(this.maintainY), Arrays.asList(this.from))
         : "GoalRunAwayFrom" + Arrays.<BlockPos>asList(this.from);
   }
}
