package com.allaymc.landclaimremastered.hooks;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.hooks.griefprevention.GriefPreventionProvider;

public final class ClaimProviderManager {

    private final AllayClaimsPlugin plugin;
    private ClaimProvider activeProvider;

    public ClaimProviderManager(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        ClaimProvider provider = new GriefPreventionProvider(plugin);
        if (provider.isAvailable()) {
            this.activeProvider = provider;
        }
    }

    public boolean isAvailable() {
        return activeProvider != null && activeProvider.isAvailable();
    }

    public ClaimProvider getActiveProvider() {
        return activeProvider;
    }

    public String getProviderName() {
        return activeProvider == null ? "NONE" : activeProvider.getName();
    }
}
