package wtf.onetap.render;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

public class Fonts {
   public static final Supplier<FontAtlas> SUPPLIER = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("montserrat").withText3("montserrat").withText2("montserrat").build());
   public static final Supplier<FontAtlas> SUPPLIER2 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("montserrat_semibold").withText3("montserrat_semibold").withText2("montserrat_semibold").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER3 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("montserrat_bold").withText3("montserrat_bold").withText2("montserrat_bold").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER4 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("bold").withText3("bold").withText2("bold").build());
   public static final Supplier<FontAtlas> SUPPLIER5 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("icons2").withText3("icons2").withText2("icons2").build());
   public static final Supplier<FontAtlas> SUPPLIER6 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("plusclient").withText3("plusclient").withText2("plusclient").build());
   public static final Supplier<FontAtlas> SUPPLIER7 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("plusclient_new").withText3("plusclient_new").withText2("plusclient_new").build());
   public static final Supplier<FontAtlas> SUPPLIER8 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("icons").withText3("iconss").withText2("iconss").build());
   public static final Supplier<FontAtlas> SUPPLIER9 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("GREY_BOLD").withText3("grey_bold").withText2("grey_bold").build());
   public static final Supplier<FontAtlas> SUPPLIER10 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("GREY_SEMIBOLD").withText3("grey_semibold").withText2("grey_semibold").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER11 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("GREY_MEDIUM").withText3("grey_medium").withText2("grey_medium").build());
   public static final Supplier<FontAtlas> SUPPLIER12 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("GREY_REGULAR").withText3("grey_regular").withText2("grey_regular").build());
   public static final Supplier<FontAtlas> SUPPLIER13 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Inter Regular").withText3("inter_regular").withText2("inter_regular").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER14 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("Inter Medium").withText3("inter_medium").withText2("inter_medium").build());
   public static final Supplier<FontAtlas> SUPPLIER15 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Inter SemiBold").withText3("inter_semibold").withText2("inter_semibold").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER16 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("Inter Bold").withText3("inter_bold").withText2("inter_bold").build());
   public static final Supplier<FontAtlas> SUPPLIER17 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Fallback").withText3("fallback/medium").withText2("fallback/medium").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER18 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("Geist Bold").withText3("geist_bold").withText2("geist_bold").build());
   public static final Supplier<FontAtlas> SUPPLIER19 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Oxanium Regular").withText3("oxanium_regular").withText2("oxanium_regular").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER20 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Oxanium Medium").withText3("oxanium_medium").withText2("oxanium_medium").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER21 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Oxanium SemiBold").withText3("oxanium_semibold").withText2("oxanium_semibold").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER22 = Suppliers.memoize(() -> FontAtlas.m_0_C1().withText("Oxanium Bold").withText3("oxanium_bold").withText2("oxanium_bold").build());
   public static final Supplier<FontAtlas> SUPPLIER23 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Unbounded Regular").withText3("unbounded_regular").withText2("unbounded_regular").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER24 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Unbounded Medium").withText3("unbounded_medium").withText2("unbounded_medium").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER25 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Unbounded SemiBold").withText3("unbounded_semibold").withText2("unbounded_semibold").build()
   );
   public static final Supplier<FontAtlas> SUPPLIER26 = Suppliers.memoize(
      () -> FontAtlas.m_0_C1().withText("Unbounded Bold").withText3("unbounded_bold").withText2("unbounded_bold").build()
   );
   private static String text;

   private Fonts() {
   }

   static {
      m_4_void("wYNk7b");
   }

   public static void m_4_void(String text2) {
      text = text2;
   }

   public static String m_1_String() {
      return text;
   }
}
