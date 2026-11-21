package wtf.onetap.module.movement;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.PiercingWeapon;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraft.world.scores.Team;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.PostTravelEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.MovementStateAccess;
import wtf.onetap.util.PacketUtil;

@ModuleInformation(name = "Speed", description = "Ускорение под Grim через толкание энтити и края блоков", category = Category.MOVEMENT)
public class Speed extends Module {
   private final ModeSetting anticheat = new ModeSetting("Античит", "Grim", "Grim", "GroundedGrim", "Elytra 1.17.1/1.21.8", "Spear").onChange(this::restoreSlots).build();
   private final ModeSetting mode = new ModeSetting("Режим", "Пакетный", "Пакетный", "Легитный").visibleWhen(this::isSpearMode).build();
   private final MultiModeSetting sources = new MultiModeSetting("Источники", "Энтити", "Энтити", "Край").visibleWhen(() -> !this.isElytraMode() && !this.isSpearMode()).build();
   private final ModeSetting swap = new ModeSetting("Свап", "Быстрый", "Быстрый", "Задержка").visibleWhen(this::isElytraMode).build();
   private final FloatSetting radius = new FloatSetting("Радиус", 0.15F, 0.0F, 0.2F, 0.01F)
      .visibleWhen(() -> this.sources.isSelected("Энтити") && !this.isGroundedGrim() && !this.isElytraMode() && !this.isSpearMode())
      .build();
   private final FloatSetting ggRadius = new FloatSetting("Радиус GG", 0.45F, 0.0F, 0.5F, 0.01F).visibleWhen(() -> this.sources.isSelected("Энтити") && this.isGroundedGrim()).build();
   private final BooleanSetting autoSneak = new BooleanSetting("Авто-шифт", true).visibleWhen(() -> this.sources.isSelected("Край") && !this.isElytraMode() && !this.isSpearMode()).build();
   private final FloatSetting reserve = new FloatSetting("Запас", 0.002F, 0.0F, 0.02F, 5.0E-4F).visibleWhen(() -> !this.isElytraMode() && !this.isSpearMode()).build();
   private final int[] valueArray = new int[3];
   private int value;
   private AABB aABB;
   private boolean flag;
   private double doubleValue;
   private boolean flag2;
   private boolean flag3;
   private Speed.DataRecord c6;
   private double doubleValue2;
   private double doubleValue3;
   private boolean flag4;
   private int value2;
   private ItemStack itemStack;
   private int value3;
   private int value4 = 0;
   private int value5;
   private int value6;
   private int value7 = -1;
   private int value8 = -1;

   public static boolean m_7_boolean() {
      Speed speed = Onetap.MODULE_MANAGER.get(Speed.class);
      return speed != null && speed.isEnabled() && !speed.isElytraMode() && !speed.isSpearMode() && speed.sources.isSelected("Энтити");
   }

   public static Speed m_3_C0u() {
      Speed speed = Onetap.MODULE_MANAGER.get(Speed.class);
      return speed != null && speed.isEnabled() && !speed.isElytraMode() && !speed.isSpearMode() && speed.sources.isSelected("Край") ? speed : null;
   }

   private boolean isGroundedGrim() {
      return ((String)this.anticheat.getValue()).equals("GroundedGrim");
   }

   private boolean isElytraMode() {
      return ((String)this.anticheat.getValue()).equals("Elytra 1.17.1/1.21.8");
   }

