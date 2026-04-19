package com.allaymc.landclaimremastered.hooks.griefprevention;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.hooks.ClaimProvider;
import com.allaymc.landclaimremastered.model.ClaimContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class GriefPreventionProvider implements ClaimProvider {

    private final AllayClaimsPlugin plugin;

    public GriefPreventionProvider(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "GriefPrevention";
    }

    @Override
    public boolean isAvailable() {
        return plugin.getServer().getPluginManager().getPlugin("GriefPrevention") != null;
    }

    @Override
    public Optional<ClaimContext> getClaimAt(Location location) {
        // Temporary safe implementation.
        // Real GriefPrevention hook should be added once dependency access is set up properly.
        return Optional.empty();
    }

    @Override
    public int getTotalClaimBlocks(Player player) {
        // Temporary safe implementation.
        return 0;
    }
}
