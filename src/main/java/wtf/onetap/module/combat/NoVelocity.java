package wtf.onetap.module.combat;

import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "NoVelocity", description = "Отменяет или снижает отбрасывание под античиты", category = Category.COMBAT)
public class NoVelocity extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Grim", "Grim", "Отмена").build();
   private boolean flag;
   private int value;

   @Override
   public void onDisable() {
      this.flag = false;
      this.value = 0;
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (event.getPacket() instanceof ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket) {
            if (clientboundSetEntityMotionPacket.id() != MinecraftAccess.MINECRAFT.player.getId()) {
               return;
            }

            if (((String)this.mode.getValue()).equals("Отмена")) {
               event.cancel();
            } else if (((String)this.mode.getValue()).equals("Grim")) {
               this.flag = true;
               this.value = 0;
            }
         } else if (event.getPacket() instanceof ClientboundExplodePacket) {
            if (((String)this.mode.getValue()).equals("Отмена")) {
               event.cancel();
            } else if (((String)this.mode.getValue()).equals("Grim")) {
               this.flag = true;
               this.value = 0;
            }
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (((String)this.mode.getValue()).equals("Grim") && this.flag) {
            if (MinecraftAccess.MINECRAFT.player.onGround()) {
               event.setJumping(true);
               MinecraftAccess.MINECRAFT.player.setSprinting(true);
               event.setForward(1.0F);
               this.flag = false;
               this.value = 0;
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (((String)this.mode.getValue()).equals("Grim") && this.flag) {
            if (++this.value > 3 || MinecraftAccess.MINECRAFT.player.hurtTime == 0 && !MinecraftAccess.MINECRAFT.player.onGround()) {
               this.flag = false;
               this.value = 0;
            }
         }
      }
   }
}
