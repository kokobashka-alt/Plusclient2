package wtf.onetap.pve;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalNear;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.autobuy.ItemEntry;
import wtf.onetap.combat.rotation.RotationModes;
import wtf.onetap.event.MovementInputEvent;
import wtf.onetap.mixin.client.MinecraftAccessor;
import wtf.onetap.mixin.input.KeyMappingAccessor;
import wtf.onetap.module.pve.C4q;
import wtf.onetap.module.render.Notifications;
import wtf.onetap.net.HtmlParser;
import wtf.onetap.platform.Diagnostics;
import wtf.onetap.platform.PlatformConnection;
import wtf.onetap.render.TargetArrow;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.InventoryUtil;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;
import wtf.onetap.util.SoundPlayer;

public class RemoteActionHandler implements MinecraftAccess {
   private static final Pattern PATTERN = Pattern.compile("records/[\\w.-]+\\.json");
   private JsonObject jsonObject;
   private final Map<KeyMapping, Boolean> keyMappingMap;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private boolean flag;
   private boolean flag2;
   private final Map<String, Object> stringMap;
   private final List<Block> blockList;

   public RemoteActionHandler() {
      this.jsonObject = new JsonObject();
      this.keyMappingMap = new HashMap<>();
      this.stringMap = new HashMap<>();
      this.blockList = new ArrayList<>();
   }

   public JsonObject getJsonObject() {
      return this.jsonObject;
   }

   public void m_6(JsonArray jsonArray, PlatformConnection platformConnection) {
      if (jsonArray != null) {
         for (JsonElement jsonElement : jsonArray) {
            try {
               this.m_3(jsonElement.getAsJsonObject(), platformConnection);
            } catch (Exception e) {
               Diagnostics.m_3("remote action failed: {}", jsonElement, e);
            }
         }
      }
   }

   public void m_1_void(JsonObject jsonObject2) {
      this.jsonObject = jsonObject2 == null ? new JsonObject() : jsonObject2;
   }

