package wtf.onetap.module.player;

import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "SendCoords", description = "Пишет координаты в чат по нажатию клавиши", category = Category.PLAYER)
public class SendCoords extends Module {
   private final KeybindSetting send = new KeybindSetting("Отправить", -1).build();

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (event.getAction() == 1) {
            if (event.getKey() == (Integer)this.send.getValue()) {
               if (MinecraftAccess.MINECRAFT.player != null) {
                  int value = (int)MinecraftAccess.MINECRAFT.player.getX();
                  int value2 = (int)MinecraftAccess.MINECRAFT.player.getZ();
                  MinecraftAccess.MINECRAFT.player.connection.sendChat("! " + value + " " + value2);
               }
            }
         }
      }
   }
}
