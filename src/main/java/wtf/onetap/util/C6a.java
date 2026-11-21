package wtf.onetap.util;

import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public record C6a(float f_6_c, float f_6_x, int f_56, int f_5_a, Difficulty f_2_d, boolean f_33, int f_5__, float f_6_n) {
   public static C6a m_0_C6a(Player player) {
      return m_3(player, 0, 0.0F);
   }

   public static C6a m_3(Player player, int value, float floatValue) {
      MobEffectInstance mobEffectInstance = player.getEffect(MobEffects.RESISTANCE);
      return new C6a(
         player.getArmorValue(),
         (float)player.getAttributeValue(Attributes.ARMOR_TOUGHNESS),
         m_8_int(player),
         mobEffectInstance == null ? 0 : mobEffectInstance.getAmplifier() + 1,
         player.level().getDifficulty(),
         player.isInvulnerable(),
         value,
         floatValue
      );
   }

   private static int m_8_int(Player player) {
      var registry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
      Reference reference = registry.getOrThrow(Enchantments.PROTECTION);
      Reference reference2 = registry.getOrThrow(Enchantments.BLAST_PROTECTION);
      int value = 0;

      for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         ItemStack itemStack = player.getItemBySlot(equipmentSlot);
         value += EnchantmentHelper.getItemEnchantmentLevel(reference2, itemStack) * 2 + EnchantmentHelper.getItemEnchantmentLevel(reference, itemStack);
      }

      return value;
   }

   private float m_8_float(int value, float floatValue, int value2) {
      double[] doubleArray = new double[]{
         value, this.f_6_c, this.f_6_x, this.f_56, this.f_5_a, this.f_2_d.getId(), this.f_33 ? 1.0 : 0.0, this.f_5__, this.f_6_n, floatValue, value2
      };
      C9_q.m_7(4, doubleArray);
      return (float)doubleArray[0];
   }

   public float m_8_float(float floatValue) {
      return this.m_8_float(0, floatValue, 0);
   }

   public float m_3_float(float floatValue) {
      return this.m_8_float(1, floatValue, 0);
   }

   public float m_0_float(float floatValue) {
      return this.m_8_float(2, floatValue, 0);
   }

   public float m_3_float(float floatValue, int value) {
      return this.m_8_float(3, floatValue, value);
   }

   public C6a m_3_C6a(float floatValue, int value) {
      float floatValue2 = this.m_8_float(floatValue);
      if (!this.m_1_boolean(value)) {
         return new C6a(this.f_6_c, this.f_6_x, this.f_56, this.f_5_a, this.f_2_d, this.f_33, value + 20, floatValue2);
      } else {
         return floatValue2 <= this.f_6_n ? this : new C6a(this.f_6_c, this.f_6_x, this.f_56, this.f_5_a, this.f_2_d, this.f_33, this.f_5__, floatValue2);
      }
   }

   public float m_4_float(float floatValue) {
      return this.m_8_float(4, floatValue, 0);
   }

   private boolean m_1_boolean(int value) {
      return this.f_5__ - value > 10;
   }

   public int m_5_t() {
      return this.f_5__ - 10;
   }

   public float m_6_c() {
      return this.f_6_c;
   }

   public float m_6_x() {
      return this.f_6_x;
   }

   public int m_56() {
      return this.f_56;
   }

   public int m_5_a() {
      return this.f_5_a;
   }

   public Difficulty m_2_d() {
      return this.f_2_d;
   }

   public boolean m_33() {
      return this.f_33;
   }

   public int m_5__() {
      return this.f_5__;
   }

   public float m_6_n_float() {
      return this.f_6_n;
   }
}
