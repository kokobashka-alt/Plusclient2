package wtf.onetap.module.misc;

import java.util.Locale;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.Blocks;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.ServerDetector;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AutoJoiner", description = "Авто заход на гриф RW и дуэли SpookyTime", category = Category.MISC)
public class AutoJoiner extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "ReallyWorld", "ReallyWorld", "SpookyTime Duel").build();
   private final TextSetting grief = new TextSetting("Гриф", "1").visibleWhen(this::isReallyWorld).build();
   private final TimeHelper timeHelper = new TimeHelper();
   private boolean flag;
   private int value;

   @Override
   public void onEnable() {
      if (this.isSpookyTimeDuel()) {
         this.timeHelper.reset();
         this.flag = false;
         this.value = 0;
      } else {
         ServerDetector.SERVER_DETECTOR.m_9_boolean(Math.max(1, Math.min(56, this.grief.getInt())));
      }
   }

   @Override
   public void onDisable() {
      if (!this.isSpookyTimeDuel()) {
         ServerDetector.SERVER_DETECTOR.stop();
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (this.isSpookyTimeDuel()
         && event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket
         && clientboundSystemChatPacket.content().getString().toLowerCase(Locale.ROOT).contains("сервер заполнен")) {
         this.flag = false;
         this.value = 0;
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.isSpookyTimeDuel()) {
         this.m_4_void();
      } else if (!ServerDetector.SERVER_DETECTOR.isFlag()) {
         this.setEnabled(false);
      }
   }

   private void m_4_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.timeHelper.hasElapsed(50L)) {
            this.timeHelper.reset();
            if (this.flag) {
               this.value++;
               if (this.value > 10 && this.isNotOnBeacon()) {
                  this.setEnabled(false);
               } else {
                  if (this.value > 80) {
                     this.flag = false;
                     this.value = 0;
                  }
               }
            } else if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
               int value2 = this.m_3_int(containerScreen);
               if (value2 != -1) {
                  MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(((ChestMenu)containerScreen.getMenu()).containerId, value2, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
                  this.flag = true;
                  this.value = 0;
               }
            } else {
               ServerDetector.m_6_boolean();
            }
         }
      }
   }

   private int m_3_int(ContainerScreen containerScreen) {
      int value2 = ((ChestMenu)containerScreen.getMenu()).getRowCount() * 9;

      for (int i = 0; i < value2; i++) {
         Slot slot = ((ChestMenu)containerScreen.getMenu()).slots.get(i);
         String text = slot.getItem().getHoverName().getString().toLowerCase(Locale.ROOT);
         if (text.contains("дуэли 1.16.5")) {
            return i;
         }
      }

      return -1;
   }

   private boolean isNotOnBeacon() {
      return !MinecraftAccess.MINECRAFT.level.getBlockState(MinecraftAccess.MINECRAFT.player.blockPosition().below()).is(Blocks.BEACON);
   }

   private boolean isReallyWorld() {
      return "ReallyWorld".equals(this.mode.getValue());
   }

   private boolean isSpookyTimeDuel() {
      return "SpookyTime Duel".equals(this.mode.getValue());
   }
}
