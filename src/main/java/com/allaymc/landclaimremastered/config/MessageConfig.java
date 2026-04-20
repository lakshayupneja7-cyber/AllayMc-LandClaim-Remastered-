package com.allaymc.landclaimremastered.config;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class MessageConfig {

    private final AllayClaimsPlugin plugin;
    private final String fileName;
    private YamlConfiguration yaml;

    public MessageConfig(AllayClaimsPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        reload();
    }

    public MessageConfig(AllayClaimsPlugin plugin) {
        this(plugin, "messages.yml");
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String key, String def) {
        return yaml.getString(key, def);
    }
}
