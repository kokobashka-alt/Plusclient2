package wtf.onetap.module.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "CreamyKeys", description = "Механические звуки нажатий клавиатуры", category = Category.MISC)
public class CreamyKeys extends Module {
   private final ModeSetting keyboard = new ModeSetting("Клавиатура", "Красные", "Красные", "Синие", "Коричневые", "Чёрные", "Кремовые", "Альпака", "Бирюзовые").build();
   private final FloatSetting volume = new FloatSetting("Громкость", 100.0F, 0.0F, 100.0F, 1.0F).build();
   private final FloatSetting amplification = new FloatSetting("Усиление", 1.0F, 1.0F, 8.0F, 1.0F).build();

   @EventTarget(priority = EventPriority.HIGHEST)
   private void onKeyPress(KeyPressEvent event) {
      if (event.getAction() == 1 && event.getKey() >= 0) {
         this.m_1_void();
      }
   }

   private void m_1_void() {
      Identifier identifier = Identifier.fromNamespaceAndPath("onetap", switch ((String)this.keyboard.getValue()) {
         case "Синие" -> "keyboards.cherrymx_blue_abs";
         case "Коричневые" -> "keyboards.cherrymx_brown_pbt";
         case "Красные" -> "keyboards.cherrymx_red_abs";
         case "Кремовые" -> "keyboards.nk_cream";
         case "Альпака" -> "keyboards.alpaca";
         case "Бирюзовые" -> "keyboards.tealios_v2";
         default -> "keyboards.cherrymx_black_pbt";
      });
      long longValue = ThreadLocalRandom.current().nextLong();

      for (int i = 0; i < this.amplification.getInt(); i++) {
         MinecraftAccess.MINECRAFT
            .getSoundManager()
            .play(
               new SimpleSoundInstance(
                  identifier, SoundSource.MASTER, this.volume.getFloat() / 100.0F, 1.0F, RandomSource.create(longValue), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true
               )
            );
      }
   }
}
