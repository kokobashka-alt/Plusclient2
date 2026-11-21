package wtf.onetap.autobuy;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class FunTimeItemPresets {
   private static final List<ItemKey> ITEM_KEY_LIST = m_5_List();

   public static List<ItemKey> getKeys() {
      return ITEM_KEY_LIST;
   }

   public static ItemKey findByName(String text) {
      ItemMatcher.m_4_String();

      for (ItemKey itemKey : ITEM_KEY_LIST) {
         if (itemKey.getText().equals(text)) {
            return itemKey;
         }
      }

      return null;
   }

   private static List<ItemKey> m_5_List() {
      ArrayList list = new ArrayList();
      list.add(
         m_1(new ItemKey(Items.NETHERITE_SWORD, 1, "Незеритовый меч").withFlag(true).withFlag2(true))
            .m_4(new EnchantMatcher(Enchantments.SHARPNESS, 7))
            .m_4(new EnchantMatcher(Enchantments.FIRE_ASPECT, 2))
            .m_1(new AttributeMatcher("Яд", 3))
            .m_1(new AttributeMatcher("Вампиризм", 2))
            .m_1(new AttributeMatcher("Окисление", 2))
            .m_1(new AttributeMatcher("Опытный", 3, ItemMatcher.ModeKind.OFF))
            .m_1(new AttributeMatcher("Детекция", 3))
      );
      list.add(
         m_1(new ItemKey(Items.MACE, 1, "Булава").withFlag(true).withFlag2(true))
            .m_4(new EnchantMatcher(Enchantments.SHARPNESS, 7))
            .m_4(new EnchantMatcher(Enchantments.BREACH, 3))
            .m_4(new EnchantMatcher(Enchantments.DENSITY, 5))
      );
      list.add(
         m_1(new ItemKey(Items.TRIDENT, 1, "Трезубец").withFlag(true).withFlag2(true))
            .m_1(new AttributeMatcher("Ступор", 3))
            .m_1(new AttributeMatcher("Притяжение", 2))
            .m_1(new AttributeMatcher("Скаут", 3))
            .m_1(new AttributeMatcher("Возвращение"))
            .m_1(new AttributeMatcher("Подрывник"))
      );
      list.add(
         m_1(new ItemKey(Items.NETHERITE_HELMET, 1, "Незеритовый шлем").withFlag(true).withFlag2(true))
            .m_4(new EnchantMatcher(Enchantments.PROTECTION, 5))
            .m_4(new EnchantMatcher(Enchantments.UNBREAKING, 5))
            .m_4(new EnchantMatcher(Enchantments.RESPIRATION, 3))
            .m_4(new EnchantMatcher(Enchantments.MENDING))
      );
      list.add(
         m_1(new ItemKey(Items.NETHERITE_CHESTPLATE, 1, "Незеритовый нагрудник").withFlag(true).withFlag2(true))
            .m_4(new EnchantMatcher(Enchantments.PROTECTION, 5))
            .m_4(new EnchantMatcher(Enchantments.UNBREAKING, 5))
            .m_4(new EnchantMatcher(Enchantments.MENDING))
      );
      list.add(
         m_1(new ItemKey(Items.NETHERITE_LEGGINGS, 1, "Незеритовые поножи").withFlag(true).withFlag2(true))
            .m_4(new EnchantMatcher(Enchantments.PROTECTION, 5))
            .m_4(new EnchantMatcher(Enchantments.UNBREAKING, 5))
            .m_4(new EnchantMatcher(Enchantments.MENDING))
      );
      list.add(
         m_1(new ItemKey(Items.NETHERITE_BOOTS, 1, "Незеритовые ботинки").withFlag(true).withFlag2(true))
            .m_4(new EnchantMatcher(Enchantments.PROTECTION, 5))
            .m_4(new EnchantMatcher(Enchantments.UNBREAKING, 5))
            .m_4(new EnchantMatcher(Enchantments.DEPTH_STRIDER, 3))
            .m_4(new EnchantMatcher(Enchantments.MENDING))
      );
      list.add(m_1(new ItemKey(Items.NETHERITE_SCRAP, 8, "Трапка").withFlag(true)).m_1(new AttributeMatcher("Каст: Нерушимая клетка")));
      list.add(m_1(new ItemKey(Items.SUGAR, 12, "Явная пыль").withFlag(true)).m_1(new AttributeMatcher("Каст: Световая вспышка")));
      list.add(m_1(new ItemKey(Items.PHANTOM_MEMBRANE, 4, "Божья аура").withFlag(true)).m_1(new AttributeMatcher("Каст: Божественная аура")));
      list.add(m_1(new ItemKey(Items.ENDER_EYE, 16, "Дезориентация").withFlag(true)).m_1(new AttributeMatcher("Каст: Звуковая волна")));
      list.add(new ItemKey(Items.WIND_CHARGE, 32, "Заряд ветра").withFlag(true));
      list.add(m_1(new ItemKey(Items.DRIED_KELP, 16, "Пласт").withFlag(true)).m_1(new AttributeMatcher("Каст: Нерушимая стена")));
      list.add(m_1(new ItemKey(Items.SNOWBALL, 4, "Снежок заморозка").withFlag(true)).m_1(new AttributeMatcher("Каст: Ледяная сфера")));
      list.add(new ItemKey(Items.ENDER_PEARL, 16, "Перка").withFlag(true));
      list.add(new ItemKey(Items.TOTEM_OF_UNDYING, 1, "Тотем бессмертия").withFlag(true));
      list.add(
         m_1(new ItemKey(Items.CROSSBOW, 1, "Арбалет").withFlag(true))
            .m_4(new EnchantMatcher(Enchantments.QUICK_CHARGE, 3))
            .m_4(new EnchantMatcher(Enchantments.MENDING))
            .m_4(new EnchantMatcher(Enchantments.MULTISHOT))
      );
      list.add(new ItemKey(Items.GOLDEN_APPLE, 16, "Золотое яблоко").withFlag(true));
      list.add(new ItemKey(Items.ENCHANTED_GOLDEN_APPLE, 8, "Зачарованное золотое яб").withFlag(true));
      list.add(new ItemKey(Items.GOLDEN_CARROT, 64, "Золотая морковь").withFlag(true));
      list.add(new ItemKey(Items.CHORUS_FRUIT, 64, "Хорус").withFlag(true));
      list.add(new ItemKey(Items.ELYTRA, 1, "Элитры").withFlag(true));
      list.add(new ItemKey(Items.FIREWORK_ROCKET, 64, "Фейерверк").withFlag(true));
      list.add(
         new ItemKey(Items.SPLASH_POTION, 1, "Хлопушка")
            .withFlag(true)
            .withValue2(16711680)
            .addPotion(new PotionUtil(MobEffects.SLOWNESS, 10, 200))
            .addPotion(new PotionUtil(MobEffects.SPEED, 5, 300))
            .addPotion(new PotionUtil(MobEffects.BLINDNESS, 10, 100))
            .addPotion(new PotionUtil(MobEffects.GLOWING, 1, 3600))
      );
      list.add(
         new ItemKey(Items.SPLASH_POTION, 1, "Святая вода")
            .withFlag(true)
            .withValue2(16777215)
            .addPotion(new PotionUtil(MobEffects.REGENERATION, 2, 900))
            .addPotion(new PotionUtil(MobEffects.INVISIBILITY, 2, 12000))
            .addPotion(new PotionUtil(MobEffects.INSTANT_HEALTH, 2, 0))
      );
      list.add(
         new ItemKey(Items.SPLASH_POTION, 1, "Зелье Гнева")
            .withFlag(true)
            .withFlag2(true)
            .withValue2(10040115)
            .addPotion(new PotionUtil(MobEffects.STRENGTH, 5, 600))
            .addPotion(new PotionUtil(MobEffects.SLOWNESS, 4, 600))
      );
      list.add(
         new ItemKey(Items.SPLASH_POTION, 1, "Зелье Палладина")
            .withFlag(true)
            .withValue2(65535)
            .addPotion(new PotionUtil(MobEffects.RESISTANCE, 1, 12000))
            .addPotion(new PotionUtil(MobEffects.FIRE_RESISTANCE, 1, 12000))
            .addPotion(new PotionUtil(MobEffects.HEALTH_BOOST, 3, 1200))
            .addPotion(new PotionUtil(MobEffects.INVISIBILITY, 1, 18000))
      );
      list.add(
         new ItemKey(Items.SPLASH_POTION, 1, "Зелье Ассасина")
            .withFlag(true)
            .withValue2(3355443)
            .addPotion(new PotionUtil(MobEffects.STRENGTH, 4, 1200))
            .addPotion(new PotionUtil(MobEffects.SPEED, 3, 6000))
            .addPotion(new PotionUtil(MobEffects.HASTE, 1, 1200))
            .addPotion(new PotionUtil(MobEffects.INSTANT_DAMAGE, 2, 0))
      );
      list.add(
         new ItemKey(Items.SPLASH_POTION, 1, "Зелье Радиации")
            .withFlag(true)
            .withValue2(3329330)
            .addPotion(new PotionUtil(MobEffects.POISON, 2, 300))
            .addPotion(new PotionUtil(MobEffects.WITHER, 2, 300))
            .addPotion(new PotionUtil(MobEffects.SLOWNESS, 3, 300))
            .addPotion(new PotionUtil(MobEffects.HUNGER, 5, 300))
            .addPotion(new PotionUtil(MobEffects.GLOWING, 1, 300))
      );
      list.add(
         new ItemKey(Items.SPLASH_POTION, 1, "Снотворное")
            .withFlag(true)
            .withValue2(4737096)
            .addPotion(new PotionUtil(MobEffects.WEAKNESS, 2, 1800))
            .addPotion(new PotionUtil(MobEffects.MINING_FATIGUE, 2, 200))
            .addPotion(new PotionUtil(MobEffects.WITHER, 3, 1800))
            .addPotion(new PotionUtil(MobEffects.BLINDNESS, 1, 200))
      );
      list.add(new ItemKey(Items.POTION, 1, "Зелье").withFlag(true).addPotion(new PotionUtil(MobEffects.STRENGTH, 3, 3600)).addPotion(new PotionUtil(MobEffects.SPEED, 3, 3600)));
      list.add(
         new ItemKey(Items.POTION, 1, "Зелье регенерации")
            .withFlag(true)
            .addPotion(new PotionUtil(MobEffects.INSTANT_HEALTH, 2, 0))
            .addPotion(new PotionUtil(MobEffects.REGENERATION, 1, 900))
      );
      list.add(
         new ItemKey(Items.TIPPED_ARROW, 32, "Кровавая стрела")
            .withFlag(true)
            .addPotion(new PotionUtil(MobEffects.WEAKNESS, 3, 60))
            .addPotion(new PotionUtil(MobEffects.BLINDNESS, 1, 40))
            .addPotion(new PotionUtil(MobEffects.MINING_FATIGUE, 1, 40))
            .addPotion(new PotionUtil(MobEffects.NAUSEA, 1, 100))
      );
      list.add(new ItemKey(Items.TIPPED_ARROW, 64, "Стрела обледенения").addPotion(new PotionUtil(MobEffects.SLOWNESS, 10, 100)).addPotion(new PotionUtil(MobEffects.MINING_FATIGUE, 3, 40)));
      list.add(
         new ItemKey(Items.TIPPED_ARROW, 64, "Мучительная стрела")
            .addPotion(new PotionUtil(MobEffects.SLOWNESS, 3, 100))
            .addPotion(new PotionUtil(MobEffects.WITHER, 3, 100))
            .addPotion(new PotionUtil(MobEffects.POISON, 3, 100))
      );
      return list;
   }

   private static ItemKey m_1(ItemKey itemKey) {
      return itemKey.denyEnchantments(Enchantments.THORNS, Enchantments.KNOCKBACK, Enchantments.BINDING_CURSE, Enchantments.VANISHING_CURSE)
         .denyAttributes("Попрыгун", "Нестабильный ");
   }
}
