package wtf.onetap.module.pve;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.AuctionScanner;
import wtf.onetap.autobuy.ItemUtil;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "MoneyFarm", description = "Автоматически крафтит альмазные мечи и продаёт их FT/SP", category = Category.PVE)
public class MoneyFarm extends Module {
   private static final Pattern PATTERN = Pattern.compile("у\\s+вас\\s+купили", 66);
   private static final Pattern PATTERN2 = Pattern.compile("не\\s+удалось\\s+выставить|освободите\\s+хранилищ|арендуйте\\s+больше\\s+слот|ah\\s*rent", 66);
   private final TextSetting sellPrice = new TextSetting("Цена продажи", "15000").build();
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private final TimeHelper timeHelper3 = new TimeHelper();
   private MoneyFarm.ModeKind c1 = MoneyFarm.ModeKind.CHECK;
   private MoneyFarm.ModeKind2 c2 = MoneyFarm.ModeKind2.IDLE;
   private int value;
   private int value2;
   private int value3;
   private int value4;
   private long longValue;
   private int value5 = -1;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private int value6;
   private long longValue2 = -1L;
   private int value7;
   private long longValue3 = -1L;
   private boolean flag4;

   @Override
   public void onEnable() {
      this.resetState();
      this.timeHelper2.reset();
      this.timeHelper3.reset();
      ChatUtil.log("§fMoneyFarm §7» §7Инструкция:");
      ChatUtil.log("§71. §fПеред включением модуля смотрите на верстак.");
      ChatUtil.log("§72. §fВ инвентаре должно быть минимум 3–4 стака палок (чем больше, тем лучше). Оставляйте 4 свободных слота.");
      ChatUtil.log("§73. §fНа аккаунте должно быть минимум 10к монет.");
   }

   @Override
   public void onDisable() {
      this.resetState();
   }

