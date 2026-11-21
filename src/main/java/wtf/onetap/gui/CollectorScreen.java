package wtf.onetap.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.module.pve.C4q;
import wtf.onetap.platform.PlatformConnection;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.ItemRenderer;
import wtf.onetap.render.Render2D;
import wtf.onetap.render.TextRenderer;

public class CollectorScreen extends BaseScreen {
   private static final String[] TEXT_ARRAY = new String[]{"Все", "Активные"};
   private static CollectorScreen collectorScreen;
   private final Animation animation = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation2 = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation3 = new Animation(150L, 0.0F, Easing.EASING8);
   private final Animation animation4 = new Animation(250L, 0.0F, Easing.EASING8);
   private final Map<String, CollectorScreen.StateHolder> stringMap = new HashMap<>();
   private final Map<String, Animation> stringMap2 = new HashMap<>();
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private int value;
   private String text;
   private static String text2;

   private CollectorScreen() {
      super("Collector", 444.0F, 280.0F);
   }

   public static CollectorScreen getInstance() {
      if (collectorScreen == null) {
         collectorScreen = new CollectorScreen();
      }

      collectorScreen.m_0_void();
      return collectorScreen;
   }

   private static List<JsonObject> getEntries() {
      ArrayList list = new ArrayList();
      JsonElement jsonElement = PlatformConnection.PLATFORM_CONNECTION.m_6_JsonElement("collector");
      if (jsonElement != null && jsonElement.isJsonArray()) {
         for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
            list.add(jsonElement2.getAsJsonObject());
         }
      }

