package wtf.onetap.module.pve;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.platform.Diagnostics;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.setting.Setting;
import wtf.onetap.setting.SettingRegistry;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.LatencyStats;

public class C4q extends Module {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final String text;
   private final Path path;
   private final Map<String, String> stringMap = new HashMap<>();
   private final Set<String> stringSet = new HashSet<>();
   private final boolean flag;
   private JsonObject jsonObject;
   private JsonObject jsonObject2 = new JsonObject();
   private String text2;
   private String text3 = "";
   private int value = 1;
   private int value2 = -1;
   private static boolean flag2;

   public C4q(JsonObject jsonObject3, Path path2) {
      super(jsonObject3.get("name").getAsString(), getString(jsonObject3, "desc"), parseCategory(getString(jsonObject3, "cat")));
      this.text = jsonObject3.get("id").getAsString();
      this.flag = jsonObject3.has("hidden") && jsonObject3.get("hidden").getAsBoolean();
      this.path = path2;
      this.m_5_void(jsonObject3.getAsJsonArray("settings"));
      this.text2 = String.valueOf(jsonObject3.get("settings"));
      this.loadFile();
   }

   public String getText() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void m_7(JsonObject jsonObject3, JsonObject jsonObject4) {
      if (jsonObject3 == null) {
         this.jsonObject = null;
      } else {
         this.jsonObject = jsonObject3;
         if (jsonObject4 != null && !jsonObject4.equals(this.jsonObject2)) {
            this.jsonObject2 = jsonObject4.deepCopy();
            this.value++;
         }
      }
   }

   public boolean m_8_boolean() {
      return this.flag ? this.jsonObject != null : this.isEnabled();
   }

   public String m_5_String() {
      for (Setting setting : this.getSettings()) {
         if (this.stringSet.contains(setting.getName()) && setting instanceof TextSetting textSetting) {
            return (String)textSetting.getValue();
         }
      }

      return "";
   }

   public Object getSettingValue(String text4) {
      for (Setting setting : this.getSettings()) {
         if (setting.getName().equals(text4)) {
            return setting.getValue();
         }
      }

      return null;
   }

   public void m_9_void(JsonObject jsonObject3) {
      String text4 = String.valueOf(jsonObject3.get("settings"));
      if (!text4.equals(this.text2)) {
         JsonObject jsonObject4 = this.settingsToJson();
         this.text2 = text4;
         this.getSettings().clear();
         this.stringMap.clear();
         SettingRegistry.begin(this.getSettings());

         try {
            this.m_5_void(jsonObject3.getAsJsonArray("settings"));
         } finally {
            SettingRegistry.end();
         }

         for (Setting setting : this.getSettings()) {
            JsonElement jsonElement = jsonObject4.get(setting.getName());
            if (jsonElement != null) {
               applySetting(setting, jsonElement);
            }
         }

         this.value++;
      }
   }

