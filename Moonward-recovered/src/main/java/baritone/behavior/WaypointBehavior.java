package baritone.behavior;

import baritone.Baritone;
import baritone.api.cache.IWaypoint;
import baritone.api.cache.Waypoint;
import baritone.api.event.events.BlockInteractEvent;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.utils.BlockStateInterface;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class WaypointBehavior extends Behavior {
   public WaypointBehavior(Baritone var1) {
      super(var1);
   }

   @Override
   public void onBlockInteract(BlockInteractEvent var1) {
      if (Baritone.settings().doBedWaypoints.value) {
         if (var1.getType() == BlockInteractEvent.Type.USE) {
            BetterBlockPos var2 = BetterBlockPos.from(var1.getPos());
            BlockState var3 = BlockStateInterface.get(this.ctx, var2);
            if (var3.getBlock() instanceof BedBlock) {
               if (var3.getValue(BedBlock.PART) == BedPart.FOOT) {
                  var2 = var2.relative(var3.getValue(BedBlock.FACING));
               }

               var var4 = this.baritone.getWorldProvider().getCurrentWorld().getWaypoints().getByTag(IWaypoint.Tag.BED);
               boolean var5 = var4.stream().map(IWaypoint::getLocation).filter(var2::equals).findFirst().isPresent();
               if (!var5) {
                  this.baritone.getWorldProvider().getCurrentWorld().getWaypoints().addWaypoint(new Waypoint("bed", IWaypoint.Tag.BED, var2));
               }
            }
         }
      }
   }

   @Override
   public void onPlayerDeath() {
      if (Baritone.settings().doDeathWaypoints.value) {
         Waypoint var1 = new Waypoint("death", IWaypoint.Tag.DEATH, this.ctx.playerFeet());
         this.baritone.getWorldProvider().getCurrentWorld().getWaypoints().addWaypoint(var1);
         MutableComponent var2 = Component.literal("Death position saved.");
         var2.setStyle(
            var2.getStyle()
               .withColor(ChatFormatting.WHITE)
               .withHoverEvent(new ShowText(Component.literal("Click to goto death")))
               .withClickEvent(
                  new RunCommand(String.format("%s%s goto %s @ %d", Helper.FORCE_COMMAND_PREFIX, "wp", var1.getTag().getName(), var1.getCreationTimestamp()))
               )
         );
         Helper.HELPER.logDirect(var2);
      }
   }
}
