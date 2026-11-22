package wtf.onetap.module.combat;

import java.util.Locale;

/**
 * Pure logic for AntiTotemExploit: item-name matching and the swap / sneak / swap-back sequence.
 * No Minecraft types, so it can be checked outside the game.
 */
public final class AntiTotemLogic {
   public static final int MAX_WAIT_TICKS = 3;

   private AntiTotemLogic() {
   }

   /** Lowercases, drops legacy section-sign color codes, maps the letter yo to ye and trims. */
   public static String normalize(String text) {
      if (text == null) {
         return "";
      }
      String lower = text.toLowerCase(Locale.ROOT);
      return lower.replaceAll("\u00A7[0-9a-fk-or]", "").replace('\u0451', '\u0435').trim();
   }

   /** An empty filter matches any name. Otherwise the normalized name must contain the normalized filter. */
   public static boolean nameMatches(String stackName, String filter) {
      String wanted = normalize(filter);
      return wanted.isEmpty() || normalize(stackName).contains(wanted);
   }

   /**
    * Sequence after the item is in the main hand:
    * the sneak key must go from released to pressed so the server sees a sneak toggle,
    * then the item is swapped back on a later tick.
    *
    * Phases: ARMED (item swapped in, waiting for the next input tick), RELEASED (sneak forced off this tick
    * because the player was already holding sneak), SNEAKING (sneak forced on, waiting to swap back).
    */
   public static final class Sequence {
      public enum Phase { IDLE, ARMED, RELEASED, SNEAKING }

      private Phase phase = Phase.IDLE;
      private int waitedTicks;

      public Phase phase() {
         return phase;
      }

      public boolean active() {
         return phase != Phase.IDLE;
      }

      public void start() {
         phase = Phase.ARMED;
         waitedTicks = 0;
      }

      public void reset() {
         phase = Phase.IDLE;
         waitedTicks = 0;
      }

      /**
       * Called from the movement input hook once per tick.
       *
       * @param physicallySneaking whether the player is holding sneak right now
       * @return TRUE or FALSE to force the sneak state this tick, or null to leave the input unchanged
       */
      public Boolean onMovementInput(boolean physicallySneaking) {
         switch (phase) {
            case ARMED:
               if (physicallySneaking) {
                  phase = Phase.RELEASED;
                  return Boolean.FALSE;
               }
               phase = Phase.SNEAKING;
               waitedTicks = 0;
               return Boolean.TRUE;
            case RELEASED:
               phase = Phase.SNEAKING;
               waitedTicks = 0;
               return Boolean.TRUE;
            default:
               return null;
         }
      }

      /**
       * Called from the tick hook once per tick, before that tick's input is processed.
       *
       * @return true when the caller must swap the item back now; the sequence is finished afterwards
       */
      public boolean onTick() {
         switch (phase) {
            case SNEAKING:
               reset();
               return true;
            case ARMED:
            case RELEASED:
               waitedTicks++;
               if (waitedTicks > MAX_WAIT_TICKS) {
                  reset();
                  return true;
               }
               return false;
            default:
               return false;
         }
      }
   }
}
