package com.dmitri401.tigersshinobiuniverse.client;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.client.network.ClientStatsSyncBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import com.dmitri401.tigersshinobiuniverse.network.payload.RequestStatsPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.ChargeChakraPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.NinjaJumpPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import com.dmitri401.tigersshinobiuniverse.client.screen.ShinobiScreenRouter;

@EventBusSubscriber(
        modid = TigersShinobiUniverse.MOD_ID,
        value = Dist.CLIENT
)
public final class ClientKeyEvents {

    private static final double CLIENT_INITIAL_JUMP_VELOCITY = 0.55D;
    private static final double CLIENT_HELD_BOOST_PER_TICK = 0.035D;
    private static final int CLIENT_MAXIMUM_BOOST_TICKS = 10;

    private static boolean wasJumpDown;
    private static boolean clientJumpStarted;
    private static int clientBoostTicks;

    private ClientKeyEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            wasJumpDown = false;
            return;
        }

        boolean jumpDown = minecraft.options.keyJump.isDown();

        if (jumpDown != wasJumpDown) {
            PacketDistributor.sendToServer(
                    new NinjaJumpPayload(jumpDown)
            );

            wasJumpDown = jumpDown;
        }

        handleClientNinjaJump(
                minecraft,
                jumpDown
        );

        while (ModKeyMappings.HAND_SIGN_1.consumeClick()) {
            showMessage(minecraft, "Hand Sign 1");
        }

        while (ModKeyMappings.HAND_SIGN_2.consumeClick()) {
            showMessage(minecraft, "Hand Sign 2");
        }

        if (ModKeyMappings.CHARGE.isDown()) {
            PacketDistributor.sendToServer(
                    new ChargeChakraPayload()
            );
        }

        while (ModKeyMappings.MENU.consumeClick()) {
            ShinobiScreenRouter.requestMenuOpen();

            PacketDistributor.sendToServer(
                    new RequestStatsPayload()
            );
        }
    }


    private static void handleClientNinjaJump(
            Minecraft minecraft,
            boolean jumpDown
    ) {
        if (!ClientStatsSyncBridge.isNinja()) {
            clientJumpStarted = false;
            clientBoostTicks = 0;
            return;
        }

        if (minecraft.player.onGround()) {
            /*
             * Reset after landing. When jump is held, vanilla starts the
             * jump and this client-side correction raises it to the ninja
             * minimum immediately.
             */
            clientJumpStarted = false;
            clientBoostTicks = 0;

            if (jumpDown) {
                Vec3 movement = minecraft.player.getDeltaMovement();

                minecraft.player.setDeltaMovement(
                        movement.x,
                        Math.max(
                                movement.y,
                                CLIENT_INITIAL_JUMP_VELOCITY
                        ),
                        movement.z
                );

                clientJumpStarted = true;
            }

            return;
        }

        /*
         * The first airborne tick can occur before the grounded branch sees
         * the vanilla jump, so initialize here as a fallback.
         */
        if (jumpDown
                && !clientJumpStarted
                && minecraft.player.getDeltaMovement().y > 0.0D) {
            Vec3 movement = minecraft.player.getDeltaMovement();

            minecraft.player.setDeltaMovement(
                    movement.x,
                    Math.max(
                            movement.y,
                            CLIENT_INITIAL_JUMP_VELOCITY
                    ),
                    movement.z
            );

            clientJumpStarted = true;
        }

        if (!jumpDown
                || !clientJumpStarted
                || clientBoostTicks >= CLIENT_MAXIMUM_BOOST_TICKS) {
            return;
        }

        Vec3 movement = minecraft.player.getDeltaMovement();

        if (movement.y <= 0.0D) {
            clientJumpStarted = false;
            return;
        }

        minecraft.player.setDeltaMovement(
                movement.x,
                movement.y + CLIENT_HELD_BOOST_PER_TICK,
                movement.z
        );

        clientBoostTicks++;
    }

    private static void showMessage(
            Minecraft minecraft,
            String message
    ) {
        minecraft.player.displayClientMessage(
                Component.literal(message),
                true
        );
    }
}
