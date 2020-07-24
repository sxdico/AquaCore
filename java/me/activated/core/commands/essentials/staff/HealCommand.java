package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HealCommand extends BaseCommand {

    @Command(name = "heal", permission = "Aqua.command.heal")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                Tasks.run(plugin, () -> player.performCommand(command.getLabel() + " " + player.getName()));
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            target.setHealth(target.getMaxHealth());
            player.sendMessage(Language.HEAL_OTHER.toString()
                    .replace("<target>", target.getDisplayName()));
        });
    }
}
