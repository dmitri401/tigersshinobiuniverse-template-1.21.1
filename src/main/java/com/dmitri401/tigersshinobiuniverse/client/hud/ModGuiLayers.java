package com.dmitri401.tigersshinobiuniverse.client.hud;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public final class ModGuiLayers {

    private static final ResourceLocation SHINOBI_HUD =
            ResourceLocation.fromNamespaceAndPath(
                    TigersShinobiUniverse.MOD_ID,
                    "shinobi_hud"
            );

    private ModGuiLayers() {
    }

    public static void registerGuiLayers(
            RegisterGuiLayersEvent event
    ) {
        event.registerAboveAll(
                SHINOBI_HUD,
                ChakraHud::render
        );
    }
}