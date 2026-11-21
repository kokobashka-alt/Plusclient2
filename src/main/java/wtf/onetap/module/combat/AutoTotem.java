package wtf.onetap.module.combat;

import java.util.ArrayDeque;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.RemoveEntitiesEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.AimPoints;
import wtf.onetap.util.C6a;
import wtf.onetap.util.CollisionUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoTotem", description = "Автоматически ставит тотем в оффхенд", category = Category.COMBAT)
public class AutoTotem extends Module {
   private final FloatSetting health = new FloatSetting("Хп", 4.0F, 1.0F, 20.0F, 0.1F).build();
   private final MultiModeSetting consider = new MultiModeSetting("Учитывать", "Золотые сердца", "Золотые сердца", "Падение", "Хп на элитре", "Кристаллы", "Динамит", "Булава").build();
   private final ModeSetting mace = new ModeSetting("Булава", "Только с булавой", "Только с булавой", "Все падающие игроки")
      .visibleWhen(() -> this.consider.isSelected("Булава"))
      .build();
   private final FloatSetting elytraHealth = new FloatSetting("Хп на элитре", 11.0F, 1.0F, 20.0F, 0.1F).visibleWhen(() -> this.consider.isSelected("Хп на элитре")).build();
   private final FloatSetting tntDistance = new FloatSetting("Дист. до тнт", 10.0F, 1.0F, 50.0F, 1.0F).visibleWhen(() -> this.consider.isSelected("Динамит")).build();
   private final FloatSetting fallDistance = new FloatSetting("Дист. падения", 10.0F, 1.0F, 50.0F, 0.5F).visibleWhen(() -> this.consider.isSelected("Падение")).build();
   private final BooleanSetting keepEnchantment = new BooleanSetting("Сохранять зачарование", true).build();
   private final BooleanSetting excludeDraconidTotem = new BooleanSetting("Исключать тотем Драконида", false).build();
   private final BooleanSetting considerOrb = new BooleanSetting("Учитывать шар", true).visibleWhen(() -> this.consider.isSelected("Кристаллы")).build();
   private final BooleanSetting customHealthWithOrb = new BooleanSetting("Кастомное хп с шаром", true).visibleWhen(() -> this.consider.isSelected("Кристаллы") && (Boolean)this.considerOrb.getValue()).build();
   private final FloatSetting healthWithOrb = new FloatSetting("Хп с шаром", 5.0F, 1.0F, 20.0F, 1.0F)
      .visibleWhen(() -> this.consider.isSelected("Кристаллы") && (Boolean)this.considerOrb.getValue() && (Boolean)this.customHealthWithOrb.getValue())
      .build();
   private final ArrayDeque<Long> arrayDeque = new ArrayDeque<>();
   private long longValue = -1L;
   private long longValue2;
   private long longValue3;
   private ItemStack itemStack;
   private int value = -1;
   private int value2;
   private int value3;

   @Override
   public void onDisable() {
      this.reset();
   }

   private void reset() {
      this.longValue = -1L;
      this.longValue3 = 0L;
      this.itemStack = null;
      this.value = -1;
      this.value2 = 0;
      this.value3 = 0;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.value3 > 0) {
            this.value3--;
         }

         long longValue4 = System.currentTimeMillis();
         if (this.longValue >= 0L && longValue4 - this.longValue > 5000L) {
            this.longValue = -1L;
            this.arrayDeque.clear();
         }

