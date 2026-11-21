package wtf.onetap.module.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.AutoSwapMode;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ClickPearl", description = "Бросает жемчуг по нажатию клавиши", category = Category.PLAYER)
public class ClickPearl extends Module {
   private final KeybindSetting key = new KeybindSetting("Клавиша", -1).build();
   private final ModeSetting throwItem = new ModeSetting("Бросок", "Пакетный", "Пакетный", "Легитный", "GroundedGrim").build();
   private final ModeSetting swapMode = new ModeSetting("Режим свапа", "Дефолт", "Дефолт", "Грим", "Легит")
      .visibleWhen(() -> !((String)this.throwItem.getValue()).equals("GroundedGrim"))
      .build();
   private final FloatSetting delay = new FloatSetting("Задержка", 50.0F, 0.0F, 150.0F, 1.0F).visibleWhen(() -> ((String)this.swapMode.getValue()).equals("Легит")).build();
   private final AutoSwapMode autoSwapMode = new AutoSwapMode();
   private boolean flag;

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
            if (event.getAction() == 1) {
               if ((Integer)this.key.getValue() != -1 && event.getKey() == (Integer)this.key.getValue()) {
                  event.cancel();
                  if (((String)this.throwItem.getValue()).equals("GroundedGrim")) {
                     this.flag = true;
                  } else {
                     this.autoSwapMode.m_7((String)this.throwItem.getValue(), (String)this.swapMode.getValue(), (Float)this.delay.getValue());
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (!this.flag || this.m_6_boolean()) {
         this.flag = false;
         this.autoSwapMode.m_5_void();
      }
   }

   @Override
   public void onDisable() {
      this.flag = false;
   }

   private boolean m_6_boolean() {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.gameMode == null) {
         return true;
      }

      if (InventoryUtil.isBusy()) {
         return false;
      }

      if (MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(Items.ENDER_PEARL.getDefaultInstance())) {
         return true;
      }

      if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.ENDER_PEARL)) {
         this.useHand(InteractionHand.MAIN_HAND);
         return true;
      }

      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.ENDER_PEARL)) {
         this.useHand(InteractionHand.OFF_HAND);
         return true;
      }

      int value = InventoryUtil.findItemSlotInRange(Items.ENDER_PEARL, 0, 36);
      if (value == -1) {
         return true;
      }

      int value2 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      int value3 = value < 9 ? value + 36 : value;
      InventoryUtil.m_6(() -> {
         InventoryUtil.clickSlot(0, value3, value2, ContainerInput.SWAP);
         this.useHand(InteractionHand.MAIN_HAND);
         InventoryUtil.clickSlot(0, value3, value2, ContainerInput.SWAP);
      });
      return true;
   }

   private void useHand(InteractionHand interactionHand) {
      MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, interactionHand);
      MinecraftAccess.MINECRAFT.player.swing(interactionHand);
   }
}
