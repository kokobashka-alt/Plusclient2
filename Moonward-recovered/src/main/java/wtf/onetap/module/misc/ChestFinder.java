package wtf.onetap.module.misc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import wtf.onetap.Onetap;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.TickEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.util.ChatUtil;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "ChestFinder", description = "Пишет в чат координаты найденных сундуков", category = Category.MISC)
public class ChestFinder extends Module {
   private static final Path PATH = FabricLoader.getInstance().getGameDir().resolve("onetap").resolve("chests");
   private final Set<GlobalPos> globalPosSet = new HashSet<>();
   private Path path;
   private String text;
   private int value;
   private int value2;
   private boolean flag = true;

   @Override
   public void onEnable() {
      this.value2 = 0;
      this.flag = true;
   }

   @EventTarget
   private void onTick(TickEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null) {
         if (this.value2++ % 20 == 0) {
            ServerData serverData = MinecraftAccess.MINECRAFT.getCurrentServer();
            String text2 = serverData != null ? serverData.ip.replaceAll("[^a-zA-Z0-9.-]", "_") : "singleplayer";
            if (!text2.equals(this.text)) {
               this.m_4_void(text2);
            }

            ArrayList<GlobalPos> globalPosList = new ArrayList<>();
            int value3 = MinecraftAccess.MINECRAFT.options.renderDistance().get();
            ChunkPos chunkPos = MinecraftAccess.MINECRAFT.player.chunkPosition();

            for (int i = -value3; i <= value3; i++) {
               for (int i2 = -value3; i2 <= value3; i2++) {
                  int value4 = chunkPos.x() + i;
                  int value5 = chunkPos.z() + i2;
                  if (MinecraftAccess.MINECRAFT.level.hasChunk(value4, value5)) {
                     for (BlockEntity blockEntity : MinecraftAccess.MINECRAFT.level.getChunk(value4, value5).getBlockEntities().values()) {
                        if (blockEntity instanceof ChestBlockEntity) {
                           GlobalPos globalPos = GlobalPos.of(MinecraftAccess.MINECRAFT.level.dimension(), blockEntity.getBlockPos());
                           if (this.globalPosSet.add(globalPos)) {
                              globalPosList.add(globalPos);
                           }
                        }
                     }
                  }
               }
            }

            if (globalPosList.isEmpty()) {
               if (this.flag) {
                  ChatUtil.log("Не нашёл сундуков!");
               }

               this.flag = false;
            } else {
               this.flag = false;
               StringBuilder stringBuilder = new StringBuilder();

               for (GlobalPos globalPos2 : globalPosList) {
                  this.value++;
                  BlockPos blockPos = globalPos2.pos();
                  String text3 = globalPos2.dimension() == Level.OVERWORLD ? "" : " §8(" + globalPos2.dimension().identifier().getPath() + ")";
                  ChatUtil.log(String.format("Нашёл сундук №%d: %d %d %d%s", this.value, blockPos.getX(), blockPos.getY(), blockPos.getZ(), text3));
                  stringBuilder.append('#')
                     .append(this.value)
                     .append(' ')
                     .append(blockPos.getX())
                     .append(' ')
                     .append(blockPos.getY())
                     .append(' ')
                     .append(blockPos.getZ())
                     .append(' ')
                     .append(globalPos2.dimension().identifier())
                     .append('\n');
               }

               try {
                  Files.createDirectories(PATH);
                  Files.writeString(this.path, stringBuilder.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
               } catch (IOException e) {
                  Onetap.LOGGER.error("Failed to write chest log", e);
               }
            }
         }
      }
   }

   private void m_4_void(String text2) {
      this.globalPosSet.clear();
      this.value = 0;
      this.text = text2;
      this.path = PATH.resolve(text2 + ".log");
      if (Files.exists(this.path)) {
         try {
            for (String text3 : Files.readAllLines(this.path, StandardCharsets.UTF_8)) {
               String[] stringArray = text3.trim().split("\\s+");
               if (stringArray.length >= 5) {
                  this.value = Math.max(this.value, Integer.parseInt(stringArray[0].substring(1)));
                  Identifier identifier = Identifier.tryParse(stringArray[4]);
                  if (identifier != null) {
                     var resourceKey = ResourceKey.create(Registries.DIMENSION, identifier);
                     this.globalPosSet.add(GlobalPos.of(resourceKey, new BlockPos(Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2]), Integer.parseInt(stringArray[3]))));
                  }
               }
            }
         } catch (IOException | NumberFormatException e) {
            Onetap.LOGGER.error("Failed to load chest log", e);
         }
      }
   }
}
