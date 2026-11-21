package wtf.onetap.module.render;

import net.minecraft.util.Mth;
import wtf.onetap.animation.Interpolator;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.KeyPressEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.gui.ChatInputHelper;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.KeybindSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.C5_w;
import wtf.onetap.util.C6w;
import wtf.onetap.util.KeyNames;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Zoom", description = "Приближает картинку по клавише", category = Category.RENDER)
public class Zoom extends Module {
   private static final double DOUBLE_VALUE = Mth.lerp(Double.longBitsToDouble(4604480259023595110L), 1.0, Double.longBitsToDouble(4591870180066957722L));
   private final ModeSetting bindMode = new ModeSetting("Режим бинда", "Зажимать", "Нажимать", "Зажимать").build();
   private final KeybindSetting button = new KeybindSetting("Кнопка", 67).build();
   private final FloatSetting zoom = new FloatSetting("Приближение", 4.0F, 2.0F, 10.0F, 1.0F).build();
   private final BooleanSetting scrollZoom = new BooleanSetting("Зум колёсиком", true).build();
   private final C6w c6w = new C6w(
      new Interpolator.StateHolder4(() -> C5_w.EASE_OUT_CUBIC, () -> C5_w.EASE_OUT_CUBIC, () -> 0.25, () -> 0.2),
      new Interpolator.StateHolder3(() -> DOUBLE_VALUE),
      this.zoom::getInt,
      () -> 150,
      () -> 10
   );
   private boolean flag;
   private int value;
   private double doubleValue = 1.0;

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public float m_1_float(float floatValue) {
      if (!this.isEnabled()) {
         return 1.0F;
      }

      if (!this.flag) {
         this.value = 0;
         this.c6w.m_4_void();
      }

      this.doubleValue = this.c6w.m_7_double(floatValue);
      return (float)this.doubleValue;
   }

   public boolean m_1_boolean(double doubleValue2) {
      if (this.isEnabled() && this.flag && (Boolean)this.scrollZoom.getValue() && doubleValue2 != 0.0) {
         this.value = Mth.clamp(doubleValue2 > 0.0 ? this.value + 1 : this.value - 1, 0, 10);
         return true;
      } else {
         return false;
      }
   }

   @EventTarget
   private void onKeyPress(KeyPressEvent event) {
      if (event.getAction() == 1 && !ChatInputHelper.isTextInputOpen() && MinecraftAccess.MINECRAFT.gui.screen() == null) {
         int value2 = (Integer)this.button.getValue();
         if (((String)this.bindMode.getValue()).equals("Нажимать") && value2 != -1 && event.getKey() == value2) {
            this.flag = !this.flag;
         }
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (((String)this.bindMode.getValue()).equals("Зажимать")) {
         this.flag = this.isKeyPressed((Integer)this.button.getValue());
      }

      this.value = Mth.clamp(this.value, 0, 10);
      this.c6w.m_7(this.flag, this.value);
   }

   private boolean isKeyPressed(int value2) {
      return MinecraftAccess.MINECRAFT.gui.screen() == null && KeyNames.m_0_boolean(value2);
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.value = 0;
      this.doubleValue = 1.0;
      this.c6w.reset();
   }
}
