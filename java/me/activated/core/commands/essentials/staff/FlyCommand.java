package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyCommand extends BaseCommand {

    @Command(name = "fly", permission = "Aqua.command.fly")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.performCommand(command.getLabel() + " " + player.getName());
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }
        if (!target.getName().equalsIgnoreCase(player.getName()) && !player.hasPermission("Aqua.command.fly.others")) {
            player.sendMessage(Language.FLY_NO_PERMISSION_OTHER.toString());
            return;
        }

        if (target.getAllowFlight()) {
            target.setAllowFlight(false);
            target.setFlying(false);
            player.sendMessage(Language.FLY_DISABLED.toString()
                    .replace("<player>", target.getDisplayName()));
        } else {
            target.setAllowFlight(true);
            target.setFlying(true);
            player.sendMessage(Language.FLY_ENABLED.toString()
                    .replace("<player>", target.getDisplayName()));
        }
    }
}
