package wtf.onetap.funpay;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.onetap.exploit.anydesk.PeerInfo;
import wtf.onetap.util.ChatUtil;

public class FunPayClient {
   private static final Logger LOGGER = LoggerFactory.getLogger("FunDeliver");
   private final List<String> stringList;
   private final Set<String> stringSet;
   private final FunPayHttp funPayHttp;
   private List<PeerInfo> peerInfoList;
   private String text;
   private int value;
   private int value2;

   public FunPayClient(FunPayHttp funPayHttp2) {
      this.stringList = new ArrayList<>();
      this.stringSet = new HashSet<>();
      this.text = Integer.toHexString((int)(Math.random() * 2.1474836E9F));
      this.value = -1;
      this.value2 = -1;
      this.funPayHttp = Objects.requireNonNull(funPayHttp2);
   }

   public List<PeerInfo> getPeerInfoList() {
      return this.peerInfoList;
   }

   public JsonObject m_2_JsonObject() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("type", "orders_counters");
      jsonObject.addProperty("id", this.funPayHttp.m_1_long());
      jsonObject.addProperty("tag", this.text);
      jsonObject.addProperty("data", false);
      return jsonObject;
   }

   public void m_1_void(JsonObject jsonObject) {
      try {
         this.text = jsonObject.get("tag").getAsString();
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("data");
         int value3 = jsonObject2 != null && jsonObject2.has("buyer") && !jsonObject2.get("buyer").isJsonNull() ? jsonObject2.get("buyer").getAsInt() : -1;
         int value4 = jsonObject2 != null && jsonObject2.has("seller") && !jsonObject2.get("seller").isJsonNull() ? jsonObject2.get("seller").getAsInt() : -1;
         if (value3 != this.value || value4 != this.value2) {
            LOGGER.info("orders counter changed: buyer={} seller={} (was {}/{})", new Object[]{value3, value4, this.value, this.value2});
            if (this.funPayHttp.m_8_boolean()) {
               ChatUtil.log("§7[FD] Счётчик заказов изменился: покупок=" + value3 + ", продаж=" + value4);
            }

            this.value = value3;
            this.value2 = value4;
            this.m_2_void();
         }
      } catch (Exception e) {
         LOGGER.warn("counters error", e);
         if (this.funPayHttp.m_8_boolean()) {
            ChatUtil.log("§c[Fun Deliver] Ошибка счётчика заказов: §f" + e);
         }
      }
   }

   public boolean m_0_boolean(String text2) {
      return this.stringSet.contains(text2.toLowerCase(Locale.ROOT));
   }

   private void m_2_void() {
      var peerInfoList2 = this.m_2_List();
      if (peerInfoList2 != null) {
         List list = this.peerInfoList;
         this.peerInfoList = peerInfoList2;
         if (list != null) {
            for (PeerInfo peerInfo : (Iterable<PeerInfo>)list) {
               if (peerInfoList2.stream().noneMatch(peerInfo2 -> peerInfo2.m_1_b_String().equals(peerInfo.m_1_b_String()))) {
                  this.stringSet.remove(peerInfo.m_1___String().toLowerCase(Locale.ROOT));
                  if (this.funPayHttp.getFunPayChat().getFunPayParserList() != null) {
                     this.funPayHttp
                        .getFunPayChat()
                        .getFunPayParserList()
                        .stream()
                        .filter(funPayParser -> funPayParser.getText2().equalsIgnoreCase(peerInfo.m_1___String()))
                        .findFirst()
                        .ifPresent(funPayParser2 -> {
                           funPayParser2.setC2((FunPayParser.ModeKind)null);
                           funPayParser2.setFlag(false);
                        });
                  }
               }
            }
         }

         for (PeerInfo peerInfo3 : peerInfoList2) {
            if (!this.stringList.contains(peerInfo3.m_1_b_String())) {
               this.stringList.add(peerInfo3.m_1_b_String());
               long longValue = peerInfo3.m_5_o() * 1000000L;
               long longValue2 = this.funPayHttp.m_5_long();
               if (longValue2 >= 0L && longValue > longValue2) {
                  LOGGER.info("insufficient balance for order #{}: have {} coins, need {} -> immediate refund", new Object[]{peerInfo3.m_1_b_String(), longValue2, longValue});
                  this.funPayHttp.m_3_void(peerInfo3.m_1_b_String());
                  this.funPayHttp.sendChatMessage(peerInfo3.m_13(), "❌ У бота не хватает валюты на балансе, приносим свои извинения!\n\n↩️ Средства возвращены.");
                  if (this.funPayHttp.m_8_boolean()) {
                     String text2 = peerInfo3.m_1_b_String();
                     long longValue3 = longValue;
                     long longValue4 = longValue2;
                     ChatUtil.log("§e[Fun Deliver] Недостаточно монет (§f" + longValue4 + "§e < §f" + longValue3 + "§e) — заказ §f#" + text2 + "§e возвращён сразу.");
                  }
               } else if (peerInfo3.m_5_o() < this.funPayHttp.m_6_float()) {
                  LOGGER.info("order #{} below min ({} < {}) -> refund", new Object[]{peerInfo3.m_1_b_String(), peerInfo3.m_5_o(), this.funPayHttp.m_6_float()});
                  this.funPayHttp.m_3_void(peerInfo3.m_1_b_String());
                  this.funPayHttp.sendChatMessage(peerInfo3.m_13(), "❌ Минимальная сумма заказа — " + (int)this.funPayHttp.m_6_float() + "кк. Средства возвращены, приносим извинения!");
                  if (this.funPayHttp.m_8_boolean()) {
                     ChatUtil.log("§e[Fun Deliver] Заказ §f" + peerInfo3.m_1_b_String() + "§e ниже минимума — средства возвращены.");
                  }
               } else {
                  LOGGER.info(
                     "greeting order #{} buyer={} chat={} count={}", new Object[]{peerInfo3.m_1_b_String(), peerInfo3.m_1___String(), peerInfo3.m_13(), peerInfo3.m_5_o()}
                  );
                  this.funPayHttp
                     .sendChatMessage(
                        peerInfo3.m_13(),
                        "\ud83e\uddf8 Для начала выдачи, пожалуйста, укажите ваш игровой никнейм:\n\nℹ️ Если заказ был создан по ошибке, напишите «Отмена» для возврата средств."
                     );
                  this.stringSet.add(peerInfo3.m_1___String().toLowerCase(Locale.ROOT));
                  if (this.funPayHttp.getFunPayChat().getFunPayParserList() != null) {
                     this.funPayHttp
                        .getFunPayChat()
                        .getFunPayParserList()
                        .stream()
                        .filter(funPayParser3 -> funPayParser3.getText2().equalsIgnoreCase(peerInfo3.m_1___String()))
                        .filter(funPayParser4 -> funPayParser4.getC2() == null)
                        .findFirst()
                        .ifPresent(funPayParser5 -> funPayParser5.setC2(FunPayParser.ModeKind.NICKNAME));
                  }

                  if (this.funPayHttp.m_8_boolean()) {
                     ChatUtil.log("§a[Fun Deliver] Новый заказ §f" + peerInfo3.m_1_b_String() + "§a от §f" + peerInfo3.m_1___String() + "§a — отправил приветствие.");
                  }
               }
            }
         }
      }
   }

   public PeerInfo findPeer(String text2) {
      return this.peerInfoList == null ? null : this.peerInfoList.stream().filter(peerInfo -> peerInfo.m_1___String().equalsIgnoreCase(text2)).findFirst().orElse(null);
   }

   private List<PeerInfo> m_2_List() {
      ArrayList list = new ArrayList();

      try {
         int value3 = 0;

         for (Element element : this.funPayHttp.get("https://funpay.com/orders/trade").parse().select(".tc-item")) {
            value3++;

            try {
               String text2 = Objects.requireNonNull(element.selectFirst(".tc-status")).text();
               String text3 = Objects.requireNonNull(element.selectFirst(".order-desc .text-muted")).text();
               if (text2.equalsIgnoreCase("Оплачен") && text3.contains("Валюта")) {
                  String text4 = Objects.requireNonNull(element.selectFirst(".tc-order")).text();
                  String text5 = Objects.requireNonNull(element.selectFirst(".order-desc > div")).text();
                  Element element2 = Objects.requireNonNull(element.selectFirst(".media-user-name > span"));
                  String text6 = element2.text();
                  long longValue = this.parseDigits(element2.attr("data-href"));
                  String text7 = Objects.requireNonNull(element.selectFirst(".tc-price")).ownText().replace(" ", "").replace(" ", "").replace(",", ".").trim();
                  double doubleValue = Double.parseDouble(text7);
                  String text8 = Objects.requireNonNull(element.selectFirst(".tc-price > span")).text();
                  int value4 = 1;
                  String[] stringArray = text5.split(",");
                  if (stringArray.length > 1) {
                     String text9 = stringArray[stringArray.length - 1].trim();
                     if (text9.contains("шт.")) {
                        value4 = Integer.parseInt(text9.split("шт.")[0].trim());
                     }
                  }

                  double doubleValue2 = doubleValue / value4;
                  boolean flag = Math.abs(doubleValue2 - this.funPayHttp.m_1_float()) <= 0.010000003747573418;
                  LOGGER.info(
                     "order row: #{} buyer={} price={} count={} perUnit={} trigger={} -> {}",
                     new Object[]{text4, text6, doubleValue, value4, String.format("%.3f", doubleValue2), this.funPayHttp.m_1_float(), flag ? "TAKE" : "SKIP(price)"}
                  );
                  if (this.funPayHttp.m_8_boolean()) {
                     String text10 = String.format("%.2f", doubleValue2);
                     float floatValue = this.funPayHttp.m_1_float();
                     String text11 = flag ? "§aвзят" : "§cскип: не совпала цена";
                     float floatValue2 = floatValue;
                     String text12 = text10;
                     int value5 = value4;
                     double doubleValue3 = doubleValue;
                     String text13 = text6;
                     String text14 = text4;
                     ChatUtil.log(
                        "§7[FD] Заказ §f"
                           + text14
                           + "§7 от §f"
                           + text13
                           + "§7: цена §f"
                           + doubleValue3
                           + "§7 за §f"
                           + value5
                           + " шт.§7 (§f"
                           + text12
                           + "§7/шт, триггер §f"
                           + floatValue2
                           + "§7) → "
                           + text11
                     );
                  }

                  if (flag) {
                     list.add(new PeerInfo(text4, text3, text5, text6, longValue, this.m_4_String(longValue), doubleValue, text8, value4));
                  }
               } else if (this.funPayHttp.m_8_boolean() && value3 <= 12) {
                  ChatUtil.log("§8[FD] Строка: " + text2 + " | " + text3.replaceAll("\\s+", " ").trim());
               }
            } catch (Exception e) {
               if (this.funPayHttp.m_8_boolean()) {
                  ChatUtil.log("§c[FD] Строка заказа не разобрана: §f" + e);
               }
            }
         }

         if (this.funPayHttp.m_8_boolean()) {
            ChatUtil.log("§7[FD] Страница заказов: строк=" + value3 + ", подходящих=" + list.size());
         }

         LOGGER.info("orders page parsed: rows={} matched={}", value3, list.size());
         return list;
      } catch (Exception e2) {
         LOGGER.warn("orders page failed", e2);
         if (this.funPayHttp.m_8_boolean()) {
            ChatUtil.log("§c[Fun Deliver] Не удалось получить список заказов: §f" + e2);
         }

         return null;
      }
   }

   private long parseDigits(String text2) {
      String text3 = text2 == null ? "" : text2.replaceAll("[^0-9]", "");
      return text3.isEmpty() ? 0L : Long.parseLong(text3);
   }

   private String m_4_String(long longValue) {
      long longValue2 = this.funPayHttp.m_1_long();
      long longValue3 = Math.max(longValue2, longValue);
      long longValue4 = Math.min(longValue2, longValue);
      return "users-" + longValue4 + "-" + longValue3;
   }
}
