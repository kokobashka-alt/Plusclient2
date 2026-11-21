package wtf.onetap.module.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.EntityPrediction;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.network.ServerTpsTracker;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.MultiModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "ProjectHelper", description = "Помогает в прицеливании с лука/арбалета/трезубца", category = Category.COMBAT)
public class ProjectHelper extends Module {
   private static final double[][] DOUBLE_VALUE_ARRAY = buildOffsets(new double[]{Double.longBitsToDouble(-4622134369562887455L), Double.longBitsToDouble(4601237667291888353L)}, true);
   private static final double[][] DOUBLE_VALUE_ARRAY2 = buildOffsets(m_6_DArr(4), false);
   private static final double[] DOUBLE_VALUE_ARRAY3 = new double[]{0.0, Double.longBitsToDouble(-4619342137793917747L), Double.longBitsToDouble(4604029899060858061L)};
   private static final double[] DOUBLE_VALUE_ARRAY4 = new double[]{
      0.0,
      Double.longBitsToDouble(4601778099247172813L),
      Double.longBitsToDouble(-4621593937607602995L),
      Double.longBitsToDouble(4605831338911806259L),
      Double.longBitsToDouble(-4617540697942969549L)
   };
   private static final double[] DOUBLE_VALUE_ARRAY5 = new double[]{
      Double.longBitsToDouble(4602678819172646912L), Double.longBitsToDouble(4587366580439587226L), Double.longBitsToDouble(4606732058837280358L)
   };
   private final MultiModeSetting weaponTypes = new MultiModeSetting("Типы оружия", "Лук", "Лук", "Арбалет", "Трезубец").build();
   private final FloatSetting distance = new FloatSetting("Дистанция", 30.0, 10.0, 50.0, 1.0).build();
   private final BooleanSetting predict = new BooleanSetting("Предикт", true).build();
   private final BooleanSetting showBox = new BooleanSetting("Показывать бокс", true).build();
   private final Map<Integer, ProjectHelper.StateHolder> integerMap = new HashMap<>();
   private LivingEntity livingEntity;
   private Rotations rotations;
   private Rotations rotations2;
   private boolean flag;
   private boolean flag2;
   private Vec3 vec3;
   private boolean flag3;
   private boolean flag4;
   private static boolean flag5;

   public ProjectHelper() {
      this.weaponTypes.setValue(new LinkedHashSet<>(Set.of("Лук", "Арбалет", "Трезубец")));
   }

   public boolean m_3_boolean() {
      return this.isEnabled() && this.flag && this.rotations != null;
   }

