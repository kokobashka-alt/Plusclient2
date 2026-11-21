package wtf.onetap.autobuy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;

public class ItemKey {
   private final Item item;
   private final String text;
   private final List<ItemMatcher> itemMatcherList;
   private final List<ItemMatcher> itemMatcherList2;
   private final List<PotionUtil> potionUtilList;
   private boolean flag;
   private boolean flag2;
   private int value;
   private int value2;

   public ItemKey(Item item2, int value3, String text2) {
      this.itemMatcherList = new ArrayList<>();
      this.itemMatcherList2 = new ArrayList<>();
      this.potionUtilList = new ArrayList<>();
      this.item = item2;
      this.text = text2;
      this.value = value3;
   }

   public ItemKey withFlag(boolean flag3) {
      this.flag = flag3;
      return this;
   }

   public ItemKey withFlag2(boolean flag3) {
      this.flag2 = flag3;
      return this;
   }

   public ItemKey withValue2(int value3) {
      this.value2 = value3;
      return this;
   }

   public ItemKey m_4(ItemMatcher itemMatcher) {
      this.itemMatcherList.add(itemMatcher);
      return this;
   }

   public ItemKey m_1(ItemMatcher itemMatcher) {
      this.itemMatcherList2.add(itemMatcher);
      return this;
   }

   public ItemKey addPotion(PotionUtil potionUtil) {
      this.potionUtilList.add(potionUtil);
      return this;
   }

   public ItemKey denyEnchantments(ResourceKey<Enchantment>... resourceKeyArray) {
      for (ResourceKey resourceKey : resourceKeyArray) {
         this.itemMatcherList.add(new EnchantMatcher(resourceKey, 0, ItemMatcher.ModeKind.DENY));
      }

      return this;
   }

   public ItemKey denyAttributes(String... stringArray) {
      for (String text2 : stringArray) {
         this.itemMatcherList2.add(new AttributeMatcher(text2, 0, ItemMatcher.ModeKind.DENY));
      }

      return this;
   }

   public Item getItem() {
      return this.item;
   }

   public String getText() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void setFlag(boolean flag3) {
      this.flag = flag3;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public int getValue() {
      return this.value;
   }

   public void setValue(int value3) {
      this.value = value3;
   }

   public List<ItemMatcher> getItemMatcherList() {
      return this.itemMatcherList;
   }

   public List<ItemMatcher> getItemMatcherList2() {
      return this.itemMatcherList2;
   }

   public List<PotionUtil> getPotionUtilList() {
      return this.potionUtilList;
   }

   public boolean m_4_boolean(ItemStack itemStack) {
      return itemStack.is(this.item)
         && this.itemMatcherList.stream().allMatch(itemMatcher -> itemMatcher.matches(itemStack))
         && this.itemMatcherList2.stream().allMatch(itemMatcher2 -> itemMatcher2.matches(itemStack))
         && this.potionUtilList.stream().allMatch(potionUtil -> potionUtil.matches(itemStack));
   }

   public boolean isStackable() {
      return this.item.getDefaultInstance().getMaxStackSize() > 1 || this.item == Items.TOTEM_OF_UNDYING || this.item instanceof PotionItem;
   }

   public int m_7_int() {
      if (this.item instanceof PotionItem) {
         return 16;
      } else {
         return this.item != Items.TOTEM_OF_UNDYING && !(this.item instanceof SplashPotionItem) ? this.item.getDefaultInstance().getMaxStackSize() : 6;
      }
   }

   public ItemStack m_6_ItemStack() {
      ItemStack itemStack = new ItemStack(this.item, Math.max(1, this.value));
      if (this.value2 != 0) {
         var mobEffectInstanceList = this.potionUtilList.stream().map(potionUtil -> new MobEffectInstance(potionUtil.m_3_t_Holder(), potionUtil.m_9o_int(), Math.max(0, potionUtil.m_9d() - 1))).toList();
         itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(this.value2), mobEffectInstanceList, Optional.empty()));
      }

      return itemStack;
   }
}
