package com.allaymc.landclaimremastered.hooks.griefprevention;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.hooks.ClaimProvider;
import com.allaymc.landclaimremastered.model.ClaimContext;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
        if (!isAvailable()) {
            return Optional.empty();
        }

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        if (claim == null || claim.parent != null || claim.ownerID == null) {
            return Optional.empty();
        }

        Set<UUID> trusted = new HashSet<>();
        claim.getManagers().forEach(name -> {
            UUID uuid = plugin.getServer().getOfflinePlayer(name).getUniqueId();
            if (uuid != null) trusted.add(uuid);
        });
        claim.getBuilders().forEach(name -> {
            UUID uuid = plugin.getServer().getOfflinePlayer(name).getUniqueId();
            if (uuid != null) trusted.add(uuid);
        });
        claim.getContainers().forEach(name -> {
            UUID uuid = plugin.getServer().getOfflinePlayer(name).getUniqueId();
            if (uuid != null) trusted.add(uuid);
        });
        claim.getAccessors().forEach(name -> {
            UUID uuid = plugin.getServer().getOfflinePlayer(name).getUniqueId();
            if (uuid != null) trusted.add(uuid);
        });

        int width = Math.abs(claim.getLesserBoundaryCorner().getBlockX() - claim.getGreaterBoundaryCorner().getBlockX()) + 1;
        int depth = Math.abs(claim.getLesserBoundaryCorner().getBlockZ() - claim.getGreaterBoundaryCorner().getBlockZ()) + 1;
        int area = width * depth;

        String claimId = String.valueOf(claim.getID());
        return Optional.of(new ClaimContext(
                claimId,
                claim.ownerID,
                trusted,
                area,
                location.getWorld() == null ? "unknown" : location.getWorld().getName()
        ));
    }

    @Override
    public int getTotalClaimBlocks(Player player) {
        if (!isAvailable()) {
            return 0;
        }

        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        return data.getRemainingClaimBlocks() + data.getAccruedClaimBlocks() - data.getBonusClaimBlocks();
    }
}
