package com.dmitri401.tigersshinobiuniverse.network;

import com.dmitri401.tigersshinobiuniverse.client.network.ClientPayloadHandlers;
import com.dmitri401.tigersshinobiuniverse.network.payload.CompleteCharacterCreationPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.IncreaseStatPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.RequestStatsPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStatService;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {

    private ModNetworking() {
    }

    public static void registerPayloads(
            RegisterPayloadHandlersEvent event
    ) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                SyncStatsPayload.TYPE,
                SyncStatsPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleStatsSync
        );

        registrar.playToServer(
                RequestStatsPayload.TYPE,
                RequestStatsPayload.STREAM_CODEC,
                ModNetworking::handleStatsRequest
        );

        registrar.playToServer(
                IncreaseStatPayload.TYPE,
                IncreaseStatPayload.STREAM_CODEC,
                ModNetworking::handleIncreaseStat
        );

        registrar.playToServer(
                CompleteCharacterCreationPayload.TYPE,
                CompleteCharacterCreationPayload.STREAM_CODEC,
                ModNetworking::handleCompleteCharacterCreation
        );
    }

    private static void handleStatsRequest(
            RequestStatsPayload payload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            ShinobiStatService.sync(serverPlayer);
        }
    }

    private static void handleIncreaseStat(
            IncreaseStatPayload payload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            ShinobiStatService.increaseStat(
                    serverPlayer,
                    payload.statId()
            );
        }
    }

    private static void handleCompleteCharacterCreation(
            CompleteCharacterCreationPayload payload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            ShinobiStatService.completeCharacterCreation(
                    serverPlayer,
                    payload.clanId()
            );
        }
    }
}