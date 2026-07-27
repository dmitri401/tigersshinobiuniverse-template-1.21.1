package com.dmitri401.tigersshinobiuniverse.network.payload;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record IncreaseStatPayload(int statId)
        implements CustomPacketPayload {

    public static final int NINJUTSU = 0;
    public static final int TAIJUTSU = 1;
    public static final int GENJUTSU = 2;
    public static final int STRENGTH = 3;
    public static final int AGILITY = 4;
    public static final int VITALITY = 5;

    public static final Type<IncreaseStatPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            TigersShinobiUniverse.MOD_ID,
                            "increase_stat"
                    )
            );

    public static final StreamCodec<ByteBuf, IncreaseStatPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    IncreaseStatPayload::statId,
                    IncreaseStatPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}