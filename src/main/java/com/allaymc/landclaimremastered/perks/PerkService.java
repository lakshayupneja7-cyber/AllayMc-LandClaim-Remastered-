package com.allaymc.landclaimremastered.perks;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.config.MessageConfig;
import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.hooks.ClaimProviderManager;
import com.allaymc.landclaimremastered.model.ClaimContext;
import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.ClaimTrustMode;
import com.allaymc.landclaimremastered.model.PerkDefinition;
import com.allaymc.landclaimremastered.model.PerkKey;
import com.allaymc.landclaimremastered.model.Tier;
import com.allaymc.landclaimremastered.service.ClaimProfileService;
import com.allaymc.landclaimremastered.service.PlayerProgressService;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public final class PerkService {

    private final AllayClaimsPlugin plugin;
    private final ClaimProviderManager claimProviderManager;
    private final ClaimProfileService claimProfileService;
    private final PlayerProgressService playerProgressService;
    private final PerkRegistry perkRegistry;
    private final PluginConfig config;
    @SuppressWarnings("unused")
    private final MessageConfig messageConfig;

    public PerkService(
            AllayClaimsPlugin plugin,
            ClaimProviderManager claimProviderManager,
            ClaimProfileService claimProfileService,
            PlayerProgressService playerProgressService,
            PerkRegistry perkRegistry,
            PluginConfig config,
            MessageConfig messageConfig
    ) {
        this.plugin = plugin;
        this.claimProviderManager = claimProviderManager;
        this.claimProfileService = claimProfileService;
        this.playerProgressService = playerProgressService;
        this.perkRegistry = perkRegistry;
        this.config = config;
        this.messageConfig = messageConfig;
    }

    public Collection<PerkDefinition> allPerks() {
        return perkRegistry.all();
    }

    public Optional<PerkDefinition> get(PerkKey key) {
        return perkRegistry.get(key);
    }

    public Optional<ClaimContext> currentClaim(Player player) {
        if (!claimProviderManager.isAvailable()) {
            return Optional.empty();
        }
        return claimProviderManager.getProvider().getClaimAt(player.getLocation());
    }

    public boolean isUnlocked(Player player, PerkKey key) {
        Optional<PerkDefinition> definition = get(key);
        if (definition.isEmpty()) {
            return false;
        }
        Tier currentTier = playerProgressService.currentTier(player);
        return currentTier.level() >= definition.get().unlockTier().level();
    }

    private boolean canReceive(UUID uuid, ClaimContext context, ClaimProfile profile) {
        return switch (profile.getTrustMode()) {
            case OWNER_ONLY -> context.owner().equals(uuid);
            case ALL_TRUSTED -> context.isTrusted(uuid);
            case WHITELIST_ONLY -> context.owner().equals(uuid) || profile.getPerkWhitelist().contains(uuid);
        };
    }

    public Optional<PerkKey> activePerk(Player player) {
        Optional<ClaimContext> claimOptional = currentClaim(player);
        if (claimOptional.isEmpty()) {
            return Optional.empty();
        }

        ClaimContext context = claimOptional.get();
       
