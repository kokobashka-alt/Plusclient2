package wtf.onetap.module.movement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.phys.Vec2;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.CloseContainerEvent;
import wtf.onetap.event.ContainerClickEvent;
import wtf.onetap.event.ScreenOpenEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "GuiMove", description = "Позволяет ходить с открытым инвентарём", category = Category.MOVEMENT)
public class GuiMove extends Module {
   private final BooleanSetting slowdown = new BooleanSetting("Замедление", true).build();
   private final List<InventoryUtil.DataRecord> c3List = new ArrayList<>();
   private int value;
   private int value2;
   private boolean flag;

   @EventTarget
   private void onTick(TickEvent event) {
      this.updateKeys();
   }

   @EventTarget
   private void onScreenOpen(ScreenOpenEvent event) {
      this.updateKeys();
   }

   private void updateKeys() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.options != null) {
         if (!InventoryUtil.m_7_boolean()) {
            Screen screen = MinecraftAccess.MINECRAFT.gui.screen();
            if (screen instanceof ChatScreen || screen instanceof InventoryScreen || screen instanceof ContainerScreen) {
               MinecraftAccess.MINECRAFT.options.keyShift.setDown(false);
            } else if (screen == null) {
               MinecraftAccess.MINECRAFT.options.keyShift.setDown(isKeyDown(MinecraftAccess.MINECRAFT.options.keyShift));
            }

            if (this.value > 0) {
               this.setKeys(this.getMovementKeys(), false);
               MinecraftAccess.MINECRAFT.options.keySprint.setDown(isKeyDown(MinecraftAccess.MINECRAFT.options.keySprint));
               if (--this.value == 0) {
                  this.flushClicks();
               }
            } else if (!(screen instanceof ChatScreen) && !(screen instanceof AbstractSignEditScreen)) {
               this.setKeys(this.getMovementKeys(), true);
               MinecraftAccess.MINECRAFT.options.keySprint.setDown(isKeyDown(MinecraftAccess.MINECRAFT.options.keySprint));
            }
         }
      }
   }

   @EventTarget
   private void onContainerClick(ContainerClickEvent event) {
      if ((Boolean)this.slowdown.getValue() && this.isMoving()) {
         Screen screen = MinecraftAccess.MINECRAFT.gui.screen();
         if (screen instanceof InventoryScreen || event.getInput() == ContainerInput.THROW && screen instanceof ContainerScreen) {
            InventoryUtil.DataRecord c3 = InventoryUtil.handleContainerClick(event.getContainerId(), event.getSlot(), event.getButton(), event.getInput(), event.getPlayer());
            if (c3 != null) {
               this.c3List.add(c3);
               event.cancel();
               if (event.getInput() == ContainerInput.THROW) {
                  this.value = 2;
               }
            }
         }
      }
   }

   @EventTarget
   private void onCloseContainer(CloseContainerEvent event) {
      if (!this.c3List.isEmpty()) {
         this.value = 2;
         this.value2 = MinecraftAccess.MINECRAFT.player.containerMenu.containerId;
         this.flag = true;
         event.cancel();
      } else {
         if (MinecraftAccess.MINECRAFT.gui.screen() instanceof InventoryScreen && (this.isMoving() || MinecraftAccess.MINECRAFT.player.isSprinting())) {
            event.cancel();
         }
      }
   }

   private void flushClicks() {
      for (InventoryUtil.DataRecord c3 : this.c3List) {
         InventoryUtil.sendClick(c3);
      }

      this.c3List.clear();
      if (this.flag && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundContainerClosePacket(this.value2));
      }

      this.flag = false;
   }

   private void setKeys(KeyMapping[] keyMappingArray, boolean flag2) {
      for (KeyMapping keyMapping : keyMappingArray) {
         keyMapping.setDown(flag2 && isKeyDown(keyMapping));
      }
   }

   private KeyMapping[] getMovementKeys() {
      return new KeyMapping[]{MinecraftAccess.MINECRAFT.options.keyUp, MinecraftAccess.MINECRAFT.options.keyDown, MinecraftAccess.MINECRAFT.options.keyLeft, MinecraftAccess.MINECRAFT.options.keyRight, MinecraftAccess.MINECRAFT.options.keyJump};
   }

   private boolean isMoving() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.input != null && MinecraftAccess.MINECRAFT.options != null) {
         Vec2 vec2 = MinecraftAccess.MINECRAFT.player.input.getMoveVector();
         return Math.abs(vec2.x) > 1.0E-5F
            || Math.abs(vec2.y) > 1.0E-5F
            || isKeyDown(MinecraftAccess.MINECRAFT.options.keyUp)
            || isKeyDown(MinecraftAccess.MINECRAFT.options.keyDown)
            || isKeyDown(MinecraftAccess.MINECRAFT.options.keyLeft)
            || isKeyDown(MinecraftAccess.MINECRAFT.options.keyRight);
      } else {
         return false;
      }
   }

   private static boolean isKeyDown(KeyMapping keyMapping) {
      return InventoryUtil.isKeyDown(keyMapping);
   }

   @Override
   public void onDisable() {
      this.flushClicks();
      this.value = 0;
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
