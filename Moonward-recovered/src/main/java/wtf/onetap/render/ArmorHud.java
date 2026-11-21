package wtf.onetap.render;

import java.util.function.Supplier;
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

public class ArmorHud implements MinecraftAccess {
   private static final Animation ANIMATION = new Animation(350L, 0.0F, Easing.EASING8);
   private static final Animation ANIMATION2 = new Animation(350L, 0.0F, Easing.EASING8);
   private static final Animation ANIMATION3 = new Animation(250L, 0.0F, Easing.EASING8);

   private ArmorHud() {
   }

   public static void m_0(Render2DEvent event, LivingEntity livingEntity, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, String text, boolean flag) {
      Assistent.m_1_String();
      int value = (int)(255.0F * floatValue3);
      Supplier supplier = flag ? Fonts.SUPPLIER2 : Fonts.SUPPLIER4;
      float floatValue7 = 108.0F;
      float floatValue8 = 28.0F;
      HudRenderer.m_9(floatValue, floatValue2, floatValue7, floatValue8, floatValue3, flag);
      float floatValue9 = 20.0F;
      float floatValue10 = floatValue + 4.5F;
      float floatValue11 = floatValue2 + (floatValue8 - floatValue9) / 2.0F;
      float floatValue12 = Mth.clamp((livingEntity.hurtTime - event.getFloatValue()) / 10.0F, 0.0F, 1.0F);
      m_4(livingEntity, floatValue10, floatValue11, floatValue9, 2.0F, value, floatValue12);
      float floatValue13 = 8.5F;
      float floatValue14 = 1.2F;
      float floatValue15 = floatValue + floatValue7 - floatValue13 - 6.0F;
      float floatValue16 = floatValue2 + floatValue8 / 2.0F;
      float floatValue17 = floatValue10 + floatValue9 + 4.5F;
      float floatValue18 = 8.0F;
      float floatValue19 = floatValue2 + 4.5F;
      float floatValue20 = floatValue15 - floatValue13 - 5.0F - floatValue17;
      float floatValue21 = Draw2D.getTextWidth(text, supplier, floatValue18);
      if (floatValue21 <= floatValue20) {
         Draw2D.drawStyledText(text, supplier, floatValue17, floatValue19, ColorUtil.rgba(235, 235, 235, value), floatValue18);
      } else {
         Draw2D.drawColoredTextRaw(text, supplier, floatValue17, floatValue19, ColorUtil.rgba(235, 235, 235, value), 0.82F, 1.0F, floatValue20, floatValue18);
      }

      float floatValue22 = floatValue2 + 15.0F;
      m_1(event, livingEntity, floatValue17, floatValue22, 9.0F, 8.0F, floatValue3);
      float floatValue23 = Math.min(floatValue4 / floatValue5, 1.0F);
      float floatValue24 = ANIMATION.update(floatValue23);
      if (floatValue24 > 0.001F) {
         int value2 = ColorUtil.primaryColor();
         int value3 = ColorUtil.secondaryColor();
         int value4 = (int)(110.0F * floatValue3);
         m_6(floatValue15, floatValue16, floatValue13, floatValue14 + 2.0F, floatValue24, -1.5707963F, 3.0F, ColorUtil.withAlpha(value3, value4), ColorUtil.withAlpha(value2, value4), 24);
         m_6(floatValue15, floatValue16, floatValue13, floatValue14, floatValue24, -1.5707963F, 0.0F, ColorUtil.withAlpha(value3, value), ColorUtil.withAlpha(value2, value), 24);
      }

      float floatValue25 = ANIMATION3.update(floatValue6 > 0.0F ? 1.0F : 0.0F) * floatValue3;
      float floatValue26 = ANIMATION2.update(Math.min(floatValue6 / floatValue5, 1.0F));
      if (floatValue25 > 0.01F && floatValue26 > 0.001F) {
         int value5 = (int)(255.0F * floatValue25);
         Draw2D.m_5(floatValue15, floatValue16, floatValue13, floatValue14, floatValue26, -1.5707963F, 0.3F, ColorUtil.withAlpha(ColorUtil.rgba(255, 200, 80, 255), value5));
      }

      String text2 = String.valueOf((int)Math.ceil(floatValue4));
      float floatValue27 = 7.0F;
      float floatValue28 = Draw2D.getTextWidth(text2, supplier, floatValue27);
      float floatValue29 = floatValue15 - floatValue28 / 2.0F;
      float floatValue30 = Draw2D.getTextBaseline(supplier, floatValue27, floatValue16 - floatValue13, floatValue13 * 2.0F);
      Draw2D.drawStyledText(text2, supplier, floatValue29, floatValue30, ColorUtil.rgba(235, 235, 235, value), floatValue27);
   }

   private static void m_1(Render2DEvent event, LivingEntity livingEntity, float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5) {
      int value = ColorUtil.rgba(255, 255, 255, (int)(255.0F * floatValue5));
      int value2 = (int)(255.0F * floatValue5);

      for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{
         EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
      }) {
         ItemStack itemStack = livingEntity.getItemBySlot(equipmentSlot);
         if (!itemStack.isEmpty()) {
            TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
            MinecraftAccess.MINECRAFT.getItemModelResolver().updateForTopItem(trackingItemStackRenderState, itemStack, ItemDisplayContext.GUI, livingEntity.level(), livingEntity, (int)(floatValue + floatValue2));
            event.getGuiGraphicsExtractor().guiRenderState.getItemModelIdentities().add(trackingItemStackRenderState.getModelIdentity());
            Render2D.drawItem(trackingItemStackRenderState, floatValue, floatValue2, floatValue3, value);
            Render2D.drawItemDecorations(itemStack, floatValue, floatValue2, floatValue3, value2);
            floatValue += floatValue4;
         }
      }
   }

   private static void m_4(LivingEntity livingEntity, float floatValue, float floatValue2, float floatValue3, float floatValue4, int value, float floatValue5) {
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

   private static void m_6(float floatValue, float floatValue2, float floatValue3, float floatValue4, float floatValue5, float floatValue6, float floatValue7, int value, int value2, int value3) {
      if (!(floatValue3 <= 0.0F) && !(floatValue4 <= 0.0F) && !(floatValue5 <= 0.0F) && value3 >= 1) {
         double doubleValue = System.currentTimeMillis() / 200.0;
         float floatValue8 = floatValue5 * (float) (Math.PI * 2);
         float floatValue9 = floatValue8 / value3;

         for (int i = 0; i < value3; i++) {
            float floatValue10 = floatValue6 + i * floatValue9;
            float floatValue11 = (float)(Math.sin(doubleValue + floatValue10) * 0.5 + 0.5);
            int value4 = ColorUtil.lerpColor(value, value2, floatValue11);
            Draw2D.m_5(floatValue, floatValue2, floatValue3, floatValue4, floatValue9 / (float) (Math.PI * 2), floatValue10, floatValue7, value4);
         }
      }
   }
}
