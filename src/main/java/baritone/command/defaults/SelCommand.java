package baritone.command.defaults;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.ForAxis;
import baritone.api.command.datatypes.ForBlockOptionalMeta;
import baritone.api.command.datatypes.ForDirection;
import baritone.api.command.datatypes.RelativeBlockPos;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.command.exception.CommandInvalidTypeException;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.event.events.RenderEvent;
import baritone.api.event.listener.AbstractGameEventListener;
import baritone.api.schematic.CompositeSchematic;
import baritone.api.schematic.FillSchematic;
import baritone.api.schematic.ISchematic;
import baritone.api.schematic.MaskSchematic;
import baritone.api.schematic.ReplaceSchematic;
import baritone.api.schematic.ShellSchematic;
import baritone.api.schematic.WallsSchematic;
import baritone.api.schematic.mask.shape.CylinderMask;
import baritone.api.schematic.mask.shape.SphereMask;
import baritone.api.selection.ISelection;
import baritone.api.selection.ISelectionManager;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMeta;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.utils.BlockStateInterface;
import baritone.utils.IRenderer;
import baritone.utils.schematic.StaticSchematic;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SelCommand extends Command {
   private ISelectionManager manager = this.baritone.getSelectionManager();
   private BetterBlockPos pos1 = null;
   private ISchematic clipboard = null;
   private Vec3i clipboardOffset = null;

   public SelCommand(IBaritone var1) {
      super(var1, "sel", "selection", "s");
      var1.getGameEventHandler().registerEventListener(new AbstractGameEventListener() {
         @Override
         public void onRenderPass(RenderEvent var1) {
            if (Baritone.settings().renderSelectionCorners.value && SelCommand.this.pos1 != null) {
               Color var2 = Baritone.settings().colorSelectionPos1.value;
               float var3 = Baritone.settings().selectionOpacity.value;
               float var4 = Baritone.settings().selectionLineWidth.value;
               boolean var5 = Baritone.settings().renderSelectionIgnoreDepth.value;
               BufferBuilder var6 = IRenderer.startLines(var2, var3);
               IRenderer.emitAABB(var6, var1.getModelViewStack(), new AABB(SelCommand.this.pos1), var4);
               IRenderer.endLines(var6, var5);
            }
         }
      });
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandTooManyArgumentsException {
      SelCommand.Action var3 = SelCommand.Action.getByName(var2.getString());
      if (var3 == null) {
         throw new CommandInvalidTypeException(var2.consumed(), "an action");
      }

      if (var3 != SelCommand.Action.POS1 && var3 != SelCommand.Action.POS2) {
         if (var3 == SelCommand.Action.CLEAR) {
            var2.requireMax(0);
            this.pos1 = null;
            this.logDirect(String.format("Removed %d selections", this.manager.removeAllSelections().length));
         } else if (var3 == SelCommand.Action.UNDO) {
            var2.requireMax(0);
            if (this.pos1 != null) {
               this.pos1 = null;
               this.logDirect("Undid pos1");
            } else {
               ISelection[] var20 = this.manager.getSelections();
               if (var20.length < 1) {
                  throw new CommandInvalidStateException("Nothing to undo!");
               }

               this.pos1 = this.manager.removeSelection(var20[var20.length - 1]).pos1();
               this.logDirect("Undid pos2");
            }
         } else if (var3.isFillAction()) {
            BlockOptionalMeta var21 = var3 == SelCommand.Action.CLEARAREA
               ? new BlockOptionalMeta(Blocks.AIR)
               : var2.getDatatypeFor(ForBlockOptionalMeta.INSTANCE);
            Axis var6;
            BlockOptionalMetaLookup var25;
            if (var3 != SelCommand.Action.REPLACE) {
               if (var3 != SelCommand.Action.CYLINDER && var3 != SelCommand.Action.HCYLINDER) {
                  var2.requireMax(0);
                  var25 = null;
                  var6 = null;
               } else {
                  var2.requireMax(1);
                  var6 = var2.hasAny() ? var2.getDatatypeFor(ForAxis.INSTANCE) : Axis.Y;
                  var25 = null;
               }
            } else {
               var2.requireMin(1);
               ArrayList var7 = new ArrayList();
               var7.add(var21);

               while (var2.has(2)) {
                  var7.add(var2.getDatatypeFor(ForBlockOptionalMeta.INSTANCE));
               }

               var21 = var2.getDatatypeFor(ForBlockOptionalMeta.INSTANCE);
               var25 = new BlockOptionalMetaLookup((String[])var7.toArray(new BlockOptionalMeta[0]));
               var6 = null;
            }

            ISelection[] var31 = this.manager.getSelections();
            if (var31.length == 0) {
               throw new CommandInvalidStateException("No selections");
            }

            BetterBlockPos var8 = var31[0].min();
            CompositeSchematic var9 = new CompositeSchematic(0, 0, 0);

            for (ISelection var13 : var31) {
               BetterBlockPos var14 = var13.min();
               var8 = new BetterBlockPos(Math.min(var8.x, var14.x), Math.min(var8.y, var14.y), Math.min(var8.z, var14.z));
            }

            for (ISelection var50 : var31) {
               Vec3i var53 = var50.size();
               BetterBlockPos var15 = var50.min();
               UnaryOperator<baritone.api.schematic.ISchematic> var16 = var3x -> {
                  int var4x = var3x.widthX();
                  int var5x = var3x.heightY();
                  int var6x = var3x.lengthZ();
                  switch (var3) {
                     case WALLS:
                        return new WallsSchematic(var3x);
                     case SHELL:
                        return new ShellSchematic(var3x);
                     case SPHERE:
                        return MaskSchematic.create(var3x, new SphereMask(var4x, var5x, var6x, true).compute());
                     case HSPHERE:
                        return MaskSchematic.create(var3x, new SphereMask(var4x, var5x, var6x, false).compute());
                     case CYLINDER:
                        return MaskSchematic.create(var3x, new CylinderMask(var4x, var5x, var6x, true, var6).compute());
                     case HCYLINDER:
                        return MaskSchematic.create(var3x, new CylinderMask(var4x, var5x, var6x, false, var6).compute());
                     case CLEARAREA:
                     default:
                        return var3x;
                     case REPLACE:
                        return new ReplaceSchematic(var3x, var25);
                  }
               };
               ISchematic var17 = var16.apply(new FillSchematic(var53.getX(), var53.getY(), var53.getZ(), var21));
               var9.put(var17, var15.x - var8.x, var15.y - var8.y, var15.z - var8.z);
            }

            this.baritone.getBuilderProcess().build("Fill", var9, var8);
            this.logDirect("Filling now");
         } else if (var3 == SelCommand.Action.COPY) {
            BetterBlockPos var22 = this.ctx.viewerPos();
            BetterBlockPos var26 = var2.hasAny() ? var2.getDatatypePost(RelativeBlockPos.INSTANCE, var22) : var22;
            var2.requireMax(0);
            ISelection[] var29 = this.manager.getSelections();
            if (var29.length < 1) {
               throw new CommandInvalidStateException("No selections");
            }

            BlockStateInterface var32 = new BlockStateInterface(this.ctx);
            BetterBlockPos var35 = var29[0].min();
            CompositeSchematic var37 = new CompositeSchematic(0, 0, 0);

            for (ISelection var51 : var29) {
               BetterBlockPos var54 = var51.min();
               var35 = new BetterBlockPos(Math.min(var35.x, var54.x), Math.min(var35.y, var54.y), Math.min(var35.z, var54.z));
            }

            for (ISelection var52 : var29) {
               Vec3i var55 = var52.size();
               BetterBlockPos var56 = var52.min();
               BlockState[][][] var57 = new BlockState[var55.getX()][var55.getZ()][var55.getY()];

               for (int var58 = 0; var58 < var55.getX(); var58++) {
                  for (int var18 = 0; var18 < var55.getY(); var18++) {
                     for (int var19 = 0; var19 < var55.getZ(); var19++) {
                        var57[var58][var19][var18] = var32.get0(var56.x + var58, var56.y + var18, var56.z + var19);
                     }
                  }
               }

               StaticSchematic var59 = new StaticSchematic(var57);
               var37.put(var59, var56.x - var35.x, var56.y - var35.y, var56.z - var35.z);
            }

            this.clipboard = var37;
            this.clipboardOffset = var35.subtract(var26);
            this.logDirect("Selection copied");
         } else if (var3 == SelCommand.Action.PASTE) {
            BetterBlockPos var23 = this.ctx.viewerPos();
            BetterBlockPos var27 = var2.hasAny() ? var2.getDatatypePost(RelativeBlockPos.INSTANCE, var23) : var23;
            var2.requireMax(0);
            if (this.clipboard == null) {
               throw new CommandInvalidStateException("You need to copy a selection first");
            }

            this.baritone.getBuilderProcess().build("Fill", this.clipboard, var27.offset(this.clipboardOffset));
            this.logDirect("Building now");
         } else if (var3 == SelCommand.Action.EXPAND || var3 == SelCommand.Action.CONTRACT || var3 == SelCommand.Action.SHIFT) {
            var2.requireExactly(3);
            SelCommand.TransformTarget var24 = SelCommand.TransformTarget.getByName(var2.getString());
            if (var24 == null) {
               throw new CommandInvalidStateException("Invalid transform type");
            }

            Direction var28 = var2.getDatatypeFor(ForDirection.INSTANCE);
            int var30 = var2.getAs(Integer.class);
            ISelection[] var33 = this.manager.getSelections();
            if (var33.length < 1) {
               throw new CommandInvalidStateException("No selections found");
            }

            var33 = var24.transform(var33);

            for (ISelection var46 : var33) {
               if (var3 == SelCommand.Action.EXPAND) {
                  this.manager.expand(var46, var28, var30);
               } else if (var3 == SelCommand.Action.CONTRACT) {
                  this.manager.contract(var46, var28, var30);
               } else {
                  this.manager.shift(var46, var28, var30);
               }
            }

            this.logDirect(String.format("Transformed %d selections", var33.length));
         }
      } else {
         if (var3 == SelCommand.Action.POS2 && this.pos1 == null) {
            throw new CommandInvalidStateException("Set pos1 first before using pos2");
         }

         BetterBlockPos var4 = this.ctx.viewerPos();
         BetterBlockPos var5 = var2.hasAny() ? var2.getDatatypePost(RelativeBlockPos.INSTANCE, var4) : var4;
         var2.requireMax(0);
         if (var3 == SelCommand.Action.POS1) {
            this.pos1 = var5;
            this.logDirect("Position 1 has been set");
         } else {
            this.manager.addSelection(this.pos1, var5);
            this.pos1 = null;
            this.logDirect("Selection added");
         }
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      if (var2.hasExactlyOne()) {
         return new TabCompleteHelper().append(SelCommand.Action.getAllNames()).filterPrefix(var2.getString()).sortAlphabetically().stream();
      }

      SelCommand.Action var3 = SelCommand.Action.getByName(var2.getString());
      if (var3 != null) {
         if (var3 != SelCommand.Action.POS1 && var3 != SelCommand.Action.POS2) {
            if (var3.isFillAction()) {
               if (var2.hasExactlyOne() || var3 == SelCommand.Action.REPLACE) {
                  while (var2.has(2)) {
                     var2.get();
                  }

                  return var2.tabCompleteDatatype(ForBlockOptionalMeta.INSTANCE);
               }

               if (var2.hasExactly(2) && (var3 == SelCommand.Action.CYLINDER || var3 == SelCommand.Action.HCYLINDER)) {
                  var2.get();
                  return var2.tabCompleteDatatype(ForAxis.INSTANCE);
               }
            } else if (var3 == SelCommand.Action.EXPAND || var3 == SelCommand.Action.CONTRACT || var3 == SelCommand.Action.SHIFT) {
               if (var2.hasExactlyOne()) {
                  return new TabCompleteHelper().append(SelCommand.TransformTarget.getAllNames()).filterPrefix(var2.getString()).sortAlphabetically().stream();
               }

               SelCommand.TransformTarget var4 = SelCommand.TransformTarget.getByName(var2.getString());
               if (var4 != null && var2.hasExactlyOne()) {
                  return var2.tabCompleteDatatype(ForDirection.INSTANCE);
               }
            }
         } else if (var2.hasAtMost(3)) {
            return var2.tabCompleteDatatype(RelativeBlockPos.INSTANCE);
         }
      }

      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "WorldEdit-like commands";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The sel command allows you to manipulate Baritone's selections, similarly to WorldEdit.",
         "",
         "Using these selections, you can clear areas, fill them with blocks, or something else.",
         "",
         "The expand/contract/shift commands use a kind of selector to choose which selections to target. Supported ones are a/all, n/newest, and o/oldest.",
         "",
         "Usage:",
         "> sel pos1/p1/1 - Set position 1 to your current position.",
         "> sel pos1/p1/1 <x> <y> <z> - Set position 1 to a relative position.",
         "> sel pos2/p2/2 - Set position 2 to your current position.",
         "> sel pos2/p2/2 <x> <y> <z> - Set position 2 to a relative position.",
         "",
         "> sel clear/c - Clear the selection.",
         "> sel undo/u - Undo the last action (setting positions, creating selections, etc.)",
         "> sel set/fill/s/f [block] - Completely fill all selections with a block.",
         "> sel walls/w [block] - Fill in the walls of the selection with a specified block.",
         "> sel shell/shl [block] - The same as walls, but fills in a ceiling and floor too.",
         "> sel sphere/sph [block] - Fills the selection with a sphere bounded by the sides.",
         "> sel hsphere/hsph [block] - The same as sphere, but hollow.",
         "> sel cylinder/cyl [block] <axis> - Fills the selection with a cylinder bounded by the sides, oriented about the given axis. (default=y)",
         "> sel hcylinder/hcyl [block] <axis> - The same as cylinder, but hollow.",
         "> sel cleararea/ca - Basically 'set air'.",
         "> sel replace/r <blocks...> <with> - Replaces blocks with another block.",
         "> sel copy/cp <x> <y> <z> - Copy the selected area relative to the specified or your position.",
         "> sel paste/p <x> <y> <z> - Build the copied area relative to the specified or your position.",
         "",
         "> sel expand <target> <direction> <blocks> - Expand the targets.",
         "> sel contract <target> <direction> <blocks> - Contract the targets.",
         "> sel shift <target> <direction> <blocks> - Shift the targets (does not resize)."
      );
   }

   enum Action {
      POS1("pos1", "p1", "1"),
      POS2("pos2", "p2", "2"),
      CLEAR("clear", "c"),
      UNDO("undo", "u"),
      SET("set", "fill", "s", "f"),
      WALLS("walls", "w"),
      SHELL("shell", "shl"),
      SPHERE("sphere", "sph"),
      HSPHERE("hsphere", "hsph"),
      CYLINDER("cylinder", "cyl"),
      HCYLINDER("hcylinder", "hcyl"),
      CLEARAREA("cleararea", "ca"),
      REPLACE("replace", "r"),
      EXPAND("expand", "ex"),
      COPY("copy", "cp"),
      PASTE("paste", "p"),
      CONTRACT("contract", "ct"),
      SHIFT("shift", "sh");

      private final String[] names;

      Action(String... var3) {
         this.names = var3;
      }

      public static SelCommand.Action getByName(String var0) {
         for (SelCommand.Action var4 : values()) {
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

         for (SelCommand.Action var4 : values()) {
            var0.addAll(Arrays.asList(var4.names));
         }

         return (String[])var0.toArray(new String[0]);
      }

      public final boolean isFillAction() {
         return this == SET
            || this == WALLS
            || this == SHELL
            || this == SPHERE
            || this == HSPHERE
            || this == CYLINDER
            || this == HCYLINDER
            || this == CLEARAREA
            || this == REPLACE;
      }
   }

   enum TransformTarget {
      ALL(var0 -> var0, "all", "a"),
      NEWEST(var0 -> new ISelection[]{var0[var0.length - 1]}, "newest", "n"),
      OLDEST(var0 -> new ISelection[]{var0[0]}, "oldest", "o");

      private final Function<ISelection[], ISelection[]> transform;
      private final String[] names;

      TransformTarget(Function<ISelection[], ISelection[]> var3, String... var4) {
         this.transform = var3;
         this.names = var4;
      }

      public ISelection[] transform(ISelection[] var1) {
         return this.transform.apply(var1);
      }

      public static SelCommand.TransformTarget getByName(String var0) {
         for (SelCommand.TransformTarget var4 : values()) {
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

         for (SelCommand.TransformTarget var4 : values()) {
            var0.addAll(Arrays.asList(var4.names));
         }

         return (String[])var0.toArray(new String[0]);
      }
   }
}
