package baritone.api.event.events;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;

public class RenderEvent {
   private final float partialTicks;
   private final Matrix4f projectionMatrix;
   private final PoseStack modelViewStack;

   public RenderEvent(float var1, PoseStack var2, Matrix4f var3) {
      this.partialTicks = var1;
      this.modelViewStack = var2;
      this.projectionMatrix = var3;
   }

   public final float getPartialTicks() {
      return this.partialTicks;
   }

   public PoseStack getModelViewStack() {
      return this.modelViewStack;
   }

   public Matrix4f getProjectionMatrix() {
      return this.projectionMatrix;
   }
}
