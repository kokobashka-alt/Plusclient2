package wtf.onetap.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Rotations implements MinecraftAccess {
   private float floatValue;
   private float floatValue2;
   private static boolean flag;

   public Rotations() {
   }

   public Rotations(float floatValue3, float floatValue4) {
      this.floatValue = floatValue3;
      this.floatValue2 = Mth.clamp(floatValue4, -90.0F, 90.0F);
   }

   public Rotations(Entity entity) {
      this.floatValue = entity.getYRot();
      this.floatValue2 = Mth.clamp(entity.getXRot(), -90.0F, 90.0F);
   }

   public Rotations(Vec2 vec2) {
      this.floatValue = vec2.x;
      this.floatValue2 = Mth.clamp(vec2.y, -90.0F, 90.0F);
   }

   public Rotations(Vec3 vec3) {
      Vec2 vec2 = m_9_Vec2(vec3);
      this.floatValue = vec2.x;
      this.floatValue2 = vec2.y;
   }

   public Rotations(Vec3 vec3, Vec3 vec32) {
      Vec2 vec2 = m_3_Vec2(vec3, vec32);
      this.floatValue = vec2.x;
      this.floatValue2 = vec2.y;
   }

   private static Vec2 m_9_Vec2(Vec3 vec3) {
      double doubleValue = Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z);
      float floatValue3 = Mth.wrapDegrees((float)(Math.toDegrees(Math.atan2(vec3.z, vec3.x)) - 90.0));
      float floatValue4 = Mth.clamp((float)(-Math.toDegrees(Math.atan2(vec3.y, doubleValue))), -90.0F, 90.0F);
      return new Vec2(floatValue3, floatValue4);
   }

   private static Vec2 m_3_Vec2(Vec3 vec3, Vec3 vec32) {
      Vec3 vec33 = vec32.subtract(vec3);
      double doubleValue = Math.sqrt(vec33.x * vec33.x + vec33.z * vec33.z);
      float floatValue3 = Mth.wrapDegrees((float)(Math.toDegrees(Math.atan2(vec33.z, vec33.x)) - 90.0));
      float floatValue4 = Mth.clamp((float)(-Math.toDegrees(Math.atan2(vec33.y, doubleValue))), -90.0F, 90.0F);
      return new Vec2(floatValue3, floatValue4);
   }

   public float m_2(Rotations rotations) {
      float floatValue3 = Math.abs(Mth.wrapDegrees(rotations.floatValue - this.floatValue));
      float floatValue4 = Math.abs(rotations.floatValue2 - this.floatValue2);
      return (float)Math.hypot(floatValue3, floatValue4);
   }

   public double m_7_double(Rotations rotations) {
      double doubleValue = Math.abs(Mth.wrapDegrees(rotations.floatValue - this.floatValue));
      double doubleValue2 = Math.abs(Mth.wrapDegrees(rotations.floatValue2 - this.floatValue2));
      return Math.hypot(doubleValue, doubleValue2);
   }

   public Vec3 toVector() {
      float floatValue3 = this.floatValue2 * (float) (Math.PI / 180.0);
      float floatValue4 = -this.floatValue * (float) (Math.PI / 180.0);
      float floatValue5 = Mth.cos(floatValue4);
      float floatValue6 = Mth.sin(floatValue4);
      float floatValue7 = Mth.cos(floatValue3);
      float floatValue8 = Mth.sin(floatValue3);
      return new Vec3(floatValue6 * floatValue7, -floatValue8, floatValue5 * floatValue7);
   }

   public static Rotations m_6(Player player, Entity entity) {
      Vec3 vec3 = player.getEyePosition();
      Vec3 vec32 = entity.position().add(0.0, entity.getBbHeight() * 0.5, 0.0);
      double doubleValue = vec32.x - vec3.x;
      double doubleValue2 = vec32.y - vec3.y;
      double doubleValue3 = vec32.z - vec3.z;
      double doubleValue4 = Math.sqrt(doubleValue * doubleValue + doubleValue3 * doubleValue3);
      float floatValue3 = Mth.wrapDegrees((float)(Math.toDegrees(Math.atan2(doubleValue3, doubleValue)) - 90.0));
      float floatValue4 = (float)(-Math.toDegrees(Math.atan2(doubleValue2, doubleValue4)));
      return new Rotations(floatValue3, floatValue4);
   }

   public static Vec2 m_6_Vec2() {
      return new Vec2(getCameraYaw(), getCameraPitch());
   }

   public static float getCameraYaw() {
      return Mth.wrapDegrees(MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().yRot() + (MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson() ? 0 : 180));
   }

   public static float getCameraPitch() {
      return (MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson() ? 1 : -1) * MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().xRot();
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public void setFloatValue(float floatValue3) {
      this.floatValue = floatValue3;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public void m_2_void(float floatValue3) {
      this.floatValue2 = Mth.clamp(floatValue3, -90.0F, 90.0F);
   }

   public static void m_5_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_9_boolean() {
      return flag;
   }

   public static boolean m_6_boolean() {
      return true;
   }

   static {
      m_5_void(false);
   }
}
