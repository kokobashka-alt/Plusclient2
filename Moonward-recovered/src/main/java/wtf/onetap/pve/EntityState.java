package wtf.onetap.pve;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import wtf.onetap.Onetap;
import wtf.onetap.util.MinecraftAccess;
import wtf.onetap.util.PlayerUtil;

public class EntityState implements MinecraftAccess {
   private EntityState() {
   }

   public static JsonObject m_1_JsonObject(Player player) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("id", player.getId());
      jsonObject.addProperty("x", player.getX());
      jsonObject.addProperty("y", player.getY());
      jsonObject.addProperty("z", player.getZ());
      jsonObject.addProperty("eh", player.getEyeHeight(player.getPose()));
      jsonObject.addProperty("w", player.getBbWidth());
      jsonObject.addProperty("h", player.getBbHeight());
      Vec3 vec3 = player.getDeltaMovement();
      CombatPlanner.m_4_String();
      jsonObject.addProperty("vx", vec3.x);
      jsonObject.addProperty("vy", vec3.y);
      jsonObject.addProperty("vz", vec3.z);
      jsonObject.addProperty("yaw", Onetap.ROTATION_MODES.getFloatValue());
      jsonObject.addProperty("pitch", Onetap.ROTATION_MODES.getFloatValue2());
      jsonObject.addProperty("cy", Onetap.ROTATION_MODES.getRenderYaw());
      jsonObject.addProperty("cp", Onetap.ROTATION_MODES.getRenderPitch());
      jsonObject.addProperty("og", player.onGround());
      jsonObject.addProperty("ff", player.isFallFlying());
      jsonObject.addProperty("sp", player.isSprinting());
      if (MinecraftAccess.MINECRAFT.player != null && player == MinecraftAccess.MINECRAFT.player) {
         Input input = MinecraftAccess.MINECRAFT.player.input.keyPresses;
         jsonObject.addProperty("f", input.forward());
         jsonObject.addProperty("l", input.left());
         jsonObject.addProperty("r", input.right());
         jsonObject.addProperty("j", input.jump());
      }

      jsonObject.addProperty("sens", MinecraftAccess.MINECRAFT.options.sensitivity().get().floatValue());
      jsonObject.addProperty("tc", player.tickCount);
      jsonObject.addProperty("ast", Math.round(player.getAttackStrengthScale(0.0F) * player.getCurrentItemAttackStrengthDelay()));
      jsonObject.addProperty("asd", player.getCurrentItemAttackStrengthDelay());
      jsonObject.addProperty("br", player.blockInteractionRange());
      jsonObject.addProperty("er", player.entityInteractionRange());
      jsonObject.addProperty("sel", player.getInventory().getSelectedSlot());
      jsonObject.addProperty("fps", MinecraftAccess.MINECRAFT.getFps());
      return jsonObject;
   }

   public static JsonObject m_9(LocalPlayer localPlayer) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", localPlayer.getX());
      jsonObject.addProperty("y", localPlayer.getY());
      jsonObject.addProperty("z", localPlayer.getZ());
      jsonObject.addProperty("eh", localPlayer.getEyeHeight(localPlayer.getPose()));
      Vec3 vec3 = localPlayer.getDeltaMovement();
      jsonObject.addProperty("vx", vec3.x);
      jsonObject.addProperty("vy", vec3.y);
      jsonObject.addProperty("vz", vec3.z);
      jsonObject.addProperty("og", localPlayer.onGround());
      jsonObject.addProperty("ff", localPlayer.isFallFlying());
      jsonObject.addProperty("sp", localPlayer.isSprinting());
      Input input = localPlayer.input.keyPresses;
      jsonObject.addProperty("f", input.forward());
      jsonObject.addProperty("l", input.left());
      jsonObject.addProperty("r", input.right());
      jsonObject.addProperty("j", input.jump());
      PlayerUtil playerUtil = PlayerUtil.fromLocalPlayer(PlayerUtil.StateHolder.m_6(input));
      playerUtil.tick();
      jsonObject.add("p1", toJsonArray(playerUtil.vec3));
      jsonObject.addProperty("sel", localPlayer.getInventory().getSelectedSlot());
      return jsonObject;
   }

   public static JsonArray m_2_JsonArray(AABB aABB) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(aABB.minX);
      jsonArray.add(aABB.minY);
      jsonArray.add(aABB.minZ);
      jsonArray.add(aABB.maxX);
      jsonArray.add(aABB.maxY);
      jsonArray.add(aABB.maxZ);
      return jsonArray;
   }

   public static JsonArray toJsonArray(Vec3 vec3) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(vec3.x);
      jsonArray.add(vec3.y);
      jsonArray.add(vec3.z);
      return jsonArray;
   }
}
