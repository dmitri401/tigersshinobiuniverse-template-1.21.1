package com.dmitri401.tigersshinobiuniverse.client.screen;

import com.dmitri401.tigersshinobiuniverse.client.data.ClientShinobiStats;
import com.dmitri401.tigersshinobiuniverse.jutsu.JutsuSlots;
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

    private ScreenTab activeTab = ScreenTab.STATS;

    public ShinobiStatsScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int tabY = this.height / 2 - 123;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Stats"),
                        button -> switchTab(ScreenTab.STATS)
                ).bounds(
                        centerX - 145,
                        tabY,
                        70,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Jutsu"),
                        button -> switchTab(ScreenTab.JUTSU)
                ).bounds(
                        centerX - 70,
                        tabY,
                        70,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Close"),
                        button -> this.onClose()
                ).bounds(
                        centerX - 60,
                        this.height / 2 + 92,
                        120,
                        20
                ).build()
        );
    }

    private void switchTab(ScreenTab newTab) {
        activeTab = newTab;
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
        int centerX = this.width / 2;
        int startY = this.height / 2 - 98;

        int panelLeft = centerX - 145;
        int panelTop = startY - 15;
        int panelRight = centerX + 145;
        int panelBottom = startY + 180;

        graphics.fill(
                panelLeft,
                panelTop,
                panelRight,
                panelBottom,
                0xAA000000
        );

        graphics.drawCenteredString(
                this.font,
                activeTab == ScreenTab.STATS
                        ? this.title
                        : Component.literal("Jutsu Slots"),
                centerX,
                startY,
                0xFFFFFF
        );

        if (activeTab == ScreenTab.STATS) {
            renderStatsTab(graphics, centerX, startY);
        } else {
            renderJutsuTab(graphics, centerX, startY);
        }

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    private void renderStatsTab(
            GuiGraphics graphics,
            int centerX,
            int startY
    ) {
        SyncStatsPayload stats = ClientShinobiStats.get();

        int leftX = centerX - 120;
        int rightX = centerX + 15;
        int textColor = 0xFFFFFF;
        int headingColor = 0xFFD36B;

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
                "Vitality: " + stats.vitality() + " hearts",
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
                "Chakra Control: " + stats.chakraControl(),
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
    }

    private void renderJutsuTab(
            GuiGraphics graphics,
            int centerX,
            int startY
    ) {
        int slotWidth = 122;
        int slotHeight = 29;
        int horizontalGap = 12;
        int verticalGap = 7;

        int gridLeft =
                centerX - slotWidth - horizontalGap / 2;

        int gridTop = startY + 25;

        for (int slot = 1;
             slot <= JutsuSlots.SLOT_COUNT;
             slot++) {
            int index = slot - 1;
            int column = index % 2;
            int row = index / 2;

            int left = gridLeft
                    + column * (slotWidth + horizontalGap);

            int top = gridTop
                    + row * (slotHeight + verticalGap);

            graphics.fill(
                    left,
                    top,
                    left + slotWidth,
                    top + slotHeight,
                    0xAA202020
            );

            graphics.drawString(
                    this.font,
                    "Slot " + slot
                            + ": "
                            + JutsuSlots.getAssignedJutsuName(slot),
                    left + 6,
                    top + 5,
                    0xFFFFFF
            );

            graphics.drawString(
                    this.font,
                    JutsuSlots.getSequenceText(slot),
                    left + 6,
                    top + 17,
                    0xA8D8FF
            );
        }

        graphics.drawCenteredString(
                this.font,
                "Enter three signs using Hand Sign 1 and Hand Sign 2",
                centerX,
                startY + 174,
                0xB0B0B0
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private enum ScreenTab {
        STATS,
        JUTSU
    }
}
