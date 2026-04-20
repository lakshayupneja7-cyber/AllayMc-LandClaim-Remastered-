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

    public Optional<PerkDefinition> getDefinition(PerkKey key) {
        return perkRegistry.find(key);
    }

    public Optional<ClaimContext> currentClaim(Player player) {
        if (!claimProviderManager.isAvailable()) {
            return Optional.empty();
        }
        return claimProviderManager.getProvider().getClaimAt(player.getLocation());
    }

    public boolean isUnlocked(Player player, PerkKey key) {
        Tier tier = playerProgressService.currentTier(player);
        Optional<PerkDefinition> definition = perkRegistry.find(key);
        return definition.isPresent() && tier.getLevel() >= definition.get().unlockTier().getLevel();
    }

    public Optional<PerkKey> activePerk(Player player) {
        Optional<ClaimContext> claimOptional = currentClaim(player);
        if (claimOptional.isEmpty()) {
            return Optional.empty();
        }

        ClaimContext context = claimOptional.get();
        ClaimProfile profile = claimProfileService.getOrCreate(context.claimId(), context.owner());

        if (profile.getSelectedPerk() == null) {
            return Optional.empty();
        }

        if (!canReceive(player.getUniqueId(), context, profile)) {
            return Optional.empty();
        }

        if (!isUnlocked(player, profile.getSelectedPerk())) {
            return Optional.empty();
        }

        return Optional.of(profile.getSelectedPerk());
    }

    private boolean canReceive(UUID uuid, ClaimContext context, ClaimProfile profile) {
        return switch (profile.getTrustMode()) {
            case OWNER_ONLY -> context.owner().equals(uuid);
            case ALL_TRUSTED -> context.isTrusted(uuid);
            case WHITELIST_ONLY -> context.owner().equals(uuid) || profile.getPerkWhitelist().contains(uuid);
        };
    }

    public void refreshPerk(Player player) {
        clearPotionPerks(player);

        Optional<PerkKey> perkOptional = activePerk(player);
        if (perkOptional.isEmpty()) {
            return;
        }

        PerkKey key = perkOptional.get();

        switch (key) {
            case SKYBOUND -> player.addPotionEffect(effect(PotionEffectType.JUMP_BOOST, 0));
            case TRAILBLAZER -> {
                player.addPotionEffect(effect(PotionEffectType.SPEED, 0));
                player.addPotionEffect(effect(PotionEffectType.JUMP_BOOST, 0));
            }
            case STONEHEART -> player.addPotionEffect(effect(PotionEffectType.RESISTANCE, 0));
            case DEEP_FOCUS -> player.addPotionEffect(effect(PotionEffectType.HASTE, 0));
            case WINDSTEP -> player.addPotionEffect(effect(PotionEffectType.SPEED, 0));
            case MOONSIGHT -> player.addPotionEffect(effect(PotionEffectType.NIGHT_VISION, 0));
            case BUILDERS_GRACE -> player.addPotionEffect(effect(PotionEffectType.HASTE, 0));
            case HEARTHLIGHT -> player.addPotionEffect(effect(PotionEffectType.REGENERATION, 0));
            case STORMSTRIDE -> player.addPotionEffect(effect(PotionEffectType.SPEED, 1));
            case TITAN_BLOOD -> player.addPotionEffect(effect(PotionEffectType.STRENGTH, 0));
            case EVERGLOW -> {
                player.addPotionEffect(effect(PotionEffectType.REGENERATION, 0));
                player.addPotionEffect(effect(PotionEffectType.NIGHT_VISION, 0));
            }
            default -> {
                // Custom-handled perks:
                // VERDANT_PULSE
                // IRON_RHYTHM
                // HEARTHWARMTH
                // FEATHERFALL_WARD
            }
        }
    }

    private PotionEffect effect(PotionEffectType type, int amplifier) {
        return new PotionEffect(
                type,
                config.perkApplyRefreshTicks() + 20,
                amplifier,
                true,
                false,
                true
        );
    }

    public void clearPotionPerks(Player player) {
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
        player.removePotionEffect(PotionEffectType.HASTE);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.removePotionEffect(PotionEffectType.STRENGTH);
    }

    public Optional<ClaimProfile> currentClaimProfile(Player player) {
        Optional<ClaimContext> context = currentClaim(player);
        if (context.isEmpty()) {
            return Optional.empty();
        }

        ClaimProfile profile = claimProfileService.getOrCreate(
                context.get().claimId(),
                context.get().owner()
        );
        return Optional.of(profile);
    }

    public Optional<PerkKey> selectedPerkForCurrentClaim(Player player) {
        Optional<ClaimContext> context = currentClaim(player);
        if (context.isEmpty()) {
            return Optional.empty();
        }

        ClaimProfile profile = claimProfileService.getOrCreate(
                context.get().claimId(),
                context.get().owner()
        );
        return Optional.ofNullable(profile.getSelectedPerk());
    }

    public boolean playerCanManageCurrentClaim(Player player) {
        Optional<ClaimContext> context = currentClaim(player);
        return context.filter(claimContext -> claimContext.owner().equals(player.getUniqueId())).isPresent();
    }
}
