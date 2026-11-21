package wtf.onetap.module.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.annotation.EventTarget;
import wtf.onetap.annotation.ModuleInformation;
import wtf.onetap.event.RayTraceEvent;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;
import wtf.onetap.setting.FloatSetting;
import wtf.onetap.util.MinecraftAccess;

@ModuleInformation(name = "Air Place", description = "Ставит блок по лучу в воздухе, когда под курсором нет поверхности", category = Category.PLAYER)
public class AirPlace extends Module {
   private final FloatSetting distance = new FloatSetting("Дистанция", 4.0F, 1.0F, 4.0F, 0.1F).build();

   @EventTarget
   private void onRayTrace(RayTraceEvent event) {
      if (MinecraftAccess.MINECRAFT.player != null && MinecraftAccess.MINECRAFT.level != null && MinecraftAccess.MINECRAFT.options != null) {
         if (MinecraftAccess.MINECRAFT.options.keyUse.isDown()) {
            if (MinecraftAccess.MINECRAFT.player.getMainHandItem().getItem() instanceof BlockItem) {
               BlockHitResult blockHitResult = MinecraftAccess.MINECRAFT
                  .level
                  .clip(new ClipContext(event.getStart(), event.getEnd(), event.getBlock(), event.getFluid(), event.getEntity()));
               if (blockHitResult.getType() == Type.MISS) {
                  Vec3 vec3 = event.getStart();
                  Vec3 vec32 = event.getEnd();
                  Vec3 vec33 = vec32.subtract(vec3);
                  double doubleValue = vec33.length();
                  if (!(doubleValue <= 1.0E-6)) {
                     Vec3 vec34 = vec33.scale(1.0 / doubleValue);
                     Vec3 vec35 = vec3.add(vec34.scale(this.distance.getFloat()));
                     Direction direction = Direction.getApproximateNearest((float)(-vec34.x), (float)(-vec34.y), (float)(-vec34.z));
                     event.setResult(new BlockHitResult(vec35, direction, BlockPos.containing(vec35), false));
                  }
               }
            }
         }
      }
   }

   private static String b(byte[] byteArray) {
      int value = 0;
      int value2;
      char[] charArray = new char[value2 = byteArray.length];

      for (int i = 0; i < value2; i++) {
         int value3;
         if ((value3 = 255 & byteArray[i]) < 192) {
            charArray[value++] = (char)value3;
         } else if (value3 < 224) {
            char ch = (char)((char)(value3 & 31) << 6);
            byte byteValue = byteArray[++i];
            ch = (char)(ch | (char)(byteValue & 63));
            charArray[value++] = ch;
         } else if (i < value2 - 2) {
            char ch2 = (char)((char)(value3 & 15) << '\f');
            byte byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63) << 6);
            byteValue2 = byteArray[++i];
            ch2 = (char)(ch2 | (char)(byteValue2 & 63));
            charArray[value++] = ch2;
         }
      }

      return new String(charArray, 0, value);
   }
}
