package wtf.onetap.module.player;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.MultiModeSetting;

@ModuleInformation(name = "NoPush", description = "Отключает отталкивание", category = Category.PLAYER)
public class NoPush extends Module {
   public final MultiModeSetting modes = new MultiModeSetting("Режимы", "Игроки", "Игроки", "Блоки", "Вода", "Удочка").build();
}
