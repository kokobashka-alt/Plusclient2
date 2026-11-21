package baritone.behavior;

import baritone.Baritone;
import baritone.api.Settings;
import baritone.api.behavior.ILookBehavior;
import baritone.api.behavior.look.IAimProcessor;
import baritone.api.behavior.look.ITickableAimProcessor;
import baritone.api.event.events.PacketEvent;
import baritone.api.event.events.PlayerUpdateEvent;
import baritone.api.event.events.RotationMoveEvent;
import baritone.api.event.events.TickEvent;
import baritone.api.event.events.WorldEvent;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.Rotation;
import baritone.behavior.look.ForkableRandom;
import java.util.Optional;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import wtf.onetap.Onetap;
import wtf.onetap.util.Rotations;

public class LookBehavior extends Behavior implements ILookBehavior {
   private LookBehavior.Target target;
   private Rotation serverRotation;
   private Rotation appliedRotation;
   private int appliedTick = Integer.MIN_VALUE;
   private final LookBehavior.AimProcessor processor;

   public LookBehavior(Baritone var1) {
      super(var1);
      this.processor = new LookBehavior.AimProcessor(var1.getPlayerContext());
   }

   @Override
   public void updateTarget(Rotation var1, boolean var2) {
      this.target = new LookBehavior.Target(var1, LookBehavior.Target.Mode.resolve(this.ctx, var2));
      this.clearApplied();
   }

   @Override
   public IAimProcessor getAimProcessor() {
      return this.processor;
   }

   @Override
   public void onTick(TickEvent var1) {
      if (var1.getType() == TickEvent.Type.IN) {
         this.processor.tick();
      }
   }

   @Override
   public void onPlayerUpdate(PlayerUpdateEvent var1) {
      if (this.target != null) {
         switch (var1.getState()) {
            case PRE:
               if (this.target.mode != LookBehavior.Target.Mode.NONE) {
                  this.resolveAndApply(true);
               }
               break;
            case POST:
               this.target = null;
               this.clearApplied();
         }
      }
   }

   @Override
   public void onSendPacket(PacketEvent var1) {
      if (var1.getPacket() instanceof ServerboundMovePlayerPacket var2) {
         if (var2 instanceof Rot || var2 instanceof PosRot) {
            this.serverRotation = new Rotation(var2.getYRot(0.0F), var2.getXRot(0.0F));
         }
      }
   }

   @Override
   public void onWorldEvent(WorldEvent var1) {
      this.serverRotation = null;
      this.target = null;
      this.clearApplied();
   }

   public void pig() {
      Rotation var1 = this.resolveAndApply(true);
      if (var1 != null) {
         this.ctx.player().setYRot(var1.getYaw());
      }
   }

   public Optional<Rotation> getEffectiveRotation() {
      if (Onetap.ROTATION_MODES.isFlag()) {
         Rotations var1 = Onetap.ROTATION_MODES.toRotations();
         return Optional.of(new Rotation(var1.getFloatValue(), var1.getFloatValue2()));
      } else {
         return Baritone.settings().freeLook.value ? Optional.ofNullable(this.serverRotation) : Optional.empty();
      }
   }

   @Override
   public void onPlayerRotationMove(RotationMoveEvent var1) {
      Rotation var2 = this.resolveAndApply(false);
      if (var2 != null) {
         var1.setYaw(var2.getYaw());
         var1.setPitch(var2.getPitch());
      }
   }

   private void clearApplied() {
      this.appliedRotation = null;
      this.appliedTick = Integer.MIN_VALUE;
   }

   private Rotation resolveAndApply(boolean var1) {
      if (this.ctx.player() != null && this.target != null && this.target.mode != LookBehavior.Target.Mode.NONE) {
         int var2 = this.ctx.player().tickCount;
         if (this.appliedRotation != null && this.appliedTick == var2) {
            if (!var1) {
               return this.appliedRotation;
            }
         } else {
            this.appliedRotation = this.processor.peekRotation(this.target.rotation);
            this.appliedTick = var2;
         }

         Onetap.ROTATION_MODES.rotate(new Rotations(this.appliedRotation.getYaw(), this.appliedRotation.getPitch()), this.target.mode == LookBehavior.Target.Mode.CLIENT, 5);
         return this.appliedRotation;
      } else {
         return null;
      }
   }

   private abstract static class AbstractAimProcessor implements ITickableAimProcessor {
      protected final IPlayerContext ctx;
      private final ForkableRandom rand;
      private double randomYawOffset;
      private double randomPitchOffset;

