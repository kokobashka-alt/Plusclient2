package wtf.onetap.module.combat;

import net.minecraft.world.entity.player.Player;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "NoFriendDamage", description = "Не даёт бить друзей", category = Category.COMBAT)
public class NoFriendDamage extends Module {
   @EventTarget(priority = EventPriority.HIGH)
   private void onAttackEntity(AttackEntityEvent event) {
      if (event.getEntity() instanceof Player player && player != MinecraftAccess.MINECRAFT.player) {
         if (Onetap.FRIEND_MANAGER.isFriend(player.getGameProfile().name()) || Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
            event.cancel();
         }
      }
   }
}
