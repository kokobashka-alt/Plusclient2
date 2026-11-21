package wtf.onetap.module.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.combat.crystal.CrystalDamage;
import wtf.onetap.event.ContinueAttackEvent;
import wtf.onetap.event.EntitySpawnEvent;
import wtf.onetap.event.EventPriority;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.event.PostMotionEvent;
import wtf.onetap.event.RemoveEntitiesEvent;
import wtf.onetap.event.Render2DEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Draw2D;
import wtf.onetap.render.Fonts;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.ModeSetting;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.WorldToScreen;

@ModuleInformation(name = "CrystalAura", description = "Ставит и взрывает кристаллы в лучших местах", category = Category.COMBAT)
public class LegacyCrystalAura extends Module {
   private static final List<AABB> A_A_B_B_LIST = List.of(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
   private static final Set<Item> ITEM_SET = Set.of(
      Items.TOTEM_OF_UNDYING,
      Items.ELYTRA,
      Items.GOLDEN_APPLE,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.PLAYER_HEAD,
      Items.DIAMOND,
      Items.DIAMOND_BLOCK,
      Items.DIAMOND_ORE,
      Items.DEEPSLATE_DIAMOND_ORE,
      Items.NETHERITE_INGOT,
      Items.NETHERITE_SCRAP,
      Items.ANCIENT_DEBRIS,
      Items.NETHERITE_BLOCK,
      Items.DIAMOND_SWORD,
      Items.DIAMOND_PICKAXE,
      Items.DIAMOND_AXE,
      Items.DIAMOND_SHOVEL,
      Items.DIAMOND_HOE,
      Items.DIAMOND_HELMET,
      Items.DIAMOND_CHESTPLATE,
      Items.DIAMOND_LEGGINGS,
      Items.DIAMOND_BOOTS,
      Items.NETHERITE_SWORD,
      Items.NETHERITE_PICKAXE,
      Items.NETHERITE_AXE,
      Items.NETHERITE_SHOVEL,
      Items.NETHERITE_HOE,
      Items.NETHERITE_HELMET,
      Items.NETHERITE_CHESTPLATE,
      Items.NETHERITE_LEGGINGS,
      Items.NETHERITE_BOOTS
   );
   private final FloatSetting targetDistance = new FloatSetting("Дистанция цели", 12.0, 4.0, 20.0, 0.5).build();
   private final FloatSetting placeDistance = new FloatSetting("Дистанция установки", 4.5, 1.0, 6.0, 0.1).build();
   private final FloatSetting hitDistance = new FloatSetting("Дистанция удара", 3.0, 1.0, 6.0, 0.1).build();
   private final BooleanSetting throughWalls = new BooleanSetting("Сквозь стены", true).build();
   private final FloatSetting minDamage = new FloatSetting("Мин. урон", 6.0, 0.0, 36.0, 0.5).build();
   private final FloatSetting maxSelfDamage = new FloatSetting("Макс. урон себе", 10.0, 0.0, 36.0, 0.5).build();
   private final BooleanSetting finishOff = new BooleanSetting("Добивание", true).build();
   private final FloatSetting predictTicks = new FloatSetting("Предикт (тики)", 2.0, 0.0, 6.0, 1.0).build();
   private final BooleanSetting placeObsidian = new BooleanSetting("Ставить обсидиан", true).build();
   private final BooleanSetting digUnder = new BooleanSetting("Подкапывать", true).build();
   private final FloatSetting maxDigTicks = new FloatSetting("Макс. тиков копания", 45.0, 1.0, 100.0, 1.0).visibleWhen(this.digUnder::getValue).build();
   private final ModeSetting anticheat = new ModeSetting("Античит", "Grim", "Grim", "GrimGround").build();
   private final ModeSetting swap = new ModeSetting("Свап", "Тихий", "Тихий", "Обычный").build();
   private final FloatSetting turnSpeed = new FloatSetting("Скорость поворота", 180.0, 10.0, 180.0, 1.0).build();
   private final BooleanSetting silentCorrection = new BooleanSetting("Незаметная коррекция", true).build();
   private final BooleanSetting dontBlowResources = new BooleanSetting("Не взрывать ресурсы", true).build();
   private final BooleanSetting visual = new BooleanSetting("Визуал", true).build();
   private final LegacyCrystalAura.StateHolder2 c7 = new LegacyCrystalAura.StateHolder2();
   private final Map<Integer, Integer> integerMap = new HashMap<>();
   private final Map<Integer, Integer> integerMap2 = new HashMap<>();
   private final Map<BlockPos, Integer> blockPosMap = new HashMap<>();
   private final Map<BlockPos, Integer> blockPosMap2 = new HashMap<>();
   private final Map<BlockPos, Integer> blockPosMap3 = new HashMap<>();
   private final Map<BlockPos, Integer> blockPosMap4 = new HashMap<>();
   private CrystalDamage.DataRecord3 c6;
   private CrystalDamage.DataRecord2 c5;
   private BlockPos blockPos;
   private int value;
   private int value2;
   private boolean flag;
   private boolean flag2;
   private List<CrystalDamage.DataRecord8> c9List = List.of();
   private List<ItemEntity> itemEntityList = List.of();
   private BlockPos blockPos2;
   private int value3;
   private int value4;
   private int value5 = 4;
   private int value6;
   private int value7;
   private static float floatValue = Float.intBitsToFloat(-1082130432);
   private boolean flag3;
   private boolean flag4;
   private CrystalDamage.DataRecord2 c52;
   private int value8;
   private final LegacyCrystalAura.StateHolder c1 = new LegacyCrystalAura.StateHolder();
   private final LegacyCrystalAura.StateHolder c12 = new LegacyCrystalAura.StateHolder();
   private long longValue;
   private static String text;

   public boolean isFlag3() {
      return this.flag3;
   }

   public float m_2_float(float floatValue2) {
      return this.m_0_boolean() ? Math.min(28.0F, floatValue2) : floatValue2;
   }

   @Override
   public void onDisable() {
      if (this.blockPos != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
         InventoryUtil.m_8_p_void();
      }

      this.blockPos = null;
      InventoryUtil.restoreSelectedSlot();
      this.flag = false;
      this.flag2 = false;
      this.integerMap.clear();
      this.integerMap2.clear();
      this.blockPosMap.clear();
      this.blockPosMap2.clear();
      this.blockPosMap3.clear();
      this.blockPosMap4.clear();
      this.itemEntityList = List.of();
      this.c5 = null;
      this.blockPos2 = null;
      this.c52 = null;
      this.c1.reset();
      this.c12.reset();
      this.flag3 = false;
      this.value5 = 4;
      this.value6 = 0;
      this.value7 = 0;
   }

   @EventTarget(priority = EventPriority.LOWEST)
   private void onTick(TickEvent event) {
      this.flag3 = false;
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         this.value4++;
         this.value5++;
         this.m_1_void();
         if (!this.isAutoWebActive()) {
            if (this.isPlayerBusy()) {
               this.resetAll();
            } else {
               ArrayList list = new ArrayList();
               ArrayList list2 = new ArrayList();
               this.getTargets(list, list2);
               this.c9List = list;
               if (list.isEmpty()) {
                  this.resetAll();
               } else {
                  Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
                  Vec3 vec32 = Onetap.ROTATION_MODES.toRotations().toVector();
                  this.c6 = this.m_1_C6(this.minDamage.getFloat());
                  this.itemEntityList = this.dontBlowResources.getValue() ? getNearbyItems() : List.of();
                  CrystalDamage.DataRecord8 c9 = this.m_7_C9();
                  var c7lList = this.getCrystals();
                  CrystalDamage.DataRecord2 c53 = this.c5;
                  this.c5 = CrystalDamage.m_0(this.c7, vec3, vec32, c9, list, list2, c7lList, this.c6, c53, this.blockPos2);
                  if (this.c5 == null && !this.c6.m_53()) {
                     this.c6 = this.m_1_C6(this.minDamage.getFloat() * 0.35F);
                     this.c5 = CrystalDamage.m_0(this.c7, vec3, vec32, c9, list, list2, c7lList, this.c6, c53, this.blockPos2);
                  }

                  if (this.c5 == null) {
                     this.resetAll();
                  } else {
                     this.c52 = this.c5;
                     this.value8 = this.value4;
                     this.m_0(this.c5);
                     this.m_0(this.c5, vec3);
                  }
               }
            }
         }
      }
   }

