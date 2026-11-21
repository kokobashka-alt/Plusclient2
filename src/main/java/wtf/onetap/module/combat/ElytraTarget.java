package wtf.onetap.module.combat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.AimUtil;
import wtf.onetap.combat.AttackCooldownTracker;
import wtf.onetap.combat.ElytraMode;
import wtf.onetap.combat.ElytraPrediction;
import wtf.onetap.combat.EntityPrediction;
import wtf.onetap.combat.TargetTracker;
import wtf.onetap.event.ConnectedEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.ElytraTargetRenderer;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PredictionUtil;

@ModuleInformation(name = "ElytraTarget", description = "Прицеливание по цели на элитрах", category = Category.COMBAT)
public class ElytraTarget extends Module {
   public static Vec3 vec3;
   private static Vec3 vec32;
   private final FloatSetting elytraRotation = new FloatSetting("Элитра ротация", 15.0F, 0.0F, 50.0F, 1.0F).build();
   private final BooleanSetting predict = new BooleanSetting("Предикт", false).build();
   private final FloatSetting predictValue = new FloatSetting("Значение предикта", 1.0, 0.0, 5.0, 0.5).visibleWhen(() -> (Boolean)this.predict.getValue() && !this.isPingPredict()).build();
   private final ModeSetting predictMode = new ModeSetting("Режим предикта", "Физика", "Физика", "Умный", "Адаптивный", "Ping").visibleWhen(this.predict::getValue).build();
   private final BooleanSetting multipoint = new BooleanSetting("Мультипоинт", false).build();
   private final BooleanSetting renderPredict = new BooleanSetting("Рендер предикта", false).visibleWhen(this.predict::getValue).build();
   private final ModeSetting renderMode = new ModeSetting("Режим рендера", "1", "1", "2", "Фантом").visibleWhen(() -> (Boolean)this.renderPredict.getValue() && !this.isPingPredict()).build();
   private final ModeSetting elytraCorrection = new ModeSetting("Коррекция на элитрах", "Полная", "Полная", "Быстрая", "Граунд Грим").build();
   private final BooleanSetting onlyWithFirework = new BooleanSetting("Только при фейерверке", true).visibleWhen(() -> ElytraMode.isFast((String)this.elytraCorrection.getValue())).build();
   private final BooleanSetting slowdown = new BooleanSetting("Замедление", true).build();
   private final BooleanSetting turnAround = new BooleanSetting("Разворот", false).build();
   private final ElytraPrediction elytraPrediction = new ElytraPrediction();
   private final ElytraTargetRenderer elytraTargetRenderer = new ElytraTargetRenderer();
   private final TargetTracker targetTracker = new TargetTracker();
   private ElytraPrediction.DataRecord c4;
   private static int[] valueArray;

   public static ElytraTarget getActive() {
      ElytraTarget elytraTarget = Onetap.MODULE_MANAGER.get(ElytraTarget.class);
      return elytraTarget != null && elytraTarget.isEnabled() ? elytraTarget : null;
   }

   public boolean m_0_boolean() {
      return (Boolean)this.multipoint.getValue() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.isFallFlying() && !this.m_8_boolean();
   }

   private boolean m_8_boolean() {
      return (Boolean)this.predict.getValue() && this.isAdaptivePredict() && vec3 != null && this.c4 != null;
   }

   public static void reset() {
      vec32 = null;
      vec3 = null;
   }

   public static AABB getBoxAt(LivingEntity livingEntity, Vec3 vec33) {
      double doubleValue = livingEntity.getBbWidth() / 2.0;
      return new AABB(vec33.x - doubleValue, vec33.y, vec33.z - doubleValue, vec33.x + doubleValue, vec33.y + livingEntity.getBbHeight(), vec33.z + doubleValue);
   }

   public float getElytraRotation() {
      return (Float)this.elytraRotation.getValue();
   }

   public boolean isTurnAround() {
      return (Boolean)this.turnAround.getValue();
   }

   public boolean m_4_boolean() {
      Integer integer = InventoryUtil.getProtocolVersion();
      return integer == null || integer > 754;
   }