   private void resetState() {
      this.c1 = MoneyFarm.ModeKind.CHECK;
      this.c2 = MoneyFarm.ModeKind2.IDLE;
      this.value = 0;
      this.value2 = 0;
      this.value3 = 0;
      this.value4 = 0;
      this.longValue = 0L;
      this.value5 = -1;
      this.flag = false;
      this.flag2 = false;
      this.flag3 = false;
      this.value6 = 0;
      this.longValue2 = -1L;
      this.value7 = 0;
      this.longValue3 = -1L;
      this.flag4 = false;
      this.timeHelper.reset();
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      String text = getChatText(event.getPacket());
      if (text != null && !text.isEmpty() && MinecraftAccess.MINECRAFT != null) {
         MinecraftAccess.MINECRAFT.execute(() -> this.m_9_void(text));
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

   private void m_9_void(String text) {
      if (text != null && !text.isEmpty()) {
         String text2 = text.toLowerCase(Locale.ROOT).replace('ё', 'е');
         if (text2.contains("недопустимо нажимать") && text2.contains("afk")) {
            if (this.longValue2 <= 0L) {
               this.longValue2 = System.currentTimeMillis() + 1000L;
            }
         } else if (PATTERN.matcher(text2).find() || text2.contains("у вас купили")) {
            this.m_1a_void();
         } else if (!PATTERN2.matcher(text2).find()
            && !text2.contains("не удалось выставить")
            && !text2.contains("освободите хранилищ")
            && !text2.contains("арендуйте больше слот")
            && !text2.contains("ah rent")) {
            if ((this.c1 == MoneyFarm.ModeKind.SELL || this.flag2) && (text2.contains("используете команду слишком часто") || text2.contains("слишком часто"))) {
               this.flag2 = false;
               this.timeHelper.reset();
            }
         } else {
            this.m_3_void();
         }
      }
   }

   private void m_1a_void() {
      this.flag = false;
      this.flag2 = false;
      int value8 = this.countItems();
      if (value8 > 0) {
         this.value4 = value8;
         this.c1 = MoneyFarm.ModeKind.SELL;
         this.timeHelper.reset();
      } else {
         if (this.c1 == MoneyFarm.ModeKind.WAIT_STORAGE) {
            this.c1 = MoneyFarm.ModeKind.CHECK;
            this.timeHelper.reset();
         }
      }
   }

   private void m_3_void() {
      this.flag = true;
      this.flag2 = false;
      this.flag3 = false;
      if (MinecraftAccess.MINECRAFT.gui.screen() != null && MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      this.c1 = MoneyFarm.ModeKind.WAIT_STORAGE;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         this.m_1___void();
         this.m_1f_void();
         this.m_2_void();
         if (!this.isBusy()) {
            if (this.timeHelper.hasElapsed(this.getTimeoutTicks())) {
               switch (this.c1.ordinal()) {
                  case 0:
                     this.m_1x_void();
                     break;
                  case 1:
                     this.m_1y_void();
                     break;
                  case 2:
                     this.m_1_e_void();
                     break;
                  case 3:
                     this.m_14_void();
                     break;
                  case 4:
                     this.m_4_void();
                     break;
                  case 5:
                     this.m_7_void();
                     break;
                  case 6:
                     this.m_1_w_void();
                     break;
                  case 7:
                     this.m_12_void();
                     break;
                  case 8:
                     this.m_1_s_void();
                     break;
                  case 9:
                     this.m_1z_void();
               }
            }
         }
      }
   }

   private int getTimeoutTicks() {
      return switch (this.c1.ordinal()) {
         case 1, 5, 6 -> Math.min(40, 50);
         case 2, 3, 4, 7 -> Math.min(40, 35);
         case 8 -> Math.max(40, 100);
         default -> 40;
      };
   }

   private boolean isBusy() {
      return this.c2 != MoneyFarm.ModeKind2.IDLE && this.c2 != MoneyFarm.ModeKind2.WAIT_BUSY;
   }

   private boolean isShopState() {
      return this.c1 == MoneyFarm.ModeKind.SHOP_CMD
         || this.c1 == MoneyFarm.ModeKind.SHOP_GOLD
         || this.c1 == MoneyFarm.ModeKind.SHOP_WAIT
         || this.c1 == MoneyFarm.ModeKind.SHOP_EMERALD
         || this.c1 == MoneyFarm.ModeKind.SHOP_CLOSE
         || this.c1 == MoneyFarm.ModeKind.CRAFT_OPEN
         || this.c1 == MoneyFarm.ModeKind.CRAFT
         || this.c1 == MoneyFarm.ModeKind.SELL;
   }

   private void m_1___void() {
      if (this.longValue2 > 0L && System.currentTimeMillis() >= this.longValue2) {
         this.longValue2 = -1L;
         if (MinecraftAccess.MINECRAFT.getConnection() != null) {
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               MinecraftAccess.MINECRAFT.player.closeContainer();
            }

            Onetap.COMMAND_MANAGER.m_9_boolean(".rct");
            this.value5 = -1;
            this.flag2 = false;
            this.flag = false;
            this.flag3 = true;
            this.value4 = this.countItems();
            this.value7 = 0;
            this.longValue3 = -1L;
            this.c1 = this.value4 > 0 ? MoneyFarm.ModeKind.SELL : MoneyFarm.ModeKind.CHECK;
            this.timeHelper.reset();
         }
      }
   }

   private void m_1f_void() {
      if (this.flag3) {
         this.value7 = 0;
         this.longValue3 = -1L;
      } else if (this.value7 > 0 && this.longValue3 > 0L) {
         if (System.currentTimeMillis() >= this.longValue3) {
            if (MinecraftAccess.MINECRAFT.getConnection() != null && !this.isBusy()) {
               if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  MinecraftAccess.MINECRAFT.player.closeContainer();
               }

               MinecraftAccess.MINECRAFT.getConnection().sendCommand("shop");
               this.longValue = System.currentTimeMillis();
               this.value5 = -1;
               if (this.value <= 0) {
                  this.value = this.m_0_int(this.countItemTotal(Items.EMERALD), AuctionScanner.m_5_long());
               }

               if (this.value > 0 && !this.flag) {
                  this.c1 = MoneyFarm.ModeKind.SHOP_GOLD;
               }

               this.value7--;
               this.longValue3 = this.value7 > 0 ? System.currentTimeMillis() + 1200L : -1L;
               this.timeHelper.reset();
            } else {
               this.longValue3 = System.currentTimeMillis() + 1200L;
            }
         }
      }
   }

