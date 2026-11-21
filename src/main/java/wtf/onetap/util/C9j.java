package wtf.onetap.util;

public class C9j implements MinecraftAccess {
   private final double[] doubleValueArray;
   private int value;
   private boolean flag;

   public C9j() {
      this.doubleValueArray = new double[6];
      this.value = Integer.MIN_VALUE;
   }

   public boolean m_0_boolean(boolean flag2) {
      boolean flag3 = MinecraftAccess.MINECRAFT.player.isFallFlying() || MinecraftAccess.MINECRAFT.player.isInWater();
      int value2 = MinecraftAccess.MINECRAFT.player.tickCount;
      if (!flag3 && value2 == this.value) {
         return this.flag;
      }

      this.doubleValueArray[0] = flag2 ? 1.0 : 0.0;
      this.doubleValueArray[1] = MinecraftAccess.MINECRAFT.player.onGround() ? 1.0 : 0.0;
      this.doubleValueArray[2] = flag3 ? 1.0 : 0.0;
      this.doubleValueArray[3] = MinecraftAccess.MINECRAFT.player.getAttackStrengthScale(-0.5F);
      this.doubleValueArray[4] = !flag3 && !MinecraftAccess.MINECRAFT.player.onGround() && PlayerUtil.predictLocal(2).flag4 ? 1.0 : 0.0;
      this.doubleValueArray[5] = value2 == this.value + 1 ? 1.0 : 0.0;
      boolean flag4 = !C9_q.m_7(9, this.doubleValueArray) || this.doubleValueArray[0] != 0.0;
      if (flag3) {
         return flag4;
      }

      this.value = value2;
      this.flag = flag4;
      return this.flag;
   }
}