   public boolean m_3_boolean(LivingEntity livingEntity) {
      if (vec3 != null && AttackCooldownTracker.m_7_boolean()) {
         Vec3 vec33 = vec3.subtract(livingEntity.position());
         double doubleValue = vec33.lengthSqr();
         return doubleValue < 1.0E-4 || MinecraftAccess.MINECRAFT.player.position().subtract(livingEntity.position()).dot(vec33) >= doubleValue * 0.3;
      } else {
         return true;
      }
   }

   public Vec3 m_1_Vec3(LivingEntity livingEntity) {
      if ((Boolean)this.predict.getValue() && this.bothFlying(livingEntity)) {
         vec32 = vec3;
         Vec3 vec33;
         if (this.isAdaptivePredict()) {
            this.c4 = this.elytraPrediction.m_2(livingEntity, this.predictValue.getDouble());
            vec3 = this.c4.m_1_g();
            vec33 = this.c4.m_1_m();
         } else {
            vec3 = this.m_2_Vec3(livingEntity);
            vec33 = this.multipoint.getValue() && vec3 != null ? this.clampY(vec3, livingEntity.getBbHeight(), MinecraftAccess.MINECRAFT.player.getEyeY()) : vec3;
         }

         return vec33 != null && this.m_4_boolean() ? getAimPoint(vec33, livingEntity.getBoundingBox()) : vec33;
      } else {
         reset();
         return this.multipoint.getValue() ? this.clampY(livingEntity.position(), livingEntity.getBbHeight(), MinecraftAccess.MINECRAFT.player.getEyeY()) : null;
      }
   }

   private Vec3 m_2_Vec3(LivingEntity livingEntity) {
      if (this.isPingPredict()) {
         return this.targetTracker.updatePosition(livingEntity);
      } else {
         return "Умный".equals(this.predictMode.getValue()) ? PredictionUtil.m_3(livingEntity, this.predictValue.getDouble()) : PredictionUtil.predictPosition(livingEntity, this.predictValue.getInt());
      }
   }

   private boolean isAdaptivePredict() {
      return "Адаптивный".equals(this.predictMode.getValue());
   }

   private boolean isPingPredict() {
      return "Ping".equals(this.predictMode.getValue());
   }

   private boolean bothFlying(LivingEntity livingEntity) {
      return MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.isFallFlying() && livingEntity.isFallFlying();
   }

   public String m_9_String() {
      boolean flag = MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.isFallFlying();
      return ElytraMode.m_2((String)this.elytraCorrection.getValue(), flag, (Boolean)this.onlyWithFirework.getValue(), AttackCooldownTracker.m_7_boolean());
   }

   public float applySlowdown(float floatValue, LivingEntity livingEntity) {
      return !this.slowdown.getValue() ? floatValue : Math.max(0.0F, floatValue * this.m_3_float(livingEntity));
   }

   public Vec3 m_6_Vec3(LivingEntity livingEntity) {
      if (!(Boolean)this.slowdown.getValue()) {
         return null;
      } else {
         LivingEntity livingEntity2 = KillAura.livingEntity;
         if (livingEntity != null && livingEntity2 != null && livingEntity2.isFallFlying()) {
            return vec3 != null ? vec3 : (livingEntity2 instanceof Player player ? EntityPrediction.m_2(player, 1) : null);
         } else {
            return null;
         }
      }
   }

   private float m_3_float(LivingEntity livingEntity) {
      Vec3 vec33 = this.m_6_Vec3(livingEntity);
      if (vec33 == null) {
         return 1.0F;
      }

      double doubleValue = livingEntity.distanceToSqr(vec33);
      return doubleValue >= 1.75 ? 1.0F : (float)Math.sqrt(doubleValue / 1.75);
   }

