package wtf.onetap.command;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import wtf.onetap.mixin.render.PlayerTabOverlayAccessor;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.ServerDetector;

public class RctCommand implements Command {
   private static int value;

   @Override
   public String getText2() {
      return "rct";
   }

   @Override
   public String getText3() {
      return "Перезаходит на анархию";
   }

   @Override
   public String getUsage() {
      return "";
   }

   @Override
   public void showHelp(CommandHelp commandHelp) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
         ChatUtil.log("§cИгрок не найден. Подождите загрузки мира.");
      } else {
         String text = getServerName(mc);
         if (text.contains("reallyworld")) {
            this.m_8(commandHelp);
         } else if (!text.contains("spookytime") && !text.contains("funtime")) {
            ChatUtil.log("§c.rct работает только на ReallyWorld, SpookyTime и FunTime.");
         } else {
            int value2 = m_7(mc);
            if (value2 == -1) {
               ChatUtil.log("§cНе удалось определить номер анархии.");
            } else {
               ChatUtil.log(String.format("§7Перезаход на §fАнархия-%d§7...", value2));
               this.reconnect(mc, value2);
            }
         }
      }
   }

   private void m_8(CommandHelp commandHelp) {
      ServerDetector serverDetector = ServerDetector.SERVER_DETECTOR;
      if (commandHelp.matches("grief")) {
         int value2 = commandHelp.getStringList().isEmpty() ? -1 : parseInt(commandHelp.getStringList().get(0));
         if (value2 >= 1 && value2 <= 56) {
            serverDetector.m_0_boolean(value2);
            ChatUtil.log(value2 + "");
         } else {
            ChatUtil.log("§cИспользование: .rct grief <1-56>");
         }
      } else {
         ServerDetector.DataRecord c0 = ServerDetector.getCurrent();
         if (c0 != null) {
            if (!serverDetector.m_7(c0)) {
               ChatUtil.log(c0.m_3w() + "");
            } else {
               ChatUtil.log(c0.m_3_q().name() + c0.m_3w());
            }
         } else {
            int value3 = ServerDetector.getAnarchyNumber();
            if (value3 == -1) {
               ChatUtil.log("Гриф не найден!");
            } else {
               serverDetector.m_0_boolean(value3);
               ChatUtil.log(value3 + "");
            }
         }
      }
   }

   private static int parseInt(String text) {
      try {
         return Integer.parseInt(text);
      } catch (NumberFormatException numberFormatException) {
         return -1;
      }
   }

   private static String getServerName(Minecraft mc) {
      ServerData serverData = mc.getCurrentServer();
      if (serverData == null && mc.getConnection() != null) {
         serverData = mc.getConnection().getServerData();
      }

      return serverData != null && serverData.ip != null ? serverData.ip.toLowerCase(Locale.ROOT) : "";
   }

   private void reconnect(Minecraft mc, int value2) {
      CompletableFuture.runAsync(() -> {
         mc.execute(() -> {
            if (mc.getConnection() != null) {
               mc.getConnection().sendCommand("hub");
            }
         });

         try {
            TimeUnit.MILLISECONDS.sleep(900L);
         } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
         }

         mc.execute(() -> {
            if (mc.getConnection() != null) {
               mc.getConnection().sendCommand("an" + value2);
            }
         });
      });
   }

   public static int m_7(Minecraft mc) {
      if (mc.gui != null && mc.gui.hud != null) {
         PlayerTabOverlay playerTabOverlay = mc.gui.hud.getTabList();
         if (playerTabOverlay == null) {
            return -1;
         }

         Component component = ((PlayerTabOverlayAccessor)playerTabOverlay).onetap$getHeader();
         if (component == null) {
            return -1;
         }

         String text = ChatFormatting.stripFormatting(component.getString());
         if (text != null && !text.trim().isEmpty()) {
            String text2 = text.toLowerCase();
            int value2 = text2.indexOf("анархия-");
            if (value2 == -1) {
               return -1;
            }

            String text3 = text.substring(value2 + "анархия-".length());
            String text4 = text3.replaceAll("[^0-9]", "");
            if (text4.isEmpty()) {
               return -1;
            }

            try {
               return Integer.parseInt(text4);
            } catch (NumberFormatException numberFormatException) {
               return -1;
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static void m_0_void(int value2) {
      value = value2;
   }

   public static int m_4_int() {
      return value;
   }

   public static int m_1_int() {
      return 70;
   }

   static {
      m_0_void(0);
   }
}
