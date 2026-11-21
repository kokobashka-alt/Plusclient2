package baritone.cache;

import baritone.Baritone;
import baritone.api.cache.ICachedRegion;
import baritone.api.utils.BlockUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

public class CachedRegion implements ICachedRegion {
   private final CachedChunk[][] chunks = new CachedChunk[32][32];
   private final int x;
   private final int z;
   private final DimensionType dimension;
   private final ResourceKey<Level> dimensionId;
   private boolean hasUnsavedChanges;

   CachedRegion(int var1, int var2, DimensionType var3, ResourceKey<Level> var4) {
      this.x = var1;
      this.z = var2;
      this.hasUnsavedChanges = false;
      this.dimension = var3;
      this.dimensionId = var4;
   }

   @Override
   public final BlockState getBlock(int var1, int var2, int var3) {
      int var4 = var2 - this.dimension.minY();
      CachedChunk var5 = this.chunks[var1 >> 4][var3 >> 4];
      return var5 != null ? var5.getBlock(var1 & 15, var4, var3 & 15, this.dimension, this.dimensionId) : null;
   }

   @Override
   public final boolean isCached(int var1, int var2) {
      return this.chunks[var1 >> 4][var2 >> 4] != null;
   }

   public final ArrayList<BlockPos> getLocationsOf(String var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < 32; var3++) {
         for (int var4 = 0; var4 < 32; var4++) {
            if (this.chunks[var3][var4] != null) {
               var var5 = this.chunks[var3][var4].getAbsoluteBlocks(var1);
               if (var5 != null) {
                  var2.addAll(var5);
               }
            }
         }
      }

