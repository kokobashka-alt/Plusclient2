package wtf.onetap.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RayTraceEvent extends Event {
   private final Entity entity;
   private final Vec3 start;
   private final Vec3 end;
   private final Block block;
   private final Fluid fluid;
   private HitResult result;

   public RayTraceEvent(Entity entity2, Vec3 vec3, Vec3 vec32, Block block2, Fluid fluid2) {
      this.entity = entity2;
      this.start = vec3;
      this.end = vec32;
      this.block = block2;
      this.fluid = fluid2;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public Vec3 getStart() {
      return this.start;
   }

   public Vec3 getEnd() {
      return this.end;
   }

   public Block getBlock() {
      return this.block;
   }

   public Fluid getFluid() {
      return this.fluid;
   }

   public HitResult getResult() {
      return this.result;
   }

   public void setResult(HitResult hitResult) {
      this.result = hitResult;
   }
}
