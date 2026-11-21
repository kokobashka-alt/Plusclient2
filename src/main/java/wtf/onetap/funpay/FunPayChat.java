package wtf.onetap.funpay;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.onetap.exploit.anydesk.PeerInfo;
import wtf.onetap.mixin.render.PlayerTabOverlayAccessor;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

public class FunPayChat implements MinecraftAccess {
   private static final Logger LOGGER = LoggerFactory.getLogger("FunDeliver");
   private final FunPayHttp funPayHttp;
   private final Map<String, Long> stringMap;
   private String text;
   private List<FunPayParser> funPayParserList;

   public FunPayChat(FunPayHttp funPayHttp2) {
      this.stringMap = new HashMap<>();
      this.text = Integer.toHexString((int)(Math.random() * 2.1474836E9F));
      this.funPayHttp = Objects.requireNonNull(funPayHttp2);
   }

   public List<FunPayParser> getFunPayParserList() {
      return this.funPayParserList;
   }

   public void setFunPayParserList(List<FunPayParser> funPayParserList2) {
      this.funPayParserList = funPayParserList2;
   }

   public JsonObject m_7_JsonObject() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("type", "chat_bookmarks");
      jsonObject.addProperty("id", this.funPayHttp.m_1_long());
      jsonObject.addProperty("tag", this.text);
      jsonObject.addProperty("data", false);
      return jsonObject;
   }

   public void m_1_void(JsonObject jsonObject) {
      try {
         this.text = jsonObject.get("tag").getAsString();
         ArrayList<FunPayParser> funPayParserList2 = new ArrayList<>();

         for (Element element : Jsoup.parse(jsonObject.getAsJsonObject("data").get("html").getAsString()).select(".contact-item")) {
            Element element2 = element.selectFirst(".media-user-name");
            Element element3 = element.selectFirst(".contact-item-message");
            if (element2 != null && element3 != null) {
               funPayParserList2.add(
                  new FunPayParser(
                     element.hasAttr("data-id") ? element.attr("data-id") : "",
                     element.hasAttr("data-node-msg") ? Long.parseLong(element.attr("data-node-msg")) : 0L,
                     element2.text(),
                     element3.text()
                  )
               );
            }
         }

         if (funPayParserList2.isEmpty()) {
            return;
         }

         List list = this.funPayParserList;
         this.funPayParserList = funPayParserList2;
         LOGGER.info("chat bookmarks: {} chats (previous={})", funPayParserList2.size(), list == null ? "none" : list.size());
         if (this.funPayHttp.m_8_boolean() && (list == null || list.size() != funPayParserList2.size())) {
            int value = funPayParserList2.size();
            String text2 = list == null ? " §7(первая загрузка)" : " §7(было " + list.size() + ")";
            int value2 = value;
            ChatUtil.log("§7[FD] Чатов в закладках: §f" + value2 + text2);
         }

         if (list == null) {
            return;
         }

         for (FunPayParser funPayParser : funPayParserList2) {
            FunPayParser funPayParser2 = (FunPayParser)list.stream().filter(object -> ((wtf.onetap.funpay.FunPayParser)object).getText().equals(funPayParser.getText())).findFirst().orElse(null);
            if (funPayParser2 != null) {
               funPayParser.setC2(funPayParser2.getC2());
               funPayParser.setText4(funPayParser2.getText4());
               funPayParser.setTimeHelper(funPayParser2.getTimeHelper());
               funPayParser.setFlag(funPayParser2.isFlag());
               if (funPayParser.getLongValue() > funPayParser2.getLongValue()) {
                  this.m_7(funPayParser, funPayParser2.getLongValue());
               }
            } else {
               this.m_5(funPayParser);
            }
         }
      } catch (Exception e) {
         LOGGER.warn("chat bookmarks error", e);
      }
   }

   private void m_7(FunPayParser funPayParser, long longValue) throws java.io.IOException {
      JsonObject jsonObject = JsonParser.parseString(this.funPayHttp.m_8_Response(funPayParser.getText()).body()).getAsJsonObject();

      for (JsonElement jsonElement : jsonObject.getAsJsonArray("objects")) {
         JsonObject jsonObject2 = jsonElement.getAsJsonObject();
         if ("chat_node".equalsIgnoreCase(jsonObject2.get("type").getAsString())) {
            JsonObject jsonObject3 = jsonObject2.getAsJsonObject("data");
            if (jsonObject3 != null && jsonObject3.has("messages") && !jsonObject3.get("messages").isJsonNull()) {
               for (JsonElement jsonElement2 : jsonObject3.getAsJsonArray("messages")) {
                  JsonObject jsonObject4 = jsonElement2.getAsJsonObject();
                  if (jsonObject4.get("id").getAsLong() > longValue
                     && !jsonObject4.get("author").getAsString().equals(this.funPayHttp.getC1().text)
                     && !jsonObject4.get("author").getAsString().equals("0")) {
                     this.m_9(funPayParser, jsonObject4);
                  }
               }
            }
         }
      }
   }

   private void m_5(FunPayParser funPayParser) throws java.io.IOException {
      JsonObject jsonObject = JsonParser.parseString(this.funPayHttp.m_8_Response(funPayParser.getText()).body()).getAsJsonObject();
      JsonObject jsonObject2 = null;

      for (JsonElement jsonElement : jsonObject.getAsJsonArray("objects")) {
         JsonObject jsonObject3 = jsonElement.getAsJsonObject();
         if ("chat_node".equalsIgnoreCase(jsonObject3.get("type").getAsString())) {
            JsonObject jsonObject4 = jsonObject3.getAsJsonObject("data");
            if (jsonObject4 != null && jsonObject4.has("messages") && !jsonObject4.get("messages").isJsonNull()) {
               for (JsonElement jsonElement2 : jsonObject4.getAsJsonArray("messages")) {
                  JsonObject jsonObject5 = jsonElement2.getAsJsonObject();
                  if (!jsonObject5.get("author").getAsString().equals(this.funPayHttp.getC1().text) && !jsonObject5.get("author").getAsString().equals("0")) {
                     jsonObject2 = jsonObject5;
                  }
               }
            }
         }
      }

      if (jsonObject2 != null) {
         LOGGER.info("new chat {} appeared, processing last message", funPayParser.getText2());
         this.m_9(funPayParser, jsonObject2);
      }
   }

   public void m_2_void() {
      if (this.funPayParserList != null) {
         for (FunPayParser funPayParser : this.funPayParserList) {
            if (funPayParser.getC2() == FunPayParser.ModeKind.DELIVERY && funPayParser.getTimeHelper().hasElapsed(20000L)) {
               funPayParser.setC2((FunPayParser.ModeKind)null);
               LOGGER.info("delivery timeout for {}, asking nickname again", funPayParser.getText2());
               this.funPayHttp.sendChatMessage(funPayParser.getText(), "⌛ Время вышло! Укажите ваш игровой никнейм заново.");
            }
         }
      }
   }

   private void m_9(FunPayParser funPayParser, JsonObject jsonObject) {
      Element element = Jsoup.parse(jsonObject.get("html").getAsString()).selectFirst(".chat-msg-text");
      String text2 = element != null ? element.text() : "";
      LOGGER.info("message from {} (stage={}): {}", new Object[]{funPayParser.getText2(), funPayParser.getC2(), text2});
      if (this.funPayHttp.m_8_boolean()) {
         ChatUtil.log("§7[FD] Сообщение от §f" + funPayParser.getText2() + "§7: §f" + text2);
      }

      String text3 = text2.replaceAll("[^a-zA-Zа-яА-Я]", "").toLowerCase();
      PeerInfo peerInfo = this.funPayHttp.getFunPayClient().findPeer(funPayParser.getText2());
      if (funPayParser.isFlag()) {
         LOGGER.info("ignoring message from {} — already delivered", funPayParser.getText2());
      } else {
         if (funPayParser.getC2() == null && peerInfo != null && this.funPayHttp.getFunPayClient().m_0_boolean(funPayParser.getText2())) {
            funPayParser.setC2(FunPayParser.ModeKind.NICKNAME);
         }

         if (peerInfo == null && funPayParser.getC2() == null) {
            long longValue = System.currentTimeMillis();
            Long longObj = this.stringMap.get(funPayParser.getText());
            if (longObj == null || longValue - longObj >= 30000L) {
               this.stringMap.put(funPayParser.getText(), longValue);
               LOGGER.info("no-order greeting sent to {}", funPayParser.getText2());
               this.funPayHttp
                  .sendChatMessage(
                     funPayParser.getText(),
                     "\ud83d\udc4b Здравствуйте! Это бот выдачи валюты от — MOONWARD!\n\n\ud83d\udcb0 Оплатите заказ и следуйте инструкции — всё происходит мгновенно.\n\n\ud83d\uded2 Минимальная сумма заказа — "
                        + (int)this.funPayHttp.m_6_float()
                        + "кк."
                  );
            }
         } else {
            if (peerInfo == null || funPayParser.getC2() != FunPayParser.ModeKind.NICKNAME || !text3.equals("отмена") && !text3.equals("otmena")) {
               if (funPayParser.getC2() == FunPayParser.ModeKind.NICKNAME) {
                  String text4 = text2.replaceAll("[^a-zA-Z0-9_]", "");
                  funPayParser.setText4(text4);
                  funPayParser.setC2(FunPayParser.ModeKind.CONFIRM_NICKNAME);
                  this.m_1(funPayParser, text4, peerInfo);
               } else if (funPayParser.getC2() == FunPayParser.ModeKind.DELIVERY) {
                  String text5 = text2.replaceAll("[^a-zA-Z0-9_]", "");
                  if (!text5.isEmpty()) {
                     funPayParser.setText4(text5);
                     funPayParser.setC2(FunPayParser.ModeKind.CONFIRM_NICKNAME);
                     this.m_1(funPayParser, text5, peerInfo);
                  }
               } else if (funPayParser.getC2() == FunPayParser.ModeKind.CONFIRM_NICKNAME) {
                  if (text3.equals("да") || text3.equals("da") || text2.contains("✗")) {
                     this.funPayHttp
                        .sendChatMessage(
                           funPayParser.getText(),
                           "✅ Отлично! Данные подтверждены.\n\n\ud83d\udfe2 Для получения валюты зайдите на Анархию: "
                              + this.m_1_int()
                              + "\n\n⚡ У вас есть 20 секунд для получения валюты, иначе потребуется указать никнейм заново."
                        );
                     funPayParser.setC2(FunPayParser.ModeKind.DELIVERY);
                     funPayParser.getTimeHelper().reset();
                     if (this.funPayHttp.m_8_boolean()) {
                        String text6 = funPayParser.getText2();
                        String text7 = funPayParser.getText4();
                        String text8 = peerInfo != null ? " (" + peerInfo.m_5_o() + " шт.)" : "";
                        String text9 = text7;
                        String text10 = text6;
                        ChatUtil.log("§a[Fun Deliver] §f" + text10 + "§a подтвердил ник §f" + text9 + "§a — выдайте валюту" + text8 + ".");
                     }
                  } else if (text3.equals("нет") || text3.equals("net") || text2.contains("✓")) {
                     funPayParser.setC2(FunPayParser.ModeKind.NICKNAME);
                     this.funPayHttp.sendChatMessage(funPayParser.getText(), "\ud83e\uddf8 Пожалуйста, укажите ваш игровой никнейм:");
                  }
               }
            } else {
               funPayParser.setC2((FunPayParser.ModeKind)null);
               this.funPayHttp.m_3_void(peerInfo.m_1_b_String());
               this.funPayHttp.sendChatMessage(funPayParser.getText(), "↩️ Заказ отменён, средства возвращены!");
               if (this.funPayHttp.m_8_boolean()) {
                  ChatUtil.log("§e[Fun Deliver] §f" + funPayParser.getText2() + "§e отменил заказ — средства возвращены.");
               }
            }
         }
      }
   }

   private void m_1(FunPayParser funPayParser, String text2, PeerInfo peerInfo) {
      this.funPayHttp
         .sendChatMessage(
            funPayParser.getText(),
            "✅ Отлично! Давайте проверим данные:\n\n\ud83d\udcdd Ваш никнейм: "
               + text2
               + "\n\ud83d\udce6 Количество: "
               + (peerInfo != null ? peerInfo.m_5_o() : 0)
               + " шт.\n\n✔️ Всё верно? Напишите «Да»\n✏️ Нужно исправить? Напишите «Нет»"
         );
   }

   private int m_1_int() {
      try {
         if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null) {
            PlayerTabOverlay playerTabOverlay = MinecraftAccess.MINECRAFT.gui.hud.getTabList();
            if (playerTabOverlay == null) {
               return -1;
            }

            Component component = ((PlayerTabOverlayAccessor)playerTabOverlay).onetap$getHeader();
            if (component == null) {
               return -1;
            }

            String text2 = ChatFormatting.stripFormatting(component.getString());
            return text2 != null && text2.contains("Анархия-") ? Integer.parseInt(text2.split("Анархия-")[1].trim()) : -1;
         } else {
            return -1;
         }
      } catch (Exception e) {
         return -1;
      }
   }
}
