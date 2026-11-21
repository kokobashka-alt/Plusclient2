package wtf.onetap.render;

import java.lang.invoke.MethodHandles;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.obf.b;

public class WaterSplashRingParticle extends WaterSplashParticle {
   private static final Identifier[] IDENTIFIER_ARRAY;
   private static final long LONG_VALUE = wtf.onetap.obf.b.a(5917463264711458628L, -1637671305766764498L, MethodHandles.lookup().lookupClass()).a(161466175445483L);
   private static final Object[] OBJECT_ARRAY = new Object[42];
   private static final String[] TEXT_ARRAY = new String[42];

   public WaterSplashRingParticle(ClientLevel clientLevel, double doubleValue, double doubleValue2, double doubleValue3, float floatValue2) {
      super(clientLevel, doubleValue, doubleValue2, doubleValue3, floatValue2, 0.0F, IDENTIFIER_ARRAY);
   }

   public @Override
   void m_0(WaterSplashParticleGroup.StateHolder c7, Vec3 vec3, float floatValue2) {
      float floatValue3 = (float)(Mth.lerp(floatValue2, super.xo, super.x) - vec3.x);
      float floatValue4 = (float)(Mth.lerp(floatValue2, super.yo, super.y) - vec3.y);
      float floatValue5 = (float)(Mth.lerp(floatValue2, super.zo, super.z) - vec3.z);
      float floatValue6 = this.m_5_float(floatValue2);
      float floatValue7 = floatValue3 - floatValue6;
      float floatValue8 = floatValue3 + floatValue6;
      float floatValue9 = floatValue5 - floatValue6;
      float floatValue10 = floatValue5 + floatValue6;
      float floatValue11 = super.textureAtlasSprite.getU0();
      float floatValue12 = super.textureAtlasSprite.getU1();
      float floatValue13 = super.textureAtlasSprite.getV0();
      float floatValue14 = super.textureAtlasSprite.getV1();
      int value2 = this.getLightCoords(floatValue2);
      c7.m_3(floatValue7, floatValue4, floatValue9, floatValue12, floatValue14, super.value, value2);
      c7.m_3(floatValue7, floatValue4, floatValue10, floatValue12, floatValue13, super.value, value2);
      c7.m_3(floatValue8, floatValue4, floatValue10, floatValue11, floatValue13, super.value, value2);
      c7.m_3(floatValue8, floatValue4, floatValue9, floatValue11, floatValue14, super.value, value2);
   }

   static {
      try {
         long longValue = LONG_VALUE ^ 114009301605854L;
         b_void();
         Cipher cipher;
         Cipher cipher2 = cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
         SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
         byte[] byteArray = new byte[]{(byte)(longValue >>> 56), 0, 0, 0, 0, 0, 0, 0};

         for (int i = 1; i < 8; i++) {
            byteArray[i] = (byte)(longValue << i * 8 >>> 56);
         }

         cipher2.init(2, secretKeyFactory.generateSecret(new DESKeySpec(byteArray)), new IvParameterSpec(new byte[8]));
         byte[] byteArray2 = cipher.doFinal("4?üº\u009eÊí¹Û\u008fÊ\u008dÛ\u0013n\u0014`\u0013Æ\u0096\"½\bÍ".getBytes("ISO-8859-1"));
         b(byteArray2);
         byte byteValue = -1;
         String text = "water_splash_ring";
         IDENTIFIER_ARRAY = WaterSplashParticle.m_1_IdentifierArr(text);
      } catch (java.security.GeneralSecurityException | java.io.UnsupportedEncodingException e) {
         throw new ExceptionInInitializerError(e);
      }
   }

