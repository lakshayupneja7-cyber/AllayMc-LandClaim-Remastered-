package com.allaymc.landclaimremastered.listeners;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.model.ClaimContext;
import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.PerkDefinition;
import com.allaymc.landclaimremastered.model.PerkKey;
import com.allaymc.landclaimremastered.util.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerListener implements Listener {

    private final AllayClaimsPlugin plugin;
    private final Map<UUID, String> lastClaim = new ConcurrentHashMap<>();

    public PlayerListener(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getBootstrap().getPlayerProgressService().sync(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
        Player player = event.getPlayer();
        Optional<ClaimContext> context = plugin.getBootstrap().getPerkService().currentClaim(player);
        String newClaim = context.map(ClaimContext::claimId).orElse("NONE");
        String oldClaim = lastClaim.put(player.getUniqueId(), newClaim);
        plugin.getBootstrap().getPerkService().applyCurrentClaimPerk(player);

        if (!newClaim.equals(oldClaim) && !"NONE".equals(newClaim) && plugin.getBootstrap().getPluginConfig().actionBarEnterMessageEnabled()) {
            ClaimProfile profile = plugin.getBootstrap().getClaimProfileService().getOrCreate(context.get().claimId(), context.get().owner());
            if (profile.getSelectedPerk() != null) {
                String perk = plugin.getBootstrap().getPerkRegistry().find(profile.getSelectedPerk()).map(PerkDefinition::displayName).orElse(profile.getSelectedPerk().name());
                player.sendActionBar(Chat.mm(plugin.getBootstrap().getMessageConfig().get("inside-claim", "<gray>Claim perk active: <white>%perk%</white>").replace("%perk%", perk)));
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (plugin.getBootstrap().getPerkService().handleFoodPerks(player) && event.getFoodLevel() < 20) {
            event.setFoodLevel(Math.min(20, event.getFoodLevel() + 1));
        }
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && plugin.getBootstrap().getPerkService().handleFallPerks(player)) {
            event.setDamage(event.getDamage() * 0.5D);
        }
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        if (event.getBlock().getWorld() == null) return;
        plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> p.getWorld().equals(event.getBlock().getWorld()))
                .filter(p -> p.getLocation().distanceSquared(event.getBlock().getLocation()) <= 256)
                .findFirst()
                .ifPresent(player -> {
                    if (plugin.getBootstrap().getPerkService().handleSmeltPerk(player)) {
                        event.getResult().setAmount(Math.min(event.getResult().getMaxStackSize(), event.getResult().getAmount() + 1));
                    }
                });
    }
}
