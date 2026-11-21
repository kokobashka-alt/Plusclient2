package wtf.onetap.setting;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class ModeSetting extends Setting<String> {
   private final Set<String> modes;
   private Runnable onChange;
   private static int[] valueArray;

   public ModeSetting(String text, String text2, String... stringArray) {
      FloatSetting.m_4_IArr();
      super(text, text2);
      this.onChange = () -> {};
      this.modes = new LinkedHashSet<>();
      Collections.addAll(this.modes, stringArray);
      if (!this.modes.contains(text2)) {
         throw new IllegalArgumentException("default mode '" + text2 + "' not in modes " + this.modes);
      }
   }

   public ModeSetting build() {
      SettingRegistry.register(this);
      return this;
   }

   public ModeSetting visibleWhen(BooleanSupplier booleanSupplier) {
      super.visibleWhen(booleanSupplier);
      return this;
   }

   public void setValue(String text) {
      if (!this.modes.contains(text)) {
         throw new IllegalArgumentException("unknown mode '" + text + "'");
      }

      super.setValue(text);
      this.onChange.run();
   }

   public ModeSetting onChange(Runnable runnable) {
      this.onChange = runnable;
      return this;
   }

   public Set<String> getModes() {
      return this.modes;
   }

   public static void m_2(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_6_IArr() {
      return valueArray;
   }

   static {
      m_2(new int[5]);
   }
}
