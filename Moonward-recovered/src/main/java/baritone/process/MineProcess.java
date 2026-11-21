package baritone.process;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.pathing.goals.GoalRunAway;
import baritone.api.pathing.goals.GoalTwoBlocks;
import baritone.api.process.IMineProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMeta;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.BlockUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.SettingsUtil;
import baritone.api.utils.input.Input;
import baritone.cache.CachedChunk;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.MovementHelper;
import baritone.utils.BaritoneProcessHelper;
import baritone.utils.BlockStateInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MineProcess extends BaritoneProcessHelper implements IMineProcess {
   private BlockOptionalMetaLookup filter;
   private List<BlockPos> knownOreLocations;
   private List<BlockPos> blacklist;
   private Map<BlockPos, Long> anticipatedDrops;
   private BlockPos branchPoint;
   private GoalRunAway branchPointRunaway;
   private int desiredQuantity;
   private int tickCount;

   public MineProcess(Baritone var1) {
      super(var1);
   }

   @Override
   public boolean isActive() {
      return this.filter != null;
   }

   @Override
   public PathingCommand onTick(boolean var1, boolean var2) {
      if (this.desiredQuantity > 0) {
         int var3 = this.ctx
            .player()
            .getInventory()
            .getNonEquipmentItems()
            .stream()
            .filter(var1x -> this.filter.has(var1x))
            .mapToInt(ItemStack::getCount)
            .sum();
         if (var3 >= this.desiredQuantity) {
            this.logDirect("Have " + var3 + " valid items");
            this.cancel();
            return null;
         }
      }

      if (var1) {
         if (this.knownOreLocations.isEmpty() || !Baritone.settings().blacklistClosestOnFailure.value) {
            this.logDirect("Unable to find any path to " + this.filter + ", canceling mine");
            if (Baritone.settings().notificationOnMineFail.value) {
               this.logNotification("Unable to find any path to " + this.filter + ", canceling mine", true);
            }

            this.cancel();
            return null;
         }

         this.logDirect("Unable to find any path to " + this.filter + ", blacklisting presumably unreachable closest instance...");
         if (Baritone.settings().notificationOnMineFail.value) {
            this.logNotification("Unable to find any path to " + this.filter + ", blacklisting presumably unreachable closest instance...", true);
         }

         this.knownOreLocations.stream().min(Comparator.comparingDouble(this.ctx.playerFeet()::distSqr)).ifPresent(this.blacklist::add);
         this.knownOreLocations.removeIf(this.blacklist::contains);
      }

      this.updateLoucaSystem();
      int var9 = Baritone.settings().mineGoalUpdateInterval.value;
      ArrayList var4 = new ArrayList<>(this.knownOreLocations);
      if (var9 != 0 && this.tickCount++ % var9 == 0) {
         CalculationContext var5 = new CalculationContext(this.baritone, true);
         Baritone.getExecutor().execute(() -> this.rescan(var4, var5));
      }

      if (Baritone.settings().legitMine.value && !this.addNearby()) {
         this.cancel();
         return null;
      }

      Optional var10 = var4.stream()
         .filter(var1x -> ((net.minecraft.core.BlockPos)var1x).getX() == this.ctx.playerFeet().getX() && ((net.minecraft.core.BlockPos)var1x).getZ() == this.ctx.playerFeet().getZ())
         .filter(var1x -> ((net.minecraft.core.BlockPos)var1x).getY() >= this.ctx.playerFeet().getY())
         .filter(var1x -> !(BlockStateInterface.get(this.ctx, (net.minecraft.core.BlockPos)var1x).getBlock() instanceof AirBlock))
         .min(Comparator.comparingDouble(this.ctx.playerFeet().above()::distSqr));
      this.baritone.getInputOverrideHandler().clearAllKeys();
      if (var10.isPresent() && this.ctx.player().onGround()) {
         BlockPos var6 = (BlockPos)var10.get();
         BlockState var7 = this.baritone.bsi.get0(var6);
         if (!MovementHelper.avoidBreaking(this.baritone.bsi, var6.getX(), var6.getY(), var6.getZ(), var7)) {
            var var8 = RotationUtils.reachable(this.ctx, var6);
            if (var8.isPresent() && var2) {
               this.baritone.getLookBehavior().updateTarget((Rotation)var8.get(), true);
               MovementHelper.switchToBestToolFor(this.ctx, this.ctx.world().getBlockState(var6));
               if (this.ctx.isLookingAt(var6) || this.ctx.playerRotations().isReallyCloseTo((Rotation)var8.get())) {
                  this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, true);
               }

               return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
            }
         }
      }

      PathingCommand var11 = this.updateGoal();
      if (var11 == null) {
         this.cancel();
         return null;
      } else {
         return var11;
      }
   }

   private void updateLoucaSystem() {
      HashMap var1 = new HashMap<>(this.anticipatedDrops);
      this.ctx.getSelectedBlock().ifPresent(var2 -> {
         if (this.knownOreLocations.contains(var2)) {
            var1.put(var2, System.currentTimeMillis() + Baritone.settings().mineDropLoiterDurationMSThanksLouca.value);
         }
      });

      for (BlockPos var3 : this.anticipatedDrops.keySet()) {
         if ((Long)var1.get(var3) < System.currentTimeMillis()) {
            var1.remove(var3);
         }
      }

      this.anticipatedDrops = var1;
   }

   @Override
   public void onLostControl() {
      this.mine(0, (BlockOptionalMetaLookup)null);
   }

   @Override
   public String displayName0() {
      return "Mine " + this.filter;
   }

   private PathingCommand updateGoal() {
      BlockOptionalMetaLookup var1 = this.filterFilter();
      if (var1 == null) {
         return null;
      }

      boolean var2 = Baritone.settings().legitMine.value;
      List var3 = this.knownOreLocations;
      if (!var3.isEmpty()) {
         CalculationContext var7 = new CalculationContext(this.baritone);
         List var5 = prune(var7, new ArrayList<>(var3), var1, Baritone.settings().mineMaxOreLocationsCount.value, this.blacklist, this.droppedItemsScan());
         GoalComposite var6 = new GoalComposite((Goal[])var5.stream().map(var3x -> this.coalesce((net.minecraft.core.BlockPos)var3x, var5, var7)).toArray(Goal[]::new));
         this.knownOreLocations = var5;
         return new PathingCommand(var6, var2 ? PathingCommandType.FORCE_REVALIDATE_GOAL_AND_PATH : PathingCommandType.REVALIDATE_GOAL_AND_PATH);
      }

      if (!var2 && !Baritone.settings().exploreForBlocks.value) {
         return null;
      }

      int var4 = Baritone.settings().legitMineYLevel.value;
      if (this.branchPoint == null) {
         this.branchPoint = this.ctx.playerFeet();
      }

      if (this.branchPointRunaway == null) {
         this.branchPointRunaway = new GoalRunAway(1.0, var4, this.branchPoint) {
            @Override
            public boolean isInGoal(int var1, int var2x, int var3x) {
               return false;
            }

            @Override
            public double heuristic() {
               return Double.NEGATIVE_INFINITY;
            }
         };
      }

      return new PathingCommand(this.branchPointRunaway, PathingCommandType.REVALIDATE_GOAL_AND_PATH);
   }

   private void rescan(List<BlockPos> var1, CalculationContext var2) {
      BlockOptionalMetaLookup var3 = this.filterFilter();
      if (var3 != null) {
         if (!Baritone.settings().legitMine.value) {
            var var4 = this.droppedItemsScan();
            var var5 = searchWorld(var2, var3, Baritone.settings().mineMaxOreLocationsCount.value, var1, this.blacklist, var4);
            var5.addAll(var4);
            if (var5.isEmpty() && !Baritone.settings().exploreForBlocks.value) {
               this.logDirect("No locations for " + var3 + " known, cancelling");
               if (Baritone.settings().notificationOnMineFail.value) {
                  this.logNotification("No locations for " + var3 + " known, cancelling", true);
               }

               this.cancel();
            } else {
               this.knownOreLocations = var5;
            }
         }
      }
   }

   private boolean internalMiningGoal(BlockPos var1, CalculationContext var2, List<BlockPos> var3) {
      if (var3.contains(var1)) {
         return true;
      }

      BlockState var4 = var2.bsi.get0(var1);
      return Baritone.settings().internalMiningAirException.value && var4.getBlock() instanceof AirBlock
         ? true
         : this.filter.has(var4) && plausibleToBreak(var2, var1);
   }

   private Goal coalesce(BlockPos var1, List<BlockPos> var2, CalculationContext var3) {
      boolean var4 = !(this.baritone.bsi.get0(var1.above()).getBlock() instanceof FallingBlock);
      if (!Baritone.settings().forceInternalMining.value) {
         return var4 ? new MineProcess.GoalThreeBlocks(var1) : new GoalTwoBlocks(var1);
      } else {
         boolean var5 = this.internalMiningGoal(var1.above(), var3, var2);
         boolean var6 = this.internalMiningGoal(var1.below(), var3, var2);
         boolean var7 = this.internalMiningGoal(var1.below(2), var3, var2);
         if (var5 == var6) {
            return var7 && var4 ? new MineProcess.GoalThreeBlocks(var1) : new GoalTwoBlocks(var1);
         } else if (var5) {
            return new GoalBlock(var1);
         } else {
            return var7 && var4 ? new GoalTwoBlocks(var1.below()) : new GoalBlock(var1.below());
         }
      }
   }

   public List<BlockPos> droppedItemsScan() {
      if (!Baritone.settings().mineScanDroppedItems.value) {
         return Collections.emptyList();
      }

      ArrayList var1 = new ArrayList();

      for (Entity var3 : ((ClientLevel)this.ctx.world()).entitiesForRendering()) {
         if (var3 instanceof ItemEntity var4 && this.filter.has(var4.getItem())) {
            var1.add(var3.blockPosition());
         }
      }

      var1.addAll(this.anticipatedDrops.keySet());
      return var1;
   }

   public static List<BlockPos> searchWorld(
      CalculationContext var0, BlockOptionalMetaLookup var1, int var2, List<BlockPos> var3, List<BlockPos> var4, List<BlockPos> var5
   ) {
      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();

      for (BlockOptionalMeta var9 : var1.blocks()) {
         Block var10 = var9.getBlock();
         if (CachedChunk.BLOCKS_TO_KEEP_TRACK_OF.contains(var10)) {
            BetterBlockPos var11 = var0.baritone.getPlayerContext().playerFeet();
            var6.addAll(
               var0.worldData
                  .getCachedWorld()
                  .getLocationsOf(BlockUtils.blockToString(var10), Baritone.settings().maxCachedWorldScanCount.value, var11.x, var11.z, 2)
            );
         } else {
            var7.add(var10);
         }
      }

      var var12 = prune(var0, var6, var1, var2, var4, var5);
      if (!var7.isEmpty() || Baritone.settings().extendCacheOnThreshold.value && var12.size() < var2) {
         var12.addAll(BaritoneAPI.getProvider().getWorldScanner().scanChunkRadius(var0.getBaritone().getPlayerContext(), var1, var2, 10, 32));
      }

      var12.addAll(var3);
      return prune(var0, var12, var1, var2, var4, var5);
   }

   private boolean addNearby() {
      var var1 = this.droppedItemsScan();
      this.knownOreLocations.addAll(var1);
      BetterBlockPos var2 = this.ctx.playerFeet();
      BlockStateInterface var3 = new BlockStateInterface(this.ctx);
      BlockOptionalMetaLookup var4 = this.filterFilter();
      if (var4 == null) {
         return false;
      }

      byte var5 = 10;
      double var6 = 20.0;

      for (int var8 = var2.getX() - var5; var8 <= var2.getX() + var5; var8++) {
         for (int var9 = var2.getY() - var5; var9 <= var2.getY() + var5; var9++) {
            for (int var10 = var2.getZ() - var5; var10 <= var2.getZ() + var5; var10++) {
               if (var4.has(var3.get0(var8, var9, var10))) {
                  BlockPos var11 = new BlockPos(var8, var9, var10);
                  if (Baritone.settings().legitMineIncludeDiagonals.value && this.knownOreLocations.stream().anyMatch(var1x -> var1x.distSqr(var11) <= 2.0)
                     || RotationUtils.reachable(this.ctx, var11, var6).isPresent()) {
                     this.knownOreLocations.add(var11);
                  }
               }
            }
         }
      }

      this.knownOreLocations = prune(
         new CalculationContext(this.baritone), this.knownOreLocations, var4, Baritone.settings().mineMaxOreLocationsCount.value, this.blacklist, var1
      );
      return true;
   }

   private static List<BlockPos> prune(
      CalculationContext var0, List<BlockPos> var1, BlockOptionalMetaLookup var2, int var3, List<BlockPos> var4, List<BlockPos> var5
   ) {
      var5.removeIf(var3x -> {
         for (BlockPos var5x : var1) {
            if (var5x.distSqr(var3x) <= 9.0 && var2.has(var0.get(var5x.getX(), var5x.getY(), var5x.getZ())) && plausibleToBreak(var0, var5x)) {
               return true;
            }
         }

         return false;
      });
      List var6 = var1.stream()
         .distinct()
         .filter(
            var3x -> !var0.bsi.worldContainsLoadedChunk(var3x.getX(), var3x.getZ())
               || var2.has(var0.get(var3x.getX(), var3x.getY(), var3x.getZ()))
               || var5.contains(var3x)
         )
         .filter(var1x -> plausibleToBreak(var0, var1x))
         .filter(var1x -> Baritone.settings().allowOnlyExposedOres.value ? isNextToAir(var0, var1x) : true)
         .filter(var1x -> var1x.getY() >= Baritone.settings().minYLevelWhileMining.value + var0.world.dimensionType().minY())
         .filter(var0x -> var0x.getY() <= Baritone.settings().maxYLevelWhileMining.value)
         .filter(var1x -> !var4.contains(var1x))
         .sorted(Comparator.comparingDouble(var0.getBaritone().getPlayerContext().player().blockPosition()::distSqr))
         .collect(Collectors.toList());
      return var6.size() > var3 ? var6.subList(0, var3) : var6;
   }

   public static boolean isNextToAir(CalculationContext var0, BlockPos var1) {
      int var2 = Baritone.settings().allowOnlyExposedOresDistance.value;

      for (int var3 = -var2; var3 <= var2; var3++) {
         for (int var4 = -var2; var4 <= var2; var4++) {
            for (int var5 = -var2; var5 <= var2; var5++) {
               if (Math.abs(var3) + Math.abs(var4) + Math.abs(var5) <= var2
                  && MovementHelper.isTransparent(var0.getBlock(var1.getX() + var3, var1.getY() + var4, var1.getZ() + var5))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean plausibleToBreak(CalculationContext var0, BlockPos var1) {
      BlockState var2 = var0.bsi.get0(var1);
      if (MovementHelper.getMiningDurationTicks(var0, var1.getX(), var1.getY(), var1.getZ(), var2, true) >= 1000000.0) {
         return false;
      } else {
         return MovementHelper.avoidBreaking(var0.bsi, var1.getX(), var1.getY(), var1.getZ(), var2)
            ? false
            : var0.bsi.get0(var1.above()).getBlock() != Blocks.BEDROCK || var0.bsi.get0(var1.below()).getBlock() != Blocks.BEDROCK;
      }
   }

   @Override
   public void mineByName(int var1, String... var2) {
      this.mine(var1, new BlockOptionalMetaLookup(var2));
   }

   @Override
   public void mine(int var1, BlockOptionalMetaLookup var2) {
      this.filter = var2;
      if (this.filterFilter() == null) {
         this.filter = null;
      }

      this.desiredQuantity = var1;
      this.knownOreLocations = new ArrayList<>();
      this.blacklist = new ArrayList<>();
      this.branchPoint = null;
      this.branchPointRunaway = null;
      this.anticipatedDrops = new HashMap<>();
      if (var2 != null) {
         this.rescan(new ArrayList<>(), new CalculationContext(this.baritone));
      }
   }

   private BlockOptionalMetaLookup filterFilter() {
      if (this.filter == null) {
         return null;
      }

      if (!Baritone.settings().allowBreak.value) {
         BlockOptionalMetaLookup var1 = new BlockOptionalMetaLookup(
            this.filter
               .blocks()
               .stream()
               .filter(var0 -> Baritone.settings().allowBreakAnyway.value.contains(var0.getBlock()))
               .toArray(BlockOptionalMeta[]::new)
         );
         if (var1.blocks().isEmpty()) {
            this.logDirect("Unable to mine when allowBreak is false and target block is not in allowBreakAnyway!");
            return null;
         } else {
            return var1;
         }
      } else {
         return this.filter;
      }
   }

   private static class GoalThreeBlocks extends GoalTwoBlocks {
      public GoalThreeBlocks(BlockPos var1) {
         super(var1);
      }

      @Override
      public boolean isInGoal(int var1, int var2, int var3) {
         return var1 == this.x && (var2 == this.y || var2 == this.y - 1 || var2 == this.y - 2) && var3 == this.z;
      }

      @Override
      public double heuristic(int var1, int var2, int var3) {
         int var4 = var1 - this.x;
         int var5 = var2 - this.y;
         int var6 = var3 - this.z;
         return GoalBlock.calculate(var4, var5 < -1 ? var5 + 2 : (var5 == -1 ? 0 : var5), var6);
      }

      @Override
      public boolean equals(Object var1) {
         return super.equals(var1);
      }

      @Override
      public int hashCode() {
         return super.hashCode() * 393857768;
      }

      @Override
      public String toString() {
         return String.format(
            "GoalThreeBlocks{x=%s,y=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.y), SettingsUtil.maybeCensor(this.z)
         );
      }
   }
}