   private void m_2_void() {
      if (this.c2 == MoneyFarm.ModeKind2.IDLE) {
         if (this.timeHelper2.hasElapsed(62000L)) {
            if (!this.isShopState() && !this.flag2) {
               this.m_15_void();
            } else {
               this.c2 = MoneyFarm.ModeKind2.WAIT_BUSY;
            }
         }
      } else if (this.c2 == MoneyFarm.ModeKind2.WAIT_BUSY) {
         if (!this.isShopState() && !this.flag2) {
            this.m_15_void();
         }
      } else {
         switch (this.c2.ordinal()) {
            case 2:
               if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen) {
                  this.c2 = MoneyFarm.ModeKind2.CLICK_ENDER;
                  this.timeHelper3.reset();
                  return;
               }

               if (!this.flag4 || this.timeHelper3.hasElapsed(800L)) {
                  if (MinecraftAccess.MINECRAFT.getConnection() != null) {
                     MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah");
                     this.flag4 = true;
                  }

                  this.timeHelper3.reset();
               }
               break;
            case 3:
               if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen)) {
                  if (this.timeHelper3.hasElapsed(2500L)) {
                     this.c2 = MoneyFarm.ModeKind2.OPEN_AH;
                     this.flag4 = false;
                     this.timeHelper3.reset();
                  }

                  return;
               }

               if (findItemInSlots(((ChestMenu)containerScreen.getMenu()).containerId, ((ChestMenu)containerScreen.getMenu()).slots, Items.ENDER_CHEST)) {
                  this.c2 = MoneyFarm.ModeKind2.WAIT_CLOCK;
                  this.timeHelper3.reset();
               } else if (this.timeHelper3.hasElapsed(3000L)) {
                  this.c2 = MoneyFarm.ModeKind2.CLOSE;
                  this.timeHelper3.reset();
               }
               break;
            case 4:
               if (!this.timeHelper3.hasElapsed(800L)) {
                  return;
               }