   private boolean isSpearMode() {
      return ((String)this.anticheat.getValue()).equals("Spear");
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void m_2_void(boolean flag5) {
      this.flag2 |= flag5;
   }

   @Override
   public void onDisable() {
      Arrays.fill(this.valueArray, 0);
      this.aABB = null;
      this.doubleValue = 0.0;
      this.flag2 = false;
      this.flag3 = false;
      this.c6 = null;
      this.flag4 = false;
      this.value2 = 0;
      this.restoreChestplate();
      this.restoreSlots();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      this.flag3 = false;
      this.flag2 = false;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      Speed.DataRecord c62 = this.c6;
      NoClip.m_4_C3_sArr();
      this.c6 = null;
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null) {
         this.aABB = null;
      } else if (this.isElytraMode()) {
         this.aABB = null;
         this.value2 = MinecraftAccess.MINECRAFT.player.onGround() ? 0 : this.value2 + 1;
         if (this.value3 > 0 && --this.value3 == 0) {
            this.restoreChestplate();
         }
      } else if (this.isSpearMode()) {
         this.m_7_void();
      } else {
         this.aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox();
         this.flag = MinecraftAccess.MINECRAFT.player.onGround();
         int value9 = this.sources.isSelected("Энтити") ? this.m_5_int() : 0;
         if (!MinecraftAccess.MINECRAFT.player.isFallFlying()) {
            Vec3 vec3 = this.m_4(MinecraftAccess.MINECRAFT.player.input.keyPresses, MinecraftAccess.MINECRAFT.player.getYRot());
            if (vec3 != null) {
               Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
               double doubleValue4 = this.isGroundedGrim() ? (value9 > 0 ? 0.05 : 0.0) : value9 * 0.08;
               double doubleValue5 = vec3.x * doubleValue4;
               double doubleValue6 = vec3.z * doubleValue4;
               if (c62 != null && this.sources.isSelected("Край")) {
                  double doubleValue7 = m_4(c62.m_9_h(), c62.m_9g(), vec3.x, vec32.x, c62.m_2_c(), c62.m_21(), c62.m_9v_boolean());
                  double doubleValue8 = m_4(c62.m_9u(), c62.m_9w_boolean(), vec3.z, vec32.z, c62.m_2y(), c62.m_2_w_double(), c62.m_9v_boolean());
                  if (doubleValue7 != 0.0 || doubleValue8 != 0.0) {
                     this.flag3 = true;
                  }

                  doubleValue5 += doubleValue7;
                  doubleValue6 += doubleValue8;
               }

               doubleValue5 = moveTowardsZero(doubleValue5, this.reserve.getFloat());
               doubleValue6 = moveTowardsZero(doubleValue6, this.reserve.getFloat());
               if (doubleValue5 != 0.0 || doubleValue6 != 0.0) {
                  this.doubleValue2 = doubleValue5;
                  this.doubleValue3 = doubleValue6;
                  MinecraftAccess.MINECRAFT.player.setDeltaMovement(vec32.x + doubleValue5, vec32.y, vec32.z + doubleValue6);
               }
            }
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (this.isElytraMode() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.m_4(MinecraftAccess.MINECRAFT.player.input.keyPresses, MinecraftAccess.MINECRAFT.player.getYRot()) != null
            && !MinecraftAccess.MINECRAFT.player.isPassenger()
            && !MinecraftAccess.MINECRAFT.player.getAbilities().flying
            && !MinecraftAccess.MINECRAFT.player.isInWater()
            && !MinecraftAccess.MINECRAFT.player.isInLava()) {
            if (MinecraftAccess.MINECRAFT.player.onGround()) {
               if (!MinecraftAccess.MINECRAFT.player.isSprinting()
                  && MinecraftAccess.MINECRAFT.player.input.keyPresses.forward()
                  && MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() > 6
                  && !MinecraftAccess.MINECRAFT.player.isUsingItem()) {
                  MinecraftAccess.MINECRAFT.player.setSprinting(true);
               }

               event.setJumping(true);
            } else {
               if (!MinecraftAccess.MINECRAFT.player.isFallFlying() && !this.flag4 && this.value2 >= 2 && !MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.LEVITATION) && this.m_2_int() <= 1) {
                  int value9 = this.m_1_int();
                  if (value9 != -2) {
                     this.m_0(event, value9);
                  }
               }
            }
         }
      }
   }

   private int m_1_int() {
      if (LivingEntity.canGlideUsing(MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST)) {
         return -1;
      }

      int value9 = InventoryUtil.m_1_int();
      return value9 != -1
            && MinecraftAccess.MINECRAFT.player.containerMenu == MinecraftAccess.MINECRAFT.player.inventoryMenu
            && LivingEntity.canGlideUsing(MinecraftAccess.MINECRAFT.player.getInventory().getItem(value9), EquipmentSlot.CHEST)
         ? value9
         : -2;
   }

   private void m_0(MovementInputEvent event, int value9) {
      boolean flag5 = value9 == -1;
      ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.player.connection;
      MovementStateAccess movementStateAccess = (MovementStateAccess)MinecraftAccess.MINECRAFT.player;
      clientPacketListener.send(new ServerboundPlayerInputPacket(Input.EMPTY));
      if (movementStateAccess.onetap$wasSprinting()) {
         MinecraftAccess.MINECRAFT.player.setSprinting(false);
         movementStateAccess.onetap$setWasSprinting(false);
         clientPacketListener.send(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.STOP_SPRINTING));
      }

