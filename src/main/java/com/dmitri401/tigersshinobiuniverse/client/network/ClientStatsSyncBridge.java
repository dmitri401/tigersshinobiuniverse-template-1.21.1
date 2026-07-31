package com.dmitri401.tigersshinobiuniverse.client.network;

import com.dmitri401.tigersshinobiuniverse.client.data.ClientShinobiStats;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncChakraPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Lightweight client cache used by movement checks and the HUD.
 */
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
        ninja = payload.isNinja();
        chakra = Math.max(0, payload.chakra());
        maxChakra = Math.max(1, payload.maxChakra());

        ClientPayloadHandlers.handleStatsSync(
                payload,
                context
        );
    }

    public static void handleChakra(
            SyncChakraPayload payload,
            IPayloadContext context
    ) {
        maxChakra = Math.max(1, payload.maxChakra());
        chakra = Math.max(
                0,
                Math.min(payload.chakra(), maxChakra)
        );

        ClientShinobiStats.updateChakra(
                chakra,
                maxChakra
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

    public static void reset() {
        ninja = false;
        chakra = 0;
        maxChakra = 1;
        ClientShinobiStats.reset();
    }
}
