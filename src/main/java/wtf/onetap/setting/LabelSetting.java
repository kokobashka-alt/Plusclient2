package wtf.onetap.setting;

import java.util.function.BooleanSupplier;

public class LabelSetting extends Setting<String> {
   public LabelSetting(String text) {
      super(text, text);
   }

   public LabelSetting build() {
      SettingRegistry.register(this);
      return this;
   }

   public LabelSetting visibleWhen(BooleanSupplier booleanSupplier) {
      super.visibleWhen(booleanSupplier);
      return this;
   }
}
