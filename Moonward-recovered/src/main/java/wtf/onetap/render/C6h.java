package wtf.onetap.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.mixin.render.EntityRendererAccessor;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

public class C6h implements MinecraftAccess {
   private static final PoseStack POSE_STACK = new PoseStack();
   private static Module[] moduleArray;

   private C6h() {
   }

   public static C6h.DataRecord m_2_C6(LivingEntity livingEntity, float floatValue) {
      try {
         EntityRenderer entityRenderer = MinecraftAccess.MINECRAFT.getEntityRenderDispatcher().getRenderer(livingEntity);
         if (entityRenderer instanceof LivingEntityRenderer livingEntityRenderer) {
            EntityRenderState entityRenderState = entityRenderer.createRenderState();
            entityRenderer.extractRenderState(livingEntity, entityRenderState, floatValue);
            ((EntityRendererAccessor)entityRenderer).onetap$finalizeRenderState(livingEntity, entityRenderState);
            entityRenderState.isInvisible = false;
            return new C6h.DataRecord(entityRenderState, livingEntityRenderer.getTextureLocation((LivingEntityRenderState)entityRenderState));
         } else {
            return null;
         }
      } catch (Throwable e) {
         return null;
      }
   }

   public static void m_5(C6h.DataRecord c6, CameraRenderState cameraRenderState, double doubleValue, double doubleValue2, double doubleValue3, VertexConsumer vertexConsumer, int value, int value2) {
      MinecraftAccess.MINECRAFT.getEntityRenderDispatcher().submit(c6.m_8c(), cameraRenderState, doubleValue, doubleValue2, doubleValue3, POSE_STACK, new C6h.StateHolder(vertexConsumer, value, value2));
   }

   public static void m_9(C6h.DataRecord c6, Render3DEvent event, Matrix4f matrix4f, double doubleValue, double doubleValue2, double doubleValue3, int value, int value2, boolean flag) {
      AbstractTexture abstractTexture = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(c6.m_7q());
      if (abstractTexture != null) {
         GpuTextureView gpuTextureView = abstractTexture.getTextureView();
         if (gpuTextureView != null && !gpuTextureView.isClosed()) {
            Texture3DRenderer.m_5(
               matrix4f, gpuTextureView, 768, true, flag, Texture3DRenderer.m_6_GpuSampler(), bufferBuilder -> m_5(c6, event.getCameraRenderState(), doubleValue, doubleValue2, doubleValue3, bufferBuilder, value, value2)
            );
         }
      }
   }

   static {
      m_3(null);
   }

   public static void m_3(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_5_C3_sArr() {
      return moduleArray;
   }

   private static final class StateHolder extends SubmitNodeStorage {
      private final VertexConsumer vertexConsumer;
      private final int value;
      private final int value2;
      private boolean flag;

      private StateHolder(VertexConsumer vertexConsumer2, int value3, int value4) {
         this.vertexConsumer = vertexConsumer2;
         this.value = value3;
         this.value2 = value4;
      }

      @Override
      public <S> void submitModel(
         Model<? super S> model,
         S ss,
         PoseStack poseStack,
         RenderType renderType,
         int value3,
         int value4,
         int value5,
         TextureAtlasSprite textureAtlasSprite,
         int value6,
         CrumblingOverlay crumblingOverlay
      ) {
         if (!this.flag) {
            this.flag = true;
            int value7 = this.value != -1 ? this.value & 16777215 : value5 & 16777215;
            model.setupAnim(ss);
            model.renderToBuffer(poseStack, this.vertexConsumer, value3, OverlayTexture.NO_OVERLAY, this.value2 << 24 | value7);
         }
      }
   }

   public record DataRecord(EntityRenderState f_8c, Identifier f_7q) {
      private static Module[] f_5;

      public EntityRenderState m_8c() {
         return this.f_8c;
      }

      public Identifier m_7q() {
         return this.f_7q;
      }

      public static void m_9(Module[] moduleArray2) {
         f_5 = moduleArray2;
      }

      public static Module[] m_3_C3_sArr() {
         return f_5;
      }
   }
}
