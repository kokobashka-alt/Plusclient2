package wtf.onetap.module.pve;

import com.mojang.blaze3d.platform.NativeImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Screenshot;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.AuctionScanner;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.exploit.anydesk.PeerInfo;
import wtf.onetap.funpay.FunPayHttp;
import wtf.onetap.funpay.FunPayParser;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.misc.UnHook;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "FunPay Sell", description = "Автоматическая выдача валюты по заказам FunPay", category = Category.PVE)
public class FunPaySell extends Module {
   private static final Logger LOGGER = LoggerFactory.getLogger("FunDeliver");
   private final TextSetting goldenKey = new TextSetting("Укажите ваш Golden-Key", "").numericOnly().build();
   private final TextSetting itemProcessingPriceTrigger = new TextSetting("Триггер цены обработки товара", "1").build();
   private final TextSetting sellFromAmountMillions = new TextSetting("Продавать от суммы (кк)", "10").build();
   private final BooleanSetting logs = new BooleanSetting("Логи", false).build();
   private final BooleanSetting takeScreenshot = new BooleanSetting("Делать скриншот", true).build();
   private ScheduledExecutorService scheduledExecutorService;
   private FunPayHttp funPayHttp;
   private long longValue;
   private FunPaySell.DataRecord2 c8;
   private FunPaySell.DataRecord c6;
   private volatile long longValue2 = -1L;

