package wtf.onetap.autobuy;

import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class AttributeMatcher extends ItemMatcher {
   private final String text;

   public AttributeMatcher(String text2) {
      this(text2, 0);
   }

   public AttributeMatcher(String text2, int value) {
      this(text2, value, ItemMatcher.ModeKind.C0);
   }

   public AttributeMatcher(String text2, int value, ItemMatcher.ModeKind c0) {
      super(c0, c0 == ItemMatcher.ModeKind.C0 && value > 0 ? Math.min(m_9_int(text2), value) : value);
      this.text = text2;
   }

   private static int m_9_int(String text2) {
      return !"Окисление".equals(text2) && !"Вампиризм".equals(text2) ? 3 : 2;
   }

   @Override
   public int m_7_int(int value) {
      return Math.max(1, Math.min(m_9_int(this.text), value));
   }

   @Override
   public boolean matches(ItemStack itemStack) {
      if (this.getC0() == ItemMatcher.ModeKind.OFF) {
         return true;
      }

      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.level != null) {
         String text2 = itemStack.getTooltipLines(TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL)
            .stream()
            .skip(1L)
            .map(component -> component.getString().replaceAll("§.", "").toLowerCase(Locale.ROOT).replaceAll("\\s+", "").trim())
            .collect(Collectors.joining());
         String text3 = this.text.replaceAll("§.", "").toLowerCase(Locale.ROOT).replaceAll("\\s+", "").trim();
         if (this.getC0() == ItemMatcher.ModeKind.DENY) {
            return !text2.contains(text3);
         }

         if (!text2.contains(text3)) {
            return false;
         }

         if (this.getValue() == 0) {
            return true;
         }

         String text4 = text2.substring(text2.indexOf(text3) + text3.length()).trim();
         int value = text4.isEmpty() ? 0 : ItemMatcher.m_0_int(text4);
         return Math.max(1, value) >= this.getValue();
      } else {
         return true;
      }
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   public String m_5_String() {
      return this.getValue() == 0 ? this.text : this.text + " " + ItemMatcher.toRoman(this.getValue() - 1);
   }
}
