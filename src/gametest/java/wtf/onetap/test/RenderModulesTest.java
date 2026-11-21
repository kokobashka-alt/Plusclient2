package wtf.onetap.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import wtf.onetap.Onetap;
import wtf.onetap.module.Category;
import wtf.onetap.module.Module;

/**
 * Enables every RENDER module one by one in a real world, renders some frames with it and disables it again.
 * Progress is written to build/render-test.log so that a crash can be attributed to a module.
 * ONETAP_TEST_FROM=<n> skips the first n modules, ONETAP_TEST_ONLY=<name> tests a single module.
 */
public class RenderModulesTest implements FabricClientGameTest {
	private static final Path LOG = Path.of("..", "build", "render-test.log").toAbsolutePath().normalize();

	private static void log(String line) {
		System.out.println("[RenderTest] " + line);
		try {
			Files.createDirectories(LOG.getParent());
			Files.writeString(LOG, line + System.lineSeparator(), StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException ignored) {
		}
	}

	@Override
	public void runTest(ClientGameTestContext ctx) {
		int from = Integer.parseInt(System.getenv().getOrDefault("ONETAP_TEST_FROM", "0"));
		String only = System.getenv("ONETAP_TEST_ONLY");
		List<Module> modules = Onetap.MODULE_MANAGER.getByCategory(Category.RENDER);
		log("=== " + modules.size() + " render modules, starting at " + from);

		try (TestSingleplayerContext sp = ctx.worldBuilder().create()) {
			sp.getServer().runCommand("gamemode creative @a");
			sp.getServer().runCommand("time set day");
			// husks do not burn in daylight; NoAI keeps them in front of the camera so entity based modules have targets
			sp.getServer().runCommand("summon minecraft:husk ~2 ~ ~4 {NoAI:1b,PersistenceRequired:1b,Invulnerable:0b,equipment:{head:{id:\"minecraft:iron_helmet\",count:1},mainhand:{id:\"minecraft:iron_sword\",count:1}}}");
			sp.getServer().runCommand("summon minecraft:husk ~-3 ~ ~5 {NoAI:1b,PersistenceRequired:1b,Health:200f,attributes:[{id:\"minecraft:max_health\",base:200}]}");
			sp.getServer().runCommand("summon minecraft:villager ~4 ~ ~6 {NoAI:1b}");
			sp.getServer().runCommand("summon minecraft:armor_stand ~ ~ ~3");
			sp.getServer().runCommand("summon minecraft:item ~1 ~1 ~2 {Item:{id:\"minecraft:diamond_sword\",count:1}}");
			sp.getServer().runCommand("summon minecraft:chest ~ ~ ~-3");
			sp.getServer().runCommand("setblock ~2 ~ ~-2 minecraft:chest");
			sp.getServer().runCommand("setblock ~-2 ~ ~-2 minecraft:spawner");
			ctx.waitTicks(40);

			for (int i = from; i < modules.size(); i++) {
				Module module = modules.get(i);
				String name = module.getName();
				if (only != null && !only.equalsIgnoreCase(name)) {
					continue;
				}
				log("[" + i + "] ENABLE " + name + " (" + module.getClass().getSimpleName() + ")");
				try {
					ctx.runOnClient(mc -> module.setEnabled(true));
					ctx.waitTicks(15);
					sp.getServer().runCommand("damage @e[type=minecraft:husk,limit=1,sort=nearest] 2");
					ctx.waitTicks(15);
					ctx.runOnClient(mc -> { if (mc.gui.screen() != null) mc.gui.setScreen(null); });
					ctx.takeScreenshot("render-" + i + "-" + name.replaceAll("[^A-Za-z0-9]", "_"));
					ctx.waitTicks(10);
				} catch (Throwable t) {
					log("[" + i + "] FAIL " + name + ": " + t);
				} finally {
					try {
						ctx.runOnClient(mc -> {
							module.setEnabled(false);
							if (mc.gui.screen() != null) mc.gui.setScreen(null);
						});
					} catch (Throwable t) {
						log("[" + i + "] FAIL disabling " + name + ": " + t);
					}
				}
				log("[" + i + "] OK " + name);
				ctx.waitTicks(5);
			}
		}
		log("=== done");
	}
}
