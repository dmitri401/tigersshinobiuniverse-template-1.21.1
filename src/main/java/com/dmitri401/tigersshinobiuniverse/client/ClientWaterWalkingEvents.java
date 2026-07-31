package com.dmitri401.tigersshinobiuniverse.client;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.client.network.ClientStatsSyncBridge;
import com.dmitri401.tigersshinobiuniverse.skill.WaterWalkingSkill;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(
        modid = TigersShinobiUniverse.MOD_ID,
        value = Dist.CLIENT
)
public final class ClientWaterWalkingEvents {

    private ClientWaterWalkingEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (ClientStatsSyncBridge.isNinja()
                && event.getEntity() instanceof LocalPlayer player) {
            WaterWalkingSkill.tick(player);
        }
    }

    @SubscribeEvent
    public static void onLivingJump(
            LivingEvent.LivingJumpEvent event
    ) {
        if (ClientStatsSyncBridge.isNinja()
                && event.getEntity() instanceof LocalPlayer player) {
            WaterWalkingSkill.handleWaterJump(player);
        }
    }
}
