package com.dmitri401.tigersshinobiuniverse.network.payload;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WallRunSelectPayload(
        BlockPos blockPos,
        Direction face
) implements CustomPacketPayload {

    public static final Type<WallRunSelectPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            TigersShinobiUniverse.MOD_ID,
                            "wall_run_select"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            WallRunSelectPayload
            > STREAM_CODEC = StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    WallRunSelectPayload::blockPos,
                    Direction.STREAM_CODEC,
                    WallRunSelectPayload::face,
                    WallRunSelectPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
