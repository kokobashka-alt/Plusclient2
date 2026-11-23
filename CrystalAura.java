package wtf.onetap.module.combat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
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
import wtf.onetap.util.LatencyStats;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.TickExecutor;

@ModuleInformation(
   name = "CrystalAura",
   description = "Продвинутая боевая система кристаллов с предиктом, авто-обсидианом и умным подкопом",
   category = Category.COMBAT
)
public class CrystalAura extends Module {

   // =========================================================================
   // СТАТИСТИКА ПРОИЗВОДИТЕЛЬНОСТИ
   // =========================================================================
   private static final LatencyStats.StateHolder STAT_TARGETS = LatencyStats.getOrCreate("CrystalAura targets");
   private static final LatencyStats.StateHolder STAT_SEARCH  = LatencyStats.getOrCreate("CrystalAura search");
   private static final LatencyStats.StateHolder STAT_AIM     = LatencyStats.getOrCreate("CrystalAura aim");
   private static final LatencyStats.StateHolder STAT_EXEC    = LatencyStats.getOrCreate("CrystalAura execute");
   private static final LatencyStats.StateHolder STAT_ASYNC   = LatencyStats.getOrCreate("CrystalAura async");

   private static final double[] ROTATION_SCRATCH = new double[32];
   private static final Direction[] HORIZONTAL_DIRS = new Direction[]{
      Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
   };
   private static final Direction[] ALL_DIRS = Direction.values();

   // =========================================================================
   // НАСТРОЙКИ МОДУЛЯ
   // =========================================================================
   private final FloatSetting targetRange      = new FloatSetting("Дистанция целей", 10.0F, 4.0F, 16.0F, 0.5F).build();
   private final FloatSetting placeRange       = new FloatSetting("Дальность установки", 4.5F, 2.5F, 6.0F, 0.1F).build();
   private final FloatSetting breakRange       = new FloatSetting("Дальность взрыва", 4.0F, 2.0F, 6.0F, 0.1F).build();
   private final FloatSetting wallRange        = new FloatSetting("Дальность сквозь стены", 3.0F, 0.0F, 5.0F, 0.1F).build();

   private final FloatSetting minEnemyDamage   = new FloatSetting("Мин. урон врагу", 5.5F, 1.0F, 20.0F, 0.5F).build();
   private final FloatSetting maxSelfDamage    = new FloatSetting("Макс. урон себе", 8.0F, 0.0F, 20.0F, 0.5F).build();
   private final FloatSetting lethalHealth     = new FloatSetting("Порог добивания HP", 8.0F, 2.0F, 20.0F, 0.5F).build();
   private final FloatSetting antiSuicideMargin= new FloatSetting("Запас анти-суицида", 2.0F, 0.5F, 8.0F, 0.5F).build();

   private final FloatSetting placeDelayTicks  = new FloatSetting("Задержка установки (тики)", 0.0F, 0.0F, 10.0F, 1.0F).build();
   private final FloatSetting breakDelayTicks  = new FloatSetting("Задержка взрыва (тики)", 0.0F, 0.0F, 10.0F, 1.0F).build();
   private final FloatSetting predictTicks     = new FloatSetting("Предикт движения", 2.0F, 0.0F, 6.0F, 1.0F).build();
   private final FloatSetting digRange         = new FloatSetting("Дальность подкопа", 4.5F, 2.5F, 6.0F, 0.1F).build();

   private final BooleanSetting autoPlace      = new BooleanSetting("Авто установка", true).build();
   private final BooleanSetting autoBreak      = new BooleanSetting("Авто взрыв", true).build();
   private final BooleanSetting autoObsidian   = new BooleanSetting("Авто обсидиан", true).build();
   private final BooleanSetting autoDigUnder   = new BooleanSetting("Авто подкоп", true).build();
   private final BooleanSetting digSmartTool   = new BooleanSetting("Умный выбор инструмента", true).build();
   private final BooleanSetting digSurround    = new BooleanSetting("Копать защиту врага", true).build();
   private final BooleanSetting digFloor       = new BooleanSetting("Копать пол под врагом", true).build();
   private final BooleanSetting packetInstant  = new BooleanSetting("Мгновенный взрыв пакетом", true).build();
   private final BooleanSetting predictPlace   = new BooleanSetting("Ре-плейс после взрыва", true).build();
   private final BooleanSetting silentSwitch   = new BooleanSetting("Тихий свап слота", true).build();
   private final BooleanSetting antiWeakness   = new BooleanSetting("Анти-слабость", true).build();
   private final BooleanSetting dontBlowItems  = new BooleanSetting("Не взрывать ресурсы", true).build();
   private final BooleanSetting rotateEnabled  = new BooleanSetting("Ротация", true).build();
   private final BooleanSetting movementFix    = new BooleanSetting("Коррекция движения", true).build();
   private final BooleanSetting renderVisuals  = new BooleanSetting("Рендер позиций", true).build();

   // =========================================================================
   // ВНУТРЕННИЕ СОСТОЯНИЯ И КЭШИ
   // =========================================================================
   private final AtomicBoolean asyncRunning = new AtomicBoolean(false);
   private volatile long asyncStartNanos = 0L;
   private boolean loggedAsyncError = false;

   private ClientLevel lastLevel = null;
   private int lastPlayerTick = -1;

   // Кэш целей и позиций
   private volatile List<TargetProfile> activeTargets = List.of();
   private volatile List<Player> friendPlayers = List.of();
   private volatile List<ItemEntity> nearbyLoot = List.of();
   private final Map<Integer, MotionHistory> entityMotionMap = new ConcurrentHashMap<>();

   // Таймеры и кулдауны действий
   private int lastPlaceTick = -100;
   private int lastBreakTick = -100;
   private int lastObsidianTick = -100;

   // Учёт атакованных / поставленных кристаллов и обсидиана
   private final Map<Integer, Integer> attackedCrystals = new ConcurrentHashMap<>();
   private final Map<Integer, Integer> failedAttackAttempts = new ConcurrentHashMap<>();
   private final Map<BlockPos, Integer> pendingCrystalPlaces = new ConcurrentHashMap<>();
   private final Map<BlockPos, Integer> pendingObsidianPlaces = new ConcurrentHashMap<>();
   private final Map<BlockPos, Integer> blacklistedBlocks = new ConcurrentHashMap<>();
   private final Map<BlockPos, Integer> blacklistedDigBlocks = new ConcurrentHashMap<>();

   // Текущая запланированная задача на тик
   private volatile CombatTask currentTask = null;
   private volatile BlockPos currentAnchorBase = null;
   private volatile BlockPos forcedRePlacePos = null;

   // Состояние авто-подкопа (AutoDig)
   private volatile DigTask activeDigTask = null;
   private int savedSlotBeforeAction = -1;
   private boolean isSilentlySwapped = false;

   // Состояние ротации
   private volatile Rotations targetRotation = null;
   private volatile Rotations appliedRotation = null;
   private int rotationAppliedTick = -1;
   private int rotationHoldTicks = 0;

   // Визуализация (Render3D)
   private final Queue<RenderMarker> renderQueue = new ConcurrentLinkedQueue<>();
   private final List<RenderMarker> activeMarkers = new ArrayList<>();
   private volatile BlockPos renderDigPos = null;
   private volatile float renderDigProgress = 0.0F;
   private volatile long renderDigTimestamp = 0L;

   // Внешняя интеграция
   private AutoAnchor.DataRecord sharedAnchorLock = null;
   private static String debugStatusText = "IDLE";

   // =========================================================================
   // ЖИЗНЕННЫЙ ЦИКЛ МОДУЛЯ
   // =========================================================================

   public boolean isBusy() {
      return this.isEnabled() && (this.currentTask != null || this.activeDigTask != null || this.targetRotation != null);
   }

   public AutoAnchor.DataRecord m_3_C4() {
      return this.isEnabled() ? this.sharedAnchorLock : null;
   }

   public static String m_1_String() {
      return debugStatusText;
   }

   public static void m_9_void(String status) {
      debugStatusText = status;
   }

   @Override
   public void onEnable() {
      this.asyncRunning.set(false);
      this.loggedAsyncError = false;
      this.resetAllState();
   }

   @Override
   public void onDisable() {
      TickExecutor.m_0_boolean();
      this.asyncRunning.set(false);
      this.restoreHotbarSlot();
      this.resetAllState();
   }

   // =========================================================================
   // ГЛАВНЫЕ СОБЫТИЯ КЛИЕНТА
   // =========================================================================

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      Minecraft mc = MinecraftAccess.MINECRAFT;
      if (mc.player == null || mc.level == null) {
         return;
      }

      // Защита от зависшего асинхронного потока (> 60 мс)
      if (this.asyncRunning.get() && (System.nanoTime() - this.asyncStartNanos) > 60_000_000L) {
         TickExecutor.m_0_boolean();
         this.asyncRunning.set(false);
      }

      this.flushRenderQueue();
      this.maintainRotation();

