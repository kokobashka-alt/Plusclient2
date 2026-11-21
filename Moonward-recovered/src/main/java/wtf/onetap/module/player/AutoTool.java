package wtf.onetap.module.player;

import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.input.KeyMappingAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.movement.NoClip;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoTool", description = "Автоматически берёт лучший инструмент", category = Category.PLAYER)
public class AutoTool extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Обычный", "Обычный", "Скрытый").build();
   private int value = -1;
   private int value2 = -1;
   private int value3 = -1;
   private int value4;
   private int value5 = -1;
   private int value6 = -1;
   private Item item;
   private int value7 = -1;
   private int value8 = -1;
   private Item item2;
   private static boolean flag;

   @EventTarget
   private void onTick(TickEvent event) {
      this.value3 = -1;
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null && !MinecraftAccess.MINECRAFT.player.isCreative()) {
         this.m_6b_void();
         if (this.isMining()) {
            BlockPos blockPos = this.m_7_BlockPos();
            if (blockPos != null) {
               this.m_7_void(blockPos);
            }

            if (this.value != -1 || this.value5 != -1) {
               this.value4 = this.value5 == -1 ? 2 : 3;
            }
         } else if (this.value != -1 || this.value5 != -1) {
            if (this.value4 > 0) {
               this.value4--;
            } else if (!this.isSlotChanged()) {
               this.m_4_void();
               this.restoreSlot();
               this.reset();
            }
         }
      } else {
         this.reset();
         this.m_7_void();
         this.resetSlots();
      }
   }

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      if (this.value3 != -1 && MinecraftAccess.MINECRAFT.gameMode != null) {
         int value9 = this.value3;
         this.value3 = -1;
         if (MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != value9) {
            this.m_65_void();
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value9);
            this.value2 = value9;
            MinecraftAccess.MINECRAFT.gameMode.ensureHasSentCarriedItem();
         }
      }
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (event.getAction() == 1 && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gui.screen() == null) {
         for (KeyMapping keyMapping : MinecraftAccess.MINECRAFT.options.keyHotbarSlots) {
            if (((KeyMappingAccessor)keyMapping).onetap$getKey().getValue() == event.getKey()) {
               this.reset();
               return;
            }
         }
      }
   }

   private void m_7_void(BlockPos blockPos) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos);
      if (!(blockState.getDestroyProgress(MinecraftAccess.MINECRAFT.player, MinecraftAccess.MINECRAFT.level, blockPos) >= 1.0F)) {
         this.m_9_void(blockState);
         this.value3 = this.m_8_int(blockState);
      }
   }

   private void m_9_void(BlockState blockState) {
      if (this.value7 == -1 && !InventoryUtil.isBusy() && MinecraftAccess.MINECRAFT.player.containerMenu == MinecraftAccess.MINECRAFT.player.inventoryMenu) {
         int value9 = m_2(blockState, m_3_ItemStack(blockState));
         if (value9 == -1) {
            if (this.value5 != -1 && findBestToolSlot(blockState) != this.value5) {
               this.restoreSlot();
            }
         } else {
            int value10 = this.value5;
            int value11 = -1;
            if (value10 != -1) {
               if (value9 == this.value6) {
                  this.restoreSlot();
                  return;
               }

               if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(value10).getItem() == this.item) {
                  value11 = this.value6;
               }

               this.resetSlots();
            } else {
               value10 = this.m_7(blockState);
            }

            if (value10 != -1) {
               int value12 = MinecraftAccess.MINECRAFT.player.containerMenu.containerId;
               int value13 = value10;
               int value14 = value11;
               this.value7 = value10;
               this.value8 = value9;
               this.item2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value9).getItem();
               InventoryUtil.m_0(() -> {
                  if (value14 != -1) {
                     InventoryUtil.clickSlot(value12, value14, value13, ContainerInput.SWAP);
                  }

                  InventoryUtil.clickSlot(value12, value9, value13, ContainerInput.SWAP);
               });
            }
         }
      }
   }

   private void m_6b_void() {
      if (this.value7 != -1 && !InventoryUtil.isBusy()) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(this.value7).getItem() == this.item2) {
            this.value5 = this.value7;
            this.value6 = this.value8;
            this.item = this.item2;
         }

         this.m_7_void();
      }
   }

   private int m_7(BlockState blockState) {
      int value9 = m_0_int(this.value);
      if (value9 != -1) {
         return value9;
      }

      int value10 = findBestToolSlot(blockState);
      return value10 != -1 && value10 != this.value ? value10 : m_8_int(this.value);
   }

   private int m_8_int(BlockState blockState) {
      Inventory inventory = MinecraftAccess.MINECRAFT.player.getInventory();
      int value9 = findBestToolSlot(blockState);
      if (value9 != -1 && value9 != inventory.getSelectedSlot()) {
         ItemStack itemStack = inventory.getItem(value9);
         ItemStack itemStack2 = inventory.getItem(inventory.getSelectedSlot());
         if (isShearsForCobweb(itemStack, blockState) && !isShearsForCobweb(itemStack2, blockState)) {
            return value9;
         } else {
            return getToolSpeed(itemStack, blockState) > getToolSpeed(itemStack2, blockState) + 1.0E-4F ? value9 : -1;
         }
      } else {
         return -1;
      }
   }

   private void m_4_void() {
      if (this.value != -1 && MinecraftAccess.MINECRAFT.player != null) {
         if (this.value2 == -1 || MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() == this.value2) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(this.value);
         }
      }
   }

   private void restoreSlot() {
      if (this.value5 != -1 && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (MinecraftAccess.MINECRAFT.player.containerMenu == MinecraftAccess.MINECRAFT.player.inventoryMenu && !InventoryUtil.isBusy()) {
            int value9 = this.value5;
            int value10 = this.value6;
            Item item3 = this.item;
            this.resetSlots();
            if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(value9).getItem() == item3) {
               int value11 = MinecraftAccess.MINECRAFT.player.containerMenu.containerId;
               InventoryUtil.m_0(() -> InventoryUtil.clickSlot(value11, value10, value9, ContainerInput.SWAP));
            }
         }
      }
   }

   private void m_65_void() {
      int value9 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      if (this.value == -1 || value9 != this.value2) {
         this.value = value9;
      }
   }

   private boolean isSlotChanged() {
      return this.value2 != -1 && MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != this.value2;
   }

   private boolean isMining() {
      return MinecraftAccess.MINECRAFT.gameMode.isDestroying() || MinecraftAccess.MINECRAFT.options.keyAttack.isDown();
   }

   private BlockPos m_7_BlockPos() {
      NoClip noClip = Onetap.MODULE_MANAGER.get(NoClip.class);
      if (noClip != null && noClip.m_0_boolean()) {
         return null;
      } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         return null;
      } else if (MinecraftAccess.MINECRAFT.hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
         return MinecraftAccess.MINECRAFT.level.getBlockState(blockHitResult.getBlockPos()).isAir() ? null : blockHitResult.getBlockPos();
      } else {
         return null;
      }
   }

   private void reset() {
      this.value = -1;
      this.value2 = -1;
      this.value4 = 0;
   }

   private void resetSlots() {
      this.value5 = -1;
      this.value6 = -1;
      this.item = null;
   }

   private void m_7_void() {
      this.value7 = -1;
      this.value8 = -1;
      this.item2 = null;
   }

   private static int findBestToolSlot(BlockState blockState) {
      int value9 = -1;
      ItemStack itemStack = ItemStack.EMPTY;
      float floatValue = getToolSpeed(ItemStack.EMPTY, blockState);

      for (int i = 0; i < 9; i++) {
         ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         float floatValue2 = getToolSpeed(itemStack2, blockState);
         if (m_4(itemStack2, itemStack, floatValue2, floatValue, blockState)) {
            floatValue = floatValue2;
            value9 = i;
            itemStack = itemStack2;
         }
      }

      return value9;
   }

   private static ItemStack m_3_ItemStack(BlockState blockState) {
      int value9 = findBestToolSlot(blockState);
      return value9 == -1 ? MinecraftAccess.MINECRAFT.player.getMainHandItem() : MinecraftAccess.MINECRAFT.player.getInventory().getItem(value9);
   }

   private static int m_2(BlockState blockState, ItemStack itemStack) {
      int value9 = -1;
      float floatValue = getToolSpeed(itemStack, blockState);

      for (int i = 9; i < 36; i++) {
         ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         float floatValue2 = getToolSpeed(itemStack2, blockState);
         if (m_4(itemStack2, itemStack, floatValue2, floatValue, blockState)) {
            floatValue = floatValue2;
            value9 = i;
            itemStack = itemStack2;
         }
      }

      return value9;
   }

   private static boolean m_4(ItemStack itemStack, ItemStack itemStack2, float floatValue, float floatValue2, BlockState blockState) {
      boolean flag2 = isShearsForCobweb(itemStack, blockState);
      boolean flag3 = isShearsForCobweb(itemStack2, blockState);
      if (flag2 != flag3) {
         return flag2;
      }

      if (floatValue > floatValue2 + 1.0E-4F) {
         return true;
      }

      if (floatValue + 1.0E-4F < floatValue2) {
         return false;
      }

      boolean flag4 = itemStack.isCorrectToolForDrops(blockState);
      boolean flag5 = !itemStack2.isEmpty() && itemStack2.isCorrectToolForDrops(blockState);
      if (flag4 != flag5) {
         return flag4;
      }

      boolean flag6 = m_4_boolean(itemStack);
      boolean flag7 = m_4_boolean(itemStack2);
      return flag6 != flag7 && !flag6;
   }

   private static boolean isShearsForCobweb(ItemStack itemStack, BlockState blockState) {
      return blockState.is(Blocks.COBWEB) && itemStack.is(Items.SHEARS);
   }

   private static boolean m_4_boolean(ItemStack itemStack) {
      return itemStack.is(Items.SHEARS) && getToolScore(itemStack) <= 0.0F;
   }

   private static float getToolSpeed(ItemStack itemStack, BlockState blockState) {
      float floatValue = itemStack.isEmpty() ? 1.0F : itemStack.getDestroySpeed(blockState);
      if (floatValue > 1.0F) {
         floatValue += getToolScore(itemStack);
      }

      boolean flag2 = !blockState.requiresCorrectToolForDrops() || itemStack.isCorrectToolForDrops(blockState);
      return floatValue / (flag2 ? 30 : 100);
   }

   private static float getToolScore(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return 0.0F;
      }

      ItemEnchantments itemEnchantments = itemStack.getEnchantments();
      float floatValue = 0.0F;

      for (Holder holder : itemEnchantments.keySet()) {
         for (EnchantmentAttributeEffect enchantmentAttributeEffect : ((Enchantment)holder.value()).getEffects(EnchantmentEffectComponents.ATTRIBUTES)) {
            if (Attributes.MINING_EFFICIENCY.unwrapKey().filter(enchantmentAttributeEffect.attribute()::is).isPresent()) {
               floatValue += enchantmentAttributeEffect.amount().calculate(itemEnchantments.getLevel(holder));
            }
         }
      }

      return floatValue;
   }

   private static int m_0_int(int value9) {
      for (int i = 0; i < 9; i++) {
         if (i != value9 && i != MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() && MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   private static int m_8_int(int value9) {
      for (int i = 0; i < 9; i++) {
         if (i != value9 && i != MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() && !m_1_boolean(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
            return i;
         }
      }

      return -1;
   }

   private static boolean m_1_boolean(ItemStack itemStack) {
      Item item3 = itemStack.getItem();
      return item3 == Items.NETHERITE_SWORD
         || item3 == Items.DIAMOND_SWORD
         || item3 == Items.IRON_SWORD
         || item3 instanceof AxeItem
         || item3 == Items.MACE
         || item3 == Items.ELYTRA
         || item3 == Items.TOTEM_OF_UNDYING
         || item3 == Items.END_CRYSTAL
         || item3 == Items.OBSIDIAN
         || item3 == Items.CRYING_OBSIDIAN;
   }

   public int getSlot(Inventory inventory) {
      return this.isSilentActive() && inventory == MinecraftAccess.MINECRAFT.player.getInventory() ? this.value : inventory.getSelectedSlot();
   }

   public ItemStack m_1(LivingEntity livingEntity, HumanoidArm humanoidArm, ItemStack itemStack) {
      return this.isSilentActive() && livingEntity == MinecraftAccess.MINECRAFT.player && humanoidArm == livingEntity.getMainArm() ? MinecraftAccess.MINECRAFT.player.getInventory().getItem(this.value) : itemStack;
   }

   private boolean isSilentActive() {
      return this.m_4_boolean() && this.value != -1 && MinecraftAccess.MINECRAFT.player != null;
   }

   public boolean m_4_boolean() {
      return ((String)this.mode.getValue()).equals("Скрытый");
   }

   @Override
   public void onDisable() {
      this.m_6m_void();
   }

   public void m_6m_void() {
      this.m_4_void();
      this.restoreSlot();
      this.reset();
      this.m_7_void();
      this.resetSlots();
   }

   public static void m_9_void(boolean flag2) {
      flag = flag2;
   }

   public static boolean m_7_boolean() {
      return flag;
   }

   public static boolean m_1_boolean() {
      return true;
   }

   static {
      m_9_void(false);
   }
}
