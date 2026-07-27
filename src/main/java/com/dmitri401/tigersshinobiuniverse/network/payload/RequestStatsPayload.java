package com.dmitri401.tigersshinobiuniverse.network.payload;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestStatsPayload() implements CustomPacketPayload {

    public static final Type<RequestStatsPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            TigersShinobiUniverse.MOD_ID,
                            "request_stats"
                    )
            );

    public static final StreamCodec<ByteBuf, RequestStatsPayload> STREAM_CODEC =
            StreamCodec.unit(new RequestStatsPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}