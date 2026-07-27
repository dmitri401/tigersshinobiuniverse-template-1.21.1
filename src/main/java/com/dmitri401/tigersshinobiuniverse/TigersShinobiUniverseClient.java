package com.dmitri401.tigersshinobiuniverse;

import com.dmitri401.tigersshinobiuniverse.client.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(
        value = TigersShinobiUniverse.MOD_ID,
        dist = Dist.CLIENT
)
@EventBusSubscriber(
        modid = TigersShinobiUniverse.MOD_ID,
        value = Dist.CLIENT
)
public class TigersShinobiUniverseClient {

    public TigersShinobiUniverseClient(
            IEventBus modEventBus,
            ModContainer container
    ) {
        // Adds the Config button on the Mods screen.
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                ConfigurationScreen::new
        );

        // Registers the key mappings on the client mod event bus.
        modEventBus.addListener(this::registerKeyMappings);

    }

    private void registerKeyMappings(
            RegisterKeyMappingsEvent event
    ) {
        event.register(ModKeyMappings.HAND_SIGN_1);
        event.register(ModKeyMappings.HAND_SIGN_2);
        event.register(ModKeyMappings.HAND_SIGN_3);
        event.register(ModKeyMappings.MENU);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        TigersShinobiUniverse.LOGGER.info(
                "Tiger's Shinobi Universe client setup started"
        );

        TigersShinobiUniverse.LOGGER.info(
                "Minecraft player name: {}",
                Minecraft.getInstance().getUser().getName()
        );
    }
}