package com.dmitri401.tigersshinobiuniverse.player;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Persistent server-side wall-running data.
 *
 * The active runtime calculations remain in WallRunService. This attachment
 * stores the gravity direction required to restore wall running safely after
 * reconnecting.
 */
public final class WallRunData
        implements INBTSerializable<CompoundTag> {

    private static final String ACTIVE_KEY = "Active";
    private static final String GRAVITY_DIRECTION_KEY =
            "GravityDirection";
    private boolean active;
    private int gravityDirectionOrdinal =
            Direction.DOWN.ordinal();

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

    public void activate(Direction gravityDirection) {
        if (gravityDirection == null
                || gravityDirection == Direction.DOWN) {
            clear();
            return;
        }

        active = true;
        gravityDirectionOrdinal = gravityDirection.ordinal();
    }

    public void clear() {
        active = false;
        gravityDirectionOrdinal = Direction.DOWN.ordinal();
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
         * Invalid or normal-down gravity is not a wall-running state.
         */
        if (getGravityDirection() == Direction.DOWN) {
            clear();
        }
    }
}
