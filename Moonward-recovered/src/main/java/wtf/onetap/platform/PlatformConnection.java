package wtf.onetap.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import ston.base.client.ClientLog;
import ston.base.client.link.StonLink.Listener;
import ston.base.client.link.StonLink.State;
import ston.base.client.link.StonLink;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EraseAfterRun;
import wtf.onetap.command.Command;
import wtf.onetap.command.CommandHelp;
import wtf.onetap.gui.AutoBuyScreen;
import wtf.onetap.gui.CollectorScreen;
import wtf.onetap.module.pve.C4q;
import wtf.onetap.pve.CombatPlanner;
import wtf.onetap.pve.PveController;
import wtf.onetap.pve.PveSnapshot;
import wtf.onetap.render.AvatarTexture;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.LatencyStats;

public class PlatformConnection {
   public static final PlatformConnection PLATFORM_CONNECTION = new PlatformConnection();
   private final Path path;
   private final Map<String, C4q> stringMap;
   private final PveController pveController;
   private JsonObject jsonObject;
   private StonLink stonLink;
   private volatile boolean flag;
   private String text;
   private volatile boolean flag2;
   private static final Map<String, PlatformConnection.DataRecord> STRING_MAP = new ConcurrentHashMap<>();
   private static int[] valueArray;

   private PlatformConnection() {
      this.path = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("remote");
      this.stringMap = new LinkedHashMap<>();
      this.pveController = new PveController(this);
      this.jsonObject = new JsonObject();
      this.text = "";
   }

   public List<C4q> m_6_List() {
      Path path2 = this.path.resolve("manifest.json");
      if (!Files.exists(path2)) {
         return List.of();
      }

      try {
         this.jsonObject = JsonParser.parseString(Files.readString(path2, StandardCharsets.UTF_8)).getAsJsonObject();
      } catch (Exception e) {
         Diagnostics.warn("Ignoring a broken PvE manifest cache", e);
         return List.of();
      }

      ArrayList list = new ArrayList();

      for (JsonElement jsonElement : this.getModulesJson()) {
         C4q c4q = new C4q(jsonElement.getAsJsonObject(), this.getPath(jsonElement.getAsJsonObject().get("id").getAsString()));
         this.stringMap.put(c4q.getText(), c4q);
         if (!c4q.isFlag()) {
            list.add(c4q);
         }
      }

      return list;
   }

