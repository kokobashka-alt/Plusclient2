package wtf.onetap.module.misc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.BlockStateChangeEvent;
import wtf.onetap.event.Render3DEvent;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.render.Box3DRenderer;
import wtf.onetap.render.ColorUtil;
import wtf.onetap.render.Line3DRenderer;
import wtf.onetap.setting.BooleanSetting;
import wtf.onetap.setting.ColorSetting;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(
   name = "BaseFinder",
   description = "Ищет базы по характерным блокам(не совсем точный) (на Funtime лучше искать в аду)",
   category = Category.PLAYER
)
public class BaseFinder extends Module {
   private final ColorSetting color = new ColorSetting("Цвет", ColorUtil.rgba(130, 100, 255, 220)).build();
   private final BooleanSetting fill = new BooleanSetting("Заливка", true).build();
   private final BooleanSetting tracers = new BooleanSetting("Трассеры", true).build();
   private final FloatSetting radius = new FloatSetting("Радиус", 64.0F, 16.0F, 328.0F, 16.0F).build();
   private final Map<Long, Set<BlockPos>> longMap = new HashMap<>();
   private final ArrayDeque<Long> arrayDeque = new ArrayDeque<>();
   private final Set<Long> longSet = new HashSet<>();
   private final Matrix4f matrix4f = new Matrix4f();
   private ClientLevel clientLevel;
   private boolean flag = true;
   private int value;
   private float floatValue = Float.NaN;

   @Override
   public void onEnable() {
      this.start();
   }

   @Override
   public void onDisable() {
      this.reset();
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (this.clientLevel != MinecraftAccess.MINECRAFT.level) {
         this.clientLevel = MinecraftAccess.MINECRAFT.level;
         this.reset();
      }

      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.radius.getFloat() != this.floatValue) {
            this.floatValue = this.radius.getFloat();
            this.start();
         }

         if (this.flag || this.value++ >= 20) {
            this.value = 0;
            this.m_7_void();
         }

