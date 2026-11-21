package wtf.onetap.module.movement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.MoveEvent;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.PreMotionEvent;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.SendPacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.module.player.AutoTool;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.NoClipHelper;
import wtf.onetap.util.PacketUtil;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "NoClip", description = "Проход сквозь стены", category = Category.MOVEMENT)
public class NoClip extends Module {
   public final ModeSetting mode = new ModeSetting("Режим", "Копание", "Копание", "Полёт", "GrimGround").build();
   public Vector2f vector2f;
   private static float floatValue;
   private static float floatValue2;
   private static boolean flag;
   private static boolean flag2;
   private final List<BlockPos> blockPosList = new ArrayList<>();
   private BlockPos blockPos;
   private boolean flag3;
   private boolean flag4;
   private int value;
   private int value2;
   private final Map<BlockPos, Long> blockPosMap = new HashMap<>();
   private final Map<BlockPos, NoClip.DataRecord> blockPosMap2 = new HashMap<>();
   private boolean flag5;
   private boolean flag6;
   private final TimeHelper timeHelper = new TimeHelper();
   private final ArrayDeque<ServerboundPongPacket> arrayDeque = new ArrayDeque<>();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private boolean flag7;
   private ClientLevel clientLevel;
   private static Module[] moduleArray;

   public static boolean m_7_boolean() {
      NoClip noClip = Onetap.MODULE_MANAGER == null ? null : Onetap.MODULE_MANAGER.get(NoClip.class);
      return noClip != null && noClip.isEnabled() && noClip.isFlyMode();
   }

   public static void m_7s_void() {
      if (m_7_boolean() && MinecraftAccess.MINECRAFT.player != null) {
         floatValue = MinecraftAccess.MINECRAFT.player.getYRot();
         floatValue2 = MinecraftAccess.MINECRAFT.player.getXRot();
         flag = true;
      }
   }

   public static void m_7i_void() {
      if (m_7_boolean() && flag && MinecraftAccess.MINECRAFT.player != null) {
         flag = false;
         flag2 = true;
         MinecraftAccess.MINECRAFT.player.setYRot(floatValue);
         MinecraftAccess.MINECRAFT.player.setXRot(floatValue2);
         MinecraftAccess.MINECRAFT.player.yHeadRot = floatValue;
         MinecraftAccess.MINECRAFT.player.yBodyRot = floatValue;
         MinecraftAccess.MINECRAFT.player.yRotO = floatValue;
         MinecraftAccess.MINECRAFT.player.xRotO = floatValue2;
         MinecraftAccess.MINECRAFT.player.yHeadRotO = floatValue;
         MinecraftAccess.MINECRAFT.player.yBodyRotO = floatValue;
      }
   }

   private static void reset() {
      flag = false;
      flag2 = false;
   }

