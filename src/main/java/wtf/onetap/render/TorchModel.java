package wtf.onetap.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.SwingAnimation;
import wtf.onetap.module.render.ViewModel;

public class TorchModel {
   private static double doubleValue = System.nanoTime() / Double.longBitsToDouble(4741671816366391296L);
   private static double doubleValue2;
   private static double doubleValue3;
   private static float floatValue;
   private static float floatValue2;
   private static float floatValue3;
   private static float floatValue4;
   private static float floatValue5;
   private static float floatValue6;
   private static float floatValue7;
   private static float floatValue8;
   private static float floatValue9;
   private static float floatValue10;
   private static float floatValue11;
   private static float floatValue12;
   private static float floatValue13;
   private static boolean flag;
   private static boolean flag2;
   private static float floatValue14;
   private static boolean flag3;
   private static float floatValue15;
   private static float floatValue16;

   private TorchModel() {
   }

   private static Minecraft getMinecraft() {
      return Minecraft.getInstance();
   }

   private static boolean isSwingAnimationEnabled() {
      SwingAnimation swingAnimation = Onetap.MODULE_MANAGER.get(SwingAnimation.class);
      return swingAnimation != null && swingAnimation.m_5_boolean() && !swingAnimation.m_7_boolean();
   }

   private static boolean isCrawling(AbstractClientPlayer abstractClientPlayer) {
      return abstractClientPlayer.isVisuallyCrawling();
   }

   private static boolean isClimbing(AbstractClientPlayer abstractClientPlayer) {
      return abstractClientPlayer.onClimbable() && !abstractClientPlayer.onGround() && Math.abs(abstractClientPlayer.getDeltaMovement().y) > 0.0;
   }

   private static boolean m_0_boolean(ItemStack itemStack) {
      return itemStack.getItem() instanceof ExperienceBottleItem
         || itemStack.is(Items.EGG)
         || itemStack.getItem() instanceof SnowballItem
         || itemStack.getItem() instanceof EnderpearlItem
         || itemStack.getItem() instanceof SplashPotionItem
         || itemStack.getItem() instanceof LingeringPotionItem
         || itemStack.is(Items.ENDER_EYE);
   }

   private static boolean isSwordOrAxe(ItemStack itemStack) {
      return itemStack.is(ItemTags.SWORDS) || itemStack.getItem() instanceof AxeItem;
   }

   private static boolean isWeaponOrTool(ItemStack itemStack) {
      return itemStack.is(ItemTags.SWORDS)
         || itemStack.is(ItemTags.AXES)
         || itemStack.is(ItemTags.PICKAXES)
         || itemStack.is(ItemTags.HOES)
         || itemStack.getItem() instanceof TridentItem;
   }

   private static boolean isShovel(ItemStack itemStack) {
      return itemStack.is(ItemTags.SHOVELS);
   }

   private static boolean isLantern(ItemStack itemStack) {
      return itemStack.is(Items.LANTERN) || itemStack.is(Items.SOUL_LANTERN);
   }

