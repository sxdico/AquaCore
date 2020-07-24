package me.activated.core.commands.essentials;

import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand extends BaseCommand {

    @Command(name = "ping", aliases = {"latency", "ms"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.PING_SELF.toString()
                        .replace("<ping>", String.valueOf(Utilities.getPing(player))));
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            player.sendMessage(Language.PING_OTHER.toString()
                    .replace("<target>", target.getDisplayName())
                    .replace("<ping>", String.valueOf(Utilities.getPing(target))));
        });
    }
}
