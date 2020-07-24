package me.activated.core.commands.rank;

import me.activated.core.api.ServerData;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.RankData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRankCommand extends BaseCommand {

    @Command(name = "setrank", permission = "Aqua.command.setrank", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length < 5) {
                sender.sendMessage(Color.translate("&cCorrect usage: /setrank <player> <rank> <duration> <server> <reason...>"));
                return;
            }
            RankData rankData = plugin.getRankManagement().getRank(args[1]);
            if (rankData == null) {
                sender.sendMessage(Language.RANK_NOT_EXISTS.toString()
                        .replace("<rank>", args[1]));
                return;
            }
            String durationTime = "";
            long duration = -1L;
            if (args[2].equalsIgnoreCase("perm") || args[2].equalsIgnoreCase("permanent")) {
                durationTime = "permanent";
            } else {
                try {
                    duration = System.currentTimeMillis() - DateUtils.parseDateDiff(args[2], false);
                } catch (Exception e) {
                    sender.sendMessage(Language.INVALID_TIME_DURAITON.toString());
                    return;
                }
            }
            ServerData serverData = plugin.getServerManagement().getServerData(args[3]);
            if (serverData == null && !args[3].equalsIgnoreCase("Global")) {
                sender.sendMessage(Language.SERVER_MANAGER_SERVER_DONT_EXISTS.toString());
                return;
            }
            String server = serverData != null ? serverData.getServerName() : "Global";
            String reason = StringUtils.buildMessage(args, 4);

            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                if (targetData.hasRank(rankData)) {
                    sender.sendMessage(Language.GRANT_PROCEDURE_ALREADY_HAVE_RANK.toString().replace("<player>", targetData.getPlayerName()));
                    return;
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                    if (!plugin.getRankManagement().canGrant(playerData, rankData) && !player.hasPermission("Aqua.grant.all")) {
                        player.sendMessage(Language.GRANT_PROCEDURE_CANT_GRANT.toString());
                        return;
                    }
                }
                plugin.getRankManagement().giveRank(sender, targetData, duration, durationTime.equalsIgnoreCase("permanent"), reason, rankData, server);
            } else {
                sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                if (targetData.hasRank(rankData)) {
                    sender.sendMessage(Language.GRANT_PROCEDURE_ALREADY_HAVE_RANK.toString().replace("<player>", targetData.getPlayerName()));
                    return;
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                    if (!plugin.getRankManagement().canGrant(playerData, rankData) && !player.hasPermission("Aqua.grant.all")) {
                        player.sendMessage(Language.GRANT_PROCEDURE_CANT_GRANT.toString());
                        return;
                    }
                }
                targetData.loadData();
                plugin.getRankManagement().giveRank(sender, targetData, duration, durationTime.equalsIgnoreCase("permanent"), reason, rankData, server);
            }
        });
    }
}
