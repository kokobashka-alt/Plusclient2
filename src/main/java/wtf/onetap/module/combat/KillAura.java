package wtf.onetap.module.combat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.combat.EntityPrediction;
import wtf.onetap.combat.ShieldBreaker;
import wtf.onetap.combat.rotation.RotationModes;
import wtf.onetap.combat.rotation.ShardbetaAuraMode;
import wtf.onetap.combat.rotation.ShardbetaAuraPipeline;
import wtf.onetap.combat.rotation.ShardbetaRotation;
import wtf.onetap.combat.rotation.ShardbetaRotationStorage;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.PostMotionEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.movement.AirStuck;
import wtf.onetap.module.movement.ElytraResolver;
import wtf.onetap.module.player.ElytraSwap;
import wtf.onetap.pve.CombatPlanner;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.C6_f;
import wtf.onetap.util.C9j;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.LatencyStats;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.MovementStateAccess;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.TickExecutor;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "KillAura", description = "Автоматически атакует ближайшую цель", category = Category.COMBAT)
public class KillAura extends Module {
   private static final LatencyStats.StateHolder C0 = LatencyStats.getOrCreate("KillAura target");
   private static final LatencyStats.StateHolder C02 = LatencyStats.getOrCreate("KillAura crit windows");
   private static final LatencyStats.StateHolder C03 = LatencyStats.getOrCreate("KillAura rotation");
   public static LivingEntity livingEntity;
   private final ModeSetting rotation = new ModeSetting("Ротация", "Vanilla", Onetap.ROTATION_MODES.getModeNames()).build();
   private final FloatSetting hitDistance = new FloatSetting("Дистанция удара", 4.5, 1.0, 7.0, 0.1F).build();
   private final FloatSetting rotationDistance = new FloatSetting("Дистанция ротации", 2.0, 0.0, 6.0, 0.1F).build();
   private final FloatSetting aimBackAfterHitMs = new FloatSetting("Отвод после удара (мс)", 100.0F, 0.0F, 400.0F, 10.0F)
      .visibleWhen(() -> ((String)this.rotation.getValue()).equals("Snap2"))
      .build();
   private final BooleanSetting highRefreshRate = new BooleanSetting("Повышенная частота обновления", false).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Snap2")).build();
   private final FloatSetting assistStrength = new FloatSetting("Сила помощи", 1.5, 0.1, 2.0, 0.05).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Assist")).build();
   private final FloatSetting grip = new FloatSetting("Цепкость", 75.0F, 0.0F, 100.0F, 1.0F).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Assist")).build();
   private final FloatSetting speed = new FloatSetting("Скорость", 90.0F, 0.0F, 100.0F, 1.0F).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Assist")).build();
   private final FloatSetting responseMs = new FloatSetting("Отклик (мс)", 40.0F, 0.0F, 200.0F, 5.0F).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Assist")).build();
   private final FloatSetting verticalAssist = new FloatSetting("Помощь по вертикали", 0.6, 0.0, 1.0, 0.05).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Assist")).build();
   private final FloatSetting hitAssist = new FloatSetting("Доводка к удару", 0.4, 0.0, 1.0, 0.05).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Assist")).build();
   private final FloatSetting legitFov = new FloatSetting("Угол FOV", 90.0F, 10.0F, 180.0F, 1.0F).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Legit")).build();
   private final FloatSetting legitStrength = new FloatSetting("Сила доводки", 0.5F, 0.0F, 1.0F, 0.05F).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Legit")).build();
   private final FloatSetting legitSpeed = new FloatSetting("Скорость (град/с)", 60.0F, 5.0F, 300.0F, 5.0F).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Legit")).build();
   private final FloatSetting legitVertical = new FloatSetting("Вертикаль", 0.6F, 0.0F, 1.0F, 0.05F).visibleWhen(() -> ((String)this.rotation.getValue()).equals("Legit")).build();
   private final MultiModeSetting targets = new MultiModeSetting("Цели", "Игроки", "Игроки", "Голые", "Мобы", "Животные").build();
   private final ModeSetting priority = new ModeSetting("Приоритет", "Дистанция", "Дистанция", "ФОВ", "Здоровье").build();
   private final ModeSetting correction = new ModeSetting("Коррекция", "Полная", "Полная", "Прямая", "Таргетированная", "Незаметная").build();
   private final BooleanSetting humanClicks = new BooleanSetting("Человечные клики", false).build();
   private final BooleanSetting clientLook = new BooleanSetting("ClientLook", false).visibleWhen(() -> !((String)this.rotation.getValue()).equals("Assist") && !((String)this.rotation.getValue()).equals("Legit")).build();
   private final BooleanSetting aimBackDelay = new BooleanSetting("Задержка отводки", false).build();
   private final BooleanSetting rayTrace = new BooleanSetting("RayTrace", false).build();
   private final BooleanSetting breakShield = new BooleanSetting("Ломать щит", true).build();
   private final BooleanSetting autoChorus = new BooleanSetting("Авто Хорус", false).build();
   private final BooleanSetting dontHitThroughEntities = new BooleanSetting("Не бить сквозь сущностей", false).build();
   private final BooleanSetting hitThroughBlocks = new BooleanSetting("Бить сквозь блоки", true).build();
   private final BooleanSetting dontHitWhileEating = new BooleanSetting("Не бить когда ешь", false).build();
   private final BooleanSetting onlyCrits = new BooleanSetting("Только криты", true).build();
   private final BooleanSetting smartCrits = new BooleanSetting("Умные криты", true).visibleWhen(this.onlyCrits::getValue).build();
   private final ModeSetting sprintReset = new ModeSetting("Сброс спринта", "Нету", "Легитный", "Пакетный", "W-тап", "Нету").build();
   private final BooleanSetting visual360 = new BooleanSetting("Визуальный 360", false).build();
   private final ShieldBreaker shieldBreaker = new ShieldBreaker();
   private final C9j c9j = new C9j();
   private final TimeHelper timeHelper = new TimeHelper();
   private final ShardbetaRotation shardbetaRotation = new ShardbetaRotation();
   private int value;
   private JsonObject jsonObject;
   private boolean flag;
   private int value2 = -1;
   private int value3;
   private Vec3 vec3;
   private int value4;
   private int value5 = -1;
   private int value6 = -1;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private int value7;
   private LivingEntity livingEntity2;
   private LivingEntity livingEntity3;
   private int value8 = -1;
   private boolean flag6;
   private Rotations rotations;
   private boolean flag7;
   private boolean flag8;
   private LivingEntity livingEntity4;
   private boolean flag9;
   private int value9;
   private int value10;
   private int value11;
   public boolean flag10;
   public static boolean flag11;
   private long longValue;
   private static boolean flag12;

