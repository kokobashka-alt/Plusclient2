package wtf.onetap.module.misc;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "FakePlayer", description = "Спавнит фейкового игрока-копию", category = Category.MISC)
public class FakePlayer extends Module {
   private static int value = -1;
   private final ModeSetting mode = new ModeSetting("Мод", "Статичный", "Статичный", "Физика").build();
   private RemotePlayer remotePlayer;
   private String text;

   public static boolean m_5(Player player, Entity entity) {
      if (!(entity instanceof FakePlayer.StateHolder c5)) {
         return false;
      } else if (player.level() instanceof ClientLevel clientLevel) {
         float floatValue = player.getAttackStrengthScale(0.5F);
         boolean flag = floatValue > 0.9F;
         boolean flag2 = player.isSprinting() && flag;
         boolean flag3 = flag
            && player.fallDistance > 0.0
            && !player.onGround()
            && !player.onClimbable()
            && !player.isInWater()
            && !player.isMobilityRestricted()
            && !player.isPassenger()
            && !player.isSprinting();
         if (flag2) {
            playSound(clientLevel, player, SoundEvents.PLAYER_ATTACK_KNOCKBACK);
         }

         DamageSource damageSource = player.damageSources().playerAttack(player);
         c5.m_5(damageSource, player, flag2);
         if (flag3) {
            playSound(clientLevel, player, SoundEvents.PLAYER_ATTACK_CRIT);
            player.crit(c5);
         } else {
            playSound(clientLevel, player, flag ? SoundEvents.PLAYER_ATTACK_STRONG : SoundEvents.PLAYER_ATTACK_WEAK);
         }

         player.setLastHurtMob(c5);
         player.onAttack();
         player.resetAttackStrengthTicker();
         return true;
      } else {
         return false;
      }
   }

