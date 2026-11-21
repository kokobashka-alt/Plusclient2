package wtf.onetap.module.misc;

import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;

@ModuleInformation(name = "RpHelper", description = "Не перезагружает одинаковый ресурспак", category = Category.MISC)
public class RpHelper extends Module {
   private String text;

   public boolean isSameText(String text2) {
      return text2 != null && text2.equalsIgnoreCase(this.text);
   }

   public void setText(String text2) {
      this.text = text2;
   }

   public void m_2_void() {
      this.text = null;
   }

   @Override
   public void onDisable() {
      this.m_2_void();
      super.onDisable();
   }
}
