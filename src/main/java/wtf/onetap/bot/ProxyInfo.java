package wtf.onetap.bot;

import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wtf.onetap.config.ProxyEntry;

public record ProxyInfo(String f_5, int f_4, String f_3, String f_7, ProxyEntry.ModeKind f_8) {
   private static final Pattern f_2 = Pattern.compile("(?i)^socks([45])h?(://|:)");

   public ProxyInfo(String f_5, int f_4, String f_3, String f_7, ProxyEntry.ModeKind f_8) {
      if (f_3 != null && f_3.isEmpty()) {
         f_3 = null;
      }

      if (f_7 != null && f_7.isEmpty()) {
         f_7 = null;
      }

      this.f_5 = f_5;
      this.f_4 = f_4;
      this.f_3 = f_3;
      this.f_7 = f_7;
      this.f_8 = f_8;
   }

   public static ProxyInfo m_7_C8_s(List<String> stringList) {
      if (stringList != null && !stringList.isEmpty()) {
         byte byteValue = 0;
         Integer integer = parseSocksVersion((String)stringList.get(0));
         if (integer != null) {
            byteValue = 1;
            if (byteValue >= stringList.size()) {
               return null;
            }
         }

         String text = (String)stringList.get(byteValue);
         if (text != null && !text.isBlank()) {
            ProxyInfo.DataRecord c0 = m_2_C0(text);
            if (c0.f_7r != null) {
               if (integer != null && !integer.equals(c0.f_7r)) {
                  return null;
               }

               integer = c0.f_7r;
               text = c0.f_1;
            }

            if (text != null && !text.isBlank()) {
               ProxyInfo.DataRecord2 c9 = m_6_C9(text);
               if (c9 == null) {
                  return null;
               }

               String text2 = c9.f_9;
               String text3 = c9.f_0;
               if (byteValue + 1 < stringList.size()) {
                  text2 = (String)stringList.get(byteValue + 1);
                  if (byteValue + 2 < stringList.size()) {
                     text3 = String.join(" ", stringList.subList(byteValue + 2, stringList.size()));
                  }
               }

               if (text2 != null && text2.isEmpty()) {
                  text2 = null;
               }

               if (text3 != null && text3.isEmpty()) {
                  text3 = null;
               }

               ProxyEntry.ModeKind c6;
               if (integer != null) {
                  c6 = integer == 4 ? ProxyEntry.ModeKind.SOCKS4 : ProxyEntry.ModeKind.SOCKS5;
               } else {
                  c6 = text3 != null ? ProxyEntry.ModeKind.SOCKS5 : ProxyEntry.ModeKind.SOCKS4;
               }

               return new ProxyInfo(c9.f_2, c9.f_6, text2, text3, c6);
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public ProxyHandler m_9_ProxyHandler() {
      InetSocketAddress inetSocketAddress = new InetSocketAddress(this.f_5, this.f_4);
      return this.f_8 == ProxyEntry.ModeKind.SOCKS5 ? new Socks5ProxyHandler(inetSocketAddress, this.f_3, this.f_7) : new Socks4ProxyHandler(inetSocketAddress, this.f_3);
   }

   private static Integer parseSocksVersion(String text) {
      if (text == null) {
         return null;
      }

      return switch (text.toLowerCase(Locale.ROOT)) {
         case "socks4", "socks4h" -> 4;
         case "socks5", "socks5h" -> 5;
         default -> null;
      };
   }

   private static ProxyInfo.DataRecord m_2_C0(String text) {
      Matcher matcher = f_2.matcher(text);
      if (!matcher.find()) {
         return new ProxyInfo.DataRecord(null, text);
      }

      int value = matcher.group(1).charAt(0) == '4' ? 4 : 5;
      return new ProxyInfo.DataRecord(value, text.substring(matcher.end()));
   }

   private static ProxyInfo.DataRecord2 m_6_C9(String text) {
      int value = text.lastIndexOf(64);
      if (value >= 0) {
         if (value != 0 && value != text.length() - 1) {
            ProxyInfo.DataRecord2 c9 = m_8_C9(text.substring(value + 1), false);
            if (c9 == null) {
               return null;
            }

            String text2 = text.substring(0, value);
            int value2 = text2.indexOf(58);
            String text3 = value2 < 0 ? text2 : text2.substring(0, value2);
            String text4 = value2 < 0 ? null : text2.substring(value2 + 1);
            return text3.isEmpty() ? null : new ProxyInfo.DataRecord2(c9.f_2, c9.f_6, text3, text4);
         } else {
            return null;
         }
      } else {
         return m_8_C9(text, true);
      }
   }

   private static ProxyInfo.DataRecord2 m_8_C9(String text, boolean flag) {
      if (text.startsWith("[")) {
         int value = text.indexOf(93);
         if (value <= 1) {
            return null;
         }

         String text2 = text.substring(1, value);
         String text3 = text.substring(value + 1);
         return !text3.startsWith(":") ? null : m_7(text2, text3.substring(1), flag);
      } else {
         int value2 = text.indexOf(58);
         return value2 <= 0 ? null : m_7(text.substring(0, value2), text.substring(value2 + 1), flag);
      }
   }

   private static ProxyInfo.DataRecord2 m_7(String text, String text2, boolean flag) {
      if (text != null && !text.isBlank() && text.indexOf(32) < 0) {
         int value = text2.indexOf(58);
         String text3 = null;
         String text4 = null;
         String text5;
         if (value < 0) {
            text5 = text2;
         } else {
            if (!flag) {
               return null;
            }

            text5 = text2.substring(0, value);
            String text6 = text2.substring(value + 1);
            int value2 = text6.indexOf(58);
            if (value2 < 0) {
               text3 = text6;
            } else {
               text3 = text6.substring(0, value2);
               text4 = text6.substring(value2 + 1);
            }
         }

         int value3 = m_6_int(text5);
         return value3 < 0 ? null : new ProxyInfo.DataRecord2(text, value3, text3, text4);
      } else {
         return null;
      }
   }

   private static int m_6_int(String text) {
      if (text != null && !text.isEmpty()) {
         try {
            int value = Integer.parseInt(text);
            return value >= 1 && value <= (int)8851199853502463999L ? value : -1;
         } catch (NumberFormatException numberFormatException) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public String m_5_String() {
      return this.f_5;
   }

   public int m_4_int() {
      return this.f_4;
   }

   public String m_3_String() {
      return this.f_3;
   }

   public String m_7_String() {
      return this.f_7;
   }

   public ProxyEntry.ModeKind m_8_C6() {
      return this.f_8;
   }

   private record DataRecord(Integer f_7r, String f_1) {
      public Integer m_7r_Integer() {
         return this.f_7r;
      }

      public String m_1_String() {
         return this.f_1;
      }
   }

   private record DataRecord2(String f_2, int f_6, String f_9, String f_0) {
      public String m_2_String() {
         return this.f_2;
      }

      public int m_6_int() {
         return this.f_6;
      }

      public String m_9_String() {
         return this.f_9;
      }

      public String m_0_String() {
         return this.f_0;
      }
   }
}
