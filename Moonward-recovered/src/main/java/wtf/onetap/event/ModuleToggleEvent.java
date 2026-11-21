package wtf.onetap.event;

public class ModuleToggleEvent extends Event {
   private final String moduleName;
   private final boolean enabled;

   public ModuleToggleEvent(String text, boolean flag) {
      this.moduleName = text;
      this.enabled = flag;
   }

   public String getModuleName() {
      return this.moduleName;
   }

   public boolean isEnabled() {
      return this.enabled;
   }
}
