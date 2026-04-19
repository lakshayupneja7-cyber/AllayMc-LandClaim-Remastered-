package com.allaymc.landclaimremastered.service;

import com.allaymc.landclaimremastered.hooks.ClaimProviderManager;
import com.allaymc.landclaimremastered.model.Tier;
import com.allaymc.landclaimremastered.storage.repository.PlayerProgressRepository;
import org.bukkit.entity.Player;

public final class PlayerProgressService {

    private final ClaimProviderManager providerManager;
    private final TierService tierService;
    private final PlayerProgressRepository repository;

    public PlayerProgressService(ClaimProviderManager providerManager, TierService tierService, PlayerProgressRepository repository) {
        this.providerManager = providerManager;
        this.tierService = tierService;
        this.repository = repository;
    }

    public int totalClaimBlocks(Player player) {
        if (!providerManager.isAvailable()) return 0;
        return Math.max(0, providerManager.getActiveProvider().getTotalClaimBlocks(player));
    }

    public Tier currentTier(Player player) {
        return tierService.resolveTier(totalClaimBlocks(player));
    }

    public void sync(Player player) {
        repository.upsert(player.getUniqueId(), totalClaimBlocks(player), currentTier(player).getLevel());
    }
}
