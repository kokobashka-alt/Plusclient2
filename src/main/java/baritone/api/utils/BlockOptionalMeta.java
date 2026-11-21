package baritone.api.utils;

import baritone.api.utils.accessor.IItemStack;
import baritone.api.utils.accessor.ILootTable;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries.Holder;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import sun.misc.Unsafe;

public class BlockOptionalMeta {
   private static final Pattern PATTERN = Pattern.compile("^(?<id>.+?)(?:\\[(?<properties>.+?)?\\])?$");
   private final Block block;
   private final String propertiesDescription;
   private final Set<BlockState> blockstates;
   private final ImmutableSet<Integer> stateHashes;
   private final ImmutableSet<Integer> stackHashes;
   private static Map<Block, List<Item>> drops = new HashMap<>();
   private static Method getVanillaServerPack;

   public BlockOptionalMeta(@Nonnull Block var1) {
      this.block = var1;
      this.propertiesDescription = "{}";
      this.blockstates = getStates(var1, Collections.emptyMap());
      this.stateHashes = getStateHashes(this.blockstates);
      this.stackHashes = getStackHashes(this.blockstates);
   }

   public BlockOptionalMeta(@Nonnull String var1) {
      Matcher var2 = PATTERN.matcher(var1);
      if (!var2.find()) {
         throw new IllegalArgumentException("invalid block selector");
      }

      this.block = BlockUtils.stringToBlockRequired(var2.group("id"));
      String var3 = var2.group("properties");
      Map var4 = var3 != null && !var3.equals("") ? parseProperties(this.block, var3) : Collections.emptyMap();
      this.propertiesDescription = var3 == null ? "{}" : "{" + var3.replace("=", ":") + "}";
      this.blockstates = getStates(this.block, var4);
      this.stateHashes = getStateHashes(this.blockstates);
      this.stackHashes = getStackHashes(this.blockstates);
   }

   private static <C extends Comparable<C>, P extends Property<C>> P castToIProperty(Object var0) {
      return (P)var0;
   }

   private static Map<Property<?>, ?> parseProperties(Block var0, String var1) {
      Builder var2 = ImmutableMap.builder();

      for (String var6 : var1.split(",")) {
         String[] var7 = var6.split("=");
         if (var7.length != 2) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid property-value pair", var6));
         }

         String var8 = var7[0];
         String var9 = var7[1];
         Property var10 = var0.getStateDefinition().getProperty(var8);
         Comparable var11 = (Comparable)castToIProperty(var10)
            .getValue(var9)
            .orElseThrow(() -> new IllegalArgumentException(String.format("\"%s\" is not a valid value for %s on %s", var9, var10, var0)));
         var2.put(var10, var11);
      }

