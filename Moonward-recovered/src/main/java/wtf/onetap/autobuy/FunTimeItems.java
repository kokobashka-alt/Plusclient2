package wtf.onetap.autobuy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import wtf.onetap.util.C55;

public class FunTimeItems {
   private static List<ItemUtil> itemUtilList;

   private FunTimeItems() {
   }

   public static List<ItemUtil> getItems() {
      if (itemUtilList == null) {
         itemUtilList = new ArrayList<>();
         m_3_void(itemUtilList);
      }

      return itemUtilList;
   }

   public static List<ItemUtil> getEnabledItems() {
      ArrayList list = new ArrayList();

      for (ItemUtil itemUtil : getItems()) {
         if (itemUtil.isFlag2()) {
            list.add(itemUtil);
         }
      }

      return list;
   }

   @SafeVarargs
   private static Predicate<ItemStack> itemPredicate(Item item, Predicate<ItemStack>... predicateArray) {
      return itemStack -> {
         if (!itemStack.is(item)) {
            return false;
         }

         for (Predicate predicate : predicateArray) {
            if (!predicate.test(itemStack)) {
               return false;
            }
         }

         return true;
      };
   }

   private static Predicate<ItemStack> itemPredicate(Item item) {
      return itemStack -> itemStack.is(item);
   }