   private boolean isAutoWebActive() {
      AutoWeb autoWeb = Onetap.MODULE_MANAGER.get(AutoWeb.class);
      return autoWeb != null && autoWeb.isFlag();
   }

   private boolean isPlayerBusy() {
      return MinecraftAccess.MINECRAFT.player.isUsingItem()
         || MinecraftAccess.MINECRAFT.player.isSpectator()
         || MinecraftAccess.MINECRAFT.gui.screen() != null
         || InventoryUtil.isBusy()
         || InventoryUtil.isKeyDown(MinecraftAccess.MINECRAFT.options.keyAttack)
         || InventoryUtil.isKeyDown(MinecraftAccess.MINECRAFT.options.keyUse);
   }

   private boolean canRun() {
      return MinecraftAccess.MINECRAFT.player != null
         && MinecraftAccess.MINECRAFT.level != null
         && MinecraftAccess.MINECRAFT.gameMode != null
         && this.c6 != null
         && this.c5 != null
         && this.blockPos == null
         && !this.isAutoWebActive()
         && !this.isPlayerBusy();
   }

   private boolean m_2_boolean(Vec3 vec3) {
      CrystalDamage.DataRecord8 c9 = this.m_7_C9();
      float floatValue2 = CrystalDamage.m_6(this.c7, vec3, CrystalDamage.DataRecord.f_0, c9, this.c6.m_5_u(), true);
      if (!(floatValue2 > this.c6.m_1_f()) && !(floatValue2 + 2.0F >= c9.m_2d_float())) {
         for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
            if (player != MinecraftAccess.MINECRAFT.player
               && Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())
               && CrystalDamage.m_6(this.c7, vec3, CrystalDamage.DataRecord.f_0, this.predictPlayer(player, 0), this.c6.m_5_u(), true) > this.c6.m_1_f()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void m_0(CrystalDamage.DataRecord2 c53) {
      boolean flag5 = c53.m_5l() == CrystalDamage.ModeKind2.MINE || c53.m_5l() == CrystalDamage.ModeKind2.PLACE_OBSIDIAN || c53.m_5l() == CrystalDamage.ModeKind2.WAIT;
      if (flag5) {
         this.blockPos2 = c53.m_1_s_BlockPos();
      }

      if (flag5 || c53.m_1_s_BlockPos().equals(this.blockPos2)) {
         this.value3 = this.value4;
      }
   }

   @EventTarget
   private void onMovementInput(MovementInputEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && this.flag3 && (Boolean)this.silentCorrection.getValue()) {
         fixMovement(event);
      }
   }

   public static void fixMovement(MovementInputEvent event) {
      float floatValue2 = event.getForward();
      float floatValue3 = event.getStrafe();
      if (floatValue2 != 0.0F || floatValue3 != 0.0F) {
         float floatValue4 = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera().yRot();
         if (MinecraftAccess.MINECRAFT.options.getCameraType().isMirrored()) {
            floatValue4 -= 180.0F;
         }

         float floatValue5 = floatValue4 - MinecraftAccess.MINECRAFT.player.getYRot();
         float floatValue6 = (float)Math.toDegrees(Math.atan2(floatValue2, floatValue3));
         float floatValue7 = Mth.wrapDegrees(floatValue6 + floatValue5);
         float floatValue8 = Math.round(floatValue7 / 45.0F) * 45.0F;
         double doubleValue = Math.toRadians(floatValue8);
         Vec2 vec2 = new Vec2(Math.round((float)Math.cos(doubleValue)), Math.round((float)Math.sin(doubleValue))).normalized();
         event.setForward(vec2.y);
         event.setStrafe(vec2.x);
      }
   }

   @EventTarget
   private void onContinueAttack(ContinueAttackEvent event) {
      if (this.blockPos != null) {
         event.cancel();
      }
   }

   @EventTarget
   private void onEntitySpawn(EntitySpawnEvent event) {
      if (event.getEntity() instanceof EndCrystal endCrystal) {
         if (this.blockPosMap.remove(endCrystal.blockPosition().below()) != null && !this.isGrim() && this.canRun()) {
            if (this.m_4_boolean(endCrystal.position()) && this.m_2_boolean(endCrystal.position()) && !isItemNearby(endCrystal.position(), this.itemEntityList)) {
               this.m_9(endCrystal, MinecraftAccess.MINECRAFT.player.getEyePosition(), Onetap.ROTATION_MODES.toRotations());
            }
         }
      }
   }

   @EventTarget
   private void onPostMotion(PostMotionEvent event) {
      this.flag = false;
      this.flag2 = false;
   }

   public static List<ItemEntity> getNearbyItems() {
      return MinecraftAccess.MINECRAFT
         .level
         .getEntitiesOfClass(
            ItemEntity.class,
            MinecraftAccess.MINECRAFT.player.getBoundingBox().inflate(18.0),
            itemEntity -> ITEM_SET.contains(itemEntity.getItem().getItem())
               || itemEntity.getItem().isEnchanted()
               || Block.byItem(itemEntity.getItem().getItem()) instanceof ShulkerBoxBlock
         );
   }

   public static boolean isItemNearby(Vec3 vec3, List<ItemEntity> itemEntityList2) {
      for (ItemEntity itemEntity : itemEntityList2) {
         if (itemEntity.isAlive() && itemEntity.position().distanceToSqr(vec3) <= 144.0) {
            return true;
         }
      }

      return false;
   }

   private boolean m_4_boolean(Vec3 vec3) {
      for (CrystalDamage.DataRecord8 c9 : this.c9List) {
         float floatValue2 = this.c6.m_5_c() ? Math.min(this.c6.m_11(), c9.m_2d_float()) : this.c6.m_11();
         if (CrystalDamage.m_6(this.c7, vec3, CrystalDamage.DataRecord.f_0, c9, this.c6.m_5_u(), false) >= floatValue2) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   private void onRemoveEntities(RemoveEntitiesEvent event) {
      if (this.integerMap.containsKey(event.getValue()) && !this.isGrim() && this.canRun()) {
         if (MinecraftAccess.MINECRAFT.level.getEntity(event.getValue()) instanceof EndCrystal endCrystal) {
            BlockPos blockPos3 = endCrystal.blockPosition().below();
            if (blockPos3.equals(this.c5.m_1_s_BlockPos()) && this.c6.m_51() && this.m_2_boolean(endCrystal.position())) {
               this.m_3(blockPos3, MinecraftAccess.MINECRAFT.player.getEyePosition(), getEyePosition(), Onetap.ROTATION_MODES.toRotations(), true);
            }
         }
      }
   }

   private CrystalDamage.DataRecord3 m_1_C6(float floatValue2) {
      boolean flag5 = MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.END_CRYSTAL) || InventoryUtil.findHotbarSlot(Items.END_CRYSTAL) >= 0;
      boolean flag6 = MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.OBSIDIAN) || InventoryUtil.findHotbarSlot(Items.OBSIDIAN) >= 0;
      int value9 = MinecraftAccess.MINECRAFT.level.damageSources().explosion(null, MinecraftAccess.MINECRAFT.player).scalesWithDifficulty() ? MinecraftAccess.MINECRAFT.level.getDifficulty().getId() : 2;
      return new CrystalDamage.DataRecord3(
         Math.min(this.placeDistance.getDouble(), MinecraftAccess.MINECRAFT.player.blockInteractionRange()),
         Math.min(this.hitDistance.getDouble(), MinecraftAccess.MINECRAFT.player.entityInteractionRange()),
         floatValue2,
         this.maxSelfDamage.getFloat(),
         (Boolean)this.finishOff.getValue(),
         flag5,
         (Boolean)this.placeObsidian.getValue() && flag6 && this.value4 >= this.value7,
         (Boolean)this.digUnder.getValue(),
         this.maxDigTicks.getInt(),
         value9,
         (Boolean)this.throughWalls.getValue()
      );
   }

   private void getTargets(List<CrystalDamage.DataRecord8> c9List2, List<CrystalDamage.DataRecord8> c9List3) {
      AntiBot antiBot = Onetap.MODULE_MANAGER.get(AntiBot.class);
      double doubleValue = this.targetDistance.getDouble();
      ArrayList<Player> playerList = new ArrayList<>();

      for (Player player : MinecraftAccess.MINECRAFT.level.players()) {
         if (player != MinecraftAccess.MINECRAFT.player && player.isAlive() && !player.isSpectator() && !player.isCreative() && !(MinecraftAccess.MINECRAFT.player.distanceTo(player) > doubleValue)) {
            if (Onetap.FRIEND_MANAGER.isFriend(player.getName().getString())) {
               c9List3.add(this.predictPlayer(player, 0));
            } else if (antiBot == null || !antiBot.isBot(player)) {
               playerList.add(player);
            }
         }
      }

      playerList.sort(Comparator.comparingDouble(MinecraftAccess.MINECRAFT.player::distanceToSqr));

      for (int i = 0; i < Math.min(3, playerList.size()); i++) {
         c9List2.add(this.predictPlayer((Player)playerList.get(i), this.predictTicks.getInt()));
      }
   }

   private CrystalDamage.DataRecord8 m_7_C9() {
      CrystalDamage.DataRecord8 c9 = this.predictPlayer(MinecraftAccess.MINECRAFT.player, Math.min(this.getPing() + 2, 6));
      return !MinecraftAccess.MINECRAFT.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING) && !MinecraftAccess.MINECRAFT.player.getMainHandItem().is(Items.TOTEM_OF_UNDYING)
         ? c9
         : new CrystalDamage.DataRecord8(
            c9.m_1_h(), c9.m_6a_AABB(), c9.m_17_Vec3(), c9.m_6k_AABB(), Float.MAX_VALUE, c9.m_2t(), c9.m_2_v(), c9.m_24(), c9.m_3_m()
         );
   }

