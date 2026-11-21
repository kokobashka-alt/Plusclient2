package wtf.onetap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.ClickEvent.SuggestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.render.PlayerTabOverlayAccessor;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

public class WarpManager implements MinecraftAccess {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Path PATH = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("warps.json");
   private static final Pattern PATTERN = Pattern.compile("(?i)(?:^|/)(?:[\\w-]+:)?warp\\s+(\\S+)");
   private static final Pattern PATTERN2 = Pattern.compile("(?iu)(?:варп(?:ы|ов)?|warps?)\\s*[:：]\\s*(.+)");
   private static final Pattern PATTERN3 = Pattern.compile("(?iu)(?:страница|page)\\s+(\\d+)\\s+(?:из|of)\\s+(\\d+)");
   private static final Pattern PATTERN4 = Pattern.compile("(?iu)анархия-(\\d+)");
   private static final Set<String> STRING_SET = Set.of(
      "list", "help", "set", "delete", "create", "remove", "info", "p", "page", "warp", "warps", "варп", "варпы", "next", "prev", "далее", "назад", "закрыть"
   );
   private static final Map<String, WarpManager.StateHolder> STRING_MAP = new LinkedHashMap<>();
   private static final LinkedHashSet<String> STRING_SET2 = new LinkedHashSet<>();
   private static final LinkedHashSet<String> STRING_SET3 = new LinkedHashSet<>();
   private static final List<String> STRING_LIST = new ArrayList<>();
   private static final TimeHelper TIME_HELPER = new TimeHelper();
   private static WarpManager.ModeKind c7 = WarpManager.ModeKind.IDLE;
   private static boolean flag;
   private static String text;
   private static int value;
   private static String text2;
   private static double doubleValue;
   private static double doubleValue2;
   private static boolean flag2;
   private static long longValue = 1500L;
   private static int value2;
   private static int value3;
   private static int value4;
   private static int value5;
   private static boolean flag3;

   public WarpManager() {
      load();
   }

   public static boolean m_8_boolean() {
      return c7 != WarpManager.ModeKind.IDLE;
   }

   public static Collection<WarpManager.DataRecord> getWarpCollection() {
      WarpManager.StateHolder c6 = STRING_MAP.get(getServerKey());
      return c6 == null ? List.of() : c6.stringMap.values();
   }

   public static WarpManager.DataRecord findNearestWarp(double doubleValue3, double doubleValue4) {
      WarpManager.DataRecord c0 = null;
      double doubleValue5 = Double.MAX_VALUE;

      for (WarpManager.DataRecord c02 : getWarpCollection()) {
         if (isOverworld(c02.f_1p)) {
            double doubleValue6 = c02.f_3_p - doubleValue3;
            double doubleValue7 = c02.f_3s - doubleValue4;
            double doubleValue8 = doubleValue6 * doubleValue6 + doubleValue7 * doubleValue7;
            if (doubleValue8 < doubleValue5) {
               doubleValue5 = doubleValue8;
               c0 = c02;
            }
         }
      }

      return c0;
   }

