package wtf.onetap.module.pve;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.BlockTypeUtil;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AutoMine", description = "Автоматически копает шахту на анархии SpookyTime", category = Category.PVE)
public class AutoMine extends Module {
   private final ModeSetting anarchyVersion = new ModeSetting("Версия анархии", "Обе", "1.16.5", "1.21", "Обе").build();
   private final TextSetting storageAnarchy = new TextSetting("Анархия склада", "601").build();
   private static final BlockPos BLOCK_POS = new BlockPos(-58, 72, 35);
   private static final BlockPos BLOCK_POS2 = new BlockPos(-58, 70, 35);
   private static final BlockPos BLOCK_POS3 = new BlockPos(-59, 69, 35);
   private static final int[] VALUE_ARRAY = new int[]{
      1001,
      1002,
      1003,
      1004,
      1005,
      1006,
      1007,
      1008,
      2001,
      2002,
      2003,
      2004,
      2005,
      2006,
      2007,
      2008,
      2009,
      2010,
      3001,
      3002,
      3003,
      3004,
      3005,
      4001,
      4002,
      5001,
      5002,
      5003,
      6001,
      6002
   };
   private static final int[] VALUE_ARRAY2 = new int[]{
      101, 102, 103, 104, 105, 106, 107, 108, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 301, 302, 303, 304, 305, 401, 402, 501, 502, 503, 601, 602, 603
   };
   private static final int[] VALUE_ARRAY3 = new int[VALUE_ARRAY.length + VALUE_ARRAY2.length];
   private static final Pattern PATTERN = Pattern.compile("анархия\\D*(\\d+)");
   private static final Pattern PATTERN2 = Pattern.compile("\\d{2}:\\d{2}");
   private AutoMine.ModeKind c0 = AutoMine.ModeKind.IDLE;
   private BlockPos blockPos = null;
   private BlockPos blockPos2 = null;
   private BlockPos blockPos3 = null;
   private BlockPos blockPos4 = null;
   private int value = 0;
   private int value2 = 0;
   private int value3 = 0;
   private BlockPos blockPos5 = null;
   private int value4 = 0;
   private Vec3 vec3 = Vec3.ZERO;
   private int value5 = 0;
   private int value6 = 0;
   private int value7 = Integer.MIN_VALUE;
   private int value8 = 0;
   private boolean flag = false;
   private boolean flag2 = false;
   private boolean flag3 = false;
   private boolean flag4 = false;
   private int value9 = 0;
   private boolean flag5 = false;
   private int value10 = 0;
   private int value11 = 0;
   private int value12 = -1;
   private boolean flag6 = false;
   private boolean flag7 = false;
   private float floatValue = 0.0F;
   private ItemStack itemStack = ItemStack.EMPTY;
   private boolean flag8 = false;
   private boolean flag9 = false;
   private AutoMine.ModeKind2 c3 = AutoMine.ModeKind2.DEPOSIT;
   private AutoMine.ModeKind3 c4 = AutoMine.ModeKind3.OPEN;
   private BlockPos blockPos6 = null;
   private int value13 = 0;
   private int value14 = 0;
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private int value15 = -1;
   private int value16 = 0;
   private final Map<Integer, Long> integerMap = new HashMap<>();
   private final Map<Integer, Long> integerMap2 = new HashMap<>();
   private int value17 = 0;
   private boolean flag10 = false;
   private long longValue = -1L;
   private Vec3 vec32 = null;
   private final Random random = new Random();
   private float floatValue2 = 45.0F;
   private float floatValue3 = 45.0F;
   private int value18 = 0;

   @Override
   public void onEnable() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         BlockTypeUtil.m_8_void(-73, -43, 20, 50);
         BlockTypeUtil.BLOCK_POS_SET.clear();
         this.value16 = 0;
         this.blockPos2 = null;
         this.value6 = 0;
         this.flag10 = false;
         this.longValue = -1L;
         this.flag = false;
         this.flag2 = false;
         this.flag3 = false;
         this.flag5 = false;
         Integer integer = this.readScoreboardValue();
         if (integer != null) {
            this.value15 = integer;
         }

