package wtf.onetap.module.combat;

import java.util.OptionalDouble;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.phys.EntityHitResult;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoMace", description = "Берет булаву под smash-удар и возвращает слот", category = Category.COMBAT)
public class AutoMace extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Быстрый", "Быстрый", "Простой").build();
   private final FloatSetting leadTimeMs = new FloatSetting("Заранее (мс)", 300.0F, 50.0F, 700.0F, 50.0F).visibleWhen(this::isSimpleMode).build();
   private final FloatSetting height = new FloatSetting("Высота", 3.0, 1.5, 6.0, 0.5).build();
   private final BooleanSetting attack = new BooleanSetting("Атака", true).build();
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private double doubleValue;
   private int value = -1;
   private int value2 = -1;
   private int value3 = 7;
   private int value4 = -1;

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (MinecraftAccess.MINECRAFT.player.onGround() || MinecraftAccess.MINECRAFT.player.fallDistance < this.doubleValue) {
            this.doubleValue = 0.0;
         }

         if (this.flag3 && MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != this.value2) {
            this.flag3 = false;
         }

         if (this.isSimpleMode() && this.m_3_boolean()) {
            this.value3 = 0;
         } else if (this.value3 <= 6) {
            this.value3++;
         }

         if (this.flag3) {
            this.m_3_void();
         } else if (this.value3 == 0 && !this.flag2 && MinecraftAccess.MINECRAFT.gui.screen() == null && !MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.MACE)) {
            this.selectMace();
         }

         this.m_7_void();
      }
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (event.getPacket() instanceof ServerboundClientTickEndPacket) {
         this.flag2 = false;
      }

      if (MinecraftAccess.MINECRAFT.player != null && !this.flag && event.getPacket() instanceof ServerboundAttackPacket) {
         this.flag2 = true;
         if (!this.isSimpleMode() && this.isFallingEnough() && !MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.MACE) && this.selectMace()) {
            event.cancel();
            this.value3 = 0;
            this.flag = true;
            MinecraftAccess.MINECRAFT.player.connection.send(event.getPacket());
            this.flag = false;
         }

         if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.MACE) && MaceItem.canSmashAttack(MinecraftAccess.MINECRAFT.player) && this.getFallDistance() > 1.5) {
            this.flag4 = true;
            this.doubleValue = MinecraftAccess.MINECRAFT.player.fallDistance;
         }
      }
   }

   private boolean selectMace() {
      int value5 = this.findMaceSlot();
      if (value5 == -1) {
         return false;
      }

      this.value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      InventoryUtil.selectHotbarSlot(value5);
      this.value2 = value5;
      this.flag3 = true;
      this.flag4 = false;
      this.value4 = -1;
      return true;
   }

   private void m_3_void() {
      boolean flag5 = this.flag4
         || this.value3 > 6
         || MinecraftAccess.MINECRAFT.player.onGround()
         || MinecraftAccess.MINECRAFT.player.isFallFlying()
         || MinecraftAccess.MINECRAFT.player.isUsingItem()
         || MinecraftAccess.MINECRAFT.options.keyUse.isDown();
      if (!flag5) {
         this.value4 = -1;
      } else {
         if (this.value4 < 0) {
            this.value4 = this.isSimpleMode() ? 1 + ThreadLocalRandom.current().nextInt(3) : 0;
         }

         if (this.value4 > 0) {
            this.value4--;
         } else if (!this.flag2) {
            this.restoreSlot();
         }
      }
   }

   private void restoreSlot() {
      this.flag3 = false;
      if (MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() == this.value2) {
         InventoryUtil.selectHotbarSlot(this.value);
      }
   }

   private void m_7_void() {
      if ((Boolean)this.attack.getValue()
         && !this.flag2
         && MinecraftAccess.MINECRAFT.gameMode != null
         && this.getKillAura() == null
         && MinecraftAccess.MINECRAFT.gui.screen() == null
         && this.m_1_boolean()) {
         if (MinecraftAccess.MINECRAFT.hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof Player player && this.m_8_boolean(player)) {
            MinecraftAccess.MINECRAFT.gameMode.attack(MinecraftAccess.MINECRAFT.player, player);
            MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
         }
      }
   }

   private boolean m_3_boolean() {
      if (!MinecraftAccess.MINECRAFT.player.onGround() && !MinecraftAccess.MINECRAFT.player.isFallFlying() && !MinecraftAccess.MINECRAFT.player.isUsingItem()) {
         OptionalDouble optionalDouble = this.m_5_OptionalDouble();
         if (optionalDouble.isEmpty()) {
            return false;
         }

         double doubleValue2 = MinecraftAccess.MINECRAFT.player.getY();
         double doubleValue3 = MinecraftAccess.MINECRAFT.player.getDeltaMovement().y;
         double doubleValue4 = this.getFallDistance();

         for (int i = 0; i <= this.leadTimeMs.getInt() / 50; i++) {
            if (doubleValue2 <= optionalDouble.getAsDouble() + 1.0) {
               return doubleValue4 > ((Float)this.height.getValue()).floatValue();
            }

            doubleValue2 += doubleValue3;
            if (doubleValue3 < 0.0) {
               doubleValue4 -= doubleValue3;
            }

            doubleValue3 = (doubleValue3 - 0.08) * 0.98;
         }

         return false;
      } else {
         return false;
      }
   }

   private OptionalDouble m_5_OptionalDouble() {
      KillAura killAura = this.getKillAura();
      Stream stream = killAura != null ? Stream.ofNullable(killAura.getLivingEntity2()) : MinecraftAccess.MINECRAFT.level.players().stream().filter(this::m_8_boolean);
      return stream.filter(object -> {
         double doubleValue2 = ((net.minecraft.world.entity.LivingEntity)object).getX() - MinecraftAccess.MINECRAFT.player.getX();
         double doubleValue3 = ((net.minecraft.world.entity.LivingEntity)object).getZ() - MinecraftAccess.MINECRAFT.player.getZ();
         return doubleValue2 * doubleValue2 + doubleValue3 * doubleValue3 <= 16.0;
      }).mapToDouble(object2 -> ((net.minecraft.world.entity.LivingEntity)object2).getBoundingBox().maxY).filter(doubleValue4 -> doubleValue4 < MinecraftAccess.MINECRAFT.player.getY()).max();
   }

   private boolean m_8_boolean(Player player) {
      if (player != MinecraftAccess.MINECRAFT.player && player.isAlive() && !player.isSpectator()) {
         if (Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
            return false;
         }

         AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);
         return antiBot == null || !antiBot.isBot(player);
      } else {
         return false;
      }
   }

   private boolean isFallingEnough() {
      return !MinecraftAccess.MINECRAFT.player.isFallFlying() && this.getFallDistance() > ((Float)this.height.getValue()).floatValue();
   }

   private double getFallDistance() {
      return MinecraftAccess.MINECRAFT.player.fallDistance - this.doubleValue;
   }

   public boolean m_0_boolean() {
      return MinecraftAccess.MINECRAFT.player != null && (this.flag3 || this.isFallingEnough() && this.findMaceSlot() != -1);
   }

   public float getMaceRange() {
      return 4.5F + (float)Math.max(0.0, -MinecraftAccess.MINECRAFT.player.getDeltaMovement().y) * 4.0F;
   }

   public boolean m_1_boolean() {
      return MinecraftAccess.MINECRAFT.player != null && this.isFallingEnough() && (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.MACE) || !this.isSimpleMode() && this.findMaceSlot() != -1);
   }

   public boolean m_4_boolean() {
      return this.m_1_boolean() && MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(0.0F) > 0.0F && (this.flag3 || !MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.MACE));
   }

   private boolean isSimpleMode() {
      return ((String)this.mode.getValue()).equals("Простой");
   }

   private KillAura getKillAura() {
      KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
      return killAura != null && killAura.isEnabled() ? killAura : null;
   }

   private int findMaceSlot() {
      for (int i = 0; i < 9; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).is(Items.MACE)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void onDisable() {
      if (this.flag3 && MinecraftAccess.MINECRAFT.player != null) {
         this.restoreSlot();
      }

      this.flag3 = this.flag4 = this.flag2 = false;
      this.doubleValue = 0.0;
      this.value3 = 7;
   }
}
