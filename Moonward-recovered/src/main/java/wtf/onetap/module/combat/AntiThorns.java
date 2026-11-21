package wtf.onetap.module.combat;

import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AntiThorns", description = "Отменяет отбрасывание от шипов (элитра)", category = Category.COMBAT)
public class AntiThorns extends Module {
   private int value;

   @Override
   public void onEnable() {
      this.value = 0;
   }

   @Override
   public void onDisable() {
      this.value = 0;
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (event.getPacket() instanceof ClientboundEntityEventPacket clientboundEntityEventPacket
            && clientboundEntityEventPacket.getEventId() == 33
            && clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) == MinecraftAccess.MINECRAFT.player
            && MinecraftAccess.MINECRAFT.player.isFallFlying()) {
            this.value = (int)3415252079885680648L;
         } else {
            if (event.getPacket() instanceof ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket && clientboundSetEntityMotionPacket.id() == MinecraftAccess.MINECRAFT.player.getId() && this.m_0_boolean()) {
               event.cancel();
               this.value = 0;
            }
         }
      } else {
         this.value = 0;
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.isFallFlying()) {
         if (this.value > 0) {
            this.value--;
         }
      } else {
         this.value = 0;
      }
   }

   private boolean m_0_boolean() {
      return this.value > 0 && MinecraftAccess.MINECRAFT.player.isFallFlying();
   }
}
