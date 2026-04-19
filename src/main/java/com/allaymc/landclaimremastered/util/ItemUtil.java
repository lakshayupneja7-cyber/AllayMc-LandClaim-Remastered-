package com.allaymc.landclaimremastered.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ItemUtil {

    private ItemUtil() {
    }

    public static ItemStack item(Material material, String name, List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }

        meta.setDisplayName(Chat.colorize(name));
        meta.setLore(lore.stream().map(Chat::colorize).toList());
        stack.setItemMeta(meta);
        return stack;
    }
}
