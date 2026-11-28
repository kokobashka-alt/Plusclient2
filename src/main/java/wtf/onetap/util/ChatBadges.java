package wtf.onetap.util;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class ChatBadges {
   private static final String[][] TEXT_ARRAY = new String[][]{
      {"ꕗꕘ", ChatFormatting.YELLOW + "SPONSOR"},
      {"ꔗ", ChatFormatting.BLUE + "MODER"},
      {"ꔥ", ChatFormatting.BLUE + "ST.MODER"},
      {"ꔡ", ChatFormatting.LIGHT_PURPLE + "MODER+"},
      {"ꔀ", ChatFormatting.GRAY + "PLAYER"},
      {"ꔉ", ChatFormatting.YELLOW + "HELPER"},
      {"ꔳ", ChatFormatting.AQUA + "ML.ADMIN"},
      {"ꔅ", ChatFormatting.RED + "Y" + ChatFormatting.WHITE + "T"},
      {"ꔂ", ChatFormatting.BLUE + "D.MODER"},
      {"ꕠ", ChatFormatting.YELLOW + "D.HELPER"},
      {"ꕄ", ChatFormatting.RED + "DRACULA"},
      {"ꔖ", ChatFormatting.AQUA + "OVERLORD"},
      {"ꕈ", ChatFormatting.GREEN + "COBRA"},
      {"ꔨ", ChatFormatting.LIGHT_PURPLE + "DRAGON"},
      {"ꔤ", ChatFormatting.RED + "IMPERATOR"},
      {"ꔠ", ChatFormatting.GOLD + "MAGISTER"},
      {"ꔄ", ChatFormatting.BLUE + "HERO"},
      {"ꔒ", ChatFormatting.GREEN + "AVENGER"},
      {"ꕒ", ChatFormatting.WHITE + "RABBIT"},
      {"ꔈ", ChatFormatting.YELLOW + "TITAN"},
      {"ꕀ", ChatFormatting.DARK_GREEN + "HYDRA"},
      {"ꔶ", ChatFormatting.GOLD + "TIGER"},
      {"ꔲ", ChatFormatting.DARK_PURPLE + "BULL"},
      {"ꕖ", ChatFormatting.BLACK + "BUNNY"},
      {"ꕓ", ChatFormatting.DARK_GRAY + "GHOST"},
      {"ꔦ", ChatFormatting.BLUE + "D.ML.ADMIN"},
      {"ꔰ", ChatFormatting.GRAY + "D.ML.ADMIN"},
      {"ꔆ", ChatFormatting.GRAY + "D.MODER"},
      {"ꔐ", ChatFormatting.DARK_BLUE + "D.GL.MODER"},
      {"ꔔ", ChatFormatting.GRAY + "D.GL.MODER"},
      {"ꔢ", ChatFormatting.GRAY + "D.ST.MODER"},
      {"ꔘ", ChatFormatting.BLUE + "D.ST.MODER"},
      {"ꕡ", ChatFormatting.GOLD + "ST.HELPER"},
      {"ꕅ", ChatFormatting.DARK_PURPLE + "MEDIA+"},
      {"ꔁ", ChatFormatting.DARK_PURPLE + "MEDIA"},
      {"ꕗ", ChatFormatting.DARK_RED + "D.ADMIN"},
      {"ꕁ", ChatFormatting.GOLD + "LEGENDA"},
      {"◆", "@"},
      {"┃", "|"},
      {"\ud83d\udd25", "@"},
      {"ᴀ", "A"},
      {"ʙ", "B"},
      {"ᴄ", "C"},
      {"ᴅ", "D"},
      {"ᴇ", "E"},
      {"ғ", "F"},
      {"ꜰ", "F"},
      {"ɢ", "G"},
      {"ʜ", "H"},
      {"ɪ", "I"},
      {"ᴊ", "J"},
      {"ᴋ", "K"},
      {"ʟ", "L"},
      {"ᴍ", "M"},
      {"ɴ", "N"},
      {"ᴏ", "O"},
      {"ᴘ", "P"},
      {"ǫ", "Q"},
      {"ʀ", "R"},
      {"ꜱ", "S"},
      {"ᴛ", "T"},
      {"ᴜ", "U"},
      {"ᴠ", "V"},
      {"ᴡ", "W"},
      {"ʏ", "Y"},
      {"ᴢ", "Z"},
      {"ပ", ""},
      {"္", ""},
      {"း", ""},
      {"ႏ", ""}
   };
   private static String text;

   private ChatBadges() {
   }

   public static String m_1_String(String text2) {
      if (text2 != null && !text2.isEmpty()) {
         for (String[] stringArray : TEXT_ARRAY) {
            text2 = text2.replace(stringArray[0], ChatFormatting.stripFormatting(stringArray[1]));
         }

         return ChatFormatting.stripFormatting(text2);
      } else {
         return text2;
      }
   }

   /**
    * Replaces the RoleBadges glyphs with their coloured labels. Every other part of the component keeps its style.
    * Used by the name tags only, after the name has been substituted, so the labels cannot be taken for the player's name.
    */
   public static Component m_roles(Component component) {
      MutableComponent mutableComponent = Component.empty();
      component.visit((style, text2) -> {
         StringBuilder plain = new StringBuilder();
         for (int i = 0; i < text2.length(); i++) {
            RoleBadges.Role role = RoleBadges.of(text2.charAt(i));
            if (role == null) {
               plain.append(text2.charAt(i));
               continue;
            }

            if (!plain.isEmpty()) {
               mutableComponent.append(Component.literal(plain.toString()).setStyle(style));
               plain.setLength(0);
            }

            mutableComponent.append(Component.literal(role.label()).setStyle(style.withColor(TextColor.fromRgb(role.rgb()))));
         }

         if (!plain.isEmpty()) {
            mutableComponent.append(Component.literal(plain.toString()).setStyle(style));
         }

         return Optional.empty();
      }, Style.EMPTY);
      return mutableComponent;
   }

   public static Component m_6(Component component) {
      MutableComponent mutableComponent = Component.empty();
      component.visit((style, text2) -> {
         m_0(mutableComponent, text2, style);
         return Optional.empty();
      }, Style.EMPTY);
      return mutableComponent;
   }

   public static Component m_8_Component(Component component) {
      MutableComponent mutableComponent = Component.empty();
      component.visit((style, text2) -> {
         m_8(mutableComponent, text2, style);
         return Optional.empty();
      }, Style.EMPTY);
      return mutableComponent;
   }

   private static void m_0(MutableComponent mutableComponent, String text2, Style style) {
      Style style2 = style;
      int value = 0;

      for (int i = 0; i < text2.length(); i++) {
         String[] stringArray = findBadge(text2, i);
         if (stringArray != null) {
            style2 = m_8(mutableComponent, text2.substring(value, i), style2);
            m_8(mutableComponent, stringArray[1], style2);
            i += stringArray[0].length() - 1;
            value = i + 1;
         }
      }

      m_8(mutableComponent, text2.substring(value), style2);
   }

   private static Style m_8(MutableComponent mutableComponent, String text2, Style style) {
      Style style2 = style;
      ChatUtil.m_3_IArr();
      int value = 0;

      for (int i = 0; i < text2.length() - 1; i++) {
         if (text2.charAt(i) == 167) {
            if (i > value) {
               mutableComponent.append(Component.literal(text2.substring(value, i)).setStyle(style2));
            }

            ChatFormatting chatFormatting = ChatFormatting.getByCode(text2.charAt(++i));
            if (chatFormatting != null) {
               style2 = chatFormatting == ChatFormatting.RESET ? style : style2.applyLegacyFormat(chatFormatting);
            }

            value = i + 1;
         }
      }

      if (value < text2.length()) {
         mutableComponent.append(Component.literal(text2.substring(value)).setStyle(style2));
      }

      return style2;
   }

   private static String[] findBadge(String text2, int value) {
      for (String[] stringArray : TEXT_ARRAY) {
         if (text2.startsWith(stringArray[0], value)) {
            return stringArray;
         }
      }

      return null;
   }

   public static void m_3_void(String text2) {
      text = text2;
   }

   public static String m_5_String() {
      return text;
   }

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
