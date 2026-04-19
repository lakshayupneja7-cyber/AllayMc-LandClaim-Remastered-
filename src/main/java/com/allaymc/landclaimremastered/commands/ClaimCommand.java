package com.allaymc.landclaimremastered.commands;

import com.allaymc.landclaimremastered.AllayClaimsPlugin;
import com.allaymc.landclaimremastered.bootstrap.PluginBootstrap;
import com.allaymc.landclaimremastered.model.ClaimContext;
import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.Tier;
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
            sender.sendMessage(Chat.message(plugin, CommandMessages.PLAYERS_ONLY));
            return true;
        }

        PluginBootstrap bootstrap = plugin.getBootstrap();
        Optional<ClaimContext> claimOptional = bootstrap.getPerkService().currentClaim(player);

        if (claimOptional.isEmpty()) {
            player.sendMessage(Chat.message(plugin, CommandMessages.NO_CLAIM_HERE));
            return true;
        }

        ClaimContext context = claimOptional.get();
        ClaimProfile profile = bootstrap.getClaimProfileService().getOrCreate(context.claimId(), context.owner());
        Tier tier = bootstrap.getPlayerProgressService().currentTier(player);
        int total = bootstrap.getPlayerProgressService().totalClaimBlocks(player);

        player.sendMessage(Chat.colorize("&8&m------------------------------"));
        player.sendMessage(Chat.colorize("&b&lAllayClaims &7- &fCurrent Claim"));
        player.sendMessage(Chat.colorize("&7Claim ID: &f" + context.claimId()));
        player.sendMessage(Chat.colorize("&7Claim Name: &f" + profile.getName()));
        player.sendMessage(Chat.colorize("&7Tier: &f" + tier.name()));
        player.sendMessage(Chat.colorize("&7Total Claim Blocks: &f" + total));
        player.sendMessage(Chat.colorize("&7Selected Perk: &f" + (profile.getSelectedPerk() == null ? "None" : profile.getSelectedPerk().name())));
        player.sendMessage(Chat.colorize("&8&m------------------------------"));
        return true;
    }
}
