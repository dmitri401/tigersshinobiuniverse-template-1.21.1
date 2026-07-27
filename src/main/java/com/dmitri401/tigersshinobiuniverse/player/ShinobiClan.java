package com.dmitri401.tigersshinobiuniverse.player;

import java.util.Optional;

public enum ShinobiClan {

    CLANLESS(0, "Clanless"),
    UCHIHA(1, "Uchiha"),
    HYUGA(2, "Hyuga"),
    UZUMAKI(3, "Uzumaki");

    private final int id;
    private final String displayName;

    ShinobiClan(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<ShinobiClan> fromId(int id) {
        for (ShinobiClan clan : values()) {
            if (clan.id == id) {
                return Optional.of(clan);
            }
        }

        return Optional.empty();
    }

    public static ShinobiClan fromIdOrClanless(int id) {
        return fromId(id).orElse(CLANLESS);
    }
}