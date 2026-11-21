package wtf.onetap.module.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor.Brightness;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.command.BlockEspCommand;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "BlockESP", description = "Подсвечивает добавленные вами блоки и сущности через .blockesp", category = Category.RENDER)
public class BlockESP extends Module {
   private final List<BlockPos> blockPosList = new CopyOnWriteArrayList<>();
   private final Map<Block, Integer> blockMap = new HashMap<>();
   private final Matrix4f matrix4f = new Matrix4f();
   private final List<Vec3> vec3List = new ArrayList<>(24);
   private ExecutorService executorService;
   private int value;

   @Override
   public void onEnable() {
      this.blockPosList.clear();
      this.blockMap.clear();
      this.executorService = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon().factory());
      this.value = 0;
   }

   @Override
   public void onDisable() {
      this.blockPosList.clear();
      this.blockMap.clear();
      if (this.executorService != null) {
         this.executorService.shutdownNow();
         this.executorService = null;
      }
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (++this.value % 12 == 0) {
            var blockList = BlockEspCommand.getBlocks();
            if (blockList.isEmpty()) {
               this.blockPosList.clear();
            } else {
               var blockSet = Set.copyOf(blockList);
               this.executorService.submit(() -> this.m_2_void(blockSet));
            }
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         var blockList = BlockEspCommand.getBlocks();
         var entityTypeList = BlockEspCommand.getEntityTypes();
         if (!blockList.isEmpty() || !entityTypeList.isEmpty()) {
            this.matrix4f.set(event.getMatrix4f()).mul(event.getMatrix4f2());
            Vec3 vec3 = event.getVec3();
            if (!blockList.isEmpty()) {
               for (BlockPos blockPos : this.blockPosList) {
                  this.m_0(blockPos, vec3);
               }
            }

            if (!entityTypeList.isEmpty()) {
               for (Entity entity : MinecraftAccess.MINECRAFT.level.entitiesForRendering()) {
                  if (entityTypeList.contains(entity.getType())) {
                     this.m_4(entity, event.getFloatValue(), vec3);
                  }
               }
            }
         }
      }
   }

   private void m_4(Entity entity, float floatValue, Vec3 vec3) {
      Vec3 vec32 = entity.getPosition(floatValue).subtract(entity.position());
      AABB aABB = entity.getBoundingBox().move(vec32);
      this.vec3List.clear();
      this.m_0(aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ);
      Line3DRenderer.drawLines(this.matrix4f, this.vec3List, vec3, 1.0F, ColorUtil.primaryColor());
   }

   private void m_0(BlockPos blockPos, Vec3 vec3) {
      BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos);
      Block block = blockState.getBlock();
      Integer integer = this.blockMap.get(block);
      if (integer == null) {
         integer = this.m_4(blockState, blockPos);
         this.blockMap.put(block, integer);
      }

      this.vec3List.clear();
      this.m_0(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1);
      Line3DRenderer.drawLines(this.matrix4f, this.vec3List, vec3, 1.0F, integer);
   }

   private int m_4(BlockState blockState, BlockPos blockPos) {
      BlockTintSource blockTintSource = MinecraftAccess.MINECRAFT.getBlockColors().getTintSource(blockState, 0);
      if (blockTintSource != null) {
         int value2 = blockTintSource.colorInWorld(blockState, MinecraftAccess.MINECRAFT.level, blockPos);
         if (value2 >>> 24 != 0) {
            return value2;
         }
      }

      MapColor mapColor = blockState.getMapColor(MinecraftAccess.MINECRAFT.level, blockPos);
      return mapColor != MapColor.NONE ? mapColor.calculateARGBColor(Brightness.NORMAL) : ColorUtil.primaryColor();
   }

   private void m_0(double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5, double doubleValue6) {
      this.addLine(doubleValue, doubleValue2, doubleValue3, doubleValue4, doubleValue2, doubleValue3);
      this.addLine(doubleValue, doubleValue2, doubleValue6, doubleValue4, doubleValue2, doubleValue6);
      this.addLine(doubleValue, doubleValue5, doubleValue3, doubleValue4, doubleValue5, doubleValue3);
      this.addLine(doubleValue, doubleValue5, doubleValue6, doubleValue4, doubleValue5, doubleValue6);
      this.addLine(doubleValue, doubleValue2, doubleValue3, doubleValue, doubleValue2, doubleValue6);
      this.addLine(doubleValue4, doubleValue2, doubleValue3, doubleValue4, doubleValue2, doubleValue6);
      this.addLine(doubleValue, doubleValue5, doubleValue3, doubleValue, doubleValue5, doubleValue6);
      this.addLine(doubleValue4, doubleValue5, doubleValue3, doubleValue4, doubleValue5, doubleValue6);
      this.addLine(doubleValue, doubleValue2, doubleValue3, doubleValue, doubleValue5, doubleValue3);
      this.addLine(doubleValue4, doubleValue2, doubleValue3, doubleValue4, doubleValue5, doubleValue3);
      this.addLine(doubleValue, doubleValue2, doubleValue6, doubleValue, doubleValue5, doubleValue6);
      this.addLine(doubleValue4, doubleValue2, doubleValue6, doubleValue4, doubleValue5, doubleValue6);
   }

   private void addLine(double doubleValue, double doubleValue2, double doubleValue3, double doubleValue4, double doubleValue5, double doubleValue6) {
      this.vec3List.add(new Vec3(doubleValue, doubleValue2, doubleValue3));
      this.vec3List.add(new Vec3(doubleValue4, doubleValue5, doubleValue6));
   }

   private void m_2_void(Set<Block> blockSet) {
      ArrayList list = new ArrayList();
      BlockPos blockPos = MinecraftAccess.MINECRAFT.player.blockPosition();
      Notifications.m_5_C3_sArr();
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      int value2 = MinecraftAccess.MINECRAFT.level.getMinY();
      int value3 = MinecraftAccess.MINECRAFT.level.getMaxY();

      for (int i = blockPos.getX() - 70; i <= blockPos.getX() + 70; i++) {
         for (int i2 = blockPos.getZ() - 70; i2 <= blockPos.getZ() + 70; i2++) {
            if (MinecraftAccess.MINECRAFT.level.hasChunk(i >> 4, i2 >> 4)) {
               for (int i3 = value2; i3 <= value3; i3++) {
                  if (blockSet.contains(MinecraftAccess.MINECRAFT.level.getBlockState(mutableBlockPos.set(i, i3, i2)).getBlock())) {
                     list.add(mutableBlockPos.immutable());
                  }
               }
            }
         }
      }

      this.blockPosList.clear();
      this.blockPosList.addAll(list);
   }
}
