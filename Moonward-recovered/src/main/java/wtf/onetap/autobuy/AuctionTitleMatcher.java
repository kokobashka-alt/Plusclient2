package wtf.onetap.autobuy;

import java.util.Locale;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class AuctionTitleMatcher {
   public static boolean matches(AbstractContainerScreen<?> abstractContainerScreen) {
      if (abstractContainerScreen == null) {
         return false;
      }

      String text = m_7_String(abstractContainerScreen.getTitle().getString());
      return m_2_boolean(text);
   }

   private static boolean m_2_boolean(String text) {
      return text.contains("аукцион")
         || text.contains("auction")
         || text.contains("поиск:")
         || text.contains("search:")
         || text.contains("п:")
         || text.contains("漢:")
         || text.contains("��:")
         || text.contains("��:");
   }

   private static String m_7_String(String text) {
      return text == null
         ? ""
         : text.replaceAll("(?i)§.", "")
            .replace(' ', ' ')
            .replace((char)((int)-5835010987727454438L), ':')
            .replaceAll("\\s*:\\s*", ":")
            .trim()
            .toLowerCase(Locale.ROOT);
   }
}
