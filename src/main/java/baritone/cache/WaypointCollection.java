package baritone.cache;

import baritone.api.cache.IWaypoint;
import baritone.api.cache.IWaypointCollection;
import baritone.api.cache.Waypoint;
import baritone.api.utils.BetterBlockPos;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WaypointCollection implements IWaypointCollection {
   private final Path directory;
   private final Map<IWaypoint.Tag, Set<IWaypoint>> waypoints;

   WaypointCollection(Path var1) {
      this.directory = var1;
      if (!Files.exists(var1)) {
         try {
            Files.createDirectories(var1);
         } catch (IOException var3) {
         }
      }

      System.out.println("Would save waypoints to " + var1);
      this.waypoints = new HashMap<>();
      this.load();
   }

   private void load() {
      for (IWaypoint.Tag var4 : IWaypoint.Tag.values()) {
         this.load(var4);
      }
   }

   private synchronized void load(IWaypoint.Tag var1) {
      this.waypoints.put(var1, new HashSet<>());
      Path var2 = this.directory.resolve(var1.name().toLowerCase() + ".mp4");
      if (Files.exists(var2)) {
         try (
            FileInputStream var3 = new FileInputStream(var2.toFile());
            BufferedInputStream var4 = new BufferedInputStream(var3);
            DataInputStream var5 = new DataInputStream(var4);
         ) {
            long var6 = var5.readLong();
            if (var6 != 121977993584L) {
               throw new IOException("Bad magic value " + var6);
            }

            long var8 = var5.readLong();

            while (var8-- > 0L) {
               String var10 = var5.readUTF();
               long var11 = var5.readLong();
               int var13 = var5.readInt();
               int var14 = var5.readInt();
               int var15 = var5.readInt();
               this.waypoints.get(var1).add(new Waypoint(var10, var1, new BetterBlockPos(var13, var14, var15), var11));
            }
         } catch (IOException var22) {
         }
      }
   }

   private synchronized void save(IWaypoint.Tag var1) {
      Path var2 = this.directory.resolve(var1.name().toLowerCase() + ".mp4");

      try (
         FileOutputStream var3 = new FileOutputStream(var2.toFile());
         BufferedOutputStream var4 = new BufferedOutputStream(var3);
         DataOutputStream var5 = new DataOutputStream(var4);
      ) {
         var5.writeLong(121977993584L);
         var5.writeLong(this.waypoints.get(var1).size());

         for (IWaypoint var7 : this.waypoints.get(var1)) {
            var5.writeUTF(var7.getName());
            var5.writeLong(var7.getCreationTimestamp());
            var5.writeInt(var7.getLocation().getX());
            var5.writeInt(var7.getLocation().getY());
            var5.writeInt(var7.getLocation().getZ());
         }
      } catch (IOException var14) {
         var14.printStackTrace();
      }
   }

   @Override
   public void addWaypoint(IWaypoint var1) {
      if (this.waypoints.get(var1.getTag()).add(var1)) {
         this.save(var1.getTag());
      }
   }

   @Override
   public void removeWaypoint(IWaypoint var1) {
      if (this.waypoints.get(var1.getTag()).remove(var1)) {
         this.save(var1.getTag());
      }
   }

   @Override
   public IWaypoint getMostRecentByTag(IWaypoint.Tag var1) {
      return this.waypoints.get(var1).stream().min(Comparator.comparingLong(var0 -> -var0.getCreationTimestamp())).orElse(null);
   }

   @Override
   public Set<IWaypoint> getByTag(IWaypoint.Tag var1) {
      return Collections.unmodifiableSet(this.waypoints.get(var1));
   }

   @Override
   public Set<IWaypoint> getAllWaypoints() {
      return this.waypoints.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
   }
}
