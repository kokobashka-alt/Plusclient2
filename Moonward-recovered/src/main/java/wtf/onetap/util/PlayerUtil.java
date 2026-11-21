package wtf.onetap.util;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.mixin.entity.LivingEntityInvoker;
import wtf.onetap.module.player.NoDelay;

public class PlayerUtil implements MinecraftAccess {
   private static final Identifier IDENTIFIER = Identifier.withDefaultNamespace("sprinting");
   public final Player player;
   public final PlayerUtil.StateHolder c1;
   public Vec3 vec3;
   public Vec3 vec32;
   public AABB aABB;
   public float floatValue;
   public float floatValue2;
   public boolean flag;
   public float floatValue3;
   public int value;
   public boolean flag2;
   public boolean flag3;
   public boolean flag4;
   public boolean flag5;
   public boolean flag6;
   public boolean flag7;
   public boolean flag8;
   public boolean flag9;
   private final Object2DoubleMap<TagKey<Fluid>> object2DoubleMap;
   private final Set<TagKey<Fluid>> tagKeySet;
   private int value2;
   private boolean flag10;
   private static String text;

   public PlayerUtil(
      Player player2,
      PlayerUtil.StateHolder c12,
      Vec3 vec33,
      Vec3 vec34,
      AABB aABB2,
      float floatValue4,
      float floatValue5,
      boolean flag11,
      float floatValue6,
      int value3,
      boolean flag12,
      boolean flag13,
      boolean flag14,
      boolean flag15,
      boolean flag16,
      boolean flag17,
      boolean flag18,
      boolean flag19,
      Object2DoubleMap<TagKey<Fluid>> object2DoubleMap2,
      Set<TagKey<Fluid>> tagKeySet2
   ) {
      this.player = player2;
      this.c1 = c12;
      this.vec3 = vec33;
      this.vec32 = vec34;
      this.aABB = aABB2;
      this.floatValue = floatValue4;
      this.floatValue2 = floatValue5;
      this.flag = flag11;
      this.floatValue3 = floatValue6;
      this.value = value3;
      this.flag2 = flag12;
      this.flag3 = flag13;
      this.flag4 = flag14;
      this.flag5 = flag15;
      this.flag6 = flag16;
      this.flag7 = flag17;
      this.flag8 = flag18;
      this.flag9 = flag19;
      this.object2DoubleMap = object2DoubleMap2;
      this.tagKeySet = tagKeySet2;
   }

   public static PlayerUtil predictLocal(int value3) {
      PlayerUtil playerUtil = fromLocalPlayer(PlayerUtil.StateHolder.m_6(MinecraftAccess.MINECRAFT.player.input.keyPresses));

      for (int i = 0; i < value3; i++) {
         playerUtil.tick();
      }

      return playerUtil;
   }

   public static PlayerUtil predict(Player player2, int value3) {
      PlayerUtil playerUtil = fromPlayer(player2, PlayerUtil.StateHolder.m_7_C1(player2));

      for (int i = 0; i < value3; i++) {
         playerUtil.tick();
      }

      return playerUtil;
   }

   private static boolean isInFluid(Player player2, TagKey<Fluid> tagKey) {
      BlockPos blockPos = BlockPos.containing(player2.getX(), player2.getEyeY(), player2.getZ());
      return player2.level().getFluidState(blockPos).typeHolder().is(tagKey);
   }

   public static PlayerUtil fromLocalPlayer(PlayerUtil.StateHolder c12) {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      return new PlayerUtil(
         localPlayer,
         c12,
         localPlayer.position(),
         localPlayer.getDeltaMovement(),
         localPlayer.getBoundingBox(),
         localPlayer.getYRot(),
         localPlayer.getXRot(),
         localPlayer.isSprinting(),
         (float)localPlayer.fallDistance,
         m_3_int(localPlayer),
         localPlayer.isJumping(),
         localPlayer.isFallFlying(),
         localPlayer.onGround(),
         localPlayer.horizontalCollision,
         localPlayer.verticalCollision,
         localPlayer.isInWater(),
         localPlayer.isSwimming(),
         isInFluid(localPlayer, FluidTags.WATER),
         new Object2DoubleArrayMap(),
         new HashSet<>()
      );
   }

   private static int m_3_int(Player player2) {
      NoDelay noDelay = Onetap.MODULE_MANAGER.get(NoDelay.class);
      return noDelay != null && noDelay.isEnabled() && noDelay.multiModeSetting.isSelected("Прыжок") ? 0 : ((LivingEntityInvoker)player2).onetap$getNoJumpDelay();
   }

