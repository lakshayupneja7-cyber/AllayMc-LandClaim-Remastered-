package com.allaymc.landclaimremastered.service;

import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.model.Tier;

import java.util.Map;

public final class TierService {

    private final Map<Tier, Integer> thresholds;

    public TierService(PluginConfig config) {
        this.thresholds = config.tierThresholds();
    }

    public int requiredBlocks(Tier tier) {
        return thresholds.getOrDefault(tier, tier.getDefaultBlocks());
    }

    public Tier resolveTier(int totalClaimBlocks) {
        Tier result = Tier.I;
        for (Tier tier : Tier.values()) {
            if (totalClaimBlocks >= requiredBlocks(tier)) result = tier;
        }
        return result;
    }
}
