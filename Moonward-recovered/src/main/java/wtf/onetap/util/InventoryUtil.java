package wtf.onetap.util;

import com.google.common.primitives.Shorts;
import com.google.common.primitives.SignedBytes;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.phys.Vec2;
import org.lwjgl.glfw.GLFW;
import wtf.onetap.mixin.input.ClientInputAccessor;
import wtf.onetap.mixin.input.KeyMappingAccessor;
import wtf.onetap.mixin.player.MultiPlayerGameModeAccessor;
import wtf.onetap.obf.w;

public class InventoryUtil implements MinecraftAccess {
   private static boolean flag;
   private static int value = -1;
   private static boolean flag2;
   private static int value2 = -1;
   private static Runnable runnable = null;
   private static boolean flag3 = false;
   private static int value3;
   private static boolean flag4;
   private static boolean flag5;
   private static Runnable runnable2 = null;
   private static long longValue = 0L;
   private static boolean flag6 = false;
   private static boolean flag7 = false;
   private static boolean flag8 = false;
   private static long longValue2 = 50L;
   private static long longValue3 = 50L;
   private static String[] textArray;

   private InventoryUtil() {
   }

   public static int findItemSlot(Item item) {
      return findItemSlotInRange(item, 0, 36);
   }

   public static int findItemSlotInRange(Item item, int value4, int value5) {
      for (int i = value4; i < value5; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).is(item)) {
            return i;
         }
      }

      return -1;
   }

   public static int findHotbarSlot(Item item) {
      for (int i = 0; i < 9; i++) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getItem(i).is(item)) {
            return i;
         }
      }

      return -1;
   }

   public static int findSlot(Predicate<ItemStack> predicate) {
      return findSlotInRange(predicate, 0, 36);
   }

   public static int findSlotInRange(Predicate<ItemStack> predicate, int value4, int value5) {
      for (int i = value4; i < value5; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (!itemStack.isEmpty() && predicate.test(itemStack)) {
            return i;
         }
      }

      return -1;
   }

   public static void releaseKeys() {
      flag = true;
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.input != null) {
         MinecraftAccess.MINECRAFT.player.input.keyPresses = Input.EMPTY;
      }

      if (MinecraftAccess.MINECRAFT.options != null) {
         for (KeyMapping keyMapping : getMovementKeys()) {
            keyMapping.setDown(false);
         }
      }
   }

   public static void m_3_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.input != null) {
         MinecraftAccess.MINECRAFT.player.input.keyPresses = Input.EMPTY;
         ((ClientInputAccessor)MinecraftAccess.MINECRAFT.player.input).onetap$setMoveVector(Vec2.ZERO);
      }

      if (MinecraftAccess.MINECRAFT.options != null) {
         for (KeyMapping keyMapping : getMovementKeys()) {
            keyMapping.setDown(false);
         }

         MinecraftAccess.MINECRAFT.options.keySprint.setDown(false);
      }
   }

   public static void restoreKeys() {
      flag = false;
      if (MinecraftAccess.MINECRAFT.options != null) {
         for (KeyMapping keyMapping : getMovementKeys()) {
            keyMapping.setDown(isKeyDown(keyMapping));
         }

         if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.input != null) {
            Options options = MinecraftAccess.MINECRAFT.options;
            MinecraftAccess.MINECRAFT.player.input.keyPresses = new Input(
               options.keyUp.isDown(),
               options.keyDown.isDown(),
               options.keyLeft.isDown(),
               options.keyRight.isDown(),
               options.keyJump.isDown(),
               options.keyShift.isDown(),
               options.keySprint.isDown()
            );
         }
      }
   }

   private static KeyMapping[] getMovementKeys() {
      return new KeyMapping[]{MinecraftAccess.MINECRAFT.options.keyUp, MinecraftAccess.MINECRAFT.options.keyDown, MinecraftAccess.MINECRAFT.options.keyLeft, MinecraftAccess.MINECRAFT.options.keyRight, MinecraftAccess.MINECRAFT.options.keyJump};
   }

   public static boolean isKeyDown(KeyMapping keyMapping) {
      if (keyMapping != null && MinecraftAccess.MINECRAFT.getWindow() != null) {
         Key key = ((KeyMappingAccessor)keyMapping).onetap$getKey();
         return key.getType() != Type.KEYSYM ? false : InputConstants.isKeyDown(MinecraftAccess.MINECRAFT.getWindow(), key.getValue());
      } else {
         return false;
      }
   }

   public static boolean m_9_boolean() {
      if (MinecraftAccess.MINECRAFT.options != null && MinecraftAccess.MINECRAFT.getWindow() != null) {
         Key key = ((KeyMappingAccessor)MinecraftAccess.MINECRAFT.options.keyUse).onetap$getKey();
         return key.getType() == Type.MOUSE ? GLFW.glfwGetMouseButton(MinecraftAccess.MINECRAFT.getWindow().handle(), key.getValue()) == 1 : isKeyDown(MinecraftAccess.MINECRAFT.options.keyUse);
      } else {
         return false;
      }
   }

   public static void clickSlot(int value4, int value5, int value6, ContainerInput containerInput) {
      if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player != null) {
         MinecraftAccess.MINECRAFT.gameMode.handleContainerInput(value4, value5, value6, containerInput, MinecraftAccess.MINECRAFT.player);
      }
   }

   public static void selectHotbarSlot(int value4) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         if (MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() != value4) {
            MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value4);
            MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value4));
            ((MultiPlayerGameModeAccessor)MinecraftAccess.MINECRAFT.gameMode).onetap$setCarriedIndex(value4);
            MinecraftAccess.MINECRAFT.gameMode.handleSlotStateChanged(value4, 0, false);
         }
      }
   }

   public static void m_7_void(int value4) {
      if (value2 < 0) {
         value2 = value4;
      }
   }

   public static void restoreSlot() {
      if (value2 >= 0) {
         int value4 = value2;
         value2 = -1;
         selectHotbarSlot(value4);
      }
   }

   public static void restoreSelectedSlot() {
      if (value2 >= 0) {
         m_9_void(value2);
         value2 = -1;
      }
   }

   public static void m_9_void(int value4) {
      value = value4;
   }

   public static void m_8_p_void() {
      flag2 = true;
   }

   public static void m_2_void() {
      if (flag2) {
         flag2 = false;
         if (MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.gameMode.isDestroying()) {
            MinecraftAccess.MINECRAFT.gameMode.stopDestroyBlock();
         }
      }

      if (value >= 0) {
         int value4 = value;
         value = -1;
         selectHotbarSlot(value4);
      }
   }

   public static InventoryUtil.DataRecord handleContainerClick(int value4, int value5, int value6, ContainerInput containerInput, Player player) {
      if (MinecraftAccess.MINECRAFT.getConnection() != null && value4 == player.containerMenu.containerId) {
         AbstractContainerMenu abstractContainerMenu = player.containerMenu;
         NonNullList nonNullList = abstractContainerMenu.slots;
         ArrayList<ItemStack> itemStackList = new ArrayList<>(nonNullList.size());

         for (Slot slot : (Iterable<Slot>)nonNullList) {
            itemStackList.add(slot.getItem().copy());
         }

         ItemStack itemStack = abstractContainerMenu.getCarried().copy();
         abstractContainerMenu.clicked(value5, value6, containerInput, player);
         Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
         Int2ObjectOpenHashMap int2ObjectOpenHashMap2 = new Int2ObjectOpenHashMap();

         for (int i = 0; i < nonNullList.size(); i++) {
            if (!ItemStack.matches((ItemStack)itemStackList.get(i), ((Slot)nonNullList.get(i)).getItem())) {
               int2ObjectOpenHashMap.put(i, HashedStack.create(((Slot)nonNullList.get(i)).getItem(), MinecraftAccess.MINECRAFT.getConnection().decoratedHashOpsGenenerator()));
               int2ObjectOpenHashMap2.put(i, ((Slot)nonNullList.get(i)).getItem().copy());
            }
         }

         ServerboundContainerClickPacket serverboundContainerClickPacket = new ServerboundContainerClickPacket(
            value4,
            abstractContainerMenu.getStateId(),
            Shorts.checkedCast(value5),
            SignedBytes.checkedCast(value6),
            containerInput,
            int2ObjectOpenHashMap,
            HashedStack.create(abstractContainerMenu.getCarried(), MinecraftAccess.MINECRAFT.getConnection().decoratedHashOpsGenenerator())
         );
         return new InventoryUtil.DataRecord(serverboundContainerClickPacket, int2ObjectOpenHashMap2, abstractContainerMenu.getCarried().copy(), itemStackList, itemStack);
      } else {
         return null;
      }
   }

   public static void sendClick(InventoryUtil.DataRecord c3) {
      if (!m_8(c3)) {
         PacketUtil.sendPacket(c3.m_31());
      }
   }

   private static boolean m_8(InventoryUtil.DataRecord c3) {
      m_8_StringArr();
      Integer integer = getProtocolVersion();
      if (integer != null && integer <= 769 && MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null) {
         try {
            if (integer <= 754) {
               var clazz = MinecraftAccess.MINECRAFT.gameMode.getClass();
               setField(clazz, "viaFabricPlus$oldItems", c3.m_1_w_List());
               setField(clazz, "viaFabricPlus$oldCursorStack", c3.m_29());
               Class[] classArray = new Class[]{ServerboundContainerClickPacket.class};
               Method method = clazz.getDeclaredMethod(w.b("viaFabricPlus$clickSlot1_16_5", clazz, classArray), classArray);
               method.setAccessible(true);
               method.invoke(MinecraftAccess.MINECRAFT.gameMode, c3.m_31());
               return true;
            }

            AbstractContainerMenu abstractContainerMenu = MinecraftAccess.MINECRAFT.player.containerMenu;
            Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
            ObjectIterator objectIterator = c3.m_1_t_Int2ObjectOpenHashMap().int2ObjectEntrySet().iterator();

            while (objectIterator.hasNext()) {
               Entry entry = (Entry)objectIterator.next();
               int2ObjectOpenHashMap.put(entry.getIntKey(), abstractContainerMenu.slots.get(entry.getIntKey()).getItem());
               abstractContainerMenu.slots.get(entry.getIntKey()).set((ItemStack)entry.getValue());
            }

            ItemStack itemStack = abstractContainerMenu.getCarried();
            abstractContainerMenu.setCarried(c3.m_2_g());

            try {
               var clazz2 = MinecraftAccess.MINECRAFT.gameMode.getClass();
               Class[] classArray2 = new Class[]{ServerboundContainerClickPacket.class};
               Method method2 = clazz2.getDeclaredMethod(w.b("viaFabricPlus$clickSlot1_21_4", clazz2, classArray2), classArray2);
               method2.setAccessible(true);
               method2.invoke(MinecraftAccess.MINECRAFT.gameMode, c3.m_31());
               return true;
            } finally {
               ObjectIterator objectIterator2 = int2ObjectOpenHashMap.int2ObjectEntrySet().iterator();

               while (objectIterator2.hasNext()) {
                  Entry entry2 = (Entry)objectIterator2.next();
                  abstractContainerMenu.slots.get(entry2.getIntKey()).set((ItemStack)entry2.getValue());
               }

               abstractContainerMenu.setCarried(itemStack);
            }
         } catch (ReflectiveOperationException reflectiveOperationException) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static Integer getProtocolVersion() {
      try {
         Object object = Class.forName("com.viaversion.viafabricplus.ViaFabricPlus").getMethod("api").invoke(null);
         if (object == null) {
            return null;
         }

         Object object2 = Class.forName("com.viaversion.viafabricplus.api.ViaFabricPlusAPI").getMethod("targetVersion").invoke(object);
         if (object2 == null) {
            return null;
         }

         var clazz = object2.getClass();
         Class[] classArray = new Class[0];
         return clazz.getMethod(w.b("getOriginalVersion", clazz, classArray), classArray).invoke(object2) instanceof Integer integer ? integer : null;
      } catch (ReflectiveOperationException | LinkageError e) {
         return null;
      }
   }

   private static void setField(Class<?> clazz, String text, Object object) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException {
      Field field = clazz.getDeclaredField(text);
      field.setAccessible(true);
      field.set(MinecraftAccess.MINECRAFT.gameMode, object);
   }

   public static boolean m_8_boolean() {
      return runnable2 != null || flag3;
   }

   public static boolean m_7_boolean() {
      return flag || runnable2 != null || runnable != null && value3 > 0;
   }

   public static boolean isBusy() {
      return runnable != null || flag3 || runnable2 != null;
   }

   public static boolean m_6_boolean() {
      return runnable != null || flag3;
   }

   public static boolean m_5_boolean() {
      return runnable2 != null;
   }

   public static void m_7_void() {
      flag3 = true;
   }

   private static boolean m_2_boolean() {
      if (MinecraftAccess.MINECRAFT.player == null || MinecraftAccess.MINECRAFT.options == null) {
         return false;
      } else {
         return isKeyDown(MinecraftAccess.MINECRAFT.options.keyShift) ? false : MinecraftAccess.MINECRAFT.player.input.hasForwardImpulse() || isKeyDown(MinecraftAccess.MINECRAFT.options.keyUp);
      }
   }

   private static boolean m_1_boolean() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         MovementStateAccess movementStateAccess = (MovementStateAccess)MinecraftAccess.MINECRAFT.player;
         if (MinecraftAccess.MINECRAFT.player.isSprinting() && movementStateAccess.onetap$wasSprinting()) {
            MinecraftAccess.MINECRAFT.player.setSprinting(false);
            movementStateAccess.onetap$setWasSprinting(false);
            MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundPlayerCommandPacket(MinecraftAccess.MINECRAFT.player, Action.STOP_SPRINTING));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void m_0_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         if (m_2_boolean() && m_4_boolean()) {
            MinecraftAccess.MINECRAFT.player.setSprinting(true);
         }
      }
   }

   private static boolean m_0_boolean() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return false;
      } else {
         MovementStateAccess movementStateAccess = (MovementStateAccess)MinecraftAccess.MINECRAFT.player;
         if (MinecraftAccess.MINECRAFT.player.isSprinting() && movementStateAccess.onetap$wasSprinting()) {
            MinecraftAccess.MINECRAFT.player.setSprinting(false);
            return true;
         } else {
            return false;
         }
      }
   }

   private static boolean m_4_boolean() {
      return MinecraftAccess.MINECRAFT.player == null
         ? false
         : MinecraftAccess.MINECRAFT.player.onGround() || MinecraftAccess.MINECRAFT.player.isInWater() || MinecraftAccess.MINECRAFT.player.isInLava() || MinecraftAccess.MINECRAFT.player.getAbilities().flying;
   }

   public static void m_9_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         if (flag5) {
            flag5 = false;
            restoreKeys();
         }

         if (runnable != null) {
            if (value3 == 1) {
               releaseKeys();
               if (m_0_boolean()) {
                  flag4 = true;
               }

               value3 = 2;
               return;
            }

            runnable.run();
            runnable = null;
            value3 = 0;
            restoreKeys();
         } else if (flag3) {
            flag3 = false;
            m_0_void();
         }
      } else {
         runnable = null;
         flag3 = false;
         value3 = 0;
         flag4 = false;
         flag5 = false;
         restoreKeys();
      }
   }

   public static void m_8_void() {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         if (runnable2 != null) {
            releaseKeys();
            if (m_1_boolean()) {
               flag7 = true;
            }

            long longValue4 = System.currentTimeMillis() - longValue;
            if (longValue4 >= longValue2 && !flag6) {
               runnable2.run();
               flag6 = true;
               PacketUtil.sendPacket(new ServerboundContainerClosePacket(MinecraftAccess.MINECRAFT.player.containerMenu.containerId));
               if (flag7) {
                  flag8 = true;
               }
            }

            if (longValue4 >= longValue2 + longValue3) {
               runnable2 = null;
               flag6 = false;
               restoreKeys();
            }
         }

         if (flag8 && runnable2 == null) {
            flag8 = false;
            m_0_void();
         }
      } else {
         runnable2 = null;
         flag8 = false;
         flag6 = false;
         restoreKeys();
      }
   }

   public static void m_0(Runnable runnable3) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         if (runnable == null && !flag3) {
            boolean flag9 = isModernProtocol();
            value3 = flag9 ? 1 : 0;
            flag4 = false;
            runnable = () -> {
               if (m_1_boolean()) {
                  flag4 = true;
               }

               runnable3.run();
               PacketUtil.sendPacket(new ServerboundContainerClosePacket(MinecraftAccess.MINECRAFT.player.containerMenu.containerId));
               if (flag4) {
                  flag3 = true;
               }
            };
         }
      }
   }

   public static boolean isModernProtocol() {
      Integer integer = getProtocolVersion();
      return integer == null || integer >= 768;
   }

   public static void m_6(Runnable runnable3) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundPlayerInputPacket(Input.EMPTY));
         boolean flag9 = m_1_boolean();
         runnable3.run();
         PacketUtil.sendPacket(new ServerboundContainerClosePacket(0));
         if (flag9) {
            releaseKeys();
            flag5 = true;
            flag3 = true;
         } else {
            if (MinecraftAccess.MINECRAFT.player.input != null) {
               MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundPlayerInputPacket(MinecraftAccess.MINECRAFT.player.input.keyPresses));
            }
         }
      }
   }

   public static void m_4(Runnable runnable3) {
      m_8(runnable3, 50L, 50L);
   }

   public static void m_3(Runnable runnable3, float floatValue) {
      m_8(runnable3, (long)floatValue, (long)floatValue);
   }

   private static void m_8(Runnable runnable3, long longValue4, long longValue5) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         if (runnable2 == null) {
            runnable2 = runnable3;
            longValue2 = longValue4;
            longValue3 = longValue5;
            longValue = System.currentTimeMillis();
            flag6 = false;
            flag7 = false;
            flag8 = false;
            releaseKeys();
         }
      }
   }

   public static void runWithMode(String text, Runnable runnable3, float floatValue) {
      switch (text) {
         case "Грим":
            m_0(runnable3);
            break;
         case "Легит":
            m_3(runnable3, floatValue);
            break;
         default:
            runnable3.run();
      }
   }

   public static void useItem(Item item) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.gameMode != null && MinecraftAccess.MINECRAFT.player.connection != null) {
         if (!MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(MinecraftAccess.MINECRAFT.player.getMainHandItem()) || !MinecraftAccess.MINECRAFT.player.getMainHandItem().is(item)) {
            if (!MinecraftAccess.MINECRAFT.player.getCooldowns().isOnCooldown(MinecraftAccess.MINECRAFT.player.getOffhandItem()) || !MinecraftAccess.MINECRAFT.player.getOffhandItem().is(item)) {
               if (MinecraftAccess.MINECRAFT.player.getMainHandItem().is(item)) {
                  MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
               } else if (MinecraftAccess.MINECRAFT.player.getOffhandItem().is(item)) {
                  MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.OFF_HAND);
               } else {
                  int value4 = MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot();
                  int value5 = findHotbarSlot(item);
                  int value6 = findItemSlotInRange(item, 9, 36);
                  if (value5 != -1) {
                     MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value5));
                     MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
                     MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value4));
                  } else {
                     if (value6 != -1) {
                        int value7 = findEmptyHotbarSlot();
                        if (value7 == -1) {
                           return;
                        }

                        m_0(() -> {
                           clickSlot(0, value6, value7, ContainerInput.SWAP);
                           MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value7));
                           MinecraftAccess.MINECRAFT.gameMode.useItem(MinecraftAccess.MINECRAFT.player, InteractionHand.MAIN_HAND);
                           clickSlot(0, value6, value7, ContainerInput.SWAP);
                           MinecraftAccess.MINECRAFT.player.connection.send(new ServerboundSetCarriedItemPacket(value4));
                        });
                        MinecraftAccess.MINECRAFT.player.getInventory().setSelectedSlot(value4);
                     }
                  }
               }
            }
         }
      }
   }

   public static int m_5_int() {
      int value4 = -1;

      for (int i = 0; i < 8; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.isEmpty() || itemStack.getUseAnimation() == ItemUseAnimation.NONE) {
            value4 = i;
         }
      }

      return value4 == -1 ? MinecraftAccess.MINECRAFT.player.getInventory().getSelectedSlot() : value4;
   }

   public static int findEmptyHotbarSlot() {
      for (int i = 0; i < 9; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.isEmpty()) {
            return i;
         }
      }

      for (int i2 = 0; i2 < 9; i2++) {
         ItemStack itemStack2 = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i2);
         Equippable equippable = itemStack2.get(DataComponents.EQUIPPABLE);
         if (equippable == null && itemStack2.getUseAnimation() == ItemUseAnimation.NONE) {
            return i2;
         }
      }

      return -1;
   }

   public static int m_8_int() {
      int value4 = -1;
      int value5 = -1;
      float floatValue = Float.NEGATIVE_INFINITY;
      float floatValue2 = Float.NEGATIVE_INFINITY;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.getItem() instanceof AxeItem) {
            float floatValue3 = getAxeDamage(itemStack);
            if (i < 9) {
               if (floatValue3 > floatValue) {
                  floatValue = floatValue3;
                  value4 = i;
               }
            } else if (floatValue3 > floatValue2) {
               floatValue2 = floatValue3;
               value5 = i;
            }
         }
      }

      return value4 != -1 ? value4 : value5;
   }

   private static float getAxeDamage(ItemStack itemStack) {
      if (itemStack.is(Items.NETHERITE_AXE)) {
         return 6.0F;
      } else if (itemStack.is(Items.DIAMOND_AXE)) {
         return 5.0F;
      } else if (itemStack.is(Items.IRON_AXE)) {
         return 4.0F;
      } else if (itemStack.is(Items.STONE_AXE)) {
         return 3.0F;
      } else if (itemStack.is(Items.GOLDEN_AXE)) {
         return 2.0F;
      } else {
         return itemStack.is(Items.WOODEN_AXE) ? 1.0F : 0.0F;
      }
   }

   public static int m_1_int() {
      int value4 = -1;
      double doubleValue = -1.0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         if (itemStack.is(Items.ELYTRA)) {
            int value5 = getEnchantmentLevel(itemStack, Enchantments.PROTECTION);
            int value6 = getEnchantmentLevel(itemStack, Enchantments.UNBREAKING);
            int value7 = getEnchantmentLevel(itemStack, Enchantments.MENDING);
            int value8 = itemStack.getMaxDamage();
            int value9 = itemStack.getDamageValue();
            double doubleValue2 = (double)(value8 - value9) / value8;
            double doubleValue3 = value5 * 100 + value6 * 10 + (value7 > 0 ? 1 : 0) + doubleValue2 * 10.0;
            if (doubleValue3 > doubleValue) {
               doubleValue = doubleValue3;
               value4 = i;
            }
         }
      }

      return value4;
   }

   public static int findBestChestplateSlot() {
      int value4 = -1;
      double doubleValue = -1.0;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
         if (equippable != null && equippable.slot() == EquipmentSlot.CHEST && isChestplate(itemStack.getItem())) {
            int value5 = getEnchantmentLevel(itemStack, Enchantments.PROTECTION);
            int value6 = getEnchantmentLevel(itemStack, Enchantments.UNBREAKING);
            int value7 = getEnchantmentLevel(itemStack, Enchantments.MENDING);
            int value8 = getChestplateTier(itemStack.getItem());
            int value9 = itemStack.getMaxDamage();
            int value10 = itemStack.getDamageValue();
            double doubleValue2 = (double)(value9 - value10) / value9;
            double doubleValue3 = value8 * 10000 + value5 * 100 + value6 * 10 + (value7 > 0 ? 1 : 0) + doubleValue2 * 10.0;
            if (doubleValue3 > doubleValue) {
               doubleValue = doubleValue3;
               value4 = i;
            }
         }
      }

      return value4;
   }

   public static int getEnchantmentLevel(ItemStack itemStack, ResourceKey<Enchantment> resourceKey) {
      for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry entry : itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet()) {
         if (((Holder)entry.getKey()).is(resourceKey)) {
            return entry.getIntValue();
         }
      }

      return 0;
   }

   private static boolean isChestplate(Item item) {
      return item == Items.NETHERITE_CHESTPLATE
         || item == Items.DIAMOND_CHESTPLATE
         || item == Items.IRON_CHESTPLATE
         || item == Items.CHAINMAIL_CHESTPLATE
         || item == Items.GOLDEN_CHESTPLATE
         || item == Items.LEATHER_CHESTPLATE;
   }

   private static int getChestplateTier(Item item) {
      if (item == Items.NETHERITE_CHESTPLATE) {
         return 6;
      } else if (item == Items.DIAMOND_CHESTPLATE) {
         return 5;
      } else if (item == Items.IRON_CHESTPLATE) {
         return 4;
      } else if (item == Items.CHAINMAIL_CHESTPLATE) {
         return 3;
      } else if (item == Items.GOLDEN_CHESTPLATE) {
         return 2;
      } else {
         return item == Items.LEATHER_CHESTPLATE ? 1 : 0;
      }
   }

   static {
      m_6(new String[4]);
   }

   public static void m_6(String[] stringArray) {
      textArray = stringArray;
   }

   public static String[] m_8_StringArr() {
      return textArray;
   }

   public record DataRecord(ServerboundContainerClickPacket f_31, Int2ObjectOpenHashMap<ItemStack> f_1_t, ItemStack f_2_g, List<ItemStack> f_1_w, ItemStack f_29) {
      public ServerboundContainerClickPacket m_31() {
         return this.f_31;
      }

      public Int2ObjectOpenHashMap<ItemStack> m_1_t_Int2ObjectOpenHashMap() {
         return this.f_1_t;
      }

      public ItemStack m_2_g() {
         return this.f_2_g;
      }

      public List<ItemStack> m_1_w_List() {
         return this.f_1_w;
      }

      public ItemStack m_29() {
         return this.f_29;
      }
   }
}
