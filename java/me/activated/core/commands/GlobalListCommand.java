package me.activated.core.commands;

import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class GlobalListCommand extends BaseCommand {

    @Command(name = "globallist", aliases = {"glist"}, permission = "Aqua.command.glist")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();

            plugin.getCoreConfig().getStringList("global-list.format").forEach(message -> {
                if (!message.toLowerCase().contains("<servers>")) {
                    player.sendMessage(message
                            .replace("<players>", String.valueOf(plugin.getServerManagement().getGlobalPlayers().size())));
                } else {
                    plugin.getServerManagement().getConnectedServers().forEach(serverData -> {
                        String format = plugin.getCoreConfig().getString("global-list.server-format");

                        player.sendMessage(format.replace("<server>", serverData.getServerName())
                                .replace("<online>", String.valueOf(serverData.getOnlinePlayers().size()))
                                .replace("<max_online>", String.valueOf(serverData.getMaxPlayers())));
                    });
                }
            });
        });
    }
}
