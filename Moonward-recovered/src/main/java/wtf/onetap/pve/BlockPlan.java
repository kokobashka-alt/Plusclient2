package wtf.onetap.pve;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import wtf.onetap.util.MinecraftAccess;

public class BlockPlan implements MinecraftAccess {
   private final Map<BlockState, Integer> blockStateMap;
   private final List<BlockState> blockStateList;
   private final Set<Integer> integerSet;
   private final Map<Long, Integer> longMap;
   private final ConcurrentLinkedQueue<Long> concurrentLinkedQueue;
   private final ConcurrentLinkedQueue<long[]> concurrentLinkedQueue2;
   private final ArrayDeque<Long> arrayDeque;
   private int value;
   private int value2;
   private int value3;
   private int value4;
   private int value5;

   public BlockPlan() {
      this.blockStateMap = new IdentityHashMap<>();
      this.blockStateList = new ArrayList<>();
      this.integerSet = new HashSet<>();
      this.longMap = new HashMap<>();
      this.concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
      this.concurrentLinkedQueue2 = new ConcurrentLinkedQueue<>();
      this.arrayDeque = new ArrayDeque<>();
      this.value = Integer.MIN_VALUE;
      this.value2 = -1;
      this.value3 = -1;
   }

   public void m_6_void() {
      this.longMap.clear();
      this.integerSet.clear();
      this.concurrentLinkedQueue.clear();
      this.concurrentLinkedQueue2.clear();
      this.arrayDeque.clear();
      this.value = Integer.MIN_VALUE;
      this.value2 = -1;
      this.value3 = -1;
   }

   public int getValue3() {
      return this.value3;
   }

   public int getValue4() {
      return this.value4;
   }

   public int getValue5() {
      return this.value5;
   }

   public void m_2(Packet<?> packet) {
      if (packet instanceof ClientboundBlockUpdatePacket clientboundBlockUpdatePacket) {
         this.m_0_void(clientboundBlockUpdatePacket.getPos());
      } else if (packet instanceof ClientboundSectionBlocksUpdatePacket clientboundSectionBlocksUpdatePacket) {
         clientboundSectionBlocksUpdatePacket.runUpdates((blockPos, blockState) -> this.m_0_void(blockPos));
      } else if (packet instanceof ClientboundLevelChunkWithLightPacket clientboundLevelChunkWithLightPacket) {
         this.addPair(clientboundLevelChunkWithLightPacket.getX(), clientboundLevelChunkWithLightPacket.getZ());
      } else if (packet instanceof ClientboundForgetLevelChunkPacket clientboundForgetLevelChunkPacket) {
         this.addPair(clientboundForgetLevelChunkPacket.pos().x(), clientboundForgetLevelChunkPacket.pos().z());
      }
   }

   public void m_0_void(BlockPos blockPos) {
      this.concurrentLinkedQueue.add(packPos(blockPos.getX() >> 3, blockPos.getY() >> 3, blockPos.getZ() >> 3));
   }

   private void addPair(int value6, int value7) {
      this.concurrentLinkedQueue2.add(new long[]{value6, value7});
   }

   public JsonObject m_5_JsonObject(int value6) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         int value7 = MinecraftAccess.MINECRAFT.player.getBlockX();
         int value8 = MinecraftAccess.MINECRAFT.player.getBlockY();
         int value9 = MinecraftAccess.MINECRAFT.player.getBlockZ();
         ArrayList<Long> longList = new ArrayList<>();

         for (int i = value7 - 12 >> 3; i <= value7 + 12 >> 3; i++) {
            for (int i2 = value8 - 8 >> 3; i2 <= value8 + 8 >> 3; i2++) {
               for (int i3 = value9 - 12 >> 3; i3 <= value9 + 12 >> 3; i3++) {
                  longList.add(packPos(i, i2, i3));
               }
            }
         }

         HashSet set = new HashSet(longList);

         long[] longArray;
         while ((longArray = this.concurrentLinkedQueue2.poll()) != null) {
            for (Long longObj : longList) {
               if (m_5_int(longObj) >> 1 == longArray[0] && m_6_int(longObj) >> 1 == longArray[1]) {
                  this.concurrentLinkedQueue.add(longObj);
               }
            }
         }

         LinkedHashSet<Long> longSet = new LinkedHashSet<>();

         Long longObj2;
         while ((longObj2 = this.concurrentLinkedQueue.poll()) != null) {
            if (set.contains(longObj2)) {
               longSet.add(longObj2);
            }
         }

         for (Long longObj3 : longList) {
            if (!this.longMap.containsKey(longObj3)) {
               longSet.add(longObj3);
            }
         }

         if (this.arrayDeque.isEmpty()) {
            this.arrayDeque.addAll(longList);
         }

         for (int i4 = 0; i4 < 8 && !this.arrayDeque.isEmpty(); i4++) {
            Long longObj4 = this.arrayDeque.poll();
            if (set.contains(longObj4)) {
               longSet.add(longObj4);
            }
         }

         JsonArray jsonArray = new JsonArray();
         var iterator = this.longMap.keySet().iterator();

