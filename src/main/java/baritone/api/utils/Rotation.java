package baritone.api.utils;

public class Rotation {
   private final float yaw;
   private final float pitch;

   public Rotation(float var1, float var2) {
      this.yaw = var1;
      this.pitch = var2;
      if (Float.isInfinite(var1) || Float.isNaN(var1) || Float.isInfinite(var2) || Float.isNaN(var2)) {
         throw new IllegalStateException(var1 + " " + var2);
      }
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public Rotation add(Rotation var1) {
      return new Rotation(this.yaw + var1.yaw, this.pitch + var1.pitch);
   }

   public Rotation subtract(Rotation var1) {
      return new Rotation(this.yaw - var1.yaw, this.pitch - var1.pitch);
   }

   public Rotation clamp() {
      return new Rotation(this.yaw, clampPitch(this.pitch));
   }

   public Rotation normalize() {
      return new Rotation(normalizeYaw(this.yaw), this.pitch);
   }

   public Rotation normalizeAndClamp() {
      return new Rotation(normalizeYaw(this.yaw), clampPitch(this.pitch));
   }

   public Rotation withPitch(float var1) {
      return new Rotation(this.yaw, var1);
   }

   public boolean isReallyCloseTo(Rotation var1) {
      return this.yawIsReallyClose(var1) && Math.abs(this.pitch - var1.pitch) < 0.01;
   }

   public boolean yawIsReallyClose(Rotation var1) {
      float var2 = Math.abs(normalizeYaw(this.yaw) - normalizeYaw(var1.yaw));
      return var2 < 0.01 || var2 > 359.99;
   }

   public static float clampPitch(float var0) {
      return Math.max(-90.0F, Math.min(90.0F, var0));
   }

   public static float normalizeYaw(float var0) {
      float var1 = var0 % 360.0F;
      if (var1 < -180.0F) {
         var1 += 360.0F;
      }

      if (var1 > 180.0F) {
         var1 -= 360.0F;
      }

      return var1;
   }

   public static float yawDistanceFromOffset(float var0, float var1) {
      if (!(var0 > 0.0F ^ var1 > 0.0F) || !((var0 > 90.0F || var0 < -90.0F) ^ (var1 > 90.0F || var1 < -90.0F))) {
         return var0 - var1;
      } else {
         return var0 < 0.0F ? 360.0F + (var0 - var1) : 360.0F - (var0 - var1);
      }
   }

   @Override
   public String toString() {
      return "Yaw: " + this.yaw + ", Pitch: " + this.pitch;
   }
}
