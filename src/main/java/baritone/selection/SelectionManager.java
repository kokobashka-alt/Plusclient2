package baritone.selection;

import baritone.Baritone;
import baritone.api.selection.ISelection;
import baritone.api.selection.ISelectionManager;
import baritone.api.utils.BetterBlockPos;
import java.util.LinkedList;
import java.util.ListIterator;
import net.minecraft.core.Direction;

public class SelectionManager implements ISelectionManager {
   private final LinkedList<ISelection> selections = new LinkedList<>();
   private ISelection[] selectionsArr = new ISelection[0];

   public SelectionManager(Baritone var1) {
      new SelectionRenderer(var1, this);
   }

   private void resetSelectionsArr() {
      this.selectionsArr = this.selections.toArray(new ISelection[0]);
   }

   @Override
   public synchronized ISelection addSelection(ISelection var1) {
      this.selections.add(var1);
      this.resetSelectionsArr();
      return var1;
   }

   @Override
   public ISelection addSelection(BetterBlockPos var1, BetterBlockPos var2) {
      return this.addSelection(new Selection(var1, var2));
   }

   @Override
   public synchronized ISelection removeSelection(ISelection var1) {
      this.selections.remove(var1);
      this.resetSelectionsArr();
      return var1;
   }

   @Override
   public synchronized ISelection[] removeAllSelections() {
      ISelection[] var1 = this.getSelections();
      this.selections.clear();
      this.resetSelectionsArr();
      return var1;
   }

   @Override
   public ISelection[] getSelections() {
      return this.selectionsArr;
   }

   @Override
   public synchronized ISelection getOnlySelection() {
      return this.selections.size() == 1 ? this.selections.peekFirst() : null;
   }

   @Override
   public ISelection getLastSelection() {
      return this.selections.peekLast();
   }

   @Override
   public synchronized ISelection expand(ISelection var1, Direction var2, int var3) {
      var var4 = this.selections.listIterator();

      while (var4.hasNext()) {
         ISelection var5 = (ISelection)var4.next();
         if (var5 == var1) {
            var4.remove();
            var4.add(var5.expand(var2, var3));
            this.resetSelectionsArr();
            return (ISelection)var4.previous();
         }
      }

      return null;
   }

   @Override
   public synchronized ISelection contract(ISelection var1, Direction var2, int var3) {
      var var4 = this.selections.listIterator();

      while (var4.hasNext()) {
         ISelection var5 = (ISelection)var4.next();
         if (var5 == var1) {
            var4.remove();
            var4.add(var5.contract(var2, var3));
            this.resetSelectionsArr();
            return (ISelection)var4.previous();
         }
      }

      return null;
   }

   @Override
   public synchronized ISelection shift(ISelection var1, Direction var2, int var3) {
      var var4 = this.selections.listIterator();

      while (var4.hasNext()) {
         ISelection var5 = (ISelection)var4.next();
         if (var5 == var1) {
            var4.remove();
            var4.add(var5.shift(var2, var3));
            this.resetSelectionsArr();
            return (ISelection)var4.previous();
         }
      }

      return null;
   }
}
