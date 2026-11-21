package wtf.onetap.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wtf.onetap.animation.Animation;
import wtf.onetap.animation.Easing;
import wtf.onetap.module.render.ClickGUI;

public class MainMenu {
   private final List<MainMenu.DataRecord2> c2List;
   private final List<Animation> animationList;
   private final List<Boolean> booleanList;
   private final Animation animation;
   private ServerEntry.ModeKind c7;
   private int value;
   private int value2;
   private float floatValue;
   private float floatValue2;
   private boolean flag;
   private float floatValue3;
   private MainMenu.ModeKind c9;
   private boolean flag2;
   private static String text;

   public MainMenu() {
      this.c2List = new ArrayList<>();
      this.animationList = new ArrayList<>();
      this.booleanList = new ArrayList<>();
      this.animation = new Animation(180L, 0.0F, Easing.EASING8);
      this.c7 = ServerEntry.ModeKind.WORLDS;
      this.value = -1;
      this.value2 = 8;
      this.floatValue = 36.0F;
      this.c9 = MainMenu.ModeKind.NONE;
   }

   public void m_9(ServerEntry.ModeKind c72) {
      if (c72 != ServerEntry.ModeKind.NONE) {
         if (this.c7 != c72) {
            this.value = -1;
            this.floatValue2 = 0.0F;
            this.animation.reset(0.0F);
         }

         this.c7 = c72;
      }
   }

   public ServerEntry.ModeKind getC7() {
      return this.c7;
   }

   public void setEntries(List<MainMenu.DataRecord2> c2List2) {
      this.c2List.clear();
      this.c2List.addAll(c2List2);
      this.animationList.clear();
      this.booleanList.clear();
      if (this.value >= this.c2List.size()) {
         this.value = this.c2List.size() - 1;
      }

      this.m_0_void();
   }

   public void clear() {
      this.c2List.clear();
      this.animationList.clear();
      this.booleanList.clear();
      this.flag = false;
   }

   public List<MainMenu.DataRecord2> getEntries() {
      return Collections.unmodifiableList(this.c2List);
   }

   public MainMenu.DataRecord2 getSelected() {
      return this.value >= 0 && this.value < this.c2List.size() ? this.c2List.get(this.value) : null;
   }

   public int getValue() {
      return this.value;
   }

