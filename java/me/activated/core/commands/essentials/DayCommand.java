package me.activated.core.commands.essentials;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class DayCommand extends BaseCommand {

    @Command(name = "day")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.setWorldTime("DAY");
            player.setPlayerTime(0L, false);

            player.sendMessage(Language.PLAYER_DAY_SET.toString());
        });
    }
}
