package baritone.utils;

import baritone.api.BaritoneAPI;
import baritone.api.event.events.RenderEvent;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalTwoBlocks;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.pathing.goals.GoalYLevel;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.interfaces.IGoalRenderPos;
import baritone.behavior.PathingBehavior;
import baritone.pathing.path.PathExecutor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PathRenderer implements IRenderer {
   private PathRenderer() {
   }

   public static double posX() {
      return renderManager.renderPosX();
   }

   public static double posY() {
      return renderManager.renderPosY();
   }

   public static double posZ() {
      return renderManager.renderPosZ();
   }

   public static void render(RenderEvent var0, PathingBehavior var1) {
      IPlayerContext var2 = var1.ctx;
      if (var2.world() != null) {
         if (var2.minecraft().gui.screen() instanceof GuiClick) {
            ((GuiClick)var2.minecraft().gui.screen()).onRender(var0.getModelViewStack(), var0.getProjectionMatrix());
         }

         float var3 = var0.getPartialTicks();
         Goal var4 = var1.getGoal();
         DimensionType var5 = var2.world().dimensionType();
         DimensionType var6 = BaritoneAPI.getProvider().getPrimaryBaritone().getPlayerContext().world().dimensionType();
         if (var5 == var6) {
            if (var4 != null && settings.renderGoal.value) {
               drawGoal(var0.getModelViewStack(), var2, var4, var3, settings.colorGoalBox.value);
            }

            if (settings.renderPath.value) {
               PathExecutor var7 = var1.getCurrent();
               PathExecutor var8 = var1.getNext();
               if (var7 != null && settings.renderSelectionBoxes.value) {
                  drawManySelectionBoxes(var0.getModelViewStack(), var2.player(), var7.toBreak(), settings.colorBlocksToBreak.value);
                  drawManySelectionBoxes(var0.getModelViewStack(), var2.player(), var7.toPlace(), settings.colorBlocksToPlace.value);
                  drawManySelectionBoxes(var0.getModelViewStack(), var2.player(), var7.toWalkInto(), settings.colorBlocksToWalkInto.value);
               }

               if (var7 != null && var7.getPath() != null) {
                  int var9 = Math.max(var7.getPosition() - 3, 0);
                  drawPath(var0.getModelViewStack(), var7.getPath().positions(), var9, settings.colorCurrentPath.value, settings.fadePath.value, 10, 20);
               }

               if (var8 != null && var8.getPath() != null) {
                  drawPath(var0.getModelViewStack(), var8.getPath().positions(), 0, settings.colorNextPath.value, settings.fadePath.value, 10, 20);
               }

               var1.getInProgress()
                  .ifPresent(
                     var2x -> {
                        var2x.bestPathSoFar()
                           .ifPresent(
                              var1xx -> drawPath(
                                 var0.getModelViewStack(), var1xx.positions(), 0, settings.colorBestPathSoFar.value, settings.fadePath.value, 10, 20
                              )
                           );
                        var2x.pathToMostRecentNodeConsidered()
                           .ifPresent(
                              var2xx -> {
                                 drawPath(
                                    var0.getModelViewStack(), var2xx.positions(), 0, settings.colorMostRecentConsidered.value, settings.fadePath.value, 10, 20
                                 );
                                 drawManySelectionBoxes(
                                    var0.getModelViewStack(),
                                    var2.player(),
                                    Collections.singletonList(var2xx.getDest()),
                                    settings.colorMostRecentConsidered.value
                                 );
                              }
                           );
                     }
                  );
            }
         }
      }
   }

   public static void drawPath(PoseStack var0, List<BetterBlockPos> var1, int var2, Color var3, boolean var4, int var5, int var6) {
      drawPath(var0, var1, var2, var3, var4, var5, var6, 0.5);
   }

   public static void drawPath(PoseStack var0, List<BetterBlockPos> var1, int var2, Color var3, boolean var4, int var5, int var6, double var7) {
      BufferBuilder var9 = IRenderer.startLines(var3);
      int var10 = var5 + var2;
      int var11 = var6 + var2;
      int var12 = var2;

      while (var12 < var1.size() - 1) {
         BetterBlockPos var14 = (BetterBlockPos)var1.get(var12);
         int var13;
         BetterBlockPos var15 = (BetterBlockPos)var1.get(var13 = var12 + 1);
         int var16 = var15.x - var14.x;
         int var17 = var15.y - var14.y;
         int var18 = var15.z - var14.z;

         while (
            var13 + 1 < var1.size()
               && (!var4 || var13 + 1 < var10)
               && var16 == ((BetterBlockPos)var1.get(var13 + 1)).x - var15.x
               && var17 == ((BetterBlockPos)var1.get(var13 + 1)).y - var15.y
               && var18 == ((BetterBlockPos)var1.get(var13 + 1)).z - var15.z
         ) {
            var15 = (BetterBlockPos)var1.get(++var13);
         }

         if (var4) {
            float var19;
            if (var12 <= var10) {
               var19 = 0.4F;
            } else {
               if (var12 > var11) {
                  break;
               }

               var19 = 0.4F * (1.0F - (float)(var12 - var10) / (var11 - var10));
            }

            IRenderer.glColor(var3, var19);
         }

         emitPathLine(var9, var0, var14.x, var14.y, var14.z, var15.x, var15.y, var15.z, var7);
         var12 = var13;
      }

      IRenderer.endLines(var9, settings.renderPathIgnoreDepth.value);
   }

   private static void emitPathLine(
      BufferBuilder var0, PoseStack var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14
   ) {
      double var16 = var14 + 0.03;
      double var18 = posX();
      double var20 = posY();
      double var22 = posZ();
      boolean var24 = !settings.renderPathAsLine.value;
      IRenderer.emitLine(
         var0,
         var1,
         var2 + var14 - var18,
         var4 + var14 - var20,
         var6 + var14 - var22,
         var8 + var14 - var18,
         var10 + var14 - var20,
         var12 + var14 - var22,
         settings.pathRenderLineWidthPixels.value
      );
      if (var24) {
         IRenderer.emitLine(
            var0,
            var1,
            var8 + var14 - var18,
            var10 + var14 - var20,
            var12 + var14 - var22,
            var8 + var14 - var18,
            var10 + var16 - var20,
            var12 + var14 - var22,
            settings.pathRenderLineWidthPixels.value
         );
         IRenderer.emitLine(
            var0,
            var1,
            var8 + var14 - var18,
            var10 + var16 - var20,
            var12 + var14 - var22,
            var2 + var14 - var18,
            var4 + var16 - var20,
            var6 + var14 - var22,
            settings.pathRenderLineWidthPixels.value
         );
         IRenderer.emitLine(
            var0,
            var1,
            var2 + var14 - var18,
            var4 + var16 - var20,
            var6 + var14 - var22,
            var2 + var14 - var18,
            var4 + var14 - var20,
            var6 + var14 - var22,
            settings.pathRenderLineWidthPixels.value
         );
      }
   }

   public static void drawManySelectionBoxes(PoseStack var0, Entity var1, Collection<BlockPos> var2, Color var3) {
      BufferBuilder var4 = IRenderer.startLines(var3);
      BlockStateInterface var5 = new BlockStateInterface(BaritoneAPI.getProvider().getPrimaryBaritone().getPlayerContext());
      var2.forEach(var4x -> {
         BlockState var5x = var5.get0(var4x);
         VoxelShape var6 = var5x.getShape(var1.level(), var4x);
         AABB var7 = var6.isEmpty() ? Shapes.block().bounds() : var6.bounds();
         var7 = var7.move(var4x);
         IRenderer.emitAABB(var4, var0, var7, 0.002, settings.pathRenderLineWidthPixels.value);
      });
      IRenderer.endLines(var4, settings.renderSelectionBoxesIgnoreDepth.value);
   }

   public static void drawGoal(PoseStack var0, IPlayerContext var1, Goal var2, float var3, Color var4) {
      drawGoal(null, var0, var1, var2, var3, var4, true);
   }

   private static void drawGoal(@Nullable BufferBuilder var0, PoseStack var1, IPlayerContext var2, Goal var3, float var4, Color var5, boolean var6) {
      if (!var6 && var0 == null) {
         throw new RuntimeException("BufferBuilder must not be null if setupRender is false");
      }

      double var7 = posX();
      double var9 = posY();
      double var11 = posZ();
      double var25;
      if (!settings.renderGoalAnimated.value) {
         var25 = 0.999F;
      } else {
         var25 = Mth.cos((float)((float)(System.nanoTime() / 100000L % 20000L) / 20000.0F * Math.PI * 2.0));
      }

      if (var3 instanceof IGoalRenderPos) {
         BlockPos var31 = ((IGoalRenderPos)var3).getGoalPos();
         double var13 = var31.getX() + 0.002 - var7;
         double var15 = var31.getX() + 1 - 0.002 - var7;
         double var17 = var31.getZ() + 0.002 - var11;
         double var19 = var31.getZ() + 1 - 0.002 - var11;
         if (var3 instanceof GoalGetToBlock || var3 instanceof GoalTwoBlocks) {
            var25 /= 2.0;
         }

         double var27 = 1.0 + var25 + var31.getY() - var9;
         double var29 = 1.0 - var25 + var31.getY() - var9;
         double var21 = var31.getY() - var9;
         double var23 = var21 + 2.0;
         if (var3 instanceof GoalGetToBlock || var3 instanceof GoalTwoBlocks) {
            var27 -= 0.5;
            var29 -= 0.5;
            var23--;
         }

         drawDankLitGoalBox(var0, var1, var5, var13, var15, var17, var19, var21, var23, var27, var29, var6);
      } else if (var3 instanceof GoalXZ var55) {
         double var45 = var2.world().getMinY();
         double var48 = var2.world().getMaxY();
         double var37 = var55.getX() + 0.002 - var7;
         double var39 = var55.getX() + 1 - 0.002 - var7;
         double var41 = var55.getZ() + 0.002 - var11;
         double var43 = var55.getZ() + 1 - 0.002 - var11;
         double var51 = 0.0;
         double var53 = 0.0;
         var45 -= var9;
         var48 -= var9;
         drawDankLitGoalBox(var0, var1, var5, var37, var39, var41, var43, var45, var48, var51, var53, var6);
         drawGoalXZBeacon(var1, var2, (GoalXZ)var3, var45, var48, var4, var5);
      } else if (var3 instanceof GoalComposite) {
         boolean var56 = Arrays.stream(((GoalComposite)var3).goals()).allMatch(IGoalRenderPos.class::isInstance);
         BufferBuilder var32 = var0;
         if (var56) {
            var32 = IRenderer.startLines(var5, settings.goalRenderLineWidthPixels.value);
         }

         for (Goal var36 : ((GoalComposite)var3).goals()) {
            drawGoal(var32, var1, var2, var36, var4, var5, !var56);
         }

         if (var56) {
            IRenderer.endLines(var32, settings.renderGoalIgnoreDepth.value);
         }
      } else if (var3 instanceof GoalInverted) {
         drawGoal(var1, var2, ((GoalInverted)var3).origin, var4, settings.colorInvertedGoalBox.value);
      } else if (var3 instanceof GoalYLevel var57) {
         double var38 = var2.player().position().x - settings.yLevelBoxSize.value - var7;
         double var42 = var2.player().position().z - settings.yLevelBoxSize.value - var11;
         double var40 = var2.player().position().x + settings.yLevelBoxSize.value - var7;
         double var44 = var2.player().position().z + settings.yLevelBoxSize.value - var11;
         double var47 = ((GoalYLevel)var3).level - var9;
         double var50 = var47 + 2.0;
         double var52 = 1.0 + var25 + var57.level - var9;
         double var54 = 1.0 - var25 + var57.level - var9;
         drawDankLitGoalBox(var0, var1, var5, var38, var40, var42, var44, var47, var50, var52, var54, var6);
      }
   }

   private static void drawDankLitGoalBox(
      BufferBuilder var0,
      PoseStack var1,
      Color var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      boolean var19
   ) {
      if (var19) {
         var0 = IRenderer.startLines(var2);
      }

      renderHorizontalQuad(var0, var1, var3, var5, var7, var9, var15, settings.goalRenderLineWidthPixels.value);
      renderHorizontalQuad(var0, var1, var3, var5, var7, var9, var17, settings.goalRenderLineWidthPixels.value);

      for (double var20 = var11; var20 < var13; var20 += 16.0) {
         double var22 = Math.min(var13, var20 + 16.0);
         IRenderer.emitLine(var0, var1, var3, var20, var7, var3, var22, var7, 0.0, 1.0, 0.0, settings.goalRenderLineWidthPixels.value);
         IRenderer.emitLine(var0, var1, var5, var20, var7, var5, var22, var7, 0.0, 1.0, 0.0, settings.goalRenderLineWidthPixels.value);
         IRenderer.emitLine(var0, var1, var5, var20, var9, var5, var22, var9, 0.0, 1.0, 0.0, settings.goalRenderLineWidthPixels.value);
         IRenderer.emitLine(var0, var1, var3, var20, var9, var3, var22, var9, 0.0, 1.0, 0.0, settings.goalRenderLineWidthPixels.value);
      }

      if (var19) {
         IRenderer.endLines(var0, settings.renderGoalIgnoreDepth.value);
      }
   }

   private static void renderHorizontalQuad(BufferBuilder var0, PoseStack var1, double var2, double var4, double var6, double var8, double var10, float var12) {
      if (var10 != 0.0) {
         IRenderer.emitLine(var0, var1, var2, var10, var6, var4, var10, var6, 1.0, 0.0, 0.0, var12);
         IRenderer.emitLine(var0, var1, var4, var10, var6, var4, var10, var8, 0.0, 0.0, 1.0, var12);
         IRenderer.emitLine(var0, var1, var4, var10, var8, var2, var10, var8, -1.0, 0.0, 0.0, var12);
         IRenderer.emitLine(var0, var1, var2, var10, var8, var2, var10, var6, 0.0, 0.0, -1.0, var12);
      }
   }

   private static void drawGoalXZBeacon(PoseStack var0, IPlayerContext var1, GoalXZ var2, double var3, double var5, float var7, Color var8) {
      float var9 = settings.renderGoalAnimated.value ? (float)var1.world().getGameTime() + var7 : 0.0F;
      int var10 = var8.getRGB() & 16777215 | 536870912;
      double var11 = var5 - var3;
      var0.pushPose();
      var0.translate(var2.getX() - posX(), var3 - posY(), var2.getZ() - posZ());
      renderGoalXZBeaconLayer(var0, var11, var9, var8.getRGB(), 0.2F, false);
      renderGoalXZBeaconLayer(var0, var11, var9, var10, 0.25F, true);
      var0.popPose();
   }

   private static void renderGoalXZBeaconLayer(PoseStack var0, double var1, float var3, int var4, float var5, boolean var6) {
      BufferBuilder var7 = IRenderer.startBlockQuads();
      float var8 = Mth.frac(-var3 * 0.2F - Mth.floor(-var3 * 0.1F));
      var0.pushPose();
      var0.translate(0.5, 0.0, 0.5);
      if (!var6) {
         var0.pushPose();
         var0.mulPose(Axis.YP.rotationDegrees(var3 * 2.25F - 45.0F));
      }

      float var9 = -1.0F + var8;
      float var10 = (float)(var6 ? var1 + var9 : var1 * (0.5F / var5) + var9);
      Pose var11 = var0.last();
      if (var6) {
         emitBeaconShell(var7, var11, var4, 0.0F, (float)var1, -var5, -var5, var5, -var5, -var5, var5, var5, var5, var9, var10);
      } else {
         emitBeaconShell(var7, var11, var4, 0.0F, (float)var1, 0.0F, var5, var5, 0.0F, -var5, 0.0F, 0.0F, -var5, var9, var10);
      }

      if (!var6) {
         var0.popPose();
      }

      var0.popPose();
      IRenderer.endBuffer(var7, IRenderer.beaconBeam(BeaconRenderer.BEAM_LOCATION, var6, settings.renderGoalIgnoreDepth.value));
   }

   private static void emitBeaconShell(
      BufferBuilder var0,
      Pose var1,
      int var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14
   ) {
      emitBeaconFace(var0, var1, var2, var3, var4, var5, var6, var7, var8, 0.0F, 1.0F, var13, var14);
      emitBeaconFace(var0, var1, var2, var3, var4, var11, var12, var9, var10, 0.0F, 1.0F, var13, var14);
      emitBeaconFace(var0, var1, var2, var3, var4, var7, var8, var11, var12, 0.0F, 1.0F, var13, var14);
      emitBeaconFace(var0, var1, var2, var3, var4, var9, var10, var5, var6, 0.0F, 1.0F, var13, var14);
   }

   private static void emitBeaconFace(
      BufferBuilder var0,
      Pose var1,
      int var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12
   ) {
      float var13 = var8 - var6;
      float var14 = var5 - var7;
      float var15 = Mth.sqrt(var13 * var13 + var14 * var14);
      if (var15 != 0.0F) {
         var13 /= var15;
         var14 /= var15;
      }

      IRenderer.emitTexturedVertex(var0, var1, var5, var4, var6, var2, var10, var11, var13, 0.0F, var14);
      IRenderer.emitTexturedVertex(var0, var1, var5, var3, var6, var2, var10, var12, var13, 0.0F, var14);
      IRenderer.emitTexturedVertex(var0, var1, var7, var3, var8, var2, var9, var12, var13, 0.0F, var14);
      IRenderer.emitTexturedVertex(var0, var1, var7, var4, var8, var2, var9, var11, var13, 0.0F, var14);
   }
}
