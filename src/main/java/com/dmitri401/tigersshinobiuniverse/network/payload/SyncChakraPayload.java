package com.dmitri401.tigersshinobiuniverse.network.payload;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Small frequent update for the HUD. Permanent stats use SyncStatsPayload.
 */
public record SyncChakraPayload(
        int chakra,
        int maxChakra
) implements CustomPacketPayload {

    public static final Type<SyncChakraPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            TigersShinobiUniverse.MOD_ID,
                            "sync_chakra"
                    )
            );

    public static final StreamCodec<ByteBuf, SyncChakraPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    SyncChakraPayload::chakra,
                    ByteBufCodecs.VAR_INT,
                    SyncChakraPayload::maxChakra,
                    SyncChakraPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