   public void m_0_void(float floatValue) {
      LivingEntity livingEntity = KillAura.livingEntity;
      if (MinecraftAccess.MINECRAFT.player != null && livingEntity != null && (Boolean)this.multipoint.getValue() && MinecraftAccess.MINECRAFT.player.isFallFlying()) {
         if (!(Boolean)this.predict.getValue() || !this.isAdaptivePredict() || vec3 == null || this.c4 == null) {
            Vec3 vec33 = livingEntity.getPosition(floatValue);
            Vec3 vec34 = vec3 != null ? vec3 : vec33;
            AABB aABB = livingEntity.getBoundingBox().move(vec33.subtract(livingEntity.position()));
            boolean flag = vec3 != null && this.m_4_boolean();
            AABB aABB2 = vec3 != null && !flag ? getBoxAt(livingEntity, vec34) : aABB;
            Vec3 vec35 = MinecraftAccess.MINECRAFT.player.getEyePosition(floatValue);
            Vec3 vec36 = this.clampY(vec34, livingEntity.getBbHeight(), vec35.y);
            Onetap.ROTATION_MODES.updateTarget(flag ? getAimPoint(vec36, aABB) : vec36, aABB2);
         }
      }
   }

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender3D(Render3DEvent event) {
      LivingEntity livingEntity = KillAura.livingEntity;
      boolean flag = MinecraftAccess.MINECRAFT.player != null
         && livingEntity != null
         && livingEntity.isAlive()
         && (Boolean)this.predict.getValue()
         && (Boolean)this.renderPredict.getValue()
         && this.bothFlying(livingEntity);
      if (this.isPingPredict()) {
         if (flag && vec3 != null) {
            this.m_5(event, livingEntity, vec3);
         }
      } else {
         Vec3 vec33 = flag ? this.m_1_Vec3(event.getFloatValue()) : null;
         if (((String)this.renderMode.getValue()).equals("Фантом")) {
            this.elytraTargetRenderer.m_1(event, livingEntity, vec33);
         } else if (vec33 != null) {
            if (((String)this.renderMode.getValue()).equals("2")) {
               Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
               Line3DRenderer.drawLines(matrix4f, boxToLines(getBoxAt(livingEntity, vec33).inflate(0.002)), event.getVec3(), 1.0F, ColorUtil.rgba(255, 255, 255, 255));
            } else {
               this.m_1(event, livingEntity, vec33);
            }
         }
      }
   }

   private Vec3 m_1_Vec3(float floatValue) {
      if (vec3 == null) {
         return null;
      } else {
         return vec32 == null ? vec3 : new Vec3(Mth.lerp(floatValue, vec32.x, vec3.x), Mth.lerp(floatValue, vec32.y, vec3.y), Mth.lerp(floatValue, vec32.z, vec3.z));
      }
   }

   private void m_1(Render3DEvent event, LivingEntity livingEntity, Vec3 vec33) {
      float floatValue = event.getFloatValue();
      Vec3 vec34 = event.getVec3();
      Vec3 vec35 = new Vec3(
         Mth.lerp(floatValue, livingEntity.xo, livingEntity.getX()), Mth.lerp(floatValue, livingEntity.yo, livingEntity.getY()) + livingEntity.getBbHeight() * 0.92, Mth.lerp(floatValue, livingEntity.zo, livingEntity.getZ())
      );
      Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
      Line3DRenderer.drawLines(matrix4f, List.of(vec35, vec33), vec34, 1.0F, ColorUtil.rgba(205, 205, 205, 220));
      Vec3 vec36 = MinecraftAccess.MINECRAFT.player.getEyePosition(floatValue);
      Line3DRenderer.drawLines(matrix4f, List.of(vec36, vec33), vec34, 1.0F, ColorUtil.rgba(90, 170, 255, 200));
      float floatValue2 = (float)(vec33.x - vec34.x);
      float floatValue3 = (float)(vec33.y - vec34.y);
      float floatValue4 = (float)(vec33.z - vec34.z);
      Box3DRenderer.drawBox(matrix4f, floatValue2 - 0.21F, floatValue3 - 0.21F, floatValue4 - 0.21F, floatValue2 + 0.21F, floatValue3 + 0.21F, floatValue4 + 0.21F, ColorUtil.rgba(115, 115, 115, 120));
      Box3DRenderer.drawBox(matrix4f, floatValue2 - 0.08F, floatValue3 - 0.08F, floatValue4 - 0.08F, floatValue2 + 0.08F, floatValue3 + 0.08F, floatValue4 + 0.08F, ColorUtil.rgba(215, 215, 215, 205));
      int value = ColorUtil.rgba(235, 235, 235, 255);
      Line3DRenderer.drawLines(matrix4f, boxAround(vec33, 0.21), vec34, 1.0F, value);
      Line3DRenderer.drawLines(matrix4f, boxAround(vec33, 0.08), vec34, 1.0F, value);
   }