      return list;
   }

   private static C4q getCollectorModule() {
      return PlatformConnection.PLATFORM_CONNECTION.getModule("collector");
   }

   private static JsonObject getEntry(String text3, boolean flag) {
      C4q c4q = getCollectorModule();
      if (c4q == null) {
         return null;
      }

      JsonObject jsonObject = c4q.getJsonObject2();
      if (!jsonObject.has("items") || !jsonObject.get("items").isJsonArray()) {
         if (!flag) {
            return null;
         }

         jsonObject.add("items", new JsonArray());
      }

      for (JsonElement jsonElement : jsonObject.getAsJsonArray("items")) {
         JsonObject jsonObject2 = jsonElement.getAsJsonObject();
         if (jsonObject2.has("name") && jsonObject2.get("name").getAsString().equals(text3)) {
            return jsonObject2;
         }
      }

      if (!flag) {
         return null;
      }

      JsonObject jsonObject3 = new JsonObject();
      jsonObject3.addProperty("name", text3);
      jsonObject.getAsJsonArray("items").add(jsonObject3);
      return jsonObject3;
   }

   private static void refresh() {
      C4q c4q = getCollectorModule();
      if (c4q != null) {
         c4q.m_4_void();
      }
   }

   private static String getName(JsonObject jsonObject) {
      return jsonObject.get("name").getAsString();
   }

   private static boolean isActive(JsonObject jsonObject) {
      JsonObject jsonObject2 = getEntry(getName(jsonObject), false);
      return jsonObject2 != null && jsonObject2.has("active") ? jsonObject2.get("active").getAsBoolean() : jsonObject.get("active").getAsBoolean();
   }

   private static int getCount(JsonObject jsonObject) {
      JsonObject jsonObject2 = getEntry(getName(jsonObject), false);
      return jsonObject2 != null && jsonObject2.has("count") ? jsonObject2.get("count").getAsInt() : jsonObject.get("count").getAsInt();
   }

   private static boolean isStack(JsonObject jsonObject) {
      return jsonObject.get("stack").getAsBoolean();
   }

   private static int getMax(JsonObject jsonObject) {
      return jsonObject.get("max").getAsInt();
   }

   private static List<JsonObject> getConditions(JsonObject jsonObject) {
      ArrayList list = new ArrayList();
      if (jsonObject.has("cond")) {
         for (JsonElement jsonElement : jsonObject.getAsJsonArray("cond")) {
            list.add(jsonElement.getAsJsonObject());
         }
      }

      return list;
   }

   private static JsonObject m_6(JsonObject jsonObject, JsonObject jsonObject2, boolean flag) {
      JsonObject jsonObject3 = getEntry(getName(jsonObject), flag);
      if (jsonObject3 == null) {
         return null;
      }

      String text3 = "e".equals(jsonObject2.get("k").getAsString()) ? "enchantments" : "descriptions";
      if (!jsonObject3.has(text3)) {
         if (!flag) {
            return null;
         }

         jsonObject3.add(text3, new JsonObject());
      }

      JsonObject jsonObject4 = jsonObject3.getAsJsonObject(text3);
      String text4 = jsonObject2.get("id").getAsString();
      if (!jsonObject4.has(text4)) {
         if (!flag) {
            return null;
         }

         jsonObject4.add(text4, new JsonObject());
      }

      return jsonObject4.getAsJsonObject(text4);
   }

   private static int m_6(JsonObject jsonObject, JsonObject jsonObject2) {
      JsonObject jsonObject3 = m_6(jsonObject, jsonObject2, false);
      return jsonObject3 != null && jsonObject3.has("level") ? jsonObject3.get("level").getAsInt() : jsonObject2.get("lvl").getAsInt();
   }

   private static boolean m_4(JsonObject jsonObject, JsonObject jsonObject2) {
      JsonObject jsonObject3 = m_6(jsonObject, jsonObject2, false);
      return jsonObject3 != null && jsonObject3.has("enabled") ? jsonObject3.get("enabled").getAsBoolean() : jsonObject2.get("on").getAsBoolean();
   }

   private static String m_1(JsonObject jsonObject, JsonObject jsonObject2) {
      int value2 = m_6(jsonObject, jsonObject2);
      String text3 = jsonObject2.get("id").getAsString();
      if ("e".equals(jsonObject2.get("k").getAsString())) {
         Identifier identifier = Identifier.parse(text3);
         String text4 = Component.translatable("enchantment." + identifier.getNamespace() + "." + identifier.getPath().replace("/", ".")).getString();
         return value2 == 0 ? text4 : text4 + " " + value2;
      } else {
         return value2 == 0 ? text3 : text3 + " " + toRoman(value2 - 1);
      }
   }

   private static String toRoman(int value2) {
      String[] stringArray = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
      return value2 >= 0 && value2 < stringArray.length ? stringArray[value2] : String.valueOf(value2 + 1);
   }

   private static ItemStack m_9_ItemStack(JsonObject jsonObject) {
      Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(jsonObject.get("item").getAsString()));
      ItemStack itemStack = new ItemStack(item, Math.max(1, getCount(jsonObject)));
      if (jsonObject.has("color")) {
         ArrayList list = new ArrayList();
         if (jsonObject.has("pot")) {
            for (JsonElement jsonElement : jsonObject.getAsJsonArray("pot")) {
               JsonObject jsonObject2 = jsonElement.getAsJsonObject();
               BuiltInRegistries.MOB_EFFECT
                  .get(Identifier.parse(jsonObject2.get("e").getAsString()))
                  .ifPresent(reference -> list.add(new MobEffectInstance(reference, jsonObject2.get("d").getAsInt(), Math.max(0, jsonObject2.get("l").getAsInt() - 1))));
            }
         }

         itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(jsonObject.get("color").getAsInt()), list, Optional.empty()));
      }

      return itemStack;
   }

   private static JsonObject findByName(List<JsonObject> jsonObjectList, String text3) {
      if (text3 == null) {
         return null;
      }

      for (JsonObject jsonObject : jsonObjectList) {
         if (getName(jsonObject).equals(text3)) {
            return jsonObject;
         }
      }

      return null;
   }

   @Override
   public void m_0(GuiGraphicsExtractor guiGraphicsExtractor, float floatValue5, float floatValue6, float floatValue7) {
      var jsonObjectList = getEntries();
      JsonObject jsonObject = findByName(jsonObjectList, this.text);
      if (jsonObject == null && !jsonObjectList.isEmpty()) {
         jsonObject = (JsonObject)jsonObjectList.getFirst();
         this.text = getName(jsonObject);
      }

      var jsonObjectList2 = this.filter(jsonObjectList);
      JsonObject jsonObject2 = this.m_6(jsonObjectList2, floatValue5, floatValue6);
      ItemRow.m_4("\uf468", "Collector", "FunTime", 8.0F, 444.0F, 38.0F, floatValue7);
      ItemRow.m_8(TEXT_ARRAY, 250.0F, 38.0F, this.animation.update(this.value), floatValue5, floatValue6, floatValue7);
      this.m_8(floatValue5, floatValue6, floatValue7);
      Draw2D.drawPanel(0.0F, 48.0F, 444.0F, 232.0F, floatValue7);
      this.m_0(guiGraphicsExtractor, jsonObjectList2, jsonObjectList.isEmpty(), jsonObject, jsonObject2, floatValue7);
      this.m_6(guiGraphicsExtractor, jsonObjectList, jsonObject, floatValue5, floatValue6, floatValue7);
      if (jsonObject2 != null) {
         ItemRow.m_2(this.m_7_C6(jsonObject2).textRenderer, getStatusText(jsonObject2), floatValue5, floatValue6, 444.0F, 280.0F, floatValue7);
      }
   }

   private void m_8(float floatValue5, float floatValue6, float floatValue7) {
      float floatValue8 = this.animation2.update(ItemRow.isInside(floatValue5, floatValue6, 256.0F, 6.0F, 180.0F, 26.0F) ? 1.0F : 0.0F);
      int value2 = (int)((175.0F + 60.0F * floatValue8) * floatValue7);
      Draw2D.m_0(256.0F, 6.0F, 180.0F, 26.0F, 8.0F, 0.0F, ColorUtil.withAlpha(ColorUtil.primaryColor(), value2), ColorUtil.withAlpha(ColorUtil.secondaryColor(), value2));
      ItemRow.m_5("\uf04b", "Запустить сбор", 256.0F, 6.0F, 180.0F, 26.0F, 0, 1.0F, floatValue7);
   }

   private void m_0(GuiGraphicsExtractor guiGraphicsExtractor, List<JsonObject> jsonObjectList, boolean flag, JsonObject jsonObject, JsonObject jsonObject2, float floatValue5) {
      int value2 = (jsonObjectList.size() + 7 - 1) / 7;
      float floatValue6 = value2 > 0 ? value2 * 35.0F - 5.0F : 0.0F;
      float floatValue7 = Math.max(0.0F, floatValue6 - 216.0F);
      this.floatValue2 = Mth.clamp(this.floatValue2, 0.0F, floatValue7);
      this.floatValue = ItemRow.smoothStep(this.floatValue, this.floatValue2);
      Render2D.pushScissor(7.0F, 55.0F, 242.0F, 218.0F);

      for (int i = 0; i < jsonObjectList.size(); i++) {
         float floatValue8 = 8.0F + i % 7 * 35.0F;
         float floatValue9 = 56.0F + i / 7 * 35.0F - this.floatValue;
         if (!(floatValue9 + 30.0F < 55.0F) && !(floatValue9 > 273.0F)) {
            JsonObject jsonObject3 = (JsonObject)jsonObjectList.get(i);
            this.m_2(guiGraphicsExtractor, jsonObject3, floatValue8, floatValue9, jsonObject3 == jsonObject2, jsonObject3 == jsonObject, floatValue5);
         }
      }

      Render2D.popScissor();
      if (jsonObjectList.isEmpty()) {
         String text3 = flag ? "Список появится после входа: .ston login" : "Нет активных предметов";
         ItemRow.drawCenteredText(text3, Fonts.SUPPLIER16, 8.0F, 240.0F, 158.0F, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(200.0F * floatValue5)), 12.0F);
      }

      ItemRow.m_5(250.5F, 56.0F, 216.0F, floatValue6, this.floatValue, floatValue7, floatValue5);
   }

   private void m_2(GuiGraphicsExtractor guiGraphicsExtractor, JsonObject jsonObject, float floatValue5, float floatValue6, boolean flag, boolean flag2, float floatValue7) {
      CollectorScreen.StateHolder c6 = this.m_7_C6(jsonObject);
      float floatValue8 = c6.animation.update(flag ? 1.0F : 0.0F);
      ItemRow.m_6(floatValue5, floatValue6, 30.0F, floatValue8, c6.animation2.update(isActive(jsonObject) ? 1.0F : 0.0F), c6.animation3.update(flag2 ? 1.0F : 0.0F), floatValue7);
      float floatValue9 = 16.0F + 2.0F * floatValue8;
      float floatValue10 = floatValue5 + (30.0F - floatValue9) / 2.0F;
      float floatValue11 = floatValue6 + (30.0F - floatValue9) / 2.0F;
      ItemStack itemStack = c6.m_4_ItemStack(jsonObject);
      c6.itemRenderer.m_2(guiGraphicsExtractor, itemStack, floatValue10, floatValue11, floatValue9, floatValue7);
      Render2D.drawItemCount(itemStack, floatValue10, floatValue11, floatValue9, (int)(255.0F * floatValue7));
   }

   private void m_6(GuiGraphicsExtractor guiGraphicsExtractor, List<JsonObject> jsonObjectList, JsonObject jsonObject, float floatValue5, float floatValue6, float floatValue7) {
      Draw2D.roundedRect(256.0F, 56.0F, 180.0F, 216.0F, 6.0F, ColorUtil.rgba(0, 0, 0, (int)(45.0F * floatValue7)));
      int value2 = 0;

      for (JsonObject jsonObject2 : jsonObjectList) {
         if (isActive(jsonObject2)) {
            value2++;
         }
      }

      ItemRow.drawCenteredText("Активно: " + value2 + " из " + jsonObjectList.size(), Fonts.SUPPLIER15, 256.0F, 180.0F, 255.0F, ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue7)), 9.5F);
      if (jsonObject != null) {
         CollectorScreen.StateHolder c6 = this.m_7_C6(jsonObject);
         boolean flag = isActive(jsonObject);
         ItemRow.m_5(guiGraphicsExtractor, c6.itemRenderer, c6.m_4_ItemStack(jsonObject), c6.textRenderer, getStatusText(jsonObject), flag ? ColorUtil.primaryColor() : ItemRow.VALUE2, 262.0F, 62.0F, 168.0F, floatValue7);
         ItemRow.m_8(
            "\uf466",
            "Собирать",
            262.0F,
            104.0F,
            168.0F,
            this.animation3.update(ItemRow.isInside(floatValue5, floatValue6, 262.0F, 104.0F, 168.0F, 24.0F) ? 1.0F : 0.0F),
            this.animation4.update(flag ? 1.0F : 0.0F),
            floatValue7
         );
         if (isStack(jsonObject)) {
            this.m_0(jsonObject, floatValue5, floatValue6, floatValue7);
         }

         this.m_1(jsonObject, floatValue5, floatValue6, floatValue7);
      }
   }

   private void m_0(JsonObject jsonObject, float floatValue5, float floatValue6, float floatValue7) {
      ItemRow.m_8(262.0F, 130.0F, 168.0F, 24.0F, 0.0F, floatValue7);
      Draw2D.drawStyledText("\uf468", Fonts.SUPPLIER8, 269.0F, Draw2D.getCenteredY("\uf468", Fonts.SUPPLIER8, 10.0F, 130.0F, 24.0F), ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue7)), 10.0F);
      Draw2D.drawTextWithShadow("Количество", Fonts.SUPPLIER16, 284.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.5F, 130.0F, 24.0F), ColorUtil.withAlpha(ItemRow.VALUE, (int)(230.0F * floatValue7)), 11.5F);
      int value2 = getCount(jsonObject);
      float floatValue8 = m_7_float(168.0F);
      float floatValue9 = floatValue8 - 14.0F - 22.0F;
      float floatValue10 = 135.0F;
      m_6("\uf068", floatValue9, floatValue10, floatValue5, floatValue6, value2 > 1, floatValue7);
      m_6("\uf067", floatValue8, floatValue10, floatValue5, floatValue6, value2 < getMax(jsonObject), floatValue7);
      ItemRow.drawCenteredText(String.valueOf(value2), Fonts.SUPPLIER16, floatValue9 + 14.0F, 22.0F, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 11.0F, 130.0F, 24.0F), ColorUtil.withAlpha(ItemRow.VALUE, (int)(240.0F * floatValue7)), 11.0F);
   }

   private void m_1(JsonObject jsonObject, float floatValue5, float floatValue6, float floatValue7) {
      var jsonObjectList = getConditions(jsonObject);
      float floatValue8 = m_6_float(jsonObject);
      int value2 = ColorUtil.withAlpha(ItemRow.VALUE2, (int)(220.0F * floatValue7));
      Draw2D.drawStyledText("Условия", Fonts.SUPPLIER15, 264.0F, floatValue8, value2, 10.0F);
      String text3 = String.valueOf(jsonObjectList.size());
      Draw2D.drawStyledText(text3, Fonts.SUPPLIER15, 428.0F - Draw2D.getTextWidth(text3, Fonts.SUPPLIER15, 10.0F), floatValue8, value2, 10.0F);
      float floatValue9 = floatValue8 + 16.0F;
      float floatValue10 = 249.0F - floatValue9;
      float floatValue11 = jsonObjectList.isEmpty() ? 0.0F : jsonObjectList.size() * 24.0F - 2.0F;
      float floatValue12 = Math.max(0.0F, floatValue11 - floatValue10);
      this.floatValue4 = Mth.clamp(this.floatValue4, 0.0F, floatValue12);
      this.floatValue3 = ItemRow.smoothStep(this.floatValue3, this.floatValue4);
      float floatValue13 = m_8_float(floatValue12);
      if (jsonObjectList.isEmpty()) {
         ItemRow.drawCenteredText("Без условий", Fonts.SUPPLIER13, 256.0F, 180.0F, floatValue9 + 4.0F, value2, 10.0F);
      } else {
         boolean flag = ItemRow.isInside(floatValue5, floatValue6, 262.0F, floatValue9, 168.0F, floatValue10);
         Render2D.pushScissor(261.0F, floatValue9 - 1.0F, 170.0F, floatValue10 + 2.0F);

         for (int i = 0; i < jsonObjectList.size(); i++) {
            float floatValue14 = floatValue9 + i * 24.0F - this.floatValue3;
            if (!(floatValue14 + 22.0F < floatValue9 - 1.0F) && !(floatValue14 > floatValue9 + floatValue10 + 1.0F)) {
               this.m_1(jsonObject, (JsonObject)jsonObjectList.get(i), floatValue14, floatValue13, flag, floatValue5, floatValue6, floatValue7);
            }
         }

         Render2D.popScissor();
         ItemRow.m_5(431.5F, floatValue9, floatValue10, floatValue11, this.floatValue3, floatValue12, floatValue7);
      }
   }

   private void m_1(JsonObject jsonObject, JsonObject jsonObject2, float floatValue5, float floatValue6, boolean flag, float floatValue7, float floatValue8, float floatValue9) {
      boolean flag2 = flag && ItemRow.isInside(floatValue7, floatValue8, 262.0F, floatValue5, floatValue6, 22.0F);
      boolean flag3 = m_4(jsonObject, jsonObject2);
      ItemRow.m_8(262.0F, floatValue5, floatValue6, 22.0F, flag2 ? 1.0F : 0.0F, floatValue9);
      String text3 = getName(jsonObject) + "\u0000" + jsonObject2.get("k").getAsString() + ":" + jsonObject2.get("id").getAsString();
      float floatValue10 = this.stringMap2.computeIfAbsent(text3, text4 -> new Animation(250L, flag3 ? 1.0F : 0.0F, Easing.EASING8)).update(flag3 ? 1.0F : 0.0F);
      ItemRow.m_8_void(266.0F, floatValue5 + 4.0F, floatValue10, floatValue9);
      float floatValue11 = 286.0F;
      float floatValue12 = 262.0F + floatValue6 - 4.0F;
      if (m_6(jsonObject, jsonObject2) > 0) {
         float floatValue13 = m_7_float(floatValue6);
         float floatValue14 = floatValue13 - 14.0F - 2.0F;
         float floatValue15 = floatValue5 + 4.0F;
         m_6("\uf068", floatValue14, floatValue15, floatValue7, floatValue8, flag3, floatValue9);
         m_6("\uf067", floatValue13, floatValue15, floatValue7, floatValue8, flag3, floatValue9);
         floatValue12 = floatValue14 - 4.0F;
      }

      int value2 = ColorUtil.withAlpha(flag3 ? ItemRow.VALUE : ItemRow.VALUE2, (int)(230.0F * floatValue9));
      Draw2D.drawColoredTextRaw(m_1(jsonObject, jsonObject2), Fonts.SUPPLIER16, floatValue11, Draw2D.getTextBaseline(Fonts.SUPPLIER16, 10.5F, floatValue5, 22.0F), value2, 0.75F, 1.0F, floatValue12 - floatValue11, 10.5F);
   }

   private static void m_6(String text3, float floatValue5, float floatValue6, float floatValue7, float floatValue8, boolean flag, float floatValue9) {
      boolean flag2 = flag && ItemRow.isInside(floatValue7, floatValue8, floatValue5, floatValue6, 14.0F, 14.0F);
      Draw2D.roundedRect(floatValue5, floatValue6, 14.0F, 14.0F, 4.0F, ColorUtil.rgba(255, 255, 255, (int)((flag2 ? 34 : 14) * floatValue9)));
      Draw2D.drawStyledText(
         text3,
         Fonts.SUPPLIER8,
         Draw2D.getCenteredX(text3, Fonts.SUPPLIER8, 8.0F, floatValue5, 14.0F),
         Draw2D.getCenteredY(text3, Fonts.SUPPLIER8, 8.0F, floatValue6, 14.0F),
         ColorUtil.withAlpha(flag ? ItemRow.VALUE : ItemRow.VALUE2, (int)((flag ? 230 : 120) * floatValue9)),
         8.0F
      );
   }

   private static float m_7_float(float floatValue5) {
      return 262.0F + floatValue5 - 4.0F - 14.0F;
   }

   private static float m_8_float(float floatValue5) {
      return floatValue5 > 0.0F ? 163.0F : 168.0F;
   }

   private static float m_6_float(JsonObject jsonObject) {
      return isStack(jsonObject) ? 162.0F : 134.0F;
   }

   private static String getStatusText(JsonObject jsonObject) {
      String text3 = isActive(jsonObject) ? "Собирается" : "Выключен";
      int value2 = getCount(jsonObject);
      return value2 > 1 ? text3 + "  ·  ×" + value2 : text3;
   }

   private CollectorScreen.StateHolder m_7_C6(JsonObject jsonObject) {
      return this.stringMap.computeIfAbsent(getName(jsonObject), text3 -> new CollectorScreen.StateHolder(jsonObject));
   }

   private List<JsonObject> filter(List<JsonObject> jsonObjectList) {
      if (this.value == 0) {
         return jsonObjectList;
      }

      ArrayList list = new ArrayList();

      for (JsonObject jsonObject : jsonObjectList) {
         if (isActive(jsonObject)) {
            list.add(jsonObject);
         }
      }

      return list;
   }

   private JsonObject m_6(List<JsonObject> jsonObjectList, float floatValue5, float floatValue6) {
      if (!ItemRow.isInside(floatValue5, floatValue6, 8.0F, 56.0F, 240.0F, 216.0F)) {
         return null;
      } else {
         float floatValue7 = floatValue5 - 8.0F;
         float floatValue8 = floatValue6 - 56.0F + this.floatValue;
         int value2 = (int)(floatValue7 / 35.0F);
         int value3 = (int)(floatValue8 / 35.0F);
         if (value2 < 7 && !(floatValue7 % 35.0F >= 30.0F) && !(floatValue8 % 35.0F >= 30.0F)) {
            int value4 = value3 * 7 + value2;
            return value4 < jsonObjectList.size() ? (JsonObject)jsonObjectList.get(value4) : null;
         } else {
            return null;
         }
      }
   }

   @Override
   public int m_1_int() {
      C4q c4q = getCollectorModule();
      return c4q != null ? c4q.m_2_int("collector.gui") : -1;
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent event, boolean flag) {
      float floatValue5 = this.m_4_float(event.x());
      float floatValue6 = this.m_7_float(event.y());
      int value2 = event.button();
      if (value2 >= 2 && this.m_7_boolean(-value2 - 1)) {
         return true;
      }

      var jsonObjectList = getEntries();
      if (jsonObjectList.isEmpty()) {
         return super.mouseClicked(event, flag);
      }

      if (ItemRow.isInside(floatValue5, floatValue6, 256.0F, 6.0F, 180.0F, 26.0F)) {
         C4q c4q = getCollectorModule();
         if (c4q != null) {
            if (!c4q.isEnabled()) {
               c4q.toggle();
            }

            PlatformConnection.PLATFORM_CONNECTION.sendUi("collector", "start", null);
         }

         return true;
      } else {
         int value3 = ItemRow.m_1(TEXT_ARRAY, 250.0F, 38.0F, floatValue5, floatValue6);
         if (value3 >= 0) {
            this.value = value3;
            this.floatValue2 = 0.0F;
            return true;
         }

         JsonObject jsonObject = this.m_6(this.filter(jsonObjectList), floatValue5, floatValue6);
         if (jsonObject != null) {
            if (!getName(jsonObject).equals(this.text)) {
               this.floatValue3 = this.floatValue4 = 0.0F;
            }

            this.text = getName(jsonObject);
            if (value2 == 1 || value2 == 0 && flag) {
               toggleActive(jsonObject);
            }

            return true;
         } else {
            JsonObject jsonObject2 = findByName(jsonObjectList, this.text);
            return jsonObject2 != null && this.m_0(jsonObject2, floatValue5, floatValue6) ? true : super.mouseClicked(event, flag);
         }
      }
   }

   private boolean m_0(JsonObject jsonObject, float floatValue5, float floatValue6) {
      if (ItemRow.isInside(floatValue5, floatValue6, 262.0F, 104.0F, 168.0F, 24.0F)) {
         toggleActive(jsonObject);
         return true;
      }

      String text3 = getName(jsonObject);
      if (isStack(jsonObject)) {
         float floatValue7 = m_7_float(168.0F);
         float floatValue8 = 135.0F;
         if (ItemRow.isInside(floatValue5, floatValue6, floatValue7 - 14.0F - 22.0F, floatValue8, 14.0F, 14.0F)) {
            getEntry(text3, true).addProperty("count", Math.max(1, getCount(jsonObject) - 1));
            refresh();
            return true;
         }

         if (ItemRow.isInside(floatValue5, floatValue6, floatValue7, floatValue8, 14.0F, 14.0F)) {
            getEntry(text3, true).addProperty("count", Math.min(getMax(jsonObject), getCount(jsonObject) + 1));
            refresh();
            return true;
         }
      }

      var jsonObjectList = getConditions(jsonObject);
      float floatValue9 = m_6_float(jsonObject) + 16.0F;
      if (!ItemRow.isInside(floatValue5, floatValue6, 262.0F, floatValue9, 168.0F, 249.0F - floatValue9)) {
         return false;
      }

      float floatValue10 = jsonObjectList.size() * 24.0F - 2.0F;
      float floatValue11 = m_8_float(Math.max(0.0F, floatValue10 - (249.0F - floatValue9)));

      for (int i = 0; i < jsonObjectList.size(); i++) {
         float floatValue12 = floatValue9 + i * 24.0F - this.floatValue3;
         if (ItemRow.isInside(floatValue5, floatValue6, 262.0F, floatValue12, floatValue11, 22.0F)) {
            JsonObject jsonObject2 = (JsonObject)jsonObjectList.get(i);
            int value2 = m_6(jsonObject, jsonObject2);
            boolean flag = m_4(jsonObject, jsonObject2);
            int value3 = jsonObject2.get("max").getAsInt();
            float floatValue13 = m_7_float(floatValue11);
            float floatValue14 = floatValue12 + 4.0F;
            if (value2 > 0 && ItemRow.isInside(floatValue5, floatValue6, floatValue13 - 14.0F - 2.0F, floatValue14, 14.0F, 14.0F)) {
               if (flag) {
                  m_6(jsonObject, jsonObject2, true).addProperty("level", Math.max(1, Math.min(value3, value2 - 1)));
               }
            } else if (value2 <= 0 || !ItemRow.isInside(floatValue5, floatValue6, floatValue13, floatValue14, 14.0F, 14.0F)) {
               JsonObject jsonObject3 = m_6(jsonObject, jsonObject2, true);
               jsonObject3.addProperty("enabled", !flag);
               jsonObject3.addProperty("level", value2);
            } else if (flag) {
               m_6(jsonObject, jsonObject2, true).addProperty("level", Math.max(1, Math.min(value3, value2 + 1)));
            }

            refresh();
            return true;
         }
      }

      return false;
   }

   private static void toggleActive(JsonObject jsonObject) {
      getEntry(getName(jsonObject), true).addProperty("active", !isActive(jsonObject));
      refresh();
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

   static {
      m_0_void(null);
   }

   public static void m_0_void(String text3) {
      text2 = text3;
   }

   public static String m_3_String() {
      return text2;
   }

   private static final class StateHolder {
      private final Animation animation;
      private final Animation animation2;
      private final Animation animation3;
      private final ItemRenderer itemRenderer;
      private final TextRenderer textRenderer;
      private ItemStack itemStack;

      private StateHolder(JsonObject jsonObject) {
         this.animation = new Animation(150L, 0.0F, Easing.EASING8);
         this.animation3 = new Animation(150L, 0.0F, Easing.EASING8);
         this.itemRenderer = new ItemRenderer();
         this.itemStack = ItemStack.EMPTY;
         this.textRenderer = TextRenderer.m_2(CollectorScreen.getName(jsonObject), Fonts.SUPPLIER16, Fonts.SUPPLIER17);
         this.animation2 = new Animation(200L, CollectorScreen.isActive(jsonObject) ? 1.0F : 0.0F, Easing.EASING8);
      }

      private ItemStack m_4_ItemStack(JsonObject jsonObject) {
         if (this.itemStack.getCount() != Math.max(1, CollectorScreen.getCount(jsonObject))) {
            this.itemStack = CollectorScreen.m_9_ItemStack(jsonObject);
         }

         return this.itemStack;
      }
   }
}
