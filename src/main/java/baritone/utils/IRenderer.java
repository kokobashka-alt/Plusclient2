package baritone.utils;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.utils.accessor.IEntityRenderManager;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface IRenderer {
   IEntityRenderManager renderManager = (IEntityRenderManager)Minecraft.getInstance().getEntityRenderDispatcher();
   Settings settings = BaritoneAPI.getSettings();
   RenderType linesWithDepthRenderType = RenderTypes.lines();
   RenderType linesNoDepthRenderType = RenderTypes.linesTranslucent();
   ByteBufferBuilder lineBytes = new ByteBufferBuilder(786432);
   ByteBufferBuilder quadBytes = new ByteBufferBuilder(786432);
   List<IRenderer.LineSegment> pendingLines = new ArrayList<>();
   List<IRenderer.LineSegment> pendingLinesNoDepth = new ArrayList<>();
   List<IRenderer.LineSegment> stagedLines = new ArrayList<>();
   float[] color = new float[]{1.0F, 1.0F, 1.0F, Float.intBitsToFloat(1132396544)};

   static void glColor(Color var0, float var1) {
      float[] var2 = var0.getColorComponents(null);
      color[0] = var2[0];
      color[1] = var2[1];
      color[2] = var2[2];
      color[3] = var1;
   }

   static int packedColor() {
      int var0 = (int)(Math.min(1.0F, Math.max(0.0F, color[3])) * 255.0F);
      int var1 = (int)(Math.min(1.0F, Math.max(0.0F, color[0])) * 255.0F);
      int var2 = (int)(Math.min(1.0F, Math.max(0.0F, color[1])) * 255.0F);
      int var3 = (int)(Math.min(1.0F, Math.max(0.0F, color[2])) * 255.0F);
      return var0 << 24 | var1 << 16 | var2 << 8 | var3;
   }

   static BufferBuilder startLines(Color var0, float var1) {
      glColor(var0, var1);
      lineBytes.discard();
      return new BufferBuilder(lineBytes, PrimitiveTopology.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
   }

   static BufferBuilder startLines(Color var0) {
      return startLines(var0, 0.4F);
   }

   static void endLines(BufferBuilder var0, boolean var1) {
      MeshData var2 = var0.build();
      if (var2 != null) {
         var2.close();
      }

      lineBytes.discard();
      (var1 ? pendingLinesNoDepth : pendingLines).addAll(stagedLines);
      stagedLines.clear();
   }

   static BufferBuilder startBlockQuads() {
      quadBytes.discard();
      return new BufferBuilder(quadBytes, PrimitiveTopology.QUADS, DefaultVertexFormat.BLOCK);
   }

   static void endBuffer(BufferBuilder var0, RenderType var1) {
      MeshData var2 = var0.build();
      if (var2 != null) {
         var2.close();
      }

      quadBytes.discard();
   }

   static List<IRenderer.LineSegment> drainLines(boolean var0) {
      List var1 = var0 ? pendingLinesNoDepth : pendingLines;
      ArrayList var2 = new ArrayList(var1);
      var1.clear();
      return var2;
   }

   static void emitLine(BufferBuilder var0, PoseStack var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14) {
      double var15 = var8 - var2;
      double var17 = var10 - var4;
      double var19 = var12 - var6;
      double var21 = 1.0 / Math.sqrt(var15 * var15 + var17 * var17 + var19 * var19);
      float var23 = (float)(var15 * var21);
      float var24 = (float)(var17 * var21);
      float var25 = (float)(var19 * var21);
      emitLine(var0, var1, var2, var4, var6, var8, var10, var12, var23, var24, var25, var14);
   }

   static void emitLine(
      BufferBuilder var0,
      PoseStack var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      float var20
   ) {
      emitLine(var0, var1, (float)var2, (float)var4, (float)var6, (float)var8, (float)var10, (float)var12, (float)var14, (float)var16, (float)var18, var20);
   }

   static void emitLine(
      BufferBuilder var0,
      PoseStack var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      Pose var12 = var1.last();
      var0.addVertex(var12, var2, var3, var4).setColor(color[0], color[1], color[2], color[3]).setNormal(var12, var8, var9, var10).setLineWidth(var11);
      var0.addVertex(var12, var5, var6, var7).setColor(color[0], color[1], color[2], color[3]).setNormal(var12, var8, var9, var10).setLineWidth(var11);
      stagedLines.add(new IRenderer.LineSegment(var2, var3, var4, var5, var6, var7, var11, packedColor()));
   }

   static void emitAABB(BufferBuilder var0, PoseStack var1, AABB var2, float var3) {
      AABB var4 = var2.move(-renderManager.renderPosX(), -renderManager.renderPosY(), -renderManager.renderPosZ());
      emitLine(var0, var1, var4.minX, var4.minY, var4.minZ, var4.maxX, var4.minY, var4.minZ, 1.0, 0.0, 0.0, var3);
      emitLine(var0, var1, var4.maxX, var4.minY, var4.minZ, var4.maxX, var4.minY, var4.maxZ, 0.0, 0.0, 1.0, var3);
      emitLine(var0, var1, var4.maxX, var4.minY, var4.maxZ, var4.minX, var4.minY, var4.maxZ, -1.0, 0.0, 0.0, var3);
      emitLine(var0, var1, var4.minX, var4.minY, var4.maxZ, var4.minX, var4.minY, var4.minZ, 0.0, 0.0, -1.0, var3);
      emitLine(var0, var1, var4.minX, var4.maxY, var4.minZ, var4.maxX, var4.maxY, var4.minZ, 1.0, 0.0, 0.0, var3);
      emitLine(var0, var1, var4.maxX, var4.maxY, var4.minZ, var4.maxX, var4.maxY, var4.maxZ, 0.0, 0.0, 1.0, var3);
      emitLine(var0, var1, var4.maxX, var4.maxY, var4.maxZ, var4.minX, var4.maxY, var4.maxZ, -1.0, 0.0, 0.0, var3);
      emitLine(var0, var1, var4.minX, var4.maxY, var4.maxZ, var4.minX, var4.maxY, var4.minZ, 0.0, 0.0, -1.0, var3);
      emitLine(var0, var1, var4.minX, var4.minY, var4.minZ, var4.minX, var4.maxY, var4.minZ, 0.0, 1.0, 0.0, var3);
      emitLine(var0, var1, var4.maxX, var4.minY, var4.minZ, var4.maxX, var4.maxY, var4.minZ, 0.0, 1.0, 0.0, var3);
      emitLine(var0, var1, var4.maxX, var4.minY, var4.maxZ, var4.maxX, var4.maxY, var4.maxZ, 0.0, 1.0, 0.0, var3);
      emitLine(var0, var1, var4.minX, var4.minY, var4.maxZ, var4.minX, var4.maxY, var4.maxZ, 0.0, 1.0, 0.0, var3);
   }

   static void emitAABB(BufferBuilder var0, PoseStack var1, AABB var2, double var3, float var5) {
      emitAABB(var0, var1, var2.inflate(var3, var3, var3), var5);
   }

   static void emitLine(BufferBuilder var0, PoseStack var1, Vec3 var2, Vec3 var3, float var4) {
      double var5 = renderManager.renderPosX();
      double var7 = renderManager.renderPosY();
      double var9 = renderManager.renderPosZ();
      emitLine(var0, var1, var2.x - var5, var2.y - var7, var2.z - var9, var3.x - var5, var3.y - var7, var3.z - var9, var4);
   }

   static void emitTexturedVertex(
      BufferBuilder var0, Pose var1, float var2, float var3, float var4, int var5, float var6, float var7, float var8, float var9, float var10
   ) {
      var0.addVertex(var1, var2, var3, var4)
         .setColor(var5)
         .setUv(var6, var7)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(var1, var8, var9, var10);
   }

   static RenderType beaconBeam(Identifier var0, boolean var1) {
      return RenderTypes.beaconBeam(var0, var1);
   }

   static RenderType beaconBeam(Identifier var0, boolean var1, boolean var2) {
      return RenderTypes.beaconBeam(var0, var1);
   }

   record LineSegment(double x1, double y1, double z1, double x2, double y2, double z2, float width, int argb) {
   }
}
