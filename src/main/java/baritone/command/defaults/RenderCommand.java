package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RenderCommand extends Command {
   public RenderCommand(IBaritone var1) {
      super(var1, "render");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandTooManyArgumentsException {
      var2.requireMax(0);
      BetterBlockPos var3 = this.ctx.playerFeet();
      int var4 = (this.ctx.minecraft().options.renderDistance().get() + 1) * 16;
      this.ctx
         .minecraft()
         .levelExtractor
         .setBlocksDirty(var3.x - var4, this.ctx.world().getMinY(), var3.z - var4, var3.x + var4, this.ctx.world().getMaxY(), var3.z + var4);
      this.logDirect("Done");
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) {
      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Fix glitched chunks";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList("The render command fixes glitched chunk rendering without having to reload all of them.", "", "Usage:", "> render");
   }
}
