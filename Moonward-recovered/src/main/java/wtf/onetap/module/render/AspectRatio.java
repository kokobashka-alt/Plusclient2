package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;

@ModuleInformation(name = "AspectRatio", description = "Изменяет соотношение сторон экрана", category = Category.RENDER)
public class AspectRatio extends Module {
   public final ModeSetting mode = new ModeSetting("Режим", "16:10", "16:10", "4:3", "5:4", "21:9", "Кастом").build();
   public final FloatSetting custom = new FloatSetting("Кастом", 1.78F, 0.5F, 3.0F, 0.01F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Кастом")).build();

   public float m_5_float() {
      Notifications.m_5_C3_sArr();

      return switch ((String)this.mode.getValue()) {
         case "16:10" -> 1.6F;
         case "4:3" -> 1.3333334F;
         case "5:4" -> 1.25F;
         case "21:9" -> 2.3333333F;
         case "Кастом" -> this.custom.getValue();
         default -> 0.0F;
      };
   }
}
