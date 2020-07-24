package me.activated.core.commands.essentials.staff;

import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class CraftCommand extends BaseCommand {

    @Command(name = "craft", permission = "Aqua.command.craft")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();

            player.openWorkbench(player.getLocation(), true);
        });
    }
}
