package com.allaymc.landclaimremastered.hook;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.hook.gp.GriefPreventionProvider;

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
}
