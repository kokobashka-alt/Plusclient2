package wtf.onetap.event;

import net.minecraft.world.entity.Entity;

public class EntitySpawnEvent extends Event {
   private final Entity entity;

   public EntitySpawnEvent(Entity entity2) {
      this.entity = entity2;
   }

   public Entity getEntity() {
      return this.entity;
   }
}
