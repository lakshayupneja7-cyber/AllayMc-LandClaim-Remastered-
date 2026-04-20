package com.allaymc.landclaimremastered.config;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class MessageConfig {

    private final AllayClaimsPlugin plugin;
    private YamlConfiguration yaml;

    public MessageConfig(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String key, String fallback) {
        return yaml.getString(key, fallback);
    }
}
