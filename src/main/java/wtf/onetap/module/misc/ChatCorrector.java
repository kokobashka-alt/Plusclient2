package wtf.onetap.module.misc;

import java.util.Locale;
import java.util.Map;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;

@ModuleInformation(name = "Chat Corrector", description = "Исправляет команды, набранные в русской раскладке", category = Category.MISC)
public class ChatCorrector extends Module {
   private static final Map<Character, Character> CHARACTER_MAP = Map.ofEntries(
      Map.entry('й', 'q'),
      Map.entry('ц', 'w'),
      Map.entry('у', 'e'),
      Map.entry('к', 'r'),
      Map.entry('е', 't'),
      Map.entry('н', 'y'),
      Map.entry('г', 'u'),
      Map.entry('ш', 'i'),
      Map.entry('щ', 'o'),
      Map.entry('з', 'p'),
      Map.entry('х', '['),
      Map.entry('ъ', ']'),
      Map.entry('ф', 'a'),
      Map.entry('ы', 's'),
      Map.entry('в', 'd'),
      Map.entry('а', 'f'),
      Map.entry('п', 'g'),
      Map.entry('р', 'h'),
      Map.entry('о', 'j'),
      Map.entry('л', 'k'),
      Map.entry('д', 'l'),
      Map.entry('ж', ';'),
      Map.entry('э', '\''),
      Map.entry('я', 'z'),
      Map.entry('ч', 'x'),
      Map.entry('с', 'c'),
      Map.entry('м', 'v'),
      Map.entry('и', 'b'),
      Map.entry('т', 'n'),
      Map.entry('ь', 'm'),
      Map.entry('б', ','),
      Map.entry('ю', '.'),
      Map.entry('ё', '`')
   );

   public static ChatCorrector.DataRecord m_9_C0(String text) {
      if (text != null && text.length() > 1) {
         String text2 = text.substring(1);
         if (getEnabledChatCorrector() == null) {
            return ChatCorrector.DataRecord.m_1_C0(text2);
         }

         if (isIrcCommand(text)) {
            return ChatCorrector.DataRecord.m_1_C0(text2);
         }

         if (isCommand(getFirstWord(text2))) {
            return ChatCorrector.DataRecord.m_1_C0(text2);
         }

         String text3 = correctPlain(text2);
         if (!text3.equals(text2)) {
            return ChatCorrector.DataRecord.clientMessage(text3);
         }

         String text4 = correct(text2);
         return !text4.equals(text2) ? ChatCorrector.DataRecord.serverMessage(text4) : ChatCorrector.DataRecord.m_1_C0(text2);
      } else {
         return ChatCorrector.DataRecord.m_1_C0("");
      }
   }

   public static String correctCommand(String text) {
      if (getEnabledChatCorrector() == null) {
         return text;
      } else if (text != null && text.startsWith("/") && text.length() > 1) {
         String text2 = text.substring(1);
         String text3 = correct(text2);
         return text3.equals(text2) ? text : "/" + text3;
      } else {
         return text;
      }
   }

   private static ChatCorrector getEnabledChatCorrector() {
      ChatCorrector chatCorrector = Onetap.MODULE_MANAGER.get(ChatCorrector.class);
      return chatCorrector != null && chatCorrector.isEnabled() ? chatCorrector : null;
   }

   private static String correctPlain(String text) {
      String text2 = fixLayout(text);
      if (text2.equals(text)) {
         return text;
      }

      String text3 = getFirstWord(text2).toLowerCase(Locale.ROOT);
      return Onetap.COMMAND_MANAGER.getStringMap().get(text3) == null ? text : text2;
   }

   private static String correct(String text) {
      String text2 = fixLayout(text);
      if (text2.equals(text)) {
         return text;
      }

      String text3 = getFirstWord(text);
      String text4 = getFirstWord(text2);
      return text4.equals(text3) ? text : text2;
   }

   private static String getFirstWord(String text) {
      String text2 = text.stripLeading();
      int value = text2.indexOf(32);
      return value == -1 ? text2 : text2.substring(0, value);
   }

   private static boolean isIrcCommand(String text) {
      String text2 = text.toLowerCase(Locale.ROOT);
      return text2.equals(".irc") || text2.startsWith(".irc ");
   }

   private static String fixLayout(String text) {
      UnHook.m_7_IArr();
      StringBuilder stringBuilder = new StringBuilder(text.length());

      for (int i = 0; i < text.length(); i++) {
         char ch = text.charAt(i);
         char ch2 = Character.toLowerCase(ch);
         Character character = CHARACTER_MAP.get(ch2);
         if (character == null) {
            stringBuilder.append(ch);
         } else if (Character.isUpperCase(ch)) {
            stringBuilder.append(Character.toUpperCase(character));
         } else {
            stringBuilder.append(character);
         }
      }

      return stringBuilder.toString();
   }

   private static boolean isCommand(String text) {
      return text != null && !text.isBlank() ? Onetap.COMMAND_MANAGER.getStringMap().get(text.toLowerCase(Locale.ROOT)) != null : false;
   }

   public record DataRecord(ChatCorrector.ModeKind f_66, String f_6_b) {
      public static ChatCorrector.DataRecord m_1_C0(String text) {
         return new ChatCorrector.DataRecord(ChatCorrector.ModeKind.NONE, text);
      }

      public static ChatCorrector.DataRecord clientMessage(String text) {
         return new ChatCorrector.DataRecord(ChatCorrector.ModeKind.CLIENT, text);
      }

      public static ChatCorrector.DataRecord serverMessage(String text) {
         return new ChatCorrector.DataRecord(ChatCorrector.ModeKind.SERVER, text);
      }

      public ChatCorrector.ModeKind m_66() {
         return this.f_66;
      }

      public String m_6_b() {
         return this.f_6_b;
      }
   }

   public enum ModeKind {
      NONE,
      CLIENT,
      SERVER;

      private static String a(byte[] byteArray) {
         int value = 0;
         int value2;
         char[] charArray = new char[value2 = byteArray.length];

         for (int i = 0; i < value2; i++) {
            int value3;
            if ((value3 = 255 & byteArray[i]) < 192) {
               charArray[value++] = (char)value3;
            } else if (value3 < 224) {
               char ch = (char)((char)(value3 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value++] = ch;
            } else if (i < value2 - 2) {
               char ch2 = (char)((char)(value3 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value++] = ch2;
            }
         }

         return new String(charArray, 0, value);
      }
   }
}
