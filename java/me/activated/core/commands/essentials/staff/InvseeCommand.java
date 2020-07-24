package me.activated.core.commands.essentials.staff;

import me.activated.core.api.player.PlayerData;
import me.activated.core.menus.inventory.PlayerInventoryMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class InvseeCommand extends BaseCommand {

    @Command(name = "invsee", permission = "Aqua.command.invsee")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /invsee <player>"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

                new PlayerInventoryMenu(targetData).open(player);
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.getOfflineInventory().loadInventory();

                new PlayerInventoryMenu(targetData).open(player);
            }
        });
    }
}
