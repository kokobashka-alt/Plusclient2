package wtf.onetap.module.player;

import java.util.Locale;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.AuctionScanner;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Clan Invest", description = "Авто-инвест в клан", category = Category.PLAYER)
public class ClanInvest extends Module {
   private final TextSetting currencyThreshold = new TextSetting("Порог валюты", "1000000").build();
   private final FloatSetting investPercent = new FloatSetting("Процент инвестиций", 30.0F, 1.0F, 100.0F, 1.0F).build();
   private boolean flag;
   private boolean flag2;
   private long longValue;
   private long longValue2;
   private long longValue3;

   @Override
   public void onEnable() {
      this.reset();
   }

   @Override
   public void onDisable() {
      this.reset();
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.isEnabled()) {
         String text = getChatText(event.getPacket());
         if (text != null && !text.isBlank()) {
            String text2 = text.toLowerCase(Locale.ROOT).replace('ё', 'е');
            if (!text2.contains("/clan create - создать клан ($10000)") && !text2.contains("вы не можете пополнить баланс клана")) {
               if (this.flag2 && text2.contains("пополнил баланс казны")) {
                  this.flag = true;
                  this.flag2 = false;
                  this.longValue = 0L;
                  this.longValue2 = 0L;
               }
            } else {
               this.reset();
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         long longValue4 = System.currentTimeMillis();
         if (this.flag2 && longValue4 > this.longValue2) {
            this.flag2 = false;
            this.longValue = 0L;
            this.longValue2 = 0L;
         }

         int value = this.currencyThreshold.getInt();
         int value2 = ((Float)this.investPercent.getValue()).intValue();
         if (value > 0 && value2 >= 1 && value2 <= 100) {
            long longValue5 = AuctionScanner.m_5_long();
            if (longValue5 >= 0L) {
               if (longValue5 < value) {
                  this.flag = false;
                  this.longValue3 = 0L;
               } else if (!this.flag || longValue5 > this.longValue3) {
                  if (!this.flag2) {
                     long longValue6 = Math.max(1L, Math.min(2147483647L, longValue5 * value2 / 100L));
                     MinecraftAccess.MINECRAFT.getConnection().sendCommand("clan invest " + longValue6);
                     this.longValue = longValue6;
                     this.longValue3 = longValue5;
                     this.flag2 = true;
                     this.longValue2 = longValue4 + 4000L;
                  }
               }
            }
         } else {
            this.setEnabled(false);
         }
      }
   }

   private void reset() {
      this.flag = false;
      this.flag2 = false;
      this.longValue = 0L;
      this.longValue2 = 0L;
      this.longValue3 = 0L;
   }

   private static String getChatText(Packet<?> packet) {
      if (packet instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
         return stripColors(clientboundSystemChatPacket.content().getString());
      } else if (packet instanceof ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {
         return stripColors(clientboundDisguisedChatPacket.message().getString());
      } else if (packet instanceof ClientboundPlayerChatPacket clientboundPlayerChatPacket) {
         return clientboundPlayerChatPacket.unsignedContent() != null ? stripColors(clientboundPlayerChatPacket.unsignedContent().getString()) : stripColors(clientboundPlayerChatPacket.body().content());
      } else {
         return null;
      }
   }

   private static String stripColors(String text) {
      return text == null ? "" : text.replaceAll("§x(§[0-9a-fA-F]){6}", "").replaceAll("§.", "").replace(' ', ' ').replace(' ', ' ').trim();
   }
}
