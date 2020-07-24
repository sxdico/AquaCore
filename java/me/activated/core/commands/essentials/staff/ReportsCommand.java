package me.activated.core.commands.essentials.staff;

import me.activated.core.api.player.PlayerData;
import me.activated.core.menus.ReportsMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ReportsCommand extends BaseCommand {

    @Command(name = "reports", permission = "Aqua.command.reports")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /reports <player>"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                if (targetData.getReports().size() == 0) {
                    player.sendMessage(Language.REPORT_DOESNT_HAVE.toString().replace("<player>", targetData.getPlayerName()));
                    return;
                }
                new ReportsMenu(targetData).open(player);
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());

                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadData();
                if (targetData.getReports().size() == 0) {
                    player.sendMessage(Language.REPORT_DOESNT_HAVE.toString().replace("<player>", targetData.getPlayerName()));
                    plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
                    return;
                }
                new ReportsMenu(targetData).open(player);
            }
        });
    }
}
