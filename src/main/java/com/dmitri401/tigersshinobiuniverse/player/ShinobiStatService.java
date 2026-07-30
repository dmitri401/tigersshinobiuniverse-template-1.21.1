package com.dmitri401.tigersshinobiuniverse.player;

import com.dmitri401.tigersshinobiuniverse.attachment.ModAttachments;
import com.dmitri401.tigersshinobiuniverse.network.payload.IncreaseStatPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncChakraPayload;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ShinobiStatService {

    /*
     * IDs for the two new basic stats. Existing IDs remain compatible:
     * STRENGTH now increases Defense.
     */
    public static final int CHAKRA_STAT_ID = 6;
    public static final int CHAKRA_CONTROL_STAT_ID = 7;

    private static final int CHARGE_INTERVAL_TICKS = 20;
    private static final int CHAKRA_PER_CHARGE = 4;

    /*
     * Server-side rate limiting. This prevents modified clients from
     * bypassing the client packet throttle.
     */
    private static final Map<UUID, Integer> LAST_CHARGE_TICK =
            new ConcurrentHashMap<>();

    private ShinobiStatService() {
    }

    public static ShinobiStats get(ServerPlayer player) {
        return player.getData(ModAttachments.SHINOBI_STATS);
    }

    public static void sync(ServerPlayer player) {
        ShinobiStats stats = get(player);

        PacketDistributor.sendToPlayer(
                player,
                SyncStatsPayload.from(stats)
        );
    }

    public static void syncChakra(ServerPlayer player) {
        ShinobiStats stats = get(player);

        PacketDistributor.sendToPlayer(
                player,
                new SyncChakraPayload(
                        stats.getChakra(),
                        stats.getMaxChakra()
                )
        );
    }

    public static boolean increaseStat(
            ServerPlayer player,
            int statId
    ) {
        ShinobiStats stats = get(player);

        if (stats.getStatPoints() <= 0) {
            return false;
        }

        switch (statId) {
            case IncreaseStatPayload.NINJUTSU ->
                    stats.setNinjutsu(stats.getNinjutsu() + 1);

            case IncreaseStatPayload.TAIJUTSU ->
                    stats.setTaijutsu(stats.getTaijutsu() + 1);

            case IncreaseStatPayload.GENJUTSU ->
                    stats.setGenjutsu(stats.getGenjutsu() + 1);

            case IncreaseStatPayload.STRENGTH ->
                    stats.setDefense(stats.getDefense() + 1);

            case IncreaseStatPayload.AGILITY ->
                    stats.setAgility(stats.getAgility() + 1);

            case IncreaseStatPayload.VITALITY ->
                    stats.setVitality(stats.getVitality() + 1);

            case CHAKRA_STAT_ID -> {
                stats.setMaxChakra(stats.getMaxChakra() + 10);
                stats.setChakra(stats.getChakra() + 10);
            }

            case CHAKRA_CONTROL_STAT_ID ->
                    stats.setChakraControl(
                            stats.getChakraControl() + 1
                    );

            default -> {
                return false;
            }
        }

        stats.setStatPoints(stats.getStatPoints() - 1);
        BasicStatEffects.apply(player);
        sync(player);

        return true;
    }

    public static boolean completeCharacterCreation(
            ServerPlayer player,
            int requestedClanId
    ) {
        ShinobiStats stats = get(player);

        if (stats.isNinja()) {
            return false;
        }

        ShinobiClan selectedClan =
                ShinobiClan.fromId(requestedClanId)
                        .orElse(null);

        if (selectedClan == null) {
            return false;
        }

        boolean completed =
                stats.completeCharacterCreation(selectedClan);

        if (completed) {
            BasicStatEffects.apply(player);
            sync(player);
        }

        return completed;
    }


    public static boolean chargeChakra(ServerPlayer player) {
        ShinobiStats stats = get(player);

        if (!stats.isNinja()) {
            return false;
        }

        int currentTick = player.tickCount;
        Integer previousTick =
                LAST_CHARGE_TICK.get(player.getUUID());

        if (previousTick != null) {
            int elapsed = currentTick - previousTick;

            /*
             * Also allow charging after a relog, where the entity tick
             * counter may be lower than its previous value.
             */
            if (elapsed >= 0
                    && elapsed < CHARGE_INTERVAL_TICKS) {
                return false;
            }
        }

        LAST_CHARGE_TICK.put(
                player.getUUID(),
                currentTick
        );

        boolean restored =
                stats.restoreChakra(CHAKRA_PER_CHARGE);

        if (restored) {
            syncChakra(player);
        }

        return restored;
    }


    public static boolean consumeChakra(
            ServerPlayer player,
            int amount
    ) {
        ShinobiStats stats = get(player);

        boolean consumed = stats.consumeChakra(amount);

        if (consumed) {
            syncChakra(player);
        }

        return consumed;
    }
}