   public static void m_9_void(boolean flag4) {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.getConnection() == null) {
         ChatUtil.log("§cno player");
      } else if (c7 != WarpManager.ModeKind.IDLE) {
         ChatUtil.log("§cbusy");
      } else {
         text = getServerKey();
         flag = flag4;
         STRING_SET2.clear();
         STRING_SET3.clear();
         STRING_LIST.clear();
         value = 0;
         text2 = null;
         flag2 = false;
         longValue = 1500L;
         value5 = 0;
         flag3 = false;
         WarpManager.StateHolder c6 = getServerData();
         if (flag4) {
            c6.stringMap.clear();
            c6.stringSet.clear();
            c6.value = 0;
            c6.value2 = 0;
            save();
            startScan();
         } else {
            STRING_SET2.addAll(c6.stringSet);
            if (c6.value2 > 0 && c6.value < c6.value2) {
               value2 = c6.value;
               value3 = c6.value2;
               value4 = value2;
               c7 = WarpManager.ModeKind.LISTING;
               TIME_HELPER.reset();
               int value6 = value2 + 1;
               int value7 = value3;
               int value8 = STRING_SET2.size();
               int value9 = value7;
               int value10 = value6;
               ChatUtil.log("§7list " + value10 + "/" + value9 + " " + value8);
            } else {
               int value11 = m_4_int();
               if (value11 > 0) {
                  ChatUtil.log("§7visit " + value11 + "/" + c6.stringSet.size());
                  m_2_f_void();
               } else if (c6.value2 > 0 && c6.value >= c6.value2) {
                  ChatUtil.log("§7done " + c6.stringMap.size());
               } else {
                  ChatUtil.log("§7list resume");
                  startScan();
               }
            }
         }
      }
   }

   public static void m_2_void() {
      if (c7 == WarpManager.ModeKind.IDLE) {
         ChatUtil.log("§7idle");
      } else {
         m_4_void();
         c7 = WarpManager.ModeKind.IDLE;
         save();
         ChatUtil.log("§7stop " + getServerData().stringMap.size() + " left " + m_4_int());
      }
   }

   public static boolean m_6_boolean(double doubleValue3, double doubleValue4) {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.getConnection() == null) {
         ChatUtil.log("§cno player");
         return false;
      } else if (c7 != WarpManager.ModeKind.IDLE) {
         ChatUtil.log("§cbusy");
         return false;
      } else {
         WarpManager.DataRecord c0 = findNearestWarp(doubleValue3, doubleValue4);
         if (c0 == null) {
            ChatUtil.log("§cno ow");
            return false;
         } else {
            double doubleValue5 = Math.sqrt((c0.f_3_p - doubleValue3) * (c0.f_3_p - doubleValue3) + (c0.f_3s - doubleValue4) * (c0.f_3s - doubleValue4));
            String text3 = c0.f_1_p;
            long longValue2 = Math.round(doubleValue5);
            long longValue3 = Math.round(c0.f_3_p);
            long longValue4 = Math.round(c0.f_3s);
            long longValue5 = longValue3;
            long longValue6 = longValue2;
            String text4 = text3;
            ChatUtil.log("§7ow " + text4 + " " + longValue6 + "m " + longValue5 + " " + longValue4);
            sendCommand("warp " + c0.f_1_p);
            return true;
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (c7 != WarpManager.ModeKind.IDLE) {
         if (c7 == WarpManager.ModeKind.LISTING) {
            Component component = getChatComponent(event);
            if (component != null) {
               handleComponent(component);
            }
         } else if (c7 == WarpManager.ModeKind.WAIT_TP && MinecraftAccess.MINECRAFT.player != null) {
            if (event.getPacket() instanceof ClientboundPlayerPositionPacket clientboundPlayerPositionPacket) {
               Vec3 vec3 = clientboundPlayerPositionPacket.change().position();
               var relativeSet = clientboundPlayerPositionPacket.relatives();
               double doubleValue3 = relativeSet.contains(Relative.X) ? MinecraftAccess.MINECRAFT.player.getX() + vec3.x : vec3.x;
               double doubleValue4 = relativeSet.contains(Relative.Z) ? MinecraftAccess.MINECRAFT.player.getZ() + vec3.z : vec3.z;
               if (m_0(doubleValue, doubleValue2, doubleValue3, doubleValue4) >= 64.0) {
                  flag2 = true;
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (c7 != WarpManager.ModeKind.IDLE) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
            switch (c7.ordinal()) {
               case 1:
                  m_7_void();
                  break;
               case 2:
                  m_3_void();
                  break;
               case 3:
                  if (TIME_HELPER.hasElapsed(longValue)) {
                     m_9_void();
                  }
            }
         } else {
            m_4_void();
            save();
            c7 = WarpManager.ModeKind.IDLE;
            ChatUtil.log("§cabort");
         }
      }
   }

   private static void m_7_void() {
      if (value3 > 0) {
         m_2c_void();
      } else {
         m_5_void();
         boolean flag4 = !STRING_SET2.isEmpty() || !STRING_SET3.isEmpty();
         if (!flag4 || !TIME_HELPER.hasElapsed(1500L) || MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen) {
            if (TIME_HELPER.hasElapsed(2500L)) {
               if (flag4) {
                  m_2_f_void();
               } else {
                  c7 = WarpManager.ModeKind.IDLE;
                  ChatUtil.log("§cempty");
               }
            }
         } else {
            m_2_f_void();
         }
      }
   }

   private static void m_2c_void() {
      if (flag3) {
         if (TIME_HELPER.hasElapsed(2000L)) {
            if (value5 < 1) {
               value5++;
               sendCommand(value4 <= 1 ? "warp" : "warp " + value4);
               TIME_HELPER.reset();
            } else {
               ChatUtil.log("§cskip page " + value4);
               value2 = Math.max(value2, value4);
               flag3 = false;
               TIME_HELPER.reset();
            }
         }
      } else if (value2 < value3) {
         if (TIME_HELPER.hasElapsed(2200L)) {
            value4 = value2 + 1;
            value5 = 0;
            flag3 = true;
            sendCommand("warp " + value4);
            int value6 = value4;
            int value7 = value3;
            int value8 = STRING_SET2.size();
            int value9 = value7;
            int value10 = value6;
            ChatUtil.log("§7list " + value10 + "/" + value9 + " " + value8);
            TIME_HELPER.reset();
         }
      } else if (STRING_SET2.isEmpty() && STRING_SET3.isEmpty()) {
         c7 = WarpManager.ModeKind.IDLE;
         ChatUtil.log("§cempty");
      } else {
         m_2_f_void();
      }
   }

   private static void m_3_void() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (!flag2 && TIME_HELPER.getElapsed() > 250L && m_0(doubleValue, doubleValue2, MinecraftAccess.MINECRAFT.player.getX(), MinecraftAccess.MINECRAFT.player.getZ()) >= 64.0) {
            flag2 = true;
         }

         if (flag2) {
            recordWarp();
            m_1_void();
         } else {
            if (TIME_HELPER.hasElapsed(4000L)) {
               ChatUtil.log("§cskip " + text2);
               m_1_void();
            }
         }
      }
   }

   private static void m_1_void() {
      if (value >= STRING_LIST.size()) {
         finishScan();
      } else {
         longValue = 1500L;
         c7 = WarpManager.ModeKind.DELAY;
         TIME_HELPER.reset();
      }
   }

   private static void startScan() {
      value2 = 0;
      value3 = 0;
      value4 = 1;
      flag3 = false;
      c7 = WarpManager.ModeKind.LISTING;
      TIME_HELPER.reset();
      sendCommand("warp");
      ChatUtil.log("§7list");
   }

   private static int m_4_int() {
      WarpManager.StateHolder c6 = getServerData();
      int value6 = 0;

      for (String text3 : c6.stringSet) {
         if (!c6.stringMap.containsKey(text3.toLowerCase(Locale.ROOT))) {
            value6++;
         }
      }

      return value6;
   }

   private static void m_4_void() {
      if (!STRING_SET2.isEmpty() || value3 != 0) {
         WarpManager.StateHolder c6 = getServerData();
         c6.stringSet.addAll(STRING_SET2);
         if (value3 > 0) {
            c6.value = value2;
            c6.value2 = value3;
         }
      }
   }

   private static void m_2_f_void() {
      LinkedHashSet set = !STRING_SET2.isEmpty() ? STRING_SET2 : STRING_SET3;
      WarpManager.StateHolder c6 = getServerData();
      c6.stringSet.clear();
      c6.stringSet.addAll(set);
      if (value3 > 0) {
         c6.value = value3;
         c6.value2 = value3;
      }

      if (flag) {
         c6.stringMap.clear();
      }

      STRING_LIST.clear();
      value = 0;

      for (String text3 : (Iterable<String>)set) {
         if (!c6.stringMap.containsKey(text3.toLowerCase(Locale.ROOT))) {
            STRING_LIST.add(text3);
         }
      }

      save();
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      if (STRING_LIST.isEmpty()) {
         finishScan();
      } else {
         ChatUtil.log("§7visit " + STRING_LIST.size());
         longValue = 300L;
         c7 = WarpManager.ModeKind.DELAY;
         TIME_HELPER.reset();
      }
   }

   private static void m_9_void() {
      if (value >= STRING_LIST.size()) {
         finishScan();
      } else if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen) {
            MinecraftAccess.MINECRAFT.player.closeContainer();
         }

         text2 = STRING_LIST.get(value++);
         doubleValue = MinecraftAccess.MINECRAFT.player.getX();
         doubleValue2 = MinecraftAccess.MINECRAFT.player.getZ();
         flag2 = false;
         c7 = WarpManager.ModeKind.WAIT_TP;
         TIME_HELPER.reset();
         int value6 = value;
         int value7 = STRING_LIST.size();
         String text3 = text2;
         int value8 = value7;
         int value9 = value6;
         ChatUtil.log("§7" + value9 + "/" + value8 + " " + text3);
         sendCommand("warp " + text2);
      } else {
         c7 = WarpManager.ModeKind.IDLE;
      }
   }

   private static void recordWarp() {
      if (text2 != null && MinecraftAccess.MINECRAFT.player != null) {
         WarpManager.DataRecord c0 = new WarpManager.DataRecord(text2, MinecraftAccess.MINECRAFT.player.getX(), MinecraftAccess.MINECRAFT.player.getZ(), getDimensionPath());
         getWarps().put(text2.toLowerCase(Locale.ROOT), c0);
         save();
         String text3 = text2;
         long longValue2 = Math.round(c0.f_3_p);
         long longValue3 = Math.round(c0.f_3s);
         String text4 = c0.f_1p;
         long longValue4 = longValue3;
         long longValue5 = longValue2;
         String text5 = text3;
         ChatUtil.log("§7" + text5 + " " + longValue5 + " " + longValue4 + " " + text4);
      }
   }

   private static void finishScan() {
      c7 = WarpManager.ModeKind.IDLE;
      save();
      ChatUtil.log("§7done " + getWarps().size());
   }

   private static void m_5_void() {
      if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.screen() instanceof AbstractContainerScreen abstractContainerScreen) {
         AbstractContainerMenu abstractContainerMenu = abstractContainerScreen.getMenu();

         for (Slot slot : abstractContainerMenu.slots) {
            if (!(slot.container instanceof Inventory)) {
               ItemStack itemStack = slot.getItem();
               if (!itemStack.isEmpty() && !isIgnoredItem(itemStack)) {
                  handleComponent(itemStack.getHoverName());
                  ItemLore itemLore = itemStack.get(DataComponents.LORE);
                  if (itemLore != null) {
                     for (Component component : itemLore.lines()) {
                        handleComponent(component);
                     }
                  }

                  if (STRING_SET2.isEmpty()) {
                     m_2_void(itemStack.getHoverName().getString());
                  }
               }
            }
         }
      }
   }

   private static boolean isIgnoredItem(ItemStack itemStack) {
      if (itemStack.getItem() == Items.BARRIER) {
         return true;
      }

      String text3 = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
      return text3.endsWith("glass_pane");
   }

   private static void handleComponent(Component component) {
      m_5_void(component.getString());
      component.visit((style, text3) -> {
         handleClick(style.getClickEvent());
         if (style.getHoverEvent() instanceof ShowText(Component component2)) {
            handleComponent(component2);
         }

         return Optional.empty();
      }, Style.EMPTY);
   }

   private static void handleClick(ClickEvent event) {
      if (event instanceof RunCommand(String text3)) {
         m_1_void(text3);
      } else if (event instanceof SuggestCommand(String text4)) {
         m_1_void(text4);
      }
   }

   private static void m_1_void(String text3) {
      if (text3 != null) {
         Matcher matcher = PATTERN.matcher(text3.trim());
         if (matcher.find()) {
            addWarp(matcher.group(1), STRING_SET2);
         }
      }
   }

   private static void m_5_void(String text3) {
      if (text3 != null && !text3.isEmpty()) {
         String text4 = ChatFormatting.stripFormatting(text3);
         if (text4 != null && !text4.isEmpty()) {
            Matcher matcher = PATTERN3.matcher(text4);
            if (matcher.find()) {
               value2 = Integer.parseInt(matcher.group(1));
               value3 = Integer.parseInt(matcher.group(2));
               value4 = value2;
               flag3 = true;
               value5 = 0;
               TIME_HELPER.reset();
            } else {
               Matcher matcher2 = PATTERN.matcher(text4);

               while (matcher2.find()) {
                  addWarp(matcher2.group(1), STRING_SET2);
               }

               Matcher matcher3 = PATTERN2.matcher(text4);
               boolean flag4 = matcher3.find();
               String text5 = flag4 ? matcher3.group(1) : text4;
               if (flag3 || flag4 || value3 > 0 && text5.indexOf(44) >= 0) {
                  for (String text6 : text5.split("\\s*,\\s*")) {
                     addWarp(text6, STRING_SET2);
                  }

                  if (flag3) {
                     flag3 = false;
                     m_4_void();
                     save();
                     TIME_HELPER.reset();
                  }
               }
            }
         }
      }
   }

   private static void m_2_void(String text3) {
      String text4 = ChatFormatting.stripFormatting(text3);
      if (text4 != null) {
         addWarp(text4, STRING_SET3);
      }
   }

   private static void addWarp(String text3, LinkedHashSet<String> stringSet) {
      if (text3 != null) {
         text3 = text3.trim();
         if (!text3.isEmpty() && text3.length() <= 64) {
            if (!STRING_SET.contains(text3.toLowerCase(Locale.ROOT))) {
               if (!isNumeric(text3)) {
                  for (String text4 : stringSet) {
                     if (text4.equalsIgnoreCase(text3)) {
                        return;
                     }
                  }

                  stringSet.add(text3);
               }
            }
         }
      }
   }

   private static boolean isNumeric(String text3) {
      for (int i = 0; i < text3.length(); i++) {
         if (!Character.isDigit(text3.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   private static Component getChatComponent(ReceivePacketEvent event) {
      return switch (event.getPacket()) {
         case ClientboundSystemChatPacket clientboundSystemChatPacket when !clientboundSystemChatPacket.overlay() -> clientboundSystemChatPacket.content();
         case ClientboundDisguisedChatPacket clientboundDisguisedChatPacket -> clientboundDisguisedChatPacket.message();
         default -> null;
      };
   }

   private static WarpManager.StateHolder getServerData() {
      String text3 = text != null ? text : getServerKey();
      return STRING_MAP.computeIfAbsent(text3, text4 -> new WarpManager.StateHolder());
   }

   private static LinkedHashMap<String, WarpManager.DataRecord> getWarps() {
      return getServerData().stringMap;
   }

   private static String getServerKey() {
      ServerData serverData = MinecraftAccess.MINECRAFT.getCurrentServer();
      String text3 = serverData != null ? serverData.ip.replaceAll("[^a-zA-Z0-9.-]", "_") : "singleplayer";
      if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null) {
         PlayerTabOverlay playerTabOverlay = MinecraftAccess.MINECRAFT.gui.hud.getTabList();
         if (playerTabOverlay == null) {
            return text3;
         }

         Component component = ((PlayerTabOverlayAccessor)playerTabOverlay).onetap$getHeader();
         if (component == null) {
            return text3;
         }

         String text4 = ChatFormatting.stripFormatting(component.getString());
         if (text4 == null) {
            return text3;
         }

         Matcher matcher = PATTERN4.matcher(text4);
         return matcher.find() ? text3 + "_an" + matcher.group(1) : text3;
      } else {
         return text3;
      }
   }

   private static void sendCommand(String text3) {
      if (MinecraftAccess.MINECRAFT.getConnection() != null) {
         MinecraftAccess.MINECRAFT.getConnection().sendCommand(text3);
      }
   }

   private static double m_0(double doubleValue3, double doubleValue4, double doubleValue5, double doubleValue6) {
      double doubleValue7 = doubleValue3 - doubleValue5;
      double doubleValue8 = doubleValue4 - doubleValue6;
      return doubleValue7 * doubleValue7 + doubleValue8 * doubleValue8;
   }

   private static String getDimensionPath() {
      return MinecraftAccess.MINECRAFT.level == null ? "" : MinecraftAccess.MINECRAFT.level.dimension().identifier().getPath();
   }

   private static boolean isOverworld(String text3) {
      return text3 != null && !text3.isEmpty() ? text3.endsWith("overworld") : true;
   }

   private static void load() {
      if (Files.exists(PATH)) {
         try {
            label68: {
               try (BufferedReader bufferedReader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                  JsonElement jsonElement = JsonParser.parseReader(bufferedReader);
                  if (jsonElement.isJsonObject()) {
                     STRING_MAP.clear();

                     for (Entry entry : jsonElement.getAsJsonObject().entrySet()) {
                        WarpManager.StateHolder c6 = m_0((JsonElement)entry.getValue());
                        if (c6 != null) {
                           STRING_MAP.put((String)entry.getKey(), c6);
                        }
                     }
                     break label68;
                  }
               }

               return;
            }
         } catch (Exception e) {
            Onetap.LOGGER.error("Failed to load warps", e);
         }
      }
   }

   private static WarpManager.StateHolder m_0(JsonElement jsonElement) {
      WarpManager.StateHolder c6 = new WarpManager.StateHolder();
      JsonArray jsonArray;
      if (jsonElement.isJsonArray()) {
         jsonArray = jsonElement.getAsJsonArray();
      } else {
         if (!jsonElement.isJsonObject()) {
            return null;
         }

         JsonObject jsonObject = jsonElement.getAsJsonObject();
         jsonArray = jsonObject.has("warps") && jsonObject.get("warps").isJsonArray() ? jsonObject.getAsJsonArray("warps") : new JsonArray();
         if (jsonObject.has("listed") && jsonObject.get("listed").isJsonArray()) {
            for (JsonElement jsonElement2 : jsonObject.getAsJsonArray("listed")) {
               if (jsonElement2.isJsonPrimitive()) {
                  addWarp(jsonElement2.getAsString(), c6.stringSet);
               }
            }
         }

         if (jsonObject.has("listPage")) {
            c6.value = jsonObject.get("listPage").getAsInt();
         }

         if (jsonObject.has("listPages")) {
            c6.value2 = jsonObject.get("listPages").getAsInt();
         }
      }

      for (JsonElement jsonElement3 : jsonArray) {
         if (jsonElement3.isJsonObject()) {
            JsonObject jsonObject2 = jsonElement3.getAsJsonObject();
            if (jsonObject2.has("name") && jsonObject2.has("x") && jsonObject2.has("z")) {
               String text3 = jsonObject2.get("name").getAsString();
               String text4 = jsonObject2.has("dim") ? jsonObject2.get("dim").getAsString() : "";
               c6.stringMap.put(text3.toLowerCase(Locale.ROOT), new WarpManager.DataRecord(text3, jsonObject2.get("x").getAsDouble(), jsonObject2.get("z").getAsDouble(), text4));
               addWarp(text3, c6.stringSet);
            }
         }
      }

      return c6;
   }

   private static void save() {
      try {
         Files.createDirectories(PATH.getParent());
         JsonObject jsonObject = new JsonObject();

         for (Entry entry : STRING_MAP.entrySet()) {
            WarpManager.StateHolder c6 = (WarpManager.StateHolder)entry.getValue();
            JsonObject jsonObject2 = new JsonObject();
            JsonArray jsonArray = new JsonArray();

            for (WarpManager.DataRecord c0 : c6.stringMap.values()) {
               JsonObject jsonObject3 = new JsonObject();
               jsonObject3.addProperty("name", c0.f_1_p);
               jsonObject3.addProperty("x", c0.f_3_p);
               jsonObject3.addProperty("z", c0.f_3s);
               jsonObject3.addProperty("dim", c0.f_1p == null ? "" : c0.f_1p);
               jsonArray.add(jsonObject3);
            }

            JsonArray jsonArray2 = new JsonArray();

            for (String text3 : c6.stringSet) {
               jsonArray2.add(text3);
            }

            jsonObject2.add("warps", jsonArray);
            jsonObject2.add("listed", jsonArray2);
            jsonObject2.addProperty("listPage", c6.value);
            jsonObject2.addProperty("listPages", c6.value2);
            jsonObject.add((String)entry.getKey(), jsonObject2);
         }

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonObject, bufferedWriter);
         }
      } catch (IOException e) {
         Onetap.LOGGER.error("Failed to save warps", e);
      }
   }

   public record DataRecord(String f_1_p, double f_3_p, double f_3s, String f_1p) {
      public String m_1_p_String() {
         return this.f_1_p;
      }

      public double m_3_p() {
         return this.f_3_p;
      }

      public double m_3s() {
         return this.f_3s;
      }

      public String m_1p() {
         return this.f_1p;
      }
   }

   private static final class StateHolder {
      public final LinkedHashMap<String, WarpManager.DataRecord> stringMap = new LinkedHashMap<>();
      public final LinkedHashSet<String> stringSet = new LinkedHashSet<>();
      public int value;
      public int value2;
   }

   private enum ModeKind {
      IDLE,
      LISTING,
      WAIT_TP,
      DELAY;

      private static String a(byte[] byteArray) {
         int value6 = 0;
         int value7;
         char[] charArray = new char[value7 = byteArray.length];

         for (int i = 0; i < value7; i++) {
            int value8;
            if ((value8 = 255 & byteArray[i]) < 192) {
               charArray[value6++] = (char)value8;
            } else if (value8 < 224) {
               char ch = (char)((char)(value8 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value6++] = ch;
            } else if (i < value7 - 2) {
               char ch2 = (char)((char)(value8 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value6++] = ch2;
            }
         }

         return new String(charArray, 0, value6);
      }
   }
}
