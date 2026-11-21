package wtf.onetap.module;

public enum Category {
   COMBAT("Combat", "\ue904"),
   MOVEMENT("Movement", "\ue905"),
   PLAYER("Player", "\ue907"),
   RENDER("Render", "\ue90b"),
   MISC("Misc", "\ue906"),
   PVE("PVE", "\ue91c");

   private final String displayName;
   private final String icon;

   Category(String text, String text2) {
      this.displayName = text;
      this.icon = text2;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getIcon() {
      return this.icon;
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
}
