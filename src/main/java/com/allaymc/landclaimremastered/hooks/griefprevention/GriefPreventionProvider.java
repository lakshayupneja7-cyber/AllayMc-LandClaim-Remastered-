package com.allaymc.landclaimremastered.hooks.griefprevention;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.hooks.ClaimProvider;
import com.allaymc.landclaimremastered.model.ClaimContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
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
        return Bukkit.getPluginManager().getPlugin("GriefPrevention") != null;
    }

    @Override
    public Optional<ClaimContext> getClaimAt(Location location) {
        try {
            Object gp = getGpInstance();
            if (gp == null) return Optional.empty();
            Object dataStore = gp.getClass().getField("dataStore").get(gp);
            Method getClaimAt = dataStore.getClass().getMethod("getClaimAt", Location.class, boolean.class, Object.class);
            Object claim = getClaimAt.invoke(dataStore, location, false, null);
            if (claim == null) return Optional.empty();

            Object parent = getFieldValue(claim, "parent");
            if (parent != null) return Optional.empty();

            UUID owner = (UUID) getFieldValue(claim, "ownerID");
            if (owner == null) return Optional.empty();

            Object idObj = invokeNoArgs(claim, "getID");
            String claimId = String.valueOf(idObj);

            Location lesser = (Location) invokeNoArgs(claim, "getLesserBoundaryCorner");
            Location greater = (Location) invokeNoArgs(claim, "getGreaterBoundaryCorner");
            if (lesser == null || greater == null) return Optional.empty();

            int width = Math.abs(lesser.getBlockX() - greater.getBlockX()) + 1;
            int depth = Math.abs(lesser.getBlockZ() - greater.getBlockZ()) + 1;
            int area = width * depth;

            Set<UUID> trusted = new HashSet<>();
            trusted.addAll(readTrustList(claim, "getManagers"));
            trusted.addAll(readTrustList(claim, "getBuilders"));
            trusted.addAll(readTrustList(claim, "getContainers"));
            trusted.addAll(readTrustList(claim, "getAccessors"));

            return Optional.of(new ClaimContext(
                    claimId,
                    owner,
                    trusted,
                    area,
                    location.getWorld() == null ? "unknown" : location.getWorld().getName(),
                    lesser,
                    greater
            ));
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to read GriefPrevention claim data: " + ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int getTotalClaimBlocks(Player player) {
        try {
            Object gp = getGpInstance();
            if (gp == null) return 0;
            Object dataStore = gp.getClass().getField("dataStore").get(gp);
            Method getPlayerData = dataStore.getClass().getMethod("getPlayerData", UUID.class);
            Object playerData = getPlayerData.invoke(dataStore, player.getUniqueId());
            if (playerData == null) return 0;

            int accrued = readInt(playerData, "accruedClaimBlocks");
            int bonus = readInt(playerData, "bonusClaimBlocks");
            int remaining = readInt(playerData, "remainingClaimBlocks");

            int totalOwned = accrued + bonus - Math.max(0, remaining);
            return Math.max(accrued + bonus, totalOwned);
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to read total claim blocks: " + ex.getMessage());
            return 0;
        }
    }

    private Object getGpInstance() throws Exception {
        Class<?> gpClass = Class.forName("me.ryanhamshire.GriefPrevention.GriefPrevention");
        Field instanceField = gpClass.getField("instance");
        return instanceField.get(null);
    }

    @SuppressWarnings("unchecked")
    private Set<UUID> readTrustList(Object claim, String methodName) {
        Set<UUID> result = new HashSet<>();
        try {
            Object raw = invokeNoArgs(claim, methodName);
            if (raw instanceof List<?> list) {
                for (Object entry : list) {
                    if (entry == null) continue;
                    OfflinePlayer offline = Bukkit.getOfflinePlayer(entry.toString());
                    if (offline.getUniqueId() != null) result.add(offline.getUniqueId());
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private Object getFieldValue(Object target, String name) throws Exception {
        Field field = target.getClass().getField(name);
        return field.get(target);
    }

    private Object invokeNoArgs(Object target, String name) throws Exception {
        Method method = target.getClass().getMethod(name);
        return method.invoke(target);
    }

    private int readInt(Object target, String fieldName) {
        try {
            Field f = target.getClass().getField(fieldName);
            return f.getInt(target);
        } catch (Exception ignored) {
            return 0;
        }
    }
}
