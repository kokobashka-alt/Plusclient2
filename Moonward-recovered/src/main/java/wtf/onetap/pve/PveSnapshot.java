package wtf.onetap.pve;

import baritone.api.BaritoneAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import wtf.onetap.Onetap;
import wtf.onetap.mixin.render.BossHealthOverlayAccessor;
import wtf.onetap.mixin.render.PlayerTabOverlayAccessor;
import wtf.onetap.util.BlockTypeUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

public class PveSnapshot implements MinecraftAccess {
   private final Map<ItemStack, List<String>> itemStackMap;
   private int value;
   private Object object;
   private Integer integer;
   private Integer integer2;

   public PveSnapshot() {
      this.itemStackMap = new WeakHashMap<>();
   }

   public void reset() {
      this.integer = null;
      this.integer2 = null;
   }

   public JsonObject m_4_JsonObject(JsonObject jsonObject) {
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.add("self", this.m_1_JsonObject());
      if (getBoolean(jsonObject, "inv")) {
         JsonArray jsonArray = this.m_8_JsonArray(getBoolean(jsonObject, "tip"));
         int value2 = jsonArray.toString().hashCode();
         jsonObject2.addProperty("invh", value2);
         if (this.integer == null || this.integer != value2) {
            jsonObject2.add("inv", jsonArray);
            this.integer = value2;
         }
      }

      if (getBoolean(jsonObject, "scr")) {
         JsonObject jsonObject3 = this.m_7_JsonObject(getBoolean(jsonObject, "tip"));
         int value3 = jsonObject3.toString().hashCode();
         jsonObject2.addProperty("scrh", value3);
         if (this.integer2 == null || this.integer2 != value3) {
            jsonObject2.add("scr", jsonObject3);
            this.integer2 = value3;
         }
      }

      if (getBoolean(jsonObject, "sb")) {
         jsonObject2.add("sb", this.m_4_JsonObject());
      }

      if (getBoolean(jsonObject, "bb")) {
         jsonObject2.add("bb", this.m_2_JsonArray());
      }

      if (getBoolean(jsonObject, "pth")) {
         jsonObject2.addProperty("pth", this.isBaritonePathing());
      }

      var tagKeyList = toTagList(jsonObject.getAsJsonArray("tags"));
      var stringList = toStringList(jsonObject.getAsJsonArray("props"));
      boolean flag = getBoolean(jsonObject, "look");
      if (jsonObject.has("ray") && !jsonObject.get("ray").isJsonNull()) {
         JsonObject jsonObject4 = this.m_3(jsonObject.get("ray").getAsDouble(), tagKeyList, stringList, flag);
         if (jsonObject4 != null) {
            jsonObject2.add("ray", jsonObject4);
         }
      }

      if (jsonObject.has("bl")) {
         jsonObject2.add("bl", this.m_4(jsonObject.getAsJsonArray("bl"), tagKeyList, stringList, flag));
      }

      if (jsonObject.has("at")) {
         JsonArray jsonArray2 = new JsonArray();

         for (JsonElement jsonElement : jsonObject.getAsJsonArray("at")) {
            JsonArray jsonArray3 = jsonElement.getAsJsonArray();
            BlockPos blockPos = new BlockPos(jsonArray3.get(0).getAsInt(), jsonArray3.get(1).getAsInt(), jsonArray3.get(2).getAsInt());
            jsonArray2.add(this.m_3(blockPos, MinecraftAccess.MINECRAFT.level.getBlockState(blockPos), tagKeyList, stringList, flag));
         }

         jsonObject2.add("at", jsonArray2);
      }

      if (jsonObject.has("sg") && jsonObject.get("sg").isJsonArray()) {
         jsonObject2.add("sg", this.m_1_JsonArray(jsonObject.getAsJsonArray("sg")));
      }

      if (jsonObject.has("en") && jsonObject.get("en").isJsonObject()) {
         jsonObject2.add("en", this.m_7_JsonArray(jsonObject.getAsJsonObject("en")));
      }

      if (flag) {
         JsonObject jsonObject5 = this.m_8_JsonObject();
         if (jsonObject5 != null) {
            jsonObject2.add("look", jsonObject5);
         }
      }

      if (getBoolean(jsonObject, "tab")) {
         jsonObject2.add("tab", this.m_7_JsonObject());
      }

      return jsonObject2;
   }