         this.m_2_void();
      } else {
         this.reset();
      }
   }

   @EventTarget
   private void onBlockStateChange(BlockStateChangeEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         long longValue = ChunkPos.pack(event.getPos());
         var blockPosSet = this.longMap.get(longValue);
         if (blockPosSet != null) {
            if (isBaseBlock(event.getNewState())) {
               blockPosSet.add(event.getPos().immutable());
            } else {
               blockPosSet.remove(event.getPos());
            }
         }
      }
   }

   @EventTarget
   private void onRender3D(Render3DEvent event) {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null && !this.longMap.isEmpty()) {
         boolean flag2 = (Boolean)this.fill.getValue();
         boolean flag3 = (Boolean)this.tracers.getValue();
         if (flag2 || flag3) {
            BlockPos blockPos = MinecraftAccess.MINECRAFT.player.blockPosition();
            double doubleValue = this.radius.getDouble() * this.radius.getDouble();
            int value2 = (Integer)this.color.getValue();
            this.matrix4f.set(event.getMatrix4f()).mul(event.getMatrix4f2());
            Vec3 vec3 = event.getVec3();
            ArrayList<AABB> aABBList = new ArrayList<>();

            for (Set set : this.longMap.values()) {
               for (BlockPos blockPos2 : (Iterable<BlockPos>)set) {
                  if (!(m_9(blockPos, blockPos2) > doubleValue)) {
                     BlockState blockState = MinecraftAccess.MINECRAFT.level.getBlockState(blockPos2);
                     if (isBaseBlock(blockState)) {
                        aABBList.add(this.isSolidBlock(blockPos2, blockState));
                     }
                  }
               }
            }

            if (!aABBList.isEmpty()) {
               if (flag2) {
                  int value3 = (int)((value2 >>> 24 & 0xFF) * 0.1F);
                  int value4 = value3 << 24 | value2 & 16777215;
                  int value5 = value4 >> 16 & 0xFF;
                  int value6 = value4 >> 8 & 0xFF;
                  int value7 = value4 & 0xFF;
                  int value8 = value4 >>> 24 & 0xFF;
                  Box3DRenderer.m_8(
                     this.matrix4f,
                     aABBList.size() * 24,
                     bufferBuilder -> {
                        for (AABB aABB : aABBList) {
                           Box3DRenderer.m_7(
                              bufferBuilder,
                              (float)(aABB.minX - vec3.x),
                              (float)(aABB.minY - vec3.y),
                              (float)(aABB.minZ - vec3.z),
                              (float)(aABB.maxX - vec3.x),
                              (float)(aABB.maxY - vec3.y),
                              (float)(aABB.maxZ - vec3.z),
                              value5,
                              value6,
                              value7,
                              value8
                           );
                        }
                     }
                  );
               }

               if (flag3) {
                  Camera camera = MinecraftAccess.MINECRAFT.gameRenderer.mainCamera();
                  Vector3fc vector3fc = camera.forwardVector();
                  Vec3 vec32 = vec3.add(vector3fc.x() * 3.0, vector3fc.y() * 3.0, vector3fc.z() * 3.0);
                  ArrayList list = new ArrayList(aABBList.size() * 2);
                  int[] intArray = new int[aABBList.size()];
                  int value9 = 0;

                  for (AABB aABB2 : aABBList) {
                     list.add(vec32);
                     list.add(aABB2.getCenter());
                     intArray[value9++] = value2;
                  }

                  Line3DRenderer.m_7(this.matrix4f, list, intArray, vec3, 1.0F);
               }
            }
         }
      }
   }

   private void reset() {
      this.longMap.clear();
      this.arrayDeque.clear();
      this.longSet.clear();
      this.flag = true;
      this.value = 0;
   }

   private void start() {
      this.flag = true;
   }

   private void m_7_void() {
      if (MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.player != null) {
         if (this.flag) {
            this.longMap.clear();
            this.arrayDeque.clear();
            this.longSet.clear();
            this.flag = false;
         }

         int value2 = MinecraftAccess.MINECRAFT.player.blockPosition().getX() >> 4;
         int value3 = MinecraftAccess.MINECRAFT.player.blockPosition().getZ() >> 4;
         int value4 = Math.max(1, (int)Math.ceil(this.radius.getFloat() / 16.0F));
         this.longMap.keySet().removeIf(longObj -> isOutOfRange(longObj, value2, value3, value4) || !MinecraftAccess.MINECRAFT.level.hasChunk(ChunkPos.getX(longObj), ChunkPos.getZ(longObj)));
         this.longSet.removeIf(longObj2 -> isOutOfRange(longObj2, value2, value3, value4));
         this.arrayDeque.removeIf(longObj3 -> isOutOfRange(longObj3, value2, value3, value4));
         ArrayList<java.lang.Long> longList = new ArrayList<>();

         for (int i = -value4; i <= value4; i++) {
            for (int i2 = -value4; i2 <= value4; i2++) {
               int value5 = value2 + i;
               int value6 = value3 + i2;
               if (MinecraftAccess.MINECRAFT.level.hasChunk(value5, value6)) {
                  long longValue = ChunkPos.pack(value5, value6);
                  if (!this.longMap.containsKey(longValue) && this.longSet.add(longValue)) {
                     longList.add(longValue);
                  }
               }
            }
         }

         longList.sort(Comparator.comparingInt(longObj4 -> chunkDistanceSq(longObj4, value2, value3)));
         this.arrayDeque.addAll(longList);
      }
   }

   private void m_2_void() {
      if (MinecraftAccess.MINECRAFT.level != null && !this.arrayDeque.isEmpty()) {
         long longValue = this.arrayDeque.removeFirst();
         this.longSet.remove(longValue);
         int value2 = ChunkPos.getX(longValue);
         int value3 = ChunkPos.getZ(longValue);
         if (MinecraftAccess.MINECRAFT.level.hasChunk(value2, value3)) {
            LevelChunk levelChunk = MinecraftAccess.MINECRAFT.level.getChunk(value2, value3);
            HashSet set = new HashSet();
            LevelChunkSection[] levelChunkSectionArray = levelChunk.getSections();

            for (int i = 0; i < levelChunkSectionArray.length; i++) {
               LevelChunkSection levelChunkSection = levelChunkSectionArray[i];
               if (levelChunkSection != null && !levelChunkSection.hasOnlyAir()) {
                  int value4 = SectionPos.sectionToBlockCoord(levelChunk.getSectionYFromSectionIndex(i));

                  for (int i2 = 0; i2 < 16; i2++) {
                     for (int i3 = 0; i3 < 16; i3++) {
                        for (int i4 = 0; i4 < 16; i4++) {
                           BlockState blockState = levelChunkSection.getBlockState(i3, i2, i4);
                           if (isBaseBlock(blockState)) {
                              set.add(new BlockPos((value2 << 4) + i3, value4 + i2, (value3 << 4) + i4));
                           }
                        }
                     }
                  }
               }
            }

            this.longMap.put(longValue, set);
         }
      }
   }

   private AABB isSolidBlock(BlockPos blockPos, BlockState blockState) {
      VoxelShape voxelShape = blockState.getShape(MinecraftAccess.MINECRAFT.level, blockPos);
      return !voxelShape.isEmpty()
         ? voxelShape.bounds().move(blockPos).inflate(0.002)
         : new AABB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1).inflate(0.002);
   }

   private static boolean isBaseBlock(BlockState blockState) {
      if (!blockState.is(Blocks.OAK_LOG) && !blockState.is(Blocks.BONE_BLOCK) && !(blockState.getBlock() instanceof AmethystClusterBlock)) {
         return blockState.hasProperty(BlockStateProperties.AXIS) && blockState.getValue(BlockStateProperties.AXIS) != Axis.Y
            ? true
            : blockState.hasProperty(BlockStateProperties.FACING) && blockState.getValue(BlockStateProperties.FACING) != Direction.NORTH;
      } else {
         return false;
      }
   }

   private static boolean isOutOfRange(long longValue, int value2, int value3, int value4) {
      return Math.abs(ChunkPos.getX(longValue) - value2) > value4 || Math.abs(ChunkPos.getZ(longValue) - value3) > value4;
   }

   private static int chunkDistanceSq(long longValue, int value2, int value3) {
      int value4 = ChunkPos.getX(longValue) - value2;
      int value5 = ChunkPos.getZ(longValue) - value3;
      return value4 * value4 + value5 * value5;
   }

   private static double m_9(BlockPos blockPos, BlockPos blockPos2) {
      double doubleValue = blockPos.getX() - blockPos2.getX();
      double doubleValue2 = blockPos.getY() - blockPos2.getY();
      double doubleValue3 = blockPos.getZ() - blockPos2.getZ();
      return doubleValue * doubleValue + doubleValue2 * doubleValue2 + doubleValue3 * doubleValue3;
   }
}
