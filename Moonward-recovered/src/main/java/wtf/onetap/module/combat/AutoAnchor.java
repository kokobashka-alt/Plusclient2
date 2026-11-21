package wtf.onetap.module.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.crystal.CrystalInteraction;
import wtf.onetap.combat.crystal.CrystalMining;
import wtf.onetap.combat.crystal.CrystalPlacer;
import wtf.onetap.combat.crystal.CrystalTargeting;
import wtf.onetap.combat.crystal.HotbarSwitcher;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.ContinueAttackEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.PreMotionEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.mixin.player.MultiPlayerGameModeAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "AutoAnchor", description = "Ставит, заряжает и взрывает якорь возрождения в выгодных условиях", category = Category.COMBAT)
public class AutoAnchor extends Module {
   private final FloatSetting placeRange = new FloatSetting("Дальность установки", 4.5F, 3.0F, 6.0F, 0.1F).build();
   private final FloatSetting minEnemyDamage = new FloatSetting("Мин. урон врагу", 6.0F, 1.0F, 10.0F, 0.5F).build();
   private final FloatSetting maxSelfDamage = new FloatSetting("Макс. урон себе", 8.0F, 0.0F, 10.0F, 0.5F).build();
   private final BooleanSetting autoDigUnder = new BooleanSetting("Авто подкоп", true).build();
   private final BooleanSetting dontBlowResources = new BooleanSetting("Не взрывать ресурсы", true).build();
   private final BooleanSetting renderPosition = new BooleanSetting("Рендер позиции", true).build();
   private final HotbarSwitcher hotbarSwitcher = new HotbarSwitcher();
   private final Map<BlockPos, Integer> blockPosMap = new HashMap<>();
   private ClientLevel clientLevel;
   private int value = -1;
   private List<ItemEntity> itemEntityList = List.of();
   private AutoAnchor.DataRecord4 c9;
   private BlockPos blockPos;
   private int value2;
   private boolean flag;
   private BlockPos blockPos2;
   private int value3;
   private BlockPos blockPos3;
   private AutoAnchor.DataRecord c4;
   private Rotations rotations;
   private Rotations rotations2;
   private int value4 = -1;
   private int value5;
   private static String text;

   public boolean m_6_boolean() {
      return this.isEnabled() && (this.c9 != null || this.blockPos2 != null || this.blockPos != null && (this.flag || CrystalInteraction.m_5_boolean(this.blockPos)));
   }

   public AutoAnchor.DataRecord m_5_C4() {
      return this.isEnabled() ? this.c4 : null;
   }

