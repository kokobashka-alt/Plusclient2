package wtf.onetap.module.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.mixin.player.MultiPlayerGameModeAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.C9_q;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.LatencyStats;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.TickExecutor;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "CrystalAura", description = "Автоматически ставит обсидиан/кристал в выгодных условиях", category = Category.COMBAT)
public class CrystalAura extends Module {
   private static final LatencyStats.StateHolder C0 = LatencyStats.getOrCreate("CrystalAura targets");
   private static final LatencyStats.StateHolder C02 = LatencyStats.getOrCreate("CrystalAura boxes");
   private static final LatencyStats.StateHolder C03 = LatencyStats.getOrCreate("CrystalAura aim");
   private static final LatencyStats.StateHolder C04 = LatencyStats.getOrCreate("CrystalAura execute");
   private static final LatencyStats.StateHolder C05 = LatencyStats.getOrCreate("CrystalAura async");
   private static final LatencyStats.StateHolder C06 = LatencyStats.getOrCreate("CrystalAura hold");
   private static final LatencyStats.StateHolder C07 = LatencyStats.getOrCreate("CrystalAura deferred");
   private static final double[] DOUBLE_VALUE_ARRAY = new double[32];
   private final FloatSetting placeRange = new FloatSetting("Дальность установки", 4.5F, 3.0F, 6.0F, 0.1F).build();
   private final FloatSetting explosionRange = new FloatSetting("Дальность взрыва", 3.0F, 1.0F, 6.0F, 0.1F).build();
   private final FloatSetting minEnemyDamage = new FloatSetting("Мин. урон врагу", 6.0F, 1.0F, 20.0F, 0.5F).build();
   private final FloatSetting maxSelfDamage = new FloatSetting("Макс. урон себе", 8.0F, 0.0F, 20.0F, 0.5F).build();
   private final FloatSetting predictTickCount = new FloatSetting("Предикт тиков", 2.0F, 0.0F, 4.0F, 1.0F).build();
   private final BooleanSetting autoDigUnder = new BooleanSetting("Авто подкоп", true).build();
   private final BooleanSetting renderPosition = new BooleanSetting("Рендер позиции", true).build();
   private final BooleanSetting dontBlowResources = new BooleanSetting("Не взрывать ресурсы", true).build();
   private final BooleanSetting crystalDelay = new BooleanSetting("Задержка кристаллов", true).build();
   private final BooleanSetting autoWeb = new BooleanSetting("Авто паутина", false).build();
   private final TimeHelper timeHelper = new TimeHelper();
   private final HotbarSwitcher hotbarSwitcher = new HotbarSwitcher();
   private final Map<BlockPos, Integer> blockPosMap = new HashMap<>();
   private final Map<BlockPos, Integer> blockPosMap2 = new HashMap<>();
   private final Map<BlockPos, Integer> blockPosMap3 = new HashMap<>();
   private final Map<BlockPos, Integer> blockPosMap4 = new HashMap<>();
   private final Map<Integer, Integer> integerMap = new HashMap<>();
   private final List<CrystalAura.DataRecord2> c6List = new ArrayList<>();
   private ClientLevel clientLevel;
   private int value = -1;
   private BlockPos blockPos;
   private float floatValue;
   private int value2;
   private BlockPos blockPos2;
   private int value3;
   private int value4 = -1;
   private BlockPos blockPos3;
   private BlockPos blockPos4;
   private BlockPos blockPos5;
   private BlockPos blockPos6;
   private int value5;
   private int value6 = -2;
   private BlockPos blockPos7;
   private BlockPos blockPos8;
   private int value7;
   private int value8;
   private AABB aABB;
   private List<ItemEntity> itemEntityList = List.of();
   private int value9;
   private int value10;
   private int value11;
   private final Map<Integer, Integer> integerMap2 = new HashMap<>();
   private final Map<Integer, CrystalAura.DataRecord> integerMap3 = new HashMap<>();
   private CrystalAura.DataRecord3 c9;
   private Vec3 vec3;
   private Rotations rotations;
   private Rotations rotations2;
   private int value12 = -1;
   private int value13;
   private BlockPos blockPos9;
   private float floatValue2;
   private long longValue;
   private AutoAnchor.DataRecord c4;
   private int value14 = -1;
   private List<Player> playerList = List.of();
   private List<Player> playerList2 = List.of();
   private int value15 = -1;
   private int value16 = Integer.MIN_VALUE;
   private List<CrystalPlacer.DataRecord4> c5List;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private final Queue<CrystalAura.DataRecord2> queue = new ConcurrentLinkedQueue<>();
   private float floatValue3 = Float.NaN;
   private float floatValue4;
   private float floatValue5;
   private int value17 = -1;
   private float floatValue6;
   private static String text;

   public boolean isBusy() {
      return this.isEnabled() && (this.c9 != null || this.rotations != null);
   }

   public AutoAnchor.DataRecord m_3_C4() {
      return this.isEnabled() ? this.c4 : null;
   }

   @Override
   public void onEnable() {
      this.flag = false;
   }

   @Override
   public void onDisable() {
      TickExecutor.m_0_boolean();
      this.flag = false;
      this.resetAll();
   }

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      if (this.flag) {
         TickExecutor.m_0_boolean();
         this.flag = false;
      }

