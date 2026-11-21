package wtf.onetap.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;
import wtf.onetap.Onetap;
import wtf.onetap.module.render.SwingAnimation;
import wtf.onetap.module.render.ViewModel;

public class ViewModelRenderer {
   private static final Quaternionf QUATERNIONF = new Quaternionf();
   private static final Set<Item> ITEM_SET = Stream.of(
         List.of(
            Items.STRING,
            Items.RESIN_CLUMP,
            Items.VINE,
            Items.KELP,
            Items.SEAGRASS,
            Items.IRON_BARS,
            Items.GLASS_PANE,
            Items.LADDER,
            Items.OAK_SIGN,
            Items.SPRUCE_SIGN,
            Items.BIRCH_SIGN,
            Items.JUNGLE_SIGN,
            Items.ACACIA_SIGN,
            Items.DARK_OAK_SIGN,
            Items.MANGROVE_SIGN,
            Items.CHERRY_SIGN,
            Items.BAMBOO_SIGN,
            Items.CRIMSON_SIGN,
            Items.WARPED_SIGN,
            Items.PALE_OAK_SIGN,
            Items.TRIPWIRE_HOOK,
            Items.HOPPER,
            Items.CAULDRON,
            Items.RAIL,
            Items.POWERED_RAIL,
            Items.DETECTOR_RAIL,
            Items.ACTIVATOR_RAIL,
            Items.REPEATER,
            Items.COMPARATOR,
            Items.TWISTING_VINES,
            Items.WEEPING_VINES,
            Items.SNIFFER_EGG,
            Items.CANDLE,
            Items.FROGSPAWN,
            Items.LIGHT,
            Items.STRUCTURE_VOID,
            Items.BARRIER,
            Items.CARROT,
            Items.POWDER_SNOW_BUCKET,
            Items.GLOW_BERRIES,
            Items.POTATO,
            Items.SWEET_BERRIES,
            Items.REDSTONE
         ),
         Items.STAINED_GLASS_PANE.asList(),
         Items.DYED_CANDLE.asList()
      )
      .flatMap(Collection::stream)
      .collect(Collectors.toUnmodifiableSet());
   private static float floatValue;
   private static float floatValue2;
   private static boolean flag;
   private static boolean flag2;
   private static boolean flag3;
   private static boolean flag4;
   private static float floatValue3;
   private static float floatValue4;
   private static Item item = Items.AIR;
   private static Item item2 = Items.AIR;
   private static boolean flag5;
   private static boolean flag6;
   private static int value;
   private static double doubleValue;
   private static boolean flag7;
   private static float floatValue5;
   private static double doubleValue2;
   private static double doubleValue3;
   private static double doubleValue4;
   private static double doubleValue5;
   private static double doubleValue6;
   private static double doubleValue7;
   private static double doubleValue8;
   private static double doubleValue9;
   private static double doubleValue10;
   private static double doubleValue11;
   private static boolean flag8;
   private static boolean flag9;
   private static boolean flag10;
   private static boolean flag11;
   private static boolean flag12;
   private static double doubleValue12;
   private static double doubleValue13;
   private static double doubleValue14;
   private static double doubleValue15;
   private static double doubleValue16;
   private static double doubleValue17;
   private static double doubleValue18;
   private static double doubleValue19;
   private static double doubleValue20;
   private static double doubleValue21;
   private static double doubleValue22;
   private static double doubleValue23;
   private static double doubleValue24;
   private static double doubleValue25;
   private static double doubleValue26;
   private static double doubleValue27;
   private static double doubleValue28;
   private static double doubleValue29;
   private static double doubleValue30;
   private static double doubleValue31;
   private static double doubleValue32;
   private static double doubleValue33;
   private static double doubleValue34;
   private static double doubleValue35;
   private static double doubleValue36;
   private static double doubleValue37;
   private static double doubleValue38;
   private static double doubleValue39;
   private static double doubleValue40;
   private static double doubleValue41;
   private static double doubleValue42;
   private static double doubleValue43;
   private static double doubleValue44;
   private static double doubleValue45;
   private static double doubleValue46;
   private static double doubleValue47;
   private static double doubleValue48;
   private static double doubleValue49;
   private static double doubleValue50;
   private static double doubleValue51;
   private static double doubleValue52;
   private static double doubleValue53;
   private static double doubleValue54;
   private static double doubleValue55;
   private static double doubleValue56;
   private static double doubleValue57;
   private static double doubleValue58;
   private static double doubleValue59;
   private static double doubleValue60;
   private static double doubleValue61;
   private static double doubleValue62;
   private static double doubleValue63;
   private static double doubleValue64;
   private static double doubleValue65;
   private static double doubleValue66;
   private static double doubleValue67;
   private static double doubleValue68;

   private ViewModelRenderer() {
   }

   private static Minecraft getMinecraft() {
      return Minecraft.getInstance();
   }

   public static boolean isSwingAnimationEnabled() {
      SwingAnimation swingAnimation = Onetap.MODULE_MANAGER.get(SwingAnimation.class);
      return swingAnimation != null && swingAnimation.m_7_boolean();
   }

   public static void onSwing(InteractionHand interactionHand) {
      if (isSwingAnimationEnabled() && interactionHand == InteractionHand.MAIN_HAND) {
         value++;
      }
   }

   public static float m_0(LivingEntity livingEntity, ItemStack itemStack, float floatValue6) {
      return isSwingAnimationEnabled() && livingEntity == getMinecraft().player && itemStack.is(Items.BOW) && livingEntity.getUseItem() == itemStack ? floatValue5 : floatValue6;
   }

   public static boolean m_9(LivingEntity livingEntity, ItemStack itemStack, boolean flag13) {
      return isSwingAnimationEnabled() && livingEntity == getMinecraft().player && itemStack.is(Items.BOW) && livingEntity.getUseItem() == itemStack ? flag7 : flag13;
   }

   public static void m_6(LocalPlayer localPlayer) {
      if (isSwingAnimationEnabled()) {
         float floatValue6 = (float)GLFW.glfwGetTime();
         floatValue = getMinecraft().isPaused() ? 0.0F : Math.min(0.05F, floatValue6 - floatValue2);
         floatValue2 = floatValue6;
         boolean flag13 = localPlayer != null && localPlayer.swinging;
         boolean flag14 = flag13 && localPlayer.swingingArm == InteractionHand.MAIN_HAND;
         boolean flag15 = flag13 && localPlayer.swingingArm == InteractionHand.OFF_HAND;
         flag = flag14 && !flag3;
         flag2 = flag15 && !flag4;
         flag3 = flag14;
         flag4 = flag15;
      }
   }

   public static boolean m_4(
      ItemInHandRenderer itemInHandRenderer,
      AbstractClientPlayer abstractClientPlayer,
      InteractionHand interactionHand,
      float floatValue6,
      ItemStack itemStack,
      float floatValue7,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int value2
   ) {
      if (!isSwingAnimationEnabled()) {
         return false;
      }

      boolean flag13 = interactionHand == InteractionHand.MAIN_HAND;
      HumanoidArm humanoidArm = flag13 ? abstractClientPlayer.getMainArm() : abstractClientPlayer.getMainArm().getOpposite();
      boolean flag14 = humanoidArm == HumanoidArm.RIGHT;
      if (flag13) {
         floatValue3 = floatValue6;
         flag5 = itemStack.getItem() != item;
      } else {
         floatValue4 = floatValue6;
         flag6 = itemStack.getItem() != item2;
      }

      boolean flag15 = getMinecraft().gameMode != null && getMinecraft().gameMode.isDestroying();
      ViewModelRenderer.DataRecord c8 = new ViewModelRenderer.DataRecord(abstractClientPlayer, interactionHand, flag13, flag14, flag14 ? 1.0 : -1.0, itemStack, itemStack.getUseAnimation(), floatValue6, floatValue7, floatValue * 30.0, flag15, poseStack);
      poseStack.pushPose();
      ViewModel.m_4(poseStack, humanoidArm, itemStack);
      poseStack.pushPose();
      m_8(c8);
      if (!itemStack.isEmpty()) {
         poseStack.translate(0.0, -0.35, 0.2);
      }

      poseStack.pushPose();
      m_9(c8);
      itemInHandRenderer.renderPlayerArm(poseStack, submitNodeCollector, value2, 0.0F, 0.0F, humanoidArm);
      poseStack.popPose();
      poseStack.pushPose();
      if (!itemStack.isEmpty()) {
         poseStack.translate(0.5 * c8.f_9s, -0.15, -0.85);
         rotateXAround(poseStack, 15.0, 0.5, 0.5, 0.5);
         scale(poseStack, 0.9, 0.9, 0.9);
         m_3(c8);
         ItemDisplayContext itemDisplayContext = flag14 ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
         if (c8.f_0y != ItemUseAnimation.EAT
            && c8.f_0y != ItemUseAnimation.DRINK
            && c8.f_0y != ItemUseAnimation.SPEAR
            && itemStack.has(DataComponents.MAP_ID)) {
            poseStack.pushPose();
            poseStack.translate(-0.05 * c8.f_9s, 0.2, 0.1);
            rotateY(poseStack, -12.0 * c8.f_9s);
            itemInHandRenderer.renderMap(poseStack, submitNodeCollector, value2, itemStack);
            poseStack.popPose();
         } else {
            itemInHandRenderer.renderItem(abstractClientPlayer, itemStack, itemDisplayContext, poseStack, submitNodeCollector, value2);
         }
      }

      poseStack.popPose();
      poseStack.popPose();
      if (flag13) {
         item = itemStack.getItem();
      } else {
         item2 = itemStack.getItem();
      }

      poseStack.popPose();
      return true;
   }

   private static void translateX(PoseStack poseStack, double doubleValue69) {
      poseStack.translate(doubleValue69, 0.0, 0.0);
   }

   private static void translateY(PoseStack poseStack, double doubleValue69) {
      poseStack.translate(0.0, doubleValue69, 0.0);
   }

   private static void translateZ(PoseStack poseStack, double doubleValue69) {
      poseStack.translate(0.0, 0.0, doubleValue69);
   }

   private static void rotateX(PoseStack poseStack, double doubleValue69) {
      poseStack.mulPose(QUATERNIONF.rotationX((float)doubleValue69 * (float) (Math.PI / 180.0)));
   }

   private static void rotateY(PoseStack poseStack, double doubleValue69) {
      poseStack.mulPose(QUATERNIONF.rotationY((float)doubleValue69 * (float) (Math.PI / 180.0)));
   }

   private static void rotateZ(PoseStack poseStack, double doubleValue69) {
      poseStack.mulPose(QUATERNIONF.rotationZ((float)doubleValue69 * (float) (Math.PI / 180.0)));
   }

   private static void rotateXAround(PoseStack poseStack, double doubleValue69, double doubleValue70, double doubleValue71, double doubleValue72) {
      poseStack.rotateAround(QUATERNIONF.rotationX((float)doubleValue69 * (float) (Math.PI / 180.0)), (float)doubleValue70, (float)doubleValue71, (float)doubleValue72);
   }

   private static void rotateYAround(PoseStack poseStack, double doubleValue69, double doubleValue70, double doubleValue71, double doubleValue72) {
      poseStack.rotateAround(QUATERNIONF.rotationY((float)doubleValue69 * (float) (Math.PI / 180.0)), (float)doubleValue70, (float)doubleValue71, (float)doubleValue72);
   }

   private static void rotateZAround(PoseStack poseStack, double doubleValue69, double doubleValue70, double doubleValue71, double doubleValue72) {
      poseStack.rotateAround(QUATERNIONF.rotationZ((float)doubleValue69 * (float) (Math.PI / 180.0)), (float)doubleValue70, (float)doubleValue71, (float)doubleValue72);
   }

   private static void scale(PoseStack poseStack, double doubleValue69, double doubleValue70, double doubleValue71) {
      poseStack.scale((float)doubleValue69, (float)doubleValue70, (float)doubleValue71);
   }

   private static void m_7(PoseStack poseStack, double doubleValue69) {
      poseStack.last().pose().mul(new Matrix4f(1.0F, 0.0F, 0.0F, 0.0F, (float)doubleValue69, 1.0F, (float)doubleValue69, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F));
   }

   private static double clamp01(double doubleValue69) {
      return Mth.clamp(doubleValue69, 0.0, 1.0);
   }

   private static float m_5_float(float floatValue6, boolean flag13) {
      double doubleValue69 = floatValue6 * floatValue6;
      return (float)(3.0 * floatValue6 * (1.0 - floatValue6) * (1.0 - floatValue6) * 0.44 + 3.0 * doubleValue69 * (1.0 - floatValue6) * (flag13 ? 1.0 : 0.94) + doubleValue69 * floatValue6);
   }

   private static double easeInOutBack(double doubleValue69) {
      double doubleValue70 = 2.5949095320324886;
      return doubleValue69 < 0.5
         ? Math.pow(2.0 * doubleValue69, 2.0) * ((doubleValue70 + 1.0) * 2.0 * doubleValue69 - doubleValue70) / 2.0
         : (Math.pow(2.0 * doubleValue69 - 2.0, 2.0) * ((doubleValue70 + 1.0) * (doubleValue69 * 2.0 - 2.0) + doubleValue70) + 2.0) / 2.0;
   }

