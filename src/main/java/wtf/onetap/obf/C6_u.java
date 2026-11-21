package wtf.onetap.obf;

import net.minecraft.client.Minecraft;

public class C6_u {
   public static boolean m_8_boolean() {
      return Minecraft.getInstance().player != null;
   }

   public static boolean m_5_boolean() {
      return Minecraft.getInstance().level != null;
   }

   public static boolean m_4_boolean() {
      return m_8_boolean() && m_5_boolean();
   }

   private C6_u() {
   }
}
