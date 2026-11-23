package wtf.onetap.combat.rotation;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class ShardbetaAuraInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(c -> ShardbetaAuraPipeline.tick());
    }
}
