package wtf.onetap.module.combat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoCart", description = "Ставит тнт-вагонетку по стреле", category = Category.COMBAT)
public class AutoCart extends Module {
   private final KeybindSetting shootButton = new KeybindSetting("Кнопка выстрела", -1).build();
   private final Set<Integer> integerSet = new HashSet<>();
   private boolean flag;
   private boolean flag2;
   private int value;
   private int value2 = -1;

   @Override
   public void onEnable() {
      this.integerSet.clear();
      this.reset();
      this.m_1_void();
   }

   @Override
   public void onDisable() {
      this.integerSet.clear();
      if (MinecraftAccess.MINECRAFT.options != null) {
         MinecraftAccess.MINECRAFT.options.keyUse.setDown(false);
      }

      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player.isUsingItem()) {
         MinecraftAccess.MINECRAFT.gameMode.releaseUsingItem(MinecraftAccess.MINECRAFT.player);
      }

      this.reset();
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
            if (event.getAction() == 1) {
               if (event.getKey() == (Integer)this.shootButton.getValue()) {
                  this.flag = true;
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         this.m_2_void();
         HashSet set = new HashSet();

         for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
            if (entity instanceof AbstractArrow abstractArrow && !(entity instanceof ThrownTrident) && abstractArrow.getOwner() == MinecraftAccess.MINECRAFT.player) {
               int value3 = entity.getId();
               set.add(value3);
               if (!this.integerSet.contains(value3) && this.hasRequiredItems() && this.isMoving(abstractArrow)) {
                  AutoCart.DataRecord c6 = this.predictArrowLanding(abstractArrow);
                  if (c6 != null) {
                     double doubleValue = 23.04;
                     if (MinecraftAccess.MINECRAFT.player.distanceToSqr(Vec3.atCenterOf(c6.m_8_i_BlockPos())) > doubleValue) {
                        this.integerSet.add(value3);
                     } else if (this.m_8_boolean(c6.m_8_i_BlockPos()) || abstractArrow.onGround()) {
                        this.integerSet.add(value3);
                     }
                  }
               }
            }
         }

         this.integerSet.retainAll(set);
      }
   }

   private void m_2_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.options != null) {
         if (this.flag2) {
            if (!MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.BOW)) {
               this.m_1(false);
            } else {
               if (MinecraftAccess.MINECRAFT.player.tickCount - this.value >= 12) {
                  this.m_1(true);
               }
            }
         } else if (this.flag) {
            this.flag = false;
            int value3 = InventoryUtil.findHotbarSlot(Items.BOW);
            if (value3 != -1) {
               this.value2 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
               if (this.value2 != value3) {
                  InventoryUtil.selectHotbarSlot(value3);
               }

               MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
               MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
               this.flag2 = true;
               this.value = MinecraftAccess.MINECRAFT.player.tickCount;
            }
         }
      }
   }

   private void m_1(boolean flag3) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.options != null) {
         MinecraftAccess.MINECRAFT.options.keyUse.setDown(false);
         if (MinecraftAccess.MINECRAFT.player.isUsingItem()) {
            MinecraftAccess.MINECRAFT.gameMode.releaseUsingItem(MinecraftAccess.MINECRAFT.player);
         }

         if (this.value2 >= 0 && this.value2 <= 8 && MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != this.value2) {
            InventoryUtil.selectHotbarSlot(this.value2);
         }

         this.reset();
      } else {
         this.reset();
      }
   }

   private void reset() {
      this.flag = false;
      this.flag2 = false;
      this.value = 0;
      this.value2 = -1;
   }

   private void m_1_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
            if (entity instanceof AbstractArrow abstractArrow && !(entity instanceof ThrownTrident) && abstractArrow.getOwner() == MinecraftAccess.MINECRAFT.player) {
               this.integerSet.add(entity.getId());
            }
         }
      }
   }

   private boolean hasRequiredItems() {
      return InventoryUtil.findHotbarSlot(Items.RAIL) != -1 && InventoryUtil.findHotbarSlot(Items.TNT_MINECART) != -1;
   }

   private boolean isMoving(AbstractArrow abstractArrow) {
      return !abstractArrow.onGround() && abstractArrow.getDeltaMovement().lengthSqr() > 1.0E-4;
   }

   private AutoCart.DataRecord predictArrowLanding(AbstractArrow abstractArrow) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return null;
      }

      Vec3 vec3 = abstractArrow.position();
      Vec3 vec32 = abstractArrow.getDeltaMovement();
      double doubleValue = abstractArrow.getGravity();

      for (int i = 1; i <= 80; i++) {
         Vec3 vec33 = vec3;
         vec3 = vec3.add(vec32);
         vec32 = this.m_5(abstractArrow, vec33, vec32, doubleValue);
         BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec33, vec3, Block.COLLIDER, Fluid.NONE, abstractArrow));
         if (blockHitResult.getType() != Type.MISS) {
            BlockPos blockPos = this.getRailPos(blockHitResult);
            if (blockPos != null) {
               return new AutoCart.DataRecord(blockPos, i);
            }

            return null;
         }

         if (vec3.y < MinecraftAccess.MINECRAFT.level.getMinY() - 2) {
            break;
         }
      }

      return null;
   }

   private Vec3 m_5(AbstractArrow abstractArrow, Vec3 vec3, Vec3 vec32, double doubleValue) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return vec32;
      }

      boolean flag3 = MinecraftAccess.MINECRAFT.level.getBlockState(BlockPos.containing(vec3)).getFluidState().is(FluidTags.WATER);
      double doubleValue2 = flag3 ? 0.6 : 0.99;
      return vec32.multiply(doubleValue2, doubleValue2, doubleValue2).add(0.0, -doubleValue, 0.0);
   }

   private BlockPos getRailPos(BlockHitResult blockHitResult) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return null;
      }

      BlockPos blockPos = blockHitResult.getBlockPos();
      if (this.isRail(blockPos)) {
         return blockPos;
      }

      BlockPos blockPos2 = blockHitResult.getDirection() == Direction.UP ? blockPos.above() : blockPos;
      if (this.canPlaceRail(blockPos2)) {
         return blockPos2;
      }

      BlockPos blockPos3 = blockPos.above();
      if (this.canPlaceRail(blockPos3)) {
         return blockPos3;
      }

      BlockPos blockPos4 = blockPos.below();
      return this.canPlaceRail(blockPos4) ? blockPos4 : null;
   }

   private boolean m_8_boolean(BlockPos blockPos) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         int value3 = InventoryUtil.findHotbarSlot(Items.RAIL);
         int value4 = InventoryUtil.findHotbarSlot(Items.TNT_MINECART);
         if (value3 != -1 && value4 != -1) {
            int value5 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
            boolean flag3 = false;

            try {
               if (!this.isRail(blockPos) && !this.m_2_boolean(blockPos, value3)) {
                  return false;
               }

               if (this.m_2_boolean(blockPos)) {
                  return true;
               }

               if (this.m_9(blockPos, value4)) {
                  flag3 = true;
               }
            } finally {
               InventoryUtil.selectHotbarSlot(value5);
            }

            return flag3;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean isRail(BlockPos blockPos) {
      return MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.level.getBlockState(blockPos).is(BlockTags.RAILS);
   }

   private boolean canPlaceRail(BlockPos blockPos) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return false;
      }

      if (this.isRail(blockPos)) {
         return true;
      }

      if (!MinecraftAccess.MINECRAFT.level.getBlockState(blockPos).canBeReplaced()) {
         return false;
      }

      BlockPos blockPos2 = blockPos.below();
      return MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2).isFaceSturdy(MinecraftAccess.MINECRAFT.level, blockPos2, Direction.UP, SupportType.FULL);
   }

   private boolean m_2_boolean(BlockPos blockPos, int value3) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         InventoryUtil.selectHotbarSlot(value3);
         BlockPos blockPos2 = blockPos.below();
         BlockHitResult blockHitResult = new BlockHitResult(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5), Direction.UP, blockPos2, false);
         InteractionResult interactionResult = MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
         if (interactionResult.consumesAction()) {
            MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
            return true;
         } else {
            return this.isRail(blockPos);
         }
      } else {
         return false;
      }
   }

   private boolean m_9(BlockPos blockPos, int value3) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         InventoryUtil.selectHotbarSlot(value3);
         BlockHitResult blockHitResult = new BlockHitResult(new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.15, blockPos.getZ() + 0.5), Direction.UP, blockPos, false);
         InteractionResult interactionResult = MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
         if (interactionResult.consumesAction()) {
            MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean m_2_boolean(BlockPos blockPos) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return false;
      } else {
         AABB aABB = new AABB(blockPos).inflate(0.2, 1.0, 0.2);
         var iterator = MinecraftAccess.MINECRAFT.level.getEntities((Entity)null, aABB, entity -> entity instanceof MinecartTNT).iterator();
         if (iterator.hasNext()) {
            iterator.next();
            return true;
         } else {
            return false;
         }
      }
   }

   private static String b(byte[] byteArray) {
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

   private record DataRecord(BlockPos f_8_i, int f_9h) {
      public BlockPos m_8_i_BlockPos() {
         return this.f_8_i;
      }

      public int m_9h_int() {
         return this.f_9h;
      }
   }
}
