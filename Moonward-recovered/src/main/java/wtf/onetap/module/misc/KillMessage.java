package wtf.onetap.module.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.entity.player.Player;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "KillMessage", description = "Пишет в глобальный чат при убийстве и сносе тотема таргету", category = Category.MISC)
public class KillMessage extends Module {
   private final ModeSetting server = new ModeSetting("Сервер", "ReallyWorld", "ReallyWorld", "FunTime").build();
   private final BooleanSetting kill = new BooleanSetting("Убийство", true).build();
   private final BooleanSetting totem = new BooleanSetting("Тотем", true).build();
   private final Map<UUID, KillMessage.DataRecord> uUIDMap = new HashMap<>();
   private final TimeHelper timeHelper = new TimeHelper();
   private String text;
   private boolean flag;

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      if (event.getEntity() instanceof Player player && player != MinecraftAccess.MINECRAFT.player) {
         long longValue = System.currentTimeMillis();
         this.uUIDMap.values().removeIf(c1 -> longValue - c1.m_8o() > 180000L);
         this.uUIDMap.put(player.getUUID(), new KillMessage.DataRecord(player.getName().getString(), longValue));
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && !this.uUIDMap.isEmpty()) {
         if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
            String text2 = clientboundSystemChatPacket.content().getString();
            if (!clientboundSystemChatPacket.overlay() && ChatUtil.containsWord(text2, MinecraftAccess.MINECRAFT.player.getName().getString())) {
               long longValue = System.currentTimeMillis();

               for (Entry entry : List.copyOf(this.uUIDMap.entrySet())) {
                  if (longValue - ((KillMessage.DataRecord)entry.getValue()).m_8o() <= 10000L && ChatUtil.containsWord(text2, ((KillMessage.DataRecord)entry.getValue()).m_9r())) {
                     this.onKill((UUID)entry.getKey());
                  }
               }
            }
         } else if (event.getPacket() instanceof ClientboundEntityEventPacket clientboundEntityEventPacket) {
            if (clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) instanceof Player player && player != MinecraftAccess.MINECRAFT.player) {
               if (clientboundEntityEventPacket.getEventId() == 3) {
                  this.onKill(player.getUUID());
               } else if (clientboundEntityEventPacket.getEventId() == 35 && (Boolean)this.totem.getValue()) {
                  KillMessage.DataRecord c1 = this.uUIDMap.get(player.getUUID());
                  if (c1 == null || System.currentTimeMillis() - c1.m_8o() > 10000L) {
                     return;
                  }
                  this.m_6(switch ((String)this.server.getValue()) {
                     case "FunTime" -> "Кидай Сферу/Талисман отпускаю клянусь";
                     default -> "Кидай Шар/Элитры отпускаю клянусь";
                  }, false);
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null) {
         for (UUID uUID : List.copyOf(this.uUIDMap.keySet())) {
            Player player = MinecraftAccess.MINECRAFT.level.getPlayerByUUID(uUID);
            if (player != null && (player.isDeadOrDying() || player.isSpectator())) {
               this.onKill(uUID);
            }
         }
      }

      if (this.text != null && this.timeHelper.hasElapsed(4000L)) {
         this.sendMessage();
      }
   }

   private void onKill(UUID uUID) {
      KillMessage.DataRecord c1 = this.uUIDMap.remove(uUID);
      if (c1 != null && (Boolean)this.kill.getValue() && System.currentTimeMillis() - c1.m_8o() <= 180000L) {
         this.m_6("!-ezz " + c1.m_9r() + " хули ваш сын как долбоёб купите ему MoonWard", true);
      }
   }

   private void m_6(String text2, boolean flag2) {
      if (!this.flag || flag2) {
         this.text = text2;
         this.flag = flag2;
         if (this.timeHelper.hasElapsed(4000L)) {
            this.sendMessage();
         }
      }
   }

   private void sendMessage() {
      if (MinecraftAccess.MINECRAFT.getConnection() != null) {
         MinecraftAccess.MINECRAFT.getConnection().sendChat(this.text);
      }

      this.text = null;
      this.flag = false;
      this.timeHelper.reset();
   }

   @Override
   public void onDisable() {
      this.uUIDMap.clear();
      this.text = null;
      this.flag = false;
   }

   private record DataRecord(String f_9r, long f_8o) {
      public String m_9r() {
         return this.f_9r;
      }

      public long m_8o() {
         return this.f_8o;
      }
   }
}
