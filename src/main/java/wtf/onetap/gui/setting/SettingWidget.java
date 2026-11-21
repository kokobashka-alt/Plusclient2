package wtf.onetap.gui.setting;

public interface SettingWidget {
   void deactivate();

   void onKeyPress(int value);

   void bindKey(int value);
}
