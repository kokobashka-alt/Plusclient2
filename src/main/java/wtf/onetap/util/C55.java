package wtf.onetap.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import wtf.onetap.autobuy.ItemMatcher;

public record C55(Holder<Attribute> f_5_i, Operation f_6_a, double f_8_v) {
   public boolean m_0_boolean(ItemStack itemStack) {
      ItemMatcher.m_4_String();
      ItemAttributeModifiers itemAttributeModifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

      for (Entry entry : itemAttributeModifiers.modifiers()) {
         if (entry.attribute().equals(this.f_5_i) && entry.modifier().operation() == this.f_6_a && Math.abs(entry.modifier().amount() - this.f_8_v) < 1.0E-4) {
            return true;
         }
      }

      return false;
   }

   public Holder<Attribute> m_5_i() {
      return this.f_5_i;
   }

   public Operation m_6_a() {
      return this.f_6_a;
   }

   public double m_8_v_double() {
      return this.f_8_v;
   }
}
