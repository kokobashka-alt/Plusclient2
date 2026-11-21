package wtf.onetap.module.combat;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "AutoEXP", description = "Бросает бутылочки опыта под себя, пока удерживается заданная клавиша", category = Category.COMBAT)
public class AutoEXP extends Module {
   private final KeybindSetting activationButton = new KeybindSetting("Кнопка активации", -1).build();
   private int value = -1;
   private int value2 = -1;
   private boolean flag;
   private boolean flag2;

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (this.flag && !this.flag2 && event.getPacket() instanceof ServerboundSetCarriedItemPacket) {
         event.cancel();
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         this.m_0_void(false);
      } else {
         if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
            this.m_0_void(false);
         } else if (this.isActivationPressed()) {
            this.m_0_void(true);
         } else {
            this.m_0_void(false);
         }

         if (this.flag) {
            this.m_3_void();
         } else {
            this.m_1_void();
         }
      }
   }

   private void m_1_void() {
      if (MinecraftAccess.MINECRAFT.player != null && this.value != -1) {
         int value3 = this.value;
         int value4 = this.value2;
         this.value = -1;
         this.value2 = -1;
         if (value4 == -1) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value3);
            this.m_9_void(value3);
         } else {
            InventoryUtil.m_6(() -> InventoryUtil.clickSlot(0, value4, 7, ContainerInput.SWAP));
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value3);
            this.m_9_void(value3);
         }
      }
   }

   private boolean isActivationPressed() {
      int value3 = (Integer)this.activationButton.getValue();
      return value3 < 0 ? false : GLFW.glfwGetKey(MinecraftAccess.MINECRAFT.getWindow().handle(), value3) == 1;
   }

   private void m_0_void(boolean flag3) {
      if (this.flag != flag3) {
         this.flag = flag3;
         if (flag3) {
            this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
         }
      }
   }

   private void m_9_void(int value3) {
      this.flag2 = true;
      MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value3));
      this.flag2 = false;
   }

   private void m_3_void() {
      boolean flag3 = MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.EXPERIENCE_BOTTLE);
      if (!flag3) {
         int value3 = InventoryUtil.findHotbarSlot(Items.EXPERIENCE_BOTTLE);
         if (value3 != -1) {
            if (MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != value3) {
               MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value3);
               this.m_9_void(value3);
            }
         } else {
            if (!InventoryUtil.isBusy() && !MinecraftAccess.MINECRAFT.player.getInventory().getItem(7).is(Items.EXPERIENCE_BOTTLE)) {
               int value4 = InventoryUtil.findItemSlotInRange(Items.EXPERIENCE_BOTTLE, 9, 36);
               if (value4 != -1) {
                  if (this.value2 == -1) {
                     this.value2 = value4;
                  }

                  InventoryUtil.m_6(() -> InventoryUtil.clickSlot(0, value4, 7, ContainerInput.SWAP));
                  MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(7);
                  this.m_9_void(7);
               }
            }
         }
      } else {
         float floatValue = MinecraftAccess.MINECRAFT.player.tickCount + MinecraftAccess.MINECRAFT.getDeltaTracker().getGameTimeDeltaPartialTick(true);
         float floatValue2 = (float)(Math.sin(floatValue * 0.8F) * 11.0)
            + (float)(Math.sin(floatValue * 0.04000000011823444 + 17.20000385061287) * 1.5)
            + (float)(Math.sin(floatValue * 0.10999997043280933 + 5.800000963109878) * 3.0)
            + (float)Math.sin(floatValue * 0.07000004669766619 + 12.300002384186381);
         floatValue2 /= 3.0F;
         float floatValue3 = (float)(Math.sin(floatValue * 0.10000000392993033) + Math.sin(floatValue * 0.029999989348000328 + 54.099982886210135) * 0.5);
         Onetap.ROTATION_MODES.rotateWithSensitivity(new Rotations(Onetap.ROTATION_MODES.getRenderYaw() + floatValue2, 86.0F + floatValue3), false, 20);
         if (Onetap.ROTATION_MODES.toRotations().getFloatValue2() > 83.0F) {
            MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
            MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
         }
      }
   }

   @Override
   public void onDisable() {
      this.m_1_void();
      this.flag = false;
   }

   private static String b(byte[] byteArray) {
      int value3 = 0;
      int value4;
      char[] charArray = new char[value4 = byteArray.length];

      for (int i = 0; i < value4; i++) {
         int value5;
         if ((value5 = 255 & byteArray[i]) < 192) {
            charArray[value3++] = (char)value5;
         } else if (value5 < 224) {
            char ch = (char)((char)(value5 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value3++] = ch;
         } else if (i < value4 - 2) {
            char ch2 = (char)((char)(value5 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value3++] = ch2;
         }
      }

      return new String(charArray, 0, value3);
   }
}
