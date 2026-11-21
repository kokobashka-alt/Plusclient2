package wtf.onetap.setting;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class MultiModeSetting extends Setting<Set<String>> {
   private final Set<String> options;
   private static String[] textArray;

   public MultiModeSetting(String text, String text2, String... stringArray) {
      FloatSetting.m_4_IArr();
      super(text, new LinkedHashSet<>(Set.of(text2)));
      this.options = new LinkedHashSet<>(stringArray.length);
      Collections.addAll(this.options, stringArray);
   }

   public MultiModeSetting build() {
      SettingRegistry.register(this);
      return this;
   }

   public MultiModeSetting visibleWhen(BooleanSupplier booleanSupplier) {
      super.visibleWhen(booleanSupplier);
      return this;
   }

   public Set<String> getOptions() {
      return this.options;
   }

   public boolean isSelected(String text) {
      return ((Set)super.value).contains(text);
   }

   public static void m_1(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_7_StringArr() {
      return textArray;
   }
}
