package wtf.onetap.pve;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import wtf.onetap.Onetap;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.Rotations;

public class PathStep implements MinecraftAccess {
   private final CombatPlanner combatPlanner;
   private final List<float[]> floatList;
   private String text;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private double doubleValue5;
   @Nullable
   private Entity entity;
   @Nullable
   private Vec3 vec3;
   private boolean flag;
   private int value;
   private double doubleValue6;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private int value2;
   @Nullable
   private JsonObject jsonObject;
   private float floatValue5;
   private float floatValue6;
   private int value3;
   private long longValue;
   private int value4;
   private int value5;
   private int value6;

   public PathStep(CombatPlanner combatPlanner2) {
      this.floatList = new ArrayList<>();
      this.text = "t";
      this.doubleValue2 = 1.0;
      this.value = -1;
      this.doubleValue6 = Double.NaN;
      this.value2 = Integer.MIN_VALUE;
      this.value3 = Integer.MIN_VALUE;
      this.longValue = Long.MIN_VALUE;
      this.combatPlanner = combatPlanner2;
   }

   public void m_4(@Nullable Entity entity2, @Nullable Vec3 vec32) {
      this.entity = entity2;
      this.vec3 = vec32;
      this.flag = false;
   }

   public void reset() {
      this.entity = null;
      this.vec3 = null;
      this.flag = true;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public void setVec3(Vec3 vec32) {
      this.vec3 = vec32;
   }

   public void m_8_void() {
      this.floatList.clear();
      this.value = -1;
      this.doubleValue6 = Double.NaN;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.value2 = Integer.MIN_VALUE;
      this.jsonObject = null;
   }

   public void m_0_void(JsonObject jsonObject2) {
      String text2 = jsonObject2.get("u").getAsString();
      CombatPlanner.m_4_String();
      double doubleValue7 = jsonObject2.get("from").getAsDouble();
      double doubleValue8 = jsonObject2.get("step").getAsDouble();
      boolean flag2 = jsonObject2.has("reset") && jsonObject2.get("reset").getAsBoolean();
      boolean flag3 = jsonObject2.has("base") && jsonObject2.get("base").getAsBoolean();
      this.value4++;
      if (flag2) {
         this.value5++;
      }

      if (!flag2 && text2.equals(this.text) && !this.floatList.isEmpty() && !(Math.abs(doubleValue8 - this.doubleValue2) > 1.0E-9)) {
         int value7 = (int)Math.round((doubleValue7 - this.doubleValue) / this.doubleValue2);
         if (value7 >= 0 && value7 <= this.floatList.size()) {
            if (value7 < this.floatList.size()) {
               this.floatList.subList(value7, this.floatList.size()).clear();
            }

            if (flag3 && value7 > this.value) {
               this.floatValue = 0.0F;
               this.floatValue2 = 0.0F;
            }
         } else {
            this.m_8_void();
            this.doubleValue = doubleValue7;
         }
      } else {
         this.m_8_void();
         this.text = text2;
         this.doubleValue = doubleValue7;
         this.doubleValue2 = doubleValue8;
      }

      JsonArray jsonArray = jsonObject2.getAsJsonArray("cam");
      int value8 = jsonObject2.has("tid") ? jsonObject2.get("tid").getAsInt() : Integer.MIN_VALUE;
      this.m_0(jsonObject2.getAsJsonArray("s"), jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), value8);
      this.jsonObject = jsonObject2.has("hit") && jsonObject2.get("hit").isJsonObject() ? jsonObject2.getAsJsonObject("hit") : null;
      this.floatValue5 = jsonArray.get(0).getAsFloat();
      this.floatValue6 = jsonArray.get(1).getAsFloat();
      this.value3 = value8;
      JsonArray jsonArray2 = jsonObject2.getAsJsonArray("ao");
      this.doubleValue3 = jsonArray2.get(0).getAsDouble();
      this.doubleValue4 = jsonArray2.get(1).getAsDouble();
      this.doubleValue5 = jsonArray2.get(2).getAsDouble();
      this.m_2_void();
   }

