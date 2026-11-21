package wtf.onetap.module.combat;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AntiBot", description = "Обнаруживает и игнорирует ботов", category = Category.COMBAT)
public class AntiBot extends Module {
   public final ModeSetting mode = new ModeSetting("Режим", "ReallyWorld", "Matrix", "ReallyWorld", "Ручной").build();
   private final BooleanSetting removeFromWorld = new BooleanSetting("Удалять из мира", false).visibleWhen(() -> !this.isManualMode()).build();
   private final Set<UUID> uUIDSet = new HashSet<>();
   private static final Set<UUID> U_U_I_D_SET = new HashSet<>();
   private UUID uUID;
   private String text;
   private boolean flag;
   private static String[] textArray;

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      switch (event.getPacket()) {
         case ClientboundPlayerInfoUpdatePacket clientboundPlayerInfoUpdatePacket:
            this.onPlayerInfoUpdate(clientboundPlayerInfoUpdatePacket);
            break;
         case ClientboundPlayerInfoRemovePacket clientboundPlayerInfoRemovePacket:
            this.onPlayerRemove(clientboundPlayerInfoRemovePacket);
            break;
         default:
      }
   }

   @EventTarget(priority = EventPriority.HIGH)
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.m_7_void();
         if (((String)this.mode.getValue()).equals("Matrix")) {
            this.m_2_void();
         } else {
            this.uUIDSet.clear();
            if (((String)this.mode.getValue()).equals("ReallyWorld")) {
               this.scanOfflineUuids();
            }

            this.m_4_void();
         }
      }
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (event.getPacket() instanceof ServerboundAttackPacket serverboundAttackPacket) {
         if (this.isManualMode() && MinecraftAccess.MINECRAFT.level != null) {
            if (MinecraftAccess.MINECRAFT.level.getEntity(serverboundAttackPacket.entityId()) instanceof Player player && player != MinecraftAccess.MINECRAFT.player && this.isLookedAt(player)) {
               if (!player.getUUID().equals(this.uUID) || !player.getName().getString().equals(this.text)) {
                  this.uUID = player.getUUID();
                  this.text = player.getName().getString();
                  this.log("AntiBot: цель выбрана — " + player.getName().getString());
               }
            }
         }
      }
   }

   private void m_7_void() {
      if (!this.isManualMode()) {
         this.flag = false;
         this.uUID = null;
         this.text = null;
      } else {
         if (!this.flag) {
            this.log("AntiBot: ударьте игрока вручную — аура будет бить только его, пока не ударите другого.");
            this.flag = true;
         }

         if (this.text != null) {
            Player player = this.getTargetPlayer();
            if (player != null && player.isAlive()) {
               this.uUID = player.getUUID();
            }
         }
      }
   }

   private boolean isManualMode() {
      return ((String)this.mode.getValue()).equals("Ручной");
   }

   private boolean shouldRemoveFromWorld() {
      return (Boolean)this.removeFromWorld.getValue() && !this.isManualMode();
   }

   private void log(String text2) {
      ChatUtil.log(text2);
   }

   private boolean isLookedAt(Player player) {
      return MinecraftAccess.MINECRAFT.hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == player;
   }

   private Player getTargetPlayer() {
      if (MinecraftAccess.MINECRAFT.level != null && this.text != null) {
         for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
            if (player.getName().getString().equals(this.text)) {
               return player;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void onPlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket clientboundPlayerInfoUpdatePacket) {
      clientboundPlayerInfoUpdatePacket.newEntries().forEach(entry -> {
         GameProfile gameProfile = entry.profile();
         if (gameProfile != null && !this.isBotProfile(entry, gameProfile)) {
            if (this.isOnline(gameProfile)) {
               U_U_I_D_SET.add(gameProfile.id());
            } else {
               this.uUIDSet.add(gameProfile.id());
            }
         }
      });
   }

   private void onPlayerRemove(ClientboundPlayerInfoRemovePacket clientboundPlayerInfoRemovePacket) {
      clientboundPlayerInfoRemovePacket.profileIds().forEach(uUID2 -> {
         this.uUIDSet.remove(uUID2);
         U_U_I_D_SET.remove(uUID2);
      });
   }

   private boolean isBotProfile(Entry entry, GameProfile gameProfile) {
      return entry.latency() < 2 || gameProfile.properties() != null && !gameProfile.properties().isEmpty();
   }

   private void m_2_void() {
      for (Iterator iterator = this.uUIDSet.iterator(); iterator.hasNext(); iterator.remove()) {
         UUID uUID2 = (UUID)iterator.next();
         Player player = this.getPlayer(uUID2);
         if (player != null) {
            String text2 = player.getName().getString();
            boolean flag2 = text2.startsWith("CIT-") && !text2.contains("NPC") && !text2.contains("[ZNPC]");
            int value = 0;

            for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
               ItemStack itemStack = player.getItemBySlot(equipmentSlot);
               if (!itemStack.isEmpty()) {
                  value++;
               }
            }

            boolean flag3 = value == 4;
            boolean flag4 = !player.getUUID().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + text2).getBytes()));
            if (flag3 || flag2 || flag4) {
               U_U_I_D_SET.add(uUID2);
            }
         }
      }

      if (MinecraftAccess.MINECRAFT.player.tickCount % 100 == 0) {
         U_U_I_D_SET.removeIf(uUID3 -> this.getPlayer(uUID3) == null);
      }

      this.m_4_void();
   }

   private void scanOfflineUuids() {
      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (!player.getUUID().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName().getString()).getBytes()))
            && !U_U_I_D_SET.contains(player.getUUID())
            && !player.getName().getString().contains("NPC")
            && !player.getName().getString().startsWith("[ZNPC]")) {
            U_U_I_D_SET.add(player.getUUID());
         }
      }
   }

   private void m_4_void() {
      if (this.shouldRemoveFromWorld() && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         for (Player player : new ArrayList<>(MinecraftAccess.MINECRAFT.level.players())) {
            if (player != MinecraftAccess.MINECRAFT.player && this.isBot(player)) {
               MinecraftAccess.MINECRAFT.level.removeEntity(player.getId(), RemovalReason.DISCARDED);
            }
         }
      }
   }

   public boolean isOnline(GameProfile gameProfile) {
      return Objects.requireNonNull(MinecraftAccess.MINECRAFT.getConnection())
            .getOnlinePlayers()
            .stream()
            .filter(playerInfo -> playerInfo.getProfile().name().equals(gameProfile.name()) && !playerInfo.getProfile().id().equals(gameProfile.id()))
            .count()
         == 1L;
   }

   public boolean isBot(Player player) {
      if (!this.isEnabled()) {
         return false;
      }

      if (this.isManualMode()) {
         return !this.isTarget(player);
      }

      String text2 = player.getName().getString();
      boolean flag2 = text2.startsWith("CIT-") && !text2.contains("NPC") && !text2.startsWith("[ZNPC]");
      boolean flag3 = U_U_I_D_SET.contains(player.getUUID());
      return flag2 || flag3;
   }

   public boolean m_7(UUID uUID2) {
      return !this.isEnabled() ? false : U_U_I_D_SET.contains(uUID2);
   }

   private boolean isTarget(Player player) {
      return this.text == null ? false : player.getUUID().equals(this.uUID) || player.getName().getString().equals(this.text);
   }

   private Player getPlayer(UUID uUID2) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return null;
      } else {
         return MinecraftAccess.MINECRAFT.level.getEntity(uUID2) instanceof Player player ? player : null;
      }
   }

   public void clear() {
      this.uUIDSet.clear();
      U_U_I_D_SET.clear();
      this.uUID = null;
      this.text = null;
      this.flag = false;
   }

   @Override
   public void onEnable() {
      this.flag = false;
   }

   @Override
   public void onDisable() {
      this.clear();
   }

   static {
      m_2_void(new String[1]);
   }

   public static void m_2_void(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_0_StringArr() {
      return textArray;
   }
}
