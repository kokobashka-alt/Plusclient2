package wtf.onetap.module.pve;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.AuctionScanner;
import wtf.onetap.autobuy.PriceParser;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.mixin.input.KeyMappingAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "CacaoFarm", description = "Автоматический фарм какао с продажей и автопочинкой", category = Category.PVE)
public class CacaoFarm extends Module {
   private static final Direction[] DIRECTION_ARRAY = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
   private final ModeSetting sellMode = new ModeSetting("Режим продажи", "Buyer", "Buyer", "Аукцион").build();
   private final FloatSetting sell = new FloatSetting("Продажа", 10.0F, 3.0F, 15.0F, 1.0F).visibleWhen(() -> ((String)this.sellMode.getValue()).equals("Buyer")).build();
   private final TextSetting pricePerStack = new TextSetting("Цена за стак", "1000").visibleWhen(() -> ((String)this.sellMode.getValue()).equals("Аукцион")).build();
   private final FloatSetting food = new FloatSetting("Еда", 3.0F, 1.0F, 9.0F, 1.0F).build();
   private final TextSetting pricePerExp = new TextSetting("Цена за штуку опыта", "2000").build();
   private Vec3 vec3;
   private Vec3 vec32;
   private boolean flag = true;
   private int value;
   private final TimeHelper timeHelper = new TimeHelper();
   private boolean flag2;
   private boolean flag3;
   private CacaoFarm.ModeKind3 c7 = CacaoFarm.ModeKind3.IDLE;
   private boolean flag4;
   private BlockPos blockPos;
   private Direction direction;
   private BlockPos blockPos2;
   private Vec3 vec33;
   private BlockPos blockPos3;
   private boolean flag5;
   private boolean flag6;
   private BlockPos blockPos4;
   private BlockPos blockPos5;
   private final TimeHelper timeHelper2 = new TimeHelper();
   private final TimeHelper timeHelper3 = new TimeHelper();
   private final TimeHelper timeHelper4 = new TimeHelper();
   private CacaoFarm.ModeKind4 c8 = CacaoFarm.ModeKind4.FREE;
   private CacaoFarm.ModeKind4 c82 = CacaoFarm.ModeKind4.FREE;
   private boolean flag7;
   private final TimeHelper timeHelper5 = new TimeHelper();
   private final TimeHelper timeHelper6 = new TimeHelper();
   private final TimeHelper timeHelper7 = new TimeHelper();
   private final TimeHelper timeHelper8 = new TimeHelper();
   private final TimeHelper timeHelper9 = new TimeHelper();
   private CacaoFarm.ModeKind c3 = CacaoFarm.ModeKind.IDLE;
   private int value2;
   private int value3 = -1;
   private CacaoFarm.ModeKind2 c6 = CacaoFarm.ModeKind2.IDLE;
   private boolean flag8;
   private boolean flag9;
   private boolean flag10;
   private boolean flag11;
   private Item item;
   private Item item2;
   private int value4;
   private boolean flag12;
   private boolean flag13;
   private boolean flag14;
   private final TimeHelper timeHelper10 = new TimeHelper();
   private static boolean flag15;

   @Override
   public void onEnable() {
      ChatUtil.log("§fCacaoFarm §7» §7Введите §f.cacao1 §7и §f.cacao2 §7для установки точек.");
      this.flag7 = false;
      this.stopMining();
      this.setRotationActive(false);
      this.timeHelper7.reset();
      this.c6 = CacaoFarm.ModeKind2.IDLE;
      this.flag8 = false;
      this.timeHelper8.reset();
      this.item2 = null;
      this.value4 = 0;
      this.c3 = CacaoFarm.ModeKind.IDLE;
      this.value2 = 0;
      this.value3 = -1;
      this.c8 = CacaoFarm.ModeKind4.FREE;
      this.c82 = CacaoFarm.ModeKind4.FREE;
      this.timeHelper5.reset();
      this.timeHelper6.reset();
      this.flag9 = false;
      this.flag10 = false;
      this.flag11 = false;
      this.resetFarmState();
      this.releaseUseKey();
      this.flag13 = false;
      this.flag14 = false;
   }

   @Override
   public void onDisable() {
      this.stopMining();
      this.releaseUseKey();
      this.setRotationActive(false);
      this.flag = true;
      this.value = 0;
      this.resetFarmState();
      this.timeHelper7.reset();
      this.c6 = CacaoFarm.ModeKind2.IDLE;
      this.flag8 = false;
      this.timeHelper8.reset();
      this.item2 = null;
      this.value4 = 0;
      Onetap.ROTATION_MODES.resetSmoothing();
      this.c3 = CacaoFarm.ModeKind.IDLE;
      this.value2 = 0;
      this.value3 = -1;
      this.c8 = CacaoFarm.ModeKind4.FREE;
      this.c82 = CacaoFarm.ModeKind4.FREE;
      this.flag9 = false;
      this.flag10 = false;
      this.flag11 = false;
      this.flag13 = false;
      this.flag14 = false;
      this.flag7 = false;
   }

   private void resetFarmState() {
      this.c7 = CacaoFarm.ModeKind3.IDLE;
      this.flag4 = false;
      this.blockPos = null;
      this.direction = null;
      this.blockPos2 = null;
      this.vec33 = null;
      this.blockPos3 = null;
      this.flag5 = false;
      this.flag6 = false;
      this.blockPos4 = null;
      this.blockPos5 = null;
      this.timeHelper2.reset();
      this.timeHelper3.reset();
      this.timeHelper4.reset();
   }

   public void setPoint1(Vec3 vec34) {
      this.vec3 = vec34;
      ChatUtil.log("§fCacaoFarm §7» §7Точка 1: §f" + formatVec(vec34));
   }

   public void setPoint2(Vec3 vec34) {
      this.vec32 = vec34;
      ChatUtil.log("§fCacaoFarm §7» §7Точка 2: §f" + formatVec(vec34));
   }

