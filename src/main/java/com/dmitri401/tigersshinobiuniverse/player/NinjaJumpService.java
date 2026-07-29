package com.dmitri401.tigersshinobiuniverse.player;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.attachment.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = TigersShinobiUniverse.MOD_ID)
public final class NinjaJumpService {

    /*
     * Approximate tuning:
     * quick release = roughly 2.5 blocks
     * full hold = roughly 5 blocks
     */
    private static final double INITIAL_VERTICAL_VELOCITY = 0.55D;
    private static final double HELD_BOOST_PER_TICK = 0.035D;
    private static final int MAXIMUM_BOOST_TICKS = 10;

    private static final Map<UUID, JumpState> JUMP_STATES =
            new ConcurrentHashMap<>();

    private NinjaJumpService() {
    }

    /*
     * The packet only records whether the jump key is currently held.
     * The actual start of a jump is detected with LivingJumpEvent.
     */
    public static void setJumpHeld(
            ServerPlayer player,
            boolean held
    ) {
        ShinobiStats stats = player.getData(
                ModAttachments.SHINOBI_STATS
        );

        if (!stats.isNinja()) {
            JUMP_STATES.remove(player.getUUID());
            return;
        }

        JumpState state = JUMP_STATES.computeIfAbsent(
                player.getUUID(),
                ignored -> new JumpState()
        );

        state.held = held;
    }

    /*
     * This fires at the moment vanilla performs a real ground jump.
     * It avoids relying on a network packet arriving while onGround is
     * still true.
     */
    @SubscribeEvent
    public static void onLivingJump(
            LivingEvent.LivingJumpEvent event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ShinobiStats stats = player.getData(
                ModAttachments.SHINOBI_STATS
        );

        if (!stats.isNinja() || !canNinjaJump(player)) {
            return;
        }

        Vec3 movement = player.getDeltaMovement();

        player.setDeltaMovement(
                movement.x,
                Math.max(
                        movement.y,
                        INITIAL_VERTICAL_VELOCITY
                ),
                movement.z
        );

        player.hurtMarked = true;

        JumpState state = JUMP_STATES.computeIfAbsent(
                player.getUUID(),
                ignored -> new JumpState()
        );

        state.jumpStarted = true;
        state.boostTicks = 0;
    }


    @SubscribeEvent
    public static void onLivingFall(
            LivingFallEvent event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ShinobiStats stats = player.getData(
                ModAttachments.SHINOBI_STATS
        );

        if (!stats.isNinja()) {
            return;
        }

        /*
         * Ninjas keep their fall distance for normal landing behavior,
         * but the damage multiplier is reduced to zero.
         */
        event.setDamageMultiplier(0.0F);
    }

    @SubscribeEvent
    public static void onPlayerTick(
            PlayerTickEvent.Post event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        JumpState state = JUMP_STATES.get(player.getUUID());

        if (state == null) {
            return;
        }

        ShinobiStats stats = player.getData(
                ModAttachments.SHINOBI_STATS
        );

        if (!stats.isNinja() || !canNinjaJump(player)) {
            JUMP_STATES.remove(player.getUUID());
            return;
        }

        if (player.onGround()) {
            state.jumpStarted = false;
            state.boostTicks = 0;
            return;
        }

        if (!state.held
                || !state.jumpStarted
                || state.boostTicks >= MAXIMUM_BOOST_TICKS) {
            return;
        }

        Vec3 movement = player.getDeltaMovement();

        if (movement.y <= 0.0D) {
            state.jumpStarted = false;
            return;
        }

        /*
         * Change only vertical motion and mark the velocity as changed.
         * hasImpulse synchronizes the new motion without using hurtMarked,
         * which was causing horizontal slowdown.
         */
        player.setDeltaMovement(
                movement.x,
                movement.y + HELD_BOOST_PER_TICK,
                movement.z
        );

        player.hasImpulse = true;
        state.boostTicks++;
    }

    private static boolean canNinjaJump(
            ServerPlayer player
    ) {
        return !player.getAbilities().flying
                && !player.isPassenger()
                && !player.isInWaterOrBubble()
                && !player.isFallFlying();
    }

    private static final class JumpState {
        private boolean held;
        private boolean jumpStarted;
        private int boostTicks;
    }
}
