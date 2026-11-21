package wtf.onetap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import net.fabricmc.loader.api.FabricLoader;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EraseAfterRun;

public class ProxyConfig {
   private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("ProxyServerConfig.json");
   public static HashMap<String, ProxyEntry> stringMap = new HashMap<>();

   private ProxyConfig() {
   }

   @EraseAfterRun
   public static void m_9_void() {
      try {
         if (!Files.exists(PATH)) {
            Files.createDirectories(PATH.getParent());
            Files.createFile(PATH);
            return;
         }

         String text = Files.readString(PATH, StandardCharsets.UTF_8);
         if (text.isEmpty()) {
            return;
         }

         JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();
         if (jsonObject.has("proxy-enabled")) {
            ProxyState.flag = jsonObject.get("proxy-enabled").getAsBoolean();
         }

         Type type = (new TypeToken<HashMap<String, ProxyEntry>>() {}).getType();
         stringMap = (HashMap<String, ProxyEntry>)new Gson().fromJson(jsonObject.get("accounts"), type);
         if (stringMap == null) {
            stringMap = new HashMap<>();
         }

         if (jsonObject.has("current-proxy")) {
            ProxyState.proxyEntry = (ProxyEntry)new Gson().fromJson(jsonObject.get("current-proxy"), ProxyEntry.class);
            if (ProxyState.proxyEntry == null) {
               ProxyState.proxyEntry = new ProxyEntry();
            }
         }

         if (jsonObject.has("last-used-proxy")) {
            ProxyState.proxyEntry2 = (ProxyEntry)new Gson().fromJson(jsonObject.get("last-used-proxy"), ProxyEntry.class);
            if (ProxyState.proxyEntry2 == null) {
               ProxyState.proxyEntry2 = new ProxyEntry();
            }
         }
      } catch (Exception e) {
         Onetap.LOGGER.error("[Onetap] Error reading ProxyServerConfig.json", e);
      }
   }

   public static void m_6(ProxyEntry proxyEntry) {
      stringMap.put("", proxyEntry);
   }

   public static void m_0_void() {
      try {
         Gson gson = new Gson();
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("proxy-enabled", ProxyState.flag);
         jsonObject.add("accounts", gson.toJsonTree(stringMap));
         jsonObject.add("current-proxy", gson.toJsonTree(ProxyState.proxyEntry));
         jsonObject.add("last-used-proxy", gson.toJsonTree(ProxyState.proxyEntry2));
         Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
         Files.writeString(PATH, gson2.toJson(jsonObject), StandardCharsets.UTF_8);
      } catch (IOException e) {
         Onetap.LOGGER.error("[Onetap] Error writing ProxyServerConfig.json", e);
      }
   }
}