               this.c2 = MoneyFarm.ModeKind2.CLICK_CLOCK;
               this.timeHelper3.reset();
               break;
            case 5:
               if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen2)) {
                  this.c2 = MoneyFarm.ModeKind2.CLOSE;
                  return;
               }

               if (findItemInSlots(((ChestMenu)containerScreen2.getMenu()).containerId, ((ChestMenu)containerScreen2.getMenu()).slots, Items.CLOCK)) {
                  this.c2 = MoneyFarm.ModeKind2.WAIT_ESC;
                  this.timeHelper3.reset();
               } else if (this.timeHelper3.hasElapsed(2500L)) {
                  this.c2 = MoneyFarm.ModeKind2.CLOSE;
                  this.timeHelper3.reset();
               }
               break;
            case 6:
               if (!this.timeHelper3.hasElapsed(800L)) {
                  return;
               }

               this.c2 = MoneyFarm.ModeKind2.CLOSE;
               this.timeHelper3.reset();
               break;
            case 7:
               this.closeScreens();
               if (MinecraftAccess.MINECRAFT.gui.screen() == null || this.timeHelper3.hasElapsed(1000L)) {
                  this.reset();
               }
         }
      }
   }

   private void m_15_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() != null && this.isShopState() && MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
         if (this.c1 != MoneyFarm.ModeKind.WAIT_STORAGE && this.c1 != MoneyFarm.ModeKind.SELL) {
            this.c1 = MoneyFarm.ModeKind.CHECK;
         }
      }

      this.flag2 = false;
      this.value5 = -1;
      this.flag4 = false;
      this.c2 = MoneyFarm.ModeKind2.OPEN_AH;
      this.timeHelper3.reset();
   }

   private void closeScreens() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.containerMenu != null && MinecraftAccess.MINECRAFT.player.containerMenu.containerId != 0) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.gui.screen().onClose();
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }
   }

   private void reset() {
      this.closeScreens();
      this.c2 = MoneyFarm.ModeKind2.IDLE;
      this.flag4 = false;
      this.timeHelper2.reset();
      this.timeHelper3.reset();
      if (this.flag) {
         this.c1 = MoneyFarm.ModeKind.WAIT_STORAGE;
      } else if (this.countItems() <= 0 && this.value4 <= 0) {
         this.c1 = MoneyFarm.ModeKind.CHECK;
      } else {
         if (this.value4 <= 0) {
            this.value4 = this.countItems();
         }

         this.c1 = MoneyFarm.ModeKind.SELL;
      }

      this.timeHelper.reset();
   }

   private static boolean findItemInSlots(int value8, List<Slot> slotList, Item item) {
      if (slotList != null && item != null) {
         int value9 = Math.min(slotList.size(), 54);

         for (int i = 0; i < value9; i++) {
            Slot slot = (Slot)slotList.get(i);
            if (slot != null && slot.hasItem() && slot.getItem().getItem() == item) {
               InventoryUtil.clickSlot(value8, slot.index, 0, ContainerInput.PICKUP);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void m_1z_void() {
      if (!this.flag) {
         int value8 = this.countItems();
         if (value8 > 0) {
            this.value4 = value8;
            this.c1 = MoneyFarm.ModeKind.SELL;
         } else {
            this.c1 = MoneyFarm.ModeKind.CHECK;
         }

         this.timeHelper.reset();
      }
   }

   private void m_1x_void() {
      if (this.flag) {
         this.c1 = MoneyFarm.ModeKind.WAIT_STORAGE;
      } else {
         int value8 = this.countItems();
         if (this.flag3) {
            if (value8 > 0 || this.value4 > 0) {
               this.value4 = Math.max(this.value4, value8);
               this.flag2 = false;
               this.c1 = MoneyFarm.ModeKind.SELL;
               this.timeHelper.reset();
               return;
            }

            this.flag3 = false;
         }

         if (value8 > 0) {
            this.value4 = Math.max(this.value4, value8);
            this.flag2 = false;
            this.c1 = MoneyFarm.ModeKind.SELL;
            this.timeHelper.reset();
         } else if (this.value4 > 0) {
            this.c1 = MoneyFarm.ModeKind.SELL;
            this.timeHelper.reset();
         } else {
            int value9 = this.countItemTotal(Items.EMERALD);
            int value10 = this.countItemTotal(Items.STICK);
            if (value9 >= 2 && value10 >= 1) {
               this.value2 = Math.min(3, Math.min(value9 / 2, value10 / 1));
               if (this.value2 > 0) {
                  this.value3 = 0;
                  this.value4 = 0;
                  this.c1 = MoneyFarm.ModeKind.CRAFT_OPEN;
                  this.timeHelper.reset();
                  return;
               }
            }

            if (value10 >= 3) {
               long longValue4 = AuctionScanner.m_5_long();
               int value11 = this.m_0_int(value9, longValue4);
               if (value11 > 0) {
                  this.value = value11;
                  this.value5 = -1;
                  this.c1 = MoneyFarm.ModeKind.SHOP_CMD;
                  this.timeHelper.reset();
               }
            }
         }
      }
   }

   private void m_1y_void() {
      if (this.flag) {
         this.c1 = MoneyFarm.ModeKind.WAIT_STORAGE;
      } else if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen) {
         this.value5 = -1;
         this.c1 = MoneyFarm.ModeKind.SHOP_GOLD;
         this.timeHelper.reset();
      } else {
         long longValue4 = System.currentTimeMillis();
         if (longValue4 - this.longValue >= 600L) {
            MinecraftAccess.MINECRAFT.getConnection().sendCommand("shop");
            this.longValue = longValue4;
            this.value5 = -1;
            this.c1 = MoneyFarm.ModeKind.SHOP_GOLD;
            this.timeHelper.reset();
         }
      }
   }

   private void m_1_e_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
         int value8 = this.m_2(containerScreen);
         if (value8 == -1) {
            if (System.currentTimeMillis() - this.longValue > 4000L) {
               MinecraftAccess.MINECRAFT.player.closeContainer();
               this.value5 = -1;
               this.c1 = MoneyFarm.ModeKind.SHOP_CMD;
               this.timeHelper.reset();
            }
         } else {
            this.value5 = ((ChestMenu)containerScreen.getMenu()).containerId;
            InventoryUtil.clickSlot(this.value5, value8, 0, ContainerInput.PICKUP);
            this.c1 = MoneyFarm.ModeKind.SHOP_WAIT;
            this.timeHelper.reset();
         }
      } else {
         if (System.currentTimeMillis() - this.longValue > 3500L) {
            this.c1 = MoneyFarm.ModeKind.SHOP_CMD;
            this.timeHelper.reset();
         }
      }
   }

   private void m_14_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
         if (this.m_3_boolean(containerScreen)) {
            this.c1 = MoneyFarm.ModeKind.SHOP_EMERALD;
            this.timeHelper.reset();
         } else {
            int value8 = this.m_2(containerScreen);
            if (value8 != -1) {
               this.value5 = ((ChestMenu)containerScreen.getMenu()).containerId;
               InventoryUtil.clickSlot(this.value5, value8, 0, ContainerInput.PICKUP);
               this.timeHelper.reset();
            } else {
               if (System.currentTimeMillis() - this.longValue > 5000L) {
                  MinecraftAccess.MINECRAFT.player.closeContainer();
                  this.value5 = -1;
                  this.c1 = MoneyFarm.ModeKind.SHOP_CMD;
                  this.timeHelper.reset();
               }
            }
         }
      } else {
         if (System.currentTimeMillis() - this.longValue > 5000L) {
            this.value5 = -1;
            this.c1 = MoneyFarm.ModeKind.SHOP_CMD;
            this.timeHelper.reset();
         }
      }
   }

   private boolean m_3_boolean(ContainerScreen containerScreen) {
      return this.value5 >= 0 && ((ChestMenu)containerScreen.getMenu()).containerId != this.value5 ? true : this.m_2(containerScreen) == -1 && this.m_6_int(containerScreen) != -1;
   }

   private void m_4_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
         if (!this.m_3_boolean(containerScreen)) {
            this.c1 = MoneyFarm.ModeKind.SHOP_WAIT;
            this.timeHelper.reset();
         } else if (this.value <= 0) {
            this.c1 = MoneyFarm.ModeKind.SHOP_CLOSE;
            this.timeHelper.reset();
         } else {
            long longValue4 = AuctionScanner.m_5_long();
            if (longValue4 >= 0L && longValue4 < 5000L) {
               this.value = 0;
               this.c1 = MoneyFarm.ModeKind.SHOP_CLOSE;
               this.timeHelper.reset();
            } else {
               int value8 = this.m_5(containerScreen, Items.EMERALD);
               if (value8 == -1) {
                  int value9 = this.m_6_int(containerScreen);
                  if (value9 != -1) {
                     InventoryUtil.clickSlot(((ChestMenu)containerScreen.getMenu()).containerId, value9, 0, ContainerInput.QUICK_MOVE);
                     this.timeHelper.reset();
                  } else if (System.currentTimeMillis() - this.longValue > 4000L) {
                     this.c1 = MoneyFarm.ModeKind.SHOP_CLOSE;
                     this.timeHelper.reset();
                  }
               } else if (longValue4 >= 0L && longValue4 < 5000L) {
                  this.value = 0;
                  this.c1 = MoneyFarm.ModeKind.SHOP_CLOSE;
                  this.timeHelper.reset();
               } else {
                  InventoryUtil.clickSlot(((ChestMenu)containerScreen.getMenu()).containerId, value8, 0, ContainerInput.QUICK_MOVE);
                  this.value--;
                  this.timeHelper.reset();
                  if (this.value <= 0) {
                     this.c1 = MoneyFarm.ModeKind.SHOP_CLOSE;
                  }
               }
            }
         }
      } else {
         this.value5 = -1;
         this.c1 = MoneyFarm.ModeKind.SHOP_CMD;
         this.timeHelper.reset();
      }
   }

   private int m_6_int(ContainerScreen containerScreen) {
      int value8 = this.m_2(containerScreen, Items.PAPER, "изумруд");
      return value8 != -1 ? value8 : this.m_2(containerScreen, Items.PAPER, "emerald");
   }

   private int m_2(ContainerScreen containerScreen) {
      int value8 = this.m_2(containerScreen, Items.GOLD_INGOT, "магазин");
      if (value8 != -1) {
         return value8;
      }

      value8 = this.m_5(containerScreen, Items.GOLD_INGOT);
      return value8 != -1 ? value8 : this.m_2(containerScreen, Items.GOLD_INGOT, "сервер");
   }

   private void m_7_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
         this.timeHelper.reset();
      } else {
         this.value5 = -1;
         this.c1 = MoneyFarm.ModeKind.CHECK;
         this.timeHelper.reset();
      }
   }

   private void m_1_w_void() {
      if (this.flag) {
         this.c1 = MoneyFarm.ModeKind.WAIT_STORAGE;
      } else if (MinecraftAccess.MINECRAFT.gui.screen() instanceof CraftingScreen) {
         this.value3 = 0;
         this.c1 = MoneyFarm.ModeKind.CRAFT;
         this.timeHelper.reset();
      } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
         this.timeHelper.reset();
      } else {
         BlockPos blockPos = this.m_4_BlockPos();
         if (blockPos == null) {
            this.c1 = MoneyFarm.ModeKind.CHECK;
            this.timeHelper.reset();
         } else {
            Vec3 vec3 = Vec3.atCenterOf(blockPos);
            MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, new BlockHitResult(vec3, Direction.UP, blockPos, false));
            this.timeHelper.reset();
         }
      }
   }

   private void m_12_void() {
      if (this.flag) {
         if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
            MinecraftAccess.MINECRAFT.player.closeContainer();
         }

         this.c1 = MoneyFarm.ModeKind.WAIT_STORAGE;
      } else if (MinecraftAccess.MINECRAFT.gui.screen() instanceof CraftingScreen craftingScreen) {
         if (this.value2 <= 0) {
            MinecraftAccess.MINECRAFT.player.closeContainer();
            this.value4 = Math.max(this.value4, this.countItems());
            this.c1 = this.value4 > 0 ? MoneyFarm.ModeKind.SELL : MoneyFarm.ModeKind.CHECK;
            this.timeHelper.reset();
         } else {
            int value8 = ((CraftingMenu)craftingScreen.getMenu()).containerId;
            ItemStack itemStack = ((CraftingMenu)craftingScreen.getMenu()).getCarried();
            switch (this.value3) {
               case 0:
                  this.m_7(craftingScreen);
                  this.value3 = 1;
                  break;
               case 1:
                  if (!itemStack.isEmpty()) {
                     this.m_7_void(craftingScreen);
                  } else {
                     int value9 = this.m_2(craftingScreen, Items.EMERALD);
                     if (value9 == -1) {
                        this.value2 = 0;
                     } else {
                        InventoryUtil.clickSlot(value8, value9, 0, ContainerInput.PICKUP);
                        this.value3 = 2;
                     }
                  }
                  break;
               case 2:
                  itemStack = ((CraftingMenu)craftingScreen.getMenu()).getCarried();
                  if (!itemStack.isEmpty() && itemStack.is(Items.EMERALD)) {
                     InventoryUtil.clickSlot(value8, 2, 1, ContainerInput.PICKUP);
                     this.value3 = 3;
                  } else {
                     this.value3 = 1;
                  }
                  break;
               case 3:
                  itemStack = ((CraftingMenu)craftingScreen.getMenu()).getCarried();
                  if (!itemStack.isEmpty() && itemStack.is(Items.EMERALD)) {
                     InventoryUtil.clickSlot(value8, 5, 1, ContainerInput.PICKUP);
                     this.value3 = 4;
                  } else {
                     this.value3 = 1;
                  }
                  break;
               case 4:
                  itemStack = ((CraftingMenu)craftingScreen.getMenu()).getCarried();
                  if (!itemStack.isEmpty()) {
                     this.m_7_void(craftingScreen);
                  } else {
                     this.value3 = 5;
                  }
                  break;
               case 5:
                  itemStack = ((CraftingMenu)craftingScreen.getMenu()).getCarried();
                  if (!itemStack.isEmpty()) {
                     this.m_7_void(craftingScreen);
                  } else {
                     int value10 = this.m_2(craftingScreen, Items.STICK);
                     if (value10 == -1) {
                        this.m_7(craftingScreen);
                        this.value2 = 0;
                     } else {
                        InventoryUtil.clickSlot(value8, value10, 0, ContainerInput.PICKUP);
                        this.value3 = 6;
                     }
                  }
                  break;
               case 6:
                  itemStack = ((CraftingMenu)craftingScreen.getMenu()).getCarried();
                  if (!itemStack.isEmpty() && itemStack.is(Items.STICK)) {
                     InventoryUtil.clickSlot(value8, 8, 1, ContainerInput.PICKUP);
                     this.value3 = 7;
                  } else {
                     this.value3 = 5;
                  }
                  break;
               case 7:
                  itemStack = ((CraftingMenu)craftingScreen.getMenu()).getCarried();
                  if (!itemStack.isEmpty()) {
                     this.m_7_void(craftingScreen);
                  } else {
                     this.value3 = 8;
                  }
                  break;
               case 8:
                  ItemStack itemStack2 = ((CraftingMenu)craftingScreen.getMenu()).getSlot(0).getItem();
                  if (itemStack2.isEmpty()) {
                     this.m_7(craftingScreen);
                     this.value2 = 0;
                  } else {
                     InventoryUtil.clickSlot(value8, 0, 0, ContainerInput.QUICK_MOVE);
                     this.value2--;
                     this.value4++;
                     this.value3 = 0;
                  }
            }

            this.timeHelper.reset();
         }
      } else {
         this.c1 = MoneyFarm.ModeKind.CRAFT_OPEN;
         this.timeHelper.reset();
      }
   }

   private void m_1_s_void() {
      if (this.flag) {
         this.flag2 = false;
         this.c1 = MoneyFarm.ModeKind.WAIT_STORAGE;
      } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
         this.timeHelper.reset();
      } else if (this.flag2) {
         if (this.timeHelper.hasElapsed(250L)) {
            int value8 = this.countItems();
            if (value8 < this.value6) {
               this.value4 = Math.max(0, this.value4 - (this.value6 - value8));
            }

            this.flag2 = false;
            this.value6 = 0;
            this.timeHelper.reset();
            if (this.value4 <= 0 && this.countItems() <= 0) {
               this.c1 = MoneyFarm.ModeKind.CHECK;
            }
         }
      } else {
         if (this.value4 <= 0) {
            int value9 = this.countItems();
            if (value9 <= 0) {
               this.c1 = MoneyFarm.ModeKind.CHECK;
               this.timeHelper.reset();
               return;
            }

            this.value4 = value9;
         }

         int value10 = this.m_7_int();
         if (value10 == -1) {
            this.value4 = 0;
            this.c1 = MoneyFarm.ModeKind.CHECK;
            this.timeHelper.reset();
         } else {
            this.m_7_void(value10);
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
            if (!itemStack.isEmpty() && m_6_boolean(itemStack)) {
               this.value6 = this.countItems();
               MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah sell " + Math.max(1, this.sellPrice.getInt()));
               this.flag2 = true;
               this.timeHelper.reset();
            } else {
               this.timeHelper.reset();
            }
         }
      }
   }

   private int m_0_int(int value8, long longValue4) {
      if (longValue4 < 5000L && longValue4 >= 0L) {
         return 0;
      }

      int value9 = longValue4 < 0L ? 6 : (int)Math.min(6L, longValue4 / 5000L);
      if (value9 <= 0) {
         return 0;
      }

      int value10 = value9;
      if ((value8 + value10) % 2 != 0) {
         value10--;
      }

      if (value10 <= 0 && value9 >= 1 && value8 % 2 != 0) {
         value10 = 1;
      }

      return Math.max(0, value10);
   }

   private void m_7(CraftingScreen craftingScreen) {
      int value8 = ((CraftingMenu)craftingScreen.getMenu()).containerId;

      for (int i = 1; i <= 9; i++) {
         Slot slot = ((CraftingMenu)craftingScreen.getMenu()).getSlot(i);
         if (slot != null && slot.hasItem()) {
            InventoryUtil.clickSlot(value8, i, 0, ContainerInput.QUICK_MOVE);
         }
      }

      if (!((CraftingMenu)craftingScreen.getMenu()).getCarried().isEmpty()) {
         this.m_7_void(craftingScreen);
      }
   }

   private void m_7_void(AbstractContainerScreen<?> abstractContainerScreen) {
      int value8 = abstractContainerScreen.getMenu().containerId;
      int value9 = this.m_7_int(abstractContainerScreen);
      if (value9 != -1) {
         InventoryUtil.clickSlot(value8, value9, 0, ContainerInput.PICKUP);
      } else {
         InventoryUtil.clickSlot(value8, -999, 0, ContainerInput.PICKUP);
      }
   }

   private int m_2(AbstractContainerScreen<?> abstractContainerScreen, Item item) {
      var nonNullList = abstractContainerScreen.getMenu().slots;
      int value8 = Math.max(0, nonNullList.size() - 36);

      for (int i = value8; i < nonNullList.size(); i++) {
         Slot slot = (Slot)nonNullList.get(i);
         if (slot != null && slot.hasItem() && slot.getItem().is(item)) {
            return slot.index;
         }
      }

      return -1;
   }

   private int m_7_int(AbstractContainerScreen<?> abstractContainerScreen) {
      var nonNullList = abstractContainerScreen.getMenu().slots;
      int value8 = Math.max(0, nonNullList.size() - 36);

      for (int i = value8; i < nonNullList.size(); i++) {
         Slot slot = (Slot)nonNullList.get(i);
         if (slot != null && !slot.hasItem()) {
            return slot.index;
         }
      }

      return -1;
   }

   private int m_5(ContainerScreen containerScreen, Item item) {
      NonNullList nonNullList = ((ChestMenu)containerScreen.getMenu()).slots;
      int value8 = Math.max(0, nonNullList.size() - 36);

      for (int i = 0; i < value8; i++) {
         Slot slot = (Slot)nonNullList.get(i);
         if (slot != null && slot.hasItem() && slot.getItem().is(item)) {
            return slot.index;
         }
      }

      return -1;
   }

   private int m_2(ContainerScreen containerScreen, Item item, String text) {
      NonNullList nonNullList = ((ChestMenu)containerScreen.getMenu()).slots;
      int value8 = Math.max(0, nonNullList.size() - 36);
      String text2 = text.toLowerCase(Locale.ROOT).replace('ё', 'е');

      for (int i = 0; i < value8; i++) {
         Slot slot = (Slot)nonNullList.get(i);
         if (slot != null && slot.hasItem() && slot.getItem().is(item)) {
            String text3 = ItemUtil.stripFormatting(slot.getItem().getHoverName().getString()).toLowerCase(Locale.ROOT).replace('ё', 'е');
            if (text3.contains(text2)) {
               return slot.index;
            }
         }
      }

      return -1;
   }

   private BlockPos m_4_BlockPos() {
      BlockPos blockPos = MinecraftAccess.MINECRAFT.player.blockPosition();
      BlockPos blockPos2 = null;
      double doubleValue = Double.MAX_VALUE;

      for (int i = -4; i <= 4; i++) {
         for (int i2 = -2; i2 <= 2; i2++) {
            for (int i3 = -4; i3 <= 4; i3++) {
               BlockPos blockPos3 = blockPos.offset(i, i2, i3);
               if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3).is(Blocks.CRAFTING_TABLE)) {
                  double doubleValue2 = MinecraftAccess.MINECRAFT.player.distanceToSqr(blockPos3.getX() + 0.5, blockPos3.getY() + 0.5, blockPos3.getZ() + 0.5);
                  if (doubleValue2 < doubleValue && doubleValue2 <= 25.0) {
                     doubleValue = doubleValue2;
                     blockPos2 = blockPos3.immutable();
                  }
               }
            }
         }
      }

      return blockPos2;
   }

   private int countItemTotal(Item item) {
      int value8 = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.is(item)) {
            value8 += itemStack.getCount();
         }
      }

      return value8;
   }

   private int countItems() {
      int value8 = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (m_6_boolean(itemStack)) {
            value8 += itemStack.getCount();
         }
      }

      return value8;
   }

   private int m_7_int() {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (m_6_boolean(itemStack)) {
            return i;
         }
      }

      return -1;
   }

   private static boolean m_6_boolean(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() && isSword(itemStack.getItem()) && !itemStack.isDamaged();
   }

   private static boolean isSword(Item item) {
      return item == Items.WOODEN_SWORD || item == Items.DIAMOND_SWORD;
   }

   private void m_7_void(int value8) {
      if (value8 >= 0 && value8 <= 35) {
         if (value8 < 9) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value8);
            MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundSetCarriedItemPacket(value8));
         } else {
            byte byteValue = 0;
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(byteValue);
            MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundSetCarriedItemPacket(byteValue));
            InventoryUtil.clickSlot(0, value8, byteValue, ContainerInput.SWAP);
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(byteValue);
            MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundSetCarriedItemPacket(byteValue));
         }
      }
   }

   private enum ModeKind {
      CHECK,
      SHOP_CMD,
      SHOP_GOLD,
      SHOP_WAIT,
      SHOP_EMERALD,
      SHOP_CLOSE,
      CRAFT_OPEN,
      CRAFT,
      SELL,
      WAIT_STORAGE;

      private static String a(byte[] byteArray) {
         int value8 = 0;
         int value9;
         char[] charArray = new char[value9 = byteArray.length];

         for (int i = 0; i < value9; i++) {
            int value10;
            if ((value10 = 255 & byteArray[i]) < 192) {
               charArray[value8++] = (char)value10;
            } else if (value10 < 224) {
               char ch = (char)((char)(value10 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value8++] = ch;
            } else if (i < value9 - 2) {
               char ch2 = (char)((char)(value10 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value8++] = ch2;
            }
         }

         return new String(charArray, 0, value8);
      }
   }

   private enum ModeKind2 {
      IDLE,
      WAIT_BUSY,
      OPEN_AH,
      CLICK_ENDER,
      WAIT_CLOCK,
      CLICK_CLOCK,
      WAIT_ESC,
      CLOSE;

      private static String a(byte[] byteArray) {
         int value8 = 0;
         int value9;
         char[] charArray = new char[value9 = byteArray.length];

         for (int i = 0; i < value9; i++) {
            int value10;
            if ((value10 = 255 & byteArray[i]) < 192) {
               charArray[value8++] = (char)value10;
            } else if (value10 < 224) {
               char ch = (char)((char)(value10 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value8++] = ch;
            } else if (i < value9 - 2) {
               char ch2 = (char)((char)(value10 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value8++] = ch2;
            }
         }

         return new String(charArray, 0, value8);
      }
   }
}
