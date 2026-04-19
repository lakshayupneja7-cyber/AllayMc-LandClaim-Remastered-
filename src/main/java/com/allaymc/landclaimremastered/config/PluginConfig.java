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
        return raw().getString(ConfigKeys.DATABASE_TYPE, "SQLITE").toUpperCase(Locale.ROOT);
    }

    public String sqliteFile() {
        return raw().getString(ConfigKeys.DATABASE_SQLITE_FILE, "claims.db");
    }

    public String claimsProvider() {
        return raw().getString(ConfigKeys.CLAIMS_PROVIDER, "GRIEFPREVENTION");
    }

    public int perkSwitchCooldownSeconds() {
        return raw().getInt(ConfigKeys.PERKS_SWITCH_COOLDOWN_SECONDS, 30);
    }

    public int perkApplyRefreshTicks() {
        return raw().getInt(ConfigKeys.PERKS_APPLY_REFRESH_TICKS, 40);
    }

    public ClaimTrustMode defaultTrustMode() {
        String value = raw().getString(ConfigKeys.PERKS_TRUSTED_DEFAULT_MODE, "ALL_TRUSTED");
        try {
            return ClaimTrustMode.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return ClaimTrustMode.ALL_TRUSTED;
        }
    }

    public boolean scoreboardEnabled() {
        return raw().getBoolean(ConfigKeys.UI_SCOREBOARD_ENABLED, true);
    }

    public boolean actionbarEnterMessageEnabled() {
        return raw().getBoolean(ConfigKeys.UI_ACTIONBAR_ENTER_MESSAGE, true);
    }

    public boolean showComingSoonTiers() {
        return raw().getBoolean(ConfigKeys.UI_SHOW_COMING_SOON_TIERS, true);
    }

    public Map<Tier, Integer> tierThresholds() {
        Map<Tier, Integer> map = new EnumMap<>(Tier.class);
        for (Tier tier : Tier.values()) {
            map.put(tier, raw().getInt("tiers." + tier.getLevel(), tier.getRequiredBlocks()));
        }
        return map;
    }
}
