package com.allaymc.landclaimremastered.config;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.model.Tier;

public final class PluginConfig {

    private final AllayClaimsPlugin plugin;

    public PluginConfig(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public String sqliteFile() {
        return plugin.getConfig().getString("database.sqlite-file", "claims.db");
    }

    public int perkRefreshTicks() {
        return plugin.getConfig().getInt("perks.refresh-ticks", 40);
    }

    public int switchCooldownSeconds() {
        return plugin.getConfig().getInt("perks.switch-cooldown-seconds", 30);
    }

    public boolean actionBarEnterMessageEnabled() {
        return plugin.getConfig().getBoolean("ui.actionbar-enter-message", true);
    }

    public int requiredBlocks(Tier tier) {
        return plugin.getConfig().getInt("tiers." + tier.level(), tier.defaultBlocks());
    }
}
