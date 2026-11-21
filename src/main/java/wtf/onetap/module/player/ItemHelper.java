package wtf.onetap.module.player;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "ItemHelper", description = "Подсветка предметов и бинды", category = Category.PLAYER)
public class ItemHelper extends Module {
   private final BooleanSetting highlightHealingPotion = new BooleanSetting("Подсвет зелья исцеления", false).build();
   private final ColorSetting healingPotionColor = new ColorSetting("Цвет зелья исцеления", -54599).build();
   private final BooleanSetting highlightEnchantedApples = new BooleanSetting("Подсвет чарок", false).build();
   private final ColorSetting enchantedAppleColor = new ColorSetting("Цвет чарок", -21357).build();
   private final BooleanSetting highlightGoldenApples = new BooleanSetting("Подсвет золотых яблок", false).build();
   private final ColorSetting goldenAppleColor = new ColorSetting("Цвет золотого яблока", -1578154).build();
   private final KeybindSetting goldenApple = new KeybindSetting("Золотое яблоко", -1).build();
   private final KeybindSetting enchantedApple = new KeybindSetting("Зачарованное яблоко", -1).build();
   private final KeybindSetting healingPotion = new KeybindSetting("Зелье исцеления", -1).build();
   private final KeybindSetting goldenCarrot = new KeybindSetting("Золотая морковь", -1).build();
   private final KeybindSetting chorus = new KeybindSetting("Хорус", -1).build();
   private final KeybindSetting trident = new KeybindSetting("Трезубец", -1).build();
   private final KeybindSetting windCharge = new KeybindSetting("Заряд ветра", -1).build();
   private int value = -1;
   private int value2 = -1;
   private int value3 = -1;
   private int value4 = -1;
   private int value5 = -1;
   private int value6 = -1;
   private Item item = null;
   private int value7 = -1;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private boolean flag6;
   private float floatValue;
   private float floatValue2;
   private boolean flag7;
   private float floatValue3;
   private int value8;

   public static ItemHelper getInstance() {
      return Onetap.MODULE_MANAGER.get(ItemHelper.class);
   }

   public void renderSlots(GuiGraphicsExtractor guiGraphicsExtractor, AbstractContainerMenu abstractContainerMenu) {
      for (Slot slot : abstractContainerMenu.slots) {
         if (slot != null && slot.hasItem()) {
            this.m_5(guiGraphicsExtractor, slot.getItem(), slot.x, slot.y);
         }
      }
   }

