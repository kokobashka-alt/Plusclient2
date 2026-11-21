package wtf.onetap.module.pve;

import java.util.Locale;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.misc.AhHelper;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "Auto Resell", description = "Автоматически перевыставляет предметы на аукционе", category = Category.PVE)
public class AutoResell extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Вручную", "Вручную", "Командой").build();
   private final TimeHelper timeHelper = new TimeHelper();
   private AutoResell.ModeKind c9 = AutoResell.ModeKind.WAIT;
   private boolean flag;

   @Override
   public void onEnable() {
      this.c9 = AutoResell.ModeKind.WAIT;
      this.flag = false;
      this.timeHelper.reset();
   }

   @Override
   public void onDisable() {
      this.c9 = AutoResell.ModeKind.WAIT;
      this.flag = false;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         if (((String)this.mode.getValue()).equals("Командой")) {
            this.m_3_void();
         } else {
            this.m_7_void();
         }
      }
   }

   private void m_3_void() {
      if (this.c9 == AutoResell.ModeKind.WAIT) {
         if (this.timeHelper.hasElapsed(1000L)) {
            MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah resell");
            this.c9 = AutoResell.ModeKind.COOLDOWN;
            this.timeHelper.reset();
         }
      } else {
         if (this.c9 == AutoResell.ModeKind.COOLDOWN && this.timeHelper.hasElapsed(61000L)) {
            MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah resell");
            this.timeHelper.reset();
         }
      }
   }

   private void m_7_void() {
      switch (this.c9.ordinal()) {
         case 0:
            if (!this.timeHelper.hasElapsed(1000L)) {
               return;
            }

            this.c9 = AutoResell.ModeKind.OPEN_AH;
            this.flag = false;
            this.timeHelper.reset();
            break;
         case 1:
            if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen) {
               this.c9 = AutoResell.ModeKind.CLICK_STORAGE;
               this.timeHelper.reset();
               return;
            }

            if (!this.flag || this.timeHelper.hasElapsed(800L)) {
               MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah");
               this.flag = true;
               this.timeHelper.reset();
            }

            if (this.flag && this.timeHelper.hasElapsed(2500L) && !(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
               this.flag = false;
            }
            break;
         case 2:
            if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen)) {
               if (this.timeHelper.hasElapsed(2500L)) {
                  this.c9 = AutoResell.ModeKind.OPEN_AH;
                  this.flag = false;
                  this.timeHelper.reset();
               }

               return;
            }

            if (!this.timeHelper.hasElapsed(300L)) {
               return;
            }

            if (m_8(containerScreen, Items.ENDER_CHEST, "хранилищ") || m_2(containerScreen, Items.ENDER_CHEST)) {
               this.c9 = AutoResell.ModeKind.CLICK_CLOCK;
               this.timeHelper.reset();
            } else if (this.timeHelper.hasElapsed(2500L)) {
               this.closeScreens();
               this.c9 = AutoResell.ModeKind.COOLDOWN;
               this.timeHelper.reset();
            }
            break;
         case 3:
            if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen2)) {
               this.c9 = AutoResell.ModeKind.CLOSE;
               this.timeHelper.reset();
               return;
            }

            if (!this.timeHelper.hasElapsed(300L)) {
               return;
            }

            if (m_2(containerScreen2, Items.CLOCK)) {
               this.c9 = AutoResell.ModeKind.CLOSE;
               this.timeHelper.reset();
            } else if (this.timeHelper.hasElapsed(2500L)) {
               this.c9 = AutoResell.ModeKind.CLOSE;
               this.timeHelper.reset();
            }
            break;
         case 4:
            if (!this.timeHelper.hasElapsed(300L)) {
               return;
            }

            this.closeScreens();
            if (MinecraftAccess.MINECRAFT.gui.screen() == null || this.timeHelper.hasElapsed(1000L)) {
               this.c9 = AutoResell.ModeKind.COOLDOWN;
               this.timeHelper.reset();
            }
            break;
         case 5:
            if (!this.timeHelper.hasElapsed(61000L)) {
               return;
            }

            this.c9 = AutoResell.ModeKind.OPEN_AH;
            this.flag = false;
            this.timeHelper.reset();
      }
   }

   private void closeScreens() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.containerMenu != null && MinecraftAccess.MINECRAFT.player.containerMenu.containerId != 0) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.gui.screen().onClose();
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }
   }

   private static boolean m_8(ContainerScreen containerScreen, Item item, String text) {
      String text2 = text.toLowerCase(Locale.ROOT);
      NonNullList nonNullList = ((ChestMenu)containerScreen.getMenu()).slots;
      int value = Math.min(nonNullList.size(), 54);

      for (int i = 0; i < value; i++) {
         Slot slot = (Slot)nonNullList.get(i);
         if (slot != null && slot.hasItem() && slot.getItem().getItem() == item) {
            String text3 = AhHelper.m_7_String(slot.getItem().getHoverName().getString());
            if (text3.contains(text2)) {
               InventoryUtil.clickSlot(((ChestMenu)containerScreen.getMenu()).containerId, slot.index, 0, ContainerInput.PICKUP);
               return true;
            }
         }
      }

      return false;
   }

   private static boolean m_2(ContainerScreen containerScreen, Item item) {
      NonNullList nonNullList = ((ChestMenu)containerScreen.getMenu()).slots;
      int value = Math.min(nonNullList.size(), 54);

      for (int i = 0; i < value; i++) {
         Slot slot = (Slot)nonNullList.get(i);
         if (slot != null && slot.hasItem() && slot.getItem().getItem() == item) {
            InventoryUtil.clickSlot(((ChestMenu)containerScreen.getMenu()).containerId, slot.index, 0, ContainerInput.PICKUP);
            return true;
         }
      }

      return false;
   }

   private enum ModeKind {
      WAIT,
      OPEN_AH,
      CLICK_STORAGE,
      CLICK_CLOCK,
      CLOSE,
      COOLDOWN;

      private static String a(byte[] byteArray) {
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
}
