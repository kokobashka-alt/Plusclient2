package baritone.process.elytra;

import baritone.api.utils.BetterBlockPos;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.phys.Vec3;

public class NetherPath extends AbstractList<BetterBlockPos> {
   private static final NetherPath EMPTY_PATH = new NetherPath(Collections.emptyList());
   private final List<BetterBlockPos> backing;

   NetherPath(List<BetterBlockPos> var1) {
      this.backing = var1;
   }

   public BetterBlockPos get(int var1) {
      return this.backing.get(var1);
   }

   @Override
   public int size() {
      return this.backing.size();
   }

   public BetterBlockPos getLast() {
      return this.isEmpty() ? null : this.backing.get(this.backing.size() - 1);
   }

   public Vec3 getVec(int var1) {
      BetterBlockPos var2 = this.get(var1);
      return new Vec3(var2.x, var2.y, var2.z);
   }

   public static NetherPath emptyPath() {
      return EMPTY_PATH;
   }
}
