package wtf.onetap.module.movement;

import java.util.ArrayDeque;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.ElytraTarget;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;

@ModuleInformation(name = "Blink", description = "Копит исходящие пакеты и отправляет", category = Category.MOVEMENT)
public class Blink extends Module {
   private final BooleanSetting pulse = new BooleanSetting("Пульсация", false).build();
   private final FloatSetting intervalMs = new FloatSetting("Интервал (мс)", 500.0F, 100.0F, 2000.0F, 50.0F).visibleWhen(this.pulse::getValue).build();
   private final BooleanSetting showPosition = new BooleanSetting("Показывать позицию", true).build();
   private final ArrayDeque<Packet<?>> arrayDeque = new ArrayDeque<>();
   private ClientCommonPacketListenerImpl clientCommonPacketListenerImpl;
   private long longValue;
   private boolean flag;

   public boolean m_3(ClientCommonPacketListenerImpl clientCommonPacketListenerImpl2, Packet<?> packet) {
      if (!this.isEnabled() || this.flag || MinecraftAccess.MINECRAFT.player == null || clientCommonPacketListenerImpl2 != MinecraftAccess.MINECRAFT.getConnection()) {
         return false;
      }

      if (packet instanceof ServerboundConfigurationAcknowledgedPacket) {
         this.setEnabled(false);
         return false;
      }

      if (clientCommonPacketListenerImpl2 != this.clientCommonPacketListenerImpl) {
         this.arrayDeque.clear();
         this.clientCommonPacketListenerImpl = clientCommonPacketListenerImpl2;
      }

      if (this.arrayDeque.isEmpty()) {
         this.longValue = System.currentTimeMillis();
      }

      this.arrayDeque.add(packet);
      return true;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if ((Boolean)this.pulse.getValue() && !this.arrayDeque.isEmpty() && System.currentTimeMillis() - this.longValue >= this.intervalMs.getInt()) {
         this.m_7_void();
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      NoClip.m_4_C3_sArr();
      Vec3 vec3 = PacketUtil.m_1_Vec3();
      if ((Boolean)this.showPosition.getValue() && !this.arrayDeque.isEmpty() && vec3 != null && MinecraftAccess.MINECRAFT.player != null) {
         Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         Vec3 vec32 = event.getVec3();
         AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().move(vec3.subtract(MinecraftAccess.MINECRAFT.player.position()));
         int value = ColorUtil.primaryColor();
         Box3DRenderer.drawBox(
            matrix4f,
            (float)(aABB.minX - vec32.x),
            (float)(aABB.minY - vec32.y),
            (float)(aABB.minZ - vec32.z),
            (float)(aABB.maxX - vec32.x),
            (float)(aABB.maxY - vec32.y),
            (float)(aABB.maxZ - vec32.z),
            ColorUtil.withAlpha(value, 40)
         );
         Line3DRenderer.drawLines(matrix4f, ElytraTarget.boxToLines(aABB), vec32, 1.5F, ColorUtil.withAlpha(value, 220));
      }
   }

   @Override
   public void onDisable() {
      this.m_7_void();
      this.clientCommonPacketListenerImpl = null;
   }

   private void m_7_void() {
      if (this.clientCommonPacketListenerImpl == MinecraftAccess.MINECRAFT.getConnection() && MinecraftAccess.MINECRAFT.player != null) {
         this.flag = true;

         Packet packet;
         try {
            while ((packet = this.arrayDeque.poll()) != null) {
               PacketUtil.sendPacket(packet);
            }
         } finally {
            this.flag = false;
         }
      } else {
         this.arrayDeque.clear();
      }
   }
}
