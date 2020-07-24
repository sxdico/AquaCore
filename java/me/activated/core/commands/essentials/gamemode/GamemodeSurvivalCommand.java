package me.activated.core.commands.essentials.gamemode;

import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class GamemodeSurvivalCommand extends BaseCommand {

    @Command(name = "gms", permission = "Aqua.command.gamemode", aliases = "gm0")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.performCommand("gamemode survival");
        } else {
            player.performCommand("gamemode " + args[0] + " survival");
        }
    }
}
