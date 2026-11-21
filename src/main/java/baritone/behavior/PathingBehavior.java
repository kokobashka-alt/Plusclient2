package baritone.behavior;

import baritone.Baritone;
import baritone.api.behavior.IPathingBehavior;
import baritone.api.event.events.PathEvent;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.events.SprintStateEvent;
import baritone.api.event.events.TickEvent;
import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.process.PathingCommand;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.api.utils.PathCalculationResult;
import baritone.api.utils.interfaces.IGoalRenderPos;
import baritone.pathing.calc.AStarPathFinder;
import baritone.pathing.calc.AbstractNodeCostSearch;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.path.PathExecutor;
import baritone.utils.PathRenderer;
import baritone.utils.PathingCommandContext;
import baritone.utils.pathing.Favoring;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class PathingBehavior extends Behavior implements IPathingBehavior, Helper {
   private PathExecutor current;
   private PathExecutor next;
   private Goal goal;
   private CalculationContext context;
   private int ticksElapsedSoFar;
   private BetterBlockPos startPosition;
   private boolean safeToCancel;
   private boolean pauseRequestedLastTick;
   private boolean unpausedLastTick;
   private boolean pausedThisTick;
   private boolean cancelRequested;
   private boolean calcFailedLastTick;
   private volatile AbstractNodeCostSearch inProgress;
   private final Object pathCalcLock = new Object();
   private final Object pathPlanLock = new Object();
   private boolean lastAutoJump;
   private BetterBlockPos expectedSegmentStart;
   private final LinkedBlockingQueue<PathEvent> toDispatch = new LinkedBlockingQueue<>();

   public PathingBehavior(Baritone var1) {
      super(var1);
   }

   private void queuePathEvent(PathEvent var1) {
      this.toDispatch.add(var1);
   }

   private void dispatchEvents() {
      ArrayList<PathEvent> var1 = new ArrayList<>();
      this.toDispatch.drainTo(var1);
      this.calcFailedLastTick = var1.contains(PathEvent.CALC_FAILED);

      for (PathEvent var3 : var1) {
         this.baritone.getGameEventHandler().onPathEvent(var3);
      }
   }

   @Override
   public void onTick(TickEvent var1) {
      this.dispatchEvents();
      if (var1.getType() == TickEvent.Type.OUT) {
         this.secretInternalSegmentCancel();
         this.baritone.getPathingControlManager().cancelEverything();
      } else {
         this.expectedSegmentStart = this.pathStart();
         this.baritone.getPathingControlManager().preTick();
         this.tickPath();
         this.ticksElapsedSoFar++;
         this.dispatchEvents();
      }
   }

   @Override
   public void onPlayerSprintState(SprintStateEvent var1) {
      if (this.isPathing()) {
         var1.setState(this.current.isSprinting());
      }
   }

   private void tickPath() {
      this.pausedThisTick = false;
      if (this.pauseRequestedLastTick && this.safeToCancel) {
         this.pauseRequestedLastTick = false;
         if (this.unpausedLastTick) {
            this.baritone.getInputOverrideHandler().clearAllKeys();
            this.baritone.getInputOverrideHandler().getBlockBreakHelper().stopBreakingBlock();
         }

         this.unpausedLastTick = false;
         this.pausedThisTick = true;
      } else {
         this.unpausedLastTick = true;
         if (this.cancelRequested) {
            this.cancelRequested = false;
            this.baritone.getInputOverrideHandler().clearAllKeys();
         }

         synchronized (this.pathPlanLock) {
            synchronized (this.pathCalcLock) {
               if (this.inProgress != null) {
                  BetterBlockPos var3 = this.inProgress.getStart();
                  var var4 = this.inProgress.bestPathSoFar();
                  if ((this.current == null || !this.current.getPath().getDest().equals(var3))
                     && !var3.equals(this.ctx.playerFeet())
                     && !var3.equals(this.expectedSegmentStart)
                     && (
                        !var4.isPresent()
                           || !((IPath)var4.get()).positions().contains(this.ctx.playerFeet())
                              && !((IPath)var4.get()).positions().contains(this.expectedSegmentStart)
                     )) {
                     this.inProgress.cancel();
                  }
               }
            }

            if (this.current != null) {
               this.safeToCancel = this.current.onTick();
               if (this.current.failed() || this.current.finished()) {
                  this.current = null;
                  if (this.goal != null && !this.goal.isInGoal(this.ctx.playerFeet())) {
                     if (this.next != null
                        && !this.next.getPath().positions().contains(this.ctx.playerFeet())
                        && !this.next.getPath().positions().contains(this.expectedSegmentStart)) {
                        this.logDebug("Discarding next path as it does not contain current position");
                        this.queuePathEvent(PathEvent.DISCARD_NEXT);
                        this.next = null;
                     }

                     if (this.next != null) {
                        this.logDebug("Continuing on to planned next path");
                        this.queuePathEvent(PathEvent.CONTINUING_ONTO_PLANNED_NEXT);
                        this.current = this.next;
                        this.next = null;
                        this.current.onTick();
                     } else {
                        synchronized (this.pathCalcLock) {
                           if (this.inProgress != null) {
                              this.queuePathEvent(PathEvent.PATH_FINISHED_NEXT_STILL_CALCULATING);
                              return;
                           }

                           this.queuePathEvent(PathEvent.CALC_STARTED);
                           this.findPathInNewThread(this.expectedSegmentStart, true, this.context);
                        }
                     }
                  } else {
                     this.logDebug("All done. At " + this.goal);
                     this.queuePathEvent(PathEvent.AT_GOAL);
                     this.next = null;
                     if (Baritone.settings().disconnectOnArrival.value && this.ctx.world() instanceof ClientLevel var14) {
                        var14.disconnect(Component.literal("[Baritone] Arrived at goal!"));
                     }
                  }
               } else if (this.safeToCancel && this.next != null && this.next.snipsnapifpossible()) {
                  this.logDebug("Splicing into planned next path early...");
                  this.queuePathEvent(PathEvent.SPLICING_ONTO_NEXT_EARLY);
                  this.current = this.next;
                  this.next = null;
                  this.current.onTick();
               } else {
                  if (Baritone.settings().splicePath.value) {
                     this.current = this.current.trySplice(this.next);
                  }

                  if (this.next != null && this.current.getPath().getDest().equals(this.next.getPath().getDest())) {
                     this.next = null;
                  }

                  synchronized (this.pathCalcLock) {
                     if (this.inProgress != null) {
                        return;
                     }

                     if (this.next != null) {
                        return;
                     }

                     if (this.goal == null || this.goal.isInGoal(this.current.getPath().getDest())) {
                        return;
                     }

                     if (this.ticksRemainingInSegment(false).get() < Baritone.settings().planningTickLookahead.value.intValue()) {
                        this.logDebug("Path almost over. Planning ahead...");
                        this.queuePathEvent(PathEvent.NEXT_SEGMENT_CALC_STARTED);
                        this.findPathInNewThread(this.current.getPath().getDest(), false, this.context);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onPlayerUpdate(PlayerUpdateEvent var1) {
      if (this.current != null) {
         switch (var1.getState()) {
            case PRE:
               this.lastAutoJump = this.ctx.minecraft().options.autoJump().get();
               this.ctx.minecraft().options.autoJump().set(false);
               break;
            case POST:
               this.ctx.minecraft().options.autoJump().set(this.lastAutoJump);
         }
      }
   }

   public void secretInternalSetGoal(Goal var1) {
      this.goal = var1;
   }

   public boolean secretInternalSetGoalAndPath(PathingCommand var1) {
      this.secretInternalSetGoal(var1.goal);
      if (var1 instanceof PathingCommandContext) {
         this.context = ((PathingCommandContext)var1).desiredCalcContext;
      } else {
         this.context = new CalculationContext(this.baritone, true);
      }

      if (this.goal == null) {
         return false;
      }

      if (this.goal.isInGoal(this.ctx.playerFeet())) {
         return false;
      }

      synchronized (this.pathPlanLock) {
         if (this.current != null) {
            return false;
         }

         boolean var10000;
         synchronized (this.pathCalcLock) {
            if (this.inProgress != null) {
               return false;
            }

            this.queuePathEvent(PathEvent.CALC_STARTED);
            this.findPathInNewThread(this.expectedSegmentStart, true, this.context);
            var10000 = true;
         }

         return var10000;
      }
   }

   @Override
   public Goal getGoal() {
      return this.goal;
   }

   @Override
   public boolean isPathing() {
      return this.hasPath() && !this.pausedThisTick;
   }

   public PathExecutor getCurrent() {
      return this.current;
   }

   public PathExecutor getNext() {
      return this.next;
   }

   @Override
   public Optional<AbstractNodeCostSearch> getInProgress() {
      return Optional.ofNullable(this.inProgress);
   }

   public boolean isSafeToCancel() {
      return this.current != null ? this.safeToCancel : !this.baritone.getElytraProcess().isActive() || this.baritone.getElytraProcess().isSafeToCancel();
   }

   public void requestPause() {
      this.pauseRequestedLastTick = true;
   }

   public boolean cancelSegmentIfSafe() {
      if (this.isSafeToCancel()) {
         this.secretInternalSegmentCancel();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean cancelEverything() {
      boolean var1 = this.isSafeToCancel();
      if (var1) {
         this.secretInternalSegmentCancel();
      }

      this.baritone.getPathingControlManager().cancelEverything();
      return var1;
   }

   public boolean calcFailedLastTick() {
      return this.calcFailedLastTick;
   }

   public void softCancelIfSafe() {
      synchronized (this.pathPlanLock) {
         this.getInProgress().ifPresent(AbstractNodeCostSearch::cancel);
         if (!this.isSafeToCancel()) {
            return;
         }

         this.current = null;
         this.next = null;
      }

      this.cancelRequested = true;
   }

   public void secretInternalSegmentCancel() {
      this.queuePathEvent(PathEvent.CANCELED);
      synchronized (this.pathPlanLock) {
         this.getInProgress().ifPresent(AbstractNodeCostSearch::cancel);
         if (this.current != null) {
            this.current = null;
            this.next = null;
            this.baritone.getInputOverrideHandler().clearAllKeys();
            this.baritone.getInputOverrideHandler().getBlockBreakHelper().stopBreakingBlock();
         }
      }
   }

   @Override
   public void forceCancel() {
      this.cancelEverything();
      this.secretInternalSegmentCancel();
      synchronized (this.pathCalcLock) {
         this.inProgress = null;
      }
   }

   public CalculationContext secretInternalGetCalculationContext() {
      return this.context;
   }

   @Override
   public Optional<Double> estimatedTicksToGoal() {
      BetterBlockPos var1 = this.ctx.playerFeet();
      if (this.goal != null && var1 != null && this.startPosition != null) {
         if (this.goal.isInGoal(this.ctx.playerFeet())) {
            this.resetEstimatedTicksToGoal();
            return Optional.of(0.0);
         }

         if (this.ticksElapsedSoFar == 0) {
            return Optional.empty();
         }

         double var2 = this.goal.heuristic(var1.x, var1.y, var1.z);
         double var4 = this.goal.heuristic(this.startPosition.x, this.startPosition.y, this.startPosition.z);
         if (var2 == var4) {
            return Optional.empty();
         }

         double var6 = Math.abs(var2 - this.goal.heuristic()) * this.ticksElapsedSoFar / Math.abs(var4 - var2);
         return Optional.of(var6);
      } else {
         return Optional.empty();
      }
   }

   private void resetEstimatedTicksToGoal() {
      this.resetEstimatedTicksToGoal(this.expectedSegmentStart);
   }

   private void resetEstimatedTicksToGoal(BlockPos var1) {
      this.resetEstimatedTicksToGoal(new BetterBlockPos(var1));
   }

   private void resetEstimatedTicksToGoal(BetterBlockPos var1) {
      this.ticksElapsedSoFar = 0;
      this.startPosition = var1;
   }

   public BetterBlockPos pathStart() {
      BetterBlockPos var1 = this.ctx.playerFeet();
      if (!MovementHelper.canWalkOn(this.ctx, var1.below())) {
         if (this.ctx.player().onGround()) {
            double var2 = this.ctx.player().position().x;
            double var4 = this.ctx.player().position().z;
            ArrayList<BetterBlockPos> var6 = new ArrayList<>();

            for (int var7 = -1; var7 <= 1; var7++) {
               for (int var8 = -1; var8 <= 1; var8++) {
                  var6.add(new BetterBlockPos(var1.x + var7, var1.y, var1.z + var8));
               }
            }

            var6.sort(Comparator.comparingDouble(var4x -> (var4x.x + 0.5 - var2) * (var4x.x + 0.5 - var2) + (var4x.z + 0.5 - var4) * (var4x.z + 0.5 - var4)));

            for (int var13 = 0; var13 < 4; var13++) {
               BetterBlockPos var14 = (BetterBlockPos)var6.get(var13);
               double var9 = Math.abs(var14.x + 0.5 - var2);
               double var11 = Math.abs(var14.z + 0.5 - var4);
               if ((!(var9 > 0.8) || !(var11 > 0.8))
                  && MovementHelper.canWalkOn(this.ctx, var14.below())
                  && MovementHelper.canWalkThrough(this.ctx, var14)
                  && MovementHelper.canWalkThrough(this.ctx, var14.above())) {
                  return var14;
               }
            }
         } else if (MovementHelper.canWalkOn(this.ctx, var1.below().below())) {
            return var1.below();
         }
      }

      return var1;
   }

   private void findPathInNewThread(BlockPos var1, boolean var2, CalculationContext var3) {
      if (!Thread.holdsLock(this.pathCalcLock)) {
         throw new IllegalStateException("Must be called with synchronization on pathCalcLock");
      }

      if (this.inProgress != null) {
         throw new IllegalStateException("Already doing it");
      }

      if (!var3.safeForThreadedUse) {
         throw new IllegalStateException("Improper context thread safety level");
      }

      Goal var4 = this.goal;
      if (var4 == null) {
         this.logDebug("no goal");
      } else {
         long var5;
         long var7;
         if (this.current == null) {
            var5 = Baritone.settings().primaryTimeoutMS.value;
            var7 = Baritone.settings().failureTimeoutMS.value;
         } else {
            var5 = Baritone.settings().planAheadPrimaryTimeoutMS.value;
            var7 = Baritone.settings().planAheadFailureTimeoutMS.value;
         }

         AbstractNodeCostSearch var9 = this.createPathfinder(var1, var4, this.current == null ? null : this.current.getPath(), var3);
         if (!Objects.equals(var9.getGoal(), var4)) {
            this.logDebug("Simplifying " + var4.getClass() + " to GoalXZ due to distance");
         }

         this.inProgress = var9;
         Baritone.getExecutor()
            .execute(
               () -> {
                  if (var2) {
                     this.logDebug("Starting to search for path from " + var1 + " to " + var4);
                  }

                  PathCalculationResult var9x = var9.calculate(var5, var7);
                  synchronized (this.pathPlanLock) {
                     var var11 = var9x.getPath().map(var1xx -> new PathExecutor(this, var1xx));
                     if (this.current == null) {
                        if (var11.isPresent()) {
                           if (((PathExecutor)var11.get()).getPath().positions().contains(this.expectedSegmentStart)) {
                              this.queuePathEvent(PathEvent.CALC_FINISHED_NOW_EXECUTING);
                              this.current = (PathExecutor)var11.get();
                              this.resetEstimatedTicksToGoal(var1);
                           } else {
                              this.logDebug("Warning: discarding orphan path segment with incorrect start");
                           }
                        } else if (var9x.getType() != PathCalculationResult.Type.CANCELLATION && var9x.getType() != PathCalculationResult.Type.EXCEPTION) {
                           this.queuePathEvent(PathEvent.CALC_FAILED);
                        }
                     } else if (this.next == null) {
                        if (var11.isPresent()) {
                           if (((PathExecutor)var11.get()).getPath().getSrc().equals(this.current.getPath().getDest())) {
                              this.queuePathEvent(PathEvent.NEXT_SEGMENT_CALC_FINISHED);
                              this.next = (PathExecutor)var11.get();
                           } else {
                              this.logDebug("Warning: discarding orphan next segment with incorrect start");
                           }
                        } else {
                           this.queuePathEvent(PathEvent.NEXT_CALC_FAILED);
                        }
                     } else {
                        this.logDirect("Warning: PathingBehaivor illegal state! Discarding invalid path!");
                     }

                     if (var2 && this.current != null && this.current.getPath() != null) {
                        if (var4.isInGoal(this.current.getPath().getDest())) {
                           this.logDebug(
                              "Finished finding a path from "
                                 + var1
                                 + " to "
                                 + var4
                                 + ". "
                                 + this.current.getPath().getNumNodesConsidered()
                                 + " nodes considered"
                           );
                        } else {
                           this.logDebug(
                              "Found path segment from "
                                 + var1
                                 + " towards "
                                 + var4
                                 + ". "
                                 + this.current.getPath().getNumNodesConsidered()
                                 + " nodes considered"
                           );
                        }
                     }

                     synchronized (this.pathCalcLock) {
                        this.inProgress = null;
                     }
                  }
               }
            );
      }
   }

   private AbstractNodeCostSearch createPathfinder(BlockPos var1, Goal var2, IPath var3, CalculationContext var4) {
      Goal var5 = var2;
      if (Baritone.settings().simplifyUnloadedYCoord.value && var2 instanceof IGoalRenderPos) {
         BlockPos var6 = ((IGoalRenderPos)var2).getGoalPos();
         if (!var4.bsi.worldContainsLoadedChunk(var6.getX(), var6.getZ())) {
            var5 = new GoalXZ(var6.getX(), var6.getZ());
         }
      }

      Favoring var10 = new Favoring(var4.getBaritone().getPlayerContext(), var3, var4);
      BetterBlockPos var7 = this.ctx.playerFeet();
      BetterBlockPos var8 = new BetterBlockPos(var1);
      BlockPos var9 = var7.subtract(var8);
      if (var7.getY() == var8.getY() && Math.abs(var9.getX()) <= 1 && Math.abs(var9.getZ()) <= 1) {
         var8 = var7;
      }

      return new AStarPathFinder(var8, var1.getX(), var1.getY(), var1.getZ(), var5, var10, var4);
   }

   @Override
   public void onRenderPass(RenderEvent var1) {
      PathRenderer.render(var1, this);
   }
}
