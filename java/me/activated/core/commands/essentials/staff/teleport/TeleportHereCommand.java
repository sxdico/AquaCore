package me.activated.core.commands.essentials.staff.teleport;

import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportHereCommand extends BaseCommand {

    @Command(name = "teleporthere", permission = "Aqua.command.teleporthere", aliases = {"tphere"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Color.translate("&cCorrect usage: /" + command.getLabel() + " <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }

        target.teleport(player);
        player.sendMessage(Language.TELEPORT_HERE_TO_SENDER.toString()
                .replace("<target>", target.getDisplayName()));
        target.sendMessage(Language.TELEPORT_HERE_TO_TARGET.toString()
                .replace("<player>", player.getDisplayName()));
    }
}
