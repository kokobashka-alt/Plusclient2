package wtf.onetap.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.BlockPlacedEvent;
import wtf.onetap.event.EntitySpawnEvent;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.RemoveEntitiesEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.event.UseItemEvent;
import wtf.onetap.event.UseItemOnEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "AutoExplosion", description = "Автоматически ставит и взрывает кристаллы", category = Category.COMBAT)
public class AutoExplosion extends Module {
   private final BooleanSetting anySlot = new BooleanSetting("Независимо от слота", false).build();
   private final BooleanSetting dontBlowSelf = new BooleanSetting("Не взрывать себя", false).build();
   private final BooleanSetting dontBlowFriends = new BooleanSetting("Не взрывать друзей", false).build();
   private final BooleanSetting dontBlowResources = new BooleanSetting("Не бахать ресы", false).build();
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private AutoExplosion.DataRecord c6;
   private int value = -1;
   private int value2;
   private boolean flag;
   private boolean flag2;
   private int value3;
   private boolean flag3;

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public AutoExplosion.DataRecord getC6() {
      return this.c6;
   }

   @Override
   public void onDisable() {
      this.blockPos = null;
      this.blockPos2 = null;
      this.c6 = null;
      this.value = -1;
      this.value2 = 0;
      this.flag = false;
      this.flag2 = false;
      this.value3 = 0;
      this.flag3 = false;
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.flag3) {
         float floatValue = event.getForward();
         float floatValue2 = event.getStrafe();
         if (floatValue != 0.0F || floatValue2 != 0.0F) {
            float floatValue3 = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().yRot();
            if (MinecraftAccess.MINECRAFT.options.getCameraType().isMirrored()) {
               floatValue3 -= 180.0F;
            }

            float floatValue4 = floatValue3 - MinecraftAccess.MINECRAFT.player.getYRot();
            float floatValue5 = (float)Math.toDegrees(Math.atan2(floatValue, floatValue2));
            float floatValue6 = Mth.wrapDegrees(floatValue5 + floatValue4);
            float floatValue7 = Math.round(floatValue6 / 45.0F) * 45.0F;
            double doubleValue = Math.toRadians(floatValue7);
            event.setForward(Math.round((float)Math.sin(doubleValue)));
            event.setStrafe(Math.round((float)Math.cos(doubleValue)));
         }
      }
   }

   @EventTarget
   private void onUseItemOn(UseItemOnEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.value2 <= 0 && this.c6 == null) {
            boolean flag4 = this.m_4_boolean(event.getBlockHitResult().getBlockPos());
            int value4 = InventoryUtil.findItemSlotInRange(Items.END_CRYSTAL, 0, 9);
            int value5 = InventoryUtil.findItemSlotInRange(Items.OBSIDIAN, 0, 9);
            if (MinecraftAccess.MINECRAFT.level.getBlockState(event.getBlockHitResult().getBlockPos()).getBlock() == Blocks.OBSIDIAN) {
               if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.END_CRYSTAL)) {
                  if (!flag4) {
                     this.blockPos2 = event.getBlockHitResult().getBlockPos().above();
                  }
               } else if ((Boolean)this.anySlot.getValue() && value4 != -1) {
                  BlockPos blockPos3 = event.getBlockHitResult().getBlockPos();
                  BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3);
                  InteractionResult interactionResult = blockState.useWithoutItem(MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, event.getBlockHitResult());
                  if (!interactionResult.consumesAction() && !blockState.hasBlockEntity() && !canUseItem(MinecraftAccess.MINECRAFT.player.getMainHandItem()) && !flag4) {
                     BlockPos blockPos4 = event.getBlockHitResult().getBlockPos().above();
                     if (PlayerUtil.predictLocal(3)
                           .vec3
                           .add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight(), 0.0)
                           .distanceTo(
                              closestPointOnBox(
                                 new AABB(
                                    blockPos4.getX() - 0.5F, blockPos4.getY(), blockPos4.getZ() - 0.5F, blockPos4.getX() + 1.5F, blockPos4.above().getY(), blockPos4.getZ() + 1.5F
                                 ),
                                 0.0
                              )
                           )
                        <= 3.0) {
                        if (this.value == -1) {
                           this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
                        }

                        MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value4);
                        this.blockPos2 = event.getBlockHitResult().getBlockPos().above();
                     }
                  }
               }
            } else if ((Boolean)this.anySlot.getValue() && value4 != -1 && value5 != -1) {
               BlockPos blockPos5 = event.getBlockHitResult().getBlockPos();
               BlockState blockState2 = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos5);
               InteractionResult interactionResult2 = blockState2.useWithoutItem(MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, event.getBlockHitResult());
               if (!interactionResult2.consumesAction() && !blockState2.hasBlockEntity() && !canUseItem(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
                  if (this.value == -1) {
                     this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
                  }

                  MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value5);
               }
            }
         } else {
            event.cancel();
         }
      }
   }

   @EventTarget
   private void onUseItem(UseItemEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.value2 > 0 || this.c6 != null) {
            event.cancel();
         }
      }
   }

   @EventTarget
   private void onBlockPlaced(BlockPlacedEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (!this.m_4_boolean(event.getBlockPos())) {
            int value4 = InventoryUtil.findItemSlotInRange(Items.END_CRYSTAL, 0, 9);
            if (event.getItemStack().is(Items.OBSIDIAN) && value4 != -1) {
               BlockPos blockPos3 = event.getBlockPos();
               if (PlayerUtil.predictLocal(3)
                     .vec3
                     .add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight(), 0.0)
                     .distanceTo(
                        closestPointOnBox(
                           new AABB(blockPos3.getX() - 0.5F, blockPos3.above().getY(), blockPos3.getZ() - 0.5F, blockPos3.getX() + 1.5F, blockPos3.above(2).getY(), blockPos3.getZ() + 1.5F),
                           0.0
                        )
                     )
                  <= 3.0) {
                  if (this.value == -1) {
                     this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
                  }

                  MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value4);
                  this.blockPos = event.getBlockPos();
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         this.flag3 = false;
         if (this.value2 > 0) {
            this.value2--;
         }

         if (this.blockPos != null) {
            Rotations rotations = this.getRotationWithJitter(Vec3.atCenterOf(this.blockPos));
            Onetap.ROTATION_MODES.rotateWithSensitivity(rotations, false, 30);
            this.flag = true;
            this.flag3 = true;
         }

         if (this.c6 != null) {
            Rotations rotations2 = this.getRotationWithJitter(findBestPoint(this.c6.m_0h(), 0.0));
            Onetap.ROTATION_MODES.rotateWithSensitivity(rotations2, false, 31);
            this.flag2 = true;
            this.flag3 = true;
         }

         if (this.flag2 && this.c6 != null) {
            double doubleValue = Math.abs(MinecraftAccess.MINECRAFT.player.getX() - this.c6.m_0h().getX());
            double doubleValue2 = Math.abs(MinecraftAccess.MINECRAFT.player.getY() - this.c6.m_0h().getY());
            double doubleValue3 = Math.abs(MinecraftAccess.MINECRAFT.player.getZ() - this.c6.m_0h().getZ());
            Rotations rotations3 = this.getRotationWithJitter(findBestPoint(this.c6.m_0h(), 0.0));
            Onetap.ROTATION_MODES.rotateWithSensitivity(rotations3, false, 31);
            this.flag3 = true;
            if (this.m_0_boolean(this.c6.m_0h().blockPosition())) {
               this.flag2 = false;
               this.blockPos2 = null;
               this.c6 = null;
               this.value3 = 0;
               return;
            }

            if (!(Boolean)this.dontBlowSelf.getValue()
               || !(doubleValue < 6.0)
               || !(doubleValue2 < 6.0)
               || !(doubleValue3 < 6.0)
               || !(MinecraftAccess.MINECRAFT.player.getY() > this.c6.m_0h().getY() - 0.25)) {
               if (this.shouldBreak(this.c6.m_0h())) {
                  this.value3++;
                  return;
               }

               this.attackCrystal(this.c6.m_0h());
            }

            this.flag2 = false;
            this.blockPos2 = null;
            this.c6 = null;
            this.value3 = 0;
         }

         if (this.flag && this.blockPos != null) {
            Rotations rotations4 = this.getRotationWithJitter(Vec3.atCenterOf(this.blockPos));
            Onetap.ROTATION_MODES.rotateWithSensitivity(rotations4, false, 30);
            this.flag3 = true;
            if (m_4(this.blockPos, rotations4, 4.5)) {
               MinecraftAccess.MINECRAFT
                  .gameMode
                  .useItemOn(
                     MinecraftAccess.MINECRAFT.player,
                     InteractionHand.MAIN_HAND,
                     new BlockHitResult(Vec3.atCenterOf(this.blockPos), m_6(rotations4, MinecraftAccess.MINECRAFT.player, this.blockPos), this.blockPos, false)
                  );
               this.value2 = 1;
            }

            this.blockPos2 = this.blockPos.above();
            this.blockPos = null;
            this.flag = false;
         }

         if (this.value2 == 0 && this.value != -1) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(this.value);
            this.value = -1;
         }
      }
   }

   @EventTarget
   private void onEntitySpawn(EntitySpawnEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (event.getEntity() instanceof EndCrystal endCrystal && this.blockPos2 != null) {
            int value4 = Math.abs(event.getEntity().getBlockX() - this.blockPos2.getX());
            int value5 = Math.abs(event.getEntity().getBlockY() - this.blockPos2.getY());
            int value6 = Math.abs(event.getEntity().getBlockZ() - this.blockPos2.getZ());
            if (value4 <= 2 && value5 <= 2 && value6 <= 2) {
               if (MinecraftAccess.MINECRAFT.player.getEyePosition().distanceTo(findBestPoint(endCrystal, 0.0)) <= 3.0) {
                  this.c6 = new AutoExplosion.DataRecord(endCrystal);
               }

               this.value3 = 0;
            }
         }
      }
   }

   @EventTarget
   private void onRemoveEntities(RemoveEntitiesEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null) {
         ((MinecraftAccessor)MinecraftAccess.MINECRAFT).setRightClickDelay(0);
      }
   }

   private void attackCrystal(EndCrystal endCrystal) {
      MinecraftAccess.MINECRAFT.gameMode.attack(MinecraftAccess.MINECRAFT.player, endCrystal);
      MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
      this.value2 = 1;
   }

   private boolean shouldBreak(EndCrystal endCrystal) {
      Player player = this.m_6(endCrystal);
      return player != null && player.hurtTime > 3 && this.value3 < 4;
   }

   private boolean m_4_boolean(BlockPos blockPos3) {
      if (this.m_0_boolean(blockPos3)) {
         return true;
      }

      boolean flag4 = false;

      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         double doubleValue = Math.abs(player.getBlockX() - blockPos3.getX());
         double doubleValue2 = Math.abs(player.getBlockZ() - blockPos3.getZ());
         if (isFriend(player) && player != MinecraftAccess.MINECRAFT.player && blockPos3.getY() < player.getY() && doubleValue < 6.0 && doubleValue2 < 6.0) {
            flag4 = true;
         }
      }

      PlayerUtil playerUtil = PlayerUtil.predictLocal(3);

      for (int i = 0; i < 5; i++) {
         if (blockPos3.equals(MinecraftAccess.MINECRAFT.player.blockPosition().offset(0, -i, 0))
            || !MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3.above()).isAir()
            || (Boolean)this.dontBlowSelf.getValue()
               && (blockPos3.getY() < MinecraftAccess.MINECRAFT.player.getBlockY() || blockPos3.getY() < BlockPos.containing(playerUtil.vec3).getY() || blockPos3.getY() < MinecraftAccess.MINECRAFT.player.getBlockY())
            || (Boolean)this.dontBlowFriends.getValue() && flag4) {
            return true;
         }
      }

      return false;
   }

   private boolean m_0_boolean(BlockPos blockPos3) {
      if (!(Boolean)this.dontBlowResources.getValue()) {
         return false;
      }

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof ItemEntity itemEntity) {
            double doubleValue = Math.abs(itemEntity.getX() - blockPos3.getX());
            double doubleValue2 = Math.abs(itemEntity.getY() - blockPos3.getY());
            double doubleValue3 = Math.abs(itemEntity.getZ() - blockPos3.getZ());
            if (doubleValue < 4.0 && doubleValue2 < 2.0 && doubleValue3 < 4.0 && isProtectedItem(itemEntity.getItem())) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean isProtectedItem(ItemStack itemStack) {
      Item item = itemStack.getItem();
      return item == Items.TOTEM_OF_UNDYING
         || item == Items.ELYTRA
         || item == Items.DIAMOND
         || item == Items.DIAMOND_BLOCK
         || item == Items.DIAMOND_ORE
         || item == Items.DEEPSLATE_DIAMOND_ORE
         || item == Items.NETHERITE_INGOT
         || item == Items.NETHERITE_SCRAP
         || item == Items.ANCIENT_DEBRIS
         || item == Items.NETHERITE_BLOCK
         || item == Items.DIAMOND_SWORD
         || item == Items.DIAMOND_PICKAXE
         || item == Items.DIAMOND_AXE
         || item == Items.DIAMOND_SHOVEL
         || item == Items.DIAMOND_HOE
         || item == Items.DIAMOND_HELMET
         || item == Items.DIAMOND_CHESTPLATE
         || item == Items.DIAMOND_LEGGINGS
         || item == Items.DIAMOND_BOOTS
         || item == Items.NETHERITE_SWORD
         || item == Items.NETHERITE_PICKAXE
         || item == Items.NETHERITE_AXE
         || item == Items.NETHERITE_SHOVEL
         || item == Items.NETHERITE_HOE
         || item == Items.NETHERITE_HELMET
         || item == Items.NETHERITE_CHESTPLATE
         || item == Items.NETHERITE_LEGGINGS
         || item == Items.NETHERITE_BOOTS;
   }

   private Player m_6(EndCrystal endCrystal) {
      Player player = null;
      float floatValue = 0.0F;

      for (Player player2 : MinecraftAccess.MINECRAFT.level.players()) {
         if (player2 != MinecraftAccess.MINECRAFT.player && player2.isAlive() && !player2.isSpectator() && !isFriend(player2)) {
            float floatValue2 = this.calcDamage(endCrystal.position(), player2);
            if (floatValue2 > floatValue) {
               floatValue = floatValue2;
               player = player2;
            }
         }
      }

      return player;
   }

   private static boolean isFriend(Player player) {
      return Onetap.FRIEND_MANAGER.isFriend(player.getName().getString());
   }

   private Rotations getRotationWithJitter(Vec3 vec3) {
      Rotations rotations = new Rotations(MinecraftAccess.MINECRAFT.player.getEyePosition(), vec3);
      rotations.setFloatValue(rotations.getFloatValue() + randomBetween(-2.0F, 2.0F));
      rotations.m_2_void(rotations.getFloatValue2() + randomBetween(-2.0F, 2.0F));
      return rotations;
   }

   private static float randomBetween(float floatValue, float floatValue2) {
      return floatValue >= floatValue2 ? ThreadLocalRandom.current().nextFloat(floatValue2 - 1.0E-6F, floatValue) : ThreadLocalRandom.current().nextFloat(floatValue, floatValue2);
   }

   private static Vec3 findBestPoint(Entity entity, double doubleValue) {
      AABB aABB = entity.getBoundingBox();
      double doubleValue2 = 0.1;
      Vec3 vec3 = null;
      double doubleValue3 = Double.MAX_VALUE;

      for (double doubleValue4 = aABB.minX + doubleValue; doubleValue4 <= aABB.maxX - doubleValue; doubleValue4 += doubleValue2) {
         for (double doubleValue5 = aABB.minY + doubleValue; doubleValue5 <= aABB.maxY - doubleValue; doubleValue5 += doubleValue2) {
            for (double doubleValue6 = aABB.minZ + doubleValue; doubleValue6 <= aABB.maxZ - doubleValue; doubleValue6 += doubleValue2) {
               Vec3 vec32 = new Vec3(doubleValue4, doubleValue5, doubleValue6);
               double doubleValue7 = MinecraftAccess.MINECRAFT.player.getEyePosition().distanceTo(vec32);
               if (doubleValue7 < doubleValue3) {
                  doubleValue3 = doubleValue7;
                  vec3 = vec32;
               }
            }
         }
      }

      return vec3 != null ? vec3 : entity.position();
   }

   private static Vec3 closestPointOnBox(AABB aABB, double doubleValue) {
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      double doubleValue2 = Math.max(aABB.minX + doubleValue, Math.min(vec3.x, aABB.maxX - doubleValue));
      double doubleValue3 = Math.max(aABB.minY + doubleValue, Math.min(vec3.y, aABB.maxY - doubleValue));
      double doubleValue4 = Math.max(aABB.minZ + doubleValue, Math.min(vec3.z, aABB.maxZ - doubleValue));
      return new Vec3(doubleValue2, doubleValue3, doubleValue4);
   }

   private static boolean m_4(BlockPos blockPos3, Rotations rotations, double doubleValue) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return false;
      }

      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      Vec3 vec32 = rotations.toVector();
      Vec3 vec33 = vec3.add(vec32.scale(doubleValue));
      AABB aABB = new AABB(blockPos3);
      return aABB.contains(vec3) || aABB.clip(vec3, vec33).isPresent();
   }

   private static Direction m_6(Rotations rotations, Player player, BlockPos blockPos3) {
      Vec3 vec3 = player.getEyePosition();
      Vec3 vec32 = Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue());
      double doubleValue = blockPos3.getX() + 0.5;
      double doubleValue2 = blockPos3.getY() + 0.5;
      double doubleValue3 = blockPos3.getZ() + 0.5;
      double doubleValue4 = blockPos3.getX();
      double doubleValue5 = blockPos3.getY();
      double doubleValue6 = blockPos3.getZ();
      double doubleValue7 = blockPos3.getX() + 1.0;
      double doubleValue8 = blockPos3.getY() + 1.0;
      double doubleValue9 = blockPos3.getZ() + 1.0;
      Direction direction = null;
      double doubleValue10 = Double.MAX_VALUE;
      double doubleValue11 = m_7(vec3, vec32, doubleValue8, doubleValue4, doubleValue7, doubleValue6, doubleValue9);
      if (doubleValue11 > 0.0 && doubleValue11 < doubleValue10) {
         doubleValue10 = doubleValue11;
         direction = Direction.UP;
      }

      doubleValue11 = m_7(vec3, vec32, doubleValue5, doubleValue4, doubleValue7, doubleValue6, doubleValue9);
      if (doubleValue11 > 0.0 && doubleValue11 < doubleValue10) {
         doubleValue10 = doubleValue11;
         direction = Direction.DOWN;
      }

      doubleValue11 = m_1(vec3, vec32, doubleValue6, doubleValue4, doubleValue7, doubleValue5, doubleValue8);
      if (doubleValue11 > 0.0 && doubleValue11 < doubleValue10) {
         doubleValue10 = doubleValue11;
         direction = Direction.NORTH;
      }

      doubleValue11 = m_1(vec3, vec32, doubleValue9, doubleValue4, doubleValue7, doubleValue5, doubleValue8);
      if (doubleValue11 > 0.0 && doubleValue11 < doubleValue10) {
         doubleValue10 = doubleValue11;
         direction = Direction.SOUTH;
      }

      doubleValue11 = m_2(vec3, vec32, doubleValue4, doubleValue5, doubleValue8, doubleValue6, doubleValue9);
      if (doubleValue11 > 0.0 && doubleValue11 < doubleValue10) {
         doubleValue10 = doubleValue11;
         direction = Direction.WEST;
      }

      doubleValue11 = m_2(vec3, vec32, doubleValue7, doubleValue5, doubleValue8, doubleValue6, doubleValue9);
      if (doubleValue11 > 0.0 && doubleValue11 < doubleValue10) {
         direction = Direction.EAST;
      }

      if (direction == null) {
         double doubleValue12 = vec3.x - doubleValue;
         double doubleValue13 = vec3.y - doubleValue2;
         double doubleValue14 = vec3.z - doubleValue3;
         double doubleValue15 = Math.abs(doubleValue12);
         double doubleValue16 = Math.abs(doubleValue13);
         double doubleValue17 = Math.abs(doubleValue14);
         if (doubleValue16 >= doubleValue15 && doubleValue16 >= doubleValue17) {
            direction = doubleValue13 > 0.0 ? Direction.UP : Direction.DOWN;
         } else if (doubleValue15 >= doubleValue17) {
            direction = doubleValue12 > 0.0 ? Direction.EAST : Direction.WEST;
         } else {
            direction = doubleValue14 > 0.0 ? Direction.SOUTH : Direction.NORTH;
         }
      }

      return direction;
   }

   private static double m_7(Vec3 vec3, Vec3 vec32, double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5) {
      if (vec32.y == 0.0) {
         return -1.0;
      }

      double doubleValue6 = (doubleValue - vec3.y) / vec32.y;
      if (doubleValue6 <= 0.0) {
         return -1.0;
      }

      double doubleValue7 = vec3.x + doubleValue6 * vec32.x;
      double doubleValue8 = vec3.z + doubleValue6 * vec32.z;
      return doubleValue7 >= doubleValue2 && doubleValue7 <= doubleValue3 && doubleValue8 >= doubleValue4 && doubleValue8 <= doubleValue5 ? doubleValue6 : -1.0;
   }

   private static double m_1(Vec3 vec3, Vec3 vec32, double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5) {
      if (vec32.z == 0.0) {
         return -1.0;
      }

      double doubleValue6 = (doubleValue - vec3.z) / vec32.z;
      if (doubleValue6 <= 0.0) {
         return -1.0;
      }

      double doubleValue7 = vec3.x + doubleValue6 * vec32.x;
      double doubleValue8 = vec3.y + doubleValue6 * vec32.y;
      return doubleValue7 >= doubleValue2 && doubleValue7 <= doubleValue3 && doubleValue8 >= doubleValue4 && doubleValue8 <= doubleValue5 ? doubleValue6 : -1.0;
   }

   private static double m_2(Vec3 vec3, Vec3 vec32, double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5) {
      if (vec32.x == 0.0) {
         return -1.0;
      }

      double doubleValue6 = (doubleValue - vec3.x) / vec32.x;
      if (doubleValue6 <= 0.0) {
         return -1.0;
      }

      double doubleValue7 = vec3.y + doubleValue6 * vec32.y;
      double doubleValue8 = vec3.z + doubleValue6 * vec32.z;
      return doubleValue7 >= doubleValue2 && doubleValue7 <= doubleValue3 && doubleValue8 >= doubleValue4 && doubleValue8 <= doubleValue5 ? doubleValue6 : -1.0;
   }

   private static boolean canUseItem(ItemStack itemStack) {
      Item item = itemStack.getItem();
      InteractionResult interactionResult = item.use(MinecraftAccess.MINECRAFT.level, MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
      return item instanceof BlockItem
         || item instanceof PotionItem
         || item instanceof BucketItem
         || item instanceof SpawnEggItem
         || item instanceof EnderpearlItem
         || item instanceof SnowballItem
         || item instanceof EggItem
         || item instanceof FireworkRocketItem
         || item instanceof BoatItem
         || item instanceof MinecartItem
         || interactionResult.consumesAction();
   }

   private float calcDamage(Vec3 vec3, Player player) {
      if (MinecraftAccess.MINECRAFT.level.getDifficulty() != Difficulty.PEACEFUL && player != null) {
         if (!new AABB(
               Mth.floor(vec3.x - 11.0),
               Mth.floor(vec3.y - 11.0),
               Mth.floor(vec3.z - 11.0),
               Mth.floor(vec3.x + 13.0),
               Mth.floor(vec3.y + 13.0),
               Mth.floor(vec3.z + 13.0)
            )
            .intersects(player.getBoundingBox())) {
            return 0.0F;
         }

         if (player.isInvulnerable()) {
            return 0.0F;
         }

         double doubleValue = player.distanceToSqr(vec3) / 144.0;
         if (doubleValue > 1.0) {
            return 0.0F;
         }

         double doubleValue2 = m_5_float(vec3, player.getBoundingBox());
         double doubleValue3 = (1.0 - doubleValue) * doubleValue2;
         float floatValue = (float)Math.floor((doubleValue3 * doubleValue3 + doubleValue3) / 2.0 * 7.0 * 12.0 + 1.0);
         if (MinecraftAccess.MINECRAFT.level.getDifficulty() == Difficulty.EASY) {
            floatValue = Math.min(floatValue / 2.0F + 1.0F, floatValue);
         } else if (MinecraftAccess.MINECRAFT.level.getDifficulty() == Difficulty.HARD) {
            floatValue = floatValue * 3.0F / 2.0F;
         }

         DamageSource damageSource = MinecraftAccess.MINECRAFT.level.damageSources().explosion(null, null);
         float floatValue2 = player.getArmorValue();
         float floatValue3 = (float)player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
         floatValue = CombatRules.getDamageAfterAbsorb(player, floatValue, damageSource, floatValue2, floatValue3);
         if (player.hasEffect(MobEffects.RESISTANCE)) {
            int value4 = 25 - (player.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
            floatValue = Math.max(floatValue * value4 / 25.0F, 0.0F);
         }

         if (floatValue <= 0.0F) {
            return 0.0F;
         }

         float floatValue4 = this.getArmorValue(player);
         if (floatValue4 > 0.0F) {
            floatValue = CombatRules.getDamageAfterMagicAbsorb(floatValue, floatValue4);
         }

         return floatValue;
      } else {
         return 0.0F;
      }
   }

   private static float m_5_float(Vec3 vec3, AABB aABB) {
      int value4 = 0;
      int value5 = 0;

      for (int i = 0; i <= 1; i++) {
         for (int i2 = 0; i2 <= 1; i2++) {
            for (int i3 = 0; i3 <= 1; i3++) {
               double doubleValue = Mth.lerp(i, aABB.minX, aABB.maxX);
               double doubleValue2 = Mth.lerp(i2, aABB.minY, aABB.maxY);
               double doubleValue3 = Mth.lerp(i3, aABB.minZ, aABB.maxZ);
               Vec3 vec32 = new Vec3(doubleValue, doubleValue2, doubleValue3);
               if (MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec32, vec3, Block.COLLIDER, Fluid.NONE, MinecraftAccess.MINECRAFT.player)).getType() == Type.MISS) {
                  value4++;
               }

               value5++;
            }
         }
      }

      return (float)value4 / value5;
   }

   private float getArmorValue(Player player) {
      float floatValue = 0.0F;

      for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         ItemStack itemStack = player.getItemBySlot(equipmentSlot);
         floatValue += InventoryUtil.getEnchantmentLevel(itemStack, Enchantments.BLAST_PROTECTION) * 2 + InventoryUtil.getEnchantmentLevel(itemStack, Enchantments.PROTECTION);
      }

      return floatValue;
   }

   public record DataRecord(EndCrystal f_0h) {
      public EndCrystal m_0h() {
         return this.f_0h;
      }
   }
}
