package wtf.onetap.module.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.crystal.CrystalDamage;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.RaycastUtil;
import wtf.onetap.util.Rotations;

@ModuleInformation(name = "AutoWeb", description = "Ставит паутину врагу под ноги", category = Category.COMBAT)
public class AutoWeb extends Module {
   private final FloatSetting distance = new FloatSetting("Дистанция", 4.5, 1.0, 6.0, 0.1).build();
   private final FloatSetting predictTicks = new FloatSetting("Предикт (тики)", 2.0, 0.0, 6.0, 1.0).build();
   private final BooleanSetting onHead = new BooleanSetting("На голову", false).build();
   private final ModeSetting swap = new ModeSetting("Свап", "Тихий", "Тихий", "Обычный").build();
   private final FloatSetting turnSpeed = new FloatSetting("Скорость поворота", 180.0, 10.0, 180.0, 1.0).build();
   private final ModeSetting anticheat = new ModeSetting("Античит", "Grim", "Grim", "GrimGround").build();
   private final BooleanSetting throughWalls = new BooleanSetting("Сквозь стены", true).build();
   private final BooleanSetting silentCorrection = new BooleanSetting("Незаметная коррекция", true).build();
   private final AutoWeb.StateHolder c8 = new AutoWeb.StateHolder();
   private final Map<BlockPos, Integer> blockPosMap = new HashMap<>();
   private final Map<BlockPos, Integer> blockPosMap2 = new HashMap<>();
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private int value;
   private int value2;
   private int value3;
   private boolean flag;
   private boolean flag2;

   public boolean isFlag() {
      return this.flag;
   }

   public BlockPos m_3_BlockPos() {
      return this.blockPos2 != null && this.value3 - this.value < 10 ? this.blockPos2 : null;
   }

   @Override
   public void onDisable() {
      this.blockPos2 = null;
      this.blockPosMap.clear();
      this.blockPosMap2.clear();
      this.blockPos = null;
      this.value2 = 0;
      this.flag = false;
      if (this.flag2) {
         InventoryUtil.restoreSelectedSlot();
      }

      this.flag2 = false;
   }

