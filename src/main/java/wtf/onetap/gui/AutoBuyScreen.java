package wtf.onetap.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.autobuy.AutoBuyUtil;
import wtf.onetap.module.pve.C4q;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.ItemRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;

public class AutoBuyScreen extends BaseScreen {
   private static final String[] TEXT_ARRAY = new String[]{"Все", "Покупка", "Парсер"};
   private static final int VALUE = ColorUtil.rgba(255, 196, 64, 255);
   private static AutoBuyScreen autoBuyScreen;
   private final Animation animation = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation2 = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation3 = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation4 = new Animation(250L, 0.0F, Easing.EASING8);
   private final Animation animation5 = new Animation(250L, 0.0F, Easing.EASING8);
   private final Animation animation6 = new Animation(180L, 0.0F, Easing.EASING8);
   private final Animation animation7 = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation8 = new Animation(150L, 0.0F, Easing.EASING8);
   private final SearchBox searchBox = new SearchBox();
   private final Map<String, AutoBuyScreen.StateHolder> stringMap = new HashMap<>();
   private List<AutoBuyUtil.DataRecord> c2List = List.of();
   private float floatValue;
   private float floatValue2;
   private int value;
   private String text = "";
   private AutoBuyUtil.DataRecord c2;
   private String text2 = "";
   private boolean flag;

   private AutoBuyScreen() {
      super("AutoBuy", 440.0F, 280.0F);
   }

   public static AutoBuyScreen getInstance() {
      if (autoBuyScreen == null) {
         autoBuyScreen = new AutoBuyScreen();
      }

      autoBuyScreen.c2List = AutoBuyUtil.m_2_List();
      if (autoBuyScreen.c2 != null) {
         autoBuyScreen.c2 = autoBuyScreen.findEntry(autoBuyScreen.c2.m_7_l());
      }

      autoBuyScreen.m_0_void();
      autoBuyScreen.flag = false;
      autoBuyScreen.searchBox.m_1_void();
      return autoBuyScreen;
   }

   @Override
   public void m_0(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue3, float floatValue4, float floatValue5) {
      var c2List2 = this.getFilteredEntries();
      AutoBuyUtil.DataRecord c22 = this.m_4(c2List2, floatValue3, floatValue4);
      ItemRow.m_4("\uf07a", "AutoBuy", "SpookyTime", 8.0F, 440.0F, 38.0F, floatValue5);
      ItemRow.m_8(TEXT_ARRAY, 285.0F, 38.0F, this.animation.update(this.value), floatValue3, floatValue4, floatValue5);
      this.searchBox.m_5(291.0F, 141.0F, 38.0F, floatValue5);
      Draw2D.drawPanel(0.0F, 48.0F, 440.0F, 232.0F, floatValue5);
      this.m_0(guiGraphicsExtractor, c2List2, c22, floatValue5);
      this.m_4(guiGraphicsExtractor, floatValue5, floatValue3, floatValue4);
      if (c22 != null) {
         ItemRow.m_2(this.m_4(c22).textRenderer, getPriceText(c22), floatValue3, floatValue4, 440.0F, 280.0F, floatValue5);
      }
   }

   private void m_0(GuiGraphicsExtractor guiGraphicsExtractor, List<AutoBuyUtil.DataRecord> c2List2, AutoBuyUtil.DataRecord c22, float floatValue3) {
      int value2 = (c2List2.size() + 8 - 1) / 8;
      float floatValue4 = value2 > 0 ? value2 * 35.0F - 5.0F : 0.0F;
      float floatValue5 = Math.max(0.0F, floatValue4 - 216.0F);
      this.floatValue2 = Mth.clamp(this.floatValue2, 0.0F, floatValue5);
      this.floatValue = ItemRow.smoothStep(this.floatValue, this.floatValue2);
      Render2D.pushScissor(7.0F, 55.0F, 277.0F, 218.0F);

      for (int i = 0; i < c2List2.size(); i++) {
         float floatValue6 = 8.0F + i % 8 * 35.0F;
         float floatValue7 = 56.0F + i / 8 * 35.0F - this.floatValue;
         if (!(floatValue7 + 30.0F < 55.0F) && !(floatValue7 > 273.0F)) {
            this.m_6(guiGraphicsExtractor, (AutoBuyUtil.DataRecord)c2List2.get(i), floatValue6, floatValue7, c2List2.get(i) == c22, floatValue3);
         }
      }

      Render2D.popScissor();
      if (c2List2.isEmpty()) {
         ItemRow.drawCenteredText("Ничего не найдено", Fonts.SUPPLIER16, 8.0F, 275.0F, 158.0F, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(200.0F * floatValue3)), 12.0F);
      }

