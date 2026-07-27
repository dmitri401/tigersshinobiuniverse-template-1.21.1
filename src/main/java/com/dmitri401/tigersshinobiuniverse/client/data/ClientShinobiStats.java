package com.dmitri401.tigersshinobiuniverse.client.data;

import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiClan;

public final class ClientShinobiStats {

    private static SyncStatsPayload current = createDefault();

    private ClientShinobiStats() {
    }

    private static SyncStatsPayload createDefault() {
        return new SyncStatsPayload(
                false,                         // isNinja
                ShinobiClan.CLANLESS.getId(), // clan ID
                1,                             // level
                0,                             // ninja experience
                100,                           // chakra
                100,                           // max chakra
                1,                             // ninjutsu
                1,                             // taijutsu
                1,                             // genjutsu
                1,                             // strength
                1,                             // agility
                1,                             // vitality
                0                              // stat points
        );
    }

    public static SyncStatsPayload get() {
        return current;
    }

    public static void update(SyncStatsPayload payload) {
        current = payload;
    }

    public static void reset() {
        current = createDefault();
    }
}