   public static PlayerUtil fromPlayer(Player player2, PlayerUtil.StateHolder c12) {
      return new PlayerUtil(
         player2,
         c12,
         player2.position(),
         player2.position().subtract(player2.xo, player2.yo, player2.zo),
         player2.getBoundingBox(),
         player2.getYRot(),
         player2.getXRot(),
         player2.isSprinting(),
         (float)player2.fallDistance,
         0,
         player2.isJumping(),
         player2.isFallFlying(),
         player2.onGround(),
         player2.horizontalCollision,
         player2.verticalCollision,
         player2.isInWater(),
         player2.isSwimming(),
         isInFluid(player2, FluidTags.WATER),
         new Object2DoubleArrayMap(),
         new HashSet<>()
      );
   }

   public void tick() {
      this.value2++;
      this.flag10 = false;
      if (!(this.vec3.y <= -70.0)) {
         this.c1.m_0_void();
         this.updateWaterFlag();
         this.updateFluids();
         this.updateSwimming();
         if (this.value > 0) {
            this.value--;
         }

         this.flag2 = this.c1.input.jump();
         double doubleValue = Math.abs(this.vec32.x) < 0.003 ? 0.0 : this.vec32.x;
         double doubleValue2 = Math.abs(this.vec32.y) < 0.003 ? 0.0 : this.vec32.y;
         double doubleValue3 = Math.abs(this.vec32.z) < 0.003 ? 0.0 : this.vec32.z;
         if (this.flag4) {
            this.flag3 = false;
         }

         this.vec32 = new Vec3(doubleValue, doubleValue2, doubleValue3);
         if (this.flag2) {
            double doubleValue4 = this.isInLava() ? this.object2DoubleMap.getDouble(FluidTags.LAVA) : this.object2DoubleMap.getDouble(FluidTags.WATER);
            boolean flag11 = this.flag7 && doubleValue4 > 0.0;
            double doubleValue5 = this.m_3_double();
            if (!flag11 || this.flag4 && !(doubleValue4 > doubleValue5)) {
               if (!this.isInLava() || this.flag4 && !(doubleValue4 > doubleValue5)) {
                  if ((this.flag4 || flag11 && doubleValue4 <= doubleValue5) && this.value == 0) {
                     this.m_6_void();
                     this.value = 10;
                  }
               } else {
                  this.swimUp(FluidTags.LAVA);
               }
            } else {
               this.swimUp(FluidTags.WATER);
            }
         }

         float floatValue4 = this.c1.floatValue2 * 0.98F;
         float floatValue5 = this.c1.floatValue * 0.98F;
         if (this.hasEffect(MobEffects.SLOW_FALLING) || this.hasEffect(MobEffects.LEVITATION)) {
            this.m_4_void();
         }

         this.travel(new Vec3(floatValue4, 0.0, floatValue5));
      }
   }

