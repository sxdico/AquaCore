package me.activated.core.commands.essentials;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class NightCommand extends BaseCommand {

    @Command(name = "night")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.setWorldTime("NIGHT");
            player.setPlayerTime(20000L, false);

            player.sendMessage(Language.PLAYER_NIGHT_SET.toString());
        });
    }
}
