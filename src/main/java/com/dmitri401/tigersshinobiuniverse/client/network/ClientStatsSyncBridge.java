package com.dmitri401.tigersshinobiuniverse.client.network;

import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientStatsSyncBridge {

    private static volatile boolean ninja;
    private static volatile int chakra;
    private static volatile int maxChakra = 1;

    private ClientStatsSyncBridge() {
    }

    public static void handle(
            SyncStatsPayload payload,
            IPayloadContext context
    ) {
        /*
         * Cache the values needed by the HUD as soon as the server
         * sends a SyncStatsPayload.
         */
        ninja = payload.isNinja();
        chakra = Math.max(0, payload.chakra());
        maxChakra = Math.max(1, payload.maxChakra());

        /*
         * Keep the existing handler so the rest of your client UI
         * continues receiving all synchronized shinobi stats.
         */
        ClientPayloadHandlers.handleStatsSync(
                payload,
                context
        );
    }

    public static boolean isNinja() {
        return ninja;
    }

    public static int getChakra() {
        return chakra;
    }

    public static int getMaxChakra() {
        return maxChakra;
    }
}