   @Override
   public void onEnable() {
      this.longValue = 0L;
      this.c8 = null;
      this.c6 = null;
      LOGGER.info("module enabled, key={}", this.goldenKey.getValue() != null && !((String)this.goldenKey.getValue()).isBlank() ? "set" : "EMPTY");
      if (this.goldenKey.getValue() == null || ((String)this.goldenKey.getValue()).isBlank()) {
         ChatUtil.log("§c[FunPay] Укажите Golden-Key в настройках модуля — без него бот не работает.");
      }

      this.funPayHttp = new FunPayHttp(
         this.goldenKey::getValue,
         () -> (float)this.itemProcessingPriceTrigger.getInt(),
         () -> (float)this.sellFromAmountMillions.getInt(),
         this.logs::getValue,
         () -> this.longValue2,
         this.takeScreenshot::getValue
      );
      this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
         Thread thread = new Thread(runnable, "FunDeliver");
         thread.setDaemon(true);
         return thread;
      });
      this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
         try {
            String text = (String)this.goldenKey.getValue();
            if (text == null || text.isBlank()) {
               return;
            }

            if (!this.funPayHttp.m_4_boolean()) {
               if (System.currentTimeMillis() - this.longValue >= 30000L) {
                  this.longValue = System.currentTimeMillis();
                  if (this.funPayHttp.m_2_boolean()) {
                     if ((Boolean)this.logs.getValue()) {
                        ChatUtil.log("§a[FunPay] Вход выполнен: §f" + this.funPayHttp.getC1().getText2() + "§a. Слежу за заказами...");
                     }
                  } else {
                     ChatUtil.log("§c[FunPay] Не удалось войти в FunPay — проверьте Golden-Key.");
                  }
               }

               return;
            }

            this.funPayHttp.m_1_void();
         } catch (Exception e) {
            LOGGER.warn("poll cycle error", e);
            if ((Boolean)this.logs.getValue()) {
               ChatUtil.log("§c[FunPay] Ошибка: §f" + e);
            }
         }
      }, 0L, 2000L, TimeUnit.MILLISECONDS);
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.funPayHttp != null) {
         this.longValue2 = AuctionScanner.m_5_long();
         if (this.funPayHttp.m_4_boolean() && MinecraftAccess.MINECRAFT.getConnection() != null) {
            var funPayParserList = this.funPayHttp.getFunPayChat().getFunPayParserList();
            if (funPayParserList != null) {
               for (FunPayParser funPayParser : funPayParserList) {
                  if (funPayParser.getC2() == FunPayParser.ModeKind.DELIVERY) {
                     if (funPayParser.getText4() == null || funPayParser.getText4().isBlank()) {
                        funPayParser.setC2((FunPayParser.ModeKind)null);
                     } else if (this.isPlayerOnline(funPayParser.getText4())) {
                        this.m_5(funPayParser);
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      UnHook.m_7_IArr();
      FunPaySell.DataRecord2 c82 = this.c8;
      FunPaySell.DataRecord c62 = this.c6;
      if ((c82 != null || c62 != null) && MinecraftAccess.MINECRAFT.getConnection() != null) {
         long longValue3 = System.currentTimeMillis();
         if (c82 != null && longValue3 > c82.m_36()) {
            this.c8 = null;
            c82 = null;
         }

         if (c62 != null && longValue3 > c62.m_7_k()) {
            this.c6 = null;
            c62 = null;
         }

         if (c82 != null || c62 != null) {
            String text = getRawChatText(event.getPacket());
            if (text != null) {
               String text2 = text.toLowerCase(Locale.ROOT);
               if (c82 != null) {
                  boolean flag = text2.contains("повторно")
                     || text2.contains("ещё раз")
                     || text2.contains("еще раз")
                     || text2.contains("повторите")
                     || text2.contains("подтвердите")
                     || text2.contains("подтверждение");
                  boolean flag2 = text2.contains("pay")
                     || text2.contains("перевод")
                     || text2.contains("монет")
                     || text2.contains("деньг")
                     || text2.contains("команд");
                  if (flag && flag2) {
                     this.c8 = null;
                     LOGGER.info("server asked to confirm pay (\"{}\"), repeating command", text.trim());
                     ChatUtil.log("§e[FunPay] Сервер запросил подтверждение — повторяю команду.");
                     ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
                     long longValue4 = c82.m_3_l();
                     String text3 = c82.m_6_r();
                     clientPacketListener.sendCommand("pay " + text3 + " " + longValue4);
                     return;
                  }

                  if (this.isPaymentMessage(text2, c82.m_6_r())) {
                     this.c8 = null;
                     if (c62 != null) {
                        this.m_5(c62);
                     }

                     return;
                  }
               }

               if (c62 != null && this.isPaymentMessage(text2, c62.m_6_g())) {
                  this.c6 = null;
                  this.m_5(c62);
               }
            }
         }
      }
   }

   private boolean isPaymentMessage(String text, String text2) {
      boolean flag = text.contains("успешно")
         || text.contains("отправлено")
         || text.contains("отправлены")
         || text.contains("переведены")
         || text.contains("вы передали")
         || text.contains("вы отправили");
      boolean flag2 = text.contains(text2.toLowerCase(Locale.ROOT));
      return flag && flag2;
   }

   private void m_5(FunPaySell.DataRecord c62) {
      if (!(Boolean)this.takeScreenshot.getValue()) {
         LOGGER.info("pay success message detected, screenshot disabled for {}", c62.m_6_g());
      } else {
         LOGGER.info("pay success message detected, chat screenshot in 1s for {}", c62.m_6_g());
         if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.schedule(() -> {
               if (this.funPayHttp != null) {
                  MinecraftAccess.MINECRAFT.execute(() -> {
                     try {
                        Screenshot.takeScreenshot(MinecraftAccess.MINECRAFT.gameRenderer.mainRenderTarget(), nativeImage -> {
                           try (nativeImage) {
                              NativeImage nativeImage2 = m_2(nativeImage);
                              Path path = Files.createTempFile("funpay-proof", ".png");
                              nativeImage2.writeToFile(path);
                              nativeImage2.close();
                              byte[] byteArray = Files.readAllBytes(path);
                              Files.deleteIfExists(path);
                              LOGGER.info("chat area screenshot captured ({} bytes), uploading to FunPay chat {}", byteArray.length, c62.m_6_v_String());
                              this.funPayHttp.upload(c62.m_6_v_String(), byteArray, "proof.png");
                              if ((Boolean)this.logs.getValue()) {
                                 ChatUtil.log("§a[FunPay] Скриншот чата отправлен покупателю в чат FunPay.");
                              }
                           } catch (Exception e) {
                              LOGGER.warn("screenshot upload failed", e);
                              ChatUtil.log("§c[FunPay] Не удалось отправить скриншот: §f" + e);
                           }
                        });
                     } catch (Exception e2) {
                        LOGGER.warn("screenshot capture failed", e2);
                     }
                  });
               }
            }, 1L, TimeUnit.SECONDS);
         }
      }
   }

   private static NativeImage m_2(NativeImage nativeImage) {
      int value = nativeImage.getWidth();
      int value2 = nativeImage.getHeight();
      double doubleValue = MinecraftAccess.MINECRAFT.getWindow().getGuiScale();
      double doubleValue2 = MinecraftAccess.MINECRAFT.options.chatScale().get();
      int value3 = Math.max(9, (int)Math.round(9.0 * doubleValue * doubleValue2));
      int value4 = (int)Math.round(40.0 * doubleValue);
      byte byteValue = 5;
      int value5 = value3 * byteValue;
      int value6 = value2 - value4 - value5 + value3 / 2;
      value6 = Math.max(0, Math.min(value6, value2 - value5));
      int value7 = Math.max(1, (int)(value * 0.45));
      NativeImage nativeImage2 = new NativeImage(value7, value5, false);

      for (int i = 0; i < value5; i++) {
         for (int i2 = 0; i2 < value7; i2++) {
            nativeImage2.setPixel(i2, i, nativeImage.getPixel(i2, value6 + i));
         }
      }

      return nativeImage2;
   }

   private static String getRawChatText(Packet<?> packet) {
      if (packet instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
         return clientboundSystemChatPacket.content().getString();
      } else if (packet instanceof ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {
         return clientboundDisguisedChatPacket.message().getString();
      } else if (packet instanceof ClientboundPlayerChatPacket clientboundPlayerChatPacket) {
         return clientboundPlayerChatPacket.unsignedContent() != null ? clientboundPlayerChatPacket.unsignedContent().getString() : clientboundPlayerChatPacket.body().content();
      } else {
         return null;
      }
   }

   private boolean isPlayerOnline(String text) {
      for (PlayerInfo playerInfo : MinecraftAccess.MINECRAFT.getConnection().getOnlinePlayers()) {
         if (playerInfo.getProfile().name().equalsIgnoreCase(text)) {
            return true;
         }
      }

      return false;
   }

   private void m_5(FunPayParser funPayParser) {
      PeerInfo peerInfo = this.funPayHttp.getFunPayClient().findPeer(funPayParser.getText2());
      funPayParser.setC2((FunPayParser.ModeKind)null);
      if (peerInfo == null) {
         LOGGER.warn("delivery skipped: no active order for {}", funPayParser.getText2());
         this.funPayHttp.sendChatMessage(funPayParser.getText(), "❌ Заказ не найден. Обратитесь к продавцу!");
      } else {
         long longValue3 = peerInfo.m_5_o() * 1000000L;
         long longValue4 = AuctionScanner.m_5_long();
         if (longValue4 >= 0L && longValue4 < longValue3) {
            LOGGER.info("insufficient balance: have {} coins, order needs {} (order #{}) -> refund", new Object[]{longValue4, longValue3, peerInfo.m_1_b_String()});
            this.funPayHttp.m_3_void(peerInfo.m_1_b_String());
            this.funPayHttp.sendChatMessage(funPayParser.getText(), "❌ У бота не хватает валюты на балансе, приносим свои извинения!\n\n↩️ Средства возвращены.");
            if ((Boolean)this.logs.getValue()) {
               String text = peerInfo.m_1_b_String();
               long longValue5 = longValue3;
               long longValue6 = longValue4;
               ChatUtil.log("§e[FunPay] Недостаточно монет (§f" + longValue6 + "§e < §f" + longValue5 + "§e) — заказ §f#" + text + "§e возвращён.");
            }
         } else {
            LOGGER.info(
               "player {} joined anarchy, sending coins: /pay {} {} (order #{})", new Object[]{funPayParser.getText4(), funPayParser.getText4(), longValue3, peerInfo.m_1_b_String()}
            );
            ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
            String text2 = funPayParser.getText4();
            long longValue7 = longValue3;
            String text3 = text2;
            clientPacketListener.sendCommand("pay " + text3 + " " + longValue7);
            funPayParser.setFlag(true);
            this.c8 = new FunPaySell.DataRecord2(funPayParser.getText4(), longValue3, System.currentTimeMillis() + 10000L);
            this.c6 = new FunPaySell.DataRecord(funPayParser.getText(), funPayParser.getText4(), longValue3, peerInfo.m_1_b_String(), System.currentTimeMillis() + 20000L);
            this.funPayHttp.sendChatMessage(funPayParser.getText(), "✅ Валюта выдана! Хорошей игры \ud83c\udfae");
            this.funPayHttp.sendChatMessage(funPayParser.getText(), "⭐ Пожалуйста, оставьте отзыв за скорость — это очень помогает нам!");
            if ((Boolean)this.logs.getValue()) {
               int value = peerInfo.m_5_o();
               text2 = funPayParser.getText4();
               String text4 = peerInfo.m_1_b_String();
               String text5 = text2;
               int value2 = value;
               ChatUtil.log("§a[FunPay] Выдал §f" + value2 + " кк§a игроку §f" + text5 + "§a (заказ §f#" + text4 + "§a).");
            }
         }
      }
   }

   @Override
   public void onDisable() {
      if (this.scheduledExecutorService != null) {
         this.scheduledExecutorService.shutdownNow();
         this.scheduledExecutorService = null;
      }

      this.funPayHttp = null;
      this.c8 = null;
      this.c6 = null;
   }

   private record DataRecord(String f_6_v, String f_6_g, long f_7_s, String f_6j, long f_7_k) {
      public String m_6_v_String() {
         return this.f_6_v;
      }

      public String m_6_g() {
         return this.f_6_g;
      }

      public long m_7_s() {
         return this.f_7_s;
      }

      public String m_6j() {
         return this.f_6j;
      }

      public long m_7_k() {
         return this.f_7_k;
      }
   }

   private record DataRecord2(String f_6_r, long f_3_l, long f_36) {
      public String m_6_r() {
         return this.f_6_r;
      }

      public long m_3_l() {
         return this.f_3_l;
      }

      public long m_36() {
         return this.f_36;
      }
   }
}
