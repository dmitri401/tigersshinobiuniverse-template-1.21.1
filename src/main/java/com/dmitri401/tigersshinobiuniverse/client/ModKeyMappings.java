package com.dmitri401.tigersshinobiuniverse.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeyMappings {

    public static final KeyMapping HAND_SIGN_1 =
            new KeyMapping(
                    "key.tigersshinobiuniverse.hand_sign_1",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "key.categories.tigersshinobiuniverse"
            );

    public static final KeyMapping HAND_SIGN_2 =
            new KeyMapping(
                    "key.tigersshinobiuniverse.hand_sign_2",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_F,
                    "key.categories.tigersshinobiuniverse"
            );

    public static final KeyMapping CHARGE =
            new KeyMapping(
                    "key.tigersshinobiuniverse.charge",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_C,
                    "key.categories.tigersshinobiuniverse"
            );

    public static final KeyMapping MENU =
            new KeyMapping(
                    "key.tigersshinobiuniverse.menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_V,
                    "key.categories.tigersshinobiuniverse"
            );




    public static final KeyMapping ALT =
            new KeyMapping(
                    "key.tigersshinobiuniverse.alt",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    "key.categories.tigersshinobiuniverse"
            );

    public static final KeyMapping WALL_RUN =
            new KeyMapping(
                    "key.tigersshinobiuniverse.wall_run",
                    InputConstants.Type.KEYSYM,
                    InputConstants.UNKNOWN.getValue(),
                    "key.categories.tigersshinobiuniverse"
            );

    private ModKeyMappings() {
    }
}