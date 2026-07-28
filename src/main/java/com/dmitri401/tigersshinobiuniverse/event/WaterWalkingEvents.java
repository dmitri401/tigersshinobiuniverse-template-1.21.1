package com.dmitri401.tigersshinobiuniverse.event;

import com.dmitri401.tigersshinobiuniverse.skill.WaterWalkingSkill;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class WaterWalkingEvents {

    @SubscribeEvent
    public static void onPlayerTick(
            PlayerTickEvent.Post event
    ) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WaterWalkingSkill.tick(player);
        }
    }
}