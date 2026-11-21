package wtf.onetap.event;

import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Render3DEvent extends Event {
   private final float floatValue;
   private final Matrix4f matrix4f;
   private final Matrix4f matrix4f2;
   private final Vec3 vec3;
   private final Quaternionf quaternionf;
   private final CameraRenderState cameraRenderState;
   private static int[] valueArray;

   public Render3DEvent(float floatValue2, Matrix4f matrix4f3, Matrix4f matrix4f4, Vec3 vec32, Quaternionf quaternionf2, CameraRenderState cameraRenderState2) {
      this.floatValue = floatValue2;
      this.matrix4f = matrix4f3;
      this.matrix4f2 = matrix4f4;
      this.vec3 = vec32;
      this.quaternionf = quaternionf2;
      this.cameraRenderState = cameraRenderState2;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public Matrix4f getMatrix4f() {
      return this.matrix4f;
   }

   public Matrix4f getMatrix4f2() {
      return this.matrix4f2;
   }

   public Vec3 getVec3() {
      return this.vec3;
   }

   public Quaternionf getQuaternionf() {
      return this.quaternionf;
   }

   public CameraRenderState getCameraRenderState() {
      return this.cameraRenderState;
   }

   public static void m_2(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_9_IArr() {
      return valueArray;
   }
}
