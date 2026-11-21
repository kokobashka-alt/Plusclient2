package wtf.onetap.render;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EraseAfterRun;
import wtf.onetap.exploit.anydesk.KeyMessage;
import wtf.onetap.setting.ColorSetting;

public class ThemeManager {
   private static final KeyMessage[] KEY_MESSAGE_ARRAY = new KeyMessage[]{
      new KeyMessage("Haze", ColorUtil.rgba(120, 80, 255, 255), ColorUtil.rgba(255, 80, 180, 255)),
      new KeyMessage("Frost", ColorUtil.rgba(20, 200, 220, 255), ColorUtil.rgba(160, 220, 255, 255)),
      new KeyMessage("Bloom", ColorUtil.rgba(255, 100, 60, 255), ColorUtil.rgba(255, 180, 50, 255)),
      new KeyMessage("Void", ColorUtil.rgba(100, 40, 180, 255), ColorUtil.rgba(200, 40, 120, 255)),
      new KeyMessage("Surge", ColorUtil.rgba(0, 255, 120, 255), ColorUtil.rgba(0, 150, 255, 255)),
      new KeyMessage("Ember", ColorUtil.rgba(200, 40, 40, 255), ColorUtil.rgba(255, 140, 20, 255)),
      new KeyMessage("Sakura", ColorUtil.rgba(255, 120, 180, 255), ColorUtil.rgba(200, 60, 140, 255)),
      new KeyMessage("Mint", ColorUtil.rgba(80, 220, 160, 255), ColorUtil.rgba(40, 180, 200, 255)),
      new KeyMessage("Cosmic", ColorUtil.rgba(180, 60, 255, 255), ColorUtil.rgba(40, 100, 255, 255)),
      new KeyMessage("Amber", ColorUtil.rgba(255, 180, 40, 255), ColorUtil.rgba(255, 220, 80, 255)),
      new KeyMessage("Twilight", ColorUtil.rgba(80, 60, 190, 255), ColorUtil.rgba(200, 100, 220, 255)),
      new KeyMessage("Lime", ColorUtil.rgba(120, 230, 50, 255), ColorUtil.rgba(50, 180, 100, 255)),
      new KeyMessage("Coral", ColorUtil.rgba(255, 120, 100, 255), ColorUtil.rgba(255, 80, 140, 255)),
      new KeyMessage("Ocean", ColorUtil.rgba(20, 160, 200, 255), ColorUtil.rgba(40, 80, 180, 255)),
      new KeyMessage("Sunset", ColorUtil.rgba(255, 80, 60, 255), ColorUtil.rgba(200, 60, 160, 255)),
      new KeyMessage("Nord", ColorUtil.rgba(60, 120, 180, 255), ColorUtil.rgba(100, 200, 220, 255))
   };
   private final Path path;
   private final Path path2;
   private final ColorSetting colorSetting;
   private final ColorSetting colorSetting2;
   private KeyMessage keyMessage;
   private boolean flag;
   private int value;

   public ThemeManager() {
      this.path = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("theme.json");
      this.path2 = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("configs").resolve("theme.json");
      this.colorSetting = new ColorSetting("Цвет 1", ColorUtil.rgba(120, 80, 255, 255));
      this.colorSetting2 = new ColorSetting("Цвет 2", ColorUtil.rgba(255, 80, 180, 255));
      this.keyMessage = new KeyMessage("Custom", (Integer)this.colorSetting.getValue(), (Integer)this.colorSetting2.getValue());
   }

   private KeyMessage getKeyMessage() {
      return this.keyMessage;
   }

   public KeyMessage m_0_C54() {
      return this.value == 0 ? this.getKeyMessage() : KEY_MESSAGE_ARRAY[this.value - 1];
   }

   public int m_9_int() {
      return this.m_0_C54().m_1m_int();
   }

   public int m_8_int() {
      return this.m_0_C54().m_1c();
   }

   public int getValue() {
      return this.value;
   }

   public int m_0_int() {
      return KEY_MESSAGE_ARRAY.length + 1;
   }

   public KeyMessage m_8_C54(int value2) {
      return value2 == 0 ? this.getKeyMessage() : KEY_MESSAGE_ARRAY[value2 - 1];
   }

   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   public ColorSetting getColorSetting2() {
      return this.colorSetting2;
   }

   public boolean m_9_boolean(int value2) {
      return value2 == 0;
   }

   public boolean m_5_boolean(int value2) {
      if (value2 >= 0 && value2 < KEY_MESSAGE_ARRAY.length + 1 && value2 != this.value) {
         this.value = value2;
         ColorUtil.m_9_void();
         this.m_2_void();
         return true;
      } else {
         return false;
      }
   }

   public void m_4_void() {
      this.keyMessage = new KeyMessage("Custom", (Integer)this.colorSetting.getValue(), (Integer)this.colorSetting2.getValue());
      if (this.value == 0) {
         ColorUtil.m_9_void();
      }

      this.flag = true;
   }

   public void m_7_void() {
      if (this.flag) {
         this.flag = false;
         this.m_2_void();
      }
   }

   @EraseAfterRun
   public void m_3_void() {
      Path path3 = Files.exists(this.path) ? this.path : this.path2;
      if (Files.exists(path3)) {
         boolean flag2 = path3.equals(this.path2);

         try (BufferedReader bufferedReader = Files.newBufferedReader(path3, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            int value2 = jsonObject.get("theme").getAsInt();
            if (value2 >= 0 && value2 < KEY_MESSAGE_ARRAY.length + 1) {
               this.value = value2;
            }

            boolean flag3 = !jsonObject.has("custom1");
            if (flag3 && this.value != 0) {
               this.value++;
            }

            if (jsonObject.has("custom1")) {
               this.colorSetting.setValue(jsonObject.get("custom1").getAsInt());
            }

            if (jsonObject.has("custom2")) {
               this.colorSetting2.setValue(jsonObject.get("custom2").getAsInt());
            }

            this.keyMessage = new KeyMessage("Custom", (Integer)this.colorSetting.getValue(), (Integer)this.colorSetting2.getValue());
         } catch (IOException e) {
            Onetap.LOGGER.error("Failed to load theme", e);
            return;
         }

         if (flag2) {
            this.m_2_void();

            try {
               Files.deleteIfExists(this.path2);
            } catch (IOException e2) {
               Onetap.LOGGER.error("Failed to migrate theme", e2);
            }
         }
      }
   }

   public void m_2_void() {
      try {
         Files.createDirectories(this.path.getParent());
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("theme", this.value);
         jsonObject.addProperty("custom1", (Number)this.colorSetting.getValue());
         jsonObject.addProperty("custom2", (Number)this.colorSetting2.getValue());

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, bufferedWriter);
         }
      } catch (IOException e) {
         Onetap.LOGGER.error("Failed to save theme", e);
      }
   }
}