   private void m_5_void(JsonArray jsonArray) {
      LatencyStats.StateHolder.m_6_C3_sArr();
      this.stringSet.clear();
      if (jsonArray != null) {
         for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject3 = jsonElement.getAsJsonObject();
            String text4 = jsonObject3.get("name").getAsString();
            JsonElement jsonElement2 = jsonObject3.get("def");

            Setting setting = switch (jsonObject3.get("type").getAsString()) {
               case "mode" -> new ModeSetting(text4, jsonElement2.getAsString(), toStringArray(jsonObject3.getAsJsonArray("modes"))).build();
               case "multi" -> {
                  String[] stringArray = toStringArray(jsonObject3.getAsJsonArray("modes"));
                  MultiModeSetting multiModeSetting = new MultiModeSetting(text4, stringArray.length > 0 ? stringArray[0] : "", stringArray).build();
                  multiModeSetting.setValue(new LinkedHashSet<>(List.of(toStringArray(jsonElement2.getAsJsonArray()))));
                  yield multiModeSetting;
               }
               case "slider" -> new FloatSetting(text4, jsonElement2.getAsDouble(), jsonObject3.get("min").getAsDouble(), jsonObject3.get("max").getAsDouble(), jsonObject3.get("step").getAsDouble())
                  .build();
               case "text" -> new TextSetting(text4, jsonElement2.getAsString()).build();
               case "secret" -> {
                  this.stringSet.add(text4);
                  yield new TextSetting(text4, jsonElement2.getAsString()).build();
               }
               case "color" -> new ColorSetting(text4, jsonElement2.getAsInt()).build();
               case "bool" -> new BooleanSetting(text4, jsonElement2.getAsBoolean()).build();
               case "bind" -> {
                  if (jsonObject3.has("act")) {
                     this.stringMap.put(text4, jsonObject3.get("act").getAsString());
                  }

                  yield new KeybindSetting(text4, jsonElement2.getAsInt()).build();
               }
               default -> null;
            };
            if (setting != null) {
               BooleanSupplier booleanSupplier = this.m_1_BooleanSupplier(jsonObject3);
               if (booleanSupplier != null) {
                  setting.visibleWhen(booleanSupplier);
               }
            }
         }
      }
   }

   private BooleanSupplier m_1_BooleanSupplier(JsonObject jsonObject3) {
      ArrayList<BooleanSupplier> booleanSupplierList = new ArrayList<>();
      if (jsonObject3.has("vis")) {
         booleanSupplierList.add(this.m_2(jsonObject3.get("vis").getAsString(), jsonObject3.get("eq"), false));
      }

      if (jsonObject3.has("all") && jsonObject3.get("all").isJsonArray()) {
         for (JsonElement jsonElement : jsonObject3.getAsJsonArray("all")) {
            JsonObject jsonObject4 = jsonElement.getAsJsonObject();
            booleanSupplierList.add(this.m_2(jsonObject4.get("s").getAsString(), jsonObject4.get("eq"), jsonObject4.has("not") && jsonObject4.get("not").getAsBoolean()));
         }
      }

      return booleanSupplierList.isEmpty() ? null : () -> {
         for (BooleanSupplier booleanSupplier : booleanSupplierList) {
            if (!booleanSupplier.getAsBoolean()) {
               return false;
            }
         }

         return true;
      };
   }

   private BooleanSupplier m_2(String text4, JsonElement jsonElement, boolean flag3) {
      return () -> {
         for (Setting setting : this.getSettings()) {
            if (setting.getName().equals(text4)) {
               return Objects.equals(settingToJson(setting), jsonElement) != flag3;
            }
         }

         return true;
      };
   }

   public Map<String, Integer> buildMap() {
      HashMap map = new HashMap();

      for (Setting setting : this.getSettings()) {
         String text4 = this.stringMap.get(setting.getName());
         if (text4 != null && setting instanceof KeybindSetting keybindSetting) {
            map.put(text4, (Integer)keybindSetting.getValue());
         }
      }

      return map;
   }

   public int m_2_int(String text4) {
      return this.buildMap().getOrDefault(text4, -1);
   }

   public JsonObject settingsToJson() {
      JsonObject jsonObject3 = new JsonObject();

      for (Setting setting : this.getSettings()) {
         jsonObject3.add(setting.getName(), settingToJson(setting));
      }

      return jsonObject3;
   }

   private JsonObject m_4_JsonObject() {
      if (this.jsonObject != null) {
         return this.jsonObject;
      }

      JsonObject jsonObject3 = this.settingsToJson();

      for (String text4 : this.stringSet) {
         JsonElement jsonElement = jsonObject3.get(text4);
         jsonObject3.addProperty(text4, jsonElement != null && !jsonElement.getAsString().isBlank());
      }

      return jsonObject3;
   }

   public JsonObject m_0_JsonObject() {
      String text4 = String.valueOf(this.m_4_JsonObject());
      if (!text4.equals(this.text3)) {
         this.text3 = text4;
         this.value++;
      }

      if (this.value2 == this.value) {
         return null;
      }

      JsonObject jsonObject3 = new JsonObject();
      jsonObject3.addProperty("rev", this.value);
      jsonObject3.add("s", this.m_4_JsonObject());
      jsonObject3.add("d", this.jsonObject2.deepCopy());
      return jsonObject3;
   }

   public void setValue2(int value3) {
      this.value2 = value3;
   }

   public void m_3_void() {
      this.value2 = -1;
   }

   public JsonObject getJsonObject2() {
      return this.jsonObject2;
   }

   public void m_4_void() {
      this.value++;
      this.saveFile();
   }

   public void m_4_void(JsonObject jsonObject3) {
      mergeJson(this.jsonObject2, jsonObject3);
      this.m_4_void();
   }

   private static void mergeJson(JsonObject jsonObject3, JsonObject jsonObject4) {
      for (Entry entry : jsonObject4.entrySet()) {
         JsonElement jsonElement = jsonObject3.get((String)entry.getKey());
         if (jsonElement != null && jsonElement.isJsonObject() && ((JsonElement)entry.getValue()).isJsonObject()) {
            mergeJson(jsonElement.getAsJsonObject(), ((JsonElement)entry.getValue()).getAsJsonObject());
         } else {
            jsonObject3.add((String)entry.getKey(), ((JsonElement)entry.getValue()).deepCopy());
         }
      }
   }

   private void loadFile() {
      if (Files.exists(this.path)) {
         try (BufferedReader bufferedReader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            JsonElement jsonElement = JsonParser.parseReader(bufferedReader);
            if (jsonElement.isJsonObject()) {
               this.jsonObject2 = jsonElement.getAsJsonObject();
            }
         } catch (Exception e) {
            Diagnostics.warn("Failed to read {}", this.path, e);
         }
      }
   }

   private void saveFile() {
      try {
         Files.createDirectories(this.path.getParent());

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
            GSON.toJson(this.jsonObject2, bufferedWriter);
         }
      } catch (Exception e) {
         Diagnostics.warn("Failed to save {}", this.path, e);
      }
   }

   private static JsonElement settingToJson(Setting<?> setting) {
      Object object = setting.getValue();
      if (object instanceof Boolean bool) {
         return new JsonPrimitive(bool);
      } else if (object instanceof Number number) {
         return new JsonPrimitive(number);
      } else if (!(object instanceof Set set)) {
         return new JsonPrimitive(String.valueOf(object));
      } else {
         JsonArray jsonArray = new JsonArray();

         for (Object object2 : set) {
            jsonArray.add(String.valueOf(object2));
         }

         return jsonArray;
      }
   }

   private static void applySetting(Setting<?> setting, JsonElement jsonElement) {
      try {
         if (setting instanceof BooleanSetting booleanSetting) {
            booleanSetting.setValue(jsonElement.getAsBoolean());
         } else if (setting instanceof FloatSetting floatSetting) {
            floatSetting.setValue(jsonElement.getAsFloat());
         } else if (setting instanceof KeybindSetting keybindSetting) {
            keybindSetting.setValue(jsonElement.getAsInt());
         } else if (setting instanceof ModeSetting modeSetting) {
            modeSetting.setValue(jsonElement.getAsString());
         } else if (setting instanceof TextSetting textSetting) {
            textSetting.setValue(jsonElement.getAsString());
         } else if (setting instanceof MultiModeSetting multiModeSetting) {
            multiModeSetting.setValue(new LinkedHashSet<>(List.of(toStringArray(jsonElement.getAsJsonArray()))));
         }
      } catch (Exception e) {
      }
   }

   private static String[] toStringArray(JsonArray jsonArray) {
      if (jsonArray == null) {
         return new String[0];
      }

      String[] stringArray = new String[jsonArray.size()];

      for (int i = 0; i < stringArray.length; i++) {
         stringArray[i] = jsonArray.get(i).getAsString();
      }

      return stringArray;
   }

   private static String getString(JsonObject jsonObject3, String text4) {
      return jsonObject3.has(text4) ? jsonObject3.get(text4).getAsString() : "";
   }

   private static Category parseCategory(String text4) {
      for (Category category : Category.values()) {
         if (category.name().equalsIgnoreCase(text4)) {
            return category;
         }
      }

      return Category.PVE;
   }

   static {
      m_3_void(true);
   }

   public static void m_3_void(boolean flag3) {
      flag2 = flag3;
   }

   public static boolean m_1_boolean() {
      return flag2;
   }

   public static boolean m_4_boolean() {
      return false;
   }
}
