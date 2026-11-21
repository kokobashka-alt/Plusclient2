package wtf.onetap.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.Hat;
import wtf.onetap.util.RotationManager;

public class CrownLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
   private static final float[] FLOAT_VALUE_ARRAY = new float[]{Float.intBitsToFloat(1002740646), Float.intBitsToFloat(1011129254), Float.intBitsToFloat(1016296636)};
   private final CrownModel crownModel = new CrownModel(0.0F);
   private final CrownModel[] crownModelArray = new CrownModel[FLOAT_VALUE_ARRAY.length];

   public CrownLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderLayerParent) {
      super(renderLayerParent);

      for (int i = 0; i < this.crownModelArray.length; i++) {
         this.crownModelArray[i] = new CrownModel(FLOAT_VALUE_ARRAY[i]);
      }
   }

   public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int value, AvatarRenderState avatarRenderState, float floatValue, float floatValue2) {
      CrownModel.m_2_String();
      Hat hat = Onetap.MODULE_MANAGER.get(Hat.class);
      if (hat != null && hat.isEnabled() && hat.m_1_boolean()) {
         if (!avatarRenderState.isInvisible && avatarRenderState.headItem.isEmpty()) {
            if (hat.m_9_boolean(avatarRenderState.id)) {
               PropellerCapTexture.m_9(hat.m_3_IArr());
               poseStack.pushPose();
               ((PlayerModel)this.getParentModel()).root().translateAndRotate(poseStack);
               ((PlayerModel)this.getParentModel()).getHead().translateAndRotate(poseStack);
               if (!avatarRenderState.headEquipment.isEmpty()) {
                  poseStack.translate(0.0F, -0.25F, 0.0F);
                  poseStack.scale(1.1F, 1.1F, 1.1F);
                  poseStack.translate(0.0F, 0.25F, 0.0F);
               }

               int value2 = LivingEntityRenderer.getOverlayCoords(avatarRenderState, 0.0F);
               submitNodeCollector.submitModel(this.crownModel, avatarRenderState, poseStack, RenderTypes.entityCutout(PropellerCapTexture.IDENTIFIER), value, value2, -1, null, 0, null);
               RotationManager.StateHolder c7 = RotationManager.ROTATION_MANAGER.m_4_C7(avatarRenderState.id);
               if (c7 != null) {
                  float floatValue3 = Mth.clamp((Math.abs(c7.m_5_float()) - 10.0F) / 14.0F, 0.0F, 1.0F);
                  if (floatValue3 > 0.0F) {
                     RenderType renderType = RenderTypes.entityTranslucent(PropellerCapTexture.IDENTIFIER);

                     for (int i = 0; i < this.crownModelArray.length; i++) {
                        int value3 = (int)(170.0F * floatValue3 / (i + 1.5F));
                        submitNodeCollector.submitModel(this.crownModelArray[i], avatarRenderState, poseStack, renderType, value, value2, value3 << 24 | 16777215, null, 0, null);
                     }
                  }
               }

               poseStack.popPose();
            }
         }
      }
   }
}
