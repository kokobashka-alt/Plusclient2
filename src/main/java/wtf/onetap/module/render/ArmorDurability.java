package wtf.onetap.module.render;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.MultiModeSetting;

@ModuleInformation(name = "ArmorDurability", description = "Подсвечивает надетую броню по прочности", category = Category.RENDER)
public class ArmorDurability extends Module {
   private static final int[] VALUE_ARRAY = new int[]{
      -6750208,
      -3407872,
      -65536,
      -52480,
      -39424,
      -26368,
      -13312,
      -256,
      -1638656,
      -4980992,
      -8323328,
      -16738048,
      -16731392,
      -16724992,
      -16718336,
      -16711936,
      -13369549,
      -10027162,
      -6684775,
      -3342388,
      -1
   };
   private final MultiModeSetting multiModeSetting = createDisplaySetting();
   private final Map<Object, Boolean> objectMap = Collections.synchronizedMap(new WeakHashMap<>());
   private ItemStack itemStack;

   private static MultiModeSetting createDisplaySetting() {
      MultiModeSetting multiModeSetting2 = new MultiModeSetting("Отображать", "Игроков", "Игроков", "Друзей").build();
      ((Set)multiModeSetting2.getValue()).add("Друзей");
      return multiModeSetting2;
   }

   public boolean shouldShow(Entity entity) {
      if (entity == Minecraft.getInstance().player) {
         return true;
      } else if (entity instanceof Player player) {
         boolean flag = Onetap.FRIEND_MANAGER.isFriend(player.getName().getString());
         return this.multiModeSetting.isSelected(flag ? "Друзей" : "Игроков");
      } else {
         return false;
      }
   }

   public static int formatPercent(float floatValue) {
      if (!(floatValue < 0.0F) && !(floatValue > 1.0F)) {
         int value = (int)(floatValue * 100.0F / 5.0F);
         return VALUE_ARRAY[Math.min(value, VALUE_ARRAY.length - 1)];
      } else {
         return -1;
      }
   }

   public void setItemStack(ItemStack itemStack2) {
      this.itemStack = itemStack2;
   }

   public void m_0(Object object, Entity entity) {
      this.objectMap.put(object, this.shouldShow(entity));
   }

   public boolean isVisibleFor(Object object) {
      return this.objectMap.getOrDefault(object, true);
   }

   public void m_4_void() {
      this.itemStack = null;
   }

   public int m_7_int(int value) {
      if (!this.isEnabled()) {
         return value;
      }

      ItemStack itemStack2 = this.itemStack;
      if (itemStack2 != null && !itemStack2.isEmpty()) {
         float floatValue = 1.0F;
         if (itemStack2.isDamaged()) {
            int value2 = itemStack2.getMaxDamage();
            if (value2 > 0) {
               floatValue = (float)(value2 - itemStack2.getDamageValue()) / value2;
            }
         }

         return ARGB.multiply(value, formatPercent(floatValue));
      } else {
         return value;
      }
   }
}
