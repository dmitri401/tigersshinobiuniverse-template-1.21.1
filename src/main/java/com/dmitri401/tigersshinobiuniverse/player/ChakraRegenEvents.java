package com.dmitri401.tigersshinobiuniverse.player;

import com.dmitri401.tigersshinobiuniverse.attachment.ModAttachments;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ChakraRegenEvents {

    /*
     * Minecraft normally runs at 20 ticks per second.
     *
     * 20 ticks = restore once per second.
     * 40 ticks = restore once every two seconds.
     */
    private static final int REGEN_INTERVAL_TICKS = 40;

    /*
     * Amount restored each interval.
     */
    private static final int REGEN_AMOUNT = 1;

    private ChakraRegenEvents() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % REGEN_INTERVAL_TICKS != 0) {
            return;
        }

        ShinobiStats stats = player.getData(
                ModAttachments.SHINOBI_STATS
        );

        if (!stats.isNinja()) {
            return;
        }

        if (stats.getChakra() >= stats.getMaxChakra()) {
            return;
        }

        stats.setChakra(
                Math.min(
                        stats.getChakra() + REGEN_AMOUNT,
                        stats.getMaxChakra()
                )
        );

        PacketDistributor.sendToPlayer(
                player,
                SyncStatsPayload.from(stats)
        );
    }
}