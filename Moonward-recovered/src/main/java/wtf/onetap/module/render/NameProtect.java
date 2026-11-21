package wtf.onetap.module.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "NameProtect", description = "Локально скрывает ваш ник в чате, табе, скорборде, тегах и таргет худе", category = Category.RENDER)
public class NameProtect extends Module {
   private final BooleanSetting workOnFriends = new BooleanSetting("Работать на друзей", true).build();
   private static int[] valueArray;

   private String getPlayerName() {
      return MinecraftAccess.MINECRAFT.player == null ? null : MinecraftAccess.MINECRAFT.player.getName().getString();
   }

   public String replaceInText(String text) {
      if (this.isEnabled() && text != null && !text.isEmpty()) {
         String text2 = this.getPlayerName();
         if (text2 != null && text2.equalsIgnoreCase(text)) {
            return "Moonward";
         } else {
            return this.workOnFriends.getValue() && Onetap.FRIEND_MANAGER.isFriend(text) ? "Moonward" : text;
         }
      } else {
         return text;
      }
   }

   public Component replaceInComponent(Component component) {
      if (this.isEnabled() && component != null) {
         var stringList = this.getProtectedNames();
         if (stringList.isEmpty()) {
            return component;
         }

         MutableComponent mutableComponent = Component.empty();
         component.visit((style, text) -> {
            String text2 = this.replaceNames(text, stringList);
            mutableComponent.append(Component.literal(text2).setStyle(style));
            return Optional.empty();
         }, Style.EMPTY);
         return mutableComponent;
      } else {
         return component;
      }
   }

   private List<String> getProtectedNames() {
      ArrayList list = new ArrayList();
      String text = this.getPlayerName();
      if (text != null && !text.isEmpty()) {
         list.add(text);
      }

      if ((Boolean)this.workOnFriends.getValue()) {
         list.addAll(Onetap.FRIEND_MANAGER.getFriends());
      }

      return list;
   }

   private String replaceNames(String text, List<String> stringList) {
      if (text != null && !text.isEmpty()) {
         StringBuilder stringBuilder = new StringBuilder(text.length());
         int value = 0;

         while (value < text.length()) {
            String text2 = this.findMatch(text, value, stringList);
            if (text2 != null) {
               stringBuilder.append("Moonward");
               value += text2.length();
            } else {
               stringBuilder.append(text.charAt(value));
               value++;
            }
         }

         return stringBuilder.toString();
      } else {
         return text;
      }
   }

   private String findMatch(String text, int value, List<String> stringList) {
      for (String text2 : stringList) {
         if (this.matchesAt(text, value, text2)) {
            return text2;
         }
      }

      return null;
   }

   private boolean matchesAt(String text, int value, String text2) {
      if (value + text2.length() > text.length()) {
         return false;
      }

      for (int i = 0; i < text2.length(); i++) {
         if (Character.toLowerCase(text.charAt(value + i)) != Character.toLowerCase(text2.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static void m_2(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_2_IArr() {
      return valueArray;
   }

   static {
      m_2(null);
   }
}
