package wtf.onetap.module.render;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;

@ModuleInformation(name = "Wings", description = "Рисует анимированные крылья за спиной", category = Category.RENDER)
public class Wings extends Module {
   public final FloatSetting size = new FloatSetting("Размер", 1.0F, 0.75F, 1.5F, 0.05F).build();
   public final BooleanSetting workOnFriends = new BooleanSetting("Работать на друзей", false).build();
}
