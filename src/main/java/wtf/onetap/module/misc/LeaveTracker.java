package wtf.onetap.module.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.RemoveEntitiesEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "LeaveTracker", description = "Отслеживает координаты, на которых пропадают дальние игроки", category = Category.MISC)
public class LeaveTracker extends Module {
   @EventTarget
   private void onRemoveEntities(RemoveEntitiesEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (MinecraftAccess.MINECRAFT.level.getEntity(event.getValue()) instanceof AbstractClientPlayer abstractClientPlayer && !(abstractClientPlayer instanceof LocalPlayer)) {
            if (!(MinecraftAccess.MINECRAFT.player.distanceTo(abstractClientPlayer) < Float.intBitsToFloat((int)4602538344809365504L))) {
               String text = abstractClientPlayer.getGameProfile().name();
               if (text != null && !text.isBlank() && !text.contains("CIT-")) {
                  PlayerTeam playerTeam = abstractClientPlayer.getTeam();
                  MutableComponent mutableComponent = Component.empty();
                  if (playerTeam != null) {
                     mutableComponent.append(playerTeam.getPlayerPrefix());
                  }

                  mutableComponent.append(Component.literal(text));
                  if (playerTeam != null) {
                     mutableComponent.append(playerTeam.getPlayerSuffix());
                  }

                  mutableComponent.append(Component.literal(" замечен на координатах ").withStyle(ChatFormatting.GRAY));
                  mutableComponent.append(Component.literal((int)abstractClientPlayer.getX() + " " + (int)abstractClientPlayer.getY() + " " + (int)abstractClientPlayer.getZ()).withStyle(ChatFormatting.WHITE));
                  ChatUtil.m_8_void(mutableComponent);
               }
            }
         }
      }
   }
}