   private static double easeOutBack(double doubleValue69) {
      double doubleValue70 = 1.70158F;
      return 1.0 + (doubleValue70 + 1.0) * Math.pow(doubleValue69 - 1.0, 3.0) + doubleValue70 * Math.pow(doubleValue69 - 1.0, 2.0);
   }

   private static double easeInBack(double doubleValue69) {
      double doubleValue70 = 1.70158F;
      return (doubleValue70 + 1.0) * doubleValue69 * doubleValue69 * doubleValue69 - doubleValue70 * doubleValue69 * doubleValue69;
   }

   private static double easeInOutSine(double doubleValue69) {
      return -(Math.cos(Math.PI * doubleValue69) - 1.0) / 2.0;
   }

   private static double easeInOutExpo(double doubleValue69) {
      if (doubleValue69 == 0.0) {
         return 0.0;
      } else if (doubleValue69 == 1.0) {
         return 1.0;
      } else {
         return doubleValue69 < 0.5 ? Math.pow(2.0, 20.0 * doubleValue69 - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * doubleValue69 + 10.0)) / 2.0;
      }
   }

   private static double easeOutQuart(double doubleValue69) {
      return 1.0 - Math.pow(1.0 - doubleValue69, 4.0);
   }

   private static void playSound(String text, float floatValue6) {
      LocalPlayer localPlayer = getMinecraft().player;
      if (localPlayer != null) {
         localPlayer.playSound(SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace(text)), floatValue6, 1.0F);
      }
   }

   private static boolean isBlockItem(ItemStack itemStack) {
      return Block.byItem(itemStack.getItem()) != Blocks.AIR;
   }

   private static boolean m_8_boolean(ItemStack itemStack) {
      return Block.byItem(itemStack.getItem()) instanceof LanternBlock;
   }

   private static boolean isPetalItem(ItemStack itemStack) {
      return itemStack.is(Items.PINK_PETALS) || itemStack.is(Items.LEAF_LITTER) || itemStack.is(Items.WILDFLOWERS);
   }

   private static boolean m_9_boolean(ItemStack itemStack) {
      return itemStack.is(ItemTags.BUNDLES)
         || itemStack.is(Items.ENDER_PEARL)
         || itemStack.is(Items.ENDER_EYE)
         || itemStack.getItem() instanceof SplashPotionItem
         || itemStack.getItem() instanceof ProjectileItem
         || itemStack.is(ConventionalItemTags.MUSIC_DISCS)
         || itemStack.is(ConventionalItemTags.NUGGETS)
         || itemStack.is(ItemTags.SKULLS);
   }

   private static float m_5_float(ItemStack itemStack) {
      ItemAttributeModifiers itemAttributeModifiers = itemStack.get(DataComponents.ATTRIBUTE_MODIFIERS);
      if (itemAttributeModifiers == null) {
         return 0.0F;
      }

      float floatValue6 = 0.0F;

      for (Entry entry : itemAttributeModifiers.modifiers()) {
         if (entry.attribute().value() == Attributes.ATTACK_DAMAGE.value()) {
            floatValue6 += (float)entry.modifier().amount();
         }
      }

      return floatValue6;
   }

   private static boolean m_2_boolean(ItemStack itemStack) {
      ItemUseAnimation itemUseAnimation = itemStack.getUseAnimation();
      boolean flag13 = itemStack.getItem() instanceof FishingRodItem
         || itemStack.is(ConventionalItemTags.RODS)
         || itemStack.is(ConventionalItemTags.TOOLS)
         || itemStack.is(ItemTags.SWORDS)
         || itemStack.is(ConventionalItemTags.MACE_TOOLS)
         || itemUseAnimation == ItemUseAnimation.BLOCK;
      return !flag13 && m_5_float(itemStack) <= 0.0F || itemUseAnimation == ItemUseAnimation.EAT || itemUseAnimation == ItemUseAnimation.DRINK || itemUseAnimation == ItemUseAnimation.SPYGLASS;
   }

   private static boolean m_4_boolean(ItemStack itemStack) {
      return isBlockItem(itemStack)
         && !ITEM_SET.contains(itemStack.getItem())
         && !itemStack.is(Items.BAMBOO)
         && !itemStack.is(BlockItemTags.COPPER_GOLEM_STATUES.item())
         && !itemStack.is(ItemTags.HANGING_SIGNS)
         && !itemStack.is(BlockItemTags.DOORS.item())
         && !itemStack.is(ItemTags.SKULLS)
         && !itemStack.is(ItemTags.SIGNS)
         && !isPetalItem(itemStack)
         && !itemStack.is(Items.BELL);
   }

   private static void m_8(ViewModelRenderer.DataRecord c8) {
      PoseStack poseStack = c8.f_5v;
      AbstractClientPlayer abstractClientPlayer = c8.f_1_o;
      ItemStack itemStack = c8.f_1v;
      ItemUseAnimation itemUseAnimation = c8.f_0y;
      boolean flag13 = c8.f_6_k;
      boolean flag14 = abstractClientPlayer.isUsingItem();
      boolean flag15 = c8.m_67_boolean();
      double doubleValue69 = abstractClientPlayer.getDeltaMovement().length();
      double doubleValue70 = abstractClientPlayer.getDeltaMovement().y;
      double doubleValue71 = abstractClientPlayer.getXRot();
      boolean flag16 = itemStack.isEmpty();
      boolean flag17 = isBlockItem(itemStack);
      boolean flag18 = itemStack.is(ItemTags.SWORDS);
      boolean flag19 = itemStack.is(ItemTags.PICKAXES);
      boolean flag20 = itemStack.is(ItemTags.AXES);
      boolean flag21 = itemStack.is(Items.MACE);
      boolean flag22 = itemStack.is(Items.FILLED_MAP);
      boolean flag23 = CrossbowItem.isCharged(itemStack);
      boolean flag24 = abstractClientPlayer.onClimbable();
      boolean flag25 = abstractClientPlayer.isVisuallyCrawling();
      double doubleValue72 = c8.f_9s;
      double doubleValue73 = c8.f_9_r;
      float floatValue6 = itemUseAnimation == ItemUseAnimation.SPEAR ? 0.0F : c8.f_2i;
      float floatValue7 = m_5_float(c8.f_2_z, flag19);
      translateX(poseStack, 0.2 * doubleValue72);
      double doubleValue74 = floatValue7 < 0.70016 ? Math.sin(Mth.clamp(floatValue7, 0.0, 0.308) * 5.1) : Math.sin(Mth.clamp(floatValue7, 0.70016, 1.0) * 5.1 - 2.0);
      double doubleValue75 = floatValue7 < 0.65245 ? Math.sin(Mth.clamp(floatValue7, 0.0, 0.16675) * 3.14 * 3.0) : Math.sin(Mth.clamp(floatValue7, 0.65245, 1.0) * 4.4 - 1.2584);
      doubleValue74 = doubleValue74 * doubleValue74 * doubleValue74;
      double doubleValue76 = clamp01(Math.sin(floatValue7 * 4.78));
      double doubleValue77 = Math.sin(Mth.clamp(floatValue7, 0.16561, 0.49422) * 4.78 * 2.0 + 4.7);
      double doubleValue78 = floatValue7 < 0.65594
         ? Math.sin(Mth.clamp(floatValue7, 0.16561, 0.32991) * 4.78 * 2.0 + 4.7)
         : Math.sin(Mth.clamp(floatValue7, 0.65594, 0.82025) * 4.78 * 2.0 - 4.7);
      boolean flag26 = flag15 && itemUseAnimation == ItemUseAnimation.BOW;
      if (flag26 && !flag13) {
         doubleValue5 += 0.075 * doubleValue73;
      } else if (!flag13) {
         doubleValue5 -= 0.07 * doubleValue73;
      }

      doubleValue5 = clamp01(doubleValue5);
      if (flag26 && !flag13 && doubleValue5 == 1.0) {
         doubleValue6 += 0.025 * doubleValue73;
         doubleValue7 += 0.07 * doubleValue73;
      } else if (!flag13) {
         doubleValue6 -= 0.11 * doubleValue73;
      }

      doubleValue6 = clamp01(doubleValue6);
      if (flag26 && flag13) {
         doubleValue2 += 0.075 * doubleValue73;
      } else if (flag13) {
         doubleValue2 -= 0.07 * doubleValue73;
      }

      doubleValue2 = clamp01(doubleValue2);
      if (flag26 && flag13 && doubleValue2 == 1.0) {
         doubleValue3 += 0.025 * doubleValue73;
         doubleValue4 += 0.07 * doubleValue73;
      } else if (flag13) {
         doubleValue3 = 0.0;
      }

      doubleValue3 = clamp01(doubleValue3);
      double doubleValue79 = flag13 ? doubleValue59 : doubleValue61;
      doubleValue58 = doubleValue58 + floatValue7 * floatValue * 3.0F;
      translateZ(poseStack, flag16 ? -0.08 : -0.16);
      if (flag22 && flag13 && abstractClientPlayer.getOffhandItem().isEmpty()) {
         doubleValue10 += 0.07 * doubleValue73;
      } else if (flag22) {
         doubleValue10 -= 0.07 * doubleValue73;
      }

      doubleValue10 = clamp01(doubleValue10);
      if (flag5 && flag13 && doubleValue22 == 0.0) {
         doubleValue8 = 0.0;
      }

      doubleValue8 = clamp01(doubleValue8 + 0.015 * doubleValue73);
      if (flag13) {
         double doubleValue80 = easeInOutBack(Math.sin(Mth.clamp(doubleValue8, 0.0, 0.5) * 3.14));
         double doubleValue81 = Math.sin(Mth.clamp(doubleValue8, 0.0, 0.125) * 12.56);
         if (itemUseAnimation == ItemUseAnimation.TRIDENT && doubleValue35 > 0.9) {
            poseStack.translate(0.0, -0.15 * doubleValue81, -0.3 * doubleValue81);
            rotateXAround(poseStack, 75.0 * doubleValue81, 0.3 * doubleValue72, -0.4, 0.0);
            rotateXAround(poseStack, -75.0 * doubleValue80, 0.3 * doubleValue72, -0.4, 0.0);
            poseStack.translate(0.0, 0.15 * doubleValue81, 0.3 * doubleValue81);
         } else {
            m_0(poseStack, itemUseAnimation, doubleValue72, doubleValue81, doubleValue80);
         }
      }

      if (flag6) {
         doubleValue9 = 0.0;
      }

      doubleValue9 = clamp01(doubleValue9 + 0.015 * doubleValue73);
      if (!flag13 && doubleValue20 == 0.0) {
         m_0(poseStack, itemUseAnimation, doubleValue72, Math.sin(Mth.clamp(doubleValue9, 0.0, 0.125) * 12.56), easeInOutBack(Math.sin(Mth.clamp(doubleValue9, 0.0, 0.5) * 3.14)));
      }

      if (!abstractClientPlayer.onGround() && floatValue3 == 0.0F && flag13) {
         flag8 = false;
      } else if (flag13 && floatValue3 == 0.0F) {
         flag8 = true;
      }

      if (flag10 != flag && flag13) {
         flag9 = !flag9;
      }

      if (flag25 && doubleValue69 > 0.08) {
         doubleValue55 += doubleValue69 * doubleValue73;
      }

      if (doubleValue71 > 40.0) {
         doubleValue11 += 0.05 * doubleValue73;
      } else {
         doubleValue11 -= 0.095 * doubleValue73;
      }

      doubleValue11 = clamp01(doubleValue11);
      double doubleValue82 = doubleValue14;
      double doubleValue83 = doubleValue13;
      double doubleValue84 = doubleValue17;
      double doubleValue85 = doubleValue16;
      boolean flag27 = flag15 && itemUseAnimation == ItemUseAnimation.SPEAR;
      double doubleValue86 = clamp01(1.0 - clamp01(doubleValue76 * 8.0));
      if (flag27 && flag13) {
         doubleValue12 += 0.08 * doubleValue73;
         doubleValue13 += 0.035 * doubleValue73;
         doubleValue14 += 0.055 * doubleValue73;
      } else if (flag13) {
         doubleValue12 -= 0.08 * doubleValue73;
         doubleValue13 = 0.0;
         doubleValue14 = 0.0;
      }

      doubleValue12 = clamp01(doubleValue12) * doubleValue86;
      doubleValue13 = clamp01(doubleValue13);
      doubleValue14 = clamp01(doubleValue14);
      if (flag27 && !flag13) {
         doubleValue15 += 0.08 * doubleValue73;
         doubleValue16 += 0.035 * doubleValue73;
         doubleValue17 += 0.055 * doubleValue73;
      } else if (!flag13) {
         doubleValue15 -= 0.08 * doubleValue73;
         doubleValue16 = 0.0;
         doubleValue17 = 0.0;
      }

      doubleValue15 = clamp01(doubleValue15) * doubleValue86;
      doubleValue16 = clamp01(doubleValue16);
      doubleValue17 = clamp01(doubleValue17);
      if (doubleValue13 == 0.0) {
         doubleValue13 = Mth.lerp(0.5 * doubleValue73, doubleValue83, doubleValue13);
      }

      if (doubleValue14 == 0.0) {
         doubleValue14 = Mth.lerp(0.5 * doubleValue73, doubleValue82, doubleValue14);
      }

      if (doubleValue16 == 0.0) {
         doubleValue16 = Mth.lerp(0.5 * doubleValue73, doubleValue85, doubleValue16);
      }

      if (doubleValue17 == 0.0) {
         doubleValue17 = Mth.lerp(0.5 * doubleValue73, doubleValue84, doubleValue17);
      }

      boolean flag28 = itemUseAnimation == ItemUseAnimation.EAT || itemUseAnimation == ItemUseAnimation.DRINK || itemUseAnimation == ItemUseAnimation.TOOT_HORN;
      boolean flag29 = flag28 || itemUseAnimation == ItemUseAnimation.BRUSH;
      if (flag15 && flag28 && flag13) {
         doubleValue18 += 0.1 * doubleValue73;
      } else if (flag13) {
         doubleValue18 -= 0.1 * doubleValue73;
      }

      doubleValue18 = clamp01(doubleValue18);
      if (flag15 && flag29 && flag13 && (doubleValue18 == 1.0 || doubleValue29 > 0.4)) {
         doubleValue19 += 0.1 * doubleValue73;
      }

      if (flag15 && flag28 && !flag13) {
         doubleValue20 += 0.1 * doubleValue73;
      } else if (!flag13) {
         doubleValue20 -= 0.1 * doubleValue73;
      }

      doubleValue20 = clamp01(doubleValue20);
      if (flag15 && flag29 && !flag13 && (doubleValue20 == 1.0 || doubleValue30 > 0.4)) {
         doubleValue21 += 0.1 * doubleValue73;
      }

      if (flag15 && itemUseAnimation == ItemUseAnimation.DRINK && flag13 && doubleValue18 == 1.0) {
         doubleValue22 += 0.04 * doubleValue73;
      } else if (flag13) {
         doubleValue22 -= 0.1 * doubleValue73;
      }

      doubleValue22 = clamp01(doubleValue22);
      if (flag15 && itemUseAnimation == ItemUseAnimation.DRINK && !flag13 && doubleValue20 == 1.0) {
         doubleValue23 += 0.04 * doubleValue73;
      } else if (!flag13) {
         doubleValue23 -= 0.1 * doubleValue73;
      }

      doubleValue23 = clamp01(doubleValue23);
      boolean flag30 = flag15 && itemUseAnimation == ItemUseAnimation.CROSSBOW;
      if (flag30 && !flag13) {
         doubleValue26 += 0.1 * doubleValue73;
      } else if (!flag13) {
         doubleValue26 -= 0.1 * doubleValue73;
      }

      doubleValue26 = clamp01(doubleValue26);
      if (flag30 && !flag13 && doubleValue26 == 1.0) {
         doubleValue27 += 0.02 * doubleValue73;
      } else if (!flag13) {
         doubleValue27 -= 0.1 * doubleValue73;
      }

      doubleValue27 = clamp01(doubleValue27);
      if (flag30 && flag13 && !flag23) {
         doubleValue24 += 0.1 * doubleValue73;
      } else if (flag13) {
         doubleValue24 -= 0.1 * doubleValue73;
      }

      doubleValue24 = clamp01(doubleValue24);
      if (flag30 && flag13 && doubleValue24 == 1.0 && !flag23) {
         doubleValue25 += 0.02 * doubleValue73;
      } else if (flag13) {
         doubleValue25 -= 0.1 * doubleValue73;
      }

      doubleValue25 = clamp01(doubleValue25);
      ItemStack itemStack2 = abstractClientPlayer.getMainHandItem();
      ItemUseAnimation itemUseAnimation2 = itemStack2.getUseAnimation();
      boolean flag31 = !CrossbowItem.isCharged(itemStack2)
         && itemUseAnimation2 != ItemUseAnimation.BLOCK
         && itemUseAnimation2 != ItemUseAnimation.EAT
         && itemUseAnimation2 != ItemUseAnimation.TOOT_HORN
         && itemUseAnimation2 != ItemUseAnimation.DRINK
         && itemUseAnimation2 != ItemUseAnimation.BRUSH
         && itemUseAnimation2 != ItemUseAnimation.SPEAR;
      if (!flag13 && flag16 && (!flag14 || !flag31) && !flag24 && !abstractClientPlayer.isSwimming() && !flag25) {
         doubleValue28 += 0.08 * doubleValue73;
      } else if (!flag13 || flag14 && flag31 || flag24 || flag25) {
         doubleValue28 -= 0.08 * doubleValue73;
      }

      doubleValue28 = clamp01(doubleValue28);
      if (flag15 && itemUseAnimation == ItemUseAnimation.BRUSH && flag13) {
         doubleValue29 += 0.1 * doubleValue73;
      } else if (flag13) {
         doubleValue29 -= 0.1 * doubleValue73;
      }

      doubleValue29 = clamp01(doubleValue29);
      if (flag15 && itemUseAnimation == ItemUseAnimation.BRUSH && !flag13) {
         doubleValue30 += 0.1 * doubleValue73;
      } else if (!flag13) {
         doubleValue30 -= 0.1 * doubleValue73;
      }

      doubleValue30 = clamp01(doubleValue30);
      boolean flag32 = flag15 && itemUseAnimation == ItemUseAnimation.TRIDENT;
      boolean flag33 = abstractClientPlayer.isAutoSpinAttack();
      if (flag32 && !flag13) {
         doubleValue38 += 0.07 * doubleValue73;
         doubleValue39 += 0.1 * doubleValue73;
      } else if (!flag13) {
         doubleValue38 -= (flag33 ? 0.57 : 0.1) * doubleValue73;
         doubleValue39 -= 0.1 * doubleValue73;
      }

      doubleValue38 = clamp01(doubleValue38);
      if (flag32 && flag13) {
         doubleValue35 += 0.07 * doubleValue73;
         doubleValue36 += 0.05 * doubleValue73;
         doubleValue37 += 0.1 * doubleValue73;
      } else if (flag13) {
         doubleValue35 -= (flag33 ? 0.57 : 0.1) * doubleValue73;
         doubleValue36 -= (flag33 ? 0.53 : 0.07) * doubleValue73;
         doubleValue37 -= 0.1 * doubleValue73;
      }

      doubleValue36 = doubleValue36 * Math.pow(0.95, doubleValue73);
      doubleValue35 = clamp01(doubleValue35);
      doubleValue36 = clamp01(doubleValue36);
      boolean flag34 = flag15 && itemUseAnimation == ItemUseAnimation.BLOCK;
      double doubleValue87 = (flag18 ? 0.12 : 0.07) * doubleValue73;
      if (flag34 && flag13) {
         doubleValue42 += doubleValue87;
      } else if (flag13) {
         doubleValue42 -= doubleValue87;
      }

      doubleValue42 = clamp01(doubleValue42 - doubleValue44 * 0.04 * doubleValue73);
      if (flag34 && !flag13) {
         doubleValue43 += 0.07 * doubleValue73;
      } else if (!flag13) {
         doubleValue43 -= 0.07 * doubleValue73;
      }

      doubleValue43 = clamp01(doubleValue43 - doubleValue44 * 0.04 * doubleValue73);
      if (doubleValue69 > 0.08 && Math.abs(doubleValue70) < 0.08 && abstractClientPlayer.onGround()) {
         doubleValue45 += doubleValue69 * doubleValue73;
         doubleValue46 += 0.1 * doubleValue73;
      } else {
         doubleValue46 -= 0.1 * doubleValue73;
      }

      doubleValue46 = clamp01(doubleValue46);
      doubleValue48 = doubleValue48 + (-1.0 * doubleValue70 + Math.sin(doubleValue49 * 3.14) * 0.14 + Math.sin(doubleValue2 * 3.14) * 0.12 + Math.sin(doubleValue5 * 3.14) * 0.12) * 0.27 * doubleValue73;
      doubleValue48 = doubleValue48 - 0.1 * doubleValue47 * doubleValue73;
      doubleValue48 = doubleValue48 * Math.pow(0.82, doubleValue73);
      doubleValue47 = doubleValue47 + doubleValue48 * doubleValue73;
      if (abstractClientPlayer.isShiftKeyDown()) {
         doubleValue49 += 0.1 * doubleValue73;
      } else {
         doubleValue49 -= 0.1 * doubleValue73;
      }

      doubleValue49 = clamp01(doubleValue49);
      translateY(poseStack, -0.08 * doubleValue49);
      rotateXAround(poseStack, 4.0 * Math.sin(doubleValue49 * 3.14), 0.0, -0.4, 0.0);
      doubleValue50 += 0.04 * doubleValue73;
      if (flag24) {
         doubleValue51 += 0.1 * doubleValue73;
      } else {
         doubleValue51 -= 0.1 * doubleValue73;
      }

      doubleValue51 = clamp01(doubleValue51);
      if (flag25) {
         doubleValue53 += 0.1 * doubleValue73;
      } else {
         doubleValue53 -= 0.1 * doubleValue73;
      }

      doubleValue53 = clamp01(doubleValue53);
      if (flag25 && doubleValue69 > 0.08) {
         doubleValue54 += 0.1 * doubleValue73;
      } else {
         doubleValue54 -= 0.06 * doubleValue73;
      }

      doubleValue54 = clamp01(doubleValue54);
      if (flag24 && Math.abs(doubleValue70) > 0.08) {
         doubleValue52 += (doubleValue70 > 0.0 ? doubleValue70 : doubleValue70 / 2.0) * doubleValue73;
      }

      if (abstractClientPlayer.isSwimming() && !flag14) {
         doubleValue57 += doubleValue69 * doubleValue73;
         doubleValue56 += 0.1 * doubleValue73;
      } else {
         doubleValue56 -= 0.1 * doubleValue73;
      }

      doubleValue56 = clamp01(doubleValue56);
      double doubleValue88 = Math.cos(doubleValue57 * 0.55);
      translateZ(poseStack, 0.3 * Math.sin(doubleValue57 * 0.55) * doubleValue56);
      rotateXAround(poseStack, doubleValue71 * -0.03 + doubleValue79 * 0.1, 0.0, -0.4, 0.0);
      if (flag16) {
         translateY(poseStack, -0.15 * -doubleValue88 * doubleValue56 + 0.25 * doubleValue56);
      }

      rotateYAround(poseStack, -15.0 * doubleValue72 * doubleValue88 * doubleValue56, 0.3 * doubleValue72, -0.4, 0.0);
      rotateXAround(poseStack, -10.0 * doubleValue88 * doubleValue56, 0.3 * doubleValue72, -0.4, 0.0);
      if (flag16) {
         rotateXAround(poseStack, -30.0 * doubleValue56, 0.3 * doubleValue72, -0.4, 0.0);
         rotateZAround(poseStack, -20.0 * doubleValue72 * Math.sin(doubleValue57 * 0.55) * doubleValue56, 0.3 * doubleValue72, -0.4, 0.0);
      }

      boolean flag35 = m_8_boolean(itemStack);
      boolean flag36 = isPetalItem(itemStack);
      if (!itemStack.is(Items.BELL)
         && !flag35
         && !itemStack.is(ItemTags.HANGING_SIGNS)
         && !flag36
         && !itemStack.is(Items.END_CRYSTAL)
         && !itemStack.is(Items.PAINTING)
         && !itemStack.is(Items.ITEM_FRAME)) {
         if (!flag16 && itemUseAnimation != ItemUseAnimation.CROSSBOW) {
            translateY(poseStack, -0.12);
            rotateZ(poseStack, -6.0 * doubleValue72);
            rotateX(poseStack, 6.0);
         }
      } else if (flag36) {
         poseStack.translate(0.0, 0.25, -0.05);
      } else if (itemStack.is(Items.END_CRYSTAL)) {
         translateZ(poseStack, -0.12);
         rotateX(poseStack, -10.0);
      } else {
         poseStack.translate(0.0, -0.1, 0.05);
         rotateX(poseStack, 25.0);
      }

      translateY(poseStack, 0.01 * Math.sin(doubleValue50));
      rotateXAround(poseStack, 1.1 * doubleValue72 * Math.cos(doubleValue50), 0.3 * doubleValue72, -0.4, 0.0);
      rotateYAround(poseStack, 0.5 * Math.sin(doubleValue50), 0.3 * doubleValue72, -0.4, 0.0);
      rotateZAround(poseStack, 2.0 * Math.sin(doubleValue50 * 0.3), 0.3 * doubleValue72, -0.4, 0.0);
      if (flag16 && !flag13) {
         poseStack.translate(0.0, -easeInOutExpo(doubleValue28), 0.5 * easeInOutExpo(doubleValue28));
      }

      double doubleValue89 = !flag16 && !flag17 ? 1.0 : 0.7;
      if (flag16) {
         translateZ(poseStack, 0.06 * (doubleValue47 * doubleValue89));
      }

      rotateXAround(poseStack, 2.0 * (doubleValue47 * doubleValue89), 0.0, -0.4, 0.0);
      translateY(poseStack, 0.06 * doubleValue47 * doubleValue89);
      double doubleValue90 = c8.f_64 ? doubleValue45 : doubleValue45 - 0.75;
      rotateXAround(poseStack, 1.5 * Math.sin(doubleValue90) * doubleValue46, 0.0, -0.4, 0.0);
      rotateYAround(poseStack, -0.5 * Math.cos(doubleValue45 * 1.5) * doubleValue46 * doubleValue72, 0.0, -0.4, 0.0);
      rotateZAround(poseStack, Math.cos(doubleValue45 * 1.5) * doubleValue46 * doubleValue72, 0.0, -0.4, 0.0);
      if (itemUseAnimation == ItemUseAnimation.BLOCK && !flag18) {
         poseStack.translate(0.2 * doubleValue72, 0.0, 0.1);
         rotateYAround(poseStack, 20.0 * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
      }

      if (itemUseAnimation == ItemUseAnimation.BLOCK) {
         double doubleValue91 = flag13 ? doubleValue42 : doubleValue43;
         double doubleValue92 = easeInOutBack(doubleValue91);
         translateX(poseStack, 0.1 * doubleValue72 * doubleValue92);
         rotateYAround(poseStack, (flag18 ? 50.0 : 70.0) * doubleValue92 * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
         rotateXAround(poseStack, 13.0 * clamp01(Math.sin(doubleValue91 * 4.14)), 0.3 * doubleValue72, -0.4, 0.0);
         rotateXAround(poseStack, -15.0 * doubleValue92, 0.3 * doubleValue72, -0.4, 0.0);
      }

      double doubleValue93 = easeOutBack(doubleValue35);
      if (itemUseAnimation == ItemUseAnimation.TRIDENT && flag13) {
         poseStack.translate(0.0, -0.15 * doubleValue93, -0.3 * doubleValue93);
         rotateXAround(poseStack, 65.0 * doubleValue93, 0.3 * doubleValue72, -0.4, 0.0);
         rotateXAround(poseStack, 55.0 * doubleValue36, 0.3 * doubleValue72, -0.4, 0.0);
         rotateZAround(poseStack, 10.0 * doubleValue72 * doubleValue93, 0.3 * doubleValue72, -0.4, 0.0);
         rotateXAround(poseStack, 0.3 * Math.sin(doubleValue37 * doubleValue93 * 9.14), 0.3 * doubleValue72, -0.4, 0.0);
      }

      if (!flag13) {
         m_7(poseStack, flag16, doubleValue72, doubleValue93);
      }

      double doubleValue94 = easeOutBack(doubleValue38);
      if (itemUseAnimation == ItemUseAnimation.TRIDENT && !flag13) {
         poseStack.translate(0.0, -0.15 * doubleValue94, -0.3 * doubleValue94);
         rotateXAround(poseStack, 65.0 * doubleValue94, 0.3 * doubleValue72, -0.4, 0.0);
         rotateZAround(poseStack, 10.0 * doubleValue72 * doubleValue94, 0.3 * doubleValue72, -0.4, 0.0);
         rotateXAround(poseStack, 0.3 * Math.sin(doubleValue39 * doubleValue94 * 9.14), 0.3 * doubleValue72, -0.4, 0.0);
      }

      if (flag13) {
         m_7(poseStack, flag16, doubleValue72, doubleValue94);
      }

      m_5(c8, floatValue7, doubleValue74, doubleValue75, doubleValue76, doubleValue77, doubleValue78, flag18, flag19, flag20, flag21);
      if (flag34) {
         float floatValue8 = floatValue6 * floatValue6 * floatValue6;
         double doubleValue95 = Math.sin(floatValue8) + 4.0 * Math.sin(doubleValue44 * doubleValue44 * doubleValue44 * 3.14);
         double doubleValue96 = flag13 ? doubleValue42 : doubleValue43;
         translateX(poseStack, 0.0 - 0.25 * doubleValue95 * doubleValue72 * doubleValue96);
         rotateZAround(poseStack, 10.0 * doubleValue72 * doubleValue95 * doubleValue96, 0.3 * doubleValue72, -0.4, 0.0);
      }

      double doubleValue97 = Math.sin(floatValue6 * floatValue6 * floatValue6);
      if (itemUseAnimation == ItemUseAnimation.CROSSBOW && doubleValue24 + doubleValue26 == 0.0) {
         translateZ(poseStack, 0.25 * doubleValue97);
         rotateXAround(poseStack, 20.0 * doubleValue97, 0.3 * doubleValue72, -0.4, 0.0);
      } else if (doubleValue18 == 0.0 && itemUseAnimation != ItemUseAnimation.BOW && itemUseAnimation != ItemUseAnimation.BLOCK && doubleValue35 == 0.0 && doubleValue38 == 0.0) {
         translateZ(poseStack, 0.0 - 0.25 * doubleValue97);
         rotateXAround(poseStack, -20.0 * doubleValue97, 0.3 * doubleValue72, -0.4, 0.0);
         if (flag21) {
            translateZ(poseStack, 0.0 - 0.25 * doubleValue97);
            rotateZ(poseStack, 6.0 * doubleValue72 * doubleValue97);
            rotateXAround(poseStack, -10.0 * doubleValue97, 0.3 * doubleValue72, -0.4, 0.0);
         }

         if (!flag17) {
            rotateX(poseStack, doubleValue59 * 0.35 * doubleValue76);
         }
      }

      double doubleValue98 = doubleValue71 != 0.0 ? 90.0 / doubleValue71 / 2.5 : 1.0;
      if (doubleValue98 > 1.0 || doubleValue98 < 0.0) {
         doubleValue98 = 1.0;
      }

      double doubleValue99 = flag35 ? 0.2 : 1.0;
      translateZ(poseStack, 0.2 * doubleValue51);
      translateY(poseStack, -0.2 * Math.cos(doubleValue52) * doubleValue72 * doubleValue98 * doubleValue51 * doubleValue99);
      rotateX(poseStack, -30.0 * doubleValue72 * Math.sin(doubleValue52) * doubleValue98 * doubleValue51 * doubleValue99);
      rotateX(poseStack, doubleValue71 * doubleValue51);
      translateZ(poseStack, 0.01 * doubleValue71 * doubleValue51);
      translateY(poseStack, 0.003 * doubleValue71 * doubleValue51);
      translateX(poseStack, -0.0025 * doubleValue72 * doubleValue71 * doubleValue51);
      if (!flag16) {
         translateX(poseStack, -0.05 * doubleValue72 * doubleValue51);
         translateZ(poseStack, -0.2 * doubleValue51);
         translateY(poseStack, -0.1 * doubleValue51);
      }

      double doubleValue100 = Math.sin(doubleValue55) * doubleValue53 * doubleValue99 * doubleValue54;
      translateZ(poseStack, 0.2 * doubleValue53);
      translateZ(poseStack, -0.2 * doubleValue72 * Math.sin(doubleValue55) * doubleValue53 * doubleValue98 * doubleValue99 * doubleValue54);
      rotateY(poseStack, 10.0 * doubleValue100);
      rotateX(poseStack, Mth.clamp(20.0 * doubleValue72 * Math.cos(doubleValue55) * doubleValue53 * doubleValue99 * doubleValue54, 0.0, 20.0));
      if (flag16) {
         translateY(poseStack, 0.3 * doubleValue53);
         translateZ(poseStack, -0.55 * doubleValue53);
         rotateX(poseStack, -45.0 * doubleValue53);
         rotateZAround(poseStack, Mth.clamp(16.0 * doubleValue100, 0.0, 20.0), 0.3, -0.4, 0.0);
      }

      rotateX(poseStack, doubleValue71 * doubleValue53);
      rotateX(poseStack, -7.0 * doubleValue53);
      translateZ(poseStack, 0.01 * doubleValue71 * doubleValue53);
      if (flag16) {
         translateZ(poseStack, 0.005 * doubleValue71 * doubleValue53);
      }

      translateY(poseStack, 0.003 * doubleValue71 * doubleValue53);
      translateX(poseStack, -0.0025 * doubleValue72 * doubleValue71 * doubleValue53);
      if (!flag16) {
         translateX(poseStack, -0.1 * doubleValue72 * doubleValue53);
         translateZ(poseStack, -0.2 * doubleValue53);
         translateY(poseStack, -0.1 * doubleValue53);
      }

      double doubleValue101 = easeInOutBack(doubleValue2);
      double doubleValue102 = easeInOutBack(doubleValue5);
      if (doubleValue2 > 0.0) {
         doubleValue28 = doubleValue101 == 1.0 ? -0.1 : 1.0 - doubleValue101;
      }

      if (itemUseAnimation == ItemUseAnimation.BOW) {
         double doubleValue103 = flag13 ? doubleValue101 : doubleValue102;
         translateX(poseStack, 0.15 * doubleValue72);
         translateZ(poseStack, -0.085);
         translateX(poseStack, 0.1 * doubleValue72 * doubleValue103);
         translateZ(poseStack, 0.085 * doubleValue103);
         translateZ(poseStack, -0.1 * doubleValue103);
         translateX(poseStack, -0.2 * doubleValue72 * doubleValue103);
         translateY(poseStack, 0.15 * doubleValue103);
         rotateXAround(poseStack, 5.0 * Math.sin(doubleValue103 * 3.14), 0.3 * doubleValue72, -0.4, 0.0);
         rotateYAround(poseStack, 15.0 * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
         translateZ(poseStack, flag13 ? 0.0015 * Math.sin(doubleValue4 * 15.14) * doubleValue3 : 0.0015 * Math.sin(doubleValue7 * 15.14) * doubleValue6);
      }

      if (flag14 || flag16) {
         double doubleValue104 = flag13 ? doubleValue102 : doubleValue101;
         double doubleValue105 = easeOutBack(flag13 ? doubleValue6 : doubleValue3);
         double doubleValue106 = flag13 ? doubleValue7 : doubleValue4;
         translateX(poseStack, 0.27 * doubleValue72 * doubleValue104);
         if (!flag16) {
            translateY(poseStack, Math.sin(doubleValue104 * 1.56 + 3.14));
         }

         translateZ(poseStack, -0.65 * doubleValue104);
         rotateXAround(poseStack, 10.0 * Math.sin(doubleValue104 * 3.14) * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
         rotateYAround(poseStack, 70.0 * doubleValue104 * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
         rotateYAround(poseStack, 25.0 * doubleValue105 * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
         rotateY(poseStack, 0.25 * doubleValue72 * Math.sin(doubleValue106 * 15.14) * doubleValue105);
         rotateYAround(poseStack, 25.0 * doubleValue104 * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
         translateY(poseStack, -0.5 * Math.sin(doubleValue104 * 3.14));
      }

      double doubleValue107 = easeOutBack(doubleValue24);
      double doubleValue108 = easeOutBack(doubleValue26);
      if (flag13) {
         m_3(poseStack, itemUseAnimation, doubleValue72, doubleValue107);
         if (flag14 || flag16) {
            m_8(poseStack, flag16, doubleValue72, doubleValue108, easeOutBack(doubleValue27), doubleValue108);
         }
      } else {
         if (flag14 || flag16) {
            m_8(poseStack, flag16, doubleValue72, doubleValue107, easeOutBack(doubleValue25), easeOutBack(doubleValue25));
         }

         m_3(poseStack, itemUseAnimation, doubleValue72, doubleValue108);
      }

      m_2(poseStack, itemStack, itemUseAnimation, flag13, flag16, doubleValue72);
      double doubleValue109 = easeInOutBack(flag13 ? doubleValue29 : doubleValue30);
      if (itemUseAnimation == ItemUseAnimation.BRUSH) {
         double doubleValue110 = Math.sin((flag13 ? doubleValue19 : doubleValue21) * 4.14);
         translateZ(poseStack, -0.2 * doubleValue109);
         translateX(poseStack, -0.2 * doubleValue72 * doubleValue109);
         translateY(poseStack, -0.3 * doubleValue109);
         translateX(poseStack, -0.2 * doubleValue72 * doubleValue110 * doubleValue109);
         translateY(poseStack, -0.3 * doubleValue110 * doubleValue109);
         rotateX(poseStack, 10.0 * Math.sin(doubleValue109 * 3.14));
         rotateY(poseStack, 20.0 * doubleValue72 * doubleValue109);
         rotateY(poseStack, 10.0 * doubleValue72 * doubleValue110 * doubleValue109);
         rotateZ(poseStack, 30.0 * doubleValue72 * doubleValue109);
         rotateZ(poseStack, 30.0 * doubleValue72 * doubleValue110 * doubleValue109);
      }

      if (itemStack.is(BlockItemTags.DOORS.item())) {
         translateX(poseStack, 0.2 * doubleValue72);
         rotateXAround(poseStack, 6.0, 0.3 * doubleValue72, -0.4, 0.0);
         rotateYAround(poseStack, 20.0 * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
      }

      if (abstractClientPlayer.getCooldowns().isOnCooldown(itemStack) && itemUseAnimation == ItemUseAnimation.BLOCK) {
         doubleValue44 += 0.04 * doubleValue73;
      } else if (itemUseAnimation == ItemUseAnimation.BLOCK) {
         doubleValue44 -= 0.06 * doubleValue73;
      }

      doubleValue44 = clamp01(doubleValue44);
      double doubleValue111 = doubleValue44 * doubleValue44;
      if (itemUseAnimation == ItemUseAnimation.BLOCK) {
         translateZ(poseStack, -0.4 * doubleValue111);
         translateY(poseStack, 0.15 * doubleValue111);
         translateX(poseStack, -0.1 * doubleValue72 * doubleValue111);
         rotateX(poseStack, -30.0 * doubleValue111);
         rotateX(poseStack, -10.0 * Math.sin(doubleValue111 * 3.14));
         rotateY(poseStack, -20.0 * doubleValue72 * doubleValue111);
         rotateZ(poseStack, -6.0 * doubleValue72 * doubleValue111);
      }

      flag10 = flag;
      if (flag16 && doubleValue22 > 0.0) {
         rotateZ(poseStack, -6.0 * doubleValue72);
         translateY(poseStack, -0.35);
      }

      double doubleValue112 = easeInOutBack(doubleValue10);
      double doubleValue113 = easeOutBack(doubleValue11);
      if (flag22) {
         translateX(poseStack, (0.3 - 0.1 * doubleValue113) * doubleValue72 * doubleValue112);
         translateY(poseStack, 0.18 * doubleValue112);
         translateZ(poseStack, 0.12 * doubleValue113 * doubleValue112);
         rotateX(poseStack, Mth.clamp(doubleValue71, 0.0, 50.0) * doubleValue112);
         rotateX(poseStack, -40.0 * doubleValue112);
         rotateYAround(poseStack, (40.0 + 30.0 * doubleValue113) * doubleValue72 * doubleValue112, 0.3 * doubleValue72, -0.4, 0.0);
         double doubleValue114 = 1.0 - doubleValue112;
         translateX(poseStack, 0.1 * doubleValue72 * doubleValue114);
         translateY(poseStack, -0.35 * doubleValue114);
         translateZ(poseStack, 0.22 * doubleValue114);
         rotateX(poseStack, 24.0 * doubleValue114);
         rotateY(poseStack, 10.0 * doubleValue72 * doubleValue114);
      }

      if (itemUseAnimation == ItemUseAnimation.CROSSBOW) {
         translateX(poseStack, 0.1 * doubleValue72);
         translateZ(poseStack, 0.2);
         rotateXAround(poseStack, -5.0, 0.3 * doubleValue72, -0.4, 0.0);
         rotateYAround(poseStack, 20.0 * doubleValue72, 0.3 * doubleValue72, -0.4, 0.0);
      }

      if (flag33 && itemUseAnimation == ItemUseAnimation.TRIDENT && abstractClientPlayer.getUsedItemHand() == c8.f_8_f) {
         if (flag13) {
            doubleValue40 += 0.08 * doubleValue73;
         } else {
            doubleValue41 += 0.08 * doubleValue73;
         }
      } else if (flag13) {
         doubleValue40 -= 0.025 * doubleValue73;
      } else {
         doubleValue41 -= 0.025 * doubleValue73;
      }

      double doubleValue115 = 1.0 - clamp01(floatValue7 * 4.0F);
      doubleValue40 = clamp01(doubleValue40) * Math.pow(0.95, doubleValue73) * doubleValue115 * (1.0 - clamp01(doubleValue35 * 6.0));
      doubleValue41 = clamp01(doubleValue41) * Math.pow(0.95, doubleValue73) * doubleValue115 * clamp01(1.0 - doubleValue38 * 6.0);
      if (itemUseAnimation == ItemUseAnimation.TRIDENT) {
         double doubleValue116 = flag13 ? doubleValue40 : doubleValue41;
         double doubleValue117 = easeOutBack(doubleValue116);
         double doubleValue118 = clamp01(doubleValue116 * 3.0);
         translateZ(poseStack, 0.2 * doubleValue117 * doubleValue118);
         translateX(poseStack, -1.25 * doubleValue72 * doubleValue117 * doubleValue118);
         rotateY(poseStack, 5.0 * doubleValue72 * doubleValue117 * doubleValue118);
         double doubleValue119 = easeOutBack(Math.sin(flag13 ? doubleValue40 : doubleValue41 * 3.14));
         rotateX(poseStack, -30.0 * doubleValue119 * doubleValue118);
         rotateZAround(poseStack, 70.0 * doubleValue119 * doubleValue118 * doubleValue72, 0.7 * doubleValue72, 0.7, 0.0);
      }

      if (itemUseAnimation == ItemUseAnimation.SPEAR) {
         double doubleValue120 = flag13 ? doubleValue12 : doubleValue15;
         double doubleValue121 = easeInOutBack(flag13 ? doubleValue13 : doubleValue16);
         double doubleValue122 = flag13 ? doubleValue14 : doubleValue17;
         double doubleValue123 = easeOutBack(doubleValue122) * doubleValue122;
         double doubleValue124 = clamp01((flag13 ? doubleValue8 : doubleValue9) * 2.0);
         translateZ(poseStack, -0.25);
         translateY(poseStack, 0.1);
         rotateX(poseStack, -20.0);
         translateZ(poseStack, -0.25 * easeInOutBack(doubleValue120));
         translateZ(poseStack, 0.25 * doubleValue123);
         rotateY(poseStack, 8.0 * easeInOutBack(doubleValue120) * doubleValue72);
         rotateXAround(poseStack, 40.0 * Math.sin(doubleValue120 * 3.14), 0.0, -0.2, -0.35);
         rotateXAround(poseStack, -8.0 * doubleValue121, 0.5 * doubleValue72, -0.5, -0.35);
         rotateYAround(poseStack, -1.5 * Math.sin(doubleValue50 * 1.5) * doubleValue121 * doubleValue72, 0.5 * doubleValue72, -0.5, 0.0);
         rotateXAround(poseStack, -1.5 * Math.sin(doubleValue50 * 3.0) * doubleValue121, 0.5 * doubleValue72, -0.5, 0.0);
         rotateZAround(poseStack, -30.0 * doubleValue123 * doubleValue72, 0.5 * doubleValue72, -0.5, -0.35);
         rotateZAround(poseStack, -8.0 * Math.sin(doubleValue124 * 6.28) * Math.sin(doubleValue124 * 6.28) * doubleValue72, 0.5 * doubleValue72, -0.5, 0.0);
         rotateXAround(poseStack, 20.0 * Math.sin(doubleValue124 * 3.14) * Math.sin(doubleValue124 * 3.14), 0.5 * doubleValue72, -0.5, 0.0);
      }
   }

   private static void m_0(PoseStack poseStack, ItemUseAnimation itemUseAnimation, double doubleValue69, double doubleValue70, double doubleValue71) {
      if (itemUseAnimation == ItemUseAnimation.CROSSBOW) {
         translateY(poseStack, -0.25 * doubleValue70);
      }

      translateZ(poseStack, -0.2 * doubleValue70);
      rotateYAround(poseStack, 25.0 * doubleValue69 * doubleValue70, 0.3 * doubleValue69, -0.4, 0.0);
      rotateXAround(poseStack, -55.0 * doubleValue70, 0.3 * doubleValue69, -0.4, 0.0);
      rotateZAround(poseStack, 40.0 * doubleValue69 * doubleValue70, 0.3 * doubleValue69, -0.4, 0.0);
      rotateZAround(poseStack, -40.0 * doubleValue69 * doubleValue71, 0.3 * doubleValue69, -0.4, 0.0);
      rotateXAround(poseStack, 55.0 * doubleValue71, 0.3 * doubleValue69, -0.4, 0.0);
      rotateYAround(poseStack, -25.0 * doubleValue69 * doubleValue71, 0.3 * doubleValue69, -0.4, 0.0);
      if (itemUseAnimation == ItemUseAnimation.CROSSBOW) {
         translateY(poseStack, 0.25 * doubleValue71);
      }

      translateZ(poseStack, 0.2 * doubleValue71);
   }

   private static void m_3(PoseStack poseStack, ItemUseAnimation itemUseAnimation, double doubleValue69, double doubleValue70) {
      if (itemUseAnimation == ItemUseAnimation.CROSSBOW) {
         translateY(poseStack, -0.15 * doubleValue70);
         translateZ(poseStack, 0.3 * doubleValue70);
         rotateZAround(poseStack, 20.0 * doubleValue69 * doubleValue70, -0.3 * doubleValue69, -0.4, 0.0);
         rotateYAround(poseStack, 15.0 * doubleValue69 * doubleValue70, -0.3 * doubleValue69, -0.4, 0.0);
      }
   }

   private static void m_8(PoseStack poseStack, boolean flag13, double doubleValue69, double doubleValue70, double doubleValue71, double doubleValue72) {
      translateX(poseStack, 0.25 * doubleValue69 * doubleValue70);
      translateZ(poseStack, -0.1 * doubleValue70);
      translateY(poseStack, 0.55 * doubleValue70);
      if (!flag13) {
         translateY(poseStack, Math.sin(doubleValue70 * 1.56 + 3.14));
      }

      rotateZAround(poseStack, 15.0 * doubleValue69 * doubleValue70, 0.3 * doubleValue69, -0.4, 0.0);
      rotateYAround(poseStack, 80.0 * doubleValue69 * doubleValue70, 0.3 * doubleValue69, -0.4, 0.0);
      rotateYAround(poseStack, 15.0 * doubleValue69 * doubleValue71, 0.3 * doubleValue69, -0.4, 0.0);
      rotateXAround(poseStack, -7.0 * doubleValue72, 0.3 * doubleValue69, -0.4, 0.0);
   }

   private static void m_7(PoseStack poseStack, boolean flag13, double doubleValue69, double doubleValue70) {
      translateY(poseStack, (flag13 ? 0.6 : 0.2) * doubleValue70);
      poseStack.translate(0.25 * doubleValue69 * doubleValue70, 0.0, -0.15 * doubleValue70);
      rotateYAround(poseStack, 25.0 * doubleValue69 * doubleValue70, 0.3 * doubleValue69, -0.4, 0.0);
      rotateXAround(poseStack, -30.0 * doubleValue70, 0.3 * doubleValue69, -0.4, 0.0);
   }

   private static void m_5(
      ViewModelRenderer.DataRecord c8, float floatValue6, double doubleValue69, double doubleValue70, double doubleValue71, double doubleValue72, double doubleValue73, boolean flag13, boolean flag14, boolean flag15, boolean flag16
   ) {
      PoseStack poseStack = c8.f_5v;
      ItemUseAnimation itemUseAnimation = c8.f_0y;
      double doubleValue74 = c8.f_9s;
      double doubleValue75 = Math.sin(floatValue6 * 3.14);
      double doubleValue76 = clamp01(Math.sin(floatValue6 * 6.28));
      double doubleValue77 = Math.sin(floatValue6 * 6.28);
      double doubleValue78 = Math.sin(doubleValue58 * 2.0) * doubleValue71;
      if (c8.f_1v.isEmpty()) {
         poseStack.translate(-0.15 * doubleValue74 * doubleValue71, 0.1 * doubleValue77 + 0.33 * doubleValue71 + 0.05 * doubleValue69 + 0.14 * doubleValue76, -0.1 * doubleValue77 - 0.4 * doubleValue72 - 0.2 * doubleValue71);
         rotateX(poseStack, -10.0 * doubleValue76);
         translateZ(poseStack, 0.15 * doubleValue69);
         rotateXAround(poseStack, -20.0 * doubleValue71, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -7.0 * doubleValue72, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue69, 0.3 * doubleValue74, -0.4, 0.0);
         rotateZAround(poseStack, 20.0 * doubleValue74 * doubleValue71, 0.3 * doubleValue74, -0.4, 0.0);
         rotateYAround(poseStack, 5.0 * doubleValue74 * doubleValue71, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -5.0 * doubleValue77, 0.3 * doubleValue74, -0.4, 0.0);
         rotateZAround(poseStack, 10.0 * doubleValue74 * doubleValue77, 0.3 * doubleValue74, -0.4, 0.0);
         rotateYAround(poseStack, 5.0 * doubleValue74 * doubleValue77, 0.3 * doubleValue74, -0.4, 0.0);
         rotateYAround(poseStack, 15.0 * doubleValue74 * doubleValue72, 0.3 * doubleValue74, -0.4, 0.0);
      } else if (flag14) {
         translateZ(poseStack, -0.1 * doubleValue70);
         translateY(poseStack, -0.1 * doubleValue77);
         translateX(poseStack, -0.15 * doubleValue74 * doubleValue72);
         translateX(poseStack, 0.15 * doubleValue74 * doubleValue73);
         translateZ(poseStack, -0.3 * doubleValue75);
         translateY(poseStack, 0.2 * doubleValue75);
         translateX(poseStack, -0.15 * doubleValue74 * doubleValue75);
         rotateXAround(poseStack, 20.0 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 40.0 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue76, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue69, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -45.0 * doubleValue72, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -10.0 * doubleValue73, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -95.0 * doubleValue75, 0.3 * doubleValue74, -0.4, 0.0);
         rotateYAround(poseStack, 20.0 * doubleValue75 * doubleValue74, 0.3 * doubleValue74, -0.4, 0.0);
         rotateZAround(poseStack, Mth.clamp(20.0 * doubleValue74 * Math.sin(doubleValue58) * doubleValue71, -60.0, 60.0), 0.5 * doubleValue74, -0.5, 0.0);
      } else if (!flag13 && !flag16 && itemUseAnimation != ItemUseAnimation.TRIDENT && itemUseAnimation != ItemUseAnimation.SPEAR && !flag15) {
         if (c8.f_1v.is(ItemTags.SHOVELS)) {
            translateY(poseStack, 0.6 * doubleValue70);
            translateZ(poseStack, -0.5 * doubleValue70);
            translateZ(poseStack, -0.3 * doubleValue75);
            translateY(poseStack, -0.3 * doubleValue75);
            translateY(poseStack, -0.4 * doubleValue69);
            translateZ(poseStack, 0.1 * doubleValue69);
            translateY(poseStack, -0.1 * doubleValue73);
            rotateX(poseStack, -130.0 * doubleValue70);
            rotateX(poseStack, 70.0 * doubleValue75);
            rotateX(poseStack, 40.0 * doubleValue69);
            rotateX(poseStack, 20.0 * doubleValue73);
            rotateX(poseStack, 20.0 * doubleValue76);
            rotateX(poseStack, -10.0 * doubleValue77);
         } else {
            translateZ(poseStack, -0.1 * doubleValue71);
            translateX(poseStack, -0.1 * doubleValue74 * doubleValue71);
            translateZ(poseStack, -0.1 * doubleValue76);
            translateZ(poseStack, -0.05 * doubleValue72);
            translateY(poseStack, 0.25 * doubleValue72);
            rotateXAround(poseStack, 5.0 * doubleValue69, 0.3 * doubleValue74, -0.4, 0.0);
            rotateXAround(poseStack, -25.0 * doubleValue72, 0.3 * doubleValue74, -0.4, 0.0);
            rotateXAround(poseStack, 5.0 * doubleValue76, 0.3 * doubleValue74, -0.4, 0.0);
            rotateZ(poseStack, -5.0 * doubleValue74 * doubleValue75);
            rotateY(poseStack, 15.0 * doubleValue74 * doubleValue75);
            rotateXAround(poseStack, -2.0 * doubleValue77, 0.3 * doubleValue74, -0.4, 0.0);
            rotateZAround(poseStack, 5.0 * doubleValue74 * doubleValue77, 0.3 * doubleValue74, -0.4, 0.0);
            rotateYAround(poseStack, 5.0 * doubleValue74 * doubleValue77, 0.3 * doubleValue74, -0.4, 0.0);
         }
      } else if (flag8 && flag9 && !c8.f_6b && flag13) {
         translateZ(poseStack, 0.2 * doubleValue70);
         translateX(poseStack, -0.5 * doubleValue74 * doubleValue70);
         translateY(poseStack, -0.5 * doubleValue70);
         translateZ(poseStack, -0.2 * doubleValue72);
         translateY(poseStack, -0.1 * doubleValue77);
         translateX(poseStack, -0.15 * doubleValue74 * doubleValue72);
         translateX(poseStack, 0.15 * doubleValue74 * doubleValue73);
         translateZ(poseStack, -0.3 * doubleValue75);
         translateY(poseStack, 0.2 * doubleValue75);
         translateX(poseStack, 0.15 * doubleValue74 * doubleValue75);
         rotateXAround(poseStack, 20.0 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateZAround(poseStack, 70.0 * doubleValue74 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 30.0 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue76, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue69, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -25.0 * doubleValue72, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -10.0 * doubleValue73, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -75.0 * doubleValue75, 0.3 * doubleValue74, -0.4, 0.0);
      } else if ((!flag8 || c8.f_6b || !flag13) && itemUseAnimation != ItemUseAnimation.TRIDENT && itemUseAnimation != ItemUseAnimation.SPEAR) {
         translateZ(poseStack, -0.2 * doubleValue70);
         translateY(poseStack, -0.1 * doubleValue77);
         translateX(poseStack, -0.15 * doubleValue74 * doubleValue72);
         translateX(poseStack, 0.15 * doubleValue74 * doubleValue73);
         translateZ(poseStack, -0.3 * doubleValue75);
         translateY(poseStack, 0.2 * doubleValue75);
         translateX(poseStack, -0.15 * doubleValue74 * doubleValue75);
         rotateXAround(poseStack, 20.0 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateZAround(poseStack, (flag15 && c8.f_6b ? -60.0 : -40.0) * doubleValue74 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 30.0 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue76, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue69, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -45.0 * doubleValue72, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -10.0 * doubleValue73, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -95.0 * doubleValue75, 0.3 * doubleValue74, -0.4, 0.0);
         rotateZ(poseStack, Mth.clamp(30.0 * doubleValue74 * doubleValue78, 0.0, 30.0));
         translateY(poseStack, -0.2 * doubleValue78);
      } else if (flag13) {
         translateZ(poseStack, -0.2 * doubleValue70);
         translateZ(poseStack, -0.15 * doubleValue72);
         translateZ(poseStack, -0.25 * doubleValue73);
         translateY(poseStack, 0.2 * doubleValue72);
         translateY(poseStack, -0.1 * doubleValue77);
         translateX(poseStack, -0.15 * doubleValue74 * doubleValue72);
         translateX(poseStack, 0.15 * doubleValue74 * doubleValue73);
         translateZ(poseStack, -0.45 * doubleValue75);
         translateY(poseStack, 0.2 * doubleValue75);
         translateX(poseStack, -0.15 * doubleValue74 * doubleValue75);
         rotateXAround(poseStack, 20.0 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateZAround(poseStack, -70.0 * doubleValue74 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 30.0 * doubleValue70, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue76, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, 10.0 * doubleValue69, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -55.0 * doubleValue72, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -15.0 * doubleValue73, 0.3 * doubleValue74, -0.4, 0.0);
         rotateXAround(poseStack, -95.0 * doubleValue75, 0.3 * doubleValue74, -0.4, 0.0);
      } else {
         boolean flag17 = itemUseAnimation == ItemUseAnimation.TRIDENT;
         if (flag17) {
            translateY(poseStack, -0.5 * doubleValue70);
            translateZ(poseStack, 0.1 * doubleValue70);
         } else {
            translateZ(poseStack, 0.37 * doubleValue70);
         }

         translateX(poseStack, -0.15 * doubleValue74 * doubleValue70);
         if (flag17) {
            translateZ(poseStack, -0.25 * doubleValue73);
         } else {
            translateZ(poseStack, -0.35 * doubleValue73);
            translateZ(poseStack, -0.25 * easeOutQuart(doubleValue71));
         }

         translateZ(poseStack, 0.14 * doubleValue75);
         translateY(poseStack, -0.2 * doubleValue75);
         translateX(poseStack, -0.1 * doubleValue74 * doubleValue72);
         translateX(poseStack, -0.1 * doubleValue74 * doubleValue75);
         translateX(poseStack, -0.1 * doubleValue74 * doubleValue73);
         if (flag17) {
            rotateZ(poseStack, 80.0 * doubleValue70 * doubleValue74);
         } else {
            rotateZ(poseStack, 20.0 * doubleValue70 * doubleValue74);
            rotateY(poseStack, 6.0 * doubleValue70 * doubleValue74);
         }

         rotateZ(poseStack, 5.0 * doubleValue75 * doubleValue74);
         rotateX(poseStack, -40.0 * doubleValue70);
         rotateX(poseStack, 15.0 * doubleValue73);
         rotateX(poseStack, 15.0 * doubleValue72);
         rotateX(poseStack, 20.0 * doubleValue75);
         rotateX(poseStack, 10.0 * doubleValue75);
         rotateX(poseStack, -10.0 * doubleValue76);
         rotateZ(poseStack, Mth.clamp(10.0 * doubleValue74 * doubleValue78, 0.0, 30.0));
         translateY(poseStack, -0.1 * doubleValue78);
      }
   }

   private static void m_2(PoseStack poseStack, ItemStack itemStack, ItemUseAnimation itemUseAnimation, boolean flag13, boolean flag14, double doubleValue69) {
      double doubleValue70 = flag13 ? doubleValue18 * doubleValue18 : doubleValue20 * doubleValue20;
      double doubleValue71 = flag13 ? doubleValue22 : doubleValue23;
      double doubleValue72 = flag13 ? doubleValue19 : doubleValue21;
      if (itemUseAnimation == ItemUseAnimation.EAT || itemUseAnimation == ItemUseAnimation.TOOT_HORN) {
         translateZ(poseStack, 0.155 * doubleValue70);
         translateX(poseStack, 0.135 * doubleValue69 * doubleValue70);
         translateY(poseStack, -0.27 * doubleValue70);
         rotateX(poseStack, 30.0 * doubleValue70);
         rotateX(poseStack, 20.0 * doubleValue71);
         if (itemUseAnimation == ItemUseAnimation.EAT) {
            rotateX(poseStack, 3.0 * easeInOutBack(Math.abs(Math.sin(doubleValue72 * 3.0))) * doubleValue70);
            rotateY(poseStack, 4.0 * doubleValue69 * easeInOutBack(Math.abs(Math.sin(doubleValue72 * 2.0))) * doubleValue70);
            rotateZ(poseStack, 6.0 * doubleValue69 * easeInOutBack(Math.abs(Math.sin(doubleValue72 * 2.0))) * doubleValue70);
         } else {
            rotateX(poseStack, 2.0 * easeInOutSine(Math.sin(doubleValue72 * 2.0)) * doubleValue70);
            rotateY(poseStack, 3.0 * doubleValue69 * easeInOutSine(Math.sin(doubleValue72)) * doubleValue70);
            rotateZ(poseStack, 5.0 * doubleValue69 * easeInOutSine(Math.sin(doubleValue72)) * doubleValue70);
         }

         rotateYAround(poseStack, 60.0 * doubleValue70 * doubleValue69, 0.3 * doubleValue69, -0.4, 0.0);
         rotateXAround(poseStack, 25.0 * Math.sin(doubleValue70 * 3.14), 0.3 * doubleValue69, -0.4, 0.0);
      }

      if (itemUseAnimation == ItemUseAnimation.DRINK || flag13 && (flag14 || itemStack.is(Items.GLASS_BOTTLE))) {
         translateZ(poseStack, 0.1 * doubleValue70);
         translateX(poseStack, 0.11 * doubleValue69 * doubleValue70);
         translateY(poseStack, -0.5 * doubleValue70);
         rotateX(poseStack, 50.0 * doubleValue70);
         rotateX(poseStack, 20.0 * doubleValue71);
         rotateX(poseStack, 2.0 * Math.sin(doubleValue72 * 6.0) * doubleValue71);
         rotateYAround(poseStack, 60.0 * doubleValue69 * doubleValue70, 0.3 * doubleValue69, -0.4, 0.0);
         rotateXAround(poseStack, 25.0 * Math.sin(doubleValue70 * 3.14), 0.3 * doubleValue69, -0.4, 0.0);
      }
   }

   private static void m_9(ViewModelRenderer.DataRecord c8) {
      PoseStack poseStack = c8.f_5v;
      ItemStack itemStack = c8.f_1v;
      double doubleValue69 = c8.f_9s;
      if (itemStack.isEmpty() && doubleValue22 > 0.0) {
         translateX(poseStack, 1.5 * doubleValue69);
         translateY(poseStack, -0.3);
         translateZ(poseStack, -0.47);
         m_5(poseStack, doubleValue69);
      }

      double doubleValue70 = easeInBack(Math.sin(Mth.clamp(c8.f_6_k ? doubleValue8 : doubleValue9, 0.09723, 0.60632) * 3.24 * 1.65 - 0.1));
      if (m_9_boolean(itemStack) && c8.f_0y != ItemUseAnimation.TRIDENT) {
         rotateX(poseStack, 10.0 * doubleValue70);
         rotateZ(poseStack, 6.0 * doubleValue70);
      }

      if (!itemStack.isEmpty()) {
         poseStack.translate(1.5 * doubleValue69, -0.3, -0.6);
         m_5(poseStack, doubleValue69);
      }
   }

   private static void m_5(PoseStack poseStack, double doubleValue69) {
      rotateXAround(poseStack, 15.0, 0.5 * doubleValue69, 0.5, 0.5);
      rotateYAround(poseStack, 35.0 * doubleValue69, 0.5 * doubleValue69, 0.5, 0.5);
      rotateZAround(poseStack, -65.0 * doubleValue69, 0.5 * doubleValue69, 0.5, 0.5);
      scale(poseStack, 0.9, 0.9, 0.9);
   }

   private static void m_3(ViewModelRenderer.DataRecord c8) {
      PoseStack poseStack = c8.f_5v;
      AbstractClientPlayer abstractClientPlayer = c8.f_1_o;
      ItemStack itemStack = c8.f_1v;
      ItemUseAnimation itemUseAnimation = c8.f_0y;
      boolean flag13 = c8.f_6_k;
      double doubleValue69 = c8.f_9s;
      double doubleValue70 = c8.f_9_r;
      double doubleValue71 = abstractClientPlayer.getXRot();
      double doubleValue72 = abstractClientPlayer.getDeltaMovement().length();
      boolean flag14 = isBlockItem(itemStack);
      boolean flag15 = m_8_boolean(itemStack);
      boolean flag16 = itemStack.is(ItemTags.SWORDS);
      boolean flag17 = itemStack.is(ItemTags.PICKAXES);
      boolean flag18 = itemStack.is(ItemTags.AXES);
      boolean flag19 = itemStack.is(ItemTags.SHOVELS);
      boolean flag20 = itemStack.is(ItemTags.SKULLS);
      boolean flag21 = itemStack.is(ItemTags.HANGING_SIGNS);
      boolean flag22 = isPetalItem(itemStack);
      doubleValue32 = doubleValue32 + Math.sin(doubleValue19 * 4.14) * doubleValue29 * doubleValue70;
      doubleValue32 = doubleValue32 - 0.04 * doubleValue31 * doubleValue70;
      doubleValue32 = doubleValue32 * Math.pow(0.85, doubleValue70);
      doubleValue31 = doubleValue31 + doubleValue32 * doubleValue70;
      doubleValue34 = doubleValue34 + Math.sin(doubleValue21 * 4.14) * doubleValue30 * doubleValue70;
      doubleValue34 = doubleValue34 - 0.04 * doubleValue33 * doubleValue70;
      doubleValue34 = doubleValue34 * Math.pow(0.85, doubleValue70);
      doubleValue33 = doubleValue33 + doubleValue34 * doubleValue70;
      double doubleValue73 = abstractClientPlayer.getMainHandItem().getUseAnimation() == ItemUseAnimation.SPEAR ? 1.0 : 0.0;
      double doubleValue74 = abstractClientPlayer.getOffhandItem().getUseAnimation() == ItemUseAnimation.SPEAR ? 1.0 : 0.0;
      double doubleValue75 = abstractClientPlayer.getYHeadRot();
      double doubleValue76 = Math.sin(doubleValue45) * 3.0 * doubleValue46;
      double doubleValue77 = Math.sin(doubleValue57 * doubleValue56) * 3.0;
      double doubleValue78 = doubleValue72 * 22.0 * doubleValue46 * -1.0;
      double doubleValue79 = Math.sin(doubleValue49 * 3.14) * 0.3;
      doubleValue60 = doubleValue60 + (doubleValue78 - Math.sin(floatValue3 * 3.14) * 8.0 + doubleValue47 * 3.0 + doubleValue79 + (doubleValue71 - doubleValue67)) * 0.15 * doubleValue70;
      if (itemUseAnimation == ItemUseAnimation.BLOCK && flag13 && !flag16) {
         doubleValue60 = doubleValue60 + 10.0 * Math.sin(doubleValue44 * 3.14) * 0.15 * doubleValue70;
         doubleValue60 = doubleValue60 + 12.0 * Math.sin(doubleValue42 * 3.14) * 0.15 * doubleValue70;
      }

      doubleValue60 = doubleValue60
         + (
               -20.0 * Math.sin(doubleValue13 * 3.14) * doubleValue12
                  + 20.0 * Math.sin(doubleValue14 * 3.14) * doubleValue12
                  + 15.0 * Math.sin(doubleValue12 * 3.14)
                  + 40.0 * clamp01(Math.sin(clamp01(doubleValue8 * 1.5 * doubleValue73) * 6.28))
            )
            * 0.15
            * doubleValue70;
      doubleValue60 = doubleValue60 - 0.04 * doubleValue59 * doubleValue70;
      doubleValue60 = doubleValue60 * Math.pow(0.85, doubleValue70);
      doubleValue59 = doubleValue59 + doubleValue60 * doubleValue70;
      doubleValue64 = doubleValue64 + (doubleValue76 + Math.sin(floatValue3 * 3.14) * 8.0 + doubleValue77 + Math.sin(doubleValue8 * 6.28) * 3.0 + doubleValue75 - doubleValue68) * 0.15 * doubleValue70;
      doubleValue64 = doubleValue64 - 0.04 * doubleValue63 * doubleValue70;
      doubleValue64 = doubleValue64 * Math.pow(0.85, doubleValue70);
      doubleValue63 = doubleValue63 + doubleValue64 * doubleValue70;
      doubleValue62 = doubleValue62 + (doubleValue78 - Math.sin(floatValue4 * 3.14) * 8.0 + doubleValue47 * 3.0 + doubleValue79 + (doubleValue71 - doubleValue67)) * 0.15 * doubleValue70;
      if (itemUseAnimation == ItemUseAnimation.BLOCK && !flag13 && !flag16) {
         doubleValue62 = doubleValue62 + 10.0 * Math.sin(doubleValue44 * 3.14) * 0.15 * doubleValue70;
         doubleValue62 = doubleValue62 + 12.0 * Math.sin(doubleValue43 * 3.14) * 0.15 * doubleValue70;
      }

      doubleValue62 = doubleValue62
         + (
               -20.0 * Math.sin(doubleValue16 * 3.14) * doubleValue15
                  + 20.0 * Math.sin(doubleValue17 * 3.14) * doubleValue15
                  + 15.0 * Math.sin(doubleValue15 * 3.14)
                  + 40.0 * clamp01(Math.sin(clamp01(doubleValue9 * 1.5 * doubleValue74) * 6.28))
            )
            * 0.15
            * doubleValue70;
      doubleValue62 = doubleValue62 - 0.04 * doubleValue61 * doubleValue70;
      doubleValue62 = doubleValue62 * Math.pow(0.85, doubleValue70);
      doubleValue61 = doubleValue61 + doubleValue62 * doubleValue70;
      doubleValue66 = doubleValue66 + (doubleValue76 + Math.sin(floatValue4 * 3.14) * 8.0 + doubleValue77 + Math.sin(doubleValue9 * 6.28) * 3.0 + doubleValue75 - doubleValue68) * 0.15 * doubleValue70;
      doubleValue66 = doubleValue66 - 0.04 * doubleValue65 * doubleValue70;
      doubleValue66 = doubleValue66 * Math.pow(0.85, doubleValue70);
      doubleValue65 = doubleValue65 + doubleValue66 * doubleValue70;
      double doubleValue80 = flag13 ? doubleValue63 : doubleValue65;
      double doubleValue81 = flag13 ? doubleValue59 : doubleValue61;
      float floatValue6 = m_5_float(c8.f_2_z, flag17);
      double doubleValue82 = floatValue6 < 0.70016 ? Math.sin(Mth.clamp(floatValue6, 0.0, 0.308) * 5.1) : Math.sin(Mth.clamp(floatValue6, 0.70016, 1.0) * 5.1 - 2.0);
      double doubleValue83 = floatValue6 < 0.65245 ? Math.sin(Mth.clamp(floatValue6, 0.0, 0.16675) * 3.14 * 3.0) : Math.sin(Mth.clamp(floatValue6, 0.65245, 1.0) * 4.4 - 1.3);
      doubleValue82 = doubleValue82 * doubleValue82 * doubleValue82;
      double doubleValue84 = Math.sin(Mth.clamp(floatValue6, 0.16561, 0.49422) * 4.78 * 2.0 + 4.7);
      double doubleValue85 = Math.sin(floatValue6 * 3.14);
      double doubleValue86 = floatValue6 < 0.65594
         ? Math.sin(Mth.clamp(floatValue6, 0.16561, 0.32991) * 4.78 * 2.0 + 4.7)
         : Math.sin(Mth.clamp(floatValue6, 0.65594, 0.82025) * 4.78 * 2.0 - 4.7);
      if (itemUseAnimation == ItemUseAnimation.SPEAR) {
         double doubleValue87 = easeInOutBack((flag13 ? doubleValue12 : doubleValue15) * 0.8);
         double doubleValue88 = easeInOutBack(flag13 ? doubleValue13 : doubleValue16);
         double doubleValue89 = flag13 ? doubleValue14 : doubleValue17;
         rotateZ(poseStack, 180.0 * doubleValue69);
         rotateZ(poseStack, -180.0 * easeInOutBack(clamp01((flag13 ? doubleValue8 : doubleValue9) * 2.0)) * doubleValue69);
         translateZ(poseStack, -0.2 * easeInOutSine(doubleValue87));
         translateY(poseStack, -0.05 * doubleValue88);
         rotateX(poseStack, -70.0 * doubleValue87);
         rotateX(poseStack, -8.0 * doubleValue88);
         rotateY(poseStack, 60.0 * doubleValue87 * doubleValue69);
         rotateY(poseStack, -30.0 * doubleValue88 * doubleValue69);
         rotateY(poseStack, -60.0 * easeOutBack(doubleValue89) * doubleValue89 * doubleValue69);
      }

      if (itemUseAnimation != ItemUseAnimation.BLOCK && itemUseAnimation != ItemUseAnimation.CROSSBOW || flag16) {
         translateZ(poseStack, -0.05 * doubleValue82);
         translateY(poseStack, -0.05 * doubleValue82);
         rotateX(poseStack, 10.0 * doubleValue82);
         rotateX(poseStack, -30.0 * doubleValue82);
         rotateX(poseStack, -10.0 * doubleValue84);
         if (!flag16) {
            if (itemUseAnimation != ItemUseAnimation.TRIDENT && itemUseAnimation != ItemUseAnimation.SPEAR) {
               translateZ(poseStack, -0.05 * doubleValue82);
               translateY(poseStack, -0.05 * doubleValue82);
               rotateX(poseStack, -10.0 * doubleValue82);
               rotateX(poseStack, -25.0 * doubleValue84);
            } else {
               translateZ(poseStack, -0.1 * doubleValue82);
               translateY(poseStack, -0.05 * doubleValue82);
               if (itemUseAnimation == ItemUseAnimation.SPEAR) {
                  translateY(poseStack, -0.15 * doubleValue84);
                  rotateX(poseStack, -5.0 * doubleValue84);
               }

               rotateX(poseStack, -10.0 * doubleValue82);
               rotateX(poseStack, -15.0 * doubleValue84);
               rotateX(poseStack, -45.0 * (itemUseAnimation == ItemUseAnimation.TRIDENT ? doubleValue85 : doubleValue83));
               translateY(poseStack, 0.05 * doubleValue84);
               translateY(poseStack, 0.3 * doubleValue85);
            }
         }

         if (flag19) {
            translateY(poseStack, 0.12 * doubleValue83);
            translateZ(poseStack, 0.05 * doubleValue83);
            rotateX(poseStack, 10.0 * doubleValue83);
            rotateX(poseStack, -30.0 * doubleValue85);
            rotateX(poseStack, 20.0 * doubleValue82);
            rotateX(poseStack, 10.0 * doubleValue86);
         }

         if (flag16) {
            double doubleValue90 = easeInOutBack(Math.sin(floatValue6 * 3.14));
            translateY(poseStack, -0.1 * doubleValue90);
            rotateX(poseStack, -60.0 * doubleValue90);
         }

         if (itemUseAnimation == ItemUseAnimation.BOW) {
            translateX(poseStack, -0.065 * doubleValue69);
         }
      }

      if (itemStack.is(ItemTags.BEDS)) {
         translateZ(poseStack, 0.2);
         rotateYAround(poseStack, 180.0 * doubleValue69, -0.15 * doubleValue69, -0.4, 0.0);
      }

      if (!itemStack.is(Items.BELL) && !flag15 && !itemStack.is(Items.END_CRYSTAL) && !flag21 && !flag22) {
         if (itemStack.is(Items.PAINTING) || itemStack.is(Items.ITEM_FRAME)) {
            rotateX(poseStack, -25.0);
            translateY(poseStack, -0.65);
            rotateXAround(poseStack, Mth.clamp(doubleValue71 / 2.5, -25.0, 90.0) + doubleValue81, 0.0, 0.45, 0.0);
            rotateZAround(poseStack, -doubleValue80, 0.0, 0.55, 0.0);
         } else if (flag14) {
            translateY(poseStack, -0.025);
            translateZ(poseStack, -0.025);
            rotateX(poseStack, -5.0);
         } else {
            if (itemUseAnimation == ItemUseAnimation.NONE) {
               double doubleValue91 = Math.sin(c8.f_2i * c8.f_2i * c8.f_2i);
               if (!flag18 && !itemStack.is(Items.MACE)) {
                  rotateXAround(poseStack, -20.0 * doubleValue91 + doubleValue81 * 0.05, 0.3 * doubleValue69, -0.4, 0.0);
               } else {
                  rotateXAround(poseStack, -20.0 * doubleValue91 + doubleValue81 * (itemStack.is(Items.MACE) ? 0.2 : 0.15), 0.3 * doubleValue69, -0.3, 0.0);
               }
            }

            if ((flag18 || itemStack.is(Items.MACE)) && itemUseAnimation != ItemUseAnimation.CROSSBOW) {
               rotateXAround(poseStack, doubleValue71 * -0.05 + doubleValue81 * 0.2, 0.0, -0.2, 0.0);
            } else if (itemUseAnimation != ItemUseAnimation.CROSSBOW) {
               rotateXAround(poseStack, doubleValue71 * -0.025 + doubleValue81 * 0.1, 0.0, -0.2, 0.0);
            }
         }
      } else {
         if (!itemStack.is(Items.END_CRYSTAL)) {
            translateY(poseStack, -0.62);
         }

         if (flag21) {
            translateY(poseStack, -0.07);
         }

         if (!flag22) {
            translateZ(poseStack, 0.2);
            rotateX(poseStack, -25.0);
         } else {
            translateY(poseStack, 0.4);
            rotateX(poseStack, -70.0);
            rotateXAround(poseStack, Mth.clamp(doubleValue71 / 2.5, -20.0, 90.0) + doubleValue81 + doubleValue80 * 0.5, 0.0, -0.13, 0.0);
         }

         if (itemStack.is(Items.END_CRYSTAL)) {
            scale(poseStack, 1.0 + 0.01 * Math.sin(doubleValue50 * 15.0), 1.0 + 0.01 * Math.sin(doubleValue50 * 15.0), 1.0 + 0.01 * Math.sin(doubleValue50 * 8.0));
            translateY(poseStack, 0.03 * Math.sin(doubleValue50 * 2.0));
            translateY(poseStack, 0.25);
            translateY(poseStack, doubleValue81 / 150.0);
            translateX(poseStack, doubleValue80 / 150.0 * doubleValue69 * -1.0);
            rotateZ(poseStack, 5.0 * Math.sin(doubleValue50));
            scale(poseStack, 0.7, 0.7, 0.7);
         } else if (itemStack.is(Items.BELL)) {
            translateX(poseStack, 0.15 * doubleValue69);
            translateY(poseStack, -0.05);
            translateZ(poseStack, -0.1);
            scale(poseStack, 1.2, 1.2, 1.2);
            rotateXAround(poseStack, Mth.clamp(doubleValue71 / 2.5, -20.0, 90.0) + doubleValue81, -0.1 * doubleValue69, 0.4, 0.1);
            rotateZAround(poseStack, -doubleValue80, -0.1 * doubleValue69, 0.4, 0.1);
         } else if (flag15) {
            rotateXAround(poseStack, Mth.clamp(doubleValue71 / 2.5, -20.0, 90.0) + doubleValue81, 0.0, 0.4, 0.0);
            rotateZAround(poseStack, -doubleValue80, 0.0, 0.4, 0.0);
         }

         if (flag21) {
            rotateXAround(poseStack, Mth.clamp(doubleValue71 / 2.5, -35.0, 90.0) + doubleValue81, 0.0, 0.55, 0.0);
            rotateZAround(poseStack, -doubleValue80, 0.0, 0.55, 0.0);
         }
      }

      if (itemUseAnimation == ItemUseAnimation.DRINK || itemUseAnimation == ItemUseAnimation.EAT || itemUseAnimation == ItemUseAnimation.TOOT_HORN) {
         double doubleValue92 = flag13 ? doubleValue18 : doubleValue20;
         translateX(poseStack, 0.02 * doubleValue69 * doubleValue92);
         translateZ(poseStack, -0.05 * doubleValue92);
         if (itemUseAnimation != ItemUseAnimation.DRINK) {
            rotateX(poseStack, -23.0 * doubleValue92 * doubleValue92);
            rotateZ(poseStack, -12.0 * doubleValue69 * doubleValue92 * doubleValue92);
         }

         rotateY(poseStack, -50.0 * doubleValue69 * doubleValue92 * doubleValue92);
         if (itemUseAnimation == ItemUseAnimation.DRINK) {
            rotateX(poseStack, 15.0 * doubleValue92 * doubleValue92);
         }
      }

      if (itemUseAnimation == ItemUseAnimation.BRUSH) {
         double doubleValue93 = easeInOutBack(flag13 ? doubleValue29 : doubleValue30);
         translateZ(poseStack, -0.03 * doubleValue93);
         rotateX(poseStack, -30.0 * doubleValue93);
         rotateZ(poseStack, 15.0 * doubleValue69 * Math.sin(((flag13 ? doubleValue19 : doubleValue21) - 0.5) * 4.14) * doubleValue93);
         rotateZ(poseStack, doubleValue69 * (flag13 ? doubleValue31 : doubleValue33));
      }

      if (itemStack.is(BlockItemTags.DOORS.item())) {
         translateX(poseStack, 0.1 * doubleValue69);
         translateZ(poseStack, 0.25);
         translateY(poseStack, -0.35);
         rotateZ(poseStack, -10.0 * doubleValue69);
         rotateY(poseStack, -90.0 * doubleValue69);
      } else if (itemStack.is(ItemTags.BEDS)) {
         translateZ(poseStack, 0.17);
         rotateYAround(poseStack, -35.0 * doubleValue69, 0.3 * doubleValue69, -0.4, 0.0);
         scale(poseStack, 0.9, 0.9, 0.9);
      }

      if (itemStack.is(Items.SLIME_BALL)) {
         translateY(poseStack, -0.1);
         scale(poseStack, 1.0, 1.0 + (doubleValue47 < 0.0 ? doubleValue47 * 0.06 : doubleValue47 * 0.12), 1.0);
         translateY(poseStack, 0.1);
      } else if (itemStack.is(Items.SLIME_BLOCK) || itemStack.is(Items.HONEY_BLOCK)) {
         double doubleValue94 = doubleValue47 < 0.0 ? doubleValue47 * 0.05 : doubleValue47 * 0.1;
         double doubleValue95 = doubleValue47 < 0.0 ? doubleValue47 * 0.1 : doubleValue47 * 0.3;
         scale(poseStack, 1.0 - doubleValue94, 1.0 + doubleValue95, 1.0 - doubleValue94);
         m_7(poseStack, c8.f_64 ? 0.0 - doubleValue80 * 0.006 : 0.0 + doubleValue80 * 0.006);
      }

      if (flag19) {
         translateX(poseStack, -0.09 * doubleValue69);
         rotateY(poseStack, 80.0 * doubleValue69);
      }

      doubleValue67 = doubleValue71;
      doubleValue68 = doubleValue75;
      if (itemStack.is(Items.MAGMA_CREAM)) {
         scale(poseStack, 1.0 - doubleValue47 / 5.0, 1.0 + doubleValue47 / 5.0, 1.0);
      }

      double doubleValue96 = flag13 ? doubleValue8 : doubleValue9;
      double doubleValue97 = easeInBack(Math.sin(Mth.clamp(doubleValue96, 0.09723, 0.60632) * 3.24 * 1.65 - 0.1));
      if (m_9_boolean(itemStack) && itemUseAnimation != ItemUseAnimation.TRIDENT) {
         rotateX(poseStack, -10.0 * doubleValue97);
         translateY(poseStack, 0.62 * doubleValue97);
         translateY(poseStack, Mth.clamp(0.1 * doubleValue47, 0.0, 255.0));
         if (itemStack.is(ConventionalItemTags.NUGGETS)) {
            if (flag13 && flag5 || flag6) {
               playSound("entity.experience_orb.pickup", 0.3F);
            }

            translateY(poseStack, -0.07);
            rotateXAround(poseStack, 360.0 * easeInOutBack(clamp01(doubleValue96 * 1.65)), 0.0, 0.1, 0.0);
         } else if (itemStack.is(ConventionalItemTags.MUSIC_DISCS)) {
            rotateZAround(poseStack, 360.0 * easeInOutBack(clamp01(doubleValue96 * 1.65)), -0.1 * doubleValue69, 0.25, 0.0);
         } else {
            rotateZ(poseStack, -7.0 * doubleValue69 * Math.sin(Mth.clamp(clamp01(doubleValue96 * 1.2), 0.0943, 0.66791) * 7.07 * 1.5 - 0.8));
         }
      }

      double doubleValue98 = easeInOutBack(doubleValue10);
      double doubleValue99 = easeInOutBack(doubleValue11);
      if (itemStack.is(Items.FILLED_MAP)) {
         rotateZ(poseStack, 5.0 * doubleValue69 * doubleValue98);
         rotateY(poseStack, (-40.0 - 20.0 * doubleValue99) * doubleValue69 * doubleValue98);
         rotateZ(poseStack, 15.0 * doubleValue69 * doubleValue98);
         rotateX(poseStack, -10.0 * doubleValue99 * doubleValue98);
         double doubleValue100 = 1.0 - doubleValue98;
         translateZ(poseStack, -0.05 * doubleValue100);
         translateY(poseStack, -0.05 * doubleValue100);
         rotateX(poseStack, -40.0 * doubleValue100);
         rotateY(poseStack, -10.0 * doubleValue69 * doubleValue100);
         rotateZ(poseStack, 5.0 * doubleValue69 * doubleValue100);
      } else if (m_2_boolean(itemStack) && !flag14 && !itemStack.is(Items.BONE) && itemUseAnimation != ItemUseAnimation.BOW && itemUseAnimation != ItemUseAnimation.SPEAR) {
         m_1(poseStack, doubleValue69);
      }

      if (ITEM_SET.contains(itemStack.getItem())) {
         m_1(poseStack, doubleValue69);
      }

      if (itemStack.is(Items.SHEARS)) {
         if (!c8.f_64) {
            translateZ(poseStack, 0.1);
            rotateY(poseStack, 180.0);
         }

         rotateZ(poseStack, 45.0);
      }

      if (flag20 && !itemStack.is(Items.DRAGON_HEAD)) {
         translateX(poseStack, -0.1 * doubleValue69);
         translateY(poseStack, 0.11);
         rotateZ(poseStack, 15.0 * doubleValue69);
         rotateY(poseStack, -85.0 * doubleValue69);
         rotateX(poseStack, -55.0);
      } else if (itemStack.is(Items.DRAGON_HEAD)) {
         translateY(poseStack, 0.25);
         rotateZ(poseStack, 6.0 * doubleValue69);
         rotateY(poseStack, 160.0 * doubleValue69);
      }

      if (doubleValue != value && flag13 && itemStack.is(Items.BELL)) {
         playSound("block.bell.use", 0.3F);
      }

      doubleValue = value;
      if (flag22 && (flag13 ? flag11 != flag : flag12 != flag2)) {
         playSound("block.leaf_litter.place", 0.7F);
      }

      if (flag13) {
         flag11 = flag;
      } else {
         flag12 = flag2;
      }

      double doubleValue101 = flag13 ? doubleValue18 : doubleValue20;
      doubleValue101 = doubleValue101 * doubleValue101 * doubleValue101 * doubleValue101;
      if (itemStack.is(Items.BUCKET)
         || itemStack.is(Items.AXOLOTL_BUCKET)
         || itemStack.is(Items.POWDER_SNOW_BUCKET)
         || itemStack.is(Items.PUFFERFISH_BUCKET)
         || itemStack.is(Items.TADPOLE_BUCKET)
         || itemStack.is(Items.SALMON_BUCKET)
         || itemStack.is(Items.COD_BUCKET)
         || itemStack.is(Items.TROPICAL_FISH_BUCKET)
         || itemStack.is(Items.WATER_BUCKET)
         || itemStack.is(Items.LAVA_BUCKET)
         || itemStack.is(Items.MILK_BUCKET)) {
         translateY(poseStack, 0.025);
         translateZ(poseStack, -0.1);
         rotateY(poseStack, 180.0);
         rotateX(poseStack, -82.5);
         rotateZ(poseStack, -20.0 * doubleValue69);
         if (itemStack.is(Items.MILK_BUCKET)) {
            rotateZ(poseStack, 30.0 * doubleValue69 * doubleValue101);
            translateY(poseStack, 0.1 * doubleValue101);
            translateZ(poseStack, 0.02 * doubleValue101);
         }
      }

      if (itemUseAnimation == ItemUseAnimation.TRIDENT) {
         double doubleValue102 = easeOutBack(clamp01(flag13 ? doubleValue35 : doubleValue38 * 1.5));
         rotateX(poseStack, 180.0 * doubleValue102);
         rotateZ(poseStack, 170.0 * doubleValue69 * doubleValue102);
         translateZ(poseStack, -0.1);
         rotateY(poseStack, 40.0 * doubleValue69);
      }

      if (itemUseAnimation == ItemUseAnimation.SPEAR) {
         translateZ(poseStack, -0.1);
         rotateY(poseStack, 10.0 * doubleValue69);
      }

      if (itemUseAnimation == ItemUseAnimation.TRIDENT) {
         double doubleValue103 = easeOutBack(Math.sin(flag13 ? doubleValue40 : doubleValue41 * 3.14));
         rotateX(poseStack, -90.0 * doubleValue103);
         rotateZ(poseStack, -45.0 * doubleValue69 * doubleValue103);
      }

      if (m_4_boolean(itemStack)) {
         translateZ(poseStack, -0.05);
         if (!flag15) {
            translateY(poseStack, -0.15);
            rotateZ(poseStack, 6.0 * doubleValue69);
            rotateX(poseStack, -8.0);
         }

         rotateY(poseStack, 25.0 * doubleValue69);
         scale(poseStack, 1.1, 1.1, 1.1);
      }

      double doubleValue104 = easeOutBack(flag13 ? doubleValue3 : doubleValue6);
      flag7 = doubleValue104 >= 0.1;
      floatValue5 = (float)(doubleValue104 * doubleValue104 * (3.0 - 2.0 * doubleValue104) * 20.0);
   }

   private static void m_1(PoseStack poseStack, double doubleValue69) {
      translateX(poseStack, -0.05 * doubleValue69);
      rotateX(poseStack, -8.0);
      rotateY(poseStack, -10.0 * doubleValue69);
      rotateZ(poseStack, 6.0 * doubleValue69);
   }

   private record DataRecord(
      AbstractClientPlayer f_1_o,
      InteractionHand f_8_f,
      boolean f_6_k,
      boolean f_64,
      double f_9s,
      ItemStack f_1v,
      ItemUseAnimation f_0y,
      float f_2_z,
      float f_2i,
      double f_9_r,
      boolean f_6b,
      PoseStack f_5v
   ) {
      public boolean m_67_boolean() {
         return this.f_1_o.isUsingItem() && this.f_1_o.getUsedItemHand() == this.f_8_f;
      }

      public AbstractClientPlayer m_1_o() {
         return this.f_1_o;
      }

      public InteractionHand m_8_f() {
         return this.f_8_f;
      }

      public boolean m_6_k() {
         return this.f_6_k;
      }

      public boolean m_64_boolean() {
         return this.f_64;
      }

      public double m_9s() {
         return this.f_9s;
      }

      public ItemStack m_1v_ItemStack() {
         return this.f_1v;
      }

      public ItemUseAnimation m_0y_ItemUseAnimation() {
         return this.f_0y;
      }

      public float m_2_z() {
         return this.f_2_z;
      }

      public float m_2i() {
         return this.f_2i;
      }

      public double m_9_r() {
         return this.f_9_r;
      }

      public boolean m_6b_boolean() {
         return this.f_6b;
      }

      public PoseStack m_5v() {
         return this.f_5v;
      }
   }
}
