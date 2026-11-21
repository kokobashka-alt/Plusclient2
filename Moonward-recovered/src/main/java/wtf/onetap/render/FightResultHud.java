package wtf.onetap.render;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.module.misc.Assistent;
import wtf.onetap.util.MinecraftAccess;

public class FightResultHud implements MinecraftAccess {
   private static final Animation ANIMATION = new Animation(350L, 0.0F, Easing.EASING8);
   private static final Animation ANIMATION2 = new Animation(350L, 0.0F, Easing.EASING8);
   private static final Animation ANIMATION3 = new Animation(250L, 0.0F, Easing.EASING8);

   private FightResultHud() {
   }

   public static void m_6(Render2DEvent event, LivingEntity livingEntity, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, String text, boolean flag) {
      int value = (int)(255.0F * floatValue3);
      float floatValue7 = 3.0F;
      Assistent.m_1_String();
      float floatValue8 = 24.0F;
      float floatValue9 = 4.5F;
      float floatValue10 = 3.5F;
      float floatValue11 = 8.5F;
      float floatValue12 = 7.5F;
      Supplier supplier = Fonts.SUPPLIER16;
      boolean flag2 = MinecraftAccess.MINECRAFT.gui.screen() instanceof ChatScreen;
      float floatValue13 = MinecraftAccess.MINECRAFT.player != null ? MinecraftAccess.MINECRAFT.player.getHealth() + MinecraftAccess.MINECRAFT.player.getAbsorptionAmount() : 20.0F;
      float floatValue14 = floatValue4 + floatValue6;
      String text2 = floatValue14 < floatValue13 ? "LOSE" : (floatValue14 > floatValue13 ? "WIN" : (flag2 ? "LOSE" : "DRAW"));
      String text3 = String.format(Locale.US, "%.1f", floatValue4) + "  -> " + text2;
      float floatValue15 = Draw2D.getTextWidth(text, supplier, floatValue11);
      float floatValue16 = Draw2D.getTextWidth(text3, supplier, floatValue12);
      float floatValue17 = Math.max(64.0F, Math.max(floatValue15, floatValue16) + 4.0F);
      float floatValue18 = floatValue7 + floatValue8 + floatValue9 + floatValue17 + floatValue7;
      float floatValue19 = floatValue7 * 2.0F + floatValue8;
      m_4(event, livingEntity, floatValue + floatValue7, floatValue2, floatValue3);
      if (flag) {
         Draw2D.m_1(floatValue, floatValue2, floatValue18, floatValue19, 6.0F, 2.0F, ColorUtil.rgba(20, 20, 24, (int)(210.0F * floatValue3)));
      }

      Draw2D.roundedRect(floatValue, floatValue2, floatValue18, floatValue19, 6.0F, ColorUtil.rgba(20, 20, 24, (int)(210.0F * floatValue3)));
      float floatValue20 = floatValue + floatValue7;
      float floatValue21 = floatValue2 + floatValue7;
      float floatValue22 = Mth.clamp((livingEntity.hurtTime - event.getFloatValue()) / 10.0F, 0.0F, 1.0F);
      m_8(livingEntity, floatValue20, floatValue21, floatValue8, 0.0F, value, floatValue22);
      float floatValue23 = floatValue20 + floatValue8 + floatValue9;
      float floatValue24 = floatValue2 + floatValue7 + 1.0F;
      Draw2D.drawColoredTextRaw(text, supplier, floatValue23, floatValue24, ColorUtil.rgba(222, 222, 222, value), 0.85F, 1.0F, floatValue17, floatValue11);
      float floatValue25 = floatValue24 + 9.0F;
      Draw2D.drawStyledText(text3, supplier, floatValue23, floatValue25, ColorUtil.rgba(222, 222, 222, value), floatValue12);
      float floatValue26 = floatValue2 + floatValue19 - floatValue7 - floatValue10;
      Draw2D.roundedRect(floatValue23, floatValue26, floatValue17, floatValue10, 2.0F, ColorUtil.rgba(58, 58, 62, (int)(220.0F * floatValue3)));
      float floatValue27 = Math.min(floatValue4 / floatValue5, 1.0F);
      float floatValue28 = ANIMATION.update(floatValue27);
      float floatValue29 = floatValue17 * floatValue28;
      if (floatValue29 > 0.5F) {
         int value2 = ColorUtil.withAlpha(ColorUtil.primaryColor(), value);
         Draw2D.m_6(floatValue23, floatValue26, floatValue29, floatValue10, 1.5F, 0.75F, value2);
      }

      float floatValue30 = ANIMATION3.update(floatValue6 > 0.0F ? 1.0F : 0.0F) * floatValue3;
      float floatValue31 = ANIMATION2.update(Math.min(floatValue6 / floatValue5, 1.0F));
      if (floatValue30 > 0.01F && floatValue31 > 0.001F) {
         int value3 = (int)(255.0F * floatValue30);
         float floatValue32 = floatValue17 * floatValue31;
         if (floatValue32 > 0.5F) {
            Draw2D.m_6(floatValue23, floatValue26, floatValue32, floatValue10, 1.5F, 0.75F, ColorUtil.rgba(255, 200, 80, value3));
         }
      }
   }

   private static void m_4(Render2DEvent event, LivingEntity livingEntity, float floatValue, float floatValue2, float floatValue3) {
      int value = ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue3));
      int value2 = (int)(255.0F * floatValue3);
      float floatValue4 = floatValue;
      float floatValue5 = floatValue2 - 14.0F;
      float floatValue6 = 12.0F;
      float floatValue7 = 13.0F;
      EquipmentSlot[] equipmentSlotArray = new EquipmentSlot[]{
         EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
      };

      for (EquipmentSlot equipmentSlot : equipmentSlotArray) {
         ItemStack itemStack = livingEntity.getItemBySlot(equipmentSlot);
         if (!itemStack.isEmpty()) {
            TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
            MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack, ItemDisplayContext.GUI, livingEntity.level(), livingEntity, (int)(floatValue4 + floatValue5));
            event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
            Render2D.drawItem(trackingItemStackRenderState, floatValue4, floatValue5, floatValue6, value);
            Render2D.drawItemDecorations(itemStack, floatValue4, floatValue5, floatValue6, value2);
            floatValue4 += floatValue7;
         }
      }
   }

   private static void m_8(LivingEntity livingEntity, float floatValue, float floatValue2, float floatValue3, float floatValue4, int value, float floatValue5) {
      int value2 = ColorUtil.rgba(222, 25, 25, value);
      if (livingEntity instanceof Player player) {
         Draw2D.m_4(floatValue, floatValue2, floatValue3, floatValue3, floatValue4, ColorUtil.lerpColor(ColorUtil.rgba(255, 255, 255, value), value2, floatValue5), PlayerFaceTexture.getFace(player));
      } else {
         float floatValue6 = floatValue3 + 1.0F;
         float floatValue7 = floatValue + (floatValue3 - Draw2D.getTextWidth("\uf648", Fonts.SUPPLIER8, floatValue6)) / 2.0F;
         float floatValue8 = floatValue2 + (floatValue3 - floatValue6) / 2.0F;
         Draw2D.drawTextWithShadow("\uf648", Fonts.SUPPLIER8, floatValue7, floatValue8, ColorUtil.lerpColor(ColorUtil.rgba(222, 222, 222, value), value2, floatValue5), floatValue6);
      }
   }
}
