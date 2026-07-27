package com.dmitri401.tigersshinobiuniverse.client.screen;

import com.dmitri401.tigersshinobiuniverse.client.data.ClientShinobiStats;
import com.dmitri401.tigersshinobiuniverse.network.payload.SyncStatsPayload;
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
        // Intentionally empty so Minecraft does not blur the world.
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        SyncStatsPayload stats = ClientShinobiStats.get();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 90;

        int panelLeft = centerX - 110;
        int panelTop = startY - 15;
        int panelRight = centerX + 110;
        int panelBottom = startY + 140;

        int textX = centerX - 80;
        int textColor = 0xFFFFFF;

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
                textColor
        );

        graphics.drawString(
                this.font,
                "Level: " + stats.level(),
                textX,
                startY + 25,
                textColor
        );

        graphics.drawString(
                this.font,
                "Experience: " + stats.ninjaExperience(),
                textX,
                startY + 38,
                textColor
        );

        graphics.drawString(
                this.font,
                "Chakra: "
                        + stats.chakra()
                        + " / "
                        + stats.maxChakra(),
                textX,
                startY + 51,
                textColor
        );

        graphics.drawString(
                this.font,
                "Ninjutsu: " + stats.ninjutsu(),
                textX,
                startY + 64,
                textColor
        );

        graphics.drawString(
                this.font,
                "Taijutsu: " + stats.taijutsu(),
                textX,
                startY + 77,
                textColor
        );

        graphics.drawString(
                this.font,
                "Genjutsu: " + stats.genjutsu(),
                textX,
                startY + 90,
                textColor
        );

        graphics.drawString(
                this.font,
                "Strength: " + stats.strength(),
                textX,
                startY + 103,
                textColor
        );

        graphics.drawString(
                this.font,
                "Agility: " + stats.agility(),
                centerX + 10,
                startY + 64,
                textColor
        );

        graphics.drawString(
                this.font,
                "Vitality: " + stats.vitality(),
                centerX + 10,
                startY + 77,
                textColor
        );

        graphics.drawString(
                this.font,
                "Stat Points: " + stats.statPoints(),
                centerX + 10,
                startY + 90,
                textColor
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