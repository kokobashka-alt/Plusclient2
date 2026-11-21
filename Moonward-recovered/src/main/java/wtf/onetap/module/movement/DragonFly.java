package wtf.onetap.module.movement;

import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "DragonFly", description = "Управляемый полет", category = Category.MOVEMENT)
public class DragonFly extends Module {
   private final ModeSetting mode = new ModeSetting("Режим", "RW", "RW", "SpookyTime", "Custom").build();
   private final FloatSetting speedXZ = new FloatSetting("Скорость XZ", 1.0F, 0.1F, 5.0F, 0.1F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Custom")).build();
   private final FloatSetting speedY = new FloatSetting("Скорость Y", 0.25F, 0.1F, 2.0F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("Custom")).build();

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (MinecraftAccess.MINECRAFT.player.getAbilities().flying) {
            String text = (String)this.mode.getValue();
            double doubleValue;
            double doubleValue2;
            double doubleValue3;
            if (text.equals("RW")) {
               doubleValue = 1.0;
               doubleValue2 = 0.9;
               doubleValue3 = 1.2;
            } else if (text.equals("SpookyTime")) {
               doubleValue = 1.0;
               doubleValue2 = 0.2;
               doubleValue3 = 0.2;
            } else {
               doubleValue = this.speedXZ.getFloat();
               doubleValue2 = this.speedY.getFloat();
               doubleValue3 = this.speedY.getFloat() * 2.0;
            }

            float floatValue = 0.0F;
            float floatValue2 = 0.0F;
            if (MinecraftAccess.MINECRAFT.player.input.keyPresses.forward()) {
               floatValue++;
            }

            if (MinecraftAccess.MINECRAFT.player.input.keyPresses.backward()) {
               floatValue--;
            }

            if (MinecraftAccess.MINECRAFT.player.input.keyPresses.left()) {
               floatValue2++;
            }

            if (MinecraftAccess.MINECRAFT.player.input.keyPresses.right()) {
               floatValue2--;
            }

            float floatValue3 = MinecraftAccess.MINECRAFT.player.getYRot();
            if (floatValue != 0.0F) {
               if (floatValue2 > 0.0F) {
                  floatValue3 += floatValue > 0.0F ? -45.0F : 45.0F;
               } else if (floatValue2 < 0.0F) {
                  floatValue3 += floatValue > 0.0F ? 45.0F : -45.0F;
               }

               floatValue2 = 0.0F;
               floatValue = floatValue > 0.0F ? 1.0F : -1.0F;
            }

            double doubleValue4 = 0.0;
            double doubleValue5 = 0.0;
            if (floatValue != 0.0F || floatValue2 != 0.0F) {
               double doubleValue6 = Math.sin(Math.toRadians(floatValue3 + 90.0F));
               double doubleValue7 = Math.cos(Math.toRadians(floatValue3 + 90.0F));
               doubleValue4 = floatValue * doubleValue * doubleValue7 + floatValue2 * doubleValue * doubleValue6;
               doubleValue5 = floatValue * doubleValue * doubleValue6 - floatValue2 * doubleValue * doubleValue7;
            }

            MinecraftAccess.MINECRAFT.player.setDeltaMovement(doubleValue4, 0.0, doubleValue5);
            boolean flag = floatValue == 0.0F && floatValue2 == 0.0F;
            boolean flag2 = MinecraftAccess.MINECRAFT.options.keyLeft.isDown();
            boolean flag3 = MinecraftAccess.MINECRAFT.options.keyRight.isDown();
            if (MinecraftAccess.MINECRAFT.options.keyJump.isDown()) {
               MinecraftAccess.MINECRAFT.player.setDeltaMovement(MinecraftAccess.MINECRAFT.player.getDeltaMovement().x, doubleValue2, MinecraftAccess.MINECRAFT.player.getDeltaMovement().z);
               if (flag && !flag2 && !flag3) {
                  MinecraftAccess.MINECRAFT.player.setDeltaMovement(MinecraftAccess.MINECRAFT.player.getDeltaMovement().x, doubleValue3, MinecraftAccess.MINECRAFT.player.getDeltaMovement().z);
               }
            }

            if (MinecraftAccess.MINECRAFT.options.keyShift.isDown()) {
               MinecraftAccess.MINECRAFT.player.setDeltaMovement(MinecraftAccess.MINECRAFT.player.getDeltaMovement().x, -doubleValue2, MinecraftAccess.MINECRAFT.player.getDeltaMovement().z);
               if (flag && !flag2 && !flag3) {
                  MinecraftAccess.MINECRAFT.player.setDeltaMovement(MinecraftAccess.MINECRAFT.player.getDeltaMovement().x, -doubleValue3, MinecraftAccess.MINECRAFT.player.getDeltaMovement().z);
               }
            }
         }
      }
   }
}
