package com.allaymc.landclaimremastered.commands;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.util.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class ClaimReloadCommand implements CommandExecutor {

    private final AllayClaimsPlugin plugin;

    public ClaimReloadCommand(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("allayclaim.reload")) {
            sender.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "no-permission", "<red>No permission."));
            return true;
        }
        plugin.getBootstrap().getPluginConfig().reload();
        plugin.getBootstrap().getMessageConfig().reload();
        sender.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "reloaded", "<green>Reloaded."));
        return true;
    }
}
