package com.allaymc.landclaimremastered.service;

import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.model.Tier;

import java.util.Comparator;
import java.util.Map;

public final class TierService {

    private final Map<Tier, Integer> thresholds;

    public TierService(PluginConfig config) {
        this.thresholds = config.tierThresholds();
    }

    public Tier resolveTier(int totalClaimBlocks) {
        return thresholds.entrySet().stream()
                .filter(entry -> totalClaimBlocks >= entry.getValue())
                .map(Map.Entry::getKey)
                .max(Comparator.comparingInt(Tier::getLevel))
                .orElse(Tier.I);
    }

    public int threshold(Tier tier) {
        return thresholds.getOrDefault(tier, tier.getRequiredBlocks());
    }
}
