package com.dmitri401.tigersshinobiuniverse.client.hud;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.attachment.ModAttachments;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStats;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class ChakraHud {

    private static final ResourceLocation FRAME_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    TigersShinobiUniverse.MOD_ID,
                    "textures/gui/hud_frame.png"
            );

    private static final ResourceLocation HEALTH_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    TigersShinobiUniverse.MOD_ID,
                    "textures/gui/health_bar.png"
            );

    private static final ResourceLocation CHAKRA_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    TigersShinobiUniverse.MOD_ID,
                    "textures/gui/chakra_bar.png"
            );

    private static final int TEXTURE_WIDTH = 250;
    private static final int TEXTURE_HEIGHT = 50;

    private static final int HUD_X = 8;
    private static final int HUD_Y = 8;

    /*
     * These bounds match the non-transparent bar artwork in the PNGs.
     * Health artwork: x 40 through 246, y 3 through 15.
     * Chakra artwork: x 51 through 246, y 19 through 28.
     */
    private static final int HEALTH_FILL_X = 40;
    private static final int HEALTH_FILL_Y = 3;
    private static final int HEALTH_FILL_WIDTH = 207;
    private static final int HEALTH_FILL_HEIGHT = 13;

    private static final int CHAKRA_FILL_X = 51;
    private static final int CHAKRA_FILL_Y = 19;
    private static final int CHAKRA_FILL_WIDTH = 196;
    private static final int CHAKRA_FILL_HEIGHT = 10;

    private static float displayedHealth = -1.0F;
    private static float displayedChakra = -1.0F;

    private ChakraHud() {
    }

    public static void render(
            GuiGraphics graphics,
            DeltaTracker deltaTracker
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.level == null
                || minecraft.options.hideGui
                || minecraft.screen != null) {
            return;
        }

        ShinobiStats stats = minecraft.player.getData(
                ModAttachments.SHINOBI_STATS
        );

        if (!stats.isNinja()) {
            return;
        }

        float health = minecraft.player.getHealth();
        float maxHealth = Math.max(
                1.0F,
                minecraft.player.getMaxHealth()
        );

        int chakra = stats.getChakra();
        int maxChakra = Math.max(
                1,
                stats.getMaxChakra()
        );

        if (displayedHealth < 0.0F) {
            displayedHealth = health;
        }

        if (displayedChakra < 0.0F) {
            displayedChakra = chakra;
        }

        /*
         * Smooth the bars instead of snapping instantly.
         * A larger value approaches the real stat faster.
         */
        displayedHealth = Mth.lerp(
                0.18F,
                displayedHealth,
                health
        );

        displayedChakra = Mth.lerp(
                0.18F,
                displayedChakra,
                chakra
        );

        float healthPercent = Mth.clamp(
                displayedHealth / maxHealth,
                0.0F,
                1.0F
        );

        float chakraPercent = Mth.clamp(
                displayedChakra / maxChakra,
                0.0F,
                1.0F
        );

        renderCroppedBar(
                graphics,
                HEALTH_TEXTURE,
                HUD_X,
                HUD_Y,
                HEALTH_FILL_X,
                HEALTH_FILL_Y,
                HEALTH_FILL_WIDTH,
                HEALTH_FILL_HEIGHT,
                healthPercent
        );

        renderCroppedBar(
                graphics,
                CHAKRA_TEXTURE,
                HUD_X,
                HUD_Y,
                CHAKRA_FILL_X,
                CHAKRA_FILL_Y,
                CHAKRA_FILL_WIDTH,
                CHAKRA_FILL_HEIGHT,
                chakraPercent
        );

        // Draw the decorative frame last so it covers the fill edges.
        graphics.blit(
                FRAME_TEXTURE,
                HUD_X,
                HUD_Y,
                0,
                0,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );

        renderValues(
                graphics,
                health,
                maxHealth,
                chakra,
                maxChakra
        );
    }

    private static void renderCroppedBar(
            GuiGraphics graphics,
            ResourceLocation texture,
            int hudX,
            int hudY,
            int fillX,
            int fillY,
            int fillWidth,
            int fillHeight,
            float percentage
    ) {
        int visibleWidth = Math.round(
                fillWidth * percentage
        );

        if (visibleWidth <= 0) {
            return;
        }

        graphics.enableScissor(
                hudX + fillX,
                hudY + fillY,
                hudX + fillX + visibleWidth,
                hudY + fillY + fillHeight
        );

        /*
         * Draw the complete fill artwork through a scissor region.
         * This reveals the fill from left to right without stretching it.
         */
        graphics.blit(
                texture,
                hudX,
                hudY,
                0,
                0,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );

        graphics.disableScissor();
    }

    private static void renderValues(
            GuiGraphics graphics,
            float health,
            float maxHealth,
            int chakra,
            int maxChakra
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        String healthText =
                Math.round(health)
                        + " / "
                        + Math.round(maxHealth);

        String chakraText =
                chakra
                        + " / "
                        + maxChakra;

        int healthTextX =
                HUD_X + 244
                        - minecraft.font.width(healthText);

        int chakraTextX =
                HUD_X + 244
                        - minecraft.font.width(chakraText);

        graphics.drawString(
                minecraft.font,
                healthText,
                healthTextX,
                HUD_Y + 4,
                0xFFFFFFFF,
                true
        );

        graphics.drawString(
                minecraft.font,
                chakraText,
                chakraTextX,
                HUD_Y + 20,
                0xFFFFFFFF,
                true
        );
    }
}
