package me.activated.core.commands.permission;

import me.activated.core.api.player.GlobalPlayer;
import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class InfoCommand extends BaseCommand {

    @Command(name = "info", permission = "Aqua.command.info", aliases = "seen")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /info <player>"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(target.getName());

                plugin.getCoreConfig().getStringList("player-info-format").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("<player>", targetData.getPlayerName());
                    replacement.add("<lastIP>", targetData.getAddress());
                    replacement.add("<lastSeenAgo>", targetData.getLastSeenAgo());
                    replacement.add("<rank>", targetData.getHighestRank().getDisplayName());
                    replacement.add("<server>", globalPlayer != null ? globalPlayer.getServer() : Bukkit.getPlayer(target.getUniqueId()) != null ? plugin.getEssentialsManagement().getServerName() : "Offline [Unknown]");
                    replacement.add("<permissions>", StringUtils.getStringFromList(targetData.getPermissions()));
                    replacement.add("<firstJoined>", targetData.getFirstJoined());

                    player.sendMessage(replacement.toString());
                });
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());

                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());
                GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(target.getName());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadData();

                plugin.getCoreConfig().getStringList("player-info-format").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("<player>", targetData.getPlayerName());
                    replacement.add("<lastIP>", targetData.getAddress());
                    replacement.add("<lastSeenAgo>", targetData.getLastSeenAgo());
                    replacement.add("<rank>", targetData.getHighestRank().getDisplayName());
                    replacement.add("<server>", globalPlayer != null ? globalPlayer.getServer() : Bukkit.getPlayer(target.getUniqueId()) != null ? plugin.getEssentialsManagement().getServerName() : "Offline [Unknown]");
                    replacement.add("<permissions>", StringUtils.getStringFromList(targetData.getPermissions()));
                    replacement.add("<firstJoined>", targetData.getFirstJoined());

                    player.sendMessage(replacement.toString());
                });

                plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
            }
        });
    }
}
