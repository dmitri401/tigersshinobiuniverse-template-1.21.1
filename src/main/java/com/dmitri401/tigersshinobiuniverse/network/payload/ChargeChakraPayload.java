package com.dmitri401.tigersshinobiuniverse.network.payload;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChargeChakraPayload() implements CustomPacketPayload {

    public static final Type<ChargeChakraPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            TigersShinobiUniverse.MOD_ID,
                            "charge_chakra"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            ChargeChakraPayload
            > STREAM_CODEC =
            StreamCodec.unit(new ChargeChakraPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
