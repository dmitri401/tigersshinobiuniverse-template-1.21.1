package com.dmitri401.tigersshinobiuniverse.player;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Applies basic stats to vanilla Minecraft attributes.
 * Permanent-stat changes call apply immediately; the tick event is only
 * a low-frequency safety check.
 */
@EventBusSubscriber(modid = TigersShinobiUniverse.MOD_ID)
public final class BasicStatEffects {

    private BasicStatEffects() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.tickCount % 100 == 0) {
            apply(player);
        }
    }

    public static void apply(ServerPlayer player) {
        ShinobiStats stats = ShinobiStatService.get(player);

        if (!stats.isNinja()) {
            return;
        }

        AttributeInstance maxHealth =
                player.getAttribute(Attributes.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        double desiredHealthPoints =
                stats.getMaximumHealthPoints();

        if (Math.abs(
                maxHealth.getBaseValue() - desiredHealthPoints
        ) > 0.001D) {
            maxHealth.setBaseValue(desiredHealthPoints);

            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }
}