   public void m_2_w_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         this.m_4_void();
         this.m_5_void();
         this.m_8_void();
         this.m_6_void();
      }
   }

   public void reset() {
      this.jsonObject = new JsonObject();
      this.m_5_void();
      this.m_8_void();
      this.m_2_void();
   }

   private void m_3(JsonObject jsonObject2, PlatformConnection platformConnection) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         switch (jsonObject2.get("k").getAsString()) {
            case "cmd":
               if (MinecraftAccess.MINECRAFT.getConnection() != null) {
                  MinecraftAccess.MINECRAFT.getConnection().sendCommand(getString(jsonObject2, "s"));
               }
               break;
            case "dot":
               String text = getString(jsonObject2, "s");
               if (text.startsWith(".")) {
                  Onetap.COMMAND_MANAGER.m_9_boolean(text);
               }
               break;
            case "msg":
               ChatUtil.log(getString(jsonObject2, "s"));
               break;
            case "ntf":
               Notifications notifications = Onetap.MODULE_MANAGER.get(Notifications.class);
               if (notifications != null) {
                  notifications.m_0(Component.literal(getString(jsonObject2, "s")), m_7(jsonObject2.get("it")));
               }
               break;
            case "clk":
               this.m_2(jsonObject2);
               break;
            case "close":
               this.m_3_void();
               break;
            case "sel":
               this.selectSlot(jsonObject2.get("n").getAsInt());
               break;
            case "use":
               MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, getHand(jsonObject2));
               break;
            case "useOn":
               this.m_4_void(jsonObject2);
               break;
            case "lclk":
               this.m_6(toBlockPos(jsonObject2.getAsJsonArray("p")), jsonObject2.has("ray") ? jsonObject2.get("ray").getAsDouble() : 4.0);
               break;
            case "startUse":
               if (PveSnapshot.getBoolean(jsonObject2, "any") && MinecraftAccess.MINECRAFT.hitResult instanceof BlockHitResult blockHitResult) {
                  Block block = MinecraftAccess.MINECRAFT.level.getBlockState(blockHitResult.getBlockPos()).getBlock();
                  if (block instanceof SlabBlock || block instanceof StairBlock) {
                     MinecraftAccess.MINECRAFT.player.setXRot(-90.0F);
                  }
               }

               ((MinecraftAccessor)MinecraftAccess.MINECRAFT).setRightClickDelay(0);
               ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$startUseItem();
               break;
            case "keyClick":
               KeyMapping.click(
                  ((KeyMappingAccessor)("attack".equals(getString(jsonObject2, "s")) ? MinecraftAccess.MINECRAFT.options.keyAttack : MinecraftAccess.MINECRAFT.options.keyUse)).onetap$getKey()
               );
               break;
            case "releaseUse":
               MinecraftAccess.MINECRAFT.gameMode.releaseUsingItem(MinecraftAccess.MINECRAFT.player);
               break;
            case "respawn":
               MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundClientCommandPacket(Action.PERFORM_RESPAWN));
               break;
            case "swing":
               MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
               break;
            case "path":
               BaritoneAPI.getProvider()
                  .getPrimaryBaritone()
                  .getCustomGoalProcess()
                  .setGoalAndPath(new GoalNear(toBlockPos(jsonObject2.getAsJsonArray("p")), jsonObject2.has("n") ? jsonObject2.get("n").getAsInt() : 1));
               break;
            case "pathStop":
               BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
               break;
            case "bset":
               this.m_3_void(jsonObject2.getAsJsonObject("d"));
               break;
            case "brestore":
               this.m_7_void();
               break;
            case "rotClear":
               Onetap.ROTATION_MODES.reset();
               break;
            case "rotReset":
               Onetap.ROTATION_MODES.resetSmoothing();
               break;
            case "reload":
               this.m_0_void();
               this.flag = false;
               if (Onetap.ROTATION_MODES.isFlag5()) {
                  Onetap.ROTATION_MODES.syncWithPlayer();
               }

               Onetap.ROTATION_MODES.reset();
               break;
            case "mineReset":
               this.blockPos3 = null;
               break;
            case "save":
               this.m_3_void(getString(jsonObject2, "s"), getString(jsonObject2, "txt"));
               break;
            case "hist":
               ItemEntry.m_9_C7x().m_4_void(jsonObject2.getAsJsonObject("d"));
               break;
            case "data":
               C4q c4q = platformConnection.getModule(getString(jsonObject2, "m"));
               if (c4q != null) {
                  c4q.m_4_void(jsonObject2.getAsJsonObject("d"));
               }
               break;
            case "off":
               C4q c4q2 = platformConnection.getModule(getString(jsonObject2, "m"));
               if (c4q2 != null) {
                  c4q2.setEnabled(false);
               }
               break;
            case "http":
               this.m_4(jsonObject2, platformConnection);
               break;
            case "shot":
               this.m_1(jsonObject2.get("n").getAsInt(), platformConnection);
               break;
            case "gps":
               JsonArray jsonArray = jsonObject2.getAsJsonArray("v");
               TargetArrow.m_6(jsonArray.get(0).getAsDouble(), jsonArray.get(2).getAsDouble(), jsonObject2.has("s") ? getString(jsonObject2, "s") : null, true);
               break;
            case "sound":
               SoundPlayer.play(getString(jsonObject2, "s"));
               break;
            case "lore":
               this.m_7_void(jsonObject2);
         }
      }
   }

   private void m_4(JsonObject jsonObject2, PlatformConnection platformConnection) {
      C4q c4q = platformConnection.getModule("fundeliver");
      if (c4q != null) {
         int value = jsonObject2.get("n").getAsInt();
         JsonObject jsonObject3 = jsonObject2.has("d") && jsonObject2.get("d").isJsonObject() ? jsonObject2.getAsJsonObject("d") : new JsonObject();
         HtmlParser.HTML_PARSER.upload(c4q.m_5_String(), getString(jsonObject2, "s"), getString(jsonObject2, "txt"), jsonObject3, (integer, text) -> m_0(platformConnection, value, integer, text));
      }
   }

   private static void m_0(PlatformConnection platformConnection, int value, int value2, String text) {
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("id", value);
      jsonObject2.addProperty("st", value2);
      jsonObject2.addProperty("b", text);
      platformConnection.sendEvent("http", null, null, jsonObject2);
   }

   private void m_1(int value, PlatformConnection platformConnection) {
      C4q c4q = platformConnection.getModule("fundeliver");
      if (c4q != null) {
         String text = c4q.m_5_String();
         CompletableFuture.delayedExecutor(1L, TimeUnit.SECONDS).execute(() -> MinecraftAccess.MINECRAFT.execute(() -> {
            try {
               Screenshot.takeScreenshot(MinecraftAccess.MINECRAFT.gameRenderer.mainRenderTarget(), nativeImage -> {
                  try (nativeImage) {
                     NativeImage nativeImage2 = m_5(nativeImage);
                     Path path = Files.createTempFile("funpay-proof", ".png");
                     nativeImage2.writeToFile(path);
                     nativeImage2.close();
                     byte[] byteArray = Files.readAllBytes(path);
                     Files.deleteIfExists(path);
                     HtmlParser.HTML_PARSER.upload(text, byteArray, (integer, text2) -> m_0(platformConnection, value, integer, text2));
                  } catch (Exception e) {
                     Diagnostics.warn("[FunPay] screenshot failed", e);
                     m_0(platformConnection, value, 0, "");
                  }
               });
            } catch (Exception e2) {
               Diagnostics.warn("[FunPay] screenshot failed", e2);
               m_0(platformConnection, value, 0, "");
            }
         }));
      }
   }

   private static NativeImage m_5(NativeImage nativeImage) {
      int value = nativeImage.getWidth();
      int value2 = nativeImage.getHeight();
      double doubleValue = MinecraftAccess.MINECRAFT.getWindow().getGuiScale();
      double doubleValue2 = MinecraftAccess.MINECRAFT.options.chatScale().get();
      int value3 = Math.max(9, (int)Math.round(9.0 * doubleValue * doubleValue2));
      int value4 = (int)Math.round(40.0 * doubleValue);
      int value5 = value3 * 5;
      int value6 = value2 - value4 - value5 + value3 / 2;
      value6 = Math.max(0, Math.min(value6, value2 - value5));
      int value7 = Math.max(1, (int)(value * 0.45));
      NativeImage nativeImage2 = new NativeImage(value7, value5, false);

      for (int i = 0; i < value5; i++) {
         for (int i2 = 0; i2 < value7; i2++) {
            nativeImage2.setPixel(i2, i, nativeImage.getPixel(i2, value6 + i));
         }
      }

      return nativeImage2;
   }

   private void m_7_void(JsonObject jsonObject2) {
      AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
      if (abstractContainerMenu != null && abstractContainerMenu.containerId == jsonObject2.get("c").getAsInt()) {
         int value = jsonObject2.get("sl").getAsInt();
         if (value >= 0 && value < abstractContainerMenu.slots.size()) {
            ItemStack itemStack = abstractContainerMenu.slots.get(value).getItem();
            if (!itemStack.isEmpty()) {
               JsonObject jsonObject3 = jsonObject2.getAsJsonObject("d");
               ItemLore itemLore = itemStack.get(DataComponents.LORE);
               ArrayList list = new ArrayList(itemLore == null ? List.of() : itemLore.lines());
               if (jsonObject3.has("rm")) {
                  for (JsonElement jsonElement : jsonObject3.getAsJsonArray("rm")) {
                     int value2 = jsonElement.getAsInt();
                     if (value2 >= 0 && value2 < list.size()) {
                        list.remove(value2);
                     }
                  }
               }

               if (jsonObject3.has("t")) {
                  int value3 = jsonObject3.has("at") ? Math.max(0, Math.min(jsonObject3.get("at").getAsInt(), list.size())) : list.size();
                  list.add(value3, Component.literal(jsonObject3.get("t").getAsString()));
               }

               itemStack.set(DataComponents.LORE, new ItemLore(List.copyOf(list)));
            }
         }
      }
   }

   private void m_2(JsonObject jsonObject2) {
      AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
      int value = jsonObject2.get("c").getAsInt();
      if (value == -1) {
         value = abstractContainerMenu == null ? 0 : abstractContainerMenu.containerId;
      }

      if (!PveSnapshot.getBoolean(jsonObject2, "ifc") || abstractContainerMenu != null && !abstractContainerMenu.getCarried().isEmpty()) {
         int value2 = value;
         int value3 = jsonObject2.get("sl").getAsInt();
         if (!jsonObject2.has("x")
            || !jsonObject2.get("x").isJsonObject()
            || abstractContainerMenu != null
               && abstractContainerMenu.containerId == value2
               && value3 >= 0
               && value3 < abstractContainerMenu.slots.size()
               && PveSnapshot.m_9(abstractContainerMenu.getSlot(value3).getItem(), jsonObject2.getAsJsonObject("x"))) {
            int value4 = jsonObject2.get("b").getAsInt();
            ContainerInput containerInput = ContainerInput.valueOf(getString(jsonObject2, "m"));
            if (PveSnapshot.getBoolean(jsonObject2, "g")) {
               InventoryUtil.m_6(() -> InventoryUtil.clickSlot(value2, value3, value4, containerInput));
            } else {
               InventoryUtil.clickSlot(value2, value3, value4, containerInput);
            }
         }
      }
   }

   private void m_3_void() {
      if (MinecraftAccess.MINECRAFT.player.containerMenu != null && MinecraftAccess.MINECRAFT.player.containerMenu.containerId != 0) {
         MinecraftAccess.MINECRAFT.player.closeContainer();
      }

      if (MinecraftAccess.MINECRAFT.gui.screen() != null) {
         MinecraftAccess.MINECRAFT.gui.screen().onClose();
         MinecraftAccess.MINECRAFT.gui.setScreen(null);
      }
   }

   private void selectSlot(int value) {
      MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value);
      MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value));
   }

   private void m_4_void(JsonObject jsonObject2) {
      BlockPos blockPos4 = jsonObject2.has("p") && jsonObject2.get("p").isJsonArray() ? toBlockPos(jsonObject2.getAsJsonArray("p")) : null;
      boolean flag3 = PveSnapshot.getBoolean(jsonObject2, "any");
      BlockHitResult blockHitResult = null;
      if (jsonObject2.has("ray")) {
         BlockHitResult blockHitResult2 = raycast(jsonObject2.get("ray").getAsDouble());
         if (blockHitResult2 != null && (flag3 || blockHitResult2.getBlockPos().equals(blockPos4))) {
            blockHitResult = blockHitResult2;
         }
      }

      if (blockHitResult == null) {
         if (flag3 || blockPos4 == null) {
            return;
         }

         JsonArray jsonArray = jsonObject2.getAsJsonArray("v");
         Vec3 vec3 = jsonArray == null ? Vec3.atCenterOf(blockPos4) : new Vec3(jsonArray.get(0).getAsDouble(), jsonArray.get(1).getAsDouble(), jsonArray.get(2).getAsDouble());
         Direction direction = Direction.byName(getString(jsonObject2, "f"));
         blockHitResult = new BlockHitResult(vec3, direction == null ? Direction.UP : direction, blockPos4, PveSnapshot.getBoolean(jsonObject2, "inside"));
      }

      MinecraftAccess.MINECRAFT.gameMode.useItemOn(MinecraftAccess.MINECRAFT.player, getHand(jsonObject2), blockHitResult);
   }

   private void m_6(BlockPos blockPos4, double doubleValue) {
      if (MinecraftAccess.MINECRAFT.gui.screen() == null && !MinecraftAccess.MINECRAFT.level.getBlockState(blockPos4).isAir()) {
         BlockHitResult blockHitResult = m_5(blockPos4, doubleValue);
         HitResult hitResult = MinecraftAccess.MINECRAFT.hitResult;
         boolean flag3 = MinecraftAccess.MINECRAFT.options.keyAttack.isDown();
         MinecraftAccess.MINECRAFT.hitResult = blockHitResult;
         KeyMapping.click(((KeyMappingAccessor)MinecraftAccess.MINECRAFT.options.keyAttack).onetap$getKey());
         MinecraftAccess.MINECRAFT.options.keyAttack.setDown(true);
         this.flag2 = !flag3;

         try {
            MinecraftAccessor minecraftAccessor = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
            boolean flag4 = minecraftAccessor.onetap$startAttack();
            if (!flag4 && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
               minecraftAccessor.onetap$continueAttack(true);
            }
         } finally {
            if (this.flag2) {
               MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
               this.flag2 = false;
               ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$continueAttack(false);
            }

            MinecraftAccess.MINECRAFT.hitResult = hitResult;
         }
      }
   }

   private void m_0_void() {
      if (this.flag2) {
         this.flag2 = false;
         MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
         if (MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
            MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
         }
      }
   }

   private void m_3_void(String text, String text2) {
      if (text != null && PATTERN.matcher(text).matches()) {
         Path path = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve(text);

         try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, text2, StandardCharsets.UTF_8);
            ChatUtil.log("§fAction Recorder §7» §7Запись сохранена: §f" + path.getFileName());
         } catch (Exception e) {
            ChatUtil.log("§fAction Recorder §7» §cНе удалось сохранить запись.");
         }
      }
   }

   private void m_3_void(JsonObject jsonObject2) {
      Settings settings = BaritoneAPI.getSettings();

      for (Entry entry : jsonObject2.entrySet()) {
         if (((String)entry.getKey()).equals("avoid")) {
            for (JsonElement jsonElement : ((JsonElement)entry.getValue()).getAsJsonArray()) {
               Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(jsonElement.getAsString()));
               if (!settings.blocksToAvoid.value.contains(block)) {
                  settings.blocksToAvoid.value.add(block);
                  this.blockList.add(block);
               }
            }
         } else {
            Settings.Setting setting = settings.byLowerName.get(((String)entry.getKey()).toLowerCase());
            if (setting != null) {
               this.stringMap.putIfAbsent((String)entry.getKey(), setting.value);
               JsonElement jsonElement2 = (JsonElement)entry.getValue();
               if (setting.value instanceof Boolean) {
                  setting.value = jsonElement2.getAsBoolean();
               } else if (setting.value instanceof Double) {
                  setting.value = jsonElement2.getAsDouble();
               } else if (setting.value instanceof Integer) {
                  setting.value = jsonElement2.getAsInt();
               } else if (setting.value instanceof Float) {
                  setting.value = jsonElement2.getAsFloat();
               }
            }
         }
      }
   }

   private void m_7_void() {
      Settings settings = BaritoneAPI.getSettings();
      this.stringMap.forEach((text, object) -> {
         Settings.Setting setting = settings.byLowerName.get(text.toLowerCase());
         if (setting != null) {
            setting.value = object;
         }
      });
      this.stringMap.clear();
      settings.blocksToAvoid.value.removeAll(this.blockList);
      this.blockList.clear();
   }

   private void m_4_void() {
      if (this.jsonObject.has("rot") && this.jsonObject.get("rot").isJsonObject()) {
         JsonObject jsonObject2 = this.jsonObject.getAsJsonObject("rot");
         int value = jsonObject2.has("pr") ? jsonObject2.get("pr").getAsInt() : 10;
         Vec3 vec3 = null;
         if (jsonObject2.has("at") && jsonObject2.get("at").isJsonArray()) {
            JsonArray jsonArray = jsonObject2.getAsJsonArray("at");
            vec3 = new Vec3(jsonArray.get(0).getAsDouble(), jsonArray.get(1).getAsDouble(), jsonArray.get(2).getAsDouble());
         }

         RotationModes rotationModes = Onetap.ROTATION_MODES;
         switch (getString(jsonObject2, "m")) {
            case "look":
               if (vec3 != null) {
                  rotationModes.lookAtHumanized(vec3, value);
               }
               break;
            case "snap":
               if (vec3 != null) {
                  rotationModes.lookAtClamped(vec3, value);
               }
               break;
            case "angles":
               rotationModes.lookAtAngles(jsonObject2.get("yaw").getAsFloat(), jsonObject2.get("pitch").getAsFloat(), value);
               break;
            case "smooth":
               rotationModes.rotateWithSensitivity(getRotation(jsonObject2, vec3), false, value);
               break;
            default:
               rotationModes.rotate(getRotation(jsonObject2, vec3), false, value);
         }
      }
   }

   private static Rotations getRotation(JsonObject jsonObject2, Vec3 vec3) {
      return vec3 != null ? Onetap.ROTATION_MODES.getRotationTo(vec3) : new Rotations(jsonObject2.get("yaw").getAsFloat(), jsonObject2.get("pitch").getAsFloat());
   }

   private void m_5_void() {
      JsonObject jsonObject2 = this.jsonObject.has("keys") && this.jsonObject.get("keys").isJsonObject() ? this.jsonObject.getAsJsonObject("keys") : new JsonObject();
      this.applyKey(MinecraftAccess.MINECRAFT.options.keyUp, jsonObject2, "f");
      this.applyKey(MinecraftAccess.MINECRAFT.options.keyDown, jsonObject2, "b");
      this.applyKey(MinecraftAccess.MINECRAFT.options.keyLeft, jsonObject2, "l");
      this.applyKey(MinecraftAccess.MINECRAFT.options.keyRight, jsonObject2, "r");
      this.applyKey(MinecraftAccess.MINECRAFT.options.keyJump, jsonObject2, "j");
      this.applyKey(MinecraftAccess.MINECRAFT.options.keyUse, jsonObject2, "us");
      this.applyKey(MinecraftAccess.MINECRAFT.options.keyAttack, jsonObject2, "at");
      this.applyKey(MinecraftAccess.MINECRAFT.options.keyShift, jsonObject2, "sn");
   }

   private void applyKey(KeyMapping keyMapping, JsonObject jsonObject2, String text) {
      if (jsonObject2.has(text) && !jsonObject2.get(text).isJsonNull()) {
         boolean flag3 = jsonObject2.get(text).getAsBoolean();
         keyMapping.setDown(flag3);
         if (flag3) {
            this.keyMappingMap.put(keyMapping, true);
         } else {
            this.keyMappingMap.remove(keyMapping);
         }
      } else if (this.keyMappingMap.remove(keyMapping) != null) {
         keyMapping.setDown(false);
      }
   }

   private void m_8_void() {
      RotationModes rotationModes = Onetap.ROTATION_MODES;
      if (PveSnapshot.getBoolean(this.jsonObject, "free")) {
         if (!rotationModes.isFlag5()) {
            rotationModes.setSpoofedRotation(MinecraftAccess.MINECRAFT.player.getYRot(), MinecraftAccess.MINECRAFT.player.getXRot());
         }

         this.flag = true;
      } else if (this.flag) {
         this.flag = false;
         if (rotationModes.isFlag5()) {
            float floatValue = rotationModes.getRenderYaw();
            float floatValue2 = rotationModes.getRenderPitch();
            MinecraftAccess.MINECRAFT.player.setYRot(MinecraftAccess.MINECRAFT.player.getYRot() + Mth.wrapDegrees(floatValue - MinecraftAccess.MINECRAFT.player.getYRot()));
            MinecraftAccess.MINECRAFT.player.setXRot(floatValue2);
            rotationModes.syncWithPlayer();
         }

         rotationModes.reset();
      }
   }

   private void m_6_void() {
      if (this.jsonObject.has("mine") && this.jsonObject.get("mine").isJsonObject()) {
         JsonObject jsonObject2 = this.jsonObject.getAsJsonObject("mine");
         BlockPos blockPos4 = new BlockPos(jsonObject2.get("x").getAsInt(), jsonObject2.get("y").getAsInt(), jsonObject2.get("z").getAsInt());
         double doubleValue = jsonObject2.has("reach") ? jsonObject2.get("reach").getAsDouble() : 5.0;
         if (this.blockPos != null && !this.blockPos.equals(blockPos4) && "hold".equals(getString(jsonObject2, "mode"))) {
            this.m_2_void();
         }

         this.blockPos = blockPos4;
         if ("hold".equals(getString(jsonObject2, "mode"))) {
            MinecraftAccess.MINECRAFT.hitResult = m_5(blockPos4, doubleValue);
            MinecraftAccessor minecraftAccessor = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
            if (!blockPos4.equals(this.blockPos2)) {
               MinecraftAccess.MINECRAFT.options.keyAttack.setDown(true);
               minecraftAccessor.onetap$startAttack();
               this.blockPos2 = blockPos4;
            } else {
               minecraftAccessor.onetap$continueAttack(true);
            }
         } else {
            if (PveSnapshot.getBoolean(jsonObject2, "tool")) {
               int value = this.m_3_int(blockPos4);
               if (value != MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot()) {
                  this.selectSlot(value);
               }
            }

            Direction direction = this.m_1_Direction(blockPos4);
            if (PveSnapshot.getBoolean(jsonObject2, "pkt") && !blockPos4.equals(this.blockPos3)) {
               MinecraftAccess.MINECRAFT
                  .player
                  .connection
                  .send(
                     new ServerboundPlayerActionPacket(
                        net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos4, direction
                     )
                  );
               MinecraftAccess.MINECRAFT.player.swing(InteractionHand.MAIN_HAND);
               this.blockPos3 = blockPos4;
            }

            BlockHitResult blockHitResult = raycast(doubleValue);
            if (blockHitResult != null && blockHitResult.getBlockPos().equals(blockPos4)) {
               HitResult hitResult = MinecraftAccess.MINECRAFT.hitResult;
               MinecraftAccess.MINECRAFT.hitResult = blockHitResult;

               try {
                  MinecraftAccessor minecraftAccessor2 = (MinecraftAccessor)MinecraftAccess.MINECRAFT;
                  boolean flag3 = minecraftAccessor2.onetap$startAttack();
                  if (!flag3 && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
                     minecraftAccessor2.onetap$continueAttack(true);
                  }
               } finally {
                  MinecraftAccess.MINECRAFT.hitResult = hitResult;
               }
            } else {
               this.stopMining();
            }
         }
      } else {
         if (this.blockPos != null) {
            this.m_2_void();
         }
      }
   }

   private void m_2_void() {
      this.blockPos = null;
      this.blockPos2 = null;
      MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
      this.stopMining();
      if (MinecraftAccess.MINECRAFT.player != null) {
         ((MinecraftAccessor)MinecraftAccess.MINECRAFT).onetap$continueAttack(false);
      }
   }

   private void stopMining() {
      MinecraftAccess.MINECRAFT.options.keyAttack.setDown(false);
      if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
         MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
      }
   }

   private int m_3_int(BlockPos blockPos4) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos4);
      float floatValue = 1.0F;
      int value = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();

      for (int i = 0; i < 9; i++) {
         float floatValue2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).getDestroySpeed(blockState);
         if (floatValue2 > floatValue) {
            floatValue = floatValue2;
            value = i;
         }
      }

      return value;
   }

   private Direction m_1_Direction(BlockPos blockPos4) {
      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
      Vec3 vec32 = Vec3.atCenterOf(blockPos4);
      double doubleValue = vec3.x - vec32.x;
      double doubleValue2 = vec3.y - vec32.y;
      double doubleValue3 = vec3.z - vec32.z;
      double doubleValue4 = Math.abs(doubleValue);
      double doubleValue5 = Math.abs(doubleValue2);
      double doubleValue6 = Math.abs(doubleValue3);
      if (doubleValue4 > doubleValue5 && doubleValue4 > doubleValue6) {
         return doubleValue > 0.0 ? Direction.EAST : Direction.WEST;
      } else if (doubleValue5 > doubleValue4 && doubleValue5 > doubleValue6) {
         return doubleValue2 > 0.0 ? Direction.UP : Direction.DOWN;
      } else {
         return doubleValue3 > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   public void m_5(MovementInputEvent event) {
      if (this.jsonObject.has("steer") && this.jsonObject.get("steer").isJsonObject() && MinecraftAccess.MINECRAFT.player != null) {
         JsonObject jsonObject2 = this.jsonObject.getAsJsonObject("steer");
         boolean flag3 = PveSnapshot.getBoolean(jsonObject2, "force");
         float floatValue = event.getForward();
         float floatValue2 = event.getStrafe();
         if (flag3 || floatValue != 0.0F || floatValue2 != 0.0F) {
            Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition();
            double doubleValue = jsonObject2.get("x").getAsDouble() - vec3.x;
            double doubleValue2 = jsonObject2.get("z").getAsDouble() - vec3.z;
            float floatValue3 = (float)Math.toDegrees(Math.atan2(doubleValue2, doubleValue)) - 90.0F;
            float floatValue4 = floatValue3 - MinecraftAccess.MINECRAFT.player.getYRot();
            float floatValue5 = flag3 ? 90.0F : (float)Math.toDegrees(Math.atan2(floatValue, floatValue2));
            float floatValue6 = Math.round(Mth.wrapDegrees(floatValue5 + floatValue4) / 45.0F) * 45.0F;
            double doubleValue3 = Math.toRadians(floatValue6);
            event.setForward(Math.round((float)Math.sin(doubleValue3)));
            event.setStrafe(Math.round((float)Math.cos(doubleValue3)));
         }
      }
   }

   public boolean isNoBreak() {
      return PveSnapshot.getBoolean(this.jsonObject, "noBreak");
   }

   public static BlockHitResult raycast(double doubleValue) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         Rotations rotations = Onetap.ROTATION_MODES.toRotations();
         Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
         Vec3 vec32 = Vec3.directionFromRotation(rotations.getFloatValue2(), rotations.getFloatValue());
         return MinecraftAccess.MINECRAFT
                  .level
                  .clip(new ClipContext(vec3, vec3.add(vec32.scale(doubleValue)), net.minecraft.world.level.ClipContext.Block.OUTLINE, Fluid.NONE, MinecraftAccess.MINECRAFT.player)) instanceof BlockHitResult blockHitResult
               && blockHitResult.getType() != Type.MISS
            ? blockHitResult
            : null;
      } else {
         return null;
      }
   }

   private static BlockHitResult m_5(BlockPos blockPos4, double doubleValue) {
      BlockHitResult blockHitResult = raycast(doubleValue);
      if (blockHitResult != null && blockHitResult.getBlockPos().equals(blockPos4)) {
         return blockHitResult;
      }

      Vec3 vec3 = MinecraftAccess.MINECRAFT.player.getEyePosition(1.0F);
      Vec3 vec32 = Vec3.atCenterOf(blockPos4);
      Direction direction = Direction.getApproximateNearest(vec32.x - vec3.x, vec32.y - vec3.y, vec32.z - vec3.z);
      return new BlockHitResult(vec32, direction != null ? direction.getOpposite() : Direction.UP, blockPos4, false);
   }

   private static InteractionHand getHand(JsonObject jsonObject2) {
      return "off".equals(getString(jsonObject2, "h")) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
   }

   public static BlockPos toBlockPos(JsonArray jsonArray) {
      return new BlockPos(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt(), jsonArray.get(2).getAsInt());
   }

   public static ItemStack m_7(JsonElement jsonElement) {
      if (jsonElement != null && jsonElement.isJsonObject()) {
         JsonObject jsonObject2 = jsonElement.getAsJsonObject();
         var optional = BuiltInRegistries.ITEM.getOptional(Identifier.parse(jsonObject2.get("i").getAsString()));
         return optional.<ItemStack>map(item -> new ItemStack(item, jsonObject2.has("c") ? jsonObject2.get("c").getAsInt() : 1)).orElse(null);
      } else {
         return null;
      }
   }

   public static String getString(JsonObject jsonObject2, String text) {
      return jsonObject2.has(text) && !jsonObject2.get(text).isJsonNull() ? jsonObject2.get(text).getAsString() : "";
   }
}
