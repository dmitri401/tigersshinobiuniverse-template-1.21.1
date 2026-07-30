package com.dmitri401.tigersshinobiuniverse.config;

/**
 * Simple gameplay tuning values.
 *
 * Change HAND_SIGN_TIMEOUT_SECONDS to adjust how long the player has
 * to enter the next hand sign. Minecraft normally runs at 20 ticks/second.
 */
public final class JutsuConfig {

    public static final double HAND_SIGN_TIMEOUT_SECONDS = 1.0D;

    public static final int HAND_SIGN_TIMEOUT_TICKS =
            Math.max(
                    1,
                    (int) Math.round(
                            HAND_SIGN_TIMEOUT_SECONDS * 20.0D
                    )
            );

    private JutsuConfig() {
    }
}
