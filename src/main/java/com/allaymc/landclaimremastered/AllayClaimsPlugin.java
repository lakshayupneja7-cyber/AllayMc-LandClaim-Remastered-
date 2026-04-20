package com.allaymc.landclaimremastered;

import com.allaymc.landclaimremastered.bootstrap.PluginBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public final class AllayClaimsPlugin extends JavaPlugin {

    private static AllayClaimsPlugin instance;
    private PluginBootstrap bootstrap;

    public static AllayClaimsPlugin get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResourceIfMissing("messages.yml");

        this.bootstrap = new PluginBootstrap(this);
        this.bootstrap.enable();
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) {
            bootstrap.disable();
        }
    }

    private void saveResourceIfMissing(String path) {
        if (!new java.io.File(getDataFolder(), path).exists()) {
            saveResource(path, false);
        }
    }

    public PluginBootstrap getBootstrap() {
        return bootstrap;
    }
}