   private JsonObject m_1_JsonObject() {
      LocalPlayer localPlayer = MinecraftAccess.MINECRAFT.player;
      if (MinecraftAccess.MINECRAFT.level != this.object) {
         this.object = MinecraftAccess.MINECRAFT.level;
         this.value++;
      }

      Rotations rotations = Onetap.ROTATION_MODES.toRotations();
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", localPlayer.getX());
      jsonObject.addProperty("y", localPlayer.getY());
      jsonObject.addProperty("z", localPlayer.getZ());
      jsonObject.addProperty("eh", localPlayer.getEyeHeight());
      jsonObject.addProperty("vx", localPlayer.getDeltaMovement().x);
      jsonObject.addProperty("vz", localPlayer.getDeltaMovement().z);
      jsonObject.addProperty("yaw", localPlayer.getYRot());
      jsonObject.addProperty("pitch", localPlayer.getXRot());
      jsonObject.addProperty("sy", rotations.getFloatValue());
      jsonObject.addProperty("sp", rotations.getFloatValue2());
      jsonObject.addProperty("cy", Onetap.ROTATION_MODES.getRenderYaw());
      jsonObject.addProperty("cp", Onetap.ROTATION_MODES.getRenderPitch());
      jsonObject.addProperty("free", Onetap.ROTATION_MODES.isFlag5());
      jsonObject.addProperty("og", localPlayer.onGround());
      jsonObject.addProperty("hc", localPlayer.horizontalCollision);
      jsonObject.addProperty("hp", localPlayer.getHealth());
      jsonObject.addProperty("food", localPlayer.getFoodData().getFoodLevel());
      jsonObject.addProperty("sel", localPlayer.getInventory().getSelectedSlot());
      jsonObject.addProperty("use", localPlayer.isUsingItem());
      jsonObject.addProperty("ride", localPlayer.isPassenger() || localPlayer.getPose() == Pose.SITTING);
      jsonObject.addProperty("id", localPlayer.getId());
      jsonObject.addProperty("lvl", this.value);
      jsonObject.addProperty("tc", localPlayer.tickCount);
      jsonObject.addProperty("dim", MinecraftAccess.MINECRAFT.level.dimension().toString());
      JsonArray jsonArray = new JsonArray();

      for (MobEffectInstance mobEffectInstance : localPlayer.getActiveEffects()) {
         jsonArray.add(mobEffectInstance.getEffect().getRegisteredName());
      }

      jsonObject.add("fx", jsonArray);
      jsonObject.addProperty("name", localPlayer.getName().getString());
      jsonObject.addProperty("dead", MinecraftAccess.MINECRAFT.gui.screen() instanceof DeathScreen);
      jsonObject.addProperty("menu", localPlayer.containerMenu == null ? 0 : localPlayer.containerMenu.containerId);
      return jsonObject;
   }

   private JsonArray m_8_JsonArray(boolean flag) {
      JsonArray jsonArray = new JsonArray();

      for (int i = 0; i < 41; i++) {
         jsonArray.add(this.m_6(MinecraftAccess.MINECRAFT.player.getInventory().getItem(i), flag));
      }

      return jsonArray;
   }

