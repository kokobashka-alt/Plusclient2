package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.BlockById;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.utils.BetterBlockPos;
import baritone.cache.CachedChunk;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;

public class FindCommand extends Command {
   public FindCommand(IBaritone var1) {
      super(var1, "find");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      var2.requireMin(1);
      ArrayList<net.minecraft.world.level.block.Block> var3 = new ArrayList<>();

      while (var2.hasAny()) {
         var3.add(var2.getDatatypeFor(BlockById.INSTANCE));
      }

      BetterBlockPos var4 = this.ctx.playerFeet();
      Component[] var5 = var3.stream()
         .flatMap(
            var2x -> this.ctx
               .worldData()
               .getCachedWorld()
               .getLocationsOf(BuiltInRegistries.BLOCK.getKey(var2x).getPath(), Integer.MAX_VALUE, var4.x, var4.y, 4)
               .stream()
         )
         .map(BetterBlockPos::new)
         .map(this::positionToComponent)
         .toArray(Component[]::new);
      if (var5.length > 0) {
         var var10000 = Arrays.asList(var5);
         FindCommand var6 = this;
         var10000.forEach(var1x -> var6.logDirect(var1x));
      } else {
         this.logDirect("No positions known, are you sure the blocks are cached?");
      }
   }

   private Component positionToComponent(BetterBlockPos var1) {
      String var2 = String.format("%s %s %s", var1.x, var1.y, var1.z);
      String var3 = String.format("%sgoal %s", FORCE_COMMAND_PREFIX, var2);
      MutableComponent var4 = Component.literal(var1.toString());
      MutableComponent var5 = Component.literal("Click to set goal to this position");
      var4.setStyle(var4.getStyle().withColor(ChatFormatting.GRAY).withInsertion(var2).withClickEvent(new RunCommand(var3)).withHoverEvent(new ShowText(var5)));
      return var4;
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      return new TabCompleteHelper()
         .append(CachedChunk.BLOCKS_TO_KEEP_TRACK_OF.stream().map(BuiltInRegistries.BLOCK::getKey).map(Object::toString))
         .filterPrefixNamespaced(var2.getString())
         .sortAlphabetically()
         .stream();
   }

   @Override
   public String getShortDesc() {
      return "Find positions of a certain block";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The find command searches through Baritone's cache and attempts to find the location of the block.",
         "Tab completion will suggest only cached blocks and uncached blocks can not be found.",
         "",
         "Usage:",
         "> find <block> [...] - Try finding the listed blocks"
      );
   }
}
