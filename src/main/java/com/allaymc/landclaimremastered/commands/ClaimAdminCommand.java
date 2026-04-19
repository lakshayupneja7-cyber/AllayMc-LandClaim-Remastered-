package com.allaymc.landclaimremastered.commands;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.util.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class ClaimAdminCommand implements CommandExecutor {

    private final AllayClaimsPlugin plugin;

    public ClaimAdminCommand(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("allayclaim.admin")) {
            sender.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "no-permission", "<red>No permission."));
            return true;
        }
        sender.sendMessage(Chat.colorize("&bAllayClaims &7admin tools are online."));
        return true;
    }
}
