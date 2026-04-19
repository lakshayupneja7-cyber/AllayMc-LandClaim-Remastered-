package com.allaymc.landclaimremastered.bootstrap;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.commands.ClaimAdminCommand;
import com.allaymc.landclaimremastered.commands.ClaimCommand;
import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.hooks.ClaimProviderManager;
import com.allaymc.landclaimremastered.perks.PerkRegistry;
import com.allaymc.landclaimremastered.perks.PerkService;
import com.allaymc.landclaimremastered.service.ClaimProfileService;
import com.allaymc.landclaimremastered.service.PlayerProgressService;
import com.allaymc.landclaimremastered.service.TierService;
import com.allaymc.landclaimremastered.storage.DatabaseManager;
import com.allaymc.landclaimremastered.storage.repository.ClaimProfileRepository;
import com.allaymc.landclaimremastered.storage.repository.PlayerProgressRepository;
import org.bukkit.command.PluginCommand;

public final class PluginBootstrap {

    private final AllayClaimsPlugin plugin;

    private PluginConfig pluginConfig;
    private DatabaseManager databaseManager;
    private ClaimProviderManager claimProviderManager;

    private TierService tierService;
    private PlayerProgressService playerProgressService;
    private ClaimProfileService claimProfileService;
    private PerkRegistry perkRegistry;
    private PerkService perkService;

    public PluginBootstrap(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        this.pluginConfig = new PluginConfig(plugin);
        this.databaseManager = new DatabaseManager(plugin, pluginConfig);
        this.databaseManager.start();

        this.claimProviderManager = new ClaimProviderManager(plugin);
        this.claimProviderManager.load();

        this.tierService = new TierService(pluginConfig);
        this.playerProgressService = new PlayerProgressService(
                plugin,
                claimProviderManager,
                tierService,
                new PlayerProgressRepository(databaseManager)
        );
        this.claimProfileService = new ClaimProfileService(
                plugin,
                new ClaimProfileRepository(databaseManager)
        );
        this.perkRegistry = new PerkRegistry();
        this.perkRegistry.registerDefaults();
        this.perkService = new PerkService(
                plugin,
                claimProviderManager,
                claimProfileService,
                playerProgressService,
                perkRegistry,
                pluginConfig
        );

        registerCommands();

        if (!claimProviderManager.isAvailable()) {
            plugin.getLogger().severe("No supported claim provider found. Plugin will remain loaded but unusable.");
        } else {
            plugin.getLogger().info("Hooked into claim provider: " + claimProviderManager.getProviderName());
        }

        plugin.getLogger().info("AllayMc Land Claim Remastered enabled.");
    }

    public void disable() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
    }

    private void registerCommands() {
        PluginCommand claim = plugin.getCommand("claim");
        if (claim != null) {
            claim.setExecutor(new ClaimCommand(plugin));
        }

        PluginCommand claimAdmin = plugin.getCommand("claimadmin");
        if (claimAdmin != null) {
            claimAdmin.setExecutor(new ClaimAdminCommand(plugin));
        }
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ClaimProviderManager getClaimProviderManager() {
        return claimProviderManager;
    }

    public TierService getTierService() {
        return tierService;
    }

    public PlayerProgressService getPlayerProgressService() {
        return playerProgressService;
    }

    public ClaimProfileService getClaimProfileService() {
        return claimProfileService;
    }

    public PerkRegistry getPerkRegistry() {
        return perkRegistry;
    }

    public PerkService getPerkService() {
        return perkService;
    }
}
