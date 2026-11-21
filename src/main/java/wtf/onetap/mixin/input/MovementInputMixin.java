package wtf.onetap.mixin.input;

import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.onetap.Onetap;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.gui.ClickGuiScreen;
import wtf.onetap.module.render.ClickGUI;
import wtf.onetap.util.InventoryUtil;

@Mixin(KeyboardInput.class)
public abstract class MovementInputMixin extends ClientInput {
   @Inject(method = "tick", at = @At("TAIL"))
   private void onetap$onTick(CallbackInfo var1) {
      if (InventoryUtil.m_7_boolean()) {
         this.moveVector = Vec2.ZERO;
         this.keyPresses = Input.EMPTY;
      } else if (ClickGUI.isActive() && ClickGuiScreen.isEditing()) {
         this.moveVector = Vec2.ZERO;
         this.keyPresses = new Input(false, false, false, false, false, false, this.keyPresses.sprint());
      } else {
         MovementInputEvent var2 = new MovementInputEvent(this.moveVector.y, this.moveVector.x, this.keyPresses.jump(), this.keyPresses.shift(), 0.3);
         Onetap.EVENT_BUS.unregister(var2);
         if (var2.isCancelled()) {
            this.moveVector = Vec2.ZERO;
            this.keyPresses = new Input(false, false, false, false, false, false, this.keyPresses.sprint());
         } else if (var2.isModified()) {
            this.moveVector = new Vec2(var2.getStrafe(), var2.getForward());
            this.keyPresses = new Input(
               var2.getForward() > 0.0F,
               var2.getForward() < 0.0F,
               var2.getStrafe() > 0.0F,
               var2.getStrafe() < 0.0F,
               var2.isJumping(),
               var2.isSneaking(),
               this.keyPresses.sprint()
            );
         }
      }
   }
}
