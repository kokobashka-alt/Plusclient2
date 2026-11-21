package wtf.onetap.module.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.UseItemOnEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "NoPlaceSphere", description = "Не даёт ставить шары", category = Category.PLAYER)
public class NoPlaceSphere extends Module {
   @EventTarget
   private void onUseItemOn(UseItemOnEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (event.getInteractionHand() == InteractionHand.OFF_HAND && MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.PLAYER_HEAD)) {
            event.cancel();
         }
      }
   }
}
