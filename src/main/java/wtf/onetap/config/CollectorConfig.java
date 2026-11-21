package wtf.onetap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import wtf.onetap.Onetap;
import wtf.onetap.autobuy.FunTimeItemPresets;
import wtf.onetap.autobuy.ItemKey;
import wtf.onetap.autobuy.ItemMatcher;

public class CollectorConfig {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Path PATH = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("collector.json");
   private static boolean flag;

   private CollectorConfig() {
   }

   public static void m_8_void() {
      if (!flag) {
         flag = true;
         if (Files.exists(PATH)) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
               for (JsonElement jsonElement : JsonParser.parseReader(bufferedReader).getAsJsonArray()) {
                  if (jsonElement.isJsonObject()) {
                     JsonObject jsonObject = jsonElement.getAsJsonObject();
                     if (jsonObject.has("name")) {
                        ItemKey itemKey = FunTimeItemPresets.findByName(jsonObject.get("name").getAsString());
                        if (itemKey != null) {
                           if (jsonObject.has("active")) {
                              itemKey.setFlag(jsonObject.get("active").getAsBoolean());
                           }

                           if (jsonObject.has("count")) {
                              itemKey.setValue(jsonObject.get("count").getAsInt());
                           }

                           m_6(jsonObject, "enchantments", itemKey.getItemMatcherList());
                           m_6(jsonObject, "descriptions", itemKey.getItemMatcherList2());
                        }
                     }
                  }
               }
            } catch (Exception e) {
               Onetap.LOGGER.error("Failed to load collector.json", e);
            }
         }
      }
   }

   public static void m_5_void() {
      try {
         Files.createDirectories(PATH.getParent());
         JsonArray jsonArray = new JsonArray();

         for (ItemKey itemKey : FunTimeItemPresets.getKeys()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", itemKey.getText());
            jsonObject.addProperty("active", itemKey.isFlag());
            jsonObject.addProperty("count", itemKey.getValue());
            m_5(jsonObject, "enchantments", itemKey.getItemMatcherList());
            m_5(jsonObject, "descriptions", itemKey.getItemMatcherList2());
            jsonArray.add(jsonObject);
         }

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonArray, bufferedWriter);
         }
      } catch (IOException e) {
         Onetap.LOGGER.error("Failed to save collector.json", e);
      }
   }

   private static void m_6(JsonObject jsonObject, String text, List<ItemMatcher> itemMatcherList) {
      if (jsonObject.has(text) && jsonObject.get(text).isJsonObject()) {
         JsonObject jsonObject2 = jsonObject.getAsJsonObject(text);

         for (ItemMatcher itemMatcher : itemMatcherList) {
            if (!itemMatcher.isDeny() && jsonObject2.has(itemMatcher.getText())) {
               JsonObject jsonObject3 = jsonObject2.getAsJsonObject(itemMatcher.getText());
               if (jsonObject3.has("level")) {
                  itemMatcher.setValue(jsonObject3.get("level").getAsInt());
               }

               if (jsonObject3.has("enabled")) {
                  itemMatcher.m_2_void(jsonObject3.get("enabled").getAsBoolean());
               }
            }
         }
      }
   }

   private static void m_5(JsonObject jsonObject, String text, List<ItemMatcher> itemMatcherList) {
      JsonObject jsonObject2 = new JsonObject();

      for (ItemMatcher itemMatcher : itemMatcherList) {
         if (!itemMatcher.isDeny()) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("level", itemMatcher.getValue());
            jsonObject3.addProperty("enabled", itemMatcher.isActive());
            jsonObject2.add(itemMatcher.getText(), jsonObject3);
         }
      }

      if (jsonObject2.size() > 0) {
         jsonObject.add(text, jsonObject2);
      }
   }
}
