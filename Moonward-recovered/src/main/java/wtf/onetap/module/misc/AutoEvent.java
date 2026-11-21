package wtf.onetap.module.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.command.RctCommand;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.TargetArrow;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.SoundPlayer;

@ModuleInformation(name = "AutoEvent", description = "Ставит GPS на эвенты FunTime/ReallyWorld", category = Category.MISC)
public class AutoEvent extends Module {
   private static final Pattern PATTERN = Pattern.compile("(?:координатах|координаты:)\\s*\\[?(-?\\d+)[\\s,]+(-?\\d+)[\\s,]+(-?\\d+)", 66);
   private static final Pattern PATTERN2 = Pattern.compile("^(?:[‖|┃│▍▌]|[\\p{L} ]+:\\s*\\S)");
   private static final Pattern PATTERN3 = Pattern.compile("^\\[\\d+\\]");
   private static final Pattern PATTERN4 = Pattern.compile("\\[(.+?)\\]");
   private static final Pattern PATTERN5 = Pattern.compile("Уровень\\s+лута:\\s*(.+)", 66);
   private static final Pattern PATTERN6 = Pattern.compile("^(.*?)((?:\\d+\\s*(?:ч|мин|сек)\\S*\\s*)+)$", 66);
   private static final Pattern PATTERN7 = Pattern.compile("(\\d+)\\s*(ч|мин|сек)", 66);
   private static final Pattern PATTERN8 = Pattern.compile("^.*?статус:", 66);
   private static final List<String> STRING_LIST = List.of("адская резня", "сундук смерти", "бикини боттом");
   private static final Pattern PATTERN9 = Pattern.compile(
      "(?:x\\s*[:=]?\\s*)?(-?\\d+)[\\s,;]+(?:(?:y\\s*[:=]?\\s*)?(-?\\d+)[\\s,;]+)?(?:z\\s*[:=]?\\s*)?(-?\\d+)", 2
   );
   private static final List<String> STRING_LIST2 = List.of("аирдроп", "аир дроп", "аир-дроп", "airdrop", "air drop");
   private static final Pattern PATTERN10 = Pattern.compile("темн\\p{L}*\\s+башн");
   private static final Pattern PATTERN11 = Pattern.compile("(обычн|редк|эпическ|мифическ|легендарн)\\p{L}*\\s+(?:аирдроп|аир дроп|аир-дроп|airdrop|air drop)");
   private final ModeSetting server = new ModeSetting("Сервер", "ReallyWorld", "ReallyWorld", "FunTime").build();
   private final BooleanSetting autoEventDelay = new BooleanSetting("Авто /event delay", true).visibleWhen(this::isFunTimeServer).build();
   private final List<String> stringList = new ArrayList<>();
   private final Set<UUID> uUIDSet = new HashSet<>();
   private UUID uUID;
   private int value = -1;
   private double doubleValue;
   private double doubleValue2;
   private String text;
   private String text2;
   private long longValue;
   private long longValue2;
   private long longValue3;
   private boolean flag;