      long longValue2 = System.nanoTime();
      this.tickRenderQueue();
      C07.recordSince(longValue2);
      long longValue3 = System.nanoTime();
      this.tickRotation();
      C06.recordSince(longValue3);
      if (this.c9 == null) {
         this.hotbarSwitcher.m_5_void();
      }
   }

   public void scheduleAsync() {
      if (this.isEnabled() && !this.flag) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
            this.flag = true;
            TickExecutor.m_4(() -> {
               if (this.isEnabled()) {
                  long longValue2 = System.nanoTime();
                  CrystalPlacer.setDeadline(40000000L);
                  CrystalMining.onTickStart();

                  try {
                     this.m_6_n_void();
                  } catch (Throwable e) {
                     if (!this.flag2) {
                        this.flag2 = true;
                        Onetap.LOGGER.error("CrystalAura: the async search failed", e);
                     }
                  } finally {
                     CrystalMining.tickCooldown();
                     CrystalPlacer.setDeadline(0L);
                     C05.recordSince(longValue2);
                  }
               }
            });
         }
      }
   }

   private void tickRenderQueue() {
      this.hotbarSwitcher.m_3_void();
      if (this.flag3) {
         this.flag3 = false;
         this.c6List.clear();
      }

      if (!this.queue.isEmpty()) {
         CrystalAura.DataRecord2 c6;
         while ((c6 = this.queue.poll()) != null) {
            this.c6List.add(c6);
         }
      }
   }

   private void m_6_n_void() {
      this.c4 = null;
      if (this.clientLevel != MinecraftAccess.MINECRAFT.level || MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.tickCount < this.value) {
         this.resetAll();
         this.clientLevel = MinecraftAccess.MINECRAFT.level;
      }

      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null && !MinecraftAccess.MINECRAFT.player.isSpectator()) {
         this.value = MinecraftAccess.MINECRAFT.player.tickCount;
         this.vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         if (this.isOtherModuleBusy()) {
            this.cancelAndReset();
         } else {
            this.purgeExpired();
            var playerList3 = this.getPlayers();
            if (!playerList3.isEmpty() && !MinecraftAccess.MINECRAFT.player.isUsingItem()) {
               this.m_2(playerList3);
               this.m_6w_void();
               if (this.c9 != null && this.c9.m_4f() != CrystalAura.ModeKind.DIG) {
                  if (MinecraftAccess.MINECRAFT.player.tickCount - this.c9.m_2b() <= CrystalInteraction.getTimeoutTicks()) {
                     this.c4 = AutoAnchor.DataRecord.f_1;
                     this.m_6g_void();
                     return;
                  }

                  this.cancelCurrentAction();
               }

               Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getEyePosition();
               if (this.timeHelper.getElapsed() > 4L) {
                  this.m_0_void(vec32);
                  if (this.blockPos7 != null) {
                     BlockPos blockPos10 = this.blockPos7;
                     this.blockPos7 = null;
                     if (this.tryPlaceCrystal(blockPos10)) {
                        this.c4 = AutoAnchor.DataRecord.f_1;
                        this.blockPos3 = blockPos10;
                        return;
                     }
                  }

                  float floatValue7 = this.m_4_float();
                  var playerList4 = this.getFarPlayers();
                  this.itemEntityList = this.dontBlowResources.getValue() ? LegacyCrystalAura.getNearbyItems() : List.of();
                  if (this.blockPos5 != null && MinecraftAccess.MINECRAFT.player.tickCount - this.value9 > CrystalInteraction.getTimeoutTicks()) {
                     this.blockPos5 = null;
                  }

                  if (this.blockPos6 != null && (MinecraftAccess.MINECRAFT.player.tickCount - this.value5 > getPingDelay() || CrystalPlacer.m_8(this.blockPos6, this.integerMap.keySet()))) {
                     this.blockPos6 = null;
                  }

                  if (this.blockPos8 != null
                     && (
                        MinecraftAccess.MINECRAFT.player.tickCount - this.value7 > CrystalInteraction.getTimeoutTicks()
                           || !(
                              MinecraftAccess.MINECRAFT.level.getEntity(this.value8) instanceof Player player
                                 && !this.m_3_boolean(player.getBoundingBox())
                                 && (CrystalInteraction.m_5_boolean(this.blockPos8) || MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos8).is(Blocks.COBWEB))
                           )
                     )) {
                     this.blockPos8 = null;
                  }

                  Set set = this.integerMap.keySet();
                  if (!this.integerMap2.isEmpty()) {
                     HashSet set2 = new HashSet<>(this.integerMap.keySet());
                     this.integerMap2.forEach((integer, integer2) -> {
                        if (integer2 >= 2) {
                           set2.add(integer);
                        }
                     });
                     set = set2;
                  }

                  EndCrystal endCrystal = this.blockPos5 == null ? null : this.findCrystalAt(this.blockPos5.above());
                  if (endCrystal != null
                     && (
                        set.contains(endCrystal.getId())
                           || CrystalPlacer.m_4(endCrystal, vec32, this.getExplosionRange()) == null
                           || this.hasNearbyItems(endCrystal.position())
                           || CrystalMining.m_3(endCrystal.position(), MinecraftAccess.MINECRAFT.player) >= Math.min(floatValue7, this.maxSelfDamage.getFloat())
                     )) {
                     endCrystal = null;
                  }

                  if (endCrystal == null) {
                     endCrystal = CrystalPlacer.m_4(playerList3, vec32, this.getExplosionRange(), this.maxSelfDamage.getFloat(), this.m_9_float(), floatValue7, playerList4, set, this::hasNearbyItems);
                     if (endCrystal != null && !this.isDelayElapsed()) {
                        this.c4 = AutoAnchor.DataRecord.f_1;
                        this.stopMining();
                        this.hotbarSwitcher.m_8_void();
                        Vec3 vec33 = this.m_7(endCrystal, vec32);
                        if (vec33 != null) {
                           this.aimAt(vec33);
                        }

                        return;
                     }
                  }

                  if (endCrystal == null && this.blockPos == null) {
                     EndCrystal endCrystal2 = this.blockPos4 == null ? null : this.findCrystalAt(this.blockPos4.above());
                     if (endCrystal2 != null && playerList3.stream().anyMatch(player2 -> CrystalPlacer.collidesWithBlocks(player2.getBoundingBox()))) {
                        set.add(endCrystal2.getId());
                     }

                     endCrystal = CrystalPlacer.m_4(
                        playerList3, vec32, this.getExplosionRange(), Math.min(this.maxSelfDamage.getFloat(), this.m_3_float() * 0.5F), 0.0F, floatValue7, playerList4, set, this::hasNearbyItems
                     );
                  }

                  if (endCrystal == null || !this.m_7_boolean(endCrystal)) {
                     if (!this.tryPlaceWeb(playerList3, playerList4, vec32)) {
                        boolean flag4 = InventoryUtil.findHotbarSlot(Items.END_CRYSTAL) >= 0;
                        boolean flag5 = !this.blockPosMap3.isEmpty() || !this.blockPosMap4.isEmpty();
                        boolean flag6 = !flag5 && InventoryUtil.findHotbarSlot(Items.OBSIDIAN) >= 0;
                        boolean flag7 = flag4 && this.isDelayElapsed();
                        var c5List2 = this.m_2(playerList3, 0);
                        float floatValue8 = (float)CrystalInteraction.clampInteractionRange(this.placeRange.getFloat());
                        BlockPos blockPos11 = this.blockPos3 != null ? this.blockPos3 : (this.blockPos2 != null && MinecraftAccess.MINECRAFT.player.tickCount - this.value3 <= 80 ? this.blockPos2 : null);
                        var blockPosSet = this.getBlockedPositions();
                        CrystalPlacer.DataRecord2 c2 = flag4
                           ? CrystalPlacer.m_7(
                              c5List2,
                              vec32,
                              floatValue8,
                              this.getExplosionRange(),
                              this.maxSelfDamage.getFloat(),
                              this.minEnemyDamage.getFloat(),
                              this.m_9_float(),
                              flag6,
                              false,
                              floatValue7,
                              playerList4,
                              blockPos11,
                              blockPosSet,
                              this.integerMap.keySet(),
                              this::hasNearbyItems
                           )
                           : null;
                        if (c2 == null
                           && flag4
                           && flag6
                           && this.blockPos4 != null
                           && this.blockPos == null
                           && !CrystalPlacer.isObsidianOrBedrock(this.blockPos4)
                           && MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos4).canBeReplaced()) {
                           c2 = CrystalPlacer.m_1(
                              this.blockPos4,
                              c5List2,
                              vec32,
                              floatValue8,
                              this.getExplosionRange(),
                              this.maxSelfDamage.getFloat(),
                              this.m_9_float(),
                              true,
                              false,
                              floatValue7,
                              playerList4,
                              blockPosSet,
                              this.integerMap.keySet(),
                              this::hasNearbyItems
                           );
                        }

                        if (c2 == null && flag4 && flag6) {
                           var c5List3 = this.m_2(playerList3, CrystalInteraction.getPing() * 2 + 4);
                           c2 = CrystalPlacer.m_7(
                              c5List3,
                              vec32,
                              floatValue8,
                              this.getExplosionRange(),
                              this.maxSelfDamage.getFloat(),
                              this.minEnemyDamage.getFloat(),
                              this.m_9_float(),
                              true,
                              false,
                              floatValue7,
                              playerList4,
                              blockPos11,
                              blockPosSet,
                              this.integerMap.keySet(),
                              this::hasNearbyItems
                           );
                        }

                        if (c2 != null) {
                           if (!this.m_7(c2)) {
                              this.stopMining();
                              this.blockPos3 = c2.m_7_i();
                              if (c2.m_60()) {
                                 this.tryPlaceObsidian(c2.m_7_i());
                              } else if (!flag7) {
                                 CrystalInteraction.DataRecord c22 = CrystalInteraction.m_6(c2.m_7_i(), vec32, this.placeRange.getFloat());
                                 if (c22 != null) {
                                    this.aimAt(c22.m_7a());
                                 }
                              } else if (this.tryPlaceCrystal(c2.m_7_i())) {
                                 this.blockPos4 = null;
                              }
                           }
                        } else if (flag5) {
                           if (this.blockPos5 == null || !this.blockPosMap4.containsKey(this.blockPos5) || !this.tryPlaceCrystal(this.blockPos5)) {
                              if (this.blockPos4 != null && flag4 && (Boolean)this.autoDigUnder.getValue()) {
                                 HashSet set3 = new HashSet<>(this.blockPosMap.keySet());
                                 set3.addAll(this.blockPosMap2.keySet());
                                 CrystalPlacer.DataRecord2 c23 = CrystalPlacer.m_1(
                                    this.blockPos4,
                                    c5List2,
                                    vec32,
                                    floatValue8,
                                    this.getExplosionRange(),
                                    this.maxSelfDamage.getFloat(),
                                    this.m_3_float(),
                                    false,
                                    true,
                                    floatValue7,
                                    playerList4,
                                    set3,
                                    this.integerMap.keySet(),
                                    this::hasNearbyItems
                                 );
                                 if (c23 != null && c23.m_6y_boolean()) {
                                    if (!this.m_7(c23)) {
                                       this.m_5(c23);
                                    }

                                    return;
                                 }
                              }

                              this.c4 = AutoAnchor.DataRecord.f_1;
                              this.stopMining();
                              this.hotbarSwitcher.m_8_void();
                              BlockPos blockPos12 = (this.blockPosMap4.isEmpty() ? this.blockPosMap3 : this.blockPosMap4).keySet().iterator().next();
                              this.aimAt(new Vec3(blockPos12.getX() + 0.5, blockPos12.getY() + 1.0, blockPos12.getZ() + 0.5));
                           }
                        } else {
                           if (!(Boolean)this.autoDigUnder.getValue() || !flag4) {
                              this.stopMining();
                           }

                           if (this.blockPos3 != null && (this.blockPos3.equals(this.blockPos6) || CrystalPlacer.m_8(this.blockPos3, this.integerMap.keySet()))) {
                              if (this.value4 < 0) {
                                 this.value4 = MinecraftAccess.MINECRAFT.player.tickCount;
                              }

                              if (this.blockPos5 == null && flag4 && this.value11 < 2 && MinecraftAccess.MINECRAFT.player.tickCount >= this.value10) {
                                 CrystalPlacer.DataRecord2 c24 = CrystalPlacer.m_8(
                                    this.blockPos3,
                                    c5List2,
                                    vec32,
                                    floatValue8,
                                    this.getExplosionRange(),
                                    Math.min(this.maxSelfDamage.getFloat(), this.m_3_float()),
                                    floatValue7,
                                    playerList4,
                                    flag6,
                                    blockPosSet,
                                    this.integerMap.keySet(),
                                    this::hasNearbyItems
                                 );
                                 if (c24 != null) {
                                    this.blockPos5 = c24.m_7_i();
                                    this.value9 = MinecraftAccess.MINECRAFT.player.tickCount;
                                    this.value11++;
                                 }
                              }

                              if (this.blockPos5 != null && !this.blockPosMap3.containsKey(this.blockPos5) && this.findCrystalAt(this.blockPos5.above()) == null) {
                                 this.stopMining();
                                 if (CrystalPlacer.isObsidianOrBedrock(this.blockPos5)) {
                                    this.tryPlaceCrystal(this.blockPos5);
                                 } else {
                                    this.tryPlaceObsidian(this.blockPos5);
                                 }

                                 return;
                              }

                              if (this.blockPos5 != null || MinecraftAccess.MINECRAFT.player.tickCount < this.value10 || MinecraftAccess.MINECRAFT.player.tickCount - this.value4 <= 20) {
                                 this.hotbarSwitcher.m_8_void();
                                 return;
                              }
                           } else {
                              this.value4 = -1;
                              this.value11 = 0;
                           }

                           if (this.blockPos != null && CrystalInteraction.m_9(this.blockPos, vec32, this.placeRange.getFloat()) == null) {
                              this.hotbarSwitcher.m_8_void();
                           } else {
                              boolean flag8 = flag4 && (Boolean)this.autoDigUnder.getValue();
                              boolean flag9 = this.m_5_boolean(c5List2);
                              CrystalPlacer.DataRecord2 c25 = flag8 && this.blockPos4 != null
                                 ? CrystalPlacer.m_1(
                                    this.blockPos4,
                                    c5List2,
                                    vec32,
                                    floatValue8,
                                    this.getExplosionRange(),
                                    this.maxSelfDamage.getFloat(),
                                    this.m_3_float(),
                                    flag6,
                                    true,
                                    floatValue7,
                                    playerList4,
                                    blockPosSet,
                                    this.integerMap.keySet(),
                                    this::hasNearbyItems
                                 )
                                 : null;
                              c2 = c25 != null
                                 ? c25
                                 : (
                                    flag8 && flag9
                                       ? CrystalPlacer.m_7(
                                          c5List2,
                                          vec32,
                                          floatValue8,
                                          this.getExplosionRange(),
                                          this.maxSelfDamage.getFloat(),
                                          this.minEnemyDamage.getFloat(),
                                          flag6,
                                          true,
                                          floatValue7,
                                          playerList4,
                                          this.blockPos4,
                                          blockPosSet,
                                          this.integerMap.keySet(),
                                          this::hasNearbyItems
                                       )
                                       : null
                                 );
                              if (c2 != null
                                 && c2.m_72_BlockPos() != null
                                 && c2.m_7_i().equals(this.blockPos4)
                                 && this.blockPos != null
                                 && (this.blockPos.equals(this.blockPos4) || this.blockPos.equals(this.blockPos4.above()))
                                 && CrystalPlacer.m_8_boolean(MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos))) {
                                 c2 = new CrystalPlacer.DataRecord2(c2.m_7_i(), c2.m_60(), this.blockPos, c2.m_1_u_float(), c2.m_1_c(), false);
                              }

                              if (c2 == null) {
                                 this.stopMining();
                                 this.hotbarSwitcher.m_8_void();
                              } else if (!this.m_7(c2)) {
                                 this.m_5(c2);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               this.cancelAndReset();
               this.blockPos3 = null;
               this.blockPos4 = null;
            }
         }
      } else {
         this.resetAll();
      }
   }

   private void m_5(CrystalPlacer.DataRecord2 c2) {
      this.blockPos3 = c2.m_7_i();
      if (c2.m_72_BlockPos() != null) {
         this.blockPos4 = c2.m_7_i();
      }

      if (c2.m_72_BlockPos() == null || c2.m_6y_boolean() && c2.m_60()) {
         this.stopMining();
         if (c2.m_60()) {
            this.tryPlaceObsidian(c2.m_7_i());
         } else {
            this.tryPlaceCrystal(c2.m_7_i());
         }
      } else {
         this.m_1_void(c2.m_72_BlockPos());
      }
   }

   @EventTarget(priority = EventPriority.LOWEST)
   private void onPreMotion(PreMotionEvent event) {
      if (this.c9 != null && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null && !MinecraftAccess.MINECRAFT.player.isUsingItem()) {
         if (this.isOtherModuleBusy()) {
            this.cancelAndReset();
         } else if (this.isRotationReady()) {
            Vec3 vec32 = MinecraftAccess.MINECRAFT.player.position().add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight(), 0.0);
            long longValue2 = System.nanoTime();

            Vec3 vec33;
            try {
               if (this.c9.m_4f() == CrystalAura.ModeKind.ATTACK) {
                  vec33 = this.m_7(this.c9.m_5h(), vec32);
               } else {
                  CrystalInteraction.DataRecord c2 = switch (this.c9.m_4f().ordinal()) {
                     case 0 -> CrystalInteraction.m_6(this.c9.m_4_t_BlockPos(), vec32, this.placeRange.getFloat(), false);
                     case 1 -> CrystalInteraction.m_6(this.c9.m_4_t_BlockPos(), vec32, this.placeRange.getFloat());
                     default -> CrystalInteraction.m_9(this.c9.m_4_t_BlockPos(), vec32, this.placeRange.getFloat());
                     case 4 -> CrystalInteraction.m_6(this.c9.m_4_t_BlockPos(), vec32, this.placeRange.getFloat(), true);
                  };
                  if (c2 == null && this.c9.m_4f() != CrystalAura.ModeKind.DIG) {
                     c2 = this.c9.m_1w_C2();
                  }

                  vec33 = c2 == null ? null : c2.m_7a();
                  this.c9 = new CrystalAura.DataRecord3(this.c9.m_4f(), this.c9.m_4_t_BlockPos(), c2, this.c9.m_5h(), this.c9.m_2b());
               }
            } finally {
               C03.recordSince(longValue2);
            }

            if (vec33 == null) {
               this.c9 = null;
               this.hotbarSwitcher.m_8_void();
            } else {
               this.m_3_void(vec32);
            }
         }
      }
   }

   private void m_3_void(Vec3 vec32) {
      long longValue2 = System.nanoTime();

      try {
         this.executeAction(vec32);
      } finally {
         C04.recordSince(longValue2);
      }
   }

   private void executeAction(Vec3 vec32) {
      CrystalAura.DataRecord3 c92 = this.c9;
      if (c92.m_4f() != CrystalAura.ModeKind.ATTACK || this.m_0(c92.m_5h())) {
         int value18 = this.m_5(c92);
         if (value18 < 0) {
            this.cancelCurrentAction();
         } else {
            this.hotbarSwitcher.m_2_void(value18);
            if (c92.m_4f() == CrystalAura.ModeKind.ATTACK) {
               EndCrystal endCrystal = c92.m_5h();
               if (CrystalPlacer.m_4(endCrystal, MinecraftAccess.MINECRAFT.player.getEyePosition(), this.getExplosionRange()) == null) {
                  this.cancelCurrentAction();
               } else if (!CrystalPlacer.m_3(endCrystal, this.getPlayers(), this.maxSelfDamage.getFloat(), 0.0F, this.m_4_float(), this.getFarPlayers())) {
                  this.cancelCurrentAction();
               } else if (this.hasNearbyItems(endCrystal.position())) {
                  this.cancelCurrentAction();
               } else {
                  this.c9 = null;

                  try {
                     this.hotbarSwitcher.markTick();
                     MinecraftAccess.MINECRAFT.gameMode.attack(MinecraftAccess.MINECRAFT.player, endCrystal);
                     this.integerMap.put(endCrystal.getId(), MinecraftAccess.MINECRAFT.player.tickCount + getPingDelay());
                     if (this.blockPos5 != null && endCrystal.blockPosition().equals(this.blockPos5.above())) {
                        this.blockPos5 = null;
                        this.value10 = MinecraftAccess.MINECRAFT.player.tickCount + getPingDelay() + 4;
                     }

                     this.value6 = MinecraftAccess.MINECRAFT.player.tickCount;
                     MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
                     this.timeHelper.reset();
                     this.tryPlaceOnCrystal(endCrystal, vec32);
                  } finally {
                     this.hotbarSwitcher.m_8_void();
                  }
               }
            } else {
               Rotations rotations3 = Onetap.ROTATION_MODES.toRotations();
               boolean flag4 = c92.m_4f() == CrystalAura.ModeKind.OBSIDIAN || c92.m_4f() == CrystalAura.ModeKind.WEB;
               BlockHitResult blockHitResult = flag4 && CrystalInteraction.m_5_boolean(c92.m_1w_C2().m_5b())
                  ? null
                  : CrystalInteraction.m_2(c92.m_1w_C2(), this.placeRange.getFloat(), vec32, rotations3.getFloatValue(), rotations3.getFloatValue2());
               if (blockHitResult == null) {
                  if (c92.m_4f() != CrystalAura.ModeKind.DIG) {
                     this.c9 = null;
                  }
               } else if (c92.m_4f() == CrystalAura.ModeKind.DIG) {
                  if ((Boolean)this.autoDigUnder.getValue() && CrystalPlacer.m_8_boolean(MinecraftAccess.MINECRAFT.level.getBlockState(c92.m_4_t_BlockPos()))) {
                     this.c9 = null;
                     this.breakBlock(c92.m_4_t_BlockPos(), blockHitResult);
                  } else {
                     this.cancelCurrentAction();
                  }
               } else {
                  boolean flag5 = c92.m_4f() == CrystalAura.ModeKind.CRYSTAL
                     ? !this.blockPosMap3.containsKey(c92.m_4_t_BlockPos()) && CrystalPlacer.m_7_boolean(c92.m_4_t_BlockPos(), this.integerMap.keySet())
                     : CrystalInteraction.m_8(c92.m_4_t_BlockPos(), blockHitResult, MinecraftAccess.MINECRAFT.player.getMainHandItem())
                        && (c92.m_4f() == CrystalAura.ModeKind.WEB || !CrystalPlacer.isBlocked(c92.m_4_t_BlockPos()));
                  if (!flag5) {
                     this.cancelCurrentAction();
                  } else {
                     this.c9 = null;

                     try {
                        this.hotbarSwitcher.markTick();
                        InteractionResult interactionResult = MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
                        LegacyCrystalAura.m_0(rotations3);
                        boolean flag6 = c92.m_4f() == CrystalAura.ModeKind.CRYSTAL
                           || interactionResult instanceof Success success
                              && success.wasItemInteraction()
                              && MinecraftAccess.MINECRAFT.level.getBlockState(c92.m_4_t_BlockPos()).is(c92.m_4f() == CrystalAura.ModeKind.WEB ? Blocks.COBWEB : Blocks.OBSIDIAN);
                        if (flag6) {
                           MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
                           this.timeHelper.reset();
                           if (c92.m_4f() == CrystalAura.ModeKind.CRYSTAL) {
                              this.blockPosMap3.putIfAbsent(c92.m_4_t_BlockPos(), MinecraftAccess.MINECRAFT.player.tickCount);
                              this.value6 = MinecraftAccess.MINECRAFT.player.tickCount;
                           } else if (c92.m_4f() == CrystalAura.ModeKind.WEB) {
                              this.blockPos8 = c92.m_4_t_BlockPos();
                              this.value7 = MinecraftAccess.MINECRAFT.player.tickCount;
                           } else {
                              this.blockPosMap4.putIfAbsent(c92.m_4_t_BlockPos(), MinecraftAccess.MINECRAFT.player.tickCount);
                           }
                        } else {
                           this.blockPosMap2.put(c92.m_4_t_BlockPos(), MinecraftAccess.MINECRAFT.player.tickCount + 12);
                        }
                     } finally {
                        this.hotbarSwitcher.m_8_void();
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && event.getPacket() instanceof ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket && clientboundSetEntityMotionPacket.id() != MinecraftAccess.MINECRAFT.player.getId()) {
         CrystalTargeting.m_6(clientboundSetEntityMotionPacket.id(), clientboundSetEntityMotionPacket.movement());
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.rotations != null) {
         LegacyCrystalAura.fixMovement(event);
      }
   }

   @EventTarget
   private void onContinueAttack(ContinueAttackEvent event) {
      if (this.blockPos != null) {
         event.cancel();
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      CrystalAura.DataRecord2 c6;
      while ((c6 = this.queue.poll()) != null) {
         this.c6List.add(c6);
      }

      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (!(Boolean)this.renderPosition.getValue()) {
            this.c6List.clear();
            this.blockPos9 = null;
         } else if (!this.c6List.isEmpty() || this.blockPos9 != null) {
            Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
            Vec3 vec32 = event.getVec3();
            long longValue2 = System.currentTimeMillis();
            var iterator = this.c6List.iterator();

            while (iterator.hasNext()) {
               CrystalAura.DataRecord2 c62 = (CrystalAura.DataRecord2)iterator.next();
               float floatValue7 = (float)(longValue2 - c62.m_5j()) / 300.0F;
               if (floatValue7 >= 1.0F) {
                  iterator.remove();
               } else {
                  drawBox(matrix4f, vec32, getHighlightBox(c62.m_6_s(), 0.4F + 0.6F * floatValue7), c62.m_1i_boolean() ? -4962561 : -13795841, (1.0F - floatValue7) * 0.39215687F);
               }
            }

            this.renderPlacement(matrix4f, vec32, longValue2);
         }
      }
   }

   private void renderPlacement(Matrix4f matrix4f, Vec3 vec32, long longValue2) {
      if (this.blockPos9 != null) {
         long longValue3 = longValue2 - this.longValue;
         float floatValue7;
         if (longValue3 <= 140L) {
            floatValue7 = 1.0F;
         } else {
            float floatValue8 = (float)(longValue3 - 140L) / 160.0F;
            if (floatValue8 >= 1.0F) {
               this.blockPos9 = null;
               return;
            }

            floatValue7 = 1.0F - floatValue8;
         }

         drawBox(matrix4f, vec32, getHighlightBox(this.blockPos9, 0.55F + 0.45F * this.floatValue2), -51401, (0.45F + 0.4F * this.floatValue2) * floatValue7 * 0.5F);
      }
   }

   public static void drawBox(Matrix4f matrix4f, Vec3 vec32, AABB aABB2, int value18, float floatValue7) {
      Box3DRenderer.drawBox(
         matrix4f,
         (float)(aABB2.minX - vec32.x),
         (float)(aABB2.minY - vec32.y),
         (float)(aABB2.minZ - vec32.z),
         (float)(aABB2.maxX - vec32.x),
         (float)(aABB2.maxY - vec32.y),
         (float)(aABB2.maxZ - vec32.z),
         ColorUtil.withAlpha(value18, Mth.clamp((int)(floatValue7 * 255.0F), 0, 255))
      );
   }

   public static AABB getHighlightBox(BlockPos blockPos10, float floatValue7) {
      double doubleValue = 0.5 * Mth.clamp(floatValue7, 0.05F, 1.0F);
      Vec3 vec32 = Vec3.atCenterOf(blockPos10);
      return new AABB(vec32.x - doubleValue, vec32.y - doubleValue, vec32.z - doubleValue, vec32.x + doubleValue, vec32.y + doubleValue, vec32.z + doubleValue);
   }

   private boolean isOtherModuleBusy() {
      AutoWeb autoWeb2 = Onetap.MODULE_MANAGER.get(AutoWeb.class);
      AutoAnchor autoAnchor = Onetap.MODULE_MANAGER.get(AutoAnchor.class);
      return autoWeb2 != null && autoWeb2.isFlag() || autoAnchor != null && autoAnchor.m_6_boolean();
   }

   private boolean m_7(CrystalPlacer.DataRecord2 c2) {
      this.c4 = AutoAnchor.DataRecord.m_2(c2);
      AutoAnchor autoAnchor = Onetap.MODULE_MANAGER.get(AutoAnchor.class);
      AutoAnchor.DataRecord c42 = autoAnchor == null ? null : autoAnchor.m_5_C4();
      if (c42 != null && c42.m_1(this.c4)) {
         this.stopMining();
         this.hotbarSwitcher.m_8_void();
         this.reset();
         return true;
      } else {
         return false;
      }
   }

   public static List<Player> getEnemies(float floatValue7) {
      ArrayList list = new ArrayList();
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
            if (player != MinecraftAccess.MINECRAFT.player
               && player.isAlive()
               && !player.isSpectator()
               && CrystalPlacer.distanceSqToBox(MinecraftAccess.MINECRAFT.player.getEyePosition(), player.getBoundingBox()) <= floatValue7 * floatValue7
               && Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
               list.add(player);
            }
         }

         return list;
      } else {
         return list;
      }
   }

   public static List<Player> getNearbyPlayers(float floatValue7) {
      ArrayList<net.minecraft.world.entity.player.Player> playerList3 = new ArrayList<>();
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);
         Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getEyePosition();

         for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
            if (player != MinecraftAccess.MINECRAFT.player
               && player.isAlive()
               && !player.isSpectator()
               && !player.isCreative()
               && CrystalPlacer.distanceSqToBox(vec32, player.getBoundingBox()) <= floatValue7 * floatValue7
               && !Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())
               && (antiBot == null || !antiBot.isBot(player))) {
               playerList3.add(player);
            }
         }

         playerList3.sort(Comparator.comparingDouble(player2 -> CrystalPlacer.distanceSqToBox(vec32, player2.getBoundingBox())));
         return playerList3;
      } else {
         return playerList3;
      }
   }

   private List<Player> getPlayers() {
      this.updatePlayers();
      return this.playerList;
   }

   private List<Player> getFarPlayers() {
      this.updatePlayers();
      return this.playerList2;
   }

   private void updatePlayers() {
      if (this.value14 != MinecraftAccess.MINECRAFT.player.tickCount) {
         long longValue2 = System.nanoTime();
         this.playerList = getNearbyPlayers(8.0F);
         this.playerList2 = getEnemies(18.0F);
         C0.recordSince(longValue2);
         this.value14 = MinecraftAccess.MINECRAFT.player.tickCount;
      }
   }

   private int m_5(CrystalAura.DataRecord3 c92) {
      return switch (c92.m_4f().ordinal()) {
         case 0 -> InventoryUtil.findHotbarSlot(Items.OBSIDIAN);
         case 1 -> InventoryUtil.findHotbarSlot(Items.END_CRYSTAL);
         case 2 -> CrystalMining.m_2_int();
         case 3 -> {
            int value18 = CrystalMining.m_5(MinecraftAccess.MINECRAFT.level.getBlockState(c92.m_4_t_BlockPos()));
            yield value18 >= 0 ? value18 : MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
         }
         case 4 -> InventoryUtil.findHotbarSlot(Items.COBWEB);
         default -> throw new MatchException(null, null);
      };
   }

   private boolean tryPlaceCrystal(BlockPos blockPos10) {
      return !this.blockPosMap3.containsKey(blockPos10) && CrystalPlacer.m_7_boolean(blockPos10, this.integerMap.keySet())
         ? this.queueAction(CrystalAura.ModeKind.CRYSTAL, blockPos10, CrystalInteraction.m_6(blockPos10, MinecraftAccess.MINECRAFT.player.getEyePosition(), this.placeRange.getFloat()), null)
         : false;
   }

   private boolean tryPlaceObsidian(BlockPos blockPos10) {
      return this.queueAction(CrystalAura.ModeKind.OBSIDIAN, blockPos10, CrystalInteraction.m_6(blockPos10, MinecraftAccess.MINECRAFT.player.getEyePosition(), this.placeRange.getFloat(), false), null);
   }

   private boolean m_7_boolean(EndCrystal endCrystal) {
      if (endCrystal != null && endCrystal.isAlive()) {
         this.stopMining();
         this.c4 = AutoAnchor.DataRecord.f_1;
         return this.queueAction(CrystalAura.ModeKind.ATTACK, endCrystal.blockPosition(), null, endCrystal);
      } else {
         return false;
      }
   }

   private Vec3 m_7(EndCrystal endCrystal, Vec3 vec32) {
      Vec3 vec33 = CrystalPlacer.m_4(endCrystal, vec32, this.getExplosionRange());
      CrystalInteraction.DataRecord c2 = vec33 == null ? null : CrystalInteraction.m_6(endCrystal.blockPosition().below(), vec32, this.placeRange.getFloat());
      if (c2 == null) {
         return vec33;
      }

      AABB aABB2 = endCrystal.getBoundingBox();
      double doubleValue = this.getExplosionRange();
      return !aABB2.contains(vec32) && !aABB2.clip(vec32, c2.m_7a()).filter(vec34 -> vec32.distanceToSqr(vec34) <= doubleValue * doubleValue).isPresent() ? vec33 : c2.m_7a();
   }

   private void tryPlaceOnCrystal(EndCrystal endCrystal, Vec3 vec32) {
      BlockPos blockPos10 = endCrystal.blockPosition().below();
      if (InventoryUtil.findHotbarSlot(Items.END_CRYSTAL) >= 0
         && !this.blockPosMap3.containsKey(blockPos10)
         && !this.blockPosMap2.containsKey(blockPos10)
         && CrystalPlacer.m_7_boolean(blockPos10, this.integerMap.keySet())
         && CrystalInteraction.m_6(blockPos10, vec32, this.placeRange.getFloat()) != null) {
         CrystalPlacer.DataRecord2 c2 = CrystalPlacer.m_7(
            this.m_2(this.getPlayers(), 0),
            vec32,
            (float)CrystalInteraction.clampInteractionRange(this.placeRange.getFloat()),
            this.getExplosionRange(),
            this.maxSelfDamage.getFloat(),
            this.m_9_float(),
            false,
            false,
            this.m_4_float(),
            this.getFarPlayers(),
            blockPos10,
            this.getBlockedPositions(),
            this.integerMap.keySet(),
            this::hasNearbyItems
         );
         if (c2 != null && c2.m_7_i().equals(blockPos10)) {
            this.blockPos7 = blockPos10;
         }
      }
   }

   private boolean queueAction(CrystalAura.ModeKind c42, BlockPos blockPos10, CrystalInteraction.DataRecord c2, EndCrystal endCrystal) {
      if (c42 != CrystalAura.ModeKind.ATTACK && c2 == null) {
         this.blockPosMap2.put(blockPos10.immutable(), MinecraftAccess.MINECRAFT.player.tickCount + 6);
         return false;
      } else {
         this.c9 = new CrystalAura.DataRecord3(c42, blockPos10.immutable(), c2, endCrystal, MinecraftAccess.MINECRAFT.player.tickCount);
         this.m_6g_void();
         return true;
      }
   }

   private void m_6g_void() {
      if (this.c9 != null) {
         Vec3 vec32 = this.c9.m_4f() == CrystalAura.ModeKind.ATTACK ? this.m_7(this.c9.m_5h(), MinecraftAccess.MINECRAFT.player.getEyePosition()) : this.c9.m_1w_C2().m_7a();
         if (vec32 == null) {
            this.cancelCurrentAction();
         } else if (!this.aimAt(vec32)) {
            this.hotbarSwitcher.m_8_void();
         }
      }
   }

   private boolean aimAt(Vec3 vec32) {
      this.rotations = new Rotations(MinecraftAccess.MINECRAFT.player.getEyePosition(), vec32);
      this.value13 = 0;
      this.m_64_void();
      return this.isRotationReady();
   }

   private void tickRotation() {
      if (this.rotations != null && MinecraftAccess.MINECRAFT.player != null && this.value12 != MinecraftAccess.MINECRAFT.player.tickCount) {
         if (++this.value13 > 10) {
            this.reset();
         } else {
            this.m_64_void();
         }
      }
   }

   private void m_64_void() {
      Rotations rotations3 = m_7(Onetap.ROTATION_MODES.toRotations(), this.rotations);
      if (this.c9 != null && this.c9.m_4f() != CrystalAura.ModeKind.ATTACK && this.c9.m_4f() != CrystalAura.ModeKind.DIG && LegacyCrystalAura.m_8_boolean(rotations3)) {
         rotations3 = new Rotations(rotations3.getFloatValue() + getMouseSensitivityFactor(), rotations3.getFloatValue2());
      }

      Onetap.ROTATION_MODES.rotate(rotations3, false, 15);
      this.rotations2 = rotations3;
      this.value12 = MinecraftAccess.MINECRAFT.player.tickCount;
   }

   private boolean isRotationReady() {
      if (this.rotations2 != null && MinecraftAccess.MINECRAFT.player != null && this.value12 == MinecraftAccess.MINECRAFT.player.tickCount) {
         Rotations rotations3 = Onetap.ROTATION_MODES.toRotations();
         return rotations3.getFloatValue() == this.rotations2.getFloatValue() && rotations3.getFloatValue2() == this.rotations2.getFloatValue2();
      } else {
         return false;
      }
   }

   private void reset() {
      this.rotations = null;
      this.rotations2 = null;
      this.value12 = -1;
      this.value13 = 0;
   }

   public static Rotations smoothRotation(Rotations rotations3, Rotations rotations4) {
      double[] doubleArray = C9_q.m_3(8, 0.0, rotations3.getFloatValue(), rotations3.getFloatValue2(), rotations4.getFloatValue(), rotations4.getFloatValue2(), getMouseSensitivityFactor());
      return new Rotations((float)doubleArray[0], (float)doubleArray[1]);
   }

   public static Rotations m_7(Rotations rotations3, Rotations rotations4) {
      double[] doubleArray = DOUBLE_VALUE_ARRAY;
      doubleArray[0] = 1.0;
      doubleArray[1] = rotations3.getFloatValue();
      doubleArray[2] = rotations3.getFloatValue2();
      doubleArray[3] = rotations4.getFloatValue();
      doubleArray[4] = rotations4.getFloatValue2();
      doubleArray[5] = getMouseSensitivityFactor();
      return C9_q.m_7(8, doubleArray) ? new Rotations((float)doubleArray[0], (float)doubleArray[1]) : smoothRotation(smoothRotation(rotations3, rotations4), rotations4);
   }

   public static float getMouseSensitivityFactor() {
      boolean flag4 = !MinecraftAccess.MINECRAFT.options.smoothCamera && MinecraftAccess.MINECRAFT.options.getCameraType().isFirstPerson() && MinecraftAccess.MINECRAFT.player.isScoping();
      double doubleValue = MinecraftAccess.MINECRAFT.options.sensitivity().get() * 0.6 + 0.2;
      return (float)(doubleValue * doubleValue * doubleValue * (flag4 ? 1.0 : 8.0)) * 0.15F;
   }

   private void cancelCurrentAction() {
      if (this.c9 != null) {
         if (this.c9.m_4f() == CrystalAura.ModeKind.DIG) {
            this.blockPosMap.put(this.c9.m_4_t_BlockPos(), MinecraftAccess.MINECRAFT.player.tickCount + 40);
            this.stopMining();
         } else if (this.c9.m_4f() != CrystalAura.ModeKind.ATTACK) {
            this.blockPosMap2.put(this.c9.m_4_t_BlockPos(), MinecraftAccess.MINECRAFT.player.tickCount + 12);
         }

         this.c9 = null;
         this.hotbarSwitcher.m_8_void();
         this.reset();
      }
   }

   private void purgeExpired() {
      int value18 = MinecraftAccess.MINECRAFT.player.tickCount;
      this.blockPosMap.values().removeIf(integer -> value18 >= integer);
      this.blockPosMap2.values().removeIf(integer2 -> value18 >= integer2);
      this.integerMap2.keySet().removeIf(integer3 -> MinecraftAccess.MINECRAFT.level.getEntity(integer3) == null);
      this.integerMap.entrySet().removeIf(entry -> {
         if (value18 < entry.getValue()) {
            return false;
         }

         Entity entity = MinecraftAccess.MINECRAFT.level.getEntity(entry.getKey());
         if (entity != null && entity.isAlive()) {
            this.integerMap2.merge(entry.getKey(), 1, Integer::sum);
         }

         return true;
      });
   }

   private void m_6w_void() {
      int value18 = MinecraftAccess.MINECRAFT.player.tickCount;
      var iterator = this.blockPosMap4.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         BlockPos blockPos10 = (BlockPos)entry.getKey();
         int value19 = value18 - (Integer)entry.getValue();
         if (!CrystalInteraction.m_5_boolean(blockPos10) && MinecraftAccess.MINECRAFT.level.getBlockState(blockPos10).is(Blocks.OBSIDIAN)) {
            this.blockPos2 = blockPos10;
            this.value3 = value18;
            this.addRenderPos(blockPos10, false);
            iterator.remove();
         } else if (value19 > CrystalInteraction.getTimeoutTicks() || value19 >= 2 && !CrystalInteraction.m_5_boolean(blockPos10)) {
            this.blockPosMap2.put(blockPos10, value18 + 4);
            iterator.remove();
         }
      }

      var iterator2 = this.blockPosMap3.entrySet().iterator();

      while (iterator2.hasNext()) {
         Entry entry2 = (Entry)iterator2.next();
         BlockPos blockPos11 = (BlockPos)entry2.getKey();
         int value20 = value18 - (Integer)entry2.getValue();
         if (this.findCrystalAt(blockPos11.above()) != null) {
            this.addRenderPos(blockPos11.above(), true);
            iterator2.remove();
         } else if (value20 > getPingDelay()) {
            if (CrystalPlacer.m_8(blockPos11, this.integerMap.keySet())) {
               this.blockPos3 = blockPos11;
            } else {
               this.blockPosMap2.put(blockPos11, value18 + 10);
            }

            iterator2.remove();
         }
      }
   }

   private EndCrystal findCrystalAt(BlockPos blockPos10) {
      AABB aABB2 = new AABB(blockPos10.getX(), blockPos10.getY(), blockPos10.getZ(), blockPos10.getX() + 1, blockPos10.getY() + 2, blockPos10.getZ() + 1);
      List list = MinecraftAccess.MINECRAFT
         .level
         .getEntitiesOfClass(EndCrystal.class, aABB2, endCrystal -> endCrystal.isAlive() && !this.integerMap.containsKey(endCrystal.getId()) && endCrystal.blockPosition().equals(blockPos10));
      return list.isEmpty() ? null : (EndCrystal)list.getFirst();
   }

   private void addRenderPos(BlockPos blockPos10, boolean flag4) {
      if ((Boolean)this.renderPosition.getValue()) {
         this.queue.add(new CrystalAura.DataRecord2(blockPos10, flag4, System.currentTimeMillis()));
      }
   }

   private void m_0_void(Vec3 vec32) {
      if (this.blockPos != null) {
         BlockPos blockPos10 = this.blockPos;
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos10);
         if (!blockState.isAir() && !blockState.canBeReplaced()) {
            double doubleValue = CrystalInteraction.clampInteractionRange(this.placeRange.getFloat());
            boolean flag4 = CrystalPlacer.distanceSqToBlock(vec32, blockPos10) <= doubleValue * doubleValue;
            boolean flag5 = MinecraftAccess.MINECRAFT.player.tickCount - this.value2 <= 10;
            if (!CrystalPlacer.m_8_boolean(blockState) || !flag4 || !flag5) {
               this.blockPosMap.put(blockPos10, MinecraftAccess.MINECRAFT.player.tickCount + 40);
               this.stopMining();
            }
         } else {
            this.stopMining();
         }
      }
   }

   private void m_1_void(BlockPos blockPos10) {
      if (!this.blockPosMap.containsKey(blockPos10) && CrystalPlacer.m_8_boolean(MinecraftAccess.MINECRAFT.level.getBlockState(blockPos10))) {
         CrystalInteraction.DataRecord c2 = CrystalInteraction.m_9(blockPos10, MinecraftAccess.MINECRAFT.player.getEyePosition(), this.placeRange.getFloat());
         if (c2 == null) {
            if (!blockPos10.equals(this.blockPos)) {
               this.blockPosMap.put(blockPos10.immutable(), MinecraftAccess.MINECRAFT.player.tickCount + 10);
               this.stopMining();
            }
         } else {
            if (!blockPos10.equals(this.blockPos)) {
               this.stopMining();
               this.blockPos = blockPos10.immutable();
               this.value2 = MinecraftAccess.MINECRAFT.player.tickCount;
               this.floatValue = 0.0F;
            }

            this.queueAction(CrystalAura.ModeKind.DIG, blockPos10, c2, null);
         }
      } else {
         this.stopMining();
      }
   }

   private void breakBlock(BlockPos blockPos10, BlockHitResult blockHitResult) {
      MultiPlayerGameModeAccessor multiPlayerGameModeAccessor = (MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode;
      if (multiPlayerGameModeAccessor.onetap$getDestroyDelay() > 0) {
         multiPlayerGameModeAccessor.onetap$setDestroyDelay(0);
      }

      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos10);
      if (MinecraftAccess.MINECRAFT.gameMode.continueDestroyBlock(blockPos10, blockHitResult.getDirection())) {
         MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
      }

      if (this.blockPos4 != null
         && blockPos10.equals(this.blockPos4.above())
         && CrystalPlacer.isObsidianOrBedrock(this.blockPos4)
         && MinecraftAccess.MINECRAFT.level.getBlockState(blockPos10).isAir()
         && (!blockState.requiresCorrectToolForDrops() || MinecraftAccess.MINECRAFT.player.getMainHandItem().isCorrectToolForDrops(blockState))) {
         this.blockPos6 = this.blockPos4;
         this.value5 = MinecraftAccess.MINECRAFT.player.tickCount;
         this.blockPos3 = this.blockPos4;
      }

      float floatValue7 = Mth.clamp(multiPlayerGameModeAccessor.onetap$getDestroyProgress(), 0.0F, 1.0F);
      if (floatValue7 > this.floatValue + 1.0E-5F) {
         this.floatValue = floatValue7;
         this.value2 = MinecraftAccess.MINECRAFT.player.tickCount;
      }

      if ((Boolean)this.renderPosition.getValue()) {
         this.blockPos9 = blockPos10;
         this.floatValue2 = floatValue7;
         this.longValue = System.currentTimeMillis();
      }
   }

   private void stopMining() {
      if (this.blockPos != null) {
         if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
            MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
         }

         if (this.c9 != null && this.c9.m_4f() == CrystalAura.ModeKind.DIG) {
            this.c9 = null;
         }

         this.blockPos = null;
         this.hotbarSwitcher.m_8_void();
      }
   }

   private void cancelAndReset() {
      this.c9 = null;
      this.stopMining();
      this.hotbarSwitcher.m_8_void();
      this.reset();
   }

   private void m_2(List<Player> playerList3) {
      int value18 = MinecraftAccess.MINECRAFT.player.tickCount;
      HashSet set = new HashSet();

      for (Player player : playerList3) {
         int value19 = Mth.floor(player.getX());
         int value20 = Mth.floor(player.getZ());
         CrystalAura.DataRecord c2 = this.integerMap3.get(player.getId());
         if (c2 == null || c2.m_95() != value19 || c2.m_99() != value20) {
            this.integerMap3.put(player.getId(), new CrystalAura.DataRecord(value19, value20, value18));
         }

         set.add(player.getId());
      }

      this.integerMap3.keySet().retainAll(set);
   }

   private boolean m_5_boolean(List<CrystalPlacer.DataRecord4> c5List2) {
      for (CrystalPlacer.DataRecord4 c5 : c5List2) {
         if (!this.m_5_boolean(c5.m_1n()) && !this.m_3_boolean(c5.m_1n().getBoundingBox())) {
            CrystalAura.DataRecord c2 = this.integerMap3.get(c5.m_1n().getId());
            if (c2 == null || MinecraftAccess.MINECRAFT.player.tickCount - c2.m_9b() < 1) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean m_5_boolean(Player player) {
      return this.blockPos8 != null && player.getId() == this.value8 && !this.m_3_boolean(player.getBoundingBox());
   }

   private boolean tryPlaceWeb(List<Player> playerList3, List<Player> playerList4, Vec3 vec32) {
      if ((Boolean)this.autoWeb.getValue() && this.blockPos8 == null && InventoryUtil.findHotbarSlot(Items.COBWEB) >= 0) {
         AABB aABB2 = MinecraftAccess.MINECRAFT.player.getBoundingBox().minmax(PlayerUtil.predictLocal(2).aABB).inflate(0.05, 0.0, 0.05);
         int value18 = CrystalInteraction.getPing() + 1;

         for (Player player : playerList3) {
            if (!this.m_3_boolean(player.getBoundingBox())) {
               AABB aABB3 = PlayerUtil.predict(player, value18).aABB;
               if (!this.m_3_boolean(aABB3)) {
                  int value19 = Mth.floor(aABB3.minY + 1.0E-5);
                  BlockPos blockPos10 = null;
                  CrystalInteraction.DataRecord c2 = null;
                  double doubleValue = 0.0;

                  for (BlockPos blockPos11 : BlockPos.betweenClosed(
                     Mth.floor(aABB3.minX), value19, Mth.floor(aABB3.minZ), Mth.floor(aABB3.maxX), value19, Mth.floor(aABB3.maxZ)
                  )) {
                     AABB aABB4 = new AABB(blockPos11);
                     double doubleValue2 = (Math.min(aABB4.maxX, aABB3.maxX) - Math.max(aABB4.minX, aABB3.minX))
                        * (Math.min(aABB4.maxZ, aABB3.maxZ) - Math.max(aABB4.minZ, aABB3.minZ));
                     if (!(doubleValue2 <= doubleValue)
                        && !aABB4.intersects(aABB2)
                        && !this.blockPosMap2.containsKey(blockPos11)
                        && !playerList4.stream().anyMatch(player2 -> aABB4.intersects(player2.getBoundingBox()))) {
                        BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos11);
                        if (blockState.canBeReplaced() && blockState.getFluidState().isEmpty()) {
                           CrystalInteraction.DataRecord c22 = CrystalInteraction.m_6(blockPos11, vec32, this.placeRange.getFloat(), true);
                           if (c22 != null) {
                              blockPos10 = blockPos11.immutable();
                              c2 = c22;
                              doubleValue = doubleValue2;
                           }
                        }
                     }
                  }

                  if (blockPos10 != null && this.queueAction(CrystalAura.ModeKind.WEB, blockPos10, c2, null)) {
                     this.value8 = player.getId();
                     this.aABB = aABB3;
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private Set<BlockPos> getBlockedPositions() {
      HashSet set = new HashSet<>(this.blockPosMap.keySet());
      set.addAll(this.blockPosMap2.keySet());
      if (this.blockPos6 != null) {
         set.add(this.blockPos6);
      }

      for (BlockPos blockPos10 : this.blockPosMap3.keySet()) {
         for (BlockPos blockPos11 : BlockPos.betweenClosed(blockPos10.offset(-1, -1, -1), blockPos10.offset(1, 1, 1))) {
            set.add(blockPos11.immutable());
         }
      }

      return set;
   }

   private boolean isDelayElapsed() {
      return !(Boolean)this.crystalDelay.getValue() || MinecraftAccess.MINECRAFT.player.tickCount - this.value6 >= 2;
   }

   private boolean hasNearbyItems(Vec3 vec32) {
      return !this.itemEntityList.isEmpty() && LegacyCrystalAura.isItemNearby(vec32, this.itemEntityList);
   }

   private float m_9_float() {
      this.m_1_void();
      return this.floatValue4;
   }

   private float m_3_float() {
      this.m_1_void();
      return this.floatValue5;
   }

   private void m_1_void() {
      float floatValue7 = this.minEnemyDamage.getFloat();
      if (floatValue7 != this.floatValue3) {
         this.floatValue3 = floatValue7;
         this.floatValue4 = CrystalPlacer.m_1_float(floatValue7);
         this.floatValue5 = CrystalPlacer.m_6_float(floatValue7);
      }
   }

   private static int getPingDelay() {
      return CrystalInteraction.getPing() + 4;
   }

   private float getExplosionRange() {
      return (float)Math.min(this.explosionRange.getFloat(), MinecraftAccess.MINECRAFT.player.entityInteractionRange());
   }

   private boolean m_0(EndCrystal endCrystal) {
      if (endCrystal != null && this.vec3 != null) {
         AABB aABB2 = endCrystal.getBoundingBox();
         if (aABB2.contains(this.vec3)) {
            return true;
         }

         Rotations rotations3 = Onetap.ROTATION_MODES.toRotations();
         Vec3 vec32 = Vec3.directionFromRotation(rotations3.getFloatValue2(), rotations3.getFloatValue());
         return aABB2.clip(this.vec3, this.vec3.add(vec32.scale(this.getExplosionRange()))).isPresent();
      } else {
         return false;
      }
   }

   private float m_4_float() {
      if (this.value17 != MinecraftAccess.MINECRAFT.player.tickCount) {
         this.value17 = MinecraftAccess.MINECRAFT.player.tickCount;
         this.floatValue6 = CrystalPlacer.m_2_float(MinecraftAccess.MINECRAFT.player);
      }

      return this.floatValue6;
   }

   private boolean m_3_boolean(AABB aABB2) {
      for (BlockPos blockPos10 : BlockPos.betweenClosed(
         Mth.floor(aABB2.minX), Mth.floor(aABB2.minY), Mth.floor(aABB2.minZ), Mth.floor(aABB2.maxX), Mth.floor(aABB2.maxY), Mth.floor(aABB2.maxZ)
      )) {
         if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos10).is(Blocks.COBWEB)) {
            return true;
         }
      }

      return false;
   }

   private List<CrystalPlacer.DataRecord4> m_2(List<Player> playerList3, int value18) {
      if (this.c5List != null && this.value15 == MinecraftAccess.MINECRAFT.player.tickCount && this.value16 == value18) {
         return this.c5List;
      }

      long longValue2 = System.nanoTime();

      try {
         int value19 = this.predictTickCount.getInt();
         ArrayList<Player> playerList4 = new ArrayList<>(playerList3);
         playerList4.sort(Comparator.comparingDouble(player -> CrystalPlacer.distanceSqToBox(MinecraftAccess.MINECRAFT.player.getEyePosition(), player.getBoundingBox())));
         ArrayList list = new ArrayList();

         for (int i = 0; i < Math.min(playerList4.size(), 3); i++) {
            Player player2 = (Player)playerList4.get(i);
            AABB aABB2 = player2.getBoundingBox();
            if (this.m_5_boolean(player2)) {
               list.add(new CrystalPlacer.DataRecord4(player2, this.aABB, true));
            } else {
               boolean flag4 = !player2.onGround() || CrystalTargeting.isRecentlyTracked(player2);
               if (value19 > 0 && !this.m_3_boolean(aABB2)) {
                  AABB aABB3 = CrystalTargeting.m_3(player2, value19 + (flag4 ? Math.max(value18, CrystalInteraction.getPing() + 2) : value18));
                  if (aABB3 != null && aABB3.getXsize() >= 0.1 && aABB3.getYsize() >= 0.1 && aABB3.getZsize() >= 0.1) {
                     aABB2 = aABB3;
                  }
               }

               list.add(new CrystalPlacer.DataRecord4(player2, aABB2, flag4));
            }
         }

         this.c5List = list;
         this.value15 = MinecraftAccess.MINECRAFT.player.tickCount;
         this.value16 = value18;
         return list;
      } finally {
         C02.recordSince(longValue2);
      }
   }

   private void resetAll() {
      this.c9 = null;
      this.stopMining();
      this.hotbarSwitcher.m_8_void();
      this.reset();
      this.blockPosMap4.clear();
      this.blockPosMap3.clear();
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.blockPos4 = null;
      this.blockPos5 = null;
      this.blockPos6 = null;
      this.blockPos7 = null;
      this.blockPos8 = null;
      this.value6 = -2;
      this.itemEntityList = List.of();
      this.value10 = 0;
      this.value11 = 0;
      this.integerMap2.clear();
      this.integerMap3.clear();
      this.blockPosMap.clear();
      this.blockPosMap2.clear();
      this.integerMap.clear();
      this.value4 = -1;
      if (MinecraftAccess.MINECRAFT.isSameThread()) {
         this.c6List.clear();
      } else {
         this.flag3 = true;
      }

      this.blockPos9 = null;
      this.value14 = -1;
      this.playerList = List.of();
      this.playerList2 = List.of();
      this.value17 = -1;
      this.value15 = -1;
      this.value16 = Integer.MIN_VALUE;
      this.c5List = null;
      this.queue.clear();
   }

   static {
      m_9_void(null);
   }

   public static void m_9_void(String text2) {
      text = text2;
   }

   public static String m_1_String() {
      return text;
   }

   private record DataRecord(int f_95, int f_99, int f_9b) {
      public int m_95() {
         return this.f_95;
      }

      public int m_99() {
         return this.f_99;
      }

      public int m_9b() {
         return this.f_9b;
      }
   }

   private enum ModeKind {
      OBSIDIAN,
      CRYSTAL,
      ATTACK,
      DIG,
      WEB;

      private static String a(byte[] byteArray) {
         int value18 = 0;
         int value19;
         char[] charArray = new char[value19 = byteArray.length];

         for (int i = 0; i < value19; i++) {
            int value20;
            if ((value20 = 255 & byteArray[i]) < 192) {
               charArray[value18++] = (char)value20;
            } else if (value20 < 224) {
               char ch = (char)((char)(value20 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value18++] = ch;
            } else if (i < value19 - 2) {
               char ch2 = (char)((char)(value20 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value18++] = ch2;
            }
         }

         return new String(charArray, 0, value18);
      }
   }

   private record DataRecord2(BlockPos f_6_s, boolean f_1i, long f_5j) {
      public BlockPos m_6_s() {
         return this.f_6_s;
      }

      public boolean m_1i_boolean() {
         return this.f_1i;
      }

      public long m_5j() {
         return this.f_5j;
      }
   }

   private record DataRecord3(CrystalAura.ModeKind f_4f, BlockPos f_4_t, CrystalInteraction.DataRecord f_1w, EndCrystal f_5h, int f_2b) {
      public CrystalAura.ModeKind m_4f() {
         return this.f_4f;
      }

      public BlockPos m_4_t_BlockPos() {
         return this.f_4_t;
      }

      public CrystalInteraction.DataRecord m_1w_C2() {
         return this.f_1w;
      }

      public EndCrystal m_5h() {
         return this.f_5h;
      }

      public int m_2b() {
         return this.f_2b;
      }
   }
}
