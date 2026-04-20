package com.allaymc.landclaimremastered.model;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public record ClaimContext(
        String claimId,
        UUID ownerUuid,
        Set<UUID> trusted,
        int areaBlocks,
        String worldName
) {
    public ClaimContext {
        trusted = trusted == null
                ? Collections.emptySet()
                : Set.copyOf(trusted);
    }

    public boolean isOwner(UUID uuid) {
        return ownerUuid != null && ownerUuid.equals(uuid);
    }

    public boolean isTrusted(UUID uuid) {
        return isOwner(uuid) || trusted.contains(uuid);
    }
}
