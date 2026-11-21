package wtf.onetap.autobuy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Locale;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

public class AuctionSeller implements MinecraftAccess {
   private final TimeHelper timeHelper;
   private final Deque<AuctionSeller.StateHolder> deque;
   private AuctionSeller.ModeKind c2;
   private boolean flag;
   private boolean flag2;
   private int value;
   private long longValue;
   private int value2;
   private AuctionSeller.StateHolder c1;
   private boolean flag3;
   private long longValue2;
   private boolean flag4;

   public AuctionSeller() {
      this.timeHelper = new TimeHelper();
      this.deque = new ArrayDeque<>();
      this.c2 = AuctionSeller.ModeKind.IDLE;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void m_1_void() {
      this.flag = false;
      this.c2 = AuctionSeller.ModeKind.IDLE;
      this.flag2 = false;
      this.deque.clear();
      this.c1 = null;
      this.flag3 = false;
      this.longValue2 = 0L;
      this.value2 = 0;
      this.longValue = 0L;
      this.value = 0;
      this.timeHelper.reset();
   }

   public void m_3_void() {
      this.flag4 = false;
   }

   public void m_6_void() {
      if (this.flag) {
         if (this.c2 == AuctionSeller.ModeKind.WORKING) {
            this.m_4_void();
         }
      } else if (!this.flag4) {
         if (countSellable() >= 3) {
            this.m_2_void(3);
         }
      }
   }

   public void m_2_void(int value3) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (!this.flag4) {
            this.deque.clear();
            this.m_0_void();
            if (this.deque.size() < Math.max(1, value3)) {
               this.deque.clear();
            } else {
               this.flag2 = AuctionConfig.getConfig().isFlag() || this.flag2;
               this.flag = true;
               this.c2 = AuctionSeller.ModeKind.WORKING;
               this.value2 = 0;
               this.c1 = null;
               this.flag3 = false;
               this.longValue2 = 0L;
               this.longValue = System.currentTimeMillis();
               this.timeHelper.reset();
            }
         }
      }
   }

   public void m_1(boolean flag5, int value3) {
      this.value = Math.max(0, Math.min(100, value3));
      if (!flag5) {
         if (this.flag) {
            this.m_1_void();
         }
      } else if (this.flag && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.getConnection() != null) {
         if (this.c2 == AuctionSeller.ModeKind.RESUME_WAIT) {
            if (this.timeHelper.hasElapsed(800L)) {
               this.m_9_void();
            }
         } else if (this.c2 == AuctionSeller.ModeKind.WORKING) {
            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               MinecraftAccess.MINECRAFT.player.closeContainer();
               this.timeHelper.reset();
            } else if (this.flag3) {
               if (this.timeHelper.hasElapsed(400L)) {
                  if (this.c1 != null && this.m_3(this.c1) == -1) {
                     this.deque.pollFirst();
                     this.value2++;
                  }

                  this.c1 = null;
                  this.flag3 = false;
                  this.longValue2 = 0L;
                  this.timeHelper.reset();
               }
            } else if (this.timeHelper.hasElapsed(250L)) {
               while (!this.deque.isEmpty()) {
                  AuctionSeller.StateHolder c12 = this.deque.peekFirst();
                  int value4 = this.m_3(c12);
                  if (value4 != -1) {
                     ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value4);
                     ItemUtil itemUtil = c12.itemUtil != null ? c12.itemUtil : m_2_C6_y(itemStack);
                     if (itemUtil != null && itemUtil.getValue() > 0) {
                        this.selectSlot(value4);
                        ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getMainHandItem();
                        if (!itemStack2.isEmpty() && m_1(c12, itemStack2)) {
                           int value5 = m_9(itemUtil, itemStack2.getCount(), this.value);
                           if (value5 <= 0) {
                              this.deque.pollFirst();
                              continue;
                           }

                           if (itemStack2.getCount() > 1 && value5 > 10000000L) {
                              int value6 = applyMarkup(itemUtil.getValue(), this.value);
                              if (value6 <= 0) {
                                 value6 = 1;
                              }

                              int value7 = (int)Math.max(1L, 10000000L / value6);
                              value7 = Math.min(value7, itemStack2.getCount());
                              if (value7 < itemStack2.getCount()) {
                                 if (!this.m_8_boolean(value7)) {
                                    this.timeHelper.reset();
                                    return;
                                 }

                                 itemStack2 = MinecraftAccess.MINECRAFT.player.getMainHandItem();
                                 value5 = m_9(itemUtil, itemStack2.getCount(), this.value);
                                 if (value5 <= 0) {
                                    this.deque.pollFirst();
                                    continue;
                                 }
                              }
                           }

                           long longValue3 = m_9_long(MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot(), itemStack2);
                           if (longValue3 == this.longValue2) {
                              this.timeHelper.reset();
                              return;
                           }

                           MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah sell " + value5);
                           this.c1 = c12;
                           this.flag3 = true;
                           this.longValue2 = longValue3;
                           this.timeHelper.reset();
                           return;
                        }

                        this.timeHelper.reset();
                        return;
                     } else {
                        this.deque.pollFirst();
                     }
                  } else {
                     this.deque.pollFirst();
                  }
               }

               if (this.deque.isEmpty()) {
                  if (this.value2 == 0 && System.currentTimeMillis() - this.longValue < 800L) {
                     this.m_0_void();
                     if (!this.deque.isEmpty()) {
                        this.timeHelper.reset();
                        return;
                     }
                  }

                  this.c2 = AuctionSeller.ModeKind.RESUME_WAIT;
                  this.timeHelper.reset();
               }
            }
         }
      }
   }

   public void m_2_void() {
      this.flag4 = true;
      this.flag3 = false;
      this.c1 = null;
      this.longValue2 = 0L;
      this.flag2 = true;
      if (this.flag) {
         this.m_9_void();
      }
   }

   public void m_8_void() {
      this.flag4 = false;
      if (this.flag && this.c2 == AuctionSeller.ModeKind.WORKING) {
         this.flag3 = false;
         this.c1 = null;
         this.longValue2 = 0L;
         this.timeHelper.reset();
      } else {
         if (!this.flag && countSellable() >= 1) {
            this.m_2_void(1);
         }
      }
   }

   public void m_5_void() {
      if (this.flag) {
         this.flag3 = false;
         this.c1 = null;
         this.longValue2 = 0L;
         this.timeHelper.reset();
      }
   }

   private void m_9_void() {
      boolean flag5 = this.flag2;
      this.m_1_void();
      if (flag5) {
         AuctionConfig.getConfig().setFlag(true);
         if (MinecraftAccess.MINECRAFT.getConnection() != null) {
            MinecraftAccess.MINECRAFT.getConnection().sendCommand("ah");
         }
      }
   }

   private void m_0_void() {
      this.deque.clear();
      if (MinecraftAccess.MINECRAFT.player != null) {
         for (int i = 0; i <= 35; i++) {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
            if (!itemStack.isEmpty()) {
               ItemUtil itemUtil = m_2_C6_y(itemStack);
               if (itemUtil != null && itemUtil.getValue() > 0) {
                  this.deque.addLast(new AuctionSeller.StateHolder(itemUtil, itemStack.copy(), getItemKey(itemStack)));
               }
            }
         }
      }
   }

   private static int countSellable() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return 0;
      }

      int value3 = 0;

      for (int i = 0; i <= 35; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            ItemUtil itemUtil = m_2_C6_y(itemStack);
            if (itemUtil != null && itemUtil.getValue() > 0) {
               value3++;
            }
         }
      }

      return value3;
   }

   private void m_4_void() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         ArrayList<String> stringList = new ArrayList<>();

         for (AuctionSeller.StateHolder c12 : this.deque) {
            stringList.add(c12.text);
         }

         for (int i = 0; i <= 35; i++) {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
            if (!itemStack.isEmpty()) {
               ItemUtil itemUtil = m_2_C6_y(itemStack);
               if (itemUtil != null && itemUtil.getValue() > 0) {
                  String text = getItemKey(itemStack);
                  boolean flag5 = false;

                  for (String text2 : stringList) {
                     if (text2.equals(text)) {
                        flag5 = true;
                        break;
                     }
                  }

                  if (!flag5) {
                     this.deque.addLast(new AuctionSeller.StateHolder(itemUtil, itemStack.copy(), text));
                     stringList.add(text);
                  }
               }
            }
         }
      }
   }

   private int m_3(AuctionSeller.StateHolder c12) {
      if (MinecraftAccess.MINECRAFT.player != null && c12 != null) {
         for (int i = 0; i <= 35; i++) {
            ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
            if (!itemStack.isEmpty() && c12.itemUtil != null && c12.itemUtil.matches(itemStack) && m_1(c12, itemStack)) {
               return i;
            }
         }

         for (int i2 = 0; i2 <= 35; i2++) {
            ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i2);
            if (!itemStack2.isEmpty() && c12.itemUtil != null && c12.itemUtil.matches(itemStack2)) {
               return i2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private static boolean m_1(AuctionSeller.StateHolder c12, ItemStack itemStack) {
      if (c12 == null || itemStack == null || itemStack.isEmpty()) {
         return false;
      } else {
         return c12.itemUtil != null && !c12.itemUtil.matches(itemStack)
            ? false
            : getItemKey(itemStack).equals(c12.text)
               || itemStack.getItem() == c12.itemStack.getItem()
                  && ItemUtil.stripFormatting(itemStack.getHoverName().getString()).equalsIgnoreCase(ItemUtil.stripFormatting(c12.itemStack.getHoverName().getString()));
      }
   }

   private static String getItemKey(ItemStack itemStack) {
      return itemStack.getItem().toString() + "|" + ItemUtil.stripFormatting(itemStack.getHoverName().getString()).toLowerCase(Locale.ROOT) + "|" + itemStack.getCount();
   }

   private void selectSlot(int value3) {
      if (MinecraftAccess.MINECRAFT.player != null && value3 >= 0 && value3 <= 35) {
         if (value3 < 9) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value3);
            if (MinecraftAccess.MINECRAFT.getConnection() != null) {
               MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundSetCarriedItemPacket(value3));
            }
         } else {
            byte byteValue = 0;
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(byteValue);
            if (MinecraftAccess.MINECRAFT.getConnection() != null) {
               MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundSetCarriedItemPacket(byteValue));
            }

            InventoryUtil.clickSlot(0, value3, byteValue, ContainerInput.SWAP);
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(byteValue);
            if (MinecraftAccess.MINECRAFT.getConnection() != null) {
               MinecraftAccess.MINECRAFT.getConnection().send(new ServerboundSetCarriedItemPacket(byteValue));
            }
         }
      }
   }

   private boolean m_8_boolean(int value3) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player.inventoryMenu != null) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
         if (itemStack.isEmpty()) {
            return false;
         }

         int value4 = itemStack.getCount();
         if (value4 <= value3) {
            return true;
         }

         int value5 = this.findFreeSlot();
         if (value5 == -1) {
            return false;
         }

         int value6 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() + 36;
         InventoryUtil.clickSlot(0, value6, 0, ContainerInput.PICKUP);

         for (int i = 0; i < value3; i++) {
            InventoryUtil.clickSlot(0, value6, 1, ContainerInput.PICKUP);
         }

         InventoryUtil.clickSlot(0, value5, 0, ContainerInput.PICKUP);
         return true;
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

      int value3 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();

      for (int i2 = 36; i2 < 45; i2++) {
         if (i2 - 36 != value3 && MinecraftAccess.MINECRAFT.player.getInventory().getItem(i2 - 36).isEmpty()) {
            return i2;
         }
      }

      return -1;
   }

   private static ItemUtil m_2_C6_y(ItemStack itemStack) {
      ItemUtil itemUtil = null;

      for (ItemUtil itemUtil2 : FunTimeItems.getItems()) {
         if (itemUtil2 != null
            && itemUtil2.isFlag2()
            && itemUtil2.getValue() > 0
            && itemUtil2.matches(itemStack)
            && (itemUtil == null || itemUtil2.getText().length() > itemUtil.getText().length())) {
            itemUtil = itemUtil2;
         }
      }

      return itemUtil;
   }

   public static int m_9(ItemUtil itemUtil, int value3, int value4) {
      if (itemUtil != null && itemUtil.getValue() > 0 && value3 > 0) {
         long longValue3 = (long)Math.floor(itemUtil.getValue() * (100.0 + Math.max(0, Math.min(100, value4))) / 100.0);
         if (longValue3 <= 0L) {
            longValue3 = 1L;
         }

         long longValue4 = longValue3 * Math.max(1, value3);
         return (int)Math.min(2147483647L, Math.max(1L, longValue4));
      } else {
         return 0;
      }
   }

   public static int applyMarkup(int value3, int value4) {
      return value3 <= 0 ? 0 : (int)Math.floor(value3 * (100.0 + Math.max(0, Math.min(100, value4))) / 100.0);
   }

   private static long m_9_long(int value3, ItemStack itemStack) {
      long longValue3 = 31L * value3 + System.identityHashCode(itemStack.getItem());
      longValue3 = 31L * longValue3 + itemStack.getCount();
      return 31L * longValue3 + ItemUtil.stripFormatting(itemStack.getHoverName().getString()).hashCode();
   }

   private static final class StateHolder {
      public final ItemUtil itemUtil;
      public final ItemStack itemStack;
      public final String text;

      public StateHolder(ItemUtil itemUtil2, ItemStack itemStack2, String text2) {
         this.itemUtil = itemUtil2;
         this.itemStack = itemStack2;
         this.text = text2;
      }
   }

   private enum ModeKind {
      IDLE,
      WORKING,
      RESUME_WAIT;

      private static String a(byte[] byteArray) {
         int value3 = 0;
         int value4;
         char[] charArray = new char[value4 = byteArray.length];

         for (int i = 0; i < value4; i++) {
            int value5;
            if ((value5 = 255 & byteArray[i]) < 192) {
               charArray[value3++] = (char)value5;
            } else if (value5 < 224) {
               char ch = (char)((char)(value5 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value3++] = ch;
            } else if (i < value4 - 2) {
               char ch2 = (char)((char)(value5 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value3++] = ch2;
            }
         }

         return new String(charArray, 0, value3);
      }
   }
}
