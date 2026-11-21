package wtf.onetap.module.movement;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.PostMotionEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.module.player.ElytraSwap;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "AirStuck", description = "Замораживает игрока", category = Category.MOVEMENT)
public class AirStuck extends Module {
   private final BooleanSetting swapToChestplate = new BooleanSetting("Свап на нагрудник", true).build();
   private final BooleanSetting restoreOnDisable = new BooleanSetting("Вернуть при выкл", true).visibleWhen(this.swapToChestplate::getValue).build();
   private final BooleanSetting fallCheck = new BooleanSetting("Проверка на падение", true).build();
   private final BooleanSetting increaseHitDistance = new BooleanSetting("Увеличивать дистанцию удара", true).build();
   private Vec3 vec3 = Vec3.ZERO;
   private boolean flag;
   private boolean flag2;
   private float floatValue = Float.NaN;
   private float floatValue2 = Float.NaN;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private int value;

   private boolean canStuck() {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null) {
         return false;
      } else if (!(Boolean)this.fallCheck.getValue()) {
         return true;
      } else if (this.flag5) {
         return true;
      } else if (MinecraftAccess.MINECRAFT.player.fallDistance > 0.08F) {
         this.flag5 = true;
         return true;
      } else {
         return false;
      }
   }

   public boolean m_7_boolean() {
      return this.isEnabled() && this.canStuck();
   }

   private boolean m_3_boolean(float floatValue3, float floatValue4) {
      return !Float.isNaN(this.floatValue) && Math.abs(Mth.wrapDegrees(floatValue3 - this.floatValue)) < 0.001F && Math.abs(floatValue4 - this.floatValue2) < 0.001F;
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (event.getPacket() instanceof ServerboundMovePlayerPacket serverboundMovePlayerPacket) {
            if (this.canStuck()) {
               event.cancel();
            } else {
               this.flag3 = serverboundMovePlayerPacket.isOnGround();
               this.flag4 = serverboundMovePlayerPacket.horizontalCollision();
            }
         }
      }
   }

   @EventTarget
   private void onPostMotion(PostMotionEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.canStuck() && this.value < 18) {
         Rotations rotations = Onetap.ROTATION_MODES.isFlag() ? Onetap.ROTATION_MODES.toRotations() : new Rotations(MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot());
         if (!this.m_3_boolean(rotations.getFloatValue(), rotations.getFloatValue2())) {
            PacketUtil.sendPacket(new Rot(rotations.getFloatValue(), rotations.getFloatValue2(), this.flag3, this.flag4));
            this.floatValue = rotations.getFloatValue();
            this.floatValue2 = rotations.getFloatValue2();
            this.value++;
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
         if (!this.flag2 && killAura != null && killAura.isEnabled() && killAura.flag10) {
            this.flag2 = true;
         }

         if (this.canStuck()) {
            MinecraftAccess.MINECRAFT.player.setDeltaMovement(0.0, 0.0, 0.0);
            MinecraftAccess.MINECRAFT.player.setNoGravity(true);
         } else {
            MinecraftAccess.MINECRAFT.player.setNoGravity(false);
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.canStuck()) {
            event.cancel();
         }
      }
   }

   @Override
   public void onEnable() {
      this.flag = false;
      NoClip.m_4_C3_sArr();
      this.flag2 = false;
      this.floatValue = Float.NaN;
      this.floatValue2 = Float.NaN;
      this.flag5 = false;
      this.value = 0;
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.floatValue = MinecraftAccess.MINECRAFT.player.getYRot();
         this.floatValue2 = MinecraftAccess.MINECRAFT.player.getXRot();
         this.flag3 = MinecraftAccess.MINECRAFT.player.onGround();
         this.flag4 = MinecraftAccess.MINECRAFT.player.horizontalCollision;
         this.vec3 = limitVelocity(MinecraftAccess.MINECRAFT.player.getDeltaMovement());
         if (this.canStuck()) {
            MinecraftAccess.MINECRAFT.player.setDeltaMovement(0.0, 0.0, 0.0);
            MinecraftAccess.MINECRAFT.player.setNoGravity(true);
         }

         boolean flag6 = MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
         if (flag6 && (Boolean)this.swapToChestplate.getValue()) {
            ElytraSwap elytraSwap = Onetap.MODULE_MANAGER.get(ElytraSwap.class);
            if (elytraSwap != null) {
               this.flag = elytraSwap.m_5_boolean(true);
            }
         }
      }
   }

   public void m_1_void() {
      this.flag2 = true;
   }

   public double getExtendedHitDistance() {
      return this.increaseHitDistance.getValue() && this.flag2 ? 5.65 : -1.0;
   }

   private static Vec3 limitVelocity(Vec3 vec32) {
      return vec32.lengthSqr() > 9.0 ? Vec3.ZERO : vec32;
   }

   @Override
   public void onDisable() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.setDeltaMovement(this.vec3);
         MinecraftAccess.MINECRAFT.player.setNoGravity(false);
         boolean flag6 = isChestplate(MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).getItem());
         if (flag6 && (Boolean)this.swapToChestplate.getValue() && (Boolean)this.restoreOnDisable.getValue() && this.flag) {
            ElytraSwap elytraSwap = Onetap.MODULE_MANAGER.get(ElytraSwap.class);
            if (elytraSwap != null) {
               if (elytraSwap.m_5_boolean(false)) {
                  elytraSwap.m_39_void();
               }

               this.flag = false;
               this.flag2 = false;
               this.floatValue = Float.NaN;
               this.floatValue2 = Float.NaN;
               this.flag5 = false;
               this.value = 0;
            }
         } else {
            this.flag = false;
            this.flag2 = false;
            this.floatValue = Float.NaN;
            this.floatValue2 = Float.NaN;
            this.flag5 = false;
            this.value = 0;
         }
      }
   }

   private static boolean isChestplate(Item item) {
      return item == Items.NETHERITE_CHESTPLATE
         || item == Items.DIAMOND_CHESTPLATE
         || item == Items.IRON_CHESTPLATE
         || item == Items.CHAINMAIL_CHESTPLATE
         || item == Items.GOLDEN_CHESTPLATE
         || item == Items.LEATHER_CHESTPLATE;
   }
}
