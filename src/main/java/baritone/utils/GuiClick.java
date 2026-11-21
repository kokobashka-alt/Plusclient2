package baritone.utils;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent.RunCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class GuiClick extends Screen implements Helper {
   private Matrix4f projectionViewMatrix;
   private BlockPos clickStart;
   private BlockPos currentMouseOver;

   public GuiClick() {
      super(Component.literal("CLICK"));
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor var1, int var2, int var3, float var4) {
      double var5 = mc.mouseHandler.xpos();
      double var7 = mc.mouseHandler.ypos();
      var7 = mc.getWindow().getScreenHeight() - var7;
      var7 *= (double)mc.getWindow().getHeight() / mc.getWindow().getScreenHeight();
      var5 *= (double)mc.getWindow().getWidth() / mc.getWindow().getScreenWidth();
      Vec3 var9 = this.toWorld(var5, var7, 0.0);
      Vec3 var10 = this.toWorld(var5, var7, 1.0);
      if (var9 != null && var10 != null) {
         Vec3 var11 = new Vec3(PathRenderer.posX(), PathRenderer.posY(), PathRenderer.posZ());
         LocalPlayer var12 = BaritoneAPI.getProvider().getPrimaryBaritone().getPlayerContext().player();
         BlockHitResult var13 = var12.level().clip(new ClipContext(var9.add(var11), var10.add(var11), Block.OUTLINE, Fluid.NONE, var12));
         if (var13 != null && var13.getType() == Type.BLOCK) {
            this.currentMouseOver = var13.getBlockPos();
         }
      }
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor var1, int var2, int var3, float var4) {
   }

   @Override
   public boolean mouseReleased(MouseButtonEvent var1) {
      if (this.currentMouseOver != null) {
         if (var1.button() == 0) {
            if (this.clickStart != null && !this.clickStart.equals(this.currentMouseOver)) {
               BaritoneAPI.getProvider().getPrimaryBaritone().getSelectionManager().removeAllSelections();
               BaritoneAPI.getProvider()
                  .getPrimaryBaritone()
                  .getSelectionManager()
                  .addSelection(BetterBlockPos.from(this.clickStart), BetterBlockPos.from(this.currentMouseOver));
               MutableComponent var2 = Component.literal("Selection made! For usage: " + Baritone.settings().prefix.value + "help sel");
               var2.setStyle(var2.getStyle().withColor(ChatFormatting.WHITE).withClickEvent(new RunCommand(FORCE_COMMAND_PREFIX + "help sel")));
               Helper.HELPER.logDirect(var2);
               this.clickStart = null;
            } else {
               BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.currentMouseOver));
            }
         } else if (var1.button() == 1) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.currentMouseOver.above()));
         }
      }

      this.clickStart = null;
      return super.mouseReleased(var1);
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      this.clickStart = this.currentMouseOver;
      return super.mouseClicked(var1, var2);
   }

   public void onRender(PoseStack var1, Matrix4f var2) {
      this.projectionViewMatrix = new Matrix4f(var2);
      this.projectionViewMatrix.mul(var1.last().pose());
      this.projectionViewMatrix.invert();
      if (this.currentMouseOver != null) {
         Entity var3 = mc.getCameraEntity();
         PathRenderer.drawManySelectionBoxes(var1, var3, Collections.singletonList(this.currentMouseOver), Color.CYAN);
         if (this.clickStart != null && !this.clickStart.equals(this.currentMouseOver)) {
            BufferBuilder var4 = IRenderer.startLines(Color.RED);
            BetterBlockPos var5 = new BetterBlockPos(this.currentMouseOver);
            BetterBlockPos var6 = new BetterBlockPos(this.clickStart);
            IRenderer.emitAABB(
               var4,
               var1,
               new AABB(
                  Math.min(var5.x, var6.x),
                  Math.min(var5.y, var6.y),
                  Math.min(var5.z, var6.z),
                  Math.max(var5.x, var6.x) + 1,
                  Math.max(var5.y, var6.y) + 1,
                  Math.max(var5.z, var6.z) + 1
               ),
               Baritone.settings().pathRenderLineWidthPixels.value
            );
            IRenderer.endLines(var4, true);
         }
      }
   }

   private Vec3 toWorld(double var1, double var3, double var5) {
      if (this.projectionViewMatrix == null) {
         return null;
      }

      var1 /= mc.getWindow().getWidth();
      var3 /= mc.getWindow().getHeight();
      var1 = var1 * 2.0 - 1.0;
      var3 = var3 * 2.0 - 1.0;
      Vector4f var7 = new Vector4f((float)var1, (float)var3, (float)var5, 1.0F);
      this.projectionViewMatrix.transform(var7);
      if (var7.w() == 0.0F) {
         return null;
      }

      var7.mul(1.0F / var7.w());
      return new Vec3(var7.x(), var7.y(), var7.z());
   }
}
