package wtf.onetap.module.movement;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.entity.LivingEntityInvoker;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;

@ModuleInformation(name = "Grim Glide", description = "Ускоряет движение на элитрах", category = Category.MOVEMENT)
public class GrimGlide extends Module {
   private final ModeSetting mode = new ModeSetting("Мод", "Default", "Default", "Grim", "SlimeHVH").build();
   private int value;
   private boolean flag;

   @Override
   public void onEnable() {
      this.value = 0;
      this.flag = false;
   }

   @Override
   public void onDisable() {
      this.value = 0;
      this.flag = false;
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (((String)this.mode.getValue()).equals("SlimeHVH") && MinecraftAccess.MINECRAFT.player != null) {
         if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
            this.value = 2;
            this.flag = true;
         }
      }
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (((String)this.mode.getValue()).equals("SlimeHVH") && MinecraftAccess.MINECRAFT.player != null) {
         if (event.getPacket() instanceof ServerboundMovePlayerPacket) {
            if (MinecraftAccess.MINECRAFT.player.isFallFlying() && this.value == 0 && !this.flag) {
               PacketUtil.sendPacket(new StatusOnly(true, true));
               event.cancel();
            }

            this.flag = false;
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.value > 0) {
         this.value--;
      }

      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (!((String)this.mode.getValue()).equals("SlimeHVH")) {
            if (MinecraftAccess.MINECRAFT.player.isFallFlying()) {
               float floatValue = MinecraftAccess.MINECRAFT.player.getXRot();
               if (!((String)this.mode.getValue()).equals("Grim") || !(floatValue < -6.5F) && !(floatValue > 90.0F)) {
                  Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
                  double doubleValue = Math.toRadians(MinecraftAccess.MINECRAFT.player.getYRot());
                  double doubleValue2 = -Math.sin(doubleValue);
                  double doubleValue3 = Math.cos(doubleValue);
                  Vec3 vec32 = vec3.multiply(0.99, 0.98, 0.99).add(doubleValue2 * 0.08, 0.02, doubleValue3 * 0.08);
                  MinecraftAccess.MINECRAFT.player.setDeltaMovement(vec32);
               }
            }
         }
      }
   }

   public boolean isSlimeHvhActive() {
      return this.isEnabled() && ((String)this.mode.getValue()).equals("SlimeHVH") && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.isFallFlying();
   }

   public Vec3 m_1(Vec3 vec3, Vec3 vec32) {
      NoClip.m_4_C3_sArr();
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getLookAngle();
      float floatValue = MinecraftAccess.MINECRAFT.player.getXRot() * (float) (Math.PI / 180.0);
      double doubleValue = Math.sqrt(vec33.x * vec33.x + vec33.z * vec33.z);
      double doubleValue2 = vec3.horizontalDistance();
      boolean flag2 = MinecraftAccess.MINECRAFT.player.getDeltaMovement().y <= 0.0;
      double doubleValue3 = ((LivingEntityInvoker)MinecraftAccess.MINECRAFT.player).onetap$getEffectiveGravity();
      double doubleValue4 = flag2 && MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.SLOW_FALLING) ? Math.min(doubleValue3, 0.01) : doubleValue3;
      double doubleValue5 = Mth.square(Math.cos(floatValue));
      vec3 = vec3.add(0.0, doubleValue4 * (-1.0 + doubleValue5 * 0.75), 0.0);
      if (vec3.y < 0.0 && doubleValue > 0.0) {
         double doubleValue6 = vec3.y * -0.1 * doubleValue5;
         vec3 = vec3.add(vec33.x * doubleValue6 / doubleValue, doubleValue6, vec33.z * doubleValue6 / doubleValue);
      }

      if (floatValue < 0.0F && doubleValue > 0.0) {
         double doubleValue7 = doubleValue2 * -Mth.sin(floatValue) * 0.04F;
         vec3 = vec3.add(-vec33.x * doubleValue7 / doubleValue, doubleValue7 * 3.2, -vec33.z * doubleValue7 / doubleValue);
      }

      if (doubleValue > 0.0) {
         vec3 = vec3.add((vec33.x / doubleValue * doubleValue2 - vec3.x) * 0.1, 0.0, (vec33.z / doubleValue * doubleValue2 - vec3.z) * 0.1);
      }

      double doubleValue8 = Math.toRadians(MinecraftAccess.MINECRAFT.player.getYRot());
      double doubleValue9 = -Math.sin(doubleValue8);
      double doubleValue10 = Math.cos(doubleValue8);
      if (this.value >= 1) {
         double doubleValue11 = 0.09F;
         return vec3.multiply(0.99F, 0.98F, 0.99F).add(doubleValue9 * doubleValue11, 0.03F, doubleValue10 * doubleValue11);
      } else {
         return vec3.multiply(0.3F, 0.3F, 0.3F);
      }
   }
}