   private JsonObject m_7_JsonObject() {
      JsonObject jsonObject = new JsonObject();
      JsonArray jsonArray = new JsonArray();
      if (MinecraftAccess.MINECRAFT.getConnection() != null) {
         for (PlayerInfo playerInfo : MinecraftAccess.MINECRAFT.getConnection().getOnlinePlayers()) {
            jsonArray.add(playerInfo.getProfile().name());
         }
      }

      jsonObject.add("names", jsonArray);

      try {
         Component component = ((PlayerTabOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getTabList()).onetap$getHeader();
         if (component != null) {
            jsonObject.addProperty("header", component.getString());
         }
      } catch (Exception e) {
      }

      return jsonObject;
   }

   private JsonObject m_7_JsonObject(boolean flag) {
      JsonObject jsonObject = new JsonObject();
      Screen screen = MinecraftAccess.MINECRAFT.gui.screen();
      String text = screen == null
         ? "none"
         : (screen instanceof ContainerScreen ? "container" : (screen instanceof CraftingScreen ? "crafting" : (screen instanceof DeathScreen ? "death" : "other")));
      jsonObject.addProperty("k", text);
      if (screen != null) {
         jsonObject.addProperty("cls", screen.getClass().getSimpleName());
         jsonObject.addProperty("t", screen.getTitle().getString());
      }

      AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
      if (abstractContainerMenu != null) {
         if (screen != null) {
            jsonObject.addProperty("id", abstractContainerMenu.containerId);
            JsonArray jsonArray = new JsonArray();

            for (Slot slot : abstractContainerMenu.slots) {
               jsonArray.add(this.m_6(slot.getItem(), flag && jsonArray.size() < abstractContainerMenu.slots.size() - 36));
            }

            jsonObject.add("s", jsonArray);
         }

         JsonElement jsonElement = this.m_6(abstractContainerMenu.getCarried(), false);
         if (!jsonElement.isJsonNull()) {
            jsonObject.add("c", jsonElement);
         }
      }

      return jsonObject;
   }

   private JsonElement m_6(ItemStack itemStack, boolean flag) {
      if (itemStack != null && !itemStack.isEmpty()) {
         JsonObject jsonObject = new JsonObject();
         Item item = itemStack.getItem();
         jsonObject.addProperty("i", BuiltInRegistries.ITEM.getKey(item).toString());
         jsonObject.addProperty("c", itemStack.getCount());
         String text = itemStack.getHoverName().getString();
         jsonObject.addProperty("n", text);
         ItemLore itemLore = itemStack.get(DataComponents.LORE);
         if (itemLore != null && !itemLore.lines().isEmpty()) {
            JsonArray jsonArray = new JsonArray();

            for (Component component : itemLore.lines()) {
               jsonArray.add(component.getString());
            }

            jsonObject.add("l", jsonArray);
         }

         if (itemStack.isDamageableItem()) {
            jsonObject.addProperty("d", itemStack.getDamageValue());
            jsonObject.addProperty("m", itemStack.getMaxDamage());
         }

         FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
         if (foodProperties != null) {
            jsonObject.addProperty("f", foodProperties.nutrition());
         }

         jsonObject.addProperty("ms", itemStack.getMaxStackSize());
         JsonArray jsonArray2 = new JsonArray();
         m_4(jsonArray2, itemStack.getEnchantments());
         m_4(jsonArray2, itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));
         if (!jsonArray2.isEmpty()) {
            jsonObject.add("e", jsonArray2);
         }

         PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
         if (potionContents != null) {
            JsonArray jsonArray3 = new JsonArray();
            JsonArray jsonArray4 = new JsonArray();

            for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
               jsonArray3.add(mobEffectInstance.getEffect().getRegisteredName());
               jsonArray4.add(mobEffectInstance.getEffect().getRegisteredName() + "|" + mobEffectInstance.getAmplifier() + "|" + mobEffectInstance.getDuration());
            }

            if (!jsonArray3.isEmpty()) {
               jsonObject.add("p", jsonArray3);
            }

            if (!jsonArray4.isEmpty()) {
               jsonObject.add("pe", jsonArray4);
            }

            potionContents.potion().ifPresent(holder -> jsonObject.addProperty("po", holder.getRegisteredName()));
            potionContents.customColor().ifPresent(integer3 -> jsonObject.addProperty("pc", integer3));
         }

         JsonArray jsonArray5 = new JsonArray();
         m_5(jsonArray5, itemStack.getEnchantments());
         m_5(jsonArray5, itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));
         if (!jsonArray5.isEmpty()) {
            jsonObject.add("el", jsonArray5);
         }

         JsonArray jsonArray6 = new JsonArray();

         for (Entry entry : itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
            jsonArray6.add(entry.attribute().getRegisteredName() + "|" + entry.modifier().operation().name() + "|" + entry.modifier().amount());
         }

         if (!jsonArray6.isEmpty()) {
            jsonObject.add("am", jsonArray6);
         }

         if (itemStack.hasFoil()) {
            jsonObject.addProperty("g", true);
         }

