package baritone.pathing.movement.movements;

import baritone.api.IBaritone;
import baritone.api.pathing.movement.MovementStatus;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.VecUtils;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.movement.MovementState;
import baritone.utils.pathing.MutableMoveResult;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;

public class MovementFall extends Movement {
   private static final ItemStack STACK_BUCKET_WATER = new ItemStack(Items.WATER_BUCKET);
   private static final ItemStack STACK_BUCKET_EMPTY = new ItemStack(Items.BUCKET);

   public MovementFall(IBaritone var1, BetterBlockPos var2, BetterBlockPos var3) {
      super(var1, var2, var3, buildPositionsToBreak(var2, var3));
   }

   @Override
   public double calculateCost(CalculationContext var1) {
      MutableMoveResult var2 = new MutableMoveResult();
      MovementDescend.cost(var1, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z, var2);
      return var2.y != this.dest.y ? 1000000.0 : var2.cost;
   }

   @Override
   protected Set<BetterBlockPos> calculateValidPositions() {
      HashSet var1 = new HashSet();
      var1.add(this.src);

      for (int var2 = this.src.y - this.dest.y; var2 >= 0; var2--) {
         var1.add(this.dest.above(var2));
      }

      return var1;
   }

   private boolean willPlaceBucket() {
      CalculationContext var1 = new CalculationContext(this.baritone);
      MutableMoveResult var2 = new MutableMoveResult();
      return MovementDescend.dynamicFallCost(
         var1, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z, 0.0, var1.get(this.dest.x, this.src.y - 2, this.dest.z), var2
      );
   }

   @Override
   public MovementState updateState(MovementState var1) {
      super.updateState(var1);
      if (var1.getStatus() != MovementStatus.RUNNING) {
         return var1;
      }

      BetterBlockPos var2 = this.ctx.playerFeet();
      Rotation var3 = RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), VecUtils.getBlockPosCenter(this.dest), this.ctx.playerRotations());
      Rotation var4 = null;
      BlockState var5 = this.ctx.world().getBlockState(this.dest);
      var5.getBlock();
      if (this.ctx.world().getBlockState(this.dest.below()).is(Blocks.MAGMA_BLOCK)
         && MovementHelper.steppingOnBlocks(this.ctx).stream().allMatch(var1x -> MovementHelper.canWalkThrough(this.ctx, var1x))) {
         var1.setInput(Input.SNEAK, true);
      }

      boolean var7 = var5.getFluidState().getType() instanceof WaterFluid;
      if (!var7 && this.willPlaceBucket() && !var2.equals(this.dest)) {
         if (!Inventory.isHotbarSlot(this.ctx.player().getInventory().findSlotMatchingItem(STACK_BUCKET_WATER)) || this.ctx.world().dimension() == Level.NETHER
            )
          {
            return var1.setStatus(MovementStatus.UNREACHABLE);
         }

         if (this.ctx.player().position().y - this.dest.getY() < this.ctx.playerController().getBlockReachDistance() && !this.ctx.player().onGround()) {
            this.ctx.player().getInventory().setSelectedSlot(this.ctx.player().getInventory().findSlotMatchingItem(STACK_BUCKET_WATER));
            var4 = new Rotation(var3.getYaw(), 90.0F);
            if (this.ctx.isLookingAt(this.dest) || this.ctx.isLookingAt(this.dest.below())) {
               var1.setInput(Input.CLICK_RIGHT, true);
            }
         }
      }

      if (var4 != null) {
         var1.setTarget(new MovementState.MovementTarget(var4, true));
      } else {
         var1.setTarget(new MovementState.MovementTarget(var3, false));
      }

      if (var2.equals(this.dest) && (this.ctx.player().position().y - var2.getY() < 0.094 || var7)) {
         if (!var7) {
            return var1.setStatus(MovementStatus.SUCCESS);
         }

         if (Inventory.isHotbarSlot(this.ctx.player().getInventory().findSlotMatchingItem(STACK_BUCKET_EMPTY))) {
            this.ctx.player().getInventory().setSelectedSlot(this.ctx.player().getInventory().findSlotMatchingItem(STACK_BUCKET_EMPTY));
            if (this.ctx.player().getDeltaMovement().y >= 0.0) {
               return var1.setInput(Input.CLICK_RIGHT, true);
            }

            return var1;
         }

         if (this.ctx.player().getDeltaMovement().y >= 0.0) {
            return var1.setStatus(MovementStatus.SUCCESS);
         }
      }

      Vec3 var8 = VecUtils.getBlockPosCenter(this.dest);
      if (Math.abs(this.ctx.player().position().x + this.ctx.player().getDeltaMovement().x - var8.x) > 0.1
         || Math.abs(this.ctx.player().position().z + this.ctx.player().getDeltaMovement().z - var8.z) > 0.1) {
         if (!this.ctx.player().onGround() && Math.abs(this.ctx.player().getDeltaMovement().y) > 0.4) {
            var1.setInput(Input.SNEAK, true);
         }

         var1.setInput(Input.MOVE_FORWARD, true);
      }

      Vec3i var9 = Optional.ofNullable(this.avoid()).map(Direction::getUnitVec3i).orElse(null);
      if (var9 == null) {
         var9 = this.src.subtract(this.dest);
      } else {
         double var10 = Math.abs(var9.getX() * (var8.x - var9.getX() / 2.0 - this.ctx.player().position().x))
            + Math.abs(var9.getZ() * (var8.z - var9.getZ() / 2.0 - this.ctx.player().position().z));
         if (var10 < 0.6) {
            var1.setInput(Input.MOVE_FORWARD, true);
         } else if (!this.ctx.player().onGround()) {
            var1.setInput(Input.SNEAK, false);
         }
      }

      if (var4 == null) {
         Vec3 var12 = new Vec3(var8.x + 0.125 * var9.getX(), var8.y, var8.z + 0.125 * var9.getZ());
         var1.setTarget(new MovementState.MovementTarget(RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), var12, this.ctx.playerRotations()), false));
      }

      return var1;
   }

   private Direction avoid() {
      for (int var1 = 0; var1 < 15; var1++) {
         BlockState var2 = this.ctx.world().getBlockState(this.ctx.playerFeet().below(var1));
         if (var2.getBlock() == Blocks.LADDER) {
            return var2.getValue(LadderBlock.FACING);
         }
      }

      return null;
   }

   @Override
   public boolean safeToCancel(MovementState var1) {
      return this.ctx.playerFeet().equals(this.src) || var1.getStatus() != MovementStatus.RUNNING;
   }

   private static BetterBlockPos[] buildPositionsToBreak(BetterBlockPos var0, BetterBlockPos var1) {
      int var3 = var0.getX() - var1.getX();
      int var4 = var0.getZ() - var1.getZ();
      int var5 = Math.abs(var0.getY() - var1.getY());
      BetterBlockPos[] var2 = new BetterBlockPos[var5 + 2];

      for (int var6 = 0; var6 < var2.length; var6++) {
         var2[var6] = new BetterBlockPos(var0.getX() - var3, var0.getY() + 1 - var6, var0.getZ() - var4);
      }

      return var2;
   }

   @Override
   protected boolean prepared(MovementState var1) {
      if (var1.getStatus() == MovementStatus.WAITING) {
         return true;
      }

      for (int var2 = 0; var2 < 4 && var2 < this.positionsToBreak.length; var2++) {
         if (!MovementHelper.canWalkThrough(this.ctx, this.positionsToBreak[var2])) {
            return super.prepared(var1);
         }
      }

      return true;
   }
}
