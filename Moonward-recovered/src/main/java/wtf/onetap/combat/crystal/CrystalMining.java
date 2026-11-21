package wtf.onetap.combat.crystal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlocksAttacks.DamageReduction;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import wtf.onetap.util.C9_q;
import wtf.onetap.util.LatencyStats;
import wtf.onetap.util.MinecraftAccess;

public class CrystalMining implements MinecraftAccess {
   private static final EquipmentSlot[] EQUIPMENT_SLOT_ARRAY = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final LatencyStats.StateHolder C0 = LatencyStats.getOrCreate("CrystalAura native");
   private static final LatencyStats.StateHolder C02 = LatencyStats.getOrCreate("CrystalAura mining");
   private static final Map<CrystalMining.DataRecord, Float> C4_MAP = new HashMap<>();
   private static final Map<BlockState, Float> BLOCK_STATE_MAP = new HashMap<>();
   private static final Map<BlockState, Boolean> BLOCK_STATE_MAP2 = new HashMap<>();
   private static final Map<BlockState, Integer> BLOCK_STATE_MAP3 = new HashMap<>();
   private static int value;
   private static int value2 = -1;

   private CrystalMining() {
   }

   public static int getTickSeed() {
      return MINECRAFT.player == null ? -1 : MINECRAFT.player.tickCount * 31 + MINECRAFT.player.getInventory().getSelectedSlot();
   }

   public static void onTickStart() {
      if (value++ <= 0) {
         C4_MAP.clear();
         int value3 = getTickSeed();
         if (value3 != value2) {
            BLOCK_STATE_MAP.clear();
            BLOCK_STATE_MAP2.clear();
            BLOCK_STATE_MAP3.clear();
            value2 = value3;
         }
      }
   }

   public static void tickCooldown() {
      if (value > 0) {
         value--;
      }
   }

   public static float m_3(Vec3 vec3, Entity entity) {
      return entity == null ? 0.0F : m_6(vec3, entity, entity.getBoundingBox());
   }

   public static float m_6(Vec3 vec3, Entity entity, AABB aABB) {
      return entity instanceof LivingEntity livingEntity ? m_1(vec3, livingEntity, aABB, null, List.of()) : 0.0F;
   }

   public static float m_1(Vec3 vec3, LivingEntity livingEntity, AABB aABB, @Nullable AABB aABB2, List<BlockPos> blockPosList) {
      return m_2(vec3, livingEntity, aABB, aABB2, blockPosList, 12.0);
   }

   public static float m_2(Vec3 vec3, LivingEntity livingEntity, AABB aABB, @Nullable AABB aABB2, List<BlockPos> blockPosList, double doubleValue) {
      if (vec3 == null || livingEntity == null || aABB == null || MINECRAFT.level == null) {
         return 0.0F;
      }

      if (!livingEntity.isAlive()) {
         return 0.0F;
      }

      Vec3 vec32 = new Vec3((aABB.minX + aABB.maxX) * 0.5, aABB.minY, (aABB.minZ + aABB.maxZ) * 0.5);
      double doubleValue2 = vec32.distanceTo(vec3);
      if (doubleValue2 > doubleValue) {
         return 0.0F;
      }

      float floatValue = m_8(vec3, aABB, livingEntity, aABB2, blockPosList == null ? List.of() : blockPosList);
      double[] doubleArray = new double[2];
      m_5(livingEntity, vec3.subtract(vec32), doubleArray, 0);
      return m_5(livingEntity, doubleValue2, floatValue, doubleValue, doubleArray[0], doubleArray[1]);
   }

   public static float m_9(Vec3 vec3, LivingEntity livingEntity, AABB aABB) {
      return m_9(vec3, livingEntity, aABB, 12.0);
   }

   public static float m_9(Vec3 vec3, LivingEntity livingEntity, AABB aABB, double doubleValue) {
      if (vec3 != null && livingEntity != null && aABB != null && livingEntity.isAlive()) {
         double doubleValue2 = new Vec3((aABB.minX + aABB.maxX) * 0.5, aABB.minY, (aABB.minZ + aABB.maxZ) * 0.5).distanceTo(vec3);
         return doubleValue2 > doubleValue ? 0.0F : m_5(livingEntity, doubleValue2, 1.0F, doubleValue, 0.0, 0.0);
      } else {
         return 0.0F;
      }
   }

