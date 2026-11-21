package wtf.onetap.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer.HandRenderSelection;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.onetap.Onetap;
import wtf.onetap.combat.rotation.RotationModes;
import wtf.onetap.module.player.AutoTool;
import wtf.onetap.module.render.SwingAnimation;
import wtf.onetap.module.render.ViewModel;
import wtf.onetap.render.TorchModel;
import wtf.onetap.render.ViewModelRenderer;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
   @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
   private void onetap$holdMyItemsFrame(float var1, PoseStack var2, SubmitNodeCollector var3, LocalPlayer var4, int var5, CallbackInfo var6) {
      ViewModelRenderer.m_6(var4);
   }

   @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
   private void onetap$holdMyItemsCustomRender(
      AbstractClientPlayer var1,
      float var2,
      float var3,
      InteractionHand var4,
      float var5,
      ItemStack var6,
      float var7,
      PoseStack var8,
      SubmitNodeCollector var9,
      int var10,
      CallbackInfo var11
   ) {
      if (!var1.isScoping()
         && (
            ViewModelRenderer.m_4((ItemInHandRenderer)(Object)this, var1, var4, var5, var6, var7, var8, var9, var10)
               || TorchModel.m_3(var1, var2, var4, var5, var6, var7, var8, var9, var10)
         )) {
         var11.cancel();
      }
   }

   @Inject(
      method = "submitArmWithItem",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = Shift.AFTER, ordinal = 0)
   )
   private void onetap$viewModelHandOffset(
      AbstractClientPlayer var1,
      float var2,
      float var3,
      InteractionHand var4,
      float var5,
      ItemStack var6,
      float var7,
      PoseStack var8,
      SubmitNodeCollector var9,
      int var10,
      CallbackInfo var11
   ) {
      boolean var12 = var4 == InteractionHand.MAIN_HAND;
      HumanoidArm var13 = var12 ? var1.getMainArm() : var1.getMainArm().getOpposite();
      ViewModel.m_4(var8, var13, var6);
   }

   @Redirect(
      method = "submitArmWithItem",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V",
         ordinal = 4
      )
   )
   private void onetap$swingAnimationsSuppressBaseTransform(ItemInHandRenderer var1, PoseStack var2, HumanoidArm var3, float var4) {
      SwingAnimation var5 = Onetap.MODULE_MANAGER.get(SwingAnimation.class);
      if (var3 == HumanoidArm.LEFT || var5 == null || !var5.m_1_boolean()) {
         this.onetap$invokeApplyItemArmTransform(var2, var3, var4);
      }
   }

   @Invoker("applyItemArmTransform")
   protected abstract void onetap$invokeApplyItemArmTransform(PoseStack var1, HumanoidArm var2, float var3);

   @Redirect(
      method = "submitArmWithItem",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;swingArm(FLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V",
         ordinal = 2
      )
   )
   private void onetap$swingAnimationsRedirect(ItemInHandRenderer var1, float var2, PoseStack var3, int var4, HumanoidArm var5) {
      SwingAnimation var6 = Onetap.MODULE_MANAGER.get(SwingAnimation.class);
      if (var5 != HumanoidArm.LEFT && var6 != null && var6.m_1_boolean()) {
         var6.m_4(var3, var2, 0.0F, var5);
      } else {
         this.onetap$invokeSwingArm(var2, var3, var4, var5);
      }
   }

   @Invoker("swingArm")
   protected abstract void onetap$invokeSwingArm(float var1, PoseStack var2, int var3, HumanoidArm var4);

   @Redirect(method = "submitHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackAnim(F)F"))
   private float onetap$holdMyItemsSwingSide(LocalPlayer var1, float var2) {
      float var3 = var1.getAttackAnim(var2);
      TorchModel.m_3_void(var3);
      return var3;
   }

   @Inject(method = "evaluateWhichHandsToRender", at = @At("HEAD"), cancellable = true)
   private static void onetap$holdMyItemsBothHands(LocalPlayer var0, CallbackInfoReturnable<HandRenderSelection> var1) {
      if (ViewModelRenderer.isSwingAnimationEnabled()) {
         var1.setReturnValue(HandRenderSelection.RENDER_BOTH_HANDS);
      }
   }

   @Redirect(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;")
   )
   private ItemStack onetap$autoToolMainHand(LocalPlayer var1) {
      AutoTool var2 = Onetap.MODULE_MANAGER.get(AutoTool.class);
      ItemStack var3 = var1.getMainHandItem();
      return var2 != null ? var2.m_1(var1, var1.getMainArm(), var3) : var3;
   }

   @Redirect(method = "submitHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getXRot(F)F"))
   private float onetap$cameraPitch(LocalPlayer var1, float var2) {
      RotationModes var3 = Onetap.ROTATION_MODES;
      return var3.isFlag() ? var3.getPitchPartial(var2) : var1.getXRot(var2);
   }

   @Redirect(method = "submitHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getViewXRot(F)F"))
   private float onetap$cameraViewPitch(LocalPlayer var1, float var2) {
      RotationModes var3 = Onetap.ROTATION_MODES;
      return var3.isFlag() ? var3.getPitchPartial(var2) : var1.getViewXRot(var2);
   }

   @Redirect(method = "submitHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getViewYRot(F)F"))
   private float onetap$cameraViewYaw(LocalPlayer var1, float var2) {
      RotationModes var3 = Onetap.ROTATION_MODES;
      return var3.isFlag() ? var3.getYawPartial(var2) : var1.getViewYRot(var2);
   }
}
