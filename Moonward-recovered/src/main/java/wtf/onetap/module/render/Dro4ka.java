package wtf.onetap.module.render;

import net.minecraft.world.entity.player.Player;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Dro4ka", description = "Добавляет кастомную анимацию игрокам", category = Category.RENDER)
public class Dro4ka extends Module {
   private final BooleanSetting onlyFriends = new BooleanSetting("Только друзья", false).build();
   private final BooleanSetting all = new BooleanSetting("Все", false).visibleWhen(this.onlyFriends::getValue).build();

   public boolean m_2_boolean(Player player) {
      if (player == null) {
         return false;
      } else if (player == MinecraftAccess.MINECRAFT.player) {
         return true;
      } else if ((Boolean)this.all.getValue()) {
         return true;
      } else {
         return this.onlyFriends.getValue() ? Onetap.FRIEND_MANAGER.isFriend(player.getName().getString()) : false;
      }
   }

   public boolean m_5_boolean() {
      return (Boolean)this.all.getValue() || (Boolean)this.onlyFriends.getValue();
   }
}
