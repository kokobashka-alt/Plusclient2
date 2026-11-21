package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;

@ModuleInformation(name = "ChunkAnimator", description = "Анимация загрузки чанков: сборка из частиц, подъём", category = Category.RENDER)
public class ChunkAnimator extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Комбо", "Частицы", "Подъём", "Комбо").build();
   private final FloatSetting duration = new FloatSetting("Длительность", 0.8F, 0.1F, 2.0F, 0.1F).build();
   private final ModeSetting particleColor = new ModeSetting("Цвет частиц", "Блок", "Блок", "Белый", "Свой").build();
   private final ColorSetting customColor = new ColorSetting("Свой цвет", ColorUtil.rgba(140, 180, 255, 255)).visibleWhen(() -> ((String)this.particleColor.getValue()).equals("Свой")).build();

   public float getDuration() {
      return this.duration.getFloat();
   }

   public int m_2_int() {
      String text = (String)this.mode.getValue();
      Notifications.m_5_C3_sArr();

      return switch (text) {
         case "Частицы" -> 1;
         case "Подъём" -> 2;
         default -> 3;
      };
   }

   public int m_7_int() {
      return switch ((String)this.particleColor.getValue()) {
         case "Белый" -> 1;
         case "Свой" -> 2;
         default -> 0;
      };
   }

   public int m_8_int() {
      return this.m_7_int() != 2 ? 0 : (Integer)this.customColor.getValue() & 16777215;
   }

   public int m_3_int() {
      return this.m_2_int() << 26 | this.m_7_int() << 24 | this.m_8_int();
   }
}
