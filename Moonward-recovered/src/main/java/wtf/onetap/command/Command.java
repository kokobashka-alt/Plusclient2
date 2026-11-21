package wtf.onetap.command;

public interface Command {
   String getText2();

   String getText3();

   String getUsage();

   void showHelp(CommandHelp commandHelp);
}
