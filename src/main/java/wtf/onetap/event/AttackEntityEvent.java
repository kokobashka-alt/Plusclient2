package wtf.onetap.event;

import net.minecraft.world.entity.Entity;

public class AttackEntityEvent extends Event implements Cancellable {
   private final Entity entity;
   private boolean cancelled;
   private static int[] valueArray;

   public AttackEntityEvent(Entity entity2) {
      this.entity = entity2;
   }

   public Entity getEntity() {
      return this.entity;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }

   public static void m_4(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_6_IArr() {
      return valueArray;
   }
}