   private static String b(byte[] byteArray) {
      int value2 = 0;
      int value3;
      char[] charArray = new char[value3 = byteArray.length];

      for (int i = 0; i < value3; i++) {
         int value4;
         if ((value4 = 255 & byteArray[i]) < 192) {
            charArray[value2++] = (char)value4;
         } else if (value4 < 224) {
            char ch = (char)((char)(value4 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value2++] = ch;
         } else if (i < value3 - 2) {
            char ch2 = (char)((char)(value4 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value2++] = ch2;
         }
      }

      return new String(charArray, 0, value2);
   }

   private static void b_void() {
      OBJECT_ARRAY[0] = "\t \u000eA\u0018b\u001b \t\u001fY:<";
      OBJECT_ARRAY[1] = "2xlTXx\u0004]lTO$\bRv\u001f^3\u001a[wCO3\u001a\u001aKUI8\u001d]dXI$R";
      OBJECT_ARRAY[2] = ".HWWZl<HP\t\u001b4k";
      OBJECT_ARRAY[3] = "7t|\u0005\u001e\u001a<{mJc\u0002/|d\u0003";
      OBJECT_ARRAY[4] = "\t`6v26?E6v%j3J,=4}!C-a%}!\u0002\u0011w#v&E>z#ji";
      OBJECT_ARRAY[5] = double.class;
      TEXT_ARRAY[5] = "java/lang/Double";
      OBJECT_ARRAY[6] = "p%#2pap%4n|njn4ptmp4ynxfz%%yo&j%/hhz{n\u0003ye|k22]id\u007f3\u0004loaj%";
      OBJECT_ARRAY[7] = float.class;
      TEXT_ARRAY[7] = "java/lang/Float";
      OBJECT_ARRAY[8] = int.class;
      TEXT_ARRAY[8] = "java/lang/Integer";
      OBJECT_ARRAY[9] = "?&nN\u001f\"?&y\u0012\u0013-%mm\u000f\u0000'5mj\b\u000b8\u007f\u0015\u007f\u0003A";
      OBJECT_ARRAY[10] = "7'I#P\u000f%'N}\u0011R\u0013";
      OBJECT_ARRAY[11] = "}|4\u001c\nro|3BK*A";
      OBJECT_ARRAY[12] = void.class;
      TEXT_ARRAY[12] = "java/lang/Void";
      OBJECT_ARRAY[13] = "%jZ\f>\u00147j]R\u007fB8";
      OBJECT_ARRAY[14] = "\u0001=bbZ6\u0013=e<\u001bn=m3";
      OBJECT_ARRAY[15] = "-V\b\u0017P\r-V\u001fK\\\u00027\u001d\tMT\bm~\bQ";
      OBJECT_ARRAY[16] = "_{l~\u0007:Tt}1f4_\u007fyk";
      OBJECT_ARRAY[17] = "8\u0015t8b\u001bd\twj\u001e\u000bi\u0004Jk\u001eY4\u001ambd\t{\u001aeR";
      OBJECT_ARRAY[18] = "(LqvsCtPr$\u000fSy]O$\u000f\u0001$Ch,uQkC`\u001c";
      OBJECT_ARRAY[19] = "X\u00008u=\bL\u000eo\u0007%\u00045\n0~5\u0001Y].b+";
      OBJECT_ARRAY[20] = "\u0016\u0017\u007f\f+\u0019RE%VKQ)\u001d8\bp\r\u0016\u0016s\u0012{i";
      OBJECT_ARRAY[21] = "T\bXE\u007f<\t\u001d\u0019\f\u0015vjJA\rte\u0006\u001d_\u0011j";
      OBJECT_ARRAY[22] = "\u0011-\tS}5\u00113\u0001Q\u0011}.o\u0012\u000b~?@n\u001e^kM\u001e=\u000eNh0\u00124]\u000f\u0011";
      OBJECT_ARRAY[23] = "\u000e\u001696Su\u001a\u0018nDK\u001eXD+%Xr\u000fZ7;";
      OBJECT_ARRAY[24] = "\\\u0001\u0005|\rRH\u000fR\u000e_9\n\u000f\u0001o\u0001BJW_?";
      OBJECT_ARRAY[25] = ".]BZQe(\rNP/<\u0017\u0001NQU1mQ\u0001Q]\u0001*\f\u001cI\u001f{zC\u001cA/";
      OBJECT_ARRAY[26] = "Vl0U=NVr8WQ\u000ei.+\r>D\u0007/'X+6";
      OBJECT_ARRAY[27] = "-E:6\u0019Z-P&6h\u0000\u001c\u0001z=\u0012\u000bfQ5=\u001a;!\f(%XAqC(-h\u0006,^0o\u0012Vc^8_U\u000b~Fz%\u0005D~NJbXYf\f02\u0017Yn<w/QTnRv#\u0004A\u001c\u0001:f\u0007Ir\u000063\u0012;,S&#\u0011F Zubh";
      OBJECT_ARRAY[28] = "}.omi`{~cg\u0017=Du)c,`{~by'\u0004\u007fp<er\u007f?(b5\u0017";
      OBJECT_ARRAY[29] = "n)y\rw8z'.\u007fl4\u0003#q\u0006\u007f1oto\u001aa";
      OBJECT_ARRAY[30] = "`]r\\2:tS%.+6\rWzW:3a\u0000dK$";
      OBJECT_ARRAY[31] = "t\n\u000eY\u007fd`\u0004Y+ybe\u007f\u0004DvsR\\\u0002Qzt\u0019\u0006UIl?cV\u001aId\u000f$K\\Dda%G\tQ\u0016";
      OBJECT_ARRAY[32] = "(G\u000ffTmuRN/>&\u0016\u0005\u0016._4zR\b2A";
      OBJECT_ARRAY[33] = "IW*>\u0010\u0018\u0015K)ll\b\u0018F\u0017mlZEX3d\u0016\n\nX;T";
      OBJECT_ARRAY[34] = "G3#T[\fS=t&@g\u0011a1GP\u000bF\u007f-Y";
      OBJECT_ARRAY[35] = ":N\r>\rT.@ZL\r?f\u0015]*\u0007Q<\u0007\u001e d\u000e=D\u0000/\nT/\u0007\nL";
      OBJECT_ARRAY[36] = "O\u001a1K\"\"[\u0014f9;I\u0019H#X)%NV?F";
      OBJECT_ARRAY[37] = "pQ89I^,M;k5N!@\u0005k5\u001c|^!cOL3^)S";
      OBJECT_ARRAY[38] = "\u00071@HP0\u0001aLB.n>m\f\u0018A&Pl\u0000MT";
      OBJECT_ARRAY[39] = "\b \u0013%c'Rx\u0019{]\u007f\u000fcP\u001dfx\u001bxJq1f\u0007f(&>b\u0003{Dq ~\u001d\u0019\u0013~$z\u0000uD`8db\"Kd<y\u000euUx\"\u001b";
      OBJECT_ARRAY[40] = "\u001e4d\u0014h\u001dC!%]\u0002T v}\\cDL!c@}";
      OBJECT_ARRAY[41] = "5(\u0006zf\u001b3x\np\u0018@\fuWpr\u0003s)Ks ";
   }
}
