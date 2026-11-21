package wtf.onetap.command;

import java.util.Collections;
import java.util.List;

public class CommandHelp {
   private final String text;
   private final String text2;
   private final List<String> stringList;
   private static int[] valueArray;

   public CommandHelp(String text3, String[] stringArray) {
      this.text = text3;
      m_9_IArr();
      var stringList2 = List.of(stringArray);
      this.text2 = stringList2.isEmpty() ? null : (String)stringList2.get(0);
      this.stringList = stringList2.isEmpty() ? Collections.emptyList() : stringList2.subList(1, stringList2.size());
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public boolean matches(String text3) {
      return text3.equalsIgnoreCase(this.text2);
   }

   public List<String> getStringList() {
      return this.stringList;
   }

   public static void m_1(int[] intArray) {
      valueArray = intArray;
   }

   public static int[] m_9_IArr() {
      return valueArray;
   }
}