   public float m_8_float() {
      if ((Boolean)this.visual360.getValue() && this.longValue != 0L) {
         float floatValue = (float)(System.currentTimeMillis() - this.longValue) / 450.0F;
         if (floatValue >= 1.0F) {
            this.longValue = 0L;
            return 0.0F;
         } else {
            float floatValue2 = 1.0F - (1.0F - floatValue) * (1.0F - floatValue) * (1.0F - floatValue);
            return floatValue2 * 360.0F;
         }
      } else {
         return 0.0F;
      }
   }

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      this.flag8 = false;
      this.livingEntity4 = null;
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.flag6 = true;
         Rotations rotations2 = this.rotations;
         this.rotations = null;
         this.flag10 = false;
         this.value++;
         this.jsonObject = null;
         this.flag9 = false;
         this.shieldBreaker.m_2_void(this.getShieldTarget());
         if (this.value2 != -1 && MinecraftAccess.MINECRAFT.player.tickCount - this.value2 > 5) {
            this.restoreUseKey();
         }

         String text = (String)this.rotation.getValue();
         float floatValue = this.getHitDistance();
         boolean flag13 = MinecraftAccess.MINECRAFT.player.isFallFlying();
         ElytraTarget elytraTarget = flag13 ? ElytraTarget.getActive() : null;
         float floatValue2 = floatValue + (elytraTarget != null ? elytraTarget.getElytraRotation() : this.getRotationDistance());
         LivingEntity livingEntity5 = this.livingEntity2;
         if (this.livingEntity2 == null || !this.isValidTarget(this.livingEntity2, floatValue2)) {
            long longValue2 = System.nanoTime();
            LivingEntity livingEntity6 = this.livingEntity3;
            this.livingEntity2 = livingEntity6 != null && this.value8 == MinecraftAccess.MINECRAFT.player.tickCount && this.isValidTarget(livingEntity6, floatValue2) ? livingEntity6 : this.findTarget(floatValue2);
            C0.recordSince(longValue2);
         }

