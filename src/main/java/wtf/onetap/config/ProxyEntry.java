package wtf.onetap.config;

import com.google.gson.annotations.SerializedName;
import wtf.onetap.module.Module;

public class ProxyEntry {
   @SerializedName("IP:PORT")
   public String text;
   public ProxyEntry.ModeKind c6;
   public String text2;
   public String text3;
   private static Module[] moduleArray;

   public ProxyEntry() {
      this.text = "";
      this.c6 = ProxyEntry.ModeKind.SOCKS5;
      this.text2 = "";
      this.text3 = "";
   }

   public ProxyEntry(ProxyEntry.ModeKind c62, String text4, String text5, String text6) {
      this.text = "";
      m_8_C3_sArr();
      this.c6 = ProxyEntry.ModeKind.SOCKS5;
      this.text2 = "";
      this.text3 = "";
      this.c6 = c62;
      this.text = text4;
      this.text2 = text5;
      this.text3 = text6;
   }

   public int m_4_int() {
      if (this.text != null && !this.text.isEmpty() && this.text.contains(":")) {
         try {
            return Integer.parseInt(this.text.split(":")[1]);
         } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public String getHost() {
      return this.text != null && !this.text.isEmpty() && this.text.contains(":") ? this.text.split(":")[0] : "";
   }

   public static void m_3(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_8_C3_sArr() {
      return moduleArray;
   }

   public enum ModeKind {
      SOCKS4,
      SOCKS5;

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
