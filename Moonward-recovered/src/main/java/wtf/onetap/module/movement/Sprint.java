package wtf.onetap.module.movement;

import net.minecraft.client.player.LocalPlayer;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Sprint", description = "Автоматически спринтит", category = Category.MOVEMENT)
public class Sprint extends Module {
   private final BooleanSetting cancelOnUse = new BooleanSetting("Отменять при использовании", true).build();

   public static void m_8_void(LocalPlayer localPlayer) {
      Sprint sprint = Onetap.MODULE_MANAGER.get(Sprint.class);
      if (sprint != null && sprint.isEnabled() && (Boolean)sprint.cancelOnUse.getValue() && !NoSlow.m_9_boolean()) {
         localPlayer.setSprinting(false);
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (MinecraftAccess.MINECRAFT.player.isUsingItem() && (Boolean)this.cancelOnUse.getValue() && !NoSlow.m_9_boolean()) {
            MinecraftAccess.MINECRAFT.player.setSprinting(false);
         } else {
            if (MinecraftAccess.MINECRAFT.player.input.hasForwardImpulse() && !MinecraftAccess.MINECRAFT.player.input.keyPresses.shift() && MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() > 6) {
               MinecraftAccess.MINECRAFT.player.setSprinting(true);
            }
         }
      }
   }

   private static String b(byte[] byteArray) {
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