   private void travel(Vec3 vec33) {
      InventoryUtil.m_8_StringArr();
      double doubleValue = 0.08;
      boolean flag11 = this.vec32.y <= 0.0;
      if (this.vec32.y <= 0.0 && this.hasEffect(MobEffects.SLOW_FALLING)) {
         doubleValue = 0.01;
         this.m_4_void();
      }

      if (this.flag7 && this.player.isAffectedByFluids()) {
         double doubleValue2 = this.vec3.y;
         float floatValue4 = this.isFlag() ? 0.9F : 0.8F;
         float floatValue5 = 0.02F;
         float floatValue6 = (float)this.player.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
         if (!this.flag4) {
            floatValue6 *= 0.5F;
         }

         if (floatValue6 > 0.0F) {
            floatValue4 += (0.54600006F - floatValue4) * floatValue6 / 3.0F;
            floatValue5 += (this.getMovementSpeed() - floatValue5) * floatValue6 / 3.0F;
         }

         if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
            floatValue4 = 0.96F;
         }

         if (this.flag8 && !this.player.isPassenger()) {
            double doubleValue3 = Mth.clamp(this.getLookVector().y, -1.0, 1.0);
            double doubleValue4 = doubleValue3 < -0.2 ? 0.085 : 0.06;
            BlockPos blockPos = BlockPos.containing(this.vec3.x, this.vec3.y + 1.0 - 0.1, this.vec3.z);
            if (doubleValue3 <= 0.0 || this.flag2 || !this.player.level().getFluidState(blockPos).isEmpty()) {
               this.vec32 = this.vec32.add(0.0, (doubleValue3 - this.vec32.y) * doubleValue4, 0.0);
            }
         }

         this.moveRelative(floatValue5, vec33);
         this.move(this.vec32);
         Vec3 vec34 = this.vec32;
         if (this.flag5 && this.isOnClimbable()) {
            vec34 = new Vec3(vec34.x, 0.2, vec34.z);
         }

         this.vec32 = vec34.multiply(floatValue4, 0.8, floatValue4);
         this.m_2(doubleValue, flag11);
         if (this.flag5 && this.canMove(this.vec32.x, this.vec32.y + 0.6 - this.vec3.y + doubleValue2, this.vec32.z)) {
            this.vec32 = new Vec3(this.vec32.x, 0.3, this.vec32.z);
         }
      } else if (this.isInLava() && this.player.isAffectedByFluids()) {
         double doubleValue5 = this.vec3.y;
         this.moveRelative(0.02F, vec33);
         this.move(this.vec32);
         if (this.object2DoubleMap.getDouble(FluidTags.LAVA) <= this.m_3_double()) {
            this.vec32 = this.vec32.multiply(0.5, 0.8, 0.5);
            this.m_2(doubleValue, flag11);
         } else {
            this.vec32 = this.vec32.scale(0.5);
         }

         if (!this.player.isNoGravity()) {
            this.vec32 = this.vec32.add(0.0, -doubleValue / 4.0, 0.0);
         }

         if (this.flag5 && this.canMove(this.vec32.x, this.vec32.y + 0.6 - this.vec3.y + doubleValue5, this.vec32.z)) {
            this.vec32 = new Vec3(this.vec32.x, 0.3, this.vec32.z);
         }
      } else if (this.flag3) {
         Vec3 vec35 = this.vec32;
         if (vec35.y > -0.5) {
            this.floatValue3 = 1.0F;
         }

         Vec3 vec36 = this.getLookVector();
         float floatValue7 = this.floatValue2 * (float) (Math.PI / 180.0);
         double doubleValue6 = Math.sqrt(vec36.x * vec36.x + vec36.z * vec36.z);
         double doubleValue7 = this.vec32.horizontalDistance();
         double doubleValue8 = vec36.length();
         float floatValue8 = Mth.cos(floatValue7);
         floatValue8 = (float)(floatValue8 * (floatValue8 * Math.min(1.0, doubleValue8 / 0.4)));
         vec35 = this.vec32.add(0.0, doubleValue * (-1.0 + floatValue8 * 0.75), 0.0);
         if (vec35.y < 0.0 && doubleValue6 > 0.0) {
            double doubleValue9 = vec35.y * -0.1 * floatValue8;
            vec35 = vec35.add(vec36.x * doubleValue9 / doubleValue6, doubleValue9, vec36.z * doubleValue9 / doubleValue6);
         }

         if (floatValue7 < 0.0F && doubleValue6 > 0.0) {
            double doubleValue10 = doubleValue7 * -Mth.sin(floatValue7) * 0.04;
            vec35 = vec35.add(-vec36.x * doubleValue10 / doubleValue6, doubleValue10 * 3.2, -vec36.z * doubleValue10 / doubleValue6);
         }

         if (doubleValue6 > 0.0) {
            vec35 = vec35.add((vec36.x / doubleValue6 * doubleValue7 - vec35.x) * 0.1, 0.0, (vec36.z / doubleValue6 * doubleValue7 - vec35.z) * 0.1);
         }

         this.vec32 = vec35.multiply(0.99, 0.98, 0.99);
         this.move(this.vec32);
      } else {
         BlockPos blockPos2 = this.getGroundPos();
         float floatValue9 = this.player.level().getBlockState(blockPos2).getBlock().getFriction();
         float floatValue10 = this.flag4 ? floatValue9 * 0.91F : 0.91F;
         Vec3 vec37 = this.m_7(vec33, floatValue9);
         double doubleValue11 = vec37.y;
         if (this.hasEffect(MobEffects.LEVITATION)) {
            MobEffectInstance mobEffectInstance = this.getEffect(MobEffects.LEVITATION);
            if (mobEffectInstance != null) {
               doubleValue11 += (0.05 * (mobEffectInstance.getAmplifier() + 1) - vec37.y) * 0.2;
            }
         } else if (!this.player.isNoGravity()) {
            doubleValue11 -= doubleValue;
         }

         if (this.player.shouldDiscardFriction()) {
            this.vec32 = new Vec3(vec37.x, doubleValue11, vec37.z);
         } else {
            this.vec32 = new Vec3(vec37.x * floatValue10, doubleValue11 * 0.98F, vec37.z * floatValue10);
         }
      }
   }

   private void m_2(double doubleValue, boolean flag11) {
      if (doubleValue != 0.0 && !this.flag) {
         double doubleValue2;
         if (flag11 && Math.abs(this.vec32.y - 0.005) >= 0.003 && Math.abs(this.vec32.y - doubleValue / 16.0) < 0.003) {
            doubleValue2 = -0.003;
         } else {
            doubleValue2 = this.vec32.y - doubleValue / 16.0;
         }

         this.vec32 = new Vec3(this.vec32.x, doubleValue2, this.vec32.z);
      }
   }

   private Vec3 m_7(Vec3 vec33, float floatValue4) {
      this.moveRelative(this.getFrictionAdjustedSpeed(floatValue4), vec33);
      this.vec32 = this.applyClimbing(this.vec32);
      this.move(this.vec32);
      Vec3 vec34 = this.vec32;
      BlockPos blockPos = this.toBlockPos(this.vec3);
      BlockState blockState = this.player.level().getBlockState(blockPos);
      if ((this.flag5 || this.flag2) && (this.isOnClimbable() || blockState.is(Blocks.POWDER_SNOW) && PowderSnowBlock.canEntityWalkOnPowderSnow(this.player))) {
         vec34 = new Vec3(vec34.x, 0.2, vec34.z);
      }

      return vec34;
   }

   private void moveRelative(float floatValue4, Vec3 vec33) {
      Vec3 vec34 = this.calcRelativeMovement(vec33, floatValue4, this.floatValue);
      this.vec32 = this.vec32.add(vec34);
   }

   private float getFrictionAdjustedSpeed(float floatValue4) {
      return this.flag4 ? this.getMovementSpeed() * (0.21600002F / (floatValue4 * floatValue4 * floatValue4)) : this.getFlyingSpeed();
   }

   private float getFlyingSpeed() {
      float floatValue4 = 0.02F;
      if (this.c1.input.sprint()) {
         floatValue4 += 0.006F;
      }

      return floatValue4;
   }

   private float getMovementSpeed() {
      AttributeInstance attributeInstance = this.player.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attributeInstance == null) {
         return this.flag ? 0.13F : 0.1F;
      }

      double doubleValue = attributeInstance.getValue();
      AttributeModifier attributeModifier = attributeInstance.getModifier(IDENTIFIER);
      if (attributeModifier != null) {
         doubleValue /= 1.0 + attributeModifier.amount();
      }

      return (float)(this.flag ? doubleValue * 1.3 : doubleValue);
   }

   private Vec3 calcRelativeMovement(Vec3 vec33, float floatValue4, float floatValue5) {
      double doubleValue = vec33.lengthSqr();
      if (doubleValue < 1.0E-7) {
         return Vec3.ZERO;
      }

      Vec3 vec34 = (doubleValue > 1.0 ? vec33.normalize() : vec33).scale(floatValue4);
      float floatValue6 = Mth.sin(floatValue5 * (float) (Math.PI / 180.0));
      float floatValue7 = Mth.cos(floatValue5 * (float) (Math.PI / 180.0));
      return new Vec3(vec34.x * floatValue7 - vec34.z * floatValue6, vec34.y, vec34.x * floatValue6 + vec34.z * floatValue7);
   }

   private void move(Vec3 vec33) {
      Vec3 vec34 = this.clampSneakMovement(vec33);
      Vec3 vec35 = this.collide(vec34);
      if (vec35.lengthSqr() > 1.0E-7) {
         this.vec3 = this.vec3.add(vec35);
         this.aABB = this.getBoundingBoxAt(this.vec3);
      }

      boolean flag11 = vec33.x != vec35.x;
      boolean flag12 = vec33.z != vec35.z;
      this.flag5 = flag11 || flag12;
      this.flag6 = vec33.y != vec35.y;
      this.flag4 = this.flag6 && vec33.y < 0.0;
      if (!this.flag7) {
         this.updateWaterFlag();
      }

      if (this.flag4) {
         this.m_4_void();
      } else if (vec33.y < 0.0) {
         this.floatValue3 = this.floatValue3 - (float)vec33.y;
      }

      Vec3 vec36 = this.vec32;
      if (this.flag5 || this.flag6) {
         this.vec32 = new Vec3(flag11 ? 0.0 : vec36.x, this.flag4 ? 0.0 : vec36.y, flag12 ? 0.0 : vec36.z);
      }
   }

   private Vec3 collide(Vec3 vec33) {
      AABB aABB2 = this.aABB;
      var voxelShapeList = Entity.collectAllColliders(this.player, this.player.level(), aABB2.expandTowards(vec33));
      Vec3 vec34 = Entity.collideBoundingBox(this.player, vec33, aABB2, this.player.level(), voxelShapeList);
      boolean flag11 = vec33.x != vec34.x;
      boolean flag12 = vec33.y != vec34.y;
      boolean flag13 = vec33.z != vec34.z;
      boolean flag14 = this.flag4 || flag12 && vec33.y < 0.0;
      if (this.player.maxUpStep() > 0.0F && flag14 && (flag11 || flag13)) {
         AABB aABB3 = aABB2;
         var voxelShapeList2 = Entity.collectAllColliders(this.player, this.player.level(), aABB3.expandTowards(new Vec3(vec33.x, this.player.maxUpStep(), vec33.z)));
         Vec3 vec35 = Entity.collideBoundingBox(this.player, new Vec3(vec33.x, this.player.maxUpStep(), vec33.z), aABB3, this.player.level(), voxelShapeList2);
         List list = Entity.collectAllColliders(
            this.player, this.player.level(), aABB3.expandTowards(vec33.x, 0.0, vec33.z).expandTowards(0.0, this.player.maxUpStep(), 0.0)
         );
         Vec3 vec36 = Entity.collideBoundingBox(
            this.player, new Vec3(0.0, this.player.maxUpStep(), 0.0), aABB3.expandTowards(vec33.x, 0.0, vec33.z), this.player.level(), list
         );
         AABB aABB4 = aABB3.move(vec36);
         var voxelShapeList3 = Entity.collectAllColliders(this.player, this.player.level(), aABB4.expandTowards(vec33.x, 0.0, vec33.z));
         Vec3 vec37 = Entity.collideBoundingBox(this.player, new Vec3(vec33.x, 0.0, vec33.z), aABB4, this.player.level(), voxelShapeList3).add(vec36);
         if (vec36.y < this.player.maxUpStep() && vec37.horizontalDistanceSqr() > vec35.horizontalDistanceSqr()) {
            vec35 = vec37;
         }

         if (vec35.horizontalDistanceSqr() > vec34.horizontalDistanceSqr()) {
            AABB aABB5 = aABB3.move(vec35);
            var voxelShapeList4 = Entity.collectAllColliders(this.player, this.player.level(), aABB5.expandTowards(0.0, -vec35.y + vec33.y, 0.0));
            return vec35.add(Entity.collideBoundingBox(this.player, new Vec3(0.0, -vec35.y + vec33.y, 0.0), aABB5, this.player.level(), voxelShapeList4));
         }
      }

      return vec34;
   }

   private void m_4_void() {
      this.floatValue3 = 0.0F;
   }

   private void m_6_void() {
      this.vec32 = this.vec32.add(0.0, this.getJumpPower() - this.vec32.y, 0.0);
      if (this.isFlag()) {
         float floatValue4 = this.floatValue * (float) (Math.PI / 180.0);
         this.vec32 = this.vec32.add(-Mth.sin(floatValue4) * 0.2, 0.0, Mth.cos(floatValue4) * 0.2);
      }
   }

   private Vec3 applyClimbing(Vec3 vec33) {
      if (!this.isOnClimbable()) {
         return vec33;
      }

      this.m_4_void();
      double doubleValue = Mth.clamp(vec33.x, -0.15F, 0.15F);
      double doubleValue2 = Mth.clamp(vec33.z, -0.15F, 0.15F);
      double doubleValue3 = Math.max(vec33.y, -0.15F);
      if (doubleValue3 < 0.0 && !this.getBlockState(this.toBlockPos(this.vec3)).is(Blocks.SCAFFOLDING) && this.player.isSuppressingSlidingDownLadder()) {
         doubleValue3 = 0.0;
      }

      return new Vec3(doubleValue, doubleValue3, doubleValue2);
   }

   public boolean isOnClimbable() {
      BlockPos blockPos = this.toBlockPos(this.vec3);
      BlockState blockState = this.player.level().getBlockState(blockPos);
      return blockState.is(BlockTags.CLIMBABLE) ? true : blockState.getBlock() instanceof TrapDoorBlock && this.isClimbableTrapdoor(blockPos, blockState);
   }

   private boolean isClimbableTrapdoor(BlockPos blockPos, BlockState blockState) {
      return !blockState.getValue(TrapDoorBlock.OPEN)
         ? false
         : this.player.level().getBlockState(blockPos.below()).is(Blocks.LADDER)
            && this.player.level().getBlockState(blockPos.below()).getValue(LadderBlock.FACING) == blockState.getValue(HorizontalDirectionalBlock.FACING);
   }

   private Vec3 clampSneakMovement(Vec3 vec33) {
      if (vec33.y <= 0.0 && this.isOnGround()) {
         double doubleValue = vec33.x;
         double doubleValue2 = vec33.z;

         double doubleValue3;
         for (doubleValue3 = 0.05; doubleValue != 0.0 && this.player.level().noCollision(this.player, this.aABB.move(doubleValue, -0.5, 0.0)); doubleValue += doubleValue > 0.0 ? -doubleValue3 : doubleValue3) {
            if (doubleValue < doubleValue3 && doubleValue >= -doubleValue3) {
               doubleValue = 0.0;
               break;
            }
         }

         while (doubleValue2 != 0.0 && this.player.level().noCollision(this.player, this.aABB.move(0.0, -0.5, doubleValue2))) {
            if (doubleValue2 < doubleValue3 && doubleValue2 >= -doubleValue3) {
               doubleValue2 = 0.0;
               break;
            }

            doubleValue2 += doubleValue2 > 0.0 ? -doubleValue3 : doubleValue3;
         }

         while (doubleValue != 0.0 && doubleValue2 != 0.0 && this.player.level().noCollision(this.player, this.aABB.move(doubleValue, -0.5, doubleValue2))) {
            if (doubleValue < doubleValue3 && doubleValue >= -doubleValue3) {
               doubleValue = 0.0;
            } else {
               doubleValue += doubleValue > 0.0 ? -doubleValue3 : doubleValue3;
            }

            if (doubleValue2 < doubleValue3 && doubleValue2 >= -doubleValue3) {
               doubleValue2 = 0.0;
               break;
            }

            doubleValue2 += doubleValue2 > 0.0 ? -doubleValue3 : doubleValue3;
         }

         if (vec33.x != doubleValue || vec33.z != doubleValue2) {
            this.flag10 = true;
         }

         if (this.isSneaking()) {
            vec33 = new Vec3(doubleValue, vec33.y, doubleValue2);
         }
      }

      return vec33;
   }

   private boolean isSneaking() {
      return this.c1.input.shift() || this.c1.flag;
   }

   private boolean isOnGround() {
      return this.flag4 || this.floatValue3 < 0.5 && !this.player.level().noCollision(this.player, this.aABB.move(0.0, this.floatValue3 - 0.5, 0.0));
   }

   private boolean isFlag() {
      return this.flag;
   }

   private float getJumpPower() {
      return 0.42F * this.getBlockJumpFactor() + this.getJumpBoostBonus();
   }

   private float getJumpBoostBonus() {
      if (this.hasEffect(MobEffects.JUMP_BOOST)) {
         MobEffectInstance mobEffectInstance = this.getEffect(MobEffects.JUMP_BOOST);
         return mobEffectInstance != null ? 0.1F * (mobEffectInstance.getAmplifier() + 1) : 0.0F;
      } else {
         return 0.0F;
      }
   }

   private float getBlockJumpFactor() {
      float floatValue4 = this.getBlockState(this.toBlockPos(this.vec3)).getBlock().getJumpFactor();
      float floatValue5 = this.getBlockState(this.getGroundPos()).getBlock().getJumpFactor();
      return floatValue4 == 1.0F ? floatValue5 : floatValue4;
   }

   private boolean canMove(double doubleValue, double doubleValue2, double doubleValue3) {
      return this.isFree(this.aABB.move(doubleValue, doubleValue2, doubleValue3));
   }

   private boolean isFree(AABB aABB2) {
      return this.player.level().noCollision(this.player, aABB2) && !this.player.level().containsAnyLiquid(aABB2);
   }

   private void swimUp(TagKey<Fluid> tagKey) {
      this.vec32 = this.vec32.add(0.0, 0.04F, 0.0);
   }

   private BlockPos getGroundPos() {
      return BlockPos.containing(this.vec3.x, this.aABB.minY - 0.5000001, this.vec3.z);
   }

   private double m_3_double() {
      return this.player.getEyeHeight() < 0.4 ? 0.0 : 0.4;
   }

   public boolean isInLava() {
      return this.object2DoubleMap.getDouble(FluidTags.LAVA) > 0.0;
   }

   private void updateWaterFlag() {
      if (this.m_9(FluidTags.WATER, 0.014)) {
         this.m_4_void();
         this.flag7 = true;
      } else {
         this.flag7 = false;
      }
   }

   private void updateSwimming() {
      if (this.flag8) {
         this.flag8 = this.isFlag() && this.flag7 && !this.player.isPassenger();
      } else {
         this.flag8 = this.isFlag()
            && this.flag9
            && !this.player.isPassenger()
            && this.player.level().getFluidState(this.toBlockPos(this.vec3)).typeHolder().is(FluidTags.WATER);
      }
   }

   private void updateFluids() {
      this.flag9 = this.tagKeySet.contains(FluidTags.WATER);
      this.tagKeySet.clear();
      double doubleValue = this.vec3.y + this.player.getEyeHeight() - 0.11111111F;
      BlockPos blockPos = BlockPos.containing(this.vec3.x, doubleValue, this.vec3.z);
      FluidState fluidState = this.player.level().getFluidState(blockPos);
      double doubleValue2 = blockPos.getY() + fluidState.getHeight(this.player.level(), blockPos);
      if (doubleValue2 > doubleValue && fluidState.typeHolder().is(FluidTags.WATER)) {
         this.tagKeySet.add(FluidTags.WATER);
      }
   }

   private boolean m_9(TagKey<Fluid> tagKey, double doubleValue) {
      if (this.m_8_boolean()) {
         return false;
      }

      AABB aABB2 = this.aABB.deflate(0.001);
      int value3 = Mth.floor(aABB2.minX);
      int value4 = Mth.ceil(aABB2.maxX);
      int value5 = Mth.floor(aABB2.minY);
      int value6 = Mth.ceil(aABB2.maxY);
      int value7 = Mth.floor(aABB2.minZ);
      int value8 = Mth.ceil(aABB2.maxZ);
      double doubleValue2 = 0.0;
      boolean flag11 = true;
      boolean flag12 = false;
      Vec3 vec33 = Vec3.ZERO;
      int value9 = 0;
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = value3; i < value4; i++) {
         for (int i2 = value5; i2 < value6; i2++) {
            for (int i3 = value7; i3 < value8; i3++) {
               mutableBlockPos.set(i, i2, i3);
               FluidState fluidState = this.player.level().getFluidState(mutableBlockPos);
               if (fluidState.typeHolder().is(tagKey)) {
                  double doubleValue3 = i2 + fluidState.getHeight(this.player.level(), mutableBlockPos);
                  if (doubleValue3 >= aABB2.minY) {
                     flag12 = true;
                     doubleValue2 = Math.max(doubleValue3 - aABB2.minY, doubleValue2);
                     if (flag11) {
                        Vec3 vec34 = fluidState.getFlow(this.player.level(), mutableBlockPos);
                        if (doubleValue2 < 0.4) {
                           vec34 = vec34.scale(doubleValue2);
                        }

                        vec33 = vec33.add(vec34);
                        value9++;
                     }
                  }
               }
            }
         }
      }

      if (vec33.length() > 0.0) {
         if (value9 > 0) {
            vec33 = vec33.scale(1.0 / value9);
         }

         vec33 = vec33.scale(doubleValue);
         if (Math.abs(this.vec32.x) < 0.003 && Math.abs(this.vec32.z) < 0.003 && vec33.length() < 0.0045) {
            vec33 = vec33.normalize().scale(0.0045);
         }

         this.vec32 = this.vec32.add(vec33);
      }

      this.object2DoubleMap.put(tagKey, doubleValue2);
      return flag12;
   }

   private boolean m_8_boolean() {
      AABB aABB2 = this.aABB.inflate(1.0);
      int value3 = Mth.floor(aABB2.minX);
      int value4 = Mth.ceil(aABB2.maxX);
      int value5 = Mth.floor(aABB2.minZ);
      int value6 = Mth.ceil(aABB2.maxZ);
      return !this.player.level().hasChunksAt(value3, value5, value4, value6);
   }

   private AABB getBoundingBoxAt(Vec3 vec33) {
      EntityDimensions entityDimensions = this.player.getDimensions(this.player.getPose());
      float floatValue4 = entityDimensions.width() / 2.0F;
      return new AABB(vec33.x - floatValue4, vec33.y, vec33.z - floatValue4, vec33.x + floatValue4, vec33.y + entityDimensions.height(), vec33.z + floatValue4);
   }

   private Vec3 getLookVector() {
      return this.calcViewVector(this.floatValue2, this.floatValue);
   }

   private Vec3 calcViewVector(float floatValue4, float floatValue5) {
      float floatValue6 = floatValue4 * (float) (Math.PI / 180.0);
      float floatValue7 = -floatValue5 * (float) (Math.PI / 180.0);
      float floatValue8 = Mth.cos(floatValue7);
      float floatValue9 = Mth.sin(floatValue7);
      float floatValue10 = Mth.cos(floatValue6);
      float floatValue11 = Mth.sin(floatValue6);
      return new Vec3(floatValue9 * floatValue10, -floatValue11, floatValue8 * floatValue10);
   }

   public boolean hasEffect(Holder<MobEffect> holder) {
      MobEffectInstance mobEffectInstance = this.player.getEffect(holder);
      return mobEffectInstance != null && mobEffectInstance.getDuration() >= this.value2;
   }

   private MobEffectInstance getEffect(Holder<MobEffect> holder) {
      MobEffectInstance mobEffectInstance = this.player.getEffect(holder);
      return mobEffectInstance != null && mobEffectInstance.getDuration() >= this.value2 ? mobEffectInstance : null;
   }

   public PlayerUtil clone() {
      return new PlayerUtil(
         this.player,
         this.c1,
         this.vec3,
         this.vec32,
         this.aABB,
         this.floatValue,
         this.floatValue2,
         this.flag,
         this.floatValue3,
         this.value,
         this.flag2,
         this.flag3,
         this.flag4,
         this.flag5,
         this.flag6,
         this.flag7,
         this.flag8,
         this.flag9,
         new Object2DoubleArrayMap(this.object2DoubleMap),
         new HashSet<>(this.tagKeySet)
      );
   }

   private BlockPos toBlockPos(Vec3 vec33) {
      return BlockPos.containing(vec33.x, vec33.y, vec33.z);
   }

   private BlockState getBlockState(BlockPos blockPos) {
      return this.player.level().getBlockState(blockPos);
   }

   static {
      m_9_void((String)null);
   }

   public static void m_9_void(String text2) {
      text = text2;
   }

   public static String m_6_String() {
      return text;
   }

   public static class StateHolder {
      public Input input;
      public float floatValue;
      public float floatValue2;
      public boolean flag;
      public float floatValue3;

      public StateHolder(Input input2) {
         this.floatValue3 = 1.0F;
         this.input = input2;
      }

      public void m_0_void() {
         if (this.input.forward() != this.input.backward()) {
            this.floatValue = this.input.forward() ? 1.0F : -1.0F;
         } else {
            this.floatValue = 0.0F;
         }

         if (this.input.left() == this.input.right()) {
            this.floatValue2 = 0.0F;
         } else {
            this.floatValue2 = this.input.left() ? 1.0F : -1.0F;
         }

         if (this.input.shift()) {
            this.floatValue2 *= 0.3F;
            this.floatValue *= 0.3F;
         }

         this.floatValue2 = this.floatValue2 * this.floatValue3;
         this.floatValue = this.floatValue * this.floatValue3;
      }

      public static PlayerUtil.StateHolder m_6(Input input2) {
         return new PlayerUtil.StateHolder(input2);
      }

      public static PlayerUtil.StateHolder m_7_C1(Player player2) {
         return m_3(player2, player2.position().subtract(player2.xo, player2.yo, player2.zo));
      }

      public static PlayerUtil.StateHolder m_3(Player player2, Vec3 vec33) {
         InventoryUtil.m_8_StringArr();
         double doubleValue = vec33.x * vec33.x + vec33.z * vec33.z;
         boolean flag11 = doubleValue >= 0.014641;
         boolean flag12 = !player2.onGround();
         boolean flag13 = player2.isCrouching();
         float floatValue4 = player2.isUsingItem() && !player2.isPassenger()
            ? player2.getUseItem().getOrDefault(DataComponents.USE_EFFECTS, UseEffects.DEFAULT).speedMultiplier()
            : 1.0F;
         double doubleValue2 = 0.05 * floatValue4;
         Input input2 = new Input(false, false, false, false, flag12, flag13, flag11);
         if (doubleValue > doubleValue2 * doubleValue2) {
            double doubleValue3 = Math.toDegrees(Math.atan2(-vec33.x, vec33.z));
            double doubleValue4 = Mth.wrapDegrees(doubleValue3 - player2.getYRot());
            boolean flag14 = doubleValue4 > -45.0 && doubleValue4 <= 45.0;
            boolean flag15 = doubleValue4 < -135.0 || doubleValue4 >= 135.0;
            boolean flag16 = doubleValue4 > -135.0 && doubleValue4 <= -45.0;
            boolean flag17 = doubleValue4 > 45.0 && doubleValue4 <= 135.0;
            input2 = new Input(flag14, flag15, flag16, flag17, flag12, flag13, flag11);
         }

         PlayerUtil.StateHolder c12 = new PlayerUtil.StateHolder(input2);
         c12.floatValue3 = floatValue4;
         return c12;
      }
   }
}
