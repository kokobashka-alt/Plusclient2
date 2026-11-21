package wtf.onetap.render;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.RenderEvent;
import baritone.utils.IRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class Render3DHelper {
   private Render3DHelper() {
   }

   public static void m_4(DeltaTracker deltaTracker, Matrix4fc matrix4fc, CameraRenderState cameraRenderState) {
      for (IBaritone iBaritone : BaritoneAPI.getProvider().getAllBaritones()) {
         PoseStack poseStack = new PoseStack();
         poseStack.mulPose(matrix4fc);
         iBaritone.getGameEventHandler().onRenderPass(new RenderEvent(deltaTracker.getGameTimeDeltaPartialTick(false), poseStack, cameraRenderState.projectionMatrix));
      }

      m_3(cameraRenderState.projectionMatrix, matrix4fc);
   }

   public static void m_3(Matrix4fc matrix4fc, Matrix4fc matrix4fc2) {
      Matrix4f matrix4f = new Matrix4f(matrix4fc).mul(matrix4fc2);
      m_8(IRenderer.drainLines(false), matrix4f);
      m_8(IRenderer.drainLines(true), matrix4f);
   }

   private static void m_8(List<IRenderer.LineSegment> lineSegmentList, Matrix4f matrix4f) {
      if (!lineSegmentList.isEmpty()) {
         ArrayList list = new ArrayList(lineSegmentList.size() * 2);
         int[] intArray = new int[lineSegmentList.size()];
         float floatValue = 0.5F;

         for (int i = 0; i < lineSegmentList.size(); i++) {
            IRenderer.LineSegment lineSegment = (IRenderer.LineSegment)lineSegmentList.get(i);
            list.add(new Vec3(lineSegment.x1(), lineSegment.y1(), lineSegment.z1()));
            list.add(new Vec3(lineSegment.x2(), lineSegment.y2(), lineSegment.z2()));
            intArray[i] = lineSegment.argb();
            floatValue = Math.max(floatValue, lineSegment.width() * 0.2F);
         }

         Line3DRenderer.m_7(matrix4f, list, intArray, Vec3.ZERO, floatValue);
      }
   }
}
