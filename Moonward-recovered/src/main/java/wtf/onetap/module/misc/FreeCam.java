package wtf.onetap.module.misc;

import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "FreeCam", description = "Предоставляет инструменты для свободной камеры", category = Category.MISC)
public class FreeCam extends Module {
   private final FloatSetting speed = new FloatSetting("Скорость", 2.0F, 0.5F, 5.0F, 0.1F).build();
   private final BooleanSetting cancelPacket = new BooleanSetting("Отменять пакет", false).build();
   private Vec3 vec3 = Vec3.ZERO;
   private Vec3 vec32 = Vec3.ZERO;

   public Vec3 getInterpolatedPos(float floatValue) {
      return this.vec32.lerp(this.vec3, floatValue);
   }

   public Vec3 getVec3() {
      return this.vec3;
   }

   @Override
   public void onEnable() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameRenderer != null) {
         Camera camera = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera();
         this.vec32 = this.vec3 = camera != null ? camera.position() : MinecraftAccess.MINECRAFT.player.getEyePosition();
         MinecraftAccess.MINECRAFT.options.setCameraType(CameraType.FIRST_PERSON);
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      this.vec32 = this.vec3 = Vec3.ZERO;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         MinecraftAccess.MINECRAFT.options.setCameraType(CameraType.FIRST_PERSON);
         if ((Boolean)this.cancelPacket.getValue()) {
            MinecraftAccess.MINECRAFT.player.setDeltaMovement(Vec3.ZERO);
         }

         this.m_7_void();
      }
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if ((Boolean)this.cancelPacket.getValue() && event.getPacket() instanceof ServerboundMovePlayerPacket) {
         event.cancel();
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof ClientboundRespawnPacket || event.getPacket() instanceof ClientboundLoginPacket) {
         this.setEnabled(false);
      }
   }

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         Vec3 vec33 = event.getVec3();
         AABB aABB = this.m_0_AABB(event.getFloatValue()).inflate(0.001);
         Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         int value = ColorUtil.primaryColor();
         Box3DRenderer.drawBox(
            matrix4f,
            (float)(aABB.minX - vec33.x),
            (float)(aABB.minY - vec33.y),
            (float)(aABB.minZ - vec33.z),
            (float)(aABB.maxX - vec33.x),
            (float)(aABB.maxY - vec33.y),
            (float)(aABB.maxZ - vec33.z),
            ColorUtil.withAlpha(value, 26)
         );
         this.m_7(matrix4f, vec33, aABB, value);
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      float floatValue = event.getForward();
      float floatValue2 = event.getStrafe();
      float floatValue3 = this.speed.getFloat();
      double[] doubleArray = this.m_9_DArr(floatValue, floatValue2, floatValue3);
      double doubleValue = event.isJumping() ? floatValue3 : (event.isSneaking() ? -floatValue3 : 0.0);
      this.vec32 = this.vec3;
      this.vec3 = this.vec3.add(doubleArray[0], doubleValue, doubleArray[1]);
      event.cancel();
   }

   private AABB m_0_AABB(float floatValue) {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      double doubleValue = Mth.lerp(floatValue, localPlayer.xo, localPlayer.getX());
      double doubleValue2 = Mth.lerp(floatValue, localPlayer.yo, localPlayer.getY());
      double doubleValue3 = Mth.lerp(floatValue, localPlayer.zo, localPlayer.getZ());
      return localPlayer.getBoundingBox().move(doubleValue - localPlayer.getX(), doubleValue2 - localPlayer.getY(), doubleValue3 - localPlayer.getZ());
   }

   private void m_7(Matrix4f matrix4f, Vec3 vec33, AABB aABB, int value) {
      Vec3 vec34 = new Vec3(aABB.minX, aABB.minY, aABB.minZ);
      Vec3 vec35 = new Vec3(aABB.maxX, aABB.minY, aABB.minZ);
      Vec3 vec36 = new Vec3(aABB.maxX, aABB.minY, aABB.maxZ);
      Vec3 vec37 = new Vec3(aABB.minX, aABB.minY, aABB.maxZ);
      Vec3 vec38 = new Vec3(aABB.minX, aABB.maxY, aABB.minZ);
      Vec3 vec39 = new Vec3(aABB.maxX, aABB.maxY, aABB.minZ);
      Vec3 vec310 = new Vec3(aABB.maxX, aABB.maxY, aABB.maxZ);
      Vec3 vec311 = new Vec3(aABB.minX, aABB.maxY, aABB.maxZ);
      int value2 = ColorUtil.withAlpha(value, 210);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec34, vec35, vec36, vec37, vec34), vec33, 1.0F, value2);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec38, vec39, vec310, vec311, vec38), vec33, 1.0F, value2);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec34, vec38), vec33, 1.0F, value2);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec35, vec39), vec33, 1.0F, value2);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec36, vec310), vec33, 1.0F, value2);
      Line3DRenderer.drawLineStrip(matrix4f, List.of(vec37, vec311), vec33, 1.0F, value2);
   }

   public void m_7_void() {
      Vec3 vec33 = this.vec3;
      Vec3 vec34 = vec33.add(Vec3.directionFromRotation(MinecraftAccess.MINECRAFT.player.getXRot(), MinecraftAccess.MINECRAFT.player.getYRot()).scale(4.5));
      BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec33, vec34, Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player));
      double doubleValue = blockHitResult.getType() == Type.MISS ? 4.5 : blockHitResult.getLocation().distanceTo(vec33);
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
         MinecraftAccess.MINECRAFT.level,
         MinecraftAccess.MINECRAFT.player,
         vec33,
         vec34,
         new AABB(vec33, vec34).inflate(1.0),
         entity -> entity != MinecraftAccess.MINECRAFT.player && !entity.isSpectator() && entity.isPickable(),
         0.0F
      );
      if (entityHitResult != null && entityHitResult.getLocation().distanceTo(vec33) <= doubleValue) {
         MinecraftAccess.MINECRAFT.hitResult = entityHitResult;
         MinecraftAccess.MINECRAFT.crosshairPickEntity = entityHitResult.getEntity();
      } else {
         MinecraftAccess.MINECRAFT.hitResult = blockHitResult;
         MinecraftAccess.MINECRAFT.crosshairPickEntity = null;
      }
   }

   private double[] m_9_DArr(float floatValue, float floatValue2, float floatValue3) {
      UnHook.m_7_IArr();
      float floatValue4 = MinecraftAccess.MINECRAFT.player.getYRot();
      if (floatValue != 0.0F) {
         if (floatValue2 > 0.0F) {
            floatValue4 += floatValue > 0.0F ? -45.0F : 45.0F;
         } else if (floatValue2 < 0.0F) {
            floatValue4 += floatValue > 0.0F ? 45.0F : -45.0F;
         }

         floatValue2 = 0.0F;
         floatValue = floatValue > 0.0F ? 1.0F : -1.0F;
      }

      if (floatValue == 0.0F && floatValue2 == 0.0F) {
         return new double[]{0.0, 0.0};
      }

      double doubleValue = Math.toRadians(floatValue4 + 90.0F);
      double doubleValue2 = Math.sin(doubleValue);
      double doubleValue3 = Math.cos(doubleValue);
      return new double[]{floatValue * floatValue3 * doubleValue3 + floatValue2 * floatValue3 * doubleValue2, floatValue * floatValue3 * doubleValue2 - floatValue2 * floatValue3 * doubleValue3};
   }
}
