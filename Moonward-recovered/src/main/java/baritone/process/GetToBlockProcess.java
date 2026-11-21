package baritone.process;

import baritone.Baritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalRunAway;
import baritone.api.pathing.goals.GoalTwoBlocks;
import baritone.api.process.IGetToBlockProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.BlockOptionalMeta;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.MovementHelper;
import baritone.utils.BaritoneProcessHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GetToBlockProcess extends BaritoneProcessHelper implements IGetToBlockProcess {
   private BlockOptionalMeta gettingTo;
   private List<BlockPos> knownLocations;
   private List<BlockPos> blacklist;
   private BlockPos start;
   private int tickCount = 0;
   private int arrivalTickCount = 0;

   public GetToBlockProcess(Baritone var1) {
      super(var1);
   }

   @Override
   public void getToBlock(BlockOptionalMeta var1) {
      this.onLostControl();
      this.gettingTo = var1;
      this.start = this.ctx.playerFeet();
      this.blacklist = new ArrayList<>();
      this.arrivalTickCount = 0;
      this.rescan(new ArrayList<>(), new GetToBlockProcess.GetToBlockCalculationContext(false));
   }

   @Override
   public boolean isActive() {
      return this.gettingTo != null;
   }

   @Override
   public synchronized PathingCommand onTick(boolean var1, boolean var2) {
      if (this.knownLocations == null) {
         this.rescan(new ArrayList<>(), new GetToBlockProcess.GetToBlockCalculationContext(false));
      }

      if (this.knownLocations.isEmpty()) {
         if (Baritone.settings().exploreForBlocks.value && !var1) {
            return new PathingCommand(new GoalRunAway(1.0, this.start) {
               @Override
               public boolean isInGoal(int var1, int var2x, int var3) {
                  return false;
               }

               @Override
               public double heuristic() {
                  return Double.NEGATIVE_INFINITY;
               }
            }, PathingCommandType.FORCE_REVALIDATE_GOAL_AND_PATH);
         }

         this.logDirect("No known locations of " + this.gettingTo + ", canceling GetToBlock");
         if (var2) {
            this.onLostControl();
         }

         return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
      } else {
         GoalComposite var3 = new GoalComposite(this.knownLocations.stream().map(this::createGoal).toArray(Goal[]::new));
         if (var1) {
            if (Baritone.settings().blacklistClosestOnFailure.value) {
               this.logDirect("Unable to find any path to " + this.gettingTo + ", blacklisting presumably unreachable closest instances...");
               this.blacklistClosest();
               return this.onTick(false, var2);
            }

            this.logDirect("Unable to find any path to " + this.gettingTo + ", canceling GetToBlock");
            if (var2) {
               this.onLostControl();
            }

            return new PathingCommand(var3, PathingCommandType.CANCEL_AND_SET_GOAL);
         } else {
            int var4 = Baritone.settings().mineGoalUpdateInterval.value;
            if (var4 != 0 && this.tickCount++ % var4 == 0) {
               ArrayList var5 = new ArrayList<>(this.knownLocations);
               GetToBlockProcess.GetToBlockCalculationContext var6 = new GetToBlockProcess.GetToBlockCalculationContext(true);
               Baritone.getExecutor().execute(() -> this.rescan(var5, var6));
            }

            if (var3.isInGoal(this.ctx.playerFeet()) && var3.isInGoal(this.baritone.getPathingBehavior().pathStart()) && var2) {
               if (!this.rightClickOnArrival(this.gettingTo.getBlock())) {
                  this.onLostControl();
                  return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
               }

               if (this.rightClick()) {
                  this.onLostControl();
                  return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
               }
            }

            return new PathingCommand(var3, PathingCommandType.REVALIDATE_GOAL_AND_PATH);
         }
      }
   }

   @Override
   public synchronized boolean blacklistClosest() {
      ArrayList<BlockPos> var1 = new ArrayList<>();
      this.knownLocations.stream().min(Comparator.comparingDouble(this.ctx.playerFeet()::distSqr)).ifPresent(var1::add);

      label32:
      while (true) {
         for (BlockPos var3 : this.knownLocations) {
            for (BlockPos var5 : var1) {
               if (this.areAdjacent(var3, var5)) {
                  var1.add(var3);
                  this.knownLocations.remove(var3);
                  continue label32;
               }
            }
         }

         switch (var1.size()) {
            default:
               this.logDebug("Blacklisting unreachable locations " + var1);
               this.blacklist.addAll(var1);
               return !var1.isEmpty();
         }
      }
   }

   private boolean areAdjacent(BlockPos var1, BlockPos var2) {
      int var3 = Math.abs(var1.getX() - var2.getX());
      int var4 = Math.abs(var1.getY() - var2.getY());
      int var5 = Math.abs(var1.getZ() - var2.getZ());
      return var3 + var4 + var5 == 1;
   }

   @Override
   public synchronized void onLostControl() {
      this.gettingTo = null;
      this.knownLocations = null;
      this.start = null;
      this.blacklist = null;
      this.baritone.getInputOverrideHandler().clearAllKeys();
   }

   @Override
   public String displayName0() {
      return this.knownLocations.isEmpty()
         ? "Exploring randomly to find " + this.gettingTo + ", no known locations"
         : "Get To " + this.gettingTo + ", " + this.knownLocations.size() + " known locations";
   }

   private synchronized void rescan(List<BlockPos> var1, CalculationContext var2) {
      var var3 = MineProcess.searchWorld(var2, new BlockOptionalMetaLookup(this.gettingTo), 64, var1, this.blacklist, Collections.emptyList());
      var3.removeIf(this.blacklist::contains);
      this.knownLocations = var3;
   }

   private Goal createGoal(BlockPos var1) {
      if (this.walkIntoInsteadOfAdjacent(this.gettingTo.getBlock())) {
         return new GoalTwoBlocks(var1);
      } else {
         return this.blockOnTopMustBeRemoved(this.gettingTo.getBlock()) && MovementHelper.isBlockNormalCube(this.baritone.bsi.get0(var1.above()))
            ? new GoalBlock(var1.above())
            : new GoalGetToBlock(var1);
      }
   }

   private boolean rightClick() {
      for (BlockPos var2 : this.knownLocations) {
         var var3 = RotationUtils.reachable(this.ctx, var2, this.ctx.playerController().getBlockReachDistance());
         if (var3.isPresent()) {
            this.baritone.getLookBehavior().updateTarget((Rotation)var3.get(), true);
            if (this.knownLocations.contains(this.ctx.getSelectedBlock().orElse(null))) {
               this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
               System.out.println(this.ctx.player().containerMenu);
               if (!(this.ctx.player().containerMenu instanceof InventoryMenu)) {
                  return true;
               }
            }

            if (this.arrivalTickCount++ > 20) {
               this.logDirect("Right click timed out");
               return true;
            }

            return false;
         }
      }

      this.logDirect("Arrived but failed to right click open");
      return true;
   }

   private boolean walkIntoInsteadOfAdjacent(Block var1) {
      return !Baritone.settings().enterPortal.value ? false : var1 == Blocks.NETHER_PORTAL;
   }

   private boolean rightClickOnArrival(Block var1) {
      return !Baritone.settings().rightClickContainerOnArrival.value
         ? false
         : var1 == Blocks.CRAFTING_TABLE
            || var1 == Blocks.FURNACE
            || var1 == Blocks.BLAST_FURNACE
            || var1 == Blocks.ENDER_CHEST
            || var1 == Blocks.CHEST
            || var1 == Blocks.TRAPPED_CHEST;
   }

   private boolean blockOnTopMustBeRemoved(Block var1) {
      return !this.rightClickOnArrival(var1) ? false : var1 == Blocks.ENDER_CHEST || var1 == Blocks.CHEST || var1 == Blocks.TRAPPED_CHEST;
   }

   public class GetToBlockCalculationContext extends CalculationContext {
      public GetToBlockCalculationContext(boolean var2) {
         super(GetToBlockProcess.this.baritone, var2);
      }

      @Override
      public double breakCostMultiplierAt(int var1, int var2, int var3, BlockState var4) {
         return 1.0;
      }
   }
}
