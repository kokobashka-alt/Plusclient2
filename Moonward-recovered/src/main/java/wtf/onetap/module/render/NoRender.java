package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.MultiModeSetting;

@ModuleInformation(name = "NoRender", description = "Убирает различные визуальные эффекты", category = Category.RENDER)
public class NoRender extends Module {
   public final MultiModeSetting modes = new MultiModeSetting(
         "Режимы",
         "Огонь",
         "Огонь",
         "Размытие в воде",
         "Блоки",
         "Камераклип",
         "Тряска",
         "Иссушение",
         "Плохие эффекты",
         "Тьма вардена",
         "Погода",
         "Облака",
         "Тотем-анимация"
      )
      .build();
   private static int value;

   public static void m_2_void(int value2) {
      value = value2;
   }

   public static int m_1_int() {
      return value;
   }

   public static int m_3_int() {
      return 49;
   }

   static {
      m_2_void(0);
   }
}
