package wtf.onetap.module.player;

import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "DeathCoords", description = "Сообщает координаты смерти в чат", category = Category.PLAYER)
public class DeathCoords extends Module {
   private boolean flag;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (MinecraftAccess.MINECRAFT.player.getHealth() <= 0.0F) {
            if (!this.flag) {
               ChatUtil.log(String.format("Вы умерли на XYZ: %.0f %.0f %.0f", MinecraftAccess.MINECRAFT.player.getX(), MinecraftAccess.MINECRAFT.player.getY(), MinecraftAccess.MINECRAFT.player.getZ()));
               this.flag = true;
            }
         } else {
            this.flag = false;
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
