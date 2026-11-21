package baritone.pathing.calc.openset;

import baritone.pathing.calc.PathNode;
import java.util.Arrays;

public class BinaryHeapOpenSet implements IOpenSet {
   private PathNode[] array;
   private int size = 0;

   public BinaryHeapOpenSet() {
      this(1024);
   }

   public BinaryHeapOpenSet(int var1) {
      this.array = new PathNode[var1];
   }

   public int size() {
      return this.size;
   }

   @Override
   public final void insert(PathNode var1) {
      if (this.size >= this.array.length - 1) {
         this.array = Arrays.copyOf(this.array, this.array.length << 1);
      }

      this.size++;
      var1.heapPosition = this.size;
      this.array[this.size] = var1;
      this.update(var1);
   }

   @Override
   public final void update(PathNode var1) {
      int var2 = var1.heapPosition;
      int var3 = var2 >>> 1;
      double var4 = var1.combinedCost;

      for (PathNode var6 = this.array[var3]; var2 > 1 && var6.combinedCost > var4; var6 = this.array[var3]) {
         this.array[var2] = var6;
         this.array[var3] = var1;
         var1.heapPosition = var3;
         var6.heapPosition = var2;
         var2 = var3;
         var3 = var2 >>> 1;
      }
   }

   @Override
   public final boolean isEmpty() {
      return this.size == 0;
   }

   @Override
   public final PathNode removeLowest() {
      if (this.size == 0) {
         throw new IllegalStateException("Cannot remove from empty heap");
      }

      PathNode var1 = this.array[1];
      PathNode var2 = this.array[this.size];
      this.array[1] = var2;
      var2.heapPosition = 1;
      this.array[this.size] = null;
      this.size--;
      var1.heapPosition = -1;
      if (this.size < 2) {
         return var1;
      }

      int var3 = 1;
      int var4 = 2;
      double var5 = var2.combinedCost;

      do {
         PathNode var7 = this.array[var4];
         double var8 = var7.combinedCost;
         if (var4 < this.size) {
            PathNode var10 = this.array[var4 + 1];
            double var11 = var10.combinedCost;
            if (var8 > var11) {
               var4++;
               var8 = var11;
               var7 = var10;
            }
         }

         if (var5 <= var8) {
            break;
         }

         this.array[var3] = var7;
         this.array[var4] = var2;
         var2.heapPosition = var4;
         var7.heapPosition = var3;
         var3 = var4;
      } while ((var4 <<= 1) <= this.size);

      return var1;
   }
}
