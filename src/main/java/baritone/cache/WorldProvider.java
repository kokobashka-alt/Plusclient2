package baritone.cache;

import baritone.Baritone;
import baritone.api.cache.IWorldProvider;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.Tuple;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.lang3.SystemUtils;

public class WorldProvider implements IWorldProvider {
   private static final Map<Path, WorldData> worldCache = new HashMap<>();
   private final Baritone baritone;
   private final IPlayerContext ctx;
   private WorldData currentWorld;
   private Level mcWorld;

   public WorldProvider(Baritone var1) {
      this.baritone = var1;
      this.ctx = var1.getPlayerContext();
   }

   public final WorldData getCurrentWorld() {
      this.detectAndHandleBrokenLoading();
      return this.currentWorld;
   }

   public final void initWorld(Level var1) {
      this.getSaveDirectories(var1).ifPresent(var2 -> {
         Path var3 = var2.getA();
         Path var4 = var2.getB();

         try {
            Files.createDirectories(var4);
            Files.write(var4.resolve("readme.txt"), "https://github.com/cabaletta/baritone\n".getBytes(StandardCharsets.US_ASCII));
         } catch (IOException var10) {
         }

         Path var5 = this.getWorldDataDirectory(var3, var1);

         try {
            Files.createDirectories(var5);
         } catch (IOException var9) {
         }

         System.out.println("Baritone world data dir: " + var5);
         synchronized (worldCache) {
            this.currentWorld = worldCache.computeIfAbsent(var5, var1xx -> new WorldData(var1xx, var1.dimensionType(), var1.dimension()));
         }

         this.mcWorld = this.ctx.world();
      });
   }

   public final void closeWorld() {
      WorldData var1 = this.currentWorld;
      this.currentWorld = null;
      this.mcWorld = null;
      if (var1 != null) {
         var1.onClose();
      }
   }

   private Path getWorldDataDirectory(Path var1, Level var2) {
      Identifier var3 = var2.dimension().identifier();
      int var4 = var2.dimensionType().logicalHeight();
      return var1.resolve(var3.getNamespace()).resolve(var3.getPath() + "_" + var4);
   }

   private Optional<Tuple<Path, Path>> getSaveDirectories(Level var1) {
      Path var3;
      Path var6;
      if (this.ctx.minecraft().hasSingleplayerServer()) {
         var6 = this.ctx.minecraft().getSingleplayerServer().getWorldPath(LevelResource.ROOT);
         if (var6.relativize(this.ctx.minecraft().gameDirectory.toPath()).getNameCount() != 2) {
            var6 = var6.getParent();
         }

         var6 = var6.resolve("baritone");
         var3 = var6;
      } else {
         ServerData var5 = this.ctx.minecraft().getCurrentServer();
         if (var5 == null) {
            System.out.println("World seems to be a replay. Not loading Baritone cache.");
            this.currentWorld = null;
            this.mcWorld = this.ctx.world();
            return Optional.empty();
         }

         String var4 = var5.isRealm() ? "realms" : var5.ip;
         if (SystemUtils.IS_OS_WINDOWS) {
            var4 = var4.replace(":", "_");
         }

         var6 = this.baritone.getDirectory().resolve(var4);
         var3 = this.baritone.getDirectory();
      }

      return Optional.of(new Tuple<>(var6, var3));
   }

   private void detectAndHandleBrokenLoading() {
      if (this.mcWorld != this.ctx.world()) {
         if (this.currentWorld != null) {
            System.out.println("mc.world unloaded unnoticed! Unloading Baritone cache now.");
            this.closeWorld();
         }

         if (this.ctx.world() != null) {
            System.out.println("mc.world loaded unnoticed! Loading Baritone cache now.");
            this.initWorld(this.ctx.world());
         }
      } else if (this.currentWorld == null
         && this.ctx.world() != null
         && (this.ctx.minecraft().hasSingleplayerServer() || this.ctx.minecraft().getCurrentServer() != null)) {
         System.out.println("Retrying to load Baritone cache");
         this.initWorld(this.ctx.world());
      }
   }
}
