package com.allaymc.landclaimremastered.service;

import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.ClaimTrustMode;
import com.allaymc.landclaimremastered.model.PerkKey;
import com.allaymc.landclaimremastered.storage.repository.ClaimProfileRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClaimProfileService {

    private final PluginConfig config;
    private final ClaimProfileRepository repository;
    private final Map<String, ClaimProfile> cache = new ConcurrentHashMap<>();

    public ClaimProfileService(PluginConfig config, ClaimProfileRepository repository) {
        this.config = config;
        this.repository = repository;
    }

    public ClaimProfile getOrCreate(String claimId, UUID ownerUuid) {
        return cache.computeIfAbsent(claimId, id -> repository.findById(id).orElseGet(() -> {
            ClaimProfile profile = new ClaimProfile(id, ownerUuid, config.defaultClaimName(), config.defaultTrustMode());
            repository.save(profile);
            return profile;
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
}
