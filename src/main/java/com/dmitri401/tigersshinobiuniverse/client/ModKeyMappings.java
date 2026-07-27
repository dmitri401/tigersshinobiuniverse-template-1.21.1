package com.dmitri401.tigersshinobiuniverse.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeyMappings {

    public static final KeyMapping ACTIVATE_JUTSU =
            new KeyMapping(
                    "key.tigersshinobiuniverse.activate_jutsu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "key.categories.tigersshinobiuniverse"
            );

    private ModKeyMappings() {
    }
}