package me.activated.core.commands.essentials.tps;

import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.server.TPSUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;

public class LagCommand extends BaseCommand {

    @Command(name = "tps", permission = "Aqua.command.tps", aliases = {"lagg", "lag"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender player = command.getSender();

            plugin.getCoreConfig().getStringList("tps-command-format").forEach(message -> {
                if (!message.contains("{worlds}")) {
                    Replacement replacement = new Replacement(message);

                    replacement.add("<tps1>", TPSUtils.getNiceTPS(TPSUtils.getRecentTps()[0]));
                    replacement.add("<tps2>", TPSUtils.getNiceTPS(TPSUtils.getRecentTps()[1]));
                    replacement.add("<tps3>", TPSUtils.getNiceTPS(TPSUtils.getRecentTps()[2]));
                    replacement.add("<uptime>", DateUtils.formatTimeMillis(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()));
                    replacement.add("<maxMemory>", Runtime.getRuntime().maxMemory() / 1024 / 1024);
                    replacement.add("<allocatedMemory>", Runtime.getRuntime().totalMemory() / 1024 / 1024);
                    replacement.add("<freeMemory>", Runtime.getRuntime().freeMemory() / 1024 / 1024);

                    player.sendMessage(replacement.toString());
                } else {
                    Bukkit.getWorlds().forEach(world -> {
                        plugin.getCoreConfig().getStringList("tps-command-world-format").forEach(msg -> {
                            Replacement replacement = new Replacement(msg);

                            replacement.add("<world>", world.getName());
                            replacement.add("<entities>", world.getEntities().size());
                            replacement.add("<loadedChunks>", world.getLoadedChunks().length);
                            replacement.add("<livingEntities>", world.getLivingEntities().size());

                            player.sendMessage(replacement.toString());
                        });
                    });
                }
            });
        });
    }
}
