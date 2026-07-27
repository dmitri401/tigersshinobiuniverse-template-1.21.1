package com.dmitri401.tigersshinobiuniverse;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {

    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_JUTSU_TEST_MESSAGE =
            BUILDER
                    .comment(
                            "Whether pressing the test jutsu key displays a message."
                    )
                    .define(
                            "showJutsuTestMessage",
                            true
                    );

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}