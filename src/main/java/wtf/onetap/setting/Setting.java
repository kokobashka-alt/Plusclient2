package wtf.onetap.setting;

import java.util.function.BooleanSupplier;

public abstract class Setting<T> {
   private final String name;
   public T value;
   private BooleanSupplier visibility;
   private static String text;

   public Setting(String text2, T tt) {
      this.visibility = () -> true;
      this.name = text2;
      this.value = (T)tt;
   }

   public String getName() {
      return this.name;
   }

   public T getValue() {
      return this.value;
   }

   public void setValue(T tt) {
      this.value = (T)tt;
   }

   public boolean isVisible() {
      return this.visibility.getAsBoolean();
   }

   public Setting<T> visibleWhen(BooleanSupplier booleanSupplier) {
      this.visibility = booleanSupplier;
      return this;
   }

   public abstract Setting<T> build();

   public static void m_7_void(String text2) {
      text = text2;
   }

   public static String m_1_String() {
      return text;
   }
}
