package com.allaymc.landclaimremastered;

import com.allaymc.landclaimremastered.bootstrap.PluginBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public final class AllayClaimsPlugin extends JavaPlugin {

    private static AllayClaimsPlugin instance;
    private PluginBootstrap bootstrap;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.bootstrap = new PluginBootstrap(this);
        this.bootstrap.enable();
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) {
            bootstrap.disable();
        }
    }

    public static AllayClaimsPlugin getInstance() {
        return instance;
    }

    public PluginBootstrap getBootstrap() {
        return bootstrap;
    }
}
