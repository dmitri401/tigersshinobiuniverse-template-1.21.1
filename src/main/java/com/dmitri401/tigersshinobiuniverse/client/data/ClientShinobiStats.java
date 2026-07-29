package com.dmitri401.tigersshinobiuniverse.client.data;

import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiClan;

public final class ClientShinobiStats {

    private static SyncStatsPayload current = createDefault();

    private ClientShinobiStats() {
    }

    public static SyncStatsPayload get() {
        return current;
    }

    public static void update(SyncStatsPayload payload) {
        if (payload != null) {
            current = payload;
        }
    }

    public static void reset() {
        current = createDefault();
    }

    private static SyncStatsPayload createDefault() {
        return new SyncStatsPayload(
                false,
                ShinobiClan.CLANLESS.getId(),
                1,
                0,
                100,
                100,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                10,
                0
        );
    }
}