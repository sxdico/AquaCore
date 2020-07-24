package me.activated.core.commands.essentials;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlaytimeCommand extends BaseCommand {

    @Command(name = "playtime", permission = "Aqua.command.playtime", aliases = "pt")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                long ticks = player.getStatistic(Statistic.PLAY_ONE_TICK);
                player.sendMessage(Language.PLAYTIME_SELF.toString()
                        .replace("<playtime>", DateUtils.formatTimeMillis(ticks * 50L)));
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }

            long ticks = target.getStatistic(Statistic.PLAY_ONE_TICK);
            player.sendMessage(Language.PLAYTIME_OTHER.toString()
                    .replace("<playtime>", DateUtils.formatTimeMillis(ticks * 50L))
                    .replace("<player>", target.getName()));
        });
    }
}