         while (iterator.hasNext()) {
            long longValue = (Long)iterator.next();
            if (!set.contains(longValue)) {
               iterator.remove();
               JsonArray jsonArray2 = new JsonArray();
               jsonArray2.add(m_5_int(longValue));
               jsonArray2.add(m_0_int(longValue));
               jsonArray2.add(m_6_int(longValue));
               jsonArray.add(jsonArray2);
            }
         }

         JsonArray jsonArray3 = new JsonArray();
         int value10 = this.blockStateList.size();
         int value11 = 64;

         for (Long longObj5 : longSet) {
            if (value11 == 0) {
               this.concurrentLinkedQueue.add(longObj5);
            } else {
               int[] intArray = this.m_5(m_5_int(longObj5), m_0_int(longObj5), m_6_int(longObj5));
               int value12 = Arrays.hashCode(intArray);
               Integer integer = this.longMap.get(longObj5);
               if (integer == null || integer != value12) {
                  this.longMap.put(longObj5, value12);
                  jsonArray3.add(m_9(m_5_int(longObj5), m_0_int(longObj5), m_6_int(longObj5), intArray));
                  value11--;
               }
            }
         }

         int value13 = 0;

         for (Long longObj6 : longList) {
            if (!this.longMap.containsKey(longObj6)) {
               value13++;
            }
         }

         this.value3 = value13;
         this.value4 = jsonArray3.size();
         JsonArray jsonArray4 = new JsonArray();

         for (int i5 = 0; i5 < this.blockStateList.size(); i5++) {
            if (this.integerSet.add(i5)) {
               jsonArray4.add(this.m_3(i5, this.blockStateList.get(i5)));
            }
         }

         boolean flag = value6 != this.value || this.blockStateList.size() != this.value2;
         if (jsonArray3.isEmpty() && jsonArray.isEmpty() && jsonArray4.isEmpty() && !flag && value10 == this.blockStateList.size()) {
            this.value5 = 0;
            return null;
         }

         JsonObject jsonObject = new JsonObject();
         if (!jsonArray4.isEmpty()) {
            jsonObject.add("pal", jsonArray4);
         }

         if (!jsonArray3.isEmpty()) {
            jsonObject.add("br", jsonArray3);
         }

         if (!jsonArray.isEmpty()) {
            jsonObject.add("drop", jsonArray);
         }

         if (flag) {
            this.value = value6;
            this.value2 = this.blockStateList.size();
            jsonObject.add("tools", this.m_8_JsonArray());
         }

