package com.allaymc.landclaimremastered.util;

import com.allaymc.landclaimremastered.config.MessageConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;

public final class Chat {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private Chat() {
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component mm(String text) {
        return MINI.deserialize(text);
    }

    public static String message(MessageConfig config, String key, String def) {
        String prefix = config.get("prefix", "");
        String body = config.get(key, def);
        return color(prefix + body);
    }
}
