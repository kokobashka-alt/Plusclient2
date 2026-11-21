package wtf.onetap.platform;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class C51 {
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
   private static final DateTimeFormatter DATE_TIME_FORMATTER2 = DateTimeFormatter.ofPattern("HH:mm");

   public static String m_1(Instant instant, ZoneId zoneId) {
      if (instant == null) {
         return "N/A";
      } else {
         Instant instant2 = Instant.now();
         if (instant.isBefore(instant2)) {
            return "Исткла";
         } else {
            Duration duration = Duration.between(instant2, instant);
            if (duration.toDays() > 3650L) {
               return "Lifetime";
            } else {
               return duration.compareTo(Duration.ofHours(24L)) < 0 ? instant.atZone(zoneId).format(DATE_TIME_FORMATTER2) : instant.atZone(zoneId).format(DATE_TIME_FORMATTER);
            }
         }
      }
   }

   public static String m_1(Instant instant) {
      return m_1(instant, ZoneId.systemDefault());
   }
}
