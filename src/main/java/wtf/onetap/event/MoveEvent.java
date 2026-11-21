package wtf.onetap.event;

import net.minecraft.world.phys.Vec3;

public class MoveEvent extends Event {
   private Vec3 vec3;

   public MoveEvent(Vec3 vec32) {
      this.vec3 = vec32;
   }

   public Vec3 getVec3() {
      return this.vec3;
   }

   public void setVec3(Vec3 vec32) {
      this.vec3 = vec32;
   }
}
