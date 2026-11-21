package baritone.cache;

import baritone.Baritone;
import baritone.api.cache.ICachedWorld;
import baritone.api.cache.IWaypointCollection;
import baritone.api.cache.IWorldData;
import java.nio.file.Path;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class WorldData implements IWorldData {
   public final CachedWorld cache;
   private final WaypointCollection waypoints;
   public final Path directory;
   public final DimensionType dimension;

   WorldData(Path var1, DimensionType var2, ResourceKey<Level> var3) {
      this.directory = var1;
      this.cache = new CachedWorld(var1.resolve("cache"), var2, var3);
      this.waypoints = new WaypointCollection(var1.resolve("waypoints"));
      this.dimension = var2;
   }

   public void onClose() {
      Baritone.getExecutor().execute(() -> {
         System.out.println("Started saving the world in a new thread");
         this.cache.save();
      });
   }

   @Override
   public ICachedWorld getCachedWorld() {
      return this.cache;
   }

   @Override
   public IWaypointCollection getWaypoints() {
      return this.waypoints;
   }
}