         boolean flag14 = this.livingEntity2 != livingEntity5;
         livingEntity = this.livingEntity2;
         this.handleChorus();
         if (this.flag3 || this.isOtherAuraBusy()) {
            CombatPlanner.COMBAT_PLANNER.setPlan(null);
            ShardbetaRotationStorage.clear();
         } else if (this.livingEntity2 == null) {
            this.shardbetaRotation.reset();
            ShardbetaRotationStorage.clear();
            if (Onetap.ROTATION_MODES.isFlag()) {
               if (this.shouldUseClientLook()) {
                  Onetap.ROTATION_MODES.applyAndFinish();
               } else if (!text.equals("Vanilla") && !text.equals("NoRot") && !this.isAirStuckActive()) {
                  Onetap.ROTATION_MODES.startReturn(this.aimBackDelay.getValue() ? 100L : 0L);
               }
            }

            if (Onetap.ROTATION_MODES.m_2_boolean(text)) {
               this.m_4_void(text);
            } else {
               CombatPlanner.COMBAT_PLANNER.setPlan(null);
            }
         } else {
            if (flag13 && this.livingEntity2.isFallFlying()) {
               floatValue += 0.5F;
            }

            boolean flag15 = text.equals("Snap");
            boolean flag16 = elytraTarget != null && elytraTarget.isTurnAround();
            AABB aABB = ElytraTarget.vec3 != null ? ElytraTarget.getBoxAt(this.livingEntity2, ElytraTarget.vec3) : this.livingEntity2.getBoundingBox();
            Vec3 vec32 = this.m_5(this.livingEntity2, elytraTarget);
            boolean flag17 = elytraTarget != null && !flag15 && ElytraTarget.vec3 != null && elytraTarget.m_4_boolean();
            if (flag17) {
               aABB = this.livingEntity2.getBoundingBox();
            } else if (ElytraTarget.vec3 != null) {
               aABB = ElytraTarget.getBoxAt(this.livingEntity2, ElytraTarget.vec3);
               if (!flag16) {
                  floatValue += 0.5F;
               }
            }

            AABB aABB2 = flag16 ? this.livingEntity2.getBoundingBox() : aABB;
            SyncTPS syncTPS = Onetap.MODULE_MANAGER.get(SyncTPS.class);
            long longValue3 = syncTPS != null && syncTPS.isEnabled() ? syncTPS.m_5_long(460L) : 460L;
            float floatValue3 = syncTPS != null && syncTPS.isEnabled() ? syncTPS.getTps() : 20.0F;
            int value12 = Math.max(1, Math.round((float)longValue3 * floatValue3 / 1000.0F));
            Vec3 vec33 = MinecraftAccess.MINECRAFT.player.getEyePosition();
            float floatValue4 = flag15 && flag13 ? 2.0F : floatValue;
            boolean flag18 = aABB2.distanceToSqr(vec33) <= floatValue4 * floatValue4 && (!flag16 || elytraTarget.m_3_boolean(this.livingEntity2));
            float floatValue5 = MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.5F);
            boolean flag19 = MinecraftAccess.MINECRAFT.player.onGround();
            boolean flag20 = C6_f.m_7((Boolean)this.onlyCrits.getValue(), (Boolean)this.smartCrits.getValue()) || this.m_8_boolean();
            float floatValue6 = Math.clamp((float)this.value / value12, 0.0F, 1.0F);
            float floatValue7 = Math.clamp(floatValue5 / (flag19 ? 0.92F : 0.9F), 0.0F, 1.0F);
            float floatValue8 = Math.min(floatValue6, floatValue7) * (flag20 ? 1.0F : 0.0F);
            boolean flag21 = false;
            if (text.equals("Snap2")) {
               flag21 = flag18 && this.value >= Math.max(0, value12 - 3) && MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.5F) >= 0.71F && flag20;
            }

            if (rotations2 != null) {
               Onetap.ROTATION_MODES.rotateWithSensitivity(rotations2, this.shouldUseClientLook(), 10);
            }

            boolean flag22 = !text.equals("NoRot") && (!flag15 || flag13 || this.isAirStuckActive());
            if (flag22 && RotationModes.isCustomMode(text)) {
               if (!CombatPlanner.COMBAT_PLANNER.isOnline()) {
                  if (!this.flag) {
                     ChatUtil.log("KillAura: ротация " + text + " работает через сервер, а он сейчас недоступен");
                  }

                  this.flag = true;
               } else {
                  this.flag = false;
               }

               this.m_7(text, flag14, value12, flag20, floatValue4, aABB, vec32, elytraTarget != null && elytraTarget.m_0_boolean(), floatValue8, flag21);
            } else {
               CombatPlanner.COMBAT_PLANNER.m_1_void();
               CombatPlanner.COMBAT_PLANNER.setPlan(null);
            }

            if (flag22) {
               long longValue4 = System.nanoTime();
               String text2 = flag13 && this.getElytraResolver() != null ? "Vanilla" : text;
               if (RotationModes.isShardMode(text2)) {
                  ShardbetaAuraMode shardMode = RotationModes.parseShardMode(text2);
                  ShardbetaAuraPipeline.setMode(shardMode);
                  this.shardbetaRotation.setAggression(shardMode.p.aggression());
                  ShardbetaRotation.Rotation sbRot = this.shardbetaRotation.update(
                     this.livingEntity2,
                     aABB2,
                     MinecraftAccess.MINECRAFT.player.getYRot(),
                     MinecraftAccess.MINECRAFT.player.getXRot(),
                     floatValue2
                  );
                  ShardbetaRotationStorage.begin(sbRot.yaw(), sbRot.pitch());
                  Onetap.ROTATION_MODES.rotateWithSensitivity(new Rotations(sbRot.yaw(), sbRot.pitch()), this.shouldUseClientLook(), 10);
               } else {
                  this.shardbetaRotation.reset();
                  ShardbetaRotationStorage.clear();
                  Onetap.ROTATION_MODES.rotateByModeFull(vec32, aABB, text2, this.shouldUseClientLook(), 10, flag21, flag14, floatValue8, this.livingEntity2);
               }
               C03.recordSince(longValue4);
            } else {
               this.shardbetaRotation.reset();
               ShardbetaRotationStorage.clear();
            }

            boolean flag23 = ((String)this.sprintReset.getValue()).equals("W-тап") && !C6_f.m_4_boolean();
            if (flag23) {
               this.m_7(flag18, value12);
            }

            boolean flag24 = this.isBlocking();
            if (flag18 || flag24 && !flag16 && this.m_3(aABB2, floatValue4)) {
               if (!flag22 || !RotationModes.isCustomMode(text) || CombatPlanner.COMBAT_PLANNER.getPathStep().m_0_boolean()) {
                  if (!this.isAirStuckActive() || this.timeHelper.hasElapsed(560L)) {
                     PacketCriticals packetCriticals = Onetap.MODULE_MANAGER.get(PacketCriticals.class);
                     if (packetCriticals == null || packetCriticals.m_5_boolean()) {
                        if (flag24
                           && (C6_f.m_4_boolean() || this.value + 1 >= value12)
                           && isCooldownReady(MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(1.5F), flag19)
                           && (flag20 || this.m_72_boolean())) {
                           this.releaseShield();
                        }

                        if (flag18) {
                           boolean flag25 = this.shieldBreaker.m_4_boolean(this.getShieldTarget());
                           if (!flag25) {
                              if (((String)this.sprintReset.getValue()).equals("Легитный") && !C6_f.m_4_boolean()) {
                                 boolean flag26 = this.value >= Math.max(0, value12 - 1) && isCooldownReady(MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(1.5F), flag19);
                                 if ((this.flag7 || flag26) && this.m_7_c_boolean()) {
                                    return;
                                 }
                              }

                              if (!C6_f.m_4_boolean() && this.value < value12) {
                                 return;
                              }

                              if (!isCooldownReady(floatValue5, flag19) && !this.m_73_boolean()) {
                                 return;
                              }

                              if (!flag20) {
                                 return;
                              }

                              if (flag23
                                 && MinecraftAccess.MINECRAFT.player.isSprinting()
                                 && !MinecraftAccess.MINECRAFT.player.onGround()
                                 && MinecraftAccess.MINECRAFT.player.getDeltaMovement().y < 0.0
                                 && !this.m_73_boolean()
                                 && !this.m_8_boolean()) {
                                 return;
                              }

                              if ((packetCriticals == null || !packetCriticals.m_3_boolean()) && C6_f.m_2_boolean(this.livingEntity2)) {
                                 return;
                              }
                           }

                           ElytraSwap elytraSwap = Onetap.MODULE_MANAGER.get(ElytraSwap.class);
                           if (elytraSwap == null || !elytraSwap.m_3_boolean()) {
                              if (!flag17 || flag16 || AimUtil.isLookingAt(Onetap.ROTATION_MODES.toRotations(), floatValue4, aABB2) || AimUtil.isLookingAt(Onetap.ROTATION_MODES.getTargetRotation(), floatValue4, aABB2)) {
                                 if (this.isPacketCriticals() && !flag25) {
                                    this.flag8 = true;
                                    this.livingEntity4 = this.livingEntity2;
                                 } else {
                                    this.m_6(flag13, flag15, flag16, flag14, floatValue8, aABB2, floatValue4);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void m_2_void() {
      if (this.flag6 && this.isEnabled() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.flag6 = false;
         ElytraTarget elytraTarget = MinecraftAccess.MINECRAFT.player.isFallFlying() ? ElytraTarget.getActive() : null;
         float floatValue = this.getHitDistance() + (elytraTarget != null ? elytraTarget.getElytraRotation() : this.getRotationDistance());
         int value12 = MinecraftAccess.MINECRAFT.player.tickCount;
         TickExecutor.m_4(() -> {
            if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
               this.livingEntity3 = this.findTarget(floatValue);
               this.value8 = value12;
            }
         });
      }
   }

   @EventTarget
   private void onPostMotion(PostMotionEvent event) {
      if (this.flag8) {
         LivingEntity livingEntity5 = this.livingEntity4;
         this.flag8 = false;
         this.livingEntity4 = null;
         if (MinecraftAccess.MINECRAFT.player != null
            && MinecraftAccess.MINECRAFT.level != null
            && MinecraftAccess.MINECRAFT.gameMode != null
            && this.livingEntity2 != null
            && this.livingEntity2 == livingEntity5
            && !this.flag3
            && !this.flag4
            && this.isPacketCriticals()) {
            String text = (String)this.rotation.getValue();
            boolean flag13 = MinecraftAccess.MINECRAFT.player.isFallFlying();
            boolean flag14 = text.equals("Snap");
            float floatValue = this.getHitDistance();
            if (flag13 && this.livingEntity2.isFallFlying()) {
               floatValue += 0.5F;
            }

            AABB aABB = this.livingEntity2.getBoundingBox();
            if (ElytraTarget.vec3 != null) {
               aABB = ElytraTarget.getBoxAt(this.livingEntity2, ElytraTarget.vec3);
               floatValue += 0.5F;
            }

            float floatValue2 = flag14 && flag13 ? 2.0F : floatValue;
            if (this.isValidTarget(this.livingEntity2, floatValue + this.getRotationDistance()) && !(aABB.distanceToSqr(MinecraftAccess.MINECRAFT.player.getEyePosition()) > floatValue2 * floatValue2)) {
               SyncTPS syncTPS = Onetap.MODULE_MANAGER.get(SyncTPS.class);
               long longValue2 = syncTPS != null && syncTPS.isEnabled() ? syncTPS.m_5_long(460L) : 460L;
               float floatValue3 = syncTPS != null && syncTPS.isEnabled() ? syncTPS.getTps() : 20.0F;
               int value12 = Math.max(1, Math.round((float)longValue2 * floatValue3 / 1000.0F));
               if (!this.isAirStuckActive() || this.timeHelper.hasElapsed(560L)) {
                  if (C6_f.m_4_boolean() || this.value >= value12) {
                     float floatValue4 = MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.5F);
                     boolean flag15 = MinecraftAccess.MINECRAFT.player.onGround();
                     if ((isCooldownReady(floatValue4, flag15) || this.m_73_boolean())
                        && C6_f.m_7((Boolean)this.onlyCrits.getValue(), (Boolean)this.smartCrits.getValue())
                        && !C6_f.m_2_boolean(this.livingEntity2)) {
                        if (!this.isOtherAuraBusy()) {
                           ElytraSwap elytraSwap = Onetap.MODULE_MANAGER.get(ElytraSwap.class);
                           if (elytraSwap == null || !elytraSwap.m_3_boolean()) {
                              float floatValue5 = Math.min(Math.clamp((float)this.value / value12, 0.0F, 1.0F), Math.clamp(floatValue4 / (flag15 ? 0.92F : 0.9F), 0.0F, 1.0F));
                              this.m_6(flag13, flag14, false, false, floatValue5, aABB, floatValue2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isOtherAuraBusy() {
      CrystalAura crystalAura = Onetap.MODULE_MANAGER.get(CrystalAura.class);
      AutoWeb autoWeb = Onetap.MODULE_MANAGER.get(AutoWeb.class);
      return crystalAura != null && crystalAura.isBusy() || autoWeb != null && autoWeb.isFlag();
   }

   private boolean isPacketCriticals() {
      PacketCriticals packetCriticals = Onetap.MODULE_MANAGER.get(PacketCriticals.class);
      return packetCriticals != null && packetCriticals.m_9_boolean();
   }

   private void m_6(boolean flag13, boolean flag14, boolean flag15, boolean flag16, float floatValue, AABB aABB, float floatValue2) {
      AABB aABB2 = flag14 ? this.livingEntity2.getBoundingBox() : aABB;
      if (flag15) {
         Rotations rotations2 = Onetap.ROTATION_MODES.toRotations();
         Rotations rotations3 = AimUtil.m_9(rotations2, aABB2, floatValue2);
         if (rotations3 != rotations2) {
            this.rotations = rotations2;
            Onetap.ROTATION_MODES.rotateWithSensitivity(rotations3, this.shouldUseClientLook(), 10);
         }
      } else if (flag14) {
         Vec3 vec32 = this.livingEntity2.position().add(0.0, this.livingEntity2.getBbHeight() * 0.5, 0.0);
         Onetap.ROTATION_MODES.rotateByModeFull(vec32, aABB2, "Snap", this.shouldUseClientLook(), 10, false, flag16, floatValue, this.livingEntity2);
      }

      if ((!(Boolean)this.rayTrace.getValue() && !this.isLegitRotation()) || AimUtil.isLookingAt(Onetap.ROTATION_MODES.toRotations(), floatValue2, aABB2)) {
         if (!(Boolean)this.dontHitThroughEntities.getValue() || !AimUtil.m_5(Onetap.ROTATION_MODES.toRotations(), floatValue2, aABB2, this.livingEntity2)) {
            if ((Boolean)this.hitThroughBlocks.getValue() || !AimUtil.m_6(Onetap.ROTATION_MODES.toRotations(), floatValue2, aABB2)) {
               Player player = this.shieldBreaker.m_4_boolean(this.getShieldTarget()) ? this.getShieldTarget() : null;
               if (player != null || !(Boolean)this.humanClicks.getValue() || this.c9j.m_0_boolean(AimUtil.isLookingAt(Onetap.ROTATION_MODES.toRotations(), floatValue2, aABB2))) {
                  if (this.isBlocking()) {
                     this.releaseShield();
                  } else if (this.value2 != MinecraftAccess.MINECRAFT.player.tickCount) {
                     if (!(Boolean)this.dontHitWhileEating.getValue() || !MinecraftAccess.MINECRAFT.player.isUsingItem()) {
                        boolean flag17 = MinecraftAccess.MINECRAFT.player.isSprinting();
                        boolean flag18 = C6_f.m_4_boolean() && ((Boolean)this.onlyCrits.getValue() || !((String)this.sprintReset.getValue()).equals("Нету"));
                        boolean flag19 = flag17 && (flag18 || ((String)this.sprintReset.getValue()).equals("Пакетный")) && MinecraftAccess.MINECRAFT.getConnection() != null;
                        if (flag19) {
                           if (flag18) {
                              MinecraftAccess.MINECRAFT.player.setSprinting(false);
                              ((MovementStateAccess)MinecraftAccess.MINECRAFT.player).onetap$setWasSprinting(false);
                           }

                           MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.STOP_SPRINTING));
                        }

                        PacketCriticals packetCriticals = Onetap.MODULE_MANAGER.get(PacketCriticals.class);
                        Runnable runnable = () -> {
                           MinecraftAccess.MINECRAFT.gameMode.attack(MinecraftAccess.MINECRAFT.player, this.livingEntity2);
                           MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
                           if ((Boolean)this.visual360.getValue()) {
                              this.longValue = System.currentTimeMillis();
                           }
                        };
                        if (player != null) {
                           this.shieldBreaker.m_6(player, runnable);
                        } else if (packetCriticals != null) {
                           packetCriticals.m_1(runnable);
                        } else {
                           runnable.run();
                        }

                        if (flag13) {
                           ElytraResolver elytraResolver = Onetap.MODULE_MANAGER.get(ElytraResolver.class);
                           if (elytraResolver != null && elytraResolver.isEnabled()) {
                              elytraResolver.m_3_void(this.livingEntity2);
                           }
                        }

                        this.restoreUseKey();
                        this.value = 0;
                        if (this.jsonObject != null) {
                           this.jsonObject.addProperty("tsa", 0);
                           this.jsonObject.addProperty("hat", CombatPlanner.COMBAT_PLANNER.elapsedMs());
                           CombatPlanner.COMBAT_PLANNER.getPathStep().m_5_void();
                        }

                        if (this.isAirStuckActive()) {
                           this.timeHelper.reset();
                        }

                        Onetap.ROTATION_MODES.m_1_void();
                        this.flag10 = true;
                        if (flag14 && !flag13 && !this.isAirStuckActive()) {
                           Onetap.ROTATION_MODES.startReturn(this.aimBackDelay.getValue() ? 100L : 0L);
                        }

                        AirStuck airStuck = Onetap.MODULE_MANAGER.get(AirStuck.class);
                        if (airStuck != null && airStuck.isEnabled()) {
                           airStuck.m_1_void();
                        }

                        if (this.flag7 && !MinecraftAccess.MINECRAFT.player.isShiftKeyDown()) {
                           MinecraftAccess.MINECRAFT.player.setSprinting(true);
                           this.flag7 = false;
                        }

                        this.m_7_void();
                        this.flag9 = this.value11 > 0 && ((String)this.sprintReset.getValue()).equals("W-тап") && !C6_f.m_4_boolean();
                        if (flag19 && flag18) {
                           InventoryUtil.m_7_void();
                        } else if (flag19 && !MinecraftAccess.MINECRAFT.player.isShiftKeyDown()) {
                           MinecraftAccess.MINECRAFT.player.setSprinting(true);
                           MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.START_SPRINTING));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isBlocking() {
      return MinecraftAccess.MINECRAFT.player.isUsingItem() && (MinecraftAccess.MINECRAFT.player.isBlocking() || MinecraftAccess.MINECRAFT.player.getUseItem().is(Items.SHIELD));
   }

   private void m_7(String text, boolean flag13, int value12, boolean flag14, float floatValue, AABB aABB, Vec3 vec32, boolean flag15, float floatValue2, boolean flag16) {
      LivingEntity livingEntity5 = this.livingEntity2;
      boolean flag17 = ElytraTarget.vec3 != null;
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("mode", text);
      jsonObject2.addProperty("tid", livingEntity5.getId());
      jsonObject2.addProperty("tx", livingEntity5.getX());
      jsonObject2.addProperty("ty", livingEntity5.getY());
      jsonObject2.addProperty("tz", livingEntity5.getZ());
      Vec3 vec33 = livingEntity5.position().subtract(livingEntity5.xo, livingEntity5.yo, livingEntity5.zo);
      jsonObject2.addProperty("tvx", vec33.x);
      jsonObject2.addProperty("tvy", vec33.y);
      jsonObject2.addProperty("tvz", vec33.z);
      jsonObject2.addProperty("tw", livingEntity5.getBbWidth());
      jsonObject2.addProperty("th", livingEntity5.getBbHeight());
      jsonObject2.addProperty("tog", livingEntity5.onGround());
      jsonObject2.addProperty("tff", livingEntity5.isFallFlying());
      Vec3 vec34 = livingEntity5.position().add(0.0, livingEntity5.getBbHeight() * 0.5, 0.0);
      boolean flag18 = vec32.distanceToSqr(vec34) > 1.0E-6;
      if (flag17) {
         JsonArray jsonArray = new JsonArray();
         jsonArray.add(aABB.minX);
         jsonArray.add(aABB.minY);
         jsonArray.add(aABB.minZ);
         jsonArray.add(aABB.maxX);
         jsonArray.add(aABB.maxY);
         jsonArray.add(aABB.maxZ);
         jsonObject2.add("box", jsonArray);
      }

      if (flag18) {
         JsonArray jsonArray2 = new JsonArray();
         jsonArray2.add(vec32.x);
         jsonArray2.add(vec32.y);
         jsonArray2.add(vec32.z);
         jsonObject2.add("aim", jsonArray2);
      }

      jsonObject2.addProperty("sw", flag13);
      if (flag15) {
         jsonObject2.addProperty("fa", true);
      }

      jsonObject2.addProperty("tsa", this.value);
      jsonObject2.addProperty("hct", value12);
      long longValue2 = System.nanoTime();
      jsonObject2.addProperty("crit", this.getCritTickDelay(flag14));
      C02.recordSince(longValue2);
      jsonObject2.addProperty("rng", floatValue);
      jsonObject2.addProperty("rd", floatValue2);
      jsonObject2.addProperty("ra", flag16);
      CombatPlanner.COMBAT_PLANNER.setPlan(jsonObject2);
      this.jsonObject = jsonObject2;
      CombatPlanner.COMBAT_PLANNER.getPathStep().m_4(livingEntity5, vec32);
   }

   private void m_4_void(String text) {
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("mode", text);
      jsonObject2.addProperty("ret", true);
      jsonObject2.addProperty("rf", Onetap.ROTATION_MODES.getLongValue2());
      CombatPlanner.COMBAT_PLANNER.setPlan(jsonObject2);
      CombatPlanner.COMBAT_PLANNER.getPathStep().reset();
   }

   private int getCritTickDelay(boolean flag13) {
      if (!(Boolean)this.onlyCrits.getValue()) {
         return Integer.MAX_VALUE;
      }

      int value12 = flag13 ? 1 : 0;
      if (!MinecraftAccess.MINECRAFT.player.isFallFlying() && !MinecraftAccess.MINECRAFT.player.isInWater() && !MinecraftAccess.MINECRAFT.player.onClimbable()) {
         PlayerUtil playerUtil = PlayerUtil.fromLocalPlayer(PlayerUtil.StateHolder.m_6(MinecraftAccess.MINECRAFT.player.input.keyPresses));
         boolean flag14 = MinecraftAccess.MINECRAFT.player.onGround();

         for (int i = 1; i <= 12; i++) {
            playerUtil.tick();
            boolean flag15 = playerUtil.flag4 ? flag14 && flag13 : playerUtil.vec32.y < 0.0;
            if (flag15) {
               value12 |= 1 << i;
            }
         }

         return value12;
      } else {
         return flag13 ? Integer.MAX_VALUE : 0;
      }
   }

   private void releaseShield() {
      MinecraftAccess.MINECRAFT.options.keyUse.setDown(false);
      MinecraftAccess.MINECRAFT.gameMode.releaseUsingItem(MinecraftAccess.MINECRAFT.player);
      this.value2 = MinecraftAccess.MINECRAFT.player.tickCount;
   }

   private boolean m_72_boolean() {
      if (MinecraftAccess.MINECRAFT.player.onGround()) {
         return false;
      }

      PlayerUtil playerUtil = PlayerUtil.predictLocal(1);
      return !playerUtil.flag4 && playerUtil.vec3.y < MinecraftAccess.MINECRAFT.player.getY();
   }

   private boolean m_3(AABB aABB, float floatValue) {
      Vec3 vec32 = EntityPrediction.m_5_Vec3().add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight(MinecraftAccess.MINECRAFT.player.getPose()), 0.0);
      Vec3 vec33 = EntityPrediction.m_2_Vec3(this.livingEntity2, 1.0F).subtract(this.livingEntity2.getBoundingBox().getCenter());
      return aABB.move(vec33).distanceToSqr(vec32) <= floatValue * floatValue;
   }

   private void restoreUseKey() {
      if (this.value2 != -1) {
         this.value2 = -1;
         if (MinecraftAccess.MINECRAFT.options != null) {
            MinecraftAccess.MINECRAFT.options.keyUse.setDown(InventoryUtil.m_9_boolean());
         }
      }
   }

   private Player getShieldTarget() {
      return this.breakShield.getValue() && this.livingEntity2 instanceof Player player ? player : null;
   }

   private void handleChorus() {
      if ((Boolean)this.autoChorus.getValue() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null && this.livingEntity2 instanceof Player player) {
         boolean flag13 = player.isUsingItem() && player.getUseItem().is(Items.CHORUS_FRUIT);
         int value12 = MinecraftAccess.MINECRAFT.player.tickCount;
         if (flag13) {
            this.flag5 = false;
            if (this.value3 == 0) {
               this.value3 = value12;
               this.vec3 = player.position();
            }

            if (!this.flag3 && !this.flag4 && value12 - this.value3 >= 1) {
               int value13 = InventoryUtil.findHotbarSlot(Items.CHORUS_FRUIT);
               int value14 = value13 != -1 ? value13 : InventoryUtil.findItemSlotInRange(Items.CHORUS_FRUIT, 9, 36);
               if (value14 != -1 && MinecraftAccess.MINECRAFT.gui.screen() == null && MinecraftAccess.MINECRAFT.player.containerMenu.containerId == 0) {
                  this.value5 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
                  this.value6 = value13 == -1 ? value14 : -1;
                  if (this.value6 != -1) {
                     this.flag4 = true;
                     this.value7 = value12;
                  } else {
                     InventoryUtil.selectHotbarSlot(value13);
                     this.flag4 = true;
                     this.value7 = value12;
                  }
               }
            }
         } else if (this.value3 != 0 && !this.flag5) {
            this.flag5 = true;
            this.value4 = value12;
         }

         if (this.flag4 && value12 - this.value7 >= 1) {
            if (this.value6 != -1) {
               int value15 = this.value6;
               InventoryUtil.m_6(() -> {
                  InventoryUtil.clickSlot(0, value15, 8, ContainerInput.SWAP);
                  InventoryUtil.selectHotbarSlot(8);
                  MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
                  MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
                  this.flag2 = true;
               });
            } else {
               MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
               MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
               InventoryUtil.selectHotbarSlot(this.value5);
            }

            this.flag4 = false;
            this.flag3 = true;
            this.value7 = value12;
         }

         if (this.flag5) {
            boolean flag14 = this.vec3 != null && player.position().distanceToSqr(this.vec3) > 36.0;
            if (flag14) {
               this.flag5 = false;
               this.value3 = 0;
               this.vec3 = null;
            } else if (value12 - this.value4 > 3) {
               this.flag5 = false;
               this.value3 = 0;
               this.vec3 = null;
               this.releaseUseKey();
            }
         }

         if (this.flag3 && (!MinecraftAccess.MINECRAFT.player.isUsingItem() || !MinecraftAccess.MINECRAFT.player.getUseItem().is(Items.CHORUS_FRUIT))) {
            this.releaseUseKey();
         }
      } else {
         this.releaseUseKey();
         this.value3 = 0;
         this.vec3 = null;
      }
   }

   private void releaseUseKey() {
      if (this.flag3 || this.flag4) {
         this.flag3 = false;
         this.flag4 = false;
         if (MinecraftAccess.MINECRAFT.options != null) {
            MinecraftAccess.MINECRAFT.options.keyUse.setDown(false);
         }

         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player.isUsingItem() && MinecraftAccess.MINECRAFT.player.getUseItem().is(Items.CHORUS_FRUIT)) {
            MinecraftAccess.MINECRAFT.gameMode.releaseUsingItem(MinecraftAccess.MINECRAFT.player);
         }

         if (MinecraftAccess.MINECRAFT.player != null && this.flag2 && this.value6 >= 9 && this.value6 < 36) {
            InventoryUtil.m_6(() -> InventoryUtil.clickSlot(0, this.value6, 8, ContainerInput.SWAP));
         }

         this.flag2 = false;
         this.value6 = -1;
         if (MinecraftAccess.MINECRAFT.player != null && this.value5 >= 0 && this.value5 <= 8) {
            InventoryUtil.selectHotbarSlot(this.value5);
         }

         this.value5 = -1;
         this.value7 = 0;
         this.value3 = 0;
         this.value4 = 0;
         this.vec3 = null;
         this.flag5 = false;
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (event.getForward() > 0.0F && livingEntity != null && this.flag9) {
         event.setForward(0.0F);
      }

      float floatValue = event.getForward();
      float floatValue2 = event.getStrafe();
      String text = (String)this.correction.getValue();
      if ((floatValue != 0.0F || floatValue2 != 0.0F) && livingEntity != null && !text.equals("Полная")) {
         float floatValue3;
         if (text.equals("Прямая")) {
            flag11 = false;
            Vec3 vec32 = livingEntity.position().subtract(MinecraftAccess.MINECRAFT.player.position());
            floatValue3 = (float)Math.toDegrees(Math.atan2(vec32.z, vec32.x)) - 90.0F;
         } else if (text.equals("Таргетированная")) {
            flag11 = true;
            Vec3 vec33 = EntityPrediction.m_2_Vec3(livingEntity, 3.0F);
            Vec3 vec34 = PlayerUtil.predictLocal(1).vec3.add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight(MinecraftAccess.MINECRAFT.player.getPose()), 0.0);
            Vec3 vec35 = vec33.subtract(vec34);
            floatValue3 = (float)Math.toDegrees(Math.atan2(vec35.z, vec35.x)) - 90.0F;
         } else {
            flag11 = false;
            floatValue3 = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().yRot();
            if (MinecraftAccess.MINECRAFT.options.getCameraType().isMirrored()) {
               floatValue3 -= 180.0F;
            }
         }

         float floatValue4 = floatValue3 - MinecraftAccess.MINECRAFT.player.getYRot();
         float floatValue5 = (float)Math.toDegrees(Math.atan2(floatValue, floatValue2));
         float floatValue6 = Mth.wrapDegrees(floatValue5 + floatValue4);
         float floatValue7 = Math.round(floatValue6 / 45.0F) * 45.0F;
         double doubleValue = Math.toRadians(floatValue7);
         int value12 = Math.round((float)Math.cos(doubleValue));
         int value13 = Math.round((float)Math.sin(doubleValue));
         if (this.flag9 && value13 > 0) {
            value13 = 0;
         }

         Vec2 vec2 = new Vec2(value12, value13);
         Integer integer = InventoryUtil.getProtocolVersion();
         if (integer == null || integer > 769) {
            vec2 = vec2.normalized();
         }

         event.setForward(vec2.y);
         event.setStrafe(vec2.x);
      } else {
         flag11 = false;
      }

      if (ShardbetaRotationStorage.active() && RotationModes.isShardMode((String)this.rotation.getValue())) {
         Vec2 fixed = ShardbetaAuraPipeline.fixMoveInput(event.getForward(), event.getStrafe());
         event.setForward(fixed.y);
         event.setStrafe(fixed.x);
      }
   }

   private Vec3 m_5(LivingEntity livingEntity5, ElytraTarget elytraTarget) {
      String text = (String)this.rotation.getValue();
      if (text.equals("NoRot")) {
         ElytraTarget.reset();
         return livingEntity5.position().add(0.0, livingEntity5.getBbHeight() * 0.5, 0.0);
      }

      ElytraResolver elytraResolver = this.getElytraResolver();
      if (elytraResolver != null && MinecraftAccess.MINECRAFT.player.isFallFlying()) {
         ElytraTarget.reset();
         Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         Vec3 vec33 = livingEntity5.position();
         elytraResolver.m_1(livingEntity5, vec32.distanceTo(vec33));
         Vec3 vec34 = elytraResolver.isApproaching() ? vec33.add(0.0, livingEntity5.getBbHeight() * 0.4, 0.0) : elytraResolver.getVec3();
         if (vec34 != null) {
            return vec34;
         }
      }

      if (elytraTarget != null) {
         Vec3 vec35 = elytraTarget.m_1_Vec3(livingEntity5);
         if (vec35 != null && !text.equals("Vanilla")) {
            return vec35;
         }
      } else {
         ElytraTarget.reset();
      }

      if (text.equals("Vanilla")) {
         AABB aABB = ElytraTarget.vec3 != null ? ElytraTarget.getBoxAt(livingEntity5, ElytraTarget.vec3) : livingEntity5.getBoundingBox();
         Vec3 vec36 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         return aABB.contains(vec36) ? aABB.getCenter() : AimUtil.closestPoint(vec36, aABB);
      } else {
         return livingEntity5.position().add(0.0, livingEntity5.getBbHeight() * 0.5, 0.0);
      }
   }

   public LivingEntity getLivingEntity2() {
      return this.livingEntity2;
   }

   private ElytraResolver getElytraResolver() {
      ElytraResolver elytraResolver = Onetap.MODULE_MANAGER.get(ElytraResolver.class);
      return elytraResolver != null && elytraResolver.isEnabled() ? elytraResolver : null;
   }

   public boolean isHighRefreshRate() {
      return (Boolean)this.highRefreshRate.getValue();
   }

   public long getAimBackDelay() {
      return ((Float)this.aimBackAfterHitMs.getValue()).longValue();
   }

   public float getAssistStrength() {
      return this.assistStrength.getFloat();
   }

   public float getTurnSpeed() {
      return 30.0F - this.grip.getFloat() * 0.29F;
   }

   public float getRotationDuration() {
      return 10.0F + (100.0F - this.speed.getFloat()) * 4.9F;
   }

   public float getResponseSeconds() {
      return this.responseMs.getFloat() / 1000.0F;
   }

   public float getVerticalAssist() {
      return this.verticalAssist.getFloat();
   }

   public float getHitAssist() {
      return this.hitAssist.getFloat();
   }

   public float getLegitFov() {
      return this.legitFov.getFloat();
   }

   public float getLegitStrength() {
      return this.legitStrength.getFloat();
   }

   public float getLegitSpeed() {
      return this.legitSpeed.getFloat();
   }

   public float getLegitVertical() {
      return this.legitVertical.getFloat();
   }

   private boolean isLegitRotation() {
      return ((String)this.rotation.getValue()).equals("Legit");
   }

   private boolean shouldUseClientLook() {
      if (this.isLegitRotation()) {
         return false;
      }

      return (Boolean)this.clientLook.getValue() || ((String)this.rotation.getValue()).equals("Assist");
   }

   public static boolean isCooldownReady(float floatValue, boolean flag13) {
      return flag13 ? floatValue >= 0.92F : floatValue > 0.9F;
   }

   public float getRotationDistance() {
      AutoMace autoMace = Onetap.MODULE_MANAGER.get(AutoMace.class);
      return autoMace != null && autoMace.isEnabled() && autoMace.m_0_boolean() ? autoMace.getMaceRange() : (Float)this.rotationDistance.getValue();
   }

   private boolean m_73_boolean() {
      AutoMace autoMace = Onetap.MODULE_MANAGER.get(AutoMace.class);
      return autoMace != null && autoMace.isEnabled() && autoMace.m_4_boolean();
   }

   private boolean m_8_boolean() {
      AutoMace autoMace = Onetap.MODULE_MANAGER.get(AutoMace.class);
      return autoMace != null && autoMace.isEnabled() && autoMace.m_1_boolean();
   }

   private void m_7(boolean flag13, int value12) {
      if (this.value < this.value11) {
         this.flag9 = true;
      } else {
         boolean flag14 = false;
         if (flag13 && (MinecraftAccess.MINECRAFT.player.isSprinting() || this.value9 > 0) && this.value9 < this.value10 + 6) {
            for (int i = 1; i <= this.value10 + 1 && !flag14; i++) {
               flag14 = this.value + i >= value12 && isCooldownReady(MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(i + 0.5F), false) && this.willFall(i);
            }
         }

         this.flag9 = flag14;
         this.value9 = flag14 ? this.value9 + 1 : 0;
      }
   }

   private void m_7_void() {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      float floatValue = threadLocalRandom.nextFloat();
      this.value10 = floatValue < 0.14F ? 0 : (floatValue < 0.64F ? 1 : (floatValue < 0.92F ? 2 : 3));
      float floatValue2 = threadLocalRandom.nextFloat();
      this.value11 = (MinecraftAccess.MINECRAFT.player == null || !MinecraftAccess.MINECRAFT.player.onGround()) && !(floatValue2 < 0.45F) ? (floatValue2 < 0.88F ? 1 : 2) : 0;
      this.value9 = 0;
   }

   private boolean m_7_c_boolean() {
      boolean flag13 = !MinecraftAccess.MINECRAFT.player.onGround() && MinecraftAccess.MINECRAFT.player.getDeltaMovement().y < -0.1F;
      boolean flag14 = this.willFall(1);
      if (this.flag7) {
         if (!flag13 && !flag14) {
            this.flag7 = false;
            return false;
         } else {
            MinecraftAccess.MINECRAFT.player.setSprinting(false);
            return false;
         }
      } else if (MinecraftAccess.MINECRAFT.player.isSprinting() && flag14) {
         MinecraftAccess.MINECRAFT.player.setSprinting(false);
         this.flag7 = true;
         return true;
      } else {
         return false;
      }
   }

   private boolean isAirStuckActive() {
      AirStuck airStuck = Onetap.MODULE_MANAGER.get(AirStuck.class);
      return airStuck != null && airStuck.m_7_boolean();
   }

   public float getHitDistance() {
      AirStuck airStuck = Onetap.MODULE_MANAGER.get(AirStuck.class);
      if (airStuck != null && airStuck.isEnabled()) {
         double doubleValue = airStuck.getExtendedHitDistance();
         if (doubleValue > 0.0) {
            return (float)doubleValue;
         }
      }

      return (Float)this.hitDistance.getValue();
   }

   private boolean willFall(int value12) {
      if (!MinecraftAccess.MINECRAFT.player.isFallFlying() && !MinecraftAccess.MINECRAFT.player.isInWater() && !MinecraftAccess.MINECRAFT.player.onClimbable()) {
         PlayerUtil playerUtil = PlayerUtil.predictLocal(value12);
         return !playerUtil.flag4 && playerUtil.vec32.y < 0.0;
      } else {
         return false;
      }
   }

   public LivingEntity findTargetInRange() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         ElytraTarget elytraTarget = MinecraftAccess.MINECRAFT.player.isFallFlying() ? ElytraTarget.getActive() : null;
         float floatValue = this.getHitDistance() + (elytraTarget != null ? elytraTarget.getElytraRotation() : this.getRotationDistance());
         return this.livingEntity2 != null && this.isValidTarget(this.livingEntity2, floatValue) ? this.livingEntity2 : this.findTarget(floatValue);
      } else {
         return null;
      }
   }

   private LivingEntity findTarget(float floatValue) {
      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().inflate(floatValue);
      return MinecraftAccess.MINECRAFT
         .level
         .getEntitiesOfClass(LivingEntity.class, aABB, livingEntity5 -> this.isValidTarget(livingEntity5, floatValue))
         .stream()
         .min(Comparator.comparingDouble(livingEntity6 -> this.getTargetScore(livingEntity6)))
         .orElse(null);
   }

   private double getTargetScore(LivingEntity livingEntity5) {
      return switch ((String)this.priority.getValue()) {
         case "ФОВ" -> Onetap.ROTATION_MODES
            .getRotationTo(livingEntity5.position().add(0.0, livingEntity5.getBbHeight() * 0.5, 0.0))
            .m_7_double(new Rotations(MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot()));
         case "Здоровье" -> livingEntity5.getHealth();
         default -> livingEntity5.distanceToSqr(MinecraftAccess.MINECRAFT.player);
      };
   }

   private boolean isValidTarget(LivingEntity livingEntity5, float floatValue) {
      if (livingEntity5 != MinecraftAccess.MINECRAFT.player && livingEntity5.isAlive()) {
         if (livingEntity5.distanceToSqr(MinecraftAccess.MINECRAFT.player) > floatValue * floatValue) {
            return false;
         }

         if (livingEntity5 instanceof Player player) {
            if (Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
               return false;
            } else {
               AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);
               if (antiBot != null && antiBot.isBot(player)) {
                  return false;
               } else {
                  return this.targets.isSelected("Игроки") ? true : this.targets.isSelected("Голые") && this.isNaked(player);
               }
            }
         } else {
            return this.targets.isSelected("Мобы") && livingEntity5 instanceof Enemy ? true : this.targets.isSelected("Животные") && livingEntity5 instanceof Animal;
         }
      } else {
         return false;
      }
   }

   private boolean isNaked(Player player) {
      return player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
         && player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
         && player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
         && player.getItemBySlot(EquipmentSlot.FEET).isEmpty();
   }

   @Override
   public void onEnable() {
      this.value = 1000;
      this.timeHelper.reset();
      this.m_7_void();
      this.livingEntity3 = null;
      this.value8 = -1;
      this.flag6 = false;
      this.shardbetaRotation.reset();
      ShardbetaRotationStorage.clear();
   }

   @Override
   public void onDisable() {
      this.flag8 = false;
      this.livingEntity4 = null;
      this.livingEntity3 = null;
      this.value8 = -1;
      this.flag6 = false;
      this.shardbetaRotation.reset();
      ShardbetaRotationStorage.clear();
      this.shieldBreaker.m_2_void(null);
      this.restoreUseKey();
      this.releaseUseKey();
      CombatPlanner.COMBAT_PLANNER.setPlan(null);
      String text = (String)this.rotation.getValue();
      if (Onetap.ROTATION_MODES.isFlag()) {
         if (this.shouldUseClientLook()) {
            Onetap.ROTATION_MODES.applyAndFinish();
         } else if (!text.equals("Vanilla") && !text.equals("NoRot")) {
            Onetap.ROTATION_MODES.startReturn(this.aimBackDelay.getValue() ? 100L : 0L);
         }
      }

      this.flag10 = false;
      this.livingEntity2 = null;
      this.rotations = null;
      livingEntity = null;
      ElytraTarget.reset();
      this.flag7 = false;
      this.flag9 = false;
      flag11 = false;
      this.longValue = 0L;
   }

   static {
      m_6_void(true);
   }

   public static void m_6_void(boolean flag13) {
      flag12 = flag13;
   }

   public static boolean m_6_boolean() {
      return flag12;
   }

   public static boolean m_4_boolean() {
      return false;
   }
}