   private static void playSound(ClientLevel clientLevel, Entity entity, SoundEvent event) {
      clientLevel.playLocalSound(entity, event, SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   @Override
   public void onEnable() {
      this.spawnFake();
   }

   @Override
   public void onDisable() {
      this.removeFake();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.remotePlayer == null || !((String)this.mode.getValue()).equals(this.text)) {
         this.spawnFake();
      }
   }

   private void spawnFake() {
      UnHook.m_7_IArr();
      this.removeFake();
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         GameProfile gameProfile = new GameProfile(UUID.randomUUID(), MinecraftAccess.MINECRAFT.player.getGameProfile().name());
         boolean flag = ((String)this.mode.getValue()).equals("Физика");
         RemotePlayer remotePlayer2 = flag ? new FakePlayer.StateHolder(MinecraftAccess.MINECRAFT.level, gameProfile) : new RemotePlayer(MinecraftAccess.MINECRAFT.level, gameProfile);
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.position();
         remotePlayer2.setPos(vec3);
         remotePlayer2.setYRot(MinecraftAccess.MINECRAFT.player.getYRot());
         remotePlayer2.setXRot(MinecraftAccess.MINECRAFT.player.getXRot());
         remotePlayer2.setYHeadRot(MinecraftAccess.MINECRAFT.player.getYHeadRot());
         remotePlayer2.setYBodyRot(MinecraftAccess.MINECRAFT.player.yBodyRot);
         remotePlayer2.yRotO = MinecraftAccess.MINECRAFT.player.yRotO;
         remotePlayer2.xRotO = MinecraftAccess.MINECRAFT.player.xRotO;
         remotePlayer2.yHeadRotO = MinecraftAccess.MINECRAFT.player.yHeadRotO;
         remotePlayer2.yBodyRotO = MinecraftAccess.MINECRAFT.player.yBodyRotO;
         remotePlayer2.setDeltaMovement(Vec3.ZERO);
         remotePlayer2.setNoGravity(!flag && MinecraftAccess.MINECRAFT.player.isNoGravity());
         remotePlayer2.setOnGround(MinecraftAccess.MINECRAFT.player.onGround());
         remotePlayer2.setShiftKeyDown(MinecraftAccess.MINECRAFT.player.isShiftKeyDown());
         remotePlayer2.setSprinting(MinecraftAccess.MINECRAFT.player.isSprinting());
         remotePlayer2.setSwimming(MinecraftAccess.MINECRAFT.player.isSwimming());
         remotePlayer2.setInvisible(false);
         remotePlayer2.setId(value--);
         if (flag) {
            remotePlayer2.noPhysics = false;
         }

         remotePlayer2.setItemSlot(EquipmentSlot.HEAD, MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.HEAD).copy());
         remotePlayer2.setItemSlot(EquipmentSlot.CHEST, MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).copy());
         remotePlayer2.setItemSlot(EquipmentSlot.LEGS, MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.LEGS).copy());
         remotePlayer2.setItemSlot(EquipmentSlot.FEET, MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.FEET).copy());
         remotePlayer2.setItemSlot(EquipmentSlot.MAINHAND, MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.MAINHAND).copy());
         remotePlayer2.setItemSlot(EquipmentSlot.OFFHAND, MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.OFFHAND).copy());
         remotePlayer2.setCustomName(Component.literal(MinecraftAccess.MINECRAFT.player.getName().getString()));
         remotePlayer2.setCustomNameVisible(false);
         remotePlayer2.setOldPosAndRot();
         MinecraftAccess.MINECRAFT.level.addEntity(remotePlayer2);
         this.remotePlayer = remotePlayer2;
         this.text = (String)this.mode.getValue();
      }
   }

   private void removeFake() {
      if (this.remotePlayer != null) {
         if (MinecraftAccess.MINECRAFT.level != null) {
            MinecraftAccess.MINECRAFT.level.removeEntity(this.remotePlayer.getId(), RemovalReason.DISCARDED);
         }

         this.remotePlayer = null;
         this.text = null;
      }
   }

   private static final class StateHolder extends RemotePlayer {
      public StateHolder(ClientLevel clientLevel, GameProfile gameProfile) {
         super(clientLevel, gameProfile);
         super.noPhysics = false;
         this.setNoGravity(false);
         AttributeInstance attributeInstance = this.getAttribute(Attributes.GRAVITY);
         if (attributeInstance != null) {
            attributeInstance.setBaseValue(0.08);
         }

         AttributeInstance attributeInstance2 = this.getAttribute(Attributes.AIR_DRAG_MODIFIER);
         if (attributeInstance2 != null) {
            attributeInstance2.setBaseValue(1.0);
         }
      }

      public void m_5(DamageSource damageSource, Player player, boolean flag) {
         this.dealDefaultKnockback(damageSource, 1.0F, false);
         UnHook.m_7_IArr();
         float floatValue = (float)player.getAttributeValue(Attributes.ATTACK_KNOCKBACK) * 0.5F;
         if (flag) {
            floatValue += 0.5F;
         }

         if (floatValue > 0.0F) {
            float floatValue2 = player.getYRot() * (float) (Math.PI / 180.0);
            this.knockback(floatValue, Mth.sin(floatValue2), -Mth.cos(floatValue2), damageSource, 1.0F, true);
            player.setDeltaMovement(player.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         }

         super.invulnerableTime = 20;
         super.hurtDuration = 10;
         super.hurtTime = 10;
         this.markHurt();
         if (this.level() instanceof ClientLevel clientLevel) {
            SoundEvent event = this.getHurtSound(damageSource);
            if (event != null) {
               float floatValue3 = 1.0F + (super.random.nextFloat() - super.random.nextFloat()) * 0.2F;
               clientLevel.playLocalSound(this, event, this.getSoundSource(), this.getSoundVolume(), floatValue3);
            }
         }
      }

      @Override
      public double getDefaultGravity() {
         return 0.08;
      }

      @Override
      public boolean canSimulateMovement() {
         return true;
      }

      @Override
      public boolean isEffectiveAi() {
         return true;
      }

      @Override
      public boolean isLocalClientAuthoritative() {
         return true;
      }

      @Override
      public boolean hurtClient(DamageSource damageSource) {
         return true;
      }

      @Override
      public void tick() {
         super.noPhysics = false;
         super.tick();
      }

      @Override
      public void aiStep() {
         super.xxa = 0.0F;
         super.yya = 0.0F;
         super.zza = 0.0F;
         super.jumping = false;
         super.noPhysics = false;
         this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
         Vec3 vec3 = this.getDeltaMovement();
         double doubleValue = vec3.x;
         double doubleValue2 = vec3.y;
         double doubleValue3 = vec3.z;
         if (vec3.horizontalDistanceSqr() < 9.0E-6) {
            doubleValue = 0.0;
            doubleValue3 = 0.0;
         }

         if (Math.abs(doubleValue2) < 0.003) {
            doubleValue2 = 0.0;
         }

         this.setDeltaMovement(doubleValue, doubleValue2, doubleValue3);
         if (!this.isInWater() && !this.isInLava() && !this.isFallFlying()) {
            this.m_2_void();
         } else {
            this.travel(Vec3.ZERO);
         }

         this.updateSwingTime();
         this.updateBob();
         this.calculateEntityAnimation(false);
      }

      private void m_2_void() {
         this.move(MoverType.SELF, this.getDeltaMovement());
         Vec3 vec3 = this.getDeltaMovement();
         double doubleValue = 0.08;
         if (vec3.y <= 0.0 && this.hasEffect(MobEffects.SLOW_FALLING)) {
            doubleValue = 0.01;
         }

         BlockPos blockPos = this.getBlockPosBelowThatAffectsMyMovement();
         float floatValue = this.onGround() ? this.level().getBlockState(blockPos).getBlock().getFriction() : 1.0F;
         float floatValue2 = (this.onGround() ? floatValue : 1.0F) * 0.91F;
         double doubleValue2 = this.isNoGravity() ? vec3.y : vec3.y - doubleValue;
         this.setDeltaMovement(vec3.x * floatValue2, doubleValue2 * 0.98F, vec3.z * floatValue2);
      }
   }
}
