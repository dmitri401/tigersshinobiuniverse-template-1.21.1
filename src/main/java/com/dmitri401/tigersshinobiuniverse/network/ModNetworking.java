package com.dmitri401.tigersshinobiuniverse.network;

import com.dmitri401.tigersshinobiuniverse.client.network.ClientStatsSyncBridge;
import com.dmitri401.tigersshinobiuniverse.network.payload.CompleteCharacterCreationPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.ChargeChakraPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.IncreaseStatPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.NinjaJumpPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.WallRunSelectPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.WallRunResetPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.RequestStatsPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncChakraPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.HandSignInputPayload;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStatService;
import com.dmitri401.tigersshinobiuniverse.player.NinjaJumpService;
import com.dmitri401.tigersshinobiuniverse.player.WallRunService;
import com.dmitri401.tigersshinobiuniverse.jutsu.HandSignChainService;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {

    private ModNetworking() {
    }

    public static void registerPayloads(
            RegisterPayloadHandlersEvent event
    ) {
        PayloadRegistrar registrar = event.registrar("2");

        registrar.playToClient(
                SyncStatsPayload.TYPE,
                SyncStatsPayload.STREAM_CODEC,
                ModNetworking::handleStatsSync
        );

        registrar.playToClient(
                SyncChakraPayload.TYPE,
                SyncChakraPayload.STREAM_CODEC,
                ModNetworking::handleChakraSync
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

        registrar.playToServer(
                ChargeChakraPayload.TYPE,
                ChargeChakraPayload.STREAM_CODEC,
                ModNetworking::handleChargeChakra
        );


        registrar.playToServer(
                NinjaJumpPayload.TYPE,
                NinjaJumpPayload.STREAM_CODEC,
                ModNetworking::handleNinjaJump
        );

        registrar.playToServer(
                WallRunSelectPayload.TYPE,
                WallRunSelectPayload.STREAM_CODEC,
                ModNetworking::handleWallRunSelect
        );

        registrar.playToServer(
                WallRunResetPayload.TYPE,
                WallRunResetPayload.STREAM_CODEC,
                ModNetworking::handleWallRunReset
        );

        registrar.playToServer(
                HandSignInputPayload.TYPE,
                HandSignInputPayload.STREAM_CODEC,
                ModNetworking::handleHandSignInput
        );
    }

    private static void handleStatsSync(
            SyncStatsPayload payload,
            IPayloadContext context
    ) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientStatsSyncBridge.handle(
                    payload,
                    context
            );
        }
    }

    private static void handleChakraSync(
            SyncChakraPayload payload,
            IPayloadContext context
    ) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientStatsSyncBridge.handleChakra(
                    payload,
                    context
            );
        }
    }

    private static void handleStatsRequest(
            RequestStatsPayload ignoredPayload,
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
    private static void handleChargeChakra(
            ChargeChakraPayload ignoredPayload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            ShinobiStatService.chargeChakra(serverPlayer);
        }
    }


    private static void handleNinjaJump(
            NinjaJumpPayload payload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            NinjaJumpService.setJumpHeld(
                    serverPlayer,
                    payload.held()
            );
        }
    }


    private static void handleWallRunSelect(
            WallRunSelectPayload payload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            WallRunService.selectSurface(
                    serverPlayer,
                    payload.blockPos(),
                    payload.face()
            );
        }
    }


    private static void handleWallRunReset(
            WallRunResetPayload ignoredPayload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            WallRunService.reset(serverPlayer);
        }
    }


    private static void handleHandSignInput(
            HandSignInputPayload payload,
            IPayloadContext context
    ) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            HandSignChainService.enterSign(
                    serverPlayer,
                    payload.sign()
            );
        }
    }

}