   public boolean m_9_boolean() {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null && this.livingEntity != null && this.m_8_boolean()) {
         return MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() instanceof CrossbowItem ? !this.flag3 : !this.flag4;
      } else {
         return false;
      }
   }

   @Override
   public void onEnable() {
      this.reset();
      this.integerMap.clear();
   }

   @Override
   public void onDisable() {
      this.reset();
      if (Onetap.ROTATION_MODES.isFlag()) {
         Onetap.ROTATION_MODES.applyAndFinish();
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.integerMap.keySet().removeIf(integer -> !(MinecraftAccess.MINECRAFT.level.getEntity(integer) instanceof Player));
         this.flag4 = this.flag3;
         this.flag3 = false;
         boolean flag6 = this.m_8_boolean();
         if (flag6) {
            this.flag = true;
            ProjectHelper.DataRecord4 c6 = this.m_6_C6();
            if (c6 != null) {
               this.livingEntity = c6.m_73_Player();
               this.rotations2 = c6.m_1_j_C9().m_2_r();
               this.vec3 = c6.m_3e().m_1_Vec3(c6.m_1_j_C9().m_7b() + c6.m_3e().m_7_n_double());
               this.m_3_void();
               this.flag2 = this.m_0_boolean();
               this.m_1_void();
               this.flag3 = this.m_8(c6.m_3e(), c6.m_1_j_C9());
            } else {
               this.livingEntity = null;
               this.rotations = null;
               this.rotations2 = null;
               this.flag2 = false;
               this.vec3 = null;
               this.m_1_void();
            }
         } else {
            if (this.flag && this.rotations != null) {
               this.m_2_void();
               this.m_1_void();
            } else {
               this.reset();
            }

            this.flag = false;
         }
      } else {
         this.reset();
         this.integerMap.clear();
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         Packet packet = event.getPacket();
         switch (packet) {
            case ClientboundMoveEntityPacket clientboundMoveEntityPacket when clientboundMoveEntityPacket.hasPosition()
                  && clientboundMoveEntityPacket.getEntity(MinecraftAccess.MINECRAFT.level) instanceof Player player && player != MinecraftAccess.MINECRAFT.player:
               this.m_7(player, player.getPositionCodec().decode(clientboundMoveEntityPacket.getXa(), clientboundMoveEntityPacket.getYa(), clientboundMoveEntityPacket.getZa()), clientboundMoveEntityPacket.isOnGround());
               return;
            case ClientboundEntityPositionSyncPacket clientboundEntityPositionSyncPacket when MinecraftAccess.MINECRAFT.level.getEntity(clientboundEntityPositionSyncPacket.id()) instanceof Player player2
                  && player2 != MinecraftAccess.MINECRAFT.player:
               this.m_7(player2, clientboundEntityPositionSyncPacket.values().position(), clientboundEntityPositionSyncPacket.onGround());
               return;
            case null, default:
               return;
         }
      }
   }

   @EventTarget(priority = EventPriority.HIGH)
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.showBox.getValue() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.livingEntity != null && this.vec3 != null && this.m_8_boolean() && this.isValidTarget((Entity)this.livingEntity)) {
            float floatValue = 0.3F;
            float floatValue2 = this.livingEntity.getBbHeight();
            Vec3 vec32 = event.getVec3();
            Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
            Vec3 vec33 = new Vec3(this.vec3.x, this.livingEntity.position().y + floatValue2 * 0.5, this.vec3.z);
            Box3DRenderer.drawBox(
               matrix4f,
               (float)(vec33.x - vec32.x) - floatValue,
               (float)(vec33.y - vec32.y - floatValue2 * 0.5),
               (float)(vec33.z - vec32.z) - floatValue,
               (float)(vec33.x - vec32.x) + floatValue,
               (float)(vec33.y - vec32.y + floatValue2 * 0.5),
               (float)(vec33.z - vec32.z) + floatValue,
               ColorUtil.rgba(160, 160, 160, 136)
            );
         }
      }
   }

   private void m_1_void() {
      if (this.rotations != null) {
         Onetap.ROTATION_MODES.rotateWithSensitivity(this.rotations, false, 20);
      }
   }

   private void m_3_void() {
      if (this.rotations == null) {
         this.rotations = new Rotations(MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot());
      }

      float floatValue = this.m_3_float();
      float floatValue2 = Mth.wrapDegrees(this.rotations2.getFloatValue() - this.rotations.getFloatValue());
      float floatValue3 = Mth.wrapDegrees(this.rotations2.getFloatValue2() - this.rotations.getFloatValue2());
      float floatValue4 = Math.min(Math.abs(floatValue2), floatValue);
      float floatValue5 = Math.min(Math.abs(floatValue3), floatValue);
      float floatValue6 = this.rotations.getFloatValue() + (floatValue2 > 0.0F ? floatValue4 : -floatValue4);
      float floatValue7 = Mth.clamp(this.rotations.getFloatValue2() + (floatValue3 > 0.0F ? floatValue5 : -floatValue5), -89.0F, 89.0F);
      this.rotations = new Rotations(floatValue6, floatValue7);
   }

   private void m_2_void() {
      float floatValue = Onetap.ROTATION_MODES.getRenderYaw();
      float floatValue2 = Onetap.ROTATION_MODES.getRenderPitch();
      float floatValue3 = Mth.wrapDegrees(floatValue - this.rotations.getFloatValue());
      float floatValue4 = Mth.wrapDegrees(floatValue2 - this.rotations.getFloatValue2());
      float floatValue5 = this.m_3_float();
      if (Math.abs(floatValue3) < 1.0F && Math.abs(floatValue4) < 1.0F) {
         this.rotations = null;
      } else {
         float floatValue6 = Math.min(Math.abs(floatValue3), floatValue5);
         float floatValue7 = Math.min(Math.abs(floatValue4), floatValue5 / 3.0F);
         float floatValue8 = this.rotations.getFloatValue() + (floatValue3 > 0.0F ? floatValue6 : -floatValue6);
         float floatValue9 = Mth.clamp(this.rotations.getFloatValue2() + (floatValue4 > 0.0F ? floatValue7 : -floatValue7), -89.0F, 89.0F);
         this.rotations = new Rotations(floatValue8, floatValue9);
      }
   }

   private float m_3_float() {
      return 40.0F;
   }

   private ProjectHelper.DataRecord2 m_3_C3(Player player) {
      ProjectHelper.DataRecord c2 = this.getProjectileData();
      Vec3 vec32 = MinecraftAccess.MINECRAFT.player.position();
      Vec3 vec33 = Vec3.ZERO;
      if (c2.m_1b_boolean()) {
         Vec3 vec34 = MinecraftAccess.MINECRAFT.player.getDeltaMovement();
         boolean flag6 = MinecraftAccess.MINECRAFT.player.onGround() && vec34.y <= 0.0;
         Vec3 vec35 = new Vec3(MinecraftAccess.MINECRAFT.player.getX() - MinecraftAccess.MINECRAFT.player.xo, flag6 ? 0.0 : vec34.y, MinecraftAccess.MINECRAFT.player.getZ() - MinecraftAccess.MINECRAFT.player.zo);
         vec32 = vec32.add(vec35);
         vec33 = vec35;
      }

      Vec3 vec36 = vec32.add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight() - c2.m_0_f_double(), 0.0);
      ProjectHelper.StateHolder c4 = this.integerMap.computeIfAbsent(player.getId(), integer -> new ProjectHelper.StateHolder(EntityPrediction.getInterpolatedPosition(player), System.nanoTime(), player.onGround()));
      double doubleValue = c4.m_1_double();
      boolean flag7 = doubleValue > 3.5;
      double doubleValue2 = (c2.m_1b_boolean() ? 1.0 : 0.0) + (this.predict.getValue() ? this.getPingSeconds() + (flag7 ? 0.0 : doubleValue) : 0.0);
      return new ProjectHelper.DataRecord2(vec36, vec33, c2, this.m_8(player, c4, flag7), doubleValue2, player.getBbWidth(), player.getBbHeight());
   }

   private ProjectHelper.DataRecord4 m_6_C6() {
      if (this.rotations == null) {
         this.flag2 = false;
      }

      ArrayList<Player> playerList = new ArrayList<>();

      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (this.m_3_boolean(player)) {
            playerList.add(player);
         }
      }

      playerList.sort(Comparator.comparingDouble(this::m_9_double));
      if (this.flag2 && this.livingEntity instanceof Player player2 && playerList.remove(player2)) {
         playerList.addFirst(player2);
      }

      for (int i = 0; i < Math.min(3, playerList.size()); i++) {
         Player player3 = (Player)playerList.get(i);
         ProjectHelper.DataRecord2 c3 = this.m_3_C3(player3);
         ProjectHelper.DataRecord5 c9 = this.m_8(c3);
         if (c9 != null) {
            return new ProjectHelper.DataRecord4(player3, c3, c9);
         }
      }

      return null;
   }

   private ProjectHelper.DataRecord5 m_8(ProjectHelper.DataRecord2 c3) {
      Vec3 vec32 = c3.m_1_Vec3(c3.m_7_n_double()).subtract(c3.m_5y());
      Vec3 vec33 = new Vec3(-vec32.z, 0.0, vec32.x);
      vec33 = vec33.lengthSqr() < 1.0E-6 ? new Vec3(1.0, 0.0, 0.0) : vec33.normalize();
      ArrayList<Vec3> vec3List = new ArrayList<>();

      for (double doubleValue : DOUBLE_VALUE_ARRAY3) {
         for (double doubleValue2 : DOUBLE_VALUE_ARRAY4) {
            vec3List.add(vec33.scale(doubleValue * c3.m_7_d() * 0.5).add(0.0, doubleValue2 * c3.m_7h_double() * 0.5, 0.0));
         }
      }

      vec3List.sort(Comparator.comparingDouble(Vec3::lengthSqr));
      ProjectHelper.DataRecord5 c9 = null;
      int value = -1;

      for (Vec3 vec34 : vec3List) {
         ProjectHelper.DataRecord5 c92 = c3.m_5_C9(vec34);
         ProjectHelper.DataRecord3 c5 = this.m_9(c3, c92.m_2_r(), DOUBLE_VALUE_ARRAY, c92.m_7b(), true);
         if (c5.m_1d() > value) {
            c9 = c92;
            value = c5.m_1d();
         }

         if (c5.m_1d() == DOUBLE_VALUE_ARRAY.length) {
            break;
         }
      }

      return value > 0 ? c9 : null;
   }

   private boolean m_8(ProjectHelper.DataRecord2 c3, ProjectHelper.DataRecord5 c9) {
      ProjectHelper.DataRecord3 c5 = this.m_9(c3, Onetap.ROTATION_MODES.toRotations(), DOUBLE_VALUE_ARRAY2, c9.m_7b(), true);
      ProjectHelper.DataRecord3 c52 = this.m_9(c3, c9.m_2_r(), DOUBLE_VALUE_ARRAY2, c9.m_7b(), false);
      return c5.m_1o() > 0 && c5.m_1o() * 10 >= c52.m_1o() * 9 && c5.m_1d() == c5.m_1o();
   }

   private ProjectHelper.DataRecord3 m_9(ProjectHelper.DataRecord2 c3, Rotations rotations3, double[][] arrayArray, double doubleValue, boolean flag6) {
      ProjectHelper.DataRecord c2 = c3.m_3d();
      Vec3 vec32 = rotations3.toVector();
      int value = Math.min(120, (int)Math.ceil(doubleValue) + 4);
      int value2 = 0;
      int value3 = 0;

      for (double[] doubleArray : arrayArray) {
         Vec3 vec33 = vec32.add(doubleArray[0] * 0.0172275, doubleArray[1] * 0.0172275, doubleArray[2] * 0.0172275).scale(c2.m_0_u()).add(c3.m_5k());
         Vec3 vec34 = c3.m_5y();
         boolean flag7 = false;
         boolean flag8 = !flag6;
         boolean flag9 = false;

         for (int i = 0; i < value && (!flag7 || !flag8); i++) {
            Vec3 vec35 = vec34.add(vec33);
            AABB aABB = c3.m_2(i + 0.5 + c3.m_7_n_double());
            double doubleValue2 = Mth.clamp((i - 1) / 20.0, 0.0, 0.3);
            if (!flag7) {
               flag7 = this.m_0(aABB, vec34, vec35, doubleValue2, false);
            }

            if (!flag8) {
               BlockHitResult blockHitResult = this.raycast(vec34, vec35);
               boolean flag10 = blockHitResult.getType() != Type.MISS;
               flag9 = this.m_0(aABB, vec34, flag10 ? blockHitResult.getLocation() : vec35, doubleValue2, true);
               flag8 = flag9 || flag10;
            }

            vec34 = vec35;
            vec33 = new Vec3(vec33.x * c2.m_0_k_double(), vec33.y * c2.m_0_k_double() - c2.m_0c(), vec33.z * c2.m_0_k_double());
         }

         if (flag7) {
            value2++;
         }

         if (flag6 ? flag9 : flag7) {
            value3++;
         }
      }

      return new ProjectHelper.DataRecord3(value2, value3);
   }

   private boolean m_0(AABB aABB, Vec3 vec32, Vec3 vec33, double doubleValue, boolean flag6) {
      if (aABB.contains(vec32) || aABB.clip(vec32, vec33).isPresent()) {
         return true;
      }

      if (doubleValue <= 0.0) {
         return false;
      }

      var optional = aABB.inflate(doubleValue).clip(vec32, vec33);
      if (optional.isEmpty()) {
         return false;
      }

      Vec3 vec34 = aABB.getCenter();
      if (flag6) {
         BlockHitResult blockHitResult = this.raycast((Vec3)optional.get(), vec34);
         if (blockHitResult.getType() != Type.MISS) {
            vec34 = blockHitResult.getLocation();
         }
      }

      return aABB.clip((Vec3)optional.get(), vec34).isPresent();
   }

   private BlockHitResult raycast(Vec3 vec32, Vec3 vec33) {
      return MinecraftAccess.MINECRAFT.level.clip(new ClipContext(vec32, vec33, Block.COLLIDER, Fluid.NONE, MinecraftAccess.MINECRAFT.player));
   }

   private ProjectHelper.StateHolder2 m_8(Player player, ProjectHelper.StateHolder c4, boolean flag6) {
      if (!(Boolean)this.predict.getValue()) {
         return new ProjectHelper.StateHolder2(player.getBoundingBox().getCenter(), null, 0.0F, 0, 0);
      }

      Vec3 vec32 = flag6 ? Vec3.ZERO : c4.vec32;
      Vec3 vec33 = flag6 ? Vec3.ZERO : c4.vec34;
      PlayerUtil.StateHolder c1 = PlayerUtil.StateHolder.m_3(player, vec32);
      PlayerUtil playerUtil = PlayerUtil.fromPlayer(player, c1);
      float floatValue = 0.0F;
      int value = 0;
      int value2 = 0;
      if (!player.isFallFlying()) {
         boolean flag7 = vec33.lengthSqr() > 0.0;
         boolean flag8 = player.isSprinting() || vec32.horizontalDistance() > 0.25;
         c1.input = new Input(flag7, false, false, false, flag7 && !c4.flag, c1.input.shift(), flag8);
         playerUtil.flag = flag8;
         if (flag7) {
            playerUtil.floatValue = getYaw(vec33);
            if (c4.doubleValue > 0.0 && c4.vec36 != null) {
               value2 = Math.max(2, (int)Math.round(c4.doubleValue));
               value = Math.max(1, value2 - c4.value);
               floatValue = getYaw(c4.vec36);
            }
         }
      }

      Vec3 vec34 = c4.vec3.subtract(playerUtil.vec3);
      playerUtil.vec3 = c4.vec3;
      playerUtil.aABB = playerUtil.aABB.move(vec34);
      playerUtil.flag4 = c4.flag;
      playerUtil.vec32 = m_5(player, c4, vec32);
      return new ProjectHelper.StateHolder2(playerUtil.aABB.getCenter(), playerUtil, floatValue, value, value2);
   }

   private static Vec3 m_5(Player player, ProjectHelper.StateHolder c4, Vec3 vec32) {
      if (isFlyingOrSwimming(player)) {
         return vec32;
      }

      double doubleValue = c4.flag ? getGroundFriction(player, c4.vec3) * 0.91 : 0.91;
      return new Vec3(vec32.x * doubleValue, (vec32.y - 0.08) * 0.98, vec32.z * doubleValue);
   }

   private void m_7(Player player, Vec3 vec32, boolean flag6) {
      ProjectHelper.StateHolder c4 = this.integerMap.get(player.getId());
      if (c4 != null && !(vec32.distanceToSqr(c4.vec3) > 100.0)) {
         double doubleValue = c4.m_1_double();
         int value = Math.max(1, (int)Math.round(doubleValue));
         if (doubleValue > 3.5) {
            c4.m_3_void();
            value = 2;
         }

         Vec3 vec33 = vec32.subtract(c4.vec3);
         if (c4.flag2 && !isFlyingOrSwimming(player)) {
            m_5(player, c4, vec33, value, flag6);
         } else {
            c4.vec32 = vec33.scale(1.0 / value);
            Vec3 vec34 = new Vec3(c4.vec32.x, 0.0, c4.vec32.z);
            c4.vec34 = vec34.lengthSqr() > 0.0025 ? vec34.normalize() : Vec3.ZERO;
            c4.vec33 = Vec3.ZERO;
            c4.flag2 = true;
         }

         c4.vec3 = vec32;
         c4.longValue = System.nanoTime();
         c4.flag = flag6;
      } else {
         this.integerMap.put(player.getId(), new ProjectHelper.StateHolder(vec32, System.nanoTime(), flag6));
      }
   }

   private static void m_5(Player player, ProjectHelper.StateHolder c4, Vec3 vec32, int value, boolean flag6) {
      double doubleValue = flag6 ? getGroundFriction(player, c4.vec3) * 0.91 : 0.91;
      double doubleValue2 = Math.pow(doubleValue, value);
      double doubleValue3 = (1.0 - doubleValue2) / (1.0 - doubleValue);
      double doubleValue4 = doubleValue * doubleValue3;
      double doubleValue5 = (value - doubleValue4) / (1.0 - doubleValue);
      Vec3 vec33 = new Vec3(c4.vec32.x, 0.0, c4.vec32.z);
      Vec3 vec34 = new Vec3(vec32.x, 0.0, vec32.z);
      Vec3 vec35 = vec34.subtract(vec33.scale(doubleValue4)).scale(1.0 / doubleValue5);
      double doubleValue6 = m_5(player, c4.flag, c4.vec3);
      Vec3 vec36;
      if (c4.flag == flag6 && !(vec35.length() > doubleValue6 * 1.6 + 0.02)) {
         vec36 = vec33.scale(doubleValue2).add(vec35.scale(doubleValue3));
         m_9(c4, vec35.length() < doubleValue6 * 0.4 ? Vec3.ZERO : vec35, value);
      } else {
         Vec3 vec37 = vec34.subtract(c4.vec33.scale(doubleValue5)).scale(1.0 / doubleValue4);
         vec36 = vec37.scale(doubleValue2).add(c4.vec33.scale(doubleValue3));
         if (c4.vec35.lengthSqr() > 0.0) {
            c4.value += value;
         }
      }

      double doubleValue7 = flag6 ? 0.0 : m_2(vec32.y, value);
      c4.vec32 = new Vec3(vec36.x, doubleValue7, vec36.z);
   }

   private static void m_9(ProjectHelper.StateHolder c4, Vec3 vec32, int value) {
      c4.vec33 = vec32;
      if (vec32.lengthSqr() == 0.0) {
         c4.vec34 = Vec3.ZERO;
         c4.value2 += value;
         if (c4.value2 > 6) {
            c4.vec35 = Vec3.ZERO;
            c4.vec36 = null;
            c4.doubleValue = 0.0;
            c4.value = 0;
         }
      } else {
         Vec3 vec33 = vec32.normalize();
         boolean flag6 = c4.vec35.lengthSqr() > 0.0;
         if (flag6 && vec33.dot(c4.vec35) > 0.7) {
            c4.value = c4.value + value + c4.value2;
         } else {
            if (flag6) {
               boolean flag7 = c4.vec36 != null && vec33.dot(c4.vec36) > 0.7;
               c4.doubleValue = flag7 ? (c4.doubleValue > 0.0 ? (c4.doubleValue + c4.value + c4.value2) * 0.5 : c4.value + c4.value2) : 0.0;
               c4.vec36 = c4.vec35;
            }

            c4.value = value;
         }

         if (c4.value > c4.doubleValue * 2.0 + 4.0) {
            c4.doubleValue = 0.0;
         }

         c4.vec35 = vec33;
         c4.vec34 = vec33;
         c4.value2 = 0;
      }
   }

   private static double m_2(double doubleValue, int value) {
      double doubleValue2 = -3.92;
      double doubleValue3 = Math.pow(0.98, value);
      double doubleValue4 = doubleValue2 + (doubleValue - value * doubleValue2) * 0.02 / (1.0 - doubleValue3);
      return doubleValue2 + doubleValue3 / 0.98 * (doubleValue4 - doubleValue2);
   }

   private static double m_5(Player player, boolean flag6, Vec3 vec32) {
      PlayerUtil.StateHolder c1 = PlayerUtil.StateHolder.m_3(player, Vec3.ZERO);
      double doubleValue = getGroundFriction(player, vec32);
      double doubleValue2 = flag6
         ? player.getAttributeValue(Attributes.MOVEMENT_SPEED) * (doubleValue > 0.6 ? 0.21600002 / (doubleValue * doubleValue * doubleValue) : 1.0)
         : (player.isSprinting() ? 0.026 : 0.02);
      return doubleValue2 * 0.98 * c1.floatValue3 * (c1.input.shift() ? 0.3 : 1.0);
   }

   private static double getGroundFriction(Player player, Vec3 vec32) {
      return player.level().getBlockState(BlockPos.containing(vec32.x, vec32.y - 0.5000001, vec32.z)).getBlock().getFriction();
   }

   private static boolean isFlyingOrSwimming(Player player) {
      return player.isFallFlying() || player.isInWater() || player.isInLava();
   }

   private static float getYaw(Vec3 vec32) {
      return (float)Math.toDegrees(Math.atan2(-vec32.x, vec32.z));
   }

   private static double getServerTickNanos() {
      return 1.0E9 / Mth.clamp(ServerTpsTracker.SERVER_TPS_TRACKER.getTps(), 5.0F, 20.0F);
   }

   private double getPingSeconds() {
      ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
      PlayerInfo playerInfo = clientPacketListener != null ? clientPacketListener.getPlayerInfo(MinecraftAccess.MINECRAFT.player.getUUID()) : null;
      return playerInfo != null ? playerInfo.getLatency() * 1000000.0 / getServerTickNanos() : 0.0;
   }

   private ProjectHelper.DataRecord getProjectileData() {
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
      if (itemStack.getItem() instanceof BowItem) {
         float floatValue = BowItem.getPowerForTime(MinecraftAccess.MINECRAFT.player.getTicksUsingItem() + 1);
         return new ProjectHelper.DataRecord(Math.max(0.3, floatValue * 3.0), 0.99, 0.05, 0.1, true);
      } else if (itemStack.getItem() instanceof CrossbowItem) {
         ChargedProjectiles chargedProjectiles = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
         return chargedProjectiles.contains(Items.FIREWORK_ROCKET) ? new ProjectHelper.DataRecord(1.6, 1.0, 0.0, 0.15, false) : new ProjectHelper.DataRecord(3.15, 0.99, 0.05, 0.1, false);
      } else {
         return new ProjectHelper.DataRecord(2.5, 0.99, 0.05, 0.1, true);
      }
   }

   private static double[] m_6_DArr(int value) {
      double[] doubleArray = new double[value];

      for (int i = 0; i < value; i++) {
         double doubleValue = (i + 0.5) / value;
         doubleArray[i] = doubleValue < 0.5 ? -1.0 + Math.sqrt(2.0 * doubleValue) : 1.0 - Math.sqrt(2.0 * (1.0 - doubleValue));
      }

      return doubleArray;
   }

   private static double[][] buildOffsets(double[] doubleArray, boolean flag6) {
      ArrayList list = new ArrayList();
      if (flag6) {
         list.add(new double[]{0.0, 0.0, 0.0});
      }

      for (double doubleValue : doubleArray) {
         for (double doubleValue2 : doubleArray) {
            for (double doubleValue3 : doubleArray) {
               list.add(new double[]{doubleValue, doubleValue2, doubleValue3});
            }
         }
      }

      return (double[][])list.toArray(double[][]::new);
   }

   private double m_9_double(Player player) {
      Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      Vec3 vec33 = player.position().add(0.0, player.getBbHeight() * 0.5, 0.0);
      Vec3 vec34 = vec33.subtract(vec32);
      float floatValue = Mth.wrapDegrees((float)(Math.toDegrees(Math.atan2(vec34.z, vec34.x)) - 90.0));
      float floatValue2 = (float)(-Math.toDegrees(Math.atan2(vec34.y, Math.sqrt(vec34.x * vec34.x + vec34.z * vec34.z))));
      float floatValue3 = Math.abs(Mth.wrapDegrees(floatValue - MinecraftAccess.MINECRAFT.player.getYRot()));
      float floatValue4 = Math.abs(Mth.wrapDegrees(floatValue2 - MinecraftAccess.MINECRAFT.player.getXRot()));
      return floatValue3 + floatValue4;
   }

   private boolean isValidTarget(Entity entity) {
      if (entity instanceof Player player) {
         if (player != MinecraftAccess.MINECRAFT.player && player.isAlive() && player.tickCount >= 10) {
            double doubleValue = this.distance.getDouble();
            if (MinecraftAccess.MINECRAFT.player.distanceToSqr(entity) > doubleValue * doubleValue) {
               return false;
            }

            if (Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
               return false;
            }

            AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);
            return antiBot != null && antiBot.isBot(player) ? false : this.m_3_boolean(player);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean m_3_boolean(LivingEntity livingEntity2) {
      Vec3 vec32 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      AABB aABB = livingEntity2.getBoundingBox();

      for (double doubleValue : DOUBLE_VALUE_ARRAY5) {
         for (double doubleValue2 : DOUBLE_VALUE_ARRAY5) {
            for (double doubleValue3 : DOUBLE_VALUE_ARRAY5) {
               Vec3 vec33 = new Vec3(Mth.lerp(doubleValue2, aABB.minX, aABB.maxX), Mth.lerp(doubleValue, aABB.minY, aABB.maxY), Mth.lerp(doubleValue3, aABB.minZ, aABB.maxZ));
               if (this.raycast(vec32, vec33).getType() == Type.MISS) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean isHoldingProjectile() {
      ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
      if (itemStack.isEmpty()) {
         return false;
      } else if (itemStack.getItem() instanceof BowItem) {
         return this.weaponTypes.isSelected("Лук");
      } else if (itemStack.getItem() instanceof CrossbowItem) {
         return this.weaponTypes.isSelected("Арбалет");
      } else {
         return itemStack.getItem() instanceof TridentItem ? this.weaponTypes.isSelected("Трезубец") : false;
      }
   }

   private boolean m_8_boolean() {
      if (!this.isHoldingProjectile()) {
         return false;
      } else {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
         if (itemStack.isEmpty()) {
            return false;
         } else {
            return itemStack.getItem() instanceof CrossbowItem
               ? CrossbowItem.isCharged(itemStack)
               : MinecraftAccess.MINECRAFT.player.isUsingItem() && MinecraftAccess.MINECRAFT.player.getUsedItemHand() == InteractionHand.MAIN_HAND;
         }
      }
   }

   private boolean m_0_boolean() {
      if (this.rotations != null && this.rotations2 != null) {
         float floatValue = Math.abs(Mth.wrapDegrees(this.rotations2.getFloatValue() - this.rotations.getFloatValue()));
         float floatValue2 = Math.abs(Mth.wrapDegrees(this.rotations2.getFloatValue2() - this.rotations.getFloatValue2()));
         return floatValue <= 2.0F && floatValue2 <= 2.0F;
      } else {
         return false;
      }
   }

   private void reset() {
      this.rotations = null;
      this.rotations2 = null;
      this.livingEntity = null;
      this.flag = false;
      this.flag2 = false;
      this.vec3 = null;
      this.flag3 = false;
      this.flag4 = false;
   }

   static {
      m_7_void(false);
   }

   public static void m_7_void(boolean flag6) {
      flag5 = flag6;
   }

   public static boolean m_7_boolean() {
      return flag5;
   }

   public static boolean m_5_boolean() {
      return true;
   }

   private record DataRecord(double f_0_u, double f_0_k, double f_0c, double f_0_f, boolean f_1b) {
      public double m_7_double(double doubleValue) {
         return this.f_0_k >= 1.0 ? doubleValue : (1.0 - Math.pow(this.f_0_k, doubleValue)) / (1.0 - this.f_0_k);
      }

      public double m_0_z_double() {
         return this.f_0_k >= 1.0 ? 0.0 : -this.f_0c / (1.0 - this.f_0_k);
      }

      public double m_0_u() {
         return this.f_0_u;
      }

      public double m_0_k_double() {
         return this.f_0_k;
      }

      public double m_0c() {
         return this.f_0c;
      }

      public double m_0_f_double() {
         return this.f_0_f;
      }

      public boolean m_1b_boolean() {
         return this.f_1b;
      }
   }

   private record DataRecord2(Vec3 f_5y, Vec3 f_5k, ProjectHelper.DataRecord f_3d, ProjectHelper.StateHolder2 f_6o, double f_7_n, double f_7_d, double f_7h) {
      public Vec3 m_1_Vec3(double doubleValue) {
         return this.f_6o.m_1_Vec3(doubleValue);
      }

      public AABB m_2(double doubleValue) {
         Vec3 vec32 = this.f_6o.m_1_Vec3(doubleValue);
         return new AABB(
            vec32.x - this.f_7_d * 0.5,
            vec32.y - this.f_7h * 0.5,
            vec32.z - this.f_7_d * 0.5,
            vec32.x + this.f_7_d * 0.5,
            vec32.y + this.f_7h * 0.5,
            vec32.z + this.f_7_d * 0.5
         );
      }

      public ProjectHelper.DataRecord5 m_5_C9(Vec3 vec32) {
         double doubleValue = 0.0;
         double doubleValue2 = -1.0;
         double doubleValue3 = 0.0;
         double doubleValue4 = Double.MAX_VALUE;

         for (int i = 1; i <= 120; i++) {
            double doubleValue5 = this.m_7(vec32, i);
            if (doubleValue5 <= 0.0) {
               doubleValue = i - 1;
               doubleValue2 = i;
               break;
            }

            if (doubleValue5 < doubleValue4) {
               doubleValue4 = doubleValue5;
               doubleValue3 = i;
            }
         }

         double doubleValue6 = doubleValue3;
         if (doubleValue2 > 0.0) {
            for (int i2 = 0; i2 < 24; i2++) {
               double doubleValue7 = (doubleValue + doubleValue2) * 0.5;
               if (this.m_7(vec32, doubleValue7) <= 0.0) {
                  doubleValue2 = doubleValue7;
               } else {
                  doubleValue = doubleValue7;
               }
            }

            doubleValue6 = doubleValue2;
         }

         return new ProjectHelper.DataRecord5(new Rotations(this.m_3(vec32, doubleValue6)), doubleValue6);
      }

      private double m_7(Vec3 vec32, double doubleValue) {
         return this.m_3(vec32, doubleValue).length() - this.f_3d.m_0_u() * this.f_3d.m_7_double(doubleValue);
      }

      private Vec3 m_3(Vec3 vec32, double doubleValue) {
         Vec3 vec33 = new Vec3(0.0, this.f_3d.m_0_z_double(), 0.0);
         return this.f_6o
            .m_1_Vec3(doubleValue + this.f_7_n)
            .add(vec32)
            .subtract(this.f_5y)
            .subtract(this.f_5k.subtract(vec33).scale(this.f_3d.m_7_double(doubleValue)))
            .subtract(vec33.scale(doubleValue));
      }

      public Vec3 m_5y() {
         return this.f_5y;
      }

      public Vec3 m_5k() {
         return this.f_5k;
      }

      public ProjectHelper.DataRecord m_3d() {
         return this.f_3d;
      }

      public ProjectHelper.StateHolder2 m_6o_C8() {
         return this.f_6o;
      }

      public double m_7_n_double() {
         return this.f_7_n;
      }

      public double m_7_d() {
         return this.f_7_d;
      }

      public double m_7h_double() {
         return this.f_7h;
      }
   }

   private static final class StateHolder {
      private Vec3 vec3;
      private long longValue;
      private boolean flag;
      private boolean flag2;
      private Vec3 vec32;
      private Vec3 vec33;
      private Vec3 vec34;
      private Vec3 vec35;
      private Vec3 vec36;
      private int value;
      private int value2;
      private double doubleValue;

      private StateHolder(Vec3 vec37, long longValue2, boolean flag6) {
         this.vec32 = Vec3.ZERO;
         this.vec33 = Vec3.ZERO;
         this.vec34 = Vec3.ZERO;
         this.vec35 = Vec3.ZERO;
         this.vec3 = vec37;
         this.longValue = longValue2;
         this.flag = flag6;
      }

      private double m_1_double() {
         return (System.nanoTime() - this.longValue) / ProjectHelper.getServerTickNanos();
      }

      private void m_3_void() {
         this.flag2 = true;
         this.vec32 = Vec3.ZERO;
         this.vec33 = Vec3.ZERO;
         this.vec34 = Vec3.ZERO;
         this.vec35 = Vec3.ZERO;
         this.vec36 = null;
         this.value = 0;
         this.value2 = 0;
         this.doubleValue = 0.0;
      }
   }

   private record DataRecord3(int f_1o, int f_1d) {
      public int m_1o() {
         return this.f_1o;
      }

      public int m_1d() {
         return this.f_1d;
      }
   }

   private record DataRecord4(Player f_73, ProjectHelper.DataRecord2 f_3e, ProjectHelper.DataRecord5 f_1_j) {
      public Player m_73_Player() {
         return this.f_73;
      }

      public ProjectHelper.DataRecord2 m_3e() {
         return this.f_3e;
      }

      public ProjectHelper.DataRecord5 m_1_j_C9() {
         return this.f_1_j;
      }
   }

   private static final class StateHolder2 {
      private final List<Vec3> vec3List;
      private final PlayerUtil playerUtil;
      private final int value;
      private float floatValue;
      private int value2;

      private StateHolder2(Vec3 vec32, PlayerUtil playerUtil2, float floatValue2, int value3, int value4) {
         this.vec3List = new ArrayList<>();
         this.playerUtil = playerUtil2;
         this.floatValue = floatValue2;
         this.value2 = value3;
         this.value = value4;
         this.vec3List.add(vec32);
      }

      private Vec3 m_1_Vec3(double doubleValue) {
         int value3 = (int)Math.max(0.0, doubleValue);

         while (this.playerUtil != null && this.vec3List.size() <= value3 + 1) {
            if (this.value > 0 && this.vec3List.size() - 1 == this.value2) {
               float floatValue2 = this.playerUtil.floatValue;
               this.playerUtil.floatValue = this.floatValue;
               this.floatValue = floatValue2;
               this.value2 = this.value2 + this.value;
            }

            this.playerUtil.tick();
            this.vec3List.add(this.playerUtil.aABB.getCenter());
         }

         return value3 + 1 >= this.vec3List.size() ? this.vec3List.getLast() : this.vec3List.get(value3).lerp(this.vec3List.get(value3 + 1), doubleValue - value3);
      }
   }

   private record DataRecord5(Rotations f_2_r, double f_7b) {
      public Rotations m_2_r() {
         return this.f_2_r;
      }

      public double m_7b() {
         return this.f_7b;
      }
   }
}
