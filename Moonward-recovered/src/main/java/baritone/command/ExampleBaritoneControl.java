package baritone.command;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.command.argument.ICommandArgument;
import baritone.api.command.exception.CommandNotEnoughArgumentsException;
import baritone.api.command.exception.CommandNotFoundException;
import baritone.api.command.helpers.TabCompleteHelper;
import baritone.api.command.manager.ICommandManager;
import baritone.api.event.events.ChatEvent;
import baritone.api.event.events.TabCompleteEvent;
import baritone.api.utils.Helper;
import baritone.api.utils.SettingsUtil;
import baritone.api.utils.Tuple;
import baritone.behavior.Behavior;
import baritone.command.argument.ArgConsumer;
import baritone.command.argument.CommandArguments;
import baritone.command.manager.CommandManager;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;
import wtf.onetap.module.player.NoCommands;
import wtf.onetap.util.ChatUtil;

public class ExampleBaritoneControl extends Behavior implements Helper {
   private static final Settings settings = BaritoneAPI.getSettings();
   private final ICommandManager manager;

   public ExampleBaritoneControl(Baritone var1) {
      super(var1);
      this.manager = var1.getCommandManager();
   }

   @Override
   public void onSendChatMessage(ChatEvent var1) {
      if (!NoCommands.m_3_boolean()) {
         String var2 = var1.getMessage();
         String var3 = settings.prefix.value;
         boolean var4 = var2.startsWith(FORCE_COMMAND_PREFIX);
         if ((!settings.prefixControl.value || !var2.startsWith(var3)) && !var4) {
            if ((settings.chatControl.value || settings.chatControlAnyway.value) && this.runCommand(var2)) {
               var1.cancel();
            }
         } else {
            var1.cancel();
            String var5 = var2.substring(var4 ? FORCE_COMMAND_PREFIX.length() : var3.length());
            if (!this.runCommand(var5) && !var5.trim().isEmpty()) {
               new CommandNotFoundException(CommandManager.expand(var5).getA()).handle(null, null);
            }
         }
      }
   }

   private void logRanCommand(String var1, String var2) {
      if (settings.echoCommands.value) {
         String var3 = var1 + var2;
         String var4 = settings.censorRanCommands.value ? var1 + " ..." : var3;
         MutableComponent var5 = Component.literal(String.format("> %s", var4));
         var5.setStyle(
            var5.getStyle()
               .withColor(ChatFormatting.WHITE)
               .withHoverEvent(new ShowText(Component.literal("Click to rerun command")))
               .withClickEvent(new RunCommand(FORCE_COMMAND_PREFIX + var3))
         );
         this.logDirect(var5);
      }
   }

   public boolean runCommand(String var1) {
      if (ChatUtil.isUnHooked()) {
         return false;
      }

      if (var1.trim().equalsIgnoreCase("damn")) {
         this.logDirect("daniel");
         return false;
      }

      if (var1.trim().equalsIgnoreCase("orderpizza")) {
         try {
            Util.getPlatform().openUri("https://www.dominos.com/en/pages/order/");
         } catch (Exception var9) {
         }

         return false;
      } else {
         if (var1.isEmpty()) {
            return this.runCommand("help");
         }

         var var2 = CommandManager.expand(var1);
         String var3 = (String)var2.getA();
         String var4 = var1.substring(((String)var2.getA()).length());
         ArgConsumer var5 = new ArgConsumer(this.manager, (List<ICommandArgument>)var2.getB());
         if (!var5.hasAny()) {
            Settings.Setting var6 = settings.byLowerName.get(var3.toLowerCase(Locale.US));
            if (var6 != null) {
               this.logRanCommand(var3, var4);
               if (var6.getValueClass() == Boolean.class) {
                  this.manager.execute(String.format("set toggle %s", var6.getName()));
               } else {
                  this.manager.execute(String.format("set %s", var6.getName()));
               }

               return true;
            }
         } else if (var5.hasExactlyOne()) {
            for (Settings.Setting var7 : settings.allSettings) {
               if (!var7.isJavaOnly() && var7.getName().equalsIgnoreCase((String)var2.getA())) {
                  this.logRanCommand(var3, var4);

                  try {
                     this.manager.execute(String.format("set %s %s", var7.getName(), var5.getString()));
                  } catch (CommandNotEnoughArgumentsException var10) {
                  }

                  return true;
               }
            }
         }

         if (this.manager.getCommand((String)var2.getA()) != null) {
            this.logRanCommand(var3, var4);
         }

         return this.manager.execute(var2);
      }
   }

   @Override
   public void onPreTabComplete(TabCompleteEvent var1) {
      if (!NoCommands.m_3_boolean()) {
         if (settings.prefixControl.value) {
            String var2 = var1.prefix;
            String var3 = settings.prefix.value;
            if (var2.startsWith(var3)) {
               String var4 = var2.substring(var3.length());
               var var5 = CommandArguments.from(var4, true);
               Stream var6 = this.tabComplete(var4);
               if (var5.size() == 1) {
                  var6 = var6.map(var1x -> var3 + var1x);
               }

               var1.completions = (String[])var6.toArray(String[]::new);
            }
         }
      }
   }

   public Stream<String> tabComplete(String var1) {
      if (ChatUtil.isUnHooked()) {
         return Stream.empty();
      }

      try {
         var var2 = CommandArguments.from(var1, true);
         ArgConsumer var3 = new ArgConsumer(this.manager, var2);
         if (var3.hasAtMost(2)) {
            if (var3.hasExactly(1)) {
               return new TabCompleteHelper().addCommands(this.manager).addSettings().filterPrefix(var3.getString()).stream();
            }

            Settings.Setting var4 = settings.byLowerName.get(var3.getString().toLowerCase(Locale.US));
            if (var4 != null && !var4.isJavaOnly()) {
               if (var4.getValueClass() == Boolean.class) {
                  TabCompleteHelper var5 = new TabCompleteHelper();
                  if ((Boolean)var4.value) {
                     var5.append("true", "false");
                  } else {
                     var5.append("false", "true");
                  }

                  return var5.filterPrefix(var3.getString()).stream();
               }

               return Stream.of(SettingsUtil.settingValueToString(var4));
            }
         }

         return this.manager.tabComplete(var1);
      } catch (CommandNotEnoughArgumentsException var6) {
         return Stream.empty();
      }
   }
}
