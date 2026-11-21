package wtf.onetap.module.combat;

import java.util.UUID;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.AutoSwapMode;
import wtf.onetap.combat.ProjectilePredictor;
import wtf.onetap.event.AttackEntityEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "TargetPearl", description = "Бросает жемчуг туда, куда упадёт жемчуг противника", category = Category.COMBAT)
public class TargetPearl extends Module {
   private final BooleanSetting onlyOnTarget = new BooleanSetting("Только за таргетом", true).build();
   private final FloatSetting minDistance = new FloatSetting("Мин. дистанция", 10.0F, 4.0F, 32.0F, 1.0F).build();
   private final FloatSetting tolerance = new FloatSetting("Погрешность", 2.0F, 0.5F, 4.0F, 0.25F).build();
   private final ModeSetting throwItem = new ModeSetting("Бросок", "Пакетный", "Пакетный", "Легитный").build();
   private final ModeSetting swapMode = new ModeSetting("Режим свапа", "Дефолт", "Дефолт", "Грим", "Легит").build();
   private final FloatSetting delay = new FloatSetting("Задержка", 50.0F, 0.0F, 150.0F, 1.0F).visibleWhen(() -> ((String)this.swapMode.getValue()).equals("Легит")).build();
   private final AutoSwapMode autoSwapMode = new AutoSwapMode();
   private UUID uUID;
   private int value = -1;
   private int value2 = -1;
   private Entity entity;
   private ProjectilePredictor.DataRecord c0;
   private int value3;
   private int value4;
   private Rotations rotations;
   private boolean flag;
   private boolean flag2;
   private Vec3 vec3;
   private Vec3 vec32;
   private int value5;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.rotations != null) {
            this.m_1_void();
         } else {
            this.autoSwapMode.m_5_void();
            if (this.m_7_boolean()) {
               ProjectilePredictor.DataRecord2 c3 = this.m_2_C3();
               if (c3 != null) {
                  this.rotations = this.m_8_C3(c3.m_3_f_C3b().getFloatValue()).m_3_f_C3b();
                  this.flag = false;
                  this.flag2 = false;
                  this.m_6_void(false);
               }
            }
         }
      }
   }

   private boolean m_7_boolean() {
      if (this.value == -1 && !this.m_3_boolean()) {
         return false;
      } else if (MinecraftAccess.MINECRAFT.level.getEntity(this.value) instanceof ThrownEnderpearl thrownEnderpearl && thrownEnderpearl.isAlive()) {
         ProjectilePredictor.DataRecord c02 = ProjectilePredictor.predictEntityPath(thrownEnderpearl.position(), thrownEnderpearl.getDeltaMovement(), thrownEnderpearl);
         this.entity = thrownEnderpearl.getOwner();
         this.c0 = c02.m_3v() ? c02 : null;
         this.value3 = c02.m_2g_int();
         this.value4 = thrownEnderpearl.tickCount + c02.m_2g_int();
         return this.c0 != null;
      } else {
         if (this.c0 != null && --this.value3 >= -10) {
            return true;
         }

         this.reset();
         return false;
      }
   }

   @EventTarget
   private void onAttackEntity(AttackEntityEvent event) {
      if (event.getEntity() instanceof Player player && player != MinecraftAccess.MINECRAFT.player) {
         this.uUID = player.getUUID();
      }
   }

   private boolean m_3_boolean(Entity entity2) {
      if (!(entity2 instanceof Player player && player != MinecraftAccess.MINECRAFT.player)) {
         return false;
      } else {
         return Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())
            ? false
            : !(Boolean)this.onlyOnTarget.getValue() || player.getUUID().equals(this.uUID) || player == KillAura.livingEntity;
      }
   }

   private boolean m_3_boolean() {
      ThrownEnderpearl thrownEnderpearl = null;

      for (Entity entity2 : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity2 instanceof ThrownEnderpearl thrownEnderpearl2
            && thrownEnderpearl2.isAlive()
            && thrownEnderpearl2.getId() != this.value2
            && this.m_3_boolean(thrownEnderpearl2.getOwner())
            && (thrownEnderpearl == null || thrownEnderpearl2.tickCount < thrownEnderpearl.tickCount)) {
            thrownEnderpearl = thrownEnderpearl2;
         }
      }

      if (thrownEnderpearl == null) {
         return false;
      }

      this.value = thrownEnderpearl.getId();
      return true;
   }

   private void reset() {
      this.value2 = this.value;
      this.value = -1;
      this.entity = null;
      this.c0 = null;
      this.vec3 = null;
   }

   private ProjectilePredictor.DataRecord2 m_2_C3() {
      if (!this.autoSwapMode.m_0_boolean() && !InventoryUtil.isBusy() && this.m_4_boolean()) {
         if (MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(Items.ENDER_PEARL.getDefaultInstance())) {
            return null;
         }

         if (MinecraftAccess.MINECRAFT.player.position().distanceTo(this.c0.m_39_Vec3()) < ((Float)this.minDistance.getValue()).floatValue()) {
            return null;
         }

         if (this.vec3 != null
            && MinecraftAccess.MINECRAFT.player.tickCount - this.value5 < 10
            && MinecraftAccess.MINECRAFT.player.position().distanceTo(this.vec3) < 0.5
            && this.c0.m_39_Vec3().distanceTo(this.vec32) < 0.5) {
            return null;
         }

         ProjectilePredictor.DataRecord2 c3 = this.m_2_C3(false);
         if (this.m_7(c3)) {
            return c3;
         }

         this.vec3 = MinecraftAccess.MINECRAFT.player.position();
         this.vec32 = this.c0.m_39_Vec3();
         this.value5 = MinecraftAccess.MINECRAFT.player.tickCount;
         return null;
      } else {
         return null;
      }
   }

   private void m_1_void() {
      if (!this.flag2) {
         this.rotations = this.m_8_C3(this.rotations.getFloatValue()).m_3_f_C3b();
      }

      this.m_6_void(this.flag2);
      if (!this.flag) {
         this.flag = true;
         if (!this.autoSwapMode.m_2((String)this.throwItem.getValue(), (String)this.swapMode.getValue(), (Float)this.delay.getValue(), this::m_1_boolean)) {
            this.rotations = null;
            return;
         }
      }

      this.autoSwapMode.m_5_void();
      if (!this.autoSwapMode.m_0_boolean()) {
         this.rotations = null;
         if (this.flag2) {
            this.reset();
         }
      }
   }

   private boolean m_1_boolean() {
      if (this.c0 == null) {
         return false;
      }

      ProjectilePredictor.DataRecord2 c3 = this.m_2_C3(true);
      if (!this.m_7(c3)) {
         return false;
      }

      this.rotations = c3.m_3_f_C3b();
      this.m_6_void(true);
      this.flag2 = true;
      return true;
   }

   private boolean m_7(ProjectilePredictor.DataRecord2 c3) {
      return c3.m_37() <= ((Float)this.tolerance.getValue()).floatValue() && c3.m_2_i() <= this.value4 + 10;
   }

   private ProjectilePredictor.DataRecord2 m_2_C3(boolean flag3) {
      Vec3 vec33 = MinecraftAccess.MINECRAFT.player.position();
      return this.m_6(vec33, vec33.subtract(MinecraftAccess.MINECRAFT.player.xo, MinecraftAccess.MINECRAFT.player.yo, MinecraftAccess.MINECRAFT.player.zo), MinecraftAccess.MINECRAFT.player.onGround(), flag3);
   }

   private ProjectilePredictor.DataRecord2 m_8_C3(float floatValue) {
      PlayerUtil playerUtil = PlayerUtil.fromLocalPlayer(PlayerUtil.StateHolder.m_6(MinecraftAccess.MINECRAFT.player.input.keyPresses));
      playerUtil.floatValue = floatValue;
      playerUtil.tick();
      return this.m_6(playerUtil.vec3, playerUtil.vec3.subtract(MinecraftAccess.MINECRAFT.player.position()), playerUtil.flag4, false);
   }

   private ProjectilePredictor.DataRecord2 m_6(Vec3 vec33, Vec3 vec34, boolean flag3, boolean flag4) {
      Vec3 vec35 = vec33.add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight() - 0.1, 0.0);
      Vec3 vec36 = ProjectilePredictor.flatten(vec34, flag3);
      ProjectilePredictor.CallbackType c1 = ProjectilePredictor.m_2(MinecraftAccess.MINECRAFT.player, this.entity, this.value3);
      ProjectilePredictor.DataRecord2 c3 = ProjectilePredictor.m_3(vec35, vec36, this.c0, ((Float)this.tolerance.getValue()).floatValue(), this.value4 + 10, c1);
      return flag4 && c3.m_7_boolean() ? this.m_4(vec35, vec36, c3, c1) : c3;
   }

   private ProjectilePredictor.DataRecord2 m_4(Vec3 vec33, Vec3 vec34, ProjectilePredictor.DataRecord2 c3, ProjectilePredictor.CallbackType c1) {
      float floatValue = MinecraftAccess.MINECRAFT.options.sensitivity().get().floatValue() * 0.6F + 0.2F;
      float floatValue2 = floatValue * floatValue * floatValue * 1.2F;
      float floatValue3 = Onetap.ROTATION_MODES.getFloatValue();
      float floatValue4 = Onetap.ROTATION_MODES.getFloatValue2();
      float floatValue5 = floatValue3 + Math.round(Mth.wrapDegrees(c3.m_3_f_C3b().getFloatValue() - floatValue3) / floatValue2) * floatValue2;
      float floatValue6 = floatValue4 + Math.round((c3.m_3_f_C3b().getFloatValue2() - floatValue4) / floatValue2) * floatValue2;
      ProjectilePredictor.DataRecord2 c32 = null;

      for (int i = -2; i <= 2; i++) {
         for (int i2 = -2; i2 <= 2; i2++) {
            float floatValue7 = floatValue6 + i2 * floatValue2;
            if (!(floatValue7 < -90.0F) && !(floatValue7 > 90.0F)) {
               ProjectilePredictor.DataRecord2 c33 = ProjectilePredictor.m_4(vec33, vec34, new Rotations(floatValue5 + i * floatValue2, floatValue7), this.c0, c1);
               if (c32 == null || c33.m_37() < c32.m_37()) {
                  c32 = c33;
               }
            }
         }
      }

      return c32 != null ? c32 : c3;
   }

   private boolean m_4_boolean() {
      return MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.ENDER_PEARL)
         || MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.ENDER_PEARL)
         || InventoryUtil.findHotbarSlot(Items.ENDER_PEARL) != -1
         || InventoryUtil.findItemSlotInRange(Items.ENDER_PEARL, 9, 36) != -1;
   }

   private void m_6_void(boolean flag3) {
      if (flag3) {
         Onetap.ROTATION_MODES.rotate(this.rotations, false, 100);
      } else {
         Onetap.ROTATION_MODES.rotateWithSensitivity(this.rotations, false, 100);
      }
   }

   @Override
   public void onDisable() {
      this.uUID = null;
      this.value = -1;
      this.entity = null;
      this.c0 = null;
      this.vec3 = null;
      this.rotations = null;
      this.flag = false;
      this.flag2 = false;
   }
}