   private static void m_3_void(List<ItemUtil> itemUtilList2) {
      itemUtilList2.add(
         new ItemUtil(
               "[★] Хлопушка",
               Items.SPLASH_POTION,
               0,
               itemPredicate(
                  Items.SPLASH_POTION,
                  new PotionUtil(MobEffects.SLOWNESS, 10, 200)::matches,
                  new PotionUtil(MobEffects.SPEED, 5, 300)::matches,
                  new PotionUtil(MobEffects.BLINDNESS, 10, 100)::matches,
                  new PotionUtil(MobEffects.GLOWING, 1, 3600)::matches
               )
            )
            .withValue3(16711680)
      );
      itemUtilList2.add(
         new ItemUtil(
               "[★] Святая вода",
               Items.SPLASH_POTION,
               0,
               itemPredicate(
                  Items.SPLASH_POTION,
                  new PotionUtil(MobEffects.REGENERATION, 2, 900)::matches,
                  new PotionUtil(MobEffects.INVISIBILITY, 2, 12000)::matches,
                  new PotionUtil(MobEffects.INSTANT_HEALTH, 2, 0)::matches
               )
            )
            .withValue3(16777215)
      );
      itemUtilList2.add(
         new ItemUtil(
               "[★] Зелье Гнева",
               Items.SPLASH_POTION,
               0,
               itemPredicate(Items.SPLASH_POTION, new PotionUtil(MobEffects.STRENGTH, 5, 600)::matches, new PotionUtil(MobEffects.SLOWNESS, 4, 600)::matches)
            )
            .withValue3(10040115)
      );
      itemUtilList2.add(
         new ItemUtil(
               "[★] Зелье Палладина",
               Items.SPLASH_POTION,
               0,
               itemPredicate(
                  Items.SPLASH_POTION,
                  new PotionUtil(MobEffects.RESISTANCE, 1, 12000)::matches,
                  new PotionUtil(MobEffects.FIRE_RESISTANCE, 1, 12000)::matches,
                  new PotionUtil(MobEffects.HEALTH_BOOST, 3, 1200)::matches,
                  new PotionUtil(MobEffects.INVISIBILITY, 1, 18000)::matches
               )
            )
            .withValue3(65535)
      );
      itemUtilList2.add(
         new ItemUtil(
               "[★] Зелье Ассасина",
               Items.SPLASH_POTION,
               0,
               itemPredicate(
                  Items.SPLASH_POTION,
                  new PotionUtil(MobEffects.STRENGTH, 4, 1200)::matches,
                  new PotionUtil(MobEffects.SPEED, 3, 6000)::matches,
                  new PotionUtil(MobEffects.HASTE, 1, 1200)::matches,
                  new PotionUtil(MobEffects.INSTANT_DAMAGE, 2, 0)::matches
               )
            )
            .withValue3(3355443)
      );
      itemUtilList2.add(
         new ItemUtil(
               "[★] Зелье Радиации",
               Items.SPLASH_POTION,
               0,
               itemPredicate(
                  Items.SPLASH_POTION,
                  new PotionUtil(MobEffects.POISON, 2, 300)::matches,
                  new PotionUtil(MobEffects.WITHER, 2, 300)::matches,
                  new PotionUtil(MobEffects.SLOWNESS, 3, 300)::matches,
                  new PotionUtil(MobEffects.HUNGER, 5, 300)::matches,
                  new PotionUtil(MobEffects.GLOWING, 1, 300)::matches
               )
            )
            .withValue3(3329330)
      );
      itemUtilList2.add(
         new ItemUtil(
               "[★] Снотворное",
               Items.SPLASH_POTION,
               0,
               itemPredicate(
                  Items.SPLASH_POTION,
                  new PotionUtil(MobEffects.WEAKNESS, 2, 1800)::matches,
                  new PotionUtil(MobEffects.MINING_FATIGUE, 2, 200)::matches,
                  new PotionUtil(MobEffects.WITHER, 3, 1800)::matches,
                  new PotionUtil(MobEffects.BLINDNESS, 1, 200)::matches
               )
            )
            .withValue3(4737096)
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Талисман Крушителя",
            Items.TOTEM_OF_UNDYING,
            0,
            itemPredicate(
               Items.TOTEM_OF_UNDYING,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 4.0)::m_0_boolean,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 3.0)::m_0_boolean,
               new C55(Attributes.ARMOR_TOUGHNESS, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, 2.0)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Талисман Раздора",
            Items.TOTEM_OF_UNDYING,
            0,
            itemPredicate(
               Items.TOTEM_OF_UNDYING,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 4.0)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.10000001817743467)::m_0_boolean,
               new C55(Attributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.10000001817743467)::m_0_boolean,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, -3.0)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Талисман Тирана",
            Items.TOTEM_OF_UNDYING,
            0,
            itemPredicate(
               Items.TOTEM_OF_UNDYING,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, -4.0)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Талисман Ярости",
            Items.TOTEM_OF_UNDYING,
            0,
            itemPredicate(
               Items.TOTEM_OF_UNDYING,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 5.0)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, -4.0)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Талисман Вихря",
            Items.TOTEM_OF_UNDYING,
            0,
            itemPredicate(
               Items.TOTEM_OF_UNDYING,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.1500000347540806)::m_0_boolean,
               new C55(Attributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.1500000347540806)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Талисман Мрака",
            Items.TOTEM_OF_UNDYING,
            0,
            itemPredicate(
               Items.TOTEM_OF_UNDYING,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, 1.5)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 1.5)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Талисман Демона",
            Items.TOTEM_OF_UNDYING,
            0,
            itemPredicate(
               Items.TOTEM_OF_UNDYING,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 2.5)::m_0_boolean,
               new C55(Attributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.10000001817743467)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Талисман Карателя",
            Items.TOTEM_OF_UNDYING,
            0,
            itemPredicate(
               Items.TOTEM_OF_UNDYING,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 7.0)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, -4.0)::m_0_boolean,
               new C55(Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.10000001817743467)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Сфера Хаоса",
            Items.PLAYER_HEAD,
            0,
            itemPredicate(
               Items.PLAYER_HEAD,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, -4.0)::m_0_boolean,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, 1.5)::m_0_boolean,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 2.5)::m_0_boolean,
               new C55(Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.07000004566537438)::m_0_boolean,
               new C55(Attributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.13000000024761527)::m_0_boolean,
               new C55(Attributes.GRAVITY, Operation.ADD_MULTIPLIED_BASE, 0.09000001839414021)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Сфера Сатира",
            Items.PLAYER_HEAD,
            0,
            itemPredicate(
               Items.PLAYER_HEAD,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.JUMP_STRENGTH, Operation.ADD_MULTIPLIED_BASE, -0.10000000768432185)::m_0_boolean,
               new C55(Attributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.1500000347540806)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Сфера Бестии",
            Items.PLAYER_HEAD,
            0,
            itemPredicate(
               Items.PLAYER_HEAD,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, 1.0)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 4.0)::m_0_boolean,
               new C55(Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.10000001817743467)::m_0_boolean,
               new C55(Attributes.ATTACK_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.10000001817743467)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Сфера Ареса",
            Items.PLAYER_HEAD,
            0,
            itemPredicate(
               Items.PLAYER_HEAD,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 6.0)::m_0_boolean,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, -2.0)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, -2.0)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Сфера Гидры",
            Items.PLAYER_HEAD,
            0,
            itemPredicate(
               Items.PLAYER_HEAD,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 4.0)::m_0_boolean,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.SUBMERGED_MINING_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.5)::m_0_boolean,
               new C55(Attributes.OXYGEN_BONUS, Operation.ADD_MULTIPLIED_BASE, 0.5)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Сфера Икара",
            Items.PLAYER_HEAD,
            0,
            itemPredicate(
               Items.PLAYER_HEAD,
               new C55(Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 2.0)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Сфера Эрида",
            Items.PLAYER_HEAD,
            0,
            itemPredicate(
               Items.PLAYER_HEAD,
               new C55(Attributes.LUCK, Operation.ADD_VALUE, 1.0)::m_0_boolean,
               new C55(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 2.0)::m_0_boolean,
               new C55(Attributes.BLOCK_INTERACTION_RANGE, Operation.ADD_VALUE, 1.0)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "[★] Сфера Титана",
            Items.PLAYER_HEAD,
            0,
            itemPredicate(
               Items.PLAYER_HEAD,
               new C55(Attributes.ARMOR, Operation.ADD_VALUE, 2.5)::m_0_boolean,
               new C55(Attributes.ARMOR_TOUGHNESS, Operation.ADD_VALUE, 2.5)::m_0_boolean,
               new C55(Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, -0.15000007945819008)::m_0_boolean
            )
         )
      );
      itemUtilList2.add(new ItemUtil("[★] Трапка", Items.NETHERITE_SCRAP, 0, itemPredicate(Items.NETHERITE_SCRAP, new AttributeMatcher("Каст: Нерушимая клетка")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Явная пыль", Items.SUGAR, 0, itemPredicate(Items.SUGAR, new AttributeMatcher("Каст: Световая вспышка")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Снежок заморозка", Items.SNOWBALL, 0, itemPredicate(Items.SNOWBALL, new AttributeMatcher("Каст: Ледяная сфера")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Божья аура", Items.PHANTOM_MEMBRANE, 0, itemPredicate(Items.PHANTOM_MEMBRANE, new AttributeMatcher("Каст: Божественная аура")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Дезориентация", Items.ENDER_EYE, 0, itemPredicate(Items.ENDER_EYE, new AttributeMatcher("Каст: Звуковая волна")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Пласт", Items.DRIED_KELP, 0, itemPredicate(Items.DRIED_KELP, new AttributeMatcher("Каст: Нерушимая стена")::matches)));
      itemUtilList2.add(
         new ItemUtil(
            "xxx Меч Крушителя xxx",
            Items.NETHERITE_SWORD,
            0,
            itemPredicate(
               Items.NETHERITE_SWORD,
               new EnchantMatcher(Enchantments.KNOCKBACK, 0, ItemMatcher.ModeKind.DENY)::matches,
               new AttributeMatcher("[★] Оригинальный предмет")::matches,
               new AttributeMatcher("Опытный", 3)::matches,
               new AttributeMatcher("Вампиризм", 2)::matches,
               new AttributeMatcher("Окисление", 2)::matches,
               new AttributeMatcher("Яд", 3)::matches,
               new AttributeMatcher("Детекция", 3)::matches
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "xxx Булава Крушителя xxx",
            Items.MACE,
            0,
            itemPredicate(
               Items.MACE,
               new AttributeMatcher("[★] Оригинальный предмет")::matches,
               new AttributeMatcher("Опытный", 3)::matches,
               new AttributeMatcher("Вампиризм", 2)::matches,
               new AttributeMatcher("Окисление", 2)::matches,
               new AttributeMatcher("Яд", 3)::matches,
               new AttributeMatcher("Детекция", 3)::matches
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "xxx Трезубец Крушителя xxx",
            Items.TRIDENT,
            0,
            itemPredicate(
               Items.TRIDENT,
               new AttributeMatcher("[★] Оригинальный предмет")::matches,
               new AttributeMatcher("Скаут", 3)::matches,
               new AttributeMatcher("Ступор", 3)::matches,
               new AttributeMatcher("Притяжение", 2)::matches,
               new AttributeMatcher("Возвращение")::matches,
               new AttributeMatcher("Подрывник")::matches
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "xxx Кирка Крушителя xxx",
            Items.NETHERITE_PICKAXE,
            0,
            itemPredicate(
               Items.NETHERITE_PICKAXE,
               new AttributeMatcher("[★] Оригинальный предмет")::matches,
               new AttributeMatcher("Бульдозер", 2)::matches,
               new AttributeMatcher("Опытный", 3)::matches,
               new AttributeMatcher("Магнит")::matches,
               new AttributeMatcher("Авто-Плавка")::matches,
               new AttributeMatcher("Паутина")::matches,
               new AttributeMatcher("Пингер")::matches
            )
         )
      );
      itemUtilList2.add(new ItemUtil("xxx Арбалет Крушителя xxx", Items.CROSSBOW, 0, itemPredicate(Items.CROSSBOW, new AttributeMatcher("[★] Оригинальный предмет")::matches)));
      itemUtilList2.add(
         new ItemUtil(
            "xxx Шлем Крушителя xxx",
            Items.NETHERITE_HELMET,
            0,
            itemPredicate(
               Items.NETHERITE_HELMET,
               new EnchantMatcher(Enchantments.PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.FIRE_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.BLAST_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.PROJECTILE_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.UNBREAKING, 5)::matches,
               new EnchantMatcher(Enchantments.MENDING, 1)::matches,
               new EnchantMatcher(Enchantments.AQUA_AFFINITY, 1)::matches,
               new EnchantMatcher(Enchantments.RESPIRATION, 3)::matches,
               new EnchantMatcher(Enchantments.THORNS, 0, ItemMatcher.ModeKind.DENY)::matches
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "xxx Нагрудник Крушителя xxx",
            Items.NETHERITE_CHESTPLATE,
            0,
            itemPredicate(
               Items.NETHERITE_CHESTPLATE,
               new EnchantMatcher(Enchantments.PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.FIRE_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.BLAST_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.PROJECTILE_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.UNBREAKING, 5)::matches,
               new EnchantMatcher(Enchantments.MENDING, 1)::matches,
               new EnchantMatcher(Enchantments.THORNS, 0, ItemMatcher.ModeKind.DENY)::matches
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "xxx Поножи Крушителя xxx",
            Items.NETHERITE_LEGGINGS,
            0,
            itemPredicate(
               Items.NETHERITE_LEGGINGS,
               new EnchantMatcher(Enchantments.PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.FIRE_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.BLAST_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.PROJECTILE_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.UNBREAKING, 5)::matches,
               new EnchantMatcher(Enchantments.MENDING, 1)::matches,
               new EnchantMatcher(Enchantments.THORNS, 0, ItemMatcher.ModeKind.DENY)::matches
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "xxx Ботинки Крушителя xxx",
            Items.NETHERITE_BOOTS,
            0,
            itemPredicate(
               Items.NETHERITE_BOOTS,
               new EnchantMatcher(Enchantments.PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.FIRE_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.BLAST_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.PROJECTILE_PROTECTION, 5)::matches,
               new EnchantMatcher(Enchantments.UNBREAKING, 5)::matches,
               new EnchantMatcher(Enchantments.MENDING, 1)::matches,
               new EnchantMatcher(Enchantments.FEATHER_FALLING, 4)::matches,
               new EnchantMatcher(Enchantments.SOUL_SPEED, 3)::matches,
               new EnchantMatcher(Enchantments.DEPTH_STRIDER, 3)::matches,
               new EnchantMatcher(Enchantments.THORNS, 0, ItemMatcher.ModeKind.DENY)::matches
            )
         )
      );
      itemUtilList2.add(new ItemUtil("[★] Отмычка к Сферам", Items.TRIPWIRE_HOOK, 0, itemPredicate(Items.TRIPWIRE_HOOK, new AttributeMatcher("С Сферами")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Божье касание", Items.GOLDEN_PICKAXE, 0, itemPredicate(Items.GOLDEN_PICKAXE, new AttributeMatcher("Может добыть спавнер")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Мощный удар", Items.GOLDEN_PICKAXE, 0, itemPredicate(Items.GOLDEN_PICKAXE, new AttributeMatcher("Может разрушить бедрок")::matches)));
      itemUtilList2.add(new ItemUtil("Обычный мист", Items.CAMPFIRE, 0, itemPredicate(Items.CAMPFIRE, new AttributeMatcher("Уровень лута: Обычный")::matches)));
      itemUtilList2.add(new ItemUtil("Богатый мист", Items.CAMPFIRE, 0, itemPredicate(Items.CAMPFIRE, new AttributeMatcher("Уровень лута: Богатый")::matches)));
      itemUtilList2.add(new ItemUtil("Легендарный мист", Items.SOUL_CAMPFIRE, 0, itemPredicate(Items.SOUL_CAMPFIRE, new AttributeMatcher("Уровень лута: Легендарный")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Неизбежный скин", Items.PAPER, 0, itemPredicate(Items.PAPER, new AttributeMatcher("получаете Неизбежный скин")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Драконий скин", Items.PAPER, 0, itemPredicate(Items.PAPER, new AttributeMatcher("получаете Драконий скин")::matches)));
      itemUtilList2.add(
         new ItemUtil("[★] Прогрузчик чанков [1x1]", Items.STRUCTURE_BLOCK, 0, itemPredicate(Items.STRUCTURE_BLOCK, new AttributeMatcher("прогружаемой области (1x1)")::matches))
      );
      itemUtilList2.add(
         new ItemUtil("[★] Прогрузчик чанков [3x3]", Items.STRUCTURE_BLOCK, 0, itemPredicate(Items.STRUCTURE_BLOCK, new AttributeMatcher("прогружаемой области (3x3)")::matches))
      );
      itemUtilList2.add(
         new ItemUtil("[★] Прогрузчик чанков [5x5]", Items.STRUCTURE_BLOCK, 0, itemPredicate(Items.STRUCTURE_BLOCK, new AttributeMatcher("прогружаемой области (5x5)")::matches))
      );
      itemUtilList2.add(new ItemUtil("[★] Регион 25x25", Items.CHAIN_COMMAND_BLOCK, 0, itemPredicate(Items.CHAIN_COMMAND_BLOCK, new AttributeMatcher("Размер: 25x25 блоков")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Аирдроп", Items.REDSTONE_TORCH, 0, itemPredicate(Items.REDSTONE_TORCH, new AttributeMatcher("призыва Аирдропа")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Блок дамагер", Items.JIGSAW, 0, itemPredicate(Items.JIGSAW, new AttributeMatcher("Каст: Нанесение урона")::matches)));
      itemUtilList2.add(new ItemUtil("[⚡] Модификатор полёта", Items.FEATHER, 0, itemPredicate(Items.FEATHER, new AttributeMatcher("Доступ к /fly")::matches)));
      itemUtilList2.add(new ItemUtil("[⚡] Модификатор починки", Items.AMETHYST_SHARD, 0, itemPredicate(Items.AMETHYST_SHARD, new AttributeMatcher("Доступ к /fix")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Ключ от кейса с Привилегиями", Items.TRIAL_KEY, 0, itemPredicate(Items.TRIAL_KEY, new AttributeMatcher("Открывает: Кейс с Привилегиями")::matches)));
      itemUtilList2.add(new ItemUtil("[★] Ключ от кейса с Токенами", Items.TRIAL_KEY, 0, itemPredicate(Items.TRIAL_KEY, new AttributeMatcher("Открывает: Кейс с Токенами")::matches)));
      itemUtilList2.add(
         new ItemUtil(
            "Кровавая стрела",
            Items.TIPPED_ARROW,
            0,
            itemPredicate(
               Items.TIPPED_ARROW,
               new PotionUtil(MobEffects.WEAKNESS, 3, 60)::matches,
               new PotionUtil(MobEffects.BLINDNESS, 1, 40)::matches,
               new PotionUtil(MobEffects.MINING_FATIGUE, 1, 40)::matches,
               new PotionUtil(MobEffects.NAUSEA, 1, 100)::matches
            )
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "Стрела обледенения",
            Items.TIPPED_ARROW,
            0,
            itemPredicate(Items.TIPPED_ARROW, new PotionUtil(MobEffects.SLOWNESS, 10, 100)::matches, new PotionUtil(MobEffects.MINING_FATIGUE, 3, 40)::matches)
         )
      );
      itemUtilList2.add(
         new ItemUtil(
            "Мучительная стрела",
            Items.TIPPED_ARROW,
            0,
            itemPredicate(
               Items.TIPPED_ARROW,
               new PotionUtil(MobEffects.SLOWNESS, 3, 100)::matches,
               new PotionUtil(MobEffects.WITHER, 3, 100)::matches,
               new PotionUtil(MobEffects.POISON, 3, 100)::matches
            )
         )
      );
      itemUtilList2.add(new ItemUtil("Золотое яблоко", Items.GOLDEN_APPLE, 0, itemPredicate(Items.GOLDEN_APPLE)));
      itemUtilList2.add(new ItemUtil("Чарка", Items.ENCHANTED_GOLDEN_APPLE, 0, itemPredicate(Items.ENCHANTED_GOLDEN_APPLE)));
      itemUtilList2.add(new ItemUtil("Спавнер", Items.SPAWNER, 0, itemPredicate(Items.SPAWNER)));
      itemUtilList2.add(new ItemUtil("Изумрудная руда", Items.EMERALD_ORE, 0, itemPredicate(Items.EMERALD_ORE)));
      itemUtilList2.add(new ItemUtil("Голова дракона", Items.DRAGON_HEAD, 0, itemPredicate(Items.DRAGON_HEAD)));
      itemUtilList2.add(new ItemUtil("Яйцо дракона", Items.DRAGON_EGG, 0, itemPredicate(Items.DRAGON_EGG)));
      itemUtilList2.add(new ItemUtil("Элитры", Items.ELYTRA, 0, itemPredicate(Items.ELYTRA)));
      itemUtilList2.add(new ItemUtil("Маяк", Items.BEACON, 0, itemPredicate(Items.BEACON)));
      itemUtilList2.add(new ItemUtil("Яйцо призыва крестьянина", Items.VILLAGER_SPAWN_EGG, 0, itemPredicate(Items.VILLAGER_SPAWN_EGG)));
      itemUtilList2.add(new ItemUtil("Яйцо призыва эндермена", Items.ENDERMAN_SPAWN_EGG, 0, itemPredicate(Items.ENDERMAN_SPAWN_EGG)));
      itemUtilList2.add(new ItemUtil("Отделка незеритовая", Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 0, itemPredicate(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)));
      itemUtilList2.add(new ItemUtil("Алмаз", Items.DIAMOND, 0, itemPredicate(Items.DIAMOND)));
      itemUtilList2.add(new ItemUtil("Звезда Незера", Items.NETHER_STAR, 0, itemPredicate(Items.NETHER_STAR)));
      itemUtilList2.add(new ItemUtil("Древние обломки", Items.ANCIENT_DEBRIS, 0, itemPredicate(Items.ANCIENT_DEBRIS)));
      itemUtilList2.add(new ItemUtil("Незеритовый слиток", Items.NETHERITE_INGOT, 0, itemPredicate(Items.NETHERITE_INGOT)));

      for (ItemUtil itemUtil : itemUtilList2) {
         itemUtil.setFlag2(false);
      }
   }
}
