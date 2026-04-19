package com.allaymc.landclaimremastered.service;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.PerkKey;
import com.allaymc.landclaimremastered.storage.repository.ClaimProfileRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClaimProfileService {

    private final AllayClaimsPlugin plugin;
    private final ClaimProfileRepository repository;
    private final Map<String, ClaimProfile> cache = new ConcurrentHashMap<>();

    public ClaimProfileService(AllayClaimsPlugin plugin, ClaimProfileRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public ClaimProfile getOrCreate(String claimId, UUID ownerUuid) {
        return cache.computeIfAbsent(claimId, id -> repository.findById(id).orElseGet(() -> {
            ClaimProfile profile = new ClaimProfile(claimId, ownerUuid);
            repository.save(profile);
            return profile;
        }));
    }

    public void setSelectedPerk(String claimId, UUID ownerUuid, PerkKey perkKey) {
        ClaimProfile profile = getOrCreate(claimId, ownerUuid);
        profile.setSelectedPerk(perkKey);
        repository.save(profile);
    }
}
