package wtf.onetap.module.movement;

import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ElytraResolver", description = "Долет и отлет для элитра-ротки", category = Category.MOVEMENT)
public class ElytraResolver extends Module {
   private final FloatSetting flyAwayDistance = new FloatSetting("Дистанция отлёта", 8.0F, 5.0F, 12.0F, 0.5F).build();
   private final ModeSetting flyAwayMode = new ModeSetting("Режим отлета", "В сторону", "В сторону", "Вверх", "Диагональ").build();
   private ElytraResolver.ModeKind c7 = ElytraResolver.ModeKind.APPROACHING;
   private LivingEntity livingEntity;
   private Vec3 vec3;
   private Vec3 vec32;
   private long longValue;
   private boolean flag;

   public boolean isApproaching() {
      return this.c7 == ElytraResolver.ModeKind.APPROACHING;
   }

   public Vec3 getVec3() {
      return this.vec3;
   }

   public void m_1(LivingEntity livingEntity2, double doubleValue) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.isFallFlying() && livingEntity2 != null) {
         this.livingEntity = livingEntity2;
         if (this.m_7(livingEntity2, doubleValue) && this.c7 == ElytraResolver.ModeKind.APPROACHING) {
            this.startRetreating();
         }

         if (this.c7 == ElytraResolver.ModeKind.APPROACHING && doubleValue <= 3.0) {
            this.startRetreating();
         } else if (this.c7 == ElytraResolver.ModeKind.RETREATING && (doubleValue >= this.flyAwayDistance.getFloat() || System.currentTimeMillis() - this.longValue > 2000L)) {
            this.m_1_void();
         }

         this.m_0_void(livingEntity2);
      } else {
         this.startApproaching();
      }
   }

   public void m_3_void(LivingEntity livingEntity2) {
      if (livingEntity2 != null) {
         if (livingEntity2 != this.livingEntity) {
            this.livingEntity = livingEntity2;
         }

         if (this.c7 == ElytraResolver.ModeKind.APPROACHING) {
            this.startRetreating();
         }
      }
   }

   public void startApproaching() {
      this.c7 = ElytraResolver.ModeKind.APPROACHING;
      this.livingEntity = null;
      this.vec3 = null;
      this.vec32 = null;
      this.longValue = 0L;
   }

   @Override
   public void onEnable() {
      this.startApproaching();
   }

   @Override
   public void onDisable() {
      this.startApproaching();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null || !MinecraftAccess.MINECRAFT.player.isFallFlying()) {
         this.startApproaching();
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundLoginPacket
         || event.getPacket() instanceof ClientboundRespawnPacket
         || event.getPacket() instanceof ClientboundDisconnectPacket) {
         this.startApproaching();
      }
   }

   private boolean m_7(LivingEntity livingEntity2, double doubleValue) {
      if (!(doubleValue > 4.0) && MinecraftAccess.MINECRAFT.player != null) {
         Vec3 vec33 = MinecraftAccess.MINECRAFT.player.position().subtract(livingEntity2.position());
         return vec33.lengthSqr() < 1.0E-6 ? true : livingEntity2.getLookAngle().dot(vec33.normalize()) > 0.5 && doubleValue < 3.5;
      } else {
         return false;
      }
   }

   private Vec3 predictPosition(LivingEntity livingEntity2) {
      Vec3 vec33 = livingEntity2.position();
      return vec33.add(vec33.subtract(new Vec3(livingEntity2.xOld, livingEntity2.yOld, livingEntity2.zOld)).scale(3.0));
   }

   private Vec3 m_5(Vec3 vec33, Vec3 vec34) {
      NoClip.m_4_C3_sArr();
      Vec3 vec35 = vec34.subtract(vec33);
      vec35 = new Vec3(vec35.x, 0.0, vec35.z);
      if (vec35.lengthSqr() < 1.0E-6) {
         Vec3 vec36 = MinecraftAccess.MINECRAFT.player.getLookAngle().scale(-1.0);
         vec35 = new Vec3(vec36.x, 0.0, vec36.z);
      }

      if (vec35.lengthSqr() < 1.0E-6) {
         vec35 = new Vec3(0.0, 0.0, 1.0);
      }

      vec35 = vec35.normalize();
      double doubleValue = 45.0;
      doubleValue = this.flag ? doubleValue : -doubleValue;
      this.flag = !this.flag;
      double doubleValue2 = Math.toRadians(doubleValue);
      double doubleValue3 = vec35.x * Math.cos(doubleValue2) - vec35.z * Math.sin(doubleValue2);
      double doubleValue4 = vec35.x * Math.sin(doubleValue2) + vec35.z * Math.cos(doubleValue2);

      double doubleValue5 = switch ((String)this.flyAwayMode.getValue()) {
         case "Вверх" -> 0.55;
         case "Диагональ" -> 0.25;
         default -> 0.06;
      };
      return new Vec3(doubleValue3, doubleValue5, doubleValue4).normalize();
   }

   private void m_1_void() {
      this.c7 = ElytraResolver.ModeKind.APPROACHING;
      this.vec32 = null;
      this.longValue = System.currentTimeMillis();
   }

   private void startRetreating() {
      this.c7 = ElytraResolver.ModeKind.RETREATING;
      this.vec32 = null;
      this.longValue = System.currentTimeMillis();
   }

   private void m_0_void(LivingEntity livingEntity2) {
      Vec3 vec33 = this.predictPosition(livingEntity2);
      if (this.c7 == ElytraResolver.ModeKind.APPROACHING) {
         this.vec3 = vec33.add(0.0, livingEntity2.getBbHeight() * 0.5, 0.0);
      } else {
         if (this.vec32 == null) {
            this.vec32 = this.m_5(vec33, MinecraftAccess.MINECRAFT.player.position());
         }

         double doubleValue = ((String)this.flyAwayMode.getValue()).equals("Вверх") ? 6.0 : (((String)this.flyAwayMode.getValue()).equals("Диагональ") ? 3.0 : 1.5);
         this.vec3 = vec33.add(this.vec32.scale(this.flyAwayDistance.getFloat())).add(0.0, doubleValue, 0.0);
      }
   }

   public enum ModeKind {
      APPROACHING,
      RETREATING;

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
