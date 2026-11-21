package wtf.onetap.module.render;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "BetterMinecraft", description = "Мелкие улучшения интерфейса", category = Category.RENDER)
public class BetterMinecraft extends Module {
   public final BooleanSetting tabAnimation = new BooleanSetting("Анимация таба", true).build();
   public final BooleanSetting tabHighlight = new BooleanSetting("Подсветка в табе", true).build();
   public final BooleanSetting saturationOverHunger = new BooleanSetting("Насыщенность над голодом", false).build();

   public int m_5(PlayerInfo playerInfo, int value) {
      if (this.isEnabled() && (Boolean)this.tabHighlight.getValue()) {
         String text = playerInfo.getProfile().name();
         if (MinecraftAccess.MINECRAFT.player != null && playerInfo.getProfile().id().equals(MinecraftAccess.MINECRAFT.player.getUUID())) {
            return 1616229631;
         }

         if (Onetap.FRIEND_MANAGER.isFriend(text)) {
            return 1618018096;
         }

         Component component = playerInfo.getTeam() != null ? playerInfo.getTeam().getPlayerPrefix() : Component.empty();
         return HUD.m_8(text, component, playerInfo.getGameMode() == GameType.SPECTATOR) ? 1627336261 : value;
      } else {
         return value;
      }
   }
}