   public float getAnimation() {
      return this.animation.getValue();
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public float m_2_float() {
      if (this.flag) {
         this.animation.setDuration(this.floatValue2);
         return this.floatValue2;
      } else {
         return this.animation.update(this.floatValue2);
      }
   }

   public boolean isServersMode() {
      return this.c7 == ServerEntry.ModeKind.SERVERS;
   }

   public void setFlag2(boolean flag3) {
      this.flag2 = flag3;
   }

   public boolean m_7_boolean() {
      return this.c7 == ServerEntry.ModeKind.SERVERS && this.flag2;
   }

   public String getTitle() {
      return this.c7 == ServerEntry.ModeKind.SERVERS ? "Сетевая игра" : "Одиночная игра";
   }

   public String getButtonText() {
      return this.c7 == ServerEntry.ModeKind.SERVERS ? "Подключиться" : "Играть";
   }

   public String getEmptyText() {
      return this.c7 == ServerEntry.ModeKind.SERVERS ? "Нет серверов" : "Нет миров";
   }

   public int getValue2() {
      return this.value2;
   }

   public float getMaxScroll() {
      return Math.max(0.0F, this.c2List.size() * 38.0F - this.floatValue);
   }

   public static String[] m_9_StringArr(String text2) {
      if (text2 != null && !text2.isEmpty()) {
         String text3 = null;
         String text4 = null;

         for (String text5 : text2.replace('\r', '\n').split("\n", -1)) {
            String text6 = text5.trim();
            if (!text6.isEmpty()) {
               if (text3 != null) {
                  text4 = text6;
                  break;
               }

               text3 = text6;
            }
         }

         if (text3 == null) {
            return new String[0];
         } else {
            return text4 == null ? new String[]{text3} : new String[]{text3, text4};
         }
      } else {
         return new String[0];
      }
   }

   public static String formatPlayers(Integer integer, Integer integer2, boolean flag3) {
      if (integer != null && integer2 != null) {
         return integer + "/" + integer2;
      } else {
         return flag3 ? "..." : "—";
      }
   }

   public static String formatPing(long longValue, boolean flag3, boolean flag4) {
      if (flag4) {
         return "—";
      } else {
         return longValue <= 0L && flag3 ? "" : longValue + "мс";
      }
   }

   public static int m_9(long longValue, boolean flag3, boolean flag4) {
      if (flag4) {
         return 0;
      } else if (flag3) {
         return -1;
      } else if (longValue < 150L) {
         return 5;
      } else if (longValue < 300L) {
         return 4;
      } else if (longValue < 600L) {
         return 3;
      } else {
         return longValue < 1000L ? 2 : 1;
      }
   }

   public static int getSpinnerFrame(long longValue) {
      return (int)(longValue / 600L % 5L) + 1;
   }

   public static float m_0_float() {
      return 17.0F;
   }

   public MainMenu.DataRecord m_8_C1(float floatValue4, float floatValue5) {
      ClickGUI.m_3_String();
      boolean flag3 = this.isServersMode();
      float floatValue6 = Math.min(720.0F, Math.max(400.0F, floatValue4 - 40.0F));
      float floatValue7 = (floatValue4 - floatValue6) / 2.0F;
      float floatValue8 = 20.0F;
      byte byteValue = 2;
      float floatValue9 = 24.0F * byteValue + 4.0F * (byteValue - 1);
      float floatValue10 = 4.0F + floatValue9 + 4.0F;
      float floatValue11 = floatValue5 - 20.0F - floatValue10;
      float floatValue12 = floatValue11 + 4.0F;
      float floatValue13 = floatValue8 + 36.0F + 10.0F;
      float floatValue14 = floatValue11 - 10.0F - floatValue13;
      if (floatValue14 < 52.0F) {
         floatValue14 = 52.0F;
      }

      float floatValue15 = floatValue14 - 16.0F;
      int value3 = Math.max(1, (int)(floatValue15 / 38.0F));
      this.value2 = value3;
      this.floatValue = floatValue15;
      this.m_0_void();
      float floatValue16 = this.c2List.size() * 38.0F;
      boolean flag4 = floatValue16 > floatValue15 + 0.5F;
      float floatValue17 = floatValue7 + floatValue6 - 8.0F - 3.0F;
      float floatValue18 = floatValue13 + 8.0F;
      float floatValue19 = floatValue15;
      float floatValue20 = this.getMaxScroll();
      float floatValue21 = flag4 ? Math.max(18.0F, floatValue19 * floatValue15 / floatValue16) : floatValue19;
      float floatValue22 = floatValue20 <= 0.0F ? 0.0F : this.getAnimation() / floatValue20;
      if (floatValue22 < 0.0F) {
         floatValue22 = 0.0F;
      }

      if (floatValue22 > 1.0F) {
         floatValue22 = 1.0F;
      }

      float floatValue23 = floatValue18 + (floatValue19 - floatValue21) * floatValue22;
      MainMenu.ModeKind[][] arrayArray = this.m_2_C9ArrArr();
      ArrayList list = new ArrayList();
      float floatValue24 = floatValue7 + 4.0F;
      float floatValue25 = floatValue6 - 8.0F;
      int value4 = 0;

      for (MainMenu.ModeKind[] c9Array : arrayArray) {
         value4 = Math.max(value4, c9Array.length);
      }

      float floatValue26 = (floatValue25 - 4.0F * (value4 - 1)) / value4;
      float floatValue27 = floatValue26;

      for (int i = 0; i < arrayArray.length; i++) {
         MainMenu.ModeKind[] c9Array2 = arrayArray[i];
         float floatValue28 = floatValue12 + i * 28.0F;
         int value5 = 0;

         for (int i2 = 0; i2 < c9Array2.length; i2++) {
            int value6 = value4 / c9Array2.length + (i2 == 0 ? value4 % c9Array2.length : 0);
            float floatValue29 = floatValue26 * value6 + 4.0F * (value6 - 1);
            list.add(new MainMenu.DataRecord3(c9Array2[i2], floatValue24 + value5 * (floatValue26 + 4.0F), floatValue28, floatValue29, 24.0F));
            value5 += value6;
         }
      }

      MainMenu.DataRecord3 c3 = findByMode(list, MainMenu.ModeKind.BACK);
      MainMenu.DataRecord3 c32 = findByMode(list, MainMenu.ModeKind.PLAY);
      MainMenu.DataRecord3 c33 = findByMode(list, MainMenu.ModeKind.PROXY);
      return new MainMenu.DataRecord(
         floatValue7,
         floatValue8,
         floatValue6,
         36.0F,
         floatValue7,
         floatValue13,
         floatValue6,
         floatValue14,
         floatValue7 + 8.0F,
         floatValue8,
         floatValue12,
         24.0F,
         floatValue27,
         c3 == null ? floatValue7 : c3.f_5_w,
         c32 == null ? floatValue7 : c32.f_5_w,
         c33 == null ? Float.NaN : c33.f_5_w,
         flag3,
         value3,
         floatValue17,
         floatValue18,
         3.0F,
         floatValue19,
         floatValue23,
         floatValue21,
         flag4,
         List.copyOf(list),
         4.0F + (this.c7 == ServerEntry.ModeKind.SERVERS ? 16.0F : 0.0F)
      );
   }

   private MainMenu.ModeKind[][] m_2_C9ArrArr() {
      if (this.c7 == ServerEntry.ModeKind.SERVERS) {
         return this.m_7_boolean()
            ? new MainMenu.ModeKind[][]{
               {MainMenu.ModeKind.PLAY, MainMenu.ModeKind.DIRECT, MainMenu.ModeKind.ADD, MainMenu.ModeKind.VIA}, {MainMenu.ModeKind.EDIT, MainMenu.ModeKind.DELETE, MainMenu.ModeKind.REFRESH, MainMenu.ModeKind.PROXY, MainMenu.ModeKind.BACK}
            }
            : new MainMenu.ModeKind[][]{{MainMenu.ModeKind.PLAY, MainMenu.ModeKind.DIRECT, MainMenu.ModeKind.ADD}, {MainMenu.ModeKind.EDIT, MainMenu.ModeKind.DELETE, MainMenu.ModeKind.REFRESH, MainMenu.ModeKind.PROXY, MainMenu.ModeKind.BACK}};
      } else {
         return new MainMenu.ModeKind[][]{{MainMenu.ModeKind.PLAY, MainMenu.ModeKind.CREATE}, {MainMenu.ModeKind.EDIT, MainMenu.ModeKind.DELETE, MainMenu.ModeKind.RECREATE, MainMenu.ModeKind.BACK}};
      }
   }

   private static MainMenu.DataRecord3 findByMode(List<MainMenu.DataRecord3> c3List, MainMenu.ModeKind c92) {
      for (MainMenu.DataRecord3 c3 : c3List) {
         if (c3.f_88 == c92) {
            return c3;
         }
      }

      return null;
   }

   public MainMenu.DataRecord4 m_7_C6(float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      MainMenu.DataRecord c1 = this.m_8_C1(floatValue6, floatValue7);

      for (MainMenu.DataRecord3 c3 : c1.f_6i) {
         if (isHovered(floatValue4, floatValue5, c3.f_5_w, c3.f_52, c3.f_5g, c3.f_5_e)) {
            return new MainMenu.DataRecord4(c3.f_88, -1);
         }
      }

      if (c1.f_7k && isHovered(floatValue4, floatValue5, c1.f_0r - 2.0F, c1.f_0_j, c1.f_01 + 4.0F, c1.f_0_d)) {
         return new MainMenu.DataRecord4(MainMenu.ModeKind.SCROLLBAR, -1);
      }

      if (this.c2List.isEmpty()) {
         return MainMenu.DataRecord4.f_3;
      }

      float floatValue8 = c1.f_0p + 8.0F - this.getAnimation();
      boolean flag3 = this.c7 == ServerEntry.ModeKind.SERVERS;

      for (int i = 0; i < this.c2List.size(); i++) {
         float floatValue9 = floatValue8 + this.getEntryY(i);
         if (!(floatValue9 + 36.0F < c1.f_0p + 8.0F) && !(floatValue9 > c1.f_0p + c1.f_0m - 8.0F) && isHovered(floatValue4, floatValue5, c1.m_0d_float(), floatValue9, c1.m_05(), 36.0F)) {
            if (flag3 && isHovered(floatValue4, floatValue5, c1.m_0_f_float(), floatValue9, 16.0F, 36.0F)) {
               float floatValue10 = floatValue5 - floatValue9;
               if (floatValue10 < 11.88F && this.m_3_boolean(i)) {
                  return new MainMenu.DataRecord4(MainMenu.ModeKind.MOVE_UP, i);
               }

               if (floatValue10 > 24.12F && this.m_7_boolean(i)) {
                  return new MainMenu.DataRecord4(MainMenu.ModeKind.MOVE_DOWN, i);
               }

               return new MainMenu.DataRecord4(MainMenu.ModeKind.STAR, i);
            }

            float floatValue11 = floatValue9 + 2.0F;
            if (isHovered(floatValue4, floatValue5, c1.m_0_k_float(), floatValue11, 32.0F, 32.0F) && this.c2List.get(i).m_4_j()) {
               return new MainMenu.DataRecord4(MainMenu.ModeKind.PLAY, i);
            }

            return new MainMenu.DataRecord4(MainMenu.ModeKind.ROW, i);
         }
      }

      return MainMenu.DataRecord4.f_3;
   }

   public MainMenu.DataRecord4 m_6_C6(float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      MainMenu.DataRecord4 c6 = this.m_7_C6(floatValue4, floatValue5, floatValue6, floatValue7);
      switch (c6.m_6p_C9().ordinal()) {
         case 0:
            break;
         case 1:
            this.value = c6.m_4_s();
            break;
         case 2:
         case 14:
         case 15:
         case 16:
            if (c6.m_4_s() >= 0) {
               this.value = c6.m_4_s();
            }

            this.c9 = c6.m_6p_C9();
            break;
         case 3:
         case 4:
         case 5:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         default:
            this.c9 = c6.m_6p_C9();
            break;
         case 6:
            this.m_7_void(floatValue4, floatValue5, floatValue6, floatValue7);
      }

      return c6;
   }

   public boolean m_4(double doubleValue, float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      MainMenu.DataRecord c1 = this.m_8_C1(floatValue6, floatValue7);
      if (!isHovered(floatValue4, floatValue5, c1.f_0_c, c1.f_0p, c1.f_04, c1.f_0m) && !isHovered(floatValue4, floatValue5, c1.f_08, c1.f_0s, c1.f_0l, c1.f_0k)) {
         return false;
      }

      if (this.getMaxScroll() <= 0.0F) {
         return true;
      }

      this.floatValue2 -= (float)doubleValue * 38.0F;
      this.m_0_void();
      return true;
   }

   public boolean m_7_boolean(float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      if (!this.flag) {
         return false;
      }

      MainMenu.DataRecord c1 = this.m_8_C1(floatValue6, floatValue7);
      float floatValue8 = c1.f_0_d - c1.f_0_z;
      if (!(floatValue8 <= 0.0F) && !(this.getMaxScroll() <= 0.0F)) {
         float floatValue9 = (floatValue5 - c1.f_0_j - this.floatValue3) / floatValue8;
         if (floatValue9 < 0.0F) {
            floatValue9 = 0.0F;
         }

         if (floatValue9 > 1.0F) {
            floatValue9 = 1.0F;
         }

         this.floatValue2 = floatValue9 * this.getMaxScroll();
         this.animation.setDuration(this.floatValue2);
         return true;
      } else {
         return true;
      }
   }

   public void m_3_void() {
      this.flag = false;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public MainMenu.ModeKind m_2_C9() {
      MainMenu.ModeKind c92 = this.c9;
      this.c9 = MainMenu.ModeKind.NONE;
      return c92;
   }

   public boolean m_3_boolean(int value3) {
      return this.canMove(value3, value3 - 1);
   }

   public boolean m_7_boolean(int value3) {
      return this.canMove(value3, value3 + 1);
   }

   public boolean swapEntries(int value3, int value4) {
      if (!this.canMove(value3, value4)) {
         return false;
      }

      Collections.swap(this.c2List, value3, value4);
      if (this.animationList.size() == this.c2List.size()) {
         Collections.swap(this.animationList, value3, value4);
         Collections.swap(this.booleanList, value3, value4);
      }

      if (this.value == value3) {
         this.value = value4;
      } else if (this.value == value4) {
         this.value = value3;
      }

      return true;
   }

   public void m_7_void(int value3) {
      if (this.c7 == ServerEntry.ModeKind.SERVERS && value3 >= 0 && value3 < this.c2List.size()) {
         MainMenu.DataRecord2 c2 = this.c2List.get(value3).m_3_C2(!this.c2List.get(value3).m_46_boolean());
         this.c2List.set(value3, c2);
         int value4 = this.c2List.size();
         ArrayList list = new ArrayList(value4);

         for (int i = 0; i < value4; i++) {
            list.add(i);
         }

         list.sort((object, object2) -> {
            boolean flag3 = this.c2List.get((int)object).m_46_boolean();
            boolean flag4 = this.c2List.get((int)object2).m_46_boolean();
            if (flag3 != flag4) {
               return flag3 ? -1 : 1;
            } else {
               return Integer.compare((int)object, (int)object2);
            }
         });
         reorder(this.c2List, list);
         if (this.animationList.size() == value4) {
            reorder(this.animationList, list);
            reorder(this.booleanList, list);
         }

         this.value = this.c2List.indexOf(c2);
      }
   }

   public void syncAnimations() {
      while (this.animationList.size() < this.c2List.size()) {
         this.animationList.add(new Animation(260L, 0.0F, Easing.EASING8));
         this.booleanList.add(false);
      }

      while (this.animationList.size() > this.c2List.size()) {
         this.animationList.remove(this.animationList.size() - 1);
         this.booleanList.remove(this.booleanList.size() - 1);
      }

      float floatValue4 = 0.0F;

      for (int i = 0; i < this.c2List.size(); i++) {
         Animation animation2 = this.animationList.get(i);
         if (!this.booleanList.get(i)) {
            animation2.setDuration(floatValue4);
            this.booleanList.set(i, true);
         } else {
            animation2.updateWithThreshold(floatValue4, 19.0F);
         }

         floatValue4 += 38.0F;
      }
   }

   public float getEntryY(int value3) {
      return value3 >= 0 && value3 < this.animationList.size() ? this.animationList.get(value3).getValue() : value3 * 38.0F;
   }

   private static <T> void reorder(List<T> tList, List<Integer> integerList) {
      ArrayList list = new ArrayList(tList);

      for (int i = 0; i < integerList.size(); i++) {
         tList.set(i, (T)list.get((Integer)integerList.get(i)));
      }
   }

   public void m_6_void(String text2, String text3) {
      for (int i = 0; i < this.c2List.size(); i++) {
         MainMenu.DataRecord2 c2 = this.c2List.get(i);
         if (c2.f_8_o.equals(text2) && c2.f_8j.equals(text3)) {
            this.value = i;
            return;
         }
      }
   }

   private boolean canMove(int value3, int value4) {
      if (this.c7 != ServerEntry.ModeKind.SERVERS) {
         return false;
      } else {
         return value3 >= 0 && value4 >= 0 && value3 < this.c2List.size() && value4 < this.c2List.size() && value3 != value4
            ? this.c2List.get(value3).m_46_boolean() == this.c2List.get(value4).m_46_boolean()
            : false;
      }
   }

   private void m_7_void(float floatValue4, float floatValue5, float floatValue6, float floatValue7) {
      MainMenu.DataRecord c1 = this.m_8_C1(floatValue6, floatValue7);
      if (c1.f_7k) {
         this.flag = true;
         if (isHovered(floatValue4, floatValue5, c1.f_0r - 2.0F, c1.f_0a, c1.f_01 + 4.0F, c1.f_0_z)) {
            this.floatValue3 = floatValue5 - c1.f_0a;
         } else {
            this.floatValue3 = c1.f_0_z / 2.0F;
            this.m_7_boolean(floatValue4, floatValue5, floatValue6, floatValue7);
         }
      }
   }

   private void m_0_void() {
      float floatValue4 = this.getMaxScroll();
      if (this.floatValue2 > floatValue4) {
         this.floatValue2 = floatValue4;
      }

      if (this.floatValue2 < 0.0F) {
         this.floatValue2 = 0.0F;
      }

      if (this.animation.getValue() > floatValue4) {
         this.animation.setDuration(floatValue4);
      }
   }

   private static boolean isHovered(float floatValue4, float floatValue5, float floatValue6, float floatValue7, float floatValue8, float floatValue9) {
      return floatValue4 >= floatValue6 && floatValue4 <= floatValue6 + floatValue8 && floatValue5 >= floatValue7 && floatValue5 <= floatValue7 + floatValue9;
   }

   public static void setTitle(String text2) {
      text = text2;
   }

   public static String m_0_String() {
      return text;
   }

   static {
      setTitle((String)null);
   }

   public record DataRecord(
      float f_08,
      float f_0s,
      float f_0l,
      float f_0k,
      float f_0_c,
      float f_0p,
      float f_04,
      float f_0m,
      float f_0_l,
      float f_0f,
      float f_0_t,
      float f_0_r,
      float f_0_m,
      float f_0t,
      float f_0z,
      float f_0q,
      boolean f_7_v,
      int f_8_a,
      float f_0r,
      float f_0_j,
      float f_01,
      float f_0_d,
      float f_0a,
      float f_0_z,
      boolean f_7k,
      List<MainMenu.DataRecord3> f_6i,
      float f_0_y
   ) {
      public MainMenu.DataRecord3 m_3(MainMenu.ModeKind c92) {
         ClickGUI.m_3_String();

         for (MainMenu.DataRecord3 c3 : this.f_6i) {
            if (c3.f_88 == c92) {
               return c3;
            }
         }

         return null;
      }

      public float m_0d_float() {
         return this.f_0_c + 8.0F;
      }

      public float m_05() {
         return this.f_04 - 16.0F - (this.f_7k ? 11.0F : 0.0F);
      }

      public float m_6_float(int value3) {
         return this.f_0p + 8.0F + value3 * 38.0F;
      }

      public float m_0_k_float() {
         return this.m_0d_float() + this.f_0_y;
      }

      public float m_0_f_float() {
         return this.m_0d_float() + 2.0F;
      }

      public float m_0_float(int value3) {
         return this.m_6_float(value3) + 2.0F;
      }

      public float m_08() {
         return this.f_08;
      }

      public float m_0s() {
         return this.f_0s;
      }

      public float m_0l() {
         return this.f_0l;
      }

      public float m_0k() {
         return this.f_0k;
      }

      public float m_0_c() {
         return this.f_0_c;
      }

      public float m_0p() {
         return this.f_0p;
      }

      public float m_04() {
         return this.f_04;
      }

      public float m_0m() {
         return this.f_0m;
      }

      public float m_0_l() {
         return this.f_0_l;
      }

      public float m_0f() {
         return this.f_0f;
      }

      public float m_0_t() {
         return this.f_0_t;
      }

      public float m_0_r() {
         return this.f_0_r;
      }

      public float m_0_m() {
         return this.f_0_m;
      }

      public float m_0t() {
         return this.f_0t;
      }

      public float m_0z() {
         return this.f_0z;
      }

      public float m_0q() {
         return this.f_0q;
      }

      public boolean m_7_v() {
         return this.f_7_v;
      }

      public int m_8_a_int() {
         return this.f_8_a;
      }

      public float m_0r() {
         return this.f_0r;
      }

      public float m_0_j() {
         return this.f_0_j;
      }

      public float m_01() {
         return this.f_01;
      }

      public float m_0_d() {
         return this.f_0_d;
      }

      public float m_0a() {
         return this.f_0a;
      }

      public float m_0_z_float() {
         return this.f_0_z;
      }

      public boolean m_7k() {
         return this.f_7k;
      }

      public List<MainMenu.DataRecord3> m_6i_List() {
         return this.f_6i;
      }

      public float m_0_y() {
         return this.f_0_y;
      }
   }

   public record DataRecord2(String f_8_o, String f_8j, String f_8r, boolean f_4_j, boolean f_46) {
      public DataRecord2(String text2, String text3, String text4, boolean flag3) {
         this(text2, text3, text4, flag3, false);
      }

      public MainMenu.DataRecord2 m_3_C2(boolean flag3) {
         return new MainMenu.DataRecord2(this.f_8_o, this.f_8j, this.f_8r, this.f_4_j, flag3);
      }

      public String m_8_o() {
         return this.f_8_o;
      }

      public String m_8j() {
         return this.f_8j;
      }

      public String m_8r_String() {
         return this.f_8r;
      }

      public boolean m_4_j() {
         return this.f_4_j;
      }

      public boolean m_46_boolean() {
         return this.f_46;
      }
   }

   public record DataRecord3(MainMenu.ModeKind f_88, float f_5_w, float f_52, float f_5g, float f_5_e) {
      public MainMenu.ModeKind m_88() {
         return this.f_88;
      }

      public float m_5_w() {
         return this.f_5_w;
      }

      public float m_52() {
         return this.f_52;
      }

      public float m_5g() {
         return this.f_5g;
      }

      public float m_5_e() {
         return this.f_5_e;
      }
   }

   public record DataRecord4(MainMenu.ModeKind f_6p, int f_4_s) {
      public static final MainMenu.DataRecord4 f_3 = new MainMenu.DataRecord4(MainMenu.ModeKind.NONE, -1);

      public MainMenu.ModeKind m_6p_C9() {
         return this.f_6p;
      }

      public int m_4_s() {
         return this.f_4_s;
      }
   }

   public enum ModeKind {
      NONE,
      ROW,
      PLAY,
      BACK,
      PROXY,
      VIA,
      SCROLLBAR,
      ADD,
      DIRECT,
      EDIT,
      DELETE,
      REFRESH,
      CREATE,
      RECREATE,
      STAR,
      MOVE_UP,
      MOVE_DOWN;

      private static int value;

      public static void m_5_void(int value3) {
         value = value3;
      }

      public static int m_3_int() {
         return value;
      }

      public static int m_6_int() {
         return 0;
      }

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
