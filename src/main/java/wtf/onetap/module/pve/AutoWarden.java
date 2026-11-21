package wtf.onetap.module.pve;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalNear;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.ReceivePacketEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.setting.TextSetting;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.TimeHelper;

@ModuleInformation(name = "AutoWarden", description = "AutoWarden Bot на сервере SpookyTime ", category = Category.PVE)
public class AutoWarden extends Module {
   private static final Pattern PATTERN = Pattern.compile("анархия\\D*(\\d+)");
   private static final List<BlockPos> BLOCK_POS_LIST = List.of(
      new BlockPos(-2001, -55, -2005),
      new BlockPos(-1987, -55, -2007),
      new BlockPos(-1989, -55, -1982),
      new BlockPos(-2033, -55, -2009),
      new BlockPos(-2033, -55, -1994),
      new BlockPos(-2036, -55, -2015),
      new BlockPos(-2036, -55, -1988),
      new BlockPos(-2005, -54, -2027),
      new BlockPos(-2011, -48, -2042),
      new BlockPos(-2005, -55, -2056),
      new BlockPos(-2045, -55, -2045),
      new BlockPos(-2053, -55, -2042),
      new BlockPos(-2052, -49, -2042),
      new BlockPos(-2052, -55, -1961),
      new BlockPos(-2045, -49, -1961),
      new BlockPos(-2025, -55, -1952),
      new BlockPos(-2008, -55, -1963),
      new BlockPos(-2006, -54, -1954),
      new BlockPos(-1972, -54, -1944),
      new BlockPos(-1970, -55, -1959),
      new BlockPos(-1951, -49, -1948),
      new BlockPos(-1936, -55, -1948),
      new BlockPos(-1961, -48, -1974),
      new BlockPos(-1955, -55, -1995),
      new BlockPos(-1938, -48, -2034),
      new BlockPos(-1944, -55, -2045),
      new BlockPos(-1977, -55, -2056),
      new BlockPos(-1979, -56, -1979),
      new BlockPos(-1952, -48, -1982),
      new BlockPos(-1965, -55, -2008),
      new BlockPos(-1960, -55, -2030)
   );
   private static final List<BlockPos> BLOCK_POS_LIST2 = List.of(
      new BlockPos(-1927, -56, -1935), new BlockPos(-1928, -56, -2069), new BlockPos(-2059, -56, -2069), new BlockPos(-2059, -56, -1935)
   );
   private static final List<Item> ITEM_LIST = List.of(
      Items.TOTEM_OF_UNDYING,
      Items.NETHERITE_HELMET,
      Items.NETHERITE_CHESTPLATE,
      Items.NETHERITE_LEGGINGS,
      Items.NETHERITE_BOOTS,
      Items.NETHERITE_SWORD,
      Items.NETHERITE_PICKAXE,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.SHULKER_BOX,
      Items.NETHERITE_INGOT,
      Items.DRAGON_HEAD,
      Items.ELYTRA,
      Items.SNOWBALL,
      Items.SPLASH_POTION,
      Items.PHANTOM_MEMBRANE,
      Items.TRIPWIRE_HOOK,
      Items.NETHERITE_SCRAP,
      Items.BEACON,
      Items.ENDER_EYE,
      Items.VILLAGER_SPAWN_EGG,
      Items.ZOMBIE_VILLAGER_SPAWN_EGG,
      Items.PAPER,
      Items.FIREWORK_ROCKET,
      Items.DIAMOND,
      Items.GOLDEN_APPLE,
      Items.GOLDEN_CARROT,
      Items.TNT,
      Items.SUGAR,
      Items.WITHER_SKELETON_SKULL,
      Items.DIAMOND_BLOCK,
      Items.IRON_NUGGET,
      Items.DRIED_KELP,
      Items.GUNPOWDER,
      Items.EXPERIENCE_BOTTLE
   );
   private final List<AutoWarden.DataRecord> c7List = new ArrayList<>();
   private final TextSetting storageAnarchy = new TextSetting("Анархия склада", "1").build();
   private final TextSetting wardenAnarchy = new TextSetting("Анархия вардена", "2").build();
   private final FloatSetting resourceLimit = new FloatSetting("Лимит ресурсов", 4.0F, 4.0F, 20.0F, 1.0F).build();
   private AutoWarden.ModeKind c2 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
   private AutoWarden.ModeKind c22 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
   private AutoWarden.ModeKind c23 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
   private AutoWarden.ModeKind3 c5 = AutoWarden.ModeKind3.OPEN;
   private AutoWarden.ModeKind2 c3 = AutoWarden.ModeKind2.FOOD;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private BlockPos blockPos4;
   private int value;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private ClientLevel clientLevel;
   private int value2;
   private BlockPos blockPos5;
   private int value3;
   private String text;
   private AutoWarden.ModeKind c24;
   private int value4 = -1;
   private int value5;
   private int value6;
   private boolean flag4;
   private boolean flag5;
   private int value7 = -1;
   private int value8;
   private boolean flag6;
   private int value9;
   private boolean flag7;
   private boolean flag8;
   private int value10;
   private int value11;
   private int value12;
   private int value13;
   private BlockPos blockPos6;
   private long longValue;
   private boolean flag9;
   private AutoWarden.ModeKind c25;
   private BlockPos blockPos7;
   private int value14;
   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeHelper2 = new TimeHelper();
   private final TimeHelper timeHelper3 = new TimeHelper();
   private final TimeHelper timeHelper4 = new TimeHelper();
   private final TimeHelper timeHelper5 = new TimeHelper();
   private final TimeHelper timeHelper6 = new TimeHelper();
   private boolean flag10;
   private double doubleValue;
   private boolean flag11;
   private boolean flag12;
   private boolean flag13;
   private final List<Block> blockList = new ArrayList<>();
   private long longValue2;
   private int value15;
   private BlockPos blockPos8;
   private long longValue3;
   private long longValue4;

   @Override
   public void onEnable() {
      this.c2 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
      this.resetState();
      this.c7List.clear();
      this.value = 0;
      this.flag = false;
      this.flag2 = false;
      this.value9 = 0;
      this.flag7 = false;
      this.releaseUse();
      this.value10 = 0;
      this.value13 = 0;
      this.blockPos4 = null;
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.blockPos6 = null;
      this.longValue = 0L;
      this.flag9 = false;
      this.value5 = 0;
      this.timeHelper3.reset();
      this.timeHelper5.reset();
      this.saveBaritoneSettings();
      this.value6 = 0;
      this.showInstructions();
   }

   @Override
   public void onDisable() {
      this.cancelPathing();
      this.releaseUse();
      this.longValue4 = 0L;
      if (MinecraftAccess.MINECRAFT.options != null && MinecraftAccess.MINECRAFT.options.keyUp != null) {
         MinecraftAccess.MINECRAFT.options.keyUp.setDown(false);
      }

      Onetap.ROTATION_MODES.reset();
      this.restoreBaritoneSettings();
      this.resetState();
      this.c7List.clear();
      this.c2 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
   }

   private void showInstructions() {
      ChatUtil.log("§fИнструкция:");
      ChatUtil.log(
         "§71. Укажите в настройках свою анархию. Там, где стоят сундуки, оставайтесь на месте: никуда уходить не нужно, просто постоянно стойте возле сундуков — всё остальное модуль сделает автоматически."
      );
      ChatUtil.log(
         "§72. На вашей основной анархии должно быть 2 сундука в радиусе 4 блоков: первый сундук с табличкой «Еда»(туда кладите еду обязательно, а бафы — уже не обязательно: можете положить зелья скорости и невидимости), второй — с табличкой «Склад»."
      );
      ChatUtil.log("§73. На анархии вардена поставьте /sethome warden, чтобы в случае смерти вы могли телепортироваться обратно.");
      ChatUtil.log("§7После того как вы указали свою анархию и анархию вардена, включите модуль обязательно на своей базе — и всё, модуль начнёт работу.");
   }

