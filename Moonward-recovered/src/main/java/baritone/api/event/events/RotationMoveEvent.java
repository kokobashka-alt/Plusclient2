package baritone.api.event.events;

import baritone.api.utils.Rotation;

public class RotationMoveEvent {
   private final RotationMoveEvent.Type type;
   private final Rotation original;
   private float yaw;
   private float pitch;

   public RotationMoveEvent(RotationMoveEvent.Type var1, float var2, float var3) {
      this.type = var1;
      this.original = new Rotation(var2, var3);
      this.yaw = var2;
      this.pitch = var3;
   }

   public Rotation getOriginal() {
      return this.original;
   }

   public void setYaw(float var1) {
      this.yaw = var1;
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setPitch(float var1) {
      this.pitch = var1;
   }

   public float getPitch() {
      return this.pitch;
   }

   public RotationMoveEvent.Type getType() {
      return this.type;
   }

   public enum Type {
      MOTION_UPDATE,
      JUMP;
   }
}
