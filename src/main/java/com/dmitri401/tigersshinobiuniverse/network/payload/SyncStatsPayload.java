package com.dmitri401.tigersshinobiuniverse.network.payload;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncStatsPayload(
        boolean isNinja,
        int clanId,
        int level,
        int ninjaExperience,
        int chakra,
        int maxChakra,
        int ninjutsu,
        int taijutsu,
        int genjutsu,
        int strength,
        int agility,
        int vitality,
        int statPoints
) implements CustomPacketPayload {

    public static final Type<SyncStatsPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            TigersShinobiUniverse.MOD_ID,
                            "sync_stats"
                    )
            );

    public static final StreamCodec<ByteBuf, SyncStatsPayload> STREAM_CODEC =
            StreamCodec.of(
                    SyncStatsPayload::encode,
                    SyncStatsPayload::decode
            );

    private static void encode(
            ByteBuf buffer,
            SyncStatsPayload payload
    ) {
        ByteBufCodecs.BOOL.encode(buffer, payload.isNinja());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.clanId());

        ByteBufCodecs.VAR_INT.encode(buffer, payload.level());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.ninjaExperience());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.chakra());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.maxChakra());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.ninjutsu());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.taijutsu());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.genjutsu());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.strength());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.agility());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.vitality());
        ByteBufCodecs.VAR_INT.encode(buffer, payload.statPoints());
    }

    private static SyncStatsPayload decode(ByteBuf buffer) {
        return new SyncStatsPayload(
                ByteBufCodecs.BOOL.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),

                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer),
                ByteBufCodecs.VAR_INT.decode(buffer)
        );
    }

    public static SyncStatsPayload from(ShinobiStats stats) {
        return new SyncStatsPayload(
                stats.isNinja(),
                stats.getClan().getId(),
                stats.getLevel(),
                stats.getNinjaExperience(),
                stats.getChakra(),
                stats.getMaxChakra(),
                stats.getNinjutsu(),
                stats.getTaijutsu(),
                stats.getGenjutsu(),
                stats.getStrength(),
                stats.getAgility(),
                stats.getVitality(),
                stats.getStatPoints()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
