package com.dmitri401.tigersshinobiuniverse.player;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.attachment.ModAttachments;
import gravitychanger.api.GravityChangerAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = TigersShinobiUniverse.MOD_ID)
public final class WallRunService {

    private static final double MAX_SURFACE_DISTANCE = 4.0D;
    private static final double MAX_FALL_DISTANCE = 5.0D;
    private static final double MAX_SELECTION_DISTANCE_SQUARED = 36.0D;

    private static final Map<UUID, WallRunState> ACTIVE =
            new ConcurrentHashMap<>();

    private WallRunService() {
    }

    public static void selectSurface(
            ServerPlayer player,
            BlockPos blockPos,
            Direction face
    ) {
        ShinobiStats stats = player.getData(
                ModAttachments.SHINOBI_STATS
        );

        if (!stats.isNinja()
                || player.isUnderWater()
                || !GravityChangerAPI.canChangeGravity(player)) {
            return;
        }

        if (player.distanceToSqr(Vec3.atCenterOf(blockPos))
                > MAX_SELECTION_DISTANCE_SQUARED) {
            return;
        }

        BlockState blockState = player.level()
                .getBlockState(blockPos);

        if (blockState.isAir()
                || !blockState.isFaceSturdy(
                player.level(),
                blockPos,
                face
        )) {
            return;
        }

        Direction gravityDirection = face.getOpposite();
        Direction previousGravityDirection =
                GravityChangerAPI.getGravityDirection(player);

        /*
         * Do not carry a fall from the previous gravity direction into the
         * wall-running direction. Upward and sideways motion is preserved.
         */
        Vec3 worldVelocity = removeFallingVelocity(
                GravityChangerAPI.getWorldVelocity(player),
                previousGravityDirection
        );

        GravityChangerAPI.setBaseGravityDirection(
                player,
                gravityDirection
        );

        GravityChangerAPI.setWorldVelocity(
                player,
                worldVelocity
        );

        ACTIVE.put(
                player.getUUID(),
                new WallRunState(
                        contactCoordinate(player, gravityDirection),
                        gravityAxisPosition(player, gravityDirection)
                )
        );

        saveWallRun(player, gravityDirection);
        player.resetFallDistance();
    }

    public static boolean isWallRunning(ServerPlayer player) {
        return ACTIVE.containsKey(player.getUUID());
    }

