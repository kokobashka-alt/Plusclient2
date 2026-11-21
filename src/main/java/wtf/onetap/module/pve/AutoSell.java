package wtf.onetap.module.pve;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.autobuy.AuctionTitleMatcher;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.misc.AhHelper;
import wtf.onetap.obf.C69;
import wtf.onetap.obf.C7n;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.HotbarUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoSell", description = "Автоматически выставляет предметы на продажу", category = Category.PVE)
public class AutoSell extends Module {
   public static AutoSell autoSell;
   public final ModeSetting mode = new ModeSetting("Режим", "По бинду", "По бинду", "Авто").build();
   public final ModeSetting sellMode = new ModeSetting("Режим продажи", "По одной штуке", "По одной штуке", "Все сразу", "Одна цена").build();
   public final FloatSetting markupPercent = new FloatSetting("Наценка %", 10.0F, 0.0F, 100.0F, 10.0F)
      .visibleWhen(() -> ((String)this.mode.getValue()).equals("По бинду") && !((String)this.sellMode.getValue()).equals("Одна цена"))
      .build();
   public final KeybindSetting sellBind = new KeybindSetting("Бинд продажи", -1).visibleWhen(() -> ((String)this.mode.getValue()).equals("По бинду")).build();
   public final TextSetting singleSalePrice = new TextSetting("Цена одной продажи", "1000").visibleWhen(() -> ((String)this.sellMode.getValue()).equals("Одна цена")).build();
   public final ModeSetting singlePriceListing = new ModeSetting("Выкладка одной цены", "По одной штуке", "По одной штуке", "Все сразу")
      .visibleWhen(() -> ((String)this.sellMode.getValue()).equals("Одна цена"))
      .build();
   private final C69 c69 = new C69();
   private final C69 c692 = new C69();
   private final C69 c693 = new C69();
   private AutoSell.ModeKind c5 = AutoSell.ModeKind.IDLE;
   private boolean flag = false;
   private long longValue = 0L;
   private String text = "";
   private int value = 0;
   private boolean flag2 = false;
   private String text2 = "";
   private String text3 = "";
   private Item item = Items.AIR;
   private boolean flag3 = false;
   private int value2 = 0;
   private boolean flag4 = false;
   private boolean flag5 = false;
   private boolean flag6 = false;
   private int value3 = 0;
   private int value4 = 0;
   private int value5 = 0;
   private int value6 = 0;
   private int value7 = 9;
   private boolean flag7 = false;
   private boolean flag8 = false;
   private boolean flag9 = false;
   private int value8 = 0;
   private long longValue2 = 0L;
   private long longValue3 = 0L;
   private long longValue4 = 0L;
   private long longValue5 = 0L;
   private int value9 = -1;
   private final Map<String, Long> stringMap = new HashMap<>();

   public AutoSell() {
      autoSell = this;
   }

   public boolean isAutoMode() {
      return ((String)this.mode.getValue()).equals("Авто");
   }

   public boolean m_7_boolean() {
      return this.isSinglePrice();
   }

   @Override
   public void onEnable() {
      C7n.m_1(false);
      C7n.m_5_void(false);
      C7n.m_6_void();
      this.resetSell();
      this.c69.m_8_void();
   }

   @Override
   public void onDisable() {
      if (this.flag) {
         C7n.m_1(false);
      }

      if (C7n.m_7_boolean()) {
         C7n.m_5_void(false);
      }

      C7n.m_6_void();
      this.resetSell();
   }

   public void resetSell() {
      this.c5 = AutoSell.ModeKind.IDLE;
      this.flag = false;
      this.longValue = 0L;
      this.text = "";
      this.value = 0;
      this.flag2 = false;
      this.text2 = "";
      this.text3 = "";
      this.item = Items.AIR;
      this.flag3 = false;
      this.value2 = 0;
      this.flag4 = false;
      this.flag5 = false;
      this.flag6 = false;
      this.value3 = 0;
      this.value4 = 0;
      this.value5 = 0;
      this.value6 = 0;
      this.value7 = 9;
      this.flag7 = false;
      this.flag8 = false;
      this.flag9 = false;
      this.value8 = 0;
      this.longValue3 = 0L;
      this.longValue4 = 0L;
      this.longValue5 = 0L;
      this.value9 = -1;
   }

   public static void m_18_void() {
      if (autoSell != null) {
         autoSell.resetSell();
      }
   }

   private void forceStop() {
      this.stopSelling(true);
   }

