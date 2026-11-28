package wtf.onetap.util;

/**
 * Donate and role badges that servers put into player names as Myanmar letters. None of these glyphs exist in the
 * client's font atlases, so each one is shown as a coloured text label instead of the raw character.
 */
public final class RoleBadges {
   public record Role(char glyph, String label, int rgb) {
   }

   private static final Role[] ROLES = {
      new Role('\u101E', "DEVELOPER", 0xFFCC00),
      new Role('\u1039', "SPECTATOR", 0xA0A0A0),
      new Role('\u1027', "CURATOR", 0x00FFF2),
      new Role('\u1032', "MODER", 0x0099FF),
      new Role('\u102F', "ML.ADMIN", 0xAB73FF),
      new Role('\u1031', "HELPER", 0x33FF33),
      new Role('\u1021', "YOUTUBE", 0xFF5900),
      new Role('\u1029', "MEDIA", 0xFF00FF),
      new Role('\u1036', "TIKTOK+", 0x00FF8C),
      new Role('\u1035', "TIKTOK", 0xFFFFFF),
      new Role('\u1023', "HALLOWEEN", 0xFF7396),
      new Role('\u1025', "SUMMER", 0xFFDC73),
      new Role('\u1038', "SAKURA", 0xFF73F1),
      new Role('\u102D', "WINTER", 0x73FFE3),
      new Role('\u1034', "CUSTOM", 0xBF9126),
      new Role('\u1022', "PHANTOM", 0x6DA9F2),
      new Role('\u1037', "KRATOS", 0x00BF00),
      new Role('\u102B', "GUARDIAN", 0x26BFB8),
      new Role('\u102C', "OVERLORD", 0x73BF00),
      new Role('\u1026', "PHOENIX", 0xADFF33),
      new Role('\u1024', "PRINCE", 0x56BF61),
      new Role('\u102A', "TITAN", 0x73FF8F),
      new Role('\u1030', "HERO", 0x00BF56)
   };

   private RoleBadges() {
   }

   /** Role for a glyph, or null when the character is not a role badge. */
   public static Role of(char glyph) {
      for (Role role : ROLES) {
         if (role.glyph() == glyph) {
            return role;
         }
      }

      return null;
   }

   /** All roles, in the order they were defined. */
   public static Role[] all() {
      return ROLES.clone();
   }
}
