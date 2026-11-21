package wtf.onetap.module.player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.inventory.ContainerInput;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AutoDuel", description = "Сам отправляет запросы на дуэль", category = Category.PLAYER)
public class AutoDuel extends Module {
   private static final Pattern PATTERN = Pattern.compile("^\\w{3,16}$");
   private final ModeSetting mode = new ModeSetting("Режим", "Шары", "Шары", "Щит", "Шипы 3", "Незеритка", "Читерский рай", "Лук", "Классик", "Тотемы", "Нодебафф").build();
   private final Set<String> stringSet = new HashSet<>();
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private final TimeHelper timeHelper3 = new TimeHelper();
   private final TimeHelper timeHelper4 = new TimeHelper();
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;

   @Override
   public void onEnable() {
      this.timeHelper.reset();
      this.timeHelper2.reset();
      this.timeHelper3.reset();
      this.timeHelper4.reset();
      this.stringSet.clear();
      if (MinecraftAccess.MINECRAFT.player != null) {
         this.doubleValue = MinecraftAccess.MINECRAFT.player.getX();
         this.doubleValue2 = MinecraftAccess.MINECRAFT.player.getY();
         this.doubleValue3 = MinecraftAccess.MINECRAFT.player.getZ();
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
         String text = clientboundSystemChatPacket.content().getString();
         if (text.contains("начало") && text.contains("через") && text.contains("секунд!")
            || text.equals("дуэли » во время поединка запрещено использовать команды")) {
            this.setEnabled(false);
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         double doubleValue4 = this.doubleValue - MinecraftAccess.MINECRAFT.player.getX();
         double doubleValue5 = this.doubleValue2 - MinecraftAccess.MINECRAFT.player.getY();
         double doubleValue6 = this.doubleValue3 - MinecraftAccess.MINECRAFT.player.getZ();
         this.doubleValue = MinecraftAccess.MINECRAFT.player.getX();
         this.doubleValue2 = MinecraftAccess.MINECRAFT.player.getY();
         this.doubleValue3 = MinecraftAccess.MINECRAFT.player.getZ();
         if (doubleValue4 * doubleValue4 + doubleValue5 * doubleValue5 + doubleValue6 * doubleValue6 > 250000.0) {
            this.setEnabled(false);
         } else {
            var stringList = this.m_7_List();
            if (this.timeHelper2.hasElapsed(800L * stringList.size())) {
               this.stringSet.clear();
               this.timeHelper2.reset();
            }

            String text = MinecraftAccess.MINECRAFT.player.getGameProfile().name();
            if (this.timeHelper.hasElapsed(2000L)) {
               for (String text2 : stringList) {
                  if (!this.stringSet.contains(text2) && !text2.equals(text)) {
                     MinecraftAccess.MINECRAFT.getConnection().sendCommand("duel " + text2);
                     this.stringSet.add(text2);
                     this.timeHelper.reset();
                     break;
                  }
               }
            }

            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               int value = MinecraftAccess.MINECRAFT.player.containerMenu.containerId;
               String text3 = MinecraftAccess.MINECRAFT.gui.screen().getTitle().getString();
               if (text3.contains("Выбор набора")) {
                  if (this.timeHelper3.hasElapsed(150L)) {
                     InventoryUtil.clickSlot(value, this.m_4_int(), 0, ContainerInput.QUICK_MOVE);
                     this.timeHelper3.reset();
                  }
               } else if ((text3.contains("Настройка поединка") || text3.contains("Подтверждение дуэли")) && this.timeHelper4.hasElapsed(150L)) {
                  InventoryUtil.clickSlot(value, 0, 0, ContainerInput.QUICK_MOVE);
                  this.timeHelper4.reset();
               }
            }
         }
      }
   }

   private int m_4_int() {
      return switch ((String)this.mode.getValue()) {
         case "Щит" -> 0;
         case "Шипы 3" -> 1;
         case "Лук" -> 2;
         case "Тотемы" -> 3;
         case "Нодебафф" -> 4;
         case "Классик" -> 6;
         case "Читерский рай" -> 7;
         case "Незеритка" -> 8;
         default -> 5;
      };
   }

   private List<String> m_7_List() {
      return MinecraftAccess.MINECRAFT.getConnection().getOnlinePlayers().stream().map(playerInfo -> playerInfo.getProfile().name()).filter(text -> PATTERN.matcher(text).matches()).toList();
   }
}
