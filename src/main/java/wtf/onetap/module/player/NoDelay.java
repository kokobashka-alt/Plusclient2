
package wtf.onetap.module.player;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.MultiModeSetting;

@ModuleInformation(
    name = "NoDelay",
    description = "Убирает задержку прыжка и использования предметов",
    category = Category.PLAYER
)
public class NoDelay extends Module {
    public final MultiModeSetting multiModeSetting;
    public final FloatSetting floatSetting;

    public NoDelay() {
        super();
        this.multiModeSetting = new MultiModeSetting(
            "Режимы", "Прыжок", "Прыжок", "ПКМ", "Опыт"
        ).build();

        this.floatSetting = new FloatSetting(
            "Задержка опыта", 0.0, 0.0, 100.0, 1.0
        ).visibleWhen(() -> this.multiModeSetting.isSelected("Опыт")).build();
    }
}
