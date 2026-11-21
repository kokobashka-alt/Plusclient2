package wtf.onetap.obf;

import wtf.onetap.Onetap;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

public class C6_z implements MinecraftAccess {
   private static boolean f_8;
   private static boolean f_0;
   private static boolean f_7;
   private static boolean f_1;

   private C6_z() {
   }

   public static void m_8_void() {
      f_8 = false;
      f_0 = false;
      if (MinecraftAccess.MINECRAFT.player == null) {
         f_7 = false;
      }
   }

   public static boolean m_6_boolean() {
      if (!f_1 && !f_8 && m_4_boolean()) {
         f_7 = true;
         return true;
      } else {
         return false;
      }
   }

   public static void m_7_void() {
      f_0 = true;
   }

   public static void m_1_void() {
      InventoryUtil.m_8_StringArr();
      f_8 = true;
      if (f_7 && !f_0) {
         f_7 = false;
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gui.screen() == null) {
            f_1 = true;

            try {
               ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$startUseItem();
            } finally {
               f_1 = false;
            }
         }
      }
   }

   private static boolean m_4_boolean() {
      if (Onetap.ROTATION_MODES.isFlag()) {
         return true;
      }

      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      return killAura != null && killAura.isEnabled();
   }
}
