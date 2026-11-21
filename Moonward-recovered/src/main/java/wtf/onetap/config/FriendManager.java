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
import wtf.onetap.module.Module;

public class FriendManager {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final Set<String> stringSet;
   private final Path path;
   private static Module[] moduleArray;

   public FriendManager() {
      this.stringSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
      this.path = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("friends.json");
   }

   public void add(String text) {
      this.stringSet.add(text);
      this.m_6_void();
   }

   public boolean remove(String text) {
      boolean flag = this.stringSet.remove(text);
      if (flag) {
         this.m_6_void();
      }

      return flag;
   }

   public void clear() {
      this.stringSet.clear();
      this.m_6_void();
   }

   public boolean isFriend(String text) {
      return this.stringSet.contains(text);
   }

   public boolean isEmpty() {
      return this.stringSet.isEmpty();
   }

   public Set<String> getFriends() {
      return Collections.unmodifiableSet(this.stringSet);
   }

   @EraseAfterRun
   public void m_9_void() {
      if (Files.exists(this.path)) {
         try (BufferedReader bufferedReader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            JsonArray jsonArray = JsonParser.parseReader(bufferedReader).getAsJsonArray();
            this.stringSet.clear();

            for (JsonElement jsonElement : jsonArray) {
               this.stringSet.add(jsonElement.getAsString());
            }
         } catch (IOException e) {
            Onetap.LOGGER.error("Failed to load friends", e);
         }
      }
   }

   private void m_6_void() {
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
         Onetap.LOGGER.error("Failed to save friends", e);
      }
   }

   static {
      m_7(null);
   }

   public static void m_7(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_3_C3_sArr() {
      return moduleArray;
   }
}
