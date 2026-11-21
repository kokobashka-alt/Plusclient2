package baritone.utils;

import baritone.Baritone;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ToolSet {
   private final Map<Block, Double> breakStrengthCache;
   private final Function<Block, Double> backendCalculation;
   private final LocalPlayer player;
   private static final List<TagKey<Item>> materialTagsPriorityList = List.of(
      ItemTags.WOODEN_TOOL_MATERIALS,
      ItemTags.STONE_TOOL_MATERIALS,
      ItemTags.IRON_TOOL_MATERIALS,
      ItemTags.GOLD_TOOL_MATERIALS,
      ItemTags.DIAMOND_TOOL_MATERIALS,
      ItemTags.NETHERITE_TOOL_MATERIALS
   );

   public ToolSet(LocalPlayer var1) {
      this.breakStrengthCache = new HashMap<>();
      this.player = var1;
      if (Baritone.settings().considerPotionEffects.value) {
         double var2 = this.potionAmplifier();
         Function<Double, Double> var4 = var2x -> var2 * var2x;
         this.backendCalculation = var4.compose(this::getBestDestructionTime);
      } else {
         this.backendCalculation = this::getBestDestructionTime;
      }
   }

   public double getStrVsBlock(BlockState var1) {
      return this.breakStrengthCache.computeIfAbsent(var1.getBlock(), this.backendCalculation);
   }

   private int getMaterialCost(ItemStack var1) {
      for (int var2 = 0; var2 < materialTagsPriorityList.size(); var2++) {
         var var3 = materialTagsPriorityList.get(var2);
         if (var1.is(var3)) {
            return var2;
         }
      }

      return -1;
   }

   public boolean hasSilkTouch(ItemStack var1) {
      ItemEnchantments var2 = var1.getEnchantments();

      for (Holder var4 : var2.keySet()) {
         if (var4.is(Enchantments.SILK_TOUCH) && var2.getLevel(var4) > 0) {
            return true;
         }
      }

      return false;
   }

   public int getBestSlot(Block var1, boolean var2) {
      return this.getBestSlot(var1, var2, false);
   }

   public int getBestSlot(Block var1, boolean var2, boolean var3) {
      if (!Baritone.settings().autoTool.value && var3) {
         return this.player.getInventory().getSelectedSlot();
      }

      int var4 = 0;
      double var5 = Double.NEGATIVE_INFINITY;
      int var7 = Integer.MIN_VALUE;
      boolean var8 = false;
      BlockState var9 = var1.defaultBlockState();

      for (int var10 = 0; var10 < 9; var10++) {
         ItemStack var11 = this.player.getInventory().getItem(var10);
         if ((Baritone.settings().useSwordToMine.value || !var11.is(ItemTags.SWORDS))
            && (
               !Baritone.settings().itemSaver.value
                  || var11.getDamageValue() + Baritone.settings().itemSaverThreshold.value < var11.getMaxDamage()
                  || var11.getMaxDamage() <= 1
            )) {
            double var12 = calculateSpeedVsBlock(var11, var9);
            boolean var14 = this.hasSilkTouch(var11);
            if (var12 > var5) {
               var5 = var12;
               var4 = var10;
               var7 = this.getMaterialCost(var11);
               var8 = var14;
            } else if (var12 == var5) {
               int var15 = this.getMaterialCost(var11);
               if (var15 < var7 && (var14 || !var8) || var2 && !var8 && var14) {
                  var5 = var12;
                  var4 = var10;
                  var7 = var15;
                  var8 = var14;
               }
            }
         }
      }

      return var4;
   }

   private double getBestDestructionTime(Block var1) {
      ItemStack var2 = this.player.getInventory().getItem(this.getBestSlot(var1, false, true));
      return calculateSpeedVsBlock(var2, var1.defaultBlockState()) * this.avoidanceMultiplier(var1);
   }

   private double avoidanceMultiplier(Block var1) {
      return Baritone.settings().blocksToAvoidBreaking.value.contains(var1) ? Baritone.settings().avoidBreakingMultiplier.value : 1.0;
   }

   public static double calculateSpeedVsBlock(ItemStack var0, BlockState var1) {
      float var2;
      try {
         var2 = var1.getDestroySpeed(null, null);
      } catch (NullPointerException var10) {
         return -1.0;
      }

      if (var2 < 0.0F) {
         return -1.0;
      }

      float var3 = var0.getDestroySpeed(var1);
      if (var3 > 1.0F) {
         ItemEnchantments var4 = var0.getEnchantments();

         label43:
         for (Holder var6 : var4.keySet()) {
            for (EnchantmentAttributeEffect var9 : ((Enchantment)var6.value()).getEffects(EnchantmentEffectComponents.ATTRIBUTES)) {
               if (var9.attribute().is(Attributes.MINING_EFFICIENCY.unwrapKey().get())) {
                  var3 += var9.amount().calculate(var4.getLevel(var6));
                  break label43;
               }
            }
         }
      }

      var3 /= var2;
      return var1.requiresCorrectToolForDrops() && (var0.isEmpty() || !var0.isCorrectToolForDrops(var1)) ? var3 / 100.0F : var3 / 30.0F;
   }

   private double potionAmplifier() {
      double var1 = 1.0;
      if (this.player.hasEffect(MobEffects.HASTE)) {
         var1 *= 1.0 + (this.player.getEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2;
      }

      if (this.player.hasEffect(MobEffects.MINING_FATIGUE)) {
         switch (this.player.getEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
            case 0:
               var1 *= 0.3;
               break;
            case 1:
               var1 *= 0.09;
               break;
            case 2:
               var1 *= 0.0027;
               break;
            default:
               var1 *= 8.1E-4;
         }
      }

      return var1;
   }
}