      return var2;
   }

   public final synchronized void updateCachedChunk(int var1, int var2, CachedChunk var3) {
      this.chunks[var1][var2] = var3;
      this.hasUnsavedChanges = true;
   }

   public final synchronized void save(String var1) {
      if (this.hasUnsavedChanges) {
         this.removeExpired();

         try {
            Path var2 = Paths.get(var1);
            if (!Files.exists(var2)) {
               Files.createDirectories(var2);
            }

            System.out.println("Saving region " + this.x + "," + this.z + " to disk " + var2);
            Path var3 = getRegionFile(var2, this.x, this.z);
            if (!Files.exists(var3)) {
               Files.createFile(var3);
            }

            try (
               FileOutputStream var4 = new FileOutputStream(var3.toFile());
               GZIPOutputStream var5 = new GZIPOutputStream(var4, 16384);
               DataOutputStream var6 = new DataOutputStream(var5);
            ) {
               var6.writeInt(456022911);

               for (int var7 = 0; var7 < 32; var7++) {
                  for (int var8 = 0; var8 < 32; var8++) {
                     CachedChunk var9 = this.chunks[var7][var8];
                     if (var9 == null) {
                        var6.write(0);
                     } else {
                        var6.write(1);
                        byte[] var10 = var9.toByteArray();
                        var6.write(var10);
                        var6.write(new byte[var9.sizeInBytes - var10.length]);
                     }
                  }
               }

               for (int var21 = 0; var21 < 32; var21++) {
                  for (int var24 = 0; var24 < 32; var24++) {
                     if (this.chunks[var21][var24] != null) {
                        for (int var27 = 0; var27 < 256; var27++) {
                           var6.writeUTF(BlockUtils.blockToString(this.chunks[var21][var24].getOverview()[var27].getBlock()));
                        }
                     }
                  }
               }

               for (int var22 = 0; var22 < 32; var22++) {
                  for (int var25 = 0; var25 < 32; var25++) {
                     if (this.chunks[var22][var25] != null) {
                        var var28 = this.chunks[var22][var25].getRelativeBlocks();
                        var6.writeShort(var28.entrySet().size());

                        for (Entry var11 : var28.entrySet()) {
                           var6.writeUTF((String)var11.getKey());
                           var6.writeShort(((List)var11.getValue()).size());

                           for (BlockPos var13 : (Iterable<BlockPos>)(List)var11.getValue()) {
                              var6.writeByte((byte)(var13.getZ() << 4 | var13.getX()));
                              var6.writeInt(var13.getY() - this.dimension.minY());
                           }
                        }
                     }
                  }
               }

               for (int var23 = 0; var23 < 32; var23++) {
                  for (int var26 = 0; var26 < 32; var26++) {
                     if (this.chunks[var23][var26] != null) {
                        var6.writeLong(this.chunks[var23][var26].cacheTimestamp);
                     }
                  }
               }
            }

            this.hasUnsavedChanges = false;
            System.out.println("Saved region successfully");
         } catch (Exception var20) {
            var20.printStackTrace();
         }
      }
   }

   public synchronized void load(String var1) {
      try {
         Path var2 = Paths.get(var1);
         if (!Files.exists(var2)) {
            Files.createDirectories(var2);
         }

         Path var3 = getRegionFile(var2, this.x, this.z);
         if (!Files.exists(var3)) {
            return;
         }

         System.out.println("Loading region " + this.x + "," + this.z + " from disk " + var2);
         long var4 = System.nanoTime() / 1000000L;

         try (
            FileInputStream var6 = new FileInputStream(var3.toFile());
            GZIPInputStream var7 = new GZIPInputStream(var6, 32768);
            DataInputStream var8 = new DataInputStream(var7);
         ) {
            int var9 = var8.readInt();
            if (var9 != 456022911) {
               throw new IOException("Bad magic value " + var9);
            }

            boolean[][] var10 = new boolean[32][32];
            BitSet[][] var11 = new BitSet[32][32];
            Map[][] var12 = new Map[32][32];
            BlockState[][][] var13 = new BlockState[32][32][];
            long[][] var14 = new long[32][32];

            for (int var15 = 0; var15 < 32; var15++) {
               for (int var16 = 0; var16 < 32; var16++) {
                  int var17 = var8.read();
                  switch (var17) {
                     case 1:
                        byte[] var18 = new byte[CachedChunk.sizeInBytes(CachedChunk.size(this.dimension.height()))];
                        var8.readFully(var18);
                        var11[var15][var16] = BitSet.valueOf(var18);
                        var12[var15][var16] = new HashMap();
                        var13[var15][var16] = new BlockState[256];
                        var10[var15][var16] = true;
                        break;
                     case 0:
                     default:
                        throw new IOException("Malformed stream");
                  }
               }
            }

            for (int var35 = 0; var35 < 32; var35++) {
               for (int var39 = 0; var39 < 32; var39++) {
                  if (var10[var35][var39]) {
                     for (int var43 = 0; var43 < 256; var43++) {
                        var13[var35][var39][var43] = BlockUtils.stringToBlockRequired(var8.readUTF()).defaultBlockState();
                     }
                  }
               }
            }

            for (int var36 = 0; var36 < 32; var36++) {
               for (int var40 = 0; var40 < 32; var40++) {
                  if (var10[var36][var40]) {
                     int var44 = var8.readShort() & '\uffff';

                     for (int var46 = 0; var46 < var44; var46++) {
                        String var19 = var8.readUTF();
                        BlockUtils.stringToBlockRequired(var19);
                        ArrayList var20 = new ArrayList();
                        var12[var36][var40].put(var19, var20);
                        int var21 = var8.readShort() & '\uffff';
                        if (var21 == 0) {
                           var21 = 65536;
                        }

                        for (int var22 = 0; var22 < var21; var22++) {
                           byte var23 = var8.readByte();
                           int var24 = var23 & 15;
                           int var25 = var23 >>> 4 & 15;
                           int var26 = var8.readInt();
                           var20.add(new BlockPos(var24, var26 + this.dimension.minY(), var25));
                        }
                     }
                  }
               }
            }

            for (int var37 = 0; var37 < 32; var37++) {
               for (int var41 = 0; var41 < 32; var41++) {
                  if (var10[var37][var41]) {
                     var14[var37][var41] = var8.readLong();
                  }
               }
            }

            for (int var38 = 0; var38 < 32; var38++) {
               for (int var42 = 0; var42 < 32; var42++) {
                  if (var10[var38][var42]) {
                     int var45 = this.x;
                     int var47 = this.z;
                     int var48 = var38 + 32 * var45;
                     int var49 = var42 + 32 * var47;
                     this.chunks[var38][var42] = new CachedChunk(
                        var48, var49, this.dimension.height(), var11[var38][var42], var13[var38][var42], var12[var38][var42], var14[var38][var42]
                     );
                  }
               }
            }
         }

         this.removeExpired();
         this.hasUnsavedChanges = false;
         long var34 = System.nanoTime() / 1000000L;
         System.out.println("Loaded region successfully in " + (var34 - var4) + "ms");
      } catch (Exception var33) {
         var33.printStackTrace();
      }
   }

   public final synchronized void removeExpired() {
      long var1 = Baritone.settings().cachedChunksExpirySeconds.value;
      if (var1 >= 0L) {
         long var3 = System.currentTimeMillis();
         long var5 = var3 - var1 * 1000L;

         for (int var7 = 0; var7 < 32; var7++) {
            for (int var8 = 0; var8 < 32; var8++) {
               if (this.chunks[var7][var8] != null && this.chunks[var7][var8].cacheTimestamp < var5) {
                  System.out
                     .println(
                        "Removing chunk "
                           + (var7 + 32 * this.x)
                           + ","
                           + (var8 + 32 * this.z)
                           + " because it was cached "
                           + (var3 - this.chunks[var7][var8].cacheTimestamp) / 1000L
                           + " seconds ago, and max age is "
                           + var1
                     );
                  this.chunks[var7][var8] = null;
               }
            }
         }
      }
   }

   public final synchronized CachedChunk mostRecentlyModified() {
      CachedChunk var1 = null;

      for (int var2 = 0; var2 < 32; var2++) {
         for (int var3 = 0; var3 < 32; var3++) {
            if (this.chunks[var2][var3] != null && (var1 == null || this.chunks[var2][var3].cacheTimestamp > var1.cacheTimestamp)) {
               var1 = this.chunks[var2][var3];
            }
         }
      }

      return var1;
   }

   @Override
   public final int getX() {
      return this.x;
   }

   @Override
   public final int getZ() {
      return this.z;
   }

   private static Path getRegionFile(Path var0, int var1, int var2) {
      return Paths.get(var0.toString(), "r." + var1 + "." + var2 + ".bcr");
   }
}
