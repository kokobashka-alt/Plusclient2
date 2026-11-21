package wtf.onetap.module.pve;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.mixin.input.KeyMappingAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AutoAppleFarm", description = "Автоматически выращивает дерево и ломает его", category = Category.PVE)
public class AutoAppleFarm extends Module {
   private final ModeSetting growing = new ModeSetting("Выращивание", "Костная мука", "Костная мука", "Мотыга").build();
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private AutoAppleFarm.ModeKind c4 = AutoAppleFarm.ModeKind.PLACE;
   private int value;
   private BlockPos blockPos3;
   private boolean flag;
   private AutoAppleFarm.ModeKind3 c9 = AutoAppleFarm.ModeKind3.OPEN;
   private BlockPos blockPos4;
   private AutoAppleFarm.ModeKind2 c7;
   private BlockPos blockPos5;
   private BlockPos blockPos6;
   private BlockPos blockPos7;
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private final TimeHelper timeHelper3 = new TimeHelper();
   private Item item;
   private AutoAppleFarm.ModeKind c42;
   private int value2;
   private final TimeHelper timeHelper4 = new TimeHelper();
   private AutoAppleFarm.ModeKind c43;
   private boolean flag2;
   private final TimeHelper timeHelper5 = new TimeHelper();
   private final TimeHelper timeHelper6 = new TimeHelper();
   private final TimeHelper timeHelper7 = new TimeHelper();

   @Override
   public void onEnable() {
      this.blockPos = null;
      this.blockPos2 = null;
      this.c4 = AutoAppleFarm.ModeKind.PLACE;
      this.value = 0;
      this.reset();
      this.m_69_void();
      this.timeHelper5.reset();
      this.timeHelper6.reset();
      this.timeHelper3.reset();
      this.timeHelper4.reset();
      this.item = null;
      this.showInstructions();
   }

   @Override
   public void onDisable() {
      this.stopMining();
      this.releaseUseKey();
      Onetap.ROTATION_MODES.reset();
      this.blockPos = null;
      this.blockPos2 = null;
      this.c4 = AutoAppleFarm.ModeKind.PLACE;
      this.value = 0;
      this.reset();
      this.m_69_void();
      this.item = null;
   }

   private void reset() {
      this.blockPos3 = null;
      this.flag = false;
   }

   private void m_69_void() {
      this.c9 = AutoAppleFarm.ModeKind3.OPEN;
      this.blockPos4 = null;
      this.c7 = null;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.blockPos == null) {
            if (!this.m_1_boolean()) {
               ChatUtil.log("§fAutoAppleFarm §7» §7Не найден блок земли. Встаньте рядом и посмотрите на землю.");
               this.setEnabled(false);
               return;
            }

            this.blockPos2 = this.blockPos.above();
         }

