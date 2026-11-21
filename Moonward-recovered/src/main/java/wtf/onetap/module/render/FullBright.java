package wtf.onetap.module.render;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "FullBright", description = "Повышает яркость в игре", category = Category.RENDER)
public class FullBright extends Module {
   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false));
      }
   }

   @Override
   public void onDisable() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.removeEffect(MobEffects.NIGHT_VISION);
      }

      super.onDisable();
   }
}
