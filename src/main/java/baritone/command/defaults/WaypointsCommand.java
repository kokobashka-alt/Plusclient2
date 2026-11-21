package baritone.command.defaults;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.cache.IWaypoint;
import baritone.api.cache.IWorldData;
import baritone.api.cache.Waypoint;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.ForWaypoints;
import baritone.api.command.datatypes.RelativeBlockPos;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.command.exception.CommandInvalidTypeException;
import baritone.api.command.helpers.Paginator;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.ClickEvent.SuggestCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;

public class WaypointsCommand extends Command {
   private Map<IWorldData, List<IWaypoint>> deletedWaypoints = new HashMap<>();

   public WaypointsCommand(IBaritone var1) {
      super(var1, "waypoints", "waypoint", "wp");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandTooManyArgumentsException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      WaypointsCommand.Action var3 = var2.hasAny() ? WaypointsCommand.Action.getByName(var2.getString()) : WaypointsCommand.Action.LIST;
      if (var3 == null) {
         throw new CommandInvalidTypeException(var2.consumed(), "an action");
      }

      BiFunction<baritone.api.cache.IWaypoint, baritone.command.defaults.WaypointsCommand.Action, net.minecraft.network.chat.Component> var4 = (var1x, var2x) -> {
         MutableComponent var3x = Component.literal("");
         MutableComponent var4x = Component.literal(var1x.getTag().name() + " ");
         var4x.setStyle(var4x.getStyle().withColor(ChatFormatting.GRAY));
         String var5x = var1x.getName();
         MutableComponent var6x = Component.literal(!var5x.isEmpty() ? var5x : "<empty>");
         var6x.setStyle(var6x.getStyle().withColor(!var5x.isEmpty() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
         MutableComponent var7x = Component.literal(" @ " + new Date(var1x.getCreationTimestamp()));
         var7x.setStyle(var7x.getStyle().withColor(ChatFormatting.DARK_GRAY));
         var3x.append(var4x);
         var3x.append(var6x);
         var3x.append(var7x);
         var3x.setStyle(
            var3x.getStyle()
               .withHoverEvent(new ShowText(Component.literal("Click to select")))
               .withClickEvent(
                  new RunCommand(
                     String.format("%s%s %s %s @ %d", FORCE_COMMAND_PREFIX, var1, var2x.names[0], var1x.getTag().getName(), var1x.getCreationTimestamp())
                  )
               )
         );
         return var3x;
      };
      Function<baritone.api.cache.IWaypoint, net.minecraft.network.chat.Component> var5 = var2x -> (Component)var4.apply(var2x, var3 == WaypointsCommand.Action.LIST ? WaypointsCommand.Action.INFO : var3);
      if (var3 == WaypointsCommand.Action.LIST) {
         IWaypoint.Tag var6 = var2.hasAny() ? IWaypoint.Tag.getByName(var2.peekString()) : null;
         if (var6 != null) {
            var2.get();
         }

         IWaypoint[] var7 = var6 != null ? ForWaypoints.getWaypointsByTag(this.baritone, var6) : ForWaypoints.getWaypoints(this.baritone);
         if (var7.length <= 0) {
            var2.requireMax(0);
            throw new CommandInvalidStateException(var6 != null ? "No waypoints found by that tag" : "No waypoints found");
         }

         var2.requireMax(1);
         Paginator.paginate(
            var2,
            var7,
            () -> this.logDirect(var6 != null ? String.format("All waypoints by tag %s:", var6.name()) : "All waypoints:"),
            var5,
            String.format("%s%s %s%s", FORCE_COMMAND_PREFIX, var1, var3.names[0], var6 != null ? " " + var6.getName() : "")
         );
      } else if (var3 == WaypointsCommand.Action.SAVE) {
         IWaypoint.Tag var14 = var2.hasAny() ? IWaypoint.Tag.getByName(var2.peekString()) : null;
         if (var14 == null) {
            var14 = IWaypoint.Tag.USER;
         } else {
            var2.get();
         }

         String var18 = !var2.hasExactlyOne() && !var2.hasExactly(4) ? "" : var2.getString();
         BetterBlockPos var8 = var2.hasAny() ? var2.getDatatypePost(RelativeBlockPos.INSTANCE, this.ctx.playerFeet()) : this.ctx.playerFeet();
         var2.requireMax(0);
         Waypoint var9 = new Waypoint(var18, var14, var8);
         ForWaypoints.waypoints(this.baritone).addWaypoint(var9);
         MutableComponent var10 = Component.literal("Waypoint added: ");
         var10.setStyle(var10.getStyle().withColor(ChatFormatting.GRAY));
         var10.append((Component)var4.apply(var9, WaypointsCommand.Action.INFO));
         this.logDirect(var10);
      } else if (var3 == WaypointsCommand.Action.CLEAR) {
         var2.requireMax(1);
         String var15 = var2.getString();
         IWaypoint.Tag var19 = IWaypoint.Tag.getByName(var15);
         if (var19 == null) {
            throw new CommandInvalidStateException("Invalid tag, \"" + var15 + "\"");
         }

         IWaypoint[] var22 = ForWaypoints.getWaypointsByTag(this.baritone, var19);

         for (IWaypoint var12 : var22) {
            ForWaypoints.waypoints(this.baritone).removeWaypoint(var12);
         }

         this.deletedWaypoints.computeIfAbsent(this.baritone.getWorldProvider().getCurrentWorld(), var0 -> new ArrayList<>()).addAll(Arrays.asList(var22));
         MutableComponent var31 = Component.literal(String.format("Cleared %d waypoints, click to restore them", var22.length));
         var31.setStyle(
            var31.getStyle()
               .withClickEvent(
                  new RunCommand(
                     String.format(
                        "%s%s restore @ %s",
                        FORCE_COMMAND_PREFIX,
                        var1,
                        Stream.of(var22).map(var0 -> Long.toString(var0.getCreationTimestamp())).collect(Collectors.joining(" "))
                     )
                  )
               )
         );
         this.logDirect(var31);
      } else if (var3 == WaypointsCommand.Action.RESTORE) {
         ArrayList var16 = new ArrayList();
         var var20 = this.deletedWaypoints.getOrDefault(this.baritone.getWorldProvider().getCurrentWorld(), Collections.emptyList());
         if (!var2.peekString().equals("@")) {
            var2.requireExactly(1);
            int var24 = var20.size();
            int var32 = Math.min(var24, var2.getAs(Integer.class));
            var16 = new ArrayList(var20.subList(var24 - var32, var24));
         } else {
            var2.get();

            while (var2.hasAny()) {
               long var23 = var2.getAs(Long.class);

               for (IWaypoint var38 : var20) {
                  if (var38.getCreationTimestamp() == var23) {
                     var16.add(var38);
                     break;
                  }
               }
            }
         }

         ((Iterable<IWaypoint>)(Iterable)var16).forEach(ForWaypoints.waypoints(this.baritone)::addWaypoint);
         var20.removeIf(var16::contains);
         this.logDirect(String.format("Restored %d waypoints", var16.size()));
      } else {
         IWaypoint[] var17 = var2.getDatatypeFor(ForWaypoints.INSTANCE);
         IWaypoint var21 = null;
         if (var2.hasAny() && var2.peekString().equals("@")) {
            var2.requireExactly(2);
            var2.get();
            long var25 = var2.getAs(Long.class);

            for (IWaypoint var13 : var17) {
               if (var13.getCreationTimestamp() == var25) {
                  var21 = var13;
                  break;
               }
            }

            if (var21 == null) {
               throw new CommandInvalidStateException("Timestamp was specified but no waypoint was found");
            }
         } else {
            switch (var17.length) {
               case 0:
                  throw new CommandInvalidStateException("No waypoints found");
               case 1:
                  var21 = var17[0];
            }
         }

         if (var21 == null) {
            var2.requireMax(1);
            Paginator.paginate(
               var2,
               var17,
               () -> this.logDirect("Multiple waypoints were found:"),
               var5,
               String.format("%s%s %s %s", FORCE_COMMAND_PREFIX, var1, var3.names[0], var2.consumedString())
            );
         } else if (var3 == WaypointsCommand.Action.INFO) {
            this.logDirect((Component)var5.apply(var21));
            this.logDirect(String.format("Position: %s", var21.getLocation()));
            MutableComponent var26 = Component.literal("Click to delete this waypoint");
            var26.setStyle(
               var26.getStyle()
                  .withClickEvent(
                     new RunCommand(String.format("%s%s delete %s @ %d", FORCE_COMMAND_PREFIX, var1, var21.getTag().getName(), var21.getCreationTimestamp()))
                  )
            );
            MutableComponent var33 = Component.literal("Click to set goal to this waypoint");
            var33.setStyle(
               var33.getStyle()
                  .withClickEvent(
                     new RunCommand(String.format("%s%s goal %s @ %d", FORCE_COMMAND_PREFIX, var1, var21.getTag().getName(), var21.getCreationTimestamp()))
                  )
            );
            MutableComponent var37 = Component.literal("Click to show a command to recreate this waypoint");
            var37.setStyle(
               var37.getStyle()
                  .withClickEvent(
                     new SuggestCommand(
                        String.format(
                           "%s%s save %s %s %s %s %s",
                           Baritone.settings().prefix.value,
                           var1,
                           var21.getTag().getName(),
                           var21.getName(),
                           var21.getLocation().x,
                           var21.getLocation().y,
                           var21.getLocation().z
                        )
                     )
                  )
            );
            MutableComponent var40 = Component.literal("Click to return to the waypoints list");
            var40.setStyle(var40.getStyle().withClickEvent(new RunCommand(String.format("%s%s list", FORCE_COMMAND_PREFIX, var1))));
            this.logDirect(var26);
            this.logDirect(var33);
            this.logDirect(var37);
            this.logDirect(var40);
         } else if (var3 == WaypointsCommand.Action.DELETE) {
            ForWaypoints.waypoints(this.baritone).removeWaypoint(var21);
            this.deletedWaypoints.computeIfAbsent(this.baritone.getWorldProvider().getCurrentWorld(), var0 -> new ArrayList<>()).add(var21);
            MutableComponent var27 = Component.literal("That waypoint has successfully been deleted, click to restore it");
            var27.setStyle(
               var27.getStyle().withClickEvent(new RunCommand(String.format("%s%s restore @ %s", FORCE_COMMAND_PREFIX, var1, var21.getCreationTimestamp())))
            );
            this.logDirect(var27);
         } else if (var3 == WaypointsCommand.Action.GOAL) {
            GoalBlock var28 = new GoalBlock(var21.getLocation());
            this.baritone.getCustomGoalProcess().setGoal(var28);
            this.logDirect(String.format("Goal: %s", var28));
         } else if (var3 == WaypointsCommand.Action.GOTO) {
            GoalBlock var29 = new GoalBlock(var21.getLocation());
            this.baritone.getCustomGoalProcess().setGoalAndPath(var29);
            this.logDirect(String.format("Going to: %s", var29));
         }
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      if (var2.hasAny()) {
         if (var2.hasExactlyOne()) {
            return new TabCompleteHelper().append(WaypointsCommand.Action.getAllNames()).sortAlphabetically().filterPrefix(var2.getString()).stream();
         }

         WaypointsCommand.Action var3 = WaypointsCommand.Action.getByName(var2.getString());
         if (var2.hasExactlyOne()) {
            if (var3 != WaypointsCommand.Action.LIST && var3 != WaypointsCommand.Action.SAVE && var3 != WaypointsCommand.Action.CLEAR) {
               if (var3 == WaypointsCommand.Action.RESTORE) {
                  return Stream.empty();
               }

               return var2.tabCompleteDatatype(ForWaypoints.INSTANCE);
            }

            return new TabCompleteHelper().append(IWaypoint.Tag.getAllNames()).sortAlphabetically().filterPrefix(var2.getString()).stream();
         }

         if (var2.has(3) && var3 == WaypointsCommand.Action.SAVE) {
            var2.get();
            var2.get();
            return var2.tabCompleteDatatype(RelativeBlockPos.INSTANCE);
         }
      }

      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "Manage waypoints";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The waypoint command allows you to manage Baritone's waypoints.",
         "",
         "Waypoints can be used to mark positions for later. Waypoints are each given a tag and an optional name.",
         "",
         "Note that the info, delete, and goal commands let you specify a waypoint by tag. If there is more than one waypoint with a certain tag, then they will let you select which waypoint you mean.",
         "",
         "Missing arguments for the save command use the USER tag, creating an unnamed waypoint and your current position as defaults.",
         "",
         "Usage:",
         "> wp [l/list] - List all waypoints.",
         "> wp <l/list> <tag> - List all waypoints by tag.",
         "> wp <s/save> - Save an unnamed USER waypoint at your current position",
         "> wp <s/save> [tag] [name] [pos] - Save a waypoint with the specified tag, name and position.",
         "> wp <i/info/show> <tag/name> - Show info on a waypoint by tag or name.",
         "> wp <d/delete> <tag/name> - Delete a waypoint by tag or name.",
         "> wp <restore> <n> - Restore the last n deleted waypoints.",
         "> wp <c/clear> <tag> - Delete all waypoints with the specified tag.",
         "> wp <g/goal> <tag/name> - Set a goal to a waypoint by tag or name.",
         "> wp <goto> <tag/name> - Set a goal to a waypoint by tag or name and start pathing."
      );
   }

   private enum Action {
      LIST("list", "get", "l"),
      CLEAR("clear", "c"),
      SAVE("save", "s"),
      INFO("info", "show", "i"),
      DELETE("delete", "d"),
      RESTORE("restore"),
      GOAL("goal", "g"),
      GOTO("goto");

      private final String[] names;

      Action(String... var3) {
         this.names = var3;
      }

      public static WaypointsCommand.Action getByName(String var0) {
         for (WaypointsCommand.Action var4 : values()) {
            for (String var8 : var4.names) {
               if (var8.equalsIgnoreCase(var0)) {
                  return var4;
               }
            }
         }

         return null;
      }

      public static String[] getAllNames() {
         HashSet var0 = new HashSet();

         for (WaypointsCommand.Action var4 : values()) {
            var0.addAll(Arrays.asList(var4.names));
         }

         return (String[])var0.toArray(new String[0]);
      }
   }
}
