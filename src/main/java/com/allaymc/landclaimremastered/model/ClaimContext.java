package com.allaymc.landclaimremastered.model;

import org.bukkit.Location;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public record ClaimContext(
        String claimId,
        UUID owner,
        Set<UUID> trusted,
        int areaBlocks,
        String worldName,
        Location lesserCorner,
        Location greaterCorner
) {
    public ClaimContext {
        trusted = trusted == null ? Collections.emptySet() : Set.copyOf(trusted);
    }

    public boolean isTrusted(UUID uuid) {
        return owner.equals(uuid) || trusted.contains(uuid);
    }
}
