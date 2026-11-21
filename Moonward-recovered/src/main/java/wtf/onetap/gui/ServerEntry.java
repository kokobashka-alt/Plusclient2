package wtf.onetap.gui;

import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;

public class ServerEntry {
   private final Animation animation;
   private ServerEntry.ModeKind c7;
   private boolean flag;

   public ServerEntry() {
      this.animation = new Animation(280L, 0.0F, Easing.EASING8);
      this.c7 = ServerEntry.ModeKind.NONE;
   }

   public float m_1_float() {
      float floatValue = this.animation.update(this.flag ? 1.0F : 0.0F);
      if (!this.flag && this.animation.isFinished()) {
         this.c7 = ServerEntry.ModeKind.NONE;
      }

      return floatValue;
   }

   public void m_6(ServerEntry.ModeKind c72) {
      if (c72 != ServerEntry.ModeKind.NONE) {
         this.c7 = c72;
         this.flag = true;
      }
   }

   public void hide() {
      this.flag = false;
   }

   public void m_9_void() {
      this.flag = false;
      this.c7 = ServerEntry.ModeKind.NONE;
      this.animation.reset(0.0F);
   }

   public void show() {
      if (this.c7 != ServerEntry.ModeKind.NONE) {
         this.flag = true;
         this.animation.setDuration(1.0F);
      }
   }

   public ServerEntry.ModeKind getC7() {
      return this.c7;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public float getAnimation() {
      return this.animation.getValue();
   }

   public float m_6_float() {
      return this.animation.updateOrSnap();
   }

   public boolean m_6_boolean() {
      return this.animation.isFinished();
   }

   public boolean isVisible() {
      return this.c7 != ServerEntry.ModeKind.NONE && (this.flag || this.animation.getValue() > 0.02F);
   }

   public boolean isOpen() {
      return this.flag && this.animation.getValue() > 0.45F;
   }

   public enum ModeKind {
      NONE,
      WORLDS,
      SERVERS;

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
