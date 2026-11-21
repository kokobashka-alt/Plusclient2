package baritone.utils.pathing;

import net.minecraft.world.level.border.WorldBorder;

public class BetterWorldBorder {
   private final double minX;
   private final double maxX;
   private final double minZ;
   private final double maxZ;

   public BetterWorldBorder(WorldBorder var1) {
      this.minX = var1.getMinX();
      this.maxX = var1.getMaxX();
      this.minZ = var1.getMinZ();
      this.maxZ = var1.getMaxZ();
   }

   public boolean entirelyContains(int var1, int var2) {
      return var1 + 1 > this.minX && var1 < this.maxX && var2 + 1 > this.minZ && var2 < this.maxZ;
   }

   public boolean canPlaceAt(int var1, int var2) {
      return var1 > this.minX && var1 + 1 < this.maxX && var2 > this.minZ && var2 + 1 < this.maxZ;
   }
}
