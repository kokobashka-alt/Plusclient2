package baritone.utils;

import baritone.api.utils.input.Input;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;

public class PlayerMovementInput extends ClientInput {
   private final InputOverrideHandler handler;

   PlayerMovementInput(InputOverrideHandler var1) {
      this.handler = var1;
   }

   @Override
   public void tick() {
      float var1 = 0.0F;
      float var2 = 0.0F;
      boolean var3 = this.handler.isInputForcedDown(Input.JUMP);
      boolean var4 = this.handler.isInputForcedDown(Input.MOVE_FORWARD);
      if (var4) {
         var2++;
      }

      boolean var5 = this.handler.isInputForcedDown(Input.MOVE_BACK);
      if (var5) {
         var2--;
      }

      boolean var6 = this.handler.isInputForcedDown(Input.MOVE_LEFT);
      if (var6) {
         var1++;
      }

      boolean var7 = this.handler.isInputForcedDown(Input.MOVE_RIGHT);
      if (var7) {
         var1--;
      }

      boolean var8 = this.handler.isInputForcedDown(Input.SNEAK);
      if (var8) {
         var1 = (float)(var1 * 0.3);
         var2 = (float)(var2 * 0.3);
      }

      this.moveVector = new Vec2(var1, var2);
      boolean var9 = this.handler.isInputForcedDown(Input.SPRINT);
      this.keyPresses = new net.minecraft.world.entity.player.Input(var4, var5, var6, var7, var3, var8, var9);
   }
}
