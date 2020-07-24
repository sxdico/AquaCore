package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FeedCommand extends BaseCommand {

    @Command(name = "feed", permission = "Aqua.command.feed")
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
            target.setFoodLevel(20);
            player.sendMessage(Language.FEED_OTHER.toString()
                    .replace("<target>", target.getDisplayName()));
        });
    }
}
