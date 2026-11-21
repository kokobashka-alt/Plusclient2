package wtf.onetap.module.player;

import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.network.ServerInfoListener;
import wtf.onetap.render.GuiScale;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "No Fall", description = "Убирает урон от падения", category = Category.PLAYER)
public class NoFall extends Module {
   private final ModeSetting mode = new ModeSetting("Мод", "Grim Old", "Grim Old", "Grim New").build();
   public int value;
   public boolean flag;
   private boolean flag2;
   private int value2;
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();

   @Override
   public void onDisable() {
      super.onDisable();
      this.reset();
      this.value2 = 0;
      this.value = 0;
      GuiScale.setScale(1.0F);
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if ("LonyGrief".equals(ServerInfoListener.m_0_String())) {
            GuiScale.setScale(1.0F);
         } else {
            if (((String)this.mode.getValue()).equals("Grim Old")) {
               this.flag = MinecraftAccess.MINECRAFT.player.fallDistance > 3.0;
               if (this.value2 > 0) {
                  this.value2--;
               }

               if (this.flag && !MinecraftAccess.MINECRAFT.player.onGround() && PlayerUtil.predictLocal(1).flag4 && this.timeHelper2.hasElapsed(200L)) {
                  boolean flag3 = false;
                  ElytraSwap elytraSwap = Onetap.MODULE_MANAGER.get(ElytraSwap.class);
                  if (elytraSwap != null && MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
                     flag3 = elytraSwap.m_3_boolean(true);
                  }

                  PacketUtil.sendPacket(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.START_FALL_FLYING));
                  PacketUtil.sendPacket(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.START_FALL_FLYING));
                  if (elytraSwap != null && flag3) {
                     elytraSwap.m_3_boolean(false);
                  }

                  this.timeHelper2.reset();
                  this.flag2 = true;
                  this.timeHelper.reset();
               }

               if (this.timeHelper.hasElapsed(300L)) {
                  this.flag2 = false;
                  this.value2 = 0;
               }
            } else {
               this.flag = MinecraftAccess.MINECRAFT.player.fallDistance > 3.0;
               if (this.value == 2) {
                  GuiScale.setScale(0.5F);
               }

               if (this.value <= 1) {
                  GuiScale.setScale(1.0F);
               }

               if (this.value > 0) {
                  this.value--;
               }

               if (this.timeHelper.hasElapsed(300L)) {
                  this.flag2 = false;
                  this.value = 0;
               }
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (!(event.getPacket() instanceof ClientboundLoginPacket)
            && !(event.getPacket() instanceof ClientboundDisconnectPacket)
            && !(event.getPacket() instanceof ClientboundRespawnPacket)) {
            if (event.getPacket() instanceof ClientboundPlayerPositionPacket && this.flag2) {
               if (((String)this.mode.getValue()).equals("Grim Old")) {
                  this.value2 = 2;
               } else {
                  this.value = 2;
               }

               this.flag2 = false;
            }
         } else {
            this.reset();
            this.value2 = 0;
         }
      }
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (((String)this.mode.getValue()).equals("Grim New")
            && event.getPacket() instanceof ServerboundMovePlayerPacket serverboundMovePlayerPacket
            && this.flag
            && serverboundMovePlayerPacket.isOnGround()
            && this.value == 0) {
            PacketUtil.sendPacket(new StatusOnly(true, false));
            this.timeHelper.reset();
            this.flag2 = true;
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (((String)this.mode.getValue()).equals("Grim Old")) {
         if (this.flag2) {
            event.setJumping(false);
         }

         if (this.value2 == 2) {
            event.setJumping(false);
         }

         if (this.value2 == 1) {
            event.setJumping(true);
         }
      } else {
         if (this.flag2) {
            event.setJumping(false);
         }

         if (this.value == 2) {
            event.setJumping(false);
         }

         if (this.value == 1) {
            event.setJumping(true);
         }
      }
   }

   private void reset() {
      this.value = 0;
      this.flag = false;
      this.flag2 = false;
   }
}
