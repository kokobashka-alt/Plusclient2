package baritone.command.defaults;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.RelativeFile;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.command.exception.CommandInvalidTypeException;
import baritone.api.command.helpers.Paginator;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.utils.SettingsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.ClickEvent.SuggestCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;

public class SetCommand extends Command {
   public SetCommand(IBaritone var1) {
      super(var1, "set", "setting", "settings");
   }

   @Override
   public void execute(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandInvalidStateException, baritone.api.command.exception.CommandInvalidTypeException, baritone.api.command.exception.CommandNotEnoughArgumentsException, baritone.api.command.exception.CommandTooManyArgumentsException {
      String var3 = var2.hasAny() ? var2.getString().toLowerCase(Locale.US) : "list";
      if (Arrays.asList("s", "save").contains(var3)) {
         SettingsUtil.save(Baritone.settings());
         this.logDirect("Settings saved");
      } else if (Arrays.asList("load", "ld").contains(var3)) {
         String var16 = "settings.txt";
         if (var2.hasAny()) {
            var16 = var2.getString();
         }

         SettingsUtil.modifiedSettings(Baritone.settings()).forEach(Settings.Setting::reset);
         SettingsUtil.readAndApply(Baritone.settings(), var16);
         this.logDirect("Settings reloaded from " + var16);
      } else {
         boolean var4 = Arrays.asList("m", "mod", "modified").contains(var3);
         boolean var5 = Arrays.asList("all", "l", "list").contains(var3);
         boolean var6 = var4 || var5;
         if (var6) {
            String var17 = var2.hasAny() && var2.peekAsOrNull(Integer.class) == null ? var2.getString() : "";
            var2.requireMax(1);
            List var18 = (var4 ? SettingsUtil.modifiedSettings(Baritone.settings()) : Baritone.settings().allSettings)
               .stream()
               .filter(var0 -> !var0.isJavaOnly())
               .filter(var1x -> var1x.getName().toLowerCase(Locale.US).contains(var17.toLowerCase(Locale.US)))
               .sorted((var0, var1x) -> String.CASE_INSENSITIVE_ORDER.compare(var0.getName(), var1x.getName()))
               .collect(Collectors.toList());
            Paginator.paginate(
               var2,
               new Paginator<>(var18),
               () -> this.logDirect(
                  !var17.isEmpty()
                     ? String.format("All %ssettings containing the string '%s':", var4 ? "modified " : "", var17)
                     : String.format("All %ssettings:", var4 ? "modified " : "")
               ),
               var0 -> {
                  MutableComponent var1x = Component.literal(String.format(" (%s)", SettingsUtil.settingTypeToString((baritone.api.Settings.Setting)var0)));
                  var1x.setStyle(var1x.getStyle().withColor(ChatFormatting.DARK_GRAY));
                  MutableComponent var2x = Component.literal("");
                  var2x.setStyle(var2x.getStyle().withColor(ChatFormatting.GRAY));
                  var2x.append(((baritone.api.Settings.Setting)var0).getName());
                  var2x.append(String.format("\nType: %s", SettingsUtil.settingTypeToString((baritone.api.Settings.Setting)var0)));
                  var2x.append(String.format("\n\nValue:\n%s", SettingsUtil.settingValueToString((Settings.Setting)var0)));
                  var2x.append(String.format("\n\nDefault Value:\n%s", SettingsUtil.settingDefaultToString((baritone.api.Settings.Setting)var0)));
                  String var3x = Baritone.settings().prefix.value + String.format("set %s ", ((baritone.api.Settings.Setting)var0).getName());
                  MutableComponent var4x = Component.literal(((baritone.api.Settings.Setting)var0).getName());
                  var4x.setStyle(var4x.getStyle().withColor(ChatFormatting.GRAY));
                  var4x.append(var1x);
                  var4x.setStyle(var4x.getStyle().withHoverEvent(new ShowText(var2x)).withClickEvent(new SuggestCommand(var3x)));
                  return var4x;
               },
               FORCE_COMMAND_PREFIX + "set " + var3 + " " + var17
            );
         } else {
            var2.requireMax(1);
            boolean var7 = var3.equalsIgnoreCase("reset");
            boolean var8 = var3.equalsIgnoreCase("toggle");
            boolean var9 = var7 || var8;
            if (var7) {
               if (!var2.hasAny()) {
                  this.logDirect("Please specify 'all' as an argument to reset to confirm you'd really like to do this");
                  this.logDirect("ALL settings will be reset. Use the 'set modified' or 'modified' commands to see what will be reset.");
                  this.logDirect("Specify a setting name instead of 'all' to only reset one setting");
               } else if (var2.peekString().equalsIgnoreCase("all")) {
                  SettingsUtil.modifiedSettings(Baritone.settings()).forEach(Settings.Setting::reset);
                  this.logDirect("All settings have been reset to their default values");
                  SettingsUtil.save(Baritone.settings());
                  return;
               }
            }

            if (var8) {
               var2.requireMin(1);
            }

            String var10 = var9 ? var2.getString() : var3;
            Settings.Setting var11 = Baritone.settings().allSettings.stream().filter(var1x -> var1x.getName().equalsIgnoreCase(var10)).findFirst().orElse(null);
            if (var11 == null) {
               throw new CommandInvalidTypeException(var2.consumed(), "a valid setting");
            }

            if (var11.isJavaOnly()) {
               throw new CommandInvalidStateException(String.format("Setting %s can only be used via the api.", var11.getName()));
            }

            if (!var9 && !var2.hasAny()) {
               this.logDirect(String.format("Value of setting %s:", var11.getName()));
               this.logDirect(SettingsUtil.settingValueToString(var11));
            } else {
               String var12 = SettingsUtil.settingValueToString(var11);
               if (var7) {
                  var11.reset();
               } else if (var8) {
                  if (var11.getValueClass() != Boolean.class) {
                     throw new CommandInvalidTypeException(var2.consumed(), "a toggleable setting", "some other setting");
                  }

                  Settings.Setting var13 = var11;
                  Settings.Setting var14 = var13;
                  var14.value = (Boolean)var14.value ^ true;
                  this.logDirect(String.format("Toggled setting %s to %s", var11.getName(), Boolean.toString((Boolean)var11.value)));
               } else {
                  String var19 = var2.getString();

                  try {
                     SettingsUtil.parseAndApply(Baritone.settings(), var3, var19);
                  } catch (Throwable var15) {
                     var15.printStackTrace();
                     throw new CommandInvalidTypeException(var2.consumed(), "a valid value", var15);
                  }
               }

               if (!var8) {
                  this.logDirect(String.format("Successfully %s %s to %s", var7 ? "reset" : "set", var11.getName(), SettingsUtil.settingValueToString(var11)));
               }

               MutableComponent var20 = Component.literal(String.format("Old value: %s", var12));
               var20.setStyle(
                  var20.getStyle()
                     .withColor(ChatFormatting.GRAY)
                     .withHoverEvent(new ShowText(Component.literal("Click to set the setting back to this value")))
                     .withClickEvent(new RunCommand(FORCE_COMMAND_PREFIX + String.format("set %s %s", var11.getName(), var12)))
               );
               this.logDirect(var20);
               if ((!var11.getName().equals("chatControl") || (Boolean)var11.value || Baritone.settings().chatControlAnyway.value)
                  && (!var11.getName().equals("chatControlAnyway") || (Boolean)var11.value || Baritone.settings().chatControl.value)) {
                  if (var11.getName().equals("prefixControl") && !(Boolean)var11.value) {
                     this.logDirect(
                        "Warning: Prefixed commands will no longer work. If you want to revert this change, use chat control (if enabled) or click the old value listed above.",
                        ChatFormatting.RED
                     );
                  }
               } else {
                  this.logDirect(
                     "Warning: Chat commands will no longer work. If you want to revert this change, use prefix control (if enabled) or click the old value listed above.",
                     ChatFormatting.RED
                  );
               }
            }

            SettingsUtil.save(Baritone.settings());
         }
      }
   }

   @Override
   public Stream<String> tabComplete(String var1, IArgConsumer var2) throws baritone.api.command.exception.CommandNotEnoughArgumentsException {
      if (var2.hasAny()) {
         String var3 = var2.getString();
         if (var2.hasExactlyOne() && !Arrays.asList("s", "save").contains(var2.peekString().toLowerCase(Locale.US))) {
            if (var3.equalsIgnoreCase("reset")) {
               return new TabCompleteHelper().addModifiedSettings().prepend("all").filterPrefix(var2.getString()).stream();
            }

            if (var3.equalsIgnoreCase("toggle")) {
               return new TabCompleteHelper().addToggleableSettings().filterPrefix(var2.getString()).stream();
            }

            if (Arrays.asList("ld", "load").contains(var3.toLowerCase(Locale.US))) {
               return RelativeFile.tabComplete(var2, Minecraft.getInstance().gameDirectory.toPath().resolve("baritone").toFile());
            }

            Settings.Setting var4 = Baritone.settings().byLowerName.get(var3.toLowerCase(Locale.US));
            if (var4 != null) {
               if (var4.getType() == Boolean.class) {
                  TabCompleteHelper var5 = new TabCompleteHelper();
                  if ((Boolean)var4.value) {
                     var5.append("true", "false");
                  } else {
                     var5.append("false", "true");
                  }

                  return var5.filterPrefix(var2.getString()).stream();
               }

               return Stream.of(SettingsUtil.settingValueToString(var4));
            }
         } else if (!var2.hasAny()) {
            return new TabCompleteHelper()
               .addSettings()
               .sortAlphabetically()
               .prepend("list", "modified", "reset", "toggle", "save", "load")
               .filterPrefix(var3)
               .stream();
         }
      }

      return Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return "View or change settings";
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "Using the set command, you can manage all of Baritone's settings. Almost every aspect is controlled by these settings - go wild!",
         "",
         "Usage:",
         "> set - Same as `set list`",
         "> set list [page] - View all settings",
         "> set modified [page] - View modified settings",
         "> set <setting> - View the current value of a setting",
         "> set <setting> <value> - Set the value of a setting",
         "> set reset all - Reset ALL SETTINGS to their defaults",
         "> set reset <setting> - Reset a setting to its default",
         "> set toggle <setting> - Toggle a boolean setting",
         "> set save - Save all settings (this is automatic tho)",
         "> set load - Load settings from settings.txt",
         "> set load [filename] - Load settings from another file in your minecraft/baritone"
      );
   }
}
