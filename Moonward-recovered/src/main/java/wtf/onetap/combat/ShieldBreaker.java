package wtf.onetap.combat;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

public class ShieldBreaker implements MinecraftAccess {
   private int value;
   private int value2;
   private Player player;

   public ShieldBreaker() {
      this.value = -1;
      this.value2 = -1;
   }

   public void m_2_void(Player player2) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.value != -1) {
            InventoryUtil.selectHotbarSlot(this.value);
            this.value = -1;
         }

         if (this.player != null && (this.player != player2 || !this.isBlocking(this.player))) {
            this.player = null;
         }

         if (player2 != null && this.isBlocking(player2)) {
            this.m_8_void();
         } else {
            this.m_9_void();
         }
      } else {
         this.value = this.value2 = -1;
         this.player = null;
      }
   }

   public boolean m_4_boolean(Player player2) {
      return player2 != null && player2 != this.player && this.value == -1 && !InventoryUtil.isBusy() && player2.isBlocking() && this.m_0_boolean(player2) && this.m_3_int() != -1;
   }

   public void m_6(Player player2, Runnable runnable) {
      int value3 = this.m_3_int();
      int value4 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      if (value3 != value4) {
         InventoryUtil.selectHotbarSlot(value3);
         this.value = value4;
      }

      runnable.run();
      this.player = player2;
   }

   private boolean isBlocking(Player player2) {
      return player2.isUsingItem() && player2.getUseItem().has(DataComponents.BLOCKS_ATTACKS);
   }

   private boolean m_0_boolean(Player player2) {
      Vec3 vec3 = player2.calculateViewVector(0.0F, player2.getYHeadRot());
      Vec3 vec32 = MinecraftAccess.MINECRAFT.player.position().subtract(player2.position());
      return vec3.x * vec32.x + vec3.z * vec32.z >= 0.0;
   }

   private int m_3_int() {
      if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() instanceof AxeItem) {
         return MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      }

      int value3 = InventoryUtil.m_8_int();
      return value3 < 9 ? value3 : -1;
   }

   private void m_8_void() {
      if (this.value2 == -1 && !InventoryUtil.isBusy() && this.isNoScreenOpen()) {
         int value3 = InventoryUtil.m_8_int();
         if (value3 >= 9) {
            this.value2 = value3;
            InventoryUtil.m_0(() -> InventoryUtil.clickSlot(0, value3, 8, ContainerInput.SWAP));
         }
      }
   }

   private void m_9_void() {
      if (this.value2 != -1 && !InventoryUtil.isBusy() && this.isNoScreenOpen()) {
         int value3 = this.value2;
         this.value2 = -1;
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(8).getItem() instanceof AxeItem) {
            InventoryUtil.m_0(() -> InventoryUtil.clickSlot(0, value3, 8, ContainerInput.SWAP));
         }
      }
   }

   private boolean isNoScreenOpen() {
      return MinecraftAccess.MINECRAFT.gui.screen() == null && MinecraftAccess.MINECRAFT.player.containerMenu.containerId == 0;
   }
}