   private CrystalDamage.DataRecord8 predictPlayer(Player player, int value9) {
      Vec3 vec3 = player.position();
      AABB aABB = player.getBoundingBox();
      Vec3 vec32 = vec3;
      AABB aABB2 = aABB;
      if (value9 > 0) {
         PlayerUtil playerUtil = player == MinecraftAccess.MINECRAFT.player ? PlayerUtil.predictLocal(value9) : PlayerUtil.predict(player, value9);
         vec32 = playerUtil.vec3;
         aABB2 = playerUtil.aABB;
      }

      MobEffectInstance mobEffectInstance = player.getEffect(MobEffects.RESISTANCE);
      return new CrystalDamage.DataRecord8(
         vec3,
         aABB,
         vec32,
         aABB2,
         player.getHealth() + player.getAbsorptionAmount(),
         player.getArmorValue(),
         (float)player.getAttributeValue(Attributes.ARMOR_TOUGHNESS),
         this.getArmorProtection(player),
         mobEffectInstance == null ? -1 : mobEffectInstance.getAmplifier()
      );
   }

   private float getArmorProtection(Player player) {
      var registry = MinecraftAccess.MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
      Reference reference = registry.getOrThrow(Enchantments.PROTECTION);
      Reference reference2 = registry.getOrThrow(Enchantments.BLAST_PROTECTION);
      float floatValue2 = 0.0F;

      for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
         ItemStack itemStack = player.getItemBySlot(equipmentSlot);
         floatValue2 += EnchantmentHelper.getItemEnchantmentLevel(reference, itemStack) + EnchantmentHelper.getItemEnchantmentLevel(reference2, itemStack) * 2;
      }

