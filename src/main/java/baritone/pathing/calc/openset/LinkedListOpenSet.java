package baritone.pathing.calc.openset;

import baritone.pathing.calc.PathNode;

public class LinkedListOpenSet implements IOpenSet {
   private LinkedListOpenSet.Node first = null;

   LinkedListOpenSet() {
   }

   @Override
   public boolean isEmpty() {
      return this.first == null;
   }

   @Override
   public void insert(PathNode var1) {
      LinkedListOpenSet.Node var2 = new LinkedListOpenSet.Node();
      var2.val = var1;
      var2.nextOpen = this.first;
      this.first = var2;
   }

   @Override
   public void update(PathNode var1) {
   }

   @Override
   public PathNode removeLowest() {
      if (this.first == null) {
         return null;
      }

      LinkedListOpenSet.Node var1 = this.first.nextOpen;
      if (var1 == null) {
         LinkedListOpenSet.Node var9 = this.first;
         this.first = null;
         return var9.val;
      }

      LinkedListOpenSet.Node var2 = this.first;
      double var3 = this.first.val.combinedCost;
      LinkedListOpenSet.Node var5 = this.first;
      LinkedListOpenSet.Node var6 = null;

      while (var1 != null) {
         double var7 = var1.val.combinedCost;
         if (var7 < var3) {
            var3 = var7;
            var5 = var1;
            var6 = var2;
         }

         var2 = var1;
         var1 = var1.nextOpen;
      }

      if (var6 == null) {
         this.first = this.first.nextOpen;
         var5.nextOpen = null;
         return var5.val;
      } else {
         var6.nextOpen = var5.nextOpen;
         var5.nextOpen = null;
         return var5.val;
      }
   }

   public static class Node {
      private LinkedListOpenSet.Node nextOpen;
      private PathNode val;
   }
}