      ItemRow.m_5(285.5F, 56.0F, 216.0F, floatValue4, this.floatValue, floatValue5, floatValue3);
   }

   private void m_6(GuiGraphicsExtractor guiGraphicsExtractor, AutoBuyUtil.DataRecord c22, float floatValue3, float floatValue4, boolean flag2, float floatValue5) {
      AutoBuyScreen.StateHolder c23 = this.m_4(c22);
      float floatValue6 = c23.animation.update(flag2 ? 1.0F : 0.0F);
      float floatValue7 = c23.animation3.update(AutoBuyUtil.isParserEnabled(c22.m_7_l()) ? 1.0F : 0.0F);
      ItemRow.m_6(
         floatValue3, floatValue4, 30.0F, floatValue6, c23.animation2.update(AutoBuyUtil.isEnabled(c22.m_7_l()) ? 1.0F : 0.0F), c23.animation4.update(c22 == this.c2 ? 1.0F : 0.0F), floatValue5
      );
      float floatValue8 = 16.0F + 2.0F * floatValue6;
      c23.itemRenderer.m_2(guiGraphicsExtractor, c23.itemStack, floatValue3 + (30.0F - floatValue8) / 2.0F, floatValue4 + (30.0F - floatValue8) / 2.0F, floatValue8, floatValue5);
      if (floatValue7 > 0.01F) {
         float floatValue9 = 5.0F * floatValue7;
         Draw2D.roundedRect(floatValue3 + 30.0F - 6.0F - floatValue9 / 2.0F, floatValue4 + 6.0F - floatValue9 / 2.0F, floatValue9, floatValue9, floatValue9 / 2.0F, ColorUtil.withAlpha(VALUE, (int)(255.0F * floatValue5)));
      }
   }

   private void m_4(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue3, float floatValue4, float floatValue5) {
      Draw2D.roundedRect(291.0F, 56.0F, 141.0F, 216.0F, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue3)));
      if (this.c2 == null) {
         this.m_0_void(floatValue3);
      } else {
         this.m_6(guiGraphicsExtractor, floatValue3, floatValue4, floatValue5);
      }

      int value2 = 0;
      int value3 = 0;

      for (AutoBuyUtil.DataRecord c22 : this.c2List) {
         if (AutoBuyUtil.isEnabled(c22.m_7_l())) {
            value2++;
         }

         if (AutoBuyUtil.isParserEnabled(c22.m_7_l())) {
            value3++;
         }
      }

      int value4 = ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue3));
      ItemRow.drawCenteredText("Покупка: " + value2 + "  ·  Парсер: " + value3, Fonts.SUPPLIER15, 291.0F, 141.0F, 218.0F, value4, 9.5F);
      ItemRow.drawCenteredText("Скидка парсера: " + AutoBuyUtil.getDiscount() + "%", Fonts.SUPPLIER15, 291.0F, 141.0F, 230.0F, value4, 9.5F);
      float floatValue6 = this.animation8.update(ItemRow.isInside(floatValue4, floatValue5, 297.0F, 244.0F, 129.0F, 22.0F) ? 1.0F : 0.0F);
      int value5 = ColorUtil.lerpColor(ColorUtil.rgba(255, 255, 255, 12), ColorUtil.rgba(235, 80, 80, 70), floatValue6);
      ItemRow.m_5("\uf011", "Выключить всё", 297.0F, 244.0F, 129.0F, 22.0F, value5, 1.0F, floatValue3);
   }

   private void m_0_void(float floatValue3) {
      float floatValue4 = 22.0F;
      float floatValue5 = 76.0F;
      Draw2D.drawStyledText("\uf07a", Fonts.SUPPLIER8, Draw2D.getCenteredX("\uf07a", Fonts.SUPPLIER8, floatValue4, 291.0F, 141.0F), floatValue5, ColorUtil.rgba(120, 120, 120, (int)(200.0F * floatValue3)), floatValue4);
      ItemRow.drawCenteredText("Выберите предмет", Fonts.SUPPLIER16, 291.0F, 141.0F, floatValue5 + 34.0F, ColorUtil.withAlpha(ItemRow.VALUE, (int)(230.0F * floatValue3)), 12.0F);
      String[] stringArray = new String[]{"ЛКМ - выбрать", "2×ЛКМ - покупка", "ПКМ - парсер"};

      for (int i = 0; i < stringArray.length; i++) {
         ItemRow.drawCenteredText(stringArray[i], Fonts.SUPPLIER13, 291.0F, 141.0F, floatValue5 + 56.0F + i * 13, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue3)), 10.0F);
      }
   }

   private void m_6(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue3, float floatValue4, float floatValue5) {
      AutoBuyScreen.StateHolder c22 = this.m_4(this.c2);
      boolean flag2 = AutoBuyUtil.getPrice(this.c2.m_7_l()) > 0;
      ItemRow.m_5(guiGraphicsExtractor, c22.itemRenderer, c22.itemStack, c22.textRenderer, getPriceText(this.c2), flag2 ? ColorUtil.primaryColor() : ItemRow.VALUE2, 297.0F, 62.0F, 129.0F, floatValue3);
      ItemRow.m_8(
         "\uf07a",
         "Покупка",
         297.0F,
         104.0F,
         129.0F,
         this.animation2.update(ItemRow.isInside(floatValue4, floatValue5, 297.0F, 104.0F, 129.0F, 24.0F) ? 1.0F : 0.0F),
         this.animation4.update(AutoBuyUtil.isEnabled(this.c2.m_7_l()) ? 1.0F : 0.0F),
         floatValue3
      );
      ItemRow.m_8(
         "\uf02c",
         "Парсер",
         297.0F,
         130.0F,
         129.0F,
         this.animation3.update(ItemRow.isInside(floatValue4, floatValue5, 297.0F, 130.0F, 129.0F, 24.0F) ? 1.0F : 0.0F),
         this.animation5.update(AutoBuyUtil.isParserEnabled(this.c2.m_7_l()) ? 1.0F : 0.0F),
         floatValue3
      );
      this.m_2_void(floatValue3);
      float floatValue6 = this.animation7.update(ItemRow.isInside(floatValue4, floatValue5, 297.0F, 186.0F, 129.0F, 22.0F) ? 1.0F : 0.0F);
      ItemRow.m_5("\uf0e3", "Найти на аукционе", 297.0F, 186.0F, 129.0F, 22.0F, ColorUtil.rgba(255, 255, 255, (int)(12.0F + 18.0F * floatValue6)), floatValue6, floatValue3);
   }

   private void m_2_void(float floatValue3) {
      float floatValue4 = this.animation6.update(this.flag ? 1.0F : 0.0F);
      Draw2D.roundedRect(297.0F, 160.0F, 129.0F, 22.0F, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(40.0F * floatValue3)));
      Draw2D.m_4(297.0F, 160.0F, 129.0F, 22.0F, 0.5F, 6.0F, ColorUtil.rgba(190, 190, 190, (int)((60.0F + 60.0F * floatValue4) * floatValue3)));
      float floatValue5 = Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.0F, 160.0F, 22.0F);
      Draw2D.drawStyledText("Цена до", Fonts.SUPPLIER15, 304.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER15, 10.0F, 160.0F, 22.0F), ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue3)), 10.0F);
      int value2 = AutoBuyUtil.getPrice(this.c2.m_7_l());
      String text3 = this.flag ? "$" + this.text2 : (value2 > 0 ? formatPrice(value2) : "не задана");
      float floatValue6 = 419.0F;
      float floatValue7 = Draw2D.getTextWidth(text3, Fonts.SUPPLIER16, 11.0F);
      if (this.flag) {
         floatValue6 -= 3.0F;
         if (System.currentTimeMillis() / 500L % 2L == 0L) {
            Draw2D.roundedRect(floatValue6 + 1.5F, floatValue5, 1.0F, 12.0F, 0.0F, ColorUtil.rgba(245, 245, 245, (int)(235.0F * floatValue3)));
         }
      }

      int value3 = !this.flag && value2 <= 0 ? ItemRow.VALUE2 : ItemRow.VALUE;
      Draw2D.drawStyledText(text3, Fonts.SUPPLIER16, floatValue6 - floatValue7, floatValue5, ColorUtil.withAlpha(value3, (int)(235.0F * floatValue3)), 11.0F);
   }

   private AutoBuyScreen.StateHolder m_4(AutoBuyUtil.DataRecord c22) {
      return this.stringMap.computeIfAbsent(c22.m_7_l(), text3 -> new AutoBuyScreen.StateHolder(c22));
   }

   private AutoBuyUtil.DataRecord findEntry(String text3) {
      for (AutoBuyUtil.DataRecord c22 : this.c2List) {
         if (c22.m_7_l().equals(text3)) {
            return c22;
         }
      }

      return null;
   }

   private static String getPriceText(AutoBuyUtil.DataRecord c22) {
      int value2 = AutoBuyUtil.getPrice(c22.m_7_l());
      return value2 > 0 ? "до " + formatPrice(value2) : "цена не задана";
   }

   private static String formatPrice(int value2) {
      return "$" + String.format(Locale.ROOT, "%,d", value2).replace(',', '.');
   }

   private List<AutoBuyUtil.DataRecord> getFilteredEntries() {
      String text3 = this.searchBox.getText();
      if (!text3.equals(this.text)) {
         this.text = text3;
         this.floatValue2 = 0.0F;
      }

      String text4 = text3.toLowerCase(Locale.ROOT);
      ArrayList list = new ArrayList();

      for (AutoBuyUtil.DataRecord c22 : this.c2List) {
         if ((this.value != 1 || AutoBuyUtil.isEnabled(c22.m_7_l()))
            && (this.value != 2 || AutoBuyUtil.isParserEnabled(c22.m_7_l()))
            && (text4.isEmpty() || c22.m_7_l().toLowerCase(Locale.ROOT).contains(text4))) {
            list.add(c22);
         }
      }

      return list;
   }

   private AutoBuyUtil.DataRecord m_4(List<AutoBuyUtil.DataRecord> c2List2, float floatValue3, float floatValue4) {
      if (!ItemRow.isInside(floatValue3, floatValue4, 8.0F, 56.0F, 275.0F, 216.0F)) {
         return null;
      } else {
         float floatValue5 = floatValue3 - 8.0F;
         float floatValue6 = floatValue4 - 56.0F + this.floatValue;
         int value2 = (int)(floatValue5 / 35.0F);
         int value3 = (int)(floatValue6 / 35.0F);
         if (value2 < 8 && !(floatValue5 % 35.0F >= 30.0F) && !(floatValue6 % 35.0F >= 30.0F)) {
            int value4 = value3 * 8 + value2;
            return value4 < c2List2.size() ? (AutoBuyUtil.DataRecord)c2List2.get(value4) : null;
         } else {
            return null;
         }
      }
   }

   @Override
   public int m_1_int() {
      C4q c4q = AutoBuyUtil.getAutoBuyModule();
      return c4q != null ? c4q.m_2_int("autobuy.gui") : -1;
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent event, boolean flag2) {
      float floatValue3 = this.m_4_float(event.x());
      float floatValue4 = this.m_7_float(event.y());
      int value2 = event.button();
      if (value2 >= 2 && this.m_7_boolean(-value2 - 1)) {
         return true;
      }

      boolean flag3 = this.c2 != null && ItemRow.isInside(floatValue3, floatValue4, 297.0F, 160.0F, 129.0F, 22.0F);
      if (!flag3) {
         this.applyPrice();
      }

      if (value2 == 0 ? !this.searchBox.m_6_boolean(floatValue3, floatValue4) : !this.m_1_boolean()) {
         int value3 = ItemRow.m_1(TEXT_ARRAY, 285.0F, 38.0F, floatValue3, floatValue4);
         if (value3 >= 0) {
            this.value = value3;
            this.floatValue2 = 0.0F;
            return true;
         }

         AutoBuyUtil.DataRecord c22 = this.m_4(this.getFilteredEntries(), floatValue3, floatValue4);
         if (c22 != null) {
            this.c2 = c22;
            if (value2 == 1) {
               AutoBuyUtil.setParserEnabled(c22.m_7_l(), !AutoBuyUtil.isParserEnabled(c22.m_7_l()));
               AutoBuyUtil.m_5_void();
            } else if (value2 == 0 && flag2) {
               AutoBuyUtil.setEnabled(c22.m_7_l(), !AutoBuyUtil.isEnabled(c22.m_7_l()));
               AutoBuyUtil.m_5_void();
            }

            return true;
         } else {
            if (this.c2 != null) {
               if (ItemRow.isInside(floatValue3, floatValue4, 297.0F, 104.0F, 129.0F, 24.0F)) {
                  AutoBuyUtil.setEnabled(this.c2.m_7_l(), !AutoBuyUtil.isEnabled(this.c2.m_7_l()));
                  AutoBuyUtil.m_5_void();
                  return true;
               }

               if (ItemRow.isInside(floatValue3, floatValue4, 297.0F, 130.0F, 129.0F, 24.0F)) {
                  AutoBuyUtil.setParserEnabled(this.c2.m_7_l(), !AutoBuyUtil.isParserEnabled(this.c2.m_7_l()));
                  AutoBuyUtil.m_5_void();
                  return true;
               }

               if (flag3) {
                  this.flag = true;
                  int value4 = AutoBuyUtil.getPrice(this.c2.m_7_l());
                  this.text2 = value4 > 0 ? Integer.toString(value4) : "";
                  return true;
               }

               if (ItemRow.isInside(floatValue3, floatValue4, 297.0F, 186.0F, 129.0F, 22.0F) && super.minecraft != null && super.minecraft.getConnection() != null) {
                  super.minecraft.getConnection().sendCommand("ah search " + this.c2.m_7p_String());
                  return true;
               }
            }

            if (!ItemRow.isInside(floatValue3, floatValue4, 297.0F, 244.0F, 129.0F, 22.0F)) {
               return super.mouseClicked(event, flag2);
            }

            for (AutoBuyUtil.DataRecord c23 : this.c2List) {
               AutoBuyUtil.setEnabled(c23.m_7_l(), false);
               AutoBuyUtil.setParserEnabled(c23.m_7_l(), false);
            }

            AutoBuyUtil.m_5_void();
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean m_1_boolean() {
      this.searchBox.m_1_void();
      return false;
   }

   private void applyPrice() {
      if (this.flag) {
         this.flag = false;
         if (this.c2 != null) {
            try {
               AutoBuyUtil.setPrice(this.c2.m_7_l(), this.text2.isEmpty() ? 0 : Integer.parseInt(this.text2));
            } catch (NumberFormatException numberFormatException) {
            }

            AutoBuyUtil.m_5_void();
         }
      }
   }

   @Override
   public boolean mouseScrolled(double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4) {
      this.floatValue2 -= (float)(doubleValue4 * 35.0);
      return true;
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      int value2 = event.key();
      if (this.searchBox.isFlag()) {
         this.searchBox.m_1_void(value2);
         return true;
      }

      if (!this.flag) {
         return super.keyPressed(event);
      }

      if (value2 == 257 || value2 == 335) {
         this.applyPrice();
      } else if (value2 == 256) {
         this.flag = false;
      } else if (value2 == 259 && !this.text2.isEmpty()) {
         this.text2 = this.text2.substring(0, this.text2.length() - 1);
      }

      return true;
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      if (this.searchBox.isFlag()) {
         this.searchBox.m_3_void(event.codepoint());
         return true;
      }

      if (this.flag) {
         String text3 = event.codepointAsString();
         if (text3.length() == 1 && Character.isDigit(text3.charAt(0)) && this.text2.length() < 10) {
            this.text2 = this.text2 + text3;
         }

         return true;
      } else {
         return super.charTyped(event);
      }
   }

   private static final class StateHolder {
      private final Animation animation;
      private final Animation animation2;
      private final Animation animation3;
      private final Animation animation4;
      private final ItemRenderer itemRenderer;
      private final ItemStack itemStack;
      private final TextRenderer textRenderer;

      private StateHolder(AutoBuyUtil.DataRecord c22) {
         this.animation = new Animation(150L, 0.0F, Easing.EASING8);
         this.animation4 = new Animation(150L, 0.0F, Easing.EASING8);
         this.itemRenderer = new ItemRenderer();
         this.itemStack = c22.m_84_ItemStack();
         this.textRenderer = TextRenderer.m_2(c22.m_7_l(), Fonts.SUPPLIER16, Fonts.SUPPLIER17);
         this.animation2 = new Animation(200L, AutoBuyUtil.isEnabled(c22.m_7_l()) ? 1.0F : 0.0F, Easing.EASING8);
         this.animation3 = new Animation(200L, AutoBuyUtil.isParserEnabled(c22.m_7_l()) ? 1.0F : 0.0F, Easing.EASING8);
      }
   }
}
