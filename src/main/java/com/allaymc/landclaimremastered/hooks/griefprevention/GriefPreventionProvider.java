package com.allaymc.landclaimremastered.hooks.griefprevention;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.hooks.ClaimContext;
import com.allaymc.landclaimremastered.hooks.ClaimProvider;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class GriefPreventionProvider implements ClaimProvider {

    private final AllayClaimsPlugin plugin;
    private long lastWarningAt = 0L;

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
        try {
            Object gpInstance = getGpInstance();
            if (gpInstance == null) return Optional.empty();

            Object dataStore = getFieldValue(gpInstance, "dataStore");
            if (dataStore == null) return Optional.empty();

            Object claim = findClaimAt(dataStore, location);
            if (claim == null) return Optional.empty();

            Object parent = getFieldValue(claim, "parent");
            if (parent != null) return Optional.empty();

            Object ownerRaw = getFieldValue(claim, "ownerID");
            if (!(ownerRaw instanceof UUID ownerUuid)) return Optional.empty();

            String claimId = String.valueOf(invokeNoArgs(claim, "getID"));

            Set<UUID> trusted = new HashSet<>();
            collectTrust(claim, "getManagers", trusted);
            collectTrust(claim, "getBuilders", trusted);
            collectTrust(claim, "getContainers", trusted);
            collectTrust(claim, "getAccessors", trusted);

            Object lesser = invokeNoArgs(claim, "getLesserBoundaryCorner");
            Object greater = invokeNoArgs(claim, "getGreaterBoundaryCorner");
            if (lesser == null || greater == null) return Optional.empty();

            int lesserX = (int) invokeNoArgs(lesser, "getBlockX");
            int lesserZ = (int) invokeNoArgs(lesser, "getBlockZ");
            int greaterX = (int) invokeNoArgs(greater, "getBlockX");
            int greaterZ = (int) invokeNoArgs(greater, "getBlockZ");

            int width = Math.abs(greaterX - lesserX) + 1;
            int depth = Math.abs(greaterZ - lesserZ) + 1;
            int area = width * depth;

            String worldName = location.getWorld() == null ? "unknown" : location.getWorld().getName();

            return Optional.of(new ClaimContext(
                    claimId,
                    ownerUuid,
                    trusted,
                    area,
                    worldName
            ));
        } catch (Throwable throwable) {
            warnOnce("Failed to read GriefPrevention claim data: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int getTotalClaimBlocks(Player player) {
        try {
            Object gpInstance = getGpInstance();
            if (gpInstance == null) return 0;

            Object dataStore = getFieldValue(gpInstance, "dataStore");
            if (dataStore == null) return 0;

            Method getPlayerData = findMethod(dataStore.getClass(), "getPlayerData", 1);
            if (getPlayerData == null) return 0;

            Object playerData = getPlayerData.invoke(dataStore, player.getUniqueId());
            if (playerData == null) return 0;

            int accrued = readIntField(playerData, "accruedClaimBlocks");
            int bonus = readIntField(playerData, "bonusClaimBlocks");

            return Math.max(0, accrued + bonus);
        } catch (Throwable throwable) {
            warnOnce("Failed to read GriefPrevention player data: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            return 0;
        }
    }

    private Object findClaimAt(Object dataStore, Location location) throws Exception {
        // Try any getClaimAt overload dynamically
        for (Method method : dataStore.getClass().getMethods()) {
            if (!method.getName().equals("getClaimAt")) continue;

            Class<?>[] params = method.getParameterTypes();

            try {
                if (params.length == 3 && Location.class.isAssignableFrom(params[0]) && params[1] == boolean.class) {
                    return method.invoke(dataStore, location, false, null);
                }
                if (params.length == 2 && Location.class.isAssignableFrom(params[0]) && params[1] == boolean.class) {
                    return method.invoke(dataStore, location, false);
                }
                if (params.length == 1 && Location.class.isAssignableFrom(params[0])) {
                    return method.invoke(dataStore, location);
                }
            } catch (IllegalArgumentException ignored) {
                // try next overload
            }
        }

        // Fallback: scan all claims manually if GP overloads are weird
        Method getClaims = findMethod(dataStore.getClass(), "getClaims", 0);
        if (getClaims != null) {
            Object result = getClaims.invoke(dataStore);
            if (result instanceof Collection<?> claims) {
                for (Object claim : claims) {
                    if (claim == null) continue;
                    if (contains(claim, location)) {
                        return claim;
                    }
                }
            }
        }

        return null;
    }

    private boolean contains(Object claim, Location location) {
        try {
            Method contains = findMethod(claim.getClass(), "contains", 2);
            if (contains != null) {
                Object result = contains.invoke(claim, location, false);
                if (result instanceof Boolean bool) {
                    return bool;
                }
            }
        } catch (Throwable ignored) {
        }

        try {
            Object lesser = invokeNoArgs(claim, "getLesserBoundaryCorner");
            Object greater = invokeNoArgs(claim, "getGreaterBoundaryCorner");
            if (lesser == null || greater == null || location.getWorld() == null) return false;

            Object lesserWorld = invokeNoArgs(lesser, "getWorld");
            Object greaterWorld = invokeNoArgs(greater, "getWorld");
            if (lesserWorld == null || greaterWorld == null) return false;
            if (!location.getWorld().equals(lesserWorld) || !location.getWorld().equals(greaterWorld)) return false;

            int lx = (int) invokeNoArgs(lesser, "getBlockX");
            int lz = (int) invokeNoArgs(lesser, "getBlockZ");
            int gx = (int) invokeNoArgs(greater, "getBlockX");
            int gz = (int) invokeNoArgs(greater, "getBlockZ");

            int x = location.getBlockX();
            int z = location.getBlockZ();

            return x >= Math.min(lx, gx) && x <= Math.max(lx, gx)
                    && z >= Math.min(lz, gz) && z <= Math.max(lz, gz);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Object getGpInstance() throws Exception {
        Class<?> gpClass = Class.forName("me.ryanhamshire.GriefPrevention.GriefPrevention");
        Field instanceField = gpClass.getDeclaredField("instance");
        instanceField.setAccessible(true);
        return instanceField.get(null);
    }

    private void collectTrust(Object claim, String methodName, Set<UUID> out) {
        try {
            Object result = invokeNoArgs(claim, methodName);
            if (!(result instanceof Collection<?> collection)) return;

            for (Object entry : collection) {
                if (entry == null) continue;
                String name = String.valueOf(entry);
                if (name.isBlank()) continue;

                OfflinePlayer offline = plugin.getServer().getOfflinePlayer(name);
                UUID uuid = offline.getUniqueId();
                if (uuid != null) out.add(uuid);
            }
        } catch (Throwable ignored) {
        }
    }

    private Object invokeNoArgs(Object target, String methodName) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        method.setAccessible(true);
        return method.invoke(target);
    }

    private Object getFieldValue(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private int readIntField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(target);
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private Method findMethod(Class<?> type, String name, int parameterCount) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    private void warnOnce(String message) {
        long now = System.currentTimeMillis();
        if (now - lastWarningAt < 10000L) return;
        lastWarningAt = now;
        plugin.getLogger().warning(message);
    }
}
