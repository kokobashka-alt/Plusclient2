package wtf.onetap.module.player;

import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.PreMotionEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.mixin.player.MultiPlayerGameModeAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PacketUtil;

@ModuleInformation(name = "ElytraSwap", description = "Свап элитры и фейерверки", category = Category.PLAYER)
public class ElytraSwap extends Module {
   private final KeybindSetting swapKey = new KeybindSetting("Клавиша свапа", -1).build();
   private final KeybindSetting fireworkKey = new KeybindSetting("Клавиша фейерверка", -1).build();
   private final ModeSetting swapMode = new ModeSetting("Режим свапа", "Дефолт", "Дефолт", "Грим", "Легит").build();
   private final ModeSetting fireworkMode = new ModeSetting("Режим фейерверка", "Пакетный", "Пакетный", "Pick", "Grim 1.21.4").build();
   private final BooleanSetting autoTakeoff = new BooleanSetting("Автовзлёт", true).build();
   private final BooleanSetting autoFirework = new BooleanSetting("Авто фейерверк", true).visibleWhen(this.autoTakeoff::getValue).build();
   private final FloatSetting delay = new FloatSetting("Задержка", 50.0F, 0.0F, 150.0F, 1.0F).visibleWhen(() -> ((String)this.swapMode.getValue()).equals("Легит")).build();
   private boolean flag;
   private long longValue = 0L;
   private long longValue2 = 0L;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (event.getAction() == 1) {
            if (!ClickGuiScreen.isEditing()) {
               if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gui.screen() == null && MinecraftAccess.MINECRAFT.gui.overlay() == null) {
                  int value = event.getKey();
                  if (value == (Integer)this.swapKey.getValue()) {
                     this.m_3_n_void();
                  } else if (value == (Integer)this.fireworkKey.getValue()) {
                     this.m_7_void();
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if ((Boolean)this.autoTakeoff.getValue() || this.flag) {
            this.m_3_f_void();
         }
      }
   }

   @EventTarget
   private void onConnected(ConnectedEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (((String)this.fireworkMode.getValue()).equals("Pick") || ((String)this.fireworkMode.getValue()).equals("Grim 1.21.4")) {
            if (this.flag2 && !this.flag3 && this.m_1_boolean()) {
               InventoryUtil.releaseKeys();
               this.flag4 = true;
            }
         }
      }
   }

