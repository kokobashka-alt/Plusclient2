package baritone.process;

import baritone.Baritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.process.IFollowProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.BetterBlockPos;
import baritone.utils.BaritoneProcessHelper;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class FollowProcess extends BaritoneProcessHelper implements IFollowProcess {
   private Predicate<Entity> filter;
   private List<Entity> cache;
   private boolean into;

   public FollowProcess(Baritone var1) {
      super(var1);
   }

   @Override
   public PathingCommand onTick(boolean var1, boolean var2) {
      this.scanWorld();
      GoalComposite var3 = new GoalComposite(this.cache.stream().map(this::towards).toArray(Goal[]::new));
      return new PathingCommand(var3, PathingCommandType.REVALIDATE_GOAL_AND_PATH);
   }

   private Goal towards(Entity var1) {
      BlockPos var2;
      if (Baritone.settings().followOffsetDistance.value != 0.0 && !this.into) {
         GoalXZ var3 = GoalXZ.fromDirection(var1.position(), Baritone.settings().followOffsetDirection.value, Baritone.settings().followOffsetDistance.value);
         var2 = new BetterBlockPos(var3.getX(), var1.position().y, var3.getZ());
      } else {
         var2 = var1.blockPosition();
      }

      return this.into ? new GoalBlock(var2) : new GoalNear(var2, Baritone.settings().followRadius.value);
   }

   private boolean followable(Entity var1) {
      if (var1 == null) {
         return false;
      }

      if (!var1.isAlive()) {
         return false;
      }

      if (var1.equals(this.ctx.player())) {
         return false;
      }

      int var2 = Baritone.settings().followTargetMaxDistance.value;
      return var2 != 0 && var1.distanceToSqr(this.ctx.player()) > var2 * var2 ? false : this.ctx.entitiesStream().anyMatch(var1::equals);
   }

   private void scanWorld() {
      this.cache = this.ctx.entitiesStream().filter(this::followable).filter(this.filter).distinct().collect(Collectors.toList());
   }

   @Override
   public boolean isActive() {
      if (this.filter == null) {
         return false;
      }

      this.scanWorld();
      return !this.cache.isEmpty();
   }

   @Override
   public void onLostControl() {
      this.filter = null;
      this.cache = null;
   }

   @Override
   public String displayName0() {
      return "Following " + this.cache;
   }

   @Override
   public void follow(Predicate<Entity> var1) {
      this.filter = var1;
      this.into = false;
   }

   @Override
   public void pickup(Predicate<ItemStack> var1) {
      this.filter = var1x -> var1x instanceof ItemEntity && var1.test(((ItemEntity)var1x).getItem());
      this.into = true;
   }

   @Override
   public List<Entity> following() {
      return this.cache;
   }

   @Override
   public Predicate<Entity> currentFilter() {
      return this.filter;
   }
}
