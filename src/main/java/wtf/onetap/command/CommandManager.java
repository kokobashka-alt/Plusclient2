package wtf.onetap.command;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EraseAfterRun;
import wtf.onetap.module.Module;
import wtf.onetap.module.player.NoCommands;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.KeyNames;

public class CommandManager {
   private static final List<String> STRING_LIST = List.of("list", "add", "remove", "clear");
   private final Map<String, Command> stringMap;
   private final BooleanSupplier booleanSupplier;
   private static Map<String, Key> stringMap2;
   private static List<String> stringList;
   private static String[] textArray;

   public CommandManager() {
      this(NoCommands::m_7_boolean);
   }

   public CommandManager(BooleanSupplier booleanSupplier2) {
      this.stringMap = new LinkedHashMap<>();
      this.booleanSupplier = booleanSupplier2;
   }

   @EraseAfterRun
   public void registerCommands() {
      this.register(new HelpCommand());
      this.register(new PanicCommand());
      this.register(new BindCommand());
      this.register(new ConfigCommand());
      this.register(new StaffCommand());
      this.register(new FriendCommand());
      this.register(new BotCommand());
      this.register(new MacroCommand());
      this.register(new RctCommand());
      this.register(new GpsCommand());
      this.register(new WarpCommand());
      this.register(new BlockEspCommand());
      this.register(new VClipCommand());
      this.register(new HClipCommand());
      this.register(new TpCommand());
      this.register(new HolyWorldCommand());
   }

   public void register(Command command) {
      this.stringMap.put(command.getText2().toLowerCase(Locale.ROOT), command);
   }

   public boolean m_9_boolean(String text) {
      if (ChatUtil.isUnHooked()) {
         return false;
      }

      if (text != null && NoCommands.isAllowedCommand(text)) {
         if (NoCommands.m_7_boolean() && text.startsWith("/")) {
            text = text.substring(1);
         }

         if (!text.startsWith(".")) {
            return false;
         }

         String text2 = text.substring(".".length()).trim();
         if (text2.isEmpty()) {
            return false;
         }

         String[] stringArray = text2.split("\\s+");
         String text3 = stringArray[0].toLowerCase(Locale.ROOT);
         Command command = this.stringMap.get(text3);
         if (command == null) {
            return false;
         }

         String[] stringArray2 = new String[stringArray.length > 1 ? stringArray.length - 1 : 0];
         System.arraycopy(stringArray, 1, stringArray2, 0, stringArray2.length);

         try {
            command.showHelp(new CommandHelp(text3, stringArray2));
         } catch (Throwable e) {
            e.printStackTrace();
         }

         return true;
      } else {
         return false;
      }
   }

   public Map<String, Command> getStringMap() {
      return this.stringMap;
   }

   public List<String> m_5_List(String text) {
      if (this.booleanSupplier.getAsBoolean()) {
         return List.of();
      }

      if (text != null && !text.isEmpty()) {
         String[] stringArray = text.split("\\s+", -1);
         String text2 = stringArray[0].toLowerCase(Locale.ROOT);
         Command command = this.stringMap.get(text2);
         if (command == null) {
            return filterByPrefix(this.stringMap.keySet(), text2);
         }

         int value = stringArray.length - 1;
         if (value == 0) {
            return List.of();
         }

         String text3 = stringArray[value];
         String text4 = text3.toLowerCase(Locale.ROOT);

         return switch (text2) {
            case "bind" -> this.m_8(stringArray, value, text4);
            case "cfg" -> this.m_7(stringArray, value, text4);
            case "friend", "staff" -> this.m_3(stringArray, value, text4, text2.equals("friend") ? Onetap.FRIEND_MANAGER.getFriends() : Onetap.STAFF_MANAGER.getStaff());
            case "bot" -> BotCommand.complete(stringArray, value, text4);
            case "macro" -> this.m_4(stringArray, value, text4);
            case "gps" -> this.m_6(stringArray, value, text4);
            case "warp" -> this.m_2(stringArray, value, text4);
            case "blockesp" -> BlockEspCommand.complete(stringArray, value, text4);
            case "vclip" -> value == 1 ? filterByPrefix(List.of("up", "down"), text4) : List.of();
            case "tp" -> value == 1 ? m_4_List(text4) : List.of();
            default -> List.of();
         };
      } else {
         return new ArrayList<>(this.stringMap.keySet());
      }
   }

   private List<String> m_3(String[] stringArray, int value, String text, Iterable<String> stringList2) {
      if (value == 1) {
         return filterByPrefix(STRING_LIST, text);
      } else if (value == 2 && stringArray[1].equals("add")) {
         return m_4_List(text);
      } else {
         return value == 2 && stringArray[1].equals("remove") ? filterByPrefix(stringList2, text) : List.of();
      }
   }

   private static List<String> m_4_List(String text) {
      ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
      if (clientPacketListener == null) {
         return List.of();
      }

      ArrayList list = new ArrayList();

      for (PlayerInfo playerInfo : clientPacketListener.getOnlinePlayers()) {
         list.add(playerInfo.getProfile().name());
      }

      var list2 = filterByPrefix(list, text);
      list2.sort(null);
      return list2;
   }

