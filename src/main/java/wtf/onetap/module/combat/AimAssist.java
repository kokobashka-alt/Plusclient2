package wtf.onetap.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.combat.CombatTargetHolder;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "AimAssist", description = "Доводит прицел к цели внутри конуса FOV и опционально бьёт, когда цель в прицеле", category = Category.COMBAT)
public class AimAssist extends Module {
   private final MultiModeSetting targets = new MultiModeSetting("Цели", "Игроки", "Игроки", "Мобы").build();
   private final FloatSetting reach = new FloatSetting("Дистанция", 4.5F, 2.0F, 6.0F, 0.1F).build();
   private final FloatSetting fov = new FloatSetting("Угол FOV", 90.0F, 10.0F, 180.0F, 1.0F).build();
   private final FloatSetting strength = new FloatSetting("Сила доводки", 0.5F, 0.0F, 1.0F, 0.05F).build();
   private final FloatSetting speed = new FloatSetting("Скорость (град/с)", 60.0F, 5.0F, 300.0F, 5.0F).build();
   private final FloatSetting verticalScale = new FloatSetting("Вертикаль", 0.6F, 0.0F, 1.0F, 0.05F).build();
   private final BooleanSetting onlyWeapon = new BooleanSetting("Только с оружием", true).build();
   private final BooleanSetting onlyArmored = new BooleanSetting("Только в броне", true).build();
   private final BooleanSetting ignoreNaked = new BooleanSetting("Игнорировать голых", true)
      .visibleWhen(() -> !(Boolean)this.onlyArmored.getValue()).build();
   private final BooleanSetting wallCheck = new BooleanSetting("Проверка стен", true).build();
   private final BooleanSetting multipoint = new BooleanSetting("Мультиточка", true).build();
   private final BooleanSetting lastHit = new BooleanSetting("По последнему удару", true).build();
   private final BooleanSetting mace = new BooleanSetting("Булава", false).build();
   private final FloatSetting maceDistance = new FloatSetting("Дистанция (булава)", 10.0F, 1.0F, 15.0F, 0.5F)
      .visibleWhen(this.mace::getValue).build();
   private final BooleanSetting onlyWhileAttackKey = new BooleanSetting("Только при зажатой ЛКМ", false).build();
   private final BooleanSetting triggerBot = new BooleanSetting("Триггербот", false).build();
   private final FloatSetting delayMin = new FloatSetting("Задержка мин (мс)", 80.0F, 0.0F, 400.0F, 10.0F)
      .visibleWhen(this.triggerBot::getValue).build();
   private final FloatSetting delayMax = new FloatSetting("Задержка макс (мс)", 140.0F, 0.0F, 600.0F, 10.0F)
      .visibleWhen(this.triggerBot::getValue).build();
   private volatile LivingEntity target;
   private LivingEntity lastHitTarget;
   private long lastFrameNanos;
   private long attackAtMs;

   // Multipoint: a jittered aim point inside the target's hitbox instead of always its exact center,
   // re-randomized every so often and smoothly drifted toward the new offset. Fractions of the box
   // size along each axis; Y is centered on 0.85 (roughly head height) the way the original did.
   private double multipointX;
   private double multipointY = 0.85;
   private double multipointZ;
   private double jitterX;
   private double jitterY = 0.85;
   private double jitterZ;
   private double jitterSpeed = 4.0;
   private long nextMultipointUpdateTime;

   public AimAssist() {
   }

   @Override
   public void onDisable() {
      this.target = null;
      this.lastHitTarget = null;
      this.lastFrameNanos = 0L;
      this.attackAtMs = 0L;
      CombatTargetHolder.setAimAssistTarget(null);
   }

   /** Picks the target and runs the triggerbot once per game tick. */
   @EventTarget
   private void onTick(TickEvent event) {
      Player player = MinecraftAccess.MINECRAFT.player;
      if (!this.isEnabled() || player == null || MinecraftAccess.MINECRAFT.level == null || MinecraftAccess.MINECRAFT.gameMode == null) {
         this.target = null;
         this.attackAtMs = 0L;
         CombatTargetHolder.setAimAssistTarget(null);
         return;
      }

      boolean attacking = !(Boolean)this.onlyWhileAttackKey.getValue() || MinecraftAccess.MINECRAFT.options.keyAttack.isDown();
      Vec3 eye = player.getEyePosition();
      LivingEntity previous = this.target;
      this.target = attacking ? this.findTarget(player, eye) : null;
      if (this.target != previous) {
         this.randomizeMultipointOffset();
      }
      CombatTargetHolder.setAimAssistTarget(this.target);

      if ((Boolean)this.triggerBot.getValue()) {
         this.handleTriggerBot(player, eye);
      }
   }

