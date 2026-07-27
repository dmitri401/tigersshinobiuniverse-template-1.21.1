package com.dmitri401.tigersshinobiuniverse.client.screen;

import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import net.minecraft.client.Minecraft;

public final class ShinobiScreenRouter {

    private static boolean waitingToOpenMenu = false;

    private ShinobiScreenRouter() {
    }

    public static void requestMenuOpen() {
        waitingToOpenMenu = true;
    }

    public static void handleStatsSync(
            SyncStatsPayload stats
    ) {
        if (!waitingToOpenMenu) {
            return;
        }

        waitingToOpenMenu = false;

        Minecraft minecraft = Minecraft.getInstance();

        if (stats.isNinja()) {
            minecraft.setScreen(
                    new ShinobiStatsScreen()
            );
        } else {
            minecraft.setScreen(
                    new CharacterCreatorScreen()
            );
        }
    }
}