         switch (this.c4.ordinal()) {
            case 4:
               this.m_3_void();
               return;
            case 5:
               this.m_3(AutoAppleFarm.ModeKind2.SKLAD);
               return;
            case 6:
               this.m_3(AutoAppleFarm.ModeKind2.RESY);
               return;
            case 7:
               this.m_6_n_void();
               return;
            case 8:
               this.m_3(AutoAppleFarm.ModeKind2.EXP);
               return;
            case 9:
               this.m_2_void();
               return;
            default:
               if (MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() <= 6 && this.findFoodSlot() != -1) {
                  this.m_65_void();
               } else if (this.c4 != AutoAppleFarm.ModeKind.PLACE
                  || this.c9 != AutoAppleFarm.ModeKind3.OPEN
                  || !this.hasEnoughResources() && (!this.needsBoneMeal() || !this.timeHelper3.hasElapsed(20000L))) {
                  if (this.c4 == AutoAppleFarm.ModeKind.LEAVES && this.m_7_boolean(this.getBestHoe())) {
                     this.m_8(AutoAppleFarm.ModeKind.LEAVES);
                  } else if (this.c4 == AutoAppleFarm.ModeKind.LOGS && this.m_7_boolean(this.getBestAxe())) {
                     this.m_8(AutoAppleFarm.ModeKind.LOGS);
                  } else {
                     switch (this.c4.ordinal()) {
                        case 0:
                           this.m_6u_void();
                           break;
                        case 1:
                           this.m_7_void();
                           break;
                        case 2:
                           this.m_3(BlockTags.LEAVES, AutoAppleFarm.ModeKind.LOGS);
                           break;
                        case 3:
                           this.m_3(BlockTags.LOGS, AutoAppleFarm.ModeKind.DROP_STICKS);
                     }
                  }
               } else {
                  if (this.hasEnoughResources()) {
                     this.c4 = AutoAppleFarm.ModeKind.DEPOSIT;
                  } else {
                     this.c4 = AutoAppleFarm.ModeKind.RESTOCK;
                  }

                  this.m_6h_void();
               }
         }
      }
   }

   private boolean m_1_boolean() {
      BlockPos blockPos8 = null;
      double doubleValue = Double.MAX_VALUE;
      if (MinecraftAccess.MINECRAFT.hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != Type.MISS) {
         BlockPos blockPos9 = blockHitResult.getBlockPos();
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos9);
         if (isDirt(blockState) && MinecraftAccess.MINECRAFT.level.getBlockState(blockPos9.above()).canBeReplaced()) {
            blockPos8 = blockPos9.immutable();
            doubleValue = this.distanceTo(blockPos9);
         }
      }

      BlockPos blockPos10 = MinecraftAccess.MINECRAFT.player.blockPosition();

      for (int i = -3; i <= 3; i++) {
         for (int i2 = -1; i2 <= 1; i2++) {
            for (int i3 = -3; i3 <= 3; i3++) {
               BlockPos blockPos11 = blockPos10.offset(i, i2, i3);
               BlockState blockState2 = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos11);
               if (isDirt(blockState2) && MinecraftAccess.MINECRAFT.level.getBlockState(blockPos11.above()).canBeReplaced()) {
                  double doubleValue2 = this.distanceTo(blockPos11);
                  if (!(doubleValue2 > 5.5) && doubleValue2 < doubleValue) {
                     doubleValue = doubleValue2;
                     blockPos8 = blockPos11.immutable();
                  }
               }
            }
         }
      }

      if (blockPos8 == null) {
         return false;
      }

      this.blockPos = blockPos8;
      this.blockPos2 = blockPos8.above();
      return true;
   }

   private static boolean isDirt(BlockState blockState) {
      return blockState.is(BlockTags.DIRT);
   }

   private void m_6u_void() {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos2);
      if (!blockState.canBeReplaced()) {
         if (blockState.is(BlockTags.LOGS)) {
            this.c4 = AutoAppleFarm.ModeKind.LEAVES;
         } else if (blockState.getBlock() instanceof SaplingBlock) {
            this.c4 = AutoAppleFarm.ModeKind.GROW;
            this.value = 0;
         } else {
            this.c4 = AutoAppleFarm.ModeKind.LEAVES;
         }
      } else if (this.equipItem(Items.OAK_SAPLING)) {
         this.lookAt(this.blockPos);
         if (this.timeHelper5.hasElapsed(50L)) {
            BlockHitResult blockHitResult = this.m_1(this.getYaw(), this.getPitch(), 5.5);
            if (blockHitResult == null || !blockHitResult.getBlockPos().equals(this.blockPos)) {
               blockHitResult = new BlockHitResult(new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY() + 1.0, this.blockPos.getZ() + 0.5), Direction.UP, this.blockPos, false);
            }

            MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
            this.timeHelper5.reset();
            this.c4 = AutoAppleFarm.ModeKind.GROW;
            this.value = 0;
         }
      }
   }

   private void m_7_void() {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos2);
      if (blockState.is(BlockTags.LOGS)) {
         this.c4 = AutoAppleFarm.ModeKind.LEAVES;
         this.value = 0;
      } else if (blockState.canBeReplaced()) {
         this.c4 = AutoAppleFarm.ModeKind.PLACE;
         this.value = 0;
      } else if (!(blockState.getBlock() instanceof SaplingBlock)) {
         this.c4 = AutoAppleFarm.ModeKind.PLACE;
         this.value = 0;
      } else {
         this.value++;
         if (this.value > 80) {
            this.c4 = AutoAppleFarm.ModeKind.LEAVES;
            this.value = 0;
         } else {
            Item item2 = ((String)this.growing.getValue()).equals("Мотыга") ? this.getBestHoe() : Items.BONE_MEAL;
            if (this.equipItem(item2)) {
               this.lookAt(this.blockPos2);
               if (this.timeHelper5.hasElapsed(50L)) {
                  BlockHitResult blockHitResult = this.m_1(this.getYaw(), this.getPitch(), 5.5);
                  if (blockHitResult == null || !blockHitResult.getBlockPos().equals(this.blockPos2)) {
                     blockHitResult = new BlockHitResult(Vec3.atCenterOf(this.blockPos2), Direction.UP, this.blockPos2, false);
                  }

                  MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
                  this.timeHelper5.reset();
               }
            }
         }
      }
   }

   private void m_3(TagKey<Block> tagKey, AutoAppleFarm.ModeKind c44) {
      if (this.blockPos3 != null) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos3);
         if (blockState.isAir() || !blockState.is(tagKey)) {
            this.stopMining();
            this.reset();
         }
      }

      if (this.blockPos3 == null) {
         this.blockPos3 = this.m_3(tagKey);
         this.flag = false;
         if (this.blockPos3 == null) {
            this.c4 = c44;
            return;
         }
      }

      Item item2 = tagKey.equals(BlockTags.LEAVES) ? this.getBestHoe() : this.getBestAxe();
      if (this.equipItem(item2)) {
         this.lookAt(this.blockPos3);
         BlockHitResult blockHitResult = this.m_2(this.m_1(this.getYaw(), this.getPitch(), 5.5), this.blockPos3);
         MinecraftAccess.MINECRAFT.hitResult = blockHitResult;
         MinecraftAccessor minecraftAccessor = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
         if (!this.flag) {
            if (!this.timeHelper5.hasElapsed(50L)) {
               return;
            }

            MinecraftAccess.MINECRAFT.options.keyAttack.setDown(true);
            minecraftAccessor.onetap$startAttack();
            this.flag = true;
            this.timeHelper5.reset();
         } else {
            minecraftAccessor.onetap$continueAttack(true);
         }
      }
   }

   private BlockPos m_3(TagKey<Block> tagKey) {
      ArrayList<BlockPos> blockPosList = new ArrayList<>();
      BlockPos blockPos8 = this.blockPos2;
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);

      for (int i = -3; i <= 3; i++) {
         for (int i2 = 0; i2 <= 7; i2++) {
            for (int i3 = -3; i3 <= 3; i3++) {
               BlockPos blockPos9 = blockPos8.offset(i, i2, i3);
               BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos9);
               if (!blockState.isAir() && blockState.is(tagKey) && !blockPos9.equals(this.blockPos)) {
                  Vec3 vec32 = Vec3.atCenterOf(blockPos9);
                  if (!(vec32.distanceTo(vec3) > 5.5)) {
                     blockPosList.add(blockPos9.immutable());
                  }
               }
            }
         }
      }

      if (blockPosList.isEmpty()) {
         return null;
      }

      blockPosList.sort(Comparator.comparingDouble(blockPos10 -> Vec3.atCenterOf(blockPos10).distanceToSqr(vec3)));
      return (BlockPos)blockPosList.get(0);
   }

   private void m_3_void() {
      if (this.timeHelper7.hasElapsed(100L)) {
         if (this.countItemTotal(Items.STICK) >= 64) {
            int value3 = InventoryUtil.findItemSlot(Items.STICK);
            if (value3 != -1) {
               int value4 = value3 < 9 ? value3 + 36 : value3;
               InventoryUtil.clickSlot(0, value4, 1, ContainerInput.THROW);
               this.timeHelper7.reset();
               return;
            }
         }

         int value5 = this.countItem(Items.OAK_SAPLING);
         if (value5 > 1) {
            int value6 = this.m_8_int(Items.OAK_SAPLING);
            if (value6 != -1) {
               int value7 = value6 < 9 ? value6 + 36 : value6;
               InventoryUtil.clickSlot(0, value7, 1, ContainerInput.THROW);
               this.timeHelper7.reset();
               return;
            }
         }

         this.c4 = AutoAppleFarm.ModeKind.PLACE;
      }
   }

   private void m_8(AutoAppleFarm.ModeKind c44) {
      this.stopMining();
      this.reset();
      this.item = c44 == AutoAppleFarm.ModeKind.LEAVES ? this.getBestHoe() : this.getBestAxe();
      this.c42 = c44;
      this.value2 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      this.timeHelper4.reset();
      this.c4 = AutoAppleFarm.ModeKind.REPAIR;
   }

   private void m_6_n_void() {
      if (this.item == null) {
         this.c4 = this.c42;
      } else {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getOffhandItem();
         if (!itemStack.is(this.item)) {
            int value3 = InventoryUtil.findHotbarSlot(this.item);
            if (value3 != -1) {
               MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value3);
               MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value3));
               this.value2 = value3;
            }

            InventoryUtil.clickSlot(0, this.value2 + 36, 40, ContainerInput.SWAP);
            this.timeHelper4.reset();
         } else if (itemStack.getMaxDamage() - itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(this.value2);
            MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(this.value2));
            InventoryUtil.clickSlot(0, 45, this.value2, ContainerInput.SWAP);
            this.item = null;
            this.c4 = this.c42;
            this.timeHelper5.reset();
         } else {
            Rotations rotations = Onetap.ROTATION_MODES.toRotations();
            Onetap.ROTATION_MODES.rotate(new Rotations(rotations.getFloatValue(), Float.intBitsToFloat((int)-5840010371490643968L)), false, 10);
            if (this.countItemTotal(Items.EXPERIENCE_BOTTLE) <= 0) {
               this.c4 = AutoAppleFarm.ModeKind.GET_EXP;
               this.m_6h_void();
            } else if (!MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.EXPERIENCE_BOTTLE)) {
               if (this.equipItem(Items.EXPERIENCE_BOTTLE)) {
                  this.timeHelper4.reset();
               }
            } else {
               if (this.timeHelper4.hasElapsed(50L)) {
                  MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
                  this.timeHelper4.reset();
               }
            }
         }
      }
   }

   private boolean m_7_boolean(Item item2) {
      if (item2 == null) {
         return false;
      }

      int value3 = InventoryUtil.findItemSlot(item2);
      if (value3 == -1) {
         return false;
      }

      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value3);
      return itemStack.isDamageableItem() && itemStack.getMaxDamage() - itemStack.getDamageValue() < 500;
   }

   private void m_65_void() {
      this.stopMining();
      this.reset();
      this.c43 = this.c4;
      this.c4 = AutoAppleFarm.ModeKind.EAT;
   }

   private void m_2_void() {
      int value3 = this.findFoodSlot();
      if (MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() < 20 && value3 != -1) {
         Item item2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value3).getItem();
         if (!MinecraftAccess.MINECRAFT.player.getMainHandItem().is(item2)) {
            this.releaseUseKey();
            InventoryUtil.clickSlot(0, value3 < 9 ? value3 + 36 : value3, MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot(), ContainerInput.SWAP);
         } else {
            if (!MinecraftAccess.MINECRAFT.player.isUsingItem()) {
               Key key = ((KeyMappingAccessor)MinecraftAccess.MINECRAFT.options.keyUse).onetap$getKey();
               KeyMapping.click(key);
            }

            MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
            this.flag2 = true;
         }
      } else {
         this.releaseUseKey();
         this.c4 = this.c43;
         this.timeHelper5.reset();
      }
   }

   private int findFoodSlot() {
      int value3 = -1;
      int value4 = -1;
      int value5 = -1;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
            if (foodProperties != null) {
               Item item2 = itemStack.getItem();
               if (item2 != Items.GOLDEN_APPLE && item2 != Items.ENCHANTED_GOLDEN_APPLE) {
                  if (item2 == Items.APPLE) {
                     if (value5 == -1) {
                        value5 = i;
                     }
                  } else if (foodProperties.nutrition() > value4) {
                     value4 = foodProperties.nutrition();
                     value3 = i;
                  }
               }
            }
         }
      }

      return value3 != -1 ? value3 : value5;
   }

   private void releaseUseKey() {
      if (this.flag2) {
         this.flag2 = false;
         if (MinecraftAccess.MINECRAFT.options != null) {
            MinecraftAccess.MINECRAFT.options.keyUse.setDown(false);
         }
      }
   }

   private void m_6h_void() {
      this.reset();
      this.m_69_void();
      if (this.c4 == AutoAppleFarm.ModeKind.RESTOCK) {
         this.c7 = AutoAppleFarm.ModeKind2.RESY;
      } else if (this.c4 == AutoAppleFarm.ModeKind.DEPOSIT) {
         this.c7 = AutoAppleFarm.ModeKind2.SKLAD;
      } else if (this.c4 == AutoAppleFarm.ModeKind.GET_EXP) {
         this.c7 = AutoAppleFarm.ModeKind2.EXP;
      }

      this.timeHelper.reset();
      this.timeHelper2.reset();
   }

   private void m_3(AutoAppleFarm.ModeKind2 c72) {
      AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
      switch (this.c9.ordinal()) {
         case 0:
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.closeScreens();
               this.timeHelper.reset();
               return;
            }

            BlockPos blockPos8 = this.getModePos(c72);
            if (blockPos8 == null) {
               blockPos8 = this.m_3_BlockPos(getModeKeyword(c72));
               if (blockPos8 == null) {
                  this.m_7_void(true);
                  return;
               }

               this.m_3(c72, blockPos8);
            }

            if (!(MinecraftAccess.MINECRAFT.level.getBlockState(blockPos8).getBlock() instanceof ChestBlock)) {
               this.m_3(c72, null);
               this.m_7_void(true);
               return;
            }

            this.blockPos4 = blockPos8;
            this.c7 = c72;
            if (!this.timeHelper.hasElapsed(150L)) {
               return;
            }

            this.lookAt(blockPos8);
            BlockHitResult blockHitResult = this.m_1(this.getYaw(), this.getPitch(), 5.0);
            if (blockHitResult == null || !blockHitResult.getBlockPos().equals(blockPos8)) {
               blockHitResult = new BlockHitResult(Vec3.atCenterOf(blockPos8), Direction.UP, blockPos8, false);
            }

            MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
            this.timeHelper.reset();
            this.c9 = AutoAppleFarm.ModeKind3.WAIT_OPEN;
            break;
         case 1:
            if (abstractContainerMenu != null) {
               this.c9 = AutoAppleFarm.ModeKind3.ACTION;
               this.timeHelper.reset();
               this.timeHelper2.reset();
               return;
            }

            if (this.timeHelper.hasElapsed(2000L)) {
               this.c9 = AutoAppleFarm.ModeKind3.OPEN;
               this.timeHelper.reset();
            }
            break;
         case 2:
            if (abstractContainerMenu == null) {
               this.c9 = AutoAppleFarm.ModeKind3.OPEN;
               this.timeHelper.reset();
               return;
            }

            if (this.timeHelper2.hasElapsed(10000L)) {
               this.closeScreens();
               this.c9 = AutoAppleFarm.ModeKind3.CLOSE;
               this.timeHelper.reset();
               return;
            }

            if (!this.timeHelper.hasElapsed(80L)) {
               return;
            }
            boolean flag3 = switch (c72.ordinal()) {
               case 0 -> this.m_8_boolean(abstractContainerMenu);
               case 1 -> this.m_1_boolean(abstractContainerMenu);
               case 2 -> this.m_9(abstractContainerMenu);
               default -> throw new MatchException(null, null);
            };
            if (flag3) {
               this.closeScreens();
               this.c9 = AutoAppleFarm.ModeKind3.CLOSE;
               this.timeHelper.reset();
            } else {
               this.timeHelper.reset();
            }
            break;
         case 3:
            if (MinecraftAccess.MINECRAFT.gui.screen() == null) {
               this.m_7_void(false);
            } else if (this.timeHelper.hasElapsed(1000L)) {
               this.closeScreens();
            }
      }
   }

   private void m_7_void(boolean flag3) {
      AutoAppleFarm.ModeKind2 c72 = this.c7;
      this.c9 = AutoAppleFarm.ModeKind3.OPEN;
      this.blockPos4 = null;
      if (flag3) {
         ChatUtil.log("§fAutoAppleFarm §7» §7Сундук «" + getModeName(c72) + "» не найден.");
      }

      if (c72 == AutoAppleFarm.ModeKind2.EXP) {
         this.c4 = AutoAppleFarm.ModeKind.REPAIR;
         this.timeHelper4.reset();
      } else if (c72 == AutoAppleFarm.ModeKind2.RESY) {
         this.c4 = AutoAppleFarm.ModeKind.PLACE;
         this.timeHelper3.reset();
      } else {
         this.c4 = AutoAppleFarm.ModeKind.PLACE;
      }

      this.timeHelper5.reset();
   }

   private boolean m_8_boolean(AbstractContainerMenu abstractContainerMenu) {
      if (this.countItemTotal(Items.BONE_MEAL) < 320) {
         int value3 = this.m_2(abstractContainerMenu, Items.BONE_MEAL);
         if (value3 != -1) {
            InventoryUtil.clickSlot(abstractContainerMenu.containerId, value3, 0, ContainerInput.QUICK_MOVE);
            return false;
         }
      }

      return true;
   }

   private boolean m_1_boolean(AbstractContainerMenu abstractContainerMenu) {
      int value3 = this.m_3(abstractContainerMenu, Items.APPLE);
      if (value3 != -1) {
         InventoryUtil.clickSlot(abstractContainerMenu.containerId, value3, 0, ContainerInput.QUICK_MOVE);
         return false;
      } else {
         value3 = this.m_3(abstractContainerMenu, Items.OAK_LOG);
         if (value3 != -1) {
            InventoryUtil.clickSlot(abstractContainerMenu.containerId, value3, 0, ContainerInput.QUICK_MOVE);
            return false;
         } else {
            return true;
         }
      }
   }

   private boolean m_9(AbstractContainerMenu abstractContainerMenu) {
      if (this.countItemTotal(Items.EXPERIENCE_BOTTLE) >= 192) {
         return true;
      }

      int value3 = this.m_2(abstractContainerMenu, Items.EXPERIENCE_BOTTLE);
      if (value3 == -1) {
         return true;
      }

      InventoryUtil.clickSlot(abstractContainerMenu.containerId, value3, 0, ContainerInput.QUICK_MOVE);
      return false;
   }

   private boolean needsBoneMeal() {
      return ((String)this.growing.getValue()).equals("Костная мука") && this.countItemTotal(Items.BONE_MEAL) < 32;
   }

   private void showInstructions() {
      ChatUtil.log("§fИнструкция:");
      ChatUtil.log("§71. Перед включением выберите режим «Мотыга» или «Костная мука».");
      ChatUtil.log("§7   В режиме «Мотыга» нужна мотыга с зачарованием «Озеленитель». Костная мука и сундук «Ресы» не нужны.");
      ChatUtil.log("§7   В режиме «Костная мука» поставьте в радиусе 3 блоков сундук с табличкой «Ресы» и положите туда только костную муку.");
      ChatUtil.log("§72. Поставьте сундук «Склад» для ресурсов с фермы и сундук «Опыт» для опыта. Всего нужно 3 сундука: «Склад», «Ресы» и «Опыт».");
      ChatUtil.log("§7   Сундук «Ресы» не нужен только в режиме «Мотыга».");
      ChatUtil.log("§7После выполнения инструкции включите модуль, находясь рядом с фермой.");
   }

   private boolean hasEnoughResources() {
      return this.countItemTotal(Items.APPLE) >= 32 || this.countItem(Items.OAK_LOG) > 4;
   }

   private BlockPos getModePos(AutoAppleFarm.ModeKind2 c72) {
      return switch (c72.ordinal()) {
         case 0 -> this.blockPos5;
         case 1 -> this.blockPos6;
         case 2 -> this.blockPos7;
         default -> throw new MatchException(null, null);
      };
   }

   private void m_3(AutoAppleFarm.ModeKind2 c72, BlockPos blockPos8) {
      switch (c72.ordinal()) {
         case 0:
            this.blockPos5 = blockPos8;
            break;
         case 1:
            this.blockPos6 = blockPos8;
            break;
         case 2:
            this.blockPos7 = blockPos8;
      }
   }

   private static String getModeKeyword(AutoAppleFarm.ModeKind2 c72) {
      return switch (c72.ordinal()) {
         case 0 -> "ресы";
         case 1 -> "склад";
         case 2 -> "опыт";
         default -> throw new MatchException(null, null);
      };
   }

   private static String getModeName(AutoAppleFarm.ModeKind2 c72) {
      return switch (c72.ordinal()) {
         case 0 -> "Ресы";
         case 1 -> "Склад";
         case 2 -> "Опыт";
         default -> throw new MatchException(null, null);
      };
   }

   private BlockPos m_3_BlockPos(String text) {
      BlockPos blockPos8 = MinecraftAccess.MINECRAFT.player.blockPosition();
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
      BlockPos blockPos9 = null;
      double doubleValue = Double.MAX_VALUE;

      for (int i = -4; i <= 4; i++) {
         for (int i2 = -2; i2 <= 2; i2++) {
            for (int i3 = -4; i3 <= 4; i3++) {
               BlockPos blockPos10 = blockPos8.offset(i, i2, i3);
               if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos10).getBlock() instanceof ChestBlock
                  && !(Vec3.atCenterOf(blockPos10).distanceTo(vec3) > 5.0)
                  && this.m_3(blockPos10, text)) {
                  double doubleValue2 = Vec3.atCenterOf(blockPos10).distanceToSqr(vec3);
                  if (doubleValue2 < doubleValue) {
                     doubleValue = doubleValue2;
                     blockPos9 = blockPos10.immutable();
                  }
               }
            }
         }
      }

      return blockPos9;
   }

   private boolean m_3(BlockPos blockPos8, String text) {
      BlockPos[] blockPosArray = new BlockPos[]{
         blockPos8.above(),
         blockPos8.north(),
         blockPos8.south(),
         blockPos8.east(),
         blockPos8.west(),
         blockPos8.above().north(),
         blockPos8.above().south(),
         blockPos8.above().east(),
         blockPos8.above().west()
      };

      for (BlockPos blockPos9 : blockPosArray) {
         if (MinecraftAccess.MINECRAFT.level.getBlockEntity(blockPos9) instanceof SignBlockEntity signBlockEntity) {
            if (this.signContains(signBlockEntity.getFrontText(), text)) {
               return true;
            }

            if (this.signContains(signBlockEntity.getBackText(), text)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean signContains(SignText signText, String text) {
      if (signText == null) {
         return false;
      }

      for (Component component : signText.getMessages(false)) {
         if (component != null && normalize(component.getString()).contains(text)) {
            return true;
         }
      }

      return false;
   }

   private static String normalize(String text) {
      return text == null ? "" : text.replaceAll("§.", "").replace('ё', 'е').trim().toLowerCase(Locale.ROOT);
   }

   private int countItemTotal(Item item2) {
      int value3 = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.is(item2)) {
            value3 += itemStack.getCount();
         }
      }

      return value3;
   }

   private int countItem(Item item2) {
      int value3 = 0;

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).is(item2)) {
            value3++;
         }
      }

      return value3;
   }

   private int m_8_int(Item item2) {
      int value3 = -1;

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).is(item2)) {
            if (value3 != -1) {
               return i;
            }

            value3 = i;
         }
      }

      return -1;
   }

   private int m_2(AbstractContainerMenu abstractContainerMenu, Item item2) {
      int value3 = abstractContainerMenu.slots.size() - 36;

      for (int i = 0; i < value3; i++) {
         if (abstractContainerMenu.getSlot(i).getItem().is(item2)) {
            return i;
         }
      }

      return -1;
   }

   private int m_3(AbstractContainerMenu abstractContainerMenu, Item item2) {
      int value3 = abstractContainerMenu.slots.size();

      for (int i = Math.max(0, value3 - 36); i < value3; i++) {
         if (abstractContainerMenu.getSlot(i).getItem().is(item2)) {
            return i;
         }
      }

      return -1;
   }

   private static int m_4(int value3, int value4) {
      return value3 < 9 ? value4 + 27 + value3 : value4 + (value3 - 9);
   }

   private AbstractContainerMenu getOpenContainer() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return null;
      } else {
         AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
         if (abstractContainerMenu == null || abstractContainerMenu.containerId == 0) {
            return null;
         } else if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
            return null;
         } else {
            return abstractContainerMenu.slots.size() <= 36 ? null : abstractContainerMenu;
         }
      }
   }

   private void closeScreens() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.containerMenu != null && MinecraftAccess.MINECRAFT.player.containerMenu.containerId != 0) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.gui.screen().onClose();
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }
   }

   private boolean equipItem(Item item2) {
      if (item2 == null) {
         return false;
      }

      if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(item2)) {
         return true;
      }

      if (!this.timeHelper6.hasElapsed(150L)) {
         return false;
      }

      int value3 = InventoryUtil.findHotbarSlot(item2);
      if (value3 != -1) {
         MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value3);
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value3));
         this.timeHelper6.reset();
         return false;
      }

      int value4 = InventoryUtil.findItemSlotInRange(item2, 9, 36);
      if (value4 == -1) {
         return false;
      }

      int value5 = InventoryUtil.findEmptyHotbarSlot();
      if (value5 == -1) {
         return false;
      }

      InventoryUtil.m_6(() -> InventoryUtil.clickSlot(0, value4, value5, ContainerInput.SWAP));
      MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value5);
      MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value5));
      this.timeHelper6.reset();
      return false;
   }

   private void lookAt(BlockPos blockPos8) {
      Rotations rotations = Onetap.ROTATION_MODES.getRotationTo(blockPos8);
      Onetap.ROTATION_MODES.rotate(rotations, false, 10);
   }

   private float getYaw() {
      return Onetap.ROTATION_MODES.toRotations().getFloatValue();
   }

   private float getPitch() {
      return Onetap.ROTATION_MODES.toRotations().getFloatValue2();
   }

   private BlockHitResult m_2(BlockHitResult blockHitResult, BlockPos blockPos8) {
      if (blockHitResult != null && blockHitResult.getBlockPos().equals(blockPos8)) {
         return blockHitResult;
      }

      BlockHitResult blockHitResult2 = this.m_1(this.getYaw(), this.getPitch(), 5.5);
      if (blockHitResult2 != null && blockHitResult2.getBlockPos().equals(blockPos8)) {
         return blockHitResult2;
      }

      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
      Vec3 vec32 = Vec3.atCenterOf(blockPos8);
      Direction direction = Direction.getApproximateNearest(vec32.x - vec3.x, vec32.y - vec3.y, vec32.z - vec3.z);
      Direction direction2 = direction != null ? direction.getOpposite() : Direction.UP;
      return new BlockHitResult(vec32, direction2, blockPos8, false);
   }

   private BlockHitResult m_1(float floatValue, float floatValue2, double doubleValue) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
         Vec3 vec32 = this.getLookVector(floatValue, floatValue2);
         Vec3 vec33 = vec3.add(vec32.x * doubleValue, vec32.y * doubleValue, vec32.z * doubleValue);
         return MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec3, vec33, net.minecraft.world.level.ClipContext.Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player)) instanceof BlockHitResult blockHitResult
               && blockHitResult.getType() != Type.MISS
            ? blockHitResult
            : null;
      } else {
         return null;
      }
   }

   private Vec3 getLookVector(float floatValue, float floatValue2) {
      double doubleValue = Math.toRadians(floatValue);
      double doubleValue2 = Math.toRadians(floatValue2);
      return new Vec3(-Math.sin(doubleValue) * Math.cos(doubleValue2), -Math.sin(doubleValue2), Math.cos(doubleValue) * Math.cos(doubleValue2));
   }

   private double distanceTo(BlockPos blockPos8) {
      return Vec3.atCenterOf(blockPos8).distanceTo(MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F));
   }

   private void stopMining() {
      if (this.flag) {
         MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
         if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
            MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
         }

         MinecraftAccessor minecraftAccessor = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
         minecraftAccessor.onetap$continueAttack(false);
      }

      this.flag = false;
   }

   private Item getBestHoe() {
      if (InventoryUtil.findItemSlot(Items.NETHERITE_HOE) != -1) {
         return Items.NETHERITE_HOE;
      } else if (InventoryUtil.findItemSlot(Items.DIAMOND_HOE) != -1) {
         return Items.DIAMOND_HOE;
      } else if (InventoryUtil.findItemSlot(Items.IRON_HOE) != -1) {
         return Items.IRON_HOE;
      } else if (InventoryUtil.findItemSlot(Items.STONE_HOE) != -1) {
         return Items.STONE_HOE;
      } else {
         return InventoryUtil.findItemSlot(Items.WOODEN_HOE) != -1 ? Items.WOODEN_HOE : null;
      }
   }

   private Item getBestAxe() {
      if (InventoryUtil.findItemSlot(Items.NETHERITE_AXE) != -1) {
         return Items.NETHERITE_AXE;
      } else if (InventoryUtil.findItemSlot(Items.DIAMOND_AXE) != -1) {
         return Items.DIAMOND_AXE;
      } else if (InventoryUtil.findItemSlot(Items.IRON_AXE) != -1) {
         return Items.IRON_AXE;
      } else if (InventoryUtil.findItemSlot(Items.STONE_AXE) != -1) {
         return Items.STONE_AXE;
      } else {
         return InventoryUtil.findItemSlot(Items.WOODEN_AXE) != -1 ? Items.WOODEN_AXE : null;
      }
   }

   private enum ModeKind {
      PLACE,
      GROW,
      LEAVES,
      LOGS,
      DROP_STICKS,
      DEPOSIT,
      RESTOCK,
      REPAIR,
      GET_EXP,
      EAT;

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }

   private enum ModeKind2 {
      RESY,
      SKLAD,
      EXP;

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }

   private enum ModeKind3 {
      OPEN,
      WAIT_OPEN,
      ACTION,
      CLOSE;

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }
}
