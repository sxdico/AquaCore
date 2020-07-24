package me.activated.core.commands.essentials.staff.teleport;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TopCommand extends BaseCommand {

    @Command(name = "top", permission = "Aqua.command.top")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Location location = player.getLocation();
        int highest = location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ());

        if (location.getBlockY() >= highest) {
            player.sendMessage(Language.TOP_ALREADY_AT_TOP.toString());
            return;
        }

        player.teleport(new Location(location.getWorld(), location.getX(), highest + 1.0, location.getZ(), location.getYaw(), location.getPitch()));
        player.sendMessage(Language.TOP_TELEPORTED.toString());

    }
}
