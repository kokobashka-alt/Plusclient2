package baritone.utils.schematic.litematica;

import baritone.api.schematic.CompositeSchematic;
import baritone.api.schematic.ISchematic;
import baritone.api.schematic.IStaticSchematic;
import baritone.api.utils.Tuple;
import baritone.utils.schematic.StaticSchematic;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import wtf.onetap.obf.C5_j;
import wtf.onetap.obf.C5k;
import wtf.onetap.obf.C6;
import wtf.onetap.obf.C80;
import wtf.onetap.obf.C8;
import wtf.onetap.obf.C8u;
import wtf.onetap.obf.w;

public class LitematicaHelper {
   public static boolean isLitematicaPresent() {
      try {
         Class.forName(w.a(C6.class.getName()));
         return true;
      } catch (ClassNotFoundException | NoClassDefFoundError var1) {
         return false;
      }
   }

   public static boolean hasLoadedSchematic(int var0) {
      return 0 <= var0 && var0 < C80.m_2_C6q().m_0_List().size();
   }

   private static C5_j getPlacement(int var0) {
      return C80.m_2_C6q().m_0_List().get(var0);
   }

   private static Vec3i transform(Vec3i var0, Mirror var1, Rotation var2) {
      int var3 = var0.getX();
      int var4 = var0.getZ();
      if (var1 == Mirror.LEFT_RIGHT) {
         var4 = -var4;
      } else if (var1 == Mirror.FRONT_BACK) {
         var3 = -var3;
      }

      switch (var2) {
         case CLOCKWISE_90:
            return new Vec3i(-var4, var0.getY(), var3);
         case CLOCKWISE_180:
            return new Vec3i(-var3, var0.getY(), -var4);
         case COUNTERCLOCKWISE_90:
            return new Vec3i(var4, var0.getY(), -var3);
         default:
            return new Vec3i(var3, var0.getY(), var4);
      }
   }

   public static Tuple<IStaticSchematic, Vec3i> getSchematic(int var0) {
      C5_j var1 = getPlacement(var0);
      int var2 = Integer.MAX_VALUE;
      int var3 = Integer.MAX_VALUE;
      int var4 = Integer.MAX_VALUE;
      HashMap var5 = new HashMap();
      C8 var6 = C8u.m_2_C8();
      UnmodifiableIterator var7 = var1.m_0_ImmutableMap().entrySet().iterator();

      while (var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         C5k var9 = (C5k)var8.getValue();
         Vec3i var10 = transform(var9.m_4_BlockPos(), var1.m_1_Mirror(), var1.m_4_Rotation());
         Vec3i var11 = var1.m_4_C63().m_8_BlockPos((String)var8.getKey());
         var11 = transform(var11, var1.m_1_Mirror(), var1.m_4_Rotation());
         var11 = transform(var11, var9.m_2_Mirror(), var9.m_9_Rotation());
         int var12 = Math.min(var11.getX() + 1, 0);
         int var13 = Math.min(var11.getY() + 1, 0);
         int var14 = Math.min(var11.getZ() + 1, 0);
         var2 = Math.min(var2, var10.getX() + var12);
         var3 = Math.min(var3, var10.getY() + var13);
         var4 = Math.min(var4, var10.getZ() + var14);
         BlockPos var15 = var1.m_0_BlockPos().offset(var10).offset(var12, var13, var14);
         BlockState[][][] var16 = new BlockState[Math.abs(var11.getX())][Math.abs(var11.getZ())][Math.abs(var11.getY())];

         for (int var17 = 0; var17 < var16.length; var17++) {
            for (int var18 = 0; var18 < var16[var17].length; var18++) {
               for (int var19 = 0; var19 < var16[var17][var18].length; var19++) {
                  var16[var17][var18][var19] = var6.getBlockState(var15.offset(var17, var19, var18));
               }
            }
         }

         StaticSchematic var26 = new StaticSchematic(var16);
         var5.put(var10.offset(var12, var13, var14), var26);
      }

      LitematicaHelper.LitematicaPlacementSchematic var20 = new LitematicaHelper.LitematicaPlacementSchematic(var1.m_6_String());

      for (Entry var22 : (Iterable<Entry>)var5.entrySet()) {
         Vec3i var23 = ((Vec3i)var22.getKey()).offset(-var2, -var3, -var4);
         var20.put((ISchematic)var22.getValue(), var23.getX(), var23.getY(), var23.getZ());
      }

      return new Tuple<>(var20, var1.m_0_BlockPos().offset(var2, var3, var4));
   }

   private static class LitematicaPlacementSchematic extends CompositeSchematic implements IStaticSchematic {
      private final String name;

      public LitematicaPlacementSchematic(String var1) {
         super(0, 0, 0);
         this.name = var1;
      }

      @Override
      public BlockState getDirect(int var1, int var2, int var3) {
         return this.inSchematic(var1, var2, var3, null) ? this.desiredState(var1, var2, var3, null, Collections.emptyList()) : null;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
