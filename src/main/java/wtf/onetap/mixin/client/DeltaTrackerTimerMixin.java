package wtf.onetap.mixin.client;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.DeltaTracker.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wtf.onetap.render.GuiScale;

@Mixin(Timer.class)
public class DeltaTrackerTimerMixin {
   @Redirect(method = "advanceGameTime(J)I", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/floats/FloatUnaryOperator;apply(F)F"))
   private float onetap$timerModify(FloatUnaryOperator var1, float var2) {
      float var3 = var1.apply(var2);
      return GuiScale.getScale() != 1.0F ? var3 / GuiScale.getScale() : var3;
   }
}