      return floatValue2;
   }

   private List<CrystalDamage.DataRecord6> getCrystals() {
      ArrayList list = new ArrayList();

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (entity instanceof EndCrystal
            && entity.isAlive()
            && this.integerMap2.getOrDefault(entity.getId(), 0) < 3
            && (!this.isGrim() || !this.integerMap.containsKey(entity.getId()))
            && !isItemNearby(entity.position(), this.itemEntityList)) {
            list.add(new CrystalDamage.DataRecord6(this.integerMap.containsKey(entity.getId()) ? -1 : entity.getId(), entity.position()));
         }
      }

      for (BlockPos blockPos3 : this.blockPosMap.keySet()) {
         list.add(new CrystalDamage.DataRecord6(-1, CrystalDamage.topCenter(blockPos3)));
      }

      return list;
   }

   private void m_0(CrystalDamage.DataRecord2 c53, Vec3 vec3) {
      if (c53.m_5l() != CrystalDamage.ModeKind2.MINE) {
         this.stopMining();
         if (this.isSilentSwap() && (!this.isGrim() || c53.m_5l() != CrystalDamage.ModeKind2.PLACE_CRYSTAL && c53.m_5l() != CrystalDamage.ModeKind2.PLACE_OBSIDIAN)) {
            this.restoreSlotIfNeeded();
         }
      }

      this.flag4 = false;
      Vec3 vec32 = getEyePosition();
      Rotations rotations = this.m_0(vec3.add(vec32).scale(0.5), this.m_8(c53, vec3, vec32), this.turnSpeed.getFloat());
      switch (c53.m_5l()) {
         case BREAK:
            this.m_7(c53, vec3, rotations);
            break;
         case PLACE_CRYSTAL:
            this.m_3(c53.m_1_s_BlockPos(), vec3, vec32, rotations, true);
            break;
         case PLACE_OBSIDIAN:
            BlockHitResult blockHitResult = this.m_7(vec3, vec32, rotations, c53);
            if (blockHitResult != null && blockHitResult.getDirection() == c53.m_25() && this.m_7(Items.OBSIDIAN, blockHitResult)) {
               m_0(rotations);
               this.blockPosMap2.put(c53.m_1_s_BlockPos(), this.value4);
               if (!this.isGrim() && this.c6.m_51()) {
                  this.m_3(c53.m_1_s_BlockPos(), vec3, vec32, rotations, false);
               }
            }
            break;
         case MINE:
            this.m_4(c53, vec3, vec32, rotations);
         case WAIT:
         case HOLD:
      }

      this.m_6(c53);
   }

   private boolean m_3(BlockPos blockPos3, Vec3 vec3, Vec3 vec32, Rotations rotations, boolean flag5) {
      if (flag5 && m_8_boolean(rotations)) {
         return false;
      }

      if (this.c7.classify(blockPos3) == CrystalDamage.ModeKind.BASE && MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3.above()).isAir()) {
         if (this.c7.m_4(CrystalDamage.getBodyBox(blockPos3), true)) {
            return false;
         }

         BlockHitResult blockHitResult = clipToBlock(vec3, rotations, blockPos3, this.c6.m_5_s());
         if (blockHitResult != null && this.m_0(vec3, blockHitResult) && this.canHitBlock(vec32, rotations, blockPos3) && this.m_7(Items.END_CRYSTAL, blockHitResult)) {
            if (flag5) {
               m_0(rotations);
            }

            this.blockPosMap.put(blockPos3, this.value4);
            if (blockPos3.equals(this.blockPos2)) {
               this.blockPos2 = null;
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void m_6(CrystalDamage.DataRecord2 c53) {
      if (c53.m_5l() != CrystalDamage.ModeKind2.HOLD) {
         if (this.flag4) {
            this.blockPosMap4.remove(c53.m_1_s_BlockPos());
         } else if (this.blockPosMap4.merge(c53.m_1_s_BlockPos(), 1, Integer::sum) >= (c53.m_5l() == CrystalDamage.ModeKind2.WAIT ? 16 : 10)) {
            this.blockPosMap4.remove(c53.m_1_s_BlockPos());
            this.blockPosMap3.put(c53.m_1_s_BlockPos(), this.value4 + 40);
            if (c53.m_1_s_BlockPos().equals(this.blockPos2)) {
               this.blockPos2 = null;
            }

            if (c53.m_5l() == CrystalDamage.ModeKind2.BREAK) {
               this.integerMap2.put(c53.m_3r(), 3);
            }
         }
      }
   }

   private BlockHitResult m_7(Vec3 vec3, Vec3 vec32, Rotations rotations, CrystalDamage.DataRecord2 c53) {
      if (!m_8_boolean(rotations) && this.canHitBlock(vec32, rotations, c53.m_1_r_BlockPos())) {
         BlockHitResult blockHitResult = clipToBlock(vec3, rotations, c53.m_1_r_BlockPos(), this.c6.m_5_s());
         return blockHitResult != null && this.m_0(vec3, blockHitResult) ? blockHitResult : null;
      } else {
         return null;
      }
   }

   private boolean m_0(Vec3 vec3, BlockHitResult blockHitResult) {
      Direction direction = blockHitResult.getDirection();
      return this.c6.m_5r()
         || !this.c7.m_1(vec3, blockHitResult.getLocation().add(direction.getStepX() * 0.001, direction.getStepY() * 0.001, direction.getStepZ() * 0.001), CrystalDamage.DataRecord.f_0);
   }

   private boolean canHitBlock(Vec3 vec3, Rotations rotations, BlockPos blockPos3) {
      return !this.isGrim() || clipToBlock(vec3, rotations, blockPos3, this.c6.m_5_s()) != null;
   }

   private boolean m_4(Vec3 vec3, Vec3 vec32, Rotations rotations, BlockPos blockPos3, Direction direction) {
      BlockHitResult blockHitResult = clipToBlock(vec3, rotations, blockPos3, this.c6.m_5_s());
      return blockHitResult != null && (direction == null || blockHitResult.getDirection() == direction) && this.canHitBlock(vec32, rotations, blockPos3);
   }

   public static Vec3 getEyePosition() {
      return PlayerUtil.predictLocal(1).vec3.add(0.0, MinecraftAccess.MINECRAFT.player.getEyeHeight(), 0.0);
   }

   public static boolean m_8_boolean(Rotations rotations) {
      float floatValue2 = getYawDifference(rotations);
      return floatValue2 > 2.0F && Math.abs(floatValue2 - floatValue) < 1.0E-4F;
   }

   public static void m_0(Rotations rotations) {
      floatValue = getYawDifference(rotations);
   }

   private static float getYawDifference(Rotations rotations) {
      return Math.abs(rotations.getFloatValue() - Onetap.ROTATION_MODES.getTargetRotation().getFloatValue());
   }

   private Vec3 m_8(CrystalDamage.DataRecord2 c53, Vec3 vec3, Vec3 vec32) {
      Rotations rotations = Onetap.ROTATION_MODES.toRotations();

      boolean flag5 = switch (c53.m_5l()) {
         case BREAK -> {
            if (!(
               MinecraftAccess.MINECRAFT.level.getEntity(c53.m_3r()) instanceof EndCrystal endCrystal
                  && this.m_6(vec3, rotations, endCrystal.getBoundingBox(), this.c6.m_5p(), endCrystal.getId())
                  && (this.c6.m_5r() || !this.m_3(vec3, rotations, endCrystal.getBoundingBox(), this.c6.m_5p(), endCrystal.getId()))
            )) {
               yield false;
            }

            yield true;
         }
         case PLACE_CRYSTAL -> this.m_4(vec3, vec32, rotations, c53.m_1_s_BlockPos(), null);
         case PLACE_OBSIDIAN -> this.m_4(vec3, vec32, rotations, c53.m_1_r_BlockPos(), c53.m_25());
         case MINE -> this.m_4(vec3, vec32, rotations, c53.m_1_r_BlockPos(), null);
         case WAIT, HOLD -> false;
      };
      return flag5 ? null : c53.m_38();
   }

   private Rotations m_0(Vec3 vec3, Vec3 vec32, float floatValue2) {
      Rotations rotations = Onetap.ROTATION_MODES.toRotations();
      Rotations rotations2 = vec32 == null ? rotations : new Rotations(vec3, vec32);
      Onetap.ROTATION_MODES.rotate(smoothRotate(rotations, rotations2, this.m_2_float(floatValue2)), false, 15);
      this.flag3 = true;
      return Onetap.ROTATION_MODES.toRotations();
   }

   public static Rotations smoothRotate(Rotations rotations, Rotations rotations2, float floatValue2) {
      float floatValue3 = getSensitivityFactor();
      float floatValue4 = rotations.getFloatValue()
         + Math.round(Mth.clamp(Mth.wrapDegrees(rotations2.getFloatValue() - rotations.getFloatValue()), -floatValue2, floatValue2) / floatValue3) * floatValue3
         + ((MinecraftAccess.MINECRAFT.player.tickCount & 1) == 0 ? floatValue3 : -floatValue3);
      float floatValue5 = rotations.getFloatValue2() + Math.round(Mth.clamp(rotations2.getFloatValue2() - rotations.getFloatValue2(), -floatValue2, floatValue2) / floatValue3) * floatValue3;
      return new Rotations(floatValue4, Mth.clamp(floatValue5, -90.0F, 90.0F));
   }

   private static float getSensitivityFactor() {
      float floatValue2 = MinecraftAccess.MINECRAFT.options.sensitivity().get().floatValue() * 0.6F + 0.2F;
      return floatValue2 * floatValue2 * floatValue2 * 1.2F;
   }

   private void m_7(CrystalDamage.DataRecord2 c53, Vec3 vec3, Rotations rotations) {
      if (MinecraftAccess.MINECRAFT.level.getEntity(c53.m_3r()) instanceof EndCrystal endCrystal) {
         this.m_9(endCrystal, vec3, rotations);
      }
   }

   private void m_9(EndCrystal endCrystal, Vec3 vec3, Rotations rotations) {
      if (endCrystal.isAlive() && !this.flag && !this.flag2) {
         AABB aABB = endCrystal.getBoundingBox();
         double doubleValue = this.c6.m_5p();
         if (this.m_6(vec3, rotations, aABB, doubleValue, endCrystal.getId())) {
            if (this.c6.m_5r() || !this.m_3(vec3, Onetap.ROTATION_MODES.getTargetRotation(), aABB, doubleValue, endCrystal.getId())) {
               int value9 = MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.WEAKNESS) ? this.findSwordSlot() : MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
               if (value9 >= 0) {
                  this.runWithSlot(value9, () -> {
                     MinecraftAccess.MINECRAFT.gameMode.attack(MinecraftAccess.MINECRAFT.player, endCrystal);
                     MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
                  });
                  if (this.isGrim()) {
                     this.flag = true;
                  }

                  this.integerMap.put(endCrystal.getId(), this.value4);
                  this.value5 = 0;
                  this.flag4 = true;
               }
            }
         }
      }
   }

   private boolean m_6(Vec3 vec3, Rotations rotations, AABB aABB, double doubleValue, int value9) {
      if (aABB.contains(vec3)) {
         return true;
      }

      Vec3 vec32 = aABB.clip(vec3, vec3.add(rotations.toVector().scale(doubleValue))).orElse(null);
      return vec32 == null ? false : this.c6.m_5r() || !this.c7.m_1(vec3, vec32, CrystalDamage.DataRecord.f_0) && !this.c7.m_3(vec3, vec32, value9);
   }

   private boolean m_3(Vec3 vec3, Rotations rotations, AABB aABB, double doubleValue, int value9) {
      Vec3 vec32 = aABB.clip(vec3, vec3.add(rotations.toVector().scale(doubleValue + 3.0))).orElse(null);
      return vec32 != null && (this.c7.m_1(vec3, vec32, CrystalDamage.DataRecord.f_0) || this.c7.m_3(vec3, vec32, value9));
   }

   public static BlockHitResult clipToBlock(Vec3 vec3, Rotations rotations, BlockPos blockPos3, double doubleValue) {
      return AABB.clip(A_A_B_B_LIST, vec3, vec3.add(rotations.toVector().scale(doubleValue)), blockPos3);
   }

   private boolean m_7(Item item, BlockHitResult blockHitResult) {
      if ((!this.isGrim() || !this.flag) && !this.flag2) {
         if (MinecraftAccess.MINECRAFT.player.getOffhandItem().is(item)) {
            MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.OFF_HAND, blockHitResult);
            MinecraftAccess.MINECRAFT.player.swing(InteractionHand.OFF_HAND);
            this.flag = true;
            this.flag4 = true;
            return true;
         }

         int value9 = InventoryUtil.findHotbarSlot(item);
         if (value9 < 0) {
            return false;
         }

         this.flag = true;
         this.runWithSlot(value9, () -> {
            MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
            MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
         });
         this.flag4 = true;
         return true;
      } else {
         return false;
      }
   }

   private void runWithSlot(int value9, Runnable runnable) {
      int value10 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
      if (value9 == value10) {
         runnable.run();
      } else {
         InventoryUtil.selectHotbarSlot(value9);
         runnable.run();
         if (this.isSilentSwap() && !this.isGrim()) {
            InventoryUtil.selectHotbarSlot(value10);
         } else {
            InventoryUtil.m_7_void(value10);
         }
      }
   }

   private boolean isGrim() {
      return ((String)this.anticheat.getValue()).equals("Grim");
   }

   private boolean m_0_boolean() {
      return !this.isGrim() && this.value5 < 4;
   }

   private void m_4(CrystalDamage.DataRecord2 c53, Vec3 vec3, Vec3 vec32, Rotations rotations) {
      if (!this.isGrim() || !this.flag) {
         BlockPos blockPos3 = c53.m_1_r_BlockPos();
         BlockHitResult blockHitResult = clipToBlock(vec3, rotations, blockPos3, this.c6.m_5_s());
         if (blockHitResult != null && this.m_0(vec3, blockHitResult) && this.canHitBlock(vec32, rotations, blockPos3)) {
            if (this.blockPos != null && !this.blockPos.equals(blockPos3)) {
               this.stopMining();
            }

            int value9 = this.findBestTool(MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3), blockPos3);
            int value10 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
            if (value9 >= 0 && value9 != value10) {
               InventoryUtil.m_7_void(value10);
               InventoryUtil.selectHotbarSlot(value9);
            }

            if (this.blockPos == null) {
               this.blockPos = blockPos3;
               this.value = this.value4;
               this.value2 = Math.max(1, this.c7.m_5_int(blockPos3));
            }

            MinecraftAccess.MINECRAFT.gameMode.continueDestroyBlock(blockPos3, blockHitResult.getDirection());
            this.flag2 = true;
            MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
            this.flag4 = true;
         }
      }
   }

   private void stopMining() {
      if (this.blockPos != null) {
         this.blockPos = null;
         if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
            MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
            this.flag2 = true;
         }
      }
   }

   private void restoreSlotIfNeeded() {
      if (!this.isGrim() || !this.flag && !this.flag2) {
         InventoryUtil.restoreSlot();
      }
   }

   private void resetAll() {
      this.c5 = null;
      this.stopMining();
      this.restoreSlotIfNeeded();
      if (this.m_0_boolean() && Onetap.ROTATION_MODES.isFlag()) {
         Rotations rotations = Onetap.ROTATION_MODES.toRotations();
         float floatValue2 = Mth.wrapDegrees(Onetap.ROTATION_MODES.getRenderYaw() - rotations.getFloatValue());
         float floatValue3 = Onetap.ROTATION_MODES.getRenderPitch() - rotations.getFloatValue2();
         if (!(Math.abs(floatValue2) <= 28.0F) || !(Math.abs(floatValue3) <= 28.0F)) {
            Onetap.ROTATION_MODES.rotateWithSensitivity(new Rotations(rotations.getFloatValue() + Mth.clamp(floatValue2, -28.0F, 28.0F), rotations.getFloatValue2() + Mth.clamp(floatValue3, -28.0F, 28.0F)), false, 15);
            this.flag3 = true;
         }
      }
   }

   private void m_1_void() {
      int value9 = this.getPing() * 2 + 6;
      this.integerMap.entrySet().removeIf(entry -> {
         Entity entity = MinecraftAccess.MINECRAFT.level.getEntity(entry.getKey());
         if (entity != null && entity.isAlive()) {
            if (this.value4 - entry.getValue() <= value9) {
               return false;
            }

            this.integerMap2.merge(entry.getKey(), 1, Integer::sum);
            return true;
         } else {
            return true;
         }
      });
      this.integerMap2.keySet().removeIf(integer -> MinecraftAccess.MINECRAFT.level.getEntity(integer) == null);
      this.blockPosMap.entrySet().removeIf(entry2 -> {
         if (this.value4 - entry2.getValue() <= value9) {
            return false;
         }

         this.blockPosMap3.put(entry2.getKey(), this.value4 + 60);
         return true;
      });
      this.blockPosMap2.entrySet().removeIf(entry3 -> {
         if (this.value4 - entry3.getValue() <= value9) {
            return false;
         }

         if (MinecraftAccess.MINECRAFT.level.getBlockState(entry3.getKey()).is(Blocks.OBSIDIAN)) {
            this.value6 = 0;
         } else {
            this.blockPosMap3.put(entry3.getKey(), this.value4 + 100);
            if (++this.value6 >= 3) {
               this.value6 = 0;
               this.value7 = this.value4 + 60;
            }
         }

         return true;
      });
      this.blockPosMap3.values().removeIf(integer2 -> integer2 <= this.value4);
      if (this.blockPos2 != null && this.value4 - this.value3 > 20) {
         this.blockPos2 = null;
      }

      if (this.blockPos != null && this.value4 - this.value > this.value2 * 2 + value9) {
         this.blockPosMap3.put(this.blockPos, this.value4 + 100);
         this.stopMining();
      }
   }

   private int getPing() {
      ClientPacketListener clientPacketListener = MinecraftAccess.MINECRAFT.getConnection();
      PlayerInfo playerInfo = clientPacketListener != null ? clientPacketListener.getPlayerInfo(MinecraftAccess.MINECRAFT.player.getUUID()) : null;
      return playerInfo != null ? playerInfo.getLatency() / 50 : 0;
   }

   private boolean isSilentSwap() {
      return ((String)this.swap.getValue()).equals("Тихий");
   }

   private int findSwordSlot() {
      int value9 = -1;

      for (int i = 0; i < 9; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.is(ItemTags.SWORDS)) {
            return i;
         }

         if (value9 < 0 && itemStack.is(ItemTags.AXES)) {
            value9 = i;
         }
      }

      return value9;
   }

   private int findBestTool(BlockState blockState, BlockPos blockPos3) {
      int value9 = -1;
      int value10 = Integer.MAX_VALUE;

      for (int i = 0; i < 9; i++) {
         int value11 = this.getToolScore(blockState, blockPos3, MinecraftAccess.MINECRAFT.player.getInventory().getItem(i));
         if (value11 >= 0 && value11 < value10) {
            value10 = value11;
            value9 = i;
         }
      }

      return value9;
   }

   private int getToolScore(BlockState blockState, BlockPos blockPos3, ItemStack itemStack) {
      float floatValue2 = blockState.getDestroySpeed(MinecraftAccess.MINECRAFT.level, blockPos3);
      if (floatValue2 < 0.0F || blockState.isAir() || blockState.hasBlockEntity()) {
         return -1;
      }

      if (floatValue2 == 0.0F) {
         return 1;
      }

      boolean flag5 = !blockState.requiresCorrectToolForDrops() || itemStack.isCorrectToolForDrops(blockState);
      float floatValue3 = this.getToolEfficiency(blockState, itemStack) / floatValue2 / (flag5 ? 30.0F : 100.0F);
      return floatValue3 <= 0.0F ? -1 : Mth.ceil(1.0F / floatValue3);
   }

   private float getToolEfficiency(BlockState blockState, ItemStack itemStack) {
      float floatValue2 = itemStack.getDestroySpeed(blockState);
      if (floatValue2 > 1.0F) {
         var registry = MinecraftAccess.MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
         int value9 = EnchantmentHelper.getItemEnchantmentLevel(registry.getOrThrow(Enchantments.EFFICIENCY), itemStack);
         floatValue2 += (float)MinecraftAccess.MINECRAFT.player.getAttributeBaseValue(Attributes.MINING_EFFICIENCY) + (value9 > 0 ? value9 * value9 + 1.0F : 0.0F);
      }

      if (MobEffectUtil.hasDigSpeed(MinecraftAccess.MINECRAFT.player)) {
         floatValue2 *= 1.0F + (MobEffectUtil.getDigSpeedAmplification(MinecraftAccess.MINECRAFT.player) + 1) * 0.2F;
      }

      MobEffectInstance mobEffectInstance = MinecraftAccess.MINECRAFT.player.getEffect(MobEffects.MINING_FATIGUE);
      if (mobEffectInstance != null) {
         floatValue2 *= switch (mobEffectInstance.getAmplifier()) {
            case 0 -> 0.3F;
            case 1 -> 0.09F;
            case 2 -> 0.0027F;
            default -> 8.1E-4F;
         };
      }

      floatValue2 *= (float)MinecraftAccess.MINECRAFT.player.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
      if (MinecraftAccess.MINECRAFT.player.isEyeInFluid(FluidTags.WATER)) {
         floatValue2 *= (float)MinecraftAccess.MINECRAFT.player.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
      }

      return floatValue2;
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.visual.getValue() && MinecraftAccess.MINECRAFT.player != null) {
         long longValue2 = System.nanoTime();
         float floatValue2 = this.longValue == 0L ? 0.0F : Math.min((float)(longValue2 - this.longValue) / 1.0E9F, 0.1F);
         this.longValue = longValue2;
         boolean flag5 = this.c52 != null && this.value4 - this.value8 < 10;
         this.c1.m_3(flag5 ? Vec3.atLowerCornerOf(this.c52.m_1_s_BlockPos()) : null, floatValue2);
         if (flag5) {
            this.c1.m_4(getColor(this.c52.m_5l()), getStateText(this.c52.m_5l()), this.c52.m_50());
         }

         AutoWeb autoWeb = Onetap.MODULE_MANAGER.get(AutoWeb.class);
         BlockPos blockPos3 = autoWeb != null && autoWeb.isEnabled() ? autoWeb.m_3_BlockPos() : null;
         this.c12.m_3(blockPos3 != null ? Vec3.atLowerCornerOf(blockPos3) : null, floatValue2);
         if (blockPos3 != null) {
            this.c12.m_4(-1510145, "Паутина", Float.NaN);
         }

         Vec3 vec3 = event.getVec3();
         Matrix4f matrix4f = new Matrix4f(event.getMatrix4f()).mul(event.getMatrix4f2());
         this.renderBox(this.c1, matrix4f, vec3);
         this.renderBox(this.c12, matrix4f, vec3);
      }
   }

   private void renderBox(LegacyCrystalAura.StateHolder c13, Matrix4f matrix4f, Vec3 vec3) {
      if (c13.m_5_boolean()) {
         AABB aABB = new AABB(c13.vec3, c13.vec3.add(1.0, 1.0, 1.0));
         Box3DRenderer.drawBox(
            matrix4f,
            (float)(aABB.minX - vec3.x),
            (float)(aABB.minY - vec3.y),
            (float)(aABB.minZ - vec3.z),
            (float)(aABB.maxX - vec3.x),
            (float)(aABB.maxY - vec3.y),
            (float)(aABB.maxZ - vec3.z),
            ColorUtil.withAlpha(c13.value, (int)(50.0F * c13.floatValue))
         );
         Line3DRenderer.drawLines(matrix4f, ElytraTarget.boxToLines(aABB), vec3, 1.5F, ColorUtil.withAlpha(c13.value, (int)(220.0F * c13.floatValue)));
      }
   }

   @EventTarget
   private void onRender2D(Render2DEvent event) {
      if ((Boolean)this.visual.getValue() && (this.c1.m_5_boolean() || this.c12.m_5_boolean())) {
         CameraRenderState cameraRenderState = MinecraftAccess.MINECRAFT.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
         WorldToScreen.setMatrices(new Matrix4f(cameraRenderState.projectionMatrix), new Matrix4f(cameraRenderState.viewRotationMatrix));
         this.renderLabel(this.c1);
         this.renderLabel(this.c12);
      }
   }

   private void renderLabel(LegacyCrystalAura.StateHolder c13) {
      if (c13.m_5_boolean()) {
         WorldToScreen.DataRecord c9 = WorldToScreen.project(c13.vec3.add(0.5, 1.25, 0.5));
         if (c9 != null) {
            String text2 = Float.isNaN(c13.floatValue2) ? c13.text : c13.text + " " + String.format("%.1f", c13.floatValue2);
            float floatValue2 = 12.0F;
            float floatValue3 = Draw2D.getTextWidth(text2, Fonts.SUPPLIER14, floatValue2);
            Draw2D.drawStyledText(text2, Fonts.SUPPLIER14, c9.m_2r_float() - floatValue3 / 2.0F, c9.m_2___float(), ColorUtil.withAlpha(c13.value | 0xFF000000, (int)(255.0F * c13.floatValue)), floatValue2);
         }
      }
   }

   private static int getColor(CrystalDamage.ModeKind2 c2) {
      return switch (c2) {
         case BREAK -> -43691;
         case PLACE_CRYSTAL, HOLD -> ColorUtil.primaryColor();
         case PLACE_OBSIDIAN -> -5210881;
         case MINE, WAIT -> -16307;
      };
   }

   private static String getStateText(CrystalDamage.ModeKind2 c2) {
      return switch (c2) {
         case BREAK -> "Удар";
         case PLACE_CRYSTAL -> "Кристалл";
         case PLACE_OBSIDIAN -> "Обсидиан";
         case MINE -> "Копаю";
         case WAIT -> "Жду";
         case HOLD -> "Держу";
      };
   }

   public static boolean m_3_boolean(BlockPos blockPos3) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3);
      return !blockState.isAir()
         && !blockState.canBeReplaced()
         && blockState.isCollisionShapeFullBlock(MinecraftAccess.MINECRAFT.level, blockPos3)
         && !blockState.hasBlockEntity()
         && blockState.getMenuProvider(MinecraftAccess.MINECRAFT.level, blockPos3) == null
         && !(blockState.getBlock() instanceof NoteBlock)
         && !(blockState.getBlock() instanceof RespawnAnchorBlock);
   }

   public static boolean raycastBlocks(Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord c3, boolean flag5) {
      return BlockGetter.traverseBlocks(vec3, vec32, c3, (c32, blockPos3) -> {
         if (blockPos3.equals(c32.m_1a_BlockPos())) {
            return null;
         }

         BlockState blockState = blockPos3.equals(c32.m_1f_BlockPos()) ? Blocks.OBSIDIAN.defaultBlockState() : MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3);
         VoxelShape voxelShape = flag5 ? blockState.getCollisionShape(MinecraftAccess.MINECRAFT.level, blockPos3) : blockState.getShape(MinecraftAccess.MINECRAFT.level, blockPos3);
         return voxelShape.clip(vec3, vec32, blockPos3) != null ? Boolean.TRUE : null;
      }, c33 -> Boolean.FALSE);
   }

   static {
      m_9_void("wqTTA");
   }

   public static void m_9_void(String text2) {
      text = text2;
   }

   public static String m_7_String() {
      return text;
   }

   private static final class StateHolder {
      private Vec3 vec3;
      private float floatValue;
      private int value;
      private String text;
      private float floatValue2;

      private StateHolder() {
         this.text = "";
         this.floatValue2 = Float.NaN;
      }

      public void m_3(Vec3 vec32, float floatValue3) {
         this.floatValue = Mth.clamp(this.floatValue + (vec32 != null ? floatValue3 : -floatValue3) * 6.0F, 0.0F, 1.0F);
         if (vec32 != null) {
            this.vec3 = this.vec3 != null && !(this.floatValue <= 0.0F) ? this.vec3.lerp(vec32, 1.0 - Math.exp(-floatValue3 * 18.0)) : vec32;
         }
      }

      public void m_4(int value9, String text2, float floatValue3) {
         this.value = value9;
         this.text = text2;
         this.floatValue2 = floatValue3;
      }

      public boolean m_5_boolean() {
         return this.vec3 != null && this.floatValue > 0.01F;
      }

      public void reset() {
         this.vec3 = null;
         this.floatValue = 0.0F;
      }
   }

   private final class StateHolder2 implements CrystalDamage.CallbackType {
      @Override
      public CrystalDamage.ModeKind classify(BlockPos blockPos3) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3);
         if (blockState.isAir()) {
            return CrystalDamage.ModeKind.AIR;
         } else if (!blockState.is(Blocks.OBSIDIAN) && !blockState.is(Blocks.BEDROCK)) {
            return blockState.canBeReplaced() ? CrystalDamage.ModeKind.REPLACEABLE : CrystalDamage.ModeKind.SOLID;
         } else {
            return CrystalDamage.ModeKind.BASE;
         }
      }

      @Override
      public boolean m_9_boolean(BlockPos blockPos3) {
         return LegacyCrystalAura.m_3_boolean(blockPos3);
      }

      @Override
      public boolean m_6(Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord c3) {
         return LegacyCrystalAura.raycastBlocks(vec3, vec32, c3, true);
      }

      @Override
      public boolean m_1(Vec3 vec3, Vec3 vec32, CrystalDamage.DataRecord c3) {
         return LegacyCrystalAura.raycastBlocks(vec3, vec32, c3, false);
      }

      @Override
      public boolean m_4(AABB aABB, boolean flag5) {
         for (BlockPos blockPos3 : LegacyCrystalAura.this.blockPosMap.keySet()) {
            if (CrystalDamage.getCrystalBox(CrystalDamage.topCenter(blockPos3)).intersects(aABB)) {
               return true;
            }
         }

         for (Entity entity : MinecraftAccess.MINECRAFT.level.getEntities((Entity)null, aABB)) {
            if (entity.isAlive() && (!(entity instanceof EndCrystal) || !LegacyCrystalAura.this.integerMap.containsKey(entity.getId())) && (flag5 || entity.blocksBuilding)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean m_3(Vec3 vec3, Vec3 vec32, int value9) {
         AABB aABB = new AABB(vec3, vec32).inflate(1.0);

         for (Entity entity : MinecraftAccess.MINECRAFT.level.getEntities(MinecraftAccess.MINECRAFT.player, aABB, entity2 -> entity2.isPickable() && !entity2.isSpectator() && entity2.getId() != value9)) {
            if (entity.getBoundingBox().inflate(entity.getPickRadius()).clip(vec3, vec32).isPresent()) {
               return true;
            }
         }

         return false;
      }

      @Override
      public int m_5_int(BlockPos blockPos3) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos3);
         int value9 = LegacyCrystalAura.this.findBestTool(blockState, blockPos3);
         return value9 < 0 ? -1 : LegacyCrystalAura.this.getToolScore(blockState, blockPos3, MinecraftAccess.MINECRAFT.player.getInventory().getItem(value9));
      }

      @Override
      public boolean m_3_boolean(BlockPos blockPos3) {
         return LegacyCrystalAura.this.blockPosMap3.containsKey(blockPos3) || LegacyCrystalAura.this.blockPosMap.containsKey(blockPos3) || LegacyCrystalAura.isItemNearby(CrystalDamage.topCenter(blockPos3), LegacyCrystalAura.this.itemEntityList);
      }

      @Override
      public boolean m_9_boolean(AABB aABB) {
         for (Entity entity : MinecraftAccess.MINECRAFT.level.getEntities((Entity)null, aABB)) {
            if (entity.isAlive() && entity instanceof LivingEntity) {
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean m_6_boolean(BlockPos blockPos3) {
         return !MinecraftAccess.MINECRAFT.level.getFluidState(blockPos3).isEmpty();
      }
   }
}
