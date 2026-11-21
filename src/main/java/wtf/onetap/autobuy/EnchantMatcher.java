package wtf.onetap.autobuy;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchantMatcher extends ItemMatcher {
   private final ResourceKey<Enchantment> resourceKey;

   public EnchantMatcher(ResourceKey<Enchantment> resourceKey2) {
      this(resourceKey2, 0);
   }

   public EnchantMatcher(ResourceKey<Enchantment> resourceKey2, int value) {
      this(resourceKey2, value, ItemMatcher.ModeKind.C0);
   }

   public EnchantMatcher(ResourceKey<Enchantment> resourceKey2, int value, ItemMatcher.ModeKind c0) {
      super(c0, c0 == ItemMatcher.ModeKind.C0 ? m_1(resourceKey2, value) : value);
      this.resourceKey = resourceKey2;
   }

   @Override
   public int m_7_int(int value) {
      return Math.max(1, m_1(this.resourceKey, value));
   }

   @Override
   public boolean matches(ItemStack itemStack) {
      if (this.getC0() == ItemMatcher.ModeKind.OFF) {
         return true;
      }

      Minecraft mc = Minecraft.getInstance();
      if (mc.level == null) {
         return true;
      }

      var registry = mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
      if (!registry.containsKey(this.resourceKey)) {
         return true;
      }

      Reference reference = registry.getOrThrow(this.resourceKey);
      int value = EnchantmentHelper.getItemEnchantmentLevel(reference, itemStack);
      int value2 = this.getValue() == 0 ? 1 : this.getValue();
      return this.getC0() == ItemMatcher.ModeKind.DENY ? value < value2 : value >= value2;
   }

   @Override
   public String getText() {
      return this.resourceKey.identifier().toString();
   }

   @Override
   public String m_5_String() {
      String text = this.resourceKey.identifier().getPath().replace("/", ".");
      String text2 = Component.translatable("enchantment." + this.resourceKey.identifier().getNamespace() + "." + text).getString();
      return this.getValue() == 0 ? text2 : text2 + " " + this.getValue();
   }

   private static int m_1(ResourceKey<Enchantment> resourceKey2, int value) {
      if (value <= 0) {
         return 0;
      }

      Minecraft mc = Minecraft.getInstance();
      if (mc.level == null) {
         return value;
      }

      var registry = mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
      if (!registry.containsKey(resourceKey2)) {
         return value;
      }

      int value2;
      if (resourceKey2.equals(Enchantments.SHARPNESS)) {
         value2 = 7;
      } else if (!resourceKey2.equals(Enchantments.PROTECTION) && !resourceKey2.equals(Enchantments.UNBREAKING)) {
         value2 = ((Enchantment)registry.getOrThrow(resourceKey2).value()).getMaxLevel();
      } else {
         value2 = 5;
      }

      return Math.max(1, Math.min(value2, value));
   }

   private static String b(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
