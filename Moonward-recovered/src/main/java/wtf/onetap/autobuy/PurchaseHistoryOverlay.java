package wtf.onetap.autobuy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.gui.ItemRow;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.ItemRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;

public class PurchaseHistoryOverlay {
   private static final PurchaseHistoryOverlay PURCHASE_HISTORY_OVERLAY = new PurchaseHistoryOverlay();
   private final Animation animation;
   private final Map<ItemFormatter, PurchaseHistoryOverlay.StateHolder> itemFormatterMap;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;

   private PurchaseHistoryOverlay() {
      this.animation = new Animation(300L, 0.0F, Easing.EASING8);
      this.itemFormatterMap = new HashMap<>();
      this.floatValue = 1.0F;
   }

   public static PurchaseHistoryOverlay m_7_C5_v() {
      return PURCHASE_HISTORY_OVERLAY;
   }

   public boolean m_4_boolean() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.gui.screen() == null) {
         return false;
      } else {
         return mc.gui.screen() instanceof ContainerScreen containerScreen ? AutoBuyUtil.m_1(containerScreen) : false;
      }
   }

   @EventTarget(priority = EventPriority.LOWEST)
   private void onRender2D(Render2DEvent event) {
      if (!this.m_4_boolean()) {
         this.animation.setDuration(0.0F);
      } else {
         Minecraft mc = Minecraft.getInstance();
         ContainerScreen containerScreen = (ContainerScreen)mc.gui.screen();
         Render2D.setTransform(1.0F, 0.0F, 0.0F);
         Draw2D.roundedRect(0.0F, 0.0F, containerScreen.width, containerScreen.height, 0.0F, -938471408);
         this.animation.setTarget(1.0F);
         float floatValue6 = this.animation.get();
         if (!(floatValue6 < 0.01F)) {
            float floatValue7 = (containerScreen.width - 176) / 2.0F;
            float floatValue8 = 114 + ((ChestMenu)containerScreen.getMenu()).getRowCount() * 18;
            float floatValue9 = Math.max(4.0F, (containerScreen.height - floatValue8) / 2.0F);
            float floatValue10 = Math.min(1.0F, Math.min((floatValue7 - 12.0F) / 210.0F, (containerScreen.height - floatValue9 - 4.0F) / 284.0F));
            this.floatValue = Math.max(0.3F, floatValue10) * (0.9F + 0.1F * floatValue6);
            this.floatValue2 = Math.max(4.0F, floatValue7 - 8.0F - 210.0F * this.floatValue);
            this.floatValue3 = floatValue9;
            float floatValue11 = this.m_4_float(mc.mouseHandler.getScaledXPos(mc.getWindow()));
            float floatValue12 = this.m_8_float(mc.mouseHandler.getScaledYPos(mc.getWindow()));
            Render2D.setTransform(this.floatValue, this.floatValue2, this.floatValue3);

            try {
               this.m_6(event.getGuiGraphicsExtractor(), floatValue11, floatValue12, floatValue6);
            } finally {
               Render2D.setTransform(1.0F, 0.0F, 0.0F);
            }
         }
      }
   }

   private void m_6(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue6, float floatValue7, float floatValue8) {
      var itemFormatterList = ItemEntry.m_9_C7x().getItemFormatterList2();
      ItemFormatter.m_5_StringArr();
      this.itemFormatterMap.keySet().retainAll(new HashSet(itemFormatterList));
      m_4_void(0.0F, 0.0F, 210.0F, 38.0F, floatValue8);
      ItemRow.m_6("\uf1da", "История", "Покупок: " + itemFormatterList.size(), 8.0F, 38.0F, floatValue8);
      m_4_void(0.0F, 48.0F, 210.0F, 236.0F, floatValue8);
      float floatValue9 = itemFormatterList.isEmpty() ? 0.0F : itemFormatterList.size() * 32.0F - 4.0F;
      float floatValue10 = Math.max(0.0F, floatValue9 - 220.0F);
      this.floatValue5 = Mth.clamp(this.floatValue5, 0.0F, floatValue10);
      this.floatValue4 = ItemRow.smoothStep(this.floatValue4, this.floatValue5);
      boolean flag = ItemRow.isInside(floatValue6, floatValue7, 8.0F, 56.0F, 188.0F, 220.0F);
      ItemFormatter itemFormatter = null;
      Render2D.pushScissor(7.0F, 55.0F, 190.0F, 222.0F);
      float floatValue11 = 56.0F - this.floatValue4;

      for (ItemFormatter itemFormatter2 : itemFormatterList) {
         if (floatValue11 + 28.0F >= 55.0F && floatValue11 <= 277.0F) {
            boolean flag2 = flag && ItemRow.isInside(floatValue6, floatValue7, 8.0F, floatValue11, 188.0F, 28.0F);
            if (flag2) {
               itemFormatter = itemFormatter2;
            }

            this.m_9(guiGraphicsExtractor, itemFormatter2, 8.0F, floatValue11, flag2, floatValue8);
         }

         floatValue11 += 32.0F;
      }

      Render2D.popScissor();
      if (itemFormatterList.isEmpty()) {
         this.m_8_void(floatValue8);
      }

      ItemRow.m_5(198.5F, 56.0F, 220.0F, floatValue9, this.floatValue4, floatValue10, floatValue8);
      if (itemFormatter != null) {
         ItemRow.m_2(this.m_3(itemFormatter).textRenderer, "Продавец: " + itemFormatter.getText2(), floatValue6, floatValue7, 210.0F, 284.0F, floatValue8);
      }
   }

   private static void m_4_void(float floatValue6, float floatValue7, float floatValue8, float floatValue9, float floatValue10) {
      Draw2D.m_1(floatValue6, floatValue7, floatValue8, floatValue9, 8.0F, 4.0F, ColorUtil.rgba(12, 12, 12, (int)(80.0F * floatValue10)));
      Draw2D.m_7(floatValue6, floatValue7, floatValue8, floatValue9, 8.0F, 4.0F * floatValue10, ColorUtil.rgba(62, 62, 68, (int)(175.0F * floatValue10)));
      Draw2D.m_4(floatValue6, floatValue7, floatValue8, floatValue9, 0.5F, 8.0F, ColorUtil.rgba(200, 200, 200, (int)(45.0F * floatValue10)));
   }

   private void m_9(GuiGraphicsExtractor guiGraphicsExtractor, ItemFormatter itemFormatter, float floatValue6, float floatValue7, boolean flag, float floatValue8) {
      PurchaseHistoryOverlay.StateHolder c0 = this.m_3(itemFormatter);
      ItemRow.m_8(floatValue6, floatValue7, 188.0F, 28.0F, c0.animation.update(flag ? 1.0F : 0.0F), floatValue8);
      float floatValue9 = floatValue7 + 3.0F;
      Draw2D.roundedRect(floatValue6 + 3.0F, floatValue9, 22.0F, 22.0F, 5.0F, ColorUtil.rgba(0, 0, 0, (int)(40.0F * floatValue8)));
      Draw2D.m_4(floatValue6 + 3.0F, floatValue9, 22.0F, 22.0F, 0.5F, 5.0F, ColorUtil.rgba(190, 190, 190, (int)(60.0F * floatValue8)));
      c0.itemRenderer.m_2(guiGraphicsExtractor, c0.itemStack, floatValue6 + 3.0F + 3.0F, floatValue9 + 3.0F, 16.0F, floatValue8);
      String text = itemFormatter.m_6_String();
      String text2 = itemFormatter.m_4_String();
      float floatValue10 = floatValue6 + 188.0F - 6.0F;
      float floatValue11 = Draw2D.getTextWidth(text, Fonts.SUPPLIER16, 11.0F);
      Draw2D.drawStyledText(text, Fonts.SUPPLIER16, floatValue10 - floatValue11, floatValue7 + 4.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), (int)(240.0F * floatValue8)), 11.0F);
      Draw2D.drawStyledText(text2, Fonts.SUPPLIER15, floatValue10 - Draw2D.getTextWidth(text2, Fonts.SUPPLIER15, 9.5F), floatValue7 + 16.0F, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue8)), 9.5F);
      float floatValue12 = floatValue6 + 3.0F + 22.0F + 6.0F;
      float floatValue13 = floatValue10 - floatValue11 - 6.0F - floatValue12;
      Render2D.pushScissor(floatValue12, floatValue7, floatValue13, 28.0F);
      c0.textRenderer.draw(floatValue12, floatValue7 + 4.0F, 11.0F, (int)(240.0F * floatValue8));
      Draw2D.drawStyledText(itemFormatter.getText2(), Fonts.SUPPLIER15, floatValue12, floatValue7 + 16.0F, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue8)), 9.5F);
      Render2D.popScissor();
   }

   private void m_8_void(float floatValue6) {
      float floatValue7 = 22.0F;
      float floatValue8 = 136.0F;
      Draw2D.drawStyledText("\uf1da", Fonts.SUPPLIER8, Draw2D.getCenteredX("\uf1da", Fonts.SUPPLIER8, floatValue7, 8.0F, 188.0F), floatValue8, ColorUtil.rgba(120, 120, 120, (int)(200.0F * floatValue6)), floatValue7);
      ItemRow.drawCenteredText("История пуста", Fonts.SUPPLIER16, 8.0F, 188.0F, floatValue8 + 34.0F, ColorUtil.withAlpha(ItemRow.VALUE, (int)(230.0F * floatValue6)), 12.0F);
      ItemRow.drawCenteredText("Здесь появятся покупки AutoBuy", Fonts.SUPPLIER13, 8.0F, 188.0F, floatValue8 + 50.0F, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue6)), 10.0F);
   }

   private PurchaseHistoryOverlay.StateHolder m_3(ItemFormatter itemFormatter) {
      return this.itemFormatterMap.computeIfAbsent(itemFormatter, PurchaseHistoryOverlay.StateHolder::new);
   }

   private float m_4_float(double doubleValue) {
      return (float)((doubleValue - this.floatValue2) / this.floatValue);
   }

   private float m_8_float(double doubleValue) {
      return (float)((doubleValue - this.floatValue3) / this.floatValue);
   }

   public boolean m_3_boolean(double doubleValue, double doubleValue2, double doubleValue3) {
      if (!this.m_4_boolean()) {
         return false;
      }

      if (!ItemRow.isInside(this.m_4_float(doubleValue), this.m_8_float(doubleValue2), 0.0F, 0.0F, 210.0F, 284.0F)) {
         return false;
      }

      this.floatValue5 -= (float)doubleValue3 * 32.0F;
      return true;
   }

   private static final class StateHolder {
      private final Animation animation;
      private final ItemRenderer itemRenderer;
      private final ItemStack itemStack;
      private final TextRenderer textRenderer;

      private StateHolder(ItemFormatter itemFormatter) {
         this.animation = new Animation(150L, 0.0F, Easing.EASING8);
         this.itemRenderer = new ItemRenderer();
         this.itemStack = itemFormatter.getItemStack().isEmpty() ? new ItemStack(Items.PAPER) : itemFormatter.getItemStack();
         this.textRenderer = TextRenderer.m_2(itemFormatter.format(), Fonts.SUPPLIER16, Fonts.SUPPLIER17);
      }
   }
}
