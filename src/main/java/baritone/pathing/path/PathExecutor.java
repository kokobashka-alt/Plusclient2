package baritone.pathing.path;

import baritone.Baritone;
import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.movement.IMovement;
import baritone.api.pathing.movement.MovementStatus;
import baritone.api.pathing.path.IPathExecutor;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.Tuple;
import baritone.api.utils.VecUtils;
import baritone.api.utils.input.Input;
import baritone.behavior.PathingBehavior;
import baritone.pathing.calc.AbstractNodeCostSearch;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.movement.movements.MovementAscend;
import baritone.pathing.movement.movements.MovementDescend;
import baritone.pathing.movement.movements.MovementDiagonal;
import baritone.pathing.movement.movements.MovementFall;
import baritone.pathing.movement.movements.MovementParkour;
import baritone.pathing.movement.movements.MovementTraverse;
import baritone.utils.BlockStateInterface;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class PathExecutor implements IPathExecutor, Helper {
   private final IPath path;
   private int pathPosition;
   private int ticksAway;
   private int ticksOnCurrent;
   private Double currentMovementOriginalCostEstimate;
   private Integer costEstimateIndex;
   private boolean failed;
   private boolean recalcBP;
   private HashSet<BlockPos> toBreak;
   private HashSet<BlockPos> toPlace;
   private HashSet<BlockPos> toWalkInto;
   private final PathingBehavior behavior;
   private final IPlayerContext ctx;
   private boolean sprintNextTick;

   public PathExecutor(PathingBehavior var1, IPath var2) {
      this.recalcBP = true;
      this.toBreak = new HashSet<>();
      this.toPlace = new HashSet<>();
      this.toWalkInto = new HashSet<>();
      this.behavior = var1;
      this.ctx = var1.ctx;
      this.path = var2;
      this.pathPosition = 0;
   }

   public boolean onTick() {
      if (this.pathPosition == this.path.length() - 1) {
         this.pathPosition++;
      }

      if (this.pathPosition >= this.path.length()) {
         return true;
      }

      Movement var1 = (Movement)this.path.movements().get(this.pathPosition);
      BetterBlockPos var2 = this.ctx.playerFeet();
      if (!var1.getValidPositions().contains(var2)) {
         for (int var3 = 0; var3 < this.pathPosition && var3 < this.path.length(); var3++) {
            if (((Movement)this.path.movements().get(var3)).getValidPositions().contains(var2)) {
               int var4 = this.pathPosition;
               this.pathPosition = var3;

               for (int var5 = this.pathPosition; var5 <= var4; var5++) {
                  this.path.movements().get(var5).reset();
               }

               this.onChangeInPathPosition();
               this.onTick();
               return false;
            }
         }

         for (int var10 = this.pathPosition + 3; var10 < this.path.length() - 1; var10++) {
            if (((Movement)this.path.movements().get(var10)).getValidPositions().contains(var2)) {
               if (var10 - this.pathPosition > 2) {
                  this.logDebug("Skipping forward " + (var10 - this.pathPosition) + " steps, to " + var10);
               }

               this.pathPosition = var10 - 1;
               this.onChangeInPathPosition();
               this.onTick();
               return false;
            }
         }
      }

      var var11 = this.closestPathPos(this.path);
      if (this.possiblyOffPath(var11, 2.0)) {
         this.ticksAway++;
         System.out.println("FAR AWAY FROM PATH FOR " + this.ticksAway + " TICKS. Current distance: " + var11.getA() + ". Threshold: 2.0");
         if (this.ticksAway > 200.0) {
            this.logDebug("Too far away from path for too long, cancelling path");
            this.cancel();
            return false;
         }
      } else {
         this.ticksAway = 0;
      }

      if (this.possiblyOffPath(var11, 3.0)) {
         this.logDebug("too far from path");
         this.cancel();
         return false;
      }

      BlockStateInterface var12 = new BlockStateInterface(this.ctx);

      for (int var13 = this.pathPosition - 10; var13 < this.pathPosition + 10; var13++) {
         if (var13 >= 0 && var13 < this.path.movements().size()) {
            Movement var6 = (Movement)this.path.movements().get(var13);
            var var7 = var6.toBreak(var12);
            var var8 = var6.toPlace(var12);
            var var9 = var6.toWalkInto(var12);
            var6.resetBlockCache();
            if (!var7.equals(var6.toBreak(var12))) {
               this.recalcBP = true;
            }

            if (!var8.equals(var6.toPlace(var12))) {
               this.recalcBP = true;
            }

            if (!var9.equals(var6.toWalkInto(var12))) {
               this.recalcBP = true;
            }
         }
      }

      if (this.recalcBP) {
         HashSet var14 = new HashSet();
         HashSet var17 = new HashSet();
         HashSet var20 = new HashSet();

         for (int var21 = this.pathPosition; var21 < this.path.movements().size(); var21++) {
            Movement var23 = (Movement)this.path.movements().get(var21);
            var14.addAll(var23.toBreak(var12));
            var17.addAll(var23.toPlace(var12));
            var20.addAll(var23.toWalkInto(var12));
         }

         this.toBreak = var14;
         this.toPlace = var17;
         this.toWalkInto = var20;
         this.recalcBP = false;
      }

      if (this.pathPosition < this.path.movements().size() - 1) {
         IMovement var15 = this.path.movements().get(this.pathPosition + 1);
         if (!this.behavior.baritone.bsi.worldContainsLoadedChunk(var15.getDest().x, var15.getDest().z)) {
            this.logDebug("Pausing since destination is at edge of loaded chunks");
            this.clearKeys();
            return true;
         }
      }

      boolean var16 = var1.safeToCancel();
      if (this.costEstimateIndex == null || this.costEstimateIndex != this.pathPosition) {
         this.costEstimateIndex = this.pathPosition;
         this.currentMovementOriginalCostEstimate = var1.getCost();

         for (int var18 = 1; var18 < Baritone.settings().costVerificationLookahead.value && this.pathPosition + var18 < this.path.length() - 1; var18++) {
            if (((Movement)this.path.movements().get(this.pathPosition + var18)).calculateCost(this.behavior.secretInternalGetCalculationContext())
                  >= 1000000.0
               && var16) {
               this.logDebug("Something has changed in the world and a future movement has become impossible. Cancelling.");
               this.cancel();
               return true;
            }
         }
      }

      double var19 = var1.recalculateCost(this.behavior.secretInternalGetCalculationContext());
      if (var19 >= 1000000.0 && var16) {
         this.logDebug("Something has changed in the world and this movement has become impossible. Cancelling.");
         this.cancel();
         return true;
      }

      if (!var1.calculatedWhileLoaded() && var19 - this.currentMovementOriginalCostEstimate > Baritone.settings().maxCostIncrease.value && var16) {
         this.logDebug("Original cost " + this.currentMovementOriginalCostEstimate + " current cost " + var19 + ". Cancelling.");
         this.cancel();
         return true;
      }

      if (this.shouldPause()) {
         this.logDebug("Pausing since current best path is a backtrack");
         this.clearKeys();
         return true;
      }

      MovementStatus var22 = var1.update();
      if (var22 == MovementStatus.UNREACHABLE || var22 == MovementStatus.FAILED) {
         this.logDebug("Movement returns status " + var22);
         this.cancel();
         return true;
      }

      if (var22 == MovementStatus.SUCCESS) {
         this.pathPosition++;
         this.onChangeInPathPosition();
         this.onTick();
         return true;
      }

      this.sprintNextTick = this.shouldSprintNextTick();
      if (!this.sprintNextTick) {
         this.ctx.player().setSprinting(false);
      }

      this.ticksOnCurrent++;
      if (this.ticksOnCurrent > this.currentMovementOriginalCostEstimate + Baritone.settings().movementTimeoutTicks.value.intValue()) {
         this.logDebug(
            "This movement has taken too long (" + this.ticksOnCurrent + " ticks, expected " + this.currentMovementOriginalCostEstimate + "). Cancelling."
         );
         this.cancel();
         return true;
      } else {
         return var16;
      }
   }

   private Tuple<Double, BlockPos> closestPathPos(IPath var1) {
      double var2 = -1.0;
      BlockPos var4 = null;

      for (IMovement var6 : var1.movements()) {
         for (BlockPos var8 : ((Movement)var6).getValidPositions()) {
            double var9 = VecUtils.entityDistanceToCenter(this.ctx.player(), var8);
            if (var9 < var2 || var2 == -1.0) {
               var2 = var9;
               var4 = var8;
            }
         }
      }

      return new Tuple<>(var2, var4);
   }

   private boolean shouldPause() {
      var var1 = this.behavior.getInProgress();
      if (!var1.isPresent()) {
         return false;
      }

      if (!this.ctx.player().onGround()) {
         return false;
      }

      if (!MovementHelper.canWalkOn(this.ctx, this.ctx.playerFeet().below())) {
         return false;
      }

      if (!MovementHelper.canWalkThrough(this.ctx, this.ctx.playerFeet()) || !MovementHelper.canWalkThrough(this.ctx, this.ctx.playerFeet().above())) {
         return false;
      }

      if (!this.path.movements().get(this.pathPosition).safeToCancel()) {
         return false;
      }

      Optional var2 = ((AbstractNodeCostSearch)var1.get()).bestPathSoFar();
      if (!var2.isPresent()) {
         return false;
      }

      List var3 = ((IPath)var2.get()).positions();
      if (var3.size() < 3) {
         return false;
      }

      var3 = var3.subList(1, var3.size());
      return var3.contains(this.ctx.playerFeet());
   }

   private boolean possiblyOffPath(Tuple<Double, BlockPos> var1, double var2) {
      double var4 = (Double)var1.getA();
      if (var4 > var2) {
         if (this.path.movements().get(this.pathPosition) instanceof MovementFall) {
            BlockPos var6 = this.path.positions().get(this.pathPosition + 1);
            return VecUtils.entityFlatDistanceToCenter(this.ctx.player(), var6) >= var2;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean snipsnapifpossible() {
      if (!this.ctx.player().onGround() && this.ctx.world().getFluidState(this.ctx.playerFeet()).isEmpty()) {
         return false;
      }

      if (this.ctx.player().getDeltaMovement().y < -0.1) {
         return false;
      }

      int var1 = this.path.positions().indexOf(this.ctx.playerFeet());
      if (var1 == -1) {
         return false;
      }

      this.pathPosition = var1;
      this.clearKeys();
      return true;
   }

   private boolean shouldSprintNextTick() {
      boolean var1 = this.behavior.baritone.getInputOverrideHandler().isInputForcedDown(Input.SPRINT);
      this.behavior.baritone.getInputOverrideHandler().setInputForceState(Input.SPRINT, false);
      if (!(new CalculationContext(this.behavior.baritone, false)).canSprint) {
         return false;
      }

      IMovement var2 = this.path.movements().get(this.pathPosition);
      if (var2 instanceof MovementTraverse && this.pathPosition < this.path.length() - 3) {
         IMovement var3 = this.path.movements().get(this.pathPosition + 1);
         if (var3 instanceof MovementAscend
            && sprintableAscend(this.ctx, (MovementTraverse)var2, (MovementAscend)var3, this.path.movements().get(this.pathPosition + 2))) {
            if (skipNow(this.ctx, var2)) {
               this.logDebug("Skipping traverse to straight ascend");
               this.pathPosition++;
               this.onChangeInPathPosition();
               this.onTick();
               this.behavior.baritone.getInputOverrideHandler().setInputForceState(Input.JUMP, true);
               return true;
            }

            this.logDebug("Too far to the side to safely sprint ascend");
         }
      }

      if (var1) {
         return true;
      }

      if (var2 instanceof MovementDescend) {
         if (this.pathPosition < this.path.length() - 2) {
            IMovement var6 = this.path.movements().get(this.pathPosition + 1);
            if (MovementHelper.canUseFrostWalker(this.ctx, var6.getDest().below()) && (var6 instanceof MovementTraverse || var6 instanceof MovementParkour)) {
               boolean var4 = Baritone.settings().allowPlace.value
                  && this.behavior.baritone.getInventoryBehavior().hasGenericThrowaway()
                  && var6 instanceof MovementParkour;
               boolean var5 = !var2.getDirection().above().offset(var6.getDirection()).equals(BlockPos.ZERO)
                  && var2.getDirection().above().cross(var6.getDirection()).equals(BlockPos.ZERO);
               if (var5 && !var4) {
                  ((MovementDescend)var2).forceSafeMode();
               }
            }
         }

         if (((MovementDescend)var2).safeMode() && !((MovementDescend)var2).skipToAscend()) {
            this.logDebug("Sprinting would be unsafe");
            return false;
         }

         if (this.pathPosition < this.path.length() - 2) {
            IMovement var7 = this.path.movements().get(this.pathPosition + 1);
            if (var7 instanceof MovementAscend && var2.getDirection().above().equals(var7.getDirection().below())) {
               this.pathPosition++;
               this.onChangeInPathPosition();
               this.onTick();
               this.logDebug("Skipping descend to straight ascend");
               return true;
            }

            if (canSprintFromDescendInto(this.ctx, var2, var7)) {
               if (var7 instanceof MovementDescend && this.pathPosition < this.path.length() - 3) {
                  IMovement var12 = this.path.movements().get(this.pathPosition + 2);
                  if (var12 instanceof MovementDescend && !canSprintFromDescendInto(this.ctx, var7, var12)) {
                     return false;
                  }
               }

               if (this.ctx.playerFeet().equals(var2.getDest())) {
                  this.pathPosition++;
                  this.onChangeInPathPosition();
                  this.onTick();
               }

               return true;
            }
         }
      }

      if (var2 instanceof MovementAscend && this.pathPosition != 0) {
         IMovement var8 = this.path.movements().get(this.pathPosition - 1);
         if (var8 instanceof MovementDescend && var8.getDirection().above().equals(var2.getDirection().below())) {
            BetterBlockPos var10 = var2.getSrc().above();
            if (this.ctx.player().position().y >= var10.getY() - 0.07) {
               this.behavior.baritone.getInputOverrideHandler().setInputForceState(Input.JUMP, false);
               return true;
            }
         }

         if (this.pathPosition < this.path.length() - 2
            && var8 instanceof MovementTraverse
            && sprintableAscend(this.ctx, (MovementTraverse)var8, (MovementAscend)var2, this.path.movements().get(this.pathPosition + 1))) {
            return true;
         }
      }

      if (var2 instanceof MovementFall) {
         var var9 = this.overrideFall((MovementFall)var2);
         if (var9 != null) {
            BetterBlockPos var11 = new BetterBlockPos((BlockPos)var9.getB());
            if (!this.path.positions().contains(var11)) {
               throw new IllegalStateException(String.format("Fall override at %s %s %s returned illegal destination %s %s %s", var2.getSrc(), var11));
            }

            if (this.ctx.playerFeet().equals(var11)) {
               this.pathPosition = this.path.positions().indexOf(var11);
               this.onChangeInPathPosition();
               this.onTick();
               return true;
            }

            this.clearKeys();
            this.behavior
               .baritone
               .getLookBehavior()
               .updateTarget(RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), (Vec3)var9.getA(), this.ctx.playerRotations()), false);
            this.behavior.baritone.getInputOverrideHandler().setInputForceState(Input.MOVE_FORWARD, true);
            return true;
         }
      }

      return false;
   }

   private Tuple<Vec3, BlockPos> overrideFall(MovementFall var1) {
      BlockPos var2 = var1.getDirection();
      if (var2.getY() < -3) {
         return null;
      }

      if (!var1.toBreakCached.isEmpty()) {
         return null;
      }

      Vec3i var3 = new Vec3i(var2.getX(), 0, var2.getZ());

      int var4;
      label49:
      for (var4 = this.pathPosition + 1; var4 < this.path.length() - 1 && var4 < this.pathPosition + 3; var4++) {
         IMovement var5 = this.path.movements().get(var4);
         if (!(var5 instanceof MovementTraverse) || !var3.equals(var5.getDirection())) {
            break;
         }

         for (int var6 = var5.getDest().y; var6 <= var1.getSrc().y + 1; var6++) {
            BlockPos var7 = new BlockPos(var5.getDest().x, var6, var5.getDest().z);
            if (!MovementHelper.fullyPassable(this.ctx, var7)) {
               break label49;
            }
         }

         if (!MovementHelper.canWalkOn(this.ctx, var5.getDest().below())) {
            break;
         }
      }

      if (--var4 == this.pathPosition) {
         return null;
      }

      double var9 = var4 - this.pathPosition - 0.4;
      return new Tuple<>(
         new Vec3(var3.getX() * var9 + var1.getDest().x + 0.5, var1.getDest().y, var3.getZ() * var9 + var1.getDest().z + 0.5),
         var1.getDest().offset(var3.getX() * (var4 - this.pathPosition), 0, var3.getZ() * (var4 - this.pathPosition))
      );
   }

   private static boolean skipNow(IPlayerContext var0, IMovement var1) {
      double var2 = Math.abs(var1.getDirection().getX() * (var1.getSrc().z + 0.5 - var0.player().position().z))
         + Math.abs(var1.getDirection().getZ() * (var1.getSrc().x + 0.5 - var0.player().position().x));
      if (var2 > 0.1) {
         return false;
      }

      BlockPos var4 = var1.getSrc().subtract(var1.getDirection()).above(2);
      if (MovementHelper.fullyPassable(var0, var4)) {
         return true;
      }

      double var5 = Math.abs(var1.getDirection().getX() * (var4.getX() + 0.5 - var0.player().position().x))
         + Math.abs(var1.getDirection().getZ() * (var4.getZ() + 0.5 - var0.player().position().z));
      return var5 > 0.8;
   }

   private static boolean sprintableAscend(IPlayerContext var0, MovementTraverse var1, MovementAscend var2, IMovement var3) {
      if (!Baritone.settings().sprintAscends.value) {
         return false;
      }

      if (!var1.getDirection().equals(var2.getDirection().below())) {
         return false;
      }

      if (var3.getDirection().getX() == var2.getDirection().getX() && var3.getDirection().getZ() == var2.getDirection().getZ()) {
         if (!MovementHelper.canWalkOn(var0, var1.getDest().below())) {
            return false;
         }

         if (!MovementHelper.canWalkOn(var0, var2.getDest().below())) {
            return false;
         }

         if (!var2.toBreakCached.isEmpty()) {
            return false;
         }

         for (int var4 = 0; var4 < 2; var4++) {
            for (int var5 = 0; var5 < 3; var5++) {
               BlockPos var6 = var1.getSrc().above(var5);
               if (var4 == 1) {
                  var6 = var6.offset(var1.getDirection());
               }

               if (!MovementHelper.fullyPassable(var0, var6)) {
                  return false;
               }
            }
         }

         return MovementHelper.avoidWalkingInto(var0.world().getBlockState(var1.getSrc().above(3)))
            ? false
            : !MovementHelper.avoidWalkingInto(var0.world().getBlockState(var2.getDest().above(2)));
      } else {
         return false;
      }
   }

   private static boolean canSprintFromDescendInto(IPlayerContext var0, IMovement var1, IMovement var2) {
      if (var2 instanceof MovementDescend && var2.getDirection().equals(var1.getDirection())) {
         return true;
      } else if (!MovementHelper.canWalkOn(var0, var1.getDest().offset(var1.getDirection()))) {
         return false;
      } else {
         return var2 instanceof MovementTraverse && var2.getDirection().equals(var1.getDirection())
            ? true
            : var2 instanceof MovementDiagonal && Baritone.settings().allowOvershootDiagonalDescend.value;
      }
   }

   private void onChangeInPathPosition() {
      this.clearKeys();
      this.ticksOnCurrent = 0;
   }

   private void clearKeys() {
      this.behavior.baritone.getInputOverrideHandler().clearAllKeys();
   }

   private void cancel() {
      this.clearKeys();
      this.behavior.baritone.getInputOverrideHandler().getBlockBreakHelper().stopBreakingBlock();
      this.pathPosition = this.path.length() + 3;
      this.failed = true;
   }

   @Override
   public int getPosition() {
      return this.pathPosition;
   }

   public PathExecutor trySplice(PathExecutor var1) {
      return var1 == null ? this.cutIfTooLong() : SplicedPath.trySplice(this.path, var1.path, false).map(var2 -> {
         if (!var2.getDest().equals(var1.getPath().getDest())) {
            throw new IllegalStateException(String.format("Path has end %s instead of %s after splicing", var2.getDest(), var1.getPath().getDest()));
         }

         PathExecutor var3 = new PathExecutor(this.behavior, var2);
         var3.pathPosition = this.pathPosition;
         var3.currentMovementOriginalCostEstimate = this.currentMovementOriginalCostEstimate;
         var3.costEstimateIndex = this.costEstimateIndex;
         var3.ticksOnCurrent = this.ticksOnCurrent;
         return var3;
      }).orElseGet(this::cutIfTooLong);
   }

   private PathExecutor cutIfTooLong() {
      if (this.pathPosition > Baritone.settings().maxPathHistoryLength.value) {
         int var1 = Baritone.settings().pathHistoryCutoffAmount.value;
         CutoffPath var2 = new CutoffPath(this.path, var1, this.path.length() - 1);
         if (!var2.getDest().equals(this.path.getDest())) {
            throw new IllegalStateException(String.format("Path has end %s instead of %s after trimming its start", var2.getDest(), this.path.getDest()));
         }

         this.logDebug("Discarding earliest segment movements, length cut from " + this.path.length() + " to " + var2.length());
         PathExecutor var3 = new PathExecutor(this.behavior, var2);
         var3.pathPosition = this.pathPosition - var1;
         var3.currentMovementOriginalCostEstimate = this.currentMovementOriginalCostEstimate;
         if (this.costEstimateIndex != null) {
            var3.costEstimateIndex = this.costEstimateIndex - var1;
         }

         var3.ticksOnCurrent = this.ticksOnCurrent;
         return var3;
      } else {
         return this;
      }
   }

   @Override
   public IPath getPath() {
      return this.path;
   }

   public boolean failed() {
      return this.failed;
   }

   public boolean finished() {
      return this.pathPosition >= this.path.length();
   }

   public Set<BlockPos> toBreak() {
      return Collections.unmodifiableSet(this.toBreak);
   }

   public Set<BlockPos> toPlace() {
      return Collections.unmodifiableSet(this.toPlace);
   }

   public Set<BlockPos> toWalkInto() {
      return Collections.unmodifiableSet(this.toWalkInto);
   }

   public boolean isSprinting() {
      return this.sprintNextTick;
   }
}
