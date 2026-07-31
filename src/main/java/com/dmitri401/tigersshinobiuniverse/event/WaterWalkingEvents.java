package com.dmitri401.tigersshinobiuniverse.event;

import com.dmitri401.tigersshinobiuniverse.skill.WaterWalkingSkill;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class WaterWalkingEvents {

    private WaterWalkingEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WaterWalkingSkill.tick(player);
        }
    }

    @SubscribeEvent
    public static void onLivingJump(
            LivingEvent.LivingJumpEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WaterWalkingSkill.handleWaterJump(player);
        }
    }
}
