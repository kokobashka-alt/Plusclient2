package wtf.onetap.module.misc;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "Nuker", description = "Автоматически разрушает блоки в радиусе досягаемости", category = Category.MISC)
public class Nuker extends Module {
   private final ModeSetting digMode = new ModeSetting("Режим копания", "Общий", "Шахта FT/SP", "Общий").build();
   private final FloatSetting digDistance = new FloatSetting("Дистанция копания", 4.0F, 1.0F, 6.0F, 0.5F).visibleWhen(() -> ((String)this.digMode.getValue()).equals("Общий")).build();
   private final FloatSetting digSpeed = new FloatSetting("Скорость копания", 1.0F, 1.0F, 5.0F, 1.0F).visibleWhen(() -> ((String)this.digMode.getValue()).equals("Общий")).build();
   private final BooleanSetting dontDigUnderSelf = new BooleanSetting("Не копать под себя", true).build();
   private BlockPos blockPos;
   private final Random random = new Random();
   private float floatValue = 20.0F;
   private float floatValue2 = 20.0F;
   private int value = 0;

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         this.blockPos = null;
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
         if (itemStack.isDamageableItem() && itemStack.getMaxDamage() - itemStack.getDamageValue() < 50) {
            ChatUtil.log("§cРабота прекращена во избежание поломки инструмента");
            this.setEnabled(false);
         } else {
            boolean flag = ((String)this.digMode.getValue()).equals("Шахта FT/SP");
            boolean flag2 = itemStack.is(ItemTags.PICKAXES);
            if (!flag || flag2) {
               float floatValue3 = flag ? 3.2F : this.digDistance.getFloat();
               int value2 = this.dontDigUnderSelf.getValue() ? MinecraftAccess.MINECRAFT.player.getBlockY() : Integer.MIN_VALUE;
               AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().inflate(floatValue3 + 1.0F);
               Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
               Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getLookAngle();
               double doubleValue = Double.MAX_VALUE;
               Direction direction = null;

               for (BlockPos blockPos2 : BlockPos.betweenClosed(
                  BlockPos.containing(aABB.minX, aABB.minY, aABB.minZ), BlockPos.containing(aABB.maxX, aABB.maxY, aABB.maxZ)
               )) {
                  if (blockPos2.getY() >= value2 && this.m_0(blockPos2, flag, flag2) && this.m_4_boolean(vec3, blockPos2)) {
                     Vec3 vec33 = Vec3.atCenterOf(blockPos2);
                     Vec3 vec34 = vec33.subtract(vec3);
                     double doubleValue2 = vec34.dot(vec32);
                     if (!(doubleValue2 <= 0.0)) {
                        Optional optional = new AABB(blockPos2).clip(vec3, vec33);
                        if (!optional.isEmpty()) {
                           Vec3 vec35 = (Vec3)optional.get();
                           if (!(vec3.distanceToSqr(vec35) > floatValue3 * floatValue3)) {
                              double doubleValue3 = vec34.subtract(vec32.scale(doubleValue2)).lengthSqr();
                              if (doubleValue3 < doubleValue) {
                                 doubleValue = doubleValue3;
                                 this.blockPos = new BlockPos(blockPos2);
                                 direction = Direction.getApproximateNearest(vec35.subtract(vec33));
                              }
                           }
                        }
                     }
                  }
               }

               if (this.blockPos != null) {
                  Rotations rotations = Onetap.ROTATION_MODES.getRotationTo(this.blockPos);
                  this.m_4(rotations, 180);
                  Rotations rotations2 = Onetap.ROTATION_MODES.toRotations();
                  if (rotations2.m_2(rotations) <= 20.0F) {
                     int value3 = Math.round(flag ? 2.8F : this.digSpeed.getFloat());

                     for (int i = 0; i < value3; i++) {
                        MinecraftAccess.MINECRAFT.gameMode.continueDestroyBlock(this.blockPos, direction);
                     }

                     MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
                  }
               }
            }
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (this.blockPos != null) {
         float floatValue3 = event.getForward();
         float floatValue4 = event.getStrafe();
         if (floatValue3 != 0.0F || floatValue4 != 0.0F) {
            float floatValue5 = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().yRot();
            if (MinecraftAccess.MINECRAFT.options.getCameraType().isMirrored()) {
               floatValue5 -= 180.0F;
            }

            float floatValue6 = floatValue5 - MinecraftAccess.MINECRAFT.player.getYRot();
            float floatValue7 = (float)Math.toDegrees(Math.atan2(floatValue3, floatValue4));
            float floatValue8 = Mth.wrapDegrees(floatValue7 + floatValue6);
            float floatValue9 = Math.round(floatValue8 / 45.0F) * 45.0F;
            double doubleValue = Math.toRadians(floatValue9);
            event.setForward(Math.round((float)Math.sin(doubleValue)));
            event.setStrafe(Math.round((float)Math.cos(doubleValue)));
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (this.blockPos != null) {
         Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         Vec3 vec3 = event.getVec3();
         AABB aABB = new AABB(this.blockPos);
         int value2 = ColorUtil.rgba(255, 0, 0, 255);
         Box3DRenderer.drawBox(
            matrix4f,
            (float)(aABB.minX - vec3.x),
            (float)(aABB.minY - vec3.y),
            (float)(aABB.minZ - vec3.z),
            (float)(aABB.maxX - vec3.x),
            (float)(aABB.maxY - vec3.y),
            (float)(aABB.maxZ - vec3.z),
            ColorUtil.withAlpha(value2, 40)
         );
         this.m_0(matrix4f, vec3, aABB, value2);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.blockPos = null;
   }

   private void m_4(Rotations rotations, int value2) {
      if (++this.value >= 20) {
         this.value = 0;
         this.floatValue2 = 15.0F + this.random.nextFloat() * 15.0F;
      }

      this.floatValue = this.floatValue + (this.floatValue2 - this.floatValue) * 0.2F;
      float floatValue3 = (float)Math.sin(MinecraftAccess.MINECRAFT.player.tickCount * 0.35F) * 2.5F;
      float floatValue4 = (float)Math.cos(MinecraftAccess.MINECRAFT.player.tickCount * 0.23F) * 1.8F;
      Rotations rotations2 = Onetap.ROTATION_MODES.toRotations();
      float floatValue5 = Mth.clamp(Mth.wrapDegrees(rotations.getFloatValue() + floatValue3 - rotations2.getFloatValue()), -this.floatValue, this.floatValue);
      Onetap.ROTATION_MODES.rotateWithSensitivity(new Rotations(rotations2.getFloatValue() + floatValue5, rotations.getFloatValue2() + floatValue4), false, value2);
   }

   private boolean m_0(BlockPos blockPos2, boolean flag, boolean flag2) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
      if (blockState.isAir()) {
         return false;
      } else if (!flag) {
         return true;
      } else if (!flag2) {
         return false;
      } else {
         return blockPos2.getX() >= -85 && blockPos2.getX() <= -66 && blockPos2.getY() >= 72 && blockPos2.getY() <= 82 && blockPos2.getZ() >= -4 && blockPos2.getZ() <= 16
            ? blockState.getDestroyProgress(MinecraftAccess.MINECRAFT.player, MinecraftAccess.MINECRAFT.level, blockPos2) >= 1.0F
            : false;
      }
   }

   private void m_0(Matrix4f matrix4f, Vec3 vec3, AABB aABB, int value2) {
      int value3 = ColorUtil.withAlpha(value2, 210);
      Vec3 vec32 = new Vec3(aABB.minX, aABB.minY, aABB.minZ);
      Vec3 vec33 = new Vec3(aABB.maxX, aABB.minY, aABB.minZ);
      Vec3 vec34 = new Vec3(aABB.maxX, aABB.minY, aABB.maxZ);
      Vec3 vec35 = new Vec3(aABB.minX, aABB.minY, aABB.maxZ);
      Vec3 vec36 = new Vec3(aABB.minX, aABB.maxY, aABB.minZ);
      Vec3 vec37 = new Vec3(aABB.maxX, aABB.maxY, aABB.minZ);
      Vec3 vec38 = new Vec3(aABB.maxX, aABB.maxY, aABB.maxZ);
      Vec3 vec39 = new Vec3(aABB.minX, aABB.maxY, aABB.maxZ);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec32, vec33, vec34, vec35, vec32), vec3, 1.5F, value3);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec36, vec37, vec38, vec39, vec36), vec3, 1.5F, value3);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec32, vec36), vec3, 1.5F, value3);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec33, vec37), vec3, 1.5F, value3);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec34, vec38), vec3, 1.5F, value3);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec35, vec39), vec3, 1.5F, value3);
   }

   private boolean m_4_boolean(Vec3 vec3, BlockPos blockPos2) {
      AABB aABB = new AABB(blockPos2).deflate(0.05);
      Vec3[] vec3Array = new Vec3[9];
      vec3Array[0] = aABB.getCenter();

      for (int i = 0; i < 8; i++) {
         vec3Array[i + 1] = new Vec3((i & 1) == 0 ? aABB.minX : aABB.maxX, (i & 2) == 0 ? aABB.minY : aABB.maxY, (i & 4) == 0 ? aABB.minZ : aABB.maxZ);
      }

      for (Vec3 vec32 : vec3Array) {
         BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec3, vec32, Block.COLLIDER, Fluid.NONE, MinecraftAccess.MINECRAFT.player));
         if (blockHitResult.getBlockPos().equals(blockPos2)) {
            return true;
         }
      }

      return false;
   }
}
