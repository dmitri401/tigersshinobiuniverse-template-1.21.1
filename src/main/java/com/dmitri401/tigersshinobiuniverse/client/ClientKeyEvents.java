package com.dmitri401.tigersshinobiuniverse.client;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.client.network.ClientStatsSyncBridge;
import com.dmitri401.tigersshinobiuniverse.config.JutsuConfig;
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
import com.dmitri401.tigersshinobiuniverse.network.payload.HandSignInputPayload;
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
    private static int chargeRequestCooldown;
    private static final int[] CLIENT_HAND_SIGN_CHAIN = new int[3];
    private static int clientHandSignCount;
    private static int lastHandSignTick = Integer.MIN_VALUE;
    private static boolean hadPlayer;

    private ClientKeyEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            if (hadPlayer) {
                ClientStatsSyncBridge.reset();
            }

            hadPlayer = false;
            wasJumpDown = false;
            wasUseDown = false;
            chargeRequestCooldown = 0;
            clientJumpStarted = false;
            clientBoostTicks = 0;
            clientHandSignCount = 0;
            return;
        }

        hadPlayer = true;
        boolean isNinja = ClientStatsSyncBridge.isNinja();

        boolean jumpDown = minecraft.options.keyJump.isDown();

        if (isNinja && jumpDown != wasJumpDown) {
            PacketDistributor.sendToServer(
                    new NinjaJumpPayload(jumpDown)
            );

            wasJumpDown = jumpDown;
        } else if (!isNinja) {
            wasJumpDown = false;
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
            if (isNinja && ModKeyMappings.ALT.isDown()) {
                PacketDistributor.sendToServer(
                        new WallRunResetPayload()
                );
            }
        }

        boolean useDown = minecraft.options.keyUse.isDown();

        if (isNinja
                && !ModKeyMappings.ALT.isDown()
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
            if (isNinja) {
                enterHandSign(minecraft, 1);
            }
        }

        while (ModKeyMappings.HAND_SIGN_2.consumeClick()) {
            if (isNinja) {
                enterHandSign(minecraft, 2);
            }
        }

        /*
         * Do not send a charge packet every client tick. Send immediately
         * when charging begins, then at most once per second while held.
         */
        if (isNinja && ModKeyMappings.CHARGE.isDown()) {
            if (chargeRequestCooldown <= 0) {
                PacketDistributor.sendToServer(
                        new ChargeChakraPayload()
                );
                chargeRequestCooldown = 20;
            } else {
                chargeRequestCooldown--;
            }
        } else {
            chargeRequestCooldown = 0;
        }

        while (ModKeyMappings.MENU.consumeClick()) {
            ShinobiScreenRouter.requestMenuOpen();

            PacketDistributor.sendToServer(
                    new RequestStatsPayload()
            );
        }
    }


    private static void enterHandSign(
            Minecraft minecraft,
            int sign
    ) {
        if (minecraft.player.tickCount - lastHandSignTick
                > JutsuConfig.HAND_SIGN_TIMEOUT_TICKS) {
            clientHandSignCount = 0;
        }

        CLIENT_HAND_SIGN_CHAIN[clientHandSignCount] = sign;
        clientHandSignCount++;
        lastHandSignTick = minecraft.player.tickCount;

        PacketDistributor.sendToServer(
                new HandSignInputPayload(sign)
        );

        StringBuilder sequence = new StringBuilder(
                "Hand Signs: "
        );

        for (int index = 0;
             index < clientHandSignCount;
             index++) {
            if (index > 0) {
                sequence.append(" - ");
            }

            sequence.append(
                    CLIENT_HAND_SIGN_CHAIN[index]
            );
        }

        showMessage(
                minecraft,
                sequence.toString()
        );

        if (clientHandSignCount >= 3) {
            clientHandSignCount = 0;
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