   private static String formatVec(Vec3 vec34) {
      return String.format(Locale.ROOT, "%.1f / %.1f / %.1f", vec34.x, vec34.y, vec34.z);
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (!this.flag7) {
            this.resetAll();
            this.flag7 = true;
            this.m_92_void();
         } else if (!this.m_5_boolean()) {
            this.m_9w_void();
            switch (this.c8.ordinal()) {
               case 1:
                  this.m_9f_void();
                  break;
               case 2:
                  this.m_3_void();
                  break;
               case 3:
                  this.checkAxeDurability();
                  break;
               case 4:
                  this.m_9z_void();
                  break;
               case 5:
                  this.m_9_i_void();
                  break;
               case 6:
                  this.m_9_w_void();
                  break;
               case 7:
                  this.m_7_void();
                  break;
               case 8:
                  this.m_9___void();
            }
         }
      }
   }

   private void resetAll() {
      this.timeHelper5.reset();
      this.timeHelper6.reset();
      this.c8 = CacaoFarm.ModeKind4.FARM;
      this.item = null;
      this.resetFarmState();
      this.releaseUseKey();
      this.flag13 = false;
      this.flag14 = false;
      this.timeHelper7.reset();
      this.c6 = CacaoFarm.ModeKind2.IDLE;
      this.flag8 = false;
      this.timeHelper8.reset();
      this.item2 = null;
      this.value4 = 0;
      Onetap.ROTATION_MODES.resetSmoothing();
      this.c3 = CacaoFarm.ModeKind.IDLE;
      this.value2 = 0;
      this.value3 = -1;
      this.flag9 = false;
      this.flag10 = false;
      this.flag11 = false;
   }

   private void m_9f_void() {
      if (this.vec3 != null && this.vec32 != null) {
         if (this.m_6_boolean()) {
            this.value++;
            Vec3 vec34 = this.flag ? this.vec32 : this.vec3;
            double doubleValue = vec34.x - MinecraftAccess.MINECRAFT.player.getX();
            double doubleValue2 = vec34.z - MinecraftAccess.MINECRAFT.player.getZ();
            if (doubleValue * doubleValue + doubleValue2 * doubleValue2 <= 0.09) {
               this.flag = !this.flag;
            }

            this.tickFarmState();
         }
      }
   }

   private boolean m_6_boolean() {
      Item item3 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()).getItem();
      Item item4 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
      if ((item3 == Items.NETHERITE_AXE || item3 == Items.DIAMOND_AXE) && item4 == Items.COCOA_BEANS) {
         return true;
      }

      Item item5 = this.getBestAxe();
      if (item5 == null && item3 != Items.NETHERITE_AXE && item3 != Items.DIAMOND_AXE) {
         return false;
      }

      this.startSwapItem();
      return false;
   }

   private void m_9w_void() {
      this.setRotationActive(this.c8 == CacaoFarm.ModeKind4.FARM || this.c8 == CacaoFarm.ModeKind4.GET_EXP || this.c8 == CacaoFarm.ModeKind4.REPAIR);
      if (this.c8 == CacaoFarm.ModeKind4.FARM) {
         if (MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() <= this.food.getInt() * 2 && this.m_4_int() != -1) {
            this.c8 = CacaoFarm.ModeKind4.EAT;
            return;
         }

         if (!this.m_3_boolean() && this.c8 != CacaoFarm.ModeKind4.SELL && this.c8 != CacaoFarm.ModeKind4.SWAP_ITEM) {
            this.c8 = CacaoFarm.ModeKind4.REPAIR;
         }
      }

      if (this.c8 == CacaoFarm.ModeKind4.REPAIR) {
         Rotations rotations = Onetap.ROTATION_MODES.toRotations();
         Onetap.ROTATION_MODES.lookAtAngles(rotations.getFloatValue(), 90.0F, 10);
         if (this.countItemWithOffhand(Items.EXPERIENCE_BOTTLE) <= 0 && this.c8 != CacaoFarm.ModeKind4.SELL && this.c8 != CacaoFarm.ModeKind4.SWAP_ITEM) {
            this.c8 = CacaoFarm.ModeKind4.GET_EXP;
         }
      }

      if ((this.c8 == CacaoFarm.ModeKind4.AUTO_BUY_EXP || this.c8 == CacaoFarm.ModeKind4.GET_EXP) && this.isInventoryFull()) {
         this.c8 = CacaoFarm.ModeKind4.SELL;
      }

      this.m_9_x_void();
   }

   private void m_9_x_void() {
      if (this.c8 != CacaoFarm.ModeKind4.SWAP_ITEM && this.c8 != CacaoFarm.ModeKind4.EAT && this.c8 != CacaoFarm.ModeKind4.AH_SELL) {
         if (!((String)this.sellMode.getValue()).equals("Аукцион")) {
            if (this.countItemWithOffhand(Items.COCOA_BEANS) >= this.sell.getInt()) {
               this.startSelling(Items.COCOA_BEANS);
               if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() != Items.COCOA_BEANS) {
                  this.startSwapItem();
               }
            } else {
               if (this.isInventoryFull()) {
                  this.startSelling(Items.COCOA_BEANS);
                  if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() != Items.COCOA_BEANS) {
                     this.startSwapItem();
                  }
               }
            }
         } else if (this.flag13) {
            if (this.countFreeSlots() <= 2) {
               this.startSelling(Items.COCOA_BEANS);
               if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() != Items.COCOA_BEANS) {
                  this.startSwapItem();
               }
            }
         } else {
            if (this.m_5_int() != -1) {
               this.c8 = CacaoFarm.ModeKind4.AH_SELL;
               this.flag14 = true;
               this.timeHelper10.reset();
            }
         }
      }
   }

   private void startSwapItem() {
      this.c82 = this.c8;
      this.c8 = CacaoFarm.ModeKind4.SWAP_ITEM;
      this.timeHelper5.reset();
   }

   private void resetSell() {
      this.c6 = CacaoFarm.ModeKind2.IDLE;
      this.flag8 = false;
      this.timeHelper7.reset();
      this.item2 = null;
      this.value4 = 0;
   }

   private void startSelling(Item item3) {
      boolean flag16 = this.item != item3;
      if (this.c8 == CacaoFarm.ModeKind4.SELL && !flag16) {
         this.item = item3;
      } else {
         this.item = item3;
         this.c8 = CacaoFarm.ModeKind4.SELL;
         this.resetSell();
      }
   }

   private void m_92_void() {
      this.value2 = 0;
      this.value3 = -1;
      this.timeHelper9.reset();
      this.c3 = CacaoFarm.ModeKind.SEND_BUYER;
   }

   private boolean m_5_boolean() {
      if (this.c3 == CacaoFarm.ModeKind.IDLE) {
         return false;
      }

      AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
      switch (this.c3.ordinal()) {
         case 1:
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.closeScreens();
               return true;
            }

            this.sendCommand("buyer");
            this.timeHelper9.reset();
            this.c3 = CacaoFarm.ModeKind.WAIT_FOR_CONTAINER;
            return true;
         case 2:
            if (abstractContainerMenu != null) {
               this.timeHelper9.reset();
               this.c3 = CacaoFarm.ModeKind.CLICK_CACTUS;
            } else if (hasElapsedOver(this.timeHelper9, 3000L)) {
               this.c3 = CacaoFarm.ModeKind.SEND_BUYER;
               this.timeHelper9.reset();
            }

            return true;
         case 3:
            if (abstractContainerMenu == null) {
               this.c3 = CacaoFarm.ModeKind.WAIT_FOR_CONTAINER;
               return true;
            } else {
               if (!hasElapsedOver(this.timeHelper9, 500L)) {
                  return true;
               }

               int value5 = this.m_4_int(abstractContainerMenu);
               if (value5 != -1) {
                  InventoryUtil.clickSlot(abstractContainerMenu.containerId, value5, 0, ContainerInput.PICKUP);
                  this.moveCarriedToHotbar();
                  this.timeHelper9.reset();
                  this.c3 = CacaoFarm.ModeKind.WAIT_AFTER_CACTUS;
               } else if (hasElapsedOver(this.timeHelper9, 2000L)) {
                  this.c3 = CacaoFarm.ModeKind.FINISH;
               }

               return true;
            }
         case 4:
            if (abstractContainerMenu == null) {
               this.c3 = CacaoFarm.ModeKind.WAIT_FOR_CONTAINER;
               return true;
            }

            if (hasElapsedOver(this.timeHelper9, 1000L)) {
               this.c3 = CacaoFarm.ModeKind.PREPARE_COCOA;
               this.timeHelper9.reset();
            }

            return true;
         case 5:
            if (abstractContainerMenu == null) {
               this.c3 = CacaoFarm.ModeKind.WAIT_FOR_CONTAINER;
               return true;
            }

            this.value3 = this.m_7_int(abstractContainerMenu);
            if (this.value3 != -1) {
               this.value2 = 0;
               this.timeHelper9.reset();
               this.c3 = CacaoFarm.ModeKind.RIGHT_CLICK_COCOA;
            } else if (hasElapsedOver(this.timeHelper9, 2000L)) {
               this.c3 = CacaoFarm.ModeKind.FINISH;
            }

            return true;
         case 6:
            if (abstractContainerMenu == null) {
               this.c3 = CacaoFarm.ModeKind.WAIT_FOR_CONTAINER;
               return true;
            } else {
               if (this.value3 == -1) {
                  this.value3 = this.m_7_int(abstractContainerMenu);
                  this.timeHelper9.reset();
                  return true;
               }

               if (hasElapsedOver(this.timeHelper9, 750L)) {
                  InventoryUtil.clickSlot(abstractContainerMenu.containerId, this.value3, 1, ContainerInput.PICKUP);
                  this.moveCarriedToHotbar();
                  this.value2++;
                  this.timeHelper9.reset();
                  if (this.value2 >= 3) {
                     this.c3 = CacaoFarm.ModeKind.FINISH;
                  }
               }

               return true;
            }
         case 7:
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.closeScreens();
            }

            this.c3 = CacaoFarm.ModeKind.IDLE;
            this.value2 = 0;
            this.value3 = -1;
            this.c8 = CacaoFarm.ModeKind4.FARM;
            this.stopRotation();
            return false;
         default:
            this.c3 = CacaoFarm.ModeKind.IDLE;
            this.value2 = 0;
            this.value3 = -1;
            return false;
      }
   }

   private void stopRotation() {
      this.stopMining();
      this.flag2 = false;
      if (Onetap.ROTATION_MODES.isFlag5()) {
         Onetap.ROTATION_MODES.syncWithPlayer();
      }

      Onetap.ROTATION_MODES.reset();
   }

   private void m_3_void() {
      if (this.item == null) {
         this.c8 = CacaoFarm.ModeKind4.FARM;
      } else {
         if (this.c6 == CacaoFarm.ModeKind2.IDLE) {
            this.c6 = CacaoFarm.ModeKind2.SEND_COMMAND;
            this.flag8 = false;
         }

         AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
         switch (this.c6.ordinal()) {
            case 1:
               if (!this.flag8) {
                  this.sendCommand("buyer");
                  this.flag8 = true;
                  this.timeHelper7.reset();
               }

               if (hasElapsedOver(this.timeHelper7, 500L)) {
                  this.c6 = CacaoFarm.ModeKind2.WAIT_FOR_CONTAINER;
                  this.timeHelper7.reset();
               }
               break;
            case 2:
               if (abstractContainerMenu != null) {
                  this.c6 = CacaoFarm.ModeKind2.WAIT_BEFORE_CATEGORY;
                  this.timeHelper7.reset();
                  return;
               }

               if (hasElapsedOver(this.timeHelper7, 500L)) {
                  this.c6 = CacaoFarm.ModeKind2.RETRY_DELAY;
                  this.timeHelper7.reset();
               }
               break;
            case 3:
               if (abstractContainerMenu == null) {
                  if (hasElapsedOver(this.timeHelper7, 500L)) {
                     this.c6 = CacaoFarm.ModeKind2.RETRY_DELAY;
                     this.timeHelper7.reset();
                  }

                  return;
               }

               if (!hasElapsedOver(this.timeHelper7, 500L)) {
                  return;
               }

               int value5 = this.m_4_int(abstractContainerMenu);
               if (value5 == -1) {
                  if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                     this.closeScreens();
                  }

                  this.c6 = CacaoFarm.ModeKind2.RETRY_DELAY;
                  this.timeHelper7.reset();
                  return;
               }

               InventoryUtil.clickSlot(abstractContainerMenu.containerId, value5, 0, ContainerInput.PICKUP);
               this.c6 = CacaoFarm.ModeKind2.WAIT_AFTER_CATEGORY;
               this.timeHelper7.reset();
               break;
            case 4:
               if (abstractContainerMenu == null) {
                  if (hasElapsedOver(this.timeHelper7, 2500L)) {
                     this.c6 = CacaoFarm.ModeKind2.RETRY_DELAY;
                     this.timeHelper7.reset();
                  }

                  return;
               }

               if (!hasElapsedOver(this.timeHelper7, 1000L)) {
                  return;
               }

               CacaoFarm.StateHolder c4 = this.m_7_C4(abstractContainerMenu);
               if (c4 == null) {
                  if (hasElapsedOver(this.timeHelper7, 4000L)) {
                     if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                        this.closeScreens();
                     }

                     this.c6 = CacaoFarm.ModeKind2.RETRY_DELAY;
                     this.timeHelper7.reset();
                  }

                  return;
               }

               this.item2 = c4.item;
               this.value4 = c4.value;
               this.item = this.item2;
               int value6 = this.m_7(abstractContainerMenu, c4);
               if (value6 == -1) {
                  if (hasElapsedOver(this.timeHelper7, 4000L)) {
                     if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                        this.closeScreens();
                     }

                     this.c6 = CacaoFarm.ModeKind2.RETRY_DELAY;
                     this.timeHelper7.reset();
                  }

                  return;
               }

               InventoryUtil.clickSlot(abstractContainerMenu.containerId, value6, 0, ContainerInput.QUICK_MOVE);
               this.moveCarriedToHotbar();
               this.c6 = CacaoFarm.ModeKind2.CHECK_RESULT;
               this.timeHelper7.reset();
               break;
            case 5:
               if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  if (hasElapsedOver(this.timeHelper7, 1500L)) {
                     this.closeScreens();
                  }

                  return;
               }

               if (!hasElapsedOver(this.timeHelper7, 500L)) {
                  return;
               }

               if (this.item2 == null) {
                  this.c6 = CacaoFarm.ModeKind2.RETRY_DELAY;
                  this.timeHelper7.reset();
                  return;
               }

               int value7 = this.countItemWithOffhand(this.item2);
               int value8 = this.value4 > 0 ? this.value4 : this.sell.getInt();
               int value9 = this.toStacks(value7);
               boolean flag16 = value7 < value8 || value9 < value8;
               if (flag16) {
                  this.c8 = CacaoFarm.ModeKind4.FARM;
                  this.item = null;
                  this.resetSell();
               } else {
                  this.c6 = CacaoFarm.ModeKind2.RETRY_DELAY;
                  this.flag8 = false;
                  this.timeHelper7.reset();
               }
               break;
            case 6:
               if (!hasElapsedOver(this.timeHelper7, 1000L)) {
                  return;
               }

               if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  this.closeScreens();
               }

               this.flag8 = false;
               this.c6 = CacaoFarm.ModeKind2.SEND_COMMAND;
               this.timeHelper7.reset();
         }
      }
   }

   private void checkAxeDurability() {
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getOffhandItem();
      if (itemStack.getItem() != Items.DIAMOND_AXE && itemStack.getItem() != Items.NETHERITE_AXE) {
         this.startSwapItem();
      } else if (itemStack.isDamageableItem()) {
         if (itemStack.getMaxDamage() - itemStack.getDamageValue() == itemStack.getMaxDamage()) {
            this.c8 = CacaoFarm.ModeKind4.FARM;
         } else if (this.countItemWithOffhand(Items.EXPERIENCE_BOTTLE) <= 0) {
            this.c8 = CacaoFarm.ModeKind4.GET_EXP;
         } else {
            long longValue = 50L;
            if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() != Items.EXPERIENCE_BOTTLE) {
               longValue = 200L;
            }

            if (checkDelay(this.timeHelper5, longValue, true)) {
               if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() != Items.EXPERIENCE_BOTTLE) {
                  this.swapToMainHand(Items.EXPERIENCE_BOTTLE);
               } else {
                  MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
               }
            }
         }
      }
   }

   private void m_9z_void() {
      BlockPos blockPos6 = this.findChest();
      if (blockPos6 != null) {
         AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
         if (abstractContainerMenu != null) {
            if (this.countItemInMenu(abstractContainerMenu, Items.EXPERIENCE_BOTTLE) <= 0 && MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.c8 = CacaoFarm.ModeKind4.AUTO_BUY_EXP;
               this.closeScreens();
            }

            if (this.isInventoryFull() && this.countFreeSlots() < 5 && MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.c8 = CacaoFarm.ModeKind4.SELL;
               this.closeScreens();
            }

            if (this.countExpBottles() < 5) {
               if (this.countItemInMenu(abstractContainerMenu, Items.EXPERIENCE_BOTTLE) > 0) {
                  if (checkDelay(this.timeHelper5, 500L, true)) {
                     int value5 = this.findItemInMenu(abstractContainerMenu, Items.EXPERIENCE_BOTTLE);
                     if (value5 != -1) {
                        InventoryUtil.clickSlot(abstractContainerMenu.containerId, value5, 0, ContainerInput.QUICK_MOVE);
                        this.moveCarriedToHotbar();
                     }
                  }
               } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  this.c8 = CacaoFarm.ModeKind4.AUTO_BUY_EXP;
                  this.closeScreens();
               }
            } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.c8 = CacaoFarm.ModeKind4.FARM;
               this.closeScreens();
            }
         } else if (checkDelay(this.timeHelper6, 52L, true)) {
            Onetap.ROTATION_MODES.lookAt(blockPos6, 10);
            Rotations rotations = Onetap.ROTATION_MODES.toRotations();
            BlockHitResult blockHitResult = this.raycast(rotations.getFloatValue(), rotations.getFloatValue2(), 4.0);
            if (blockHitResult != null) {
               MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
            }
         }
      } else {
         this.c8 = CacaoFarm.ModeKind4.AUTO_BUY_EXP;
      }
   }

   private void m_9_i_void() {
      if (this.flag11 && MinecraftAccess.MINECRAFT.gui.screen() == null && hasElapsedOver(this.timeHelper8, 500L)) {
         this.flag11 = false;
         this.c8 = CacaoFarm.ModeKind4.FARM;
      } else if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
         if (MinecraftAccess.MINECRAFT.player.tickCount % 10 == 0 && !this.flag11) {
            this.flag9 = true;
            this.flag11 = false;
            this.flag10 = true;
            this.searchAuction("Пузырёк опыта");
         }
      } else if (this.flag11) {
         this.closeScreens();
      } else {
         AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
         int value5 = this.pricePerExp.getInt();
         if (value5 <= 0) {
            value5 = 2000;
         }

         int value6 = this.getBalance();
         int value7 = this.m_2(abstractContainerMenu, Items.EXPERIENCE_BOTTLE, value5, true);
         if (value7 != -1) {
            int value8 = this.getPriceOf(abstractContainerMenu.getSlot(value7).getItem());
            if (value8 > 0 && value6 >= 0 && value6 < value8) {
               this.flag11 = true;
               this.timeHelper8.reset();
            }

            if (this.flag10) {
               if (checkDelay(this.timeHelper5, 1000L, true)) {
                  this.flag10 = false;
               }
            } else {
               int value9 = Math.min(8, this.countFreeSlots() - 1);
               if (this.countExpBottles() < value9) {
                  this.quickMoveSlot(abstractContainerMenu, value7);
               } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  this.flag11 = true;
                  this.timeHelper8.reset();
               }
            }
         } else if (value6 >= 0 && value6 < value5 * 64L) {
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               if (this.countExpBottles() <= 0) {
                  this.c8 = CacaoFarm.ModeKind4.FARM;
                  this.closeScreens();
                  this.log("У вас закончились деньги и пузырьки опыта!");
                  this.setEnabled(false);
               } else {
                  this.flag11 = true;
                  this.timeHelper8.reset();
               }
            }
         } else if (this.flag9) {
            if (checkDelay(this.timeHelper5, 1050L, true)) {
               this.flag9 = false;
            }
         } else {
            this.m_8_void(abstractContainerMenu);
            this.flag9 = true;
         }
      }
   }

   private void m_9_w_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         this.closeScreens();
      } else {
         long longValue = 400L;
         if (checkDelay(this.timeHelper5, longValue, true)) {
            if (this.c82 == CacaoFarm.ModeKind4.REPAIR) {
               if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() != Items.EXPERIENCE_BOTTLE) {
                  if (!this.swapToMainHand(Items.EXPERIENCE_BOTTLE)) {
                     this.c8 = CacaoFarm.ModeKind4.GET_EXP;
                  }
               } else {
                  Item item3 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
                  if (item3 != Items.NETHERITE_AXE && item3 != Items.DIAMOND_AXE) {
                     Item item4 = this.getBestAxe();
                     if (item4 == null || !this.swapToOffhand(item4)) {
                        this.c8 = CacaoFarm.ModeKind4.FARM;
                     }
                  } else {
                     this.c8 = CacaoFarm.ModeKind4.REPAIR;
                  }
               }
            } else if (this.c82 == CacaoFarm.ModeKind4.FARM) {
               Item item5 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()).getItem();
               if (item5 != Items.NETHERITE_AXE && item5 != Items.DIAMOND_AXE) {
                  Item item6 = this.getBestAxe();
                  if (item6 != null) {
                     this.swapToMainHand(item6);
                  }
               } else {
                  Item item7 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
                  if (item7 != Items.COCOA_BEANS) {
                     this.swapToOffhand(Items.COCOA_BEANS);
                  } else {
                     this.c8 = CacaoFarm.ModeKind4.FARM;
                  }
               }
            } else {
               if (this.c82 == CacaoFarm.ModeKind4.SELL) {
                  Item item8 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
                  if (item8 != Items.COCOA_BEANS) {
                     this.swapToOffhand(Items.COCOA_BEANS);
                     return;
                  }

                  this.c8 = CacaoFarm.ModeKind4.SELL;
               }
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event != null && event.getPacket() != null) {
         String text = getChatText(event.getPacket());
         if (text != null && !text.isEmpty()) {
            String text2 = text.toLowerCase(Locale.ROOT).replace('ё', 'е');
            if (!text2.contains("не удалось выставить") && !text2.contains("освободите хранилище") && !text2.contains("арендуйте больше слотов")) {
               if (text2.contains("у вас купили") && text2.contains("какао")) {
                  this.flag13 = false;
               }
            } else {
               this.flag13 = true;
            }
         }
      }
   }

   private void m_7_void() {
      if (MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() < 20 && this.m_4_int() != -1) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
         if (itemStack.get(DataComponents.FOOD) == null) {
            this.releaseUseKey();
            int value5 = this.m_4_int();
            if (value5 == -1) {
               this.c8 = CacaoFarm.ModeKind4.FARM;
            } else {
               Item item3 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value5).getItem();
               this.swapToMainHand(item3);
            }
         } else {
            if (!MinecraftAccess.MINECRAFT.player.isUsingItem()) {
               Key key = ((KeyMappingAccessor)MinecraftAccess.MINECRAFT.options.keyUse).onetap$getKey();
               KeyMapping.click(key);
            }

            MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
            this.flag12 = true;
         }
      } else {
         this.releaseUseKey();
         this.c8 = CacaoFarm.ModeKind4.FARM;
         this.resetFarmState();
      }
   }

   private void m_9___void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         this.closeScreens();
      } else if (this.flag14) {
         if (checkDelay(this.timeHelper10, 200L, true)) {
            int value5 = this.m_5_int();
            if (value5 == -1) {
               this.c8 = CacaoFarm.ModeKind4.FARM;
            } else {
               InventoryUtil.clickSlot(0, m_9_int(value5), MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot(), ContainerInput.SWAP);
               this.flag14 = false;
               this.timeHelper10.reset();
            }
         }
      } else if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() != Items.COCOA_BEANS) {
         this.flag14 = true;
         this.timeHelper10.reset();
      } else if (checkDelay(this.timeHelper10, 150L, true)) {
         String text = ((String)this.pricePerStack.getValue()).trim();
         if (text.isEmpty()) {
            this.c8 = CacaoFarm.ModeKind4.FARM;
         } else {
            this.sendCommand("ah sell " + text);
            this.c8 = CacaoFarm.ModeKind4.FARM;
         }
      }
   }

   private int m_4_int() {
      int value5 = -1;
      int value6 = -1;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
            if (foodProperties != null) {
               Item item3 = itemStack.getItem();
               if (item3 != Items.GOLDEN_APPLE && item3 != Items.ENCHANTED_GOLDEN_APPLE && item3 != Items.APPLE && foodProperties.nutrition() > value6) {
                  value6 = foodProperties.nutrition();
                  value5 = i;
               }
            }
         }
      }

      return value5;
   }

   private int m_5_int() {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.getItem() == Items.COCOA_BEANS && itemStack.getCount() == 64) {
            return i;
         }
      }

      return -1;
   }

   private void releaseUseKey() {
      if (this.flag12) {
         this.flag12 = false;
         if (MinecraftAccess.MINECRAFT.options != null) {
            MinecraftAccess.MINECRAFT.options.keyUse.setDown(false);
         }
      }
   }

   private static String getChatText(Packet<?> packet) {
      if (packet instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
         return stripColors(clientboundSystemChatPacket.content().getString());
      } else if (packet instanceof ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {
         return stripColors(clientboundDisguisedChatPacket.message().getString());
      } else if (packet instanceof ClientboundPlayerChatPacket clientboundPlayerChatPacket) {
         return clientboundPlayerChatPacket.unsignedContent() != null ? stripColors(clientboundPlayerChatPacket.unsignedContent().getString()) : stripColors(clientboundPlayerChatPacket.body().content());
      } else {
         return null;
      }
   }

   private static String stripColors(String text) {
      return text != null && !text.isEmpty() ? text.replaceAll("§x(§[0-9a-fA-F]){6}", "").replaceAll("§.", "").replace(' ', ' ').replace(' ', ' ').trim() : "";
   }

   private void tickFarmState() {
      switch (this.c7.ordinal()) {
         case 0:
            this.c7 = CacaoFarm.ModeKind3.SEARCH;
            break;
         case 1:
            this.searchTarget();
            break;
         case 2:
            this.m_4_void();
            break;
         case 3:
            this.m_6_void(null);
            break;
         case 4:
            this.m_9(null, Onetap.ROTATION_MODES.toRotations());
            break;
         case 5:
            this.m_9_b_void();
      }
   }

   private void searchTarget() {
      this.blockPos4 = null;
      CacaoFarm.DataRecord c1 = this.m_6_C1();
      if (c1 != null) {
         this.flag4 = true;
         this.blockPos = c1.f_9i;
         this.direction = c1.f_7g;
         this.blockPos2 = null;
         this.vec33 = null;
         Onetap.ROTATION_MODES.lookAt(c1.f_9i, 10);
         this.timeHelper2.reset();
         this.c7 = CacaoFarm.ModeKind3.ROTATE;
      } else {
         CacaoFarm.DataRecord2 c9 = this.m_1_C9();
         if (c9 == null) {
            this.m_9a_void();
         } else {
            this.flag4 = false;
            this.blockPos = c9.f_6_i;
            this.direction = c9.f_8p;
            this.blockPos2 = c9.f_6_d;
            this.vec33 = c9.f_5a;
            Onetap.ROTATION_MODES.lookAtClamped(c9.f_5a, 10);
            this.timeHelper2.reset();
            this.c7 = CacaoFarm.ModeKind3.ROTATE;
         }
      }
   }

   private void m_4_void() {
      if (this.blockPos == null) {
         this.c7 = CacaoFarm.ModeKind3.SEARCH;
      } else {
         if (this.flag4) {
            if (!(MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos).getBlock() instanceof CocoaBlock)) {
               this.c7 = CacaoFarm.ModeKind3.SEARCH;
               return;
            }

            Onetap.ROTATION_MODES.lookAt(this.blockPos, 10);
         } else {
            if (!MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos).isAir() || !MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos2).is(BlockTags.JUNGLE_LOGS)) {
               this.c7 = CacaoFarm.ModeKind3.SEARCH;
               return;
            }

            Onetap.ROTATION_MODES.lookAtClamped(this.vec33, 10);
         }

         if (this.m_9_boolean() || hasElapsedOver(this.timeHelper2, 150L)) {
            if (hasElapsedOver(this.timeHelper4, 50L)) {
               Rotations rotations = Onetap.ROTATION_MODES.toRotations();
               BlockHitResult blockHitResult = this.raycast(rotations.getFloatValue(), rotations.getFloatValue2(), 4.0);
               if (this.flag4) {
                  this.m_9(blockHitResult, rotations);
               } else {
                  this.m_6_void(blockHitResult);
               }
            }
         }
      }
   }

   private boolean m_9_boolean() {
      Rotations rotations = this.flag4 ? Onetap.ROTATION_MODES.getRotationTo(this.blockPos) : Onetap.ROTATION_MODES.getRotationTo(this.vec33);
      Rotations rotations2 = Onetap.ROTATION_MODES.toRotations();
      float floatValue = Math.abs(Mth.wrapDegrees(rotations.getFloatValue() - rotations2.getFloatValue()));
      float floatValue2 = Math.abs(Mth.wrapDegrees(rotations.getFloatValue2() - rotations2.getFloatValue2()));
      return floatValue < 2.0F && floatValue2 < 2.0F;
   }

   private void m_6_void(BlockHitResult blockHitResult) {
      if (this.blockPos != null && this.blockPos2 != null) {
         if (!MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos).isAir() || !MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos2).is(BlockTags.JUNGLE_LOGS)) {
            this.c7 = CacaoFarm.ModeKind3.SEARCH;
         } else if (!this.m_4_boolean()) {
            this.c7 = CacaoFarm.ModeKind3.SEARCH;
         } else {
            BlockHitResult blockHitResult2;
            if (blockHitResult != null && blockHitResult.getBlockPos().equals(this.blockPos2)) {
               blockHitResult2 = blockHitResult;
            } else {
               blockHitResult2 = new BlockHitResult(this.vec33, this.direction, this.blockPos2, false);
            }

            MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.OFF_HAND, blockHitResult2);
            this.blockPos4 = this.blockPos;
            this.blockPos3 = this.blockPos;
            this.flag5 = false;
            this.flag6 = false;
            this.timeHelper4.reset();
            this.timeHelper3.reset();
            this.c7 = CacaoFarm.ModeKind3.WAIT_CONFIRM;
         }
      } else {
         this.c7 = CacaoFarm.ModeKind3.SEARCH;
      }
   }

   private void m_9(BlockHitResult blockHitResult, Rotations rotations) {
      if (this.blockPos != null && MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos).getBlock() instanceof CocoaBlock) {
         BlockHitResult blockHitResult2 = this.resolveHit(blockHitResult, this.blockPos, rotations);
         this.executeOnMainThread(blockHitResult2);
         this.blockPos4 = this.blockPos;
         this.blockPos3 = this.blockPos;
         this.flag5 = true;
         this.flag6 = false;
         this.timeHelper4.reset();
         this.timeHelper3.reset();
         this.c7 = CacaoFarm.ModeKind3.WAIT_CONFIRM;
      } else {
         this.c7 = CacaoFarm.ModeKind3.SEARCH;
      }
   }

   private void m_9_b_void() {
      if (this.blockPos3 == null) {
         this.c7 = CacaoFarm.ModeKind3.SEARCH;
      } else {
         boolean flag16;
         if (this.flag5) {
            flag16 = !(MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos3).getBlock() instanceof CocoaBlock);
         } else {
            flag16 = !MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos3).isAir();
         }

         if (flag16) {
            this.blockPos5 = null;
            this.c7 = CacaoFarm.ModeKind3.SEARCH;
         } else if (hasElapsedOver(this.timeHelper3, 200L)) {
            if (!this.flag6) {
               this.flag6 = true;
               this.timeHelper3.reset();
               Rotations rotations = Onetap.ROTATION_MODES.toRotations();
               BlockHitResult blockHitResult = this.raycast(rotations.getFloatValue(), rotations.getFloatValue2(), 4.0);
               if (this.flag5) {
                  this.m_9(blockHitResult, rotations);
               } else {
                  this.m_6_void(blockHitResult);
               }
            } else {
               this.blockPos5 = this.blockPos3;
               this.c7 = CacaoFarm.ModeKind3.SEARCH;
            }
         }
      }
   }

   private BlockHitResult resolveHit(BlockHitResult blockHitResult, BlockPos blockPos6, Rotations rotations) {
      if (blockHitResult != null && blockHitResult.getBlockPos().equals(blockPos6)) {
         return blockHitResult;
      }

      BlockHitResult blockHitResult2 = this.raycast(rotations.getFloatValue(), rotations.getFloatValue2(), 4.0);
      if (blockHitResult2 != null && blockHitResult2.getBlockPos().equals(blockPos6)) {
         return blockHitResult2;
      }

      Vec3 vec34 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
      Vec3 vec35 = Vec3.atCenterOf(blockPos6);
      Direction direction2 = Direction.getApproximateNearest(vec35.x - vec34.x, vec35.y - vec34.y, vec35.z - vec34.z);
      Direction direction3 = direction2 != null ? direction2.getOpposite() : Direction.UP;
      return new BlockHitResult(vec35, direction3, blockPos6, false);
   }

   private void m_9a_void() {
      Onetap.ROTATION_MODES.lookAtAngles(Onetap.ROTATION_MODES.getRenderYaw(), Onetap.ROTATION_MODES.getRenderPitch(), 10);
   }

   private CacaoFarm.DataRecord2 m_1_C9() {
      BlockPos blockPos6 = MinecraftAccess.MINECRAFT.player.blockPosition();
      double doubleValue = MinecraftAccess.MINECRAFT.player.getX();
      double doubleValue2 = MinecraftAccess.MINECRAFT.player.getY();
      double doubleValue3 = MinecraftAccess.MINECRAFT.player.getZ();
      double doubleValue4 = 24.010000000000005;
      CacaoFarm.DataRecord2 c9 = null;
      double doubleValue5 = Double.MAX_VALUE;

      for (int i = -3; i <= 3; i++) {
         for (int i2 = -4; i2 <= 4; i2++) {
            for (int i3 = -3; i3 <= 3; i3++) {
               BlockPos blockPos7 = blockPos6.offset(i, i2, i3);
               if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7).is(BlockTags.JUNGLE_LOGS)) {
                  for (Direction direction2 : DIRECTION_ARRAY) {
                     BlockPos blockPos8 = blockPos7.relative(direction2);
                     if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos8).isAir() && (this.blockPos5 == null || !blockPos8.equals(this.blockPos5))) {
                        double doubleValue6 = blockPos8.getX() + 0.5 - doubleValue;
                        double doubleValue7 = blockPos8.getZ() + 0.5 - doubleValue3;
                        double doubleValue8 = doubleValue6 * doubleValue6 + doubleValue7 * doubleValue7;
                        if (!(doubleValue8 > doubleValue4) && !(Math.abs(blockPos8.getY() + 0.5 - doubleValue2) > 4.0) && doubleValue8 < doubleValue5) {
                           doubleValue5 = doubleValue8;
                           c9 = new CacaoFarm.DataRecord2(blockPos8, blockPos7, direction2, Vec3.atCenterOf(blockPos7).relative(direction2, 0.5));
                        }
                     }
                  }
               }
            }
         }
      }

      return c9;
   }

   private boolean m_4_boolean() {
      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.COCOA_BEANS)) {
         return true;
      }

      int value5 = this.findItemSlot(Items.COCOA_BEANS);
      if (value5 == -1) {
         return false;
      }

      InventoryUtil.clickSlot(0, toContainerSlot(value5), 40, ContainerInput.SWAP);
      return MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.COCOA_BEANS);
   }

   private int findItemSlot(Item item3) {
      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).is(item3)) {
            return i;
         }
      }

      return -1;
   }

   private static int toContainerSlot(int value5) {
      if (value5 >= 0 && value5 < 9) {
         return value5 + 36;
      } else if (value5 >= 36 && value5 < 40) {
         return value5 - 31;
      } else {
         return value5 == 40 ? 45 : value5;
      }
   }

   private CacaoFarm.DataRecord m_6_C1() {
      BlockPos blockPos6 = MinecraftAccess.MINECRAFT.player.blockPosition();
      double doubleValue = MinecraftAccess.MINECRAFT.player.getX();
      double doubleValue2 = MinecraftAccess.MINECRAFT.player.getY();
      double doubleValue3 = MinecraftAccess.MINECRAFT.player.getZ();
      double doubleValue4 = 24.010000000000005;
      BlockPos blockPos7 = null;
      Direction direction2 = null;
      double doubleValue5 = Double.MAX_VALUE;

      for (int i = -3; i <= 3; i++) {
         for (int i2 = -4; i2 <= 4; i2++) {
            for (int i3 = -3; i3 <= 3; i3++) {
               BlockPos blockPos8 = blockPos6.offset(i, i2, i3);
               BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos8);
               if (blockState.getBlock() instanceof CocoaBlock && (this.blockPos5 == null || !blockPos8.equals(this.blockPos5))) {
                  int value5 = blockState.getValue(CocoaBlock.AGE);
                  if (value5 >= 2 || !blockState.canSurvive(MinecraftAccess.MINECRAFT.level, blockPos8)) {
                     double doubleValue6 = blockPos8.getX() + 0.5 - doubleValue;
                     double doubleValue7 = blockPos8.getZ() + 0.5 - doubleValue3;
                     double doubleValue8 = doubleValue6 * doubleValue6 + doubleValue7 * doubleValue7;
                     if (!(doubleValue8 > doubleValue4) && !(Math.abs(blockPos8.getY() + 0.5 - doubleValue2) > 4.0) && doubleValue8 < doubleValue5) {
                        doubleValue5 = doubleValue8;
                        blockPos7 = blockPos8.immutable();
                        direction2 = blockState.getValue(HorizontalDirectionalBlock.FACING);
                     }
                  }
               }
            }
         }
      }

      return blockPos7 == null ? null : new CacaoFarm.DataRecord(blockPos7, direction2);
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.vec3 != null && this.vec32 != null) {
         if (this.c8 == CacaoFarm.ModeKind4.FARM && this.c3 == CacaoFarm.ModeKind.IDLE) {
            Vec3 vec34 = this.flag ? this.vec32 : this.vec3;
            Vec3 vec35 = this.flag ? this.vec3 : this.vec32;
            double doubleValue = vec34.x - vec35.x;
            double doubleValue2 = vec34.z - vec35.z;
            double doubleValue3 = Math.sqrt(doubleValue * doubleValue + doubleValue2 * doubleValue2);
            if (!(doubleValue3 < 0.001)) {
               double doubleValue4 = doubleValue / doubleValue3;
               double doubleValue5 = doubleValue2 / doubleValue3;
               double doubleValue6 = -doubleValue5;
               double doubleValue7 = doubleValue4;
               Vec3 vec36 = PlayerUtil.predictLocal(1).vec3;
               double doubleValue8 = vec36.x - vec35.x;
               double doubleValue9 = vec36.z - vec35.z;
               double doubleValue10 = Mth.clamp(doubleValue8 * doubleValue4 + doubleValue9 * doubleValue5, 0.0, doubleValue3);
               double doubleValue11 = doubleValue8 * doubleValue6 + doubleValue9 * doubleValue7;
               double doubleValue12 = Math.min(doubleValue10 + 0.9, doubleValue3);
               double doubleValue13 = (this.value & 1) == 0 ? 0.3 : -0.3;
               double doubleValue14 = -doubleValue11 + doubleValue13;
               Vec3 vec37 = new Vec3(vec35.x + doubleValue4 * doubleValue12 + doubleValue6 * doubleValue14, vec35.y, vec35.z + doubleValue5 * doubleValue12 + doubleValue7 * doubleValue14);
               Vec3 vec38 = vec36.add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight(MinecraftAccess.MINECRAFT.player.getPose()), 0.0);
               Vec3 vec39 = vec37.subtract(vec38);
               float floatValue = (float)Math.toDegrees(Math.atan2(vec39.z, vec39.x)) - 90.0F;
               float floatValue2 = MinecraftAccess.MINECRAFT.player.getYRot();
               float floatValue3 = floatValue - floatValue2;
               float floatValue4 = (float)Math.toDegrees(Math.atan2(1.0, 0.0));
               float floatValue5 = Mth.wrapDegrees(floatValue4 + floatValue3);
               float floatValue6 = Math.round(floatValue5 / 45.0F) * 45.0F;
               double doubleValue15 = Math.toRadians(floatValue6);
               event.setForward(Math.round((float)Math.sin(doubleValue15)));
               event.setStrafe(Math.round((float)Math.cos(doubleValue15)));
            }
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (this.vec3 != null && this.vec32 != null) {
         Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         int value5 = ColorUtil.rgba(100, 255, 180, 160);
         int value6 = ColorUtil.rgba(100, 255, 180, 70);
         int value7 = ColorUtil.rgba(255, 255, 255, 200);
         Vec3 vec34 = event.getVec3();
         Vec3 vec35 = new Vec3(this.vec3.x, this.vec3.y + 1.2, this.vec3.z);
         Vec3 vec36 = new Vec3(this.vec32.x, this.vec32.y + 1.2, this.vec32.z);
         Line3DRenderer.drawLines(matrix4f, List.of(vec35, vec36), vec34, 3.0F, value5);
         drawMarker(matrix4f, vec35, 0.25, vec34, value6);
         drawMarker(matrix4f, vec36, 0.25, vec34, value6);
         drawMarker(matrix4f, vec35, 0.08, vec34, value7);
         drawMarker(matrix4f, vec36, 0.08, vec34, value7);
      }
   }

   private static void drawMarker(Matrix4f matrix4f, Vec3 vec34, double doubleValue, Vec3 vec35, int value5) {
      double doubleValue2 = doubleValue / 2.0;
      Box3DRenderer.drawBox(
         matrix4f,
         (float)(vec34.x - doubleValue2 - vec35.x),
         (float)(vec34.y - doubleValue2 - vec35.y),
         (float)(vec34.z - doubleValue2 - vec35.z),
         (float)(vec34.x + doubleValue2 - vec35.x),
         (float)(vec34.y + doubleValue2 - vec35.y),
         (float)(vec34.z + doubleValue2 - vec35.z),
         value5
      );
   }

   private void setRotationActive(boolean flag16) {
      if (flag16) {
         if (!Onetap.ROTATION_MODES.isFlag5()) {
            Onetap.ROTATION_MODES.setSpoofedRotation(MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot());
            this.flag2 = true;
         } else if (!this.flag2) {
            this.flag2 = true;
         }
      } else if (this.flag2) {
         this.flag2 = false;
         if (MinecraftAccess.MINECRAFT.player != null && Onetap.ROTATION_MODES.isFlag5()) {
            float floatValue = Onetap.ROTATION_MODES.getRenderYaw();
            float floatValue2 = Onetap.ROTATION_MODES.getRenderPitch();
            MinecraftAccess.MINECRAFT.player.setYRot(MinecraftAccess.MINECRAFT.player.getYRot() + Mth.wrapDegrees(floatValue - MinecraftAccess.MINECRAFT.player.getYRot()));
            MinecraftAccess.MINECRAFT.player.setXRot(floatValue2);
         }

         if (Onetap.ROTATION_MODES.isFlag5()) {
            Onetap.ROTATION_MODES.syncWithPlayer();
         }

         Onetap.ROTATION_MODES.reset();
      }
   }

   private Item getBestAxe() {
      if (this.findInventorySlot(Items.NETHERITE_AXE) != -1) {
         return Items.NETHERITE_AXE;
      } else {
         return this.findInventorySlot(Items.DIAMOND_AXE) != -1 ? Items.DIAMOND_AXE : null;
      }
   }

   private boolean m_3_boolean() {
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot());
      return (itemStack.getItem() == Items.DIAMOND_AXE || itemStack.getItem() == Items.NETHERITE_AXE) && itemStack.isDamageableItem()
         ? itemStack.getMaxDamage() - itemStack.getDamageValue() >= 500
         : true;
   }

   private boolean swapToOffhand(Item item3) {
      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() == item3) {
         return true;
      }

      int value5 = this.m_4_int(item3);
      if (value5 == -1) {
         return false;
      }

      try {
         InventoryUtil.clickSlot(0, value5, 40, ContainerInput.SWAP);
         return true;
      } catch (Exception e) {
         this.moveCarriedToHotbar();
         return false;
      }
   }

   private boolean swapToMainHand(Item item3) {
      if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()).getItem() == item3) {
         return true;
      }

      int value5 = this.m_4_int(item3);
      if (value5 == -1) {
         return false;
      }

      try {
         InventoryUtil.clickSlot(0, value5, MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot(), ContainerInput.SWAP);
         return true;
      } catch (Exception e) {
         this.moveCarriedToHotbar();
         return false;
      }
   }

   private int countExpBottles() {
      int value5 = 0;

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getItem() == Items.EXPERIENCE_BOTTLE) {
            value5++;
         }
      }

      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() == Items.EXPERIENCE_BOTTLE) {
         value5++;
      }

      return value5;
   }

   private int countItemWithOffhand(Item item3) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return 0;
      }

      int value5 = 0;

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getItem() == item3) {
            value5++;
         }
      }

      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() == item3) {
         value5++;
      }

      return value5;
   }

   private int countFreeSlots() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return 0;
      }

      int value5 = 0;

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
            value5++;
         }
      }

      return value5;
   }

   private boolean isInventoryFull() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return false;
      }

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private AbstractContainerMenu getOpenContainer() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return null;
      } else {
         AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
         if (abstractContainerMenu == null || abstractContainerMenu.containerId == 0) {
            return null;
         } else if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
            return null;
         } else {
            return abstractContainerMenu.slots.size() <= 36 ? null : abstractContainerMenu;
         }
      }
   }

   private void closeScreens() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.gui.screen().onClose();
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }
   }

   private void sendCommand(String text) {
      if (MinecraftAccess.MINECRAFT.getConnection() != null) {
         if (text.startsWith("/")) {
            text = text.substring(1);
         }

         MinecraftAccess.MINECRAFT.getConnection().sendCommand(text);
      }
   }

   private void log(String text) {
      ChatUtil.log(text);
   }

   private void moveCarriedToHotbar() {
      try {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.containerMenu.getCarried();
         if (itemStack != null && !itemStack.isEmpty()) {
            for (int i = 0; i < 9; i++) {
               if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
                  int value5 = i + 36;
                  InventoryUtil.clickSlot(0, value5, 0, ContainerInput.PICKUP);
                  return;
               }
            }

            int value6 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() + 36;
            InventoryUtil.clickSlot(0, value6, 0, ContainerInput.PICKUP);
         }
      } catch (Exception e) {
      }
   }

   private BlockPos findChest() {
      return this.findBlock(ChestBlock.class, 3);
   }

   private <T> BlockPos findBlock(Class<T> clazz, int value5) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         BlockPos blockPos6 = MinecraftAccess.MINECRAFT.player.blockPosition();

         for (int i = blockPos6.getX() - value5; i <= blockPos6.getX() + value5; i++) {
            for (int i2 = blockPos6.getY() - value5; i2 <= blockPos6.getY() + value5; i2++) {
               for (int i3 = blockPos6.getZ() - value5; i3 <= blockPos6.getZ() + value5; i3++) {
                  BlockPos blockPos7 = new BlockPos(i, i2, i3);
                  BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos7);
                  if (!blockState.isAir() && clazz.isInstance(blockState.getBlock())) {
                     return blockPos7;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private int countItemInMenu(AbstractContainerMenu abstractContainerMenu, Item item3) {
      int value5 = abstractContainerMenu.slots.size();
      int value6 = 0;

      for (int i = 0; i < value5 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (itemStack.getItem() == item3) {
            value6++;
         }
      }

      return value6;
   }

   private int findItemInMenu(AbstractContainerMenu abstractContainerMenu, Item item3) {
      int value5 = abstractContainerMenu.slots.size();

      for (int i = 0; i < value5 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (itemStack.getItem() == item3) {
            return i;
         }
      }

      return -1;
   }

   private int findSlotByName(AbstractContainerMenu abstractContainerMenu, String text) {
      int value5 = Math.max(0, abstractContainerMenu.slots.size() - 36);

      for (int i = 0; i < value5; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty()) {
            String text2 = itemStack.getHoverName().getString().toLowerCase(Locale.ROOT);
            if (text2.contains(text)) {
               return i;
            }
         }
      }

      return -1;
   }

   private int m_4_int(AbstractContainerMenu abstractContainerMenu) {
      int value5 = Math.max(0, abstractContainerMenu.slots.size() - 36);

      for (int i = 0; i < value5; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && itemStack.getItem() == Items.CACTUS) {
            return i;
         }
      }

      return this.findSlotByName(abstractContainerMenu, "кактус");
   }

   private int m_7_int(AbstractContainerMenu abstractContainerMenu) {
      int value5 = this.findItemInMenu(abstractContainerMenu, Items.COCOA_BEANS);
      return value5 != -1 ? value5 : this.findSlotByName(abstractContainerMenu, "какао");
   }

   private CacaoFarm.StateHolder m_7_C4(AbstractContainerMenu abstractContainerMenu) {
      int value5 = this.findSlotByName(abstractContainerMenu, "лимит");
      if (value5 == -1) {
         return new CacaoFarm.StateHolder(Items.COCOA_BEANS, "какао", this.sell.getInt());
      }

      ItemStack itemStack = abstractContainerMenu.getSlot(value5).getItem();
      if (itemStack.isEmpty()) {
         return null;
      }

      Item item3 = this.getFarmItem(itemStack);
      if (item3 == null) {
         return null;
      }

      int value6 = this.parseNumberFromName(itemStack);
      String text = this.m_6_String(itemStack.getHoverName().getString());
      if (text == null) {
         text = this.m_6_String(item3);
      }

      return new CacaoFarm.StateHolder(item3, text, value6);
   }

   private Item getFarmItem(ItemStack itemStack) {
      Item item3 = itemStack.getItem();
      if (item3 == Items.COCOA_BEANS) {
         return item3;
      }

      String text = itemStack.getHoverName().getString();
      return this.parseItem(text);
   }

   private Item parseItem(String text) {
      if (text == null) {
         return null;
      }

      String text2 = text.toLowerCase(Locale.ROOT);
      return text2.contains("кака") ? Items.COCOA_BEANS : null;
   }

   private String m_6_String(String text) {
      if (text == null) {
         return null;
      }

      String text2 = text.toLowerCase(Locale.ROOT);
      return text2.contains("кака") ? "какао" : null;
   }

   private String m_6_String(Item item3) {
      return item3 == Items.COCOA_BEANS ? "какао" : null;
   }

   private int parseNumberFromName(ItemStack itemStack) {
      String text = itemStack.getHoverName().getString();
      String text2 = text.replaceAll("[^0-9]", "");
      if (!text2.isEmpty()) {
         try {
            return Integer.parseInt(text2);
         } catch (NumberFormatException numberFormatException) {
         }
      }

      int value5 = itemStack.getCount();
      return value5 > 0 ? value5 : this.sell.getInt();
   }

   private int toStacks(int value5) {
      return value5 <= 0 ? 0 : (int)Math.ceil(value5 / 64.0);
   }

   private int m_7(AbstractContainerMenu abstractContainerMenu, CacaoFarm.StateHolder c4) {
      int value5 = abstractContainerMenu.slots.size();

      for (int i = 0; i < value5 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && c4.item != null && itemStack.getItem() == c4.item) {
            return i;
         }
      }

      if (c4.text != null) {
         for (int i2 = 0; i2 < value5 - 36; i2++) {
            ItemStack itemStack2 = abstractContainerMenu.getSlot(i2).getItem();
            if (!itemStack2.isEmpty()) {
               String text = itemStack2.getHoverName().getString().toLowerCase(Locale.ROOT);
               if (text.contains(c4.text)) {
                  return i2;
               }
            }
         }
      }

      return -1;
   }

   private int m_2(AbstractContainerMenu abstractContainerMenu, Item item3, int value5, boolean flag16) {
      int value6 = -1;
      double doubleValue = Double.MAX_VALUE;
      int value7 = abstractContainerMenu.slots.size();

      for (int i = 0; i < value7 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && itemStack.getItem() == item3 && (!flag16 || this.isDefaultName(itemStack))) {
            int value8 = this.getPriceOf(itemStack);
            if (value8 > 0) {
               double doubleValue2 = (double)value8 / Math.max(1, itemStack.getCount());
               if (doubleValue2 < doubleValue) {
                  doubleValue = doubleValue2;
                  value6 = i;
               }
            }
         }
      }

      if (value6 == -1) {
         return -1;
      }

      ItemStack itemStack2 = abstractContainerMenu.getSlot(value6).getItem();
      int value9 = this.getPriceOf(itemStack2);
      if (value9 <= 0) {
         return -1;
      }

      double doubleValue3 = Math.max(1, itemStack2.getCount());
      double doubleValue4 = value9 / doubleValue3;
      return doubleValue4 > value5 ? -1 : value6;
   }

   private boolean isDefaultName(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         ItemStack itemStack2 = new ItemStack(itemStack.getItem());
         return itemStack.getHoverName().getString().equals(itemStack2.getHoverName().getString());
      } else {
         return false;
      }
   }

   private int getPriceOf(ItemStack itemStack) {
      return PriceParser.getPrice(itemStack);
   }

   private void m_8_void(AbstractContainerMenu abstractContainerMenu) {
      int value5 = abstractContainerMenu.slots.size();

      for (int i = 0; i < value5 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty()) {
            String text = itemStack.getHoverName().getString();
            if ("[⟲] Обновить".equals(text) || text.contains("Обновить")) {
               InventoryUtil.clickSlot(abstractContainerMenu.containerId, i, 0, ContainerInput.PICKUP);
               return;
            }
         }
      }
   }

   private void searchAuction(String text) {
      this.sendCommand("ah search " + text);
   }

   private void quickMoveSlot(AbstractContainerMenu abstractContainerMenu, int value5) {
      if (MinecraftAccess.MINECRAFT.player.tickCount % 18 == 0) {
         InventoryUtil.clickSlot(abstractContainerMenu.containerId, value5, 0, ContainerInput.QUICK_MOVE);
      }
   }

   private int getBalance() {
      long longValue = AuctionScanner.m_5_long();
      return longValue < 0L ? -1 : (int)Math.min(2147483647L, longValue);
   }

   private int m_4_int(Item item3) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return -1;
      }

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getItem() == item3) {
            return m_9_int(i);
         }
      }

      return MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() == item3 ? 45 : -1;
   }

   private int findInventorySlot(Item item3) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return -1;
      }

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getItem() == item3) {
            if (i >= 0 && i < 9) {
               return i + 36;
            }

            return i;
         }
      }

      return MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() == item3 ? 45 : -1;
   }

   private static int m_9_int(int value5) {
      if (value5 >= 0 && value5 < 9) {
         return value5 + 36;
      } else if (value5 >= 36 && value5 < 40) {
         return value5 - 31;
      } else {
         return value5 == 40 ? 45 : value5;
      }
   }

   private static boolean hasElapsedOver(TimeHelper timeHelper11, long longValue) {
      return timeHelper11.getElapsed() > longValue;
   }

   private static boolean checkDelay(TimeHelper timeHelper11, long longValue, boolean flag16) {
      if (timeHelper11.getElapsed() > longValue) {
         if (flag16) {
            timeHelper11.reset();
         }

         return true;
      } else {
         return false;
      }
   }

   private Vec3 getLookVector(float floatValue, float floatValue2) {
      double doubleValue = Math.toRadians(floatValue);
      double doubleValue2 = Math.toRadians(floatValue2);
      double doubleValue3 = -Math.sin(doubleValue) * Math.cos(doubleValue2);
      double doubleValue4 = -Math.sin(doubleValue2);
      double doubleValue5 = Math.cos(doubleValue) * Math.cos(doubleValue2);
      return new Vec3(doubleValue3, doubleValue4, doubleValue5);
   }

   private BlockHitResult raycast(float floatValue, float floatValue2, double doubleValue) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         Vec3 vec34 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
         Vec3 vec35 = this.getLookVector(floatValue, floatValue2);
         Vec3 vec36 = vec34.add(vec35.x * doubleValue, vec35.y * doubleValue, vec35.z * doubleValue);
         return MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec34, vec36, Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player)) instanceof BlockHitResult blockHitResult
               && blockHitResult.getType() != Type.MISS
            ? blockHitResult
            : null;
      } else {
         return null;
      }
   }

   private void executeOnMainThread(BlockHitResult blockHitResult) {
      if (blockHitResult != null) {
         Runnable runnable = () -> this.interactBlock(blockHitResult);
         if (MinecraftAccess.MINECRAFT.isSameThread()) {
            runnable.run();
         } else {
            MinecraftAccess.MINECRAFT.execute(runnable);
         }
      }
   }

   private void interactBlock(BlockHitResult blockHitResult) {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.level != null) {
         if (MinecraftAccess.MINECRAFT.gui.screen() == null) {
            if (blockHitResult != null && blockHitResult.getType() != Type.MISS) {
               if (!MinecraftAccess.MINECRAFT.level.getBlockState(blockHitResult.getBlockPos()).isAir()) {
                  HitResult hitResult = MinecraftAccess.MINECRAFT.hitResult;
                  boolean flag16 = MinecraftAccess.MINECRAFT.options.keyAttack.isDown();
                  MinecraftAccess.MINECRAFT.hitResult = blockHitResult;
                  Key key = ((KeyMappingAccessor)MinecraftAccess.MINECRAFT.options.keyAttack).onetap$getKey();
                  KeyMapping.click(key);
                  MinecraftAccess.MINECRAFT.options.keyAttack.setDown(true);
                  this.flag3 = !flag16;

                  try {
                     MinecraftAccessor minecraftAccessor = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
                     boolean flag17 = minecraftAccessor.onetap$startAttack();
                     if (!flag17 && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
                        minecraftAccessor.onetap$continueAttack(true);
                     }
                  } finally {
                     if (this.flag3) {
                        MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
                        this.flag3 = false;
                        MinecraftAccessor minecraftAccessor2 = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
                        minecraftAccessor2.onetap$continueAttack(false);
                     }

                     MinecraftAccess.MINECRAFT.hitResult = hitResult;
                  }
               }
            }
         }
      }
   }

   private void stopMining() {
      if (this.flag3) {
         this.flag3 = false;
         if (MinecraftAccess.MINECRAFT.options != null) {
            MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
         }

         if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
            MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
         }
      }
   }

   static {
      m_2_void(true);
   }

   public static void m_2_void(boolean flag16) {
      flag15 = flag16;
   }

   public static boolean m_7_boolean() {
      return flag15;
   }

   public static boolean m_1_boolean() {
      return false;
   }

   private record DataRecord(BlockPos f_9i, Direction f_7g) {
      public BlockPos m_9i() {
         return this.f_9i;
      }

      public Direction m_7g() {
         return this.f_7g;
      }
   }

   private enum ModeKind {
      IDLE,
      SEND_BUYER,
      WAIT_FOR_CONTAINER,
      CLICK_CACTUS,
      WAIT_AFTER_CACTUS,
      PREPARE_COCOA,
      RIGHT_CLICK_COCOA,
      FINISH;

      private static String a(byte[] byteArray) {
         int value5 = 0;
         int value6;
         char[] charArray = new char[value6 = byteArray.length];

         for (int i = 0; i < value6; i++) {
            int value7;
            if ((value7 = 255 & byteArray[i]) < 192) {
               charArray[value5++] = (char)value7;
            } else if (value7 < 224) {
               char ch = (char)((char)(value7 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value5++] = ch;
            } else if (i < value6 - 2) {
               char ch2 = (char)((char)(value7 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value5++] = ch2;
            }
         }

         return new String(charArray, 0, value5);
      }
   }

   private static final class StateHolder {
      private final Item item;
      private final String text;
      private final int value;

      private StateHolder(Item item3, String text2, int value5) {
         this.item = item3;
         this.text = text2;
         this.value = value5;
      }
   }

   private enum ModeKind2 {
      IDLE,
      SEND_COMMAND,
      WAIT_FOR_CONTAINER,
      WAIT_BEFORE_CATEGORY,
      WAIT_AFTER_CATEGORY,
      CHECK_RESULT,
      RETRY_DELAY;

      private static String a(byte[] byteArray) {
         int value5 = 0;
         int value6;
         char[] charArray = new char[value6 = byteArray.length];

         for (int i = 0; i < value6; i++) {
            int value7;
            if ((value7 = 255 & byteArray[i]) < 192) {
               charArray[value5++] = (char)value7;
            } else if (value7 < 224) {
               char ch = (char)((char)(value7 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value5++] = ch;
            } else if (i < value6 - 2) {
               char ch2 = (char)((char)(value7 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value5++] = ch2;
            }
         }

         return new String(charArray, 0, value5);
      }
   }

   private enum ModeKind3 {
      IDLE,
      SEARCH,
      ROTATE,
      PLACE,
      BREAK,
      WAIT_CONFIRM;

      private static String a(byte[] byteArray) {
         int value5 = 0;
         int value6;
         char[] charArray = new char[value6 = byteArray.length];

         for (int i = 0; i < value6; i++) {
            int value7;
            if ((value7 = 255 & byteArray[i]) < 192) {
               charArray[value5++] = (char)value7;
            } else if (value7 < 224) {
               char ch = (char)((char)(value7 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value5++] = ch;
            } else if (i < value6 - 2) {
               char ch2 = (char)((char)(value7 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value5++] = ch2;
            }
         }

         return new String(charArray, 0, value5);
      }
   }

   private enum ModeKind4 {
      FREE,
      FARM,
      SELL,
      REPAIR,
      GET_EXP,
      AUTO_BUY_EXP,
      SWAP_ITEM,
      EAT,
      AH_SELL;

      private static String a(byte[] byteArray) {
         int value5 = 0;
         int value6;
         char[] charArray = new char[value6 = byteArray.length];

         for (int i = 0; i < value6; i++) {
            int value7;
            if ((value7 = 255 & byteArray[i]) < 192) {
               charArray[value5++] = (char)value7;
            } else if (value7 < 224) {
               char ch = (char)((char)(value7 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value5++] = ch;
            } else if (i < value6 - 2) {
               char ch2 = (char)((char)(value7 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value5++] = ch2;
            }
         }

         return new String(charArray, 0, value5);
      }
   }

   private record DataRecord2(BlockPos f_6_i, BlockPos f_6_d, Direction f_8p, Vec3 f_5a) {
      public BlockPos m_6_i() {
         return this.f_6_i;
      }

      public BlockPos m_6_d() {
         return this.f_6_d;
      }

      public Direction m_8p() {
         return this.f_8p;
      }

      public Vec3 m_5a() {
         return this.f_5a;
      }
   }
}
