package com.allaymc.landclaimremastered.config;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.model.Tier;

public final class PluginConfig {

    private final AllayClaimsPlugin plugin;

    public PluginConfig(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    public int perkRefreshTicks() {
        return plugin.getConfig().getInt("perks.refresh-ticks", 40);
    }

    public int switchCooldownSeconds() {
        return plugin.getConfig().getInt("perks.switch-cooldown-seconds", 30);
    }

    public int requiredBlocks(Tier tier) {
        return plugin.getConfig().getInt("tiers." + tier.level(), tier.defaultBlocks());
    }
}
