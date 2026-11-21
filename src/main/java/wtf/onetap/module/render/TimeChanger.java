package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;

@ModuleInformation(name = "TimeChanger", description = "Меняет время суток на клиенте", category = Category.RENDER)
public class TimeChanger extends Module {
   private final ModeSetting time = new ModeSetting("Время", "День", "Утро", "День", "Закат", "Ночь", "Полночь", "Кастом").build();
   private final FloatSetting custom = new FloatSetting("Кастом", 6000.0F, 0.0F, 23999.0F, 100.0F).visibleWhen(() -> ((String)this.time.getValue()).equals("Кастом")).build();

   public long adjustTime(long longValue) {
      return longValue - Math.floorMod(longValue, 24000L) + this.m_9_long();
   }

   private long m_9_long() {
      Notifications.m_5_C3_sArr();

      return switch ((String)this.time.getValue()) {
         case "Утро" -> 1000L;
         case "День" -> 6000L;
         case "Закат" -> 12000L;
         case "Ночь" -> 14000L;
         case "Полночь" -> 18000L;
         default -> this.custom.getInt();
      };
   }
}