      if (this.currentTask == null && this.activeDigTask == null) {
         this.restoreHotbarSlot();
      }
   }

   /**
    * Запускается планировщиком клиента для фонового поиска целей и расчёта позиций.
    */
   public void scheduleAsync() {
      if (!this.isEnabled()) {
         return;
      }
      Minecraft mc = MinecraftAccess.MINECRAFT;
      if (mc.player == null || mc.level == null) {
         return;
      }
      if (!this.asyncRunning.compareAndSet(false, true)) {
         return;
      }

      this.asyncStartNanos = System.nanoTime();
      TickExecutor.m_4(() -> {
         try {
            if (this.isEnabled()) {
               this.runCombatPlanner();
            }
         } catch (Throwable t) {
            if (!this.loggedAsyncError) {
               this.loggedAsyncError = true;
               Onetap.LOGGER.error("CrystalAura: ошибка в планировщике расчётов", t);
            }
         } finally {
            this.asyncRunning.set(false);
            STAT_ASYNC.recordSince(this.asyncStartNanos);
         }
      });
   }

   @EventTarget(priority = EventPriority.LOWEST)
   private void onPreMotion(PreMotionEvent event) {
      Minecraft mc = MinecraftAccess.MINECRAFT;
      if (mc.player == null || mc.level == null || mc.gameMode == null) {
         return;
      }

      // Если асинхронный планировщик не был вызван снаружи в этом тике — запускаем синхронный фоллбэк
      if (this.lastPlayerTick != mc.player.tickCount && !this.asyncRunning.get()) {
         this.runCombatPlanner();
      }

      if (mc.player.isUsingItem() || mc.player.isSpectator() || this.isExternalCombatModuleBusy()) {
         this.abortCurrentExecution();
         return;
      }

      long execStart = System.nanoTime();
      try {
         this.executeScheduledTask(mc);
      } finally {
         STAT_EXEC.recordSince(execStart);
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      Minecraft mc = MinecraftAccess.MINECRAFT;
      if (mc.player == null || mc.level == null) {
         return;
      }

      Object packet = event.getPacket();

      // 1. Обновление скорости сущностей для точного предикта
      if (packet instanceof ClientboundSetEntityMotionPacket motionPacket) {
         if (motionPacket.id() != mc.player.getId()) {
            this.entityMotionMap.put(
               motionPacket.id(),
               new MotionHistory(motionPacket.movement(), mc.player.tickCount)
            );
         }
         return;
      }

      // 2. Мгновенная реакция на появление EndCrystal (Packet Instant Break)
      if (this.isEnabled() && this.packetInstant.getValue() && this.autoBreak.getValue()
         && packet instanceof ClientboundAddEntityPacket addPacket
         && addPacket.getType().getDescriptionId().equals("entity.minecraft.end_crystal")) {

         Vec3 crystalPos = new Vec3(addPacket.getX(), addPacket.getY(), addPacket.getZ());
         BlockPos basePos = BlockPos.containing(crystalPos).below();
         this.pendingCrystalPlaces.remove(basePos);

         Vec3 eyePos = mc.player.getEyePosition();
         double maxBreakSq = Mth.square(this.getEffectiveBreakRange(mc.player));
         if (eyePos.distanceToSqr(crystalPos.add(0.0, 1.0, 0.0)) <= maxBreakSq) {
            this.evaluatePacketCrystal(mc, addPacket.getId(), crystalPos);
         }
         return;
      }

      // 3. Очистка кэша при удалении сущностей
      if (packet instanceof ClientboundRemoveEntitiesPacket removePacket) {
         for (int id : removePacket.getEntityIds()) {
            this.attackedCrystals.remove(id);
            this.failedAttackAttempts.remove(id);
            this.entityMotionMap.remove(id);
         }
         return;
      }

      // 4. Обновление блоков (подтверждение установки обсидиана или разрушения блока при подкопе)
      if (packet instanceof ClientboundBlockUpdatePacket blockPacket) {
         BlockPos pos = blockPacket.getPos();
         BlockState newState = blockPacket.getBlockState();
         if (newState.is(Blocks.OBSIDIAN)) {
            this.pendingObsidianPlaces.remove(pos);
            this.blacklistedBlocks.remove(pos);
         } else if (newState.isAir() || newState.canBeReplaced()) {
            this.blacklistedDigBlocks.remove(pos);
            DigTask dig = this.activeDigTask;
            if (dig != null && dig.targetBlock.equals(pos)) {
               this.activeDigTask = null;
               this.forcedRePlacePos = dig.obsidianAnchor;
            }
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (this.movementFix.getValue() && MinecraftAccess.MINECRAFT.player != null && this.targetRotation != null) {
         LegacyCrystalAura.fixMovement(event);
      }
   }

   @EventTarget
   private void onContinueAttack(ContinueAttackEvent event) {
      if (this.activeDigTask != null) {
         event.cancel();
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      this.flushRenderQueue();
      Minecraft mc = MinecraftAccess.MINECRAFT;
      if (mc.player == null || mc.level == null) {
         return;
      }

      if (!this.renderVisuals.getValue()) {
         this.activeMarkers.clear();
         this.renderDigPos = null;
         return;
      }

      if (this.activeMarkers.isEmpty() && this.renderDigPos == null && this.currentAnchorBase == null) {
         return;
      }

      Matrix4f matrix = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
      Vec3 camPos = event.getVec3();
      long now = System.currentTimeMillis();

      Iterator<RenderMarker> it = this.activeMarkers.iterator();
      while (it.hasNext()) {
         RenderMarker marker = it.next();
         float lifeProgress = (float) (now - marker.createdAtMs) / 320.0F;
         if (lifeProgress >= 1.0F) {
            it.remove();
         } else {
            float scale = 0.45F + 0.55F * lifeProgress;
            float alpha = (1.0F - lifeProgress) * 0.42F;
            int color = switch (marker.kind) {
               case CRYSTAL -> 0xFFB432FF;
               case OBSIDIAN -> 0xFF2D8CFF;
               case DIG -> 0xFFFF5533;
            };
            drawBox(matrix, camPos, createCenteredBox(marker.pos, scale), color, alpha);
         }
      }

      if (this.renderDigPos != null) {
         long elapsed = now - this.renderDigTimestamp;
         if (elapsed > 350L) {
            this.renderDigPos = null;
         } else {
            float fade = elapsed <= 180L ? 1.0F : 1.0F - ((float) (elapsed - 180L) / 170.0F);
            float prog = Mth.clamp(this.renderDigProgress, 0.05F, 1.0F);
            drawBox(matrix, camPos, createCenteredBox(this.renderDigPos, 0.35F + 0.65F * prog), 0xFFFF781F, (0.35F + 0.45F * prog) * fade);
         }
      }
   }

   // =========================================================================
   // ЯДРО ПЛАНИРОВАНИЯ (TARGETING + DAMAGE + PLACE + BREAK + DIG)
   // =========================================================================

   private void runCombatPlanner() {
      Minecraft mc = MinecraftAccess.MINECRAFT;
      LocalPlayer player = mc.player;
      ClientLevel level = mc.level;

      if (player == null || level == null || mc.gameMode == null || player.isSpectator()) {
         this.resetAllState();
         return;
      }

      if (this.lastLevel != level || player.tickCount < this.lastPlayerTick) {
         this.resetAllState();
         this.lastLevel = level;
      }

      this.lastPlayerTick = player.tickCount;
      this.sharedAnchorLock = null;

      if (this.isExternalCombatModuleBusy() || player.isUsingItem()) {
         this.abortCurrentExecution();
         return;
      }

      this.cleanupExpiredEntries(level, player.tickCount);

      // 1. Сбор и предикт целей
      long targetTimer = System.nanoTime();
      this.refreshTargetsAndEntities(mc, player, level);
      STAT_TARGETS.recordSince(targetTimer);

      List<TargetProfile> targets = this.activeTargets;
      if (targets.isEmpty()) {
         this.abortCurrentExecution();
         this.currentAnchorBase = null;
         this.forcedRePlacePos = null;
         debugStatusText = "NO_TARGET";
         return;
      }

      long searchTimer = System.nanoTime();
      try {
         Vec3 eyePos = player.getEyePosition();
         float selfMaxDmg = this.getSafeSelfDamageLimit(player);
         float minEnemyDmg = this.minEnemyDamage.getFloat();

         // Проверяем активную задачу: если она ещё актуальна и не просрочена — продолжаем её
         CombatTask existing = this.currentTask;
         if (existing != null) {
            if (player.tickCount - existing.createdTick <= 3 && this.isTaskStillValid(mc, existing)) {
               if (existing.aimPoint != null) {
                  this.requestRotationTo(player, existing.aimPoint);
               }
               return;
            } else {
               this.currentTask = null;
            }
         }

         // 2. ПРИОРИТЕТ №1: ВЗРЫВ УЖЕ СУЩЕСТВУЮЩЕГО ВЫГОДНОГО КРИСТАЛЛА
         if (this.autoBreak.getValue()) {
            CrystalCandidate bestCrystal = this.findBestCrystalToBreak(mc, player, level, eyePos, targets, minEnemyDmg, selfMaxDmg);
            if (bestCrystal != null) {
               this.stopActiveDigging(mc);
               this.sharedAnchorLock = AutoAnchor.DataRecord.f_1;

               if (this.canBreakNow(player.tickCount)) {
                  this.currentTask = new CombatTask(
                     TaskType.BREAK_CRYSTAL,
                     bestCrystal.crystal.blockPosition(),
                     bestCrystal.hitVec,
                     null,
                     bestCrystal.crystal,
                     bestCrystal.enemyDamage,
                     bestCrystal.selfDamage,
                     player.tickCount
                  );
                  this.requestRotationTo(player, bestCrystal.hitVec);
                  debugStatusText = String.format("BREAK (%.1f dmg)", bestCrystal.enemyDamage);
                  return;
               } else {
                  // Заранее наводимся на кристалл, пока тикает задержка
                  this.requestRotationTo(player, bestCrystal.hitVec);
                  debugStatusText = "AIM_BREAK";
                  return;
               }
            }
         }

         // 3. ПРИОРИТЕТ №2: МГНОВЕННЫЙ РЕ-ПЛЕЙС ПОСЛЕ ВЗРЫВА (ЕСЛИ ПОЗИЦИЯ ГОТОВА)
         BlockPos rePlace = this.forcedRePlacePos;
         if (rePlace != null && this.autoPlace.getValue() && this.hasCrystalInHotbar(player)) {
            this.forcedRePlacePos = null;
            PlaceCandidate quickCandidate = this.evaluateSinglePosition(
               mc, player, level, eyePos, rePlace, targets, minEnemyDmg, selfMaxDmg, false, false
            );
            if (quickCandidate != null && !quickCandidate.needsObsidian && !quickCandidate.needsDig) {
               this.stopActiveDigging(mc);
               this.currentAnchorBase = quickCandidate.obsidianPos;
               this.currentTask = new CombatTask(
                  TaskType.PLACE_CRYSTAL,
                  quickCandidate.obsidianPos,
                  quickCandidate.aimVec,
                  quickCandidate.hitResult,
                  null,
                  quickCandidate.enemyDamage,
                  quickCandidate.selfDamage,
                  player.tickCount
               );
               this.requestRotationTo(player, quickCandidate.aimVec);
               debugStatusText = String.format("RE-PLACE (%.1f dmg)", quickCandidate.enemyDamage);
               return;
            }
         }

         // 4. ПРИОРИТЕТ №3: ПОИСК ЛУЧШЕЙ ПОЗИЦИИ ДЛЯ КРИСТАЛЛА / ОБСИДИАНА
         boolean hasCrystal = this.hasCrystalInHotbar(player);
         boolean hasObsidian = this.autoObsidian.getValue() && this.hasObsidianInHotbar(player);

         if (this.autoPlace.getValue() && hasCrystal) {
            PlaceCandidate bestPlace = this.findBestPlacement(
               mc, player, level, eyePos, targets, minEnemyDmg, selfMaxDmg, hasObsidian, false
            );

            if (bestPlace != null && !bestPlace.needsDig) {
               this.stopActiveDigging(mc);
               this.currentAnchorBase = bestPlace.obsidianPos;

               if (bestPlace.needsObsidian) {
                  if (player.tickCount - this.lastObsidianTick >= 1) {
                     this.currentTask = new CombatTask(
                        TaskType.PLACE_OBSIDIAN,
                        bestPlace.obsidianPos,
                        bestPlace.aimVec,
                        bestPlace.hitResult,
                        null,
                        bestPlace.enemyDamage,
                        bestPlace.selfDamage,
                        player.tickCount
                     );
                     this.requestRotationTo(player, bestPlace.aimVec);
                     debugStatusText = "PLACE_OBSI";
                     return;
                  }
               } else if (this.canPlaceNow(player.tickCount)) {
                  this.currentTask = new CombatTask(
                     TaskType.PLACE_CRYSTAL,
                     bestPlace.obsidianPos,
                     bestPlace.aimVec,
                     bestPlace.hitResult,
                     null,
                     bestPlace.enemyDamage,
                     bestPlace.selfDamage,
                     player.tickCount
                  );
                  this.requestRotationTo(player, bestPlace.aimVec);
                  debugStatusText = String.format("PLACE (%.1f dmg)", bestPlace.enemyDamage);
                  return;
               } else {
                  this.requestRotationTo(player, bestPlace.aimVec);
                  debugStatusText = "AIM_PLACE";
                  return;
               }
            }
         }

         // 5. ПРИОРИТЕТ №4: АВТО-ПОДКОП (КИРКА / ЛОПАТА / ТОПОР / МЕЧ)
         if (this.autoDigUnder.getValue() && hasCrystal) {
            DigTask digCandidate = this.findBestDigTarget(mc, player, level, eyePos, targets, minEnemyDmg, selfMaxDmg, hasObsidian);
            if (digCandidate != null) {
               this.activeDigTask = digCandidate;
               this.currentAnchorBase = digCandidate.obsidianAnchor;
               this.currentTask = new CombatTask(
                  TaskType.DIG_BLOCK,
                  digCandidate.targetBlock,
                  digCandidate.hitVec,
                  digCandidate.hitResult,
                  null,
                  digCandidate.potentialDamage,
                  0.0F,
                  player.tickCount
               );
               this.requestRotationTo(player, digCandidate.hitVec);
               debugStatusText = "DIG_" + digCandidate.toolCategory.name();
               return;
            }
         }

         // Если ничего делать не нужно — останавливаем подкоп и сбрасываем ротацию
         this.stopActiveDigging(mc);
         if (this.rotationHoldTicks > 6) {
            this.clearRotation();
         }
         debugStatusText = "SEARCHING";

      } finally {
         STAT_SEARCH.recordSince(searchTimer);
      }
   }

   // =========================================================================
   // ВЫПОЛНЕНИЕ ЗАДАЧ В ТИКЕ ИГРОКА (PRE-MOTION)
   // =========================================================================

   private void executeScheduledTask(Minecraft mc) {
      LocalPlayer player = mc.player;
      ClientLevel level = mc.level;
      if (player == null || level == null || mc.gameMode == null) {
         return;
      }

      CombatTask task = this.currentTask;
      if (task == null) {
         return;
      }

      // Проверка готовности ротации
      if (this.rotateEnabled.getValue()) {
         if (task.aimPoint != null) {
            this.requestRotationTo(player, task.aimPoint);
         }
         if (!this.isRotationAligned(task.aimPoint)) {
            return;
         }
      }

      Vec3 eyePos = player.getEyePosition();

      switch (task.type) {
         case BREAK_CRYSTAL -> {
            this.currentTask = null;
            EndCrystal crystal = task.crystal;
            if (crystal == null || !crystal.isAlive()) {
               return;
            }
            if (eyePos.distanceToSqr(crystal.position().add(0.0, 1.0, 0.0)) > Mth.square(this.getEffectiveBreakRange(player))) {
               return;
            }
            if (this.dontBlowItems.getValue() && this.isLootNearExplosion(crystal.position())) {
               return;
            }

            // Если у игрока эффект Слабости — переключаемся на меч/топор
            int weaponSlot = -1;
            if (this.antiWeakness.getValue() && player.hasEffect(MobEffects.WEAKNESS)) {
               weaponSlot = this.findBestWeaponSlot(player.getInventory());
            }
            if (weaponSlot >= 0) {
               this.swapToHotbarSlot(player, weaponSlot);
            }

            mc.gameMode.attack(player, crystal);
            player.swing(InteractionHand.MAIN_HAND);

            this.attackedCrystals.put(crystal.getId(), player.tickCount + this.getPingTicks() + 3);
            this.lastBreakTick = player.tickCount;

            if (weaponSlot >= 0) {
               this.restoreHotbarSlot();
            }

            // Мгновенно планируем ре-плейс на блок под взорванным кристаллом
            if (this.predictPlace.getValue()) {
               this.forcedRePlacePos = crystal.blockPosition().below();
            }
         }

         case PLACE_CRYSTAL -> {
            this.currentTask = null;
            BlockPos basePos = task.targetPos;
            if (this.pendingCrystalPlaces.containsKey(basePos) || !this.canSupportCrystal(level, basePos, false)) {
               return;
            }

            int crystalSlot = this.findItemInHotbar(player.getInventory(), Items.END_CRYSTAL);
            if (crystalSlot < 0) {
               return;
            }

            BlockHitResult hit = this.raycastBlockForPlacement(level, eyePos, basePos, this.placeRange.getFloat(), true);
            if (hit == null) {
               this.blacklistedBlocks.put(basePos, player.tickCount + 6);
               return;
            }

            this.swapToHotbarSlot(player, crystalSlot);
            InteractionResult result = mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
            if (result.consumesAction()) {
               player.swing(InteractionHand.MAIN_HAND);
               this.pendingCrystalPlaces.put(basePos, player.tickCount);
               this.lastPlaceTick = player.tickCount;
               this.addRenderMarker(basePos.above(), RenderKind.CRYSTAL);
            } else {
               this.blacklistedBlocks.put(basePos, player.tickCount + 6);
            }

            if (this.silentSwitch.getValue()) {
               this.restoreHotbarSlot();
            }
         }

         case PLACE_OBSIDIAN -> {
            this.currentTask = null;
            BlockPos obsiPos = task.targetPos;
            BlockState current = level.getBlockState(obsiPos);
            if (!current.canBeReplaced() || !current.getFluidState().isEmpty()) {
               return;
            }

            int obsiSlot = this.findItemInHotbar(player.getInventory(), Items.OBSIDIAN);
            if (obsiSlot < 0) {
               return;
            }

            BlockHitResult hit = this.findPlaceableNeighborFace(level, eyePos, obsiPos, this.placeRange.getFloat());
            if (hit == null) {
               this.blacklistedBlocks.put(obsiPos, player.tickCount + 8);
               return;
            }

            this.swapToHotbarSlot(player, obsiSlot);
            InteractionResult result = mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
            if (result.consumesAction()) {
               player.swing(InteractionHand.MAIN_HAND);
               this.pendingObsidianPlaces.put(obsiPos, player.tickCount);
               this.lastObsidianTick = player.tickCount;
               this.addRenderMarker(obsiPos, RenderKind.OBSIDIAN);
            } else {
               this.blacklistedBlocks.put(obsiPos, player.tickCount + 6);
            }

            if (this.silentSwitch.getValue()) {
               this.restoreHotbarSlot();
            }
         }

         case DIG_BLOCK -> {
            this.currentTask = null;
            DigTask dig = this.activeDigTask;
            if (dig == null) {
               return;
            }

            BlockPos digPos = dig.targetBlock;
            BlockState state = level.getBlockState(digPos);

            // Если блок уже сломан или заменён воздухом — завершаем подкоп и готовим ре-плейс
            if (state.isAir() || state.canBeReplaced()) {
               this.activeDigTask = null;
               this.forcedRePlacePos = dig.obsidianAnchor;
               this.restoreHotbarSlot();
               return;
            }

            // Проверка на неразрушимые блоки
            if (state.getDestroySpeed(level, digPos) < 0.0F) {
               this.blacklistedDigBlocks.put(digPos, player.tickCount + 100);
               this.stopActiveDigging(mc);
               return;
            }

            // Проверка дистанции
            if (eyePos.distanceToSqr(Vec3.atCenterOf(digPos)) > Mth.square(this.digRange.getFloat())) {
               this.stopActiveDigging(mc);
               return;
            }

            // Умный выбор инструмента (кирка / лопата / топор / меч)
            if (this.digSmartTool.getValue()) {
               int bestToolSlot = this.findBestToolSlotForBlock(player.getInventory(), state);
               if (bestToolSlot >= 0) {
                  this.swapToHotbarSlot(player, bestToolSlot);
               }
            }

            BlockHitResult hit = this.raycastBlockToBreak(level, eyePos, digPos, this.digRange.getFloat());
            if (hit == null) {
               this.blacklistedDigBlocks.put(digPos, player.tickCount + 10);
               this.stopActiveDigging(mc);
               return;
            }

            MultiPlayerGameModeAccessor gmAccessor = (MultiPlayerGameModeAccessor) mc.gameMode;
            if (gmAccessor.onetap$getDestroyDelay() > 0) {
               gmAccessor.onetap$setDestroyDelay(0);
            }

            if (mc.gameMode.continueDestroyBlock(digPos, hit.getDirection())) {
               player.swing(InteractionHand.MAIN_HAND);
            }

            float progress = Mth.clamp(gmAccessor.onetap$getDestroyProgress(), 0.0F, 1.0F);
            dig.lastProgress = progress;
            dig.lastActiveTick = player.tickCount;

            if (this.renderVisuals.getValue()) {
               this.renderDigPos = digPos;
               this.renderDigProgress = progress;
               this.renderDigTimestamp = System.currentTimeMillis();
            }

            // Если блок доломался в этом же тике
            if (level.getBlockState(digPos).isAir() || progress >= 1.0F) {
               this.activeDigTask = null;
               this.forcedRePlacePos = dig.obsidianAnchor;
               this.addRenderMarker(digPos, RenderKind.DIG);
               if (this.silentSwitch.getValue()) {
                  this.restoreHotbarSlot();
               }
            }
         }
      }
   }

   // =========================================================================
   // МОДУЛЬ АВТО-ПОДКОПА (AUTO-DIG: КИРКА / ЛОПАТА / ТОПОР / МЕЧ)
   // =========================================================================

   private DigTask findBestDigTarget(
      Minecraft mc,
      LocalPlayer player,
      ClientLevel level,
      Vec3 eyePos,
      List<TargetProfile> targets,
      float minEnemyDmg,
      float maxSelfDmg,
      boolean hasObsidian
   ) {
      DigTask currentDig = this.activeDigTask;
      if (currentDig != null) {
         BlockState st = level.getBlockState(currentDig.targetBlock);
         boolean withinRange = eyePos.distanceToSqr(Vec3.atCenterOf(currentDig.targetBlock)) <= Mth.square(this.digRange.getFloat());
         boolean notTimedOut = (player.tickCount - currentDig.lastActiveTick) <= 12;

         if (!st.isAir() && !st.canBeReplaced() && st.getDestroySpeed(level, currentDig.targetBlock) >= 0.0F && withinRange && notTimedOut) {
            BlockHitResult hit = this.raycastBlockToBreak(level, eyePos, currentDig.targetBlock, this.digRange.getFloat());
            if (hit != null) {
               currentDig.hitVec = hit.getLocation();
               currentDig.hitResult = hit;
               return currentDig;
            }
         }
         this.stopActiveDigging(mc);
      }

      DigTask bestTask = null;
      double bestScore = -1.0;
      double maxDigDistSq = Mth.square(this.digRange.getFloat());

      for (TargetProfile target : targets) {
         BlockPos enemyFeet = BlockPos.containing(target.predictedBox.getCenter().x, target.predictedBox.minY + 0.1, target.predictedBox.getCenter().z);

         // Кандидаты на раскопку вокруг врага:
         // 1) Сурраунд (4 блока вокруг ног врага, мешающие поставить/взорвать кристалл рядом)
         // 2) Пол под врагом (если враг стоит в яме или на блоке, который можно выкопать)
         List<BlockPos> candidateBlocks = new ArrayList<>(12);

         if (this.digSurround.getValue()) {
            for (Direction dir : HORIZONTAL_DIRS) {
               BlockPos sidePos = enemyFeet.relative(dir);
               candidateBlocks.add(sidePos);
               candidateBlocks.add(sidePos.below());
            }
         }

         if (this.digFloor.getValue()) {
            candidateBlocks.add(enemyFeet.below());
            candidateBlocks.add(enemyFeet);
         }

         for (BlockPos blockToDig : candidateBlocks) {
            if (this.blacklistedDigBlocks.containsKey(blockToDig)) {
               continue;
            }
            if (eyePos.distanceToSqr(Vec3.atCenterOf(blockToDig)) > maxDigDistSq) {
               continue;
            }

            BlockState state = level.getBlockState(blockToDig);
            if (state.isAir() || state.canBeReplaced() || !state.getFluidState().isEmpty()) {
               continue;
            }

            float hardness = state.getDestroySpeed(level, blockToDig);
            if (hardness < 0.0F) {
               continue; // Бедрок, барьер и т.д.
            }

            // Определяем, какой блок станет опорой для кристалла после выкапывания blockToDig
            BlockPos anchorPos = this.resolveAnchorAfterDig(level, blockToDig, hasObsidian);
            if (anchorPos == null) {
               continue;
            }

            // Проверяем, сможем ли мы достать до опоры после раскопки
            if (eyePos.distanceToSqr(Vec3.atCenterOf(anchorPos)) > Mth.square(this.placeRange.getFloat())) {
               continue;
            }

            // Проверяем инструмент и скорость копания
            ToolCategory category = this.classifyBlockTool(state);
            int toolSlot = this.findBestToolSlotForBlock(player.getInventory(), state);
            float digSpeedFactor = this.estimateDigEfficiency(player.getInventory(), toolSlot, state);

            // Если блок слишком твёрдый (например, обсидиан без кирки), пропускаем
            if (hardness >= 20.0F && digSpeedFactor < 4.0F) {
               continue;
            }

            // Симулируем урон от кристалла на anchorPos, если blockToDig будет убран (игнорируем blockToDig в трассировке)
            Vec3 crystalExplosionPos = new Vec3(anchorPos.getX() + 0.5, anchorPos.getY() + 1.0, anchorPos.getZ() + 0.5);
            float selfDmg = this.calculateExplosionDamageIgnoringBlock(level, crystalExplosionPos, player, player.getBoundingBox(), blockToDig);
            if (selfDmg > maxSelfDmg) {
               continue;
            }

            float enemyDmg = this.calculateExplosionDamageIgnoringBlock(level, crystalExplosionPos, target.player, target.predictedBox, blockToDig);
            boolean lethal = enemyDmg >= (target.effectiveHealth - 1.0F);
            if (enemyDmg < minEnemyDmg && !lethal) {
               continue;
            }

            BlockHitResult hit = this.raycastBlockToBreak(level, eyePos, blockToDig, this.digRange.getFloat());
            if (hit == null) {
               continue;
            }

            // Итоговый приоритет: урон * скорость копания - урон себе
            double timePenalty = Math.max(0.2, hardness / Math.max(1.0F, digSpeedFactor));
            double score = (enemyDmg * 2.5 - selfDmg * 1.1) / timePenalty + (lethal ? 35.0 : 0.0);

            if (score > bestScore) {
               bestScore = score;
               bestTask = new DigTask(
                  blockToDig.immutable(),
                  anchorPos.immutable(),
                  hit.getLocation(),
                  hit,
                  category,
                  enemyDmg,
                  player.tickCount
               );
            }
         }
      }

      return bestTask;
   }

   /**
    * Определяет, куда встанет кристалл после разрушения blockToDig.
    */
   private BlockPos resolveAnchorAfterDig(ClientLevel level, BlockPos blockToDig, boolean hasObsidian) {
      // Вариант А: blockToDig закрывал пространство НАД обсидианом/бедроком (например, паутина, сундук, ступенька, полублок, камень на полу)
      BlockPos below = blockToDig.below();
      BlockState belowState = level.getBlockState(below);
      if (belowState.is(Blocks.OBSIDIAN) || belowState.is(Blocks.BEDROCK)) {
         // После слома blockToDig над below должно быть свободно 2 блока (включая сам blockToDig)
         BlockState aboveDig = level.getBlockState(blockToDig.above());
         if (aboveDig.isAir() || aboveDig.canBeReplaced()) {
            return below;
         }
      }

      // Вариант Б: мы выкапываем сам блок (например, землю/камень/доски), чтобы поставить на его место обсидиан, а сверху кристалл
      if (hasObsidian) {
         BlockState aboveDig = level.getBlockState(blockToDig.above());
         BlockState aboveTwo = level.getBlockState(blockToDig.above(2));
         boolean spaceClear = (aboveDig.isAir() || aboveDig.canBeReplaced()) && (aboveTwo.isAir() || aboveTwo.canBeReplaced());
         if (spaceClear && !this.isEntityBlockingCrystal(level, blockToDig.above())) {
            return blockToDig;
         }
      }

      return null;
   }

   private ToolCategory classifyBlockTool(BlockState state) {
      if (state.is(Blocks.COBWEB)) {
         return ToolCategory.SWORD;
      }
      if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
         return ToolCategory.PICKAXE;
      }
      if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
         return ToolCategory.SHOVEL;
      }
      if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
         return ToolCategory.AXE;
      }
      return ToolCategory.OTHER;
   }

   private int findBestToolSlotForBlock(Inventory inv, BlockState state) {
      int bestSlot = -1;
      float bestSpeed = 1.0F;

      for (int slot = 0; slot < 9; slot++) {
         ItemStack stack = inv.getItem(slot);
         if (stack.isEmpty()) {
            continue;
         }
         float speed = stack.getDestroySpeed(state);
         if (state.is(Blocks.COBWEB) && stack.is(ItemTags.SWORDS)) {
            speed += 15.0F;
         }
         if (speed > bestSpeed) {
            bestSpeed = speed;
            bestSlot = slot;
         }
      }

      return bestSlot;
   }

   private float estimateDigEfficiency(Inventory inv, int slot, BlockState state) {
      if (slot < 0) {
         return 1.0F;
      }
      ItemStack stack = inv.getItem(slot);
      return stack.isEmpty() ? 1.0F : stack.getDestroySpeed(state);
   }

   private int findBestWeaponSlot(Inventory inv) {
      int bestSlot = -1;
      float bestDamage = 0.0F;
      for (int slot = 0; slot < 9; slot++) {
         ItemStack stack = inv.getItem(slot);
         if (stack.is(ItemTags.SWORDS)) {
            return slot;
         }
         if (stack.getItem() instanceof AxeItem && bestDamage < 5.0F) {
            bestDamage = 5.0F;
            bestSlot = slot;
         }
      }
      return bestSlot;
   }

   // =========================================================================
   // ПОИСК ЛУЧШЕГО КРИСТАЛЛА ДЛЯ ВЗРЫВА И ПОЗИЦИИ ДЛЯ УСТАНОВКИ
   // =========================================================================

   private CrystalCandidate findBestCrystalToBreak(
      Minecraft mc,
      LocalPlayer player,
      ClientLevel level,
      Vec3 eyePos,
      List<TargetProfile> targets,
      float minEnemyDmg,
      float maxSelfDmg
   ) {
      double maxRange = this.getEffectiveBreakRange(player);
      double maxRangeSq = maxRange * maxRange;
      double wallRangeSq = Mth.square(this.wallRange.getFloat());

      AABB searchBox = player.getBoundingBox().inflate(maxRange + 1.0);
      List<EndCrystal> crystals = level.getEntitiesOfClass(EndCrystal.class, searchBox, EndCrystal::isAlive);

      CrystalCandidate best = null;
      double bestScore = -1.0;

      for (EndCrystal crystal : crystals) {
         int id = crystal.getId();
         if (this.attackedCrystals.containsKey(id)) {
            continue;
         }
         if (this.failedAttackAttempts.getOrDefault(id, 0) >= 3) {
            continue;
         }

         Vec3 crystalCenter = crystal.position().add(0.0, 1.0, 0.0);
         double distSq = eyePos.distanceToSqr(crystalCenter);
         if (distSq > maxRangeSq) {
            continue;
         }

         boolean visible = this.canSeePoint(level, eyePos, crystalCenter);
         if (!visible && distSq > wallRangeSq) {
            continue;
         }

         if (this.dontBlowItems.getValue() && this.isLootNearExplosion(crystal.position())) {
            continue;
         }

         float selfDmg = this.calculateExplosionDamage(level, crystal.position(), player, player.getBoundingBox());
         if (selfDmg > maxSelfDmg) {
            continue;
         }

         // Не взрываем союзников
         if (this.wouldHurtFriends(level, crystal.position(), maxSelfDmg)) {
            continue;
         }

         float maxEnemyDmg = 0.0F;
         boolean isLethal = false;

         for (TargetProfile target : targets) {
            float dmg = this.calculateExplosionDamage(level, crystal.position(), target.player, target.predictedBox);
            if (dmg > maxEnemyDmg) {
               maxEnemyDmg = dmg;
            }
            if (dmg >= target.effectiveHealth - 0.5F) {
               isLethal = true;
            }
         }

         if (maxEnemyDmg < minEnemyDmg && !isLethal) {
            continue;
         }

         double score = maxEnemyDmg * 2.2 - selfDmg * 1.15 - Math.sqrt(distSq) * 0.25 + (isLethal ? 50.0 : 0.0);
         if (score > bestScore) {
            bestScore = score;
            best = new CrystalCandidate(crystal, crystalCenter, maxEnemyDmg, selfDmg);
         }
      }

      return best;
   }

   private PlaceCandidate findBestPlacement(
      Minecraft mc,
      LocalPlayer player,
      ClientLevel level,
      Vec3 eyePos,
      List<TargetProfile> targets,
      float minEnemyDmg,
      float maxSelfDmg,
      boolean allowObsidian,
      boolean allowDig
   ) {
      float range = this.placeRange.getFloat();
      int radius = Mth.ceil(range);
      BlockPos center = BlockPos.containing(eyePos);

      PlaceCandidate best = null;
      double bestScore = -1.0;

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dy = -radius; dy <= radius; dy++) {
            for (int dz = -radius; dz <= radius; dz++) {
               BlockPos pos = center.offset(dx, dy, dz);
               PlaceCandidate candidate = this.evaluateSinglePosition(
                  mc, player, level, eyePos, pos, targets, minEnemyDmg, maxSelfDmg, allowObsidian, allowDig
               );
               if (candidate != null && candidate.score > bestScore) {
                  bestScore = candidate.score;
                  best = candidate;
               }
            }
         }
      }

      return best;
   }

   private PlaceCandidate evaluateSinglePosition(
      Minecraft mc,
      LocalPlayer player,
      ClientLevel level,
      Vec3 eyePos,
      BlockPos basePos,
      List<TargetProfile> targets,
      float minEnemyDmg,
      float maxSelfDmg,
      boolean allowObsidian,
      boolean allowDig
   ) {
      if (this.blacklistedBlocks.containsKey(basePos) || this.pendingCrystalPlaces.containsKey(basePos)) {
         return null;
      }

      double maxRangeSq = Mth.square(this.placeRange.getFloat());
      Vec3 blockCenter = Vec3.atCenterOf(basePos);
      if (eyePos.distanceToSqr(blockCenter) > maxRangeSq) {
         return null;
      }

      BlockState baseState = level.getBlockState(basePos);
      boolean isObsiOrBedrock = baseState.is(Blocks.OBSIDIAN) || baseState.is(Blocks.BEDROCK);
      boolean needsObsidian = false;

      if (!isObsiOrBedrock) {
         if (!allowObsidian || !baseState.canBeReplaced() || !baseState.getFluidState().isEmpty()) {
            return null;
         }
         if (this.pendingObsidianPlaces.containsKey(basePos)) {
            return null;
         }
         needsObsidian = true;
      }

      // Проверяем свободное место над опорой (2 блока для EndCrystal)
      BlockPos above1 = basePos.above();
      BlockPos above2 = basePos.above(2);
      if (!level.getBlockState(above1).isAir() || !level.getBlockState(above2).isAir()) {
         return null;
      }

      // Проверяем, не мешают ли сущности (игроки, предметы, другие кристаллы)
      if (this.isEntityBlockingCrystal(level, above1)) {
         return null;
      }

      Vec3 explosionOrigin = new Vec3(basePos.getX() + 0.5, basePos.getY() + 1.0, basePos.getZ() + 0.5);
      if (eyePos.distanceToSqr(explosionOrigin.add(0.0, 1.0, 0.0)) > Mth.square(this.getEffectiveBreakRange(player))) {
         return null;
      }

      if (this.dontBlowItems.getValue() && this.isLootNearExplosion(explosionOrigin)) {
         return null;
      }

      // Расчёт само-урона (с игнорированием basePos, если мы его только собираемся поставить)
      float selfDmg = needsObsidian
         ? this.calculateExplosionDamageWithVirtualObsidian(level, explosionOrigin, player, player.getBoundingBox(), basePos)
         : this.calculateExplosionDamage(level, explosionOrigin, player, player.getBoundingBox());

      if (selfDmg > maxSelfDmg) {
         return null;
      }

      if (this.wouldHurtFriends(level, explosionOrigin, maxSelfDmg)) {
         return null;
      }

      // Расчёт урона по врагам
      float maxEnemyDmg = 0.0F;
      boolean isLethal = false;
      double closestTargetDistSq = Double.MAX_VALUE;

      for (TargetProfile target : targets) {
         float dmg = needsObsidian
            ? this.calculateExplosionDamageWithVirtualObsidian(level, explosionOrigin, target.player, target.predictedBox, basePos)
            : this.calculateExplosionDamage(level, explosionOrigin, target.player, target.predictedBox);

         if (dmg > maxEnemyDmg) {
            maxEnemyDmg = dmg;
         }
         if (dmg >= target.effectiveHealth - 0.5F) {
            isLethal = true;
         }
         double dSq = explosionOrigin.distanceToSqr(target.predictedBox.getCenter());
         if (dSq < closestTargetDistSq) {
            closestTargetDistSq = dSq;
         }
      }

      if (maxEnemyDmg < minEnemyDmg && !isLethal) {
         return null;
      }

      BlockHitResult hitResult = needsObsidian
         ? this.findPlaceableNeighborFace(level, eyePos, basePos, this.placeRange.getFloat())
         : this.raycastBlockForPlacement(level, eyePos, basePos, this.placeRange.getFloat(), true);

      if (hitResult == null) {
         return null;
      }

      // Формула оценки позиции: баланс урона по врагу, безопасности для себя и скорости цикла
      double score = maxEnemyDmg * 2.4
         - selfDmg * 1.2
         - Math.sqrt(closestTargetDistSq) * 0.35
         - (needsObsidian ? 1.8 : 0.0)
         + (isLethal ? 60.0 : 0.0);

      return new PlaceCandidate(
         basePos.immutable(),
         hitResult.getLocation(),
         hitResult,
         maxEnemyDmg,
         selfDmg,
         needsObsidian,
         false,
         score
      );
   }

   private void evaluatePacketCrystal(Minecraft mc, int entityId, Vec3 crystalPos) {
      LocalPlayer player = mc.player;
      ClientLevel level = mc.level;
      if (player == null || level == null || mc.gameMode == null) {
         return;
      }
      if (this.attackedCrystals.containsKey(entityId)) {
         return;
      }
      if (this.dontBlowItems.getValue() && this.isLootNearExplosion(crystalPos)) {
         return;
      }

      float maxSelf = this.getSafeSelfDamageLimit(player);
      float selfDmg = this.calculateExplosionDamage(level, crystalPos, player, player.getBoundingBox());
      if (selfDmg > maxSelf) {
         return;
      }

      List<TargetProfile> targets = this.activeTargets;
      if (targets.isEmpty()) {
         return;
      }

      float maxEnemyDmg = 0.0F;
      boolean lethal = false;
      for (TargetProfile target : targets) {
         float dmg = this.calculateExplosionDamage(level, crystalPos, target.player, target.predictedBox);
         if (dmg > maxEnemyDmg) {
            maxEnemyDmg = dmg;
         }
         if (dmg >= target.effectiveHealth - 0.5F) {
            lethal = true;
         }
      }

      if (maxEnemyDmg < this.minEnemyDamage.getFloat() && !lethal) {
         return;
      }

      // Создаём временную обёртку или атакуем если ротация уже наведена
      Vec3 aimVec = crystalPos.add(0.0, 1.0, 0.0);
      if (!this.rotateEnabled.getValue() || this.isRotationAligned(aimVec)) {
         Entity entity = level.getEntity(entityId);
         if (entity instanceof EndCrystal crystal && crystal.isAlive()) {
            mc.gameMode.attack(player, crystal);
            player.swing(InteractionHand.MAIN_HAND);
            this.attackedCrystals.put(entityId, player.tickCount + this.getPingTicks() + 3);
            this.lastBreakTick = player.tickCount;
            if (this.predictPlace.getValue()) {
               this.forcedRePlacePos = BlockPos.containing(crystalPos).below();
            }
         }
      }
   }

   // =========================================================================
   // ТОЧНЫЙ РАСЧЁТ УРОНА ОТ ВЗРЫВА (VANILLA EXPLOSION + RAYCAST EXPOSURE)
   // =========================================================================

   private float calculateExplosionDamage(ClientLevel level, Vec3 explosionPos, LivingEntity target, AABB targetBox) {
      return this.computeExplosionInternal(level, explosionPos, target, targetBox, null, null);
   }

   private float calculateExplosionDamageIgnoringBlock(ClientLevel level, Vec3 explosionPos, LivingEntity target, AABB targetBox, BlockPos ignoredAirBlock) {
      return this.computeExplosionInternal(level, explosionPos, target, targetBox, ignoredAirBlock, null);
   }

   private float calculateExplosionDamageWithVirtualObsidian(ClientLevel level, Vec3 explosionPos, LivingEntity target, AABB targetBox, BlockPos virtualObsidianBlock) {
      return this.computeExplosionInternal(level, explosionPos, target, targetBox, null, virtualObsidianBlock);
   }

   private float computeExplosionInternal(
      ClientLevel level,
      Vec3 explosionPos,
      LivingEntity target,
      AABB box,
      BlockPos ignoredAirBlock,
      BlockPos virtualObsidianBlock
   ) {
      double explosionRadius = 12.0; // Сила взрыва EndCrystal = 6.0F -> радиус = 12.0
      double dist = explosionPos.distanceTo(box.getCenter());
      if (dist > explosionRadius) {
         return 0.0F;
      }

      double exposure = this.calculateExposure(level, explosionPos, box, ignoredAirBlock, virtualObsidianBlock);
      double impact = (1.0 - (dist / explosionRadius)) * exposure;
      float rawDamage = (float) ((impact * impact + impact) / 2.0 * 7.0 * explosionRadius + 1.0);

      if (rawDamage <= 0.0F) {
         return 0.0F;
      }

      return this.applyArmorAndResistance(level, target, rawDamage);
   }

   private double calculateExposure(
      ClientLevel level,
      Vec3 source,
      AABB box,
      BlockPos ignoredAirBlock,
      BlockPos virtualObsidianBlock
   ) {
      double stepX = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
      double stepY = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
      double stepZ = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);

      double offsetX = (1.0 - Math.floor(1.0 / stepX) * stepX) / 2.0;
      double offsetZ = (1.0 - Math.floor(1.0 / stepZ) * stepZ) / 2.0;

      if (stepX < 0.0 || stepY < 0.0 || stepZ < 0.0) {
         return 0.0;
      }

      int unblockedRays = 0;
      int totalRays = 0;

      for (double rx = 0.0; rx <= 1.0; rx += stepX) {
         for (double ry = 0.0; ry <= 1.0; ry += stepY) {
            for (double rz = 0.0; rz <= 1.0; rz += stepZ) {
               double px = Mth.lerp(rx, box.minX, box.maxX) + offsetX;
               double py = Mth.lerp(ry, box.minY, box.maxY);
               double pz = Mth.lerp(rz, box.minZ, box.maxZ) + offsetZ;
               Vec3 samplePoint = new Vec3(px, py, pz);

               if (!this.isRayObstructed(level, samplePoint, source, ignoredAirBlock, virtualObsidianBlock)) {
                  unblockedRays++;
               }
               totalRays++;
            }
         }
      }

      return totalRays == 0 ? 0.0 : (double) unblockedRays / (double) totalRays;
   }

   private boolean isRayObstructed(
      ClientLevel level,
      Vec3 start,
      Vec3 end,
      BlockPos ignoredAirBlock,
      BlockPos virtualObsidianBlock
   ) {
      if (ignoredAirBlock == null && virtualObsidianBlock == null) {
         BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, MinecraftAccess.MINECRAFT.player));
         return hit.getType() != HitResult.Type.MISS;
      }

      // Кастомный DDA-обход блоков для симуляции подкопа или виртуального обсидиана
      return BlockGetter.traverseBlocks(start, end, null, (ctx, pos) -> {
         if (pos.equals(ignoredAirBlock)) {
            return null;
         }
         if (pos.equals(virtualObsidianBlock)) {
            return Boolean.TRUE;
         }
         BlockState state = level.getBlockState(pos);
         if (state.isAir() || state.getCollisionShape(level, pos).isEmpty()) {
            return null;
         }
         VoxelShape shape = state.getCollisionShape(level, pos);
         BlockHitResult hit = shape.clip(start, end, pos);
         return hit != null ? Boolean.TRUE : null;
      }, ctx -> Boolean.FALSE);
   }

   private float applyArmorAndResistance(ClientLevel level, LivingEntity entity, float damage) {
      // Учёт сложности мира
      switch (level.getDifficulty()) {
         case PEACEFUL -> {
            return 0.0F;
         }
         case EASY -> damage = Math.min(damage / 2.0F + 1.0F, damage);
         case HARD -> damage = damage * 1.5F;
         default -> {
         }
      }

      // Учёт брони (упрощённая формула CombatRules Minecraft 1.21)
      float armor = entity.getArmorValue();
      float toughness = 4.0F; // Средняя твёрдость для алмазки/незеритки
      float f = 2.0F + toughness / 4.0F;
      float f1 = Mth.clamp(armor - damage / f, armor * 0.2F, 20.0F);
      damage *= (1.0F - f1 / 25.0F);

      // Учёт эффекта Сопротивления (Resistance)
      if (entity.hasEffect(MobEffects.RESISTANCE)) {
         int amp = (entity.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
         float j = Math.max(0.0F, 25.0F - amp);
         damage = damage * j / 25.0F;
      }

      return Math.max(0.0F, damage);
   }

   // =========================================================================
   // ТРАССИРОВКА БЛОКОВ ДЛЯ УСТАНОВКИ И РАЗРУШЕНИЯ
   // =========================================================================

   private BlockHitResult raycastBlockForPlacement(ClientLevel level, Vec3 eyePos, BlockPos pos, float maxRange, boolean strictTopFace) {
      double maxSq = maxRange * maxRange;
      Direction[] dirs = strictTopFace ? new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST} : ALL_DIRS;

      BlockHitResult bestHit = null;
      double bestDistSq = Double.MAX_VALUE;

      for (Direction dir : dirs) {
         Vec3 facePoint = Vec3.atCenterOf(pos).add(
            dir.getStepX() * 0.5,
            dir.getStepY() * 0.5,
            dir.getStepZ() * 0.5
         );

         double dSq = eyePos.distanceToSqr(facePoint);
         if (dSq > maxSq) {
            continue;
         }

         BlockHitResult clip = level.clip(new ClipContext(eyePos, facePoint, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, MinecraftAccess.MINECRAFT.player));
         if (clip.getType() == HitResult.Type.BLOCK && clip.getBlockPos().equals(pos)) {
            if (dSq < bestDistSq) {
               bestDistSq = dSq;
               bestHit = new BlockHitResult(clip.getLocation(), dir, pos, false);
            }
         } else if (dSq <= Mth.square(this.wallRange.getFloat()) && dSq < bestDistSq) {
            bestDistSq = dSq;
            bestHit = new BlockHitResult(facePoint, dir, pos, false);
         }
      }

      return bestHit;
   }

   private BlockHitResult findPlaceableNeighborFace(ClientLevel level, Vec3 eyePos, BlockPos targetAirPos, float maxRange) {
      double maxSq = maxRange * maxRange;
      BlockHitResult best = null;
      double bestDistSq = Double.MAX_VALUE;

      for (Direction dir : ALL_DIRS) {
         BlockPos neighbor = targetAirPos.relative(dir);
         BlockState neighborState = level.getBlockState(neighbor);
         if (neighborState.isAir() || neighborState.canBeReplaced() || !neighborState.getFluidState().isEmpty()) {
            continue;
         }

         Direction clickFace = dir.getOpposite();
         Vec3 hitVec = Vec3.atCenterOf(neighbor).add(
            clickFace.getStepX() * 0.5,
            clickFace.getStepY() * 0.5,
            clickFace.getStepZ() * 0.5
         );

         double dSq = eyePos.distanceToSqr(hitVec);
         if (dSq > maxSq) {
            continue;
         }

         BlockHitResult clip = level.clip(new ClipContext(eyePos, hitVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, MinecraftAccess.MINECRAFT.player));
         if (clip.getType() == HitResult.Type.BLOCK && clip.getBlockPos().equals(neighbor) && dSq < bestDistSq) {
            bestDistSq = dSq;
            best = new BlockHitResult(clip.getLocation(), clickFace, neighbor, false);
         }
      }

      return best;
   }

   private BlockHitResult raycastBlockToBreak(ClientLevel level, Vec3 eyePos, BlockPos digPos, float maxRange) {
      double maxSq = maxRange * maxRange;
      BlockHitResult best = null;
      double bestDistSq = Double.MAX_VALUE;

      for (Direction dir : ALL_DIRS) {
         Vec3 faceVec = Vec3.atCenterOf(digPos).add(
            dir.getStepX() * 0.48,
            dir.getStepY() * 0.48,
            dir.getStepZ() * 0.48
         );
         double dSq = eyePos.distanceToSqr(faceVec);
         if (dSq > maxSq) {
            continue;
         }

         BlockHitResult clip = level.clip(new ClipContext(eyePos, faceVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, MinecraftAccess.MINECRAFT.player));
         if (clip.getType() == HitResult.Type.BLOCK && clip.getBlockPos().equals(digPos) && dSq < bestDistSq) {
            bestDistSq = dSq;
            best = new BlockHitResult(clip.getLocation(), dir, digPos, false);
         }
      }

      // Фоллбэк через стены в пределах wallRange
      if (best == null) {
         Vec3 center = Vec3.atCenterOf(digPos);
         if (eyePos.distanceToSqr(center) <= Mth.square(this.wallRange.getFloat())) {
            Direction closestDir = Direction.getApproximateNearest(eyePos.x - center.x, eyePos.y - center.y, eyePos.z - center.z);
            best = new BlockHitResult(center, closestDir, digPos, false);
         }
      }

      return best;
   }

   private boolean canSeePoint(ClientLevel level, Vec3 from, Vec3 to) {
      BlockHitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, MinecraftAccess.MINECRAFT.player));
      return hit.getType() == HitResult.Type.MISS;
   }

   // =========================================================================
   // УПРАВЛЕНИЕ ЦЕЛЯМИ И ПРЕДИКТ ХИТБОКСОВ
   // =========================================================================

   private void refreshTargetsAndEntities(Minecraft mc, LocalPlayer self, ClientLevel level) {
      AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);
      Vec3 eyePos = self.getEyePosition();
      double targetMaxSq = Mth.square(this.targetRange.getFloat());
      double friendMaxSq = 18.0 * 18.0;

      List<TargetProfile> enemies = new ArrayList<>();
      List<Player> friends = new ArrayList<>();

      int pTicks = this.predictTicks.getInt();

      for (Player p : level.players()) {
         if (p == self || !p.isAlive() || p.isSpectator()) {
            continue;
         }
         if (antiBot != null && antiBot.isBot(p)) {
            continue;
         }

         double distSq = eyePos.distanceToSqr(p.getBoundingBox().getCenter());
         if (Onetap.FRIEND_MANAGER.isFriend(p.getName().getString())) {
            if (distSq <= friendMaxSq) {
               friends.add(p);
            }
            continue;
         }

         if (p.isCreative() || distSq > targetMaxSq) {
            continue;
         }

         AABB predicted = this.predictPlayerBox(level, p, pTicks);
         float effHp = p.getHealth() + p.getAbsorptionAmount();
         enemies.add(new TargetProfile(p, predicted, effHp, distSq));
      }

      enemies.sort(Comparator.comparingDouble(t -> t.distSq));
      if (enemies.size() > 4) {
         enemies = enemies.subList(0, 4);
      }

      this.activeTargets = enemies;
      this.friendPlayers = friends;

      if (this.dontBlowItems.getValue()) {
         AABB lootArea = self.getBoundingBox().inflate(this.targetRange.getFloat() + 6.0);
         this.nearbyLoot = level.getEntitiesOfClass(ItemEntity.class, lootArea, ItemEntity::isAlive);
      } else {
         this.nearbyLoot = List.of();
      }
   }

   private AABB predictPlayerBox(ClientLevel level, Player target, int ticks) {
      AABB baseBox = target.getBoundingBox();
      if (ticks <= 0) {
         return baseBox;
      }

      Vec3 vel = target.getDeltaMovement();
      MotionHistory history = this.entityMotionMap.get(target.getId());
      if (history != null && (MinecraftAccess.MINECRAFT.player.tickCount - history.tick) <= 4) {
         vel = history.velocity;
      }

      double dx = vel.x * ticks;
      double dy = target.onGround() ? 0.0 : vel.y * ticks;
      double dz = vel.z * ticks;

      // Проверяем коллизии, чтобы предикт не загонял хитбокс внутрь стен
      AABB moved = baseBox.move(dx, dy, dz);
      if (!level.noCollision(target, moved)) {
         AABB horizOnly = baseBox.move(dx, 0.0, dz);
         return level.noCollision(target, horizOnly) ? horizOnly : baseBox;
      }
      return moved;
   }

   // =========================================================================
   // УПРАВЛЕНИЕ РОТАЦИЯМИ И ИНВЕНТАРЁМ
   // =========================================================================

   private void requestRotationTo(LocalPlayer player, Vec3 point) {
      if (!this.rotateEnabled.getValue() || point == null) {
         return;
      }
      long aimTimer = System.nanoTime();
      try {
         this.targetRotation = new Rotations(player.getEyePosition(), point);
         this.rotationHoldTicks = 0;
         this.applyRotationStep();
      } finally {
         STAT_AIM.recordSince(aimTimer);
      }
   }

   private void maintainRotation() {
      if (this.targetRotation == null || MinecraftAccess.MINECRAFT.player == null) {
         return;
      }
      if (this.rotationAppliedTick == MinecraftAccess.MINECRAFT.player.tickCount) {
         return;
      }
      if (++this.rotationHoldTicks > 10) {
         this.clearRotation();
      } else {
         this.applyRotationStep();
      }
   }

   private void applyRotationStep() {
      Rotations current = Onetap.ROTATION_MODES.toRotations();
      Rotations next = computeSmoothRotation(current, this.targetRotation);
      Onetap.ROTATION_MODES.rotate(next, false, 15);
      this.appliedRotation = next;
      this.rotationAppliedTick = MinecraftAccess.MINECRAFT.player.tickCount;
   }

   private boolean isRotationAligned(Vec3 aimPoint) {
      if (!this.rotateEnabled.getValue() || aimPoint == null || MinecraftAccess.MINECRAFT.player == null) {
         return true;
      }
      Rotations current = Onetap.ROTATION_MODES.toRotations();
      Rotations desired = new Rotations(MinecraftAccess.MINECRAFT.player.getEyePosition(), aimPoint);
      float yawDiff = Math.abs(Mth.wrapDegrees(desired.getFloatValue() - current.getFloatValue()));
      float pitchDiff = Math.abs(desired.getFloatValue2() - current.getFloatValue2());
      return yawDiff <= 22.0F && pitchDiff <= 22.0F;
   }

   private void clearRotation() {
      this.targetRotation = null;
      this.appliedRotation = null;
      this.rotationAppliedTick = -1;
      this.rotationHoldTicks = 0;
   }

   public static Rotations computeSmoothRotation(Rotations from, Rotations to) {
      double[] scratch = ROTATION_SCRATCH;
      scratch[0] = 1.0;
      scratch[1] = from.getFloatValue();
      scratch[2] = from.getFloatValue2();
      scratch[3] = to.getFloatValue();
      scratch[4] = to.getFloatValue2();
      scratch[5] = getMouseSensitivityFactor();

      if (C9_q.m_7(8, scratch)) {
         return new Rotations((float) scratch[0], (float) scratch[1]);
      }

      double[] step1 = C9_q.m_3(8, 0.0, from.getFloatValue(), from.getFloatValue2(), to.getFloatValue(), to.getFloatValue2(), getMouseSensitivityFactor());
      double[] step2 = C9_q.m_3(8, 0.0, step1[0], step1[1], to.getFloatValue(), to.getFloatValue2(), getMouseSensitivityFactor());
      return new Rotations((float) step2[0], (float) step2[1]);
   }

   public static float getMouseSensitivityFactor() {
      Minecraft mc = MinecraftAccess.MINECRAFT;
      boolean scoping = !mc.options.smoothCamera && mc.options.getCameraType().isFirstPerson() && mc.player.isScoping();
      double sens = mc.options.sensitivity().get() * 0.6 + 0.2;
      return (float) (sens * sens * sens * (scoping ? 1.0 : 8.0)) * 0.15F;
   }

   private void swapToHotbarSlot(LocalPlayer player, int targetSlot) {
      if (targetSlot < 0 || targetSlot > 8) {
         return;
      }
      Inventory inv = player.getInventory();
      int currentSlot = inv.getSelectedSlot();
      if (currentSlot == targetSlot) {
         return;
      }
      if (!this.isSilentlySwapped) {
         this.savedSlotBeforeAction = currentSlot;
         this.isSilentlySwapped = true;
      }
      inv.setSelectedSlot(targetSlot);
   }

   private void restoreHotbarSlot() {
      if (this.isSilentlySwapped && this.savedSlotBeforeAction >= 0 && this.savedSlotBeforeAction <= 8) {
         LocalPlayer player = MinecraftAccess.MINECRAFT.player;
         if (player != null) {
            player.getInventory().setSelectedSlot(this.savedSlotBeforeAction);
         }
      }
      this.isSilentlySwapped = false;
      this.savedSlotBeforeAction = -1;
   }

   private int findItemInHotbar(Inventory inv, net.minecraft.world.item.Item item) {
      for (int i = 0; i < 9; i++) {
         if (inv.getItem(i).is(item)) {
            return i;
         }
      }
      return -1;
   }

   private boolean hasCrystalInHotbar(LocalPlayer player) {
      return player.getOffhandItem().is(Items.END_CRYSTAL) || this.findItemInHotbar(player.getInventory(), Items.END_CRYSTAL) >= 0;
   }

   private boolean hasObsidianInHotbar(LocalPlayer player) {
      return player.getOffhandItem().is(Items.OBSIDIAN) || this.findItemInHotbar(player.getInventory(), Items.OBSIDIAN) >= 0;
   }

   // =========================================================================
   // ВСПОМОГАТЕЛЬНЫЕ ПРОВЕРКИ И ОЧИСТКА
   // =========================================================================

   private boolean isTaskStillValid(Minecraft mc, CombatTask task) {
      return switch (task.type) {
         case BREAK_CRYSTAL -> task.crystal != null && task.crystal.isAlive() && !this.attackedCrystals.containsKey(task.crystal.getId());
         case PLACE_CRYSTAL -> this.canSupportCrystal(mc.level, task.targetPos, false) && !this.pendingCrystalPlaces.containsKey(task.targetPos);
         case PLACE_OBSIDIAN -> mc.level.getBlockState(task.targetPos).canBeReplaced();
         case DIG_BLOCK -> this.activeDigTask != null && !mc.level.getBlockState(task.targetPos).isAir();
      };
   }

   private boolean canSupportCrystal(ClientLevel level, BlockPos basePos, boolean allowReplaceableBase) {
      BlockState base = level.getBlockState(basePos);
      if (!base.is(Blocks.OBSIDIAN) && !base.is(Blocks.BEDROCK)) {
         if (!allowReplaceableBase || !base.canBeReplaced()) {
            return false;
         }
      }
      return level.getBlockState(basePos.above()).isAir()
         && level.getBlockState(basePos.above(2)).isAir()
         && !this.isEntityBlockingCrystal(level, basePos.above());
   }

   private boolean isEntityBlockingCrystal(ClientLevel level, BlockPos crystalPos) {
      AABB crystalBox = new AABB(
         crystalPos.getX(), crystalPos.getY(), crystalPos.getZ(),
         crystalPos.getX() + 1.0, crystalPos.getY() + 2.0, crystalPos.getZ() + 1.0
      );
      for (Entity entity : level.getEntities(null, crystalBox)) {
         if (!entity.isAlive() || entity.isSpectator()) {
            continue;
         }
         if (entity instanceof EndCrystal crystal && this.attackedCrystals.containsKey(crystal.getId())) {
            continue;
         }
         return true;
      }
      return false;
   }

   private boolean isLootNearExplosion(Vec3 explosionPos) {
      if (this.nearbyLoot.isEmpty()) {
         return false;
      }
      double safeDistSq = 6.5 * 6.5;
      for (ItemEntity item : this.nearbyLoot) {
         if (item.isAlive() && item.position().distanceToSqr(explosionPos) <= safeDistSq) {
            return true;
         }
      }
      return false;
   }

   private boolean wouldHurtFriends(ClientLevel level, Vec3 explosionPos, float maxAllowedDmg) {
      for (Player friend : this.friendPlayers) {
         float dmg = this.calculateExplosionDamage(level, explosionPos, friend, friend.getBoundingBox());
         if (dmg > maxAllowedDmg) {
            return true;
         }
      }
      return false;
   }

   private float getSafeSelfDamageLimit(LocalPlayer player) {
      float currentHp = player.getHealth() + player.getAbsorptionAmount();
      float antiSuicideCap = Math.max(0.0F, currentHp - this.antiSuicideMargin.getFloat());
      return Math.min(this.maxSelfDamage.getFloat(), antiSuicideCap);
   }

   private double getEffectiveBreakRange(LocalPlayer player) {
      return Math.min(this.breakRange.getFloat(), player.entityInteractionRange());
   }

   private boolean canPlaceNow(int currentTick) {
      return (currentTick - this.lastPlaceTick) >= this.placeDelayTicks.getInt();
   }

   private boolean canBreakNow(int currentTick) {
      return (currentTick - this.lastBreakTick) >= this.breakDelayTicks.getInt();
   }

   private int getPingTicks() {
      Minecraft mc = MinecraftAccess.MINECRAFT;
      if (mc.player != null && mc.getConnection() != null) {
         var info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
         if (info != null) {
            return Mth.clamp(info.getLatency() / 50, 1, 10);
         }
      }
      return 2;
   }

   private boolean isExternalCombatModuleBusy() {
      AutoWeb autoWeb = Onetap.MODULE_MANAGER.get(AutoWeb.class);
      AutoAnchor autoAnchor = Onetap.MODULE_MANAGER.get(AutoAnchor.class);
      return (autoWeb != null && autoWeb.isFlag()) || (autoAnchor != null && autoAnchor.m_6_boolean());
   }

   private void stopActiveDigging(Minecraft mc) {
      if (this.activeDigTask != null) {
         if (mc.gameMode != null && mc.gameMode.isDestroying()) {
            mc.gameMode.stopDestroyBlock();
         }
         this.activeDigTask = null;
         this.restoreHotbarSlot();
      }
   }

   private void abortCurrentExecution() {
      this.currentTask = null;
      this.stopActiveDigging(MinecraftAccess.MINECRAFT);
      this.clearRotation();
   }

   private void cleanupExpiredEntries(ClientLevel level, int currentTick) {
      int ping = this.getPingTicks();
      this.blacklistedBlocks.values().removeIf(expire -> currentTick >= expire);
      this.blacklistedDigBlocks.values().removeIf(expire -> currentTick >= expire);
      this.pendingObsidianPlaces.entrySet().removeIf(e -> (currentTick - e.getValue()) > (ping + 4) || level.getBlockState(e.getKey()).is(Blocks.OBSIDIAN));
      this.pendingCrystalPlaces.entrySet().removeIf(e -> (currentTick - e.getValue()) > (ping + 3));
      this.attackedCrystals.entrySet().removeIf(e -> {
         if (currentTick < e.getValue()) {
            return false;
         }
         Entity entity = level.getEntity(e.getKey());
         if (entity != null && entity.isAlive()) {
            this.failedAttackAttempts.merge(e.getKey(), 1, Integer::sum);
         }
         return true;
      });
   }

   private void flushRenderQueue() {
      RenderMarker marker;
      while ((marker = this.renderQueue.poll()) != null) {
         this.activeMarkers.add(marker);
      }
   }

   private void addRenderMarker(BlockPos pos, RenderKind kind) {
      if (this.renderVisuals.getValue()) {
         this.renderQueue.add(new RenderMarker(pos.immutable(), kind, System.currentTimeMillis()));
      }
   }

   private void resetAllState() {
      this.currentTask = null;
      this.activeDigTask = null;
      this.currentAnchorBase = null;
      this.forcedRePlacePos = null;
      this.sharedAnchorLock = null;
      this.clearRotation();

      this.activeTargets = List.of();
      this.friendPlayers = List.of();
      this.nearbyLoot = List.of();

      this.entityMotionMap.clear();
      this.attackedCrystals.clear();
      this.failedAttackAttempts.clear();
      this.pendingCrystalPlaces.clear();
      this.pendingObsidianPlaces.clear();
      this.blacklistedBlocks.clear();
      this.blacklistedDigBlocks.clear();

      this.renderQueue.clear();
      this.activeMarkers.clear();
      this.renderDigPos = null;
      this.lastPlayerTick = -1;
      debugStatusText = "IDLE";
   }

   // =========================================================================
   // МЕТОДЫ 3D-РЕНДЕРА (СОВМЕСТИМЫЕ С ДВИЖКОМ КЛИЕНТА)
   // =========================================================================

   public static void drawBox(Matrix4f matrix, Vec3 camPos, AABB box, int color, float alpha) {
      Box3DRenderer.drawBox(
         matrix,
         (float) (box.minX - camPos.x),
         (float) (box.minY - camPos.y),
         (float) (box.minZ - camPos.z),
         (float) (box.maxX - camPos.x),
         (float) (box.maxY - camPos.y),
         (float) (box.maxZ - camPos.z),
         ColorUtil.withAlpha(color, Mth.clamp((int) (alpha * 255.0F), 0, 255))
      );
   }

   public static AABB getHighlightBox(BlockPos pos, float scale) {
      return createCenteredBox(pos, scale);
   }

   public static Rotations smoothRotation(Rotations from, Rotations to) {
      return computeSmoothRotation(from, to);
   }

   public static List<Player> getNearbyPlayers(float range) {
      return collectPlayersInRange(range, false);
   }

   public static List<Player> getEnemies(float range) {
      return collectPlayersInRange(range, true);
   }

   private static List<Player> collectPlayersInRange(float range, boolean enemiesOnly) {
      Minecraft mc = MinecraftAccess.MINECRAFT;
      LocalPlayer self = mc.player;
      ClientLevel level = mc.level;
      if (self == null || level == null) {
         return List.of();
      }
      AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);
      Vec3 eyePos = self.getEyePosition();
      double maxSq = Mth.square(range);
      List<Player> result = new ArrayList<>();
      for (Player p : level.players()) {
         if (p == self || !p.isAlive() || p.isSpectator()) {
            continue;
         }
         if (eyePos.distanceToSqr(p.getBoundingBox().getCenter()) > maxSq) {
            continue;
         }
         if (enemiesOnly) {
            if (p.isCreative() || Onetap.FRIEND_MANAGER.isFriend(p.getName().getString())
               || (antiBot != null && antiBot.isBot(p))) {
               continue;
            }
         }
         result.add(p);
      }
      return result;
   }

   public static AABB createCenteredBox(BlockPos pos, float scale) {
      double half = 0.5 * Mth.clamp(scale, 0.05F, 1.0F);
      Vec3 center = Vec3.atCenterOf(pos);
      return new AABB(
         center.x - half, center.y - half, center.z - half,
         center.x + half, center.y + half, center.z + half
      );
   }

   // =========================================================================
   // СТРУКТУРЫ ДАННЫХ И ПЕРЕЧИСЛЕНИЯ
   // =========================================================================

   private enum TaskType {
      BREAK_CRYSTAL,
      PLACE_CRYSTAL,
      PLACE_OBSIDIAN,
      DIG_BLOCK
   }

   private enum ToolCategory {
      PICKAXE,
      SHOVEL,
      AXE,
      SWORD,
      OTHER
   }

   private enum RenderKind {
      CRYSTAL,
      OBSIDIAN,
      DIG
   }

   private record TargetProfile(Player player, AABB predictedBox, float effectiveHealth, double distSq) {}

   private record MotionHistory(Vec3 velocity, int tick) {}

   private record CrystalCandidate(EndCrystal crystal, Vec3 hitVec, float enemyDamage, float selfDamage) {}

   private record PlaceCandidate(
      BlockPos obsidianPos,
      Vec3 aimVec,
      BlockHitResult hitResult,
      float enemyDamage,
      float selfDamage,
      boolean needsObsidian,
      boolean needsDig,
      double score
   ) {}

   private record CombatTask(
      TaskType type,
      BlockPos targetPos,
      Vec3 aimPoint,
      BlockHitResult hitResult,
      EndCrystal crystal,
      float expectedEnemyDamage,
      float expectedSelfDamage,
      int createdTick
   ) {}

   private static final class DigTask {
      final BlockPos targetBlock;
      final BlockPos obsidianAnchor;
      Vec3 hitVec;
      BlockHitResult hitResult;
      final ToolCategory toolCategory;
      final float potentialDamage;
      int lastActiveTick;
      float lastProgress;

      DigTask(
         BlockPos targetBlock,
         BlockPos obsidianAnchor,
         Vec3 hitVec,
         BlockHitResult hitResult,
         ToolCategory toolCategory,
         float potentialDamage,
         int createdTick
      ) {
         this.targetBlock = targetBlock;
         this.obsidianAnchor = obsidianAnchor;
         this.hitVec = hitVec;
         this.hitResult = hitResult;
         this.toolCategory = toolCategory;
         this.potentialDamage = potentialDamage;
         this.lastActiveTick = createdTick;
         this.lastProgress = 0.0F;
      }
   }

   private record RenderMarker(BlockPos pos, RenderKind kind, long createdAtMs) {}
}