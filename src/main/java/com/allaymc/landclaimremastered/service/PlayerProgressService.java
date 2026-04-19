package com.allaymc.landclaimremastered.service;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.hooks.ClaimProviderManager;
import com.allaymc.landclaimremastered.model.Tier;
import com.allaymc.landclaimremastered.storage.repository.PlayerProgressRepository;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PlayerProgressService {

    private final AllayClaimsPlugin plugin;
    private final ClaimProviderManager claimProviderManager;
    private final TierService tierService;
    private final PlayerProgressRepository repository;

    public PlayerProgressService(
            AllayClaimsPlugin plugin,
            ClaimProviderManager claimProviderManager,
            TierService tierService,
            PlayerProgressRepository repository
    ) {
        this.plugin = plugin;
        this.claimProviderManager = claimProviderManager;
        this.tierService = tierService;
        this.repository = repository;
    }

    public int totalClaimBlocks(Player player) {
        if (!claimProviderManager.isAvailable()) {
            return 0;
        }
        return claimProviderManager.getActiveProvider().getTotalClaimBlocks(player);
    }

    public Tier currentTier(Player player) {
        return tierService.resolveTier(totalClaimBlocks(player));
    }

    public void sync(Player player) {
        UUID uuid = player.getUniqueId();
        int total = totalClaimBlocks(player);
        Tier tier = currentTier(player);
        repository.upsert(uuid, total, tier.getLevel());
    }
}
