package com.allaymc.landclaimremastered.service;

import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.ClaimTrustMode;
import com.allaymc.landclaimremastered.model.PerkKey;
import com.allaymc.landclaimremastered.storage.ClaimRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClaimProfileService {

    private final ClaimRepository repository;
    private final Map<String, ClaimProfile> cache = new ConcurrentHashMap<>();

    public ClaimProfileService(ClaimRepository repository) {
        this.repository = repository;
    }

    public ClaimProfile getOrCreate(String claimId, UUID ownerUuid) {
        return cache.computeIfAbsent(claimId, id -> repository.find(id).orElseGet(() -> {
            ClaimProfile created = new ClaimProfile(id, ownerUuid, "Claim #" + id, ClaimTrustMode.ALL_TRUSTED);
            repository.save(created);
            return created;
        }));
    }

    public void setSelectedPerk(String claimId, UUID ownerUuid, PerkKey perkKey) {
        ClaimProfile profile = getOrCreate(claimId, ownerUuid);
        profile.setSelectedPerk(perkKey);
        repository.save(profile);
    }

    public void toggleTrustMode(String claimId, UUID ownerUuid) {
        ClaimProfile profile = getOrCreate(claimId, ownerUuid);
        ClaimTrustMode next = switch (profile.getTrustMode()) {
            case OWNER_ONLY -> ClaimTrustMode.ALL_TRUSTED;
            case ALL_TRUSTED -> ClaimTrustMode.WHITELIST_ONLY;
            case WHITELIST_ONLY -> ClaimTrustMode.OWNER_ONLY;
        };
        profile.setTrustMode(next);
        repository.save(profile);
    }

    public boolean addWhitelistPlayer(String claimId, UUID ownerUuid, UUID target) {
        ClaimProfile profile = getOrCreate(claimId, ownerUuid);
        boolean added = profile.getPerkWhitelist().add(target);
        if (added) repository.save(profile);
        return added;
    }

    public boolean removeWhitelistPlayer(String claimId, UUID ownerUuid, UUID target) {
        ClaimProfile profile = getOrCreate(claimId, ownerUuid);
        boolean removed = profile.getPerkWhitelist().remove(target);
        if (removed) repository.save(profile);
        return removed;
    }

    public List<UUID> getWhitelist(String claimId, UUID ownerUuid) {
        return new ArrayList<>(getOrCreate(claimId, ownerUuid).getPerkWhitelist());
    }
}
