package wtf.onetap.module.player;

import java.util.Comparator;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "AutoPilot", description = "Наводится на нужные предметы", category = Category.PLAYER)
public class AutoPilot extends Module {
   public ItemEntity itemEntity;
   private final ModeSetting mode = new ModeSetting("Режим", "Камера", "Камера", "Ротация").build();
   private final MultiModeSetting aimsAt = new MultiModeSetting("Наводится на", "Шар", "Шар", "Элитра", "Осколок", "Острота VI", "Анти полёт", "Аура").build();

   public AutoPilot() {
      ((Set)this.aimsAt.getValue()).add("Элитра");
      ((Set)this.aimsAt.getValue()).add("Осколок");
      ((Set)this.aimsAt.getValue()).add("Аура");
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.itemEntity = this.m_4_ItemEntity();
         if (this.itemEntity != null) {
            Onetap.ROTATION_MODES.rotateByModeNoBox(this.itemEntity.getBoundingBox().getCenter(), "Vanilla", ((String)this.mode.getValue()).equals("Камера"), 12);
         }
      }
   }

   private ItemEntity m_4_ItemEntity() {
      return MinecraftAccess.MINECRAFT
         .level
         .getEntitiesOfClass(ItemEntity.class, MinecraftAccess.MINECRAFT.player.getBoundingBox().inflate(50.0), itemEntity2 -> itemEntity2.isAlive() && this.m_3_boolean(itemEntity2.getItem()))
         .stream()
         .min(Comparator.comparingDouble(itemEntity3 -> MinecraftAccess.MINECRAFT.player.distanceToSqr(itemEntity3)))
         .orElse(null);
   }

   private boolean m_3_boolean(ItemStack itemStack) {
      VoiceChat.m_9_StringArr();
      String text = itemStack.getHoverName().getString();
      if (this.aimsAt.isSelected("Шар") && itemStack.is(Items.PLAYER_HEAD)) {
         return true;
      } else if (this.aimsAt.isSelected("Элитра") && itemStack.is(Items.ELYTRA)) {
         return true;
      } else if (this.aimsAt.isSelected("Осколок") && itemStack.is(Items.GHAST_TEAR) && text.contains("Осколок")) {
         return true;
      } else if (this.aimsAt.isSelected("Острота VI") && this.m_1_boolean(itemStack)) {
         return true;
      } else {
         return this.aimsAt.isSelected("Анти полёт") && itemStack.is(Items.FIREWORK_STAR) && text.contains("Анти Полёт")
            ? true
            : this.aimsAt.isSelected("Аура") && this.m_3(itemStack, text);
      }
   }

   private boolean m_1_boolean(ItemStack itemStack) {
      if (!itemStack.is(ItemTags.SWORDS) && !itemStack.is(ItemTags.AXES)) {
         return false;
      }

      if (MinecraftAccess.MINECRAFT.level == null) {
         return false;
      }

      var registry = MinecraftAccess.MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
      var reference = registry.get(Enchantments.SHARPNESS).orElse(null);
      return reference != null && EnchantmentHelper.getItemEnchantmentLevel(reference, itemStack) >= 6;
   }

   private boolean m_3(ItemStack itemStack, String text) {
      return itemStack.is(Items.SUNFLOWER) && text.contains("Аура Охотника")
         || itemStack.is(Items.CLAY_BALL) && text.contains("Аура Твёрдости Брони")
         || itemStack.is(Items.POPPED_CHORUS_FRUIT) && text.contains("Аура Телепортации")
         || itemStack.is(Items.GOLD_NUGGET) && text.contains("Аура Богача")
         || itemStack.is(Items.DYE.white()) && text.contains("Аура Защиты От Падения")
         || itemStack.is(Items.GHAST_TEAR) && text.contains("Аура Защиты От Кристаллов");
   }

   @Override
   public void onDisable() {
      this.itemEntity = null;
   }
}
