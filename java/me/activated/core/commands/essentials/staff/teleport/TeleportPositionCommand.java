package me.activated.core.commands.essentials.staff.teleport;

import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportPositionCommand extends BaseCommand {

    @Command(name = "teleportposition", permission = "Aqua.command.teleportposition", aliases = {"tppos"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 3) {
            player.sendMessage(Color.translate("&cCorrect usage: /" + command.getLabel() + " <x> <y> <z>"));
            return;
        }
        if (!Utilities.isNumberInteger(args[0])
                || !Utilities.isNumberInteger(args[1])
                || !Utilities.isNumberInteger(args[2])) {
            player.sendMessage(Language.TELEPORT_INVALID_COORD.toString());
            return;
        }

        int x = Integer.valueOf(args[0]);
        int y = Integer.valueOf(args[1]);
        int z = Integer.valueOf(args[2]);

        player.teleport(new Location(player.getWorld(), x, y, z));
        player.sendMessage(Language.TELELPORT_TO_COORDS.toString()
                .replace("<x>", String.valueOf(x))
                .replace("<y>", String.valueOf(y))
                .replace("<z>", String.valueOf(z)));
    }
}
