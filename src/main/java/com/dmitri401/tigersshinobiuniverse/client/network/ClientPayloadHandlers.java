package com.dmitri401.tigersshinobiuniverse.client.network;

import com.dmitri401.tigersshinobiuniverse.client.data.ClientShinobiStats;
import com.dmitri401.tigersshinobiuniverse.client.screen.CharacterCreatorScreen;
import com.dmitri401.tigersshinobiuniverse.client.screen.ShinobiScreenRouter;
import com.dmitri401.tigersshinobiuniverse.client.screen.ShinobiStatsScreen;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPayloadHandlers {

    private ClientPayloadHandlers() {
    }

    public static void handleStatsSync(
            SyncStatsPayload payload,
            IPayloadContext context
    ) {
        ClientShinobiStats.update(payload);
        ShinobiScreenRouter.handleStatsSync(payload);

        Minecraft minecraft = Minecraft.getInstance();

        if (payload.isNinja()
                && minecraft.screen instanceof CharacterCreatorScreen) {
            minecraft.setScreen(
                    new ShinobiStatsScreen()
            );
        }
    }
}