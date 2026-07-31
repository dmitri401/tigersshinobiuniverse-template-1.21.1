package com.dmitri401.tigersshinobiuniverse.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Applies basic stats to vanilla Minecraft attributes.
 * Permanent-stat changes and player lifecycle events call apply directly.
 */
public final class BasicStatEffects {

    private BasicStatEffects() {
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
