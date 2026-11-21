package baritone.command.defaults;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.pathing.goals.Goal;
import baritone.api.process.ICustomGoalProcess;
import baritone.api.process.IElytraProcess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;

public class ElytraCommand extends Command {
   public ElytraCommand(IBaritone var1) {
      super(var1, "elytra");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandNotEnoughArgumentsException {
      ICustomGoalProcess var3 = this.baritone.getCustomGoalProcess();
      IElytraProcess var4 = this.baritone.getElytraProcess();
      if (var2.hasExactlyOne() && var2.peekString().equals("supported")) {
         this.logDirect(var4.isLoaded() ? "yes" : unsupportedSystemMessage());
      } else {
         if (!var4.isLoaded()) {
            throw new CommandInvalidStateException(unsupportedSystemMessage());
         }

         if (!var2.hasAny()) {
            if (Baritone.settings().elytraTermsAccepted.value) {
               if (this.detectOn2b2t()) {
                  this.warn2b2t();
               }
            } else {
               this.gatekeep();
            }

            Goal var11 = var3.mostRecentGoal();
            if (var11 == null) {
               throw new CommandInvalidStateException("No goal has been set");
            }

            try {
               var4.pathTo(var11);
            } catch (IllegalArgumentException var9) {
               throw new CommandInvalidStateException(var9.getMessage());
            }
         } else {
            String var5 = var2.getString();
            switch (var5) {
               case "reset":
                  try {
                     var4.resetState();
                  } catch (IllegalArgumentException var10) {
                     throw new CommandInvalidStateException(var10.getMessage());
                  }

                  this.logDirect("Reset state but still flying to same goal");
                  break;
               case "repack":
                  var4.repackChunks();
                  this.logDirect("Queued all loaded chunks for repacking");
                  break;
               default:
                  throw new CommandInvalidStateException("Invalid action");
            }
         }
      }
   }

   private void warn2b2t() {
      if (Baritone.settings().elytraPredictTerrain.value) {
         long var1 = Baritone.settings().elytraNetherSeed.value;
         if (var1 != 146008555100680L && var1 != -4100785268875389365L) {
            this.logDirect(Component.literal("It looks like you're on 2b2t, but elytraNetherSeed is incorrect."));
            this.logDirect(this.suggest2b2tSeeds());
         }
      }
   }

   private Component suggest2b2tSeeds() {
      MutableComponent var1 = Component.literal("");
      var1.append(
         "Within a few hundred blocks of spawn/axis/highways/etc, the terrain is too fragmented to be predictable. Baritone Elytra will still work, just with backtracking. "
      );
      var1.append("However, once you get more than a few thousand blocks out, you should try ");
      MutableComponent var2 = Component.literal("the older seed (click here)");
      var2.setStyle(
         var2.getStyle()
            .withUnderlined(true)
            .withBold(true)
            .withHoverEvent(new ShowText(Component.literal(Baritone.settings().prefix.value + "set elytraNetherSeed -4100785268875389365")))
            .withClickEvent(new RunCommand(FORCE_COMMAND_PREFIX + "set elytraNetherSeed -4100785268875389365"))
      );
      var1.append(var2);
      var1.append(". Once you're further out into newer terrain generation (this includes everything up through 1.12), you should try ");
      MutableComponent var3 = Component.literal("the newer seed (click here)");
      var3.setStyle(
         var3.getStyle()
            .withUnderlined(true)
            .withBold(true)
            .withHoverEvent(new ShowText(Component.literal(Baritone.settings().prefix.value + "set elytraNetherSeed 146008555100680")))
            .withClickEvent(new RunCommand(FORCE_COMMAND_PREFIX + "set elytraNetherSeed 146008555100680"))
      );
      var1.append(var3);
      var1.append(
         ". Once you get into 1.19 terrain, the terrain becomes unpredictable again, due to custom non-vanilla generation, and you should set #elytraPredictTerrain to false. "
      );
      return var1;
   }

   private void gatekeep() {
      MutableComponent var1 = Component.literal("");
      var1.append("To disable this message, enable the setting elytraTermsAccepted\n");
      var1.append(
         "Baritone Elytra is an experimental feature. It is intended for long distance travel in the Nether but will also work in the Overworld, using fireworks for vanilla boost. It will not work with any other mods (\"hacks\") for non-vanilla boost. "
      );
      MutableComponent var2 = Component.literal(
         "If you want Baritone to attempt to take off from the ground for you, you can enable the elytraAutoJump setting (not advisable on laggy servers!). "
      );
      var2.setStyle(var2.getStyle().withHoverEvent(new ShowText(Component.literal(Baritone.settings().prefix.value + "set elytraAutoJump true"))));
      var1.append(var2);
      MutableComponent var3 = Component.literal(
         "If you want Baritone to go slower, enable the elytraConserveFireworks setting and/or decrease the elytraFireworkSpeed setting. "
      );
      var3.setStyle(
         var3.getStyle()
            .withHoverEvent(
               new ShowText(
                  Component.literal(
                     Baritone.settings().prefix.value
                        + "set elytraConserveFireworks true\n"
                        + Baritone.settings().prefix.value
                        + "set elytraFireworkSpeed 0.6\n(the 0.6 number is just an example, tweak to your liking)"
                  )
               )
            )
      );
      var1.append(var3);
      MutableComponent var4 = Component.literal("Baritone Elytra for use in the ");
      MutableComponent var5 = Component.literal("Nether");
      var5.setStyle(var5.getStyle().withColor(ChatFormatting.RED).withUnderlined(true).withBold(true));
      var4.append(var5);
      var4.append(", ");
      MutableComponent var6 = Component.literal("wants to know the seed");
      var6.setStyle(var6.getStyle().withColor(ChatFormatting.RED).withUnderlined(true).withBold(true));
      var4.append(var6);
      var4.append(
         " of the world you are in. If it doesn't have the correct seed, it will frequently backtrack. It uses the seed to generate terrain far beyond what you can see, since terrain obstacles in the Nether can be much larger than your render distance. "
      );
      var1.append(var4);
      var1.append("\n");
      if (this.detectOn2b2t()) {
         MutableComponent var7 = Component.literal(
            "It looks like you're on 2b2t. Terrain prediction can be used but new nether terrain can not be predicted on 2b2t. "
         );
         var7.append(this.suggest2b2tSeeds());
         if (!Baritone.settings().elytraPredictTerrain.value) {
            var7.append(Baritone.settings().prefix.value + "elytraPredictTerrain is currently disabled. ");
         } else if (Baritone.settings().elytraNetherSeed.value == 146008555100680L) {
            var7.append("You are using the newer seed. ");
         } else if (Baritone.settings().elytraNetherSeed.value == -4100785268875389365L) {
            var7.append("You are using the older seed. ");
         } else {
            var7.append("Defaulting to the newer seed. ");
            Baritone.settings().elytraNetherSeed.value = 146008555100680L;
         }

         var1.append(var7);
      } else if (Baritone.settings().elytraNetherSeed.value == 146008555100680L) {
         MutableComponent var8 = Component.literal(
            "Baritone doesn't know the seed of your world. Set it with: " + Baritone.settings().prefix.value + "set elytraNetherSeed seedgoeshere\n"
         );
         var8.append("For the time being, elytraPredictTerrain is defaulting to false since the seed is unknown.");
         var1.append(var8);
         Baritone.settings().elytraPredictTerrain.value = false;
      } else if (Baritone.settings().elytraPredictTerrain.value) {
         MutableComponent var9 = Component.literal(
            "Baritone Elytra is predicting terrain assuming that "
               + Baritone.settings().elytraNetherSeed.value
               + " is the correct seed. Change that with "
               + Baritone.settings().prefix.value
               + "set elytraNetherSeed seedgoeshere, or disable it with "
               + Baritone.settings().prefix.value
               + "set elytraPredictTerrain false"
         );
         var1.append(var9);
      } else {
         MutableComponent var10 = Component.literal(
            "Baritone Elytra is not predicting terrain. If you don't know the seed, this is the correct thing to do. If you do know the seed, input it with "
               + Baritone.settings().prefix.value
               + "set elytraNetherSeed seedgoeshere, and then enable it with "
               + Baritone.settings().prefix.value
               + "set elytraPredictTerrain true"
         );
         var1.append(var10);
      }

      this.logDirect(var1);
   }

   private boolean detectOn2b2t() {
      ServerData var1 = this.ctx.minecraft().getCurrentServer();
      return var1 != null && var1.ip.toLowerCase().contains("2b2t.org");
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      TabCompleteHelper var3 = new TabCompleteHelper();
      if (var2.hasExactlyOne()) {
         var3.append("reset", "repack", "supported");
      }

      return var3.filterPrefix(var2.getString()).stream();
   }

   @Override
   public String getShortDesc() {
      return "elytra time";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "The elytra command tells baritone to, in the nether, automatically fly to the current goal.",
         "",
         "Usage:",
         "> elytra - fly to the current goal",
         "> elytra reset - Resets the state of the process, but will try to keep flying to the same goal.",
         "> elytra repack - Queues all of the chunks in render distance to be given to the native library.",
         "> elytra supported - Tells you if baritone ships a native library that is compatible with your PC."
      );
   }

   private static String unsupportedSystemMessage() {
      String var0 = System.getProperty("os.arch");
      String var1 = System.getProperty("os.name");
      return String.format(
         "Failed loading native library. Your CPU is %s and your operating system is %s. Supported architectures are 64 bit x86, and 64 bit ARM. Supported operating systems are Windows, Linux, and Mac",
         var0,
         var1
      );
   }
}
