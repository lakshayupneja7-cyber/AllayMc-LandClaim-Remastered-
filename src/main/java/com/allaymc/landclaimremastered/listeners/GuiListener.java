package com.allaymc.landclaimremastered.listeners;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.gui.GuiManager;
import com.allaymc.landclaimremastered.model.ClaimContext;
import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.PerkDefinition;
import com.allaymc.landclaimremastered.model.PerkKey;
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
        String title = event.getView().getTitle();
        if (!List.of(GuiManager.MAIN_TITLE, GuiManager.TREE_TITLE, GuiManager.PERKS_TITLE, GuiManager.STATUS_TITLE, GuiManager.SETTINGS_TITLE).contains(title)) {
            return;
        }
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        Optional<ClaimContext> claimOptional = plugin.getBootstrap().getPerkService().currentClaim(player);
        if (claimOptional.isEmpty()) {
            player.closeInventory();
            return;
        }
        ClaimContext context = claimOptional.get();
        ClaimProfile profile = plugin.getBootstrap().getClaimProfileService().getOrCreate(context.claimId(), context.owner());

        switch (title) {
            case GuiManager.MAIN_TITLE -> handleMain(player, context, item);
            case GuiManager.TREE_TITLE -> handleTree(player, context, item);
            case GuiManager.PERKS_TITLE -> handlePerks(player, context, item);
            case GuiManager.STATUS_TITLE -> handleStatus(player, context, item);
            case GuiManager.SETTINGS_TITLE -> handleSettings(player, context, profile, item);
        }
    }

    private void handleMain(Player player, ClaimContext context, ItemStack item) {
        switch (item.getType()) {
            case NETHER_STAR -> plugin.getBootstrap().getGuiManager().openTreeMenu(player);
            case BEACON -> plugin.getBootstrap().getGuiManager().openPerksMenu(player, context);
            case OAK_SIGN -> plugin.getBootstrap().getGuiManager().openStatusMenu(player, context);
            case COMPARATOR -> plugin.getBootstrap().getGuiManager().openSettingsMenu(player, context);
            default -> { }
        }
    }

    private void handleTree(Player player, ClaimContext context, ItemStack item) {
        if (item.getType() == Material.ARROW) {
            plugin.getBootstrap().getGuiManager().openMainMenu(player, context);
        }
    }

    private void handleStatus(Player player, ClaimContext context, ItemStack item) {
        if (item.getType() == Material.ARROW) {
            plugin.getBootstrap().getGuiManager().openMainMenu(player, context);
        }
    }

    private void handleSettings(Player player, ClaimContext context, ClaimProfile profile, ItemStack item) {
        if (item.getType() == Material.ARROW) {
            plugin.getBootstrap().getGuiManager().openMainMenu(player, context);
            return;
        }
        if (item.getType() == Material.COMPARATOR) {
            if (!context.owner().equals(player.getUniqueId())) {
                player.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "owner-only", "<red>Owner only."));
                return;
            }
            plugin.getBootstrap().getClaimProfileService().toggleTrustMode(context.claimId(), context.owner());
            plugin.getBootstrap().getGuiManager().openSettingsMenu(player, context);
        }
    }

    private void handlePerks(Player player, ClaimContext context, ItemStack item) {
        if (item.getType() == Material.ARROW) {
            plugin.getBootstrap().getGuiManager().openMainMenu(player, context);
            return;
        }
        if (!context.owner().equals(player.getUniqueId())) {
            player.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "owner-only", "<red>Owner only."));
            return;
        }
        String stripped = item.getItemMeta() == null ? "" : item.getItemMeta().getDisplayName().replace("§a", "").replace("§f", "").replace("§c", "");
        for (PerkDefinition def : plugin.getBootstrap().getPerkRegistry().all()) {
            if (!stripped.contains(def.displayName())) continue;
            if (!plugin.getBootstrap().getPerkService().isUnlocked(player, def.key())) {
                player.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "perk-locked", "<red>Locked."));
                return;
            }
            plugin.getBootstrap().getClaimProfileService().setSelectedPerk(context.claimId(), context.owner(), def.key());
            player.sendMessage(Chat.mm(plugin.getBootstrap().getMessageConfig().get("prefix", "") + plugin.getBootstrap().getMessageConfig().get("perk-set", "<green>Perk set.").replace("%perk%", def.displayName())));
            plugin.getBootstrap().getGuiManager().openPerksMenu(player, context);
            return;
        }
    }
}
