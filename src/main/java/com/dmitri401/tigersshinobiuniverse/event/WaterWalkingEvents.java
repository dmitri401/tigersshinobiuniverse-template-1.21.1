package com.dmitri401.tigersshinobiuniverse.event;

import com.dmitri401.tigersshinobiuniverse.skill.WaterWalkingSkill;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class WaterWalkingEvents {

    private WaterWalkingEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        WaterWalkingSkill.tick(event.getEntity());
    }

    @SubscribeEvent
    public static void onLivingJump(
            LivingEvent.LivingJumpEvent event
    ) {
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
            WaterWalkingSkill.handleWaterJump(player);
        }
    }
}