   @EventTarget
   private void onSendPacket(SendPacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         this.m_8_void(false);
         if (this.isGrimGroundMode()) {
            this.m_7(event);
         } else if (event.getPacket() instanceof ServerboundMovePlayerPacket serverboundMovePlayerPacket && this.isFlyMode()) {
            if (flag2) {
               flag2 = false;
               if (serverboundMovePlayerPacket.hasRotation()) {
                  event.cancel();
                  sendMovePacket(serverboundMovePlayerPacket, floatValue, floatValue2);
               }
            }
         }
      }
   }

   private static void sendMovePacket(ServerboundMovePlayerPacket serverboundMovePlayerPacket, float floatValue3, float floatValue4) {
      PacketUtil.sendPacket(
         serverboundMovePlayerPacket.hasPosition()
            ? new PosRot(
               serverboundMovePlayerPacket.getX(MinecraftAccess.MINECRAFT.player.getX()),
               serverboundMovePlayerPacket.getY(MinecraftAccess.MINECRAFT.player.getY()),
               serverboundMovePlayerPacket.getZ(MinecraftAccess.MINECRAFT.player.getZ()),
               floatValue3,
               floatValue4,
               serverboundMovePlayerPacket.isOnGround(),
               serverboundMovePlayerPacket.horizontalCollision()
            )
            : new Rot(floatValue3, floatValue4, serverboundMovePlayerPacket.isOnGround(), serverboundMovePlayerPacket.horizontalCollision())
      );
   }

   public boolean isDigMode() {
      return ((String)this.mode.getValue()).equals("Копание");
   }

   public boolean isFlyMode() {
      return ((String)this.mode.getValue()).equals("Полёт");
   }

   public boolean isGrimGroundMode() {
      return ((String)this.mode.getValue()).equals("GrimGround");
   }

   private void m_7(SendPacketEvent event) {
      BlockPos blockPos2 = null;
      if (event.getPacket() instanceof ServerboundPlayerActionPacket serverboundPlayerActionPacket
         && (serverboundPlayerActionPacket.getAction() == Action.START_DESTROY_BLOCK || serverboundPlayerActionPacket.getAction() == Action.STOP_DESTROY_BLOCK)) {
         blockPos2 = serverboundPlayerActionPacket.getPos();
      } else if (event.getPacket() instanceof ServerboundUseItemOnPacket serverboundUseItemOnPacket) {
         blockPos2 = serverboundUseItemOnPacket.getHitResult().getBlockPos();
      }

      if (blockPos2 != null) {
         if (!this.flag7) {
            this.flag7 = true;
            this.timeHelper2.reset();
         }

         this.blockPosMap.put(blockPos2.immutable(), Long.MAX_VALUE);
      } else if (this.flag7 && event.getPacket() instanceof ServerboundPongPacket serverboundPongPacket) {
         event.cancel();
         this.arrayDeque.add(serverboundPongPacket);
      } else if (event.getPacket() instanceof ServerboundMovePlayerPacket serverboundMovePlayerPacket) {
         if (this.flag7) {
            event.cancel();
         } else if (serverboundMovePlayerPacket.hasRotation() && this.vector2f != null && (serverboundMovePlayerPacket.getYRot(0.0F) != this.vector2f.x || serverboundMovePlayerPacket.getXRot(0.0F) != this.vector2f.y)) {
            event.cancel();
            sendMovePacket(serverboundMovePlayerPacket, this.vector2f.x, this.vector2f.y);
         }
      }
   }

   private void m_7r_void() {
      boolean flag8 = this.vector2f != null;
      BlockPos blockPos2 = MinecraftAccess.MINECRAFT.gameMode != null && !MinecraftAccess.MINECRAFT.player.isUsingItem() ? this.m_5_BlockPos() : null;
      if (blockPos2 == null || !blockPos2.equals(this.blockPos)) {
         this.m_3_void(blockPos2 == null);
      }

      this.blockPos = blockPos2;
      if (blockPos2 != null) {
         this.m_7_r_void();
         if (flag8) {
            this.m_6_void(blockPos2);
            if (MinecraftAccess.MINECRAFT.level.isEmptyBlock(blockPos2)) {
               this.blockPos = this.m_5_BlockPos();
               if (this.blockPos != null) {
                  this.m_6_void(this.blockPos);
               }
            }
         }
      }
   }

   private BlockPos m_5_BlockPos() {
      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().deflate(0.001, 0.0, 0.001);

      for (int i = Mth.floor(aABB.minY - 0.001);
         NoClipHelper.m_3_boolean(new BlockPos(Mth.floor(MinecraftAccess.MINECRAFT.player.getX()), i, Mth.floor(MinecraftAccess.MINECRAFT.player.getZ())));
         i--
      ) {
         for (int i2 = Mth.floor(aABB.minX); i2 <= Mth.floor(aABB.maxX); i2++) {
            for (int i3 = Mth.floor(aABB.minZ); i3 <= Mth.floor(aABB.maxZ); i3++) {
               BlockPos blockPos2 = new BlockPos(i2, i, i3);
               if (this.m_5_boolean(blockPos2)) {
                  return blockPos2;
               }
            }
         }
      }

      return null;
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (this.flag7 && event.getPacket() instanceof ClientboundPlayerPositionPacket) {
         this.m_71_void();
      }
   }

   private void m_71_void() {
      this.flag7 = false;

      while (!this.arrayDeque.isEmpty()) {
         PacketUtil.sendPacket(this.arrayDeque.poll());
      }

      this.m_8_void(true);
      this.blockPosMap.clear();
   }

   public boolean m_6_boolean() {
      return this.isEnabled() && !this.isFlyMode() && this.vector2f != null;
   }

   public boolean m_0_boolean() {
      return this.isEnabled() && !this.isFlyMode() && this.blockPos != null;
   }

   public boolean m_8_boolean() {
      return this.isEnabled() && this.isDigMode() && this.blockPos != null;
   }

   @Override
   public void onEnable() {
      AutoTool autoTool = Onetap.MODULE_MANAGER.get(AutoTool.class);
      if (autoTool != null && !this.isFlyMode()) {
         autoTool.m_6m_void();
      }

      this.flag6 = false;
      this.timeHelper.reset();
      reset();
      super.onEnable();
      this.m_7_a_void();
   }

   private void m_7_a_void() {
      if (this.isFlyMode() && MinecraftAccess.MINECRAFT.player != null && !MinecraftAccess.MINECRAFT.player.getAbilities().mayfly) {
         this.notifyNeedFly();
      }
   }

   private void notifyNeedFly() {
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.sendSystemMessage(Component.literal("§cДля использования данного мода NoClip нужен /fly"));
      }

      this.setEnabled(false);
   }

   @Override
   public void onDisable() {
      this.m_71_void();
      this.m_4_void();
      this.timeHelper.reset();
      reset();
      if (MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.player.noPhysics = false;
      }

      this.m_8_void(true);
      this.m_2_void();
      super.onDisable();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.clientLevel != MinecraftAccess.MINECRAFT.level) {
         this.clientLevel = MinecraftAccess.MINECRAFT.level;
         if (this.flag7) {
            this.m_71_void();
         }

         if (!this.isFlyMode()) {
            this.m_2_void();
         }
      }

      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.flag7 && (!this.isGrimGroundMode() || this.timeHelper2.hasElapsed(5000L))) {
            this.m_71_void();
         }

         if (this.isFlyMode()) {
            this.m_7h_void();
         } else if (this.isGrimGroundMode()) {
            this.m_7r_void();
         } else {
            this.m_8_void(false);
            this.value2 = (this.value2 + 1) % 10;
            this.m_7_void();
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.isFlyMode()) {
            this.m_7(event);
         } else if (this.isGrimGroundMode()) {
            this.cancelInput(event);
         } else {
            this.m_8_void(false);
            if (this.flag4) {
               event.setJumping(true);
               this.flag4 = false;
            }

            if (this.vector2f != null) {
               NoClipHelper.m_4(event, this.vector2f.x);
            }
         }
      }
   }

   @EventTarget
   private void onMove(MoveEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.isFlyMode()) {
         this.m_4(event);
      }
   }

   @EventTarget
   private void onPreMotion(PreMotionEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null) {
         if (this.isFlyMode()) {
            if (this.isFlying()) {
               MinecraftAccess.MINECRAFT.player.setSprinting(false);
            }
         } else if (this.vector2f != null) {
            MinecraftAccess.MINECRAFT.player.yHeadRot = this.vector2f.x;
            MinecraftAccess.MINECRAFT.player.yBodyRot = this.vector2f.x;
         }
      }
   }

   public boolean isFlying() {
      return this.isEnabled() && this.isFlyMode() && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.getAbilities().flying;
   }

   public void m_7e_void() {
      if (this.isEnabled() && MinecraftAccess.MINECRAFT.player != null) {
         if (this.isFlyMode()) {
            this.m_7o_void();
         } else if (this.isGrimGroundMode()) {
            MinecraftAccess.MINECRAFT.player.noPhysics = true;
            MinecraftAccess.MINECRAFT.player.setDeltaMovement(0.0, 0.0, 0.0);
         }
      }
   }

   private void m_7h_void() {
      if (!MinecraftAccess.MINECRAFT.player.getAbilities().mayfly) {
         this.notifyNeedFly();
      } else if (!MinecraftAccess.MINECRAFT.player.getAbilities().flying) {
         this.m_4_void();
         this.timeHelper.reset();
         MinecraftAccess.MINECRAFT.player.noPhysics = false;
      } else {
         this.m_3_void();
         if (this.timeHelper.hasElapsed(200L)) {
            this.timeHelper.reset();
            this.m_7_j_void();
         }
      }
   }

   private void m_4(MoveEvent event) {
      if (MinecraftAccess.MINECRAFT.player.getAbilities().flying) {
         event.setVec3(Vec3.ZERO);
         MinecraftAccess.MINECRAFT.player.noPhysics = true;
      }
   }

   private void m_7(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player.getAbilities().flying) {
         this.cancelInput(event);
      }
   }

   private void cancelInput(MovementInputEvent event) {
      event.setForward(0.0F);
      event.setStrafe(0.0F);
      event.setJumping(false);
      event.setSneaking(false);
   }

   private void m_7o_void() {
      if (!MinecraftAccess.MINECRAFT.player.getAbilities().flying) {
         MinecraftAccess.MINECRAFT.player.noPhysics = false;
      } else {
         MinecraftAccess.MINECRAFT.player.noPhysics = true;
         MinecraftAccess.MINECRAFT.player.setOnGround(false);
         MinecraftAccess.MINECRAFT.player.setDeltaMovement(0.0, 0.0, 0.0);
      }
   }

   private void enableCustomInput() {
      if (!this.flag6 && MinecraftAccess.MINECRAFT.player != null) {
         this.flag6 = true;
         MinecraftAccess.MINECRAFT.player.input = new ClientInput();
      }
   }

   private void m_4_void() {
      if (this.flag6 && MinecraftAccess.MINECRAFT.player != null) {
         this.flag6 = false;
         if (MinecraftAccess.MINECRAFT.player.input.getClass() == ClientInput.class) {
            MinecraftAccess.MINECRAFT.player.input = new KeyboardInput(MinecraftAccess.MINECRAFT.options);
         }
      }
   }

   private void m_3_void() {
      this.enableCustomInput();
      MinecraftAccess.MINECRAFT.player.setDeltaMovement(0.0, 0.0, 0.0);
      if (MinecraftAccess.MINECRAFT.player.isSprinting()) {
         MinecraftAccess.MINECRAFT.player.setSprinting(false);
      }

      MinecraftAccess.MINECRAFT.options.keyUp.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyDown.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyLeft.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyRight.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyJump.setDown(false);
      MinecraftAccess.MINECRAFT.options.keyShift.setDown(false);
      MinecraftAccess.MINECRAFT.options.keySprint.setDown(false);
   }

   private void m_7_j_void() {
      if (MinecraftAccess.MINECRAFT.player.connection != null) {
         Vec3 vec3 = Vec3.directionFromRotation(MinecraftAccess.MINECRAFT.player.getXRot(), MinecraftAccess.MINECRAFT.player.getYRot()).scale(1.0);
         double doubleValue = MinecraftAccess.MINECRAFT.player.getX() + vec3.x;
         double doubleValue2 = MinecraftAccess.MINECRAFT.player.getY() + vec3.y;
         double doubleValue3 = MinecraftAccess.MINECRAFT.player.getZ() + vec3.z;
         MinecraftAccess.MINECRAFT.player.setPos(doubleValue, doubleValue2, doubleValue3);
         PacketUtil.sendPacket(new PosRot(doubleValue, doubleValue2, doubleValue3, MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot(), false, MinecraftAccess.MINECRAFT.player.horizontalCollision));
      }
   }

   private void m_7_void() {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.level == null || MinecraftAccess.MINECRAFT.gameMode == null) {
         this.m_2_void();
      } else if (MinecraftAccess.MINECRAFT.player.isUsingItem()) {
         this.m_3_void(true);
         this.blockPosList.clear();
         this.vector2f = null;
      } else {
         this.m_7_t_void();
         if (this.blockPos == null || !this.m_5_boolean(this.blockPos) || this.m_4_boolean(this.blockPos)) {
            this.m_7_u_void();
         }

         if (this.blockPos != null) {
            BlockPos blockPos2 = this.blockPos;
            this.m_6_void(blockPos2);
            if (MinecraftAccess.MINECRAFT.level.isEmptyBlock(blockPos2) || !this.m_5_boolean(blockPos2)) {
               this.m_7_u_void();
               if (this.blockPos != null) {
                  this.m_6_void(this.blockPos);
               }
            }
         } else {
            this.m_3_void(true);
            this.value = 0;
         }

         this.m_7_r_void();
      }
   }

   private void m_7_t_void() {
      var blockPosList2 = this.m_4_List();
      if (blockPosList2.isEmpty()) {
         this.blockPosList.clear();
         this.blockPos = null;
      } else if (this.blockPos != null && blockPosList2.contains(this.blockPos)) {
         this.blockPosList.clear();

         for (BlockPos blockPos2 : blockPosList2) {
            if (!blockPos2.equals(this.blockPos)) {
               this.blockPosList.add(blockPos2);
            }
         }
      } else {
         this.blockPosList.clear();
         this.blockPosList.addAll(blockPosList2);
         this.blockPos = this.blockPosList.remove(0);
         this.flag3 = false;
      }
   }

   private void m_7_u_void() {
      this.m_3_void(false);

      while (!this.blockPosList.isEmpty()) {
         BlockPos blockPos2 = this.blockPosList.remove(0);
         if (this.m_5_boolean(blockPos2) && !this.m_4_boolean(blockPos2)) {
            this.blockPos = blockPos2;
            this.flag3 = false;
            return;
         }
      }

      var blockPosList2 = this.m_4_List();
      if (!blockPosList2.isEmpty()) {
         this.blockPosList.addAll(blockPosList2.subList(1, blockPosList2.size()));
         this.blockPos = (BlockPos)blockPosList2.get(0);
         this.flag3 = false;
      } else {
         this.blockPos = null;
      }
   }

   private List<BlockPos> m_4_List() {
      ArrayList list = new ArrayList(4);
      if (this.isCrawling()) {
         BlockPos blockPos2 = this.m_8_BlockPos();
         BlockPos blockPos3 = this.m_9_BlockPos();
         if (blockPos3 != null) {
            list.add(blockPos3);
         }

         if (blockPos2 != null && !blockPos2.equals(blockPos3)) {
            list.add(blockPos2);
         }
      }

      BlockPos blockPos4 = this.m_1_BlockPos(MinecraftAccess.MINECRAFT.player.getEyeY());
      BlockPos blockPos5 = this.m_1_BlockPos(MinecraftAccess.MINECRAFT.player.getY() + 0.08);
      if (blockPos4 != null && !list.contains(blockPos4)) {
         list.add(blockPos4);
      }

      if (blockPos5 != null && !blockPos5.equals(blockPos4) && !list.contains(blockPos5)) {
         list.add(blockPos5);
      }

      return list;
   }

   private boolean isCrawling() {
      Pose pose = MinecraftAccess.MINECRAFT.player.getPose();
      return MinecraftAccess.MINECRAFT.player.isVisuallyCrawling() || pose == Pose.SWIMMING;
   }

   private BlockPos m_8_BlockPos() {
      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().deflate(0.02);
      int value3 = Mth.floor(aABB.minX);
      int value4 = Mth.floor(aABB.maxX - 1.0E-6);
      int value5 = Mth.floor(aABB.minY);
      int value6 = Mth.floor(aABB.maxY - 1.0E-6);
      int value7 = Mth.floor(aABB.minZ);
      int value8 = Mth.floor(aABB.maxZ - 1.0E-6);
      BlockPos blockPos2 = BlockPos.containing(MinecraftAccess.MINECRAFT.player.getX(), MinecraftAccess.MINECRAFT.player.getY(), MinecraftAccess.MINECRAFT.player.getZ());
      if (this.m_5_boolean(blockPos2) && new AABB(blockPos2).intersects(aABB)) {
         return blockPos2.immutable();
      }

      for (int i = value3; i <= value4; i++) {
         for (int i2 = value5; i2 <= value6; i2++) {
            for (int i3 = value7; i3 <= value8; i3++) {
               BlockPos blockPos3 = new BlockPos(i, i2, i3);
               if (this.m_5_boolean(blockPos3)) {
                  return blockPos3.immutable();
               }
            }
         }
      }

      return null;
   }

   private BlockPos m_9_BlockPos() {
      BlockPos blockPos2 = BlockPos.containing(MinecraftAccess.MINECRAFT.player.getX(), MinecraftAccess.MINECRAFT.player.getY(), MinecraftAccess.MINECRAFT.player.getZ()).above();
      return this.m_5_boolean(blockPos2) && NoClipHelper.m_3_boolean(blockPos2) ? blockPos2.immutable() : null;
   }

   private boolean m_4_boolean(BlockPos blockPos2) {
      return this.m_0_boolean(blockPos2) && !this.isCrawling();
   }

   private BlockPos m_1_BlockPos(double doubleValue) {
      AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox();
      float floatValue3 = MinecraftAccess.MINECRAFT.player.getYRot() * Float.intBitsToFloat((int)-2444639703911171531L);
      double doubleValue2 = -Mth.sin(floatValue3);
      m_4_C3_sArr();
      double doubleValue3 = Mth.cos(floatValue3);
      double doubleValue4 = MinecraftAccess.MINECRAFT.player.getX();
      double doubleValue5 = MinecraftAccess.MINECRAFT.player.getZ();
      double doubleValue6 = doubleValue2 > 1.0E-4 ? (aABB.maxX - doubleValue4) / doubleValue2 : (doubleValue2 < -1.0E-4 ? (aABB.minX - doubleValue4) / doubleValue2 : Double.POSITIVE_INFINITY);
      double doubleValue7 = doubleValue3 > 1.0E-4 ? (aABB.maxZ - doubleValue5) / doubleValue3 : (doubleValue3 < -1.0E-4 ? (aABB.minZ - doubleValue5) / doubleValue3 : Double.POSITIVE_INFINITY);
      double doubleValue8 = Math.min(doubleValue6, doubleValue7);
      if (!Double.isFinite(doubleValue8)) {
         doubleValue8 = (aABB.maxX - aABB.minX) * 0.5;
      }

      double doubleValue9 = doubleValue8 + 0.02;
      double doubleValue10 = doubleValue8 + 0.45;

      for (double doubleValue11 = doubleValue9; doubleValue11 <= doubleValue10 + 1.0E-4; doubleValue11 += 0.04) {
         BlockPos blockPos2 = BlockPos.containing(doubleValue4 + doubleValue2 * doubleValue11, doubleValue, doubleValue5 + doubleValue3 * doubleValue11);
         if (!this.m_0_boolean(blockPos2)) {
            if (this.m_5_boolean(blockPos2) && NoClipHelper.m_3_boolean(blockPos2)) {
               return blockPos2.immutable();
            }

            BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
            if (!blockState.isAir() && !blockState.getCollisionShape(MinecraftAccess.MINECRAFT.level, blockPos2).isEmpty()) {
               return null;
            }
         }
      }

      return null;
   }

   private boolean m_0_boolean(BlockPos blockPos2) {
      if (blockPos2 != null && MinecraftAccess.MINECRAFT.player != null) {
         AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().deflate(0.06);
         return aABB.intersects(new AABB(blockPos2));
      } else {
         return true;
      }
   }

   private boolean m_5_boolean(BlockPos blockPos2) {
      if (blockPos2 != null && MinecraftAccess.MINECRAFT.level != null) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
         if (!blockState.isAir() && blockState.getFluidState().isEmpty()) {
            float floatValue3 = blockState.getDestroySpeed(MinecraftAccess.MINECRAFT.level, blockPos2);
            if (floatValue3 < 0.0F) {
               return false;
            } else {
               return blockState.getCollisionShape(MinecraftAccess.MINECRAFT.level, blockPos2).isEmpty() && !blockState.is(Blocks.COBWEB) ? false : NoClipHelper.m_3_boolean(blockPos2);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void m_6_void(BlockPos blockPos2) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null && !MinecraftAccess.MINECRAFT.level.isEmptyBlock(blockPos2)) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
         Direction direction = this.m_0_Direction(blockPos2);
         NoClipHelper.m_2(
            this,
            blockState,
            blockPos2,
            () -> {
               boolean flag8 = MinecraftAccess.MINECRAFT.gameMode.continueDestroyBlock(blockPos2, direction);
               if (!flag8 && !MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
                  flag8 = MinecraftAccess.MINECRAFT.gameMode.startDestroyBlock(blockPos2, direction);
               }

               if (this.isDigMode()
                  && (this.value2 == 1 || this.value2 == 4 || this.value2 == 7)
                  && (flag8 || MinecraftAccess.MINECRAFT.gameMode.isDestroying())
                  && !MinecraftAccess.MINECRAFT.level.isEmptyBlock(blockPos2)) {
                  flag8 |= MinecraftAccess.MINECRAFT.gameMode.continueDestroyBlock(blockPos2, direction);
               }

               if (flag8 || MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
                  this.flag3 = true;
                  MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
               }

               if (this.isDigMode() && MinecraftAccess.MINECRAFT.level.isEmptyBlock(blockPos2)) {
                  this.m_4_void(blockPos2);
               }
            }
         );
      }
   }

   private void m_4_void(BlockPos blockPos2) {
      this.blockPosMap.put(blockPos2.immutable(), System.currentTimeMillis() + 100L);
      this.value++;
      if (this.value >= 2) {
         this.flag4 = true;
         this.value = 0;
      }
   }

   private Direction m_0_Direction(BlockPos blockPos2) {
      Vec3 vec3 = NoClipHelper.getEyePosition();
      Vec3 vec32 = new Vec3(blockPos2.getX() + 0.5, blockPos2.getY() + 0.5, blockPos2.getZ() + 0.5);
      Vec3 vec33 = vec3.subtract(vec32);
      Direction direction = Direction.UP;
      double doubleValue = -Double.MAX_VALUE;

      for (Direction direction2 : Direction.values()) {
         double doubleValue2 = vec33.x * direction2.getStepX() + vec33.y * direction2.getStepY() + vec33.z * direction2.getStepZ();
         if (doubleValue2 > doubleValue) {
            doubleValue = doubleValue2;
            direction = direction2;
         }
      }

      return direction;
   }

   private void m_3_void(boolean flag8) {
      if (MinecraftAccess.MINECRAFT.gameMode != null && this.flag3) {
         MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
      }

      if (flag8) {
         NoClipHelper.m_1(this);
      }

      this.blockPos = null;
      this.flag3 = false;
   }

   public boolean onBlockUpdate(ClientLevel clientLevel2, BlockPos blockPos2, BlockState blockState, int value3) {
      if (this.isEnabled()
         && !this.isFlyMode()
         && !this.flag5
         && !MinecraftAccess.MINECRAFT.hasSingleplayerServer()
         && clientLevel2 != null
         && clientLevel2 == MinecraftAccess.MINECRAFT.level
         && blockPos2 != null
         && blockState != null) {
         BlockPos blockPos3 = blockPos2.immutable();
         Long longObj = this.blockPosMap.get(blockPos3);
         if (longObj == null || System.currentTimeMillis() >= longObj) {
            this.blockPosMap.remove(blockPos3);
            this.blockPosMap2.remove(blockPos3);
            return false;
         }

         if (blockState.isAir()) {
            this.blockPosMap.remove(blockPos3);
            this.blockPosMap2.remove(blockPos3);
            return false;
         }

         if (blockState.hasBlockEntity()) {
            return false;
         }

         this.blockPosMap2.put(blockPos3, new NoClip.DataRecord(clientLevel2, blockPos3, blockState, value3, longObj));
         return true;
      } else {
         return false;
      }
   }

   private void m_8_void(boolean flag8) {
      long longValue = System.currentTimeMillis();
      this.blockPosMap.entrySet().removeIf(entry -> entry.getValue() <= longValue && !this.blockPosMap2.containsKey(entry.getKey()));
      if (!this.blockPosMap2.isEmpty()) {
         var iterator = this.blockPosMap2.entrySet().iterator();
         this.flag5 = true;

         try {
            while (iterator.hasNext()) {
               Entry entry2 = (Entry)iterator.next();
               NoClip.DataRecord c2 = (NoClip.DataRecord)entry2.getValue();
               if (flag8 || longValue >= c2.m_9l()) {
                  iterator.remove();
                  this.blockPosMap.remove(entry2.getKey());
                  if (c2.m_9m() == MinecraftAccess.MINECRAFT.level) {
                     c2.m_9m().setServerVerifiedBlockState(c2.m_9p_BlockPos(), c2.m_8y(), c2.m_03_int());
                  }
               }
            }
         } finally {
            this.flag5 = false;
         }
      }
   }

   private void m_7_r_void() {
      if (MinecraftAccess.MINECRAFT.player != null && this.blockPos != null) {
         Vec3 vec3 = NoClipHelper.getEyePosition();
         BlockPos blockPos2 = this.blockPos;
         Vec3 vec32 = new Vec3(
            Mth.clamp(vec3.x, blockPos2.getX() + 0.05, blockPos2.getX() + 0.95),
            Mth.clamp(vec3.y, blockPos2.getY() + 0.05, blockPos2.getY() + 0.95),
            Mth.clamp(vec3.z, blockPos2.getZ() + 0.05, blockPos2.getZ() + 0.95)
         );
         Vector2f vector2f2 = NoClipHelper.m_4_Vector2f(vec32, blockPos2);
         if (vector2f2 == null) {
            vector2f2 = NoClipHelper.m_9_Vector2f(vec32);
         }

         this.vector2f = vector2f2;
      } else {
         this.vector2f = null;
      }
   }

   private void m_2_void() {
      this.m_3_void(true);
      this.blockPosList.clear();
      this.vector2f = null;
      this.flag4 = false;
      this.value = 0;
      this.value2 = 0;
      this.blockPosMap.clear();
      this.blockPosMap2.clear();
   }

   public static void m_0(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_4_C3_sArr() {
      return moduleArray;
   }

   static {
      m_0(new Module[5]);
   }

   private record DataRecord(ClientLevel f_9m, BlockPos f_9p, BlockState f_8y, int f_03, long f_9l) {
      public ClientLevel m_9m() {
         return this.f_9m;
      }

      public BlockPos m_9p_BlockPos() {
         return this.f_9p;
      }

      public BlockState m_8y() {
         return this.f_8y;
      }

      public int m_03_int() {
         return this.f_03;
      }

      public long m_9l() {
         return this.f_9l;
      }
   }
}
