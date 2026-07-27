package com.dmitri401.tigersshinobiuniverse.network.payload;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CompleteCharacterCreationPayload(
        int clanId
) implements CustomPacketPayload {

    public static final Type<CompleteCharacterCreationPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            TigersShinobiUniverse.MOD_ID,
                            "complete_character_creation"
                    )
            );

    public static final StreamCodec<
            ByteBuf,
            CompleteCharacterCreationPayload
            > STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    CompleteCharacterCreationPayload::clanId,
                    CompleteCharacterCreationPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}