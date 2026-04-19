package com.allaymc.landclaimremastered.perks;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.hooks.ClaimProviderManager;
import com.allaymc.landclaimremastered.model.ClaimContext;
import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.PerkDefinition;
import com.allaymc.landclaimremastered.model.PerkKey;
import com.allaymc.landclaimremastered.model.Tier;
import com.allaymc.landclaimremastered.service.ClaimProfileService;
import com.allaymc.landclaimremastered.service.PlayerProgressService;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public final class PerkService {

    private final AllayClaimsPlugin plugin;
    private final ClaimProviderManager claimProviderManager;
    private final ClaimProfileService claimProfileService;
    private final PlayerProgressService playerProgressService;
    private final PerkRegistry perkRegistry;
    private final PluginConfig config;

    public PerkService(
            AllayClaimsPlugin plugin,
            ClaimProviderManager claimProviderManager,
            ClaimProfileService claimProfileService,
            PlayerProgressService playerProgressService,
            PerkRegistry perkRegistry,
            PluginConfig config
    ) {
        this.plugin = plugin;
        this.claimProviderManager = claimProviderManager;
        this.claimProfileService = claimProfileService;
        this.playerProgressService = playerProgressService;
        this.perkRegistry = perkRegistry;
        this.config = config;
    }

    public Optional<ClaimContext> currentClaim(Player player) {
        if (!claimProviderManager.isAvailable()) {
            return Optional.empty();
        }
        return claimProviderManager.getActiveProvider().getClaimAt(player.getLocation());
    }

    public boolean canUsePerk(Player player, ClaimContext context, PerkKey perkKey) {
        Tier tier = playerProgressService.currentTier(player);
        Optional<PerkDefinition> definition = perkRegistry.find(perkKey);
        return definition.isPresent() && tier.getLevel() >= definition.get().unlockTier().getLevel();
    }

    public void applyCurrentClaimPerk(Player player) {
        Optional<ClaimContext> claimOptional = currentClaim(player);
        if (claimOptional.isEmpty()) {
            clearManagedEffects(player);
            return;
        }

        ClaimContext context = claimOptional.get();
        ClaimProfile profile = claimProfileService.getOrCreate(context.claimId(), context.owner());

        if (profile.getSelectedPerk() == null) {
            clearManagedEffects(player);
            return;
        }

        if (!context.isTrusted(player.getUniqueId())) {
            clearManagedEffects(player);
            return;
        }

        if (!canUsePerk(player, context, profile.getSelectedPerk())) {
            clearManagedEffects(player);
            return;
        }

        applyPerk(player, profile.getSelectedPerk());
    }

    public void applyPerk(Player player, PerkKey key) {
        clearManagedEffects(player);

        switch (key) {
            case SKYBOUND -> player.addPotionEffect(effect(PotionEffectType.JUMP_BOOST, 0));
            case TRAILBLAZER -> player.addPotionEffect(effect(PotionEffectType.SPEED, 0));
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
                // custom or future perks: Verdant Pulse, Iron Rhythm, Hearthwarmth, Featherfall Ward
                // these need event hooks or custom handlers rather than only potion effects.
            }
        }
    }

    private PotionEffect effect(PotionEffectType type, int amplifier) {
        return new PotionEffect(type, config.perkApplyRefreshTicks() + 20, amplifier, true, false, true);
    }

    public void clearManagedEffects(Player player) {
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
        player.removePotionEffect(PotionEffectType.HASTE);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.removePotionEffect(PotionEffectType.STRENGTH);
    }
}
