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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import wtf.onetap.Onetap;
import wtf.onetap.gui.widget.TextField;
import wtf.onetap.module.Module;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.module.render.HUD;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.setting.Setting;
import wtf.onetap.setting.TextSetting;

public class ConfigManager {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final Path path;
   private final Path path2;
   private boolean flag;
   private static String text;

   public ConfigManager() {
      this.path = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("configs");
      this.path2 = this.path.resolveSibling("secrets.json");
   }

   public Path getPath() {
      return this.path;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void saveConfig(String text2) {
      Path path3 = this.getConfigPath(text2);

      try {
         Files.createDirectories(this.path);
         JsonObject jsonObject = new JsonObject();
         JsonObject jsonObject2 = this.readJson(path3);
         UnHook unHook = Onetap.MODULE_MANAGER.get(UnHook.class);

         for (Module module : Onetap.MODULE_MANAGER.getModules()) {
            JsonObject jsonObject3 = new JsonObject();
            boolean flag2 = unHook != null && unHook.isFlag() ? unHook.isHidden(module) : module.isEnabled();
            jsonObject3.addProperty("enabled", !(module instanceof ClickGUI) && !(module instanceof UnHook) && flag2);
            jsonObject3.addProperty("key", module.getKey());
            JsonObject jsonObject4 = new JsonObject();
            m_3(jsonObject4, module.getSettings());
            jsonObject3.add("settings", jsonObject4);
            JsonObject jsonObject5 = new JsonObject();
            m_4(jsonObject5, module.getSettings());
            jsonObject3.add("binds", jsonObject5);
            jsonObject2.add(module.getName(), jsonObject3);
         }

         jsonObject.add("modules", jsonObject2);
         this.saveHud(jsonObject);

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path3, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonObject, bufferedWriter);
         }

         this.serializeAll();
      } catch (IOException e) {
         Onetap.LOGGER.error("Failed to save config {}", text2, e);
         throw new ConfigException("Не удалось сохранить конфиг: " + e.getMessage());
      }
   }

   public void loadConfig(String text2) {
      UnHook unHook = Onetap.MODULE_MANAGER.get(UnHook.class);
      if (unHook != null && unHook.isFlag()) {
         throw new ConfigException("Сначала верни клиент биндом возврата");
      }

      Path path3 = this.getConfigPath(text2);
      if (!Files.exists(path3)) {
         throw new ConfigException("Конфиг не найден: " + text2);
      }

      this.flag = true;

      try (BufferedReader bufferedReader = Files.newBufferedReader(path3, StandardCharsets.UTF_8)) {
         JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("modules");
         if (jsonObject2 != null) {
            for (Module module : Onetap.MODULE_MANAGER.getModules()) {
               JsonObject jsonObject3 = jsonObject2.getAsJsonObject(module.getName());
               if (jsonObject3 == null) {
                  if (!(module instanceof ClickGUI)) {
                     module.setEnabled(false);
                     module.setKey(module.getDefaultKey());
                  }
               } else {
                  if (jsonObject3.has("enabled") && !(module instanceof ClickGUI) && !(module instanceof UnHook)) {
                     module.setEnabled(jsonObject3.get("enabled").getAsBoolean());
                  }

                  if (jsonObject3.has("key")) {
                     module.setKey(jsonObject3.get("key").getAsInt());
                  }

                  m_5(jsonObject3.getAsJsonObject("binds"), module.getSettings());
                  JsonObject jsonObject4 = jsonObject3.getAsJsonObject("settings");
                  if (jsonObject4 != null) {
                     m_3(jsonObject4, module.getSettings(), module.getName());
                  }
               }
            }

            this.loadHud(jsonObject);
            this.m_3_void();
            return;
         }
      } catch (Exception e) {
         Onetap.LOGGER.error("Failed to load config {}", text2, e);
         throw new ConfigException("Не удалось загрузить конфиг: " + e.getMessage());
      } finally {
         this.flag = false;
      }
   }

   public void m_7(String text2, Module module) {
      JsonObject jsonObject = this.readJson(this.getConfigPath(text2)).getAsJsonObject(module.getName());
      if (jsonObject != null) {
         this.flag = true;

         try {
            if (jsonObject.has("key")) {
               module.setKey(jsonObject.get("key").getAsInt());
            }

            JsonObject jsonObject2 = jsonObject.getAsJsonObject("settings");
            if (jsonObject2 != null) {
               m_3(jsonObject2, module.getSettings(), module.getName());
            }

            if (jsonObject.has("enabled")) {
               module.setEnabled(jsonObject.get("enabled").getAsBoolean());
            }
         } finally {
            this.flag = false;
         }
      }
   }

   private JsonObject readJson(Path path3) {
      if (!Files.exists(path3)) {
         return new JsonObject();
      }

      try (BufferedReader bufferedReader = Files.newBufferedReader(path3, StandardCharsets.UTF_8)) {
         JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject().getAsJsonObject("modules");
         return jsonObject == null ? new JsonObject() : jsonObject;
      } catch (Exception e) {
         return new JsonObject();
      }
   }

   public void m_8_void() {
      if (Files.exists(this.getConfigPath("default"))) {
         this.loadConfig("default");
      }
   }

   public void m_5_void() {
      this.saveConfig("default");
   }

   public boolean deleteConfig(String text2) {
      if ("default".equalsIgnoreCase(text2)) {
         throw new ConfigException("Нельзя удалить конфиг по умолчанию");
      }

      try {
         return Files.deleteIfExists(this.getConfigPath(text2));
      } catch (IOException e) {
         throw new ConfigException("Не удалось удалить конфиг: " + e.getMessage());
      }
   }

   public List<String> listConfigs() {
      if (!Files.exists(this.path)) {
         return List.of();
      }

      try (Stream stream = Files.list(this.path)) {
         return (List<String>)stream.filter(object -> Files.isRegularFile((java.nio.file.Path)object))
            .filter(object2 -> object2.toString().endsWith(".json"))
            .map(object3 -> ((java.nio.file.Path)object3).getFileName().toString())
            .map(object4 -> ((java.lang.String)object4).substring(0, ((java.lang.String)object4).length() - ".json".length()))
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(Collectors.toList());
      } catch (IOException e) {
         throw new ConfigException("Не удалось получить список конфигов: " + e.getMessage());
      }
   }

   private Path getConfigPath(String text2) {
      String text3 = text2.replaceAll("[^a-zA-Z0-9._-]", "_");
      return this.path.resolve(text3 + ".json");
   }

   private List<TextField> m_6_List() {
      ArrayList list = new ArrayList();
      HUD hUD = Onetap.MODULE_MANAGER.get(HUD.class);
      if (hUD != null) {
         list.addAll(hUD.getFields());
      }

      return list;
   }

   private void saveHud(JsonObject jsonObject) {
      var textFieldList = this.m_6_List();
      if (!textFieldList.isEmpty()) {
         JsonObject jsonObject2 = new JsonObject();

         for (TextField textField : textFieldList) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("x", textField.m_5_float());
            jsonObject3.addProperty("y", textField.m_8_float());
            jsonObject2.add(textField.getText(), jsonObject3);
         }

         jsonObject.add("hud", jsonObject2);
      }
   }

   private void loadHud(JsonObject jsonObject) {
      JsonObject jsonObject2 = jsonObject.getAsJsonObject("hud");
      if (jsonObject2 != null) {
         for (TextField textField : this.m_6_List()) {
            JsonObject jsonObject3 = jsonObject2.getAsJsonObject(textField.getText());
            if (jsonObject3 != null) {
               try {
                  textField.m_2_void(jsonObject3.get("x").getAsFloat(), jsonObject3.get("y").getAsFloat());
                  textField.setFlag2(true);
               } catch (Exception e) {
                  Onetap.LOGGER.warn("Failed to load hud element {}: {}", textField.getText(), e.getMessage());
               }
            }
         }
      }
   }

   private void serializeAll() throws java.io.IOException {
      JsonObject jsonObject = new JsonObject();

      for (Module module : Onetap.MODULE_MANAGER.getModules()) {
         JsonObject jsonObject2 = new JsonObject();

         for (Setting setting : module.getSettings()) {
            if (setting instanceof TextSetting textSetting && textSetting.isNumericOnly()) {
               jsonObject2.addProperty(textSetting.getName(), (String)textSetting.getValue());
            }
         }

         if (!jsonObject2.isEmpty()) {
            jsonObject.add(module.getName(), jsonObject2);
         }
      }

      try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path2, StandardCharsets.UTF_8)) {
         GSON.toJson(jsonObject, bufferedWriter);
      }
   }

   private void m_3_void() throws java.io.IOException {
      if (Files.exists(this.path2)) {
         JsonObject jsonObject;
         try (BufferedReader bufferedReader = Files.newBufferedReader(this.path2, StandardCharsets.UTF_8)) {
            jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
         }

         for (Module module : Onetap.MODULE_MANAGER.getModules()) {
            JsonObject jsonObject2 = jsonObject.getAsJsonObject(module.getName());
            if (jsonObject2 != null) {
               for (Setting setting : module.getSettings()) {
                  if (setting instanceof TextSetting textSetting && textSetting.isNumericOnly() && jsonObject2.has(textSetting.getName())) {
                     textSetting.setValue(jsonObject2.get(textSetting.getName()).getAsString());
                  }
               }
            }
         }
      }
   }

   public static void m_3(JsonObject jsonObject, List<Setting<?>> settingList) {
      HashMap map = new HashMap();

      for (Setting setting : settingList) {
         m_4(jsonObject, setting, uniqueName(setting.getName(), map));
      }
   }

   public static void m_3(JsonObject jsonObject, List<Setting<?>> settingList, String text2) {
      HashMap map = new HashMap();

      for (Setting setting : settingList) {
         m_1(jsonObject, setting, text2, uniqueName(setting.getName(), map));
      }
   }

   private static void m_4(JsonObject jsonObject, List<Setting<?>> settingList) {
      HashMap map = new HashMap();

      for (Setting setting : settingList) {
         String text2 = uniqueName(setting.getName(), map);
         if (setting instanceof BooleanSetting booleanSetting && booleanSetting.getKey() != -1) {
            jsonObject.addProperty(text2, booleanSetting.getKey());
         }
      }
   }

   private static void m_5(JsonObject jsonObject, List<Setting<?>> settingList) {
      HashMap map = new HashMap();

      for (Setting setting : settingList) {
         String text2 = uniqueName(setting.getName(), map);
         if (setting instanceof BooleanSetting booleanSetting) {
            JsonElement jsonElement = jsonObject == null ? null : jsonObject.get(text2);
            booleanSetting.setKey(jsonElement != null && jsonElement.isJsonPrimitive() ? jsonElement.getAsInt() : -1);
         }
      }
   }

   public static String uniqueName(String text2, Map<String, Integer> map) {
      int value = map.merge(text2, 1, Integer::sum) - 1;
      return value == 0 ? text2 : text2 + "#" + value;
   }

   private static void m_4(JsonObject jsonObject, Setting<?> setting, String text2) {
      if (setting instanceof BooleanSetting booleanSetting) {
         jsonObject.addProperty(text2, (Boolean)booleanSetting.getValue());
      } else if (setting instanceof FloatSetting floatSetting) {
         jsonObject.addProperty(text2, (Number)floatSetting.getValue());
      } else if (setting instanceof KeybindSetting keybindSetting) {
         jsonObject.addProperty(text2, (Number)keybindSetting.getValue());
      } else if (setting instanceof ColorSetting colorSetting) {
         jsonObject.addProperty(text2, (Number)colorSetting.getValue());
      } else if (setting instanceof ModeSetting modeSetting) {
         jsonObject.addProperty(text2, (String)modeSetting.getValue());
      } else if (setting instanceof TextSetting textSetting) {
         if (!textSetting.isNumericOnly()) {
            jsonObject.addProperty(text2, (String)textSetting.getValue());
         }
      } else if (setting instanceof MultiModeSetting multiModeSetting) {
         JsonArray jsonArray = new JsonArray();

         for (String text3 : (Iterable<String>)(Set)multiModeSetting.getValue()) {
            jsonArray.add(text3);
         }

         jsonObject.add(text2, jsonArray);
      }
   }

   private static void m_1(JsonObject jsonObject, Setting<?> setting, String text2, String text3) {
      getCurrentName();
      JsonElement jsonElement = jsonObject.get(text3);
      if (jsonElement == null && text2.equals("ClickGUI") && text3.equals("Свечение")) {
         jsonElement = jsonObject.get("Жидкое стекло");
      }

      if (jsonElement != null && !jsonElement.isJsonNull()) {
         try {
            if (setting instanceof BooleanSetting booleanSetting) {
               booleanSetting.setValue(jsonElement.getAsBoolean());
            } else if (setting instanceof FloatSetting floatSetting) {
               floatSetting.setValue(jsonElement.getAsFloat());
            } else if (setting instanceof KeybindSetting keybindSetting) {
               keybindSetting.setValue(jsonElement.getAsInt());
            } else if (setting instanceof ColorSetting colorSetting) {
               colorSetting.setValue(jsonElement.getAsInt());
            } else if (setting instanceof ModeSetting modeSetting) {
               String text4 = jsonElement.getAsString();
               if (text2.equals("KillAura") && text3.equals("Ротация")) {
                  if (text4.equals("SpookyTime")) {
                     text4 = "SP (old)";
                  }

                  if (text4.equals("Neuro6")) {
                     text4 = "Shakal";
                  }

                  if (text4.equals("Neuro") || text4.equals("Neuro2")) {
                     text4 = "Vanilla";
                  }

                  if (text4.equals("shardbeta")) {
                     text4 = "Shard";
                  }
               }

               modeSetting.setValue(text4);
            } else if (setting instanceof TextSetting textSetting) {
               textSetting.setValue(jsonElement.getAsString());
            } else if (setting instanceof MultiModeSetting multiModeSetting) {
               HashSet set = new HashSet();

               for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
                  set.add(jsonElement2.getAsString());
               }

               multiModeSetting.setValue(set);
            }
         } catch (Exception e) {
            Onetap.LOGGER.warn("Failed to load setting {}.{}: {}", new Object[]{text2, setting.getName(), e.getMessage()});
         }
      }
   }

   static {
      setCurrentName("viF31b");
   }

   public static void setCurrentName(String text2) {
      text = text2;
   }

   public static String getCurrentName() {
      return text;
   }
}
