package wtf.onetap.autobuy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TextSanitizer {
   private static final Set<String> STRING_SET = Collections.synchronizedSet(new HashSet<>());

   private TextSanitizer() {
   }

   public static boolean add(String text) {
      return text != null && !text.isBlank() ? STRING_SET.add(sanitize(text)) : false;
   }

   public static boolean remove(String text) {
      return text != null && !text.isBlank() ? STRING_SET.remove(sanitize(text)) : false;
   }

   public static boolean isBlacklisted(String text) {
      return text != null && !text.isBlank() ? STRING_SET.contains(sanitize(text)) : false;
   }

   public static Set<String> getBlacklist() {
      return Collections.unmodifiableSet(new HashSet<>(STRING_SET));
   }

   public static void m_1(Collection<String> stringList) {
      STRING_SET.clear();
      if (stringList != null) {
         for (String text : stringList) {
            if (text != null && !text.isBlank()) {
               STRING_SET.add(sanitize(text));
            }
         }
      }

      if (STRING_SET.isEmpty()) {
         STRING_SET.add("deletecheater");
      }
   }

   private static String sanitize(String text) {
      return text.replaceAll("§.", "").trim().toLowerCase();
   }

   static {
      STRING_SET.add("deletecheater");
   }
}
