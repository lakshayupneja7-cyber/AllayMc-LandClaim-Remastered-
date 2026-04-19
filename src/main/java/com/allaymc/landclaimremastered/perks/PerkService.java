package com.allaymc.landclaimremastered.perks;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.config.MessageConfig;
import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.hooks.ClaimProviderManager;
import com.allaymc.landclaimremastered.model.*;
import com.allaymc.landclaimremastered.service.ClaimProfileService;
import com.allaymc.landclaimremastered.service.PlayerProgressService;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;
import java.util.UUID;

public final class PerkService {

    private final AllayClaimsPlugin plugin;
    private final ClaimProviderManager claimProviderManager;
    private final ClaimProfileService claimProfileService;
    private final PlayerProgressService playerProgressService;
    private final PerkRegistry perkRegistry;
    private final PluginConfig config;
    private final MessageConfig messages;

    public PerkService(AllayClaimsPlugin plugin,
                       ClaimProviderManager claimProviderManager,
                       ClaimProfileService claimProfileService,
                       PlayerProgressService playerProgressService,
                       PerkRegistry perkRegistry,
                       PluginConfig config,
                       MessageConfig messages) {
        this.plugin = plugin;
        this.claimProviderManager = claimProviderManager;
        this.claimProfileService = claimProfileService;
        this.playerProgressService = playerProgressService;
        this.perkRegistry = perkRegistry;
        this.config = config;
        this.messages = messages;
    }

    public Optional<ClaimContext> currentClaim(Player player) {
        if (!claimProviderManager.isAvailable()) return Optional.empty();
        return claimProviderManager.getActiveProvider().getClaimAt(player.getLocation());
    }

    public boolean isUnlocked(Player player, PerkKey key) {
        Optional<PerkDefinition> definition = perkRegistry.find(key);
        if (definition.isEmpty()) return false;
        return playerProgressService.currentTier(player).getLevel() >= definition.get().unlockTier().getLevel();
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
        if (!canReceive(player.getUniqueId(), context, profile)) {
            clearManagedEffects(player);
            return;
        }
        if (!isUnlocked(player, profile.getSelectedPerk())) {
            clearManagedEffects(player);
            return;
        }
        applyPerk(player, profile.getSelectedPerk());
    }

    private boolean canReceive(UUID uuid, ClaimContext context, ClaimProfile profile) {
        return switch (profile.getTrustMode()) {
            case OWNER_ONLY -> context.owner().equals(uuid);
            case ALL_TRUSTED -> context.isTrusted(uuid);
            case WHITELIST_ONLY -> context.owner().equals(uuid) || profile.getPerkWhitelist().contains(uuid);
        };
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
            default -> { }
        }
    }

    private PotionEffect effect(PotionEffectType type, int amplifier) {
        return new PotionEffect(type, (int) config.perkApplyRefreshTicks() + 20, amplifier, true, false, true);
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

    public boolean handleFoodPerks(Player player) {
        Optional<ClaimContext> context = currentClaim(player);
        if (context.isEmpty()) return false;
        ClaimProfile profile = claimProfileService.getOrCreate(context.get().claimId(), context.get().owner());
        return profile.getSelectedPerk() == PerkKey.HEARTHWARMTH && isUnlocked(player, PerkKey.HEARTHWARMTH);
    }

    public boolean handleFallPerks(Player player) {
        Optional<ClaimContext> context = currentClaim(player);
        if (context.isEmpty()) return false;
        ClaimProfile profile = claimProfileService.getOrCreate(context.get().claimId(), context.get().owner());
        return profile.getSelectedPerk() == PerkKey.FEATHERFALL_WARD && isUnlocked(player, PerkKey.FEATHERFALL_WARD);
    }

    public boolean handleSmeltPerk(Player player) {
        Optional<ClaimContext> context = currentClaim(player);
        if (context.isEmpty()) return false;
        ClaimProfile profile = claimProfileService.getOrCreate(context.get().claimId(), context.get().owner());
        return profile.getSelectedPerk() == PerkKey.IRON_RHYTHM && isUnlocked(player, PerkKey.IRON_RHYTHM);
    }
}
