package com.allaymc.landclaimremastered;

import com.allaymc.landclaimremastered.command.ClaimAdminCommand;
import com.allaymc.landclaimremastered.command.ClaimCommand;
import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.gui.GuiListener;
import com.allaymc.landclaimremastered.gui.MainMenu;
import com.allaymc.landclaimremastered.hook.ClaimProviderManager;
import com.allaymc.landclaimremastered.listener.PlayerActivityListener;
import com.allaymc.landclaimremastered.perk.PerkRegistry;
import com.allaymc.landclaimremastered.perk.PerkService;
import com.allaymc.landclaimremastered.service.ClaimProfileService;
import com.allaymc.landclaimremastered.service.PlayerProgressService;
import com.allaymc.landclaimremastered.service.TierService;
import com.allaymc.landclaimremastered.storage.DatabaseManager;
import com.allaymc.landclaimremastered.storage.repository.ClaimProfileRepository;
import com.allaymc.landclaimremastered.storage.repository.PlayerProgressRepository;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AllayClaimsPlugin extends JavaPlugin {

    private static AllayClaimsPlugin instance;

    private PluginConfig pluginConfig;
    private DatabaseManager databaseManager;
    private ClaimProviderManager claimProviderManager;
    private TierService tierService;
    private PlayerProgressService playerProgressService;
    private ClaimProfileService claimProfileService;
    private PerkRegistry perkRegistry;
    private PerkService perkService;
    private MainMenu mainMenu;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);

        this.pluginConfig = new PluginConfig(this);
        this.databaseManager = new DatabaseManager(this, pluginConfig);
        this.databaseManager.start();

        this.claimProviderManager = new ClaimProviderManager(this);
        this.claimProviderManager.load();

        this.tierService = new TierService(pluginConfig);
        this.playerProgressService = new PlayerProgressService(
                this,
                claimProviderManager,
                tierService,
                new PlayerProgressRepository(databaseManager)
        );
        this.claimProfileService = new ClaimProfileService(
                this,
                new ClaimProfileRepository(databaseManager)
        );

        this.perkRegistry = new PerkRegistry();
        this.perkRegistry.registerDefaults();

        this.perkService = new PerkService(
                this,
                claimProviderManager,
                claimProfileService,
                playerProgressService,
                perkRegistry,
                pluginConfig
        );

        this.mainMenu = new MainMenu(this);

        registerCommands();
        registerListeners();
        registerTasks();

        getLogger().info("AllayMc Land Claim Remastered enabled.");
        if (!claimProviderManager.isAvailable()) {
            getLogger().warning("No supported claim provider detected. Claim features will not work until one is present.");
        }
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
    }

    private void registerCommands() {
        PluginCommand claim = getCommand("claim");
        if (claim != null) {
            claim.setExecutor(new ClaimCommand(this));
        }

        PluginCommand claimAdmin = getCommand("claimadmin");
        if (claimAdmin != null) {
            claimAdmin.setExecutor(new ClaimAdminCommand(this));
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new GuiListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerActivityListener(this), this);
    }

    private void registerTasks() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                playerProgressService.sync(player);
                perkService.applyCurrentClaimPerk(player);
            });
        }, 40L, pluginConfig.perkApplyRefreshTicks());
    }

    public static AllayClaimsPlugin getInstance() {
        return instance;
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

    public MainMenu getMainMenu() {
        return mainMenu;
    }
}
