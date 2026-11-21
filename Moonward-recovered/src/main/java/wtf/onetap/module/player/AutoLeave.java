package wtf.onetap.module.player;

import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.bot.BotConnection;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoLeave", description = "Автоматически покидает сервер при появлении игрока рядом или низком здоровье", category = Category.PLAYER)
public class AutoLeave extends Module {
   private final ModeSetting trigger = new ModeSetting("Триггер", "Игрок рядом", "Игрок рядом", "ХП").build();
   private final ModeSetting action = new ModeSetting("Действие", "Kick", "Kick", "/hub", "/spawn", "/home").build();
   private final FloatSetting distance = new FloatSetting("Дистанция", 50.0F, 1.0F, 100.0F, 1.0F).visibleWhen(() -> ((String)this.trigger.getValue()).equals("Игрок рядом")).build();
   private final FloatSetting healthThreshold = new FloatSetting("Порог здоровья", 10.0F, 1.0F, 20.0F, 1.0F).visibleWhen(() -> ((String)this.trigger.getValue()).equals("ХП")).build();

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (((String)this.trigger.getValue()).equals("ХП")) {
            float floatValue = MinecraftAccess.MINECRAFT.player.getHealth() + MinecraftAccess.MINECRAFT.player.getAbsorptionAmount();
            if (floatValue <= this.healthThreshold.getFloat()) {
               this.m_9_void(null);
            }
         } else {
            double doubleValue = this.distance.getFloat();
            double doubleValue2 = doubleValue * doubleValue;

            for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
               if (this.m_7(player, doubleValue2)) {
                  this.m_9_void(player);
                  break;
               }
            }
         }
      }
   }

   private boolean m_7(Player player, double doubleValue) {
      if (player != null && player != MinecraftAccess.MINECRAFT.player) {
         if (!player.isAlive() || player.getHealth() <= 0.0F) {
            return false;
         } else if (player.distanceToSqr(MinecraftAccess.MINECRAFT.player) > doubleValue) {
            return false;
         } else {
            String text = player.getGameProfile().name();
            if (!this.m_4_boolean(text)) {
               return false;
            } else {
               return BotConnection.BOT_CONNECTION.m_9_boolean(text) ? false : !Onetap.FRIEND_MANAGER.isFriend(text);
            }
         }
      } else {
         return false;
      }
   }

   private boolean m_4_boolean(String text) {
      if (text == null) {
         return false;
      }

      String text2 = text.trim();
      if (text2.isEmpty()) {
         return false;
      }

      String text3 = text2.toLowerCase(Locale.ROOT);
      return !text3.startsWith("cit-") && !text3.contains("npc");
   }

   private void m_9_void(Player player) {
      VoiceChat.m_9_StringArr();
      String text = (String)this.action.getValue();
      String text2 = player != null
         ? "из-за игрока " + player.getGameProfile().name()
         : "мало здоровья (" + (int)(MinecraftAccess.MINECRAFT.player.getHealth() + MinecraftAccess.MINECRAFT.player.getAbsorptionAmount()) + " HP)";
      if (!"Kick".equalsIgnoreCase(text)) {
         if (!text.isEmpty() && MinecraftAccess.MINECRAFT.getConnection() != null) {
            if (text.startsWith("/")) {
               MinecraftAccess.MINECRAFT.getConnection().sendCommand(text.substring(1));
            } else {
               MinecraftAccess.MINECRAFT.getConnection().sendChat(text);
            }

            ChatUtil.log("AutoLeave: выполнена команда " + text + " " + text2);
         }
      } else if (MinecraftAccess.MINECRAFT.player.connection != null && MinecraftAccess.MINECRAFT.player.connection.getConnection() != null) {
         MinecraftAccess.MINECRAFT.player.connection.getConnection().disconnect(Component.literal("Вы вышли с сервера!\n" + text2));
      }

      this.setEnabled(false);
   }
}
