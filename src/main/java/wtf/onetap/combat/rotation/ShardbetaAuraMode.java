package wtf.onetap.combat.rotation;

public enum ShardbetaAuraMode {
    VANILLA(new Params(1.0, true, true, false, false, false, false, 40, 90, true, false, false)),
    SC3(new Params(0.55, true, true, true, true, false, true, 90, 180, true, true, true)),
    SHARD(new Params(0.7, true, true, true, false, true, true, 60, 120, true, false, true)),
    SHARD_AI(new Params(0.6, true, true, true, false, true, true, 70, 140, true, true, true)),
    GRIM(new Params(0.85, true, true, false, false, false, true, 40, 90, true, false, true)),
    MATRIX(new Params(0.75, true, true, true, false, false, true, 50, 110, false, false, true)),
    VULCAN(new Params(0.8, true, true, true, false, false, true, 45, 100, false, false, true)),
    WHITERISE(new Params(0.9, true, true, false, false, true, true, 30, 70, true, false, false)),
    ARESMINE(new Params(0.65, true, true, true, true, true, true, 80, 160, true, true, true)),
    ASSIST(new Params(0.95, true, false, false, false, false, false, 20, 50, false, false, false)),
    NCP(new Params(0.78, true, true, false, false, false, true, 55, 115, true, false, true)),
    AAC(new Params(0.72, true, true, false, false, false, true, 65, 125, true, false, true)),
    SPARTAN(new Params(0.68, true, true, true, false, false, true, 70, 140, true, false, true)),
    INTAVE(new Params(0.64, true, true, false, false, false, true, 75, 145, true, false, true)),
    VERUS(new Params(0.75, true, true, true, false, false, true, 60, 125, true, false, true)),
    KARHU(new Params(0.7, true, true, false, false, false, true, 70, 130, true, false, true)),
    WATCHDOG(new Params(0.65, true, true, false, false, false, true, 80, 160, true, false, true)),
    POLAR(new Params(0.67, true, true, false, false, false, true, 75, 150, true, false, true)),
    NEGATIVITY(new Params(0.8, true, true, false, false, false, true, 50, 105, true, false, false)),
    THEMIS(new Params(0.73, true, true, false, false, false, true, 65, 125, true, false, true)),
    LIGHT(new Params(0.88, true, true, false, false, false, false, 35, 85, true, false, false)),
    CUSTOM(new Params(1.0, true, true, false, false, false, true, 40, 90, true, false, false));

    public record Params(
            double aggression,
            boolean silent,
            boolean moveFix,
            boolean sprintReset,
            boolean wtap,
            boolean packetSprint,
            boolean tpsSync,
            int reactionMinMs,
            int reactionMaxMs,
            boolean crit,
            boolean shieldBreak,
            boolean bezier
    ) {}

    public final Params p;

    ShardbetaAuraMode(Params p) {
        this.p = p;
    }
}
