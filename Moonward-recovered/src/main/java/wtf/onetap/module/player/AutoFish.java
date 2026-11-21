package wtf.onetap.module.player;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AutoFish", description = "Автоматически ловит рыбу в AFK-режиме", category = Category.PLAYER)
public class AutoFish extends Module {
   private final TimeHelper timeHelper = new TimeHelper();
   private boolean flag;

   @Override
   public void onEnable() {
      this.flag = false;
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()).is(Items.FISHING_ROD) && MinecraftAccess.MINECRAFT.player.fishing == null) {
            this.m_1(false);
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      VoiceChat.m_9_StringArr();
      if (event.getPacket() instanceof ClientboundSoundPacket clientboundSoundPacket) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.fishing != null) {
            if (clientboundSoundPacket.getSound().value().equals(SoundEvents.FISHING_BOBBER_SPLASH)) {
               if (!(MinecraftAccess.MINECRAFT.player.fishing.distanceToSqr(clientboundSoundPacket.getX(), clientboundSoundPacket.getY(), clientboundSoundPacket.getZ()) > 0.48999979194765847)) {
                  this.m_1(true);
                  this.timeHelper.reset();
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.timeHelper.hasElapsed(450L) && this.flag) {
         this.m_1(false);
      }
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (this.flag && event.getPacket() instanceof ServerboundSetCarriedItemPacket) {
         event.cancel();
      }
   }

   private void m_1(boolean flag2) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
         MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
         this.flag = flag2;
      }
   }
}
