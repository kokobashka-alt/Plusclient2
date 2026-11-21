package wtf.onetap.autobuy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import wtf.onetap.module.pve.C4q;
import wtf.onetap.platform.PlatformConnection;

public class AutoBuyUtil {
   public static final String TEXT = "autobuy";
   private static int value;

   private AutoBuyUtil() {
   }

   public static C4q getAutoBuyModule() {
      return PlatformConnection.PLATFORM_CONNECTION.getModule("autobuy");
   }

   public static boolean isAutoBuyEnabled() {
      C4q c4q = getAutoBuyModule();
      return c4q != null && c4q.isEnabled();
   }

   public static boolean isBuying() {
      return getFlag("buying");
   }

   public static boolean isParsing() {
      return getFlag("parsing");
   }

   private static boolean getFlag(String text) {
      JsonObject jsonObject = PlatformConnection.PLATFORM_CONNECTION.getJson("autobuy");
      return jsonObject.has(text) && jsonObject.get(text).getAsBoolean();
   }

   public static void toggleBuying() {
      PlatformConnection.PLATFORM_CONNECTION.sendUi("autobuy", "toggleBuying", null);
   }

   public static void toggleParser() {
      PlatformConnection.PLATFORM_CONNECTION.sendUi("autobuy", "toggleParser", null);
   }

   public static boolean m_0_boolean(ContainerScreen containerScreen) {
      if (containerScreen == null) {
         return false;
      }

      String text = containerScreen.getTitle().getString().toLowerCase(Locale.ROOT).replace('ё', 'е');

      for (String text2 : PlatformConnection.PLATFORM_CONNECTION.m_2_List("ahTitles")) {
         if (text.contains(text2)) {
            return true;
         }
      }

      return isAutoBuyEnabled() && (isBuying() || isParsing());
   }

   public static boolean m_1(ContainerScreen containerScreen) {
      return isAutoBuyEnabled() && m_0_boolean(containerScreen);
   }

   public static List<AutoBuyUtil.DataRecord> m_2_List() {
      ArrayList list = new ArrayList();

      for (JsonElement jsonElement : PlatformConnection.PLATFORM_CONNECTION.getCatalog()) {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         var optional = BuiltInRegistries.ITEM.getOptional(Identifier.parse(jsonObject.get("item").getAsString()));
         ItemStack itemStack = optional.map(ItemStack::new).orElse(ItemStack.EMPTY);
         if (jsonObject.has("c") && !itemStack.isEmpty()) {
            itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(jsonObject.get("c").getAsInt()), List.of(), Optional.empty()));
         }

         String text = jsonObject.get("name").getAsString();
         list.add(new AutoBuyUtil.DataRecord(text, itemStack, jsonObject.has("q") ? jsonObject.get("q").getAsString() : text));
      }

      return list;
   }

   private static JsonObject getSettings() {
      C4q c4q = getAutoBuyModule();
      return c4q == null ? new JsonObject() : c4q.getJsonObject2();
   }

   private static JsonObject m_6_JsonObject(String text) {
      JsonObject jsonObject = getSettings();
      if (!jsonObject.has("items") || !jsonObject.get("items").isJsonObject()) {
         jsonObject.add("items", new JsonObject());
      }

      JsonObject jsonObject2 = jsonObject.getAsJsonObject("items");
      if (!jsonObject2.has(text) || !jsonObject2.get(text).isJsonObject()) {
         jsonObject2.add(text, new JsonObject());
      }

      return jsonObject2.getAsJsonObject(text);
   }

   private static boolean getFlag(String text, String text2) {
      JsonObject jsonObject = m_6_JsonObject(text);
      return jsonObject.has(text2) && jsonObject.get(text2).getAsBoolean();
   }

   public static boolean isEnabled(String text) {
      return getFlag(text, "enabled");
   }

   public static boolean isParserEnabled(String text) {
      return getFlag(text, "parser");
   }

   public static int getPrice(String text) {
      JsonObject jsonObject = m_6_JsonObject(text);
      return jsonObject.has("price") ? jsonObject.get("price").getAsInt() : 0;
   }

   public static void setEnabled(String text, boolean flag) {
      m_6_JsonObject(text).addProperty("enabled", flag);
   }

   public static void setParserEnabled(String text, boolean flag) {
      m_6_JsonObject(text).addProperty("parser", flag);
   }

   public static void setPrice(String text, int value2) {
      m_6_JsonObject(text).addProperty("price", Math.max(0, value2));
   }

   public static int getDiscount() {
      JsonObject jsonObject = getSettings();
      return jsonObject.has("discount") ? jsonObject.get("discount").getAsInt() : 0;
   }

   public static void setDiscount(int value2) {
      getSettings().addProperty("discount", Math.max(0, Math.min(100, value2)));
      m_5_void();
   }

   public static void m_5_void() {
      C4q c4q = getAutoBuyModule();
      if (c4q != null) {
         c4q.m_4_void();
      }
   }

   public static void m_5_void(int value2) {
      value = value2;
   }

   public static int m_2_int() {
      return value;
   }

   public static int m_9_int() {
      return 0;
   }

   static {
      m_5_void(41);
   }

   public record DataRecord(String f_7_l, ItemStack f_84, String f_7p) {
      public boolean m_3_boolean() {
         return this.f_84.getMaxStackSize() > 1;
      }

      public String m_7_l() {
         return this.f_7_l;
      }

      public ItemStack m_84_ItemStack() {
         return this.f_84;
      }

      public String m_7p_String() {
         return this.f_7p;
      }
   }
}
