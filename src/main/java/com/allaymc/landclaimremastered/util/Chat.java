package com.allaymc.landclaimremastered.util;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;

public final class Chat {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private Chat() {
    }

    public static String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static Component mini(String input) {
        return MINI.deserialize(input);
    }

    public static Component message(AllayClaimsPlugin plugin, String key) {
        String prefix = plugin.getConfig().getString("prefix", "");
        String body = plugin.getConfig().getString(key, "<red>Missing message: " + key);
        return mini(prefix + body);
    }
}
