package com.dmitri401.tigersshinobiuniverse.skill;

import com.dmitri401.tigersshinobiuniverse.player.ShinobiStatService;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public final class WaterWalkingSkill {

    private static final double STANDING_OFFSET = 0.12D;

    private WaterWalkingSkill() {
    }

    public static void tick(Player player) {
        Level level = player.level();

        /*
         * ShinobiStatService.get currently accepts ServerPlayer only,
         * so ninja validation is performed on the logical server.
         */
        if (player instanceof ServerPlayer serverPlayer) {
            ShinobiStats stats = ShinobiStatService.get(serverPlayer);

            if (!stats.isNinja()) {
                return;
            }

        }

        // Sneaking intentionally disables water walking.
        if (player.isShiftKeyDown()) {
            return;
        }

        BlockPos belowFeet = BlockPos.containing(
                player.getX(),
                player.getBoundingBox().minY - 0.05D,
                player.getZ()
        );

        // Stop controlling the player as soon as solid land is underneath.
        if (!level.getBlockState(belowFeet)
                .getCollisionShape(level, belowFeet)
                .isEmpty()) {
            return;
        }

        WaterSurface waterSurface = findWaterSurface(player, level);

        if (waterSurface == null) {
            return;
        }

        double feetY = player.getBoundingBox().minY;
        double surfaceY = waterSurface.surfaceY();

        // Ignore players who are deep underwater or far above the surface.
        if (feetY < surfaceY - 0.45D
                || feetY > surfaceY + 0.45D) {
            return;
        }

        Vec3 movement = player.getDeltaMovement();
        double standingY = surfaceY + STANDING_OFFSET;

        /*
         * Keep the player's feet above the fluid before vanilla movement
         * applies swimming behavior and water drag.
         */
        if (feetY < standingY && movement.y <= 0.0D) {
            player.setPos(
                    player.getX(),
                    standingY,
                    player.getZ()
            );
        }

        /*
         * Let vanilla treat the water surface like ground so jump input works.
         * Do not replace positive Y velocity, because that is the jump.
         */
        player.setOnGround(true);
        player.setSwimming(false);
        player.resetFallDistance();

        if (movement.y < 0.0D) {
            player.setDeltaMovement(
                    movement.x,
                    0.0D,
                    movement.z
            );
        }

        player.hasImpulse = true;
    }

    private static WaterSurface findWaterSurface(
            Player player,
            Level level
    ) {
        double feetY = player.getBoundingBox().minY;

        for (int offset = 0; offset >= -1; offset--) {
            BlockPos position = BlockPos.containing(
                    player.getX(),
                    feetY + offset,
                    player.getZ()
            );

            FluidState fluid = level.getFluidState(position);

            if (!fluid.is(FluidTags.WATER)) {
                continue;
            }

            double surfaceY = position.getY()
                    + fluid.getHeight(level, position);

            return new WaterSurface(surfaceY);
        }

        return null;
    }

    private record WaterSurface(double surfaceY) {
    }
}