   @EventTarget(priority = EventPriority.NORMAL)
   private void onTick(TickEvent event) {
      this.flag = false;
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         this.value3++;
         this.m_4_void();
         double doubleValue = Math.min(this.distance.getDouble(), MinecraftAccess.MINECRAFT.player.blockInteractionRange());
         RaycastUtil.DataRecord2 c5 = this.m_9_C5(doubleValue);
         if (c5 == null) {
            if (this.flag2) {
               InventoryUtil.restoreSlot();
            }

            this.flag2 = false;
         } else {
            this.m_1(c5, MinecraftAccess.MINECRAFT.player.getEyePosition(), doubleValue);
         }
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.flag && (Boolean)this.silentCorrection.getValue()) {
         LegacyCrystalAura.fixMovement(event);
      }
   }

   private RaycastUtil.DataRecord2 m_9_C5(double doubleValue) {
      if (!MinecraftAccess.MINECRAFT.player.isUsingItem()
         && !MinecraftAccess.MINECRAFT.player.isSpectator()
         && MinecraftAccess.MINECRAFT.gui.screen() == null
         && !InventoryUtil.isBusy()
         && !InventoryUtil.isKeyDown(MinecraftAccess.MINECRAFT.options.keyAttack)
         && !InventoryUtil.isKeyDown(MinecraftAccess.MINECRAFT.options.keyUse)) {
         if (!MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.COBWEB) && InventoryUtil.findHotbarSlot(Items.COBWEB) < 0) {
            return null;
         }

         ArrayList list = new ArrayList();
         ArrayList list2 = new ArrayList();
         this.m_7(doubleValue, list, list2);
         if (list.isEmpty()) {
            return null;
         }

         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
         Vec3 vec32 = Onetap.ROTATION_MODES.toRotations().toVector();
         AABB aABB = MinecraftAccess.MINECRAFT.player.getBoundingBox().minmax(PlayerUtil.predictLocal(2).aABB);
         return RaycastUtil.m_4(this.c8, vec3, vec32, aABB, list, list2, doubleValue, (Boolean)this.onHead.getValue(), (Boolean)this.throughWalls.getValue());
      } else {
         return null;
      }
   }

   private void m_7(double doubleValue, List<RaycastUtil.DataRecord> c1List, List<AABB> aABBList) {
      AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);
      ArrayList<Player> playerList = new ArrayList<>();

      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (player != MinecraftAccess.MINECRAFT.player && player.isAlive() && !player.isSpectator() && !(MinecraftAccess.MINECRAFT.player.distanceTo(player) > doubleValue + 3.0)) {
            if (Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
               aABBList.add(player.getBoundingBox());
            } else if (!player.isCreative() && (antiBot == null || !antiBot.isBot(player))) {
               playerList.add(player);
            }
         }
      }

      playerList.sort(Comparator.comparingDouble(MinecraftAccess.MINECRAFT.player::distanceToSqr));

      for (Player player2 : playerList) {
         AABB aABB = this.predictTicks.getInt() > 0 ? PlayerUtil.predict(player2, this.predictTicks.getInt()).aABB : player2.getBoundingBox();
         c1List.add(new RaycastUtil.DataRecord(player2.getBoundingBox(), aABB));
      }
   }

   private void m_1(RaycastUtil.DataRecord2 c5, Vec3 vec3, double doubleValue) {
      float floatValue = this.turnSpeed.getFloat();
      Rotations rotations = Onetap.ROTATION_MODES.toRotations();
      Vec3 vec32 = LegacyCrystalAura.getEyePosition();
      Rotations rotations2 = this.m_0(vec3, vec32, rotations, c5, doubleValue) != null ? rotations : new Rotations(vec3.add(vec32).scale(0.5), c5.m_2s_Vec3());
      Onetap.ROTATION_MODES.rotate(LegacyCrystalAura.smoothRotate(rotations, rotations2, floatValue), false, 16);
      this.flag = true;
      this.blockPos2 = c5.m_4_n_n();
      this.value = this.value3;
      Rotations rotations3 = Onetap.ROTATION_MODES.toRotations();
      BlockHitResult blockHitResult = LegacyCrystalAura.m_8_boolean(rotations3) ? null : this.m_0(vec3, vec32, rotations3, c5, doubleValue);
      if (blockHitResult != null && this.m_6_boolean(blockHitResult)) {
         LegacyCrystalAura.m_0(rotations3);
         this.blockPosMap.put(c5.m_4_n_n(), this.value3);
         this.blockPos = null;
         this.value2 = 0;
      } else {
         this.m_0_void(c5.m_4_n_n());
      }
   }

   private boolean m_6_boolean(BlockHitResult blockHitResult) {
      if (MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.COBWEB)) {
         MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.OFF_HAND, blockHitResult);
         MinecraftAccess.MINECRAFT.player.swing(InteractionHand.OFF_HAND);
         return true;
      }

      int value4 = InventoryUtil.findHotbarSlot(Items.COBWEB);
      if (value4 < 0) {
         return false;
      }

      int value5 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      InventoryUtil.selectHotbarSlot(value4);
      MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
      MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
      if (value4 == value5) {
         return true;
      }

      if (((String)this.swap.getValue()).equals("Тихий") && !this.isGrimAnticheat()) {
         InventoryUtil.selectHotbarSlot(value5);
      } else {
         InventoryUtil.m_7_void(value5);
         this.flag2 = true;
      }

      return true;
   }

   private BlockHitResult m_0(Vec3 vec3, Vec3 vec32, Rotations rotations, RaycastUtil.DataRecord2 c5, double doubleValue) {
      BlockHitResult blockHitResult = LegacyCrystalAura.clipToBlock(vec3, rotations, c5.m_4_n__(), doubleValue);
      if (blockHitResult == null || blockHitResult.getDirection() != c5.m_2k_Direction()) {
         return null;
      } else if (!(Boolean)this.throughWalls.getValue() && LegacyCrystalAura.raycastBlocks(vec3, CrystalDamage.offsetTowards(blockHitResult.getLocation(), blockHitResult.getDirection()), CrystalDamage.DataRecord.f_0, false)) {
         return null;
      } else {
         return this.isGrimAnticheat() && LegacyCrystalAura.clipToBlock(vec32, rotations, c5.m_4_n__(), doubleValue) == null ? null : blockHitResult;
      }
   }

   private boolean isGrimAnticheat() {
      return ((String)this.anticheat.getValue()).equals("Grim");
   }

   private void m_0_void(BlockPos blockPos3) {
      if (!blockPos3.equals(this.blockPos)) {
         this.blockPos = blockPos3;
         this.value2 = 0;
      }

      if (++this.value2 >= 10) {
         this.blockPosMap2.put(blockPos3, this.value3 + 40);
         this.blockPos = null;
         this.value2 = 0;
      }
   }

   private void m_4_void() {
      ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
      PlayerInfo playerInfo = clientPacketListener != null ? clientPacketListener.getPlayerInfo(MinecraftAccess.MINECRAFT.player.getUUID()) : null;
      int value4 = (playerInfo != null ? playerInfo.getLatency() / 50 : 0) * 2 + 6;
      this.blockPosMap.entrySet().removeIf(entry -> {
         if (MinecraftAccess.MINECRAFT.level.getBlockState(entry.getKey()).is(Blocks.COBWEB)) {
            return true;
         }

         if (this.value3 - entry.getValue() <= value4) {
            return false;
         }

         this.blockPosMap2.put(entry.getKey(), this.value3 + 40);
         return true;
      });
      this.blockPosMap2.values().removeIf(integer -> integer <= this.value3);
   }

   private final class StateHolder implements RaycastUtil.CallbackType {
      @Override
      public boolean m_3_boolean(BlockPos blockPos3) {
         if (!AutoWeb.this.blockPosMap.containsKey(blockPos3) && !AutoWeb.this.blockPosMap2.containsKey(blockPos3)) {
            BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3);
            return blockState.canBeReplaced() && !blockState.is(Blocks.COBWEB) && blockState.getFluidState().isEmpty();
         } else {
            return false;
         }
      }

      @Override
      public boolean m_9_boolean(BlockPos blockPos3) {
         return AutoWeb.this.blockPosMap.containsKey(blockPos3) || MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3).is(Blocks.COBWEB);
      }

      @Override
      public boolean isCobweb(BlockPos blockPos3) {
         return MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3).is(Blocks.COBWEB) || LegacyCrystalAura.m_3_boolean(blockPos3);
      }

      @Override
      public boolean m_3_boolean(Vec3 vec3, Vec3 vec32) {
         return LegacyCrystalAura.raycastBlocks(vec3, vec32, CrystalDamage.DataRecord.f_0, false);
      }
   }
}