   public void m_6(GuiGraphicsExtractor guiGraphicsExtractor) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof CreativeModeInventoryScreen)) {
            int value9 = guiGraphicsExtractor.guiWidth() / 2;
            int value10 = guiGraphicsExtractor.guiHeight() - 16 - 3;

            for (int i = 0; i < 9; i++) {
               ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
               if (!itemStack.isEmpty()) {
                  this.m_5(guiGraphicsExtractor, itemStack, value9 - 90 + i * 20 + 2, value10);
               }
            }
         }
      }
   }

   private void m_5(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, int value9, int value10) {
      int value11 = getHighlightColor(itemStack);
      if (value11 != 0) {
         guiGraphicsExtractor.fill(value9, value10, value9 + 16, value10 + 16, scaleAlpha(pulseColor(value11), 0.3F));
      }
   }

   private static int pulseColor(int value9) {
      float floatValue4 = (float)Math.abs(Math.sin(System.currentTimeMillis() / 3.0 * Math.PI / 180.0));
      int value10 = Math.round((value9 >>> 24 & 0xFF) * floatValue4);
      return value10 << 24 | value9 & 16777215;
   }

   private static int scaleAlpha(int value9, float floatValue4) {
      int value10 = Math.round((value9 >>> 24 & 0xFF) * floatValue4);
      return value10 << 24 | value9 & 16777215;
   }

   public static int getHighlightColor(ItemStack itemStack) {
      ItemHelper itemHelper = getInstance();
      if (itemHelper == null || !itemHelper.isEnabled()) {
         return 0;
      } else if ((Boolean)itemHelper.highlightEnchantedApples.getValue() && itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
         return (Integer)itemHelper.enchantedAppleColor.getValue();
      } else if ((Boolean)itemHelper.highlightGoldenApples.getValue() && itemStack.is(Items.GOLDEN_APPLE)) {
         return (Integer)itemHelper.goldenAppleColor.getValue();
      } else {
         return itemHelper.highlightHealingPotion.getValue() && m_7_boolean(itemStack) ? (Integer)itemHelper.healingPotionColor.getValue() : 0;
      }
   }

   private static boolean m_7_boolean(ItemStack itemStack) {
      if (!itemStack.is(Items.SPLASH_POTION)) {
         return false;
      }

      PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
      if (potionContents == null) {
         return false;
      }

      for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
         if (mobEffectInstance.is(MobEffects.INSTANT_HEALTH)) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gui.screen() == null) {
         if (event.getAction() == 1 && event.getKey() == (Integer)this.windCharge.getValue() && !ChatInputHelper.isTextInputOpen()) {
            this.m_3_void();
         } else {
            Item item2 = this.m_7_Item(event.getKey());
            if (item2 != null) {
               if (event.getAction() == 1 && this.value7 == -1 && !ChatInputHelper.isTextInputOpen()) {
                  this.m_3(item2, event.getKey());
               } else if (event.getAction() == 0 && event.getKey() == this.value7) {
                  this.flag = true;
               }
            }
         }
      }
   }

   @EventTarget(priority = EventPriority.HIGHEST)
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.value2 != -1) {
            int value9 = this.value2;
            this.value2 = -1;
            InventoryUtil.selectHotbarSlot(value9);
         }

         this.m_7_void();
         if (this.value7 != -1) {
            if (this.flag || !InputConstants.isKeyDown(MinecraftAccess.MINECRAFT.getWindow(), this.value7)) {
               this.m_1_void();
            } else if (!InventoryUtil.m_6_boolean()) {
               if (this.item == Items.TRIDENT) {
                  if (!this.flag2 && this.matches(MinecraftAccess.MINECRAFT.player.getMainHandItem(), this.item)) {
                     this.flag2 = true;
                     ((MinecraftAccessor)MinecraftAccess.MINECRAFT).setRightClickDelay(0);
                     ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$startUseItem();
                  }
               } else {
                  MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
                  if (!MinecraftAccess.MINECRAFT.player.isUsingItem()) {
                     ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
                     if (!itemStack.isEmpty() && this.matches(itemStack, this.item)) {
                        ((MinecraftAccessor)MinecraftAccess.MINECRAFT).setRightClickDelay(0);
                        ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$startUseItem();
                     } else {
                        this.m_1_void();
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget(priority = EventPriority.LOWEST)
   private void onConnected(ConnectedEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         this.m_4_void();
      }
   }

   private void m_3_void() {
      if (!this.flag3 && !this.flag4 && this.value7 == -1 && !InventoryUtil.m_6_boolean()) {
         if (this.findHotbarItem(Items.WIND_CHARGE) != -1 || this.findInventoryItem(Items.WIND_CHARGE) != -1) {
            this.flag6 = MinecraftAccess.MINECRAFT.options.keyUp.isDown();
            MinecraftAccess.MINECRAFT.options.keyUp.setDown(false);
            this.flag5 = MinecraftAccess.MINECRAFT.options.keyJump.isDown();
            Rotations rotations = Onetap.ROTATION_MODES.toRotations();
            this.floatValue = rotations.getFloatValue2();
            this.floatValue2 = rotations.getFloatValue();
            this.floatValue3 = rotations.getFloatValue2();
            this.flag7 = false;
            this.flag3 = true;
            this.flag4 = false;
         }
      }
   }

   private void m_4_void() {
      if (this.flag3 || this.flag4 || this.flag7) {
         MinecraftAccess.MINECRAFT.options.keyUp.setDown(false);
         if (this.flag3) {
            Rotations rotations = Onetap.ROTATION_MODES.toRotations();
            float floatValue4 = Math.min(90.0F, rotations.getFloatValue2() + 20.0F);
            Onetap.ROTATION_MODES.rotate(new Rotations(this.floatValue2, floatValue4), false, 1003);
            this.floatValue3 = floatValue4;
            if (!(floatValue4 < 90.0F)) {
               this.flag3 = false;
               this.m_9y_void();
               if (MinecraftAccess.MINECRAFT.player.onGround()) {
                  MinecraftAccess.MINECRAFT.options.keyJump.setDown(true);
                  this.flag4 = true;
               } else {
                  this.flag4 = false;
                  this.flag7 = true;
                  MinecraftAccess.MINECRAFT.options.keyJump.setDown(this.flag5);
               }

               this.value8 = 2;
            }
         } else {
            if (this.flag4) {
               Onetap.ROTATION_MODES.rotate(new Rotations(this.floatValue2, this.floatValue3), false, 1003);
               if (MinecraftAccess.MINECRAFT.player.onGround()) {
                  MinecraftAccess.MINECRAFT.options.keyJump.setDown(true);
                  return;
               }

               MinecraftAccess.MINECRAFT.options.keyJump.setDown(this.flag5);
               this.flag4 = false;
               this.flag7 = true;
            }

            if (this.flag7) {
               Onetap.ROTATION_MODES.toRotations();
               float floatValue5 = this.floatValue3;
               float floatValue6 = this.floatValue - floatValue5;
               float floatValue7 = Math.abs(floatValue6) <= 20.0F ? this.floatValue : floatValue5 + Math.copySign(20.0F, floatValue6);
               this.floatValue3 = floatValue7;
               Onetap.ROTATION_MODES.rotate(new Rotations(this.floatValue2, floatValue7), false, 1003);
               if (floatValue7 == this.floatValue) {
                  this.flag7 = false;
                  Onetap.ROTATION_MODES.restorePlayerRotation();
               }
            }

            if (this.value8 > 0) {
               this.value8--;
               if (this.value8 == 0) {
                  if (this.flag6) {
                     MinecraftAccess.MINECRAFT.options.keyUp.setDown(true);
                  }

                  this.flag6 = false;
               }
            }
         }
      }
   }

   private void m_9y_void() {
      if (this.findHotbarItem(Items.WIND_CHARGE) != -1) {
         this.m_7_void(Items.WIND_CHARGE);
      } else {
         InventoryUtil.m_6(() -> {
            int value9 = this.findInventoryItem(Items.WIND_CHARGE);
            if (value9 != -1) {
               int value10 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
               int value11 = InventoryUtil.m_5_int();
               boolean flag8 = value11 != value10;
               InventoryUtil.clickSlot(0, value9, value11, ContainerInput.SWAP);
               MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundContainerClosePacket(0));
               if (flag8) {
                  MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value11));
               }

               MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
               if (flag8) {
                  MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value10));
               }

               InventoryUtil.clickSlot(0, value9, value11, ContainerInput.SWAP);
            }
         });
      }
   }

   private void m_7_void(Item item2) {
      int value9 = this.findHotbarItem(item2);
      if (value9 != -1) {
         int value10 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
         if (value9 == value10) {
            MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
         } else {
            MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value9));
            MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
            MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value10));
         }
      }
   }

   private void m_3(Item item2, int value9) {
      if (!InventoryUtil.m_6_boolean()) {
         this.flag = false;
         this.flag2 = false;
         this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
         int value10 = this.findHotbarItem(item2);
         if (value10 != -1) {
            if (value10 != this.value) {
               this.value2 = value10;
            }
         } else {
            int value11 = this.findInventoryItem(item2);
            if (value11 == -1) {
               this.value = -1;
               return;
            }

            int value12 = value11;
            int value13 = InventoryUtil.m_5_int();
            this.value3 = value12;
            this.value4 = value13;
            InventoryUtil.m_0(() -> {
               InventoryUtil.clickSlot(0, value12, value13, ContainerInput.SWAP);
               MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundContainerClosePacket(0));
               InventoryUtil.selectHotbarSlot(value13);
            });
         }

         this.item = item2;
         this.value7 = value9;
      }
   }

   private void m_1_void() {
      if (this.value7 != -1) {
         boolean flag8 = MinecraftAccess.MINECRAFT.player != null && this.value != -1 && MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != this.value;
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
            MinecraftAccess.MINECRAFT.options.keyUse.setDown(false);
            MinecraftAccess.MINECRAFT.gameMode.releaseUsingItem(MinecraftAccess.MINECRAFT.player);
         }

         this.value2 = -1;
         if (flag8) {
            this.value2 = this.value;
         }

         this.m_2_void();
         this.value7 = -1;
         this.item = null;
         this.flag2 = false;
      }
   }

   private void m_2_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         if (this.value3 != -1) {
            this.value5 = this.value3;
            this.value6 = this.value4;
            this.value3 = -1;
            this.value4 = -1;
         }

         this.value = -1;
      }
   }

   private void m_7_void() {
      if (this.value5 != -1 && !InventoryUtil.m_6_boolean()) {
         int value9 = this.value5;
         int value10 = this.value6;
         this.value5 = -1;
         InventoryUtil.m_0(() -> {
            InventoryUtil.clickSlot(0, value9, value10, ContainerInput.SWAP);
            MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundContainerClosePacket(0));
         });
      }
   }

   private boolean matches(ItemStack itemStack, Item item2) {
      return item2 == Items.SPLASH_POTION ? m_7_boolean(itemStack) : itemStack.is(item2);
   }

   private Item m_7_Item(int value9) {
      if (value9 == -1) {
         return null;
      } else if (value9 == (Integer)this.goldenApple.getValue()) {
         return Items.GOLDEN_APPLE;
      } else if (value9 == (Integer)this.enchantedApple.getValue()) {
         return Items.ENCHANTED_GOLDEN_APPLE;
      } else if (value9 == (Integer)this.healingPotion.getValue()) {
         return Items.SPLASH_POTION;
      } else if (value9 == (Integer)this.goldenCarrot.getValue()) {
         return Items.GOLDEN_CARROT;
      } else if (value9 == (Integer)this.chorus.getValue()) {
         return Items.CHORUS_FRUIT;
      } else {
         return value9 == this.trident.getValue() ? Items.TRIDENT : null;
      }
   }

   private int findHotbarItem(Item item2) {
      if (item2 == Items.SPLASH_POTION) {
         for (int i = 0; i < 9; i++) {
            if (m_7_boolean(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
               return i;
            }
         }

         return -1;
      } else {
         return InventoryUtil.findHotbarSlot(item2);
      }
   }

   private int findInventoryItem(Item item2) {
      if (item2 == Items.SPLASH_POTION) {
         for (int i = 9; i < 36; i++) {
            if (m_7_boolean(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
               return i;
            }
         }

         return -1;
      } else {
         return InventoryUtil.findItemSlotInRange(item2, 9, 36);
      }
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.m_1_void();
      this.flag3 = false;
      this.flag4 = false;
      this.flag7 = false;
      this.value8 = 0;
      if (MinecraftAccess.MINECRAFT.options != null) {
         MinecraftAccess.MINECRAFT.options.keyJump.setDown(this.flag5);
         if (this.flag6) {
            MinecraftAccess.MINECRAFT.options.keyUp.setDown(true);
         }
      }

      this.flag5 = false;
      this.flag6 = false;
      if (this.value2 != -1) {
         InventoryUtil.selectHotbarSlot(this.value2);
         this.value2 = -1;
      }

      this.m_7_void();
      this.value5 = -1;
      this.value = -1;
   }
}
