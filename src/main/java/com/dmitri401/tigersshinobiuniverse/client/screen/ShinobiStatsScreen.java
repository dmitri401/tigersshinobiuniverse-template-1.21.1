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
                        this.height / 2 + 92,
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
        int startY = this.height / 2 - 108;

        int panelLeft = centerX - 145;
        int panelTop = startY - 15;
        int panelRight = centerX + 145;
        int panelBottom = startY + 190;

        int leftX = centerX - 120;
        int rightX = centerX + 15;
        int textColor = 0xFFFFFF;
        int headingColor = 0xFFD36B;

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
                leftX,
                startY + 24,
                textColor
        );

        graphics.drawString(
                this.font,
                "Experience: " + stats.ninjaExperience(),
                rightX,
                startY + 24,
                textColor
        );

        graphics.drawString(
                this.font,
                "Stat Points: " + stats.statPoints(),
                rightX,
                startY + 37,
                textColor
        );

        graphics.drawString(
                this.font,
                "Basic Stats",
                leftX,
                startY + 52,
                headingColor
        );

        graphics.drawString(
                this.font,
                "Vitality: "
                        + stats.vitality()
                        + " hearts",
                leftX,
                startY + 68,
                textColor
        );

        graphics.drawString(
                this.font,
                "Defense: " + stats.defense(),
                leftX,
                startY + 81,
                textColor
        );

        graphics.drawString(
                this.font,
                "Agility: " + stats.agility(),
                leftX,
                startY + 94,
                textColor
        );

        graphics.drawString(
                this.font,
                "Chakra Control: "
                        + stats.chakraControl(),
                leftX,
                startY + 107,
                textColor
        );

        graphics.drawString(
                this.font,
                "Chakra: "
                        + stats.chakra()
                        + " / "
                        + stats.maxChakra(),
                leftX,
                startY + 120,
                textColor
        );

        graphics.drawString(
                this.font,
                "Skills",
                rightX,
                startY + 52,
                headingColor
        );

        graphics.drawString(
                this.font,
                "Ninjutsu: " + stats.ninjutsu(),
                rightX,
                startY + 68,
                textColor
        );

        graphics.drawString(
                this.font,
                "Taijutsu: " + stats.taijutsu(),
                rightX,
                startY + 81,
                textColor
        );

        graphics.drawString(
                this.font,
                "Genjutsu: " + stats.genjutsu(),
                rightX,
                startY + 94,
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