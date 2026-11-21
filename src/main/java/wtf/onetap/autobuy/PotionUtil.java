package wtf.onetap.autobuy;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public record PotionUtil(Holder<MobEffect> f_3_t, int f_9d, int f_9o) {
   public boolean matches(ItemStack itemStack) {
      ItemMatcher.m_4_String();
      PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
      if (potionContents == null) {
         return false;
      }

      for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
         if (mobEffectInstance.getEffect().equals(this.f_3_t) && mobEffectInstance.getAmplifier() + 1 == this.f_9d && mobEffectInstance.getDuration() >= this.f_9o) {
            return true;
         }
      }

      return false;
   }

   public Holder<MobEffect> m_3_t_Holder() {
      return this.f_3_t;
   }

   public int m_9d() {
      return this.f_9d;
   }

   public int m_9o_int() {
      return this.f_9o;
   }
}
