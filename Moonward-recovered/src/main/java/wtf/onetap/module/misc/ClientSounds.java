package wtf.onetap.module.misc;

import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ModuleToggleEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.SoundPlayer;

@ModuleInformation(name = "ClientSounds", description = "Воспроизводит звуки переключения модулей", category = Category.MISC)
public class ClientSounds extends Module {
   private final FloatSetting volume = new FloatSetting("Громкость", 100.0F, 0.0F, 100.0F, 1.0F).build();

   @EventTarget
   private void onModuleToggle(ModuleToggleEvent event) {
      if (!Onetap.CONFIG_MANAGER.isFlag() && !event.getModuleName().equals("ClickGUI") && !event.getModuleName().equals(this.getName())) {
         this.playToggleSound(event.isEnabled(), 1.0F);
      }
   }

   public void playToggle(boolean flag) {
      if (this.isEnabled()) {
         this.playToggleSound(flag, 0.7F);
      }
   }

   private void playToggleSound(boolean flag, float floatValue) {
      SoundPlayer.m_7(flag ? "enable" : "disable", this.volume.getFloat() / 100.0F * floatValue);
   }
}
