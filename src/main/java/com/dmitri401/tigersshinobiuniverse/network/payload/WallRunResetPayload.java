package com.dmitri401.tigersshinobiuniverse.network.payload;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WallRunResetPayload()
        implements CustomPacketPayload {

    public static final Type<WallRunResetPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            TigersShinobiUniverse.MOD_ID,
                            "wall_run_reset"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            WallRunResetPayload
            > STREAM_CODEC =
            StreamCodec.unit(new WallRunResetPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
