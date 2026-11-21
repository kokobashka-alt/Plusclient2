package baritone.utils.pathing;

public enum PathingBlockType {
   AIR(0),
   WATER(1),
   AVOID(2),
   SOLID(3);

   private final boolean[] bits;

   PathingBlockType(int var3) {
      this.bits = new boolean[]{(var3 & 2) != 0, (var3 & 1) != 0};
   }

   public final boolean[] getBits() {
      return this.bits;
   }

   public static PathingBlockType fromBits(boolean var0, boolean var1) {
      return var0 ? (var1 ? SOLID : AVOID) : (var1 ? WATER : AIR);
   }
}