         this.showInstructions();
         this.navigateToMine();
      }
   }

   private void showInstructions() {
      ChatUtil.log("§fИнструкция:");
      ChatUtil.log("§71. В настройках модуля укажите «Анархия склада» — номер анархии вашей базы/стеша.");
      ChatUtil.log(
         "§72. На базе поставьте сундук с табличкой «Склад» — туда будут складываться алмазы и золото. Рядом поставьте сундук с табличкой «Опыт» и сложите туда пузырьки опыта: чем больше опыта, тем дольше модуль работает в полном АФК."
      );
      ChatUtil.log("§73. Встаньте в радиусе 3 блоков от обоих сундуков. Кирка (алмазная или незеритовая) должна быть в хотбаре.");
      ChatUtil.log("§74. Отсортируйте инвентарь: одна строка алмазов по 1 шт. в слоте и около 10 слотов золота по 1 шт. — туда будет складываться добыча.");
      ChatUtil.log("§7После этого включите модуль, стоя возле сундуков, — дальше всё автоматически.");
   }

   @Override
   public void onDisable() {
      this.c0 = AutoMine.ModeKind.IDLE;
      this.releaseMovementKeys();
      MinecraftAccess.MINECRAFT.options.keyShift.setDown(false);
      this.stopDigging();
      Onetap.ROTATION_MODES.reset();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      this.m_6z_void();
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         Float floatObj = null;
         if (this.c0 == AutoMine.ModeKind.WALKING_TO_MINE && this.blockPos != null) {
            floatObj = Onetap.ROTATION_MODES.getRotationTo(this.blockPos).getFloatValue();
         } else if (this.c0 == AutoMine.ModeKind.WALKING_TO_DUMP) {
            floatObj = Onetap.ROTATION_MODES.getRotationTo(BLOCK_POS3).getFloatValue();
         } else if (this.c0 == AutoMine.ModeKind.CLEARING_LAYER && this.blockPos2 != null) {
            floatObj = Onetap.ROTATION_MODES.getRotationTo(this.blockPos2).getFloatValue();
         }

         if (floatObj != null) {
            float floatValue4 = event.getForward();
            float floatValue5 = event.getStrafe();
            if (floatValue4 != 0.0F || floatValue5 != 0.0F) {
               float floatValue6 = floatObj - MinecraftAccess.MINECRAFT.player.getYRot();
               float floatValue7 = (float)Math.toDegrees(Math.atan2(floatValue4, floatValue5));
               float floatValue8 = Mth.wrapDegrees(floatValue7 + floatValue6);
               float floatValue9 = Math.round(floatValue8 / 45.0F) * 45.0F;
               double doubleValue = Math.toRadians(floatValue9);
               event.setForward(Math.round((float)Math.sin(doubleValue)));
               event.setStrafe(Math.round((float)Math.cos(doubleValue)));
            }
         }
      }
   }

   private void navigateToMine() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (this.value15 != -1 && this.value15 == this.storageAnarchy.getInt()) {
            this.goToAnarchy(this.findAnarchyToJoin());
         } else {
            BlockPos blockPos7 = MinecraftAccess.MINECRAFT.player.blockPosition();
            if (this.isInMine(blockPos7)) {
               if (blockPos7.getY() <= 69) {
                  this.c0 = AutoMine.ModeKind.CLEARING_LAYER;
               } else {
                  this.releaseMovementKeys();
                  this.c0 = AutoMine.ModeKind.DIGGING_DOWN;
               }
            } else if (!blockPos7.closerThan(BLOCK_POS3, 5.0) && !this.isInMineArea(blockPos7)) {
               this.sendWarpMine();
            } else {
               this.m_4_void();
            }
         }
      }
   }

   private boolean isInMineArea(BlockPos blockPos7) {
      return blockPos7.getX() >= -83 && blockPos7.getX() <= -33 && blockPos7.getZ() >= 10 && blockPos7.getZ() <= 60;
   }

   private void m_6z_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (++this.value17 >= 2) {
            this.value17 = 0;
            this.m_63_void();
         }

         if ((this.c0 == AutoMine.ModeKind.DIGGING_DOWN || this.c0 == AutoMine.ModeKind.CLEARING_LAYER) && !this.flag5) {
            this.releaseMovementKeys();
            this.stopDigging();
            if (++this.value10 >= 1) {
               this.value10 = 0;
               if (this.m_7_boolean()) {
                  this.flag5 = true;
                  this.value11 = 0;
               } else if (++this.value11 >= 10) {
                  this.value11 = 0;
                  if (MinecraftAccess.MINECRAFT.player.getBlockY() > 69) {
                     this.flag5 = true;
                  } else {
                     this.walkTo(BLOCK_POS3, AutoMine.ModeKind.WALKING_TO_DUMP);
                  }
               }
            }

            if (!this.flag5) {
               return;
            }
         }

         if ((this.c0 == AutoMine.ModeKind.DIGGING_DOWN || this.c0 == AutoMine.ModeKind.CLEARING_LAYER) && ++this.value14 >= 5) {
            this.value14 = 0;
            if (this.countItemTotal(Items.DIAMOND) > 0) {
               if (this.m_9_boolean()) {
                  if (this.countItemTotal(Items.EXPERIENCE_BOTTLE) > 0) {
                     this.flag = true;
                     this.walkTo(BLOCK_POS3, AutoMine.ModeKind.WALKING_TO_DUMP);
                  } else {
                     this.m_3_void();
                  }

                  return;
               }

               if (!this.flag3 && this.countItemTotal(Items.DIAMOND) >= 320 && this.countGold() >= 320) {
                  this.flag3 = true;
               }
            }
         }

         if (this.m_6_boolean()) {
            this.releaseMovementKeys();
            this.stopDigging();
            this.value16 = 0;
            this.flag10 = false;
            this.longValue = this.integerMap.getOrDefault(this.value15, -1L);
            this.c0 = AutoMine.ModeKind.WAITING_RESET;
         }

         switch (this.c0.ordinal()) {
            case 1:
               this.releaseMovementKeys();
               this.value16++;
               this.m_6h_void();
               if (this.value16 >= 200) {
                  this.walkToDump();
                  return;
               }

               if (this.value16 >= 20 && this.value16 % 5 == 0 && MinecraftAccess.MINECRAFT.gui.screen() == null) {
                  Integer integer = this.readScoreboardValue();
                  if (integer != null) {
                     this.value15 = integer;
                  }

                  if (integer != null) {
                     if (this.flag2) {
                        this.flag2 = false;
                        this.flag = true;
                        this.walkTo(BLOCK_POS3, AutoMine.ModeKind.WALKING_TO_DUMP);
                        return;
                     }

                     if (this.isInMineArea(MinecraftAccess.MINECRAFT.player.blockPosition())) {
                        this.value16 = 0;
                        this.c0 = AutoMine.ModeKind.ANALYZING;
                     } else {
                        this.navigateToMine();
                     }
                  } else {
                     this.navigateToMine();
                  }
               }
               break;
            case 2:
               this.releaseMovementKeys();
               this.value16++;
               this.m_6h_void();
               BlockPos blockPos7 = MinecraftAccess.MINECRAFT.player.blockPosition();
               if (this.isInMine(blockPos7)) {
                  this.value16 = 0;
                  this.navigateToMine();
                  return;
               }

               boolean flag11 = this.vec32 != null && MinecraftAccess.MINECRAFT.player.position().distanceTo(this.vec32) > 8.0;
               if (!flag11 && !this.isInMineArea(blockPos7)) {
                  if (this.value16 >= 80) {
                     this.value16 = 0;
                     this.sendWarpMine();
                  }
                  break;
               }

               if (this.value16 >= 3) {
                  this.value16 = 0;
                  this.c0 = AutoMine.ModeKind.ANALYZING;
               }

               return;
            case 3:
               this.releaseMovementKeys();
               this.stopDigging();
               this.value16++;
               this.m_6h_void();
               if (this.m_7_boolean()) {
                  this.flag5 = true;
                  this.value11 = 0;
                  this.value16 = 0;
                  if (this.isInMine(MinecraftAccess.MINECRAFT.player.blockPosition())) {
                     this.navigateToMine();
                  } else {
                     this.m_4_void();
                  }

                  return;
               }

               boolean flag12 = this.integerMap.containsKey(this.value15);
               if (this.value16 < 5 || !flag12 && this.value16 < 40) {
                  return;
               }

               this.value16 = 0;
               this.value11 = 0;
               if (!flag12 && this.value15 != -1) {
                  this.integerMap2.put(this.value15, System.currentTimeMillis());
               }

               this.walkTo(BLOCK_POS3, AutoMine.ModeKind.WALKING_TO_DUMP);
               break;
            case 4:
               if (++this.value6 >= 200) {
                  this.value6 = 0;
                  this.releaseMovementKeys();
                  this.stopDigging();
                  this.walkToDump();
                  return;
               }

               BlockPos blockPos8 = MinecraftAccess.MINECRAFT.player.blockPosition();
               if (!this.m_2_boolean(blockPos8) && (!this.isInMine(blockPos8) || blockPos8.getY() > 69)) {
                  if (this.blockPos == null) {
                     this.blockPos = BLOCK_POS;
                  }

                  this.lookAtBlockInstant(this.blockPos);
                  break;
               }

               this.value6 = 0;
               this.releaseMovementKeys();
               this.stopDigging();
               if (blockPos8.getY() <= 69) {
                  this.c0 = AutoMine.ModeKind.CLEARING_LAYER;
               } else {
                  this.c0 = AutoMine.ModeKind.DIGGING_DOWN;
               }

               return;
            case 5:
               this.releaseMovementKeys();
               int value19 = MinecraftAccess.MINECRAFT.player.getBlockY();
               if (value19 != this.value7) {
                  this.value7 = value19;
                  this.value8 = 0;
               } else if (++this.value8 >= 200) {
                  this.value8 = 0;
                  this.walkToDump();
                  return;
               }

               if (value19 <= 69) {
                  this.c0 = AutoMine.ModeKind.CLEARING_LAYER;
                  this.stopDigging();
                  return;
               }

               this.m_6_v_void();
               break;
            case 6:
               if (!this.isInMine(MinecraftAccess.MINECRAFT.player.blockPosition())) {
                  this.releaseMovementKeys();
                  this.c0 = AutoMine.ModeKind.WALKING_TO_MINE;
                  return;
               }

               if (this.blockPos2 != null && !this.m_6_boolean(this.blockPos2)) {
                  this.blockPos2 = null;
               }

               if (this.blockPos2 == null) {
                  this.blockPos2 = this.findNearestBlock();
               }

               if (this.blockPos2 == null) {
                  this.releaseMovementKeys();
                  this.stopDigging();
                  this.walkTo(BLOCK_POS3, AutoMine.ModeKind.WALKING_TO_DUMP);
                  return;
               }

               Vec3 vec33 = Vec3.atCenterOf(this.blockPos2);
               double doubleValue = MinecraftAccess.MINECRAFT.player.getEyePosition().distanceTo(vec33);
               if (!this.blockPos2.equals(this.blockPos4)) {
                  this.blockPos4 = this.blockPos2;
                  this.value = 0;
                  this.value2 = 0;
               }

               Vec3 vec34 = MinecraftAccess.MINECRAFT.player.getEyePosition();
               float floatValue4 = Onetap.ROTATION_MODES.getRotationTo(vec33).getFloatValue();
               BlockPos blockPos9 = MinecraftAccess.MINECRAFT.player.blockPosition().relative(Direction.fromYRot(floatValue4));
               boolean flag13 = doubleValue > 2.0 || this.value3 > 0;
               if (this.value3 > 0) {
                  this.value3--;
               }

               boolean flag14 = flag13 && !blockPos9.equals(this.blockPos2) && this.canStandAt(blockPos9);
               MinecraftAccess.MINECRAFT.options.keyUp.setDown(flag13);
               MinecraftAccess.MINECRAFT.options.keyJump.setDown(flag14);
               BlockPos blockPos10 = null;
               BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec34, vec33, Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player));
               if (blockHitResult.getType() == Type.BLOCK) {
                  BlockPos blockPos11 = blockHitResult.getBlockPos();
                  boolean flag15 = vec34.distanceTo(Vec3.atCenterOf(blockPos11)) <= 4.5;
                  boolean flag16 = blockPos11.equals(this.blockPos2) || this.isDiggableBlock(blockPos11) && blockPos11.getY() >= 69 && (!flag14 || !blockPos11.equals(blockPos9));
                  if (flag15 && flag16) {
                     blockPos10 = blockPos11;
                  }
               }

               if (blockPos10 != null) {
                  this.applyRotation(Onetap.ROTATION_MODES.getRotationTo(Vec3.atCenterOf(blockPos10)), 60);
                  Rotations rotations = Onetap.ROTATION_MODES.toRotations();
                  Vec3 vec35 = Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue());
                  BlockHitResult blockHitResult2 = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec34, vec34.add(vec35.scale(4.5)), Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player));
                  BlockPos blockPos12 = blockHitResult2.getType() == Type.BLOCK ? blockHitResult2.getBlockPos() : null;
                  if (blockPos12 == null || !blockPos12.equals(this.blockPos2) && (!this.isDiggableBlock(blockPos12) || blockPos12.getY() < 69 || flag14 && blockPos12.equals(blockPos9))) {
                     this.stopDigging();
                  } else {
                     this.switchToBestTool(blockPos12);
                  }
               } else {
                  this.applyRotation(new Rotations(floatValue4, 0.0F), 0);
                  if (!flag14 && flag13) {
                     this.m_3_void(floatValue4);
                  } else {
                     this.stopDigging();
                  }
               }

               double doubleValue2 = MinecraftAccess.MINECRAFT.player.position().distanceToSqr(this.vec3);
               this.vec3 = MinecraftAccess.MINECRAFT.player.position();
               boolean flag17 = MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying();
               if (doubleValue2 < 4.0E-4 && !flag17) {
                  this.value++;
               } else {
                  this.value = 0;
               }

               if (this.value > 40) {
                  this.value = 0;
                  this.blockPos3 = null;
                  this.value3 = 8;
                  if (++this.value2 >= 3) {
                     this.value2 = 0;
                     this.blockPos2 = null;
                     return;
                  }
               }
               break;
            case 7:
               this.value16++;
               if (!this.m_0_boolean() && this.value16 < 600) {
                  if (this.isInMine(MinecraftAccess.MINECRAFT.player.blockPosition()) && MinecraftAccess.MINECRAFT.player.getBlockY() > BLOCK_POS3.getY() && (this.m_5_boolean() || this.value5 >= 30)
                     )
                   {
                     this.releaseMovementKeys();
                     this.m_6_v_void();
                     return;
                  }

                  this.lookAtBlock(BLOCK_POS3, true);
                  break;
               }

               this.releaseMovementKeys();
               this.stopDigging();
               this.value16 = 0;
               if (this.flag) {
                  this.flag = false;
                  this.flag4 = false;
                  this.value9 = 0;
                  this.c0 = AutoMine.ModeKind.DUMP_REPAIR;
               } else {
                  this.c0 = AutoMine.ModeKind.DUMPING;
               }

               return;
            case 8:
               this.releaseMovementKeys();
               this.stopDigging();
               this.value16++;
               if (this.m_3_boolean()) {
                  Rotations rotations2 = Onetap.ROTATION_MODES.toRotations();
                  this.applyRotation(new Rotations(rotations2.getFloatValue(), -20.0F), 60);
                  if (this.value16 < 3) {
                     return;
                  }

                  this.m_6_q_void();
               }

               if (this.flag3) {
                  this.m_3_void();
                  return;
               }

               this.m_7_void();
               break;
            case 9:
               this.releaseMovementKeys();
               this.m_1_void();
               break;
            case 10:
               this.releaseMovementKeys();
               this.value16++;
               long longValue2 = this.getRemainingSeconds(this.value15);
               BlockPos blockPos13 = this.flag10 ? null : this.m_3_BlockPos();
               if (blockPos13 != null) {
                  this.applyRotation(Onetap.ROTATION_MODES.getRotationTo(blockPos13), 60);
                  this.switchToBestTool(blockPos13);
               } else {
                  this.stopDigging();
               }

               if (!this.flag10 && blockPos13 == null && longValue2 >= 0L && longValue2 <= 30L && this.m_7_n_boolean()) {
                  this.sendCommand("sit");
                  this.flag10 = true;
               }

               if (this.findNearestBlock() != null) {
                  this.beginUnsit();
                  return;
               }

               long longValue3 = System.currentTimeMillis();
               boolean flag18 = longValue2 > 60L;
               boolean flag19 = this.longValue != -1L && longValue3 > this.longValue + 15000L;
               if (flag18 || flag19 || this.value16 >= 1800) {
                  this.beginUnsit();
               }
               break;
            case 11:
               this.releaseMovementKeys();
               this.stopDigging();
               this.value16++;
               MinecraftAccess.MINECRAFT.options.keyShift.setDown(this.flag10 && this.value16 <= 2);
               if (this.value16 < 4) {
                  return;
               }

               MinecraftAccess.MINECRAFT.options.keyShift.setDown(false);
               this.flag10 = false;
               this.longValue = -1L;
               this.value16 = 0;
               BlockTypeUtil.BLOCK_POS_SET.clear();
               this.blockPos2 = null;
               this.value10 = 0;
               this.value11 = 0;
               if (this.findNearestBlock() != null) {
                  this.flag5 = true;
                  this.navigateToMine();
               } else {
                  this.m_7_void();
               }
               break;
            case 12:
               this.releaseMovementKeys();
               this.stopDigging();
               this.value16++;
               if (this.value16 % 5 != 0) {
                  return;
               }

               if (this.isWithinResetWindow(this.getRemainingSeconds(this.value15))) {
                  this.walkTo(BLOCK_POS3, AutoMine.ModeKind.WALKING_TO_DUMP);
                  return;
               }

               int value20 = this.m_3_int();
               if (value20 != -1) {
                  this.goToAnarchy(value20);
               }
               break;
            case 13:
               this.releaseMovementKeys();
               this.stopDigging();
               this.value16++;
               if (this.value16 < 20 || this.value16 % 5 != 0 || MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  return;
               }

               BlockPos blockPos14 = this.findChest("склад");
               BlockPos blockPos15 = this.findChest("опыт");
               if (blockPos14 != null && blockPos15 != null) {
                  this.m_6_q_void();
                  this.c3 = AutoMine.ModeKind2.DEPOSIT;
                  this.c4 = AutoMine.ModeKind3.OPEN;
                  this.value16 = 0;
                  this.c0 = AutoMine.ModeKind.SKLAD_CHEST;
                  return;
               }

               if (!this.flag6 && this.value16 >= 60) {
                  this.flag6 = true;
                  this.sendCommand("home home");
                  return;
               }

               if (this.value16 >= 400) {
                  this.m_6e_void();
               }
               break;
            case 14:
               this.releaseMovementKeys();
               this.m_6_h_void();
               break;
            case 15:
               this.releaseMovementKeys();
               if (!this.timeHelper2.hasElapsed(50L)) {
                  return;
               }

               int value21 = -1;

               for (int i = 0; i < 9; i++) {
                  if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
                     value21 = i;
                     break;
                  }
               }

               int value22 = value21 == -1 ? -1 : this.findExpBottleSlot();
               if (value21 == -1 || value22 == -1) {
                  this.value16 = 0;
                  if (this.m_9_boolean()) {
                     this.flag2 = true;
                  }

                  this.m_6e_void();
                  return;
               }

               InventoryUtil.clickSlot(0, value22, value21, ContainerInput.SWAP);
               this.timeHelper2.reset();
         }
      }
   }

   private void lookAtBlockInstant(BlockPos blockPos7) {
      this.lookAtBlock(blockPos7, false);
   }

   private void lookAtBlock(BlockPos blockPos7, boolean flag11) {
      Rotations rotations = Onetap.ROTATION_MODES.getRotationTo(blockPos7);
      this.applyRotation(new Rotations(rotations.getFloatValue(), 0.0F), 0);
      MinecraftAccess.MINECRAFT.options.keyUp.setDown(true);
      double doubleValue = MinecraftAccess.MINECRAFT.player.position().distanceToSqr(this.vec3);
      this.vec3 = MinecraftAccess.MINECRAFT.player.position();
      if (doubleValue < 4.0E-4) {
         this.value5++;
      } else {
         this.value5 = 0;
      }

      boolean flag12 = this.value5 >= 15;
      float floatValue4 = rotations.getFloatValue();
      Direction direction = Direction.fromYRot(floatValue4);
      BlockPos blockPos8 = MinecraftAccess.MINECRAFT.player.blockPosition().relative(direction);
      boolean flag13 = this.isWoodBlock(blockPos8);
      boolean flag14 = !flag13 && this.canStandAt(blockPos8);
      MinecraftAccess.MINECRAFT.options.keyJump.setDown((flag14 || flag12) && !flag13);
      if (flag14) {
         this.blockPos5 = null;
         this.value4 = 0;
         this.stopDigging();
      } else {
         this.m_3_void(floatValue4);
      }
   }

   private boolean canStandAt(BlockPos blockPos7) {
      return !BlockTypeUtil.m_5_boolean(blockPos7)
         && BlockTypeUtil.m_5_boolean(blockPos7.above())
         && BlockTypeUtil.m_5_boolean(blockPos7.above(2))
         && BlockTypeUtil.m_5_boolean(MinecraftAccess.MINECRAFT.player.blockPosition().above(2));
   }

   private void m_6_v_void() {
      BlockPos blockPos7 = MinecraftAccess.MINECRAFT.player.blockPosition().below();
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7);
      Rotations rotations = Onetap.ROTATION_MODES.getRotationTo(blockPos7);
      this.applyRotation(rotations, 60);
      if (!blockState.isAir() && blockState.getDestroySpeed(MinecraftAccess.MINECRAFT.level, blockPos7) >= 0.0F) {
         Rotations rotations2 = Onetap.ROTATION_MODES.toRotations();
         float floatValue4 = Math.abs(rotations.getFloatValue2() - rotations2.getFloatValue2());
         if (!(floatValue4 < 10.0F) && !(rotations2.getFloatValue2() > 75.0F)) {
            this.stopDigging();
         } else {
            this.switchToBestTool(blockPos7);
         }
      } else {
         this.stopDigging();
      }
   }

   private void m_3_void(float floatValue4) {
      Direction direction = Direction.fromYRot(floatValue4);
      BlockPos blockPos7 = MinecraftAccess.MINECRAFT.player.blockPosition().relative(direction);
      BlockPos blockPos8 = blockPos7.above();
      BlockPos blockPos9 = null;
      boolean flag11 = false;
      if (MinecraftAccess.MINECRAFT.player.horizontalCollision) {
         if (this.isDiggableBlock(blockPos8)) {
            blockPos9 = blockPos8;
         } else if (this.isDiggableBlock(blockPos7)) {
            blockPos9 = blockPos7;
         }

         flag11 = blockPos9 != null;
      }

      if (blockPos9 == null) {
         Rotations rotations = Onetap.ROTATION_MODES.toRotations();
         Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         Vec3 vec34 = Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue());
         BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec33, vec33.add(vec34.scale(5.0)), Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player));
         if (blockHitResult.getType() == Type.BLOCK && this.isDiggableBlock(blockHitResult.getBlockPos()) && (blockHitResult.getBlockPos().equals(blockPos7) || blockHitResult.getBlockPos().equals(blockPos8))) {
            blockPos9 = blockHitResult.getBlockPos();
         }
      }

      if (blockPos9 == null) {
         this.blockPos5 = null;
         this.value4 = 0;
         this.stopDigging();
      } else {
         if (blockPos9.equals(this.blockPos5)) {
            this.value4++;
         } else {
            this.blockPos5 = blockPos9;
            this.value4 = 0;
         }

         if (this.value4 > 60) {
            BlockTypeUtil.BLOCK_POS_SET.add(blockPos9);
            this.blockPos5 = null;
            this.value4 = 0;
            this.stopDigging();
         } else {
            if (flag11) {
               this.applyRotation(Onetap.ROTATION_MODES.getRotationTo(blockPos9), 60);
            }

            this.switchToBestTool(blockPos9);
         }
      }
   }

   private boolean isDiggableBlock(BlockPos blockPos7) {
      if (!BlockTypeUtil.BLOCK_POS_SET.contains(blockPos7) && BlockTypeUtil.isInArea(blockPos7)) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7);
         return !blockState.isAir() && blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) && blockState.getDestroySpeed(MinecraftAccess.MINECRAFT.level, blockPos7) >= 0.0F;
      } else {
         return false;
      }
   }

   private boolean isWoodBlock(BlockPos blockPos7) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7);
      return blockState.is(BlockTags.LOGS)
         || blockState.is(BlockTags.PLANKS)
         || blockState.is(BlockTags.LEAVES)
         || blockState.is(BlockTags.WOODEN_STAIRS)
         || blockState.is(BlockTags.WOODEN_SLABS)
         || blockState.is(BlockTags.WOODEN_FENCES);
   }

   private void m_6h_void() {
      BlockPos blockPos7 = MinecraftAccess.MINECRAFT.player.blockPosition();
      double doubleValue = blockPos7.getX() - BLOCK_POS2.getX();
      double doubleValue2 = blockPos7.getZ() - BLOCK_POS2.getZ();
      double doubleValue3 = doubleValue * doubleValue + doubleValue2 * doubleValue2;
      if (doubleValue3 > 9.0 && doubleValue3 < 400.0) {
         this.applyRotation(Onetap.ROTATION_MODES.getRotationTo(BLOCK_POS2), 0);
      }
   }

   private void applyRotation(Rotations rotations, int value19) {
      if (++this.value18 >= 20) {
         this.value18 = 0;
         this.floatValue3 = 40.0F + this.random.nextFloat() * 20.0F;
      }

      this.floatValue2 = this.floatValue2 + (this.floatValue3 - this.floatValue2) * 0.2F;
      float floatValue4 = (float)Math.sin(MinecraftAccess.MINECRAFT.player.tickCount * 0.35F) * 2.5F;
      float floatValue5 = (float)Math.cos(MinecraftAccess.MINECRAFT.player.tickCount * 0.23F) * 1.8F;
      Rotations rotations2 = Onetap.ROTATION_MODES.toRotations();
      float floatValue6 = Mth.clamp(Mth.wrapDegrees(rotations.getFloatValue() + floatValue4 - rotations2.getFloatValue()), -this.floatValue2, this.floatValue2);
      Onetap.ROTATION_MODES.rotateWithSensitivity(new Rotations(rotations2.getFloatValue() + floatValue6, rotations.getFloatValue2() + floatValue5), false, value19);
   }

   private int m_3_int() {
      long longValue2 = System.currentTimeMillis();
      int value19 = -1;
      long longValue3 = Long.MAX_VALUE;
      ArrayList<Integer> integerList = new ArrayList<>();

      for (int value20 : this.getAnarchyIds()) {
         if (value20 != this.value15 && value20 != this.storageAnarchy.getInt()) {
            Long longObj = this.integerMap.get(value20);
            if (longObj != null && longObj > longValue2) {
               long longValue4 = longObj - longValue2;
               if (longValue4 > 3000L && longValue4 <= 30000L && longObj < longValue3) {
                  longValue3 = longObj;
                  value19 = value20;
               }
            } else {
               Long longObj2 = this.integerMap2.get(value20);
               if (longObj2 == null || longValue2 - longObj2 >= 180000L) {
                  integerList.add(value20);
               }
            }
         }
      }

      if (value19 != -1) {
         return value19;
      } else {
         return integerList.isEmpty() ? -1 : (Integer)integerList.get(this.random.nextInt(integerList.size()));
      }
   }

   private int findAnarchyToJoin() {
      int value19 = this.m_3_int();
      if (value19 != -1) {
         return value19;
      }

      int[] intArray = this.getAnarchyIds();
      int value20 = intArray[this.random.nextInt(intArray.length)];

      while (value20 == this.value15 || value20 == this.storageAnarchy.getInt()) {
         value20 = intArray[this.random.nextInt(intArray.length)];
      }

      return value20;
   }

   private void m_6x_void() {
      int value19 = this.m_3_int();
      if (value19 != -1) {
         this.goToAnarchy(value19);
      } else {
         this.value16 = 0;
         this.c0 = AutoMine.ModeKind.WAITING_NEXT;
      }
   }

   private void m_7_void() {
      this.value16 = 0;
      long longValue2 = this.getRemainingSeconds(this.value15);
      if (this.isWithinResetWindow(longValue2)) {
         this.longValue = this.integerMap.getOrDefault(this.value15, -1L);
         this.flag10 = false;
         this.c0 = AutoMine.ModeKind.WAITING_RESET;
      } else {
         this.m_6x_void();
      }
   }

   private boolean isStandingInMine() {
      return MinecraftAccess.MINECRAFT.player.onGround() && MinecraftAccess.MINECRAFT.player.getBlockY() == 69 && this.isInMine(MinecraftAccess.MINECRAFT.player.blockPosition());
   }

   private boolean m_7_n_boolean() {
      return this.isStandingInMine() && !this.m_1_boolean();
   }

   private BlockPos m_3_BlockPos() {
      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().deflate(0.001);

      for (BlockPos blockPos7 : BlockPos.betweenClosed(
         Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ)
      )) {
         if (!BlockTypeUtil.m_5_boolean(blockPos7)) {
            BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7);
            if (!(blockState.getDestroySpeed(MinecraftAccess.MINECRAFT.level, blockPos7) < 0.0F)) {
               return blockPos7.immutable();
            }
         }
      }

      return null;
   }

   private boolean m_1_boolean() {
      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().deflate(0.001);

      for (BlockPos blockPos7 : BlockPos.betweenClosed(
         Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ)
      )) {
         if (!BlockTypeUtil.m_5_boolean(blockPos7)) {
            return true;
         }
      }

      return false;
   }

   private boolean m_6_boolean() {
      switch (this.c0.ordinal()) {
         case 4:
         case 6:
         case 7:
         case 8:
         case 12:
            if (this.flag) {
               return false;
            }

            long longValue2 = this.getRemainingSeconds(this.value15);
            return longValue2 >= 0L && longValue2 <= 4L && this.isStandingInMine();
         case 5:
         case 9:
         case 10:
         case 11:
         default:
            return false;
      }
   }

   private boolean isWithinResetWindow(long longValue2) {
      return longValue2 >= 0L && longValue2 <= 30L;
   }

   private void beginUnsit() {
      this.value16 = 0;
      this.c0 = AutoMine.ModeKind.UNSITTING;
   }

   private boolean m_5_boolean() {
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.position();
      double doubleValue = vec33.x - (BLOCK_POS3.getX() + 0.5);
      double doubleValue2 = vec33.z - (BLOCK_POS3.getZ() + 0.5);
      return doubleValue * doubleValue + doubleValue2 * doubleValue2 <= 2.25;
   }

   private boolean m_0_boolean() {
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.position();
      double doubleValue = vec33.x - (BLOCK_POS3.getX() + 0.5);
      double doubleValue2 = vec33.z - (BLOCK_POS3.getZ() + 0.5);
      return doubleValue * doubleValue + doubleValue2 * doubleValue2 <= 2.25 && MinecraftAccess.MINECRAFT.player.getBlockY() == BLOCK_POS3.getY();
   }

   private long getRemainingSeconds(int value19) {
      if (value19 == -1) {
         return -1L;
      }

      Long longObj = this.integerMap.get(value19);
      return longObj == null ? -1L : (longObj - System.currentTimeMillis()) / 1000L;
   }

   private int[] getAnarchyIds() {
      return switch ((String)this.anarchyVersion.getValue()) {
         case "1.16.5" -> VALUE_ARRAY;
         case "1.21" -> VALUE_ARRAY2;
         default -> VALUE_ARRAY3;
      };
   }

   private boolean m_2_boolean(int value19) {
      for (int value20 : this.getAnarchyIds()) {
         if (value20 == value19) {
            return true;
         }
      }

      return false;
   }

   private void m_63_void() {
      if (this.value15 != -1) {
         ArrayList<ArmorStand> armorStandList = new ArrayList<>();

         for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
            if (entity instanceof ArmorStand armorStand) {
               String text = getArmorStandName(armorStand);
               if (text.contains("Авто-Шахта") || text.contains("Следующая:") || text.contains("Обновление через:") || PATTERN2.matcher(text).matches()) {
                  armorStandList.add(armorStand);
               }
            }
         }

         if (armorStandList.size() >= 4) {
            armorStandList.sort(Comparator.comparingDouble(armorStand2 -> -armorStand2.getY()));
            String text2 = getArmorStandName((ArmorStand)armorStandList.get(0));
            String text3 = getArmorStandName((ArmorStand)armorStandList.get(1));
            String text4 = getArmorStandName((ArmorStand)armorStandList.get(2));
            String text5 = getArmorStandName((ArmorStand)armorStandList.get(3));
            if (text2.contains("Авто-Шахта") && text3.contains("Следующая:") && text4.contains("Обновление через:") && PATTERN2.matcher(text5).matches()) {
               String[] stringArray = text5.split(":");
               if (stringArray.length == 2) {
                  int value19 = Integer.parseInt(stringArray[0]) * 60 + Integer.parseInt(stringArray[1]);
                  this.integerMap.put(this.value15, System.currentTimeMillis() + value19 * 1000L);
                  this.integerMap2.remove(this.value15);
               }
            }
         }
      }
   }

   private static String getArmorStandName(ArmorStand armorStand) {
      return armorStand.getCustomName() != null ? armorStand.getCustomName().getString() : "";
   }

   private Integer readScoreboardValue() {
      try {
         Objective objective = MinecraftAccess.MINECRAFT.level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
         if (objective == null) {
            return null;
         }

         Matcher matcher = PATTERN.matcher(normalize(objective.getDisplayName().getString()));
         if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
         }
      } catch (Exception e) {
      }

      return null;
   }

   private static String normalize(String text) {
      return text == null ? "" : text.replaceAll("§.", "").replace('ё', 'е').trim().toLowerCase(Locale.ROOT);
   }

   private boolean m_3_boolean() {
      int value19 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();

      for (int i = 0; i < 40; i++) {
         if (i != value19) {
            ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
            if (!itemStack2.isEmpty() && !this.isJunkItem(itemStack2)) {
               return true;
            }
         }
      }

      return false;
   }

   private void m_6_q_void() {
      AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
      int value19 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();

      for (Slot slot : abstractContainerMenu.slots) {
         if (slot.container == MinecraftAccess.MINECRAFT.player.getInventory()) {
            int value20 = slot.getContainerSlot();
            if (value20 >= 0 && value20 <= 40 && value20 != 40 && value20 != value19 && slot.hasItem() && !this.isJunkItem(slot.getItem())) {
               InventoryUtil.clickSlot(abstractContainerMenu.containerId, slot.index, 1, ContainerInput.THROW);
            }
         }
      }
   }

   private boolean isJunkItem(ItemStack itemStack2) {
      Item item = itemStack2.getItem();
      return item != Items.DIAMOND
            && item != Items.GOLD_INGOT
            && item != Items.RAW_GOLD
            && item != Items.EXPERIENCE_BOTTLE
            && item != Items.PLAYER_HEAD
            && item != Items.DIAMOND_PICKAXE
            && item != Items.NETHERITE_PICKAXE
         ? itemStack2.get(DataComponents.FOOD) != null
         : true;
   }

   private void walkToDump() {
      this.value16 = 0;
      this.walkTo(BLOCK_POS3, AutoMine.ModeKind.WALKING_TO_DUMP);
   }

   private void m_3_void() {
      this.flag3 = false;
      this.flag6 = false;
      this.flag7 = false;
      this.itemStack = ItemStack.EMPTY;
      this.flag8 = false;
      this.flag9 = false;
      this.value12 = this.value15;
      this.releaseMovementKeys();
      this.stopDigging();
      this.sendCommand("an" + this.storageAnarchy.getInt());
      this.value16 = 0;
      this.c0 = AutoMine.ModeKind.GO_SKLAD;
   }

   private void m_6e_void() {
      this.c3 = AutoMine.ModeKind2.DEPOSIT;
      this.c4 = AutoMine.ModeKind3.OPEN;
      this.blockPos6 = null;
      int value19 = this.value12 != -1 ? this.value12 : this.findAnarchyToJoin();
      this.goToAnarchy(value19);
   }

   private void m_6_h_void() {
      AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
      switch (this.c4.ordinal()) {
         case 0:
            if (abstractContainerMenu != null) {
               this.c4 = AutoMine.ModeKind3.ACTION;
               this.timeHelper2.reset();
               this.timeHelper.reset();
               return;
            }

            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.closeScreens();
               this.timeHelper.reset();
               return;
            }

            if (this.blockPos6 == null || !(MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos6).getBlock() instanceof ChestBlock)) {
               this.blockPos6 = this.findChest(this.c3 == AutoMine.ModeKind2.TAKE_EXP ? "опыт" : "склад");
            }

            if (this.blockPos6 == null) {
               this.resetContainerStateMine(true);
               return;
            }

            if (!MinecraftAccess.MINECRAFT.player.blockPosition().closerThan(this.blockPos6, 4.0)) {
               if (++this.value16 >= 200) {
                  this.resetContainerStateMine(true);
                  return;
               }

               this.lookAtBlockInstant(this.blockPos6);
               return;
            }

            if (!this.timeHelper.hasElapsed(100L)) {
               return;
            }

            Rotations rotations = Onetap.ROTATION_MODES.getRotationTo(this.blockPos6);
            Onetap.ROTATION_MODES.rotate(rotations, false, 60);
            BlockHitResult blockHitResult = new BlockHitResult(
               new Vec3(this.blockPos6.getX() + 0.5, this.blockPos6.getY() + 1.0, this.blockPos6.getZ() + 0.5), Direction.UP, this.blockPos6, true
            );
            MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
            this.c4 = AutoMine.ModeKind3.WAIT_OPEN;
            this.timeHelper.reset();
            break;
         case 1:
            if (abstractContainerMenu != null) {
               this.c4 = AutoMine.ModeKind3.ACTION;
               this.timeHelper2.reset();
               this.timeHelper.reset();
               return;
            }

            if (this.timeHelper.hasElapsed(500L)) {
               this.c4 = AutoMine.ModeKind3.OPEN;
               this.timeHelper.reset();
            }
            break;
         case 2:
            if (abstractContainerMenu == null) {
               this.c4 = AutoMine.ModeKind3.OPEN;
               this.timeHelper.reset();
               return;
            }

            if (this.timeHelper.hasElapsed(15000L)) {
               this.closeScreens();
               this.c4 = AutoMine.ModeKind3.CLOSE;
               this.timeHelper.reset();
               return;
            }

            boolean flag11 = this.c3 == AutoMine.ModeKind2.TAKE_EXP ? this.m_8_boolean(abstractContainerMenu) : this.m_6_boolean(abstractContainerMenu);
            if (flag11) {
               this.closeScreens();
               this.c4 = AutoMine.ModeKind3.CLOSE;
               this.timeHelper.reset();
            }
            break;
         case 3:
            if (!this.m_3_boolean()) {
               this.c4 = AutoMine.ModeKind3.OPEN;
               this.timeHelper.reset();
               return;
            }

            Rotations rotations2 = Onetap.ROTATION_MODES.toRotations();
            Onetap.ROTATION_MODES.rotate(new Rotations(this.floatValue, rotations2.getFloatValue2()), false, 60);
            float floatValue4 = Math.abs(Mth.wrapDegrees(this.floatValue - rotations2.getFloatValue()));
            if (floatValue4 >= 10.0F && !this.timeHelper.hasElapsed(300L)) {
               return;
            }

            this.m_6_q_void();
            this.c4 = AutoMine.ModeKind3.OPEN;
            this.timeHelper.reset();
            break;
         case 4:
            if (abstractContainerMenu == null) {
               this.resetContainerStateMine(false);
            } else if (this.timeHelper.hasElapsed(300L)) {
               this.closeScreens();
               this.timeHelper.reset();
            }
      }
   }

   private void resetContainerStateMine(boolean flag11) {
      this.c4 = AutoMine.ModeKind3.OPEN;
      this.blockPos6 = null;
      this.value16 = 0;
      if (flag11) {
         this.m_6e_void();
      } else {
         if (this.c3 == AutoMine.ModeKind2.DEPOSIT) {
            this.c3 = AutoMine.ModeKind2.TAKE_EXP;
         } else {
            this.c3 = AutoMine.ModeKind2.DEPOSIT;
            this.c0 = AutoMine.ModeKind.SKLAD_FILL;
         }
      }
   }

   private boolean m_6_boolean(AbstractContainerMenu abstractContainerMenu) {
      if (!this.timeHelper2.hasElapsed(50L)) {
         return false;
      }

      int value19 = abstractContainerMenu.slots.size();

      for (int i = Math.max(0, value19 - 36); i < value19; i++) {
         ItemStack itemStack2 = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack2.isEmpty() && itemStack2.getCount() > 1) {
            Item item = itemStack2.getItem();
            if (item == Items.DIAMOND || item == Items.GOLD_INGOT || item == Items.RAW_GOLD) {
               int value20 = this.findSlotInContainer(abstractContainerMenu, itemStack2);
               if (value20 == -1) {
                  return true;
               }

               InventoryUtil.clickSlot(abstractContainerMenu.containerId, i, 0, ContainerInput.PICKUP);
               InventoryUtil.clickSlot(abstractContainerMenu.containerId, i, 1, ContainerInput.PICKUP);
               InventoryUtil.clickSlot(abstractContainerMenu.containerId, value20, 0, ContainerInput.PICKUP);
               if (!abstractContainerMenu.getCarried().isEmpty()) {
                  InventoryUtil.clickSlot(abstractContainerMenu.containerId, i, 0, ContainerInput.PICKUP);
               }

               this.timeHelper2.reset();
               return false;
            }
         }
      }

      return true;
   }

   private int findSlotInContainer(AbstractContainerMenu abstractContainerMenu, ItemStack itemStack2) {
      int value19 = abstractContainerMenu.slots.size() - 36;

      for (int i = 0; i < value19; i++) {
         ItemStack itemStack3 = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack3.isEmpty() && ItemStack.isSameItemSameComponents(itemStack3, itemStack2) && itemStack3.getCount() < itemStack3.getMaxStackSize()) {
            return i;
         }
      }

      for (int i2 = 0; i2 < value19; i2++) {
         if (abstractContainerMenu.getSlot(i2).getItem().isEmpty()) {
            return i2;
         }
      }

      return -1;
   }

   private boolean m_8_boolean(AbstractContainerMenu abstractContainerMenu) {
      if (!this.timeHelper2.hasElapsed(50L)) {
         return false;
      }

      if (this.countItemTotal(Items.EXPERIENCE_BOTTLE) >= 192) {
         return true;
      }

      int value19 = abstractContainerMenu.slots.size() - 36;

      for (int i = 0; i < value19; i++) {
         ItemStack itemStack2 = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack2.isEmpty() && itemStack2.is(Items.EXPERIENCE_BOTTLE)) {
            if (!this.hasFreeSlot()) {
               if (this.flag7) {
                  return true;
               }

               this.flag7 = true;
               this.closeScreens();
               this.floatValue = Onetap.ROTATION_MODES.toRotations().getFloatValue() + 180.0F;
               this.c4 = AutoMine.ModeKind3.DROP_JUNK;
               this.timeHelper.reset();
               return false;
            }

            InventoryUtil.clickSlot(abstractContainerMenu.containerId, i, 0, ContainerInput.QUICK_MOVE);
            this.timeHelper2.reset();
            return false;
         }
      }

      return true;
   }

   private int findExpBottleSlot() {
      for (int i = 9; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).is(Items.EXPERIENCE_BOTTLE)) {
            return i;
         }
      }

      return -1;
   }

   private boolean hasFreeSlot() {
      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private void m_1_void() {
      Rotations rotations = Onetap.ROTATION_MODES.toRotations();
      Onetap.ROTATION_MODES.rotate(new Rotations(rotations.getFloatValue(), 90.0F), false, 60);
      if (this.timeHelper2.hasElapsed(50L)) {
         this.timeHelper2.reset();
         if (!this.isPlayerNearby()) {
            if (!this.flag4 && !this.m_3_boolean()) {
               this.flag4 = true;
            }

            if (!this.flag4 && MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() != Items.PLAYER_HEAD) {
               Onetap.ROTATION_MODES.rotate(new Rotations(rotations.getFloatValue(), -20.0F), false, 60);
               if (++this.value9 >= 1) {
                  this.flag4 = true;
                  this.value9 = 0;
                  this.m_6_q_void();
               }
            } else {
               ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getOffhandItem();
               boolean flag11 = itemStack2.getItem() == Items.DIAMOND_PICKAXE || itemStack2.getItem() == Items.NETHERITE_PICKAXE;
               if (this.flag9) {
                  this.flag9 = false;
                  this.m_3_void(itemStack2);
                  if (this.flag8) {
                     this.flag8 = false;
                     this.m_3_void();
                  } else {
                     this.retryNavigation();
                  }
               } else if (!flag11) {
                  int value19 = this.findPickaxeSlot();
                  if (value19 == -1) {
                     this.retryNavigation();
                  } else {
                     this.flag9 = false;
                     if (value19 >= 9) {
                        int value20 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
                        InventoryUtil.clickSlot(0, value19, value20, ContainerInput.SWAP);
                        this.value13 = value20;
                     } else {
                        this.value13 = value19;
                        MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value19);
                        MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value19));
                        this.itemStack = itemStack2.copy();
                        InventoryUtil.clickSlot(0, value19 + 36, 40, ContainerInput.SWAP);
                     }
                  }
               } else {
                  if (itemStack2.getDamageValue() > 0 && this.countItemTotal(Items.EXPERIENCE_BOTTLE) <= 0) {
                     this.flag8 = true;
                  }

                  if (itemStack2.getDamageValue() != 0 && !this.flag8) {
                     if (!MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.EXPERIENCE_BOTTLE)) {
                        int value21 = this.findItemSlot(Items.EXPERIENCE_BOTTLE);
                        if (value21 != -1) {
                           if (value21 < 9) {
                              MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value21);
                              MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value21));
                           } else {
                              InventoryUtil.clickSlot(0, value21, MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot(), ContainerInput.SWAP);
                           }
                        }
                     } else {
                        MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
                     }
                  } else {
                     this.flag9 = true;
                     MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(this.value13);
                     MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(this.value13));
                     InventoryUtil.clickSlot(0, 45, this.value13, ContainerInput.SWAP);
                  }
               }
            }
         }
      }
   }

   private void m_3_void(ItemStack itemStack2) {
      ItemStack itemStack3 = this.itemStack;
      this.itemStack = ItemStack.EMPTY;
      if (!itemStack3.isEmpty()) {
         if (!ItemStack.isSameItemSameComponents(itemStack2, itemStack3)) {
            int value19 = -1;

            for (int i = 0; i < 36 && value19 == -1; i++) {
               if (ItemStack.isSameItemSameComponents(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i), itemStack3)) {
                  value19 = i;
               }
            }

            for (int i2 = 0; i2 < 36 && value19 == -1; i2++) {
               if (ItemStack.isSameItem(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i2), itemStack3)) {
                  value19 = i2;
               }
            }

            if (value19 != -1) {
               InventoryUtil.clickSlot(0, value19 < 9 ? value19 + 36 : value19, 40, ContainerInput.SWAP);
            }
         }
      }
   }

   private void retryNavigation() {
      this.value16 = 0;
      this.navigateToMine();
   }

   private boolean isPlayerNearby() {
      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (player != MinecraftAccess.MINECRAFT.player && player.isAlive() && !(player.distanceToSqr(MinecraftAccess.MINECRAFT.player) > 4.0)) {
            String text = player.getGameProfile().name();
            if (text != null) {
               String text2 = text.trim().toLowerCase(Locale.ROOT);
               if (!text2.isEmpty() && !text2.startsWith("cit-") && !text2.contains("npc") && !Onetap.FRIEND_MANAGER.isFriend(text)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean m_9_boolean() {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         Item item = itemStack2.getItem();
         if ((item == Items.DIAMOND_PICKAXE || item == Items.NETHERITE_PICKAXE) && itemStack2.isDamageableItem() && itemStack2.getMaxDamage() - itemStack2.getDamageValue() < 200
            )
          {
            return true;
         }
      }

      return false;
   }

   private int findPickaxeSlot() {
      for (int i = 0; i < 36; i++) {
         Item item = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getItem();
         if (item == Items.DIAMOND_PICKAXE || item == Items.NETHERITE_PICKAXE) {
            return i;
         }
      }

      return -1;
   }

   private int findItemSlot(Item item) {
      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).is(item)) {
            return i;
         }
      }

      return -1;
   }

   private int countItemTotal(Item item) {
      int value19 = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack2.is(item)) {
            value19 += itemStack2.getCount();
         }
      }

      return value19;
   }

   private int countGold() {
      return this.countItemTotal(Items.GOLD_INGOT) + this.countItemTotal(Items.RAW_GOLD);
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

   private BlockPos findChest(String text) {
      BlockPos blockPos7 = MinecraftAccess.MINECRAFT.player.blockPosition();
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      BlockPos blockPos8 = null;
      double doubleValue = Double.MAX_VALUE;

      for (int i = -6; i <= 6; i++) {
         for (int i2 = -3; i2 <= 3; i2++) {
            for (int i3 = -6; i3 <= 6; i3++) {
               BlockPos blockPos9 = blockPos7.offset(i, i2, i3);
               if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos9).getBlock() instanceof ChestBlock
                  && !(Vec3.atCenterOf(blockPos9).distanceTo(vec33) > 8.0)
                  && this.m_9(blockPos9, text)) {
                  double doubleValue2 = Vec3.atCenterOf(blockPos9).distanceToSqr(vec33);
                  if (doubleValue2 < doubleValue) {
                     doubleValue = doubleValue2;
                     blockPos8 = blockPos9.immutable();
                  }
               }
            }
         }
      }

      return blockPos8;
   }

   private boolean m_9(BlockPos blockPos7, String text) {
      BlockPos[] blockPosArray = new BlockPos[]{
         blockPos7.above(),
         blockPos7.north(),
         blockPos7.south(),
         blockPos7.east(),
         blockPos7.west(),
         blockPos7.above().north(),
         blockPos7.above().south(),
         blockPos7.above().east(),
         blockPos7.above().west()
      };

      for (BlockPos blockPos8 : blockPosArray) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos8);
         if (MinecraftAccess.MINECRAFT.level.getBlockEntity(blockPos8) instanceof SignBlockEntity signBlockEntity) {
            BlockPos blockPos9;
            if (blockState.getBlock() instanceof WallSignBlock) {
               blockPos9 = blockPos8.relative(blockState.getValue(WallSignBlock.FACING).getOpposite());
            } else if (blockState.getBlock() instanceof WallHangingSignBlock) {
               blockPos9 = blockPos8.relative(blockState.getValue(WallHangingSignBlock.FACING).getOpposite());
            } else if (blockState.getBlock() instanceof CeilingHangingSignBlock) {
               blockPos9 = blockPos8.above();
            } else {
               blockPos9 = blockPos8.below();
            }

            if (blockPos9.equals(blockPos7)) {
               if (this.signContains(signBlockEntity.getFrontText(), text)) {
                  return true;
               }

               if (this.signContains(signBlockEntity.getBackText(), text)) {
                  return true;
               }
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
         if (component != null) {
            for (String text2 : normalize(component.getString()).split("[^а-яa-z0-9]+")) {
               if (text2.startsWith(text)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private void goToAnarchy(int value19) {
      String text = "an" + value19;
      this.sendCommand(text);
      this.value15 = -1;
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.blockPos4 = null;
      this.value = 0;
      this.value2 = 0;
      this.value3 = 0;
      this.blockPos5 = null;
      this.value4 = 0;
      BlockTypeUtil.BLOCK_POS_SET.clear();
      this.value6 = 0;
      this.flag10 = false;
      this.longValue = -1L;
      MinecraftAccess.MINECRAFT.options.keyShift.setDown(false);
      this.flag5 = false;
      this.c0 = AutoMine.ModeKind.WAITING_ANARCHY;
      this.value16 = 0;
   }

   private void sendWarpMine() {
      this.sendCommand("warp mine");
      this.vec32 = MinecraftAccess.MINECRAFT.player.position();
      this.c0 = AutoMine.ModeKind.TELEPORTING_WARP;
      this.value16 = 0;
   }

   private void sendCommand(String text) {
      if (MinecraftAccess.MINECRAFT.getConnection() != null) {
         if (text.startsWith("/")) {
            text = text.substring(1);
         }

         MinecraftAccess.MINECRAFT.getConnection().sendCommand(text);
      }
   }

   private void m_4_void() {
      this.blockPos = MinecraftAccess.MINECRAFT.player.getBlockY() <= 69 ? BLOCK_POS2 : BLOCK_POS;
      this.value16 = 0;
      this.value5 = 0;
      this.c0 = AutoMine.ModeKind.WALKING_TO_MINE;
   }

   private void walkTo(BlockPos blockPos7, AutoMine.ModeKind c02) {
      this.blockPos = blockPos7;
      this.value16 = 0;
      this.value5 = 0;
      this.c0 = c02;
   }

   private void releaseMovementKeys() {
      MinecraftAccess.MINECRAFT.options.keyUp.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyDown.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyLeft.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyRight.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyJump.setDown(false);
   }

   private void switchToBestTool(BlockPos blockPos7) {
      int value19 = this.findBestToolSlot(MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7));
      if (value19 != -1 && value19 != MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()) {
         MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value19);
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value19));
      }

      Direction direction = this.getDirectionTo(blockPos7);
      if (!blockPos7.equals(this.blockPos3)) {
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, blockPos7, direction));
         MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
         this.blockPos3 = blockPos7;
      }

      this.m_1_void(blockPos7);
   }

   private void m_1_void(BlockPos blockPos7) {
      Rotations rotations = Onetap.ROTATION_MODES.toRotations();
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      Vec3 vec34 = Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue());
      BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec33, vec33.add(vec34.scale(5.0)), Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player));
      if (blockHitResult.getType() == Type.BLOCK && blockHitResult.getBlockPos().equals(blockPos7)) {
         HitResult hitResult = MinecraftAccess.MINECRAFT.hitResult;
         MinecraftAccess.MINECRAFT.hitResult = blockHitResult;

         try {
            MinecraftAccessor minecraftAccessor = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
            boolean flag11 = minecraftAccessor.onetap$startAttack();
            if (!flag11 && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
               minecraftAccessor.onetap$continueAttack(true);
            }
         } finally {
            MinecraftAccess.MINECRAFT.hitResult = hitResult;
         }
      } else {
         this.stopDigging();
      }
   }

   private void stopDigging() {
      MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
      if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
         MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
      }
   }

   private BlockPos findNearestBlock() {
      BlockPos blockPos7 = MinecraftAccess.MINECRAFT.player.blockPosition();
      BlockPos blockPos8 = null;
      double doubleValue = Double.MAX_VALUE;

      for (int i = -68; i <= -48; i++) {
         for (int i2 = 69; i2 <= 72; i2++) {
            for (int i3 = 25; i3 <= 45; i3++) {
               BlockPos blockPos9 = new BlockPos(i, i2, i3);
               net.minecraft.world.level.block.Block block = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos9).getBlock();
               if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
                  double doubleValue2 = blockPos9.distSqr(blockPos7);
                  if (doubleValue2 < doubleValue) {
                     doubleValue = doubleValue2;
                     blockPos8 = blockPos9;
                  }
               }
            }
         }
      }

      return blockPos8;
   }

   private boolean m_6_boolean(BlockPos blockPos7) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return false;
      }

      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7);
      return !blockState.isAir() && blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) && !(blockState.getDestroySpeed(MinecraftAccess.MINECRAFT.level, blockPos7) < 0.0F)
         ? this.isInMine(blockPos7) && blockPos7.getY() >= 69 && blockPos7.getY() <= 72
         : false;
   }

   private boolean m_7_boolean() {
      for (int i = -68; i <= -48; i++) {
         for (int i2 = 69; i2 <= 72; i2++) {
            for (int i3 = 25; i3 <= 45; i3++) {
               BlockPos blockPos7 = new BlockPos(i, i2, i3);
               net.minecraft.world.level.block.Block block = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7).getBlock();
               if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean isInMine(BlockPos blockPos7) {
      return blockPos7.getX() >= -68 && blockPos7.getX() <= -48 && blockPos7.getZ() >= 25 && blockPos7.getZ() <= 45;
   }

   private boolean m_2_boolean(BlockPos blockPos7) {
      return blockPos7.getX() >= -66 && blockPos7.getX() <= -50 && blockPos7.getZ() >= 27 && blockPos7.getZ() <= 43;
   }

   private Direction getDirectionTo(BlockPos blockPos7) {
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      Vec3 vec34 = Vec3.atCenterOf(blockPos7);
      double doubleValue = vec33.x - vec34.x;
      double doubleValue2 = vec33.y - vec34.y;
      double doubleValue3 = vec33.z - vec34.z;
      double doubleValue4 = Math.abs(doubleValue);
      double doubleValue5 = Math.abs(doubleValue2);
      double doubleValue6 = Math.abs(doubleValue3);
      if (doubleValue4 > doubleValue5 && doubleValue4 > doubleValue6) {
         return doubleValue > 0.0 ? Direction.EAST : Direction.WEST;
      } else if (doubleValue5 > doubleValue4 && doubleValue5 > doubleValue6) {
         return doubleValue2 > 0.0 ? Direction.UP : Direction.DOWN;
      } else {
         return doubleValue3 > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   private int findBestToolSlot(BlockState blockState) {
      float floatValue4 = 1.0F;
      int value19 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();

      for (int i = 0; i < 9; i++) {
         float floatValue5 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getDestroySpeed(blockState);
         if (floatValue5 > floatValue4) {
            floatValue4 = floatValue5;
            value19 = i;
         }
      }

      return value19;
   }

   static {
      System.arraycopy(VALUE_ARRAY, 0, VALUE_ARRAY3, 0, VALUE_ARRAY.length);
      System.arraycopy(VALUE_ARRAY2, 0, VALUE_ARRAY3, VALUE_ARRAY.length, VALUE_ARRAY2.length);
   }

   private enum ModeKind {
      IDLE,
      WAITING_ANARCHY,
      TELEPORTING_WARP,
      ANALYZING,
      WALKING_TO_MINE,
      DIGGING_DOWN,
      CLEARING_LAYER,
      WALKING_TO_DUMP,
      DUMPING,
      DUMP_REPAIR,
      WAITING_RESET,
      UNSITTING,
      WAITING_NEXT,
      GO_SKLAD,
      SKLAD_CHEST,
      SKLAD_FILL;

      private static String a(byte[] byteArray) {
         int value19 = 0;
         int value20;
         char[] charArray = new char[value20 = byteArray.length];

         for (int i = 0; i < value20; i++) {
            int value21;
            if ((value21 = 255 & byteArray[i]) < 192) {
               charArray[value19++] = (char)value21;
            } else if (value21 < 224) {
               char ch = (char)((char)(value21 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value19++] = ch;
            } else if (i < value20 - 2) {
               char ch2 = (char)((char)(value21 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value19++] = ch2;
            }
         }

         return new String(charArray, 0, value19);
      }
   }

   private enum ModeKind2 {
      DEPOSIT,
      TAKE_EXP;

      private static String a(byte[] byteArray) {
         int value19 = 0;
         int value20;
         char[] charArray = new char[value20 = byteArray.length];

         for (int i = 0; i < value20; i++) {
            int value21;
            if ((value21 = 255 & byteArray[i]) < 192) {
               charArray[value19++] = (char)value21;
            } else if (value21 < 224) {
               char ch = (char)((char)(value21 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value19++] = ch;
            } else if (i < value20 - 2) {
               char ch2 = (char)((char)(value21 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value19++] = ch2;
            }
         }

         return new String(charArray, 0, value19);
      }
   }

   private enum ModeKind3 {
      OPEN,
      WAIT_OPEN,
      ACTION,
      DROP_JUNK,
      CLOSE;

      private static String a(byte[] byteArray) {
         int value19 = 0;
         int value20;
         char[] charArray = new char[value20 = byteArray.length];

         for (int i = 0; i < value20; i++) {
            int value21;
            if ((value21 = 255 & byteArray[i]) < 192) {
               charArray[value19++] = (char)value21;
            } else if (value21 < 224) {
               char ch = (char)((char)(value21 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value19++] = ch;
            } else if (i < value20 - 2) {
               char ch2 = (char)((char)(value21 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value19++] = ch2;
            }
         }

         return new String(charArray, 0, value19);
      }
   }
}
