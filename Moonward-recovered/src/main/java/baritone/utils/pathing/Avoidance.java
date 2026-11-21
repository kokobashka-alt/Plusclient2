package baritone.utils.pathing;

import baritone.Baritone;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.IPlayerContext;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

public class Avoidance {
   private final int centerX;
   private final int centerY;
   private final int centerZ;
   private final double coefficient;
   private final int radius;
   private final int radiusSq;

   public Avoidance(BlockPos var1, double var2, int var4) {
      this(var1.getX(), var1.getY(), var1.getZ(), var2, var4);
   }

   public Avoidance(int var1, int var2, int var3, double var4, int var6) {
      this.centerX = var1;
      this.centerY = var2;
      this.centerZ = var3;
      this.coefficient = var4;
      this.radius = var6;
      this.radiusSq = var6 * var6;
   }

   public double coefficient(int var1, int var2, int var3) {
      int var4 = var1 - this.centerX;
      int var5 = var2 - this.centerY;
      int var6 = var3 - this.centerZ;
      return var4 * var4 + var5 * var5 + var6 * var6 <= this.radiusSq ? this.coefficient : 1.0;
   }

   public static List<Avoidance> create(IPlayerContext var0) {
      if (!Baritone.settings().avoidance.value) {
         return Collections.emptyList();
      }

      ArrayList var1 = new ArrayList();
      double var2 = Baritone.settings().mobSpawnerAvoidanceCoefficient.value;
      double var4 = Baritone.settings().mobAvoidanceCoefficient.value;
      if (var2 != 1.0) {
         var0.worldData()
            .getCachedWorld()
            .getLocationsOf("mob_spawner", 1, var0.playerFeet().x, var0.playerFeet().z, 2)
            .forEach(var3 -> var1.add(new Avoidance(var3, var2, Baritone.settings().mobSpawnerAvoidanceRadius.value)));
      }

      if (var4 != 1.0) {
         var0.entitiesStream()
            .filter(var0x -> var0x instanceof Mob)
            .filter(var1x -> !(var1x instanceof Spider) || var0.player().getLightLevelDependentMagicValue() < 0.5)
            .filter(var0x -> !(var0x instanceof ZombifiedPiglin) || ((ZombifiedPiglin)var0x).getLastHurtByMob() != null)
            .filter(var0x -> !(var0x instanceof EnderMan) || ((EnderMan)var0x).isCreepy())
            .forEach(var3 -> var1.add(new Avoidance(var3.blockPosition(), var4, Baritone.settings().mobAvoidanceRadius.value)));
      }

      return var1;
   }

   public void applySpherical(Long2DoubleOpenHashMap var1) {
      for (int var2 = -this.radius; var2 <= this.radius; var2++) {
         for (int var3 = -this.radius; var3 <= this.radius; var3++) {
            for (int var4 = -this.radius; var4 <= this.radius; var4++) {
               if (var2 * var2 + var3 * var3 + var4 * var4 <= this.radius * this.radius) {
                  long var5 = BetterBlockPos.longHash(this.centerX + var2, this.centerY + var3, this.centerZ + var4);
                  var1.put(var5, var1.get(var5) * this.coefficient);
               }
            }
         }
      }
   }
}
