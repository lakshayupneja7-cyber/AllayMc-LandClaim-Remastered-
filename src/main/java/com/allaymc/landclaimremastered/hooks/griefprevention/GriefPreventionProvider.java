package com.allaymc.landclaimremastered.hook.gp;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.hook.ClaimProvider;
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
        return Optional.empty();
    }

    @Override
    public int getTotalClaimBlocks(Player player) {
        return 0;
    }
}
