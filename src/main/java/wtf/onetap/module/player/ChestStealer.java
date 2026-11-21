package wtf.onetap.module.player;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "ChestStealer", description = "Автоматически лутает сундук", category = Category.PLAYER)
public class ChestStealer extends Module {
   private final TimeHelper timeHelper = new TimeHelper();
   private final ModeSetting mode = new ModeSetting("Режим", "Обычный", "Обычный", "ФТ Фаст").build();
   private final BooleanSetting close = new BooleanSetting("Закрывать", false).build();
   private final BooleanSetting disableWhenLooted = new BooleanSetting("Выключать когда залутал", false).build();
   private final FloatSetting lootDelay = new FloatSetting("Задержка лутания", 10.0F, 0.0F, 200.0F, 10.0F).build();
   private final BooleanSetting missOnPurpose = new BooleanSetting("Промахиваться", false).build();
   private final FloatSetting missChance = new FloatSetting("Шанс промаха", 15.0F, 0.0F, 75.0F, 1.0F).visibleWhen(this.missOnPurpose::getValue).build();

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
            if (this.timeHelper.hasElapsed((long)this.lootDelay.getFloat())) {
               ChestMenu chestMenu = (ChestMenu)containerScreen.getMenu();
               Container container = chestMenu.getContainer();
               ArrayList<Integer> integerList = new ArrayList<>();

               for (int i = 0; i < container.getContainerSize(); i++) {
                  if (!container.getItem(i).isEmpty()) {
                     integerList.add(i);
                  }
               }

               if (integerList.isEmpty()) {
                  if ((Boolean)this.close.getValue()) {
                     MinecraftAccess.MINECRAFT.player.closeContainer();
                  }

                  if ((Boolean)this.disableWhenLooted.getValue()) {
                     this.toggle();
                  }
               } else {
                  if ((Boolean)this.disableWhenLooted.getValue()
                     && MinecraftAccess.MINECRAFT.player.getInventory().getContainerSize() - 9 <= MinecraftAccess.MINECRAFT.player.getInventory().getContainerSize()) {
                     boolean flag = true;

                     for (int i2 = 0; i2 < 36; i2++) {
                        if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i2).isEmpty()) {
                           flag = false;
                           break;
                        }
                     }

                     if (flag) {
                        MinecraftAccess.MINECRAFT.player.closeContainer();
                        this.toggle();
                        return;
                     }
                  }

                  if ((Boolean)this.missOnPurpose.getValue() && ThreadLocalRandom.current().nextInt(100) < this.missChance.getInt()) {
                     this.timeHelper.reset();
                  } else {
                     int value = ((String)this.mode.getValue()).equals("ФТ Фаст")
                        ? (Integer)integerList.get(ThreadLocalRandom.current().nextInt(integerList.size()))
                        : (Integer)integerList.get(0);
                     MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(chestMenu.containerId, value, 0, ContainerInput.QUICK_MOVE, MinecraftAccess.MINECRAFT.player);
                     this.timeHelper.reset();
                  }
               }
            }
         }
      }
   }
}
