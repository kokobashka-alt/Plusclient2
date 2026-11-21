package wtf.onetap.util;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class WorldToScreen {
   private static final Minecraft MINECRAFT = Minecraft.getInstance();
   private static Matrix4f matrix4f;
   private static Matrix4f matrix4f2;
   private static int value;

   private WorldToScreen() {
   }

   public static void setMatrices(Matrix4f matrix4f3, Matrix4f matrix4f4) {
      matrix4f = new Matrix4f(matrix4f3);
      matrix4f2 = new Matrix4f(matrix4f4);
   }

   public static WorldToScreen.DataRecord project(double doubleValue, double doubleValue2, double doubleValue3) {
      if (matrix4f != null && matrix4f2 != null) {
         CameraRenderState cameraRenderState = MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         Vector4f vector4f = new Vector4f((float)(doubleValue - cameraRenderState.pos.x), (float)(doubleValue2 - cameraRenderState.pos.y), (float)(doubleValue3 - cameraRenderState.pos.z), 1.0F);
         matrix4f2.transform(vector4f);
         matrix4f.transform(vector4f);
         if (vector4f.w() <= 0.0F) {
            return null;
         }

         float floatValue = vector4f.x() / vector4f.w();
         float floatValue2 = vector4f.y() / vector4f.w();
         Window window = MINECRAFT.getWindow();
         float floatValue3 = (floatValue + 1.0F) * 0.5F * window.getGuiScaledWidth();
         float floatValue4 = (1.0F - floatValue2) * 0.5F * window.getGuiScaledHeight();
         return new WorldToScreen.DataRecord(floatValue3, floatValue4);
      } else {
         return null;
      }
   }

   public static WorldToScreen.DataRecord project(Vec3 vec3) {
      return project(vec3.x, vec3.y, vec3.z);
   }

   static {
      m_9_void(0);
   }

   public static void m_9_void(int value2) {
      value = value2;
   }

   public static int m_4_int() {
      return value;
   }

   public static int m_9_int() {
      return 41;
   }

   public record DataRecord(float f_2r, float f_2__) {
      private static String f_7;

      public float m_2r_float() {
         return this.f_2r;
      }

      public float m_2___float() {
         return this.f_2__;
      }

      public static void m_8_void(String text) {
         f_7 = text;
      }

      public static String m_0_String() {
         return f_7;
      }
   }
}
