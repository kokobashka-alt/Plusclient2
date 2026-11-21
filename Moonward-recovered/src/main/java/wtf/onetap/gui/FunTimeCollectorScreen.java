package wtf.onetap.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.autobuy.FunTimeItemPresets;
import wtf.onetap.autobuy.ItemKey;
import wtf.onetap.autobuy.ItemMatcher;
import wtf.onetap.config.CollectorConfig;
import wtf.onetap.module.misc.Collector;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.ItemRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;

public class FunTimeCollectorScreen extends BaseScreen {
   private static final String[] TEXT_ARRAY = new String[]{"Все", "Активные"};
   private static FunTimeCollectorScreen funTimeCollectorScreen;
   private final Animation animation = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation2 = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation3 = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation4 = new Animation(250L, 0.0F, Easing.EASING8);
   private final Map<ItemKey, FunTimeCollectorScreen.StateHolder> itemKeyMap = new HashMap<>();
   private final Map<ItemMatcher, Animation> itemMatcherMap = new HashMap<>();
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private int value;
   private ItemKey itemKey;

   private FunTimeCollectorScreen() {
      super("Collector", 444.0F, 280.0F);
   }

   public static FunTimeCollectorScreen m_7_C74() {
      if (funTimeCollectorScreen == null) {
         funTimeCollectorScreen = new FunTimeCollectorScreen();
      }

      CollectorConfig.m_8_void();
      funTimeCollectorScreen.m_0_void();
      return funTimeCollectorScreen;
   }

   @Override
   public void m_0(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue5, float floatValue6, float floatValue7) {
      var itemKeyList = FunTimeItemPresets.getKeys();
      if (this.itemKey == null && !itemKeyList.isEmpty()) {
         this.itemKey = (ItemKey)itemKeyList.getFirst();
      }

      var itemKeyList2 = this.m_9_List();
      ItemKey itemKey2 = this.m_8(itemKeyList2, floatValue5, floatValue6);
      ItemRow.m_4("\uf468", "Collector", "FunTime", 8.0F, 444.0F, 38.0F, floatValue7);
      ItemRow.m_8(TEXT_ARRAY, 250.0F, 38.0F, this.animation.update(this.value), floatValue5, floatValue6, floatValue7);
      this.m_3_void(floatValue5, floatValue6, floatValue7);
      Draw2D.drawPanel(0.0F, 48.0F, 444.0F, 232.0F, floatValue7);
      this.m_8(guiGraphicsExtractor, itemKeyList2, itemKey2, floatValue7);
      this.m_4(guiGraphicsExtractor, itemKeyList, floatValue5, floatValue6, floatValue7);
      if (itemKey2 != null) {
         ItemRow.m_2(this.m_3_C2(itemKey2).textRenderer, getStatusText(itemKey2), floatValue5, floatValue6, 444.0F, 280.0F, floatValue7);
      }
   }

