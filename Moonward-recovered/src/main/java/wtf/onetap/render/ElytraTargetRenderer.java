package wtf.onetap.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.util.MinecraftAccess;

public class ElytraTargetRenderer implements MinecraftAccess {
   private final PoseStack poseStack;
   private PlayerModel playerModel;
   private PlayerModel playerModel2;
   private Vec3 vec3;
   private AbstractClientPlayer abstractClientPlayer;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private long longValue;
   private long longValue2;

   public ElytraTargetRenderer() {
      this.poseStack = new PoseStack();
   }

   public void m_1(Render3DEvent event, LivingEntity livingEntity, Vec3 vec32) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && (this.abstractClientPlayer == null || this.abstractClientPlayer.level() == MinecraftAccess.MINECRAFT.level)) {
         AbstractClientPlayer abstractClientPlayer2 = livingEntity instanceof AbstractClientPlayer abstractClientPlayer3 ? abstractClientPlayer3 : null;
         boolean flag = vec32 != null
            && abstractClientPlayer2 != null
            && abstractClientPlayer2.level() == MinecraftAccess.MINECRAFT.level
            && abstractClientPlayer2 != MinecraftAccess.MINECRAFT.player
            && Double.isFinite(vec32.x)
            && Double.isFinite(vec32.y)
            && Double.isFinite(vec32.z);
         float floatValue4 = 1.0F;
         Vec3 vec33;
         if (flag) {
            vec33 = this.m_8(abstractClientPlayer2, vec32);
            this.longValue2 = 0L;
         } else {
            if (this.vec3 == null || this.abstractClientPlayer == null) {
               return;
            }

            long longValue3 = System.nanoTime();
            if (this.longValue2 == 0L) {
               this.longValue2 = longValue3;
            }

            float floatValue5 = Mth.clamp((float)(longValue3 - this.longValue2) / 3.0E8F, 0.0F, 1.0F);
            if (floatValue5 >= 1.0F) {
               this.reset();
               return;
            }

            floatValue4 = 1.0F - floatValue5 * floatValue5 * (3.0F - 2.0F * floatValue5);
            abstractClientPlayer2 = this.abstractClientPlayer;
            vec33 = this.vec3;
         }

         Vec3 vec34 = event.getVec3();
         if (!(vec33.distanceToSqr(vec34) > 36864.0)) {
            AbstractTexture abstractTexture = MinecraftAccess.MINECRAFT.getTextureManager().getTexture(abstractClientPlayer2.getSkin().body().texturePath());
            if (abstractTexture != null) {
               GpuTextureView gpuTextureView = abstractTexture.getTextureView();
               if (gpuTextureView != null && !gpuTextureView.isClosed()) {
                  PlayerModel playerModel3 = this.m_9_PlayerModel(abstractClientPlayer2.getSkin().model() == PlayerModelType.SLIM);
                  this.m_3(playerModel3);
                  float floatValue6 = 0.5F + 0.5F * (float)Math.sin(System.nanoTime() * 3.2E-9);
                  float floatValue7 = Math.min((0.98F + 0.18F * floatValue6) * floatValue4, 1.5F);
                  int value = (int)(0.62F * floatValue4 * 255.0F) << 24 | m_1(ColorUtil.primaryColor(), floatValue7);
                  this.poseStack.pushPose();
                  this.poseStack.translate(vec33.x - vec34.x, vec33.y - vec34.y, vec33.z - vec34.z);
                  this.poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - this.floatValue));
                  this.poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - this.floatValue3));
                  this.poseStack.translate(0.0F, -1.0F, 0.3F);
                  this.poseStack.scale(-0.9375F, -0.9375F, 0.9375F);
                  this.poseStack.translate(0.0F, -1.501F, 0.0F);
                  Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
                  Texture3DRenderer.m_5(matrix4f, gpuTextureView, 128, Texture3DRenderer.m_6_GpuSampler(), bufferBuilder -> this.m_7(playerModel3, bufferBuilder, value));
                  this.poseStack.popPose();
               }
            }
         }
      } else {
         this.reset();
      }
   }

   public void reset() {
      this.vec3 = null;
      this.abstractClientPlayer = null;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.longValue = 0L;
      this.longValue2 = 0L;
   }

   private Vec3 m_8(AbstractClientPlayer abstractClientPlayer2, Vec3 vec32) {
      long longValue3 = System.nanoTime();
      float floatValue4 = this.longValue == 0L ? 0.016666668F : Math.min(0.1F, (float)(longValue3 - this.longValue) / 1.0E9F);
      this.longValue = longValue3;
      if (this.vec3 != null && this.abstractClientPlayer == abstractClientPlayer2 && this.vec3.distanceToSqr(vec32) <= 144.0) {
         float floatValue5 = Mth.clamp(1.0F - (float)Math.exp(-11.0F * floatValue4), 0.08F, 0.32F);
         this.vec3 = this.vec3.lerp(vec32, floatValue5);
         this.floatValue = Mth.rotLerp(floatValue5, this.floatValue, abstractClientPlayer2.yBodyRot);
         this.floatValue2 = Mth.rotLerp(floatValue5, this.floatValue2, abstractClientPlayer2.yHeadRot);
         this.floatValue3 = Mth.lerp(floatValue5, this.floatValue3, abstractClientPlayer2.getXRot());
         return this.vec3;
      } else {
         this.vec3 = vec32;
         this.abstractClientPlayer = abstractClientPlayer2;
         this.floatValue = abstractClientPlayer2.yBodyRot;
         this.floatValue2 = abstractClientPlayer2.yHeadRot;
         this.floatValue3 = abstractClientPlayer2.getXRot();
         return vec32;
      }
   }

   private PlayerModel m_9_PlayerModel(boolean flag) {
      if (flag) {
         if (this.playerModel2 == null) {
            this.playerModel2 = new PlayerModel(LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64).bakeRoot(), true);
         }

         return this.playerModel2;
      } else {
         if (this.playerModel == null) {
            this.playerModel = new PlayerModel(MinecraftAccess.MINECRAFT.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
         }

         return this.playerModel;
      }
   }

   private void m_3(PlayerModel playerModel3) {
      playerModel3.hat.visible = true;
      playerModel3.jacket.visible = true;
      playerModel3.leftSleeve.visible = true;
      playerModel3.rightSleeve.visible = true;
      playerModel3.leftPants.visible = true;
      playerModel3.rightPants.visible = true;
      playerModel3.body.xRot = 0.0F;
      playerModel3.body.yRot = 0.0F;
      playerModel3.body.zRot = 0.0F;
      playerModel3.head.xRot = -0.7F;
      playerModel3.head.yRot = Mth.wrapDegrees(this.floatValue2 - this.floatValue) * (float) (Math.PI / 180.0);
      playerModel3.head.zRot = 0.0F;
      playerModel3.rightArm.xRot = 0.3F;
      playerModel3.leftArm.xRot = 0.3F;
      playerModel3.rightArm.yRot = 0.0F;
      playerModel3.leftArm.yRot = 0.0F;
      playerModel3.rightArm.zRot = 0.08F;
      playerModel3.leftArm.zRot = -0.08F;
      playerModel3.rightLeg.xRot = 0.1F;
      playerModel3.leftLeg.xRot = 0.1F;
      playerModel3.rightLeg.yRot = 0.0F;
      playerModel3.leftLeg.yRot = 0.0F;
      playerModel3.rightLeg.zRot = -0.02F;
      playerModel3.leftLeg.zRot = 0.02F;
      copyRotation(playerModel3.head, playerModel3.hat);
      copyRotation(playerModel3.body, playerModel3.jacket);
      copyRotation(playerModel3.rightArm, playerModel3.rightSleeve);
      copyRotation(playerModel3.leftArm, playerModel3.leftSleeve);
      copyRotation(playerModel3.rightLeg, playerModel3.rightPants);
      copyRotation(playerModel3.leftLeg, playerModel3.leftPants);
   }

   private static void copyRotation(ModelPart modelPart, ModelPart modelPart2) {
      modelPart2.xRot = modelPart.xRot;
      modelPart2.yRot = modelPart.yRot;
      modelPart2.zRot = modelPart.zRot;
   }

   private void m_7(PlayerModel playerModel3, VertexConsumer vertexConsumer, int value) {
      for (ModelPart modelPart : new ModelPart[]{
         playerModel3.head,
         playerModel3.body,
         playerModel3.rightArm,
         playerModel3.leftArm,
         playerModel3.rightLeg,
         playerModel3.leftLeg,
         playerModel3.hat,
         playerModel3.jacket,
         playerModel3.rightSleeve,
         playerModel3.leftSleeve,
         playerModel3.rightPants,
         playerModel3.leftPants
      }) {
         modelPart.render(this.poseStack, vertexConsumer, 15728880, 0, value);
      }
   }

   private static int m_1(int value, float floatValue4) {
      int value2 = Math.min(255, Math.round((value >> 16 & 0xFF) * floatValue4));
      int value3 = Math.min(255, Math.round((value >> 8 & 0xFF) * floatValue4));
      int value4 = Math.min(255, Math.round((value & 0xFF) * floatValue4));
      return value2 << 16 | value3 << 8 | value4;
   }
}
