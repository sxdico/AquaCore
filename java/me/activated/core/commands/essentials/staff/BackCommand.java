package me.activated.core.commands.essentials.staff;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class BackCommand extends BaseCommand {

    @Command(name = "back", permission = "Aqua.command.back", aliases = {"lastlocation", "backlocation"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null || playerData.getBackLocation() == null) {
            player.sendMessage(Language.BACK_CANT_FIND.toString());
            return;
        }
        player.teleport(playerData.getBackLocation());
        player.sendMessage(Language.BACK_TELEPORTED.toString());

        playerData.setBackLocation(null);
    }
}
