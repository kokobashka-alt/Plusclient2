package wtf.onetap.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.Wings;

public class WingsLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
   private final WingsModel wingsModel = new WingsModel();

   public WingsLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderLayerParent) {
      super(renderLayerParent);
   }

   public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int value, AvatarRenderState avatarRenderState, float floatValue, float floatValue2) {
      WingsModel.m_1_C3_sArr();
      Wings wings = Onetap.MODULE_MANAGER.get(Wings.class);
      if (wings != null && wings.isEnabled()) {
         if (!avatarRenderState.isInvisible) {
            if (this.m_4(avatarRenderState, wings)) {
               float floatValue3 = wings.size.getFloat();
               RenderType renderType = RenderTypes.armorCutoutNoCull(WingsModel.IDENTIFIER);
               poseStack.pushPose();
               ((PlayerModel)this.getParentModel()).body.translateAndRotate(poseStack);
               poseStack.scale(floatValue3, floatValue3, floatValue3);
               poseStack.translate(0.0, 0.1, 0.095);
               if (avatarRenderState.isCrouching) {
                  poseStack.translate(0.0, 0.2, 0.05);
               }

               if (!avatarRenderState.chestEquipment.isEmpty()) {
                  poseStack.translate(0.0, 0.0, 0.05);
               }

               for (int i = 0; i < 2; i++) {
                  poseStack.pushPose();
                  if (i == 1) {
                     poseStack.scale(-1.0F, 1.0F, 1.0F);
                  }

                  submitNodeCollector.submitModel(this.wingsModel, avatarRenderState, poseStack, renderType, 15728880, OverlayTexture.NO_OVERLAY, -1, null);
                  submitNodeCollector.submitModel(this.wingsModel, avatarRenderState, poseStack, RenderTypes.entityGlint(), 15728880, OverlayTexture.NO_OVERLAY, -1, null);
                  poseStack.popPose();
               }

               poseStack.popPose();
            }
         }
      }
   }

   private boolean m_4(AvatarRenderState avatarRenderState, Wings wings) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.level != null) {
         if (avatarRenderState.id == mc.player.getId()) {
            return true;
         } else if (!(Boolean)wings.workOnFriends.getValue()) {
            return false;
         } else {
            return !(mc.level.getEntity(avatarRenderState.id) instanceof Player player)
               ? false
               : Onetap.FRIEND_MANAGER.isFriend(player.getGameProfile().name()) || Onetap.FRIEND_MANAGER.isFriend(player.getName().getString());
         }
      } else {
         return false;
      }
   }
}