   @Override
   public void onDisable() {
      this.m_3_void();
   }

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      this.m_2_void();
      this.tickRotation();
      if (this.c9 == null) {
         this.hotbarSwitcher.m_5_void();
      }
   }

   private void m_2_void() {
      this.c4 = null;
      if (this.clientLevel != MinecraftAccess.MINECRAFT.level || MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.tickCount < this.value) {
         this.m_3_void();
         this.clientLevel = MinecraftAccess.MINECRAFT.level;
      }

      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null && !MinecraftAccess.MINECRAFT.player.isSpectator()) {
         int value6 = MinecraftAccess.MINECRAFT.player.tickCount;
         this.value = value6;
         this.blockPosMap.values().removeIf(integer -> value6 >= integer);
         var playerList = CrystalAura.getNearbyPlayers(8.0F);
         if (!this.isAutoWebActive() && !playerList.isEmpty() && !MinecraftAccess.MINECRAFT.player.isUsingItem() && !this.isInNether()) {
            if (this.c9 != null) {
               if (value6 - this.c9.m_2_m_int() <= CrystalInteraction.getTimeoutTicks()) {
                  this.m_6_o_void();
                  return;
               }

               this.m_5_void(20);
            }

            if (this.blockPos == null || !this.m_6_boolean(value6)) {
               if (this.blockPos2 != null) {
                  this.m_2_void(value6);
               }

               if (InventoryUtil.findHotbarSlot(Items.GLOWSTONE) >= 0 && !MinecraftAccess.MINECRAFT.player.isSecondaryUseActive()) {
                  Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
                  float floatValue = this.getEffectiveHealth();
                  var playerList2 = CrystalAura.getEnemies(18.0F);
                  var c5List = m_9(playerList, CrystalInteraction.getPing() / 2 + 1);
                  this.itemEntityList = this.dontBlowResources.getValue() ? LegacyCrystalAura.getNearbyItems() : List.of();
                  if (this.blockPos != null) {
                     AutoAnchor.DataRecord3 c8 = this.m_1(new AutoAnchor.DataRecord2(this.blockPos, AutoAnchor.ModeKind.DETONATE, 0.0F), c5List, floatValue, playerList2);
                     if (c8 != null) {
                        CrystalInteraction.DataRecord c2 = this.m_6(AutoAnchor.ModeKind.DETONATE, this.blockPos, vec3);
                        this.m_4(AutoAnchor.ModeKind.DETONATE, this.blockPos, c2 != null ? c2 : this.m_2(this.blockPos, vec3));
                        return;
                     }

                     if (value6 - this.value2 <= CrystalInteraction.getPing() + 10) {
                        return;
                     }

                     this.blockPos = null;
                  }

                  boolean flag2 = InventoryUtil.findHotbarSlot(Items.RESPAWN_ANCHOR) >= 0;
                  AutoAnchor.DataRecord3 c82 = this.m_3(m_9(playerList, CrystalInteraction.getPing() + 2), c5List, vec3, floatValue, playerList2, flag2);
                  if (c82 == null) {
                     this.stopMining();
                     this.hotbarSwitcher.m_8_void();
                  } else {
                     this.c4 = new AutoAnchor.DataRecord(c82.m_61().value, c82.m_8_b());
                     if (this.m_7(this.c4)) {
                        this.stopMining();
                        this.hotbarSwitcher.m_8_void();
                        this.reset();
                     } else {
                        BlockPos blockPos4 = c82.m_2p_BlockPos();
                        this.blockPos3 = blockPos4;
                        if (c82.m_61() != AutoAnchor.ModeKind.DIG) {
                           this.stopMining();
                        } else if (!blockPos4.equals(this.blockPos2)) {
                           this.stopMining();
                           this.blockPos2 = blockPos4;
                           this.value3 = value6;
                        }

                        this.m_4(c82.m_61(), blockPos4, this.m_6(c82.m_61(), blockPos4, vec3));
                     }
                  }
               } else {
                  this.stopMining();
                  this.hotbarSwitcher.m_8_void();
               }
            }
         } else {
            this.m_7_void();
            this.blockPos3 = null;
         }
      } else {
         this.m_3_void();
      }
   }

   private boolean m_6_boolean(int value6) {
      if (CrystalInteraction.m_5_boolean(this.blockPos)) {
         if (!this.flag) {
            return false;
         }

         if (value6 - this.value2 <= CrystalInteraction.getTimeoutTicks()) {
            return true;
         }

         this.blockPosMap.put(this.blockPos, value6 + 20);
         this.blockPos = null;
         this.flag = false;
         return false;
      } else {
         if (!MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos).is(Blocks.RESPAWN_ANCHOR)) {
            if (!this.flag) {
               this.blockPosMap.put(this.blockPos, value6 + 20);
            }

            this.blockPos = null;
         }

         this.flag = false;
         return false;
      }
   }

   private void m_2_void(int value6) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos2);
      if (blockState.canBeReplaced()) {
         this.blockPos3 = this.blockPos2;
         this.stopMining();
      } else if (!CrystalPlacer.m_8_boolean(blockState) || value6 - this.value3 > CrystalMining.m_1_float(blockState, this.blockPos2) * 1.5F + CrystalInteraction.getTimeoutTicks()) {
         this.blockPosMap.put(this.blockPos2, value6 + 40);
         this.stopMining();
      }
   }

   private AutoAnchor.DataRecord3 m_3(List<CrystalPlacer.DataRecord4> c5List, List<CrystalPlacer.DataRecord4> c5List2, Vec3 vec3, float floatValue, List<Player> playerList, boolean flag2) {
      double doubleValue = CrystalInteraction.clampInteractionRange(this.placeRange.getFloat());
      HashSet set = new HashSet();
      ArrayList list = new ArrayList();
      ArrayList list2 = new ArrayList();
      ArrayList list3 = new ArrayList();

      for (CrystalPlacer.DataRecord4 c5 : c5List) {
         BlockPos blockPos4 = BlockPos.containing(c5.m_5_l());
         boolean flag3 = c5.m_1n().onGround() && blockPos4.equals(c5.m_1n().blockPosition());

         for (BlockPos blockPos5 : BlockPos.betweenClosed(blockPos4.offset(-3, -1, -3), blockPos4.offset(3, 3, 3))) {
            BlockPos blockPos6 = blockPos5.immutable();
            if (set.add(blockPos6) && !this.blockPosMap.containsKey(blockPos6) && !(CrystalPlacer.distanceSqToBlock(vec3, blockPos6) > doubleValue * doubleValue)) {
               Vec3 vec32 = Vec3.atCenterOf(blockPos6);
               float floatValue2 = 0.0F;

               for (CrystalPlacer.DataRecord4 c52 : c5List) {
                  floatValue2 = Math.max(floatValue2, CrystalMining.m_9(vec32, c52.m_1n(), c52.m_3_i(), 10.0));
               }

               if (!(floatValue2 < this.minEnemyDamage.getFloat() * 0.5F)) {
                  AutoAnchor.ModeKind c3 = this.m_9(blockPos6, blockPos4, vec3, c5List, flag2, flag3);
                  if (c3 != null) {
                     (c3 == AutoAnchor.ModeKind.DETONATE ? list : (c3 == AutoAnchor.ModeKind.PLACE ? list2 : list3)).add(new AutoAnchor.DataRecord2(blockPos6, c3, floatValue2));
                  }
               }
            }
         }
      }

      CrystalMining.onTickStart();

      try {
         AutoAnchor.DataRecord3 c8 = this.m_6(list, c5List2, floatValue, playerList);
         if (c8 == null) {
            c8 = this.m_6(list2, c5List, floatValue, playerList);
         }

         return c8 != null ? c8 : this.m_6(list3, c5List, floatValue, playerList);
      } finally {
         CrystalMining.tickCooldown();
      }
   }

   private AutoAnchor.ModeKind m_9(BlockPos blockPos4, BlockPos blockPos5, Vec3 vec3, List<CrystalPlacer.DataRecord4> c5List, boolean flag2, boolean flag3) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos4);
      if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
         return this.m_6(AutoAnchor.ModeKind.DETONATE, blockPos4, vec3) != null ? AutoAnchor.ModeKind.DETONATE : null;
      }

      if (!flag2) {
         return null;
      }

      AABB aABB = new AABB(blockPos4);

      for (CrystalPlacer.DataRecord4 c5 : c5List) {
         if (c5.m_3_i().intersects(aABB) || c5.m_1n().getBoundingBox().intersects(aABB)) {
            return null;
         }
      }

      if (blockState.canBeReplaced()) {
         return !CrystalPlacer.isBlocked(blockPos4) && this.m_6(AutoAnchor.ModeKind.PLACE, blockPos4, vec3) != null ? AutoAnchor.ModeKind.PLACE : null;
      } else if ((Boolean)this.autoDigUnder.getValue()
         && flag3
         && blockPos4.getY() >= blockPos5.getY()
         && CrystalPlacer.m_8_boolean(blockState)
         && this.m_8_boolean(blockPos4)
         && !aABB.intersects(MinecraftAccess.MINECRAFT.player.getBoundingBox().expandTowards(0.0, -1.0, 0.0))) {
         return this.m_6(AutoAnchor.ModeKind.DIG, blockPos4, vec3) != null ? AutoAnchor.ModeKind.DIG : null;
      } else {
         return null;
      }
   }

   private AutoAnchor.DataRecord3 m_6(List<AutoAnchor.DataRecord2> c5List, List<CrystalPlacer.DataRecord4> c5List2, float floatValue, List<Player> playerList) {
      c5List.sort(Comparator.comparingDouble(c5 -> -c5.m_5z()));
      AutoAnchor.DataRecord3 c8 = null;
      int value6 = 0;

      for (AutoAnchor.DataRecord2 c52 : c5List) {
         if (c8 != null && c52.m_5z() + 0.5F <= c8.m_8a() || value6++ >= 200) {
            break;
         }

         AutoAnchor.DataRecord3 c82 = this.m_1(c52, c5List2, floatValue, playerList);
         if (c82 != null && (c8 == null || c82.m_8a() > c8.m_8a())) {
            c8 = c82;
         }
      }

      return c8;
   }

   private AutoAnchor.DataRecord3 m_1(AutoAnchor.DataRecord2 c5, List<CrystalPlacer.DataRecord4> c5List, float floatValue, List<Player> playerList) {
      BlockPos blockPos4 = c5.m_9_q();
      Vec3 vec3 = Vec3.atCenterOf(blockPos4);
      if (this.hasNearbyItems(vec3)) {
         return null;
      }

      List list = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos4).getCollisionShape(MinecraftAccess.MINECRAFT.level, blockPos4).isEmpty() ? List.of() : List.of(blockPos4);
      float floatValue2 = CrystalMining.m_2(vec3, MinecraftAccess.MINECRAFT.player, MinecraftAccess.MINECRAFT.player.getBoundingBox(), null, list, 10.0);
      if (!(floatValue2 >= floatValue) && !(floatValue2 > this.maxSelfDamage.getFloat())) {
         float floatValue3 = this.m_5(vec3, c5List, list, c5.m_0j() == AutoAnchor.ModeKind.DETONATE);
         if (floatValue3 < 0.0F) {
            return null;
         }

         float floatValue4 = this.m_9(vec3, playerList, list);
         if (floatValue4 < 0.0F) {
            return null;
         }

         float floatValue5 = floatValue3
            - floatValue2 * 2.0F
            - floatValue4 * 2.0F
            + (blockPos4.equals(this.blockPos3) ? 0.5F : 0.0F)
            - (c5.m_0j() == AutoAnchor.ModeKind.DIG ? CrystalMining.m_1_float(MinecraftAccess.MINECRAFT.level.getBlockState(blockPos4), blockPos4) / 20.0F : 0.0F);
         return new AutoAnchor.DataRecord3(blockPos4, c5.m_0j(), floatValue3, floatValue5);
      } else {
         return null;
      }
   }

   private float m_5(Vec3 vec3, List<CrystalPlacer.DataRecord4> c5List, List<BlockPos> blockPosList, boolean flag2) {
      float floatValue = -1.0F;

      for (CrystalPlacer.DataRecord4 c5 : c5List) {
         Player player = c5.m_1n();
         float floatValue2 = CrystalMining.m_2(vec3, player, c5.m_3_i(), null, blockPosList, 10.0);
         if (flag2 && !c5.m_5_h() && c5.m_3_i() != player.getBoundingBox()) {
            floatValue2 = Math.min(floatValue2, CrystalMining.m_2(vec3, player, player.getBoundingBox(), null, blockPosList, 10.0));
         }

         float floatValue3 = player.getHealth() + player.getAbsorptionAmount();
         if (floatValue2 >= this.minEnemyDamage.getFloat() || floatValue2 >= this.minEnemyDamage.getFloat() * 0.5F && floatValue2 >= floatValue3) {
            floatValue = Math.max(floatValue, floatValue2);
         }
      }

      return floatValue;
   }

   private float m_9(Vec3 vec3, List<Player> playerList, List<BlockPos> blockPosList) {
      float floatValue = 0.0F;

      for (Player player : playerList) {
         float floatValue2 = CrystalMining.m_2(vec3, player, player.getBoundingBox(), null, blockPosList, 10.0);
         if (floatValue2 > this.maxSelfDamage.getFloat() || floatValue2 > 0.0F && floatValue2 >= player.getHealth() + player.getAbsorptionAmount() - 1.0F) {
            return -1.0F;
         }

         floatValue = Math.max(floatValue, floatValue2);
      }

      return floatValue;
   }

   private boolean m_8_boolean(BlockPos blockPos4) {
      for (Direction direction : Direction.values()) {
         BlockPos blockPos5 = blockPos4.relative(direction);
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos5);
         if (!blockState.canBeReplaced() && !blockState.getShape(MinecraftAccess.MINECRAFT.level, blockPos5).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private static List<CrystalPlacer.DataRecord4> m_9(List<Player> playerList, int value6) {
      ArrayList list = new ArrayList();

      for (int i = 0; i < Math.min(playerList.size(), 4); i++) {
         Player player = (Player)playerList.get(i);
         AABB aABB = player.getBoundingBox();
         AABB aABB2 = CrystalTargeting.isInsideBlocks(aABB) ? null : CrystalTargeting.m_3(player, value6);
         if (aABB2 != null && aABB2.getXsize() >= 0.1 && aABB2.getYsize() >= 0.1 && aABB2.getZsize() >= 0.1) {
            aABB = aABB2;
         }

         list.add(new CrystalPlacer.DataRecord4(player, aABB, !player.onGround() || CrystalTargeting.isRecentlyTracked(player)));
      }

      return list;
   }

   private boolean m_7(AutoAnchor.DataRecord c42) {
      CrystalAura crystalAura = Onetap.MODULE_MANAGER.get(CrystalAura.class);
      return crystalAura != null && crystalAura.isEnabled() && !c42.m_1(crystalAura.m_3_C4());
   }

   private boolean isAutoWebActive() {
      AutoWeb autoWeb = Onetap.MODULE_MANAGER.get(AutoWeb.class);
      return autoWeb != null && autoWeb.isFlag();
   }

   private boolean isInNether() {
      return MinecraftAccess.MINECRAFT.level.dimension() == Level.NETHER;
   }

   private CrystalInteraction.DataRecord m_2(BlockPos blockPos4, Vec3 vec3) {
      Rotations rotations3 = Onetap.ROTATION_MODES.toRotations();
      Vec3 vec32 = vec3.add(Vec3.directionFromRotation(rotations3.getFloatValue2(), rotations3.getFloatValue()).scale(CrystalInteraction.clampInteractionRange(this.placeRange.getFloat())));
      BlockHitResult blockHitResult = Shapes.block().clip(vec3, vec32, blockPos4);
      return blockHitResult == null ? null : new CrystalInteraction.DataRecord(blockPos4, blockHitResult.getDirection(), blockHitResult.getLocation(), blockHitResult.isInside());
   }

   private CrystalInteraction.DataRecord m_6(AutoAnchor.ModeKind c3, BlockPos blockPos4, Vec3 vec3) {
      return c3 == AutoAnchor.ModeKind.PLACE ? CrystalInteraction.m_6(blockPos4, vec3, this.placeRange.getFloat(), false) : CrystalInteraction.m_9(blockPos4, vec3, this.placeRange.getFloat());
   }

   @EventTarget(priority = EventPriority.LOWEST)
   private void onPreMotion(PreMotionEvent event) {
      if (this.c9 != null && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null && !MinecraftAccess.MINECRAFT.player.isUsingItem()) {
         if (this.isAutoWebActive()) {
            this.m_7_void();
         } else {
            if (this.m_4_boolean()) {
               this.m_6_void(MinecraftAccess.MINECRAFT.player.position().add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight(), 0.0));
            }
         }
      }
   }

   private void m_6_void(Vec3 vec3) {
      AutoAnchor.DataRecord4 c92 = this.c9;
      Rotations rotations3 = Onetap.ROTATION_MODES.toRotations();
      BlockHitResult blockHitResult = c92.m_4w() == AutoAnchor.ModeKind.PLACE && CrystalInteraction.m_5_boolean(c92.m_0_p().m_5b())
         ? null
         : CrystalInteraction.m_2(c92.m_0_p(), this.placeRange.getFloat(), vec3, rotations3.getFloatValue(), rotations3.getFloatValue2());
      if (blockHitResult == null) {
         if (c92.m_4w() != AutoAnchor.ModeKind.DIG) {
            this.c9 = null;
         }
      } else {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(c92.m_3m());
         int value6 = blockState.is(Blocks.RESPAWN_ANCHOR) ? blockState.getValue(RespawnAnchorBlock.CHARGE) : -1;
         int value7 = value6 > 0 && !MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.GLOWSTONE) ? this.m_0_int() : -1;

         int value8 = switch (c92.m_4w().ordinal()) {
            case 0 -> InventoryUtil.findHotbarSlot(Items.RESPAWN_ANCHOR);
            case 1 -> value7 >= 0 ? value7 : InventoryUtil.findHotbarSlot(Items.GLOWSTONE);
            case 2 -> {
               int value9 = CrystalMining.m_5(MinecraftAccess.MINECRAFT.level.getBlockState(c92.m_3m()));
               yield value9 >= 0 ? value9 : MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
            }
            default -> throw new MatchException(null, null);
         };
         if (value8 < 0) {
            this.m_5_void(10);
         } else {
            switch (c92.m_4w().ordinal()) {
               case 0:
                  if (CrystalPlacer.isBlocked(c92.m_3m()) || !CrystalInteraction.m_8(c92.m_3m(), blockHitResult, MinecraftAccess.MINECRAFT.player.getInventory().getItem(value8))) {
                     this.m_5_void(10);
                     return;
                  }

                  this.c9 = null;
                  this.hotbarSwitcher.m_2_void(value8);

                  try {
                     this.hotbarSwitcher.markTick();
                     InteractionResult interactionResult = MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
                     LegacyCrystalAura.m_0(rotations3);
                     if (interactionResult instanceof Success success && success.wasItemInteraction() && MinecraftAccess.MINECRAFT.level.getBlockState(c92.m_3m()).is(Blocks.RESPAWN_ANCHOR)) {
                        MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
                        this.m_8(c92.m_3m(), false);
                        break;
                     }

                     this.blockPosMap.put(c92.m_3m(), MinecraftAccess.MINECRAFT.player.tickCount + 12);
                     break;
                  } finally {
                     this.hotbarSwitcher.m_8_void();
                  }
               case 1:
                  if (value6 < 0 || MinecraftAccess.MINECRAFT.player.isSecondaryUseActive()) {
                     this.m_5_void(10);
                     return;
                  }

                  boolean flag2 = value7 >= 0 || value6 == 4;
                  this.hotbarSwitcher.m_2_void(value8);

                  try {
                     this.hotbarSwitcher.markTick();
                     MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
                     LegacyCrystalAura.m_0(rotations3);
                     if (flag2) {
                        this.c9 = null;
                        this.m_8(c92.m_3m(), true);
                     } else {
                        MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
                     }
                     break;
                  } finally {
                     this.hotbarSwitcher.m_8_void();
                  }
               case 2:
                  if (blockState.canBeReplaced()) {
                     this.c9 = null;
                     return;
                  }

                  if (!(Boolean)this.autoDigUnder.getValue() || !CrystalPlacer.m_8_boolean(blockState)) {
                     this.m_5_void(40);
                     return;
                  }

                  this.c9 = null;
                  this.hotbarSwitcher.m_2_void(value8);
                  this.breakBlock(c92.m_3m(), blockHitResult);
            }
         }
      }
   }

   private int m_0_int() {
      int value6 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      if (this.m_0_boolean(value6)) {
         return value6;
      }

      for (int i = 0; i < 9; i++) {
         if (this.m_0_boolean(i)) {
            return i;
         }
      }

      return -1;
   }

   private boolean m_0_boolean(int value6) {
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value6);
      return itemStack.isEmpty() || !(itemStack.getItem() instanceof BlockItem);
   }

   private void m_8(BlockPos blockPos4, boolean flag2) {
      this.blockPos = blockPos4;
      this.value2 = MinecraftAccess.MINECRAFT.player.tickCount;
      this.flag = flag2;
      this.blockPos3 = null;
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.rotations != null) {
         LegacyCrystalAura.fixMovement(event);
      }
   }

   @EventTarget
   private void onContinueAttack(ContinueAttackEvent event) {
      if (this.blockPos2 != null) {
         event.cancel();
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && (Boolean)this.renderPosition.getValue()) {
         BlockPos blockPos4 = this.blockPos != null ? this.blockPos : (this.c9 != null ? this.c9.m_3m() : this.blockPos2);
         if (blockPos4 != null) {
            Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
            CrystalAura.drawBox(matrix4f, event.getVec3(), CrystalAura.getHighlightBox(blockPos4, 1.0F), blockPos4.equals(this.blockPos2) ? -51401 : -29670, 0.39215687F);
         }
      }
   }

   private void breakBlock(BlockPos blockPos4, BlockHitResult blockHitResult) {
      MultiPlayerGameModeAccessor multiPlayerGameModeAccessor = (MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode;
      if (multiPlayerGameModeAccessor.onetap$getDestroyDelay() > 0) {
         multiPlayerGameModeAccessor.onetap$setDestroyDelay(0);
      }

      if (MinecraftAccess.MINECRAFT.gameMode.continueDestroyBlock(blockPos4, blockHitResult.getDirection())) {
         MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
      }
   }

   private void stopMining() {
      if (this.blockPos2 != null) {
         if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
            MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
         }

         if (this.c9 != null && this.c9.m_4w() == AutoAnchor.ModeKind.DIG) {
            this.c9 = null;
         }

         this.blockPos2 = null;
         this.hotbarSwitcher.m_8_void();
      }
   }

   private void m_4(AutoAnchor.ModeKind c3, BlockPos blockPos4, CrystalInteraction.DataRecord c2) {
      if (c2 == null) {
         this.blockPosMap.put(blockPos4, MinecraftAccess.MINECRAFT.player.tickCount + 6);
         if (c3 == AutoAnchor.ModeKind.DIG) {
            this.stopMining();
         }
      } else {
         this.c9 = new AutoAnchor.DataRecord4(c3, blockPos4, c2, MinecraftAccess.MINECRAFT.player.tickCount);
         this.m_6_o_void();
      }
   }

   private void m_6_o_void() {
      if (this.c9 != null && !this.m_4_boolean(this.c9.m_0_p().m_7a())) {
         this.hotbarSwitcher.m_8_void();
      }
   }

   private boolean m_4_boolean(Vec3 vec3) {
      this.rotations = new Rotations(MinecraftAccess.MINECRAFT.player.getEyePosition(), vec3);
      this.value5 = 0;
      this.m_6s_void();
      return this.m_4_boolean();
   }

   private void tickRotation() {
      if (this.rotations != null && MinecraftAccess.MINECRAFT.player != null && this.value4 != MinecraftAccess.MINECRAFT.player.tickCount) {
         if (++this.value5 > 10) {
            this.reset();
         } else {
            this.m_6s_void();
         }
      }
   }

   private void m_6s_void() {
      Rotations rotations3 = CrystalAura.smoothRotation(CrystalAura.smoothRotation(Onetap.ROTATION_MODES.toRotations(), this.rotations), this.rotations);
      if (this.c9 != null && this.c9.m_4w() != AutoAnchor.ModeKind.DIG && LegacyCrystalAura.m_8_boolean(rotations3)) {
         rotations3 = new Rotations(rotations3.getFloatValue() + CrystalAura.getMouseSensitivityFactor(), rotations3.getFloatValue2());
      }

      Onetap.ROTATION_MODES.rotate(rotations3, false, 15);
      this.rotations2 = rotations3;
      this.value4 = MinecraftAccess.MINECRAFT.player.tickCount;
   }

   private boolean m_4_boolean() {
      if (this.rotations2 != null && MinecraftAccess.MINECRAFT.player != null && this.value4 == MinecraftAccess.MINECRAFT.player.tickCount) {
         Rotations rotations3 = Onetap.ROTATION_MODES.toRotations();
         return rotations3.getFloatValue() == this.rotations2.getFloatValue() && rotations3.getFloatValue2() == this.rotations2.getFloatValue2();
      } else {
         return false;
      }
   }

   private void reset() {
      this.rotations = null;
      this.rotations2 = null;
      this.value4 = -1;
      this.value5 = 0;
   }

   private void m_5_void(int value6) {
      if (this.c9 != null) {
         this.blockPosMap.put(this.c9.m_3m(), MinecraftAccess.MINECRAFT.player.tickCount + value6);
         if (this.c9.m_4w() == AutoAnchor.ModeKind.DIG) {
            this.stopMining();
         }

         this.c9 = null;
         this.hotbarSwitcher.m_8_void();
         this.reset();
      }
   }

   private void m_7_void() {
      this.c9 = null;
      this.blockPos = null;
      this.flag = false;
      this.stopMining();
      this.hotbarSwitcher.m_8_void();
      this.reset();
   }

   private boolean hasNearbyItems(Vec3 vec3) {
      return !this.itemEntityList.isEmpty() && LegacyCrystalAura.isItemNearby(vec3, this.itemEntityList);
   }

   private float getEffectiveHealth() {
      return Math.max(0.0F, MinecraftAccess.MINECRAFT.player.getHealth() + MinecraftAccess.MINECRAFT.player.getAbsorptionAmount() - 1.0F);
   }

   private void m_3_void() {
      this.m_7_void();
      this.blockPosMap.clear();
      this.blockPos3 = null;
      this.itemEntityList = List.of();
      this.c4 = null;
   }

   public static void m_2_void(String text2) {
      text = text2;
   }

   public static String m_1_String() {
      return text;
   }

   static {
      m_2_void("ZAEiNb");
   }

   private enum ModeKind {
      PLACE(1),
      DETONATE(0),
      DIG(3);

      private final int value;

      ModeKind(int value6) {
         this.value = value6;
      }

      private static String a(byte[] byteArray) {
         int value6 = 0;
         int value7;
         char[] charArray = new char[value7 = byteArray.length];

         for (int i = 0; i < value7; i++) {
            int value8;
            if ((value8 = 255 & byteArray[i]) < 192) {
               charArray[value6++] = (char)value8;
            } else if (value8 < 224) {
               char ch = (char)((char)(value8 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value6++] = ch;
            } else if (i < value7 - 2) {
               char ch2 = (char)((char)(value8 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value6++] = ch2;
            }
         }

         return new String(charArray, 0, value6);
      }
   }

   public record DataRecord(int f_0w, float f_0n) {
      public static final AutoAnchor.DataRecord f_1 = new AutoAnchor.DataRecord(0, Float.intBitsToFloat((int)-7494523521981218817L));

      public static AutoAnchor.DataRecord m_2(CrystalPlacer.DataRecord2 c2) {
         return new AutoAnchor.DataRecord(c2.m_72_BlockPos() != null ? AutoAnchor.ModeKind.DIG.value : (c2.m_60() ? 2 : 1), c2.m_1_u_float());
      }

      public boolean m_1(AutoAnchor.DataRecord c42) {
         if (c42 == null) {
            return true;
         }

         boolean flag2 = this.f_0w == AutoAnchor.ModeKind.DIG.value;
         return flag2 != (c42.f_0w == AutoAnchor.ModeKind.DIG.value) ? !flag2 : this.f_0n > c42.f_0n || this.f_0n == c42.f_0n && this.f_0w < c42.f_0w;
      }

      public int m_0w() {
         return this.f_0w;
      }

      public float m_0n() {
         return this.f_0n;
      }
   }

   private record DataRecord2(BlockPos f_9_q, AutoAnchor.ModeKind f_0j, float f_5z) {
      public BlockPos m_9_q() {
         return this.f_9_q;
      }

      public AutoAnchor.ModeKind m_0j() {
         return this.f_0j;
      }

      public float m_5z() {
         return this.f_5z;
      }
   }

   private record DataRecord3(BlockPos f_2p, AutoAnchor.ModeKind f_61, float f_8_b, float f_8a) {
      public BlockPos m_2p_BlockPos() {
         return this.f_2p;
      }

      public AutoAnchor.ModeKind m_61() {
         return this.f_61;
      }

      public float m_8_b() {
         return this.f_8_b;
      }

      public float m_8a() {
         return this.f_8a;
      }
   }

   private record DataRecord4(AutoAnchor.ModeKind f_4w, BlockPos f_3m, CrystalInteraction.DataRecord f_0_p, int f_2_m) {
      public AutoAnchor.ModeKind m_4w() {
         return this.f_4w;
      }

      public BlockPos m_3m() {
         return this.f_3m;
      }

      public CrystalInteraction.DataRecord m_0_p() {
         return this.f_0_p;
      }

      public int m_2_m_int() {
         return this.f_2_m;
      }
   }
}
