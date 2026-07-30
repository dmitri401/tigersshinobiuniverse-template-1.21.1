package com.dmitri401.tigersshinobiuniverse.player;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Persistent server-side wall-running data.
 *
 * The active runtime calculations remain in WallRunService. This attachment
 * stores the gravity direction and the world-space body anchor required to
 * restore wall running safely after reconnecting.
 */
public final class WallRunData
        implements INBTSerializable<CompoundTag> {

    private static final String ACTIVE_KEY = "Active";
    private static final String GRAVITY_DIRECTION_KEY =
            "GravityDirection";
    private static final String HAS_BODY_CENTER_KEY =
            "HasBodyCenter";
    private static final String BODY_CENTER_X_KEY =
            "BodyCenterX";
    private static final String BODY_CENTER_Y_KEY =
            "BodyCenterY";
    private static final String BODY_CENTER_Z_KEY =
            "BodyCenterZ";

    private boolean active;
    private int gravityDirectionOrdinal =
            Direction.DOWN.ordinal();

    private boolean hasBodyCenter;
    private double bodyCenterX;
    private double bodyCenterY;
    private double bodyCenterZ;

    public boolean isActive() {
        return active;
    }

    public Direction getGravityDirection() {
        Direction[] directions = Direction.values();

        if (gravityDirectionOrdinal < 0
                || gravityDirectionOrdinal >= directions.length) {
            return Direction.DOWN;
        }

        return directions[gravityDirectionOrdinal];
    }

    public boolean hasBodyCenter() {
        return hasBodyCenter;
    }

    public Vec3 getBodyCenter() {
        return new Vec3(
                bodyCenterX,
                bodyCenterY,
                bodyCenterZ
        );
    }

    /**
     * Retained for compatibility with callers that do not supply an anchor.
     */
    public void activate(Direction gravityDirection) {
        activate(gravityDirection, null);
    }

    public void activate(
            Direction gravityDirection,
            Vec3 bodyCenter
    ) {
        if (gravityDirection == null
                || gravityDirection == Direction.DOWN) {
            clear();
            return;
        }

        active = true;
        gravityDirectionOrdinal = gravityDirection.ordinal();

        hasBodyCenter = bodyCenter != null;

        if (bodyCenter != null) {
            bodyCenterX = bodyCenter.x;
            bodyCenterY = bodyCenter.y;
            bodyCenterZ = bodyCenter.z;
        }
    }

    public void clear() {
        active = false;
        gravityDirectionOrdinal = Direction.DOWN.ordinal();
        hasBodyCenter = false;
        bodyCenterX = 0.0D;
        bodyCenterY = 0.0D;
        bodyCenterZ = 0.0D;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(
            HolderLookup.Provider provider
    ) {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean(ACTIVE_KEY, active);
        tag.putInt(
                GRAVITY_DIRECTION_KEY,
                gravityDirectionOrdinal
        );
        tag.putBoolean(
                HAS_BODY_CENTER_KEY,
                hasBodyCenter
        );

        if (hasBodyCenter) {
            tag.putDouble(BODY_CENTER_X_KEY, bodyCenterX);
            tag.putDouble(BODY_CENTER_Y_KEY, bodyCenterY);
            tag.putDouble(BODY_CENTER_Z_KEY, bodyCenterZ);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(
            HolderLookup.Provider provider,
            CompoundTag tag
    ) {
        active = tag.getBoolean(ACTIVE_KEY);
        gravityDirectionOrdinal =
                tag.getInt(GRAVITY_DIRECTION_KEY);

        /*
         * Older saves do not contain this key. getBoolean then returns false,
         * so they continue to load without an invalid zero-position anchor.
         */
        hasBodyCenter = tag.getBoolean(
                HAS_BODY_CENTER_KEY
        );

        if (hasBodyCenter) {
            bodyCenterX = tag.getDouble(BODY_CENTER_X_KEY);
            bodyCenterY = tag.getDouble(BODY_CENTER_Y_KEY);
            bodyCenterZ = tag.getDouble(BODY_CENTER_Z_KEY);
        } else {
            bodyCenterX = 0.0D;
            bodyCenterY = 0.0D;
            bodyCenterZ = 0.0D;
        }

        /*
         * Invalid or normal-down gravity is not a wall-running state.
         */
        if (getGravityDirection() == Direction.DOWN) {
            clear();
        }
    }
}