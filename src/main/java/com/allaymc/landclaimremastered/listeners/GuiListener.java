package com.allaymc.landclaimremastered.listeners;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.model.ClaimContext;
import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.PerkDefinition;
import com.allaymc.landclaimremastered.util.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public final class GuiListener implements Listener {

    private final AllayClaimsPlugin plugin;

    public GuiListener(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTitle() == null) return;

        String title = event.getView().getTitle();
        if (!title.equals("AllayClaims")
                && !title.equals("Claim Perk Tree")
                && !title.equals("Claim Perks")
                && !title.equals("Claim Status")
                && !title.equals("Claim Settings")) {
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        Optional<ClaimContext> claimOptional = plugin.getBootstrap().getPerkService().currentClaim(player);
        if (claimOptional.isEmpty()) {
            player.closeInventory();
            player.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "no-claim-here", "<red>No claim here."));
            return;
        }

        ClaimContext context = claimOptional.get();
        ClaimProfile profile = plugin.getBootstrap().getClaimProfileService().getOrCreate(context.claimId(), context.owner());

        switch (title) {
            case "AllayClaims" -> handleMain(player, context, item);
            case "Claim Perk Tree" -> handleTree(player, context, item);
            case "Claim Status" -> handleStatus(player, context, item);
            case "Claim Settings" -> handleSettings(player, context, profile, item);
            case "Claim Perks" -> handlePerks(player, context, item);
        }
    }

    private void handleMain(Player player, ClaimContext context, ItemStack item) {
        switch (item.getType()) {
            case NETHER_STAR -> plugin.getBootstrap().getGuiManager().openTreeMenu(player);
            case BEACON -> plugin.getBootstrap().getGuiManager().openPerksMenu(player, context);
            case OAK_SIGN -> plugin.getBootstrap().getGuiManager().openStatusMenu(player, context);
            case COMPARATOR -> plugin.getBootstrap().getGuiManager().openSettingsMenu(player, context);
            default -> {
            }
        }
    }

    private void handleTree(Player player, ClaimContext context, ItemStack item) {
        if (item.getType() == Material.PAPER) {
            plugin.getBootstrap().getGuiManager().openMainMenu(player, context);
        }
    }

    private void handleStatus(Player player, ClaimContext context, ItemStack item) {
        if (item.getType() == Material.BLACK_STAINED_GLASS_PANE) return;
        plugin.getBootstrap().getGuiManager().openMainMenu(player, context);
    }

    private void handleSettings(Player player, ClaimContext context, ClaimProfile profile, ItemStack item) {
        if (item.getType() == Material.COMPARATOR) {
            if (!context.owner().equals(player.getUniqueId())) {
                player.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "owner-only", "<red>Owner only."));
                return;
            }

            plugin.getBootstrap().getClaimProfileService().toggleTrustMode(context.claimId(), context.owner());
            plugin.getBootstrap().getGuiManager().openSettingsMenu(player, context);
            return;
        }

        if (item.getType() != Material.BLACK_STAINED_GLASS_PANE) {
            plugin.getBootstrap().getGuiManager().openMainMenu(player, context);
        }
    }

    private void handlePerks(Player player, ClaimContext context, ItemStack item) {
        if (item.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            plugin.getBootstrap().getGuiManager().openMainMenu(player, context);
            return;
        }

        if (!context.owner().equals(player.getUniqueId())) {
            player.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "owner-only", "<red>Owner only."));
            return;
        }

        List<PerkDefinition> perks = plugin.getBootstrap().getPerkService().allPerks().stream().toList();
        for (PerkDefinition def : perks) {
            String stripped = org.bukkit.ChatColor.stripColor(item.getItemMeta() == null ? "" : item.getItemMeta().getDisplayName());
            if (!stripped.equalsIgnoreCase(def.displayName())) {
                continue;
            }

            if (!plugin.getBootstrap().getPerkService().isUnlocked(player, def.key())) {
                player.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "perk-locked", "<red>Locked."));
                return;
            }

            plugin.getBootstrap().getClaimProfileService().setSelectedPerk(context.claimId(), context.owner(), def.key());
            player.sendMessage(Chat.mm(
                    plugin.getBootstrap().getMessageConfig().get("prefix", "") +
                    plugin.getBootstrap().getMessageConfig().get("perk-set", "<green>Perk set.").replace("%perk%", def.displayName())
            ));
            plugin.getBootstrap().getGuiManager().openPerksMenu(player, context);
            return;
        }
    }
}
