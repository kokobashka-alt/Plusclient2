package baritone.api.cache;

import baritone.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public interface IWaypoint {
   String getName();

   IWaypoint.Tag getTag();

   long getCreationTimestamp();

   BetterBlockPos getLocation();

   enum Tag {
      HOME("home", "base"),
      DEATH("death"),
      BED("bed", "spawn"),
      USER("user");

      private static final List<IWaypoint.Tag> TAG_LIST = Collections.unmodifiableList(Arrays.asList(values()));
      public final String[] names;

      Tag(String... var3) {
         this.names = var3;
      }

      public String getName() {
         return this.names[0];
      }

      public static IWaypoint.Tag getByName(String var0) {
         for (IWaypoint.Tag var4 : values()) {
            for (String var8 : var4.names) {
               if (var8.equalsIgnoreCase(var0)) {
                  return var4;
               }
            }
         }

         return null;
      }

      public static String[] getAllNames() {
         HashSet var0 = new HashSet();

         for (IWaypoint.Tag var4 : values()) {
            var0.addAll(Arrays.asList(var4.names));
         }

         return (String[])var0.toArray(new String[0]);
      }
   }
}
