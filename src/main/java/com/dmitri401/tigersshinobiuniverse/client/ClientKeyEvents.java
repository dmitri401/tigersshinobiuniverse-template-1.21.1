package com.dmitri401.tigersshinobiuniverse.client;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.client.network.ClientStatsSyncBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import gravitychanger.api.GravityChangerAPI;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import com.dmitri401.tigersshinobiuniverse.network.payload.RequestStatsPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.ChargeChakraPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.NinjaJumpPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.WallRunSelectPayload;
import com.dmitri401.tigersshinobiuniverse.network.payload.WallRunResetPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import com.dmitri401.tigersshinobiuniverse.client.screen.ShinobiScreenRouter;

@EventBusSubscriber(
        modid = TigersShinobiUniverse.MOD_ID,
        value = Dist.CLIENT
)
public final class ClientKeyEvents {

    private static final double CLIENT_INITIAL_JUMP_VELOCITY = 0.55D;
    private static final double CLIENT_HELD_BOOST_PER_TICK = 0.05D;
    private static final int CLIENT_MAXIMUM_BOOST_TICKS = 10;

    private static boolean wasJumpDown;
    private static boolean clientJumpStarted;
    private static int clientBoostTicks;
    private static boolean wasUseDown;

    private ClientKeyEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            wasJumpDown = false;
            wasUseDown = false;
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

        /*
         * Holding the configurable Alt key and pressing Wall Run cancels
         * wall running. The server remains authoritative over the reset.
         */
        while (ModKeyMappings.WALL_RUN.consumeClick()) {
            if (ModKeyMappings.ALT.isDown()) {
                PacketDistributor.sendToServer(
                        new WallRunResetPayload()
                );
            }
        }

        boolean useDown = minecraft.options.keyUse.isDown();

        if (!ModKeyMappings.ALT.isDown()
                && ModKeyMappings.WALL_RUN.isDown()
                && useDown
                && !wasUseDown
                && minecraft.hitResult instanceof BlockHitResult blockHit
                && blockHit.getType() == HitResult.Type.BLOCK) {
            PacketDistributor.sendToServer(
                    new WallRunSelectPayload(
                            blockHit.getBlockPos(),
                            blockHit.getDirection()
                    )
            );
        }

        wasUseDown = useDown;

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

        Vec3 jumpDirection = Vec3.atLowerCornerOf(
                GravityChangerAPI.getGravityDirection(
                        minecraft.player
                ).getOpposite().getNormal()
        );

        if (minecraft.player.onGround()) {
            clientJumpStarted = false;
            clientBoostTicks = 0;

            if (jumpDown) {
                applyMinimumJumpVelocity(
                        minecraft.player,
                        jumpDirection
                );
                clientJumpStarted = true;
            }

            return;
        }

        Vec3 worldMovement =
                GravityChangerAPI.getWorldVelocity(
                        minecraft.player
                );
        double jumpSpeed = worldMovement.dot(jumpDirection);

        if (jumpDown
                && !clientJumpStarted
                && jumpSpeed > 0.0D) {
            applyMinimumJumpVelocity(
                    minecraft.player,
                    jumpDirection
            );
            clientJumpStarted = true;
        }

        if (!jumpDown
                || !clientJumpStarted
                || clientBoostTicks >= CLIENT_MAXIMUM_BOOST_TICKS) {
            return;
        }

        worldMovement = GravityChangerAPI.getWorldVelocity(
                minecraft.player
        );
        jumpSpeed = worldMovement.dot(jumpDirection);

        if (jumpSpeed <= 0.0D) {
            clientJumpStarted = false;
            return;
        }

        GravityChangerAPI.setWorldVelocity(
                minecraft.player,
                worldMovement.add(
                        jumpDirection.scale(
                                CLIENT_HELD_BOOST_PER_TICK
                        )
                )
        );

        clientBoostTicks++;
    }

    private static void applyMinimumJumpVelocity(
            net.minecraft.world.entity.player.Player player,
            Vec3 jumpDirection
    ) {
        Vec3 worldMovement =
                GravityChangerAPI.getWorldVelocity(player);
        double currentJumpSpeed =
                worldMovement.dot(jumpDirection);
        double additionalSpeed = Math.max(
                0.0D,
                CLIENT_INITIAL_JUMP_VELOCITY
                        - currentJumpSpeed
        );

        GravityChangerAPI.setWorldVelocity(
                player,
                worldMovement.add(
                        jumpDirection.scale(additionalSpeed)
                )
        );
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
