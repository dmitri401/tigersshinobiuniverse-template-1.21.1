package com.dmitri401.tigersshinobiuniverse.jutsu;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.config.JutsuConfig;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStatService;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStats;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-authoritative hand-sign chain collector.
 */
@EventBusSubscriber(modid = TigersShinobiUniverse.MOD_ID)
public final class HandSignChainService {

    private static final Map<UUID, ChainState> ACTIVE_CHAINS =
            new ConcurrentHashMap<>();

    private HandSignChainService() {
    }

    public static void enterSign(
            ServerPlayer player,
            int sign
    ) {
        if (sign != 1 && sign != 2) {
            return;
        }

        ShinobiStats stats = ShinobiStatService.get(player);

        if (!stats.isNinja()) {
            return;
        }

        ChainState state = ACTIVE_CHAINS.computeIfAbsent(
                player.getUUID(),
                ignored -> new ChainState()
        );

        if (player.tickCount - state.lastInputTick
                > JutsuConfig.HAND_SIGN_TIMEOUT_TICKS) {
            state.reset();
        }

        state.signs[state.count] = sign;
        state.count++;
        state.lastInputTick = player.tickCount;

        if (state.count < JutsuSlots.SEQUENCE_LENGTH) {
            return;
        }

        int slot = JutsuSlots.getSlotForSequence(
                state.signs[0],
                state.signs[1],
                state.signs[2]
        );

        state.reset();
        activateSlot(player, slot);
    }

    private static void activateSlot(
            ServerPlayer player,
            int slot
    ) {
        String jutsuName =
                JutsuSlots.getAssignedJutsuName(slot);

        if ("Empty".equals(jutsuName)) {
            player.displayClientMessage(
                    Component.literal(
                            "Jutsu Slot "
                                    + slot
                                    + " is empty ("
                                    + JutsuSlots.getSequenceText(slot)
                                    + ")"
                    ),
                    true
            );
            return;
        }

        /*
         * Future jutsu activation point:
         * JutsuRegistry.activate(player, jutsuName);
         */
        player.displayClientMessage(
                Component.literal(
                        "Activated " + jutsuName
                ),
                true
        );
    }

    @SubscribeEvent
    public static void onLogout(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {
        ACTIVE_CHAINS.remove(
                event.getEntity().getUUID()
        );
    }

    @SubscribeEvent
    public static void onChangedDimension(
            PlayerEvent.PlayerChangedDimensionEvent event
    ) {
        ACTIVE_CHAINS.remove(
                event.getEntity().getUUID()
        );
    }

    @SubscribeEvent
    public static void onRespawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        ACTIVE_CHAINS.remove(
                event.getEntity().getUUID()
        );
    }

    private static final class ChainState {

        private final int[] signs =
                new int[JutsuSlots.SEQUENCE_LENGTH];

        private int count;
        private int lastInputTick = Integer.MIN_VALUE;

        private void reset() {
            count = 0;
            lastInputTick = Integer.MIN_VALUE;
        }
    }
}
