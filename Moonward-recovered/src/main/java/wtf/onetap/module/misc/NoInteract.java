package wtf.onetap.module.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.UseItemOnEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.combat.KillAura;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "No Interact", description = "Блокирует случайное взаимодействие с контейнерами и блоками", category = Category.MISC)
public class NoInteract extends Module {
   private final BooleanSetting considerAura = new BooleanSetting("Учитывать включённую Aura", true).build();

   @EventTarget
   private void onUseItemOn(UseItemOnEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if ((Boolean)this.considerAura.getValue()) {
            KillAura killAura = Onetap.MODULE_MANAGER.get(KillAura.class);
            if (killAura == null || !killAura.isEnabled()) {
               return;
            }
         }

         BlockPos blockPos = event.getBlockHitResult().getBlockPos();
         Block block = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos).getBlock();
         if (block == Blocks.CHEST
            || block == Blocks.TRAPPED_CHEST
            || block == Blocks.FURNACE
            || block == Blocks.ANVIL
            || block == Blocks.CRAFTING_TABLE
            || block == Blocks.HOPPER
            || block == Blocks.JUKEBOX
            || block == Blocks.NOTE_BLOCK
            || block == Blocks.ENDER_CHEST
            || block == Blocks.DISPENSER
            || block == Blocks.DROPPER
            || block instanceof ShulkerBoxBlock
            || block instanceof FenceBlock
            || block instanceof FenceGateBlock
            || block instanceof TrapDoorBlock) {
            event.cancel();
         }
      }
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