   private void m_0(JsonArray jsonArray, float floatValue7, float floatValue8, int value7) {
      for (JsonElement jsonElement : jsonArray) {
         JsonArray jsonArray2 = jsonElement.getAsJsonArray();
         float[] floatArray = new float[11];
         floatArray[10] = Float.intBitsToFloat(value7);

         for (int i = 0; i < 8 && i < jsonArray2.size(); i++) {
            floatArray[i] = jsonArray2.get(i).getAsFloat();
         }

         floatArray[8] = floatValue7;
         floatArray[9] = floatValue8;
         this.floatList.add(floatArray);
      }
   }

   public void m_5_void() {
      JsonObject jsonObject2 = this.jsonObject;
      this.jsonObject = null;
      if (jsonObject2 != null && jsonObject2.get("t").getAsLong() == this.combatPlanner.getLongValue2()) {
         int value7 = (int)Math.round((jsonObject2.get("from").getAsDouble() - this.doubleValue) / this.doubleValue2);
         if (value7 > this.value && value7 <= this.floatList.size()) {
            this.floatList.subList(value7, this.floatList.size()).clear();
            this.m_0(jsonObject2.getAsJsonArray("s"), this.floatValue5, this.floatValue6, this.value3);
            this.longValue = this.combatPlanner.getLongValue2();
            this.value6++;
         }
      }
   }

   public void m_8(JsonObject jsonObject2, boolean flag2) {
      Entity entity2 = this.entity;
      if (entity2 != null) {
         JsonArray jsonArray = new JsonArray();
         jsonArray.add(entity2.getX());
         jsonArray.add(entity2.getY());
         jsonArray.add(entity2.getZ());
         jsonArray.add(entity2.xo);
         jsonArray.add(entity2.yo);
         jsonArray.add(entity2.zo);
         jsonObject2.add("nt", jsonArray);
         if (flag2) {
            jsonObject2.addProperty("nta", true);
         }
      }
   }

   public void m_7_void(JsonObject jsonObject2) {
      if (this.longValue == this.combatPlanner.getLongValue2()) {
         jsonObject2.addProperty("hb", true);
      }

      if (!Double.isNaN(this.doubleValue6)) {
         jsonObject2.addProperty("at", this.doubleValue6);
         JsonArray jsonArray = new JsonArray();
         jsonArray.add(this.floatValue + this.floatValue3);
         jsonArray.add(this.floatValue2 + this.floatValue4);
         jsonObject2.add("c", jsonArray);
      }
   }

   public boolean m_0_boolean() {
      if (this.entity != null && !Double.isNaN(this.doubleValue6)) {
         if (this.value2 != Integer.MIN_VALUE && this.value2 != this.entity.getId()) {
            return false;
         }

         double doubleValue7 = this.m_7_boolean() ? this.combatPlanner.elapsedMs() - this.doubleValue6 : (this.combatPlanner.getLongValue2() - this.doubleValue6) * 50.0;
         return doubleValue7 <= 100.0;
      } else {
         return false;
      }
   }

   public String m_8_String() {
      String text2 = this.value4 + " plan(s), " + this.value5 + " started over, " + this.value6 + " hit branch(es)";
      this.value4 = 0;
      this.value5 = 0;
      this.value6 = 0;
      return text2;
   }

   public boolean m_7_boolean() {
      return "f".equals(this.text);
   }

   @Nullable
   public Rotations m_2_C3b() {
      return !this.floatList.isEmpty() && !this.m_7_boolean() ? this.m_1_C3b((int)Math.round(this.combatPlanner.getLongValue2() - this.doubleValue)) : null;
   }

