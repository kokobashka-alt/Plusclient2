package wtf.onetap.combat;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import wtf.onetap.util.InventoryUtil;

public class AttackCooldownTracker {
   private static final Map<Integer, Integer> INTEGER_MAP = new HashMap<>();
   private static int value = 0;

   private AttackCooldownTracker() {
   }

   public static void tick() {
      value++;
      INTEGER_MAP.values().removeIf(integer -> value - integer > 2);
   }

   public static void markAttacked(int value2) {
      INTEGER_MAP.put(value2, value);
   }

   public static boolean isRecentlyAttacked(int value2) {
      InventoryUtil.m_8_StringArr();
      Integer integer = INTEGER_MAP.get(value2);
      return integer != null && value - integer <= 2;
   }

   public static boolean m_7_boolean() {
      InventoryUtil.m_8_StringArr();
      LocalPlayer localPlayer = Minecraft.getInstance().player;
      return localPlayer != null && isRecentlyAttacked(localPlayer.getId());
   }
}