         if (itemStack.get(DataComponents.EQUIPPABLE) != null) {
            jsonObject.addProperty("eq", true);
         }

         if (!text.equals(new ItemStack(item).getHoverName().getString())) {
            jsonObject.addProperty("vn", false);
         }

         if (flag) {
            JsonArray jsonArray7 = new JsonArray();

            for (String text2 : this.m_7_List(itemStack)) {
               jsonArray7.add(text2);
            }

            jsonObject.add("tip", jsonArray7);
         }

         return jsonObject;
      } else {
         return JsonNull.INSTANCE;
      }
   }

   private List<String> m_7_List(ItemStack itemStack) {
      return this.itemStackMap.computeIfAbsent(itemStack, itemStack2 -> {
         ArrayList list = new ArrayList();

         try {
            for (Component component : itemStack2.getTooltipLines(TooltipContext.of(MinecraftAccess.MINECRAFT.level), MinecraftAccess.MINECRAFT.player, TooltipFlag.NORMAL)) {
               list.add(component.getString());
            }
         } catch (Exception e) {
         }

         return list;
      });
   }

   private static void m_5(JsonArray jsonArray, ItemEnchantments itemEnchantments) {
      if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
         for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry entry : itemEnchantments.entrySet()) {
            jsonArray.add(((Holder)entry.getKey()).getRegisteredName() + "=" + entry.getIntValue());
         }
      }
   }

   private static void m_4(JsonArray jsonArray, ItemEnchantments itemEnchantments) {
      if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
         for (Holder holder : itemEnchantments.keySet()) {
            jsonArray.add(holder.getRegisteredName());
         }
      }
   }

   private JsonObject m_4_JsonObject() {
      JsonObject jsonObject = new JsonObject();
      Scoreboard scoreboard = MinecraftAccess.MINECRAFT.level.getScoreboard();
      Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
      if (objective == null) {
         return jsonObject;
      }

      jsonObject.addProperty("t", objective.getDisplayName().getString());
      JsonArray jsonArray = new JsonArray();

      for (PlayerScoreEntry playerScoreEntry : scoreboard.listPlayerScores(objective)) {
         if (playerScoreEntry != null && !playerScoreEntry.isHidden()) {
            StringBuilder stringBuilder = new StringBuilder();
            PlayerTeam playerTeam = scoreboard.getPlayersTeam(playerScoreEntry.owner());
            if (playerTeam != null) {
               stringBuilder.append(playerTeam.getPlayerPrefix().getString());
               stringBuilder.append(playerTeam.getPlayerSuffix().getString());
            }

            if (playerScoreEntry.display() != null) {
               stringBuilder.append(playerScoreEntry.display().getString());
            }

            stringBuilder.append(' ').append(playerScoreEntry.owner()).append(' ').append(playerScoreEntry.value());
            String text = ChatFormatting.stripFormatting(stringBuilder.toString());
            jsonArray.add(text != null ? text : stringBuilder.toString());
         }
      }

      jsonObject.add("l", jsonArray);
      return jsonObject;
   }

   private JsonArray m_2_JsonArray() {
      JsonArray jsonArray = new JsonArray();
      if (MinecraftAccess.MINECRAFT.gui != null && MinecraftAccess.MINECRAFT.gui.hud != null) {
         for (LerpingBossEvent event : ((BossHealthOverlayAccessor)MinecraftAccess.MINECRAFT.gui.hud.getBossOverlay()).onetap$getEvents().values()) {
            jsonArray.add(event.getName().getString());
         }

         return jsonArray;
      } else {
         return jsonArray;
      }
   }

   private boolean isBaritonePathing() {
      try {
         return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
      } catch (Throwable e) {
         return false;
      }
   }

   private JsonObject m_3(double doubleValue, List<TagKey<Block>> tagKeyList, List<String> stringList, boolean flag) {
      BlockHitResult blockHitResult = RemoteActionHandler.raycast(doubleValue);
      if (blockHitResult == null) {
         return null;
      }

      JsonObject jsonObject = this.m_3(blockHitResult.getBlockPos(), MinecraftAccess.MINECRAFT.level.getBlockState(blockHitResult.getBlockPos()), tagKeyList, stringList, flag);
      jsonObject.addProperty("fc", blockHitResult.getDirection().getSerializedName());
      jsonObject.addProperty("hx", blockHitResult.getLocation().x);
      jsonObject.addProperty("hy", blockHitResult.getLocation().y);
      jsonObject.addProperty("hz", blockHitResult.getLocation().z);
      return jsonObject;
   }

   private JsonObject m_4(JsonArray jsonArray, List<TagKey<Block>> tagKeyList, List<String> stringList, boolean flag) {
      JsonObject jsonObject = new JsonObject();
      BlockPos blockPos = MinecraftAccess.MINECRAFT.player.blockPosition();

      for (JsonElement jsonElement : jsonArray) {
         JsonObject jsonObject2 = jsonElement.getAsJsonObject();
         JsonArray jsonArray2 = jsonObject2.getAsJsonArray("min");
         JsonArray jsonArray3 = jsonObject2.getAsJsonArray("max");
         boolean flag2 = getBoolean(jsonObject2, "rel");
         int value2 = flag2 ? blockPos.getX() : 0;
         int value3 = flag2 ? blockPos.getY() : 0;
         int value4 = flag2 ? blockPos.getZ() : 0;
         HashSet set = new HashSet();
         ArrayList list = new ArrayList();

         for (String text : toStringList(jsonObject2.getAsJsonArray("m"))) {
            if (text.startsWith("#")) {
               list.add(parseBlockTag(text));
            } else {
               set.add(text);
            }
         }

         boolean flag3 = set.isEmpty() && list.isEmpty();
         int value5 = jsonObject2.has("lim") ? jsonObject2.get("lim").getAsInt() : 4096;
         JsonArray jsonArray4 = new JsonArray();
         MutableBlockPos mutableBlockPos = new MutableBlockPos();

         label92:
         for (int i = jsonArray2.get(0).getAsInt(); i <= jsonArray3.get(0).getAsInt(); i++) {
            for (int i2 = jsonArray2.get(1).getAsInt(); i2 <= jsonArray3.get(1).getAsInt(); i2++) {
               for (int i3 = jsonArray2.get(2).getAsInt(); i3 <= jsonArray3.get(2).getAsInt(); i3++) {
                  mutableBlockPos.set(value2 + i, value3 + i2, value4 + i3);
                  BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(mutableBlockPos);
                  if (!blockState.isAir()
                     && (flag3 || set.contains(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString()) || !((java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block>>)list).stream().noneMatch(blockState::is))) {
                     jsonArray4.add(this.m_3(mutableBlockPos.immutable(), blockState, tagKeyList, stringList, flag));
                     if (jsonArray4.size() >= value5) {
                        break label92;
                     }
                  }
               }
            }
         }

         jsonObject.add(jsonObject2.get("k").getAsString(), jsonArray4);
      }

      return jsonObject;
   }

   private JsonObject m_3(BlockPos blockPos, BlockState blockState, List<TagKey<Block>> tagKeyList, List<String> stringList, boolean flag) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", blockPos.getX());
      jsonObject.addProperty("y", blockPos.getY());
      jsonObject.addProperty("z", blockPos.getZ());
      jsonObject.addProperty("b", BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString());
      byte byteValue = 0;
      if (blockState.isAir()) {
         byteValue |= 1;
      }

      if (blockState.canBeReplaced()) {
         byteValue |= 2;
      }

      if (BlockTypeUtil.m_5_boolean(blockPos)) {
         byteValue |= 4;
      }

      if (blockState.getDestroySpeed(MinecraftAccess.MINECRAFT.level, blockPos) >= 0.0F) {
         byteValue |= 8;
      }

      if (blockState.canSurvive(MinecraftAccess.MINECRAFT.level, blockPos)) {
         byteValue |= 16;
      }

      jsonObject.addProperty("f", Integer.valueOf(byteValue));
      JsonArray jsonArray = new JsonArray();

      for (TagKey tagKey : tagKeyList) {
         if (blockState.is(tagKey)) {
            jsonArray.add("#" + tagKey.location());
         }
      }

      if (!jsonArray.isEmpty()) {
         jsonObject.add("g", jsonArray);
      }

      if (!stringList.isEmpty()) {
         JsonObject jsonObject2 = new JsonObject();

         for (Property property : blockState.getProperties()) {
            if (stringList.contains(property.getName())) {
               jsonObject2.addProperty(property.getName(), getPropertyName(blockState, property));
            }
         }

         if (!jsonObject2.isEmpty()) {
            jsonObject.add("s", jsonObject2);
         }
      }

      if (flag) {
         jsonObject.addProperty("st", blockState.toString());
      }

      return jsonObject;
   }

   private static <T extends Comparable<T>> String getPropertyName(BlockState blockState, Property<T> property) {
      return property.getName(blockState.getValue(property));
   }

   private JsonArray m_1_JsonArray(JsonArray jsonArray) {
      JsonArray jsonArray2 = new JsonArray();
      BlockPos blockPos = MinecraftAccess.MINECRAFT.player.blockPosition();
      int value2 = jsonArray.get(0).getAsInt();
      int value3 = jsonArray.get(1).getAsInt();
      int value4 = jsonArray.get(2).getAsInt();

      for (int i = -value2; i <= value2; i++) {
         for (int i2 = -value3; i2 <= value3; i2++) {
            for (int i3 = -value4; i3 <= value4; i3++) {
               BlockPos blockPos2 = blockPos.offset(i, i2, i3);
               if (MinecraftAccess.MINECRAFT.level.getBlockEntity(blockPos2) instanceof SignBlockEntity signBlockEntity) {
                  BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
                  BlockPos blockPos3;
                  if (blockState.getBlock() instanceof WallSignBlock) {
                     blockPos3 = blockPos2.relative(blockState.getValue(WallSignBlock.FACING).getOpposite());
                  } else if (blockState.getBlock() instanceof WallHangingSignBlock) {
                     blockPos3 = blockPos2.relative(blockState.getValue(WallHangingSignBlock.FACING).getOpposite());
                  } else if (blockState.getBlock() instanceof CeilingHangingSignBlock) {
                     blockPos3 = blockPos2.above();
                  } else {
                     blockPos3 = blockPos2.below();
                  }

                  JsonObject jsonObject = new JsonObject();
                  jsonObject.addProperty("x", blockPos2.getX());
                  jsonObject.addProperty("y", blockPos2.getY());
                  jsonObject.addProperty("z", blockPos2.getZ());
                  jsonObject.addProperty("ax", blockPos3.getX());
                  jsonObject.addProperty("ay", blockPos3.getY());
                  jsonObject.addProperty("az", blockPos3.getZ());
                  JsonArray jsonArray3 = new JsonArray();
                  m_5(jsonArray3, signBlockEntity.getFrontText());
                  m_5(jsonArray3, signBlockEntity.getBackText());
                  jsonObject.add("l", jsonArray3);
                  jsonArray2.add(jsonObject);
               }
            }
         }
      }

      return jsonArray2;
   }

   private static void m_5(JsonArray jsonArray, SignText signText) {
      if (signText != null) {
         for (Component component : signText.getMessages(false)) {
            if (component != null) {
               jsonArray.add(component.getString());
            }
         }
      }
   }

   private JsonArray m_7_JsonArray(JsonObject jsonObject) {
      JsonArray jsonArray = new JsonArray();
      double doubleValue = jsonObject.get("r").getAsDouble();
      HashSet set = new HashSet<>(toStringList(jsonObject.getAsJsonArray("t")));

      for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
         if (!(entity.distanceToSqr(MinecraftAccess.MINECRAFT.player) > doubleValue * doubleValue)) {
            String text = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
            if (set.isEmpty() || set.contains(text)) {
               JsonObject jsonObject2 = new JsonObject();
               jsonObject2.addProperty("id", entity.getId());
               jsonObject2.addProperty("t", text);
               jsonObject2.addProperty("x", entity.getX());
               jsonObject2.addProperty("y", entity.getY());
               jsonObject2.addProperty("z", entity.getZ());
               if (entity.getCustomName() != null) {
                  jsonObject2.addProperty("cn", entity.getCustomName().getString());
               }

               jsonObject2.addProperty("n", entity.getName().getString());
               if (entity instanceof Player player) {
                  jsonObject2.addProperty("pl", true);
                  jsonObject2.addProperty("pn", player.getGameProfile().name());
                  jsonObject2.addProperty("fr", Onetap.FRIEND_MANAGER.isFriend(player.getGameProfile().name()));
                  jsonObject2.addProperty("sp", player.isSpectator());
               }

               jsonObject2.addProperty("al", entity.isAlive());
               jsonArray.add(jsonObject2);
            }
         }
      }

      return jsonArray;
   }

   private JsonObject m_8_JsonObject() {
      JsonObject jsonObject = new JsonObject();
      if (MinecraftAccess.MINECRAFT.hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != Type.MISS) {
         jsonObject.addProperty("k", "block");
         BlockPos blockPos = blockHitResult.getBlockPos();
         jsonObject.addProperty("bx", blockPos.getX());
         jsonObject.addProperty("by", blockPos.getY());
         jsonObject.addProperty("bz", blockPos.getZ());
         jsonObject.addProperty("st", MinecraftAccess.MINECRAFT.level.getBlockState(blockPos).toString());
         jsonObject.addProperty("fc", blockHitResult.getDirection().getSerializedName());
         Vec3 vec3 = blockHitResult.getLocation();
         jsonObject.addProperty("x", vec3.x);
         jsonObject.addProperty("y", vec3.y);
         jsonObject.addProperty("z", vec3.z);
         return jsonObject;
      } else if (MinecraftAccess.MINECRAFT.hitResult instanceof EntityHitResult entityHitResult) {
         Entity entity = entityHitResult.getEntity();
         jsonObject.addProperty("k", "entity");
         jsonObject.addProperty("eid", entity.getId());
         jsonObject.addProperty("t", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
         jsonObject.addProperty("n", ChatFormatting.stripFormatting(entity.getName().getString()));
         jsonObject.addProperty("x", entity.getX());
         jsonObject.addProperty("y", entity.getY());
         jsonObject.addProperty("z", entity.getZ());
         return jsonObject;
      } else {
         return null;
      }
   }

   private static List<TagKey<Block>> toTagList(JsonArray jsonArray) {
      ArrayList list = new ArrayList();

      for (String text : toStringList(jsonArray)) {
         list.add(parseBlockTag(text));
      }

      return list;
   }

   private static TagKey<Block> parseBlockTag(String text) {
      return TagKey.create(Registries.BLOCK, Identifier.parse(text.startsWith("#") ? text.substring(1) : text));
   }

   public static List<String> toStringList(JsonArray jsonArray) {
      ArrayList list = new ArrayList();
      if (jsonArray != null) {
         for (JsonElement jsonElement : jsonArray) {
            list.add(jsonElement.getAsString());
         }
      }

      return list;
   }

   public static boolean m_9(ItemStack itemStack, JsonObject jsonObject) {
      if (itemStack != null && !itemStack.isEmpty()) {
         if (jsonObject.has("i") && !BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString().equals(jsonObject.get("i").getAsString())) {
            return false;
         }

         if (itemStack.getCount() != (jsonObject.has("c") ? jsonObject.get("c").getAsInt() : 1)) {
            return false;
         }

         if (jsonObject.has("n") && !itemStack.getHoverName().getString().equals(jsonObject.get("n").getAsString())) {
            return false;
         }

         var stringList = toStringList(jsonObject.getAsJsonArray("l"));
         if (stringList.isEmpty()) {
            return true;
         }

         ArrayList list = new ArrayList();
         ItemLore itemLore = itemStack.get(DataComponents.LORE);
         if (itemLore != null) {
            for (Component component : itemLore.lines()) {
               list.add(component.getString());
            }
         }

         if (list.containsAll(stringList)) {
            return true;
         }

         Minecraft mc = Minecraft.getInstance();

         try {
            for (Component component2 : itemStack.getTooltipLines(TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL)) {
               list.add(component2.getString());
            }
         } catch (Exception e) {
         }

         return list.containsAll(stringList);
      } else {
         return false;
      }
   }

   public static boolean getBoolean(JsonObject jsonObject, String text) {
      return jsonObject.has(text) && jsonObject.get(text).isJsonPrimitive() && jsonObject.get(text).getAsBoolean();
   }
}