   private void m_3_void(float floatValue5, float floatValue6, float floatValue7) {
      float floatValue8 = this.animation2.update(ItemRow.isInside(floatValue5, floatValue6, 256.0F, 6.0F, 180.0F, 26.0F) ? 1.0F : 0.0F);
      int value2 = (int)((175.0F + 60.0F * floatValue8) * floatValue7);
      Draw2D.m_0(256.0F, 6.0F, 180.0F, 26.0F, 8.0F, 0.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), value2), ColorUtil.withAlpha(ColorUtil.secondaryColor(), value2));
      ItemRow.m_5("\uf04b", "Запустить сбор", 256.0F, 6.0F, 180.0F, 26.0F, 0, 1.0F, floatValue7);
   }

   private void m_8(GuiGraphicsExtractor guiGraphicsExtractor, List<ItemKey> itemKeyList, ItemKey itemKey2, float floatValue5) {
      int value2 = (itemKeyList.size() + 7 - 1) / 7;
      float floatValue6 = value2 > 0 ? value2 * 35.0F - 5.0F : 0.0F;
      float floatValue7 = Math.max(0.0F, floatValue6 - 216.0F);
      this.floatValue2 = Mth.clamp(this.floatValue2, 0.0F, floatValue7);
      this.floatValue = ItemRow.smoothStep(this.floatValue, this.floatValue2);
      Render2D.pushScissor(7.0F, 55.0F, 242.0F, 218.0F);

      for (int i = 0; i < itemKeyList.size(); i++) {
         float floatValue8 = 8.0F + i % 7 * 35.0F;
         float floatValue9 = 56.0F + i / 7 * 35.0F - this.floatValue;
         if (!(floatValue9 + 30.0F < 55.0F) && !(floatValue9 > 273.0F)) {
            this.m_9(guiGraphicsExtractor, (ItemKey)itemKeyList.get(i), floatValue8, floatValue9, itemKeyList.get(i) == itemKey2, floatValue5);
         }
      }

      Render2D.popScissor();
      if (itemKeyList.isEmpty()) {
         ItemRow.drawCenteredText("Нет активных предметов", Fonts.SUPPLIER16, 8.0F, 240.0F, 158.0F, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(200.0F * floatValue5)), 12.0F);
      }

      ItemRow.m_5(250.5F, 56.0F, 216.0F, floatValue6, this.floatValue, floatValue7, floatValue5);
   }

   private void m_9(GuiGraphicsExtractor guiGraphicsExtractor, ItemKey itemKey2, float floatValue5, float floatValue6, boolean flag, float floatValue7) {
      FunTimeCollectorScreen.StateHolder c2 = this.m_3_C2(itemKey2);
      float floatValue8 = c2.animation.update(flag ? 1.0F : 0.0F);
      ItemRow.m_6(floatValue5, floatValue6, 30.0F, floatValue8, c2.animation2.update(itemKey2.isFlag() ? 1.0F : 0.0F), c2.animation3.update(itemKey2 == this.itemKey ? 1.0F : 0.0F), floatValue7);
      float floatValue9 = 16.0F + 2.0F * floatValue8;
      float floatValue10 = floatValue5 + (30.0F - floatValue9) / 2.0F;
      float floatValue11 = floatValue6 + (30.0F - floatValue9) / 2.0F;
      ItemStack itemStack = c2.m_5(itemKey2);
      c2.itemRenderer.m_2(guiGraphicsExtractor, itemStack, floatValue10, floatValue11, floatValue9, floatValue7);
      Render2D.drawItemCount(itemStack, floatValue10, floatValue11, floatValue9, (int)(255.0F * floatValue7));
   }

   private void m_4(GuiGraphicsExtractor guiGraphicsExtractor, List<ItemKey> itemKeyList, float floatValue5, float floatValue6, float floatValue7) {
      Draw2D.roundedRect(256.0F, 56.0F, 180.0F, 216.0F, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue7)));
      int value2 = 0;

      for (ItemKey itemKey2 : itemKeyList) {
         if (itemKey2.isFlag()) {
            value2++;
         }
      }

      ItemRow.drawCenteredText("Активно: " + value2 + " из " + itemKeyList.size(), Fonts.SUPPLIER15, 256.0F, 180.0F, 255.0F, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue7)), 9.5F);
      if (this.itemKey != null) {
         FunTimeCollectorScreen.StateHolder c2 = this.m_3_C2(this.itemKey);
         ItemRow.m_5(
            guiGraphicsExtractor,
            c2.itemRenderer,
            c2.m_5(this.itemKey),
            c2.textRenderer,
            getStatusText(this.itemKey),
            this.itemKey.isFlag() ? ColorUtil.primaryColor() : ItemRow.VALUE2,
            262.0F,
            62.0F,
            168.0F,
            floatValue7
         );
         ItemRow.m_8(
            "\uf466",
            "Собирать",
            262.0F,
            104.0F,
            168.0F,
            this.animation3.update(ItemRow.isInside(floatValue5, floatValue6, 262.0F, 104.0F, 168.0F, 24.0F) ? 1.0F : 0.0F),
            this.animation4.update(this.itemKey.isFlag() ? 1.0F : 0.0F),
            floatValue7
         );
         if (this.itemKey.isStackable()) {
            this.m_6_void(floatValue5, floatValue6, floatValue7);
         }

         this.m_4_void(floatValue5, floatValue6, floatValue7);
      }
   }

   private void m_6_void(float floatValue5, float floatValue6, float floatValue7) {
      ItemRow.m_8(262.0F, 130.0F, 168.0F, 24.0F, 0.0F, floatValue7);
      Draw2D.drawStyledText("\uf468", Fonts.SUPPLIER8, 269.0F, Draw2D.getCenteredY("\uf468", Fonts.SUPPLIER8, 10.0F, 130.0F, 24.0F), ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue7)), 10.0F);
      Draw2D.drawTextWithShadow("Количество", Fonts.SUPPLIER16, 284.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.5F, 130.0F, 24.0F), ColorUtil.withAlpha(ItemRow.VALUE, (int)(230.0F * floatValue7)), 11.5F);
      float floatValue8 = m_7_float(168.0F);
      float floatValue9 = floatValue8 - 14.0F - 22.0F;
      float floatValue10 = 135.0F;
      m_4("\uf068", floatValue9, floatValue10, floatValue5, floatValue6, this.itemKey.getValue() > 1, floatValue7);
      m_4("\uf067", floatValue8, floatValue10, floatValue5, floatValue6, this.itemKey.getValue() < this.itemKey.m_7_int(), floatValue7);
      ItemRow.drawCenteredText(
         String.valueOf(this.itemKey.getValue()),
         Fonts.SUPPLIER16,
         floatValue9 + 14.0F,
         22.0F,
         Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.0F, 130.0F, 24.0F),
         ColorUtil.withAlpha(ItemRow.VALUE, (int)(240.0F * floatValue7)),
         11.0F
      );
   }

   private void m_4_void(float floatValue5, float floatValue6, float floatValue7) {
      var itemMatcherList = m_7(this.itemKey);
      float floatValue8 = this.m_1_float();
      int value2 = ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue7));
      Draw2D.drawStyledText("Условия", Fonts.SUPPLIER15, 264.0F, floatValue8, value2, 10.0F);
      String text = String.valueOf(itemMatcherList.size());
      Draw2D.drawStyledText(text, Fonts.SUPPLIER15, 428.0F - Draw2D.getTextWidth(text, Fonts.SUPPLIER15, 10.0F), floatValue8, value2, 10.0F);
      float floatValue9 = floatValue8 + 16.0F;
      float floatValue10 = 249.0F - floatValue9;
      float floatValue11 = itemMatcherList.isEmpty() ? 0.0F : itemMatcherList.size() * 24.0F - 2.0F;
      float floatValue12 = Math.max(0.0F, floatValue11 - floatValue10);
      this.floatValue4 = Mth.clamp(this.floatValue4, 0.0F, floatValue12);
      this.floatValue3 = ItemRow.smoothStep(this.floatValue3, this.floatValue4);
      float floatValue13 = m_5_float(floatValue12);
      if (itemMatcherList.isEmpty()) {
         ItemRow.drawCenteredText("Без условий", Fonts.SUPPLIER13, 256.0F, 180.0F, floatValue9 + 4.0F, value2, 10.0F);
      } else {
         boolean flag = ItemRow.isInside(floatValue5, floatValue6, 262.0F, floatValue9, 168.0F, floatValue10);
         Render2D.pushScissor(261.0F, floatValue9 - 1.0F, 170.0F, floatValue10 + 2.0F);

         for (int i = 0; i < itemMatcherList.size(); i++) {
            float floatValue14 = floatValue9 + i * 24.0F - this.floatValue3;
            if (!(floatValue14 + 22.0F < floatValue9 - 1.0F) && !(floatValue14 > floatValue9 + floatValue10 + 1.0F)) {
               this.m_6((ItemMatcher)itemMatcherList.get(i), floatValue14, floatValue13, flag, floatValue5, floatValue6, floatValue7);
            }
         }

         Render2D.popScissor();
         ItemRow.m_5(431.5F, floatValue9, floatValue10, floatValue11, this.floatValue3, floatValue12, floatValue7);
      }
   }

   private void m_6(ItemMatcher itemMatcher, float floatValue5, float floatValue6, boolean flag, float floatValue7, float floatValue8, float floatValue9) {
      boolean flag2 = flag && ItemRow.isInside(floatValue7, floatValue8, 262.0F, floatValue5, floatValue6, 22.0F);
      ItemRow.m_8(262.0F, floatValue5, floatValue6, 22.0F, flag2 ? 1.0F : 0.0F, floatValue9);
      float floatValue10 = this.itemMatcherMap
         .computeIfAbsent(itemMatcher, itemMatcher2 -> new Animation(250L, itemMatcher2.isActive() ? 1.0F : 0.0F, Easing.EASING8))
         .update(itemMatcher.isActive() ? 1.0F : 0.0F);
      ItemRow.m_8_void(266.0F, floatValue5 + 4.0F, floatValue10, floatValue9);
      float floatValue11 = 286.0F;
      float floatValue12 = 262.0F + floatValue6 - 4.0F;
      if (itemMatcher.m_6_boolean()) {
         float floatValue13 = m_7_float(floatValue6);
         float floatValue14 = floatValue13 - 14.0F - 2.0F;
         float floatValue15 = floatValue5 + 4.0F;
         boolean flag3 = itemMatcher.isActive();
         m_4("\uf068", floatValue14, floatValue15, floatValue7, floatValue8, flag3, floatValue9);
         m_4("\uf067", floatValue13, floatValue15, floatValue7, floatValue8, flag3, floatValue9);
         floatValue12 = floatValue14 - 4.0F;
      }

      int value2 = ColorUtil.withAlpha(itemMatcher.isActive() ? ItemRow.VALUE : ItemRow.VALUE2, (int)(230.0F * floatValue9));
      Draw2D.drawColoredTextRaw(itemMatcher.m_5_String(), Fonts.SUPPLIER16, floatValue11, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 10.5F, floatValue5, 22.0F), value2, 0.75F, 1.0F, floatValue12 - floatValue11, 10.5F);
   }

   private static void m_4(String text, float floatValue5, float floatValue6, float floatValue7, float floatValue8, boolean flag, float floatValue9) {
      boolean flag2 = flag && ItemRow.isInside(floatValue7, floatValue8, floatValue5, floatValue6, 14.0F, 14.0F);
      Draw2D.roundedRect(floatValue5, floatValue6, 14.0F, 14.0F, 4.0F, ColorUtil.rgba(255, 255, 255, (int)((flag2 ? 34 : 14) * floatValue9)));
      Draw2D.drawStyledText(
         text,
         Fonts.SUPPLIER8,
         Draw2D.getCenteredX(text, Fonts.SUPPLIER8, 8.0F, floatValue5, 14.0F),
         Draw2D.getCenteredY(text, Fonts.SUPPLIER8, 8.0F, floatValue6, 14.0F),
         ColorUtil.withAlpha(flag ? ItemRow.VALUE : ItemRow.VALUE2, (int)((flag ? 230 : 120) * floatValue9)),
         8.0F
      );
   }

   private static float m_7_float(float floatValue5) {
      return 262.0F + floatValue5 - 4.0F - 14.0F;
   }

   private static float m_5_float(float floatValue5) {
      return floatValue5 > 0.0F ? 163.0F : 168.0F;
   }

   private float m_1_float() {
      return this.itemKey.isStackable() ? 162.0F : 134.0F;
   }

   private static String getStatusText(ItemKey itemKey2) {
      String text = itemKey2.isFlag() ? "Собирается" : "Выключен";
      return itemKey2.getValue() > 1 ? text + "  ·  ×" + itemKey2.getValue() : text;
   }

   private FunTimeCollectorScreen.StateHolder m_3_C2(ItemKey itemKey2) {
      return this.itemKeyMap.computeIfAbsent(itemKey2, FunTimeCollectorScreen.StateHolder::new);
   }

   private List<ItemKey> m_9_List() {
      if (this.value == 0) {
         return FunTimeItemPresets.getKeys();
      }

      ArrayList list = new ArrayList();

      for (ItemKey itemKey2 : FunTimeItemPresets.getKeys()) {
         if (itemKey2.isFlag()) {
            list.add(itemKey2);
         }
      }

      return list;
   }

   private static List<ItemMatcher> m_7(ItemKey itemKey2) {
      ArrayList list = new ArrayList();

      for (ItemMatcher itemMatcher : itemKey2.getItemMatcherList()) {
         if (!itemMatcher.isDeny()) {
            list.add(itemMatcher);
         }
      }

      for (ItemMatcher itemMatcher2 : itemKey2.getItemMatcherList2()) {
         if (!itemMatcher2.isDeny()) {
            list.add(itemMatcher2);
         }
      }

      return list;
   }

   private ItemKey m_8(List<ItemKey> itemKeyList, float floatValue5, float floatValue6) {
      if (!ItemRow.isInside(floatValue5, floatValue6, 8.0F, 56.0F, 240.0F, 216.0F)) {
         return null;
      } else {
         float floatValue7 = floatValue5 - 8.0F;
         float floatValue8 = floatValue6 - 56.0F + this.floatValue;
         int value2 = (int)(floatValue7 / 35.0F);
         int value3 = (int)(floatValue8 / 35.0F);
         if (value2 < 7 && !(floatValue7 % 35.0F >= 30.0F) && !(floatValue8 % 35.0F >= 30.0F)) {
            int value4 = value3 * 7 + value2;
            return value4 < itemKeyList.size() ? (ItemKey)itemKeyList.get(value4) : null;
         } else {
            return null;
         }
      }
   }

   @Override
   public int m_1_int() {
      Collector collector = Onetap.MODULE_MANAGER.get(Collector.class);
      return collector != null ? collector.getOpenEditor() : -1;
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent event, boolean flag) {
      float floatValue5 = this.m_4_float(event.x());
      float floatValue6 = this.m_7_float(event.y());
      int value2 = event.button();
      if (value2 >= 2 && this.m_7_boolean(-value2 - 1)) {
         return true;
      }

      if (ItemRow.isInside(floatValue5, floatValue6, 256.0F, 6.0F, 180.0F, 26.0F)) {
         Collector collector = Onetap.MODULE_MANAGER.get(Collector.class);
         if (collector != null) {
            collector.m_4_void();
         }

         return true;
      } else {
         int value3 = ItemRow.m_1(TEXT_ARRAY, 250.0F, 38.0F, floatValue5, floatValue6);
         if (value3 >= 0) {
            this.value = value3;
            this.floatValue2 = 0.0F;
            return true;
         }

         ItemKey itemKey2 = this.m_8(this.m_9_List(), floatValue5, floatValue6);
         if (itemKey2 != null) {
            if (itemKey2 != this.itemKey) {
               this.floatValue3 = this.floatValue4 = 0.0F;
            }

            this.itemKey = itemKey2;
            if (value2 == 1 || value2 == 0 && flag) {
               toggle(itemKey2);
            }

            return true;
         } else {
            return this.itemKey != null && this.m_8_boolean(floatValue5, floatValue6) ? true : super.mouseClicked(event, flag);
         }
      }
   }

   private boolean m_8_boolean(float floatValue5, float floatValue6) {
      if (ItemRow.isInside(floatValue5, floatValue6, 262.0F, 104.0F, 168.0F, 24.0F)) {
         toggle(this.itemKey);
         return true;
      }

      if (this.itemKey.isStackable()) {
         float floatValue7 = m_7_float(168.0F);
         float floatValue8 = 135.0F;
         if (ItemRow.isInside(floatValue5, floatValue6, floatValue7 - 14.0F - 22.0F, floatValue8, 14.0F, 14.0F)) {
            this.itemKey.setValue(Math.max(1, this.itemKey.getValue() - 1));
            CollectorConfig.m_5_void();
            return true;
         }

         if (ItemRow.isInside(floatValue5, floatValue6, floatValue7, floatValue8, 14.0F, 14.0F)) {
            this.itemKey.setValue(Math.min(this.itemKey.m_7_int(), this.itemKey.getValue() + 1));
            CollectorConfig.m_5_void();
            return true;
         }
      }

      var itemMatcherList = m_7(this.itemKey);
      float floatValue9 = this.m_1_float() + 16.0F;
      if (!ItemRow.isInside(floatValue5, floatValue6, 262.0F, floatValue9, 168.0F, 249.0F - floatValue9)) {
         return false;
      }

      float floatValue10 = itemMatcherList.size() * 24.0F - 2.0F;
      float floatValue11 = m_5_float(Math.max(0.0F, floatValue10 - (249.0F - floatValue9)));

      for (int i = 0; i < itemMatcherList.size(); i++) {
         float floatValue12 = floatValue9 + i * 24.0F - this.floatValue3;
         if (ItemRow.isInside(floatValue5, floatValue6, 262.0F, floatValue12, floatValue11, 22.0F)) {
            ItemMatcher itemMatcher = (ItemMatcher)itemMatcherList.get(i);
            float floatValue13 = m_7_float(floatValue11);
            float floatValue14 = floatValue12 + 4.0F;
            if (itemMatcher.m_6_boolean() && ItemRow.isInside(floatValue5, floatValue6, floatValue13 - 14.0F - 2.0F, floatValue14, 14.0F, 14.0F)) {
               itemMatcher.m_4_void(-1);
            } else if (itemMatcher.m_6_boolean() && ItemRow.isInside(floatValue5, floatValue6, floatValue13, floatValue14, 14.0F, 14.0F)) {
               itemMatcher.m_4_void(1);
            } else {
               itemMatcher.m_2_void(!itemMatcher.isActive());
            }

            CollectorConfig.m_5_void();
            return true;
         }
      }

      return false;
   }

   private static void toggle(ItemKey itemKey2) {
      itemKey2.setFlag(!itemKey2.isFlag());
      CollectorConfig.m_5_void();
   }

   @Override
   public boolean mouseScrolled(double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4) {
      if (this.m_4_float(doubleValue) >= 256.0F) {
         this.floatValue4 -= (float)(doubleValue4 * 24.0);
      } else {
         this.floatValue2 -= (float)(doubleValue4 * 35.0);
      }

      return true;
   }

   private static final class StateHolder {
      private final Animation animation;
      private final Animation animation2;
      private final Animation animation3;
      private final ItemRenderer itemRenderer;
      private final TextRenderer textRenderer;
      private ItemStack itemStack;

      private StateHolder(ItemKey itemKey2) {
         this.animation = new Animation(150L, 0.0F, Easing.EASING8);
         this.animation3 = new Animation(150L, 0.0F, Easing.EASING8);
         this.itemRenderer = new ItemRenderer();
         this.itemStack = ItemStack.EMPTY;
         this.textRenderer = TextRenderer.m_2(itemKey2.getText(), Fonts.SUPPLIER16, Fonts.SUPPLIER17);
         this.animation2 = new Animation(200L, itemKey2.isFlag() ? 1.0F : 0.0F, Easing.EASING8);
      }

      private ItemStack m_5(ItemKey itemKey2) {
         if (this.itemStack.getCount() != Math.max(1, itemKey2.getValue())) {
            this.itemStack = itemKey2.m_6_ItemStack();
         }

         return this.itemStack;
      }
   }
}
