package com.allaymc.landclaimremastered.gui;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.config.MessageConfig;
import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.hooks.ClaimProviderManager;
import com.allaymc.landclaimremastered.model.*;
import com.allaymc.landclaimremastered.perks.PerkRegistry;
import com.allaymc.landclaimremastered.perks.PerkService;
import com.allaymc.landclaimremastered.service.ClaimProfileService;
import com.allaymc.landclaimremastered.service.PlayerProgressService;
import com.allaymc.landclaimremastered.service.TierService;
import com.allaymc.landclaimremastered.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class GuiManager {

    public static final String MAIN_TITLE = "§bAllayClaims";
    public static final String TREE_TITLE = "§aClaim Perk Tree";
    public static final String PERKS_TITLE = "§dClaim Perks";
    public static final String STATUS_TITLE = "§eClaim Status";
    public static final String SETTINGS_TITLE = "§6Claim Settings";

    private final AllayClaimsPlugin plugin;
    private final ClaimProviderManager claimProviderManager;
    private final ClaimProfileService claimProfileService;
    private final PlayerProgressService playerProgressService;
    private final TierService tierService;
    private final PerkRegistry perkRegistry;
    private final PerkService perkService;
    private final PluginConfig config;
    private final MessageConfig messages;

    public GuiManager(AllayClaimsPlugin plugin, ClaimProviderManager claimProviderManager, ClaimProfileService claimProfileService,
                      PlayerProgressService playerProgressService, TierService tierService, PerkRegistry perkRegistry,
                      PerkService perkService, PluginConfig config, MessageConfig messages) {
        this.plugin = plugin;
        this.claimProviderManager = claimProviderManager;
        this.claimProfileService = claimProfileService;
        this.playerProgressService = playerProgressService;
        this.tierService = tierService;
        this.perkRegistry = perkRegistry;
        this.perkService = perkService;
        this.config = config;
        this.messages = messages;
    }

    public void openMainMenu(Player player, ClaimContext context) {
        ClaimProfile profile = claimProfileService.getOrCreate(context.claimId(), context.owner());
        Inventory inv = plugin.getServer().createInventory(player, 27, MAIN_TITLE);
        inv.setItem(10, ItemUtil.create(Material.NETHER_STAR, "&aClaim Perk Tree", List.of("&7See unlocked tiers", "&7and future rewards.")));
        inv.setItem(12, ItemUtil.create(Material.BEACON, "&dPerks", List.of("&7Choose the active perk", "&7for this claim.")));
        inv.setItem(14, ItemUtil.create(Material.OAK_SIGN, "&eClaim Status", List.of(
                "&7Name: &f" + profile.getName(),
                "&7Tier: &f" + playerProgressService.currentTier(player).name(),
                "&7Perk: &f" + (profile.getSelectedPerk() == null ? "None" : perkRegistry.find(profile.getSelectedPerk()).map(PerkDefinition::displayName).orElse(profile.getSelectedPerk().name()))
        )));
        inv.setItem(16, ItemUtil.create(Material.COMPARATOR, "&6Members & Settings", List.of("&7Switch trust mode", "&7for claim perks.")));
        player.openInventory(inv);
    }

    public void openTreeMenu(Player player) {
        Inventory inv = plugin.getServer().createInventory(player, 54, TREE_TITLE);
        Tier current = playerProgressService.currentTier(player);
        int total = playerProgressService.totalClaimBlocks(player);
        int[] slots = {10,11,12,13,14,15,28,29,30,31};
        for (Tier tier : Tier.values()) {
            boolean unlocked = current.getLevel() >= tier.getLevel();
            Material mat = unlocked ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;
            List<String> lore = new ArrayList<>();
            lore.add("&7Required Blocks: &f" + tierService.threshold(tier));
            lore.add("&7Status: " + (unlocked ? "&aUnlocked" : "&cLocked"));
            if (tier.getLevel() <= 5) {
                perkRegistry.all().stream().filter(p -> p.unlockTier() == tier)
                        .sorted(Comparator.comparing(PerkDefinition::displayName))
                        .forEach(p -> lore.add("&f- " + p.displayName()));
            } else if (config.showComingSoonTiers()) {
                lore.add("&6Coming Soon");
            }
            inv.setItem(slots[tier.getLevel() - 1], ItemUtil.create(mat, "&bTier " + tier.name(), lore));
        }
        inv.setItem(49, ItemUtil.create(Material.ARROW, "&cBack", List.of("&7Return to main menu.")));
        player.openInventory(inv);
    }

    public void openPerksMenu(Player player, ClaimContext context) {
        ClaimProfile profile = claimProfileService.getOrCreate(context.claimId(), context.owner());
        Inventory inv = plugin.getServer().createInventory(player, 54, PERKS_TITLE);
        int slot = 10;
        for (PerkDefinition def : perkRegistry.all()) {
            if (slot == 17 || slot == 26 || slot == 35) slot += 2;
            boolean unlocked = perkService.isUnlocked(player, def.key());
            boolean active = profile.getSelectedPerk() == def.key();
            List<String> lore = new ArrayList<>();
            lore.add("&7" + def.description());
            lore.add("&7Unlock Tier: &f" + def.unlockTier().name());
            lore.add(active ? "&aCurrently active" : unlocked ? "&eClick to activate" : "&cLocked");
            inv.setItem(slot++, ItemUtil.create(unlocked ? def.icon() : Material.BARRIER,
                    (active ? "&a" : unlocked ? "&f" : "&c") + def.displayName(), lore));
        }
        inv.setItem(49, ItemUtil.create(Material.ARROW, "&cBack", List.of("&7Return to main menu.")));
        player.openInventory(inv);
    }

    public void openStatusMenu(Player player, ClaimContext context) {
        ClaimProfile profile = claimProfileService.getOrCreate(context.claimId(), context.owner());
        Inventory inv = plugin.getServer().createInventory(player, 27, STATUS_TITLE);
        Tier tier = playerProgressService.currentTier(player);
        int total = playerProgressService.totalClaimBlocks(player);
        inv.setItem(11, ItemUtil.create(Material.PAPER, "&eClaim Identity", List.of(
                "&7Name: &f" + profile.getName(),
                "&7Claim ID: &f" + context.claimId(),
                "&7Area: &f" + context.areaBlocks()
        )));
        inv.setItem(13, ItemUtil.create(Material.DIAMOND, "&bProgress", List.of(
                "&7Tier: &f" + tier.name(),
                "&7Total Claim Blocks: &f" + total,
                "&7Next Unlock: &f" + nextUnlockText(tier)
        )));
        inv.setItem(15, ItemUtil.create(Material.BEACON, "&dCurrent Perk", List.of(
                "&7Perk: &f" + (profile.getSelectedPerk() == null ? "None" : perkRegistry.find(profile.getSelectedPerk()).map(PerkDefinition::displayName).orElse(profile.getSelectedPerk().name())),
                "&7Trust Mode: &f" + profile.getTrustMode().name()
        )));
        inv.setItem(22, ItemUtil.create(Material.ARROW, "&cBack", List.of("&7Return to main menu.")));
        player.openInventory(inv);
    }

    public void openSettingsMenu(Player player, ClaimContext context) {
        ClaimProfile profile = claimProfileService.getOrCreate(context.claimId(), context.owner());
        Inventory inv = plugin.getServer().createInventory(player, 27, SETTINGS_TITLE);
        inv.setItem(13, ItemUtil.create(Material.COMPARATOR, "&6Trust Mode", List.of(
                "&7Current: &f" + profile.getTrustMode().name(),
                "&eClick to cycle modes"
        )));
        inv.setItem(22, ItemUtil.create(Material.ARROW, "&cBack", List.of("&7Return to main menu.")));
        player.openInventory(inv);
    }

    private String nextUnlockText(Tier current) {
        if (current == Tier.X) return "Max Tier";
        return "Tier " + Tier.values()[current.ordinal() + 1].name();
    }
}
