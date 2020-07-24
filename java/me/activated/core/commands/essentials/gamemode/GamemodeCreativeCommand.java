package me.activated.core.commands.essentials.gamemode;

import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class GamemodeCreativeCommand extends BaseCommand {

    @Command(name = "gmc", permission = "Aqua.command.gamemode", aliases = "gm1")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.performCommand("gamemode creative");
        } else {
            player.performCommand("gamemode " + args[0] + " creative");
        }
    }
}
