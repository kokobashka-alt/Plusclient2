package wtf.onetap.module.misc;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.CloseContainerEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "XCarry", description = "Оставляет слоты крафта открытыми после закрытия инвентаря", category = Category.MISC)
public class XCarry extends Module {
   private final BooleanSetting autoDisable = new BooleanSetting("Авто выкл", true).build();

   @EventTarget(priority = EventPriority.LOW)
   private void onCloseContainer(CloseContainerEvent event) {
      if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.screen() instanceof InventoryScreen) {
         event.cancel();
         if ((Boolean)this.autoDisable.getValue()) {
            this.toggle();
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