   @Nullable
   public Rotations m_0_C3b() {
      if (!this.floatList.isEmpty() && !this.m_7_boolean()) {
         int value7 = (int)Math.round(this.combatPlanner.getLongValue2() - this.doubleValue);
         if (value7 >= 0 && value7 < this.floatList.size()) {
            return Float.floatToRawIntBits(this.floatList.get(value7)[10]) != -1 ? null : this.m_1_C3b(value7);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Nullable
   public Rotations m_4_C3b() {
      if (this.m_7_boolean() && !this.floatList.isEmpty()) {
         double doubleValue7 = this.combatPlanner.elapsedMs();
         int value7 = (int)Math.floor((doubleValue7 - this.doubleValue) / this.doubleValue2 + 0.5);
         if (this.doubleValue + value7 * this.doubleValue2 >= this.combatPlanner.m_4_double()) {
            value7 = this.getIndex(doubleValue7);
         }

         return value7 <= this.value ? null : this.m_1_C3b(value7);
      } else {
         return null;
      }
   }

   private int getIndex(double doubleValue7) {
      return (int)Math.floor((doubleValue7 - this.doubleValue) / this.doubleValue2);
   }

   @Nullable
   private Rotations m_1_C3b(int value7) {
      if (value7 < 0) {
         return null;
      }

      if (value7 >= this.floatList.size()) {
         value7 = this.floatList.size() - 1;
      }

      if (value7 < this.value) {
         return null;
      }

      Rotations rotations = this.m_5_C3b();
      int value8 = this.entity != null ? this.entity.getId() : (this.flag ? -1 : Integer.MIN_VALUE);
      int value9 = Math.max(this.value + 1, value7 - 64);

      for (int i = value9; i <= value7; i++) {
         float[] floatArray = this.floatList.get(i);
         int value10 = Float.floatToRawIntBits(floatArray[10]);
         if (value10 == Integer.MIN_VALUE || value10 == value8) {
            if (this.value2 != value8) {
               this.floatValue = 0.0F;
               this.floatValue2 = 0.0F;
            }

            this.value2 = value8;
            this.m_2(floatArray, rotations);
         }
      }

      this.value = value7;
      this.doubleValue6 = this.doubleValue + value7 * this.doubleValue2;
      float[] floatArray2 = this.floatList.get(value7);
      this.floatValue3 = floatArray2[6] * Mth.wrapDegrees(Onetap.ROTATION_MODES.getRenderYaw() - floatArray2[8]);
      this.floatValue4 = floatArray2[7] * (Onetap.ROTATION_MODES.getRenderPitch() - floatArray2[9]);
      return new Rotations(floatArray2[0] + this.floatValue + this.floatValue3, floatArray2[1] + this.floatValue2 + this.floatValue4);
   }

   private void m_2(float[] floatArray, @Nullable Rotations rotations) {
      if (rotations != null) {
         float floatValue7 = Mth.wrapDegrees(rotations.getFloatValue() - floatArray[2]);
         float floatValue8 = rotations.getFloatValue2() - floatArray[3];
         this.floatValue = this.floatValue + floatArray[4] * (floatValue7 - this.floatValue);
         this.floatValue2 = this.floatValue2 + floatArray[5] * (floatValue8 - this.floatValue2);
         this.floatValue = Mth.clamp(this.floatValue, -25.0F, 25.0F);
         this.floatValue2 = Mth.clamp(this.floatValue2, -25.0F, 25.0F);
      }
   }

   @Nullable
   private Rotations m_5_C3b() {
      if (MinecraftAccess.MINECRAFT.player == null) {
         return null;
      }

      if (this.flag) {
         return new Rotations(Onetap.ROTATION_MODES.getRenderYaw(), Onetap.ROTATION_MODES.getRenderPitch());
      }

      Vec3 vec32 = this.vec3;
      if (vec32 == null) {
         if (this.entity == null || !this.entity.isAlive()) {
            return null;
         }

         vec32 = this.entity.position().add(this.doubleValue3, this.doubleValue4, this.doubleValue5);
      }

      return new Rotations(MinecraftAccess.MINECRAFT.player.getEyePosition(), vec32);
   }

   private void m_2_void() {
      int value7 = Math.max(0, this.value - 8);
      if (value7 >= 32) {
         this.floatList.subList(0, value7).clear();
         this.doubleValue = this.doubleValue + value7 * this.doubleValue2;
         this.value -= value7;
      }
   }
}
