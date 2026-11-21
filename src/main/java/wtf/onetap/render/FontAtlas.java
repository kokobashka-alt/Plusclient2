package wtf.onetap.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class FontAtlas {
   private final String text;
   private final Map<Integer, C7y> integerMap;
   private final Identifier identifier;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private int value;
   private int value2;
   private boolean flag;
   private static int value3;

   public FontAtlas(String text2, Identifier identifier2) {
      this.integerMap = new HashMap<>();
      this.flag = false;
      this.text = text2;
      this.identifier = identifier2;
   }

   public void m_4(Identifier identifier2) {
      try {
         ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
         this.m_2(resourceManager, identifier2);
         this.flag = true;
      } catch (Exception e) {
         System.err.println("[Onetap] Failed to load font '" + this.text + "': " + e.getMessage());
      }
   }

   private void m_2(ResourceManager resourceManager, Identifier identifier2) throws java.io.IOException {
      Fonts.m_1_String();
      Identifier identifier3 = Identifier.fromNamespaceAndPath(identifier2.getNamespace(), "fonts/" + identifier2.getPath() + ".json");

      try (InputStream inputStream = resourceManager.getResource(identifier3).orElseThrow().open()) {
         String text2 = new String(inputStream.readAllBytes());
         JsonObject jsonObject = JsonParser.parseString(text2).getAsJsonObject();
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("atlas");
         this.value = jsonObject2.get("width").getAsInt();
         this.value2 = jsonObject2.get("height").getAsInt();
         this.floatValue4 = jsonObject2.get("distanceRange").getAsFloat();
         boolean flag2 = "bottom".equals(jsonObject2.get("yOrigin").getAsString());
         JsonObject jsonObject3 = jsonObject.getAsJsonObject("metrics");
         this.floatValue5 = jsonObject3.get("emSize").getAsFloat();
         this.floatValue = jsonObject3.get("lineHeight").getAsFloat();
         this.floatValue2 = jsonObject3.get("ascender").getAsFloat();
         this.floatValue3 = jsonObject3.get("descender").getAsFloat();

         for (JsonElement jsonElement : jsonObject.getAsJsonArray("glyphs")) {
            JsonObject jsonObject4 = jsonElement.getAsJsonObject();
            int value4 = jsonObject4.get("unicode").getAsInt();
            float floatValue6 = jsonObject4.get("advance").getAsFloat();
            float floatValue7 = 0.0F;
            float floatValue8 = 0.0F;
            float floatValue9 = 0.0F;
            float floatValue10 = 0.0F;
            float floatValue11 = 0.0F;
            float floatValue12 = 0.0F;
            float floatValue13 = 0.0F;
            float floatValue14 = 0.0F;
            if (jsonObject4.has("planeBounds")) {
               JsonObject jsonObject5 = jsonObject4.getAsJsonObject("planeBounds");
               floatValue7 = jsonObject5.get("left").getAsFloat();
               floatValue8 = jsonObject5.get("bottom").getAsFloat();
               floatValue9 = jsonObject5.get("right").getAsFloat();
               floatValue10 = jsonObject5.get("top").getAsFloat();
            }

            int value5 = 0;
            int value6 = 0;
            if (jsonObject4.has("atlasBounds")) {
               JsonObject jsonObject6 = jsonObject4.getAsJsonObject("atlasBounds");
               float floatValue15 = jsonObject6.get("left").getAsFloat();
               float floatValue16 = jsonObject6.get("bottom").getAsFloat();
               float floatValue17 = jsonObject6.get("right").getAsFloat();
               float floatValue18 = jsonObject6.get("top").getAsFloat();
               floatValue11 = floatValue15 / this.value;
               floatValue13 = floatValue17 / this.value;
               if (flag2) {
                  floatValue12 = 1.0F - floatValue18 / this.value2;
                  floatValue14 = 1.0F - floatValue16 / this.value2;
               } else {
                  floatValue12 = floatValue18 / this.value2;
                  floatValue14 = floatValue16 / this.value2;
               }

               value5 = (int)(floatValue17 - floatValue15);
               value6 = (int)(floatValue18 - floatValue16);
            }

            this.integerMap.put(value4, new C7y(value4, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, floatValue13, floatValue14, value5, value6));
         }
      }
   }

   public C7y getGlyph(int value4) {
      return this.integerMap.getOrDefault(value4, this.integerMap.get(63));
   }

   public boolean hasGlyph(int value4) {
      return this.integerMap.containsKey(value4);
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public float getFloatValue4() {
      return this.floatValue4;
   }

   public float getFloatValue5() {
      return this.floatValue5;
   }

   public int getValue() {
      return this.value;
   }

   public int getValue2() {
      return this.value2;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String getText() {
      return this.text;
   }

   public float m_1(String text2, float floatValue6) {
      float floatValue7 = floatValue6 / this.floatValue5;
      float floatValue8 = 0.0F;

      for (int i = 0; i < text2.length(); i++) {
         C7y c7y = this.getGlyph(text2.charAt(i));
         if (c7y != null) {
            floatValue8 += c7y.m_0x() * floatValue7;
         }
      }

      return floatValue8;
   }

   public float m_1_float(float floatValue6) {
      return this.floatValue * floatValue6 / this.floatValue5;
   }

   public static FontAtlas.StateHolder m_0_C1() {
      return new FontAtlas.StateHolder();
   }

   public static void m_1_void(int value4) {
      value3 = value4;
   }

   public static int m_8_int() {
      return value3;
   }

   public static int m_6_int() {
      return 0;
   }

   static {
      m_1_void(79);
   }

   public static final class StateHolder {
      private String text;
      private String text2;
      private String text3;

      private StateHolder() {
         this.text = "?";
      }

      public FontAtlas.StateHolder withText(String text4) {
         this.text = text4;
         return this;
      }

      public FontAtlas.StateHolder withText2(String text4) {
         this.text2 = text4;
         return this;
      }

      public FontAtlas.StateHolder withText3(String text4) {
         this.text3 = text4;
         return this;
      }

      public FontAtlas build() {
         Identifier identifier2 = Identifier.fromNamespaceAndPath("onetap", "fonts/" + this.text3 + ".png");
         FontAtlas fontAtlas = new FontAtlas(this.text, identifier2);
         fontAtlas.m_4(Identifier.fromNamespaceAndPath("onetap", this.text2));
         return fontAtlas;
      }
   }
}
