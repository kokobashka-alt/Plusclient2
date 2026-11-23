package wtf.onetap.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import wtf.onetap.util.MinecraftAccess;

public final class ShardbetaAuraPipeline {
    private static final ShardbetaRotation rot = new ShardbetaRotation();
    private static ShardbetaAuraMode mode = ShardbetaAuraMode.SHARD;
    private static LivingEntity target;
    private static int reaction;
    private static int attackCd;
    private static int sprintCd;
    private static int shieldSlot = -1;
    private static boolean sprintSent;
    private static long tpsWin = System.nanoTime();
    private static int tpsTicks;
    private static float tps = 20.0f;

    private ShardbetaAuraPipeline() {}

    public static void setMode(ShardbetaAuraMode m) {
        if (m != null) {
            mode = m;
        }
    }

    public static ShardbetaAuraMode getMode() {
        return mode;
    }

    public static LivingEntity getTarget() {
        return target;
    }

    public static float getTps() {
        return tps;
    }

    public static void reset() {
        target = null;
        reaction = 0;
        attackCd = 0;
        rot.reset();
        ShardbetaRotationStorage.clear();
    }

    public static void tick() {
        long now = System.nanoTime();
        tpsTicks++;
        if (now - tpsWin >= 1_000_000_000L) {
            tps = tpsTicks * 1_000_000_000.0f / (now - tpsWin);
            tpsTicks = 0;
            tpsWin = now;
        }

        Minecraft mc = MinecraftAccess.MINECRAFT;
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player == null || level == null || gameMode == null) {
            ShardbetaRotationStorage.clear();
            return;
        }

        if (attackCd > 0) attackCd--;
        if (sprintCd > 0) sprintCd--;

        LivingEntity found = findTarget(player, level);
        if (found != target) {
            reaction = found == null ? 0 : msToTicks(mode.p().reactionMinMs(), mode.p().reactionMaxMs());
            target = found;
            rot.reset();
        }

        if (target == null || !target.isAlive()) {
            ShardbetaRotationStorage.clear();
            restore(mc, player);
            return;
        }

        if (reaction > 0) {
            reaction--;
            ShardbetaRotationStorage.clear();
            return;
        }

        rot.setAggression(mode.p().aggression());
        ShardbetaRotation.Rotation r = rot.update(target, player.getYRot(), player.getXRot(), 3.0);
        ShardbetaRotationStorage.begin(r.yaw(), r.pitch());
        if (ready(player)) {
            attack(mc, player, gameMode);
        }
        if (!mode.p().silent()) {
            ShardbetaRotationStorage.clear();
        }
    }

    public static void applyMoveFix() {
        LocalPlayer player = MinecraftAccess.MINECRAFT.player;
        if (player == null || !mode.p().moveFix() || !ShardbetaRotationStorage.active()) return;
        float d = ShardbetaRotationStorage.yaw() - player.getYRot();
        if (Math.abs(d) < 0.01f) return;
        double rad = Math.toRadians(d);
        double co = Math.cos(rad);
        double si = Math.sin(rad);
        float f = player.input.forwardImpulse;
        float side = player.input.leftImpulse;
        player.input.forwardImpulse = (float) (f * co - side * si);
        player.input.leftImpulse = (float) (side * co + f * si);
    }

    private static LivingEntity findTarget(LocalPlayer player, ClientLevel level) {
        LivingEntity best = null;
        double bd = 9.0;
        for (Entity e : level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity le) || le == player || !le.isAlive()) continue;
            double d = player.distanceToSqr(le);
            if (d < bd && valid(player, le)) {
                bd = d;
                best = le;
            }
        }
        return best;
    }

    private static boolean valid(LocalPlayer self, LivingEntity e) {
        if (!(e instanceof Player p)) return true;
        if (p.isCreative() || p.isSpectator()) return false;
        if (p.getName().getString().isEmpty()) return false;
        if (self.isAlliedTo(p)) return false;
        return true;
    }

    private static boolean ready(LocalPlayer player) {
        if (target == null || attackCd > 0) return false;
        if (player.distanceToSqr(target) > 9.0) return false;
        if (player.getAttackStrengthScale(0.5f) < 0.92f) return false;
        if (mode.p().crit() && !canCrit(player)) return false;
        if (mode.p().tpsSync() && tps < 19.0f) {
            int every = Math.max(1, Math.round(20.0f / tps));
            if (player.tickCount % every != 0) return false;
        }
        return true;
    }

    private static boolean canCrit(LocalPlayer p) {
        return p.fallDistance > 0.0f
                && !p.onGround()
                && p.getDeltaMovement().y < 0.0
                && !p.isInWater()
                && !p.isInLava()
                && !p.onClimbable()
                && !p.isPassenger()
                && !p.isSprinting();
    }

    private static void attack(Minecraft mc, LocalPlayer player, MultiPlayerGameMode gameMode) {
        if (mode.p().shieldBreak()) {
            shieldBreak(player);
        }
        if (mode.p().sprintReset() && player.isSprinting()) {
            player.setSprinting(false);
            if (mode.p().packetSprint() && player.connection != null) {
                player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
                sprintSent = true;
            }
            sprintCd = 2;
        }
        if (mode.p().wtap()) {
            mc.options.keyUp.setDown(false);
        }
        ShardbetaRotationStorage.sendLook();
        ShardbetaRotationStorage.applyForHit();
        gameMode.attack(player, target);
        player.swing(InteractionHand.MAIN_HAND);
        ShardbetaRotationStorage.restoreRender();
        ShardbetaRotationStorage.sendReal();
        attackCd = Math.max(1, Math.round(20.0f / Math.max(1.0f, tps)));
    }

    private static void shieldBreak(LocalPlayer player) {
        if (!(target instanceof Player p) || !p.isBlocking()) return;
        var inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).is(ItemTags.AXES)) {
                if (shieldSlot < 0) {
                    shieldSlot = inv.getSelectedSlot();
                }
                inv.setSelectedSlot(i);
                return;
            }
        }
    }

    private static void restore(Minecraft mc, LocalPlayer player) {
        if (sprintSent && sprintCd <= 0 && player.connection != null) {
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_SPRINTING));
            sprintSent = false;
        }
        if (shieldSlot >= 0) {
            player.getInventory().setSelectedSlot(shieldSlot);
            shieldSlot = -1;
        }
        if (mode.p().wtap() && mc.options != null) {
            mc.options.keyUp.setDown(true);
        }
    }

    private static int msToTicks(int min, int max) {
        if (max <= 0) return 0;
        int ms = min + (int) (Math.random() * Math.max(1, max - min));
        return Math.max(0, Math.round(ms / 50.0f));
    }
}
