package com.dmitri401.tigersshinobiuniverse.client.network;

import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientStatsSyncBridge {

    private ClientStatsSyncBridge() {
    }

    public static void handle(
            SyncStatsPayload payload,
            IPayloadContext context
    ) {
        ClientPayloadHandlers.handleStatsSync(
                payload,
                context
        );
    }
}