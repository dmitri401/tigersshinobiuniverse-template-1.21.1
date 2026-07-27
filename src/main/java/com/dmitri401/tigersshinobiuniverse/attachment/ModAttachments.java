package com.dmitri401.tigersshinobiuniverse.attachment;

import com.dmitri401.tigersshinobiuniverse.TigersShinobiUniverse;
import com.dmitri401.tigersshinobiuniverse.player.ShinobiStats;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.ATTACHMENT_TYPES,
                    TigersShinobiUniverse.MOD_ID
            );

    public static final Supplier<AttachmentType<ShinobiStats>> SHINOBI_STATS =
            ATTACHMENTS.register(
                    "shinobi_stats",
                    () -> AttachmentType
                            .serializable(ShinobiStats::new)
                            .copyOnDeath()
                            .build()
            );

    private ModAttachments() {
    }
}