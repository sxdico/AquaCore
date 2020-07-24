package me.activated.core.commands.punishments;

import me.activated.core.api.player.PlayerData;
import me.activated.core.punishments.menus.staffhistory.StaffHistoryMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StaffHistoryCommand extends BaseCommand {

    @Command(name = "staffhistory", permission = "punishments.command.staffhistory")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () ->{
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /staffhistory <player>"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                new StaffHistoryMenu(targetData).open(player);
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadPunishmentsPerformed();
                new StaffHistoryMenu(targetData).open(player);
            }
        });
    }
}
