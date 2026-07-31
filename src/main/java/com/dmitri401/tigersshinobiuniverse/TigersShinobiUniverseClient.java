package com.dmitri401.tigersshinobiuniverse;

import com.dmitri401.tigersshinobiuniverse.client.ModKeyMappings;
import com.dmitri401.tigersshinobiuniverse.client.hud.ModGuiLayers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(
        value = TigersShinobiUniverse.MOD_ID,
        dist = Dist.CLIENT
)
public class TigersShinobiUniverseClient {

    public TigersShinobiUniverseClient(IEventBus modEventBus) {
        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(ModGuiLayers::registerGuiLayers);
        modEventBus.addListener(this::onClientSetup);
    }

    private void registerKeyMappings(
            RegisterKeyMappingsEvent event
    ) {
        event.register(ModKeyMappings.HAND_SIGN_1);
        event.register(ModKeyMappings.HAND_SIGN_2);
        event.register(ModKeyMappings.CHARGE);
        event.register(ModKeyMappings.MENU);
        event.register(ModKeyMappings.WALL_RUN);
        event.register(ModKeyMappings.ALT);
    }

    private void onClientSetup(
            FMLClientSetupEvent event
    ) {
        TigersShinobiUniverse.LOGGER.info(
                "Tiger's Shinobi Universe client setup started"
        );
    }
}
