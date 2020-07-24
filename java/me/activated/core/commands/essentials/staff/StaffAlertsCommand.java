package me.activated.core.commands.essentials.staff;

import me.activated.core.api.player.PlayerData;
import me.activated.core.menus.settings.StaffAlertsMenu;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class StaffAlertsCommand extends BaseCommand {

    @Command(name = "staffalerts", permission = "Aqua.command.staffalerts", aliases = {"tsm", "togglestaffmessages"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData == null) return;

            new StaffAlertsMenu(playerData).open(player);
        });
    }
}
