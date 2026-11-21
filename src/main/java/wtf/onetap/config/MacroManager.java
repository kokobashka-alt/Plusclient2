package wtf.onetap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EraseAfterRun;

public class MacroManager {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final Map<Integer, String> integerMap;
   private final Path path;
   private static int value;

   public MacroManager() {
      this.integerMap = new LinkedHashMap<>();
      this.path = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("macros.json");
   }

   public void add(int value2, String text) {
      this.integerMap.put(value2, text);
      this.m_8_void();
   }

   public boolean remove(int value2) {
      boolean flag = this.integerMap.remove(value2) != null;
      if (flag) {
         this.m_8_void();
      }

      return flag;
   }

   public void clear() {
      this.integerMap.clear();
      this.m_8_void();
   }

   public boolean isEmpty() {
      return this.integerMap.isEmpty();
   }

   public Map<Integer, String> getMacros() {
      return Collections.unmodifiableMap(this.integerMap);
   }

   @EraseAfterRun
   public void m_5_void() {
      if (Files.exists(this.path)) {
         try (BufferedReader bufferedReader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            this.integerMap.clear();

            for (Entry entry : jsonObject.entrySet()) {
               this.integerMap.put(Integer.parseInt((String)entry.getKey()), ((JsonElement)entry.getValue()).getAsString());
            }
         } catch (IOException e) {
            Onetap.LOGGER.error("Failed to load macros", e);
         }
      }
   }

   private void m_8_void() {
      try {
         Files.createDirectories(this.path.getParent());
         JsonObject jsonObject = new JsonObject();

         for (Entry entry : this.integerMap.entrySet()) {
            jsonObject.addProperty(String.valueOf(entry.getKey()), (String)entry.getValue());
         }

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonObject, bufferedWriter);
         }
      } catch (IOException e) {
         Onetap.LOGGER.error("Failed to save macros", e);
      }
   }

   static {
      m_4_void(11);
   }

   public static void m_4_void(int value2) {
      value = value2;
   }

   public static int m_5_int() {
      return value;
   }

   public static int m_6_int() {
      return 0;
   }
}
