package wtf.onetap.combat.crystal;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.player.Player;
import wtf.onetap.mixin.player.MultiPlayerGameModeAccessor;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;

public class HotbarSwitcher implements MinecraftAccess {
   private int value;
   private int value2;
   private int value3;
   private boolean flag;
   private boolean flag2;
   private Player player;

   public HotbarSwitcher() {
      this.value = -1;
      this.value2 = -1;
      this.value3 = -1;
   }

   private static void m_4_void(int value4) {
      MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value4);
      if (PacketUtil.m_1_int() != value4) {
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value4));
      }

      ((MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode).onetap$setCarriedIndex(value4);
   }

   public void markTick() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         this.value3 = MinecraftAccess.MINECRAFT.player.tickCount;
      }
   }

   public void m_8_void() {
      if (!MinecraftAccess.MINECRAFT.isSameThread()) {
         this.flag2 = true;
      } else if (MinecraftAccess.MINECRAFT.player != null && this.value3 == MinecraftAccess.MINECRAFT.player.tickCount) {
         this.flag = true;
      } else {
         this.flag = false;
         if (this.value != -1
            && MinecraftAccess.MINECRAFT.player != null
            && MinecraftAccess.MINECRAFT.player == this.player
            && MinecraftAccess.MINECRAFT.gameMode != null
            && MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() == this.value2) {
            m_4_void(this.value);
         }

         this.value = this.value2 = -1;
         this.player = null;
      }
   }

   public void m_3_void() {
      if (this.flag2) {
         this.flag2 = false;
         this.m_8_void();
      }
   }

   public void m_5_void() {
      if (this.flag && MinecraftAccess.MINECRAFT.player != null && this.value3 != MinecraftAccess.MINECRAFT.player.tickCount) {
         this.m_8_void();
      }
   }

   public void m_2_void(int value4) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && value4 >= 0 && value4 <= 8) {
         this.flag = false;
         if (this.player != MinecraftAccess.MINECRAFT.player) {
            this.value = this.value2 = -1;
            this.player = MinecraftAccess.MINECRAFT.player;
         }

         int value5 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
         if (this.value == -1 || value5 != this.value2) {
            this.value = value5;
         }

         this.value2 = value4;
         m_4_void(value4);
      }
   }
}
