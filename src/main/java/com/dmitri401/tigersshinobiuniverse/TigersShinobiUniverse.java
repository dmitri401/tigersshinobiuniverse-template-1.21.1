package com.dmitri401.tigersshinobiuniverse;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import com.dmitri401.tigersshinobiuniverse.attachment.ModAttachments;
import com.dmitri401.tigersshinobiuniverse.network.ModNetworking;
import com.dmitri401.tigersshinobiuniverse.event.WaterWalkingEvents;
import com.dmitri401.tigersshinobiuniverse.player.ChakraRegenEvents;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TigersShinobiUniverse.MOD_ID)
public class TigersShinobiUniverse {
    public static final String MOD_ID = "tigersshinobiuniverse";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TigersShinobiUniverse(IEventBus modEventBus) {
        ModAttachments.ATTACHMENTS.register(modEventBus);
        modEventBus.addListener(ModNetworking::registerPayloads);

        NeoForge.EVENT_BUS.register(WaterWalkingEvents.class);
        NeoForge.EVENT_BUS.addListener(
                ChakraRegenEvents::onPlayerTick
        );
    }
}
