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

    public FileConfiguration raw() {
        return plugin.getConfig();
    }

    public String databaseType() {
        return raw().getString("database.type", "SQLITE").toUpperCase(Locale.ROOT);
    }

    public String sqliteFile() {
        return raw().getString("database.sqlite-file", "claims.db");
    }

    public int perkSwitchCooldownSeconds() {
        return raw().getInt("perks.switch-cooldown-seconds", 30);
    }

    public int perkApplyRefreshTicks() {
        return raw().getInt("perks.apply-refresh-ticks", 40);
    }

    public ClaimTrustMode defaultTrustMode() {
        String value = raw().getString("perks.trusted-default-mode", "ALL_TRUSTED");
        try {
            return ClaimTrustMode.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return ClaimTrustMode.ALL_TRUSTED;
        }
    }

    public boolean actionbarEnterMessageEnabled() {
        return raw().getBoolean("ui.actionbar-enter-message", true);
    }

    public boolean showComingSoonTiers() {
        return raw().getBoolean("ui.show-coming-soon-tiers", true);
    }

    public Map<Tier, Integer> tierThresholds() {
        Map<Tier, Integer> map = new EnumMap<>(Tier.class);
        for (Tier tier : Tier.values()) {
            map.put(tier, raw().getInt("tiers." + tier.getLevel(), tier.getRequiredBlocks()));
        }
        return map;
    }
}
