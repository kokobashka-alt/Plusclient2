package baritone.api.pathing.movement;

public interface ActionCosts {
   double[] FALL_N_BLOCKS_COST = generateFallNBlocksCost();
   double FALL_1_25_BLOCKS_COST = distanceToTicks(Double.longBitsToDouble(4608308318706860032L));
   double FALL_0_25_BLOCKS_COST = distanceToTicks(Double.longBitsToDouble(4598175219545276416L));
   double JUMP_ONE_BLOCK_COST = FALL_1_25_BLOCKS_COST - FALL_0_25_BLOCKS_COST;

   static double[] generateFallNBlocksCost() {
      double[] var0 = new double[4097];

      for (int var1 = 0; var1 < 4097; var1++) {
         var0[var1] = distanceToTicks(var1);
      }

      return var0;
   }

   static double velocity(int var0) {
      return (Math.pow(0.98, var0) - 1.0) * -3.92;
   }

   static double oldFormula(double var0) {
      return -3.92 * (99.0 - 49.5 * (Math.pow(0.98, var0) + 1.0) - var0);
   }

   static double distanceToTicks(double var0) {
      if (var0 == 0.0) {
         return 0.0;
      }

      double var2 = var0;
      int var4 = 0;

      while (true) {
         double var5 = velocity(var4);
         if (var2 <= var5) {
            return var4 + var2 / var5;
         }

         var2 -= var5;
         var4++;
      }
   }
}