   private static float m_5(LivingEntity livingEntity, double doubleValue, float floatValue, double doubleValue2, double doubleValue3, double doubleValue4) {
      MobEffectInstance mobEffectInstance = livingEntity.getEffect(MobEffects.RESISTANCE);
      return m_6_float(
         0.0,
         doubleValue,
         floatValue,
         doubleValue2,
         livingEntity.getArmorValue(),
         (float)livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS),
         getProtectionLevel(livingEntity),
         mobEffectInstance == null ? 0.0 : mobEffectInstance.getAmplifier() + 1,
         doubleValue3,
         doubleValue4
      );
   }

   private static void m_5(LivingEntity livingEntity, Vec3 vec3, double[] doubleArray, int value3) {
      ItemStack itemStack = livingEntity.getItemBlockingWith();
      BlocksAttacks blocksAttacks = itemStack == null ? null : itemStack.get(DataComponents.BLOCKS_ATTACKS);
      if (blocksAttacks != null && MINECRAFT.level != null) {
         DamageSource damageSource = MINECRAFT.level.damageSources().explosion(null, null);
         if (!blocksAttacks.bypassedBy().map(holderSet -> holderSet.contains(damageSource.typeHolder())).orElse(false)) {
            double doubleValue = Math.acos(new Vec3(vec3.x, 0.0, vec3.z).normalize().dot(livingEntity.calculateViewVector(0.0F, livingEntity.getYHeadRot())));

            for (DamageReduction damageReduction : blocksAttacks.damageReductions()) {
               if (!(doubleValue > (float) (Math.PI / 180.0) * damageReduction.horizontalBlockingAngle())
                  && (!damageReduction.type().isPresent() || damageReduction.type().get().contains(damageSource.typeHolder()))) {
                  damageReduction.base();
                  damageReduction.factor();
               }
            }
         }
      }
   }

   public static int m_2_int() {
      int value3 = MINECRAFT.player.getInventory().getSelectedSlot();
      boolean flag = MINECRAFT.player.hasEffect(MobEffects.WEAKNESS);
      double[] doubleArray = new double[12];
      doubleArray[0] = 5.0;
      doubleArray[1] = value3;
      doubleArray[2] = flag ? 1.0 : 0.0;
      if (flag) {
         Reference reference = MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS);

         for (int i = 0; i < 9; i++) {
            doubleArray[3 + i] = EnchantmentHelper.getItemEnchantmentLevel(reference, MINECRAFT.player.getInventory().getItem(i));
         }
      }

      return (int)m_6_float(doubleArray);
   }

   private static float m_6_float(double... doubleArray) {
      long longValue = System.nanoTime();

      try {
         return (float)C9_q.m_3(5, doubleArray)[0];
      } finally {
         C0.recordSince(longValue);
      }
   }

   public static float m_1_float(BlockState blockState, BlockPos blockPos) {
      if (blockState != null && blockPos != null && MINECRAFT.player != null && MINECRAFT.level != null) {
         Float floatObj = value > 0 ? BLOCK_STATE_MAP.get(blockState) : null;
         if (floatObj != null) {
            return floatObj;
         }

         long longValue = System.nanoTime();

         try {
            float floatValue = blockState.getDestroySpeed(MINECRAFT.level, blockPos);
            int value3 = m_5(blockState);
            ItemStack itemStack = value3 >= 0 ? MINECRAFT.player.getInventory().getItem(value3) : ItemStack.EMPTY;
            boolean flag = MobEffectUtil.hasDigSpeed(MINECRAFT.player);
            MobEffectInstance mobEffectInstance = MINECRAFT.player.getEffect(MobEffects.MINING_FATIGUE);
            float floatValue2 = m_6_float(
               1.0,
               floatValue,
               itemStack.isEmpty() ? 1.0 : itemStack.getDestroySpeed(blockState),
               getEfficiency(itemStack),
               flag ? 1.0 : 0.0,
               flag ? MobEffectUtil.getDigSpeedAmplification(MINECRAFT.player) : 0.0,
               mobEffectInstance == null ? 0.0 : 1.0,
               mobEffectInstance == null ? 0.0 : mobEffectInstance.getAmplifier(),
               (float)MINECRAFT.player.getAttributeValue(Attributes.BLOCK_BREAK_SPEED),
               MINECRAFT.player.isEyeInFluid(FluidTags.WATER) ? (float)MINECRAFT.player.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED) : 1.0,
               MINECRAFT.player.onGround() ? 1.0 : 0.0,
               canHarvest(itemStack, blockState) ? 1.0 : 0.0
            );
            if (value > 0) {
               BLOCK_STATE_MAP.put(blockState, floatValue2);
            }

            return floatValue2;
         } finally {
            C02.recordSince(longValue);
         }
      } else {
         return Float.POSITIVE_INFINITY;
      }
   }

   public static int m_5(BlockState blockState) {
      if (blockState != null && MINECRAFT.player != null) {
         Integer integer = value > 0 ? BLOCK_STATE_MAP3.get(blockState) : null;
         if (integer != null) {
            return integer;
         }

         long longValue = System.nanoTime();

         try {
            double[] doubleArray = new double[32];
            doubleArray[0] = 4.0;
            doubleArray[1] = 9.0;
            doubleArray[2] = 1.0;
            doubleArray[3] = 0.0;
            doubleArray[4] = canHarvest(ItemStack.EMPTY, blockState) ? 1.0 : 0.0;

            for (int i = 0; i < 9; i++) {
               ItemStack itemStack = MINECRAFT.player.getInventory().getItem(i);
               int value3 = 5 + i * 3;
               doubleArray[value3] = itemStack.isEmpty() ? 1.0 : itemStack.getDestroySpeed(blockState);
               doubleArray[value3 + 1] = getEfficiency(itemStack);
               doubleArray[value3 + 2] = (canHarvest(itemStack, blockState) ? 1 : 0) | (isShearsWithoutEfficiency(itemStack) ? 2 : 0);
            }

            long longValue2 = System.nanoTime();

            boolean flag;
            try {
               flag = C9_q.m_7(5, doubleArray);
            } finally {
               C0.recordSince(longValue2);
            }

            int value4 = flag ? (int)doubleArray[0] : findBestToolSlot(blockState);
            if (value > 0) {
               BLOCK_STATE_MAP3.put(blockState, value4);
            }

            return value4;
         } finally {
            C02.recordSince(longValue);
         }
      } else {
         return -1;
      }
   }

   private static int findBestToolSlot(BlockState blockState) {
      int value3 = -1;
      ItemStack itemStack = ItemStack.EMPTY;
      float floatValue = m_0_float(itemStack, blockState);

      for (int i = 0; i < 9; i++) {
         ItemStack itemStack2 = MINECRAFT.player.getInventory().getItem(i);
         float floatValue2 = m_0_float(itemStack2, blockState);
         if (m_5(itemStack2, itemStack, floatValue2, floatValue, blockState)) {
            value3 = i;
            itemStack = itemStack2;
            floatValue = floatValue2;
         }
      }

      return value3;
   }

   public static float m_8(Vec3 vec3, AABB aABB, Entity entity, @Nullable AABB aABB2, List<BlockPos> blockPosList) {
      List list = blockPosList == null ? List.of() : blockPosList;
      if (value == 0) {
         return m_1(vec3, aABB, entity, aABB2, list);
      }

      CrystalMining.DataRecord c4 = new CrystalMining.DataRecord(entity.getId(), vec3, aABB, aABB2, List.copyOf(list));
      Float floatObj = C4_MAP.get(c4);
      if (floatObj != null) {
         return floatObj;
      }

      float floatValue = m_1(vec3, aABB, entity, aABB2, list);
      C4_MAP.put(c4, floatValue);
      return floatValue;
   }

   public static void m_6(LivingEntity livingEntity, double[] doubleArray, int value3) {
      MobEffectInstance mobEffectInstance = livingEntity.getEffect(MobEffects.RESISTANCE);
      livingEntity.getArmorValue();
      livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
      getProtectionLevel(livingEntity);
      if (mobEffectInstance != null) {
         mobEffectInstance.getAmplifier();
      }

      livingEntity.getHealth();
      livingEntity.getAbsorptionAmount();
      if (livingEntity.isAlive()) {
      }
   }

   public static void m_7(Vec3 vec3, LivingEntity livingEntity, AABB aABB, @Nullable AABB aABB2, List<BlockPos> blockPosList, double[] doubleArray, int value3) {
      Vec3 vec32 = new Vec3((aABB.minX + aABB.maxX) * 0.5, aABB.minY, (aABB.minZ + aABB.maxZ) * 0.5);
      double doubleValue = vec32.distanceTo(vec3);
      boolean flag = doubleValue <= 12.0 && livingEntity.isAlive();
      if (flag) {
         m_8(vec3, aABB, livingEntity, aABB2, blockPosList);
      }

      if (flag) {
         m_5(livingEntity, vec3.subtract(vec32), doubleArray, value3 + 2);
      }
   }

   private static float m_1(Vec3 vec3, AABB aABB, Entity entity, @Nullable AABB aABB2, List<BlockPos> blockPosList) {
      double doubleValue = 1.0 / ((aABB.maxX - aABB.minX) * 2.0 + 1.0);
      double doubleValue2 = 1.0 / ((aABB.maxY - aABB.minY) * 2.0 + 1.0);
      double doubleValue3 = 1.0 / ((aABB.maxZ - aABB.minZ) * 2.0 + 1.0);
      if (!(doubleValue < 0.0) && !(doubleValue2 < 0.0) && !(doubleValue3 < 0.0)) {
         double doubleValue4 = (1.0 - Math.floor(1.0 / doubleValue) * doubleValue) * 0.5;
         double doubleValue5 = (1.0 - Math.floor(1.0 / doubleValue3) * doubleValue3) * 0.5;
         int value3 = 0;
         int value4 = 0;

         for (double doubleValue6 = 0.0; doubleValue6 <= 1.0; doubleValue6 += doubleValue) {
            for (double doubleValue7 = 0.0; doubleValue7 <= 1.0; doubleValue7 += doubleValue2) {
               for (double doubleValue8 = 0.0; doubleValue8 <= 1.0; doubleValue8 += doubleValue3) {
                  Vec3 vec32 = new Vec3(
                     Mth.lerp(doubleValue6, aABB.minX, aABB.maxX) + doubleValue4, Mth.lerp(doubleValue7, aABB.minY, aABB.maxY), Mth.lerp(doubleValue8, aABB.minZ, aABB.maxZ) + doubleValue5
                  );
                  if (m_7(vec32, vec3, entity, aABB2, blockPosList)) {
                     value3++;
                  }

                  value4++;
               }
            }
         }

         return value4 == 0 ? 0.0F : (float)value3 / value4;
      } else {
         return 0.0F;
      }
   }

   private static boolean m_7(Vec3 vec3, Vec3 vec32, Entity entity, @Nullable AABB aABB, List<BlockPos> blockPosList) {
      if (aABB != null && aABB.clip(vec3, vec32).isPresent()) {
         return false;
      }

      Vec3 vec33 = vec3;
      int value3 = blockPosList == null ? 0 : blockPosList.size();
      int value4 = 0;

      while (value4 <= value3) {
         BlockHitResult blockHitResult = MINECRAFT.level.clip(new ClipContext(vec33, vec32, Block.COLLIDER, Fluid.NONE, entity));
         if (blockHitResult.getType() == Type.MISS) {
            return true;
         }

         if (blockPosList != null && blockPosList.contains(blockHitResult.getBlockPos())) {
            Vec3 vec34 = vec32.subtract(vec3).normalize();
            Vec3 vec35 = m_8(blockHitResult.getLocation(), vec34, blockHitResult.getBlockPos());
            if (vec35 != null && !(vec32.subtract(vec35).dot(vec34) <= 0.0)) {
               vec33 = vec35;
               value4++;
               continue;
            }

            return true;
         }

         return false;
      }

      return false;
   }

   private static Vec3 m_8(Vec3 vec3, Vec3 vec32, BlockPos blockPos) {
      AABB aABB = new AABB(blockPos);
      double doubleValue = Double.POSITIVE_INFINITY;
      double doubleValue2 = 1.0E-8;
      if (vec32.x > doubleValue2) {
         doubleValue = Math.min(doubleValue, (aABB.maxX - vec3.x) / vec32.x);
      } else if (vec32.x < -doubleValue2) {
         doubleValue = Math.min(doubleValue, (aABB.minX - vec3.x) / vec32.x);
      }

      if (vec32.y > doubleValue2) {
         doubleValue = Math.min(doubleValue, (aABB.maxY - vec3.y) / vec32.y);
      } else if (vec32.y < -doubleValue2) {
         doubleValue = Math.min(doubleValue, (aABB.minY - vec3.y) / vec32.y);
      }

      if (vec32.z > doubleValue2) {
         doubleValue = Math.min(doubleValue, (aABB.maxZ - vec3.z) / vec32.z);
      } else if (vec32.z < -doubleValue2) {
         doubleValue = Math.min(doubleValue, (aABB.minZ - vec3.z) / vec32.z);
      }

      return Double.isFinite(doubleValue) && doubleValue >= 0.0 ? vec3.add(vec32.scale(doubleValue + 1.0E-4)) : vec3.add(vec32.scale(1.0E-4));
   }

   private static int getProtectionLevel(LivingEntity livingEntity) {
      if (MINECRAFT.level == null) {
         return 0;
      }

      var registry = MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
      Reference reference = registry.getOrThrow(Enchantments.PROTECTION);
      Reference reference2 = registry.getOrThrow(Enchantments.BLAST_PROTECTION);
      int value3 = 0;

      for (EquipmentSlot equipmentSlot : EQUIPMENT_SLOT_ARRAY) {
         ItemStack itemStack = livingEntity.getItemBySlot(equipmentSlot);
         if (!itemStack.isEmpty()) {
            value3 += EnchantmentHelper.getItemEnchantmentLevel(reference, itemStack);
            value3 += EnchantmentHelper.getItemEnchantmentLevel(reference2, itemStack) * 2;
         }
      }

      return value3;
   }

   private static int getEfficiency(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty() && MINECRAFT.level != null) {
         var registry = MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
         return EnchantmentHelper.getItemEnchantmentLevel(registry.getOrThrow(Enchantments.EFFICIENCY), itemStack);
      } else {
         return 0;
      }
   }

   private static float m_0_float(ItemStack itemStack, BlockState blockState) {
      return itemStack != null && !itemStack.isEmpty() ? m_6_float(2.0, itemStack.getDestroySpeed(blockState), getEfficiency(itemStack)) : 1.0F;
   }

   private static boolean m_5(ItemStack itemStack, ItemStack itemStack2, float floatValue, float floatValue2, BlockState blockState) {
      return m_6_float(
            3.0,
            floatValue,
            floatValue2,
            canHarvest(itemStack, blockState) ? 1.0 : 0.0,
            canHarvest(itemStack2, blockState) ? 1.0 : 0.0,
            getEfficiency(itemStack),
            getEfficiency(itemStack2),
            isShearsWithoutEfficiency(itemStack) ? 1.0 : 0.0,
            isShearsWithoutEfficiency(itemStack2) ? 1.0 : 0.0
         )
         != 0.0F;
   }

   private static boolean isShearsWithoutEfficiency(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() && itemStack.is(Items.SHEARS) && getEfficiency(itemStack) == 0;
   }

   private static boolean canHarvest(ItemStack itemStack, BlockState blockState) {
      return !blockState.requiresCorrectToolForDrops() || itemStack != null && !itemStack.isEmpty() && itemStack.isCorrectToolForDrops(blockState);
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

   private record DataRecord(int f_6_o, Vec3 f_7_h, AABB f_1g, @Nullable AABB f_1j, List<BlockPos> f_3t) {
      public int m_6_o_int() {
         return this.f_6_o;
      }

      public Vec3 m_7_h() {
         return this.f_7_h;
      }

      public AABB m_1g() {
         return this.f_1g;
      }

      @Nullable
      public AABB m_1j() {
         return this.f_1j;
      }

      public List<BlockPos> m_3t() {
         return this.f_3t;
      }
   }
}