         boolean flag = this.m_0_boolean();
         this.value2 = flag ? 0 : Math.min(this.value2 + 1, 10);
         if (!InventoryUtil.m_6_boolean() && longValue4 >= this.longValue3) {
            if (MinecraftAccess.MINECRAFT.player.containerMenu == MinecraftAccess.MINECRAFT.player.inventoryMenu) {
               if (flag || this.hasTotemTarget() && this.value2 < 10) {
                  if (this.m_9_boolean() && this.m_2_int() != -1) {
                     this.m_4(longValue4, true);
                  }
               } else if (this.hasTotemTarget()) {
                  if (this.m_8_boolean(MinecraftAccess.MINECRAFT.player.getOffhandItem())) {
                     this.clearTarget();
                  } else if (this.m_7_int() == -1) {
                     this.clearTarget();
                  } else {
                     this.m_4(longValue4, false);
                  }
               }
            }
         }
      }
   }

   private void m_4(long longValue4, boolean flag) {
      boolean flag2 = InventoryUtil.isModernProtocol();
      if (this.longValue < 0L || longValue4 - this.longValue >= this.longValue2 - (flag2 ? 100L : 50L)) {
         InventoryUtil.m_0(() -> this.m_1(flag));
      }
   }

   private void m_1(boolean flag) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.containerMenu == MinecraftAccess.MINECRAFT.player.inventoryMenu) {
         int value4 = flag ? (this.m_9_boolean() ? this.m_2_int() : -1) : this.m_7_int();
         if (value4 != -1) {
            ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getOffhandItem().copy();
            if (MinecraftAccess.MINECRAFT.player.containerMenu.getCarried().isEmpty()) {
               InventoryUtil.clickSlot(0, -999, 0, ContainerInput.PICKUP);
            }

            InventoryUtil.clickSlot(0, value4 < 9 ? value4 + 36 : value4, 40, ContainerInput.SWAP);
            if (!flag) {
               this.clearTarget();
            } else if (!this.hasTotemTarget() && !itemStack2.isEmpty() && !this.isValidTotem(itemStack2)) {
               this.itemStack = itemStack2;
               this.value = value4;
            }

            long longValue4 = System.currentTimeMillis();
            if (this.longValue >= 0L) {
               this.arrayDeque.addLast(longValue4 - this.longValue);

               while (this.arrayDeque.size() > 9) {
                  this.arrayDeque.removeFirst();
               }

               this.longValue = -1L;
            }

            this.longValue3 = longValue4 + this.getPingMs();
         }
      }
   }

   private long randomDelay() {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      if (this.arrayDeque.size() < 2) {
         return 140L + threadLocalRandom.nextLong(51L);
      }

      for (long longValue4 = 140L; longValue4 <= 450L; longValue4 += 10L) {
         if (this.m_2_double(longValue4) >= 70.0) {
            return longValue4 + threadLocalRandom.nextLong(16L);
         }
      }

      return 450L;
   }

   private double m_2_double(long longValue4) {
      double doubleValue = longValue4;

      for (long longValue5 : this.arrayDeque) {
         doubleValue += longValue5;
      }

      double doubleValue2 = doubleValue / (this.arrayDeque.size() + 1);
      double doubleValue3 = (longValue4 - doubleValue2) * (longValue4 - doubleValue2);

      for (long longValue6 : this.arrayDeque) {
         doubleValue3 += (longValue6 - doubleValue2) * (longValue6 - doubleValue2);
      }

      return Math.sqrt(doubleValue3 / (this.arrayDeque.size() + 1));
   }

   private long getPingMs() {
      ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
      PlayerInfo playerInfo = clientPacketListener != null ? clientPacketListener.getPlayerInfo(MinecraftAccess.MINECRAFT.player.getUUID()) : null;
      int value4 = playerInfo != null ? playerInfo.getLatency() : 0;
      return Math.max(150L, (long)(value4 * 1.25F) + 100L);
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (event.getPacket() instanceof ClientboundRespawnPacket) {
            this.reset();
            this.arrayDeque.clear();
         } else if (event.getPacket() instanceof ClientboundEntityEventPacket clientboundEntityEventPacket) {
            if (clientboundEntityEventPacket.getEventId() == 35) {
               if (clientboundEntityEventPacket.getEntity(MinecraftAccess.MINECRAFT.level) == MinecraftAccess.MINECRAFT.player) {
                  this.longValue = System.currentTimeMillis();
                  this.longValue2 = this.randomDelay();
                  this.value2 = 0;
                  if (this.consider.isSelected("Кристаллы")) {
                     this.value3 = 5;
                  }

                  for (InteractionHand interactionHand : InteractionHand.values()) {
                     ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getItemInHand(interactionHand);
                     if (itemStack2.has(DataComponents.DEATH_PROTECTION)) {
                        itemStack2.shrink(1);
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onRemoveEntities(RemoveEntitiesEvent event) {
      if (this.consider.isSelected("Кристаллы") && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && !this.m_3_boolean()) {
         if (MinecraftAccess.MINECRAFT.level.getEntity(event.getValue()) instanceof EndCrystal endCrystal) {
            if (this.m_2_boolean(endCrystal.position())) {
               this.value3 = 9;
            }
         }
      }
   }

   private boolean m_9_boolean() {
      ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getOffhandItem();
      if (!this.isValidTotem(itemStack2)) {
         return true;
      } else {
         return !this.keepEnchantment.getValue() ? false : itemStack2.isEnchanted() && InventoryUtil.findSlot(itemStack3 -> this.isValidTotem(itemStack3) && !itemStack3.isEnchanted()) != -1;
      }
   }

   private int m_2_int() {
      if ((Boolean)this.keepEnchantment.getValue()) {
         int value4 = InventoryUtil.findSlot(itemStack2 -> this.isValidTotem(itemStack2) && !itemStack2.isEnchanted());
         if (value4 != -1) {
            return value4;
         }

         if (this.isValidTotem(MinecraftAccess.MINECRAFT.player.getOffhandItem())) {
            return -1;
         }
      }

      return InventoryUtil.findSlot(this::isValidTotem);
   }

   private boolean isValidTotem(ItemStack itemStack2) {
      return !itemStack2.is(Items.TOTEM_OF_UNDYING)
         ? false
         : !(Boolean)this.excludeDraconidTotem.getValue() || !itemStack2.getHoverName().getString().toLowerCase().contains("драконид");
   }

   private boolean hasTotemTarget() {
      return this.itemStack != null && !this.itemStack.isEmpty();
   }

   private void clearTarget() {
      this.itemStack = null;
      this.value = -1;
   }

   private boolean m_8_boolean(ItemStack itemStack2) {
      return this.hasTotemTarget() && !itemStack2.isEmpty() ? itemStack2.is(this.itemStack.getItem()) && itemStack2.isEnchanted() == this.itemStack.isEnchanted() : false;
   }

   private int m_7_int() {
      if (!this.hasTotemTarget()) {
         return -1;
      }

      if (this.value >= 0 && this.value < 36 && this.m_8_boolean(MinecraftAccess.MINECRAFT.player.getInventory().getItem(this.value))) {
         return this.value;
      }

      int value4 = MinecraftAccess.MINECRAFT.player.getInventory().findSlotMatchingItem(this.itemStack);
      return value4 >= 0 && value4 < 36 ? value4 : InventoryUtil.findSlot(this::m_8_boolean);
   }

   private float getEffectiveHealth() {
      return this.consider.isSelected("Золотые сердца") ? MinecraftAccess.MINECRAFT.player.getHealth() + MinecraftAccess.MINECRAFT.player.getAbsorptionAmount() : MinecraftAccess.MINECRAFT.player.getHealth();
   }

   private boolean m_0_boolean() {
      float floatValue = this.getEffectiveHealth();
      if (floatValue <= (Float)this.health.getValue()) {
         return true;
      } else if (this.consider.isSelected("Хп на элитре")
         && floatValue <= (Float)this.elytraHealth.getValue()
         && MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         return true;
      } else if (this.consider.isSelected("Падение") && MinecraftAccess.MINECRAFT.player.fallDistance > ((Float)this.fallDistance.getValue()).floatValue()) {
         return true;
      } else if (this.consider.isSelected("Динамит") && this.m_4_double() <= ((Float)this.tntDistance.getValue()).floatValue()) {
         return true;
      } else {
         return this.consider.isSelected("Булава") && this.m_1_boolean() ? true : this.consider.isSelected("Кристаллы") && this.m_5_boolean();
      }
   }

   private boolean m_5_boolean() {
      if (this.value3 > 0) {
         return true;
      }

      if (this.m_3_boolean()) {
         return false;
      }

      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().inflate(12.0);

      for (EndCrystal endCrystal : MinecraftAccess.MINECRAFT.level.getEntitiesOfClass(EndCrystal.class, aABB)) {
         if (this.m_2_boolean(endCrystal.position())) {
            return true;
         }
      }

      return this.m_7_boolean();
   }

   private boolean m_3_boolean() {
      return (Boolean)this.considerOrb.getValue()
         && MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.PLAYER_HEAD)
         && (!(Boolean)this.customHealthWithOrb.getValue() || this.getEffectiveHealth() > (Float)this.healthWithOrb.getValue());
   }

   private boolean m_2_boolean(Vec3 vec3) {
      float floatValue = C6a.m_0_C6a(MinecraftAccess.MINECRAFT.player).m_0_float(AimPoints.m_3(CollisionUtil.m_7(MinecraftAccess.MINECRAFT.level), vec3, MinecraftAccess.MINECRAFT.player.getBoundingBox()));
      return this.getEffectiveHealth() - floatValue <= (Float)this.health.getValue();
   }

   private boolean m_7_boolean() {
      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (player != MinecraftAccess.MINECRAFT.player
            && player.isAlive()
            && !player.isSpectator()
            && !Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())
            && (player.getMainHandItem().is(Items.END_CRYSTAL) || player.getOffhandItem().is(Items.END_CRYSTAL))) {
            BlockHitResult blockHitResult = AimUtil.m_3(player, 5.0);
            if (blockHitResult != null) {
               BlockPos blockPos = blockHitResult.getBlockPos();
               BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos);
               if (blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.BEDROCK)) {
                  BlockPos blockPos2 = blockPos.above();
                  if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2).isAir() && this.m_2_boolean(Vec3.atBottomCenterOf(blockPos2))) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean m_1_boolean() {
      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (player != MinecraftAccess.MINECRAFT.player
            && player.isAlive()
            && !player.isSpectator()
            && !player.onGround()
            && !Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())
            && (((String)this.mace.getValue()).equals("Только с булавой") ? player.getMainHandItem().is(Items.MACE) : !(player.getY() >= player.yo))) {
            double doubleValue = player.getX() - MinecraftAccess.MINECRAFT.player.getX();
            double doubleValue2 = player.getZ() - MinecraftAccess.MINECRAFT.player.getZ();
            if (doubleValue * doubleValue + doubleValue2 * doubleValue2 <= 9.0 && player.getY() - MinecraftAccess.MINECRAFT.player.getY() > 1.5) {
               return true;
            }
         }
      }

      return false;
   }

   private double m_4_double() {
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.position();
      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().inflate(((Float)this.tntDistance.getValue()).floatValue());
      double doubleValue = Double.MAX_VALUE;

      for (PrimedTnt primedTnt : MinecraftAccess.MINECRAFT.level.getEntitiesOfClass(PrimedTnt.class, aABB)) {
         doubleValue = Math.min(doubleValue, vec3.distanceTo(primedTnt.position()));
      }

      for (MinecartTNT minecartTNT : MinecraftAccess.MINECRAFT.level.getEntitiesOfClass(MinecartTNT.class, aABB)) {
         doubleValue = Math.min(doubleValue, vec3.distanceTo(minecartTNT.position()));
      }

      return doubleValue;
   }
}
