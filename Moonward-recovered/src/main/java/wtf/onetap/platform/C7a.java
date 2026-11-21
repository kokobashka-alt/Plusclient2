package wtf.onetap.platform;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.jetbrains.annotations.Nullable;
import ston.base.client.link.StonLink;
import wtf.onetap.module.ModuleManager;
import wtf.onetap.obf.b;

public class C7a {
   private static volatile String text;
   private static final long LONG_VALUE = wtf.onetap.obf.b.a(1327174042755237526L, 7360325715393658616L, MethodHandles.lookup().lookupClass()).a(175454977330582L);
   private static final Object[] OBJECT_ARRAY = new Object[23];
   private static final String[] TEXT_ARRAY = new String[23];

   public static String m_3_String() {
      ModuleManager.m_1_StringArr();
      StonLink stonLink = getStonLink();
      String text2 = stonLink != null ? stonLink.getAccountName() : null;
      if (text2 != null && !text2.equals(text)) {
         text = text2;
      }

      return text;
   }

   @Nullable
   public static String m_1_String() {
      StonLink stonLink = getStonLink();
      return stonLink != null ? stonLink.getUserId() : null;
   }

   @Nullable
   public static String m_4_String() {
      StonLink stonLink = getStonLink();
      return stonLink != null ? stonLink.getVisibleUid() : null;
   }

   @Nullable
   public static String m_0_String() {
      StonLink stonLink = getStonLink();
      return stonLink != null ? stonLink.getTopRole() : null;
   }

   @Nullable
   public static String m_8_String() {
      StonLink stonLink = getStonLink();
      return stonLink != null ? stonLink.getAvatarUrl() : null;
   }

   @Nullable
   public static Instant m_1_Instant() {
      StonLink stonLink = getStonLink();
      return stonLink != null ? stonLink.getExpiresAt() : null;
   }

   @Nullable
   private static StonLink getStonLink() {
      return PlatformConnection.PLATFORM_CONNECTION.getStonLink();
   }

   private C7a() {
   }

   static {
      try {
         long longValue = LONG_VALUE ^ 108075666886084L;
         a();
         Cipher cipher;
         Cipher cipher2 = cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
         SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
         byte[] byteArray = new byte[]{(byte)(longValue >>> 56), 0, 0, 0, 0, 0, 0, 0};

         for (int i = 1; i < 8; i++) {
            byteArray[i] = (byte)(longValue << i * 8 >>> 56);
         }

         cipher2.init(2, secretKeyFactory.generateSecret(new DESKeySpec(byteArray)), new IvParameterSpec(new byte[8]));
         byte[] byteArray2 = cipher.doFinal("Nà$û\u001fÊZ\u0000BÝRü3f\nß".getBytes("ISO-8859-1"));
         a(byteArray2);
         byte byteValue = -1;
         String text2 = "Гость";
         text = text2;
      } catch (java.security.GeneralSecurityException | java.io.UnsupportedEncodingException e) {
         throw new ExceptionInInitializerError(e);
      }
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

   private static void a() {
      OBJECT_ARRAY[0] = "\f|\u007fJ1@\u001e|x\u0014p\u0019\u001a";
      OBJECT_ARRAY[1] = "Al3\u001cJ\u0010Sk9\\\u0007\u001e[}2\u0006J\u001e[v7\\7\u0006]v\u0010\u001b\n\u0019";
      OBJECT_ARRAY[2] = "\u0016\u0001!\u0015\u0014\u0010\u0004\u0001&KUF\u000b";
      OBJECT_ARRAY[3] = "\u00174]\b\u001d\u0019\u001c;LG`\u0001\u000f<E\u000e";
      OBJECT_ARRAY[4] = "z'P\u0010r-h'WN3{:";
      OBJECT_ARRAY[5] = "Va)b\u001dB#A\"m\f\r^Y1j\u0005D6";
      OBJECT_ARRAY[6] = "\u0001#N&\u0007j\n,_ifd\u0001'[3";
      OBJECT_ARRAY[7] = boolean.class;
      TEXT_ARRAY[7] = "java/lang/Boolean";
      OBJECT_ARRAY[8] = "uB}6\u0001agBzh@6t";
      OBJECT_ARRAY[9] = "f\"!9`\"e.2v\u00078\u007f766:";
      OBJECT_ARRAY[10] = "-nOsf\u0014bwLg\u001b\u0016\u0010.MtcV/aTww";
      OBJECT_ARRAY[11] = "\u0007{\u000bP9\u001c\u0003{K>wf\u001aq\u0017\\0\u0000\b\u007f\u0005>";
      OBJECT_ARRAY[12] = ")V\u001ai\u0011d-VZ\u0007]\u001e~\u0010Tf\bf>MY>";
      OBJECT_ARRAY[13] = "x vs]w79ug rE-{hB3#?uz ";
      OBJECT_ARRAY[14] = " \\\u0014!H\u0001 X\u0018by\u0005\u001b\\[j\u001cAk\u001cB9\u001a<";
      OBJECT_ARRAY[15] = "*,\u001auE\u001c.,Z\u001b^f~-\u001cc\u000fY?i\\+7_>-\u001d#\b\u001ezmU\u001b";
      OBJECT_ARRAY[16] = "_#\u0004A%YM-\u0016#1RS\u000f\u0006X?UK<:B2?\u0015oVB5GU2[\u001a^";
      OBJECT_ARRAY[17] = "j\u001fF\u0003-Vx\u0011Ta9]f1B\u0019\fW~\u0000%[h\u0001{\u0006]\u001b5\f#m";
      OBJECT_ARRAY[18] = "\u0007,{\u000f_X\u0015\"imKS\u000b\u0017f\u0004XW\r\u0003b\t$\u0004Ioy\u0006\\D\u0014b!m";
      OBJECT_ARRAY[19] = "b4\u0003TW,p:\u00116C'n\u000b\u0010NM0\u007f=)J,t{&\u0001N\u00156\u007f-\u001c6";
      OBJECT_ARRAY[20] = "\u0019\f5\u001d\u000f\u001d\u000b\u0002'\u007f\u001b\u0016\u0015#-\u0012\u000e:\u0005~lAE\u001a\u0002\u0006,\u001cHBi";
      OBJECT_ARRAY[21] = "]P,Q7ZO^>3#QQk$X+AK^\tZ)Q-\u0018q\u0002-WUX,\u000fu<";
      OBJECT_ARRAY[22] = "`\n\u0016u>* W\u001b-U?#INp.RfT\u001f{9#:\nI.UocI\u0019w7?cMI\u0014";
   }
}
