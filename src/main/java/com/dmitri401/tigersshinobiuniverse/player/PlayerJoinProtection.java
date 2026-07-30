package com.dmitri401.tigersshinobiuniverse.player;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import gravitychanger.api.GravityChangerAPI;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Prevents players from taking accidental fall or collision damage while
 * their chunks, gravity direction, and shinobi attributes settle after login.
 */
@EventBusSubscriber(modid = TigersShinobiUniverse.MOD_ID)
public final class PlayerJoinProtection {

    private static final int PROTECTION_TICKS = 40;
    private static final int APPLY_STATS_AFTER_TICKS = 5;

    private static final Map<UUID, Integer> PROTECTED_PLAYERS =
            new ConcurrentHashMap<>();

    private PlayerJoinProtection() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(
            PlayerEvent.PlayerLoggedInEvent event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PROTECTED_PLAYERS.put(
                player.getUUID(),
                PROTECTION_TICKS
        );

        resetUnsafeMovementState(player);

        /*
         * Vanilla invulnerability time protects against brief collision,
         * suffocation, and other damage while the player finishes loading.
         */
        player.invulnerableTime = Math.max(
                player.invulnerableTime,
                PROTECTION_TICKS
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(
            PlayerTickEvent.Post event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Integer remaining =
                PROTECTED_PLAYERS.get(player.getUUID());

        if (remaining == null) {
            return;
        }

        /*
         * Keep fall distance cleared throughout the protection window.
         * Gravity is only forced downward at the beginning so legitimate
         * gravity changes after login are not continuously overwritten.
         */
        player.resetFallDistance();
        player.fallDistance = 0.0F;

        int elapsed = PROTECTION_TICKS - remaining;

        if (elapsed == APPLY_STATS_AFTER_TICKS) {
            BasicStatEffects.apply(player);
            ShinobiStatService.sync(player);
        }

        if (remaining <= 1) {
            PROTECTED_PLAYERS.remove(player.getUUID());
            return;
        }

        PROTECTED_PLAYERS.put(
                player.getUUID(),
                remaining - 1
        );
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {
        PROTECTED_PLAYERS.remove(
                event.getEntity().getUUID()
        );
    }

    @SubscribeEvent
    public static void onPlayerRespawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PROTECTED_PLAYERS.put(
                player.getUUID(),
                PROTECTION_TICKS
        );

        resetUnsafeMovementState(player);

        player.invulnerableTime = Math.max(
                player.invulnerableTime,
                PROTECTION_TICKS
        );
    }

    private static void resetUnsafeMovementState(
            ServerPlayer player
    ) {
        if (GravityChangerAPI.canChangeGravity(player)) {
            GravityChangerAPI.resetGravity(player);
        }

        player.resetFallDistance();
        player.fallDistance = 0.0F;
    }
}
