package wtf.onetap.module.pve;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.AuctionScanner;
import wtf.onetap.autobuy.PriceParser;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.mixin.input.KeyMappingAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AutoFarm", description = "Автоматический фарм байера", category = Category.PVE)
public class AutoFarm extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "Продажа", "Продажа", "Прокачка").build();
   private final FloatSetting sell = new FloatSetting("Продажа", 10.0F, 3.0F, 15.0F, 1.0F).build();
   private final TextSetting pricePerExp = new TextSetting("Цена за штуку опыта", "2000").build();
   private final List<BlockPos> blockPosList = new ArrayList<>();
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private AutoFarm.ModeKind3 c4 = AutoFarm.ModeKind3.FREE;
   private AutoFarm.ModeKind3 c42 = AutoFarm.ModeKind3.FREE;
   private Item item;
   private AutoFarm.ModeKind c1 = AutoFarm.ModeKind.PLANT;
   private AutoFarm.ModeKind5 c6 = AutoFarm.ModeKind5.CARROT;
   private final List<Item> itemList = Arrays.asList(Items.CARROT, Items.POTATO, Items.SWEET_BERRIES);
   private boolean flag;
   private boolean flag2;
   private int value;
   private int value2;
   private int value3;
   private boolean flag3;
   private AutoFarm.ModeKind6 c9 = AutoFarm.ModeKind6.IDLE;
   private int value4 = -1;
   private boolean flag4;
   private AutoFarm.ModeKind2 c2 = AutoFarm.ModeKind2.IDLE;
   private boolean flag5;
   private boolean flag6;
   private boolean flag7;
   private boolean flag8;
   private Item item2;
   private int value5;
   private final TimeHelper timeHelper3 = new TimeHelper();
   private final TimeHelper timeHelper4 = new TimeHelper();
   private final TimeHelper timeHelper5 = new TimeHelper();
   private boolean flag9;
   private AutoFarm.ModeKind c12;
   private AutoFarm.ModeKind5 c62;
   private int value6;
   private AutoFarm.ModeKind4 c5 = AutoFarm.ModeKind4.IDLE;
   private int value7;
   private int value8 = -1;
   private boolean flag10;
   private boolean flag11;
   private static Module[] moduleArray;

   @Override
   public void onEnable() {
      this.flag = false;
      this.stopMining();
      this.setRotationActive(false);
      this.timeHelper3.reset();
      this.c2 = AutoFarm.ModeKind2.IDLE;
      this.flag5 = false;
      this.timeHelper4.reset();
      this.item2 = null;
      this.value5 = 0;
      this.c5 = AutoFarm.ModeKind4.IDLE;
      this.value7 = 0;
      this.value8 = -1;
      this.flag9 = false;
      this.value = 0;
      this.value2 = 0;
      this.c12 = null;
      this.c62 = null;
      this.value6 = 0;
      this.flag3 = false;
      this.c9 = AutoFarm.ModeKind6.IDLE;
      this.value4 = -1;
      this.flag4 = false;
   }

   @Override
   public void onDisable() {
      this.stopMining();
      this.setRotationActive(false);
      this.flag6 = false;
      this.flag7 = false;
      this.flag8 = false;
      this.flag2 = false;
      this.timeHelper3.reset();
      this.c2 = AutoFarm.ModeKind2.IDLE;
      this.flag5 = false;
      this.timeHelper4.reset();
      this.item2 = null;
      this.value5 = 0;
      Onetap.ROTATION_MODES.resetSmoothing();
      this.c5 = AutoFarm.ModeKind4.IDLE;
      this.value7 = 0;
      this.value8 = -1;
      this.value = 0;
      this.value2 = 0;
      this.flag9 = false;
      this.c12 = null;
      this.c62 = null;
      this.value6 = 0;
      this.flag3 = false;
      this.c9 = AutoFarm.ModeKind6.IDLE;
      this.value4 = -1;
      this.flag4 = false;
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event != null && event.getPacket() != null) {
         Packet packet = event.getPacket();
         if ((packet instanceof ClientboundLoginPacket || packet instanceof ClientboundRespawnPacket) && (this.c9 == AutoFarm.ModeKind6.SENT || this.c9 == AutoFarm.ModeKind6.HUB)) {
            this.value4 = -1;
            this.flag4 = false;
         }

         String text = getChatText(packet);
         if (text != null && !text.isEmpty()) {
            String text2 = text.toLowerCase(Locale.ROOT).replace('ё', 'е');
            if (text2.contains("данная команда недоступна в режиме afk")) {
               this.flag3 = true;
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.flag3) {
         this.flag3 = false;
         this.m_7_void();
      }

      this.m_2a_void();
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (!this.flag) {
            this.m_2_n_void();
            this.flag = true;
            this.m_4_void();
         } else if (!this.m_6_boolean()) {
            if (this.c9 != AutoFarm.ModeKind6.SENT && this.c9 != AutoFarm.ModeKind6.HUB) {
               this.m_2d_void();
               switch (this.c4.ordinal()) {
                  case 1:
                     this.m_2_o_void();
                     break;
                  case 2:
                     this.m_2s_void();
                  case 3:
                  case 7:
                  default:
                     break;
                  case 4:
                     this.m_2g_void();
                     break;
                  case 5:
                     this.m_2k_void();
                     break;
                  case 6:
                     this.m_2_l_void();
                     break;
                  case 8:
                     this.m_27_void();
               }
            }
         }
      }
   }

   private void m_7_void() {
      this.flag2 = false;
      this.c5 = AutoFarm.ModeKind4.IDLE;
      this.value7 = 0;
      this.value8 = -1;
      this.stopRotation();
      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         this.closeScreens();
      }

      Onetap.COMMAND_MANAGER.m_9_boolean(".rct");
      this.c9 = AutoFarm.ModeKind6.SENT;
      this.value4 = -1;
      this.flag4 = false;
      this.c4 = AutoFarm.ModeKind3.FARM;
   }

   private void m_2a_void() {
      if (this.c9 == AutoFarm.ModeKind6.SENT || this.c9 == AutoFarm.ModeKind6.HUB) {
         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
            int value9 = System.identityHashCode(MinecraftAccess.MINECRAFT.level);
            if (value9 != this.value4) {
               this.value4 = value9;
               this.flag4 = false;
            }

            if (!this.flag4) {
               this.flag4 = true;
               this.m_3_void();
            }
         } else {
            this.flag4 = false;
         }
      }
   }

   private void m_3_void() {
      this.stopRotation();
      if (this.c9 == AutoFarm.ModeKind6.SENT) {
         this.c9 = AutoFarm.ModeKind6.HUB;
      } else {
         if (this.c9 == AutoFarm.ModeKind6.HUB) {
            this.c9 = AutoFarm.ModeKind6.SECOND_WORLD;
            this.flag2 = false;
            this.blockPosList.clear();
            this.blockPos = null;
            this.refreshFarmland();
            this.m_4_void();
         }
      }
   }

   private void stopRotation() {
      this.stopMining();
      this.flag10 = false;
      if (Onetap.ROTATION_MODES.isFlag5()) {
         Onetap.ROTATION_MODES.syncWithPlayer();
      }

      Onetap.ROTATION_MODES.reset();
   }

   private void m_4_void() {
      this.value7 = 0;
      this.value8 = -1;
      this.timeHelper5.reset();
      this.c5 = AutoFarm.ModeKind4.SEND_BUYER;
   }

   private boolean m_6_boolean() {
      if (this.c5 == AutoFarm.ModeKind4.IDLE) {
         return false;
      }

      AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
      switch (this.c5.ordinal()) {
         case 1:
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.closeScreens();
               return true;
            }

            this.sendCommand("buyer");
            this.timeHelper5.reset();
            this.c5 = AutoFarm.ModeKind4.WAIT_FOR_CONTAINER;
            return true;
         case 2:
            if (abstractContainerMenu != null) {
               this.timeHelper5.reset();
               this.c5 = AutoFarm.ModeKind4.CLICK_FOOD;
            } else if (hasElapsedOver(this.timeHelper5, 3000L)) {
               this.c5 = AutoFarm.ModeKind4.SEND_BUYER;
               this.timeHelper5.reset();
            }

            return true;
         case 3:
            if (abstractContainerMenu == null) {
               this.c5 = AutoFarm.ModeKind4.WAIT_FOR_CONTAINER;
               return true;
            } else {
               if (!hasElapsedOver(this.timeHelper5, 500L)) {
                  return true;
               }

               int value9 = this.m_8_int(abstractContainerMenu);
               if (value9 != -1) {
                  InventoryUtil.clickSlot(abstractContainerMenu.containerId, value9, 0, ContainerInput.PICKUP);
                  this.moveCarriedToHotbar();
                  this.timeHelper5.reset();
                  this.c5 = AutoFarm.ModeKind4.WAIT_AFTER_FOOD;
               } else if (hasElapsedOver(this.timeHelper5, 2000L)) {
                  this.c5 = AutoFarm.ModeKind4.FINISH;
               }

               return true;
            }
         case 4:
            if (abstractContainerMenu == null) {
               this.c5 = AutoFarm.ModeKind4.WAIT_FOR_CONTAINER;
               return true;
            }

            if (hasElapsedOver(this.timeHelper5, 1000L)) {
               this.c5 = AutoFarm.ModeKind4.PREPARE_CARROT;
               this.timeHelper5.reset();
            }

            return true;
         case 5:
            if (abstractContainerMenu == null) {
               this.c5 = AutoFarm.ModeKind4.WAIT_FOR_CONTAINER;
               return true;
            }

            this.value8 = this.m_6_int(abstractContainerMenu);
            if (this.value8 != -1) {
               this.value7 = 0;
               this.timeHelper5.reset();
               this.c5 = AutoFarm.ModeKind4.RIGHT_CLICK_CARROT;
            } else if (hasElapsedOver(this.timeHelper5, 2000L)) {
               this.c5 = AutoFarm.ModeKind4.FINISH;
            }

            return true;
         case 6:
            if (abstractContainerMenu == null) {
               this.c5 = AutoFarm.ModeKind4.WAIT_FOR_CONTAINER;
               return true;
            } else {
               if (this.value8 == -1) {
                  this.value8 = this.m_6_int(abstractContainerMenu);
                  this.timeHelper5.reset();
                  return true;
               }

               if (hasElapsedOver(this.timeHelper5, 750L)) {
                  InventoryUtil.clickSlot(abstractContainerMenu.containerId, this.value8, 1, ContainerInput.PICKUP);
                  this.moveCarriedToHotbar();
                  this.value7++;
                  this.timeHelper5.reset();
                  if (this.value7 >= 3) {
                     this.c5 = AutoFarm.ModeKind4.FINISH;
                  }
               }

               return true;
            }
         case 7:
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.closeScreens();
            }

            this.c5 = AutoFarm.ModeKind4.IDLE;
            this.value7 = 0;
            this.value8 = -1;
            this.refreshFarmland();
            this.flag2 = false;
            this.c4 = AutoFarm.ModeKind3.FARM;
            if (this.c9 == AutoFarm.ModeKind6.SECOND_WORLD) {
               this.c9 = AutoFarm.ModeKind6.IDLE;
            }

            this.stopRotation();
            return false;
         default:
            this.c5 = AutoFarm.ModeKind4.IDLE;
            this.value7 = 0;
            this.value8 = -1;
            return false;
      }
   }

   private void m_2_n_void() {
      this.timeHelper.reset();
      this.c1 = AutoFarm.ModeKind.PLANT;
      this.c4 = AutoFarm.ModeKind3.FARM;
      this.c6 = AutoFarm.ModeKind5.CARROT;
      this.blockPos = null;
      this.blockPosList.clear();
      this.value3 = 0;
      this.blockPos2 = null;
      this.item = null;
      this.flag2 = false;
      this.timeHelper2.reset();
      this.flag6 = false;
      this.flag7 = false;
      this.flag8 = false;
      this.timeHelper3.reset();
      this.c2 = AutoFarm.ModeKind2.IDLE;
      this.flag5 = false;
      this.timeHelper4.reset();
      this.item2 = null;
      this.value5 = 0;
      Onetap.ROTATION_MODES.resetSmoothing();
      this.c5 = AutoFarm.ModeKind4.IDLE;
      this.value7 = 0;
      this.value8 = -1;
      this.refreshFarmland();
      this.flag9 = false;
      this.c12 = null;
      this.c62 = null;
      this.value6 = 0;
      this.flag3 = false;
      this.c9 = AutoFarm.ModeKind6.IDLE;
      this.value4 = -1;
      this.flag4 = false;
   }

   private void m_2d_void() {
      this.setRotationActive(this.c4 == AutoFarm.ModeKind3.FARM || this.c4 == AutoFarm.ModeKind3.GET_EXP || this.c4 == AutoFarm.ModeKind3.REPAIR);
      if (this.c4 == AutoFarm.ModeKind3.FARM && !this.m_9_boolean() && this.c4 != AutoFarm.ModeKind3.SELL && this.c4 != AutoFarm.ModeKind3.SWAP_ITEM) {
         this.c4 = AutoFarm.ModeKind3.REPAIR;
      }

      if (this.c4 == AutoFarm.ModeKind3.REPAIR) {
         Rotations rotations = Onetap.ROTATION_MODES.toRotations();
         Onetap.ROTATION_MODES.lookAtAngles(rotations.getFloatValue(), 90.0F, 10);
         if (this.countItemWithOffhand(Items.EXPERIENCE_BOTTLE) <= 0 && this.c4 != AutoFarm.ModeKind3.SELL && this.c4 != AutoFarm.ModeKind3.SWAP_ITEM) {
            this.c4 = AutoFarm.ModeKind3.GET_EXP;
         }
      }

      if ((this.c4 == AutoFarm.ModeKind3.AUTO_BUY_EXP || this.c4 == AutoFarm.ModeKind3.GET_EXP) && this.isInventoryFull()) {
         this.c4 = AutoFarm.ModeKind3.SELL;
      }

      this.m_2_void();
   }

   private void m_2_void() {
      if (this.c4 != AutoFarm.ModeKind3.SWAP_ITEM) {
         for (Item item3 : this.itemList) {
            if (this.countItemWithOffhand(item3) >= this.sell.getInt()) {
               this.startSelling(item3);
               if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() != this.item) {
                  this.startSwapItem();
               }

               return;
            }
         }

         if (this.isInventoryFull()) {
            HashMap map = new HashMap();

            for (Item item4 : this.itemList) {
               map.put(item4, this.countItemWithOffhand(item4));
            }

            int value9 = 0;

            for (Entry entry : (Iterable<Entry>)map.entrySet()) {
               if ((Integer)entry.getValue() > value9) {
                  value9 = (Integer)entry.getValue();
                  this.item = (Item)entry.getKey();
               }
            }

            if (this.item != null) {
               this.startSelling(this.item);
            }

            if (this.item != null && MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() != this.item) {
               this.startSwapItem();
            }
         }
      }
   }

   private void startSwapItem() {
      this.c42 = this.c4;
      this.c4 = AutoFarm.ModeKind3.SWAP_ITEM;
      this.timeHelper.reset();
   }

   private void resetState() {
      this.c2 = AutoFarm.ModeKind2.IDLE;
      this.flag5 = false;
      this.timeHelper3.reset();
      this.item2 = null;
      this.value5 = 0;
   }

   private void startSelling(Item item3) {
      boolean flag12 = this.item != item3;
      if (this.c4 == AutoFarm.ModeKind3.SELL && !flag12) {
         this.item = item3;
      } else {
         this.item = item3;
         this.c4 = AutoFarm.ModeKind3.SELL;
         this.resetState();
      }
   }

   private void m_27_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         this.closeScreens();
      } else {
         long longValue = 400L;
         if (checkDelay(this.timeHelper, longValue, true)) {
            if (this.c42 == AutoFarm.ModeKind3.REPAIR) {
               if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() != Items.EXPERIENCE_BOTTLE) {
                  if (!this.swapToMainHand(Items.EXPERIENCE_BOTTLE)) {
                     this.c4 = AutoFarm.ModeKind3.GET_EXP;
                  }
               } else {
                  Item item3 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
                  if (item3 != Items.NETHERITE_HOE && item3 != Items.DIAMOND_HOE) {
                     Item item4 = this.getBestHoe();
                     if (item4 == null || !this.swapToOffhand(item4)) {
                        this.c4 = AutoFarm.ModeKind3.FARM;
                     }
                  } else {
                     this.c4 = AutoFarm.ModeKind3.REPAIR;
                  }
               }
            } else if (this.c42 == AutoFarm.ModeKind3.FARM) {
               Item item5 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()).getItem();
               if (item5 != Items.NETHERITE_HOE && item5 != Items.DIAMOND_HOE) {
                  Item item6 = this.getBestHoe();
                  if (item6 != null) {
                     this.swapToMainHand(item6);
                  }
               } else {
                  Item item7 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
                  if (!this.itemList.contains(item7)) {
                     this.swapToOffhand(this.c6.item);
                  } else if (item7 != this.c6.item) {
                     this.swapToOffhand(this.c6.item);
                  } else {
                     this.c4 = AutoFarm.ModeKind3.FARM;
                  }
               }
            } else {
               if (this.c42 == AutoFarm.ModeKind3.SELL) {
                  Item item8 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
                  if (item8 != this.item) {
                     this.swapToOffhand(this.item);
                     return;
                  }

                  this.c4 = AutoFarm.ModeKind3.SELL;
               }
            }
         }
      }
   }

   private boolean m_5_boolean() {
      if (this.blockPosList.isEmpty()) {
         this.refreshFarmland();
         if (this.blockPosList.isEmpty()) {
            return false;
         }
      }

      Item item3 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()).getItem();
      Item item4 = MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem();
      if ((item3 == Items.NETHERITE_HOE || item3 == Items.DIAMOND_HOE) && this.itemList.contains(item4) && item4 != Items.SWEET_BERRIES) {
         return true;
      }

      Item item5 = this.getBestHoe();
      if (item5 == null && item3 != Items.NETHERITE_HOE && item3 != Items.DIAMOND_HOE) {
         return false;
      }

      this.startSwapItem();
      return false;
   }

   private void m_2_o_void() {
      if (this.m_5_boolean()) {
         if (((String)this.mode.getValue()).equals("Продажа")) {
            if (!this.flag2) {
               this.value = 0;
               this.value2 = 0;
               this.flag2 = true;
            }

            this.m_2_e_void();
         } else {
            if (this.c1 == AutoFarm.ModeKind.PLANT) {
               if (!this.flag2) {
                  this.value = 0;
                  this.value2 = 0;
                  this.flag2 = true;
               }

               this.m_2_e_void();
            } else if (this.c1 == AutoFarm.ModeKind.SWEET_BERRIES) {
               if (this.blockPos == null) {
                  this.refreshFarmland();
               }

               if (this.blockPos != null) {
                  Onetap.ROTATION_MODES.lookAtHumanized(this.blockPos, 10);
                  Rotations rotations = Onetap.ROTATION_MODES.toRotations();
                  BlockHitResult blockHitResult = this.raycast(rotations.getFloatValue(), rotations.getFloatValue2(), 4.0);
                  if (blockHitResult != null && checkDelay(this.timeHelper2, 10L, true)) {
                     MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
                  }
               } else {
                  this.c1 = AutoFarm.ModeKind.PLANT;
               }
            }

            this.m_2_t_void();
         }
      }
   }

   private void m_2_e_void() {
      if (this.isEnabled()
         && this.c4 == AutoFarm.ModeKind3.FARM
         && (!((String)this.mode.getValue()).equals("Прокачка") || this.c1 == AutoFarm.ModeKind.PLANT)
         && !this.blockPosList.isEmpty()) {
         if (this.value >= this.blockPosList.size()) {
            this.value = 0;
         }

         this.blockPos2 = this.blockPosList.get(this.value);
         Onetap.ROTATION_MODES.lookAtHumanized(this.blockPos2, 10);
         Rotations rotations = Onetap.ROTATION_MODES.toRotations();
         if (checkDelay(this.timeHelper2, 52L, false)) {
            BlockHitResult blockHitResult = this.raycast(rotations.getFloatValue(), rotations.getFloatValue2(), 4.0);
            if (blockHitResult != null) {
               this.timeHelper2.reset();
               if (this.value2 == 0) {
                  MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.OFF_HAND, blockHitResult);
               } else if (this.value2 < 6) {
                  MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
               } else {
                  BlockPos blockPos3 = new BlockPos(this.blockPos2.getX(), this.blockPos2.getY() + 1, this.blockPos2.getZ());
                  BlockHitResult blockHitResult2 = this.resolveHit(blockHitResult, blockPos3, rotations);
                  this.executeOnMainThread(blockHitResult2);
               }

               this.value++;
               if (this.value == 4) {
                  this.value = 0;
                  this.value2++;
               }

               if (this.value2 >= 7) {
                  this.flag2 = false;
               }
            }
         }
      } else {
         this.flag2 = false;
      }
   }

   private void m_2_t_void() {
      if (MinecraftAccess.MINECRAFT.player.tickCount % 50 == 0) {
         this.value3++;
      }

      if (this.value3 < 20) {
         this.c1 = AutoFarm.ModeKind.PLANT;
         this.c6 = AutoFarm.ModeKind5.CARROT;
      } else if (this.value3 < 33) {
         if (this.c6 == AutoFarm.ModeKind5.CARROT) {
            this.startSwapItem();
         }

         this.c1 = AutoFarm.ModeKind.PLANT;
         this.c6 = AutoFarm.ModeKind5.POTATO;
      } else if (this.value3 < 39) {
         this.c1 = AutoFarm.ModeKind.SWEET_BERRIES;
      } else {
         this.value3 = 0;
         this.c1 = AutoFarm.ModeKind.PLANT;
         this.c6 = AutoFarm.ModeKind5.CARROT;
         this.startSwapItem();
      }
   }

   private void m_2s_void() {
      if (this.item == null) {
         this.c4 = AutoFarm.ModeKind3.FARM;
      } else {
         if (this.c2 == AutoFarm.ModeKind2.IDLE) {
            this.c2 = AutoFarm.ModeKind2.SEND_COMMAND;
            this.flag5 = false;
         }

         AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
         switch (this.c2.ordinal()) {
            case 1:
               if (!this.flag5) {
                  this.sendCommand("buyer");
                  this.flag5 = true;
                  this.timeHelper3.reset();
               }

               if (hasElapsedOver(this.timeHelper3, 500L)) {
                  this.c2 = AutoFarm.ModeKind2.WAIT_FOR_CONTAINER;
                  this.timeHelper3.reset();
               }
               break;
            case 2:
               if (abstractContainerMenu != null) {
                  this.c2 = AutoFarm.ModeKind2.WAIT_BEFORE_CATEGORY;
                  this.timeHelper3.reset();
                  return;
               }

               if (hasElapsedOver(this.timeHelper3, 500L)) {
                  this.c2 = AutoFarm.ModeKind2.RETRY_DELAY;
                  this.timeHelper3.reset();
               }
               break;
            case 3:
               if (abstractContainerMenu == null) {
                  if (hasElapsedOver(this.timeHelper3, 500L)) {
                     this.c2 = AutoFarm.ModeKind2.RETRY_DELAY;
                     this.timeHelper3.reset();
                  }

                  return;
               }

               if (!hasElapsedOver(this.timeHelper3, 500L)) {
                  return;
               }

               int value9 = this.m_8_int(abstractContainerMenu);
               if (value9 == -1) {
                  if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                     this.closeScreens();
                  }

                  this.c2 = AutoFarm.ModeKind2.RETRY_DELAY;
                  this.timeHelper3.reset();
                  return;
               }

               InventoryUtil.clickSlot(abstractContainerMenu.containerId, value9, 0, ContainerInput.PICKUP);
               this.c2 = AutoFarm.ModeKind2.WAIT_AFTER_CATEGORY;
               this.timeHelper3.reset();
               break;
            case 4:
               if (abstractContainerMenu == null) {
                  if (hasElapsedOver(this.timeHelper3, 2500L)) {
                     this.c2 = AutoFarm.ModeKind2.RETRY_DELAY;
                     this.timeHelper3.reset();
                  }

                  return;
               }

               if (!hasElapsedOver(this.timeHelper3, 1000L)) {
                  return;
               }

               AutoFarm.StateHolder c8 = this.m_4_C8(abstractContainerMenu);
               if (c8 == null) {
                  if (hasElapsedOver(this.timeHelper3, 4000L)) {
                     if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                        this.closeScreens();
                     }

                     this.c2 = AutoFarm.ModeKind2.RETRY_DELAY;
                     this.timeHelper3.reset();
                  }

                  return;
               }

               this.item2 = c8.item;
               this.value5 = c8.value;
               this.item = this.item2;
               int value10 = this.m_9(abstractContainerMenu, c8);
               if (value10 == -1) {
                  if (hasElapsedOver(this.timeHelper3, 4000L)) {
                     if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                        this.closeScreens();
                     }

                     this.c2 = AutoFarm.ModeKind2.RETRY_DELAY;
                     this.timeHelper3.reset();
                  }

                  return;
               }

               InventoryUtil.clickSlot(abstractContainerMenu.containerId, value10, 0, ContainerInput.QUICK_MOVE);
               this.moveCarriedToHotbar();
               this.c2 = AutoFarm.ModeKind2.CHECK_RESULT;
               this.timeHelper3.reset();
               break;
            case 5:
               if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  if (hasElapsedOver(this.timeHelper3, 1500L)) {
                     this.closeScreens();
                  }

                  return;
               }

               if (!hasElapsedOver(this.timeHelper3, 500L)) {
                  return;
               }

               if (this.item2 == null) {
                  this.c2 = AutoFarm.ModeKind2.RETRY_DELAY;
                  this.timeHelper3.reset();
                  return;
               }

               int value11 = this.countItemWithOffhand(this.item2);
               int value12 = this.value5 > 0 ? this.value5 : this.sell.getInt();
               int value13 = this.toStacks(value11);
               boolean flag12 = value11 < value12 || value13 < value12;
               if (flag12) {
                  this.c4 = AutoFarm.ModeKind3.FARM;
                  this.item = null;
                  this.resetState();
               } else {
                  this.c2 = AutoFarm.ModeKind2.RETRY_DELAY;
                  this.flag5 = false;
                  this.timeHelper3.reset();
               }
               break;
            case 6:
               if (!hasElapsedOver(this.timeHelper3, 1500L)) {
                  return;
               }

               if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  this.closeScreens();
               }

               this.flag5 = false;
               this.c2 = AutoFarm.ModeKind2.SEND_COMMAND;
               this.timeHelper3.reset();
         }
      }
   }

   private void m_2g_void() {
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getOffhandItem();
      if (itemStack.getItem() != Items.DIAMOND_HOE && itemStack.getItem() != Items.NETHERITE_HOE) {
         this.startSwapItem();
      } else if (itemStack.isDamageableItem()) {
         if (itemStack.getMaxDamage() - itemStack.getDamageValue() == itemStack.getMaxDamage()) {
            this.c4 = AutoFarm.ModeKind3.FARM;
         } else if (this.countItemWithOffhand(Items.EXPERIENCE_BOTTLE) <= 0) {
            this.c4 = AutoFarm.ModeKind3.GET_EXP;
         } else {
            long longValue = 50L;
            if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() != Items.EXPERIENCE_BOTTLE) {
               longValue = 200L;
            }

            if (checkDelay(this.timeHelper, longValue, true)) {
               if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() != Items.EXPERIENCE_BOTTLE) {
                  this.swapToMainHand(Items.EXPERIENCE_BOTTLE);
               } else {
                  MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
               }
            }
         }
      }
   }

   private int m_8_int(AbstractContainerMenu abstractContainerMenu) {
      int value9 = Math.max(0, abstractContainerMenu.slots.size() - 36);

      for (int i = 0; i < value9; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && itemStack.getItem() == Items.CARROT) {
            String text = itemStack.getHoverName().getString().toLowerCase(Locale.ROOT).replace('ё', 'е');
            if (text.contains("еда")) {
               return i;
            }
         }
      }

      for (int i2 = 0; i2 < value9; i2++) {
         ItemStack itemStack2 = abstractContainerMenu.getSlot(i2).getItem();
         if (!itemStack2.isEmpty()) {
            String text2 = itemStack2.getHoverName().getString().toLowerCase(Locale.ROOT).replace('ё', 'е');
            if (text2.contains("еда") && (itemStack2.getItem() == Items.CARROT || text2.contains("морков"))) {
               return i2;
            }
         }
      }

      return this.findSlotByName(abstractContainerMenu, "еда");
   }

   private int m_6_int(AbstractContainerMenu abstractContainerMenu) {
      int value9 = this.findItemInMenu(abstractContainerMenu, Items.CARROT);
      return value9 != -1 ? value9 : this.findSlotByName(abstractContainerMenu, "морков");
   }

   private AutoFarm.StateHolder m_4_C8(AbstractContainerMenu abstractContainerMenu) {
      int value9 = this.findSlotByName(abstractContainerMenu, "лимит");
      if (value9 == -1) {
         if (this.item == null) {
            return null;
         }

         String text = this.getItemKeyword(this.item);
         return new AutoFarm.StateHolder(this.item, text, this.sell.getInt());
      } else {
         ItemStack itemStack = abstractContainerMenu.getSlot(value9).getItem();
         if (itemStack.isEmpty()) {
            return null;
         }

         Item item3 = this.getFarmItem(itemStack);
         if (item3 == null) {
            return null;
         }

         int value10 = this.parseNumberFromName(itemStack);
         String text2 = this.m_7_String(itemStack.getHoverName().getString());
         if (text2 == null) {
            text2 = this.getItemKeyword(item3);
         }

         return new AutoFarm.StateHolder(item3, text2, value10);
      }
   }

   private Item getFarmItem(ItemStack itemStack) {
      Item item3 = itemStack.getItem();
      if (this.itemList.contains(item3)) {
         return item3;
      }

      String text = itemStack.getHoverName().getString();
      return this.m_5_Item(text);
   }

   private Item m_5_Item(String text) {
      if (text == null) {
         return null;
      } else {
         String text2 = text.toLowerCase(Locale.ROOT);
         if (text2.contains("морков")) {
            return Items.CARROT;
         } else if (text2.contains("картоф")) {
            return Items.POTATO;
         } else {
            return text2.contains("ягод") ? Items.SWEET_BERRIES : null;
         }
      }
   }

   private String m_7_String(String text) {
      if (text == null) {
         return null;
      } else {
         String text2 = text.toLowerCase(Locale.ROOT);
         if (text2.contains("морков")) {
            return "морков";
         } else if (text2.contains("картоф")) {
            return "картоф";
         } else {
            return text2.contains("ягод") ? "ягод" : null;
         }
      }
   }

   private String getItemKeyword(Item item3) {
      if (item3 == Items.CARROT) {
         return "морков";
      } else if (item3 == Items.POTATO) {
         return "картоф";
      } else {
         return item3 == Items.SWEET_BERRIES ? "ягод" : null;
      }
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

      int value9 = itemStack.getCount();
      return value9 > 0 ? value9 : this.sell.getInt();
   }

   private int toStacks(int value9) {
      return value9 <= 0 ? 0 : (int)Math.ceil(value9 / 64.0);
   }

   private int findSlotByName(AbstractContainerMenu abstractContainerMenu, String text) {
      int value9 = Math.max(0, abstractContainerMenu.slots.size() - 36);

      for (int i = 0; i < value9; i++) {
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

   private int m_9(AbstractContainerMenu abstractContainerMenu, AutoFarm.StateHolder c8) {
      int value9 = abstractContainerMenu.slots.size();

      for (int i = 0; i < value9 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && c8.item != null && itemStack.getItem() == c8.item) {
            return i;
         }
      }

      if (c8.text != null) {
         for (int i2 = 0; i2 < value9 - 36; i2++) {
            ItemStack itemStack2 = abstractContainerMenu.getSlot(i2).getItem();
            if (!itemStack2.isEmpty()) {
               String text = itemStack2.getHoverName().getString().toLowerCase(Locale.ROOT);
               if (text.contains(c8.text)) {
                  return i2;
               }
            }
         }
      }

      return -1;
   }

   private void m_2k_void() {
      BlockPos blockPos3 = this.findChest();
      if (blockPos3 != null) {
         AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
         if (abstractContainerMenu != null) {
            if (this.countItemInMenu(abstractContainerMenu, Items.EXPERIENCE_BOTTLE) <= 0 && MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.c4 = AutoFarm.ModeKind3.AUTO_BUY_EXP;
               this.closeScreens();
            }

            if (this.isInventoryFull() && this.countFreeSlots() < 5 && MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.c4 = AutoFarm.ModeKind3.SELL;
               this.closeScreens();
            }

            if (this.countExpBottles() < 5) {
               if (this.countItemInMenu(abstractContainerMenu, Items.EXPERIENCE_BOTTLE) > 0) {
                  if (checkDelay(this.timeHelper, 500L, true)) {
                     int value9 = this.findItemInMenu(abstractContainerMenu, Items.EXPERIENCE_BOTTLE);
                     if (value9 != -1) {
                        InventoryUtil.clickSlot(abstractContainerMenu.containerId, value9, 0, ContainerInput.QUICK_MOVE);
                        this.moveCarriedToHotbar();
                     }
                  }
               } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  this.c4 = AutoFarm.ModeKind3.AUTO_BUY_EXP;
                  this.closeScreens();
               }
            } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.c4 = AutoFarm.ModeKind3.FARM;
               this.closeScreens();
            }
         } else if (checkDelay(this.timeHelper2, 52L, true)) {
            Onetap.ROTATION_MODES.lookAt(blockPos3, 10);
            Rotations rotations = Onetap.ROTATION_MODES.toRotations();
            BlockHitResult blockHitResult = this.raycast(rotations.getFloatValue(), rotations.getFloatValue2(), 4.0);
            if (blockHitResult != null) {
               MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
            }
         }
      } else {
         this.c4 = AutoFarm.ModeKind3.AUTO_BUY_EXP;
      }
   }

   private void m_2_l_void() {
      if (this.flag8 && MinecraftAccess.MINECRAFT.gui.screen() == null && hasElapsedOver(this.timeHelper4, 800L)) {
         this.flag8 = false;
         this.c4 = AutoFarm.ModeKind3.FARM;
      } else if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
         if (MinecraftAccess.MINECRAFT.player.tickCount % 10 == 0 && !this.flag8) {
            this.flag6 = true;
            this.flag8 = false;
            this.flag7 = true;
            this.searchAuction("Пузырёк опыта");
         }
      } else if (this.flag8) {
         this.closeScreens();
      } else {
         AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
         int value9 = this.pricePerExp.getInt();
         if (value9 <= 0) {
            value9 = 2000;
         }

         int value10 = this.getBalance();
         int value11 = this.m_2(abstractContainerMenu, Items.EXPERIENCE_BOTTLE, value9, true);
         if (value11 != -1) {
            int value12 = this.getPriceOf(abstractContainerMenu.getSlot(value11).getItem());
            if (value12 > 0 && value10 >= 0 && value10 < value12) {
               this.flag8 = true;
               this.timeHelper4.reset();
            }

            if (this.flag7) {
               if (checkDelay(this.timeHelper, 1000L, true)) {
                  this.flag7 = false;
               }
            } else {
               int value13 = Math.min(8, this.countFreeSlots() - 1);
               if (this.countExpBottles() < value13) {
                  this.quickMoveSlot(abstractContainerMenu, value11);
               } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  this.flag8 = true;
                  this.timeHelper4.reset();
               }
            }
         } else if (value10 >= 0 && value10 < value9 * 64L) {
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               if (this.countExpBottles() <= 0) {
                  this.c4 = AutoFarm.ModeKind3.FARM;
                  this.closeScreens();
                  this.log("У вас закончились деньги и пузырьки опыта!");
                  this.setEnabled(false);
               } else {
                  this.flag8 = true;
                  this.timeHelper4.reset();
               }
            }
         } else if (this.flag6) {
            if (checkDelay(this.timeHelper, 1050L, true)) {
               this.flag6 = false;
            }
         } else {
            this.m_1_void(abstractContainerMenu);
            this.flag6 = true;
         }
      }
   }

   private boolean m_9_boolean() {
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot());
      return (itemStack.getItem() == Items.DIAMOND_HOE || itemStack.getItem() == Items.NETHERITE_HOE) && itemStack.isDamageableItem()
         ? itemStack.getMaxDamage() - itemStack.getDamageValue() >= 500
         : true;
   }

   private int countExpBottles() {
      int value9 = 0;

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getItem() == Items.EXPERIENCE_BOTTLE) {
            value9++;
         }
      }

      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() == Items.EXPERIENCE_BOTTLE) {
         value9++;
      }

      return value9;
   }

   private Item getBestHoe() {
      if (this.findInventorySlot(Items.NETHERITE_HOE) != -1) {
         return Items.NETHERITE_HOE;
      } else {
         return this.findInventorySlot(Items.DIAMOND_HOE) != -1 ? Items.DIAMOND_HOE : null;
      }
   }

   private boolean swapToOffhand(Item item3) {
      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() == item3) {
         return true;
      }

      int value9 = this.findItemSlot(item3);
      if (value9 == -1) {
         return false;
      }

      try {
         InventoryUtil.clickSlot(0, value9, 40, ContainerInput.SWAP);
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

      int value9 = this.findItemSlot(item3);
      if (value9 == -1) {
         return false;
      }

      try {
         InventoryUtil.clickSlot(0, value9, MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot(), ContainerInput.SWAP);
         return true;
      } catch (Exception e) {
         this.moveCarriedToHotbar();
         return false;
      }
   }

   private boolean refreshFarmland() {
      this.blockPosList.clear();
      this.blockPosList.addAll(this.m_4(FarmlandBlock.class, 3, 4));
      this.blockPos = this.findBlock(SweetBerryBushBlock.class, 3);
      return this.blockPos == null && ((String)this.mode.getValue()).equals("Прокачка") ? false : !this.blockPosList.isEmpty();
   }

   private BlockPos findChest() {
      return this.findBlock(ChestBlock.class, 3);
   }

   private void moveCarriedToHotbar() {
      try {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.containerMenu.getCarried();
         if (itemStack != null && !itemStack.isEmpty()) {
            for (int i = 0; i < 9; i++) {
               if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
                  int value9 = i + 36;
                  InventoryUtil.clickSlot(0, value9, 0, ContainerInput.PICKUP);
                  return;
               }
            }

            int value10 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() + 36;
            InventoryUtil.clickSlot(0, value10, 0, ContainerInput.PICKUP);
         }
      } catch (Exception e) {
      }
   }

   private void m_2z_void() {
      if (!this.flag9) {
         this.c12 = this.c1;
         this.c62 = this.c6;
         this.value6 = this.value3;
         this.flag9 = true;
      }
   }

   private void m_2p_void() {
      if (this.flag9) {
         if (this.c12 != null) {
            this.c1 = this.c12;
         }

         if (this.c62 != null) {
            this.c6 = this.c62;
         }

         this.value3 = this.value6;
         this.flag9 = false;
         this.c12 = null;
         this.c62 = null;
         this.value6 = 0;
      }
   }

   private void setRotationActive(boolean flag12) {
      if (flag12) {
         if (!Onetap.ROTATION_MODES.isFlag5()) {
            Onetap.ROTATION_MODES.setSpoofedRotation(MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot());
            this.flag10 = true;
         } else if (!this.flag10) {
            this.flag10 = true;
         }
      } else if (this.flag10) {
         this.flag10 = false;
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
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
         Vec3 vec32 = this.getLookVector(floatValue, floatValue2);
         Vec3 vec33 = vec3.add(vec32.x * doubleValue, vec32.y * doubleValue, vec32.z * doubleValue);
         return MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec3, vec33, Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player)) instanceof BlockHitResult blockHitResult
               && blockHitResult.getType() != Type.MISS
            ? blockHitResult
            : null;
      } else {
         return null;
      }
   }

   private int countItemInMenu(AbstractContainerMenu abstractContainerMenu, Item item3) {
      int value9 = abstractContainerMenu.slots.size();
      int value10 = 0;

      for (int i = 0; i < value9 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (itemStack.getItem() == item3) {
            value10++;
         }
      }

      return value10;
   }

   private int findItemInMenu(AbstractContainerMenu abstractContainerMenu, Item item3) {
      int value9 = abstractContainerMenu.slots.size();

      for (int i = 0; i < value9 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (itemStack.getItem() == item3) {
            return i;
         }
      }

      return -1;
   }

   private <T> BlockPos findBlock(Class<T> clazz, int value9) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         BlockPos blockPos3 = MinecraftAccess.MINECRAFT.player.blockPosition();

         for (int i = blockPos3.getX() - value9; i <= blockPos3.getX() + value9; i++) {
            for (int i2 = blockPos3.getY() - value9; i2 <= blockPos3.getY() + value9; i2++) {
               for (int i3 = blockPos3.getZ() - value9; i3 <= blockPos3.getZ() + value9; i3++) {
                  BlockPos blockPos4 = new BlockPos(i, i2, i3);
                  BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos4);
                  if (!blockState.isAir() && clazz.isInstance(blockState.getBlock())) {
                     return blockPos4;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private <T> List<BlockPos> m_4(Class<T> clazz, int value9, int value10) {
      ArrayList list = new ArrayList();
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         BlockPos blockPos3 = MinecraftAccess.MINECRAFT.player.blockPosition();

         for (int i = blockPos3.getX() - value9; i <= blockPos3.getX() + value9; i++) {
            for (int i2 = blockPos3.getY() - value9; i2 <= blockPos3.getY() + value9; i2++) {
               for (int i3 = blockPos3.getZ() - value9; i3 <= blockPos3.getZ() + value9; i3++) {
                  BlockPos blockPos4 = new BlockPos(i, i2, i3);
                  BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos4);
                  if (!blockState.isAir() && clazz.isInstance(blockState.getBlock())) {
                     list.add(blockPos4);
                     if (list.size() >= value10) {
                        return list;
                     }
                  }
               }
            }
         }

         return list;
      } else {
         return list;
      }
   }

   private int findItemSlot(Item item3) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return -1;
      }

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getItem() == item3) {
            return toContainerSlot(i);
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

   private int countFreeSlots() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return 0;
      }

      int value9 = 0;

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
            value9++;
         }
      }

      return value9;
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

   private int countItemWithOffhand(Item item3) {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return 0;
      }

      int value9 = 0;

      for (int i = 0; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getItem() == item3) {
            value9++;
         }
      }

      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().getItem() == item3) {
         value9++;
      }

      return value9;
   }

   private static int toContainerSlot(int value9) {
      if (value9 >= 0 && value9 < 9) {
         return value9 + 36;
      } else if (value9 >= 36 && value9 < 40) {
         return value9 - 31;
      } else {
         return value9 == 40 ? 45 : value9;
      }
   }

   private int m_2(AbstractContainerMenu abstractContainerMenu, Item item3, int value9, boolean flag12) {
      int value10 = -1;
      double doubleValue = Double.MAX_VALUE;
      int value11 = abstractContainerMenu.slots.size();

      for (int i = 0; i < value11 - 36; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && itemStack.getItem() == item3 && (!flag12 || this.isDefaultName(itemStack))) {
            int value12 = this.getPriceOf(itemStack);
            if (value12 > 0) {
               double doubleValue2 = (double)value12 / Math.max(1, itemStack.getCount());
               if (doubleValue2 < doubleValue) {
                  doubleValue = doubleValue2;
                  value10 = i;
               }
            }
         }
      }

      if (value10 == -1) {
         return -1;
      }

      ItemStack itemStack2 = abstractContainerMenu.getSlot(value10).getItem();
      int value13 = this.getPriceOf(itemStack2);
      if (value13 <= 0) {
         return -1;
      }

      double doubleValue3 = Math.max(1, itemStack2.getCount());
      double doubleValue4 = value13 / doubleValue3;
      return doubleValue4 > value9 ? -1 : value10;
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

   private void m_1_void(AbstractContainerMenu abstractContainerMenu) {
      int value9 = abstractContainerMenu.slots.size();

      for (int i = 0; i < value9 - 36; i++) {
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

   private void quickMoveSlot(AbstractContainerMenu abstractContainerMenu, int value9) {
      if (MinecraftAccess.MINECRAFT.player.tickCount % 18 == 0) {
         InventoryUtil.clickSlot(abstractContainerMenu.containerId, value9, 0, ContainerInput.QUICK_MOVE);
      }
   }

   private int getBalance() {
      long longValue = AuctionScanner.m_5_long();
      return longValue < 0L ? -1 : (int)Math.min(2147483647L, longValue);
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

   private static boolean hasElapsedOver(TimeHelper timeHelper6, long longValue) {
      return timeHelper6.getElapsed() > longValue;
   }

   private static boolean checkDelay(TimeHelper timeHelper6, long longValue, boolean flag12) {
      if (timeHelper6.getElapsed() > longValue) {
         if (flag12) {
            timeHelper6.reset();
         }

         return true;
      } else {
         return false;
      }
   }

   private BlockHitResult resolveHit(BlockHitResult blockHitResult, BlockPos blockPos3, Rotations rotations) {
      if (blockHitResult != null && blockHitResult.getBlockPos().equals(blockPos3)) {
         return blockHitResult;
      }

      BlockHitResult blockHitResult2 = this.raycast(rotations.getFloatValue(), rotations.getFloatValue2(), 4.0);
      if (blockHitResult2 != null && blockHitResult2.getBlockPos().equals(blockPos3)) {
         return blockHitResult2;
      }

      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
      Vec3 vec32 = Vec3.atCenterOf(blockPos3);
      Direction direction = Direction.getApproximateNearest(vec32.x - vec3.x, vec32.y - vec3.y, vec32.z - vec3.z);
      Direction direction2 = direction != null ? direction.getOpposite() : Direction.UP;
      return new BlockHitResult(vec32, direction2, blockPos3, false);
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
                  boolean flag12 = MinecraftAccess.MINECRAFT.options.keyAttack.isDown();
                  MinecraftAccess.MINECRAFT.hitResult = blockHitResult;
                  Key key = ((KeyMappingAccessor)MinecraftAccess.MINECRAFT.options.keyAttack).onetap$getKey();
                  KeyMapping.click(key);
                  MinecraftAccess.MINECRAFT.options.keyAttack.setDown(true);
                  this.flag11 = !flag12;

                  try {
                     MinecraftAccessor minecraftAccessor = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
                     boolean flag13 = minecraftAccessor.onetap$startAttack();
                     if (!flag13 && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
                        minecraftAccessor.onetap$continueAttack(true);
                     }
                  } finally {
                     if (this.flag11) {
                        MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
                        this.flag11 = false;
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
      if (this.flag11) {
         this.flag11 = false;
         if (MinecraftAccess.MINECRAFT.options != null) {
            MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
         }

         if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
            MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
         }
      }
   }

   public static void m_1(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_3_C3_sArr() {
      return moduleArray;
   }

   static {
      m_1(new Module[1]);
   }

   private enum ModeKind {
      PLANT,
      SWEET_BERRIES;

      private static String a(byte[] byteArray) {
         int value9 = 0;
         int value10;
         char[] charArray = new char[value10 = byteArray.length];

         for (int i = 0; i < value10; i++) {
            int value11;
            if ((value11 = 255 & byteArray[i]) < 192) {
               charArray[value9++] = (char)value11;
            } else if (value11 < 224) {
               char ch = (char)((char)(value11 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value9++] = ch;
            } else if (i < value10 - 2) {
               char ch2 = (char)((char)(value11 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value9++] = ch2;
            }
         }

         return new String(charArray, 0, value9);
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
         int value9 = 0;
         int value10;
         char[] charArray = new char[value10 = byteArray.length];

         for (int i = 0; i < value10; i++) {
            int value11;
            if ((value11 = 255 & byteArray[i]) < 192) {
               charArray[value9++] = (char)value11;
            } else if (value11 < 224) {
               char ch = (char)((char)(value11 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value9++] = ch;
            } else if (i < value10 - 2) {
               char ch2 = (char)((char)(value11 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value9++] = ch2;
            }
         }

         return new String(charArray, 0, value9);
      }
   }

   private enum ModeKind3 {
      FREE,
      FARM,
      SELL,
      COOLDOWN,
      REPAIR,
      GET_EXP,
      AUTO_BUY_EXP,
      BUY_EXP,
      SWAP_ITEM;

      private static String a(byte[] byteArray) {
         int value9 = 0;
         int value10;
         char[] charArray = new char[value10 = byteArray.length];

         for (int i = 0; i < value10; i++) {
            int value11;
            if ((value11 = 255 & byteArray[i]) < 192) {
               charArray[value9++] = (char)value11;
            } else if (value11 < 224) {
               char ch = (char)((char)(value11 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value9++] = ch;
            } else if (i < value10 - 2) {
               char ch2 = (char)((char)(value11 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value9++] = ch2;
            }
         }

         return new String(charArray, 0, value9);
      }
   }

   private enum ModeKind4 {
      IDLE,
      SEND_BUYER,
      WAIT_FOR_CONTAINER,
      CLICK_FOOD,
      WAIT_AFTER_FOOD,
      PREPARE_CARROT,
      RIGHT_CLICK_CARROT,
      FINISH;

      private static String a(byte[] byteArray) {
         int value9 = 0;
         int value10;
         char[] charArray = new char[value10 = byteArray.length];

         for (int i = 0; i < value10; i++) {
            int value11;
            if ((value11 = 255 & byteArray[i]) < 192) {
               charArray[value9++] = (char)value11;
            } else if (value11 < 224) {
               char ch = (char)((char)(value11 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value9++] = ch;
            } else if (i < value10 - 2) {
               char ch2 = (char)((char)(value11 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value9++] = ch2;
            }
         }

         return new String(charArray, 0, value9);
      }
   }

   private enum ModeKind5 {
      CARROT(Items.CARROT),
      POTATO(Items.POTATO);

      public final Item item;

      ModeKind5(Item item3) {
         this.item = item3;
      }

      private static String a(byte[] byteArray) {
         int value9 = 0;
         int value10;
         char[] charArray = new char[value10 = byteArray.length];

         for (int i = 0; i < value10; i++) {
            int value11;
            if ((value11 = 255 & byteArray[i]) < 192) {
               charArray[value9++] = (char)value11;
            } else if (value11 < 224) {
               char ch = (char)((char)(value11 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value9++] = ch;
            } else if (i < value10 - 2) {
               char ch2 = (char)((char)(value11 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value9++] = ch2;
            }
         }

         return new String(charArray, 0, value9);
      }
   }

   private static final class StateHolder {
      private final Item item;
      private final String text;
      private final int value;

      private StateHolder(Item item3, String text2, int value9) {
         this.item = item3;
         this.text = text2;
         this.value = value9;
      }
   }

   private enum ModeKind6 {
      IDLE,
      SENT,
      HUB,
      SECOND_WORLD;

      private static String a(byte[] byteArray) {
         int value9 = 0;
         int value10;
         char[] charArray = new char[value10 = byteArray.length];

         for (int i = 0; i < value10; i++) {
            int value11;
            if ((value11 = 255 & byteArray[i]) < 192) {
               charArray[value9++] = (char)value11;
            } else if (value11 < 224) {
               char ch = (char)((char)(value11 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value9++] = ch;
            } else if (i < value10 - 2) {
               char ch2 = (char)((char)(value11 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value9++] = ch2;
            }
         }

         return new String(charArray, 0, value9);
      }
   }
}