   private static List<String> filterByPrefix(Iterable<String> stringList2, String text) {
      ArrayList list = new ArrayList();

      for (String text2 : stringList2) {
         if (text2.toLowerCase(Locale.ROOT).startsWith(text)) {
            list.add(text2);
         }
      }

      return list;
   }

   private List<String> m_4(String[] stringArray, int value, String text) {
      if (value == 1) {
         return filterByPrefix(STRING_LIST, text);
      }

      if (value != 2) {
         return List.of();
      }

      if (stringArray[1].equals("add")) {
         return suggestCommands(text);
      }

      if (!stringArray[1].equals("remove")) {
         return List.of();
      }

      ArrayList list = new ArrayList();

      for (int value2 : Onetap.MACRO_MANAGER.getMacros().keySet()) {
         list.add(KeyNames.getKeyName(value2));
      }

      var list2 = filterByPrefix(list, text);
      list2.sort(null);
      return list2;
   }

   private List<String> m_8(String[] stringArray, int value, String text) {
      if (value == 1) {
         return filterByPrefix(STRING_LIST, text);
      }

      if (!stringArray[1].equals("add") && !stringArray[1].equals("remove")) {
         return List.of();
      }

      boolean flag = stringArray[1].equals("remove");
      String text2 = joinArgs(stringArray, 2, value - 1);
      int value2 = text2.isEmpty() ? 0 : text2.length() + 1;
      String text3 = joinArgs(stringArray, 2, value).toLowerCase(Locale.ROOT);
      ArrayList list = new ArrayList();

      for (Module module : Onetap.MODULE_MANAGER.getModules()) {
         if (!flag || module.getKey() > 0) {
            String text4 = module.getName();
            if (text4.length() > value2 && text4.toLowerCase(Locale.ROOT).startsWith(text3)) {
               list.add(text4.substring(value2));
            }
         }
      }

      if (!flag && Onetap.MODULE_MANAGER.findByName(text2).isPresent()) {
         list.addAll(suggestCommands(text));
      }

      return list;
   }

   private static String joinArgs(String[] stringArray, int value, int value2) {
      if (value > value2) {
         return "";
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (int i = value; i <= value2; i++) {
         if (stringBuilder.length() > 0) {
            stringBuilder.append(' ');
         }

         stringBuilder.append(stringArray[i]);
      }

      return stringBuilder.toString();
   }

   private static void m_5_void() {
      if (stringMap2 == null) {
         HashMap map = new HashMap();
         ArrayList list = new ArrayList();

         try {
            Field field = Key.class.getDeclaredField("NAME_MAP");
            field.setAccessible(true);
            Map map2 = (Map)field.get(null);

            for (Key key : (Iterable<Key>)map2.values()) {
               if (key.getName().startsWith("key.keyboard.")) {
                  String text = KeyNames.getKeyName(key.getValue());
                  if (!text.isEmpty() && !text.equals("Unknown")) {
                     map.putIfAbsent(key.getDisplayName().getString().toLowerCase(Locale.ROOT).replace(" ", ""), key);
                     map.put(text.toLowerCase(Locale.ROOT), key);
                     list.add(text);
                  }
               }
            }
         } catch (Exception e) {
         }

         list.sort(null);
         stringMap2 = map;
         stringList = list;
      }
   }

   private static List<String> suggestCommands(String text) {
      m_5_void();
      return filterByPrefix(stringList, text);
   }

   public static Key getCommand(String text) {
      m_5_void();
      return stringMap2.get(text.toLowerCase(Locale.ROOT).replace(" ", ""));
   }

   private List<String> m_7(String[] stringArray, int value, String text) {
      if (value == 1) {
         return filterByPrefix(List.of("list", "save", "load", "delete", "dir"), text);
      } else {
         return value < 2 || !stringArray[1].equals("load") && !stringArray[1].equals("save") && !stringArray[1].equals("delete") ? List.of() : filterByPrefix(Onetap.CONFIG_MANAGER.listConfigs(), text);
      }
   }

   private List<String> m_6(String[] stringArray, int value, String text) {
      if (value == 1) {
         ArrayList list = new ArrayList();
         if ("off".startsWith(text)) {
            list.add("off");
         }

         if (text.isEmpty()) {
            list.add("X");
         }

         return list;
      } else {
         return value == 2 && !stringArray[1].equalsIgnoreCase("off") && text.isEmpty() ? List.of("Z") : List.of();
      }
   }

   private List<String> m_2(String[] stringArray, int value, String text) {
      if (value == 1) {
         var stringList2 = filterByPrefix(List.of("parse", "stop", "list"), text);
         if (text.isEmpty()) {
            stringList2.add("X");
         }

         return stringList2;
      } else if (value == 2 && stringArray[1].equalsIgnoreCase("parse")) {
         return filterByPrefix(List.of("new", "old", "continue"), text);
      } else {
         return value == 2 && !isSubcommand(stringArray[1]) && text.isEmpty() ? List.of("Z") : List.of();
      }
   }

   private static boolean isSubcommand(String text) {
      return text.equalsIgnoreCase("parse") || text.equalsIgnoreCase("stop") || text.equalsIgnoreCase("list");
   }

   static {
      m_9(null);
   }

   public static void m_9(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_1_StringArr() {
      return textArray;
   }
}
