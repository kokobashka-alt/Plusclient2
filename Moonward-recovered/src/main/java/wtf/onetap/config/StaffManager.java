package wtf.onetap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import net.fabricmc.loader.api.FabricLoader;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EraseAfterRun;

public class StaffManager {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final Set<String> stringSet;
   private final Path path;
   private static int[] valueArray;

   public StaffManager() {
      this.stringSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
      this.path = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("staff.json");
   }

   public void add(String text) {
      this.stringSet.add(text);
      this.m_2_void();
   }

   public boolean remove(String text) {
      boolean flag = this.stringSet.remove(text);
      if (flag) {
         this.m_2_void();
      }

      return flag;
   }

   public void clear() {
      this.stringSet.clear();
      this.m_2_void();
   }

   public boolean isStaff(String text) {
      return this.stringSet.contains(text);
   }

   public boolean isEmpty() {
      return this.stringSet.isEmpty();
   }

   public Set<String> getStaff() {
      return Collections.unmodifiableSet(this.stringSet);
   }

   @EraseAfterRun
   public void m_6_void() {
      if (Files.exists(this.path)) {
         try (BufferedReader bufferedReader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            JsonArray jsonArray = JsonParser.parseReader(bufferedReader).getAsJsonArray();
            this.stringSet.clear();

            for (JsonElement jsonElement : jsonArray) {
               this.stringSet.add(jsonElement.getAsString());
            }
         } catch (IOException e) {
            Onetap.LOGGER.error("Failed to load staff", e);
         }
      }
   }

   private void m_2_void() {
      try {
         Files.createDirectories(this.path.getParent());
         JsonArray jsonArray = new JsonArray();

         for (String text : this.stringSet) {
            jsonArray.add(text);
         }

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonArray, bufferedWriter);
         }
      } catch (IOException e) {
         Onetap.LOGGER.error("Failed to save staff", e);
      }
   }

   static {
      m_7(null);
   }

   public static void m_7(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_7_IArr() {
      return valueArray;
   }
}
