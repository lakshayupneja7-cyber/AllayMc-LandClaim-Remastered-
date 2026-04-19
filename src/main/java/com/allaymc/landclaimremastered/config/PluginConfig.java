package com.allaymc.landclaimremastered.config;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.model.ClaimTrustMode;
import com.allaymc.landclaimremastered.model.Tier;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class PluginConfig {

    private final AllayClaimsPlugin plugin;

    public PluginConfig(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    private FileConfiguration raw() {
        return plugin.getConfig();
    }

    public String sqliteFile() {
        return raw().getString("database.sqlite-file", "claims.db");
    }

    public int perkSwitchCooldownSeconds() {
        return raw().getInt("perks.switch-cooldown-seconds", 30);
    }

    public long perkApplyRefreshTicks() {
        return raw().getLong("perks.apply-refresh-ticks", 40L);
    }

    public ClaimTrustMode defaultTrustMode() {
        try {
            return ClaimTrustMode.valueOf(raw().getString("perks.trusted-default-mode", "ALL_TRUSTED").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return ClaimTrustMode.ALL_TRUSTED;
        }
    }

    public boolean actionBarEnterMessageEnabled() {
        return raw().getBoolean("ui.actionbar-enter-message", true);
    }

    public boolean showComingSoonTiers() {
        return raw().getBoolean("ui.show-coming-soon-tiers", true);
    }

    public String defaultClaimName() {
        return raw().getString("settings.default-claim-name", "Unnamed Claim");
    }

    public Map<Tier, Integer> tierThresholds() {
        Map<Tier, Integer> map = new EnumMap<>(Tier.class);
        for (Tier tier : Tier.values()) {
            map.put(tier, raw().getInt("tiers." + tier.getLevel(), tier.getRequiredBlocks()));
        }
        return map;
    }
}
