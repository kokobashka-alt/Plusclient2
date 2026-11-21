package baritone.process.elytra;

import baritone.api.utils.BetterBlockPos;
import dev.babbaj.pathfinder.PathSegment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnpackedSegment {
   private final Stream<BetterBlockPos> path;
   private final boolean finished;

   public UnpackedSegment(Stream<BetterBlockPos> var1, boolean var2) {
      this.path = var1;
      this.finished = var2;
   }

   public UnpackedSegment append(Stream<BetterBlockPos> var1, boolean var2) {
      return new UnpackedSegment(Stream.concat(this.path, var1), var2);
   }

   public UnpackedSegment prepend(Stream<BetterBlockPos> var1) {
      return new UnpackedSegment(Stream.concat(var1, this.path), this.finished);
   }

   public List<BetterBlockPos> collect() {
      var var1 = this.path.collect(Collectors.toList());
      HashMap var2 = new HashMap();

      for (int var3 = 0; var3 < var1.size(); var3++) {
         BetterBlockPos var4 = (BetterBlockPos)var1.get(var3);
         if (var2.containsKey(var4)) {
            for (int var5 = (Integer)var2.get(var4); var3 > var5; var3--) {
               var1.remove(var3);
            }
         } else {
            var2.put(var4, var3);
         }
      }

      return var1;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public static UnpackedSegment from(PathSegment var0) {
      return new UnpackedSegment(Arrays.stream(var0.packed).mapToObj(BetterBlockPos::deserializeFromLong), var0.finished);
   }
}