   private void saveBaritoneSettings() {
      Settings settings = BaritoneAPI.getSettings();
      this.flag10 = settings.allowSprint.value;
      this.doubleValue = settings.randomLooking.value;
      this.flag11 = settings.freeLook.value;
      this.flag12 = settings.allowBreak.value;
      this.flag13 = settings.allowPlace.value;
      settings.allowSprint.value = true;
      settings.randomLooking.value = 2.0;
      settings.freeLook.value = true;
      settings.allowBreak.value = false;
      settings.allowPlace.value = false;
      if (!settings.blocksToAvoid.value.contains(Blocks.SCULK_SHRIEKER)) {
         settings.blocksToAvoid.value.add(Blocks.SCULK_SHRIEKER);
         this.blockList.add(Blocks.SCULK_SHRIEKER);
      }

      for (Block block : BuiltInRegistries.BLOCK) {
         if (block instanceof CandleBlock && !settings.blocksToAvoid.value.contains(block)) {
            settings.blocksToAvoid.value.add(block);
            this.blockList.add(block);
         }
      }
   }

   private void restoreBaritoneSettings() {
      Settings settings = BaritoneAPI.getSettings();
      settings.allowSprint.value = this.flag10;
      settings.randomLooking.value = this.doubleValue;
      settings.freeLook.value = this.flag11;
      settings.allowBreak.value = this.flag12;
      settings.allowPlace.value = this.flag13;
      settings.blocksToAvoid.value.removeAll(this.blockList);
      this.blockList.clear();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         if (this.c2 != AutoWarden.ModeKind.EAT && this.c2 != AutoWarden.ModeKind.DRINK_BUFFS && (this.flag8 || MinecraftAccess.MINECRAFT.options.keyUse.isDown())) {
            this.releaseUse();
         }

         if (MinecraftAccess.MINECRAFT.player.getHealth() <= 0.0F) {
            this.value5++;
         } else {
            this.value5 = 0;
         }

         if ((this.value5 >= 5 || MinecraftAccess.MINECRAFT.gui.screen() instanceof DeathScreen) && !this.flag) {
            this.handleDeath();
         } else if (this.flag && MinecraftAccess.MINECRAFT.player.getHealth() > 0.0F) {
            this.flag = false;
            this.value5 = 0;
            this.value = 0;
            this.value6 = 0;
            this.flag4 = false;
            this.flag9 = false;
            this.blockPos2 = null;
            this.blockPos3 = null;
            this.c2 = AutoWarden.ModeKind.GO_SKLAD;
            this.timeHelper3.reset();
            this.timeHelper5.reset();
         } else if (this.flag) {
            if (this.timeHelper3.hasElapsed(2000L)) {
               MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundClientCommandPacket(Action.PERFORM_RESPAWN));
               this.timeHelper3.reset();
            }
         } else {
            if (!this.flag && (MinecraftAccess.MINECRAFT.player.isPassenger() || MinecraftAccess.MINECRAFT.player.getPose() == Pose.SITTING) && this.timeHelper6.hasElapsed(2000L)) {
               MinecraftAccess.MINECRAFT.player.connection.sendCommand("sit");
               this.timeHelper6.reset();
            }

            if (this.c2 != AutoWarden.ModeKind.EAT
               && this.c2 != AutoWarden.ModeKind.DEATH_RECOVERY
               && this.c2 != AutoWarden.ModeKind.WAIT_TELEPORT
               && this.c2 != AutoWarden.ModeKind.GO_WARDEN_ANARCHY
               && this.c2 != AutoWarden.ModeKind.DEPOSIT
               && this.c2 != AutoWarden.ModeKind.SKLAD_FIND_FOOD
               && this.c2 != AutoWarden.ModeKind.SKLAD_TAKE_FOOD
               && this.c2 != AutoWarden.ModeKind.GO_SKLAD_DEPOSIT
               && this.c2 != AutoWarden.ModeKind.DRINK_BUFFS
               && this.c2 != AutoWarden.ModeKind.GO_WARDEN
               && MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() <= 8
               && this.findFoodSlot() != -1) {
               this.c25 = this.c2;
               this.cancelPathing();
               Onetap.ROTATION_MODES.reset();
               this.c2 = AutoWarden.ModeKind.EAT;
               this.timeHelper3.reset();
            } else {
               switch (this.c2.ordinal()) {
                  case 0:
                     this.handleGoStorage();
                     break;
                  case 1:
                     this.handleStorageFindFood();
                     break;
                  case 2:
                     this.handleGoHome();
                     break;
                  case 3:
                     this.handleWaitTeleport();
                     break;
                  case 4:
                     this.handleStorageTakeFood();
                     break;
                  case 5:
                     this.handleGoWardenAnarchy();
                     break;
                  case 6:
                     this.handleDrinkBuffs();
                     break;
                  case 7:
                     this.handleGoWarden();
                     break;
                  case 8:
                     this.handleWardenSearch();
                     break;
                  case 9:
                     this.handleWardenLoot();
                     break;
                  case 10:
                     this.handleWaitPvp();
                     break;
                  case 11:
                     this.handleGoStorageDeposit();
                     break;
                  case 12:
                     this.handleDeposit();
                     break;
                  case 13:
                     this.handleEat();
                  case 14:
               }
            }
         }
      }
   }

   private void handleDeath() {
      if (!this.flag) {
         this.flag = true;
         this.cancelPathing();
         this.releaseUse();
         this.longValue4 = 0L;
         if (MinecraftAccess.MINECRAFT.options.keyUp != null) {
            MinecraftAccess.MINECRAFT.options.keyUp.setDown(false);
         }

         this.closeContainer();
         Onetap.ROTATION_MODES.reset();
         this.c2 = AutoWarden.ModeKind.DEATH_RECOVERY;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      }
   }

   @EventTarget
   private void onReceivePacket(ReceivePacketEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && !this.flag) {
         String text2 = null;
         if (event.getPacket() instanceof ClientboundSystemChatPacket clientboundSystemChatPacket) {
            text2 = clientboundSystemChatPacket.content().getString();
         } else if (event.getPacket() instanceof ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {
            text2 = clientboundDisguisedChatPacket.message().getString();
         }

         if (text2 != null && text2.toLowerCase(Locale.ROOT).contains("погибли")) {
            this.handleDeath();
         }
      }
   }

   private void handleEat() {
      if (MinecraftAccess.MINECRAFT.player.getFoodData().getFoodLevel() >= 20 || this.findFoodSlot() == -1) {
         this.releaseUse();
         this.moveHotbarToInventory();
         this.c2 = this.c25;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      } else if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         this.closeContainer();
      } else {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getMainHandItem();
         if (itemStack.get(DataComponents.FOOD) == null) {
            this.releaseUse();
            int value16 = this.findFoodSlot();
            if (value16 == -1) {
               this.c2 = this.c25;
            } else {
               Item item = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value16).getItem();
               if (this.selectItem(item)) {
                  ;
               }
            }
         } else {
            if (!MinecraftAccess.MINECRAFT.player.isUsingItem()) {
               this.lookUpIfSlab();
               ((MinecraftAccessor)MinecraftAccess.MINECRAFT).setRightClickDelay(0);
               ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$startUseItem();
            }

            MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
            this.flag8 = true;
         }
      }
   }

   private int findFoodSlot() {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
            if (foodProperties != null) {
               Item item = itemStack.getItem();
               if (item != Items.GOLDEN_APPLE && item != Items.ENCHANTED_GOLDEN_APPLE) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   private void handleGoStorage() {
      if (this.timeHelper3.hasElapsed(500L)) {
         if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
            this.closeContainer();
            this.timeHelper3.reset();
         } else {
            Integer integer = this.getAnarchyNumber();
            if (integer != null ? integer != this.storageAnarchy.getInt() : this.findChestByPurpose("склад") == null) {
               String text2 = "an" + this.storageAnarchy.getInt();
               MinecraftAccess.MINECRAFT.player.connection.sendCommand(text2);
               this.m_3(text2, AutoWarden.ModeKind.SKLAD_FIND_FOOD);
               this.c22 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
               this.c2 = AutoWarden.ModeKind.WAIT_TELEPORT;
               this.flag2 = false;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            } else {
               this.c2 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            }
         }
      }
   }

   private void handleStorageFindFood() {
      if (this.hasInvisibilityAndSpeed()) {
         this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      } else if (this.hasInvisibilityPotion() && this.hasSpeedPotion() && this.countFood() >= 1) {
         this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      } else if (this.blockPos2 != null && MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos2).getBlock() instanceof ChestBlock
         || this.timeHelper5.hasElapsed(this.flag4 ? 1000L : 4000L)) {
         this.flag4 = false;
         if (this.blockPos2 == null || !(MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos2).getBlock() instanceof ChestBlock)) {
            this.blockPos2 = this.findChestByPurpose("еда");
         }

         if (this.blockPos2 != null && this.isSameChest(this.blockPos2, this.blockPos3)) {
            this.blockPos2 = null;
         }

         if (this.blockPos2 != null) {
            this.c2 = AutoWarden.ModeKind.SKLAD_TAKE_FOOD;
            this.c3 = AutoWarden.ModeKind2.FOOD;
            this.resetState();
            this.timeHelper3.reset();
            this.timeHelper5.reset();
         } else {
            if (!this.flag9) {
               this.c23 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
               this.c2 = AutoWarden.ModeKind.GO_HOME;
            } else {
               this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
            }

            this.timeHelper3.reset();
            this.timeHelper5.reset();
         }
      }
   }

   private void handleGoHome() {
      if (this.timeHelper3.hasElapsed(500L)) {
         if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
            this.closeContainer();
            this.timeHelper3.reset();
         } else {
            MinecraftAccess.MINECRAFT.player.connection.sendCommand("home home");
            this.flag9 = true;
            this.blockPos2 = null;
            this.blockPos3 = null;
            this.c22 = this.c23;
            this.c2 = AutoWarden.ModeKind.WAIT_TELEPORT;
            this.flag2 = false;
            this.flag3 = false;
            this.timeHelper3.reset();
            this.timeHelper5.reset();
         }
      }
   }

   private void handleWaitTeleport() {
      if (this.hasBossBar("телепорт")) {
         this.flag2 = true;
         this.timeHelper3.reset();
      } else {
         Integer integer = this.getAnarchyNumber();
         boolean flag14;
         if (this.flag3) {
            flag14 = this.value4 != -1 && integer != null && integer == this.value4
               || MinecraftAccess.MINECRAFT.level != this.clientLevel
               || MinecraftAccess.MINECRAFT.player.getId() != this.value2
               || this.blockPos5 != null && MinecraftAccess.MINECRAFT.player.blockPosition().distSqr(this.blockPos5) > 10000.0;
         } else {
            flag14 = this.flag2 || this.timeHelper5.hasElapsed(3000L);
         }

         if (flag14) {
            this.c2 = this.c22;
            this.c24 = null;
            this.timeHelper3.reset();
            this.timeHelper5.reset();
         } else if (this.flag3 && this.timeHelper5.hasElapsed(10000L)) {
            if (this.value3 < 2) {
               this.value3++;
               MinecraftAccess.MINECRAFT.player.connection.sendCommand(this.text);
               this.timeHelper5.reset();
            } else {
               this.c2 = this.c24 != null ? this.c24 : this.c22;
               this.c24 = null;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            }
         } else {
            if (!this.flag3 && this.timeHelper5.hasElapsed(15000L)) {
               this.c2 = this.c22;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            }
         }
      }
   }

   private void m_3(String text2, AutoWarden.ModeKind c26) {
      this.flag3 = true;
      this.clientLevel = MinecraftAccess.MINECRAFT.level;
      this.value2 = MinecraftAccess.MINECRAFT.player.getId();
      this.blockPos5 = MinecraftAccess.MINECRAFT.player.blockPosition().immutable();
      this.value3 = 0;
      this.text = text2;
      this.c24 = c26;

      try {
         this.value4 = text2.startsWith("an") ? Integer.parseInt(text2.substring(2)) : -1;
      } catch (Exception e) {
         this.value4 = -1;
      }
   }

   private void handleStorageTakeFood() {
      if (this.timeHelper5.hasElapsed(10000L)) {
         this.closeContainer();
         this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      } else if (MinecraftAccess.MINECRAFT.gui.screen() != null && !(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
         this.closeContainer();
         this.timeHelper3.reset();
      } else {
         this.runContainerStateMachine(this.blockPos2, AutoWarden.ModeKind2.FOOD);
      }
   }

   private void handleGoWardenAnarchy() {
      if (this.timeHelper3.hasElapsed(200L)) {
         if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
            this.closeContainer();
            this.timeHelper3.reset();
         } else {
            this.cancelPathing();
            Integer integer = this.getAnarchyNumber();
            if (integer != null && integer == this.wardenAnarchy.getInt()) {
               this.c2 = AutoWarden.ModeKind.DRINK_BUFFS;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            } else {
               String text2 = "an" + this.wardenAnarchy.getInt();
               MinecraftAccess.MINECRAFT.player.connection.sendCommand(text2);
               this.m_3(text2, AutoWarden.ModeKind.SKLAD_FIND_FOOD);
               this.c22 = AutoWarden.ModeKind.DRINK_BUFFS;
               this.c2 = AutoWarden.ModeKind.WAIT_TELEPORT;
               this.flag2 = false;
               this.flag9 = false;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            }
         }
      }
   }

   private void handleDrinkBuffs() {
      if (this.timeHelper5.hasElapsed(1500L)) {
         if (!this.hasInvisibilityAndSpeed() && this.timeHelper5.hasElapsed(15000L)) {
            this.releaseUse();
            this.value9 = 0;
            this.flag7 = false;
            this.moveHotbarToInventory();
            this.c2 = AutoWarden.ModeKind.GO_WARDEN;
            this.timeHelper3.reset();
            this.timeHelper5.reset();
         } else if (this.hasInvisibilityAndSpeed()) {
            if (!this.dropEmptyBottles()) {
               this.moveHotbarToInventory();
               this.c2 = AutoWarden.ModeKind.GO_WARDEN;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            }
         } else {
            int value16 = this.findPotionToDrink();
            if (value16 == -1) {
               if (this.timeHelper5.hasElapsed(3000L)) {
                  if (this.dropEmptyBottles()) {
                     return;
                  }

                  this.moveHotbarToInventory();
                  this.c2 = AutoWarden.ModeKind.GO_WARDEN;
                  this.timeHelper3.reset();
                  this.timeHelper5.reset();
               }
            } else if (this.value9 == 0) {
               Item item = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value16).getItem();
               if (this.selectItem(item)) {
                  this.value9 = 1;
                  this.timeHelper3.reset();
               }
            } else if (this.value9 == 1) {
               if (!MinecraftAccess.MINECRAFT.player.isUsingItem()) {
                  this.lookUpIfSlab();
                  ((MinecraftAccessor)MinecraftAccess.MINECRAFT).setRightClickDelay(0);
                  ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$startUseItem();
               }

               MinecraftAccess.MINECRAFT.options.keyUse.setDown(true);
               this.flag8 = true;
               this.value9 = 2;
               this.timeHelper3.reset();
            } else {
               if (this.value9 == 2) {
                  if (MinecraftAccess.MINECRAFT.player.isUsingItem()) {
                     this.flag7 = true;
                     this.timeHelper3.reset();
                     return;
                  }

                  if (this.flag7) {
                     this.releaseUse();
                     this.flag7 = false;
                     this.value9 = 0;
                     this.timeHelper3.reset();
                     return;
                  }

                  if (this.timeHelper3.hasElapsed(300L)) {
                     this.value9 = 1;
                     this.timeHelper3.reset();
                  }
               }
            }
         }
      }
   }

   private boolean dropEmptyBottles() {
      if (!this.timeHelper4.hasElapsed(100L)) {
         return true;
      }

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.is(Items.GLASS_BOTTLE)) {
            int value16 = i < 9 ? i + 36 : i;
            InventoryUtil.clickSlot(0, value16, 1, ContainerInput.THROW);
            this.timeHelper4.reset();
            return true;
         }
      }

      return false;
   }

   private void handleGoWarden() {
      if (this.timeHelper3.hasElapsed(100L)) {
         if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
            this.closeContainer();
            this.timeHelper3.reset();
         } else {
            Integer integer = this.getAnarchyNumber();
            if (integer != null && integer == this.storageAnarchy.getInt()) {
               this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            } else if (this.isInWardenArea()) {
               this.value = 0;
               this.blockPos4 = null;
               this.blockPos6 = null;
               this.longValue = 0L;
               this.longValue2 = 0L;
               this.value15 = 0;
               this.blockPos8 = null;
               this.c2 = AutoWarden.ModeKind.WARDEN_SEARCH;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            } else {
               MinecraftAccess.MINECRAFT.player.connection.sendCommand("home warden");
               this.c22 = AutoWarden.ModeKind.WARDEN_SEARCH;
               this.c2 = AutoWarden.ModeKind.WAIT_TELEPORT;
               this.flag2 = false;
               this.flag3 = false;
               this.value = 0;
               this.blockPos4 = null;
               this.blockPos6 = null;
               this.longValue = 0L;
               this.longValue2 = 0L;
               this.value15 = 0;
               this.blockPos8 = null;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            }
         }
      }
   }

   private boolean m_1_boolean() {
      if (this.longValue4 > 0L) {
         if (System.currentTimeMillis() >= this.longValue4) {
            if (MinecraftAccess.MINECRAFT.options.keyUp != null) {
               MinecraftAccess.MINECRAFT.options.keyUp.setDown(false);
            }

            this.longValue4 = 0L;
            this.blockPos6 = null;
         }

         this.timeHelper3.reset();
         return true;
      } else if (this.isStuck()) {
         long longValue5 = System.currentTimeMillis();
         if (longValue5 - this.longValue3 > 3000L) {
            this.longValue3 = longValue5;
            this.longValue4 = longValue5 + 250L;
            if (MinecraftAccess.MINECRAFT.options.keyUp != null) {
               MinecraftAccess.MINECRAFT.options.keyUp.setDown(true);
            }
         } else {
            this.cancelPathing();
         }

         this.timeHelper3.reset();
         return true;
      } else {
         return false;
      }
   }

   private void handleWardenSearch() {
      Integer integer = this.getAnarchyNumber();
      if (integer != null && integer == this.storageAnarchy.getInt()) {
         this.cancelPathing();
         this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      } else if (this.findChestByPurpose("склад") != null) {
         this.cancelPathing();
         this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      } else if (!this.m_1_boolean()) {
         BlockPos blockPos9 = this.findNearestUnvisitedChest();
         if (blockPos9 != null) {
            double doubleValue2 = this.distanceTo(blockPos9);
            if (doubleValue2 <= 2.5) {
               this.cancelPathing();
               this.resetState();
               this.value15 = 0;
               this.blockPos8 = null;
               this.blockPos = blockPos9;
               this.blockPos7 = blockPos9;
               this.c2 = AutoWarden.ModeKind.WARDEN_LOOT;
               this.c3 = AutoWarden.ModeKind2.LOOT;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            } else if (this.blockPos4 != null && this.blockPos4.equals(blockPos9) && this.isPathing()) {
               this.timeHelper3.reset();
            } else {
               long longValue5 = System.currentTimeMillis();
               if (longValue5 - this.longValue2 < 2000L) {
                  this.timeHelper3.reset();
               } else {
                  if (!blockPos9.equals(this.blockPos8)) {
                     this.blockPos8 = blockPos9.immutable();
                     this.value15 = 0;
                  }

                  this.value15++;
                  if (this.value15 > 3) {
                     this.markVisited(blockPos9, 120000L);
                     this.cancelPathing();
                     this.value15 = 0;
                     this.blockPos8 = null;
                     this.timeHelper3.reset();
                  } else {
                     this.cancelPathing();
                     this.blockPos4 = blockPos9;
                     this.pathTo(blockPos9);
                     this.longValue2 = longValue5;
                     this.timeHelper3.reset();
                  }
               }
            }
         } else {
            BlockPos blockPos10 = this.findNextChestToRevisit();
            if (blockPos10 != null) {
               if (!this.isPathing() && this.distanceTo(blockPos10) > 5.0) {
                  long longValue6 = System.currentTimeMillis();
                  if (longValue6 - this.longValue2 < 2000L) {
                     this.timeHelper3.reset();
                     return;
                  }

                  this.cancelPathing();
                  this.blockPos4 = blockPos10;
                  this.pathTo(blockPos10);
                  this.longValue2 = longValue6;
               }

               this.timeHelper3.reset();
            } else {
               this.timeHelper3.reset();
            }
         }
      }
   }

   private boolean isStuck() {
      if (!this.isPathing()) {
         return false;
      } else {
         BlockPos blockPos9 = MinecraftAccess.MINECRAFT.player.blockPosition();
         if (this.blockPos6 == null) {
            this.blockPos6 = blockPos9;
            this.longValue = System.currentTimeMillis();
            return false;
         } else if (!blockPos9.equals(this.blockPos6)) {
            this.blockPos6 = blockPos9;
            this.longValue = System.currentTimeMillis();
            return false;
         } else {
            return System.currentTimeMillis() - this.longValue > 4000L;
         }
      }
   }

   private void handleWardenLoot() {
      if (MinecraftAccess.MINECRAFT.gui.screen() != null && !(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
         this.closeContainer();
         this.timeHelper3.reset();
      } else {
         this.runContainerStateMachine(this.blockPos, AutoWarden.ModeKind2.LOOT);
      }
   }

   private void handleWaitPvp() {
      if (!this.hasBossBar("pvp") && this.timeHelper5.hasElapsed(5000L)) {
         this.cancelPathing();
         this.c2 = AutoWarden.ModeKind.GO_SKLAD_DEPOSIT;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      } else if (this.timeHelper5.hasElapsed(120000L)) {
         this.cancelPathing();
         this.c2 = AutoWarden.ModeKind.GO_SKLAD_DEPOSIT;
         this.timeHelper3.reset();
         this.timeHelper5.reset();
      } else if (!this.m_1_boolean()) {
         this.evadeThreat();
      }
   }

   private void evadeThreat() {
      Entity entity = this.findThreat();
      BlockPos blockPos9;
      if (entity != null) {
         blockPos9 = this.findFarthestSpot(entity.blockPosition());
      } else {
         blockPos9 = this.findNearestSafeSpot();
         if (blockPos9 == null) {
            blockPos9 = this.findFarthestSpot(MinecraftAccess.MINECRAFT.player.blockPosition());
         }
      }

      if (entity == null && MinecraftAccess.MINECRAFT.player.blockPosition().distManhattan(blockPos9) <= 2) {
         if (this.isPathing()) {
            this.cancelPathing();
         }

         this.timeHelper3.reset();
      } else if (this.blockPos4 != null && this.blockPos4.equals(blockPos9) && this.isPathing()) {
         this.timeHelper3.reset();
      } else {
         long longValue5 = System.currentTimeMillis();
         if (longValue5 - this.longValue2 < 2000L) {
            this.timeHelper3.reset();
         } else {
            this.cancelPathing();
            this.blockPos4 = blockPos9;
            this.pathTo(blockPos9);
            this.longValue2 = longValue5;
            this.timeHelper3.reset();
         }
      }
   }

   private Entity findThreat() {
      Entity entity = null;
      double doubleValue2 = 256.0;

      for (Entity entity2 : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (this.isThreat(entity2)) {
            double doubleValue3 = entity2.distanceToSqr(MinecraftAccess.MINECRAFT.player);
            if (doubleValue3 < doubleValue2) {
               doubleValue2 = doubleValue3;
               entity = entity2;
            }
         }
      }

      return entity;
   }

   private boolean isThreat(Entity entity) {
      return entity instanceof Warden ? true : entity instanceof Player player && entity != MinecraftAccess.MINECRAFT.player && !player.isSpectator();
   }

   private boolean isAreaSafe(BlockPos blockPos9) {
      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (this.isThreat(entity) && entity.blockPosition().distSqr(blockPos9) <= 256.0) {
            return false;
         }
      }

      return true;
   }

   private BlockPos findNearestSafeSpot() {
      BlockPos blockPos9 = null;
      double doubleValue2 = Double.MAX_VALUE;
      BlockPos blockPos10 = MinecraftAccess.MINECRAFT.player.blockPosition();

      for (BlockPos blockPos11 : BLOCK_POS_LIST2) {
         if (this.isAreaSafe(blockPos11)) {
            double doubleValue3 = blockPos11.distSqr(blockPos10);
            if (doubleValue3 < doubleValue2) {
               doubleValue2 = doubleValue3;
               blockPos9 = blockPos11;
            }
         }
      }

      return blockPos9;
   }

   private BlockPos findFarthestSpot(BlockPos blockPos9) {
      BlockPos blockPos10 = null;
      double doubleValue2 = -1.0;

      for (BlockPos blockPos11 : BLOCK_POS_LIST2) {
         double doubleValue3 = blockPos11.distSqr(blockPos9);
         if (doubleValue3 > doubleValue2) {
            doubleValue2 = doubleValue3;
            blockPos10 = blockPos11;
         }
      }

      return blockPos10;
   }

   private void handleGoStorageDeposit() {
      if (this.timeHelper3.hasElapsed(100L)) {
         if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
            this.closeContainer();
            this.timeHelper3.reset();
         } else if (!this.hasUsableItem()) {
            this.value = 0;
            this.c2 = AutoWarden.ModeKind.WARDEN_SEARCH;
            this.timeHelper3.reset();
            this.timeHelper5.reset();
         } else {
            this.cancelPathing();
            Integer integer = this.getAnarchyNumber();
            if (integer != null && integer == this.storageAnarchy.getInt()) {
               this.c2 = AutoWarden.ModeKind.DEPOSIT;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            } else {
               String text2 = "an" + this.storageAnarchy.getInt();
               MinecraftAccess.MINECRAFT.player.connection.sendCommand(text2);
               this.m_3(text2, AutoWarden.ModeKind.DEPOSIT);
               this.c22 = AutoWarden.ModeKind.DEPOSIT;
               this.c2 = AutoWarden.ModeKind.WAIT_TELEPORT;
               this.flag2 = false;
               this.timeHelper3.reset();
               this.timeHelper5.reset();
            }
         }
      }
   }

   private void handleDeposit() {
      if (this.timeHelper5.hasElapsed(this.value6 > 0 ? 500L : 6000L)) {
         if (!this.hasUsableItem()) {
            this.value = 0;
            this.blockPos3 = null;
            this.c2 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
            this.timeHelper3.reset();
            this.timeHelper5.reset();
         } else {
            if (this.blockPos3 == null || !(MinecraftAccess.MINECRAFT.level.getBlockState(this.blockPos3).getBlock() instanceof ChestBlock)) {
               this.blockPos3 = this.findChestByPurpose("склад");
            }

            if (this.blockPos3 == null) {
               if (!this.flag9 && this.findChestByPurpose("еда") == null) {
                  this.c23 = AutoWarden.ModeKind.DEPOSIT;
                  this.c2 = AutoWarden.ModeKind.GO_HOME;
                  this.timeHelper3.reset();
                  this.timeHelper5.reset();
               } else {
                  if (this.timeHelper5.hasElapsed(10000L)) {
                     this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
                     this.timeHelper3.reset();
                     this.timeHelper5.reset();
                  }
               }
            } else {
               this.runContainerStateMachine(this.blockPos3, AutoWarden.ModeKind2.DEPOSIT);
            }
         }
      }
   }

   private void runContainerStateMachine(BlockPos blockPos9, AutoWarden.ModeKind2 c32) {
      AbstractContainerMenu abstractContainerMenu = this.getOpenContainer();
      switch (this.c5.ordinal()) {
         case 0:
            if (abstractContainerMenu != null) {
               if (this.flag6) {
                  this.c5 = AutoWarden.ModeKind3.ACTION;
                  this.timeHelper.reset();
                  this.timeHelper2.reset();
                  this.value10 = 0;
               } else {
                  this.closeContainer();
                  this.timeHelper.reset();
               }

               return;
            }

            if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
               this.closeContainer();
               this.timeHelper.reset();
               return;
            }

            if (blockPos9 == null) {
               this.resetContainerState(true);
               return;
            }

            if (c32 == AutoWarden.ModeKind2.FOOD && this.isSameChest(blockPos9, this.blockPos3)) {
               this.blockPos2 = null;
               this.resetContainerState(true);
               return;
            }

            if (!(MinecraftAccess.MINECRAFT.level.getBlockState(blockPos9).getBlock() instanceof ChestBlock)) {
               if (c32 == AutoWarden.ModeKind2.FOOD) {
                  this.blockPos2 = null;
               }

               if (c32 == AutoWarden.ModeKind2.DEPOSIT) {
                  this.blockPos3 = null;
               }

               if (c32 == AutoWarden.ModeKind2.LOOT) {
                  this.markVisited(blockPos9, 300000L);
               }

               this.resetContainerState(true);
               return;
            }

            if (!this.timeHelper.hasElapsed(150L)) {
               return;
            }

            this.lookAt(blockPos9);
            BlockHitResult blockHitResult = new BlockHitResult(new Vec3(blockPos9.getX() + 0.5, blockPos9.getY() + 1.0, blockPos9.getZ() + 0.5), Direction.UP, blockPos9, true);
            MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND, blockHitResult);
            this.flag6 = true;
            this.timeHelper.reset();
            this.c5 = AutoWarden.ModeKind3.WAIT_OPEN;
            break;
         case 1:
            if (abstractContainerMenu != null) {
               this.c5 = AutoWarden.ModeKind3.ACTION;
               this.timeHelper.reset();
               this.timeHelper2.reset();
               this.value10 = 0;
               return;
            }

            if (this.timeHelper.hasElapsed(1000L)) {
               this.c5 = AutoWarden.ModeKind3.OPEN;
               this.timeHelper.reset();
            }
            break;
         case 2:
            if (abstractContainerMenu == null) {
               this.c5 = AutoWarden.ModeKind3.OPEN;
               this.timeHelper.reset();
               return;
            }

            if (this.timeHelper2.hasElapsed(15000L)) {
               this.closeContainer();
               this.c5 = AutoWarden.ModeKind3.CLOSE;
               this.timeHelper.reset();
               return;
            }

            if (c32 == AutoWarden.ModeKind2.LOOT && blockPos9 != null) {
               this.lookAt(blockPos9);
            }
            boolean flag14 = switch (c32.ordinal()) {
               case 0 -> this.m_4_boolean(abstractContainerMenu);
               case 1 -> this.m_9(abstractContainerMenu);
               case 2 -> this.m_1_boolean(abstractContainerMenu);
               default -> throw new MatchException(null, null);
            };
            if (flag14) {
               this.closeContainer();
               this.c5 = AutoWarden.ModeKind3.CLOSE;
               this.timeHelper.reset();
            }
            break;
         case 3:
            if (MinecraftAccess.MINECRAFT.gui.screen() == null) {
               this.resetContainerState(false);
            } else if (this.timeHelper.hasElapsed(1000L)) {
               this.closeContainer();
            }
      }
   }

   private boolean m_4_boolean(AbstractContainerMenu abstractContainerMenu) {
      if (this.value10 > 0) {
         if (!this.timeHelper4.hasElapsed(50L)) {
            return false;
         }

         if (this.value10 == 1) {
            InventoryUtil.clickSlot(abstractContainerMenu.containerId, this.value11, 1, ContainerInput.PICKUP);
            this.value10 = 2;
            this.timeHelper4.reset();
            return false;
         }

         if (this.value10 == 2) {
            InventoryUtil.clickSlot(abstractContainerMenu.containerId, this.value12, 1, ContainerInput.PICKUP);
            this.value13--;
            if (this.value13 > 0) {
               this.timeHelper4.reset();
               return false;
            }

            this.value10 = 3;
            this.timeHelper4.reset();
            return false;
         }

         if (this.value10 == 3) {
            if (!abstractContainerMenu.getCarried().isEmpty()) {
               InventoryUtil.clickSlot(abstractContainerMenu.containerId, this.value11, 0, ContainerInput.PICKUP);
            }

            this.value10 = 0;
            this.timeHelper4.reset();
            return false;
         }
      }

      if (!this.timeHelper4.hasElapsed(50L)) {
         return false;
      }

      if (!this.hasInvisibilityPotion()) {
         int value16 = this.m_7(abstractContainerMenu, itemStack -> this.isInvisibilityPotion(itemStack));
         if (value16 != -1) {
            this.m_7(abstractContainerMenu, value16, 1);
            this.timeHelper4.reset();
            return false;
         }
      }

      if (!this.hasSpeedPotion()) {
         int value17 = this.m_7(abstractContainerMenu, itemStack2 -> this.isSpeedPotion(itemStack2));
         if (value17 != -1) {
            this.m_7(abstractContainerMenu, value17, 1);
            this.timeHelper4.reset();
            return false;
         }
      }

      int value18 = this.countFood();
      if (value18 < 4) {
         int value19 = 4 - value18;
         int value20 = this.m_7(abstractContainerMenu, itemStack3 -> {
            FoodProperties foodProperties = itemStack3.get(DataComponents.FOOD);
            if (foodProperties == null) {
               return false;
            }

            Item item = itemStack3.getItem();
            return item != Items.GOLDEN_APPLE && item != Items.ENCHANTED_GOLDEN_APPLE;
         });
         if (value20 != -1) {
            this.m_7(abstractContainerMenu, value20, Math.min(value19, abstractContainerMenu.getSlot(value20).getItem().getCount()));
            this.timeHelper4.reset();
            return false;
         }
      }

      return true;
   }

   private void m_7(AbstractContainerMenu abstractContainerMenu, int value16, int value17) {
      ItemStack itemStack = abstractContainerMenu.getSlot(value16).getItem();
      if (itemStack.getCount() <= value17) {
         InventoryUtil.clickSlot(abstractContainerMenu.containerId, value16, 0, ContainerInput.QUICK_MOVE);
      } else {
         int value18 = this.findEmptyContainerSlot(abstractContainerMenu);
         if (value18 == -1) {
            InventoryUtil.clickSlot(abstractContainerMenu.containerId, value16, 0, ContainerInput.QUICK_MOVE);
         } else {
            this.value11 = value16;
            this.value12 = value18;
            this.value13 = value17;
            this.value10 = 1;
         }
      }
   }

   private boolean m_9(AbstractContainerMenu abstractContainerMenu) {
      if (!this.timeHelper4.hasElapsed(50L)) {
         return false;
      }

      int value16 = abstractContainerMenu.slots.size() - 36;

      for (int i = 0; i < value16; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && this.m_6_boolean(itemStack)) {
            InventoryUtil.clickSlot(abstractContainerMenu.containerId, i, 0, ContainerInput.QUICK_MOVE);
            this.value14++;
            this.timeHelper4.reset();
            return false;
         }
      }

      return true;
   }

   private boolean m_1_boolean(AbstractContainerMenu abstractContainerMenu) {
      if (!this.timeHelper4.hasElapsed(150L)) {
         return false;
      }

      int value16 = abstractContainerMenu.slots.size();

      for (int i = Math.max(0, value16 - 36); i < value16; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && this.m_6_boolean(itemStack)) {
            if (i == this.value7 && itemStack.getCount() >= this.value8) {
               this.flag5 = true;
               return true;
            }

            this.value7 = i;
            this.value8 = itemStack.getCount();
            InventoryUtil.clickSlot(abstractContainerMenu.containerId, i, 0, ContainerInput.QUICK_MOVE);
            this.timeHelper4.reset();
            return false;
         }
      }

      return true;
   }

   private void resetContainerState(boolean flag14) {
      AutoWarden.ModeKind2 c32 = this.c3;
      this.c5 = AutoWarden.ModeKind3.OPEN;
      this.blockPos = null;
      this.blockPos4 = null;
      this.value10 = 0;
      this.value13 = 0;
      this.flag6 = false;
      if (c32 == AutoWarden.ModeKind2.LOOT) {
         if (!flag14 && this.blockPos7 != null) {
            long longValue5 = this.readChestTimerMillis(this.blockPos7);
            if (longValue5 <= 0L) {
               longValue5 = 180000L;
            }

            this.markVisited(this.blockPos7, longValue5);
            this.value = this.value + this.value14;
            this.value14 = 0;
         }

         this.blockPos7 = null;
         this.moveHotbarToInventory();
         if (this.value >= this.resourceLimit.getInt()) {
            this.c2 = AutoWarden.ModeKind.WAIT_PVP;
         } else {
            this.c2 = AutoWarden.ModeKind.WARDEN_SEARCH;
         }
      } else if (c32 == AutoWarden.ModeKind2.FOOD) {
         this.c2 = AutoWarden.ModeKind.GO_WARDEN_ANARCHY;
      } else if (c32 == AutoWarden.ModeKind2.DEPOSIT) {
         this.cancelPathing();
         if (this.hasUsableItem() && !this.flag5 && this.value6 < 2) {
            this.value6++;
            this.c2 = AutoWarden.ModeKind.DEPOSIT;
         } else {
            this.value6 = 0;
            this.value = 0;
            this.flag9 = true;
            this.flag4 = true;
            this.c2 = AutoWarden.ModeKind.SKLAD_FIND_FOOD;
         }
      }

      this.timeHelper3.reset();
      this.timeHelper5.reset();
   }

   private void resetState() {
      this.c5 = AutoWarden.ModeKind3.OPEN;
      this.blockPos = null;
      this.blockPos7 = null;
      this.value10 = 0;
      this.value13 = 0;
      this.value14 = 0;
      this.flag5 = false;
      this.value7 = -1;
      this.flag6 = false;
      this.timeHelper.reset();
      this.timeHelper2.reset();
      this.timeHelper4.reset();
   }

   private void pathTo(BlockPos blockPos9) {
      BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalNear(blockPos9, 1));
   }

   private void cancelPathing() {
      BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      this.blockPos4 = null;
   }

   private boolean isPathing() {
      return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
   }

   private Integer getAnarchyNumber() {
      try {
         Objective objective = MinecraftAccess.MINECRAFT.level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
         if (objective == null) {
            return null;
         }

         Matcher matcher = PATTERN.matcher(normalize(objective.getDisplayName().getString()));
         if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
         }
      } catch (Exception e) {
      }

      return null;
   }

   private boolean hasBossBar(String text2) {
      if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null) {
         try {
            Map map = ((BossHealthOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getBossOverlay()).onetap$getEvents();

            for (LerpingBossEvent event : (Iterable<LerpingBossEvent>)map.values()) {
               String text3 = event.getName().getString().toLowerCase(Locale.ROOT);
               if (text3.contains(text2.toLowerCase())) {
                  return true;
               }
            }
         } catch (Exception e) {
         }

         return false;
      } else {
         return false;
      }
   }

   private BlockPos findNextChestToRevisit() {
      BlockPos blockPos9 = null;
      long longValue5 = Long.MAX_VALUE;
      long longValue6 = System.currentTimeMillis();

      for (AutoWarden.DataRecord c7 : this.c7List) {
         if (c7.f_2c > longValue6 && c7.f_2c < longValue5) {
            longValue5 = c7.f_2c;
            blockPos9 = c7.f_0_s;
         }
      }

      return blockPos9;
   }

   private BlockPos findNearestUnvisitedChest() {
      BlockPos blockPos9 = null;
      double doubleValue2 = Double.MAX_VALUE;
      BlockPos blockPos10 = MinecraftAccess.MINECRAFT.player.blockPosition();

      for (BlockPos blockPos11 : BLOCK_POS_LIST) {
         if (!this.isChestOnCooldown(blockPos11)) {
            double doubleValue3 = blockPos11.distSqr(blockPos10);
            if (doubleValue3 < doubleValue2) {
               doubleValue2 = doubleValue3;
               blockPos9 = blockPos11;
            }
         }
      }

      return blockPos9;
   }

   private boolean isChestOnCooldown(BlockPos blockPos9) {
      long longValue5 = System.currentTimeMillis();

      for (AutoWarden.DataRecord c7 : this.c7List) {
         if (this.isSameChest(c7.f_0_s, blockPos9) && c7.f_2c > longValue5) {
            return true;
         }
      }

      return false;
   }

   private void markVisited(BlockPos blockPos9, long longValue5) {
      long longValue6 = System.currentTimeMillis() + longValue5;
      this.c7List.removeIf(c7 -> this.isSameChest(c7.f_0_s, blockPos9));
      this.c7List.add(new AutoWarden.DataRecord(blockPos9, longValue6));
   }

   private long readChestTimerMillis(BlockPos blockPos9) {
      try {
         for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
            if (entity instanceof ArmorStand armorStand && armorStand.blockPosition().distManhattan(blockPos9) <= 3) {
               String text2 = armorStand.getCustomName() != null ? armorStand.getCustomName().getString() : "";
               String text3 = text2.replaceAll("[^0-9:]", "").trim();
               if (text3.contains(":")) {
                  String[] stringArray = text3.split(":");
                  if (stringArray.length == 2) {
                     return (Integer.parseInt(stringArray[0]) * 60 + Integer.parseInt(stringArray[1])) * 1000L;
                  }
               }

               String text4 = text3.replaceAll("[^0-9]", "");
               if (!text4.isEmpty()) {
                  return Integer.parseInt(text4) * 1000L;
               }
            }
         }
      } catch (Exception e) {
      }

      return 180000L;
   }

   private boolean isSameChest(BlockPos blockPos9, BlockPos blockPos10) {
      if (blockPos9 == null || blockPos10 == null) {
         return false;
      } else {
         return blockPos9.equals(blockPos10) ? true : this.normalizeChestPos(blockPos9).equals(blockPos10) || this.normalizeChestPos(blockPos10).equals(blockPos9);
      }
   }

   private BlockPos normalizeChestPos(BlockPos blockPos9) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos9);
      return !(blockState.getBlock() instanceof ChestBlock) ? blockPos9 : blockPos9.relative(ChestBlock.getConnectedDirection(blockState));
   }

   private BlockPos findChestByPurpose(String text2) {
      BlockPos blockPos9 = text2.equals("еда") ? this.blockPos3 : (text2.equals("склад") ? this.blockPos2 : null);
      BlockPos blockPos10 = MinecraftAccess.MINECRAFT.player.blockPosition();
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
      BlockPos blockPos11 = null;
      double doubleValue2 = Double.MAX_VALUE;

      for (int i = -6; i <= 6; i++) {
         for (int i2 = -3; i2 <= 3; i2++) {
            for (int i3 = -6; i3 <= 6; i3++) {
               BlockPos blockPos12 = blockPos10.offset(i, i2, i3);
               if (MinecraftAccess.MINECRAFT.level.getBlockState(blockPos12).getBlock() instanceof ChestBlock
                  && (blockPos9 == null || !this.isSameChest(blockPos12, blockPos9))
                  && !(Vec3.atCenterOf(blockPos12).distanceTo(vec3) > 8.0)
                  && this.m_0(blockPos12, text2)) {
                  double doubleValue3 = Vec3.atCenterOf(blockPos12).distanceToSqr(vec3);
                  if (doubleValue3 < doubleValue2) {
                     doubleValue2 = doubleValue3;
                     blockPos11 = blockPos12.immutable();
                  }
               }
            }
         }
      }

      return blockPos11;
   }

   private boolean m_0(BlockPos blockPos9, String text2) {
      BlockPos[] blockPosArray = new BlockPos[]{
         blockPos9.above(),
         blockPos9.north(),
         blockPos9.south(),
         blockPos9.east(),
         blockPos9.west(),
         blockPos9.above().north(),
         blockPos9.above().south(),
         blockPos9.above().east(),
         blockPos9.above().west()
      };

      for (BlockPos blockPos10 : blockPosArray) {
         BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos10);
         if (MinecraftAccess.MINECRAFT.level.getBlockEntity(blockPos10) instanceof SignBlockEntity signBlockEntity) {
            BlockPos blockPos11;
            if (blockState.getBlock() instanceof WallSignBlock) {
               blockPos11 = blockPos10.relative(blockState.getValue(WallSignBlock.FACING).getOpposite());
            } else if (blockState.getBlock() instanceof WallHangingSignBlock) {
               blockPos11 = blockPos10.relative(blockState.getValue(WallHangingSignBlock.FACING).getOpposite());
            } else if (blockState.getBlock() instanceof CeilingHangingSignBlock) {
               blockPos11 = blockPos10.above();
            } else {
               blockPos11 = blockPos10.below();
            }

            if (blockPos11.equals(blockPos9)) {
               if (this.signContains(signBlockEntity.getFrontText(), text2)) {
                  return true;
               }

               if (this.signContains(signBlockEntity.getBackText(), text2)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean signContains(SignText signText, String text2) {
      if (signText == null) {
         return false;
      }

      for (Component component : signText.getMessages(false)) {
         if (component != null) {
            for (String text3 : normalize(component.getString()).split("[^а-яa-z0-9]+")) {
               if (text3.startsWith(text2)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static String normalize(String text2) {
      return text2 == null ? "" : text2.replaceAll("§.", "").replace('ё', 'е').trim().toLowerCase(Locale.ROOT);
   }

   private boolean m_6_boolean(ItemStack itemStack) {
      Item item = itemStack.getItem();

      for (Item item2 : ITEM_LIST) {
         if (item == item2) {
            return true;
         }
      }

      if (this.isWandPiece(itemStack)) {
         return true;
      } else {
         return item instanceof BlockItem blockItem ? blockItem.getBlock() instanceof ShulkerBoxBlock : false;
      }
   }

   private boolean isWandPiece(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return false;
      }

      String text2 = normalize(itemStack.getHoverName().getString());
      return text2.contains("кусоч") && (text2.contains("жезл") || text2.contains("жезел"));
   }

   private boolean hasUsableItem() {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty() && this.m_6_boolean(itemStack)) {
            return true;
         }
      }

      return false;
   }

   private boolean hasPotionEffect(ItemStack itemStack, Holder<MobEffect> holder) {
      if (itemStack.isEmpty()) {
         return false;
      }

      PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
      if (potionContents == null) {
         return false;
      }

      for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
         if (mobEffectInstance.is(holder)) {
            return true;
         }
      }

      return false;
   }

   private boolean isInvisibilityPotion(ItemStack itemStack) {
      return this.hasPotionEffect(itemStack, MobEffects.INVISIBILITY);
   }

   private boolean isSpeedPotion(ItemStack itemStack) {
      return this.hasPotionEffect(itemStack, MobEffects.SPEED);
   }

   private boolean hasPotionInInventory(Holder<MobEffect> holder) {
      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty() && this.hasPotionEffect(itemStack, holder)) {
            return true;
         }
      }

      return false;
   }

   private boolean hasInvisibilityPotion() {
      return this.hasPotionInInventory(MobEffects.INVISIBILITY);
   }

   private boolean hasSpeedPotion() {
      return this.hasPotionInInventory(MobEffects.SPEED);
   }

   private int findPotionToDrink() {
      boolean flag14 = MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.INVISIBILITY);
      boolean flag15 = MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.SPEED);

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            if (!flag14 && this.isInvisibilityPotion(itemStack)) {
               return i;
            }

            if (!flag15 && this.isSpeedPotion(itemStack)) {
               return i;
            }
         }
      }

      return -1;
   }

   private boolean hasInvisibilityAndSpeed() {
      return MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.INVISIBILITY) && MinecraftAccess.MINECRAFT.player.hasEffect(MobEffects.SPEED);
   }

   private int countFood() {
      int value16 = 0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
            if (foodProperties != null) {
               Item item = itemStack.getItem();
               if (item != Items.GOLDEN_APPLE && item != Items.ENCHANTED_GOLDEN_APPLE) {
                  value16 += itemStack.getCount();
               }
            }
         }
      }

      return value16;
   }

   private int m_7(AbstractContainerMenu abstractContainerMenu, Predicate<ItemStack> predicate) {
      int value16 = abstractContainerMenu.slots.size() - 36;

      for (int i = 0; i < value16; i++) {
         ItemStack itemStack = abstractContainerMenu.getSlot(i).getItem();
         if (!itemStack.isEmpty() && predicate.test(itemStack)) {
            return i;
         }
      }

      return -1;
   }

   private int findEmptyContainerSlot(AbstractContainerMenu abstractContainerMenu) {
      int value16 = abstractContainerMenu.slots.size();

      for (int i = Math.max(0, value16 - 36); i < value16; i++) {
         if (!abstractContainerMenu.getSlot(i).hasItem()) {
            return i;
         }
      }

      return -1;
   }

   private AbstractContainerMenu getOpenContainer() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return null;
      } else {
         AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
         if (abstractContainerMenu == null || abstractContainerMenu.containerId == 0) {
            return null;
         } else if (!(MinecraftAccess.MINECRAFT.gui.screen() instanceof ContainerScreen)) {
            return null;
         } else {
            return abstractContainerMenu.slots.size() <= 36 ? null : abstractContainerMenu;
         }
      }
   }

   private void closeContainer() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.containerMenu != null && MinecraftAccess.MINECRAFT.player.containerMenu.containerId != 0) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.gui.screen().onClose();
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }
   }

   private boolean selectItem(Item item) {
      if (item == null) {
         return false;
      }

      if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(item)) {
         return true;
      }

      int value16 = InventoryUtil.findHotbarSlot(item);
      if (value16 != -1) {
         MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value16);
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value16));
         return true;
      }

      int value17 = InventoryUtil.findItemSlotInRange(item, 9, 36);
      if (value17 == -1) {
         return false;
      }

      int value18 = InventoryUtil.findEmptyHotbarSlot();
      if (value18 == -1) {
         return false;
      }

      InventoryUtil.clickSlot(0, value17, value18, ContainerInput.SWAP);
      MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value18);
      MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value18));
      return false;
   }

   private void lookAt(BlockPos blockPos9) {
      Rotations rotations = Onetap.ROTATION_MODES.getRotationTo(blockPos9);
      Onetap.ROTATION_MODES.rotate(rotations, false, 10);
   }

   private double distanceTo(BlockPos blockPos9) {
      return Vec3.atCenterOf(blockPos9).distanceTo(MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F));
   }

   private boolean isInWardenArea() {
      BlockPos blockPos9 = MinecraftAccess.MINECRAFT.player.blockPosition();
      return blockPos9.getX() >= -2060 && blockPos9.getX() <= -1924 && blockPos9.getZ() >= -2068 && blockPos9.getZ() <= -1937;
   }

   private void moveHotbarToInventory() {
      for (int i = 0; i < 9; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty()) {
            if (itemStack.get(DataComponents.EQUIPPABLE) != null) {
               int value16 = this.findFreeInventorySlot();
               if (value16 != -1) {
                  InventoryUtil.clickSlot(0, i + 36, 0, ContainerInput.PICKUP);
                  InventoryUtil.clickSlot(0, value16, 0, ContainerInput.PICKUP);
               }
            } else {
               InventoryUtil.clickSlot(0, i + 36, 0, ContainerInput.QUICK_MOVE);
            }
         }
      }

      int value17 = InventoryUtil.findEmptyHotbarSlot();
      if (value17 != -1 && MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != value17) {
         MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value17);
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value17));
      }
   }

   private int findFreeInventorySlot() {
      for (int i = 9; i <= 35; i++) {
         if (!MinecraftAccess.MINECRAFT.player.inventoryMenu.getSlot(i).hasItem()) {
            return i;
         }
      }

      return -1;
   }

   private void lookUpIfSlab() {
      if (MinecraftAccess.MINECRAFT.hitResult instanceof BlockHitResult blockHitResult) {
         Block block = MinecraftAccess.MINECRAFT.level.getBlockState(blockHitResult.getBlockPos()).getBlock();
         if (block instanceof SlabBlock || block instanceof StairBlock) {
            MinecraftAccess.MINECRAFT.player.setXRot(-90.0F);
         }
      }
   }

   private void releaseUse() {
      this.flag8 = false;
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         MinecraftAccess.MINECRAFT.gameMode.releaseUsingItem(MinecraftAccess.MINECRAFT.player);
      }

      if (MinecraftAccess.MINECRAFT.options != null && MinecraftAccess.MINECRAFT.options.keyUse != null) {
         MinecraftAccess.MINECRAFT.options.keyUse.setDown(false);
      }
   }

   private enum ModeKind {
      GO_SKLAD,
      SKLAD_FIND_FOOD,
      GO_HOME,
      WAIT_TELEPORT,
      SKLAD_TAKE_FOOD,
      GO_WARDEN_ANARCHY,
      DRINK_BUFFS,
      GO_WARDEN,
      WARDEN_SEARCH,
      WARDEN_LOOT,
      WAIT_PVP,
      GO_SKLAD_DEPOSIT,
      DEPOSIT,
      EAT,
      DEATH_RECOVERY;

      private static String a(byte[] byteArray) {
         int value16 = 0;
         int value17;
         char[] charArray = new char[value17 = byteArray.length];

         for (int i = 0; i < value17; i++) {
            int value18;
            if ((value18 = 255 & byteArray[i]) < 192) {
               charArray[value16++] = (char)value18;
            } else if (value18 < 224) {
               char ch = (char)((char)(value18 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value16++] = ch;
            } else if (i < value17 - 2) {
               char ch2 = (char)((char)(value18 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value16++] = ch2;
            }
         }

         return new String(charArray, 0, value16);
      }
   }

   private enum ModeKind2 {
      FOOD,
      LOOT,
      DEPOSIT;

      private static String a(byte[] byteArray) {
         int value16 = 0;
         int value17;
         char[] charArray = new char[value17 = byteArray.length];

         for (int i = 0; i < value17; i++) {
            int value18;
            if ((value18 = 255 & byteArray[i]) < 192) {
               charArray[value16++] = (char)value18;
            } else if (value18 < 224) {
               char ch = (char)((char)(value18 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value16++] = ch;
            } else if (i < value17 - 2) {
               char ch2 = (char)((char)(value18 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value16++] = ch2;
            }
         }

         return new String(charArray, 0, value16);
      }
   }

   private enum ModeKind3 {
      OPEN,
      WAIT_OPEN,
      ACTION,
      CLOSE;

      private static String a(byte[] byteArray) {
         int value16 = 0;
         int value17;
         char[] charArray = new char[value17 = byteArray.length];

         for (int i = 0; i < value17; i++) {
            int value18;
            if ((value18 = 255 & byteArray[i]) < 192) {
               charArray[value16++] = (char)value18;
            } else if (value18 < 224) {
               char ch = (char)((char)(value18 & 31) << 6);
               byte byteValue = byteArray[++i];
               ch = (char)(ch | (char)(byteValue & 63));
               charArray[value16++] = ch;
            } else if (i < value17 - 2) {
               char ch2 = (char)((char)(value18 & 15) << '\f');
               byte byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
               byteValue2 = byteArray[++i];
               ch2 = (char)(ch2 | (char)(byteValue2 & 63));
               charArray[value16++] = ch2;
            }
         }

         return new String(charArray, 0, value16);
      }
   }

   private record DataRecord(BlockPos f_0_s, long f_2c) {
      public BlockPos m_0_s() {
         return this.f_0_s;
      }

      public long m_2c_long() {
         return this.f_2c;
      }
   }
}