   @EventTarget
   private void onPreMotion(PreMotionEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (this.flag2) {
            if (this.flag3) {
               this.flag3 = false;
            } else if (((String)this.fireworkMode.getValue()).equals("Pick")) {
               this.m_3_e_void();
            } else if (((String)this.fireworkMode.getValue()).equals("Grim 1.21.4")) {
               this.m_4_void();
            } else {
               this.flag2 = false;
               InventoryUtil.useItem(Items.FIREWORK_ROCKET);
            }
         }
      }
   }

   private void m_3_n_void() {
      long longValue3 = System.currentTimeMillis();
      if (longValue3 - this.longValue >= 110L) {
         this.longValue = longValue3;
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST);
         this.m_5_boolean(itemStack.is(Items.ELYTRA));
      }
   }

   private boolean m_0_boolean() {
      int value = InventoryUtil.m_1_int();
      if (value == -1) {
         return false;
      }

      this.swapWithMode(value);
      if ((Boolean)this.autoTakeoff.getValue()) {
         if (MinecraftAccess.MINECRAFT.player.onGround()) {
            MinecraftAccess.MINECRAFT.player.jumpFromGround();
         }

         this.flag = true;
      }

      return true;
   }

   private boolean m_5_boolean() {
      int value = InventoryUtil.findBestChestplateSlot();
      if (value == -1) {
         return false;
      }

      this.swapWithMode(value);
      return true;
   }

   public boolean m_5_boolean(boolean flag5) {
      return flag5 ? this.m_5_boolean() : this.m_0_boolean();
   }

   public boolean m_3_boolean(boolean flag5) {
      int value = flag5 ? InventoryUtil.findBestChestplateSlot() : InventoryUtil.m_1_int();
      if (value == -1) {
         return false;
      }

      InventoryUtil.m_6(this.createSwapAction(value));
      return true;
   }

   public boolean m_3_boolean() {
      return (((String)this.fireworkMode.getValue()).equals("Pick") || ((String)this.fireworkMode.getValue()).equals("Grim 1.21.4")) && (this.flag2 || this.flag3);
   }

   public void m_39_void() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         this.flag = true;
         this.m_3_f_void();
      }
   }

   private void m_3_f_void() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (!InventoryUtil.isModernProtocol() || !InventoryUtil.m_7_boolean()) {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST);
            if (!itemStack.is(Items.ELYTRA)
               || MinecraftAccess.MINECRAFT.player.isInLava()
               || MinecraftAccess.MINECRAFT.player.isInWater()
               || MinecraftAccess.MINECRAFT.player.isPassenger()
               || MinecraftAccess.MINECRAFT.player.isFallFlying()
               || MinecraftAccess.MINECRAFT.player.isSpectator()) {
               this.flag = false;
            } else if (MinecraftAccess.MINECRAFT.player.onGround()) {
               MinecraftAccess.MINECRAFT.player.jumpFromGround();
            } else {
               MinecraftAccess.MINECRAFT.player.startFallFlying();
               MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.START_FALL_FLYING));
               if ((Boolean)this.autoFirework.getValue()) {
                  this.m_1_void();
               }

               this.flag = false;
            }
         }
      }
   }

   private void swapWithMode(int value) {
      InventoryUtil.runWithMode((String)this.swapMode.getValue(), this.createSwapAction(value), (Float)this.delay.getValue());
   }

   public void swapNow(int value) {
      this.createSwapAction(value).run();
   }

   private Runnable createSwapAction(int value) {
      return () -> {
         if (value >= 0 && value < 9) {
            InventoryUtil.clickSlot(0, 6, value, ContainerInput.SWAP);
         } else if (MinecraftAccess.MINECRAFT.player.getInventory().getFreeSlot() != -1) {
            if (!MinecraftAccess.MINECRAFT.player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
               InventoryUtil.clickSlot(0, 6, 0, ContainerInput.QUICK_MOVE);
            }

            InventoryUtil.clickSlot(0, value, 0, ContainerInput.QUICK_MOVE);
         } else {
            int value2 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
            InventoryUtil.clickSlot(0, value, value2, ContainerInput.SWAP);
            InventoryUtil.clickSlot(0, 6, value2, ContainerInput.SWAP);
            InventoryUtil.clickSlot(0, value, value2, ContainerInput.SWAP);
         }
      };
   }

   private void m_7_void() {
      if (MinecraftAccess.MINECRAFT.player.isFallFlying()) {
         long longValue3 = System.currentTimeMillis();
         if (longValue3 - this.longValue2 >= 200L) {
            this.longValue2 = longValue3;
            this.m_1_void();
         }
      }
   }

   private void m_1_void() {
      this.flag2 = true;
      this.flag3 = this.flag;
   }

   private void m_3_e_void() {
      this.flag2 = false;

      try {
         this.m_2_void();
      } finally {
         if (this.flag4) {
            InventoryUtil.restoreKeys();
            this.flag4 = false;
         }
      }
   }

   private void m_2_void() {
      if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.FIREWORK_ROCKET)) {
         this.m_9(InteractionHand.MAIN_HAND);
      } else if (MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.FIREWORK_ROCKET)) {
         this.m_9(InteractionHand.OFF_HAND);
      } else {
         int value = InventoryUtil.findItemSlot(Items.FIREWORK_ROCKET);
         if (value != -1) {
            int value2 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
            if (value < 9) {
               InventoryUtil.selectHotbarSlot(value);
               this.m_9(InteractionHand.MAIN_HAND);
               InventoryUtil.clickSlot(0, value + 36, value2, ContainerInput.SWAP);
               PacketUtil.sendPacket(new ServerboundContainerClosePacket(0));
            } else {
               InventoryUtil.clickSlot(0, value, value2, ContainerInput.SWAP);
               this.m_9(InteractionHand.MAIN_HAND);
               InventoryUtil.clickSlot(0, value, value2, ContainerInput.SWAP);
               PacketUtil.sendPacket(new ServerboundContainerClosePacket(0));
            }
         }
      }
   }

   private void m_4_void() {
      this.flag2 = false;

      try {
         this.m_3_void();
      } finally {
         if (this.flag4) {
            InventoryUtil.restoreKeys();
            this.flag4 = false;
         }
      }
   }

   private boolean m_1_boolean() {
      if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.FIREWORK_ROCKET)) {
         return false;
      } else {
         return MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.FIREWORK_ROCKET) ? false : InventoryUtil.findItemSlot(Items.FIREWORK_ROCKET) != -1;
      }
   }

   private void m_3_void() {
      if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.FIREWORK_ROCKET)) {
         this.m_9(InteractionHand.MAIN_HAND);
      } else if (MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.FIREWORK_ROCKET)) {
         this.m_9(InteractionHand.OFF_HAND);
      } else {
         int value = InventoryUtil.findItemSlot(Items.FIREWORK_ROCKET);
         if (value != -1) {
            int value2 = value < 9 ? value + 36 : value;
            InventoryUtil.clickSlot(0, value2, 40, ContainerInput.SWAP);
            this.m_9(InteractionHand.OFF_HAND);
            InventoryUtil.clickSlot(0, value2, 40, ContainerInput.SWAP);
            PacketUtil.sendPacket(new ServerboundContainerClosePacket(0));
         }
      }
   }

   private void m_9(InteractionHand interactionHand) {
      ((MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode)
         .onetap$startPrediction(MinecraftAccess.MINECRAFT.level, value -> new ServerboundUseItemPacket(interactionHand, value, MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot()));
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.flag2 = false;
      this.flag3 = false;
      if (this.flag4) {
         InventoryUtil.restoreKeys();
         this.flag4 = false;
      }
   }
}
