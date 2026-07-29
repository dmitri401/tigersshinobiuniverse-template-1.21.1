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
        Vec3 worldVelocity =
                GravityChangerAPI.getWorldVelocity(player);

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

        player.resetFallDistance();
    }

    public static boolean isWallRunning(ServerPlayer player) {
        return ACTIVE.containsKey(player.getUUID());
    }

    public static void reset(ServerPlayer player) {
        ACTIVE.remove(player.getUUID());

        if (GravityChangerAPI.canChangeGravity(player)) {
            Vec3 worldVelocity =
                    GravityChangerAPI.getWorldVelocity(player);

            GravityChangerAPI.resetGravity(player);
            GravityChangerAPI.setWorldVelocity(
                    player,
                    worldVelocity
            );
        }

        player.resetFallDistance();
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
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
                || player.isFallFlying()) {
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
        if (event.getEntity() instanceof ServerPlayer player) {
            ACTIVE.remove(player.getUUID());
        }
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