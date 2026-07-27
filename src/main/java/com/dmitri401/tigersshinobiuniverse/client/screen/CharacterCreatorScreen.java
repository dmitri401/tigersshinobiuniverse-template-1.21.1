package com.dmitri401.tigersshinobiuniverse.client.screen;

import com.dmitri401.tigersshinobiuniverse.network.payload.CompleteCharacterCreationPayload;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiClan;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public final class CharacterCreatorScreen extends Screen {

    private static final Component TITLE =
            Component.translatable(
                    "screen.tigersshinobiuniverse.character_creator"
            );

    private ShinobiClan selectedClan =
            ShinobiClan.CLANLESS;

    private Button confirmButton;

    public CharacterCreatorScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int buttonWidth = 100;
        int buttonHeight = 20;
        int gap = 24;

        int leftX = centerX - buttonWidth - 4;
        int rightX = centerX + 4;
        int firstY = centerY - 35;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Uchiha"),
                        button -> selectClan(ShinobiClan.UCHIHA)
                ).bounds(
                        leftX,
                        firstY,
                        buttonWidth,
                        buttonHeight
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Hyuga"),
                        button -> selectClan(ShinobiClan.HYUGA)
                ).bounds(
                        rightX,
                        firstY,
                        buttonWidth,
                        buttonHeight
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Uzumaki"),
                        button -> selectClan(ShinobiClan.UZUMAKI)
                ).bounds(
                        leftX,
                        firstY + gap,
                        buttonWidth,
                        buttonHeight
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Clanless"),
                        button -> selectClan(ShinobiClan.CLANLESS)
                ).bounds(
                        rightX,
                        firstY + gap,
                        buttonWidth,
                        buttonHeight
                ).build()
        );

        confirmButton = this.addRenderableWidget(
                Button.builder(
                        Component.literal("Create Shinobi"),
                        button -> finishCharacterCreation()
                ).bounds(
                        centerX - 70,
                        centerY + 45,
                        140,
                        buttonHeight
                ).build()
        );
    }

    private void selectClan(ShinobiClan clan) {
        selectedClan = clan;

        confirmButton.setMessage(
                Component.literal(
                        "Create "
                                + clan.getDisplayName()
                                + " Shinobi"
                )
        );
    }

    private void finishCharacterCreation() {
        PacketDistributor.sendToServer(
                new CompleteCharacterCreationPayload(
                        selectedClan.getId()
                )
        );

        confirmButton.active = false;
    }

    @Override
    public void renderBackground(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        // Keeps the world visible without the normal blur.
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        graphics.fill(
                centerX - 125,
                centerY - 100,
                centerX + 125,
                centerY + 85,
                0xCC24160D
        );

        graphics.drawCenteredString(
                this.font,
                this.title,
                centerX,
                centerY - 80,
                0xFFFFFF
        );

        graphics.drawCenteredString(
                this.font,
                "Choose your clan",
                centerX,
                centerY - 63,
                0xDDDDDD
        );

        graphics.drawCenteredString(
                this.font,
                "Selected: "
                        + selectedClan.getDisplayName(),
                centerX,
                centerY + 18,
                0xFFD27F
        );

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}