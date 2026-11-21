package baritone.utils;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.event.events.TickEvent;
import baritone.api.utils.IInputOverrideHandler;
import baritone.api.utils.input.Input;
import baritone.behavior.Behavior;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.player.KeyboardInput;
import wtf.onetap.util.ChatUtil;

public class InputOverrideHandler extends Behavior implements IInputOverrideHandler {
   private final Map<Input, Boolean> inputForceStateMap = new HashMap<>();
   private final BlockBreakHelper blockBreakHelper;
   private final BlockPlaceHelper blockPlaceHelper;

   public InputOverrideHandler(Baritone var1) {
      super(var1);
      this.blockBreakHelper = new BlockBreakHelper(var1.getPlayerContext());
      this.blockPlaceHelper = new BlockPlaceHelper(var1.getPlayerContext());
   }

   @Override
   public final boolean isInputForcedDown(Input var1) {
      if (ChatUtil.isUnHooked()) {
         return false;
      } else {
         return var1 == null ? false : this.inputForceStateMap.getOrDefault(var1, false);
      }
   }

   @Override
   public final void setInputForceState(Input var1, boolean var2) {
      if (!var2 || !ChatUtil.isUnHooked()) {
         this.inputForceStateMap.put(var1, var2);
      }
   }

   @Override
   public final void clearAllKeys() {
      this.inputForceStateMap.clear();
   }

   @Override
   public final void onTick(TickEvent var1) {
      if (var1.getType() != TickEvent.Type.OUT) {
         if (this.isInputForcedDown(Input.CLICK_LEFT)) {
            this.setInputForceState(Input.CLICK_RIGHT, false);
         }

         this.blockBreakHelper.tick(this.isInputForcedDown(Input.CLICK_LEFT));
         this.blockPlaceHelper.tick(this.isInputForcedDown(Input.CLICK_RIGHT));
         if (this.inControl()) {
            if (this.ctx.player().input.getClass() != PlayerMovementInput.class) {
               this.ctx.player().input = new PlayerMovementInput(this);
            }
         } else if (this.ctx.player().input.getClass() == PlayerMovementInput.class) {
            this.ctx.player().input = new KeyboardInput(this.ctx.minecraft().options);
         }
      }
   }

   private boolean inControl() {
      for (Input var4 : new Input[]{Input.MOVE_FORWARD, Input.MOVE_BACK, Input.MOVE_LEFT, Input.MOVE_RIGHT, Input.SNEAK, Input.JUMP}) {
         if (this.isInputForcedDown(var4)) {
            return true;
         }
      }

      return this.baritone.getPathingBehavior().isPathing() || this.baritone != BaritoneAPI.getProvider().getPrimaryBaritone();
   }

   public BlockBreakHelper getBlockBreakHelper() {
      return this.blockBreakHelper;
   }
}
