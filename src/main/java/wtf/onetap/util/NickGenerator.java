package wtf.onetap.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import wtf.onetap.Onetap;

public class NickGenerator {
   private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
   private static List<String> stringList;

   private NickGenerator() {
   }

   public static String m_3_String() {
      return m_8(text -> false);
   }

   public static String m_8(Predicate<String> predicate) {
      m_7_void();
      if (stringList.isEmpty()) {
         return "Player" + ThreadLocalRandom.current().nextInt(1000, 9999);
      }

      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

      for (int i = 0; i < 64; i++) {
         String text = stringList.get(threadLocalRandom.nextInt(stringList.size()));
         if (!predicate.test(text)) {
            return text;
         }
      }

      return stringList.get(threadLocalRandom.nextInt(stringList.size()));
   }

   private static void m_7_void() {
      if (stringList == null) {
         stringList = new ArrayList<>();

         try {
            label70: {
               try (InputStream inputStream = NickGenerator.class.getResourceAsStream("/assets/onetap/nicks.txt")) {
                  if (inputStream != null) {
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                     String text;
                     while ((text = bufferedReader.readLine()) != null) {
                        text = text.strip();
                        if (PATTERN.matcher(text).matches()) {
                           stringList.add(text);
                        }
                     }
                     break label70;
                  }

                  Onetap.LOGGER.error("[Onetap] Nick corpus {} not found in jar", "/assets/onetap/nicks.txt");
               }

               return;
            }
         } catch (Exception e) {
            Onetap.LOGGER.error("[Onetap] Failed to read nick corpus", e);
         }
      }
   }
}
