package com.dmitri401.tigersshinobiuniverse.event;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStatService;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(
        modid = TigersShinobiUniverse.MOD_ID
)
public final class PlayerStatEvents {

    private PlayerStatEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(
            PlayerEvent.PlayerLoggedInEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ShinobiStatService.sync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ShinobiStatService.sync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(
            PlayerEvent.PlayerChangedDimensionEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ShinobiStatService.sync(serverPlayer);
        }
    }
}