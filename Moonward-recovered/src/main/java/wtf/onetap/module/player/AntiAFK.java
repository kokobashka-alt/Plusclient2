package wtf.onetap.module.player;

import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AntiAFK", description = "Периодически ходит и прыгает, чтобы не кикнуло за АФК", category = Category.PLAYER)
public class AntiAFK extends Module {
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private final TimeHelper timeHelper3 = new TimeHelper();
   private boolean flag;
   private boolean flag2;

   @Override
   public void onEnable() {
      this.timeHelper.reset();
      this.flag = false;
      this.flag2 = false;
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.flag2 = false;
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (MinecraftAccess.MINECRAFT.gui == null || MinecraftAccess.MINECRAFT.gui.screen() == null) {
            if (!(Math.abs(event.getForward()) > 1.0E-5F) && !(Math.abs(event.getStrafe()) > 1.0E-5F) && !event.isJumping() && !event.isSneaking()) {
               if (this.flag2) {
                  if (this.timeHelper3.hasElapsed(100L)) {
                     this.flag2 = false;
                  } else {
                     event.setJumping(true);
                  }
               }

               if (this.flag) {
                  if (this.timeHelper2.hasElapsed(2000L)) {
                     this.flag = false;
                     this.timeHelper.reset();
                  } else {
                     event.setForward(1.0F);
                  }
               } else if (this.timeHelper.hasElapsed(5000L)) {
                  this.flag2 = true;
                  this.flag = true;
                  this.timeHelper3.reset();
                  this.timeHelper2.reset();
                  event.setJumping(true);
                  event.setForward(1.0F);
               }
            } else {
               this.timeHelper.reset();
               this.flag = false;
               this.flag2 = false;
            }
         }
      }
   }
}
