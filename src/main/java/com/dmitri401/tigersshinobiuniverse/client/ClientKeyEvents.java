package com.dmitri401.tigersshinobiuniverse.client;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import com.dmitri401.tigersshinobiuniverse.client.screen.ShinobiStatsScreen;
import com.dmitri401.tigersshinobiuniverse.network.payload.RequestStatsPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import com.dmitri401.tigersshinobiuniverse.client.screen.ShinobiScreenRouter;
import com.dmitri401.tigersshinobiuniverse.network.payload.RequestStatsPayload;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
        modid = TigersShinobiUniverse.MOD_ID,
        value = Dist.CLIENT
)
public final class ClientKeyEvents {

    private ClientKeyEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        while (ModKeyMappings.HAND_SIGN_1.consumeClick()) {
            showMessage(minecraft, "Hand Sign 1");
        }

        while (ModKeyMappings.HAND_SIGN_2.consumeClick()) {
            showMessage(minecraft, "Hand Sign 2");
        }

        while (ModKeyMappings.HAND_SIGN_3.consumeClick()) {
            showMessage(minecraft, "Hand Sign 3");
        }

        while (ModKeyMappings.MENU.consumeClick()) {
            ShinobiScreenRouter.requestMenuOpen();

            PacketDistributor.sendToServer(
                    new RequestStatsPayload()
            );
        }
    }

    private static void showMessage(
            Minecraft minecraft,
            String message
    ) {
        minecraft.player.displayClientMessage(
                Component.literal(message),
                true
        );
    }
}