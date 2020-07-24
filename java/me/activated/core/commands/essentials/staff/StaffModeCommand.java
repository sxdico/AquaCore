package me.activated.core.commands.essentials.staff;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class StaffModeCommand extends BaseCommand {

    @Command(name = "staffmode", permission = "Aqua.command.staffmode", aliases = {"mod", "h", "staff"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.isInStaffMode()) {
            plugin.getStaffModeManagement().disableStaffMode(player);
            player.sendMessage(Language.STAFF_MODE_DISABLED.toString());
        } else {
            plugin.getStaffModeManagement().enableStaffMode(player);
            player.sendMessage(Language.STAFF_MODE_ENABLED.toString());
        }
    }
}
