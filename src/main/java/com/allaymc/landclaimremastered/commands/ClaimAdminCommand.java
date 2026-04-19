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
            sender.sendMessage(Chat.message(plugin, CommandMessages.NO_PERMISSION));
            return true;
        }

        sender.sendMessage(Chat.colorize("&bAllayClaims Admin &7is wired."));
        return true;
    }
}
