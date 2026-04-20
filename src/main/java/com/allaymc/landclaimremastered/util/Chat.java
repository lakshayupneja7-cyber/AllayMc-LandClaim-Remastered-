package com.allaymc.landclaimremastered.util;

import com.allaymc.landclaimremastered.config.MessageConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;

public final class Chat {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private Chat() {
    }

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static Component mm(String input) {
        return MINI.deserialize(input);
    }

    public static Component message(MessageConfig config, String key, String fallback) {
        String prefix = config.get("prefix", "");
        String body = config.get(key, fallback);
        return mm(color(prefix) + color(body));
    }
}
