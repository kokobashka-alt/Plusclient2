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
import java.util.ArrayList;
import net.fabricmc.loader.api.FabricLoader;
import wtf.onetap.Onetap;
import wtf.onetap.autobuy.AuctionParser;
import wtf.onetap.autobuy.FunTimeItems;
import wtf.onetap.autobuy.ItemUtil;
import wtf.onetap.autobuy.TextSanitizer;

public class AutoBuyBans {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Path PATH = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("autobuy.json");
   private static boolean flag;

   private AutoBuyBans() {
   }

   public static void m_8_void() {
      if (!flag) {
         flag = true;
         if (Files.exists(PATH)) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
               JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
               if (jsonObject.has("bans") && jsonObject.get("bans").isJsonArray()) {
                  ArrayList list = new ArrayList();

                  for (JsonElement jsonElement : jsonObject.getAsJsonArray("bans")) {
                     if (jsonElement != null && jsonElement.isJsonPrimitive()) {
                        list.add(jsonElement.getAsString());
                     }
                  }

                  TextSanitizer.m_1(list);
               }

               if (jsonObject.has("items") && jsonObject.get("items").isJsonObject()) {
                  JsonObject jsonObject2 = jsonObject.getAsJsonObject("items");

                  for (ItemUtil itemUtil : FunTimeItems.getItems()) {
                     if (jsonObject2.has(itemUtil.getText())) {
                        JsonObject jsonObject3 = jsonObject2.getAsJsonObject(itemUtil.getText());
                        if (jsonObject3.has("enabled")) {
                           itemUtil.setFlag2(jsonObject3.get("enabled").getAsBoolean());
                        }

                        if (jsonObject3.has("parser")) {
                           itemUtil.setFlag3(jsonObject3.get("parser").getAsBoolean());
                        }

                        if (jsonObject3.has("price")) {
                           itemUtil.setCount(jsonObject3.get("price").getAsInt());
                        }

                        if (jsonObject3.has("minQuantity")) {
                           itemUtil.setMinCount(jsonObject3.get("minQuantity").getAsInt());
                        }
                     }
                  }
               }

               if (jsonObject.has("discount")) {
                  AuctionParser.setDiscount(jsonObject.get("discount").getAsInt());
               }
            } catch (Exception e) {
               Onetap.LOGGER.error("Failed to load autobuy.json", e);
            }
         }
      }
   }

   public static void m_0_void() {
      try {
         Files.createDirectories(PATH.getParent());
         JsonObject jsonObject = new JsonObject();
         JsonArray jsonArray = new JsonArray();

         for (String text : TextSanitizer.getBlacklist()) {
            jsonArray.add(text);
         }

         jsonObject.add("bans", jsonArray);
         jsonObject.addProperty("discount", AuctionParser.getDiscount());
         JsonObject jsonObject2 = new JsonObject();

         for (ItemUtil itemUtil : FunTimeItems.getItems()) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("enabled", itemUtil.isFlag2());
            jsonObject3.addProperty("parser", itemUtil.isFlag3());
            jsonObject3.addProperty("price", itemUtil.getValue());
            jsonObject3.addProperty("minQuantity", itemUtil.getValue2());
            jsonObject2.add(itemUtil.getText(), jsonObject3);
         }

         jsonObject.add("items", jsonObject2);

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonObject, bufferedWriter);
         }
      } catch (IOException e) {
         Onetap.LOGGER.error("Failed to save autobuy.json", e);
      }
   }
}
