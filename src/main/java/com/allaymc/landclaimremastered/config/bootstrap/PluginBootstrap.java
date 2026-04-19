package com.allaymc.landclaimremastered.bootstrap;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.commands.ClaimAdminCommand;
import com.allaymc.landclaimremastered.commands.ClaimCommand;
import com.allaymc.landclaimremastered.commands.ClaimReloadCommand;
import com.allaymc.landclaimremastered.config.MessageConfig;
import com.allaymc.landclaimremastered.config.PluginConfig;
import com.allaymc.landclaimremastered.gui.GuiManager;
import com.allaymc.landclaimremastered.hooks.ClaimProviderManager;
import com.allaymc.landclaimremastered.listeners.GuiListener;
import com.allaymc.landclaimremastered.listeners.PlayerListener;
import com.allaymc.landclaimremastered.perks.PerkRegistry;
import com.allaymc.landclaimremastered.perks.PerkService;
import com.allaymc.landclaimremastered.placeholders.PlaceholderHook;
import com.allaymc.landclaimremastered.service.ClaimProfileService;
import com.allaymc.landclaimremastered.service.PlayerProgressService;
import com.allaymc.landclaimremastered.service.TierService;
import com.allaymc.landclaimremastered.storage.DatabaseManager;
import com.allaymc.landclaimremastered.storage.repository.ClaimProfileRepository;
import com.allaymc.landclaimremastered.storage.repository.PlayerProgressRepository;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

public final class PluginBootstrap {

    private final AllayClaimsPlugin plugin;

    private PluginConfig pluginConfig;
    private MessageConfig messageConfig;
    private DatabaseManager databaseManager;
    private ClaimProviderManager claimProviderManager;

    private TierService tierService;
    private PlayerProgressService playerProgressService;
    private ClaimProfileService claimProfileService;
    private PerkRegistry perkRegistry;
    private PerkService perkService;
    private GuiManager guiManager;

    public PluginBootstrap(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        this.pluginConfig = new PluginConfig(plugin);
        this.messageConfig = new MessageConfig(plugin);
        this.databaseManager = new DatabaseManager(plugin, pluginConfig);
        this.databaseManager.start();

        this.claimProviderManager = new ClaimProviderManager(plugin);
        this.claimProviderManager.load();

        this.tierService = new TierService(pluginConfig);
        this.playerProgressService = new PlayerProgressService(
                claimProviderManager,
                tierService,
                new PlayerProgressRepository(databaseManager)
        );
        this.claimProfileService = new ClaimProfileService(
                pluginConfig,
                new ClaimProfileRepository(databaseManager)
        );
        this.perkRegistry = new PerkRegistry();
        this.perkRegistry.registerDefaults();
        this.perkService = new PerkService(plugin, claimProviderManager, claimProfileService, playerProgressService, perkRegistry, pluginConfig, messageConfig);
        this.guiManager = new GuiManager(plugin, claimProviderManager, claimProfileService, playerProgressService, tierService, perkRegistry, perkService, pluginConfig, messageConfig);

        registerCommands();
        registerListeners();
        registerTasks();
        new PlaceholderHook(plugin).register();

        plugin.getLogger().info("AllayMc Land Claim Remastered enabled.");
    }

    public void disable() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
    }

    private void registerCommands() {
        PluginCommand claim = plugin.getCommand("claim");
        if (claim != null) claim.setExecutor(new ClaimCommand(plugin));

        PluginCommand reload = plugin.getCommand("claimreload");
        if (reload != null) reload.setExecutor(new ClaimReloadCommand(plugin));

        PluginCommand admin = plugin.getCommand("claimadmin");
        if (admin != null) admin.setExecutor(new ClaimAdminCommand(plugin));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new GuiListener(plugin), plugin);
    }

    private void registerTasks() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            playerProgressService.sync(player);
            perkService.applyCurrentClaimPerk(player);
        }), 20L, pluginConfig.perkApplyRefreshTicks());
    }

    public PluginConfig getPluginConfig() { return pluginConfig; }
    public MessageConfig getMessageConfig() { return messageConfig; }
    public ClaimProviderManager getClaimProviderManager() { return claimProviderManager; }
    public TierService getTierService() { return tierService; }
    public PlayerProgressService getPlayerProgressService() { return playerProgressService; }
    public ClaimProfileService getClaimProfileService() { return claimProfileService; }
    public PerkRegistry getPerkRegistry() { return perkRegistry; }
    public PerkService getPerkService() { return perkService; }
    public GuiManager getGuiManager() { return guiManager; }
}
