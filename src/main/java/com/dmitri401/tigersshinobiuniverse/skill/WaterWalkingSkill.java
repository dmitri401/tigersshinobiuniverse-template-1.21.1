package com.dmitri401.tigersshinobiuniverse.skill;

import com.dmitri401.tigersshinobiuniverse.player.ShinobiStatService;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.Vec3;

public final class WaterWalkingSkill {

    private static final int CHAKRA_COST = 1;
    private static final int CHAKRA_INTERVAL = 20;

    private WaterWalkingSkill() {
    }

    public static void tick(ServerPlayer player) {
        ShinobiStats stats = ShinobiStatService.get(player);

        // Only completed ninja characters may use this.
        if (!stats.isNinja()) {
            return;
        }

        // Do not activate while deliberately sneaking.
        if (player.isShiftKeyDown()) {
            return;
        }

        // The player's feet must be touching the top of water.
        if (!isAtWaterSurface(player)) {
            return;
        }

        // Charge one chakra per second.
        if (player.tickCount % CHAKRA_INTERVAL == 0) {
            if (!ShinobiStatService.consumeChakra(
                    player,
                    CHAKRA_COST
            )) {
                return;
            }
        }

        Vec3 movement = player.getDeltaMovement();

        /*
         * A small upward force counteracts gravity and keeps the
         * player around the water surface.
         */
        double verticalSpeed = movement.y;

        if (verticalSpeed < 0.08D) {
            verticalSpeed = 0.08D;
        }

        player.setDeltaMovement(
                movement.x,
                verticalSpeed,
                movement.z
        );

        player.fallDistance = 0.0F;
        player.hasImpulse = true;
    }

    private static boolean isAtWaterSurface(
            ServerPlayer player
    ) {
        BlockPos feetPosition = BlockPos.containing(
                player.getX(),
                player.getBoundingBox().minY,
                player.getZ()
        );

        BlockPos belowFeet = feetPosition.below();

        boolean feetInWater = player.level()
                .getFluidState(feetPosition)
                .is(FluidTags.WATER);

        boolean waterBelow = player.level()
                .getFluidState(belowFeet)
                .is(FluidTags.WATER);

        /*
         * This covers the moment the player touches the surface
         * and the moment their feet enter the upper water block.
         */
        return feetInWater || waterBelow;
    }
}