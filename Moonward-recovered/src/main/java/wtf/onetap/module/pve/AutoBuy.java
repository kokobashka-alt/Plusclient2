package wtf.onetap.module.pve;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.AuctionConfig;
import wtf.onetap.autobuy.AuctionParser;
import wtf.onetap.autobuy.AuctionScanner;
import wtf.onetap.autobuy.AuctionSeller;
import wtf.onetap.autobuy.FunTimeItems;
import wtf.onetap.autobuy.ItemUtil;
import wtf.onetap.config.AutoBuyBans;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.AutoBuyScreen;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AutoBuy", description = "Автопокупка SpookyTime", category = Category.PVE)
public class AutoBuy extends Module {
   private static final Pattern PATTERN = Pattern.compile("Вы\\s+(?:успешно\\s+)?купили\\s+(.+?)\\s+за\\s+\\$?\\s*([\\d,.\\s]+)", 66);
   private static final Pattern PATTERN2 = Pattern.compile("у\\s+вас\\s+купили\\s+(.+?)\\s+за\\s+\\$?\\s*([\\d,.\\s]+)", 66);
   private final KeybindSetting openGui = new KeybindSetting("Открыть GUI", -1).build();
   private final BooleanSetting autoSell = new BooleanSetting("Авто продажа", false).build();
   private final FloatSetting parsePercent = new FloatSetting("Процент от парса", 10.0F, 0.0F, 100.0F, 1.0F).visibleWhen(this.autoSell::getValue).build();
   private final AuctionScanner auctionScanner = new AuctionScanner();
   private final AuctionSeller auctionSeller = new AuctionSeller();
   private final List<ItemUtil> itemUtilList = new ArrayList<>();
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private final TimeHelper timeHelper3 = new TimeHelper();
   private boolean flag;
   private boolean flag2;
   private long longValue = -1L;
   private int value;
   private long longValue2 = -1L;
   private AutoBuy.ModeKind c0 = AutoBuy.ModeKind.IDLE;

   @Override
   public void onEnable() {
      AutoBuyBans.m_8_void();
      this.auctionScanner.m_6_void();
      this.auctionSeller.m_1_void();
      this.auctionSeller.m_3_void();
      this.flag = false;
      this.flag2 = false;
      this.longValue = -1L;
      this.value = 0;
      this.longValue2 = -1L;
      this.c0 = AutoBuy.ModeKind.IDLE;
      this.timeHelper.reset();
      this.timeHelper2.reset();
      this.timeHelper3.reset();
      this.reloadItems();
   }

   @Override
   public void onDisable() {
      AutoBuyBans.m_0_void();
      AuctionConfig.getConfig().setFlag(false);
      if (AuctionParser.isRunning()) {
         AuctionParser.m_9_void(false);
      }

      this.auctionScanner.m_6_void();
      this.auctionSeller.m_1_void();
      this.auctionSeller.m_3_void();
      this.flag = false;
      this.flag2 = false;
      this.longValue = -1L;
      this.value = 0;
      this.longValue2 = -1L;
      this.c0 = AutoBuy.ModeKind.IDLE;
      this.itemUtilList.clear();
   }

