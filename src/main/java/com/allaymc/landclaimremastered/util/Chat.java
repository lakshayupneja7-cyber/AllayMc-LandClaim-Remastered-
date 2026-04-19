package com.allaymc.landclaimremastered.util;

import com.allaymc.landclaimremastered.config.MessageConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;

public final class Chat {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private Chat() {}

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component mm(String text) {
        return MINI.deserialize(text);
    }

    public static Component message(MessageConfig messages, String key, String fallback) {
        String prefix = messages.get("prefix", "");
        return MINI.deserialize(prefix + messages.get(key, fallback));
    }
}
