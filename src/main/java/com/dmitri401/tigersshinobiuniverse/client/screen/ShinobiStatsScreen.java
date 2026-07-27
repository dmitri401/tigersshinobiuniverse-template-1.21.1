package com.dmitri401.tigersshinobiuniverse.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ShinobiStatsScreen extends Screen {

    private static final Component TITLE =
            Component.translatable(
                    "screen.tigersshinobiuniverse.shinobi_stats"
            );

    public ShinobiStatsScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 120;
        int buttonHeight = 20;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Close"),
                        button -> this.onClose()
                ).bounds(
                        this.width / 2 - buttonWidth / 2,
                        this.height / 2 + 70,
                        buttonWidth,
                        buttonHeight
                ).build()
        );
    }

    @Override
    public void renderBackground(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        // Prevent the normal blurred background.
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 90;

        int panelLeft = centerX - 110;
        int panelTop = startY - 15;
        int panelRight = centerX + 110;
        int panelBottom = startY + 140;

        graphics.fill(
                panelLeft,
                panelTop,
                panelRight,
                panelBottom,
                0xAA000000
        );

        graphics.drawCenteredString(
                this.font,
                this.title,
                centerX,
                startY,
                0xFFFFFF
        );

        graphics.drawString(
                this.font,
                "Level: 1",
                centerX - 80,
                startY + 30,
                0xFFFFFF
        );

        graphics.drawString(
                this.font,
                "Chakra: 100 / 100",
                centerX - 80,
                startY + 45,
                0xFFFFFF
        );

        graphics.drawString(
                this.font,
                "Chakra Control: 1",
                centerX - 80,
                startY + 60,
                0xFFFFFF
        );

        graphics.drawString(
                this.font,
                "Ninjutsu: 1",
                centerX - 80,
                startY + 75,
                0xFFFFFF
        );

        graphics.drawString(
                this.font,
                "Taijutsu: 1",
                centerX - 80,
                startY + 90,
                0xFFFFFF
        );

        graphics.drawString(
                this.font,
                "Genjutsu: 1",
                centerX - 80,
                startY + 105,
                0xFFFFFF
        );

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}