    public static void reset(ServerPlayer player) {
        ACTIVE.remove(player.getUUID());
        clearSavedWallRun(player);

        if (GravityChangerAPI.canChangeGravity(player)) {
            Vec3 worldVelocity =
                    GravityChangerAPI.getWorldVelocity(player);

            GravityChangerAPI.resetGravity(player);

            Direction restoredGravityDirection =
                    GravityChangerAPI.getGravityDirection(player);

            /*
             * A velocity already pointing toward the restored gravity makes
             * the player dip immediately during the handoff. Remove only that
             * falling component; wall jumps and sideways movement remain.
             */
            GravityChangerAPI.setWorldVelocity(
                    player,
                    removeFallingVelocity(
                            worldVelocity,
                            restoredGravityDirection
                    )
            );
        }

        player.resetFallDistance();
    }

    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        /*
         * ACTIVE is runtime-only and disappears when the player logs out.
         * Restore the serialized gravity before vanilla movement runs so the
         * player never receives a normal-gravity falling tick first.
         */
        if (!ACTIVE.containsKey(player.getUUID())) {
            restoreSavedWallRun(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        WallRunState state = ACTIVE.get(player.getUUID());

        if (state == null) {
            return;
        }

        ShinobiStats stats = player.getData(
                ModAttachments.SHINOBI_STATS
        );

        if (!stats.isNinja()
                || player.getAbilities().flying
                || player.isPassenger()
                || player.isFallFlying()
                || player.isUnderWater()) {
            reset(player);
            return;
        }

        Direction gravityDirection =
                GravityChangerAPI.getGravityDirection(player);

        double gravityPosition =
                gravityAxisPosition(player, gravityDirection);

        /*
         * While grounded, continually record the current gravity-relative
         * floor as the last standing plane. Walking along the wall does not
         * increase either the jump-away distance or the fall distance.
         */
        if (player.onGround()) {
            state.planeCoordinate = contactCoordinate(
                    player,
                    gravityDirection
            );
            state.fallReferenceCoordinate = gravityPosition;
        }

        double distanceFromPlane =
                distanceFromPlane(player, state);

        if (distanceFromPlane > MAX_SURFACE_DISTANCE) {
            reset(player);
            return;
        }

        if (!player.onGround()) {
            Vec3 worldVelocity =
                    GravityChangerAPI.getWorldVelocity(player);

            Vec3 gravityVector = Vec3.atLowerCornerOf(
                    gravityDirection.getNormal()
            );

            double velocityAlongGravity =
                    worldVelocity.dot(gravityVector);

            /*
             * Negative means the player is still moving opposite gravity
             * (rising away from the surface). Move the reference point with
             * them so falling is measured from the apex of the jump.
             */
            if (velocityAlongGravity < 0.0D) {
                state.fallReferenceCoordinate = gravityPosition;
            }

            double gravityRelativeFallDistance =
                    gravityPosition
                            - state.fallReferenceCoordinate;

            if (gravityRelativeFallDistance > MAX_FALL_DISTANCE) {
                reset(player);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(
            PlayerEvent.PlayerChangedDimensionEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer player) {
            reset(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer player) {
            reset(player);
        }
    }

    @SubscribeEvent
    public static void onLogout(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        /*
         * Keep the serialized attachment active, but remove the runtime entry.
         * It will be reconstructed on the first server tick after login.
         */
        if (ACTIVE.containsKey(player.getUUID())) {
            Direction gravityDirection =
                    GravityChangerAPI.getGravityDirection(player);

            saveWallRun(player, gravityDirection);
        }

        ACTIVE.remove(player.getUUID());
    }

    private static void saveWallRun(
            ServerPlayer player,
            Direction gravityDirection
    ) {
        WallRunData data = player.getData(
                ModAttachments.WALL_RUN_DATA
        );

        data.activate(gravityDirection);
    }

    private static void clearSavedWallRun(
            ServerPlayer player
    ) {
        player.getData(
                ModAttachments.WALL_RUN_DATA
        ).clear();
    }

    private static boolean restoreSavedWallRun(
            ServerPlayer player
    ) {
        WallRunData data = player.getData(
                ModAttachments.WALL_RUN_DATA
        );

        if (!data.isActive()) {
            return false;
        }

        Direction gravityDirection =
                data.getGravityDirection();

        ShinobiStats stats = player.getData(
                ModAttachments.SHINOBI_STATS
        );

        /*
         * These are the same states that normally cancel wall running.
         * Invalid saved data is cleared rather than restored.
         */
        if (!stats.isNinja()
                || gravityDirection == Direction.DOWN
                || player.getAbilities().flying
                || player.isPassenger()
                || player.isFallFlying()
                || player.isUnderWater()) {
            reset(player);
            return false;
        }

        /*
         * Gravity Changer may not be ready on the first login tick. Leave the
         * attachment intact and try again on the next tick in that case.
         */
        if (!GravityChangerAPI.canChangeGravity(player)) {
            return false;
        }

        Direction previousGravityDirection =
                GravityChangerAPI.getGravityDirection(player);

        Vec3 worldVelocity = removeFallingVelocity(
                GravityChangerAPI.getWorldVelocity(player),
                previousGravityDirection
        );

        GravityChangerAPI.setBaseGravityDirection(
                player,
                gravityDirection
        );

        GravityChangerAPI.setWorldVelocity(
                player,
                worldVelocity
        );

        /*
         * Rebuild the runtime reference coordinates from the player's loaded
         * position. This avoids restoring stale coordinates from before logout.
         */
        ACTIVE.put(
                player.getUUID(),
                new WallRunState(
                        contactCoordinate(player, gravityDirection),
                        gravityAxisPosition(player, gravityDirection)
                )
        );

        player.resetFallDistance();
        return true;
    }

    /**
     * Removes only velocity that is moving in the gravity direction.
     * Negative movement along the gravity vector is upward and is preserved.
     */
    private static Vec3 removeFallingVelocity(
            Vec3 worldVelocity,
            Direction gravityDirection
    ) {
        Vec3 gravityVector = Vec3.atLowerCornerOf(
                gravityDirection.getNormal()
        );

        double velocityAlongGravity =
                worldVelocity.dot(gravityVector);

        if (velocityAlongGravity <= 0.0D) {
            return worldVelocity;
        }

        return worldVelocity.subtract(
                gravityVector.scale(velocityAlongGravity)
        );
    }

    /*
     * Converts the player's position into a coordinate that always increases
     * when the player moves in the current gravity direction.
     */
    private static double gravityAxisPosition(
            ServerPlayer player,
            Direction gravityDirection
    ) {
        return switch (gravityDirection) {
            case WEST -> -player.getX();
            case EAST -> player.getX();
            case DOWN -> -player.getY();
            case UP -> player.getY();
            case NORTH -> -player.getZ();
            case SOUTH -> player.getZ();
        };
    }

    private static double distanceFromPlane(
            ServerPlayer player,
            WallRunState state
    ) {
        Direction gravityDirection =
                GravityChangerAPI.getGravityDirection(player);

        double currentContactCoordinate =
                contactCoordinate(player, gravityDirection);

        return Math.abs(
                currentContactCoordinate - state.planeCoordinate
        );
    }

    /*
     * Returns the coordinate of the side of the player's bounding box
     * that faces the current gravity direction. This is the side that
     * acts as the player's feet while standing on a wall or ceiling.
     */
    private static double contactCoordinate(
            ServerPlayer player,
            Direction gravityDirection
    ) {
        return switch (gravityDirection) {
            case WEST -> player.getBoundingBox().minX;
            case EAST -> player.getBoundingBox().maxX;
            case DOWN -> player.getBoundingBox().minY;
            case UP -> player.getBoundingBox().maxY;
            case NORTH -> player.getBoundingBox().minZ;
            case SOUTH -> player.getBoundingBox().maxZ;
        };
    }

    private static final class WallRunState {
        private double planeCoordinate;
        private double fallReferenceCoordinate;

        private WallRunState(
                double planeCoordinate,
                double fallReferenceCoordinate
        ) {
            this.planeCoordinate = planeCoordinate;
            this.fallReferenceCoordinate = fallReferenceCoordinate;
        }
    }
}