   private boolean isFunTimeServer() {
      return ((String)this.server.getValue()).equals("FunTime");
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundLoginPacket) {
         this.value = -1;
         this.m_3_void();
      } else if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket && !clientboundSystemChatPacket.overlay()) {
         int value2 = 0;

         for (String text3 : clientboundSystemChatPacket.content().getString().split("\\R")) {
            String text4 = stripColors(text3);
            if (!text4.isEmpty()) {
               if (this.isFunTimeServer() && normalize(text4).startsWith("[ивенты]")) {
                  this.longValue3 = System.currentTimeMillis() + 1500L;
                  this.flag = false;
               }

               this.stringList.add(text4);
               value2++;
            }
         }

         while (this.stringList.size() > 12) {
            this.stringList.removeFirst();
         }

         for (int i = Math.max(0, this.stringList.size() - value2); i < this.stringList.size(); i++) {
            Matcher matcher = (this.isFunTimeServer() ? PATTERN : PATTERN9).matcher(this.stringList.get(i));
            if (matcher.find()) {
               AutoEvent.DataRecord c7 = this.isFunTimeServer() ? this.m_7_C7(i) : this.m_0_C7(i);
               if (c7 != null) {
                  this.stringList.clear();
                  this.m_3(matcher, c7);
                  return;
               }
            }
         }
      }
   }

   private void m_3(Matcher matcher, AutoEvent.DataRecord c7) {
      double doubleValue3 = Double.parseDouble(matcher.group(1));
      double doubleValue4 = Double.parseDouble(matcher.group(3));
      boolean flag2 = this.text != null && doubleValue3 == this.doubleValue && doubleValue4 == this.doubleValue2;
      this.flag = true;
      if (!flag2 || c7.m_3_o() != 0L) {
         this.uUID = null;
         this.doubleValue = doubleValue3;
         this.doubleValue2 = doubleValue4;
         this.text = c7.m_63_String();
         this.text2 = c7.m_6c();
         this.longValue = c7.m_3_o();
         this.longValue2 = this.longValue == 0L ? System.currentTimeMillis() + 60000L : 0L;
         TargetArrow.m_6(doubleValue3, doubleValue4, this.text, false);
         if (!flag2) {
            SoundPlayer.play("event");
         }
      }
   }

   private void m_1_void() {
      Map map = ((BossHealthOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getBossOverlay()).onetap$getEvents();
      this.uUIDSet.retainAll(map.keySet());
      if (this.uUID != null && !map.containsKey(this.uUID)) {
         this.m_3_void();
      }

      for (Entry entry : (Iterable<Entry>)map.entrySet()) {
         if (!this.uUIDSet.contains(entry.getKey())) {
            String text3 = stripColors(((LerpingBossEvent)entry.getValue()).getName().getString());
            AutoEvent.DataRecord c7 = m_7(text3, text3);
            Matcher matcher = PATTERN9.matcher(text3);
            if (c7 != null && matcher.find()) {
               this.uUIDSet.add((UUID)entry.getKey());
               this.m_3(matcher, c7);
               this.uUID = (UUID)entry.getKey();
            }
         }
      }
   }

   private AutoEvent.DataRecord m_0_C7(int value2) {
      if (!normalize(this.stringList.get(value2)).contains("координат")) {
         return null;
      }

      int value3 = Math.max(0, value2 - 10);
      String text3 = String.join(" ", this.stringList.subList(value3, value2 + 1));

      for (int i = value2; i >= value3; i--) {
         AutoEvent.DataRecord c7 = m_7(this.stringList.get(i), text3);
         if (c7 != null) {
            return c7;
         }
      }

      return null;
   }

   private static AutoEvent.DataRecord m_7(String text3, String text4) {
      String text5 = normalize(text3);
      if (!STRING_LIST2.stream().anyMatch(text5::contains)) {
         if (text5.contains("талисман")) {
            return new AutoEvent.DataRecord("Талисман", null, 0L);
         } else {
            return PATTERN10.matcher(text5).find() ? new AutoEvent.DataRecord("Тёмная башня", null, 0L) : null;
         }
      } else {
         Matcher matcher = PATTERN11.matcher(normalize(text4));
         String text6 = null;

         while (matcher.find()) {
            text6 = matcher.group(1);
         }

         return new AutoEvent.DataRecord(text6 != null ? "Аирдроп · " + m_4_String(text6) : "Аирдроп", null, 0L);
      }
   }

   private static String m_4_String(String text3) {
      return switch (text3) {
         case "обычн" -> "Обычный";
         case "редк" -> "Редкий";
         case "эпическ" -> "Эпический";
         case "мифическ" -> "Мифический";
         default -> "Легендарный";
      };
   }

   private static String stripColors(String text3) {
      return text3.replaceAll("§x(§[0-9a-fA-F]){6}", "").replaceAll("§.", "").trim();
   }

   private AutoEvent.DataRecord m_7_C7(int value2) {
      int value3 = -1;

      for (int i = value2 - 1; i >= 0 && i >= value2 - 5; i--) {
         if (!PATTERN2.matcher(this.stringList.get(i)).find()) {
            value3 = i;
            break;
         }
      }

      String text3 = value3 == -1 ? "" : m_7_String(this.stringList.get(value3));
      if (text3.isEmpty()) {
         text3 = "Эвент";
      }

      String text4 = normalize(text3);
      if (STRING_LIST.stream().anyMatch(text4::contains)) {
         return null;
      }

      String text5 = null;
      String text6 = null;
      long longValue4 = 0L;

      for (int i2 = value3 + 1; value3 != -1 && i2 < value2; i2++) {
         String text7 = this.stringList.get(i2);
         Matcher matcher = PATTERN5.matcher(text7);
         if (text5 == null && matcher.find()) {
            text5 = matcher.group(1).trim();
         }

         Matcher matcher2 = PATTERN6.matcher(text7);
         if (longValue4 == 0L && matcher2.find()) {
            long longValue5 = m_9_long(matcher2.group(2));
            if (longValue5 > 0L) {
               text6 = m_8_String(matcher2.group(1));
               longValue4 = System.currentTimeMillis() + longValue5 * 1000L;
            }
         }
      }

      return new AutoEvent.DataRecord(text5 != null ? text3 + " · " + text5 : text3, text6, longValue4);
   }

   private static String m_7_String(String text3) {
      String text4 = PATTERN3.matcher(text3).replaceFirst("");
      Matcher matcher = PATTERN4.matcher(text4);
      return trimNonLetters(matcher.find() ? matcher.group(1) : text4);
   }

   private static String m_8_String(String text3) {
      String text4 = PATTERN8.matcher(text3).replaceFirst("");
      text4 = trimNonLetters(text4.substring(text4.lastIndexOf(44) + 1));
      return text4.isEmpty() ? "Таймер" : text4;
   }

   private static long m_9_long(String text3) {
      long longValue4 = 0L;
      Matcher matcher = PATTERN7.matcher(text3);

      while (matcher.find()) {
         String text4 = matcher.group(2).toLowerCase(Locale.ROOT);
         longValue4 += Long.parseLong(matcher.group(1)) * (text4.startsWith("ч") ? 3600 : (text4.startsWith("м") ? 60 : 1));
      }

      return longValue4;
   }

   private static String trimNonLetters(String text3) {
      return text3.replaceAll("^[^\\p{L}\\d]+|[^\\p{L}\\d]+$", "");
   }

   private static String normalize(String text3) {
      return text3.toLowerCase(Locale.ROOT).replace('ё', 'е');
   }

   private void m_4_void() {
      if (this.longValue != 0L) {
         long longValue4 = (this.longValue - System.currentTimeMillis() + 999L) / 1000L;
         if (longValue4 <= 0L) {
            this.longValue = 0L;
            this.longValue2 = System.currentTimeMillis() + 2000L;
            TargetArrow.m_4_void(this.text);
         } else {
            String text3 = longValue4 >= 3600L
               ? String.format("%d:%02d:%02d", longValue4 / 3600L, longValue4 / 60L % 60L, longValue4 % 60L)
               : String.format("%d:%02d", longValue4 / 60L, longValue4 % 60L);
            TargetArrow.m_4_void(this.text + "\n" + this.text2 + ": " + text3);
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         this.m_4_void();
         if (!this.isFunTimeServer()) {
            this.m_1_void();
         } else {
            long longValue4 = System.currentTimeMillis();
            if (this.longValue3 != 0L && longValue4 > this.longValue3) {
               this.longValue3 = 0L;
               if (!this.flag) {
                  this.m_3_void();
               }
            }

            if ((Boolean)this.autoEventDelay.getValue()) {
               int value2 = RctCommand.m_7(MinecraftAccess.MINECRAFT);
               boolean flag2 = value2 != -1 && value2 != this.value;
               this.value = value2;
               if (flag2 || this.text != null && this.longValue2 != 0L && longValue4 >= this.longValue2) {
                  MinecraftAccess.MINECRAFT.getConnection().sendCommand("event delay");
                  this.longValue2 = this.text != null ? longValue4 + 60000L : 0L;
               }
            }
         }
      }
   }

   private void m_3_void() {
      if (this.text != null) {
         TargetArrow.m_7_void();
      }

      this.text = null;
      this.uUID = null;
      this.longValue = 0L;
      this.longValue2 = 0L;
      this.stringList.clear();
      this.uUIDSet.clear();
   }

   @Override
   public void onDisable() {
      this.m_3_void();
      this.value = -1;
      super.onDisable();
   }

   private record DataRecord(String f_63, String f_6c, long f_3_o) {
      public String m_63_String() {
         return this.f_63;
      }

      public String m_6c() {
         return this.f_6c;
      }

      public long m_3_o() {
         return this.f_3_o;
      }
   }
}