      public AbstractAimProcessor(IPlayerContext var1) {
         this.ctx = var1;
         this.rand = new ForkableRandom();
      }

      private AbstractAimProcessor(LookBehavior.AbstractAimProcessor var1) {
         this.ctx = var1.ctx;
         this.rand = var1.rand.fork();
         this.randomYawOffset = var1.randomYawOffset;
         this.randomPitchOffset = var1.randomPitchOffset;
      }

      @Override
      public final Rotation peekRotation(Rotation var1) {
         Rotation var2 = this.getPrevRotation();
         float var3 = var1.getYaw();
         float var4 = var1.getPitch();
         if (var4 == var2.getPitch()) {
            var4 = this.nudgeToLevel(var4);
         }

         var3 = (float)(var3 + this.randomYawOffset);
         var4 = (float)(var4 + this.randomPitchOffset);
         return new Rotation(this.calculateMouseMove(var2.getYaw(), var3), this.calculateMouseMove(var2.getPitch(), var4)).clamp();
      }

      @Override
      public final void tick() {
         this.randomYawOffset = (this.rand.nextDouble() - 0.5) * Baritone.settings().randomLooking.value;
         this.randomPitchOffset = (this.rand.nextDouble() - 0.5) * Baritone.settings().randomLooking.value;
         double var1 = this.rand.nextDouble() - 0.5;
         if (Math.abs(var1) < 0.1) {
            var1 *= 4.0;
         }

         this.randomYawOffset = this.randomYawOffset + var1 * Baritone.settings().randomLooking113.value;
      }

      @Override
      public final void advance(int var1) {
         for (int var2 = 0; var2 < var1; var2++) {
            this.tick();
         }
      }

      @Override
      public Rotation nextRotation(Rotation var1) {
         Rotation var2 = this.peekRotation(var1);
         this.tick();
         return var2;
      }

      @Override
      public final ITickableAimProcessor fork() {
         return new LookBehavior.AbstractAimProcessor(this) {
            private Rotation prev = AbstractAimProcessor.this.getPrevRotation();

            @Override
            public Rotation nextRotation(Rotation var1) {
               return this.prev = super.nextRotation(var1);
            }

            @Override
            protected Rotation getPrevRotation() {
               return this.prev;
            }
         };
      }

      protected abstract Rotation getPrevRotation();

      private float nudgeToLevel(float var1) {
         if (var1 < -20.0F) {
            return var1 + 1.0F;
         } else {
            return var1 > 10.0F ? var1 - 1.0F : var1;
         }
      }

      private float calculateMouseMove(float var1, float var2) {
         float var3 = var2 - var1;
         double var4 = this.angleToMouse(var3);
         return var1 + this.mouseToAngle(var4);
      }

      private double angleToMouse(float var1) {
         float var2 = this.mouseToAngle(1.0);
         return Math.round(var1 / var2);
      }

      private float mouseToAngle(double var1) {
         double var3 = this.ctx.minecraft().options.sensitivity().get() * 0.6F + 0.2F;
         return (float)(var1 * var3 * var3 * var3 * 8.0) * 0.15F;
      }
   }

   private static final class AimProcessor extends LookBehavior.AbstractAimProcessor {
      public AimProcessor(IPlayerContext var1) {
         super(var1);
      }

      @Override
      protected Rotation getPrevRotation() {
         return this.ctx.playerRotations();
      }
   }

   private static class Target {
      public final Rotation rotation;
      public final LookBehavior.Target.Mode mode;

      public Target(Rotation var1, LookBehavior.Target.Mode var2) {
         this.rotation = var1;
         this.mode = var2;
      }

      enum Mode {
         CLIENT,
         SERVER,
         NONE;

         static LookBehavior.Target.Mode resolve(IPlayerContext var0, boolean var1) {
            Settings var2 = Baritone.settings();
            boolean var3 = var2.antiCheatCompatibility.value;
            boolean var4 = var2.blockFreeLook.value;
            if (var0.player().isFallFlying()) {
               return var2.elytraFreeLook.value ? SERVER : CLIENT;
            }

            if (var2.freeLook.value) {
               if (var1) {
                  return var4 ? SERVER : CLIENT;
               } else {
                  return var3 ? SERVER : NONE;
               }
            } else {
               return CLIENT;
            }
         }
      }
   }
}