      return var2.build();
   }

   private static Set<BlockState> getStates(@Nonnull Block var0, @Nonnull Map<Property<?>, ?> var1) {
      return var0.getStateDefinition()
         .getPossibleStates()
         .stream()
         .filter(var1x -> var1.entrySet().stream().allMatch(var1xx -> var1x.getValue((Property)var1xx.getKey()) == var1xx.getValue()))
         .collect(Collectors.toSet());
   }

   private static ImmutableSet<Integer> getStateHashes(Set<BlockState> var0) {
      return ImmutableSet.copyOf(var0.stream().map(StateHolder::hashCode).toArray(Integer[]::new));
   }

   private static ImmutableSet<Integer> getStackHashes(Set<BlockState> var0) {
      return ImmutableSet.copyOf(
         var0.stream()
            .flatMap(var0x -> drops(var0x.getBlock()).stream().map(var0xx -> new ItemStack(var0xx, 1)))
            .map(var0x -> ((IItemStack)(Object)var0x).getBaritoneHash())
            .toArray(Integer[]::new)
      );
   }

   public Block getBlock() {
      return this.block;
   }

   public boolean matches(@Nonnull Block var1) {
      return var1 == this.block;
   }

   public boolean matches(@Nonnull BlockState var1) {
      Block var2 = var1.getBlock();
      return var2 == this.block && this.stateHashes.contains(var1.hashCode());
   }

   public boolean matches(ItemStack var1) {
      int var2 = ((IItemStack)(Object)var1).getBaritoneHash();
      var2 -= var1.getDamageValue();
      return this.stackHashes.contains(var2);
   }

   @Override
   public String toString() {
      return String.format("BlockOptionalMeta{block=%s,properties=%s}", this.block, this.propertiesDescription);
   }

   public BlockState getAnyBlockState() {
      return this.blockstates.size() > 0 ? this.blockstates.iterator().next() : null;
   }

   public Set<BlockState> getAllBlockStates() {
      return this.blockstates;
   }

   public Set<Integer> stackHashes() {
      return this.stackHashes;
   }

   private static VanillaPackResources getVanillaServerPack() {
      if (getVanillaServerPack == null) {
         getVanillaServerPack = Arrays.stream(ServerPacksSource.class.getDeclaredMethods())
            .filter(var0 -> var0.getReturnType() == VanillaPackResources.class)
            .findFirst()
            .orElseThrow();
         getVanillaServerPack.setAccessible(true);
      }

      try {
         return (VanillaPackResources)getVanillaServerPack.invoke(null);
      } catch (Exception var1) {
         var1.printStackTrace();
         return null;
      }
   }

   private static synchronized List<Item> drops(Block var0) {
      return drops.computeIfAbsent(
         var0,
         var1 -> {
            var var2 = var1.getLootTable();
            if (var2.isEmpty()) {
               return Collections.emptyList();
            }

            ArrayList var3 = new ArrayList();

            try {
               BlockOptionalMeta.ServerLevelStub var4 = BlockOptionalMeta.ServerLevelStub.fastCreate();
               net.minecraft.world.level.storage.loot.LootParams.Builder var5 = new net.minecraft.world.level.storage.loot.LootParams.Builder(var4)
                  .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                  .withParameter(LootContextParams.BLOCK_STATE, var0.defaultBlockState())
                  .withParameter(LootContextParams.TOOL, new ItemStack(Items.NETHERITE_PICKAXE, 1));
               getDrops(var1, var5).stream().map(ItemStack::getItem).forEach(var3::add);
            } catch (Throwable var6) {
               var3.clear();
            }

            if (var3.isEmpty()) {
               Item var7 = var1.asItem();
               if (var7 != Items.AIR) {
                  var3.add(var7);
               }
            }

            return var3;
         }
      );
   }

   private static List<ItemStack> getDrops(Block var0, net.minecraft.world.level.storage.loot.LootParams.Builder var1) {
      var var2 = var0.getLootTable();
      if (var2.isEmpty()) {
         return Collections.emptyList();
      }

      LootParams var3 = var1.withParameter(LootContextParams.BLOCK_STATE, var0.defaultBlockState()).create(LootContextParamSets.BLOCK);
      BlockOptionalMeta.ServerLevelStub var4 = (BlockOptionalMeta.ServerLevelStub)var3.getLevel();
      LootTable var5 = var4.holder().getLootTable((ResourceKey<LootTable>)var2.get());
      return ((ILootTable)var5)
         .invokeGetRandomItems(new net.minecraft.world.level.storage.loot.LootContext.Builder(var3).withOptionalRandomSeed(1L).create(null));
   }

   public static class ServerLevelStub extends ServerLevel {
      private static Minecraft client = Minecraft.getInstance();
      private static Unsafe unsafe = getUnsafe();
      private static CompletableFuture<RegistryAccess> registryAccess = load();

      public ServerLevelStub(
         MinecraftServer var1,
         Executor var2,
         LevelStorageAccess var3,
         ServerLevelData var4,
         ResourceKey<Level> var5,
         LevelStem var6,
         boolean var7,
         long var8,
         List<CustomSpawner> var10,
         boolean var11
      ) {
         super(var1, var2, var3, var4, var5, var6, var7, var8, var10, var11);
      }

      @Override
      public FeatureFlagSet enabledFeatures() {
         assert client.level != null;
         return client.level.enabledFeatures();
      }

      public static BlockOptionalMeta.ServerLevelStub fastCreate() {
         try {
            return (BlockOptionalMeta.ServerLevelStub)unsafe.allocateInstance(BlockOptionalMeta.ServerLevelStub.class);
         } catch (InstantiationException var1) {
            throw new RuntimeException(var1);
         }
      }

      @Override
      public RegistryAccess registryAccess() {
         return client.level != null ? client.level.registryAccess() : registryAccess.join();
      }

      public Holder holder() {
         return new Holder(this.registryAccess().freeze());
      }

      public static Unsafe getUnsafe() {
         try {
            Field var0 = Unsafe.class.getDeclaredField("theUnsafe");
            var0.setAccessible(true);
            return (Unsafe)var0.get(null);
         } catch (Exception var1) {
            throw new RuntimeException(var1);
         }
      }

      public static CompletableFuture<RegistryAccess> load() {
         MultiPackResourceManager var0 = new MultiPackResourceManager(PackType.SERVER_DATA, List.of(ServerPacksSource.createVanillaPackSource()));
         LayeredRegistryAccess var1 = RegistryLayer.createRegistryAccess();
         List var2 = TagLoader.loadTagsForExistingRegistries(var0, var1.getLayer(RegistryLayer.STATIC));
         var var3 = TagLoader.buildUpdatedLookups(var1.getAccessForLoading(RegistryLayer.WORLDGEN), var2);
         LayeredRegistryAccess var4 = var1.replaceFrom(
            RegistryLayer.WORLDGEN, (RegistryAccess.Frozen)RegistryDataLoader.load(var0, var3, RegistryDataLoader.WORLDGEN_REGISTRIES, ForkJoinPool.commonPool()).join()
         );
         return ReloadableServerRegistries.reload(var4, var2, var0, ForkJoinPool.commonPool()).thenApply(var0x -> ((net.minecraft.server.ReloadableServerRegistries.LoadResult)var0x).layers().compositeAccess());
      }
   }
}