   private void m_5(Render3DEvent event, LivingEntity livingEntity, Vec3 vec33) {
      Vec3 vec34 = event.getVec3();
      Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
      Vec3 vec35 = MinecraftAccess.MINECRAFT.player.getEyePosition(event.getFloatValue());
      Line3DRenderer.drawLines(matrix4f, List.of(vec35, vec33), vec34, 1.0F, ColorUtil.rgba(0, 255, 255, 255));
      if (livingEntity.isFallFlying()) {
         Vec3 vec36 = livingEntity.position().add(0.0, livingEntity.getBbHeight() * 0.5, 0.0);
         Line3DRenderer.drawLines(matrix4f, List.of(vec36, vec33), vec34, 1.0F, ColorUtil.rgba(255, 0, 255, 255));
      }

      double doubleValue = 0.18;
      Vec3 vec37 = vec33.add(-doubleValue, -doubleValue * 0.5, -doubleValue);
      Vec3 vec38 = vec33.add(doubleValue, -doubleValue * 0.5, -doubleValue);
      Vec3 vec39 = vec33.add(doubleValue, -doubleValue * 0.5, doubleValue);
      Vec3 vec310 = vec33.add(-doubleValue, -doubleValue * 0.5, doubleValue);
      Line3DRenderer.drawLines(matrix4f, List.of(vec37, vec38, vec38, vec39, vec39, vec310, vec310, vec37), vec34, 1.0F, ColorUtil.rgba(255, 255, 0, 255));
   }

   @EventTarget(priority = EventPriority.LOWEST)
   private void onConnected(ConnectedEvent event) {
      this.targetTracker.m_5_void();
   }

   private static List<Vec3> boxAround(Vec3 vec33, double doubleValue) {
      return boxToLines(new AABB(vec33.x - doubleValue, vec33.y - doubleValue, vec33.z - doubleValue, vec33.x + doubleValue, vec33.y + doubleValue, vec33.z + doubleValue));
   }

   public static List<Vec3> boxToLines(AABB aABB) {
      ArrayList list = new ArrayList(24);
      addLine(list, aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.minY, aABB.minZ);
      addLine(list, aABB.maxX, aABB.minY, aABB.minZ, aABB.maxX, aABB.minY, aABB.maxZ);
      addLine(list, aABB.maxX, aABB.minY, aABB.maxZ, aABB.minX, aABB.minY, aABB.maxZ);
      addLine(list, aABB.minX, aABB.minY, aABB.maxZ, aABB.minX, aABB.minY, aABB.minZ);
      addLine(list, aABB.minX, aABB.maxY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.minZ);
      addLine(list, aABB.maxX, aABB.maxY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ);
      addLine(list, aABB.maxX, aABB.maxY, aABB.maxZ, aABB.minX, aABB.maxY, aABB.maxZ);
      addLine(list, aABB.minX, aABB.maxY, aABB.maxZ, aABB.minX, aABB.maxY, aABB.minZ);
      addLine(list, aABB.minX, aABB.minY, aABB.minZ, aABB.minX, aABB.maxY, aABB.minZ);
      addLine(list, aABB.maxX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.minZ);
      addLine(list, aABB.maxX, aABB.minY, aABB.maxZ, aABB.maxX, aABB.maxY, aABB.maxZ);
      addLine(list, aABB.minX, aABB.minY, aABB.maxZ, aABB.minX, aABB.maxY, aABB.maxZ);
      return list;
   }

   private static void addLine(List<Vec3> vec3List, double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5, double doubleValue6) {
      vec3List.add(new Vec3(doubleValue, doubleValue2, doubleValue3));
      vec3List.add(new Vec3(doubleValue4, doubleValue5, doubleValue6));
   }

   @Override
   public void onDisable() {
      reset();
      this.elytraPrediction.m_6_void();
      this.c4 = null;
      this.elytraTargetRenderer.reset();
      this.targetTracker.reset();
   }

   private static Vec3 getAimPoint(Vec3 vec33, AABB aABB) {
      return AimUtil.closestPoint(vec33, aABB.deflate(0.1));
   }

   private Vec3 clampY(Vec3 vec33, double doubleValue, double doubleValue2) {
      return vec33.add(0.0, Mth.clamp(doubleValue2 - vec33.y, 0.0, doubleValue), 0.0);
   }

   public static void m_3(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_3_IArr() {
      return valueArray;
   }

   static {
      m_3((int[])null);
   }
}