   public int getOpenGui() {
      return (Integer)this.openGui.getValue();
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (event.getAction() == 1) {
            if (!ClickGuiScreen.isEditing()) {
               if ((Integer)this.openGui.getValue() != -1 && event.getKey() == (Integer)this.openGui.getValue()) {
                  if (MinecraftAccess.MINECRAFT.player != null) {
                     MinecraftAccess.MINECRAFT.gui.setScreen(AutoBuyScreen.getInstance());
                     event.cancel();
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
         String text = clientboundSystemChatPacket.content().getString().replaceAll("§[0-9a-fk-or]", "");
         if (MinecraftAccess.MINECRAFT != null) {
            MinecraftAccess.MINECRAFT.execute(() -> this.m_8_void(text));
         }
      }
   }

   private void m_8_void(String text) {
      if (text != null && !text.isEmpty()) {
         String text2 = text.toLowerCase(Locale.ROOT).replace('ё', 'е');
         boolean flag3 = AuctionConfig.getConfig().isFlag();
         if (flag3 && text2.contains("недопустимо нажимать") && text2.contains("afk")) {
            if (this.longValue <= 0L) {
               this.longValue = System.currentTimeMillis() + 1000L;
            }
         } else {
            if (flag3 && (Boolean)this.autoSell.getValue()) {
               if (PATTERN2.matcher(text2).find() || text2.contains("у вас купили")) {
                  this.auctionSeller.m_8_void();
               }

               if (text2.contains("освободите хранилище")
                  || text2.contains("арендуйте больше слотов")
                  || text2.contains("ah rent")
                  || text2.contains("не удалось выставить") && (text2.contains("хранилищ") || text2.contains("rent"))) {
                  this.auctionSeller.m_2_void();
                  return;
               }

               if (this.auctionSeller.isFlag() && (text2.contains("используете команду слишком часто") || text2.contains("слишком часто"))) {
                  this.auctionSeller.m_5_void();
                  return;
               }
            }

            if (!text2.contains("не хватает") || !text2.contains("монет")) {
               Matcher matcher = PATTERN.matcher(text);
               if (matcher.find()) {
                  try {
                     int value2 = Integer.parseInt(matcher.group(2).replaceAll("[\\s,.]", ""));
                     if (value2 > 0) {
                        this.auctionScanner.m_9_void(matcher.group(1).trim(), value2);
                        if (flag3 && (Boolean)this.autoSell.getValue() && !this.isBusy() && this.c0 != AutoBuy.ModeKind.WAIT_BUSY) {
                           this.auctionSeller.m_6_void();
                        }
                     }
                  } catch (NumberFormatException numberFormatException) {
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         boolean flag3 = AuctionConfig.getConfig().isFlag();
         if (!flag3) {
            if (this.auctionSeller.isFlag()) {
               this.auctionSeller.m_1_void();
            }

            if (this.c0 != AutoBuy.ModeKind.IDLE) {
               this.c0 = AutoBuy.ModeKind.IDLE;
               this.timeHelper3.reset();
            }

            this.longValue = -1L;
            this.value = 0;
            this.longValue2 = -1L;
         }

         if (flag3) {
            this.m_1_void();
            this.m_4_void();
            this.m_2_void();
         }

         if (!this.isBusy()) {
            boolean flag4 = flag3 && (Boolean)this.autoSell.getValue();
            boolean flag5 = flag4 && this.auctionSeller.isFlag();
            if (flag4) {
               this.auctionSeller.m_1(true, this.parsePercent.getInt());
               flag5 = this.auctionSeller.isFlag();
            } else if (this.auctionSeller.isFlag()) {
               this.auctionSeller.m_1_void();
               flag5 = false;
            }

            if (!flag5) {
               this.m_3_void();
               flag3 = AuctionConfig.getConfig().isFlag();
               boolean flag6 = AuctionParser.isRunning();
               if (this.flag2 && !flag6) {
                  this.timeHelper.reset();
                  this.reloadItems();
               }

               this.flag2 = flag6;
               if (flag3 || flag6) {
                  if (flag6 && !(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
                     AuctionParser.m_5(MinecraftAccess.MINECRAFT);
                  }

                  if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
                     String text = containerScreen.getTitle().getString();
                     NonNullList nonNullList = ((ChestMenu)containerScreen.getMenu()).slots;
                     int value2 = ((ChestMenu)containerScreen.getMenu()).containerId;
                     if (m_3_boolean(text)) {
                        if (flag3 || flag6) {
                           this.m_7(containerScreen);
                        }

                        return;
                     }

                     if (m_9_boolean(containerScreen) || flag6) {
                        if (!this.flag) {
                           this.flag = true;
                           this.auctionScanner.m_0_void();
                           this.reloadItems();
                        }

                        if (flag6) {
                           AuctionParser.m_0(MinecraftAccess.MINECRAFT, nonNullList);
                           return;
                        }

                        if (flag3) {
                           if (this.itemUtilList.isEmpty()) {
                              this.reloadItems();
                           }

                           this.auctionScanner.m_7(value2, nonNullList, this.itemUtilList);
                        }

                        return;
                     }
                  }

                  if (this.flag) {
                     this.flag = false;
                     this.auctionScanner.m_0_void();
                  }
               }
            }
         }
      }
   }

   private boolean isBusy() {
      return this.c0 != AutoBuy.ModeKind.IDLE && this.c0 != AutoBuy.ModeKind.WAIT_BUSY;
   }

   private boolean isSellerOrParserRunning() {
      return this.auctionSeller.isFlag() || AuctionParser.isRunning();
   }

   private void m_2_void() {
      if (!AuctionConfig.getConfig().isFlag()) {
         this.c0 = AutoBuy.ModeKind.IDLE;
      } else if (this.c0 == AutoBuy.ModeKind.IDLE) {
         if (this.timeHelper2.hasElapsed(62000L)) {
            if (this.isSellerOrParserRunning()) {
               this.c0 = AutoBuy.ModeKind.WAIT_BUSY;
            } else {
               this.m_9v_void();
            }
         }
      } else if (this.c0 == AutoBuy.ModeKind.WAIT_BUSY) {
         if (!AuctionConfig.getConfig().isFlag()) {
            this.c0 = AutoBuy.ModeKind.IDLE;
         } else if (!this.isSellerOrParserRunning()) {
            this.m_9v_void();
         }
      } else if (this.c0 != AutoBuy.ModeKind.STOP_BUY && this.c0 != AutoBuy.ModeKind.WAIT_BEFORE_ENDER) {
         if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen)) {
            if (MinecraftAccess.MINECRAFT.getConnection() != null && this.timeHelper3.hasElapsed(800L)) {
               MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah");
               this.timeHelper3.reset();
            }
         } else {
            int value2 = ((ChestMenu)containerScreen.getMenu()).containerId;
            NonNullList nonNullList = ((ChestMenu)containerScreen.getMenu()).slots;
            switch (this.c0.ordinal()) {
               case 4:
                  if (findItemInSlots(value2, nonNullList, Items.ENDER_CHEST)) {
                     this.c0 = AutoBuy.ModeKind.WAIT_CLOCK;
                     this.timeHelper3.reset();
                  } else if (this.timeHelper3.hasElapsed(3000L)) {
                     if (MinecraftAccess.MINECRAFT.getConnection() != null) {
                        MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah");
                     }

                     this.timeHelper3.reset();
                  }
                  break;
               case 5:
                  if (!this.timeHelper3.hasElapsed(1000L)) {
                     return;
                  }

                  this.c0 = AutoBuy.ModeKind.CLICK_CLOCK;
                  this.timeHelper3.reset();
                  break;
               case 6:
                  if (findItemInSlots(value2, nonNullList, Items.CLOCK)) {
                     this.c0 = AutoBuy.ModeKind.WAIT_ARROW;
                     this.timeHelper3.reset();
                  } else if (this.timeHelper3.hasElapsed(3000L)) {
                     this.timeHelper3.reset();
                  }
                  break;
               case 7:
                  if (!this.timeHelper3.hasElapsed(1000L)) {
                     return;
                  }

                  this.c0 = AutoBuy.ModeKind.CLICK_ARROW;
                  this.timeHelper3.reset();
                  break;
               case 8:
                  if (findItemInSlots(value2, nonNullList, Items.TIPPED_ARROW)) {
                     this.c0 = AutoBuy.ModeKind.IDLE;
                     this.timeHelper2.reset();
                     this.timeHelper3.reset();
                  } else if (this.timeHelper3.hasElapsed(3000L)) {
                     if (!findItemInSlots(value2, nonNullList, Items.ARROW) && !m_4(value2, nonNullList, "arrow") && !m_4(value2, nonNullList, "стрел")) {
                        this.timeHelper3.reset();
                     } else {
                        this.c0 = AutoBuy.ModeKind.IDLE;
                        this.timeHelper2.reset();
                        this.timeHelper3.reset();
                     }
                  }
            }
         }
      } else if (this.c0 == AutoBuy.ModeKind.STOP_BUY) {
         this.c0 = AutoBuy.ModeKind.WAIT_BEFORE_ENDER;
         this.timeHelper3.reset();
      } else if (this.timeHelper3.hasElapsed(1500L)) {
         this.c0 = AutoBuy.ModeKind.CLICK_ENDER;
         this.timeHelper3.reset();
         if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen) && MinecraftAccess.MINECRAFT.getConnection() != null) {
            MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah");
         }
      }
   }

   private void m_9v_void() {
      this.auctionScanner.m_0_void();
      this.flag = false;
      this.c0 = AutoBuy.ModeKind.STOP_BUY;
      this.timeHelper3.reset();
   }

   private static boolean findItemInSlots(int value2, List<Slot> slotList, Item item) {
      if (slotList != null && item != null) {
         int value3 = Math.min(slotList.size(), 54);

         for (int i = 0; i < value3; i++) {
            Slot slot = (Slot)slotList.get(i);
            if (slot != null && slot.hasItem() && slot.getItem().getItem() == item) {
               InventoryUtil.clickSlot(value2, slot.index, 0, ContainerInput.PICKUP);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean m_4(int value2, List<Slot> slotList, String text) {
      if (slotList != null && text != null && !text.isBlank()) {
         String text2 = text.toLowerCase(Locale.ROOT);
         int value3 = Math.min(slotList.size(), 54);

         for (int i = 0; i < value3; i++) {
            Slot slot = (Slot)slotList.get(i);
            if (slot != null && slot.hasItem()) {
               String text3 = slot.getItem().getHoverName().getString().toLowerCase(Locale.ROOT);
               String text4 = slot.getItem().getItem().toString().toLowerCase(Locale.ROOT);
               if (text3.contains(text2) || text4.contains(text2)) {
                  InventoryUtil.clickSlot(value2, slot.index, 0, ContainerInput.PICKUP);
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void m_1_void() {
      if (this.longValue > 0L && System.currentTimeMillis() >= this.longValue) {
         this.longValue = -1L;
         if (MinecraftAccess.MINECRAFT.getConnection() != null) {
            Onetap.COMMAND_MANAGER.m_9_boolean(".rct");
            this.value = 2;
            this.longValue2 = System.currentTimeMillis() + 4500L;
         }
      }
   }

   private void m_4_void() {
      if (this.value > 0 && this.longValue2 > 0L) {
         if (System.currentTimeMillis() >= this.longValue2) {
            if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
               MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah");
               this.value--;
               if (this.value > 0) {
                  this.longValue2 = System.currentTimeMillis() + 1200L;
               } else {
                  this.longValue2 = -1L;
               }
            } else {
               this.longValue2 = System.currentTimeMillis() + 1200L;
            }
         }
      }
   }

   private void m_3_void() {
      if (!this.auctionSeller.isFlag() && !this.isBusy() && this.c0 != AutoBuy.ModeKind.WAIT_BUSY) {
         boolean flag3 = AuctionConfig.getConfig().isFlag();
         boolean flag4 = AuctionParser.isRunning();
         if (flag3 && !flag4) {
            if (this.timeHelper.hasElapsed(300000L)) {
               this.timeHelper.reset();
               AuctionParser.m_9_void(true);
               if (AuctionParser.isRunning()) {
               }
            }
         }
      }
   }

   private void m_7(ContainerScreen containerScreen) {
      NonNullList nonNullList = ((ChestMenu)containerScreen.getMenu()).slots;

      for (int i = 0; i < Math.min(nonNullList.size(), 54); i++) {
         Slot slot = (Slot)nonNullList.get(i);
         if (slot != null && !slot.getItem().isEmpty()) {
            String text = slot.getItem().getHoverName().getString().toLowerCase();
            if (text.contains("подтверд") || text.contains("купить") || text.contains("да") || text.contains("accept")) {
               InventoryUtil.clickSlot(((ChestMenu)containerScreen.getMenu()).containerId, slot.index, 0, ContainerInput.QUICK_MOVE);
               return;
            }
         }
      }

      if (nonNullList.size() > 15) {
         InventoryUtil.clickSlot(((ChestMenu)containerScreen.getMenu()).containerId, 15, 0, ContainerInput.QUICK_MOVE);
      }
   }

   private void reloadItems() {
      this.itemUtilList.clear();
      this.itemUtilList.addAll(FunTimeItems.getEnabledItems());
   }

   public void m_7_void() {
      this.reloadItems();
   }

   public AuctionScanner getAuctionScanner() {
      return this.auctionScanner;
   }

   public boolean isAutoSell() {
      return (Boolean)this.autoSell.getValue();
   }

   public int getParsePercent() {
      return this.parsePercent.getInt();
   }

   public boolean m_1_boolean() {
      return this.auctionSeller.isFlag();
   }

   public boolean isAuctionEnabled() {
      return AuctionConfig.getConfig().isFlag();
   }

   public void setAuctionEnabled(boolean flag3) {
      AuctionConfig.getConfig().setFlag(flag3);
      if (flag3) {
         this.timeHelper.reset();
         this.timeHelper2.reset();
         this.reloadItems();
      } else {
         this.auctionScanner.m_0_void();
         this.auctionSeller.m_1_void();
         this.c0 = AutoBuy.ModeKind.IDLE;
         this.longValue = -1L;
         this.value = 0;
         this.longValue2 = -1L;
      }
   }

   public void toggleAuction() {
      this.setAuctionEnabled(!this.isAuctionEnabled());
   }

   public static boolean m_2_boolean(String text) {
      if (text == null) {
         return false;
      }

      String text2 = text.toLowerCase().replace('ё', 'е');
      return text2.contains("аукцион")
         || text2.contains("поиск")
         || text2.contains("auction")
         || text2.contains("search")
         || text2.contains("ah")
         || text2.contains("п:")
         || text2.contains("лот")
         || text2.contains("торг")
         || text2.contains("маркет")
         || text2.contains("market")
         || text2.contains("store")
         || text2.contains("базар");
   }

   public static boolean m_3_boolean(String text) {
      if (text == null) {
         return false;
      }

      String text2 = text.toLowerCase();
      return text2.contains("подозрительная цена") || text2.contains("подтверждение") || text2.contains("confirm") || text2.contains("покупка предмета");
   }

   public static boolean m_9_boolean(ContainerScreen containerScreen) {
      if (containerScreen == null) {
         return false;
      } else if (m_2_boolean(containerScreen.getTitle().getString())) {
         return true;
      } else {
         return AuctionParser.isRunning() ? true : AuctionConfig.getConfig().isFlag();
      }
   }

   public static boolean m_1(ContainerScreen containerScreen) {
      AutoBuy autoBuy = Onetap.MODULE_MANAGER.get(AutoBuy.class);
      return autoBuy != null && autoBuy.isEnabled() && containerScreen != null ? m_9_boolean(containerScreen) : false;
   }

   private enum ModeKind {
      IDLE,
      WAIT_BUSY,
      STOP_BUY,
      WAIT_BEFORE_ENDER,
      CLICK_ENDER,
      WAIT_CLOCK,
      CLICK_CLOCK,
      WAIT_ARROW,
      CLICK_ARROW;

      private static String a(byte[] byteArray) {
         int value2 = 0;
         int value3;
         char[] charArray = new char[value3 = byteArray.length];

         for (int i = 0; i < value3; i++) {
            int value4;
            if ((value4 = 255 & byteArray[i]) < 192) {
               charArray[value2++] = (char)value4;
            } else if (value4 < 224) {
               char ch = (char)((char)(value4 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value2++] = ch;
            } else if (i < value3 - 2) {
               char ch2 = (char)((char)(value4 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value2++] = ch2;
            }
         }

         return new String(charArray, 0, value2);
      }
   }
}
