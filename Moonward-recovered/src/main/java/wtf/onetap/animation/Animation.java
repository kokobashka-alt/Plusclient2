package wtf.onetap.animation;

import wtf.onetap.module.Module;

public class Animation {
   private long durationMs;
   private float current;
   private Easing easing;
   private long startTime;
   private float from;
   private float target;
   private boolean finished;
   private static Module[] moduleArray;

   public Animation(long longValue, float floatValue, Easing easing2) {
      this.durationMs = longValue;
      this.easing = easing2;
      this.current = floatValue;
      this.from = floatValue;
      this.target = floatValue;
      this.finished = true;
   }

   public Animation(Easing easing2, long longValue) {
      this(longValue, 0.0F, easing2);
   }

   public void animate(boolean flag) {
      this.update(flag ? 1.0F : 0.0F);
   }

   public float update(float floatValue) {
      m_8_C3_sArr();
      long longValue = System.nanoTime();
      if (floatValue != this.target) {
         this.from = this.current;
         this.target = floatValue;
         this.startTime = longValue;
         this.finished = false;
      }

      long longValue2 = longValue - this.startTime;
      long longValue3 = this.durationMs * 1000000L;
      if (longValue2 >= longValue3) {
         this.current = this.target;
         this.finished = true;
         return this.current;
      } else {
         float floatValue2 = (float)longValue2 / (float)longValue3;
         float floatValue3 = this.easing.ease(floatValue2, 0.0F, 1.0F, 1.0F);
         this.current = this.from + (this.target - this.from) * floatValue3;
         return this.current;
      }
   }

   public void setDuration(float floatValue) {
      this.current = floatValue;
      this.from = floatValue;
      this.target = floatValue;
      this.finished = true;
   }

   public void reset(float floatValue) {
      this.current = floatValue;
      this.from = floatValue;
      this.target = floatValue;
      this.finished = true;
   }

   public void reset() {
      this.reset(0.0F);
   }

   public void setTarget(float floatValue) {
      if (floatValue != this.target) {
         this.from = this.current;
         this.target = floatValue;
         this.startTime = System.nanoTime();
         this.finished = false;
      }
   }

   public float get() {
      return this.update(this.target);
   }

   public float updateWithThreshold(float floatValue, float floatValue2) {
      if (Math.abs(floatValue - this.target) > floatValue2) {
         return this.update(floatValue);
      }

      if (!this.finished) {
         return this.get();
      }

      this.setDuration(floatValue);
      return this.current;
   }

   public float updateOrSnap(float floatValue, boolean flag) {
      if (flag) {
         if (!this.finished) {
            return this.get();
         }

         this.setDuration(floatValue);
         return this.current;
      } else {
         return this.update(floatValue);
      }
   }

   public long getDuration() {
      return this.durationMs;
   }

   public void setDuration(long longValue) {
      this.durationMs = longValue;
   }

   public float getValue() {
      return this.current;
   }

   public Easing getEasing() {
      return this.easing;
   }

   public void setEasing(Easing easing2) {
      this.easing = easing2;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public float update() {
      return this.from;
   }

   public float updateOrSnap() {
      return this.target;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public static void m_6(Module[] moduleArray2) {
      moduleArray = moduleArray2;
   }

   public static Module[] m_8_C3_sArr() {
      return moduleArray;
   }
}