         this.value5 = jsonObject.toString().length();
         return jsonObject;
      } else {
         return null;
      }
   }

   private int[] m_5(int value6, int value7, int value8) {
      int[] intArray = new int[512];
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = 0; i < 8; i++) {
         for (int i2 = 0; i2 < 8; i2++) {
            for (int i3 = 0; i3 < 8; i3++) {
               mutableBlockPos.set((value6 << 3) + i3, (value7 << 3) + i, (value8 << 3) + i2);
               intArray[i << 6 | i2 << 3 | i3] = this.m_3_int(MinecraftAccess.MINECRAFT.level.getBlockState(mutableBlockPos));
            }
         }
      }

      return intArray;
   }

   private int m_3_int(BlockState blockState) {
      Integer integer = this.blockStateMap.get(blockState);
      if (integer != null) {
         return integer;
      }

      int value6 = this.blockStateList.size();
      this.blockStateMap.put(blockState, value6);
      this.blockStateList.add(blockState);
      return value6;
   }

   private static JsonArray m_9(int value6, int value7, int value8, int[] intArray) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(value6);
      jsonArray.add(value7);
      jsonArray.add(value8);
      int value9 = 1;

      for (int i = 1; i <= intArray.length; i++) {
         if (i < intArray.length && intArray[i] == intArray[i - 1]) {
            value9++;
         } else {
            jsonArray.add(intArray[i - 1]);
            jsonArray.add(value9);
            value9 = 1;
         }
      }

      return jsonArray;
   }

   private JsonObject m_3(int value6, BlockState blockState) {
      BlockPos blockPos = MinecraftAccess.MINECRAFT.player.blockPosition();
      byte byteValue = 0;
      if (blockState.isAir()) {
         byteValue |= 1;
      }

      if (blockState.canBeReplaced()) {
         byteValue |= 2;
      }

      if (!blockState.getFluidState().isEmpty()) {
         byteValue |= 4;
      }

      if (blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.BEDROCK)) {
         byteValue |= 8;
      }

      if (blockState.is(Blocks.COBWEB)) {
         byteValue |= 16;
      }

      VoxelShape voxelShape = blockState.getCollisionShape(MinecraftAccess.MINECRAFT.level, blockPos);
      boolean flag = blockState.is(Blocks.COBWEB) || !blockState.isAir() && !blockState.canBeReplaced() && (!voxelShape.isEmpty() || blockState.blocksMotion() || blockState.isSolid());
      if (flag) {
         byteValue |= 32;
      }

      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("i", value6);
      jsonObject.addProperty("n", BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString());
      jsonObject.addProperty("f", Integer.valueOf(byteValue));
      JsonArray jsonArray = new JsonArray();

      for (AABB aABB : voxelShape.toAabbs()) {
         jsonArray.add(EntityState.m_2_JsonArray(aABB));
      }

      if (!jsonArray.isEmpty()) {
         jsonObject.add("c", jsonArray);
      }

      jsonObject.addProperty("h", blockState.getDestroySpeed(MinecraftAccess.MINECRAFT.level, blockPos));
      return jsonObject;
   }

   private JsonArray m_8_JsonArray() {
      JsonArray jsonArray = new JsonArray();

      for (int i = 0; i < this.blockStateList.size(); i++) {
         BlockState blockState = this.blockStateList.get(i);
         if (!blockState.isAir() && blockState.getFluidState().isEmpty()) {
            int value6 = m_9_int(blockState);
            float floatValue = 0.0F;
            boolean flag = false;
            boolean flag2 = false;
            if (value6 >= 0) {
               ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(value6);
               floatValue = itemStack.getDestroySpeed(blockState);
               flag = itemStack.isCorrectToolForDrops(blockState);
               flag2 = itemStack.is(ItemTags.SWORDS);
            }

            JsonArray jsonArray2 = new JsonArray();
            jsonArray2.add(i);
            jsonArray2.add(value6);
            jsonArray2.add(floatValue);
            jsonArray2.add(flag ? 1 : 0);
            jsonArray2.add(flag2 ? 1 : 0);
            jsonArray2.add(m_6(blockState));
            jsonArray2.add(this.m_9(blockState, value6));
            jsonArray2.add(this.m_8(blockState, value6));
            jsonArray.add(jsonArray2);
         }
      }

      return jsonArray;
   }

   private static int m_9_int(BlockState blockState) {
      int value6 = -1;
      float floatValue = 1.0F;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         float floatValue2 = itemStack.getDestroySpeed(blockState);
         if (floatValue2 <= 1.0F && itemStack.isCorrectToolForDrops(blockState)) {
            floatValue2 = 10.0F;
         }

         if (floatValue2 > floatValue) {
            floatValue = floatValue2;
            value6 = i;
         }
      }

      return value6;
   }

   private static float m_6(BlockState blockState) {
      float floatValue = 1.0F;

      for (int i = 0; i < 36; i++) {
         ItemStack itemStack = MinecraftAccess.MINECRAFT.player.getInventory().getItem(i);
         float floatValue2 = itemStack.getDestroySpeed(blockState);
         if (floatValue2 <= 1.0F && itemStack.isCorrectToolForDrops(blockState)) {
            floatValue2 = 10.0F;
         }

         if (floatValue2 > floatValue) {
            floatValue = floatValue2;
         }
      }

      return floatValue;
   }

   private float m_8(BlockState blockState, int value6) {
      ItemStack itemStack = value6 >= 0 ? MinecraftAccess.MINECRAFT.player.getInventory().getItem(value6) : MinecraftAccess.MINECRAFT.player.getMainHandItem();
      float floatValue = Math.max(1.0F, itemStack.getDestroySpeed(blockState));
      boolean flag = itemStack.isCorrectToolForDrops(blockState);
      if (flag) {
         Reference reference = MinecraftAccess.MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY);
         int value7 = EnchantmentHelper.getItemEnchantmentLevel(reference, itemStack);
         floatValue += value7 * value7 + 1;
      }

      return floatValue / (!flag && blockState.requiresCorrectToolForDrops() ? 100.0F : 30.0F);
   }

   private float m_9(BlockState blockState, int value6) {
      ItemStack itemStack = value6 >= 0 ? MinecraftAccess.MINECRAFT.player.getInventory().getItem(value6) : ItemStack.EMPTY;
      if (itemStack.is(ItemTags.SWORDS) || !itemStack.isCorrectToolForDrops(blockState) && itemStack.getDestroySpeed(blockState) <= 1.0F) {
         itemStack = ItemStack.EMPTY;
      }

      float floatValue = itemStack.getDestroySpeed(blockState);
      if (floatValue > 1.0F) {
         Reference reference = MinecraftAccess.MINECRAFT.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY);
         int value7 = EnchantmentHelper.getItemEnchantmentLevel(reference, itemStack);
         if (value7 > 0) {
            floatValue += value7 * value7 + 1;
         }
      }

      boolean flag = !blockState.requiresCorrectToolForDrops() || itemStack.isCorrectToolForDrops(blockState);
      return floatValue / (flag ? 30.0F : 100.0F);
   }

   private static long packPos(int value6, int value7, int value8) {
      return (long)(value6 & 4194303) << 42 | (long)(value8 & 4194303) << 20 | value7 & 1048575;
   }

   private static int m_5_int(long longValue) {
      return (int)(longValue >> 42) << 10 >> 10;
   }

   private static int m_6_int(long longValue) {
      return (int)(longValue >> 20 & 4194303L) << 10 >> 10;
   }

   private static int m_0_int(long longValue) {
      return (int)(longValue & 1048575L) << 12 >> 12;
   }
}
