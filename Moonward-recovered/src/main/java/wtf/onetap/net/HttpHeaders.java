package wtf.onetap.net;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.util.Locale;
import wtf.onetap.staff.StaffSync;

public class HttpHeaders {
   private HttpHeaders() {
   }

   public static HttpURLConnection openConnection(URL uRL) throws java.io.IOException {
      HttpURLConnection httpURLConnection = (HttpURLConnection)(ProxySelector.getDefault() != null ? uRL.openConnection() : uRL.openConnection(Proxy.NO_PROXY));
      httpURLConnection.setInstanceFollowRedirects(true);
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setRequestProperty("User-Agent", m_6_String());
      httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.9,ru;q=0.8");
      httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
      return httpURLConnection;
   }

   public static void setTimeouts(HttpURLConnection httpURLConnection, int value, int value2) {
      httpURLConnection.setConnectTimeout(value);
      httpURLConnection.setReadTimeout(value2);
   }

   private static String m_6_String() {
      StaffSync.m_9_IArr();
      String text = System.getProperty("os.name", "Unknown").toLowerCase(Locale.ROOT);
      String text2 = System.getProperty("os.arch", "x64");
      if (!text.contains("mac") && !text.contains("darwin")) {
         return text.contains("linux")
            ? "Mozilla/5.0 (X11; Linux " + text2 + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            : "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
      } else {
         return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
      }
   }

   public static int getResponseCode(HttpURLConnection httpURLConnection) throws java.io.IOException {
      return httpURLConnection.getResponseCode();
   }
}
