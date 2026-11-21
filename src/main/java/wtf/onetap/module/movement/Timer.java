package wtf.onetap.module.movement;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.AttackCooldownTracker;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.misc.FreeCam;
import wtf.onetap.render.GuiScale;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Timer", description = "Ускоряет тики клиента", category = Category.MOVEMENT)
public class Timer extends Module {
   private final FloatSetting boost = new FloatSetting("Буст", 2.0F, 1.0F, 10.0F, 0.1F).build();
   private final BooleanSetting forGrim = new BooleanSetting("Под грим", false).build();
   private final BooleanSetting onFirework = new BooleanSetting("При фейерверке", false).build();
   private static boolean flag;
   private float floatValue = -1.0F;
   private float floatValue2 = 80.0F;
   private boolean flag2;
   private boolean flag3;
   private Vec3 vec3;
   private float floatValue3;
   private float floatValue4;

   public static boolean m_4_boolean() {
      boolean flag4 = flag;
      flag = false;
      return flag4;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      flag = false;
      FreeCam freeCam = Onetap.MODULE_MANAGER.get(FreeCam.class);
      if (MinecraftAccess.MINECRAFT.player != null
         && MinecraftAccess.MINECRAFT.level != null
         && !MinecraftAccess.MINECRAFT.player.isPassenger()
         && !MinecraftAccess.MINECRAFT.player.isDeadOrDying()
         && (freeCam == null || !freeCam.isEnabled())) {
         if ((Boolean)this.forGrim.getValue()) {
            GuiScale.setScale(this.m_6_float());
         } else {
            boolean flag4 = AttackCooldownTracker.m_7_boolean();
            GuiScale.setScale(this.onFirework.getValue() && !flag4 ? 1.0F : (Float)this.boost.getValue());
         }
      } else {
         GuiScale.setScale(1.0F);
      }
   }

   private float m_6_float() {
      this.m_1_void();
      boolean flag4 = (Boolean)this.onFirework.getValue();
      NoClip.m_4_C3_sArr();
      boolean flag5 = AttackCooldownTracker.m_7_boolean();
      if (this.flag2) {
         if (!flag4 || flag5) {
            this.floatValue = this.floatValue - 50.0F * (1.0F - 1.0F / (Float)this.boost.getValue());
            if (this.floatValue <= 0.0F) {
               this.floatValue = 0.0F;
               this.flag2 = false;
            }

            return (Float)this.boost.getValue();
         }

         this.flag2 = false;
      }

      if (this.m_6_boolean() && !this.flag3) {
         flag = true;
         this.flag3 = true;
         this.floatValue = Math.min(this.floatValue + 50.0F, this.floatValue2);
      } else {
         this.flag3 = false;
         if (this.m_1_boolean()) {
            this.floatValue = Math.min(this.floatValue + 50.0F, this.floatValue2);
         } else if (!flag4) {
            this.floatValue += 2.6315808F;
         }
      }

      this.floatValue = Math.min(this.floatValue, this.floatValue2);
      if (this.floatValue >= this.floatValue2 * 0.9F && (!flag4 || flag5)) {
         this.flag2 = true;
      }

      return flag4 ? 1.0F : 0.95F;
   }

   private boolean m_6_boolean() {
      return MinecraftAccess.MINECRAFT.player.isFallFlying() && AttackCooldownTracker.m_7_boolean() && MinecraftAccess.MINECRAFT.player.getDeltaMovement().length() <= 0.8F;
   }

   private boolean m_1_boolean() {
      Vec3 vec32 = MinecraftAccess.MINECRAFT.player.position();
      boolean flag4 = this.vec3 != null
         && this.vec3.distanceToSqr(vec32) <= 4.0E-8
         && MinecraftAccess.MINECRAFT.player.getYRot() == this.floatValue3
         && MinecraftAccess.MINECRAFT.player.getXRot() == this.floatValue4;
      this.vec3 = vec32;
      this.floatValue3 = MinecraftAccess.MINECRAFT.player.getYRot();
      this.floatValue4 = MinecraftAccess.MINECRAFT.player.getXRot();
      return flag4;
   }

   private void m_1_void() {
      ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
      PlayerInfo playerInfo = clientPacketListener != null ? clientPacketListener.getPlayerInfo(MinecraftAccess.MINECRAFT.player.getUUID()) : null;
      int value = playerInfo != null ? playerInfo.getLatency() : 0;
      this.floatValue2 = Math.max(80.0F, Math.min(350.0F, value * 0.5F + 40.0F));
      if (this.floatValue < 0.0F) {
         this.floatValue = this.floatValue2;
      }

      this.floatValue = Math.min(this.floatValue, this.floatValue2);
   }

   @Override
   public void onDisable() {
      GuiScale.setScale(1.0F);
      flag = false;
      this.floatValue = -1.0F;
      this.flag2 = false;
      this.flag3 = false;
   }
}
