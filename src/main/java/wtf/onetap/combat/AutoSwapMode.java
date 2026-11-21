package wtf.onetap.combat;

import java.util.function.BooleanSupplier;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

public class AutoSwapMode implements MinecraftAccess {
   private String text;
   private String text2;
   private float floatValue;
   private BooleanSupplier booleanSupplier;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private int value;
   private int value2;
   private int value3;
   private int value4;
   private int value5;

   public AutoSwapMode() {
      this.text = "Пакетный";
      this.text2 = "Дефолт";
   }

   public boolean m_0_boolean() {
      if (this.flag5 && !InventoryUtil.isBusy()) {
         this.flag5 = false;
      }

      return this.flag4 || this.flag5;
   }

   public boolean m_7(String text3, String text4, float floatValue2) {
      return this.m_6(text3, text4, floatValue2, null);
   }

   public boolean m_6(String text3, String text4, float floatValue2, Runnable runnable) {
      return this.m_3(text3, text4, floatValue2, runnable == null ? null : () -> {
         runnable.run();
         return true;
      }, false);
   }

   public boolean m_2(String text3, String text4, float floatValue2, BooleanSupplier booleanSupplier2) {
      return this.m_3(text3, text4, floatValue2, booleanSupplier2, true);
   }

   private boolean m_3(String text3, String text4, float floatValue2, BooleanSupplier booleanSupplier2, boolean flag6) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && !this.m_0_boolean()) {
         this.flag = flag6;
         this.flag2 = false;
         this.flag3 = false;
         if (MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(Items.ENDER_PEARL.getDefaultInstance())) {
            return false;
         } else {
            this.text = text3;
            this.text2 = text4;
            this.floatValue = floatValue2;
            this.booleanSupplier = booleanSupplier2;
            if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.ENDER_PEARL)) {
               return this.m_6(InteractionHand.MAIN_HAND);
            } else if (MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.ENDER_PEARL)) {
               return this.m_6(InteractionHand.OFF_HAND);
            } else {
               this.value3 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
               int value6 = InventoryUtil.findHotbarSlot(Items.ENDER_PEARL);
               if (value6 != -1) {
                  this.value4 = -1;
                  this.flag2 = text3.equals("Пакетный");
                  this.value5 = value6;
                  this.reset();
                  return true;
               } else {
                  int value7 = InventoryUtil.findItemSlotInRange(Items.ENDER_PEARL, 9, 36);
                  if (value7 == -1) {
                     return false;
                  } else if (text3.equals("Пакетный")) {
                     int value8 = this.value3;
                     this.flag5 = true;
                     InventoryUtil.runWithMode(text4, () -> {
                        this.flag5 = false;
                        InventoryUtil.clickSlot(0, value7, value8, ContainerInput.SWAP);
                        this.m_6(InteractionHand.MAIN_HAND);
                        InventoryUtil.clickSlot(0, value7, value8, ContainerInput.SWAP);
                     }, floatValue2);
                     return true;
                  } else {
                     this.value4 = value7;
                     this.reset();
                     return true;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public void m_5_void() {
      if (this.flag4 && MinecraftAccess.MINECRAFT.player != null) {
         this.value++;
         if (this.value4 == -1) {
            this.m_3_void();
         } else {
            this.m_2_void();
         }
      }
   }

   private void reset() {
      this.flag4 = true;
      this.value2 = 0;
      this.value = 0;
   }

   private void m_3_void() {
      if (this.text.equals("Легитный")) {
         if (this.value2 == 0 && this.value >= 1) {
            InventoryUtil.selectHotbarSlot(this.value5);
            this.m_2_void(1);
         } else if (this.value2 == 1 && this.value >= 1) {
            this.m_6(InteractionHand.MAIN_HAND);
            this.m_2_void(2);
         } else if (this.value2 == 2 && this.value >= 5) {
            InventoryUtil.selectHotbarSlot(this.value3);
            this.flag4 = false;
         }
      } else if (this.value2 == 0 && this.value >= 1) {
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(this.value5));
         this.flag3 = true;
         this.m_2_void(1);
      } else if (this.value2 == 1 && this.value >= 1) {
         this.m_6(InteractionHand.MAIN_HAND);
         this.m_2_void(2);
      } else if (this.value2 == 2 && this.value >= 1) {
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(this.value3));
         this.flag3 = false;
         this.flag4 = false;
      }
   }

   private void m_2_void() {
      if (this.value2 == 0) {
         if (this.m_4_boolean()) {
            this.m_1_void();
            this.m_2_void(1);
         }
      } else if (this.value2 == 1) {
         if (this.m_4_boolean() && this.value >= 1) {
            this.m_6(InteractionHand.MAIN_HAND);
            this.m_2_void(2);
         }
      } else if (this.value2 == 2) {
         if (this.value >= 1 && this.m_4_boolean()) {
            this.m_1_void();
            this.m_2_void(3);
         }
      } else if (this.value2 == 3 && this.m_4_boolean() && this.value >= 1) {
         this.flag4 = false;
      }
   }

   private void m_2_void(int value6) {
      this.value2 = value6;
      this.value = 0;
   }

   private void m_1_void() {
      InventoryUtil.runWithMode(this.text2, () -> InventoryUtil.clickSlot(0, this.value4, this.value3, ContainerInput.SWAP), this.floatValue);
   }

   private boolean m_4_boolean() {
      if (this.text2.equals("Грим")) {
         return !InventoryUtil.m_6_boolean();
      } else {
         return this.text2.equals("Легит") ? !InventoryUtil.m_5_boolean() : true;
      }
   }

   private boolean m_6(InteractionHand interactionHand) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         boolean flag6 = this.flag2 && this.flag3 && interactionHand == InteractionHand.MAIN_HAND;
         int value6 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
         if (flag6) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(this.value5);
         }

         try {
            if (!this.flag
               || MinecraftAccess.MINECRAFT.player.getItemInHand(interactionHand).is(Items.ENDER_PEARL) && !MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(MinecraftAccess.MINECRAFT.player.getItemInHand(interactionHand))) {
               if (this.booleanSupplier != null && !this.booleanSupplier.getAsBoolean()) {
                  return false;
               }

               MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, interactionHand);
               MinecraftAccess.MINECRAFT.player.swing(interactionHand);
               return true;
            } else {
               return false;
            }
         } finally {
            if (flag6) {
               MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value6);
            }
         }
      } else {
         return false;
      }
   }
}