   @EraseAfterRun
   public void m_2_void() {
      LatencyStats.StateHolder.m_6_C3_sArr();
      Onetap.EVENT_BUS.register(this.pveController);
      CombatPlanner.COMBAT_PLANNER.m_8_void();
      this.m_7_void();
      Properties properties = new Properties();

      try (InputStream inputStream = PlatformConnection.class.getResourceAsStream("/onetap-ston.properties")) {
         if (inputStream != null) {
            properties.load(inputStream);
         }
      } catch (Exception e) {
      }

      String text2 = m_1("onetap.ston.web", "ONETAP_STON_WEB", properties.getProperty("web", "https://cloud.moonward.lol"));
      String text3 = properties.getProperty("product", "onetap-26e.2");
      String text4 = properties.getProperty("version", "dev");
      String text5 = m_1("onetap.ston.server", "ONETAP_STON_SERVER", properties.getProperty("server", ""));
      this.text = m_1("onetap.ston.token", "ONETAP_STON_TOKEN", "");
      String text6 = m_1("onetap.ston.hwid", "ONETAP_STON_HWID", "");
      this.stonLink = StonLink.create(text2, text3, text4, text5, text6, this.path.resolve("session"), new Listener() {
         @Override
         public void onState(State state, @Nullable String text7) {
            String text8 = text7 != null ? "ston link: " + state + " (" + text7 + ")" : "ston link: " + state;
            if (state == State.OFFLINE) {
               Onetap.LOGGER.warn(text8);
            } else if (ClientLog.enabled()) {
               Onetap.LOGGER.info(text8);
            }

            Minecraft.getInstance().execute(() -> PlatformConnection.this.m_1(state));
         }
      });
      this.stonLink.setTimings(PlatformConnection::m_3);
      Diagnostics.m_7(this.stonLink);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stonLink.close()));
   }

   @EraseAfterRun
   public void m_1_void() {
      if (!this.flag2 && this.stonLink != null) {
         this.flag2 = true;
         this.stonLink.start(this.text.isBlank() ? null : this.text);
      }
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean isFlag() {
      return this.flag;
   }

   private static String m_1(String text2, String text3, String text4) {
      String text5 = System.getProperty(text2);
      if (text5 == null || text5.isBlank()) {
         text5 = System.getenv(text3);
      }

      return text5 != null && !text5.isBlank() ? text5 : text4;
   }

   private static void m_3(String text2, long longValue, long longValue2, long longValue3) {
      String text3 = text2.substring(0, Math.max(text2.indexOf(46), 0));
      if (!text3.equals("diag")) {
         PlatformConnection.DataRecord c9 = STRING_MAP.computeIfAbsent(text3, PlatformConnection.DataRecord::new);
         c9.f_80.m_4_void(longValue);
         c9.f_8w.m_4_void(longValue2);
         c9.f_8_e.m_4_void(longValue3);
      }
   }

   public StonLink getStonLink() {
      return this.stonLink;
   }

   public boolean isOnline() {
      return this.stonLink != null && this.stonLink.isOnline();
   }

   @Nullable
   public C4q getModule(String text2) {
      return this.stringMap.get(text2);
   }

   public Collection<C4q> m_6_Collection() {
      return this.stringMap.values();
   }

   public List<C4q> getEnabledModules() {
      ArrayList list = new ArrayList();

      for (C4q c4q : this.stringMap.values()) {
         if (c4q.m_8_boolean()) {
            list.add(c4q);
         }
      }

      return list;
   }

   public void sendEvent(String text2, @Nullable String text3, @Nullable String text4, @Nullable JsonObject jsonObject2) {
      this.pveController.enqueue(text2, text3, text4, jsonObject2);
   }

   @Nullable
   public JsonElement m_6_JsonElement(String text2) {
      return this.jsonObject.has("extra") && this.jsonObject.get("extra").isJsonObject() ? this.jsonObject.getAsJsonObject("extra").get(text2) : null;
   }

   public JsonObject getJson(String text2) {
      return this.pveController.getJson(text2);
   }

   public void sendUi(String text2, String text3, @Nullable JsonObject jsonObject2) {
      this.pveController.enqueue("ui", text2, text3, jsonObject2);
   }

   public JsonArray getCatalog() {
      return this.jsonObject.has("catalog") ? this.jsonObject.getAsJsonArray("catalog") : new JsonArray();
   }

   public List<String> m_2_List(String text2) {
      return !this.jsonObject.has("ui") ? List.of() : PveSnapshot.toStringList(this.jsonObject.getAsJsonObject("ui").getAsJsonArray(text2));
   }

   public boolean m_3_boolean(String text2, String text3) {
      switch (text3) {
         case "autobuy.gui":
            Minecraft.getInstance().gui.setScreen(AutoBuyScreen.getInstance());
            break;
         case "collector.gui":
            Minecraft.getInstance().gui.setScreen(CollectorScreen.getInstance());
            break;
         default:
            this.pveController.enqueue("ui", text2, text3, null);
            return false;
      }

      return true;
   }

   private void m_1(State state) {
      if (state == State.ONLINE || state == State.SIGNED_OUT) {
         AvatarTexture.m_9_void();
      }

      switch (state) {
         case ONLINE:
            if (!this.flag) {
               this.m_6_void();
            }

            this.m_5_void();
            break;
         case SIGNED_OUT:
            this.flag = false;
            this.pveController.m_6_void();
            break;
         case ACTIVATING:
            this.flag = false;
            break;
         case OFFLINE:
            this.pveController.m_6_void();
      }
   }

   private void m_6_void() {
      this.flag = true;
      String text2 = this.stonLink.getSessionToken();
      Diagnostics.info(
         "[Neuro2] link online with the {} token {} (launcher token {})",
         text2 != null ? "link's" : "launcher's",
         VmBinding.hashShort(text2 != null ? text2 : ""),
         VmBinding.hashShort(this.text)
      );
   }

   private void m_5_void() {
      this.stonLink.call("pve.manifest", "{}", 10000L).whenComplete((text2, e) -> {
         if (e != null) {
            Diagnostics.warn("PvE manifest unavailable: {}", e.getMessage());
         } else {
            Minecraft.getInstance().execute(() -> this.m_1_void(JsonParser.parseString(text2).getAsJsonObject()));
         }
      });
   }

   private void m_1_void(JsonObject jsonObject2) {
      if (!jsonObject2.toString().equals(this.jsonObject.toString())) {
         try {
            Files.createDirectories(this.path);
            Files.writeString(this.path.resolve("manifest.json"), jsonObject2.toString(), StandardCharsets.UTF_8);
         } catch (Exception e) {
            Diagnostics.warn("Could not cache the PvE manifest", e);
         }
      }

      this.jsonObject = jsonObject2;

      for (JsonElement jsonElement : this.getModulesJson()) {
         JsonObject jsonObject3 = jsonElement.getAsJsonObject();
         String text2 = jsonObject3.get("id").getAsString();
         C4q c4q = this.stringMap.get(text2);
         if (c4q != null) {
            c4q.m_9_void(jsonObject3);
         } else {
            C4q c4q2 = new C4q(jsonObject3, this.getPath(text2));
            this.stringMap.put(text2, c4q2);
            if (!c4q2.isFlag()) {
               Onetap.MODULE_MANAGER.register(c4q2);
               Onetap.CONFIG_MANAGER.m_7("autocfg", c4q2);
            }
         }
      }

      this.m_7_void();
   }

   private void m_7_void() {
      for (JsonElement jsonElement : this.getModulesJson()) {
         JsonObject jsonObject2 = jsonElement.getAsJsonObject();
         if (jsonObject2.has("commands")) {
            String text2 = jsonObject2.get("id").getAsString();

            for (JsonElement jsonElement2 : jsonObject2.getAsJsonArray("commands")) {
               JsonObject jsonObject3 = jsonElement2.getAsJsonObject();
               Onetap.COMMAND_MANAGER.register(new PlatformConnection.StateHolder(text2, jsonObject3.get("name").getAsString(), jsonObject3.get("desc").getAsString()));
            }
         }
      }
   }

   private JsonArray getModulesJson() {
      return this.jsonObject.has("modules") ? this.jsonObject.getAsJsonArray("modules") : new JsonArray();
   }

   private Path getPath(String text2) {
      if ("autobuy".equals(text2)) {
         return this.path.getParent().resolve("autobuy.json");
      }

      Path path2 = this.path.resolve(text2 + ".json");
      if ("collector".equals(text2)) {
         this.m_8_void(path2);
      }

      return path2;
   }

   private void m_8_void(Path path2) {
      Path path3 = this.path.getParent().resolve("collector.json");
      if (!Files.exists(path2) && Files.exists(path3)) {
         try {
            JsonElement jsonElement = JsonParser.parseString(Files.readString(path3, StandardCharsets.UTF_8));
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("items", jsonElement);
            Files.createDirectories(this.path);
            Files.writeString(path2, jsonObject2.toString(), StandardCharsets.UTF_8);
         } catch (Exception e) {
            Diagnostics.warn("Could not carry collector.json over", e);
         }
      }
   }

   static {
      m_8(new int[5]);
   }

   public static void m_8(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_5_IArr() {
      return valueArray;
   }

   private final class StateHolder implements Command {
      private final String text;
      private final String text2;
      private final String text3;

      public StateHolder(String text4, String text5, String text6) {
         this.text = text4;
         this.text2 = text5;
         this.text3 = text6;
      }

      @Override
      public String getText2() {
         return this.text2;
      }

      @Override
      public String getText3() {
         return this.text3;
      }

      @Override
      public String getUsage() {
         return "";
      }

      @Override
      public void showHelp(CommandHelp commandHelp) {
         if (Minecraft.getInstance().player == null) {
            ChatUtil.log("§cИгрок не найден.");
         } else {
            C4q c4q = PlatformConnection.this.stringMap.get(this.text);
            if (c4q != null && c4q.isEnabled()) {
               JsonObject jsonObject2 = new JsonObject();
               JsonArray jsonArray = new JsonArray();
               commandHelp.getStringList().forEach(jsonArray::add);
               jsonObject2.add("args", jsonArray);
               PlatformConnection.this.pveController.enqueue("cmd", this.text, this.text2, jsonObject2);
            } else {
               ChatUtil.log("§cМодуль не включён.");
            }
         }
      }
   }

   private record DataRecord(LatencyStats.StateHolder f_80, LatencyStats.StateHolder f_8w, LatencyStats.StateHolder f_8_e) {
      public DataRecord(String text2) {
         this(LatencyStats.getOrCreate(text2 + " link out"), LatencyStats.getOrCreate(text2 + " wire"), LatencyStats.getOrCreate(text2 + " link in"));
      }

      public LatencyStats.StateHolder m_80_C0() {
         return this.f_80;
      }

      public LatencyStats.StateHolder m_8w_C0() {
         return this.f_8w;
      }

      public LatencyStats.StateHolder m_8_e() {
         return this.f_8_e;
      }
   }
}
