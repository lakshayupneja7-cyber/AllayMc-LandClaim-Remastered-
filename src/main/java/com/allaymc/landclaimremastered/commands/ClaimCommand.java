package com.allaymc.landclaimremastered.commands;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.model.ClaimContext;
import com.allaymc.landclaimremastered.util.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class ClaimCommand implements CommandExecutor {

    private final AllayClaimsPlugin plugin;

    public ClaimCommand(AllayClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "players-only", "<red>Players only."));
            return true;
        }
        Optional<ClaimContext> context = plugin.getBootstrap().getPerkService().currentClaim(player);
        if (context.isEmpty()) {
            player.sendMessage(Chat.message(plugin.getBootstrap().getMessageConfig(), "no-claim-here", "<red>No claim here."));
            return true;
        }
        plugin.getBootstrap().getGuiManager().openMainMenu(player, context.get());
        return true;
    }
}
