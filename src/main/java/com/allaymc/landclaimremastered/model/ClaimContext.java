package com.allaymc.landclaimremastered.model;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public final class ClaimContext {

    private final String claimId;
    private final UUID owner;
    private final Set<UUID> trusted;
    private final int areaBlocks;
    private final String worldName;

    public ClaimContext(String claimId, UUID owner, Set<UUID> trusted, int areaBlocks, String worldName) {
        this.claimId = claimId;
        this.owner = owner;
        this.trusted = trusted == null ? Collections.emptySet() : Set.copyOf(trusted);
        this.areaBlocks = areaBlocks;
        this.worldName = worldName;
    }

    public String claimId() {
        return claimId;
    }

    public UUID owner() {
        return owner;
    }

    public Set<UUID> trusted() {
        return trusted;
    }

    public int areaBlocks() {
        return areaBlocks;
    }

    public String worldName() {
        return worldName;
    }

    public boolean isTrusted(UUID uuid) {
        return owner.equals(uuid) || trusted.contains(uuid);
    }
}
