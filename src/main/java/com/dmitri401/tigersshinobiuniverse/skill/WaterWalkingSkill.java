package com.dmitri401.tigersshinobiuniverse.skill;

import com.dmitri401.tigersshinobiuniverse.player.ShinobiStatService;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStats;
import com.dmitri401.tigersshinobiuniverse.player.WallRunService;
import gravitychanger.api.GravityChangerAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public final class WaterWalkingSkill {

    private static final double STANDING_OFFSET = 0.02D;

    /*
     * About a 1.5-block jump under normal Minecraft gravity.
     * Vanilla jump velocity is approximately 0.42D.
     */
    private static final double WATER_JUMP_VELOCITY = 0.465D;

    private WaterWalkingSkill() {
    }

    public static void tick(Player player) {
        Level level = player.level();

        if (player instanceof ServerPlayer serverPlayer) {
            ShinobiStats stats = ShinobiStatService.get(serverPlayer);

            if (!stats.isNinja()
                    || WallRunService.isWallRunning(serverPlayer)) {
                return;
            }
        }

        /*
         * Wall running has priority over water walking. The gravity-direction
         * check runs on both logical sides, preventing the client from applying
         * water support while attached to a wall or ceiling.
         */
        if (GravityChangerAPI.getGravityDirection(player) != Direction.DOWN) {
            return;
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
         * Do not interfere with a real jump that is moving upward.
         * Water walking takes control again when the player descends.
         */
        if (movement.y > 0.0D) {
            player.setOnGround(false);
            return;
        }

        /*
         * Always land at one exact height. The old behavior only moved players
         * upward when they were below standingY, then cancelled downward
         * velocity even when they were as much as 0.45 blocks above the water.
         * That could leave the player suspended above the surface.
         */
        if (Math.abs(feetY - standingY) > 0.001D) {
            player.setPos(
                    player.getX(),
                    standingY,
                    player.getZ()
            );
        }

        player.setOnGround(true);
        player.setSwimming(false);
        player.resetFallDistance();

        player.setDeltaMovement(
                movement.x,
                0.0D,
                movement.z
        );

        player.hasImpulse = true;
    }

    public static void handleWaterJump(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ShinobiStats stats = ShinobiStatService.get(serverPlayer);

            if (!stats.isNinja()
                    || WallRunService.isWallRunning(serverPlayer)) {
                return;
            }
        }

        /*
         * Water walking only operates with normal downward gravity.
         * Wall-running jumps are handled by their own movement system.
         */
        if (GravityChangerAPI.getGravityDirection(player) != Direction.DOWN) {
            return;
        }

        if (player.isShiftKeyDown() || player.isUnderWater()) {
            return;
        }

        Level level = player.level();

        BlockPos belowFeet = BlockPos.containing(
                player.getX(),
                player.getBoundingBox().minY - 0.05D,
                player.getZ()
        );

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
        double expectedStandingY =
                waterSurface.surfaceY() + STANDING_OFFSET;

        /*
         * Only boost jumps that begin from the actual water-walking surface.
         * This prevents nearby water from modifying jumps made from land.
         */
        if (Math.abs(feetY - expectedStandingY) > 0.12D) {
            return;
        }

        Vec3 movement = player.getDeltaMovement();

        player.setDeltaMovement(
                movement.x,
                Math.max(movement.y, WATER_JUMP_VELOCITY),
                movement.z
        );

        player.setOnGround(false);
        player.hasImpulse = true;

        if (player instanceof ServerPlayer) {
            player.hurtMarked = true;
        }
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