   private static boolean m_2_boolean(ItemStack itemStack) {
      if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
         return false;
      } else {
         Block block = blockItem.getBlock();
         return itemStack.is(Items.STRING)
            || itemStack.is(Items.REDSTONE)
            || itemStack.is(Items.LEVER)
            || itemStack.is(Items.TRIPWIRE_HOOK)
            || block instanceof IronBarsBlock
            || block.defaultBlockState().is(BlockTags.RAILS)
            || block.defaultBlockState().is(BlockTags.CLIMBABLE)
            || block.defaultBlockState().is(BlockTags.DOORS);
      }
   }

   private static boolean isTorch(ItemStack itemStack) {
      String text = itemStack.getHoverName().getString().toLowerCase();
      return text.contains("torch") || text.contains("факел");
   }

   private static boolean m_4_boolean(ItemStack itemStack) {
      return !(itemStack.getItem() instanceof BlockItem)
         && !isWeaponOrTool(itemStack)
         && !isSwordOrAxe(itemStack)
         && !(itemStack.getItem() instanceof FishingRodItem)
         && !(itemStack.getItem() instanceof BucketItem)
         && itemStack.getUseAnimation() != ItemUseAnimation.BOW
         && itemStack.getUseAnimation() != ItemUseAnimation.SPEAR
         && itemStack.getUseAnimation() != ItemUseAnimation.BLOCK;
   }

   private static float m_0_float(ItemStack itemStack) {
      float floatValue17 = 0.0F;
      ItemAttributeModifiers itemAttributeModifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

      for (Entry entry : itemAttributeModifiers.modifiers()) {
         if (entry.attribute().is(Attributes.ATTACK_DAMAGE) && entry.slot() == EquipmentSlotGroup.MAINHAND) {
            floatValue17 += (float)entry.modifier().amount();
         }
      }

      return floatValue17;
   }

   private static float easeInOutBack(float floatValue17) {
      float floatValue18 = 1.70158F;
      float floatValue19 = floatValue18 * 1.525F;
      if (floatValue17 < 0.5F) {
         float floatValue20 = 2.0F * floatValue17;
         return floatValue20 * floatValue20 * ((floatValue19 + 1.0F) * floatValue20 - floatValue19) * 0.5F;
      } else {
         float floatValue21 = 2.0F * floatValue17 - 2.0F;
         return (floatValue21 * floatValue21 * ((floatValue19 + 1.0F) * floatValue21 + floatValue19) + 2.0F) * 0.5F;
      }
   }

   private static float m_0_float(float floatValue17) {
      return floatValue17 < 0.6F ? Mth.sin(Mth.clamp(floatValue17, 0.0F, 0.12506F) * 12.56F) : Mth.sin(Mth.clamp(floatValue17, 0.62532F, 0.75038F) * 12.56F);
   }

   public static void m_9_void() {
      double doubleValue4 = System.nanoTime() / 1.0E9;
      doubleValue2 = Math.min(0.05, Math.max(0.0, doubleValue4 - doubleValue));
      doubleValue = doubleValue4;
      flag3 = false;
   }

   private static float m_9_float(float floatValue17, float floatValue18) {
      if (floatValue18 <= floatValue17) {
         return floatValue18;
      }

      float floatValue19 = Mth.clamp((float)(doubleValue2 * 30.0), 0.0F, 1.0F);
      float floatValue20 = Mth.lerp(floatValue19, floatValue17, floatValue18);
      return Math.abs(floatValue20 - floatValue18) < 0.001F ? floatValue18 : floatValue20;
   }

   public static boolean m_3(AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand, ItemStack itemStack) {
      return isSwingAnimationEnabled()
         && !(itemStack.getItem() instanceof MapItem)
         && !(itemStack.getItem() instanceof CrossbowItem)
         && (!abstractClientPlayer.isUsingItem() || abstractClientPlayer.getUsedItemHand() != interactionHand)
         && !abstractClientPlayer.isAutoSpinAttack();
   }

   public static boolean m_8(AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand, ItemStack itemStack) {
      return isSwingAnimationEnabled() && itemStack.getUseAnimation() == ItemUseAnimation.BOW && abstractClientPlayer.isUsingItem() && abstractClientPlayer.getUsedItemHand() == interactionHand;
   }

   public static boolean m_1(AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand, ItemStack itemStack) {
      ItemStack itemStack2 = !abstractClientPlayer.getUseItem().isEmpty() && abstractClientPlayer.getUsedItemHand() == interactionHand ? abstractClientPlayer.getUseItem() : itemStack;
      ItemUseAnimation itemUseAnimation = itemStack2.getUseAnimation();
      return isSwingAnimationEnabled() && (itemUseAnimation == ItemUseAnimation.EAT || itemUseAnimation == ItemUseAnimation.DRINK) && abstractClientPlayer.isUsingItem() && abstractClientPlayer.getUsedItemHand() == interactionHand;
   }

   private static void m_3(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int value, float floatValue17, float floatValue18, HumanoidArm humanoidArm) {
      boolean flag4 = humanoidArm != HumanoidArm.LEFT;
      float floatValue19 = flag4 ? 1.0F : -1.0F;
      float floatValue20 = Mth.sqrt(floatValue18);
      float floatValue21 = -0.3F * Mth.sin(floatValue20 * (float) Math.PI);
      float floatValue22 = 0.4F * Mth.sin(floatValue20 * (float) (Math.PI * 2));
      float floatValue23 = -0.4F * Mth.sin(floatValue18 * (float) Math.PI);
      poseStack.translate(floatValue19 * (floatValue21 + 0.64000005F), floatValue22 + -0.6F + floatValue17 * -0.6F, floatValue23 + -0.71999997F);
      poseStack.mulPose(Axis.YP.rotationDegrees(floatValue19 * 45.0F));
      float floatValue24 = Mth.sin(floatValue18 * floatValue18 * (float) Math.PI);
      float floatValue25 = Mth.sin(floatValue20 * (float) Math.PI);
      poseStack.mulPose(Axis.YP.rotationDegrees(floatValue19 * floatValue25 * 70.0F));
      poseStack.mulPose(Axis.ZP.rotationDegrees(floatValue19 * floatValue24 * -20.0F));
      LocalPlayer localPlayer = getMinecraft().player;
      poseStack.translate(floatValue19 * -1.0F, 3.6F, 3.5F);
      poseStack.mulPose(Axis.ZP.rotationDegrees(floatValue19 * 120.0F));
      poseStack.mulPose(Axis.XP.rotationDegrees(200.0F));
      poseStack.mulPose(Axis.YP.rotationDegrees(floatValue19 * -135.0F));
      poseStack.translate(floatValue19 * 5.6F, 0.0F, 0.0F);
      AvatarRenderer avatarRenderer = getMinecraft().getEntityRenderDispatcher().getPlayerRenderer(localPlayer);
      Identifier identifier = localPlayer.getSkin().body().texturePath();
      if (flag4) {
         avatarRenderer.renderRightHand(poseStack, submitNodeCollector, value, identifier, localPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE));
      } else {
         avatarRenderer.renderLeftHand(poseStack, submitNodeCollector, value, identifier, localPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE));
      }
   }

   private static void m_5(AbstractClientPlayer abstractClientPlayer, ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int value) {
      if (!itemStack.isEmpty()) {
         ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
         getMinecraft().getItemModelResolver().updateForTopItem(itemStackRenderState, itemStack, itemDisplayContext, abstractClientPlayer.level(), abstractClientPlayer, abstractClientPlayer.getId() + itemDisplayContext.ordinal());
         itemStackRenderState.submit(poseStack, submitNodeCollector, value, OverlayTexture.NO_OVERLAY, 0);
      }
   }

   private static void applyViewModel(PoseStack poseStack, HumanoidArm humanoidArm, ItemStack itemStack) {
      ViewModel.m_4(poseStack, humanoidArm, itemStack);
   }

   private static void m_5(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand, HumanoidArm humanoidArm, ItemStack itemStack, float floatValue17, float floatValue18) {
      float floatValue19 = Mth.lerp(floatValue18, abstractClientPlayer.yRotO, abstractClientPlayer.getYRot());
      double doubleValue4 = Math.toRadians(floatValue19);
      double doubleValue5 = -Math.sin(doubleValue4);
      double doubleValue6 = Math.cos(doubleValue4);
      Vec3 vec3 = abstractClientPlayer.getDeltaMovement();
      double doubleValue7 = vec3.x * doubleValue5 + vec3.z * doubleValue6;
      double doubleValue8 = vec3.x * doubleValue6 - vec3.z * doubleValue5;
      float floatValue20 = abstractClientPlayer.getXRot() != 0.0F ? 90.0F / abstractClientPlayer.getXRot() / 10.0F : 1.0F;
      if (floatValue20 > 1.0F || floatValue20 < 0.0F) {
         floatValue20 = 1.0F;
      }

      boolean flag4 = isCrawling(abstractClientPlayer);
      boolean flag5 = isClimbing(abstractClientPlayer);
      boolean flag6 = abstractClientPlayer.isFallFlying();
      double doubleValue9 = doubleValue2 * 30.0;
      float floatValue21 = interactionHand == InteractionHand.MAIN_HAND ? 1.0F : -1.0F;
      int value = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      if (flag6) {
         if (!flag3) {
            floatValue9 = 0.0F;
            floatValue13 = 0.0F;
            floatValue6 = floatValue6 * (float)Math.pow(0.72, doubleValue9);
            floatValue7 = floatValue7 * (float)Math.pow(0.72, doubleValue9);
            floatValue8 = floatValue8 * (float)Math.pow(0.72, doubleValue9);
            flag3 = true;
         }

         if (!itemStack.isEmpty() && itemStack.getUseAnimation() != ItemUseAnimation.BLOCK) {
            poseStack.translate(0.0, -0.1, 0.1);
         }

         if (isLantern(itemStack)) {
            poseStack.translate(0.0, 0.1, 0.0);
         }
      } else {
         if (!flag3) {
            if (vec3.length() >= 0.08) {
               double doubleValue10 = Math.min(vec3.length(), 0.22);
               double doubleValue11 = Mth.clamp(doubleValue7, -0.22, 0.22);
               double doubleValue12 = Mth.clamp(doubleValue8, -0.22, 0.22);
               floatValue10 = (float)(floatValue10 + 0.1 * doubleValue10 * 2.0 * doubleValue9);
               floatValue11 = (float)(floatValue11 + 0.1 * doubleValue11 * 4.0 * doubleValue9);
               floatValue11 = (float)(floatValue11 + (doubleValue11 > 0.0 ? 0.1 * Math.abs(doubleValue12) * 4.0 * doubleValue9 : 0.1 * Math.abs(doubleValue12) * -4.0 * doubleValue9));
            }

            if (vec3.y > 0.0) {
               floatValue12 = (float)(floatValue12 + 0.1 * doubleValue9);
            }

            if (vec3.y < 0.0) {
               floatValue12 = (float)(floatValue12 - 0.1 * doubleValue9);
            }

            float floatValue22 = abstractClientPlayer.onGround() ? 0.0F : (float)Mth.clamp(vec3.y, -0.42, 0.42);
            floatValue6 = (float)(floatValue6 + floatValue22 * 0.015 * doubleValue9);
            floatValue6 = (float)(floatValue6 - 0.1 * floatValue6 * doubleValue9);
            floatValue6 = (float)(floatValue6 * Math.pow(0.88, doubleValue9));
            floatValue7 = (float)(floatValue7 + floatValue22 * 0.015 * doubleValue9);
            floatValue7 = (float)(floatValue7 - 0.1 * floatValue8 * doubleValue9);
            floatValue7 = (float)(floatValue7 * Math.pow(0.88, doubleValue9));
            floatValue8 = (float)(floatValue8 + floatValue7 * doubleValue9);
            if (abstractClientPlayer.isInWater() && !abstractClientPlayer.isSwimming() && !abstractClientPlayer.isEyeInFluid(FluidTags.WATER)) {
               floatValue13 = (float)(floatValue13 + 0.1 * doubleValue9);
               if (floatValue13 > 1.0F) {
                  floatValue13 = 1.0F;
               }
            } else {
               floatValue13 = (float)(floatValue13 * Math.pow(0.88, doubleValue9));
            }

            flag3 = true;
         }

         if ((flag4 || flag5) && !abstractClientPlayer.isUsingItem() && floatValue17 == 0.0F) {
            floatValue9 = (float)(floatValue9 + 0.1 * doubleValue9);
            if (floatValue9 > 1.0F) {
               floatValue9 = 1.0F;
            }

            if (!isLantern(itemStack)) {
               poseStack.mulPose(Axis.XP.rotationDegrees(-20.0F * floatValue9));
            }
         } else {
            floatValue9 = (float)(floatValue9 * Math.pow(0.88, doubleValue9));
         }

         if (floatValue17 == 0.0F) {
            float floatValue23 = abstractClientPlayer.getXRot();
            poseStack.translate(floatValue21 > 0.0F ? floatValue23 / 650.0F * floatValue9 * -1.0F : floatValue23 / 650.0F * floatValue9, 0.0F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(floatValue23 * floatValue9));
         }

         if (!isLantern(itemStack)) {
            poseStack.translate(0.0, 0.0, abstractClientPlayer.getXRot() / 120.0F * floatValue9);
         } else if (floatValue17 == 0.0F) {
            poseStack.translate(0.0, 0.0, abstractClientPlayer.getXRot() / 80.0F * floatValue9);
         }

         if (flag5 && !isLantern(itemStack) && !abstractClientPlayer.isUsingItem()) {
            poseStack.translate(0.0, 0.1, -0.2);
         }

         poseStack.translate(0.0, 0.02 * floatValue13, 0.0);
         poseStack.mulPose(Axis.ZP.rotationDegrees(8.0F * floatValue21 * floatValue13));
         poseStack.translate(0.0, -floatValue6, 0.0);
         poseStack.translate(0.0, Math.sin(abstractClientPlayer.tickCount * 0.1) * 0.007 * value, 0.0);
         poseStack.mulPose(Axis.YP.rotationDegrees(0.15F * Mth.sin(abstractClientPlayer.tickCount * 0.15F) * value));
         if ((!itemStack.isEmpty() || flag4 || flag5 || abstractClientPlayer.isSwimming()) && itemStack.getUseAnimation() != ItemUseAnimation.BLOCK) {
            poseStack.translate(0.0, -0.1, 0.1);
         }

         if (isLantern(itemStack)) {
            poseStack.translate(0.0, 0.1, 0.0);
            if (abstractClientPlayer.isSwimming()) {
               poseStack.translate(0.0, -0.1, 0.1);
            }
         }

         if (abstractClientPlayer.isSwimming() && floatValue17 == 0.0F) {
            double doubleValue13 = (abstractClientPlayer.tickCount + floatValue18) * 0.2;
            double doubleValue14 = Math.sin(doubleValue13) * 1.5;
            double doubleValue15 = doubleValue14 * 0.8 + doubleValue3 * 0.2;
            poseStack.mulPose(Axis.YP.rotationDegrees((float)(interactionHand == InteractionHand.MAIN_HAND ? doubleValue15 : -doubleValue15)));
            poseStack.translate(0.0, 0.0, doubleValue15 * 0.2);
            doubleValue3 = doubleValue15;
         }

         if ((flag5 || flag4) && !abstractClientPlayer.isUsingItem() && floatValue17 == 0.0F) {
            float floatValue24 = Mth.sin(floatValue11 * 4.0F);
            float floatValue25 = Mth.cos(floatValue11 * 4.0F);
            if (isLantern(itemStack)) {
               floatValue24 *= 0.14F;
               floatValue25 *= 0.14F;
            }

            poseStack.translate(0.2 * floatValue24, 0.3 * floatValue24 * value, -0.2 * floatValue24 * value * floatValue20);
            poseStack.mulPose(Axis.YP.rotationDegrees(25.0F * floatValue24));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.clamp(20.0F * floatValue25 * value, 0.0F, 20.0F)));
         }
      }
   }

   private static void m_5(PoseStack poseStack, HumanoidArm humanoidArm, float floatValue17, float floatValue18) {
      int value = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      float floatValue19 = Mth.sin(floatValue18 * (float) Math.PI);
      poseStack.translate(value, -floatValue17 * 0.3, 0.3);
      poseStack.mulPose(Axis.YP.rotationDegrees(45.0F * value));
      poseStack.mulPose(Axis.ZP.rotationDegrees(-40.0F * value));
      poseStack.mulPose(Axis.XP.rotationDegrees(30.0F));
      poseStack.mulPose(Axis.YP.rotationDegrees(value * (45.0F + floatValue19 * 0.0F)));
      poseStack.mulPose(Axis.YP.rotationDegrees(value * -45.0F));
      poseStack.scale(0.9F, 0.9F, 0.9F);
   }

   private static void m_3(PoseStack poseStack, ItemStack itemStack, HumanoidArm humanoidArm) {
      int value = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      if (isLantern(itemStack)) {
         poseStack.translate(0.1 * value, 0.0, -0.1);
         poseStack.mulPose(Axis.XP.rotationDegrees(10.0F));
      } else {
         if (itemStack.getUseAnimation() == ItemUseAnimation.BLOCK) {
            poseStack.translate(0.0, -0.2, 0.0);
         }
      }
   }

   private static void m_2(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, HumanoidArm humanoidArm, float floatValue17) {
      float floatValue18 = (float)(doubleValue2 * 30.0);
      int value = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      float floatValue19 = abstractClientPlayer.yHeadRotO - abstractClientPlayer.getYHeadRot();
      float floatValue20 = abstractClientPlayer.xRotO - abstractClientPlayer.getXRot();
      floatValue3 += floatValue19 * 0.015F * floatValue18;
      floatValue3 += floatValue17 * 2.0F * floatValue18;
      floatValue4 += floatValue20 * 0.015F * floatValue18;
      floatValue3 = floatValue3 - 0.1F * floatValue * floatValue18;
      floatValue4 = floatValue4 - 0.1F * floatValue2 * floatValue18;
      floatValue3 = (float)(floatValue3 * Math.pow(0.88, floatValue18));
      floatValue4 = (float)(floatValue4 * Math.pow(0.88, floatValue18));
      floatValue = floatValue + floatValue3 * floatValue18;
      floatValue2 = floatValue2 + floatValue4 * floatValue18;
      double doubleValue4 = abstractClientPlayer.getDeltaMovement().length();
      floatValue5 = (float)(floatValue5 + (value > 0 ? doubleValue4 * -15.0 - floatValue5 : doubleValue4 * 15.0 - floatValue5) * 0.1 * floatValue18);
      if (doubleValue4 > 0.09 && (abstractClientPlayer.onGround() || abstractClientPlayer.isSwimming() || isClimbing(abstractClientPlayer)) && getMinecraft().options.bobView().get()) {
         floatValue3 = floatValue3 + (float)((Math.random() < 0.5 ? -5.5 : 5.5) * doubleValue4 * floatValue18);
      }

      poseStack.translate(0.0, 0.0, -0.1);
      poseStack.mulPose(Axis.YN.rotationDegrees(35.0F * value + floatValue));
      poseStack.mulPose(Axis.XP.rotationDegrees(15.0F + floatValue2));
      poseStack.mulPose(Axis.ZP.rotationDegrees(75.0F * value + floatValue5));
      poseStack.translate(0.3 * value, -0.35, 0.0);
      poseStack.translate(0.0, 0.0, 0.1);
      poseStack.scale(1.5F, 1.5F, 1.5F);
   }

   private static void m_7(PoseStack poseStack, float floatValue17, float floatValue18, float floatValue19) {
      poseStack.translate(0.1 * floatValue17 * floatValue18, 0.1 * floatValue18, -0.1 * floatValue19);
      poseStack.mulPose(Axis.XN.rotationDegrees(-30.0F * floatValue18));
      poseStack.mulPose(Axis.ZP.rotationDegrees(-10.0F * floatValue18 * floatValue17));
      poseStack.mulPose(Axis.XN.rotationDegrees(40.0F * floatValue19));
      poseStack.mulPose(Axis.YP.rotationDegrees(10.0F * floatValue19 * floatValue17));
   }

   private static void m_5(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand, ItemStack itemStack, float floatValue17) {
      boolean flag4 = interactionHand == InteractionHand.MAIN_HAND;
      if (abstractClientPlayer.getMainArm() == HumanoidArm.LEFT) {
         flag4 = !flag4;
      }

      float floatValue18 = flag4 ? 1.0F : -1.0F;
      float floatValue19 = interactionHand == InteractionHand.MAIN_HAND ? 1.0F : -1.0F;
      float floatValue20 = m_0_float(floatValue17);
      float floatValue21 = easeInOutBack(Mth.sin(floatValue17 * (float) Math.PI));
      if ((flag2 || itemStack.getItem() instanceof AxeItem || itemStack.getUseAnimation() == ItemUseAnimation.SPEAR || itemStack.getUseAnimation() == ItemUseAnimation.BLOCK)
         && !isShovel(itemStack)) {
         if (isSwordOrAxe(itemStack)) {
            poseStack.translate(0.8 * floatValue18 * floatValue20, 0.3 * floatValue20, -0.5 * floatValue21);
            poseStack.mulPose(Axis.YP.rotationDegrees(15.0F * floatValue20 * floatValue18));
            poseStack.mulPose(Axis.XN.rotationDegrees(-20.0F * floatValue20));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-70.0F * floatValue20 * floatValue18));
            poseStack.mulPose(Axis.XN.rotationDegrees((itemStack.is(ItemTags.SWORDS) ? 40.0F : 30.0F) * floatValue21));
         } else if (itemStack.getUseAnimation() == ItemUseAnimation.SPEAR) {
            poseStack.translate(0.0, 0.0, 0.45 * floatValue20);
            poseStack.translate(-0.25 * floatValue19 * floatValue21, -0.35 * floatValue20, -0.6 * floatValue21);
            poseStack.translate(0.0, 0.1 * floatValue21, 0.0);
            poseStack.mulPose(Axis.YP.rotationDegrees(15.0F * floatValue20 * floatValue18));
            poseStack.mulPose(Axis.ZP.rotationDegrees(30.0F * floatValue20 * floatValue18));
         } else if (isWeaponOrTool(itemStack) && itemStack.getUseAnimation() != ItemUseAnimation.BLOCK) {
            poseStack.translate(0.1 * floatValue18 * floatValue20, 0.1 * floatValue20, -0.5 * floatValue21);
            poseStack.mulPose(Axis.XN.rotationDegrees(-30.0F * floatValue20));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-20.0F * floatValue20 * floatValue18));
            poseStack.mulPose(Axis.XN.rotationDegrees(40.0F * floatValue21));
         } else if (itemStack.getUseAnimation() != ItemUseAnimation.BLOCK) {
            poseStack.translate(0.1 * floatValue18 * floatValue20, 0.1 * floatValue20, -0.1 * floatValue21);
            poseStack.mulPose(Axis.XN.rotationDegrees(-30.0F * floatValue20));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-10.0F * floatValue20 * floatValue18));
            poseStack.mulPose(Axis.XN.rotationDegrees(40.0F * floatValue21));
            poseStack.mulPose(Axis.YP.rotationDegrees(10.0F * floatValue21 * floatValue18));
         } else {
            poseStack.translate(0.1 * floatValue18 * floatValue20, 0.1 * floatValue20, -0.2 * floatValue21);
            poseStack.mulPose(Axis.XN.rotationDegrees(-10.0F * floatValue20));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-10.0F * floatValue20 * floatValue18));
            poseStack.mulPose(Axis.XN.rotationDegrees(20.0F * floatValue21));
         }
      } else if (isShovel(itemStack)) {
         poseStack.translate(0.0, 0.15 * floatValue20, -0.25 * floatValue20);
         poseStack.translate(0.0, 0.0, -0.2 * floatValue21);
         poseStack.mulPose(Axis.YP.rotationDegrees(15.0F * floatValue20));
         poseStack.mulPose(Axis.XP.rotationDegrees(-35.0F * floatValue20));
         poseStack.mulPose(Axis.XP.rotationDegrees(30.0F * floatValue21));
      } else if (itemStack.is(ItemTags.SWORDS)) {
         poseStack.translate(-0.55 * floatValue18 * floatValue20, -0.8 * floatValue20, -0.77 * floatValue21);
         poseStack.mulPose(Axis.YP.rotationDegrees(5.0F * floatValue20 * floatValue18));
         poseStack.mulPose(Axis.XN.rotationDegrees(-30.0F * floatValue20));
         poseStack.mulPose(Axis.ZP.rotationDegrees(70.0F * floatValue20 * floatValue18));
         poseStack.mulPose(Axis.XN.rotationDegrees(50.0F * floatValue21));
      } else if (isWeaponOrTool(itemStack)) {
         poseStack.translate(0.1 * floatValue18 * floatValue20, 0.1 * floatValue20, -0.5 * floatValue21);
         poseStack.mulPose(Axis.XN.rotationDegrees(-30.0F * floatValue20));
         poseStack.mulPose(Axis.ZP.rotationDegrees(-20.0F * floatValue20 * floatValue18));
         poseStack.mulPose(Axis.XN.rotationDegrees(40.0F * floatValue21));
      } else {
         m_7(poseStack, floatValue18, floatValue20, floatValue21);
      }
   }

   private static void m_4(PoseStack poseStack, float floatValue17, float floatValue18) {
      if (!(floatValue18 <= 0.1F)) {
         float floatValue19 = Mth.sin((floatValue17 - 0.1F) * 1.3F);
         float floatValue20 = floatValue19 * (floatValue18 - 0.1F);
         poseStack.translate(0.0, floatValue20 * 0.004, 0.0);
      }
   }

   private static void m_1(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand, HumanoidArm humanoidArm, ItemStack itemStack, float floatValue17) {
      int value = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      boolean flag4 = interactionHand == InteractionHand.MAIN_HAND;
      if (abstractClientPlayer.getMainArm() == HumanoidArm.LEFT) {
         flag4 = !flag4;
      }

      poseStack.translate(-0.3 * value, 0.65, -0.1);
      poseStack.mulPose(Axis.YP.rotationDegrees(-65.0F * value));
      poseStack.mulPose(Axis.XP.rotationDegrees(10.0F));
      if (itemStack.getItem() instanceof BlockItem && !(itemStack.getItem() instanceof BucketItem) && itemStack.getUseAnimation() != ItemUseAnimation.EAT) {
         if (isTorch(itemStack)) {
            poseStack.scale(1.5F, 1.5F, 1.5F);
            poseStack.mulPose(Axis.YN.rotationDegrees(25.0F * value));
            poseStack.mulPose(Axis.XP.rotationDegrees(5.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(75.0F * value));
            poseStack.translate(0.2 * value, 0.2, 0.05);
         } else if (m_2_boolean(itemStack)) {
            poseStack.translate(0.0, 0.0, -0.1);
            poseStack.mulPose(Axis.YN.rotationDegrees(5.0F * value));
            poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(75.0F * value));
         } else if (isLantern(itemStack)) {
            m_2(poseStack, abstractClientPlayer, humanoidArm, floatValue17);
         } else {
            poseStack.mulPose(Axis.YN.rotationDegrees(25.0F * value));
            poseStack.mulPose(Axis.XP.rotationDegrees(5.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(75.0F * value));
            poseStack.translate(0.2 * value, 0.2, 0.05);
         }
      } else if (m_4_boolean(itemStack) && m_0_float(itemStack) == 0.0F) {
         poseStack.mulPose(Axis.YN.rotationDegrees(5.0F * value));
         poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
         poseStack.mulPose(Axis.ZP.rotationDegrees(75.0F * value));
         poseStack.translate(0.0, -0.05, -0.1);
         poseStack.scale(0.7F, 0.7F, 0.7F);
      } else if (itemStack.getUseAnimation() == ItemUseAnimation.BLOCK && itemStack.getUseAnimation() != ItemUseAnimation.SPEAR) {
         poseStack.mulPose(Axis.ZP.rotationDegrees(160.0F * value));
         poseStack.mulPose(Axis.YP.rotationDegrees(-60.0F * value));
         poseStack.mulPose(Axis.XP.rotationDegrees(-70.0F));
         poseStack.scale(0.75F, 0.75F, 0.75F);
         poseStack.translate(0.15 * value, flag4 ? 0.35 : 0.45, flag4 ? -0.15 : -0.1);
         poseStack.translate(0.17 * value, 0.0, 0.3);
         poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F * value));
      } else if (itemStack.getUseAnimation() == ItemUseAnimation.SPEAR) {
         poseStack.mulPose(Axis.YN.rotationDegrees(75.0F * value));
         poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
         poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F * value));
         poseStack.translate(-0.3 * value, 0.0, 0.0);
      } else {
         poseStack.mulPose(Axis.YN.rotationDegrees(75.0F * value));
         poseStack.mulPose(Axis.XP.rotationDegrees(70.0F));
         poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F * value));
         if (itemStack.getUseAnimation() != ItemUseAnimation.BLOCK) {
            poseStack.scale(1.2F, 1.2F, 1.2F);
         }

         if (itemStack.getUseAnimation() == ItemUseAnimation.BOW && !abstractClientPlayer.isUsingItem()) {
            poseStack.translate(-0.1 * value, -0.2, 0.0);
         }
      }
   }

   private static void m_8(
      AbstractClientPlayer abstractClientPlayer,
      float floatValue17,
      InteractionHand interactionHand,
      float floatValue18,
      ItemStack itemStack,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int value,
      HumanoidArm humanoidArm
   ) {
      ItemStack itemStack2 = !abstractClientPlayer.getUseItem().isEmpty() && abstractClientPlayer.getUsedItemHand() == interactionHand ? abstractClientPlayer.getUseItem() : itemStack;
      int value2 = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
      float floatValue19 = itemStack2.getUseDuration(abstractClientPlayer) - (abstractClientPlayer.getUseItemRemainingTicks() - floatValue17 + 1.0F);
      float floatValue20 = Mth.clamp(floatValue19 / 5.0F, 0.0F, 1.0F);
      float floatValue21 = Mth.sin(floatValue19 / 2.0F * (float) Math.PI) * 0.1F;
      poseStack.pushPose();
      applyViewModel(poseStack, humanoidArm, itemStack);
      poseStack.translate(value2, 0.1, 0.3);
      poseStack.translate(0.2 * value2 * floatValue20, -0.7 * floatValue20, -0.2 * floatValue20);
      poseStack.translate(0.0, -0.2 * floatValue21, -0.2 * floatValue21);
      poseStack.translate(0.0, 0.1 * easeInOutBack(Mth.sin(floatValue20 * (float) Math.PI)), 0.0);
      poseStack.mulPose(Axis.YP.rotationDegrees(45.0F * value2));
      poseStack.mulPose(Axis.ZP.rotationDegrees(-40.0F * value2));
      poseStack.mulPose(Axis.XP.rotationDegrees(30.0F));
      poseStack.scale(0.9F, 0.9F, 0.9F);
      poseStack.mulPose(Axis.YP.rotationDegrees(45.0F * floatValue20 * value2));
      m_3(poseStack, submitNodeCollector, value, 0.0F, floatValue18, humanoidArm);
      m_1(poseStack, abstractClientPlayer, interactionHand, humanoidArm, itemStack2, floatValue18);
      m_5(abstractClientPlayer, itemStack2, humanoidArm == HumanoidArm.RIGHT ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, poseStack, submitNodeCollector, value);
      poseStack.popPose();
      flag = getMinecraft().options.keyAttack.isDown();
   }

   private static void m_6(
      AbstractClientPlayer abstractClientPlayer,
      float floatValue17,
      InteractionHand interactionHand,
      float floatValue18,
      ItemStack itemStack,
      float floatValue19,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int value,
      HumanoidArm humanoidArm
   ) {
      boolean flag4 = humanoidArm == HumanoidArm.RIGHT;
      int value2 = flag4 ? 1 : -1;
      float floatValue20 = itemStack.getUseDuration(abstractClientPlayer) - (abstractClientPlayer.getUseItemRemainingTicks() - floatValue17 + 1.0F);
      float floatValue21 = Mth.clamp(floatValue20 / 20.0F, 0.0F, 1.0F);
      poseStack.pushPose();
      applyViewModel(poseStack, humanoidArm, itemStack);
      m_5(poseStack, abstractClientPlayer, interactionHand, humanoidArm, itemStack, floatValue18, floatValue17);
      poseStack.pushPose();
      m_4(poseStack, floatValue20, floatValue21);
      poseStack.translate(flag4 ? -0.1 : 0.1, 0.0, floatValue21 * 0.15);
      m_3(poseStack, submitNodeCollector, value, floatValue19, floatValue18, humanoidArm);
      poseStack.popPose();
      poseStack.pushPose();
      poseStack.translate(flag4 ? -0.5 : 0.5, -0.45, 0.1);
      poseStack.mulPose(Axis.XP.rotation(0.3F));
      if (flag4) {
         poseStack.mulPose(Axis.ZN.rotation(-0.3F));
         poseStack.mulPose(Axis.YN.rotation(1.0F));
         m_3(poseStack, submitNodeCollector, value, floatValue19, floatValue18, humanoidArm.getOpposite());
         poseStack.mulPose(Axis.YN.rotation(2.5F));
      } else {
         poseStack.mulPose(Axis.ZP.rotation(-0.3F));
         poseStack.mulPose(Axis.YP.rotation(1.0F));
         m_3(poseStack, submitNodeCollector, value, floatValue19, floatValue18, humanoidArm.getOpposite());
         poseStack.mulPose(Axis.YP.rotation(2.5F));
      }

      poseStack.translate(flag4 ? -0.65 : 0.65, -0.35, 0.27);
      poseStack.popPose();
      poseStack.mulPose(Axis.XN.rotationDegrees(75.0F));
      poseStack.mulPose(Axis.ZN.rotationDegrees(-15.0F * value2));
      poseStack.translate(0.8 * value2, -floatValue19 * 0.3, -0.1);
      m_4(poseStack, floatValue20, floatValue21);
      m_1(poseStack, abstractClientPlayer, interactionHand, humanoidArm, itemStack, floatValue18);
      m_5(abstractClientPlayer, itemStack, flag4 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, poseStack, submitNodeCollector, value);
      poseStack.popPose();
      flag = getMinecraft().options.keyAttack.isDown();
   }

   private static void m_5(
      AbstractClientPlayer abstractClientPlayer,
      float floatValue17,
      InteractionHand interactionHand,
      float floatValue18,
      ItemStack itemStack,
      float floatValue19,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int value,
      HumanoidArm humanoidArm
   ) {
      boolean flag4 = humanoidArm == HumanoidArm.RIGHT;
      poseStack.pushPose();
      applyViewModel(poseStack, humanoidArm, itemStack);
      if (itemStack.isEmpty()) {
         m_5(poseStack, abstractClientPlayer, interactionHand, itemStack, floatValue18);
         m_5(poseStack, abstractClientPlayer, interactionHand, humanoidArm, itemStack, floatValue18, floatValue17);
         m_5(poseStack, humanoidArm, floatValue19, floatValue18);
         m_3(poseStack, submitNodeCollector, value, 0.0F, 0.0F, humanoidArm);
         poseStack.popPose();
      } else {
         m_5(poseStack, abstractClientPlayer, interactionHand, itemStack, floatValue18);
         m_5(poseStack, abstractClientPlayer, interactionHand, humanoidArm, itemStack, floatValue18, floatValue17);
         m_3(poseStack, itemStack, humanoidArm);
         m_5(poseStack, humanoidArm, floatValue19, floatValue18);
         m_3(poseStack, submitNodeCollector, value, 0.0F, 0.0F, humanoidArm);
         m_1(poseStack, abstractClientPlayer, interactionHand, humanoidArm, itemStack, floatValue18);
         m_5(abstractClientPlayer, itemStack, flag4 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, poseStack, submitNodeCollector, value);
         poseStack.popPose();
         flag = getMinecraft().options.keyAttack.isDown();
      }
   }

   public static void m_3_void(float floatValue17) {
      if (floatValue17 > 0.0F && floatValue14 == 0.0F) {
         flag2 = !flag2;
      }

      floatValue14 = floatValue17;
   }

   public static boolean m_3(
      AbstractClientPlayer abstractClientPlayer, float floatValue17, InteractionHand interactionHand, float floatValue18, ItemStack itemStack, float floatValue19, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int value
   ) {
      if (interactionHand == InteractionHand.MAIN_HAND) {
         m_9_void();
      }

      boolean flag4 = interactionHand == InteractionHand.MAIN_HAND;
      HumanoidArm humanoidArm = flag4 ? abstractClientPlayer.getMainArm() : abstractClientPlayer.getMainArm().getOpposite();
      if (flag4) {
         floatValue15 = m_9_float(floatValue15, floatValue18);
         floatValue18 = floatValue15;
      } else {
         floatValue16 = m_9_float(floatValue16, floatValue18);
         floatValue18 = floatValue16;
      }

      if (m_8(abstractClientPlayer, interactionHand, itemStack)) {
         m_6(abstractClientPlayer, floatValue17, interactionHand, floatValue18, itemStack, floatValue19, poseStack, submitNodeCollector, value, humanoidArm);
         return true;
      } else if (m_1(abstractClientPlayer, interactionHand, itemStack)) {
         m_8(abstractClientPlayer, floatValue17, interactionHand, floatValue18, itemStack, poseStack, submitNodeCollector, value, humanoidArm);
         return true;
      } else if (m_3(abstractClientPlayer, interactionHand, itemStack)) {
         m_5(abstractClientPlayer, floatValue17, interactionHand, floatValue18, itemStack, floatValue19, poseStack, submitNodeCollector, value, humanoidArm);
         return true;
      } else {
         return false;
      }
   }

   public static void m_7(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int value, float floatValue17, float floatValue18, InteractionHand interactionHand) {
      LocalPlayer localPlayer = getMinecraft().player;
      if (localPlayer != null) {
         boolean flag4 = interactionHand == InteractionHand.MAIN_HAND;
         HumanoidArm humanoidArm = flag4 ? localPlayer.getMainArm() : localPlayer.getMainArm().getOpposite();
         poseStack.pushPose();
         m_3(poseStack, submitNodeCollector, value, floatValue17, floatValue18, humanoidArm);
         poseStack.popPose();
      }
   }
}
