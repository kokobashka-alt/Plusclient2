package wtf.onetap.module;

import java.util.ArrayList;
import java.util.List;
import wtf.onetap.Onetap;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ModuleToggleEvent;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.setting.Setting;
import wtf.onetap.setting.SettingRegistry;
import wtf.onetap.util.MinecraftAccess;

public abstract class Module implements MinecraftAccess {
   private final String name;
   private final String description;
   private final Category category;
   private final int defaultKey;
   private final List<Setting<?>> settings;
   private boolean enabled;
   private int key;
   private final Animation animation;
   private static String text;

   public Module() {
      this.settings = new ArrayList<>();
      ModuleManager.m_1_StringArr();
      this.enabled = false;
      this.animation = new Animation(250L, 0.0F, Easing.EASING8);
      ModuleInformation moduleInformation = this.getClass().getAnnotation(ModuleInformation.class);
      if (moduleInformation == null) {
         throw new IllegalStateException(this.getClass().getName() + " missing @ModuleInformation");
      }

      this.name = moduleInformation.name();
      this.description = moduleInformation.description();
      this.category = moduleInformation.category();
      this.defaultKey = moduleInformation.key();
      this.key = this.defaultKey;
      SettingRegistry.begin(this.settings);
   }

   public Module(String text2, String text3, Category category2) {
      this.settings = new ArrayList<>();
      this.enabled = false;
      this.animation = new Animation(250L, 0.0F, Easing.EASING8);
      this.name = text2;
      this.description = text3;
      this.category = category2;
      this.defaultKey = -1;
      this.key = this.defaultKey;
      SettingRegistry.begin(this.settings);
   }

   public void finishSettings() {
      SettingRegistry.end();
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void m_5_void() {
   }

   public void m_1_void(float floatValue) {
   }

   public void toggle(float floatValue) {
   }

   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   public void setEnabled(boolean flag) {
      if (flag != this.enabled) {
         UnHook unHook = Onetap.MODULE_MANAGER.get(UnHook.class);
         if (!flag || unHook == null || !unHook.isFlag()) {
            this.enabled = flag;
            if (this.enabled) {
               Onetap.EVENT_BUS.register(this);
               this.onEnable();
            } else {
               try {
                  this.onDisable();
               } finally {
                  Onetap.EVENT_BUS.unregister(this);
               }
            }

            Onetap.EVENT_BUS.unregister(new ModuleToggleEvent(this.getName(), this.enabled));
         }
      }
   }

   public float getAnimationProgress() {
      return this.animation.update(this.enabled ? 1.0F : 0.0F);
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Category getCategory() {
      return this.category;
   }

   public List<Setting<?>> getSettings() {
      return this.settings;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getKey() {
      return this.key;
   }

   public int getDefaultKey() {
      return this.defaultKey;
   }

   public void setKey(int value) {
      this.key = value;
   }

   public static void finishSettings(String text2) {
      text = text2;
   }

   public static String m_0_String() {
      return text;
   }

   private static String a(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