   private void stopSelling(boolean flag10) {
      this.resetSell();
      this.c69.m_8_void();
      if (this.isSinglePrice() || !flag10 || !this.isAutoMode() || !C7n.m_0_boolean() || !this.m_0_boolean() || !this.m_3_boolean() || !this.validateConfig()) {
         C7n.m_1(true);
      }
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (!ChatInputHelper.isTextInputOpen()) {
         if (event.getAction() == 1) {
            if (((String)this.mode.getValue()).equals("По бинду")
               && event.getKey() == (Integer)this.sellBind.getValue()
               && !this.flag
               && MinecraftAccess.MINECRAFT.player != null) {
               if (!this.isNotEmpty(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
                  ChatUtil.log("§3[AutoSell] §fВозьмите предмет в руку");
                  return;
               }

               this.validateConfig();
            }
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         boolean flag10 = C7n.m_8_boolean();
         boolean flag11 = C7n.m_0_boolean();
         if (this.isAutoMode() && this.isSinglePrice() && !this.flag) {
            if (this.c69.m_9_boolean(750L)) {
               if (C7n.m_3_boolean()) {
                  this.flag = true;
                  this.m_1_t_void();
                  return;
               }

               if (flag11) {
                  if (!this.flag2 && !this.isNotEmpty(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
                     this.m_3_boolean();
                  }

                  if (!this.validateConfig()) {
                     this.logThrottled("§c[AutoSell] §fДля режима одной цены возьмите нужный предмет в руку или дождитесь покупки AutoBuy.");
                     this.c69.m_8_void();
                  }
               }
            }
         } else if (this.isAutoMode() && !this.flag && this.c69.m_9_boolean(750L) && flag11) {
            if (this.m_3_boolean()) {
               this.validateConfig();
            } else if (flag10) {
               C7n.m_1(true);
            }
         }

         if (this.flag) {
            if (this.isSinglePrice() && this.c5 == AutoSell.ModeKind.IDLE) {
               this.m_1_j_void();
               if (!this.flag || this.c5 == AutoSell.ModeKind.IDLE) {
                  return;
               }
            }

            switch (this.c5.ordinal()) {
               case 1:
                  if (this.isSinglePrice()) {
                     if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                        this.closeContainer();
                        this.c692.m_8_void();
                        return;
                     }

                     if (!this.m_8_boolean()) {
                        this.forceStop();
                        return;
                     }

                     if (!this.hasItemToSell()) {
                        this.forceStop();
                        return;
                     }

                     this.c5 = AutoSell.ModeKind.SELLGUI_SELLING;
                     this.c692.m_8_void();
                  } else {
                     if (this.isAutoMode()) {
                        if (!this.isNotEmpty(MinecraftAccess.MINECRAFT.player.getMainHandItem()) && !this.m_3_boolean()) {
                           this.forceStop();
                           return;
                        }
                     } else if (!this.isNotEmpty(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
                        this.forceStop();
                        return;
                     }

                     if (((String)this.sellMode.getValue()).equals("По одной штуке")) {
                        this.c5 = AutoSell.ModeKind.SPLITTING;
                     } else {
                        this.c5 = AutoSell.ModeKind.SEARCHING;
                     }

                     this.c692.m_8_void();
                  }
                  break;
               case 2:
                  if (this.c692.m_9_boolean(900L)) {
                     if (this.m_9_boolean()) {
                        this.c5 = AutoSell.ModeKind.SEARCHING;
                     } else {
                        this.forceStop();
                     }

                     this.c692.m_8_void();
                  }
                  break;
               case 3:
                  if (!this.c692.m_9_boolean(432L)) {
                     break;
                  }

                  ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
                  if (itemStack.isEmpty()) {
                     this.c5 = AutoSell.ModeKind.PREPARING;
                     return;
                  }

                  if (((String)this.sellMode.getValue()).equals("По одной штуке") && itemStack.getCount() > 1) {
                     this.c5 = AutoSell.ModeKind.SPLITTING;
                     return;
                  }

                  String text4 = AhHelper.m_2_String(itemStack);
                  if (text4 != null && !text4.isEmpty()) {
                     Long longObj = this.stringMap.get(text4);
                     if (longObj != null && System.currentTimeMillis() < longObj) {
                        this.stopSelling(false);
                        return;
                     }

                     this.text = text4;
                     if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                        if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
                           this.value9 = ((ChestMenu)containerScreen.getMenu()).containerId;
                        }

                        this.closeContainer();
                        this.c692.m_8_void();
                        return;
                     }

                     this.searchAuction();
                     this.c5 = AutoSell.ModeKind.SCANNING;
                     this.c692.m_8_void();
                     this.c693.m_8_void();
                     break;
                  }

                  ChatUtil.log("§3[AutoSell] Не удалось определить имя предмета");
                  this.forceStop();
                  return;
               case 4:
                  if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen2 && this.m_3_boolean(containerScreen2)) {
                     if (this.c692.m_9_boolean(525L)) {
                        try {
                           this.m_3_void(containerScreen2);
                        } catch (Exception e) {
                           this.forceStop();
                        }
                     }
                  } else {
                     if (this.c693.m_9_boolean(1350L)) {
                        if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                           this.closeContainer();
                        } else {
                           this.searchAuction();
                        }

                        this.c693.m_8_void();
                     }

                     if (this.c692.m_9_boolean(9750L)) {
                        this.forceStop();
                     }
                  }
                  break;
               case 5:
                  if (this.c692.m_9_boolean(432L)) {
                     if (this.longValue > 0L) {
                        this.sellOnAuction();
                     }

                     this.c5 = AutoSell.ModeKind.FINISHING;
                     this.c692.m_8_void();
                     this.c693.m_8_void();
                  }
                  break;
               case 6:
                  if (this.longValue > 0L && this.c693.m_9_boolean(1350L)) {
                     this.sellOnAuction();
                     this.c693.m_8_void();
                  }

                  if (this.c692.m_9_boolean(15000L)) {
                     ChatUtil.log("§e[AutoSell] §fНет ответа от аукциона, возвращаю AutoBuy.");
                     this.stopSelling(false);
                  }
                  break;
               case 7:
                  this.m_2_void(false);
                  break;
               case 8:
                  this.m_5_void(false);
                  break;
               case 9:
                  this.m_1_s_void();
                  break;
               case 10:
                  this.m_1t_void();
                  break;
               case 11:
                  this.m_2_void();
                  break;
               case 12:
                  this.m_2_void(true);
                  break;
               case 13:
                  this.m_5_void(true);
            }
         }
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
         String text4 = clientboundSystemChatPacket.content().getString();
         if (!this.m_0_boolean(text4)) {
            if (this.m_4_boolean(text4)) {
               C7n.m_0_void();
               if (this.flag) {
                  if (this.isSinglePrice()) {
                     int value10 = Math.max(1, this.value4);
                     this.value5 += value10;
                     if (this.flag8) {
                        this.value7 = Math.max(1, Math.min(this.value7, this.value5));
                     }

                     this.value6 = Math.max(0, this.value6 - value10);
                     this.value4 = 0;
                     this.flag8 = false;
                     this.flag5 = false;
                     this.flag4 = true;
                     this.longValue4 = System.currentTimeMillis();
                     return;
                  }

                  this.stopSelling(true);
               }
            } else if (text4.contains("Не удалось выставить") && text4.contains("освободите хранилище")) {
               C7n.m_5_void();
               if (this.flag) {
                  if (this.isSinglePrice()) {
                     this.flag5 = true;
                     this.value4 = 0;
                     this.flag8 = false;
                     return;
                  }

                  ChatUtil.log("§c[AutoSell] Хранилище заполнено. Продажа приостановлена.");
                  this.stopSelling(false);
               }
            } else if ((text4.contains("Максимальная цена") || text4.contains("максимальная цена")) && this.flag) {
               if (!this.text.isEmpty()) {
                  this.stringMap.entrySet().removeIf(entry -> entry.getValue() < System.currentTimeMillis());
                  this.stringMap.put(this.text, System.currentTimeMillis() + 180000L);
               }

               ChatUtil.log("§c[AutoSell] Цена превышает лимит $10М, предмет отложен.");
               this.stopSelling(false);
            }
         } else {
            C7n.m_7_void();
            if (this.isSinglePrice() && (this.flag || this.value5 > 0)) {
               int value11 = this.parseAmount(text4);
               this.value5 = Math.max(0, this.value5 - value11);
               this.value6 += value11;
               this.longValue4 = System.currentTimeMillis();
               if (this.flag) {
                  this.c5 = AutoSell.ModeKind.IDLE;
                  this.c692.m_8_void();
               }

               return;
            }

            if (this.flag) {
               this.stopSelling(false);
            }
         }
      }
   }

   private boolean validateConfig() {
      if (this.isSinglePrice()) {
         if (this.getSinglePrice() <= 0L) {
            ChatUtil.log("§c[AutoSell] §fЦена одной продажи не задана.");
            return false;
         }

         if (!this.m_8_boolean()) {
            return false;
         }
      }

      if (!C7n.m_4_boolean()) {
         return false;
      }

      if (this.isSinglePrice()) {
         this.value5 = 0;
         this.value6 = 0;
         this.value7 = 9;
         this.longValue4 = 0L;
         this.value4 = 0;
         this.flag8 = false;
      }

      this.flag = true;
      this.c5 = AutoSell.ModeKind.PREPARING;
      this.c692.m_8_void();
      this.c693.m_8_void();
      return true;
   }

   private void searchAuction() {
      if (MinecraftAccess.MINECRAFT.player != null && !this.text.isEmpty()) {
         MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah search " + this.text);
      }
   }

   private void sellOnAuction() {
      if (MinecraftAccess.MINECRAFT.player != null && this.longValue > 0L) {
         MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah sell " + this.longValue);
      }
   }

   private void m_1_j_void() {
      if (this.flag5) {
         this.m_1_t_void();
      } else if (this.value6 > 0 && this.hasItemToSell()) {
         if (this.value5 < this.value7) {
            this.c5 = AutoSell.ModeKind.SELLGUI_SELLING;
            this.c692.m_8_void();
         } else if (System.currentTimeMillis() - this.longValue4 >= 5000L) {
            this.m_1_t_void();
         }
      } else if (this.value5 > 0) {
         if (this.hasItemToSell() && this.value5 < this.value7) {
            this.c5 = AutoSell.ModeKind.SELLGUI_SELLING;
            this.c692.m_8_void();
         } else if (System.currentTimeMillis() - this.longValue4 >= 5000L) {
            this.m_1_t_void();
         }
      } else if (this.hasItemToSell()) {
         this.c5 = AutoSell.ModeKind.SELLGUI_SELLING;
         this.c692.m_8_void();
      } else {
         this.stopAll(true);
      }
   }

   private void m_2_void(boolean flag10) {
      if (this.c692.m_9_boolean(432L)) {
         if (this.flag5) {
            this.m_1_t_void();
         } else {
            long longValue6 = this.getSinglePrice();
            if (longValue6 <= 0L) {
               ChatUtil.log("§c[AutoSell] §fЦена одной продажи не задана.");
               this.stopAll(false);
            } else if (this.m_8_boolean() && this.hasItemToSell()) {
               if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen && this.m_0_boolean(containerScreen)) {
                  this.m_2(containerScreen, flag10);
               } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
                  this.closeContainer();
                  this.c692.m_8_void();
               } else if (this.isCooldownOver()) {
                  this.reset();
                  this.flag4 = false;
                  this.flag5 = false;
                  this.sellGui(longValue6);
                  this.c5 = flag10 ? AutoSell.ModeKind.RESALE_WAIT_SELL_RESULT : AutoSell.ModeKind.SELLGUI_WAITING_RESULT;
                  this.c692.m_8_void();
                  this.c693.m_8_void();
               }
            } else {
               this.c5 = AutoSell.ModeKind.IDLE;
               this.c692.m_8_void();
            }
         }
      }
   }

   private void m_5_void(boolean flag10) {
      if (this.flag5) {
         this.m_1_t_void();
      } else if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen && this.m_0_boolean(containerScreen)) {
         this.m_2(containerScreen, flag10);
      } else if (this.flag4) {
         this.flag4 = false;
         this.reset();
         this.c5 = this.value5 > 0 ? AutoSell.ModeKind.IDLE : (this.hasItemToSell() ? (flag10 ? AutoSell.ModeKind.RESALE_SELLING : AutoSell.ModeKind.SELLGUI_SELLING) : AutoSell.ModeKind.IDLE);
         this.c692.m_8_void();
      } else if (this.c692.m_9_boolean(18000L)) {
         this.closeContainer();
         this.value4 = 0;
         this.c5 = this.value5 > 0 ? AutoSell.ModeKind.IDLE : (this.hasItemToSell() ? (flag10 ? AutoSell.ModeKind.RESALE_SELLING : AutoSell.ModeKind.SELLGUI_SELLING) : AutoSell.ModeKind.IDLE);
         if (this.c5 == AutoSell.ModeKind.IDLE && this.value5 <= 0) {
            this.stopAll(false);
         }

         this.c692.m_8_void();
      }
   }

   private void m_1_s_void() {
      long longValue6 = System.currentTimeMillis();
      if (longValue6 >= this.longValue5) {
         if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
            this.closeContainer();
            this.longValue5 = longValue6 + 525L;
         } else if (!this.flag9 && !this.flag6 && this.hasItemToSell()) {
            this.c5 = AutoSell.ModeKind.RESALE_SELLING;
            this.c692.m_8_void();
         } else {
            this.flag6 = false;
            if (MinecraftAccess.MINECRAFT.player == null) {
               this.stopAll(false);
            } else {
               this.value8++;
               this.sendAhCommand(MinecraftAccess.MINECRAFT.player.getName().getString(), this.value8);
               this.value3 = 0;
               this.c5 = AutoSell.ModeKind.RESALE_WAITING_OWN_AH;
               this.c692.m_8_void();
               this.c693.m_8_void();
            }
         }
      }
   }

   private void m_1t_void() {
      if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen && this.m_6_boolean(containerScreen)) {
         this.c5 = AutoSell.ModeKind.RESALE_TAKE_ITEM;
         this.c692.m_8_void();
      } else if (this.c693.m_9_boolean(27000L)) {
         ChatUtil.log("§c[AutoSell] §fТаймаут поиска своих товаров.");
         this.stopAll(false);
      } else if (MinecraftAccess.MINECRAFT.gui.screen() != null && this.c692.m_9_boolean(1800L)) {
         this.closeContainer();
         this.c5 = AutoSell.ModeKind.RESALE_SEARCH_OWN_AH;
         this.longValue5 = System.currentTimeMillis() + 525L;
         this.c692.m_8_void();
      } else if (this.c692.m_9_boolean(6000L)) {
         this.c5 = AutoSell.ModeKind.RESALE_SEARCH_OWN_AH;
         this.longValue5 = System.currentTimeMillis();
         this.c692.m_8_void();
      }
   }

   private void m_2_void() {
      if (this.c692.m_9_boolean(300L)) {
         if (MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen containerScreen) {
            if (!this.m_6_boolean(containerScreen)) {
               if (this.c692.m_9_boolean(15000L)) {
                  this.closeContainer();
                  this.c5 = AutoSell.ModeKind.RESALE_SEARCH_OWN_AH;
                  this.c692.m_8_void();
               }
            } else {
               int value10 = this.m_9_int(containerScreen);
               if (value10 != -1) {
                  this.clickContainerSlot(containerScreen, value10, 0, ContainerInput.QUICK_MOVE);
                  this.value3 = 0;
                  this.c692.m_8_void();
               } else if (this.value3++ < 2) {
                  this.c692.m_8_void();
               } else {
                  this.closeContainer();
                  this.flag5 = false;
                  if (!this.hasItemToSell()) {
                     ChatUtil.log("§e[AutoSell] §fСвои лоты не найдены, предметов в инвентаре нет.");
                     this.stopAll(true);
                  } else {
                     this.flag9 = false;
                     this.c5 = AutoSell.ModeKind.RESALE_SELLING;
                     this.c692.m_8_void();
                  }
               }
            }
         } else {
            this.c5 = AutoSell.ModeKind.RESALE_SEARCH_OWN_AH;
            this.c692.m_8_void();
         }
      }
   }

   private void m_2(ContainerScreen containerScreen, boolean flag10) {
      AbstractContainerMenu abstractContainerMenu = containerScreen.getMenu();
      int value10 = this.getChestSlotCount(containerScreen);
      if (this.flag3) {
         if (this.c692.m_9_boolean(2250L)) {
            this.closeContainer();
         }
      } else {
         int value11 = 0;

         for (int i = this.getSellAmount(flag10); value11 < 2 && this.value2 < i; value11++) {
            int value12 = this.m_0_int(containerScreen);
            int value13 = this.m_7_int(abstractContainerMenu, value10);
            if (value12 == -1) {
               this.flag7 = true;
               break;
            }

            if (value13 == -1) {
               break;
            }

            if (((String)this.singlePriceListing.getValue()).equals("По одной штуке")) {
               if (!this.clickContainerSlot(abstractContainerMenu, value13, value12)) {
                  break;
               }
            } else {
               MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(abstractContainerMenu.containerId, value13, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
               MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(abstractContainerMenu.containerId, value12, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
            }

            this.value2++;
         }

         if (value11 > 0) {
            this.c692.m_8_void();
         } else if (this.value2 > 0) {
            int value14 = this.m_2(containerScreen);
            if (value14 != -1) {
               this.clickContainerSlot(containerScreen, value14, 0, ContainerInput.PICKUP);
               this.flag3 = true;
               this.value4 = this.value2;
               this.flag8 = this.flag7;
               if (this.flag7) {
                  this.value7 = Math.max(1, this.value2);
               }

               this.c692.m_8_void();
            } else if (this.c692.m_9_boolean(4500L)) {
               this.closeContainer();
               this.c5 = flag10 ? AutoSell.ModeKind.RESALE_SELLING : AutoSell.ModeKind.SELLGUI_SELLING;
               this.c692.m_8_void();
            }
         } else {
            this.closeContainer();
            if (this.flag7 && this.value5 > 0) {
               this.value7 = Math.max(1, Math.min(this.value7, this.value5));
               this.logThrottled("§e[AutoSell] §fВ sellgui нет свободных слотов, жду перевыставление.");
            }

            this.c5 = flag10 ? AutoSell.ModeKind.RESALE_SEARCH_OWN_AH : AutoSell.ModeKind.IDLE;
            if (this.c5 == AutoSell.ModeKind.IDLE && this.value5 <= 0) {
               this.stopAll(true);
            } else {
               this.c692.m_8_void();
            }
         }
      }
   }

   private void m_1_t_void() {
      this.flag5 = false;
      this.flag4 = false;
      this.flag6 = true;
      this.flag9 = true;
      this.value3 = 0;
      this.value5 = 0;
      this.value6 = 0;
      this.flag7 = false;
      this.flag8 = false;
      this.value8 = 0;
      this.longValue5 = System.currentTimeMillis() + 525L;
      this.reset();
      this.c5 = AutoSell.ModeKind.RESALE_SEARCH_OWN_AH;
      this.c692.m_8_void();
      this.c693.m_8_void();
      this.closeContainer();
      C7n.m_1(false);
      C7n.m_6_boolean();
   }

   private void stopAll(boolean flag10) {
      if (C7n.m_7_boolean()) {
         C7n.m_5_void(flag10);
      }

      this.stopSelling(flag10);
   }

   private boolean isSinglePrice() {
      return ((String)this.sellMode.getValue()).equals("Одна цена");
   }

   private void logThrottled(String text4) {
      long longValue6 = System.currentTimeMillis();
      if (longValue6 - this.longValue3 >= 3000L) {
         this.longValue3 = longValue6;
         ChatUtil.log(text4);
      }
   }

   private boolean m_8_boolean() {
      if (this.flag2) {
         return true;
      } else if (MinecraftAccess.MINECRAFT.player != null && this.isNotEmpty(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
         this.rememberItem(MinecraftAccess.MINECRAFT.player.getMainHandItem());
         return true;
      } else {
         return false;
      }
   }

   private void rememberItem(ItemStack itemStack) {
      if (this.isNotEmpty(itemStack)) {
         this.flag2 = true;
         this.text2 = this.getItemDisplayKey(itemStack);
         this.text3 = itemStack.getHoverName().getString();
         this.item = itemStack.getItem();
      }
   }

   private boolean hasItemToSell() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return false;
      }

      if (this.isSellTarget(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
         return true;
      }

      for (int i = 0; i < 36; i++) {
         if (this.isSellTarget(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
            return true;
         }
      }

      return false;
   }

   private boolean isSellTarget(ItemStack itemStack) {
      if (this.isNotEmpty(itemStack) && this.flag2) {
         if (this.item != Items.AIR && itemStack.getItem() != this.item) {
            return false;
         }

         String text4 = this.normalizeSmallCaps(this.getItemDisplayKey(itemStack));
         String text5 = this.normalizeSmallCaps(this.text2);
         String text6 = this.normalizeSmallCaps(this.text3);
         if (text5.isEmpty() && text6.isEmpty()) {
            return true;
         }

         if (!text4.isEmpty()) {
            if (text4.equals(text5) || text4.equals(text6) || !text5.isEmpty() && (text4.contains(text5) || text5.contains(text4))) {
               return true;
            }

            if (!text6.isEmpty()) {
               if (text4.contains(text6)) {
                  return true;
               }

               if (text6.contains(text4)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private int m_7_int(AbstractContainerMenu abstractContainerMenu, int value10) {
      for (int i = value10; i < abstractContainerMenu.slots.size(); i++) {
         Slot slot = abstractContainerMenu.getSlot(i);
         if (slot.hasItem() && this.isSellTarget(slot.getItem())) {
            return i;
         }
      }

      return -1;
   }

   private int m_9_int(ContainerScreen containerScreen) {
      int value10 = this.getChestSlotCount(containerScreen);

      for (int i = 0; i < value10; i++) {
         Slot slot = ((ChestMenu)containerScreen.getMenu()).getSlot(i);
         if (slot.hasItem() && this.isSellTarget(slot.getItem())) {
            return i;
         }
      }

      return -1;
   }

   private int m_0_int(ContainerScreen containerScreen) {
      int value10 = Math.min(9, this.getChestSlotCount(containerScreen));

      for (int i = 0; i < value10; i++) {
         Slot slot = ((ChestMenu)containerScreen.getMenu()).getSlot(i);
         if (!slot.hasItem()) {
            return i;
         }
      }

      return -1;
   }

   private int m_2(ContainerScreen containerScreen) {
      int value10 = this.getChestSlotCount(containerScreen);
      int value11 = -1;

      for (int i = value10 - 1; i >= 0; i--) {
         Slot slot = ((ChestMenu)containerScreen.getMenu()).getSlot(i);
         if (slot.hasItem()) {
            ItemStack itemStack = slot.getItem();
            String text4 = this.stripFormatting(itemStack.getHoverName().getString()).toLowerCase(Locale.ROOT);
            if (itemStack.is(Items.DYE.lime())
               || itemStack.is(Items.DYE.green())
               || itemStack.is(Items.STAINED_GLASS_PANE.lime())
               || itemStack.is(Items.STAINED_GLASS_PANE.green())) {
               return i;
            }

            if (text4.contains("выстав") || text4.contains("продать") || text4.contains("подтверд")) {
               value11 = i;
            }
         }
      }

      return value11;
   }

   private boolean m_0_boolean(ContainerScreen containerScreen) {
      if (containerScreen == null) {
         return false;
      }

      String text4 = this.stripFormatting(containerScreen.getTitle().getString()).toLowerCase(Locale.ROOT);
      return text4.contains("продажа") || text4.contains("sellgui") || text4.contains("sell gui");
   }

   private boolean m_3_boolean(ContainerScreen containerScreen) {
      if (containerScreen != null && ((ChestMenu)containerScreen.getMenu()).containerId != this.value9) {
         if (AuctionTitleMatcher.matches(containerScreen)) {
            return true;
         }

         int value10 = Math.min(45, ((ChestMenu)containerScreen.getMenu()).slots.size());

         for (int i = 0; i < value10; i++) {
            if (getSlotPrice(((ChestMenu)containerScreen.getMenu()).getSlot(i)) > 0L) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean m_6_boolean(ContainerScreen containerScreen) {
      if (containerScreen != null && MinecraftAccess.MINECRAFT.player != null) {
         String text4 = this.stripFormatting(containerScreen.getTitle().getString()).toLowerCase(Locale.ROOT);
         String text5 = this.stripFormatting(MinecraftAccess.MINECRAFT.player.getName().getString()).toLowerCase(Locale.ROOT);
         return AuctionTitleMatcher.matches(containerScreen) || text4.contains(text5) || text4.contains("мои товары") || text4.contains("мои предметы") || text4.contains("поиск:");
      } else {
         return false;
      }
   }

   private int getChestSlotCount(ContainerScreen containerScreen) {
      int value10 = ((ChestMenu)containerScreen.getMenu()).getRowCount();
      int value11 = ((ChestMenu)containerScreen.getMenu()).slots.size();
      return Math.max(0, Math.min(value10 * 9, value11));
   }

   private void clickContainerSlot(ContainerScreen containerScreen, int value10, int value11, ContainerInput containerInput) {
      MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(((ChestMenu)containerScreen.getMenu()).containerId, value10, value11, containerInput, MinecraftAccess.MINECRAFT.player);
   }

   private boolean clickContainerSlot(AbstractContainerMenu abstractContainerMenu, int value10, int value11) {
      if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(abstractContainerMenu.containerId, value10, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
         if (abstractContainerMenu.getCarried().isEmpty()) {
            return false;
         }

         MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(abstractContainerMenu.containerId, value11, 1, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
         if (!abstractContainerMenu.getCarried().isEmpty()) {
            MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(abstractContainerMenu.containerId, value10, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
         }

         return true;
      } else {
         return false;
      }
   }

   private void sendAhCommand(String text4, int value10) {
      if (MinecraftAccess.MINECRAFT.player != null && text4 != null && !text4.isBlank()) {
         MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah " + text4.trim());
      }
   }

   private boolean isCooldownOver() {
      return System.currentTimeMillis() - this.longValue2 >= 5000L;
   }

   private void sellGui(long longValue6) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah sellgui " + longValue6);
         this.longValue2 = System.currentTimeMillis();
      }
   }

   private void reset() {
      this.flag3 = false;
      this.value2 = 0;
      this.value4 = 0;
      this.flag7 = false;
      this.flag8 = false;
   }

   private long getSinglePrice() {
      return this.parsePrice((String)this.singleSalePrice.getValue());
   }

   private int getSellAmount(boolean flag10) {
      if (flag10) {
         return Integer.MAX_VALUE;
      } else {
         int value10 = Math.max(1, this.value7 - this.value5);
         if (this.value6 > 0) {
            return Math.max(1, Math.min(this.value6, value10));
         } else {
            return this.value5 > 0 ? value10 : Integer.MAX_VALUE;
         }
      }
   }

   private int parseAmount(String text4) {
      if (text4 == null) {
         return 1;
      }

      String text5 = this.stripFormatting(text4).toLowerCase(Locale.ROOT);
      int value10 = text5.indexOf(" за ");
      if (value10 > 0) {
         text5 = text5.substring(0, value10);
      }

      int value11 = this.m_2_int(text5, "x");
      if (value11 > 0) {
         return value11;
      }

      value11 = this.m_2_int(text5, "х");
      if (value11 > 0) {
         return value11;
      }

      value11 = this.m_0(text5, "предмет");
      if (value11 > 0) {
         return value11;
      }

      value11 = this.m_0(text5, "шт");
      return value11 > 0 ? value11 : 1;
   }

   private int m_2_int(String text4, String text5) {
      int value10 = text4.indexOf(text5);
      if (value10 < 0) {
         return 0;
      }

      String text6 = text4.substring(value10 + text5.length()).replaceFirst("[^0-9]*", "").replaceFirst("[^0-9].*$", "");
      return this.parseCount(text6);
   }

   private int m_0(String text4, String text5) {
      int value10 = text4.indexOf(text5);
      if (value10 <= 0) {
         return 0;
      }

      String text6 = text4.substring(0, value10).trim();
      String text7 = text6.replaceFirst("^.*?([0-9]+)\\s*$", "$1");
      return text7.equals(text6) && !text7.matches("[0-9]+") ? 0 : this.parseCount(text7);
   }

   private int parseCount(String text4) {
      if (text4 != null && !text4.isBlank()) {
         try {
            return Math.max(0, Integer.parseInt(text4));
         } catch (NumberFormatException numberFormatException) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private long parsePrice(String text4) {
      if (text4 == null) {
         return 0L;
      }

      String text5 = text4.toLowerCase(Locale.ROOT).replace(" ", "").replace("_", "").replace(",", "").replace(".", "");
      long longValue6 = 1L;
      if (text5.endsWith("тысяч")) {
         longValue6 = 1000L;
         text5 = text5.substring(0, text5.length() - 5);
      } else if (text5.endsWith("тысячи")) {
         longValue6 = 1000L;
         text5 = text5.substring(0, text5.length() - 6);
      } else if (text5.endsWith("тысяча")) {
         longValue6 = 1000L;
         text5 = text5.substring(0, text5.length() - 6);
      } else if (text5.endsWith("тыс")) {
         longValue6 = 1000L;
         text5 = text5.substring(0, text5.length() - 3);
      } else if (text5.endsWith("k") || text5.endsWith("к")) {
         longValue6 = 1000L;
         text5 = text5.substring(0, text5.length() - 1);
      } else if (text5.endsWith("m") || text5.endsWith("м")) {
         longValue6 = 1000000L;
         text5 = text5.substring(0, text5.length() - 1);
      }

      String text6 = text5.replaceAll("[^0-9]", "");
      if (text6.isEmpty()) {
         return 0L;
      }

      try {
         return Math.multiplyExact(Long.parseLong(text6), longValue6);
      } catch (NumberFormatException | ArithmeticException e) {
         return 0L;
      }
   }

   private void closeContainer() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }
   }

   private boolean m_9_boolean() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player.inventoryMenu != null) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
         if (itemStack.isEmpty()) {
            return false;
         } else if (itemStack.getCount() == 1) {
            return true;
         } else {
            int value10 = this.findFreeSlot();
            if (value10 == -1) {
               ChatUtil.log("§3[AutoSell] Нет места для стака");
               return false;
            } else {
               int value11 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() + 36;
               MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(MinecraftAccess.MINECRAFT.player.inventoryMenu.containerId, value11, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
               MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(MinecraftAccess.MINECRAFT.player.inventoryMenu.containerId, value11, 1, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
               MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(MinecraftAccess.MINECRAFT.player.inventoryMenu.containerId, value10, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private int findFreeSlot() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return -1;
      }

      for (int i = 9; i < 36; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).isEmpty()) {
            return i;
         }
      }

      for (int i2 = 36; i2 < 45; i2++) {
         if (i2 != MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() + 36 && MinecraftAccess.MINECRAFT.player.getInventory().getItem(i2 - 36).isEmpty()) {
            return i2;
         }
      }

      return -1;
   }

   private boolean m_0_boolean() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return false;
      }

      if (this.isNotEmpty(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
         return true;
      }

      for (int i = 0; i < 36; i++) {
         if (this.isNotEmpty(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
            return true;
         }
      }

      return false;
   }

   private boolean m_3_boolean() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player.inventoryMenu != null) {
         if (this.m_9_boolean(MinecraftAccess.MINECRAFT.player.getMainHandItem())) {
            return true;
         }

         for (int i = 0; i < 9; i++) {
            if (this.m_9_boolean(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i))) {
               HotbarUtil.m_9_void(i);
               this.c692.m_8_void();
               return true;
            }
         }

         for (int i2 = 9; i2 < 36; i2++) {
            if (this.m_9_boolean(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i2))) {
               MinecraftAccess.MINECRAFT
                  .gameMode
                  .handleContainerInput(
                     MinecraftAccess.MINECRAFT.player.inventoryMenu.containerId, i2, MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot(), ContainerInput.SWAP, MinecraftAccess.MINECRAFT.player
                  );
               this.c692.m_8_void();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isNotEmpty(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() && itemStack.getItem() != Items.AIR;
   }

   private boolean m_9_boolean(ItemStack itemStack) {
      if (!this.isNotEmpty(itemStack)) {
         return false;
      }

      String text4 = this.getItemDisplayKey(itemStack);
      if (text4.isEmpty()) {
         return false;
      }

      Long longObj = this.stringMap.get(text4);
      return longObj == null || System.currentTimeMillis() >= longObj;
   }

   private boolean m_2_boolean(int value10) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player.inventoryMenu != null) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
         if (itemStack.isEmpty()) {
            return false;
         }

         int value11 = itemStack.getCount();
         if (value11 <= value10) {
            return true;
         }

         int value12 = this.findFreeSlot();
         if (value12 == -1) {
            ChatUtil.log("§3[AutoSell] Нет места для стака");
            return false;
         }

         int value13 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() + 36;
         MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(MinecraftAccess.MINECRAFT.player.inventoryMenu.containerId, value13, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);

         for (int i = 0; i < value10; i++) {
            MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(MinecraftAccess.MINECRAFT.player.inventoryMenu.containerId, value13, 1, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
         }

         MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(MinecraftAccess.MINECRAFT.player.inventoryMenu.containerId, value12, 0, ContainerInput.PICKUP, MinecraftAccess.MINECRAFT.player);
         return true;
      } else {
         return false;
      }
   }

   private void m_3_void(ContainerScreen containerScreen) {
      double doubleValue = Double.MAX_VALUE;
      boolean flag10 = false;
      int value10 = Math.min(45, ((ChestMenu)containerScreen.getMenu()).slots.size());

      for (int i = 0; i < value10; i++) {
         Slot slot = ((ChestMenu)containerScreen.getMenu()).getSlot(i);
         if (slot.hasItem()) {
            long longValue6 = getSlotPrice(slot);
            if (longValue6 > 0L) {
               int value11 = Math.max(1, slot.getItem().getCount());
               double doubleValue2 = (double)longValue6 / value11;
               if (doubleValue2 < doubleValue) {
                  doubleValue = doubleValue2;
                  flag10 = true;
               }
            }
         }
      }

      int value12 = MinecraftAccess.MINECRAFT.player != null ? Math.max(1, MinecraftAccess.MINECRAFT.player.getMainHandItem().getCount()) : 1;
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      if (flag10) {
         long longValue7 = ((String)this.sellMode.getValue()).equals("Все сразу") ? (long)(doubleValue * value12) : (long)doubleValue;
         long longValue8;
         if (this.isAutoMode()) {
            longValue8 = longValue7 - 1L;
            if (longValue8 < 1L) {
               longValue8 = 1L;
            }
         } else {
            double doubleValue3 = 1.0 + ((Float)this.markupPercent.getValue()).floatValue() / 100.0;
            longValue8 = (long)(longValue7 * doubleValue3);
         }

         if (((String)this.sellMode.getValue()).equals("Все сразу") && value12 > 1 && longValue8 > 10000000L) {
            double doubleValue4 = doubleValue;
            if (!this.isAutoMode()) {
               doubleValue4 = doubleValue * (1.0 + ((Float)this.markupPercent.getValue()).floatValue() / 100.0);
            }

            long longValue9 = (long)Math.floor(doubleValue4);
            int value13 = (int)Math.max(1L, longValue9 > 0L ? 10000000L / longValue9 : value12);
            value13 = Math.min(value13, value12);
            if (value13 < value12) {
               if (!this.m_2_boolean(value13)) {
                  this.stopSelling(false);
                  return;
               }

               long longValue10 = longValue9 * value13;
               if (this.isAutoMode()) {
                  if (--longValue10 < 1L) {
                     longValue10 = 1L;
                  }
               }

               this.longValue = longValue10;
               this.c5 = AutoSell.ModeKind.SELLING;
               this.c692.m_8_void();
               return;
            }
         }

         this.longValue = longValue8;
         this.c5 = AutoSell.ModeKind.SELLING;
         this.c692.m_8_void();
      } else {
         this.stringMap.entrySet().removeIf(entry -> entry.getValue() < System.currentTimeMillis());
         this.stringMap.put(this.text, System.currentTimeMillis() + 180000L);
         ChatUtil.log("§3[AutoSell] Конкурентов нет, ставьте вручную");
         this.stopSelling(false);
      }
   }

   private String getItemDisplayKey(ItemStack itemStack) {
      String text4 = itemStack.getHoverName().getString();
      if (text4.contains("TIER WHITE")) {
         return "Тнт вайт";
      }

      if (text4.contains("TIER BLACK")) {
         return "Тнт блек";
      }

      if (text4.contains("Рассадник монстров")) {
         return "Спавнер";
      }

      if (text4.contains("Прогрузчик чанков [1x1]")) {
         return "Прогрузчик чанков";
      }

      if (text4.contains("Яйцо призыва зомби-крестьянина")) {
         return "Яйцо зомби-крестьянина";
      }

      String text5 = text4.replaceAll("(?i)§.", "")
         .replaceAll("(?i)&.", "")
         .replace(' ', ' ')
         .replaceAll("\\[[^\\]]*]", " ")
         .replaceAll("[★✦✧✪✫✬✭✮✯✰❄☃⚒☠❤❣♕♛♜♞♟��]", " ")
         .replace("xxx", " ")
         .replaceAll("\\s+", " ")
         .trim();
      if (text5.isEmpty()) {
         text5 = this.stripFormatting(itemStack.getItem().getName(itemStack).getString());
      }

      return text5;
   }

   private String normalizeSmallCaps(String text4) {
      String text5 = this.stripFormatting(text4);
      text5 = text5.replace("ᴀ", "a")
         .replace("ʙ", "b")
         .replace("ᴄ", "c")
         .replace("ᴅ", "d")
         .replace("ᴇ", "e")
         .replace("ғ", "f")
         .replace("ɢ", "g")
         .replace("ʜ", "h")
         .replace("ɪ", "i")
         .replace("ᴊ", "j")
         .replace("ᴋ", "k")
         .replace("ʟ", "l")
         .replace("ᴍ", "m")
         .replace("ɴ", "n")
         .replace("ᴏ", "o")
         .replace("ᴘ", "p")
         .replace("ǫ", "q")
         .replace("ʀ", "r")
         .replace("ѕ", "s")
         .replace("ᴛ", "t")
         .replace("ᴜ", "u")
         .replace("ᴠ", "v")
         .replace("ᴡ", "w")
         .replace("х", "x")
         .replace("ʏ", "y")
         .replace("ᴢ", "z");
      text5 = text5.replace("инфинити", "infinity").replace("этернити", "eternity").replace("етернити", "eternity").replace("стингер", "stinger");
      return text5.trim().toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", "");
   }

   private String stripFormatting(String text4) {
      return text4 == null ? "" : text4.replaceAll("§.", "").replace(' ', ' ').trim();
   }

   private boolean m_4_boolean(String text4) {
      if (text4 == null) {
         return false;
      }

      String text5 = this.stripFormatting(text4).toLowerCase(Locale.ROOT);
      return text5.contains("выстав") && text5.contains("продаж");
   }

   private boolean m_0_boolean(String text4) {
      if (text4 == null) {
         return false;
      }

      String text5 = this.stripFormatting(text4).toLowerCase(Locale.ROOT);
      return !text5.contains("у вас купили") || !text5.contains("на /ah") && !text5.contains(" за ")
         ? text5.contains("купил у вас") && text5.contains(" за ") && (text5.contains("¤") || text5.contains("$"))
         : true;
   }

   public static long getSlotPrice(Slot slot) {
      if (!slot.hasItem()) {
         return 0L;
      }

      ItemStack itemStack = slot.getItem();
      ItemLore itemLore = itemStack.get(DataComponents.LORE);
      if (itemLore != null) {
         for (Component component : itemLore.lines()) {
            String text4 = component.getString();
            if (text4.contains("$") || text4.contains("Цена")) {
               String text5 = text4.replaceAll("[^0-9]", "");
               if (!text5.isEmpty()) {
                  try {
                     return Long.parseLong(text5);
                  } catch (NumberFormatException numberFormatException) {
                  }
               }
            }
         }
      }

      return 0L;
   }

   private enum ModeKind {
      IDLE,
      PREPARING,
      SPLITTING,
      SEARCHING,
      SCANNING,
      SELLING,
      FINISHING,
      SELLGUI_SELLING,
      SELLGUI_WAITING_RESULT,
      RESALE_SEARCH_OWN_AH,
      RESALE_WAITING_OWN_AH,
      RESALE_TAKE_ITEM,
      RESALE_SELLING,
      RESALE_WAIT_SELL_RESULT;

      private static String a(byte[] byteArray) {
         int value10 = 0;
         int value11;
         char[] charArray = new char[value11 = byteArray.length];

         for (int i = 0; i < value11; i++) {
            int value12;
            if ((value12 = 255 & byteArray[i]) < 192) {
               charArray[value10++] = (char)value12;
            } else if (value12 < 224) {
               char ch = (char)((char)(value12 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value10++] = ch;
            } else if (i < value11 - 2) {
               char ch2 = (char)((char)(value12 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value10++] = ch2;
            }
         }

         return new String(charArray, 0, value10);
      }
   }
}
