package com.dmitri401.tigersshinobiuniverse.client;

import com.dmitri401.tigersshinobiuniverse.Config;
import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(
        modid = TigersShinobiUniverse.MOD_ID,
        value = Dist.CLIENT
)
public final class ClientKeyEvents {

    private ClientKeyEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(
            ClientTickEvent.Post event
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        if (minecraft.level == null) {
            return;
        }

        while (ModKeyMappings.ACTIVATE_JUTSU.consumeClick()) {
            activateTestJutsu(minecraft);
        }
    }

    private static void activateTestJutsu(
            Minecraft minecraft
    ) {
        if (Config.SHOW_JUTSU_TEST_MESSAGE.get()) {
            minecraft.player.displayClientMessage(
                    Component.literal(
                            "Jutsu button pressed!"
                    ),
                    true
            );
        }
    }
}