      if (!flag5) {
         this.m_4(value9, true);
      }

      clientPacketListener.send(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.START_FALL_FLYING));
      MinecraftAccess.MINECRAFT.player.startFallFlying();
      if (!flag5) {
         this.m_4(value9, false);
      }

      PacketUtil.sendPacket(new ServerboundContainerClosePacket(0));
      if (!flag5 && ((String)this.swap.getValue()).equals("Задержка")) {
         this.m_7_void(value9);
      }

      Input input = MinecraftAccess.MINECRAFT.player.input.keyPresses;
      clientPacketListener.send(new ServerboundPlayerInputPacket(new Input(input.forward(), input.backward(), input.left(), input.right(), true, input.shift(), input.sprint())));
      event.setJumping(true);
   }

   private void m_7_void(int value9) {
      this.restoreChestplate();
      this.itemStack = MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).copy();
      MinecraftAccess.MINECRAFT.player.setItemSlot(EquipmentSlot.CHEST, MinecraftAccess.MINECRAFT.player.getInventory().getItem(value9).copy());
      this.value3 = 1;
   }

   private void restoreChestplate() {
      if (this.itemStack != null && MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.setItemSlot(EquipmentSlot.CHEST, this.itemStack);
      }

      this.itemStack = null;
      this.value3 = 0;
   }

   private int m_2_int() {
      AABB aABB2 = MinecraftAccess.MINECRAFT.player.getBoundingBox();
      double doubleValue4 = MinecraftAccess.MINECRAFT.player.getDeltaMovement().y;
      double doubleValue5 = 0.0;

      for (int i = 0; i < 4; i++) {
         doubleValue5 += doubleValue4;
         if (doubleValue4 < 0.0 && !this.hasNoCollision(aABB2.move(0.0, doubleValue5, 0.0))) {
            return i;
         }

         doubleValue4 = (doubleValue4 - MinecraftAccess.MINECRAFT.player.getGravity()) * 0.98;
      }

      return Integer.MAX_VALUE;
   }

   private void m_4(int value9, boolean flag5) {
      int value10 = value9 < 9 ? value9 : MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      if (value9 >= 9 && flag5) {
         InventoryUtil.clickSlot(0, value9, value10, ContainerInput.SWAP);
      }

      InventoryUtil.clickSlot(0, 6, value10, ContainerInput.SWAP);
      if (value9 >= 9 && !flag5) {
         InventoryUtil.clickSlot(0, value9, value10, ContainerInput.SWAP);
      }
   }

   @EventTarget
   private void onPostTravel(PostTravelEvent event) {
      if (this.isElytraMode()) {
         if (MinecraftAccess.MINECRAFT.player != null) {
            this.flag4 = MinecraftAccess.MINECRAFT.player.isFallFlying();
         }
      } else if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && this.aABB != null) {
         AABB aABB2 = MinecraftAccess.MINECRAFT.player.getBoundingBox();
         double doubleValue4 = aABB2.minY - this.aABB.minY;
         boolean flag5 = this.flag || this.doubleValue < MinecraftAccess.MINECRAFT.player.maxUpStep() && !this.hasNoCollision(this.aABB.move(0.0, this.doubleValue - MinecraftAccess.MINECRAFT.player.maxUpStep(), 0.0));
         if (MinecraftAccess.MINECRAFT.player.onGround()) {
            this.doubleValue = 0.0;
         } else if (doubleValue4 < 0.0) {
            this.doubleValue -= doubleValue4;
         }

         if (this.sources.isSelected("Край") && flag5 && !MinecraftAccess.MINECRAFT.player.getAbilities().flying && !MinecraftAccess.MINECRAFT.player.isPassenger() && !MinecraftAccess.MINECRAFT.player.isFallFlying()
            )
          {
            Input input = MinecraftAccess.MINECRAFT.player.input.keyPresses;
            boolean flag6 = input.shift();
            if (flag6 || (Boolean)this.autoSneak.getValue()) {
               Speed.DataRecord c62 = this.m_2_C6(aABB2);
               if (c62 != null) {
                  if (!flag6) {
                     Vec3 vec3 = this.m_4(input, MinecraftAccess.MINECRAFT.player.getYRot());
                     if (vec3 == null || !m_1(c62, vec3)) {
                        return;
                     }

                     MinecraftAccess.MINECRAFT.player.input.keyPresses = new Input(input.forward(), input.backward(), input.left(), input.right(), input.jump(), true, input.sprint());
                  }

                  this.c6 = c62;
               }
            }
         }
      }
   }

   private Vec3 m_4(Input input, float floatValue) {
      double doubleValue4 = (input.forward() ? 1 : 0) - (input.backward() ? 1 : 0);
      double doubleValue5 = (input.left() ? 1 : 0) - (input.right() ? 1 : 0);
      if (doubleValue4 == 0.0 && doubleValue5 == 0.0) {
         return null;
      }

      float floatValue2 = floatValue * (float) (Math.PI / 180.0);
      double doubleValue6 = Mth.sin(floatValue2);
      double doubleValue7 = Mth.cos(floatValue2);
      double doubleValue8 = doubleValue5 * doubleValue7 - doubleValue4 * doubleValue6;
      double doubleValue9 = doubleValue4 * doubleValue7 + doubleValue5 * doubleValue6;
      double doubleValue10 = Math.max(Math.abs(doubleValue8), Math.abs(doubleValue9));
      return new Vec3(doubleValue8 / doubleValue10, 0.0, doubleValue9 / doubleValue10);
   }

   private static boolean m_1(Speed.DataRecord c62, Vec3 vec3) {
      return vec3.x > 0.1 && c62.m_9_h() || vec3.x < -0.1 && c62.m_9g() || vec3.z > 0.1 && c62.m_9u() || vec3.z < -0.1 && c62.m_9w_boolean();
   }

   private int m_5_int() {
      this.valueArray[this.value] = !MinecraftAccess.MINECRAFT.player.isPassenger() && !MinecraftAccess.MINECRAFT.player.isSpectator() ? this.m_3_int() : 0;
      this.value = (this.value + 1) % 3;
      int value9 = 0;

      for (int value10 : this.valueArray) {
         value9 = Math.max(value9, value10);
      }

      return value9;
   }

   private Speed.DataRecord m_2_C6(AABB aABB2) {
      double doubleValue4 = Mth.clamp(aABB2.minX - this.aABB.minX, -16.0, 16.0);
      double doubleValue5 = Mth.clamp(aABB2.minZ - this.aABB.minZ, -16.0, 16.0);
      double doubleValue6 = MinecraftAccess.MINECRAFT.player.maxUpStep();
      double doubleValue7 = Math.max(0.05, doubleValue4 + 0.05);
      double doubleValue8 = Math.max(0.05, doubleValue5 + 0.05);
      double doubleValue9 = Math.min(-0.05, doubleValue4 - 0.05);
      double doubleValue10 = Math.min(-0.05, doubleValue5 - 0.05);
      double[] doubleArray = this.m_9(this.aABB, doubleValue7, doubleValue10, doubleValue6);
      double[] doubleArray2 = this.m_9(this.aABB, doubleValue9, doubleValue10, doubleValue6);
      double[] doubleArray3 = this.m_9(this.aABB, doubleValue7, doubleValue8, doubleValue6);
      double[] doubleArray4 = this.m_9(this.aABB, doubleValue9, doubleValue8, doubleValue6);
      boolean flag5 = doubleArray[0] != doubleValue7 || doubleArray3[0] != doubleValue7;
      boolean flag6 = doubleArray2[0] != doubleValue9 || doubleArray4[0] != doubleValue9;
      boolean flag7 = doubleArray[1] != doubleValue10 || doubleArray2[1] != doubleValue10;
      boolean flag8 = doubleArray3[1] != doubleValue8 || doubleArray4[1] != doubleValue8;
      return !flag5 && !flag6 && !flag7 && !flag8 ? null : new Speed.DataRecord(flag5, flag6, flag7, flag8, doubleValue4, doubleValue5, doubleValue4 - this.doubleValue2, doubleValue5 - this.doubleValue3, this.flag2);
   }

   private double[] m_9(AABB aABB2, double doubleValue4, double doubleValue5, double doubleValue6) {
      while (doubleValue4 != 0.0 && this.hasNoCollision(aABB2.move(doubleValue4, -doubleValue6, 0.0))) {
         doubleValue4 = stepToZero(doubleValue4);
      }

      while (doubleValue5 != 0.0 && this.hasNoCollision(aABB2.move(0.0, -doubleValue6, doubleValue5))) {
         doubleValue5 = stepToZero(doubleValue5);
      }

      while (doubleValue4 != 0.0 && doubleValue5 != 0.0 && this.hasNoCollision(aABB2.move(doubleValue4, -doubleValue6, doubleValue5))) {
         doubleValue4 = stepToZero(doubleValue4);
         doubleValue5 = stepToZero(doubleValue5);
      }

      return new double[]{doubleValue4, doubleValue5};
   }

   private static double stepToZero(double doubleValue4) {
      if (doubleValue4 < 0.05 && doubleValue4 >= -0.05) {
         return 0.0;
      } else {
         return doubleValue4 > 0.0 ? doubleValue4 - 0.05 : doubleValue4 + 0.05;
      }
   }

   private boolean hasNoCollision(AABB aABB2) {
      return MinecraftAccess.MINECRAFT.level.noBlockCollision(MinecraftAccess.MINECRAFT.player, aABB2);
   }

   private static double m_4(boolean flag5, boolean flag6, double doubleValue4, double doubleValue5, double doubleValue6, double doubleValue7, boolean flag7) {
      int value9 = doubleValue4 > 0.1 ? 1 : (doubleValue4 < -0.1 ? -1 : 0);
      if (value9 != 0 && (value9 > 0 ? flag5 : flag6)) {
         double doubleValue8 = value9 * doubleValue6;
         double doubleValue9;
         if (flag7) {
            double doubleValue10 = doubleValue8 * (doubleValue8 >= 0.0 ? 0.546 : 0.91);
            doubleValue9 = 0.15 + doubleValue10 - value9 * doubleValue5;
         } else {
            doubleValue9 = Math.max(0.15, value9 * doubleValue7 * 0.546);
         }

         return doubleValue9 > 0.0 ? value9 * doubleValue9 : 0.0;
      } else {
         return 0.0;
      }
   }

   private static double moveTowardsZero(double doubleValue4, double doubleValue5) {
      return Math.abs(doubleValue4) <= doubleValue5 ? 0.0 : doubleValue4 - Math.signum(doubleValue4) * doubleValue5;
   }

   private void m_7_void() {
      this.aABB = null;
      if (this.value6 > 0) {
         this.value6--;
      }

      if (this.value4 == 1) {
         if (--this.value5 <= 0) {
            this.restoreSlots();
         }
      } else if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.gameMode == null) {
         this.restoreSlots();
      } else if (this.value6 <= 0 && MinecraftAccess.MINECRAFT.gui.screen() == null && !MinecraftAccess.MINECRAFT.player.isUsingItem()) {
         if (MinecraftAccess.MINECRAFT.player.input.keyPresses.forward()) {
            this.m_4_void();
         }
      }
   }

   private void m_4_void() {
      int value9 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      int value10 = this.m_7_int();
      if (value10 != -1) {
         if (value10 != value9) {
            if (value10 < 9) {
               InventoryUtil.selectHotbarSlot(value10);
            } else {
               InventoryUtil.clickSlot(0, value10, value9, ContainerInput.SWAP);
            }
         }

         if (!this.isHoldingSpear()) {
            this.swapSlot(value10, value9);
         } else {
            this.m_2_void();
            this.value6 = 10;
            if (((String)this.mode.getValue()).equals("Легитный")) {
               this.value7 = value10;
               this.value8 = value9;
               this.value4 = 1;
               this.value5 = 1;
            } else {
               this.swapSlot(value10, value9);
            }
         }
      }
   }

   private void m_2_void() {
      ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getMainHandItem();
      PiercingWeapon piercingWeapon = itemStack2.get(DataComponents.PIERCING_WEAPON);
      if (piercingWeapon != null) {
         MinecraftAccess.MINECRAFT.gameMode.piercingAttack(piercingWeapon);
         MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
      }
   }

   private void restoreSlots() {
      if (this.value7 != -1 && MinecraftAccess.MINECRAFT.player != null) {
         this.swapSlot(this.value7, this.value8);
      }

      this.value7 = -1;
      this.value8 = -1;
      this.value4 = 0;
      this.value5 = 0;
   }

   private void swapSlot(int value9, int value10) {
      if (value9 != value10) {
         if (value9 < 9) {
            InventoryUtil.selectHotbarSlot(value10);
         } else {
            InventoryUtil.clickSlot(0, value9, value10, ContainerInput.SWAP);
         }
      }
   }

   private boolean isHoldingSpear() {
      return MinecraftAccess.MINECRAFT.player != null && this.isSpear(MinecraftAccess.MINECRAFT.player.getMainHandItem());
   }

   private int m_7_int() {
      int value9 = -1;
      int value10 = -1;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack2.isEmpty() && this.isSpear(itemStack2)) {
            int value11 = this.m_1_int(itemStack2);
            if (value11 > value10) {
               value10 = value11;
               value9 = i;
            }
         }
      }

      return value9;
   }

   private boolean isSpear(ItemStack itemStack2) {
      return !itemStack2.isEmpty() && this.getItemId(itemStack2).contains("spear");
   }

   private String getItemId(ItemStack itemStack2) {
      return BuiltInRegistries.ITEM.getKey(itemStack2.getItem()).getPath();
   }

   private int getLungeLevel(ItemStack itemStack2) {
      for (Entry entry : itemStack2.getEnchantments().entrySet()) {
         if (((Holder)entry.getKey()).is(Enchantments.LUNGE)) {
            return entry.getIntValue();
         }
      }

      return 0;
   }

   private int m_1_int(ItemStack itemStack2) {
      int value9 = this.getMaterialTier(itemStack2);
      int value10 = this.getLungeLevel(itemStack2);
      return value10 > 0 ? 100 + value10 * 10 + value9 : value9;
   }

   private int getMaterialTier(ItemStack itemStack2) {
      String text = this.getItemId(itemStack2);
      if (text.contains("netherite")) {
         return 7;
      } else if (text.contains("diamond")) {
         return 6;
      } else if (text.contains("iron")) {
         return 5;
      } else if (text.contains("gold")) {
         return 4;
      } else if (text.contains("copper")) {
         return 3;
      } else {
         return text.contains("stone") ? 2 : 1;
      }
   }

   private int m_3_int() {
      boolean flag5 = this.isGroundedGrim();
      AABB aABB2 = MinecraftAccess.MINECRAFT.player.getBoundingBox().inflate(flag5 ? this.ggRadius.getFloat() : this.radius.getFloat());
      PlayerTeam playerTeam = MinecraftAccess.MINECRAFT.player.getTeam();
      int value9 = 0;

      for (Entity entity : MinecraftAccess.MINECRAFT.level.getEntities(MinecraftAccess.MINECRAFT.player, aABB2)) {
         if (m_9(entity, flag5) && m_8(entity.getTeam(), playerTeam)) {
            value9++;
         }
      }

      return value9;
   }

   private static boolean m_9(Entity entity, boolean flag5) {
      if (!entity.isSpectator() && !(entity instanceof ArmorStand) && !(entity instanceof Bat) && !(entity instanceof Parrot)) {
         return flag5 && entity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying()
            ? false
            : entity instanceof LivingEntity || entity instanceof AbstractBoat || entity instanceof AbstractMinecart;
      } else {
         return false;
      }
   }

   private static boolean m_8(Team team, Team team2) {
      CollisionRule collisionRule = team == null ? CollisionRule.ALWAYS : team.getCollisionRule();
      CollisionRule collisionRule2 = team2 == null ? CollisionRule.ALWAYS : team2.getCollisionRule();
      if (collisionRule != CollisionRule.NEVER && collisionRule2 != CollisionRule.NEVER) {
         boolean flag5 = team != null && team == team2;
         return (!flag5 || collisionRule != CollisionRule.PUSH_OWN_TEAM && collisionRule2 != CollisionRule.PUSH_OWN_TEAM)
            && (collisionRule != CollisionRule.PUSH_OTHER_TEAMS && collisionRule2 != CollisionRule.PUSH_OTHER_TEAMS || flag5);
      } else {
         return false;
      }
   }

   private record DataRecord(boolean f_9_h, boolean f_9g, boolean f_9w, boolean f_9u, double f_2_c, double f_2y, double f_21, double f_2_w, boolean f_9v) {
      public boolean m_9_h() {
         return this.f_9_h;
      }

      public boolean m_9g() {
         return this.f_9g;
      }

      public boolean m_9w_boolean() {
         return this.f_9w;
      }

      public boolean m_9u() {
         return this.f_9u;
      }

      public double m_2_c() {
         return this.f_2_c;
      }

      public double m_2y() {
         return this.f_2y;
      }

      public double m_21() {
         return this.f_21;
      }

      public double m_2_w_double() {
         return this.f_2_w;
      }

      public boolean m_9v_boolean() {
         return this.f_9v;
      }
   }
}
