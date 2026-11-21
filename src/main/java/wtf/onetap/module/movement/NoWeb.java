package wtf.onetap.module.movement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.CobwebEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.player.MultiPlayerGameModeAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "NoWeb", description = "Ослабляет замедление от паутины", category = Category.MOVEMENT)
public class NoWeb extends Module {
   private final ModeSetting mode = new ModeSetting("Мод", "Grim", "Grim", "GrimGround", "RW", "New").build();
   private final FloatSetting strength = new FloatSetting("Сила", 0.8F, 0.3F, 1.05F, 0.05F).visibleWhen(() -> ((String)this.mode.getValue()).equals("New")).build();
   public final BooleanSetting breakThroughWeb = new BooleanSetting("Ломать через паутину", false).build();
   public final BooleanSetting placeThroughWeb = new BooleanSetting("Ставить через паутину", false).visibleWhen(this.breakThroughWeb::getValue).build();

   @EventTarget
   private void onTick(TickEvent event) {
      if (((String)this.mode.getValue()).equals("New") && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && this.m_9_boolean()) {
         this.m_7_void();
      }
   }

   @EventTarget
   private void onCobweb(CobwebEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && !((String)this.mode.getValue()).equals("New")) {
         if (((String)this.mode.getValue()).equals("RW")) {
            ((MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode)
               .onetap$startPrediction(
                  MinecraftAccess.MINECRAFT.level, value -> new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, event.getBlockPos(), Direction.UP, value)
               );
            event.cancel();
         } else {
            Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
            float floatValue = MinecraftAccess.MINECRAFT.player.getYRot();
            double doubleValue = 0.0;
            double doubleValue2 = 0.0;
            if (MinecraftAccess.MINECRAFT.player.input.keyPresses.forward()) {
               doubleValue++;
            }

            if (MinecraftAccess.MINECRAFT.player.input.keyPresses.backward()) {
               doubleValue--;
            }

            if (MinecraftAccess.MINECRAFT.player.input.keyPresses.left()) {
               doubleValue2++;
            }

            if (MinecraftAccess.MINECRAFT.player.input.keyPresses.right()) {
               doubleValue2--;
            }

            if (doubleValue != 0.0 || doubleValue2 != 0.0) {
               if (doubleValue != 0.0) {
                  if (doubleValue2 > 0.0) {
                     floatValue += doubleValue > 0.0 ? -45 : 45;
                  } else if (doubleValue2 < 0.0) {
                     floatValue += doubleValue > 0.0 ? 45 : -45;
                  }

                  doubleValue2 = 0.0;
                  doubleValue = doubleValue > 0.0 ? 1.0 : -1.0;
               }

               double doubleValue3 = Math.toDegrees(Math.atan2(doubleValue2, doubleValue)) + floatValue;
               floatValue = (float)((doubleValue3 % 360.0 + 360.0) % 360.0);
            }

            float floatValue2 = 0.21F;
            if ((!(floatValue >= 313.0F) || !(floatValue <= 317.0F))
               && (!(floatValue >= 223.0F) || !(floatValue <= 227.0F))
               && (!(floatValue >= 133.0F) || !(floatValue <= 137.0F))
               && (!(floatValue >= 43.0F) || !(floatValue <= 47.0F))) {
               if ((!(floatValue >= 311.0F) || !(floatValue <= 319.0F))
                  && (!(floatValue >= 221.0F) || !(floatValue <= 229.0F))
                  && (!(floatValue >= 131.0F) || !(floatValue <= 139.0F))
                  && (!(floatValue >= 41.0F) || !(floatValue <= 49.0F))) {
                  if ((!(floatValue >= 310.8F) || !(floatValue <= 320.8F))
                     && (!(floatValue >= 220.8F) || !(floatValue <= 230.8F))
                     && (!(floatValue >= 130.8F) || !(floatValue <= 140.8F))
                     && (!(floatValue >= 40.8F) || !(floatValue <= 50.8F))) {
                     if ((!(floatValue >= 308.7F) || !(floatValue <= 322.7F))
                        && (!(floatValue >= 218.7F) || !(floatValue <= 232.7F))
                        && (!(floatValue >= 128.7F) || !(floatValue <= 142.7F))
                        && (!(floatValue >= 38.7F) || !(floatValue <= 52.7F))) {
                        if ((!(floatValue >= 306.5F) || !(floatValue <= 324.5F))
                           && (!(floatValue >= 216.5F) || !(floatValue <= 234.5F))
                           && (!(floatValue >= 126.5F) || !(floatValue <= 144.5F))
                           && (!(floatValue >= 36.5F) || !(floatValue <= 54.5F))) {
                           if (floatValue >= 304.0F && floatValue <= 327.0F
                              || floatValue >= 214.0F && floatValue <= 237.0F
                              || floatValue >= 124.0F && floatValue <= 147.0F
                              || floatValue >= 34.0F && floatValue <= 57.0F) {
                              floatValue2 = 0.268F;
                           }
                        } else {
                           floatValue2 = 0.281F;
                        }
                     } else {
                        floatValue2 = 0.289F;
                     }
                  } else {
                     floatValue2 = 0.298F;
                  }
               } else {
                  floatValue2 = 0.307F;
               }
            } else {
               floatValue2 = 0.31F;
            }

            boolean flag = ((String)this.mode.getValue()).equals("GrimGround");
            if (MinecraftAccess.MINECRAFT.options.keyJump.isDown()) {
               MinecraftAccess.MINECRAFT.player.setDeltaMovement(vec3.x, flag ? 0.001 : 1.219, vec3.z);
            } else if (MinecraftAccess.MINECRAFT.options.keyShift.isDown()) {
               MinecraftAccess.MINECRAFT.player.setDeltaMovement(vec3.x, flag ? -2.519 : (MinecraftAccess.MINECRAFT.player.isFallFlying() ? -1.5 : -3.5), vec3.z);
            } else {
               MinecraftAccess.MINECRAFT.player.setDeltaMovement(vec3.x, 0.0, vec3.z);
            }

            this.m_4_void(floatValue2);
         }
      }
   }

   private void m_4_void(double doubleValue) {
      float floatValue = MinecraftAccess.MINECRAFT.player.getYRot();
      double doubleValue2 = Math.sin(Math.toRadians(floatValue + 90.0F));
      double doubleValue3 = Math.cos(Math.toRadians(floatValue + 90.0F));
      double doubleValue4 = 0.0;
      double doubleValue5 = 0.0;
      if (MinecraftAccess.MINECRAFT.player.input.keyPresses.forward()) {
         doubleValue4++;
      }

      if (MinecraftAccess.MINECRAFT.player.input.keyPresses.backward()) {
         doubleValue4--;
      }

      if (MinecraftAccess.MINECRAFT.player.input.keyPresses.left()) {
         doubleValue5++;
      }

      if (MinecraftAccess.MINECRAFT.player.input.keyPresses.right()) {
         doubleValue5--;
      }

      if (doubleValue4 != 0.0 && doubleValue5 != 0.0) {
         if (doubleValue4 != 0.0) {
            if (doubleValue5 > 0.0) {
               floatValue += doubleValue4 > 0.0 ? -45 : 45;
            } else if (doubleValue5 < 0.0) {
               floatValue += doubleValue4 > 0.0 ? 45 : -45;
            }

            doubleValue5 = 0.0;
            doubleValue4 = doubleValue4 > 0.0 ? 1.0 : -1.0;
         }

         doubleValue2 = Math.sin(Math.toRadians(floatValue + 90.0F));
         doubleValue3 = Math.cos(Math.toRadians(floatValue + 90.0F));
      }

      double doubleValue6 = doubleValue4 * doubleValue * doubleValue3 + doubleValue5 * doubleValue * doubleValue2;
      double doubleValue7 = doubleValue4 * doubleValue * doubleValue2 - doubleValue5 * doubleValue * doubleValue3;
      MinecraftAccess.MINECRAFT.player.setDeltaMovement(doubleValue6, MinecraftAccess.MINECRAFT.player.getDeltaMovement().y, doubleValue7);
   }

   private void m_7_void() {
      if (!MinecraftAccess.MINECRAFT.player.isSpectator() && !MinecraftAccess.MINECRAFT.player.getAbilities().flying) {
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
         double doubleValue = vec3.x;
         double doubleValue2 = 0.0;
         double doubleValue3 = vec3.z;
         Vec2 vec2 = MinecraftAccess.MINECRAFT.player.input.getMoveVector();
         if (vec2.lengthSquared() >= 1.0E-6F) {
            Vec3 vec32 = this.m_3_Vec3(vec2.y, vec2.x);
            if (vec32 != Vec3.ZERO) {
               double doubleValue4 = this.strength.getFloat() * 0.15 / 0.25;
               doubleValue += vec32.x * doubleValue4;
               doubleValue3 += vec32.z * doubleValue4;
            }
         }

         if (MinecraftAccess.MINECRAFT.options.keyJump.isDown()) {
            doubleValue2 = 0.15;
         } else if (MinecraftAccess.MINECRAFT.options.keyShift.isDown()) {
            doubleValue2 = -2.0;
         }

         MinecraftAccess.MINECRAFT.player.setDeltaMovement(doubleValue, doubleValue2, doubleValue3);
      }
   }

   private Vec3 m_3_Vec3(float floatValue, float floatValue2) {
      float floatValue3 = Mth.clamp(floatValue, -1.0F, 1.0F);
      float floatValue4 = Mth.clamp(floatValue2, -1.0F, 1.0F);
      double doubleValue = Math.toRadians(MinecraftAccess.MINECRAFT.player.getYRot());
      double doubleValue2 = Math.sin(doubleValue);
      double doubleValue3 = Math.cos(doubleValue);
      double doubleValue4 = floatValue4 * doubleValue3 - floatValue3 * doubleValue2;
      double doubleValue5 = floatValue4 * doubleValue2 + floatValue3 * doubleValue3;
      double doubleValue6 = Math.sqrt(doubleValue4 * doubleValue4 + doubleValue5 * doubleValue5);
      return doubleValue6 < 1.0E-6 ? Vec3.ZERO : new Vec3(doubleValue4 / doubleValue6, 0.0, doubleValue5 / doubleValue6);
   }

   private boolean m_9_boolean() {
      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox();

      for (BlockPos blockPos : BlockPos.betweenClosed(
         (int)Math.floor(aABB.minX),
         (int)Math.floor(aABB.minY),
         (int)Math.floor(aABB.minZ),
         (int)Math.floor(aABB.maxX),
         (int)Math.floor(aABB.maxY),
         (int)Math.floor(aABB.maxZ)
      )) {
         if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos).is(Blocks.COBWEB)) {
            return true;
         }
      }

      return false;
   }
}
