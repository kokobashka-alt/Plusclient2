package wtf.onetap.setting;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ButtonSetting extends Setting<Void> {
   private final Runnable runnable;
   private Supplier<String> supplier = this::getName;
   private BooleanSupplier booleanSupplier = () -> true;

   public ButtonSetting(String text, Runnable runnable2) {
      super(text, null);
      this.runnable = runnable2;
   }

   public ButtonSetting withSupplier(Supplier<String> supplier2) {
      this.supplier = supplier2;
      return this;
   }

   public ButtonSetting withBooleanSupplier(BooleanSupplier booleanSupplier2) {
      this.booleanSupplier = booleanSupplier2;
      return this;
   }

   public String m_7_String() {
      return this.supplier.get();
   }

   public boolean m_8_boolean() {
      return this.booleanSupplier.getAsBoolean();
   }

   public void click() {
      if (this.m_8_boolean()) {
         this.runnable.run();
      }
   }

   public ButtonSetting build() {
      SettingRegistry.register(this);
      return this;
   }
}