   /** Tracks whatever entity was just actually attacked (real click, triggerbot or otherwise). */
   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      if (this.isEnabled() && event.getEntity() instanceof LivingEntity living) {
         this.lastHitTarget = living;
      }
   }

   /** Moves the view toward the target once per rendered frame, using the real frame time. */
   @EventTarget
   private void onRender3D(Render3DEvent event) {
      Player player = MinecraftAccess.MINECRAFT.player;
      long now = System.nanoTime();
      float dtSeconds = this.lastFrameNanos == 0L ? 0.0F : (float) ((now - this.lastFrameNanos) / 1.0E9);
      this.lastFrameNanos = now;

      LivingEntity current = this.target;
      if (!this.isEnabled() || player == null || current == null || !current.isAlive()) {
         return;
      }

      this.updateMultipointDrift(dtSeconds);
      Vec3 aimPoint = this.getMultipointAimPoint(current);
      Rotations toTarget = new Rotations(player.getEyePosition(), aimPoint);
      float yaw = player.getYRot();
      float pitch = player.getXRot();
      float targetYaw = toTarget.getFloatValue();
      float targetPitch = toTarget.getFloatValue2();
      if (AimAssistMath.fovDistance(yaw, pitch, targetYaw, targetPitch) > this.fov.getFloat() / 2.0F) {
         return;
      }

      float[] step = AimAssistMath.smoothStep(
         yaw, pitch, targetYaw, targetPitch,
         this.strength.getFloat(), this.speed.getFloat(), this.verticalScale.getFloat(), dtSeconds
      );
      player.setYRot(yaw + step[0]);
      player.setXRot(AimAssistMath.clampPitch(pitch + step[1]));
   }

   private void handleTriggerBot(Player player, Vec3 eye) {
      LivingEntity current = this.target;
      boolean inCrosshair = current != null && AimUtil.isLookingAtEntity(current, null, this.currentReach(player));
      boolean ready = player.getAttackStrengthScale(0.0F) >= 1.0F;
      if (!inCrosshair || !ready) {
         this.attackAtMs = 0L;
         return;
      }

      long nowMs = System.nanoTime() / 1_000_000L;
      if (this.attackAtMs == 0L) {
         this.attackAtMs = nowMs + AimAssistMath.humanDelayMs(
            ThreadLocalRandom.current().nextDouble(),
            Math.round(this.delayMin.getFloat()),
            Math.round(this.delayMax.getFloat())
         );
         return;
      }

      if (nowMs >= this.attackAtMs) {
         MinecraftAccess.MINECRAFT.gameMode.attack(player, current);
         player.swing(InteractionHand.MAIN_HAND);
         this.attackAtMs = 0L;
      }
   }

   private LivingEntity findTarget(Player player, Vec3 eye) {
      if ((Boolean)this.onlyWeapon.getValue() && !this.hasWeaponInMainHand(player)) {
         return null;
      }

      double range = this.currentReach(player);
      double rangeSq = range * range;

      if ((Boolean)this.lastHit.getValue()) {
         LivingEntity last = this.lastHitTarget;
         return last != null && this.isValidTarget(last, player, eye, rangeSq) ? last : null;
      }

      var box = player.getBoundingBox().inflate(range + 2.0);
      LivingEntity best = null;
      float bestFov = Float.MAX_VALUE;
      for (Entity entity : MinecraftAccess.MINECRAFT.level.getEntities(player, box, e -> e.isPickable() && !e.isSpectator())) {
         if (!(entity instanceof LivingEntity living) || !this.isValidTarget(living, player, eye, rangeSq)) {
            continue;
         }
         Rotations toEntity = new Rotations(eye, living.getBoundingBox().getCenter());
         float fovValue = AimAssistMath.fovDistance(player.getYRot(), player.getXRot(), toEntity.getFloatValue(), toEntity.getFloatValue2());
         if (fovValue < bestFov) {
            bestFov = fovValue;
            best = living;
         }
      }
      return best;
   }

   /** The shared filters: alive, right type, in range, armor, line of sight. Used for both target
    * selection and for re-validating a remembered last-hit target. */
   private boolean isValidTarget(LivingEntity living, Player player, Vec3 eye, double rangeSq) {
      if (!living.isAlive() || living.isRemoved() || living.getId() == player.getId()) {
         return false;
      }
      boolean isPlayer = living instanceof Player;
      if (isPlayer && !this.targets.isSelected("Игроки") || !isPlayer && !this.targets.isSelected("Мобы")) {
         return false;
      }
      if (living.getBoundingBox().distanceToSqr(eye) > rangeSq) {
         return false;
      }
      if (isPlayer && this.requiresArmorFilter() && !this.hasAnyArmor(living)) {
         return false;
      }
      if ((Boolean)this.wallCheck.getValue() && !this.hasLineOfSight(eye, living.getBoundingBox().getCenter())) {
         return false;
      }
      return true;
   }

   private boolean requiresArmorFilter() {
      return (Boolean)this.onlyArmored.getValue() || (Boolean)this.ignoreNaked.getValue();
   }

   private boolean hasAnyArmor(LivingEntity living) {
      for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         if (!living.getItemBySlot(slot).isEmpty()) {
            return true;
         }
      }
      return false;
   }

   private boolean hasLineOfSight(Vec3 from, Vec3 to) {
      if (MinecraftAccess.MINECRAFT.level == null) {
         return true;
      }
      ClipContext context = new ClipContext(from, to, Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player);
      return MinecraftAccess.MINECRAFT.level.clip(context).getType() == Type.MISS;
   }

   private boolean hasWeaponInMainHand(Player player) {
      ItemStack stack = player.getMainHandItem();
      Item item = stack.getItem();
      return stack.is(ItemTags.SWORDS) || item instanceof AxeItem || item instanceof TridentItem
         || (Boolean)this.mace.getValue() && item instanceof MaceItem;
   }

   private double currentReach(Player player) {
      boolean maceHand = (Boolean)this.mace.getValue() && player.getMainHandItem().getItem() instanceof MaceItem;
      return maceHand ? this.maceDistance.getFloat() : this.reach.getFloat();
   }

   private Vec3 getMultipointAimPoint(LivingEntity entity) {
      AABB box = entity.getBoundingBox();
      double fracX = (Boolean)this.multipoint.getValue() ? 0.5 + this.multipointX : 0.5;
      double fracY = (Boolean)this.multipoint.getValue() ? this.multipointY : 0.85;
      double fracZ = (Boolean)this.multipoint.getValue() ? 0.5 + this.multipointZ : 0.5;
      double x = box.minX + box.getXsize() * AimAssistMath.clampUnit(fracX);
      double y = box.minY + box.getYsize() * AimAssistMath.clampUnit(fracY);
      double z = box.minZ + box.getZsize() * AimAssistMath.clampUnit(fracZ);
      return new Vec3(x, y, z);
   }

   private void randomizeMultipointOffset() {
      if (!(Boolean)this.multipoint.getValue()) {
         this.jitterX = this.jitterZ = 0.0;
         this.jitterY = 0.85;
         this.nextMultipointUpdateTime = 0L;
         return;
      }
      ThreadLocalRandom random = ThreadLocalRandom.current();
      this.jitterX = random.nextDouble(-0.35, 0.35);
      this.jitterY = 0.85 + random.nextDouble(-0.12, 0.12);
      this.jitterZ = random.nextDouble(-0.35, 0.35);
      this.jitterSpeed = random.nextDouble(3.0, 6.0);
      this.nextMultipointUpdateTime = System.currentTimeMillis() + random.nextLong(500L, 1200L);
   }

   private void updateMultipointDrift(float dtSeconds) {
      if (!(Boolean)this.multipoint.getValue()) {
         this.multipointX = 0.0;
         this.multipointY = 0.85;
         this.multipointZ = 0.0;
         return;
      }
      long now = System.currentTimeMillis();
      if (now >= this.nextMultipointUpdateTime) {
         this.randomizeMultipointOffset();
      }
      float factor = AimAssistMath.expSmoothingFactor((float) this.jitterSpeed, dtSeconds);
      this.multipointX += (this.jitterX - this.multipointX) * factor;
      this.multipointY += (this.jitterY - this.multipointY) * factor;
      this.multipointZ += (this.jitterZ - this.multipointZ) * factor;
   }
}
