package com.allaymc.landclaimremastered.config;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class MessageConfig {

    private final AllayClaimsPlugin plugin;
    private FileConfiguration config;

    public MessageConfig(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String path, String fallback) {
        return config.getString(path, fallback);
    }
}
